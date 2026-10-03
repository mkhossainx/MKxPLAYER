package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MKxDatabase
import com.example.data.entity.ExcludedFolderEntity
import com.example.data.entity.PlaylistEntity
import com.example.data.entity.SpatialPresetEntity
import com.example.data.repository.MKxRepository
import com.example.engine.SpatialAudioConverter
import com.example.engine.SpatialAudioProcessor
import com.example.model.AudioTrackItem
import com.example.model.BatchConversionJob
import com.example.model.ConversionHistoryItem
import com.example.model.EqualizerBand
import com.example.model.ExportFormat
import com.example.model.HrtfProfile
import com.example.model.LibrarySortOrder
import com.example.model.LyricLine
import com.example.model.OrbitDirection
import com.example.model.RepeatMode
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import com.example.model.StudioAudioEffects
import com.example.model.VisualizerMode
import com.example.playback.AudioPlayerEngine
import com.example.service.SpatialPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class ScreenTab {
    HOME,
    LIBRARY,
    NOW_PLAYING,
    STUDIO,
    VISUALIZER,
    SETTINGS,
    EQUALIZER,
    QUEUE,
    ABOUT,
    CONVERSION_HISTORY
}

enum class LibrarySubTab {
    SONGS,
    ALBUMS,
    ARTISTS,
    GENRES,
    FOLDERS,
    PLAYLISTS,
    CONVERTED,
    HISTORY
}

enum class SmartLibraryFilter {
    ALL,
    RECENTLY_PLAYED,
    RECENTLY_ADDED,
    MOST_PLAYED,
    FAVORITES,
    CONTINUE_LISTENING,
    DUPLICATES,
    CORRUPTED
}

data class ConversionState(
    val isConverting: Boolean = false,
    val progressPercent: Int = 0,
    val statusText: String = "",
    val exportedFile: File? = null,
    val error: String? = null
)

class MKxPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MKxDatabase.getDatabase(application)
    private val repository = MKxRepository(
        context = application,
        trackDao = database.trackDao(),
        playlistDao = database.playlistDao(),
        spatialPresetDao = database.spatialPresetDao(),
        conversionHistoryDao = database.conversionHistoryDao(),
        excludedFolderDao = database.excludedFolderDao()
    )

    val playerEngine = AudioPlayerEngine(application)
    val converter = SpatialAudioConverter(application)

    // Navigation and UI states
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _librarySubTab = MutableStateFlow(LibrarySubTab.SONGS)
    val librarySubTab: StateFlow<LibrarySubTab> = _librarySubTab.asStateFlow()

    private val _smartFilter = MutableStateFlow(SmartLibraryFilter.ALL)
    val smartFilter: StateFlow<SmartLibraryFilter> = _smartFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(LibrarySortOrder.TITLE_ASC)
    val sortOrder: StateFlow<LibrarySortOrder> = _sortOrder.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    private val _isSpatialPanelOpen = MutableStateFlow(false)
    val isSpatialPanelOpen: StateFlow<Boolean> = _isSpatialPanelOpen.asStateFlow()

    private val _showLyrics = MutableStateFlow(false)
    val showLyrics: StateFlow<Boolean> = _showLyrics.asStateFlow()

    private val _lyricsOffsetMs = MutableStateFlow(0L)
    val lyricsOffsetMs: StateFlow<Long> = _lyricsOffsetMs.asStateFlow()

    private val _lyricsFontSizeSp = MutableStateFlow(18)
    val lyricsFontSizeSp: StateFlow<Int> = _lyricsFontSizeSp.asStateFlow()

    private val _highContrastLyrics = MutableStateFlow(false)
    val highContrastLyrics: StateFlow<Boolean> = _highContrastLyrics.asStateFlow()

    private val _showQueue = MutableStateFlow(false)
    val showQueue: StateFlow<Boolean> = _showQueue.asStateFlow()

    private val _showBatchConverter = MutableStateFlow(false)
    val showBatchConverter: StateFlow<Boolean> = _showBatchConverter.asStateFlow()

    private val _showPrivacyCenter = MutableStateFlow(false)
    val showPrivacyCenter: StateFlow<Boolean> = _showPrivacyCenter.asStateFlow()

    private val _showHeadphoneAlert = MutableStateFlow(true)
    val showHeadphoneAlert: StateFlow<Boolean> = _showHeadphoneAlert.asStateFlow()

    private val _isAmoledBlack = MutableStateFlow(false)
    val isAmoledBlack: StateFlow<Boolean> = _isAmoledBlack.asStateFlow()

    private val _visualizerMode = MutableStateFlow(VisualizerMode.ORBIT_8D)
    val visualizerMode: StateFlow<VisualizerMode> = _visualizerMode.asStateFlow()

    private val _conversionState = MutableStateFlow(ConversionState())
    val conversionState: StateFlow<ConversionState> = _conversionState.asStateFlow()

    // Data streams from repository
    val allTracks: StateFlow<List<AudioTrackItem>> = repository.allTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTracks: StateFlow<List<AudioTrackItem>> = repository.favoriteTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayedTracks: StateFlow<List<AudioTrackItem>> = repository.recentlyPlayedTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mostPlayedTracks: StateFlow<List<AudioTrackItem>> = repository.mostPlayedTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyAddedTracks: StateFlow<List<AudioTrackItem>> = repository.recentlyAddedTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val spatialConvertedTracks: StateFlow<List<AudioTrackItem>> = repository.spatialConvertedTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val corruptedTracks: StateFlow<List<AudioTrackItem>> = repository.corruptedTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversionHistory: StateFlow<List<ConversionHistoryItem>> = repository.conversionHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val excludedFolders: StateFlow<List<ExcludedFolderEntity>> = repository.excludedFolders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlaylists: StateFlow<List<PlaylistEntity>> = repository.allPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSpatialPresets: StateFlow<List<SpatialPresetEntity>> = repository.allSpatialPresets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val batchQueue: StateFlow<List<BatchConversionJob>> = converter.batchQueue
    val isBatchRunning: StateFlow<Boolean> = converter.isBatchRunning

    // Filtered and Sorted track stream
    val displayedTracks: StateFlow<List<AudioTrackItem>> = combine(
        allTracks,
        favoriteTracks,
        recentlyPlayedTracks,
        mostPlayedTracks,
        recentlyAddedTracks,
        corruptedTracks,
        _smartFilter,
        _searchQuery,
        _sortOrder
    ) { args ->
        val rawAll = args[0] as List<AudioTrackItem>
        val rawFavs = args[1] as List<AudioTrackItem>
        val rawRecent = args[2] as List<AudioTrackItem>
        val rawMost = args[3] as List<AudioTrackItem>
        val rawAdded = args[4] as List<AudioTrackItem>
        val rawCorrupt = args[5] as List<AudioTrackItem>
        val filter = args[6] as SmartLibraryFilter
        val query = args[7] as String
        val sort = args[8] as LibrarySortOrder

        var list = when (filter) {
            SmartLibraryFilter.ALL -> rawAll
            SmartLibraryFilter.FAVORITES -> rawFavs
            SmartLibraryFilter.RECENTLY_PLAYED, SmartLibraryFilter.CONTINUE_LISTENING -> rawRecent
            SmartLibraryFilter.MOST_PLAYED -> rawMost
            SmartLibraryFilter.RECENTLY_ADDED -> rawAdded
            SmartLibraryFilter.CORRUPTED -> rawCorrupt
            SmartLibraryFilter.DUPLICATES -> {
                // Find songs with same title or artist
                val titleCounts = rawAll.groupingBy { it.title.lowercase().trim() }.eachCount()
                rawAll.filter { (titleCounts[it.title.lowercase().trim()] ?: 0) > 1 }
            }
        }

        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true) ||
                it.genre.contains(query, ignoreCase = true)
            }
        }

        when (sort) {
            LibrarySortOrder.TITLE_ASC -> list.sortedBy { it.title.lowercase() }
            LibrarySortOrder.ARTIST_ASC -> list.sortedBy { it.artist.lowercase() }
            LibrarySortOrder.ALBUM_ASC -> list.sortedBy { it.album.lowercase() }
            LibrarySortOrder.DURATION_DESC -> list.sortedByDescending { it.durationMs }
            LibrarySortOrder.DATE_ADDED_DESC -> list.sortedByDescending { it.dateAdded }
            LibrarySortOrder.PLAY_COUNT_DESC -> list.sortedByDescending { it.playCount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTracks: StateFlow<List<AudioTrackItem>> get() = displayedTracks

    private val _isVisualizer3DView = MutableStateFlow(false)
    val isVisualizer3DView: StateFlow<Boolean> = _isVisualizer3DView.asStateFlow()

    fun toggleVisualizerView() {
        _isVisualizer3DView.value = !_isVisualizer3DView.value
    }

    // Player Engine states
    val currentTrack: StateFlow<AudioTrackItem?> = playerEngine.currentTrack
    val isPlaying: StateFlow<Boolean> = playerEngine.isPlaying
    val currentPositionMs: StateFlow<Long> = playerEngine.currentPositionMs
    val durationMs: StateFlow<Long> = playerEngine.durationMs
    val spatialMode: StateFlow<SpatialMode> = playerEngine.spatialMode
    val spatialParams: StateFlow<SpatialParams> = playerEngine.spatialParams
    val studioEffects: StateFlow<StudioAudioEffects> = playerEngine.studioEffects
    val repeatMode: StateFlow<RepeatMode> = playerEngine.repeatMode
    val isShuffle: StateFlow<Boolean> = playerEngine.isShuffle
    val queue: StateFlow<List<AudioTrackItem>> = playerEngine.queue
    val trajectoryPoint: StateFlow<SpatialAudioProcessor.SpatialTrajectoryPoint> = playerEngine.trajectoryPoint
    val spectrumBars: StateFlow<FloatArray> = playerEngine.spectrumBars
    val audioAmplitude: StateFlow<Float> = playerEngine.audioAmplitude
    val panPosition: StateFlow<Float> = playerEngine.panPosition
    val equalizerBands: StateFlow<List<EqualizerBand>> = playerEngine.equalizerBands
    val sleepTimerRemainingSec: StateFlow<Int?> = playerEngine.sleepTimerRemainingSec

    init {
        viewModelScope.launch {
            repository.initialize()
        }

        // Keep foreground service notification in sync with playback
        viewModelScope.launch {
            combine(currentTrack, isPlaying, spatialMode) { track, playing, mode ->
                Triple(track, playing, mode)
            }.collect { (track, playing, mode) ->
                if (track != null) {
                    val app = getApplication<Application>()
                    val serviceIntent = Intent(app, SpatialPlaybackService::class.java)
                    try {
                        app.startService(serviceIntent)
                    } catch (ignored: Exception) {}
                }
            }
        }
    }

    fun navigateToTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun setLibrarySubTab(subTab: LibrarySubTab) {
        _librarySubTab.value = subTab
    }

    fun setSmartFilter(filter: SmartLibraryFilter) {
        _smartFilter.value = filter
    }

    fun setSortOrder(order: LibrarySortOrder) {
        _sortOrder.value = order
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setVisualizerMode(mode: VisualizerMode) {
        _visualizerMode.value = mode
    }

    fun toggleAmoledBlack() {
        _isAmoledBlack.value = !_isAmoledBlack.value
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    fun toggleSpatialPanel(open: Boolean? = null) {
        _isSpatialPanelOpen.value = open ?: !_isSpatialPanelOpen.value
    }

    fun toggleLyricsView(show: Boolean? = null) {
        _showLyrics.value = show ?: !_showLyrics.value
    }

    fun setCustomLyrics(lrcText: String) {
        val track = currentTrack.value ?: return
        viewModelScope.launch {
            repository.saveTrackLyrics(track.id, lrcText)
        }
    }

    fun adjustLyricsOffset(deltaMs: Long) {
        _lyricsOffsetMs.value += deltaMs
    }

    fun setLyricsFontSize(sp: Int) {
        _lyricsFontSizeSp.value = sp
    }

    fun toggleHighContrastLyrics() {
        _highContrastLyrics.value = !_highContrastLyrics.value
    }

    fun toggleQueueView(show: Boolean? = null) {
        _showQueue.value = show ?: !_showQueue.value
    }

    fun toggleBatchConverter(show: Boolean? = null) {
        _showBatchConverter.value = show ?: !_showBatchConverter.value
    }

    fun togglePrivacyCenter(show: Boolean? = null) {
        _showPrivacyCenter.value = show ?: !_showPrivacyCenter.value
    }

    fun dismissHeadphoneAlert() {
        _showHeadphoneAlert.value = false
    }

    fun playTrack(track: AudioTrackItem, queueList: List<AudioTrackItem>? = null) {
        playerEngine.playTrack(track, queueList)
        viewModelScope.launch {
            repository.recordTrackPlayed(track.id)
        }
    }

    fun togglePlayPause() {
        if (currentTrack.value == null && allTracks.value.isNotEmpty()) {
            playTrack(allTracks.value.first())
        } else {
            playerEngine.togglePlayPause()
        }
    }

    fun seekTo(positionMs: Long) {
        playerEngine.seekTo(positionMs)
    }

    fun skipNext() {
        playerEngine.skipNext()
    }

    fun skipPrevious() {
        playerEngine.skipPrevious()
    }

    fun setSpatialMode(mode: SpatialMode) {
        playerEngine.setSpatialMode(mode)
    }

    fun updateSpatialParams(params: SpatialParams) {
        playerEngine.updateSpatialParams(params)
    }

    fun toggleSpatialBypass() {
        playerEngine.toggleSpatialBypass()
    }

    fun updateStudioEffects(effects: StudioAudioEffects) {
        playerEngine.updateStudioEffects(effects)
    }

    fun setPlaybackSpeed(speed: Float) {
        playerEngine.setPlaybackSpeed(speed)
    }

    fun setPitch(pitch: Float) {
        playerEngine.setPitch(pitch)
    }

    fun setStereoBalance(balance: Float) {
        playerEngine.setStereoBalance(balance)
    }

    fun toggleMonoStereo() {
        playerEngine.toggleMonoStereo()
    }

    fun applyPreset(preset: SpatialPresetEntity) {
        setSpatialMode(preset.getSpatialMode())
        updateSpatialParams(preset.toSpatialParams())
    }

    fun setRepeatMode(mode: RepeatMode) {
        playerEngine.setRepeatMode(mode)
    }

    fun toggleShuffle() {
        playerEngine.toggleShuffle()
    }

    fun setEqualizerBandLevel(index: Int, levelMilliBels: Short) {
        playerEngine.setEqualizerBandLevel(index, levelMilliBels)
    }

    fun setBassBoost(level: Short) {
        playerEngine.setBassBoost(level)
    }

    fun startSleepTimer(minutes: Int) {
        playerEngine.startSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        playerEngine.cancelSleepTimer()
    }

    fun reorderQueue(from: Int, to: Int) {
        playerEngine.reorderQueue(from, to)
    }

    fun removeFromQueue(index: Int) {
        playerEngine.removeFromQueue(index)
    }

    fun clearQueue() {
        playerEngine.clearQueue()
    }

    fun toggleFavorite(track: AudioTrackItem) {
        viewModelScope.launch {
            repository.toggleFavorite(track.id, track.isFavorite)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, trackId)
        }
    }

    fun saveCustomPreset(name: String) {
        viewModelScope.launch {
            repository.saveCustomPreset(name, spatialMode.value, spatialParams.value)
        }
    }

    fun deleteCustomPreset(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomPreset(id)
        }
    }

    fun addExcludedFolder(path: String, name: String) {
        viewModelScope.launch {
            repository.addExcludedFolder(path, name)
        }
    }

    fun removeExcludedFolder(path: String) {
        viewModelScope.launch {
            repository.removeExcludedFolder(path)
        }
    }

    fun importM3u(uri: Uri, name: String) {
        viewModelScope.launch {
            repository.importM3uPlaylist(uri, name)
        }
    }

    fun exportM3u(playlist: PlaylistEntity, context: Context): File? {
        val exportDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Playlists")
        if (!exportDir.exists()) exportDir.mkdirs()
        val file = File(exportDir, "${playlist.name.replace(" ", "_")}.m3u")
        viewModelScope.launch {
            repository.exportM3uPlaylist(playlist.id, playlist.name, file)
        }
        return file
    }

    fun deleteConversionHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteConversionHistory(id)
        }
    }

    fun clearAllConversionHistory() {
        viewModelScope.launch {
            repository.clearAllConversionHistory()
        }
    }

    fun importAudioUri(uri: Uri) {
        viewModelScope.launch {
            val imported = repository.importAudioFile(uri)
            if (imported != null) {
                playTrack(imported)
            }
        }
    }

    fun rescanMedia() {
        viewModelScope.launch {
            repository.scanLocalAudioFiles()
        }
    }

    fun startSpatialConversion(
        track: AudioTrackItem,
        mode: SpatialMode,
        params: SpatialParams,
        format: ExportFormat,
        customFilename: String? = null
    ) {
        viewModelScope.launch {
            _conversionState.value = ConversionState(isConverting = true, progressPercent = 0, statusText = "Initializing on-device DSP...")
            val resultFile = converter.convertTrack(
                track = track,
                mode = mode,
                params = params,
                format = format,
                customFilename = customFilename
            ) { percent, status ->
                _conversionState.value = _conversionState.value.copy(progressPercent = percent, statusText = status)
            }

            if (resultFile != null && resultFile.exists()) {
                val registered = repository.registerConvertedTrack(resultFile, track, mode, "Custom DSP")
                _conversionState.value = ConversionState(
                    isConverting = false,
                    progressPercent = 100,
                    statusText = "Completed: ${resultFile.name}",
                    exportedFile = resultFile
                )
            } else {
                _conversionState.value = ConversionState(
                    isConverting = false,
                    progressPercent = 0,
                    statusText = "Conversion cancelled or failed.",
                    error = "Could not convert track."
                )
            }
        }
    }

    fun addBatchJobs(tracks: List<AudioTrackItem>, mode: SpatialMode, params: SpatialParams, format: ExportFormat) {
        val jobs = tracks.map { t ->
            BatchConversionJob(
                id = "${t.id}_${System.currentTimeMillis()}",
                track = t,
                mode = mode,
                params = params,
                format = format
            )
        }
        converter.addBatchJobs(jobs)
    }

    fun startBatchProcessing() {
        viewModelScope.launch {
            converter.executeBatchQueue { track, file, mode ->
                viewModelScope.launch {
                    repository.registerConvertedTrack(file, track, mode, "Batch Conversion")
                }
            }
        }
    }

    fun cancelBatchJob(jobId: String) {
        converter.cancelJob(jobId)
    }

    fun clearBatchQueue() {
        converter.clearBatchQueue()
    }

    fun dismissConversionDialog() {
        _conversionState.value = ConversionState()
    }

    fun shareExportedFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share MKxPLAYER Audio Track"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun parseCurrentLyrics(): List<LyricLine> {
        val track = currentTrack.value ?: return emptyList()
        val rawLyrics = repository.parseLrcLyrics(track.lrcContent)
        val offset = _lyricsOffsetMs.value
        return if (offset == 0L) rawLyrics
        else rawLyrics.map { it.copy(timestampMs = it.timestampMs + offset) }
    }

    override fun onCleared() {
        super.onCleared()
        playerEngine.release()
    }
}
