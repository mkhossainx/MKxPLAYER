package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AudioTrackItem
import com.example.model.SpatialMode

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val path: String,
    val uriString: String,
    val isDemo: Boolean = false,
    val spatialMode: String = SpatialMode.STEREO.name,
    val dateAdded: Long = System.currentTimeMillis(),
    val albumArtUri: String? = null,
    val isFavorite: Boolean = false,
    val folderName: String = "Music",
    val lrcContent: String? = null,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L,
    val genre: String = "All",
    val fileSizeBytes: Long = 0L,
    val bitrate: Int = 320,
    val isCorrupted: Boolean = false
) {
    fun toAudioTrackItem(): AudioTrackItem {
        return AudioTrackItem(
            id = id,
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs,
            path = path,
            uriString = uriString,
            isDemo = isDemo,
            spatialMode = try { SpatialMode.valueOf(spatialMode) } catch (e: Exception) { SpatialMode.STEREO },
            dateAdded = dateAdded,
            albumArtUri = albumArtUri,
            isFavorite = isFavorite,
            folderName = folderName,
            lrcContent = lrcContent,
            playCount = playCount,
            lastPlayedTimestamp = lastPlayedTimestamp,
            genre = genre,
            fileSizeBytes = fileSizeBytes,
            bitrate = bitrate,
            isCorrupted = isCorrupted
        )
    }

    companion object {
        fun fromAudioTrackItem(item: AudioTrackItem): TrackEntity {
            return TrackEntity(
                id = item.id,
                title = item.title,
                artist = item.artist,
                album = item.album,
                durationMs = item.durationMs,
                path = item.path,
                uriString = item.uriString,
                isDemo = item.isDemo,
                spatialMode = item.spatialMode.name,
                dateAdded = item.dateAdded,
                albumArtUri = item.albumArtUri,
                isFavorite = item.isFavorite,
                folderName = item.folderName,
                lrcContent = item.lrcContent,
                playCount = item.playCount,
                lastPlayedTimestamp = item.lastPlayedTimestamp,
                genre = item.genre,
                fileSizeBytes = item.fileSizeBytes,
                bitrate = item.bitrate,
                isCorrupted = item.isCorrupted
            )
        }
    }
}
