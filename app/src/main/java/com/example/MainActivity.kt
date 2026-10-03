package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.SleepTimerDialog
import com.example.ui.components.SpatialConverterPanel
import com.example.ui.components.SplashScreen
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.BatchConverterDialog
import com.example.ui.screens.ConversionHistoryScreen
import com.example.ui.screens.EqualizerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.LyricsScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.PrivacyCenterDialog
import com.example.ui.screens.QueueScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.VisualizerScreen
import com.example.ui.theme.MKxPlayerTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.viewmodel.MKxPlayerViewModel
import com.example.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MKxPlayerTheme {
                MKxPlayerApp()
            }
        }
    }
}

@Composable
fun MKxPlayerApp(
    viewModel: MKxPlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    var showSplash by remember { mutableStateOf(true) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    // Request permissions for storage and notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val grantedAny = permissions.values.any { it }
        if (grantedAny) {
            viewModel.rescanMedia()
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    if (showSplash) {
        SplashScreen(onFinished = { showSplash = false })
        return
    }

    // State bindings from ViewModel
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val librarySubTab by viewModel.librarySubTab.collectAsStateWithLifecycle()
    val smartFilter by viewModel.smartFilter.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val allTracks by viewModel.allTracks.collectAsStateWithLifecycle()
    val filteredTracks by viewModel.filteredTracks.collectAsStateWithLifecycle()
    val favoriteTracks by viewModel.favoriteTracks.collectAsStateWithLifecycle()
    val recentlyPlayedTracks by viewModel.recentlyPlayedTracks.collectAsStateWithLifecycle()
    val spatialConvertedTracks by viewModel.spatialConvertedTracks.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val conversionHistory by viewModel.conversionHistory.collectAsStateWithLifecycle()
    val spatialPresets by viewModel.allSpatialPresets.collectAsStateWithLifecycle()
    val batchQueue by viewModel.batchQueue.collectAsStateWithLifecycle()
    val isBatchRunning by viewModel.isBatchRunning.collectAsStateWithLifecycle()

    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val spatialMode by viewModel.spatialMode.collectAsStateWithLifecycle()
    val spatialParams by viewModel.spatialParams.collectAsStateWithLifecycle()
    val studioEffects by viewModel.studioEffects.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val isShuffle by viewModel.isShuffle.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()
    val trajectoryPoint by viewModel.trajectoryPoint.collectAsStateWithLifecycle()
    val spectrumBars by viewModel.spectrumBars.collectAsStateWithLifecycle()
    val audioAmplitude by viewModel.audioAmplitude.collectAsStateWithLifecycle()
    val panPosition by viewModel.panPosition.collectAsStateWithLifecycle()
    val equalizerBands by viewModel.equalizerBands.collectAsStateWithLifecycle()
    val sleepTimerSec by viewModel.sleepTimerRemainingSec.collectAsStateWithLifecycle()
    val visualizerMode by viewModel.visualizerMode.collectAsStateWithLifecycle()
    val isAmoledBlack by viewModel.isAmoledBlack.collectAsStateWithLifecycle()

    val isSpatialPanelOpen by viewModel.isSpatialPanelOpen.collectAsStateWithLifecycle()
    val isVisualizer3DView by viewModel.isVisualizer3DView.collectAsStateWithLifecycle()
    val showLyrics by viewModel.showLyrics.collectAsStateWithLifecycle()
    val lyricsOffsetMs by viewModel.lyricsOffsetMs.collectAsStateWithLifecycle()
    val lyricsFontSizeSp by viewModel.lyricsFontSizeSp.collectAsStateWithLifecycle()
    val highContrastLyrics by viewModel.highContrastLyrics.collectAsStateWithLifecycle()
    val showBatchConverter by viewModel.showBatchConverter.collectAsStateWithLifecycle()
    val showPrivacyCenter by viewModel.showPrivacyCenter.collectAsStateWithLifecycle()
    val showHeadphoneAlert by viewModel.showHeadphoneAlert.collectAsStateWithLifecycle()
    val conversionState by viewModel.conversionState.collectAsStateWithLifecycle()

    // Handle back button behavior for sub-screens
    BackHandler(enabled = currentTab != ScreenTab.HOME || showLyrics) {
        if (showLyrics) {
            viewModel.toggleLyricsView(false)
        } else if (currentTab != ScreenTab.HOME) {
            viewModel.navigateToTab(ScreenTab.HOME)
        }
    }

    val isFullScreenDestination = currentTab == ScreenTab.NOW_PLAYING || showLyrics

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .statusBarsPadding(),
        bottomBar = {
            if (!isFullScreenDestination) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    // Mini Player docked right above bottom navigation
                    if (currentTrack != null) {
                        MiniPlayerBar(
                            track = currentTrack,
                            isPlaying = isPlaying,
                            currentPositionMs = currentPositionMs,
                            durationMs = durationMs,
                            spatialMode = spatialMode,
                            onPlayPause = { viewModel.togglePlayPause() },
                            onSkipNext = { viewModel.skipNext() },
                            onClick = { viewModel.navigateToTab(ScreenTab.NOW_PLAYING) }
                        )
                    }

                    // Bottom Navigation Bar with 5 core tabs
                    NavigationBar(
                        containerColor = Color(0xFF0D1322),
                        contentColor = NeonCyan,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentTab == ScreenTab.HOME,
                            onClick = { viewModel.navigateToTab(ScreenTab.HOME) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = NeonCyan,
                                indicatorColor = NeonCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == ScreenTab.LIBRARY,
                            onClick = { viewModel.navigateToTab(ScreenTab.LIBRARY) },
                            icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                            label = { Text("Library", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = NeonCyan,
                                indicatorColor = NeonCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == ScreenTab.STUDIO,
                            onClick = { viewModel.navigateToTab(ScreenTab.STUDIO) },
                            icon = { Icon(Icons.Default.Tune, contentDescription = "Studio") },
                            label = { Text("Studio", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = NeonCyan,
                                indicatorColor = NeonCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == ScreenTab.VISUALIZER,
                            onClick = { viewModel.navigateToTab(ScreenTab.VISUALIZER) },
                            icon = { Icon(Icons.Default.SurroundSound, contentDescription = "Visualizer") },
                            label = { Text("Visualizer", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = NeonCyan,
                                indicatorColor = NeonCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == ScreenTab.SETTINGS,
                            onClick = { viewModel.navigateToTab(ScreenTab.SETTINGS) },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = { Text("Settings", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = NeonCyan,
                                indicatorColor = NeonCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                showLyrics -> {
                    LyricsScreen(
                        track = currentTrack,
                        lyrics = viewModel.parseCurrentLyrics(),
                        currentPositionMs = currentPositionMs,
                        offsetMs = lyricsOffsetMs,
                        fontSizeSp = lyricsFontSizeSp,
                        isHighContrast = highContrastLyrics,
                        onAdjustOffset = { viewModel.adjustLyricsOffset(it) },
                        onFontSizeChange = { viewModel.setLyricsFontSize(it) },
                        onToggleHighContrast = { viewModel.toggleHighContrastLyrics() },
                        onImportLrcText = { viewModel.setCustomLyrics(it) },
                        onBack = { viewModel.toggleLyricsView(false) }
                    )
                }
                currentTab == ScreenTab.HOME -> {
                    HomeScreen(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        recentTracks = recentlyPlayedTracks,
                        favoriteTracks = favoriteTracks,
                        allTracks = allTracks,
                        spatialMode = spatialMode,
                        isBypassed = spatialParams.isBypassed,
                        onTrackSelect = { viewModel.playTrack(it, allTracks) },
                        onPlayPause = { viewModel.togglePlayPause() },
                        onNavigateTab = { viewModel.navigateToTab(it) },
                        onOpenSpatialPanel = { viewModel.toggleSpatialPanel(true) },
                        onToggleBypass = { viewModel.toggleSpatialBypass() },
                        onOpenBatchConverter = { viewModel.toggleBatchConverter(true) },
                        onOpenPrivacyCenter = { viewModel.togglePrivacyCenter(true) },
                        onSelectFilter = {
                            viewModel.setSmartFilter(it)
                            viewModel.navigateToTab(ScreenTab.LIBRARY)
                        }
                    )
                }
                currentTab == ScreenTab.LIBRARY -> {
                    LibraryScreen(
                        tracks = filteredTracks,
                        spatialTracks = spatialConvertedTracks,
                        playlists = allPlaylists,
                        historyItems = conversionHistory,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        subTab = librarySubTab,
                        smartFilter = smartFilter,
                        sortOrder = sortOrder,
                        searchQuery = searchQuery,
                        showHeadphoneAlert = showHeadphoneAlert,
                        onSubTabChange = { viewModel.setLibrarySubTab(it) },
                        onSmartFilterChange = { viewModel.setSmartFilter(it) },
                        onSortOrderChange = { viewModel.setSortOrder(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onTrackSelect = { viewModel.playTrack(it, filteredTracks) },
                        onImportAudioFile = { viewModel.importAudioUri(it) },
                        onRescanLibrary = { viewModel.rescanMedia() },
                        onCreatePlaylist = { viewModel.createPlaylist(it) },
                        onAddTrackToPlaylist = { plId, trkId -> viewModel.addTrackToPlaylist(plId, trkId) },
                        onDeletePlaylist = { viewModel.deletePlaylist(it) },
                        onImportM3u = { uri, name -> viewModel.importM3u(uri, name) },
                        onExportM3u = { viewModel.exportM3u(it, context) },
                        onOpenBatchConverter = { viewModel.toggleBatchConverter(true) },
                        onDismissHeadphoneAlert = { viewModel.dismissHeadphoneAlert() }
                    )
                }
                currentTab == ScreenTab.STUDIO -> {
                    StudioScreen(
                        bands = equalizerBands,
                        studioEffects = studioEffects,
                        onBandChange = { idx, lvl -> viewModel.setEqualizerBandLevel(idx, lvl) },
                        onStudioEffectsChange = { viewModel.updateStudioEffects(it) },
                        onPlaybackSpeedChange = { viewModel.setPlaybackSpeed(it) },
                        onPitchChange = { viewModel.setPitch(it) }
                    )
                }
                currentTab == ScreenTab.VISUALIZER -> {
                    VisualizerScreen(
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        spatialMode = spatialMode,
                        spatialParams = spatialParams,
                        visualizerMode = visualizerMode,
                        trajectoryPoint = trajectoryPoint,
                        spectrumBars = spectrumBars,
                        audioAmplitude = audioAmplitude,
                        panPosition = panPosition,
                        onPlayPause = { viewModel.togglePlayPause() },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onVisualizerModeChange = { viewModel.setVisualizerMode(it) },
                        onSpatialModeChange = { viewModel.setSpatialMode(it) },
                        onParamsChange = { viewModel.updateSpatialParams(it) },
                        onToggleBypass = { viewModel.toggleSpatialBypass() }
                    )
                }
                currentTab == ScreenTab.SETTINGS -> {
                    SettingsScreen(
                        studioEffects = studioEffects,
                        spatialMode = spatialMode,
                        spatialParams = spatialParams,
                        visualizerMode = visualizerMode,
                        isAmoledBlack = isAmoledBlack,
                        sortOrder = sortOrder,
                        onStudioEffectsChange = { viewModel.updateStudioEffects(it) },
                        onSpatialModeChange = { viewModel.setSpatialMode(it) },
                        onParamsChange = { viewModel.updateSpatialParams(it) },
                        onVisualizerModeChange = { viewModel.setVisualizerMode(it) },
                        onToggleAmoled = { viewModel.toggleAmoledBlack() },
                        onSortOrderChange = { viewModel.setSortOrder(it) },
                        onRescanLibrary = { viewModel.rescanMedia() },
                        onOpenPrivacyCenter = { viewModel.togglePrivacyCenter(true) },
                        onClearConversionHistory = { viewModel.clearAllConversionHistory() },
                        onNavigateTab = { viewModel.navigateToTab(it) }
                    )
                }
                currentTab == ScreenTab.NOW_PLAYING -> {
                    NowPlayingScreen(
                        track = currentTrack,
                        isPlaying = isPlaying,
                        currentPositionMs = currentPositionMs,
                        durationMs = durationMs,
                        spatialMode = spatialMode,
                        repeatMode = repeatMode,
                        isShuffle = isShuffle,
                        sleepTimerRemainingSec = sleepTimerSec,
                        trajectoryPoint = trajectoryPoint,
                        spectrumBars = spectrumBars,
                        audioAmplitude = audioAmplitude,
                        panPosition = panPosition,
                        isVisualizer3DView = isVisualizer3DView,
                        onBack = { viewModel.navigateToTab(ScreenTab.HOME) },
                        onPlayPause = { viewModel.togglePlayPause() },
                        onSeek = { viewModel.seekTo(it) },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onSpatialModeChange = { viewModel.setSpatialMode(it) },
                        onRepeatToggle = {
                            val nextRepeat = when (repeatMode) {
                                com.example.model.RepeatMode.OFF -> com.example.model.RepeatMode.ALL
                                com.example.model.RepeatMode.ALL -> com.example.model.RepeatMode.ONE
                                com.example.model.RepeatMode.ONE -> com.example.model.RepeatMode.OFF
                            }
                            viewModel.setRepeatMode(nextRepeat)
                        },
                        onShuffleToggle = { viewModel.toggleShuffle() },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) },
                        onOpenSpatialPanel = { viewModel.toggleSpatialPanel(true) },
                        onOpenEqualizer = { viewModel.navigateToTab(ScreenTab.STUDIO) },
                        onOpenQueue = { viewModel.navigateToTab(ScreenTab.QUEUE) },
                        onOpenLyrics = { viewModel.toggleLyricsView(true) },
                        onOpenSleepTimer = { showSleepTimerDialog = true },
                        onToggleVisualizerView = { viewModel.toggleVisualizerView() }
                    )
                }
                currentTab == ScreenTab.EQUALIZER -> {
                    EqualizerScreen(
                        bands = equalizerBands,
                        onBandChange = { idx, lvl -> viewModel.setEqualizerBandLevel(idx, lvl) },
                        onBassBoostChange = { viewModel.setBassBoost(it) },
                        onBack = { viewModel.navigateToTab(ScreenTab.STUDIO) }
                    )
                }
                currentTab == ScreenTab.QUEUE -> {
                    QueueScreen(
                        queue = queue,
                        currentTrack = currentTrack,
                        onTrackSelect = { viewModel.playTrack(it) },
                        onMoveTrack = { from, to -> viewModel.reorderQueue(from, to) },
                        onRemoveTrack = { viewModel.removeFromQueue(it) },
                        onClearQueue = { viewModel.clearQueue() },
                        onBack = { viewModel.navigateToTab(ScreenTab.HOME) }
                    )
                }
                currentTab == ScreenTab.ABOUT -> {
                    AboutScreen(onBack = { viewModel.navigateToTab(ScreenTab.SETTINGS) })
                }
                currentTab == ScreenTab.CONVERSION_HISTORY -> {
                    ConversionHistoryScreen(
                        historyItems = conversionHistory,
                        onPlayFile = { viewModel.playTrack(it) },
                        onShareFile = { viewModel.shareExportedFile(context, it) },
                        onDeleteHistory = { viewModel.deleteConversionHistory(it) },
                        onBack = { viewModel.navigateToTab(ScreenTab.SETTINGS) }
                    )
                }
            }
        }
    }

    // Modal: MKx Spatial Converter Panel
    if (isSpatialPanelOpen) {
        SpatialConverterPanel(
            currentTrack = currentTrack,
            currentMode = spatialMode,
            currentParams = spatialParams,
            presets = spatialPresets,
            conversionState = conversionState,
            onModeChange = { viewModel.setSpatialMode(it) },
            onParamsChange = { viewModel.updateSpatialParams(it) },
            onApplyPreset = { viewModel.applyPreset(it) },
            onSavePreset = { viewModel.saveCustomPreset(it) },
            onStartConversion = { trk, mode, params, fmt ->
                viewModel.startSpatialConversion(trk, mode, params, fmt)
            },
            onShareExportedFile = { viewModel.shareExportedFile(context, it) },
            onPlayTrack = { viewModel.playTrack(it) },
            onDismissConversionState = { viewModel.dismissConversionDialog() },
            onDismiss = { viewModel.toggleSpatialPanel(false) }
        )
    }

    // Modal: Batch Converter Dialog
    if (showBatchConverter) {
        BatchConverterDialog(
            allTracks = allTracks,
            batchQueue = batchQueue,
            isBatchRunning = isBatchRunning,
            onAddJobs = { trks, mode, params, fmt -> viewModel.addBatchJobs(trks, mode, params, fmt) },
            onStartBatch = { viewModel.startBatchProcessing() },
            onCancelJob = { viewModel.cancelBatchJob(it) },
            onClearBatchQueue = { viewModel.clearBatchQueue() },
            onDismiss = { viewModel.toggleBatchConverter(false) }
        )
    }

    // Modal: Privacy Center Dialog
    if (showPrivacyCenter) {
        PrivacyCenterDialog(
            onDismiss = { viewModel.togglePrivacyCenter(false) }
        )
    }

    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        SleepTimerDialog(
            remainingSeconds = sleepTimerSec,
            onSetTimer = { viewModel.startSleepTimer(it) },
            onCancelTimer = { viewModel.cancelSleepTimer() },
            onDismiss = { showSleepTimerDialog = false }
        )
    }
}
