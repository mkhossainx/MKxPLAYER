package com.example.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.data.dao.ConversionHistoryDao
import com.example.data.dao.ExcludedFolderDao
import com.example.data.dao.PlaylistDao
import com.example.data.dao.SpatialPresetDao
import com.example.data.dao.TrackDao
import com.example.data.entity.ConversionHistoryEntity
import com.example.data.entity.ExcludedFolderEntity
import com.example.data.entity.PlaylistEntity
import com.example.data.entity.PlaylistTrackCrossRef
import com.example.data.entity.SpatialPresetEntity
import com.example.data.entity.TrackEntity
import com.example.engine.DemoAudioGenerator
import com.example.model.AudioTrackItem
import com.example.model.ConversionHistoryItem
import com.example.model.LyricLine
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MKxRepository(
    private val context: Context,
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao,
    private val spatialPresetDao: SpatialPresetDao,
    private val conversionHistoryDao: ConversionHistoryDao,
    private val excludedFolderDao: ExcludedFolderDao
) {

    private val demoGenerator = DemoAudioGenerator(context)

    val allTracks: Flow<List<AudioTrackItem>> = trackDao.getAllTracks().map { list ->
        list.map { it.toAudioTrackItem() }
    }

    val favoriteTracks: Flow<List<AudioTrackItem>> = trackDao.getFavoriteTracks().map { list ->
        list.map { it.toAudioTrackItem() }
    }

    val spatialConvertedTracks: Flow<List<AudioTrackItem>> = trackDao.getSpatialConvertedTracks().map { list ->
        list.map { it.toAudioTrackItem() }
    }

    val recentlyPlayedTracks: Flow<List<AudioTrackItem>> = trackDao.getRecentlyPlayedTracks().map { list ->
        list.map { it.toAudioTrackItem() }
    }

    val mostPlayedTracks: Flow<List<AudioTrackItem>> = trackDao.getMostPlayedTracks().map { list ->
        list.map { it.toAudioTrackItem() }
    }

    val recentlyAddedTracks: Flow<List<AudioTrackItem>> = trackDao.getRecentlyAddedTracks().map { list ->
        list.map { it.toAudioTrackItem() }
    }

    val corruptedTracks: Flow<List<AudioTrackItem>> = trackDao.getCorruptedTracks().map { list ->
        list.map { it.toAudioTrackItem() }
    }

    val conversionHistory: Flow<List<ConversionHistoryItem>> = conversionHistoryDao.getAllHistory().map { list ->
        list.map { it.toItem() }
    }

    val excludedFolders: Flow<List<ExcludedFolderEntity>> = excludedFolderDao.getAllExcludedFolders()

    val allPlaylists: Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

    val allSpatialPresets: Flow<List<SpatialPresetEntity>> = spatialPresetDao.getAllPresets()

    fun getTracksForPlaylist(playlistId: Long): Flow<List<AudioTrackItem>> {
        return playlistDao.getTracksForPlaylist(playlistId).map { list ->
            list.map { it.toAudioTrackItem() }
        }
    }

    suspend fun initialize() = withContext(Dispatchers.IO) {
        val demos = demoGenerator.getOrCreateDemoTracks()
        demos.forEach { demo ->
            val existing = trackDao.getTrackById(demo.id)
            if (existing == null) {
                trackDao.insertTrack(TrackEntity.fromAudioTrackItem(demo))
            }
        }
        scanLocalAudioFiles()
    }

    suspend fun recordTrackPlayed(trackId: Long) = withContext(Dispatchers.IO) {
        trackDao.recordPlay(trackId, System.currentTimeMillis())
    }

    suspend fun markTrackCorrupted(trackId: Long, isCorrupted: Boolean) = withContext(Dispatchers.IO) {
        trackDao.setCorrupted(trackId, isCorrupted)
    }

    suspend fun scanLocalAudioFiles(): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            val excludedPaths = excludedFolderDao.getExcludedFolderPaths()

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.SIZE
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 8000"
            val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

            val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            context.contentResolver.query(
                queryUri,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                val entities = mutableListOf<TrackEntity>()

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val data = cursor.getString(dataCol) ?: ""

                    // Check if file is in excluded folder
                    if (excludedPaths.any { excluded -> data.startsWith(excluded) }) {
                        continue
                    }

                    val title = cursor.getString(titleCol) ?: "Unknown Title"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val album = cursor.getString(albumCol) ?: "Unknown Album"
                    val duration = cursor.getLong(durationCol)
                    val dateAdded = cursor.getLong(dateAddedCol)
                    val albumId = cursor.getLong(albumIdCol)
                    val size = cursor.getLong(sizeCol)

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                    val albumArtUri = try {
                        ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        ).toString()
                    } catch (e: Exception) {
                        null
                    }

                    val fileObj = File(data)
                    val folder = if (data.isNotEmpty()) fileObj.parentFile?.name ?: "Music" else "Music"
                    val isCorrupted = data.isNotEmpty() && (!fileObj.exists() || (fileObj.length() < 100 && !data.startsWith("content:")))

                    // Look for companion .lrc file
                    var lrcText: String? = null
                    if (data.isNotEmpty()) {
                        val baseWithoutExt = data.substringBeforeLast('.')
                        val lrcFile = File("$baseWithoutExt.lrc")
                        if (lrcFile.exists() && lrcFile.canRead()) {
                            try {
                                lrcText = lrcFile.readText()
                            } catch (ignored: Exception) {}
                        }
                    }

                    entities.add(
                        TrackEntity(
                            id = id,
                            title = title,
                            artist = if (artist == "<unknown>") "Unknown Artist" else artist,
                            album = if (album == "<unknown>") "Unknown Album" else album,
                            durationMs = duration,
                            path = data,
                            uriString = contentUri.toString(),
                            isDemo = false,
                            spatialMode = SpatialMode.STEREO.name,
                            dateAdded = dateAdded * 1000L,
                            albumArtUri = albumArtUri,
                            folderName = folder,
                            lrcContent = lrcText,
                            fileSizeBytes = size,
                            isCorrupted = isCorrupted
                        )
                    )
                }

                if (entities.isNotEmpty()) {
                    trackDao.insertTracks(entities)
                    count = entities.size
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        count
    }

    suspend fun importAudioFile(uri: Uri): AudioTrackItem? = withContext(Dispatchers.IO) {
        try {
            val fileName = uri.lastPathSegment ?: "Imported Track"
            val title = fileName.substringBeforeLast('.').replace("%20", " ")
            val id = System.currentTimeMillis()

            val track = AudioTrackItem(
                id = id,
                title = title,
                artist = "Local Import",
                album = "Imported",
                durationMs = 180000L,
                path = uri.toString(),
                uriString = uri.toString(),
                isDemo = false,
                spatialMode = SpatialMode.STEREO,
                folderName = "Imports"
            )
            trackDao.insertTrack(TrackEntity.fromAudioTrackItem(track))
            track
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun registerConvertedTrack(
        file: File,
        originalTrack: AudioTrackItem,
        mode: SpatialMode,
        presetName: String = "Custom"
    ): AudioTrackItem = withContext(Dispatchers.IO) {
        val newId = System.currentTimeMillis()
        val convertedItem = AudioTrackItem(
            id = newId,
            title = "${originalTrack.title} [${mode.dimensionLabel} Spatial]",
            artist = originalTrack.artist,
            album = "${originalTrack.album} (${mode.dimensionLabel})",
            durationMs = originalTrack.durationMs,
            path = file.absolutePath,
            uriString = Uri.fromFile(file).toString(),
            isDemo = false,
            spatialMode = mode,
            folderName = "Spatial Converted",
            lrcContent = originalTrack.lrcContent,
            fileSizeBytes = file.length()
        )
        trackDao.insertTrack(TrackEntity.fromAudioTrackItem(convertedItem))

        // Record into Conversion History
        conversionHistoryDao.insertHistory(
            ConversionHistoryEntity(
                originalTitle = originalTrack.title,
                originalPath = originalTrack.path,
                outputFilename = file.name,
                outputPath = file.absolutePath,
                spatialMode = mode.name,
                presetName = presetName,
                durationMs = originalTrack.durationMs,
                format = file.extension.uppercase(),
                fileSizeBytes = file.length()
            )
        )

        convertedItem
    }

    suspend fun toggleFavorite(trackId: Long, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        trackDao.updateFavorite(trackId, !currentFavorite)
    }

    suspend fun saveTrackLyrics(trackId: Long, lrcContent: String) = withContext(Dispatchers.IO) {
        val track = trackDao.getTrackById(trackId) ?: return@withContext
        trackDao.updateTrack(track.copy(lrcContent = lrcContent))
    }

    suspend fun createPlaylist(name: String): Long = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylist(playlistId)
    }

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) = withContext(Dispatchers.IO) {
        playlistDao.insertTrackToPlaylist(PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId))
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) = withContext(Dispatchers.IO) {
        playlistDao.removeTrackFromPlaylist(playlistId = playlistId, trackId = trackId)
    }

    suspend fun addExcludedFolder(path: String, name: String) = withContext(Dispatchers.IO) {
        excludedFolderDao.insertExcludedFolder(ExcludedFolderEntity(folderPath = path, folderName = name))
        scanLocalAudioFiles()
    }

    suspend fun removeExcludedFolder(path: String) = withContext(Dispatchers.IO) {
        excludedFolderDao.deleteExcludedFolder(path)
        scanLocalAudioFiles()
    }

    suspend fun saveCustomPreset(name: String, mode: SpatialMode, params: SpatialParams): Long = withContext(Dispatchers.IO) {
        spatialPresetDao.insertPreset(
            SpatialPresetEntity(
                name = name,
                mode = mode.name,
                intensity = params.intensity,
                speedHz = params.speedHz,
                reverbAmount = params.reverbAmount,
                echoAmount = params.echoAmount,
                bassBoost = params.bassBoost,
                wetDryMix = params.wetDryMix,
                isSystemDefault = false
            )
        )
    }

    suspend fun deleteCustomPreset(id: Long) = withContext(Dispatchers.IO) {
        spatialPresetDao.deleteCustomPreset(id)
    }

    suspend fun deleteConversionHistory(id: Long) = withContext(Dispatchers.IO) {
        conversionHistoryDao.deleteHistoryById(id)
    }

    suspend fun clearAllConversionHistory() = withContext(Dispatchers.IO) {
        conversionHistoryDao.clearAllHistory()
    }

    /**
     * Imports an M3U / M3U8 playlist file and creates a new playlist in Room.
     */
    suspend fun importM3uPlaylist(uri: Uri, playlistName: String): Int = withContext(Dispatchers.IO) {
        var importedCount = 0
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext 0
            val lines = inputStream.bufferedReader().readLines()
            inputStream.close()

            val playlistId = createPlaylist(playlistName)
            var pos = 0

            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue

                // Check by path or title
                val track = trackDao.getTrackByPath(trimmed)
                if (track != null) {
                    playlistDao.insertTrackToPlaylist(PlaylistTrackCrossRef(playlistId, track.id, pos++))
                    importedCount++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        importedCount
    }

    /**
     * Exports a playlist to M3U format in the destination file.
     */
    suspend fun exportM3uPlaylist(playlistId: Long, playlistName: String, destinationFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val tracks = playlistDao.getTracksForPlaylist(playlistId).first()
            val writer = destinationFile.bufferedWriter()
            writer.write("#EXTM3U\n")
            writer.write("#PLAYLIST:$playlistName\n")
            tracks.forEach { t ->
                val durationSec = t.durationMs / 1000
                writer.write("#EXTINF:$durationSec,${t.artist} - ${t.title}\n")
                writer.write("${t.path}\n")
            }
            writer.flush()
            writer.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun parseLrcLyrics(lrcContent: String?): List<LyricLine> {
        if (lrcContent.isNullOrBlank()) return emptyList()
        val lines = mutableListOf<LyricLine>()
        val regex = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)")

        lrcContent.lines().forEach { line ->
            val match = regex.find(line.trim())
            if (match != null) {
                val (minStr, secStr, fracStr, text) = match.destructured
                val mins = minStr.toLongOrNull() ?: 0L
                val secs = secStr.toLongOrNull() ?: 0L
                val millis = if (fracStr.length == 2) (fracStr.toLongOrNull() ?: 0L) * 10L else fracStr.toLongOrNull() ?: 0L
                val totalMs = mins * 60000L + secs * 1000L + millis
                if (text.isNotBlank()) {
                    lines.add(LyricLine(timestampMs = totalMs, text = text.trim()))
                }
            } else if (line.isNotBlank() && !line.startsWith("[")) {
                lines.add(LyricLine(timestampMs = lines.size * 3000L, text = line.trim()))
            }
        }
        return lines.sortedBy { it.timestampMs }
    }
}
