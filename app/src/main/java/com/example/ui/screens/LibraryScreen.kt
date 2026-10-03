package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PlaylistEntity
import com.example.model.AudioTrackItem
import com.example.model.ConversionHistoryItem
import com.example.model.LibrarySortOrder
import com.example.model.SpatialMode
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple
import com.example.viewmodel.LibrarySubTab
import com.example.viewmodel.SmartLibraryFilter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryScreen(
    tracks: List<AudioTrackItem>,
    spatialTracks: List<AudioTrackItem>,
    playlists: List<PlaylistEntity>,
    historyItems: List<ConversionHistoryItem>,
    currentTrack: AudioTrackItem?,
    isPlaying: Boolean,
    subTab: LibrarySubTab,
    smartFilter: SmartLibraryFilter,
    sortOrder: LibrarySortOrder,
    searchQuery: String,
    showHeadphoneAlert: Boolean,
    onSubTabChange: (LibrarySubTab) -> Unit,
    onSmartFilterChange: (SmartLibraryFilter) -> Unit,
    onSortOrderChange: (LibrarySortOrder) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTrackSelect: (AudioTrackItem) -> Unit,
    onImportAudioFile: (Uri) -> Unit,
    onRescanLibrary: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onAddTrackToPlaylist: (playlistId: Long, trackId: Long) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    onImportM3u: (Uri, String) -> Unit,
    onExportM3u: (PlaylistEntity) -> Unit,
    onOpenBatchConverter: () -> Unit,
    onDismissHeadphoneAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            onImportAudioFile(uri)
        }
    }

    val m3uPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = uri.lastPathSegment?.substringBeforeLast('.') ?: "Imported Playlist"
            onImportM3u(uri, name)
        }
    }

    var showNewPlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var trackForPlaylist by remember { mutableStateOf<AudioTrackItem?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .testTag("library_screen")
    ) {
        // App Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF0F172A), CircleShape)
                        .border(1.5.dp, NeonCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Smart Music Library",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "${tracks.size} tracks available offline",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenBatchConverter,
                    modifier = Modifier.testTag("batch_converter_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Batch Convert",
                        tint = VortexPurple
                    )
                }
                IconButton(
                    onClick = { filePickerLauncher.launch(arrayOf("audio/*")) },
                    modifier = Modifier.testTag("import_file_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileOpen,
                        contentDescription = "Import Audio",
                        tint = NeonCyan
                    )
                }
                IconButton(onClick = onRescanLibrary) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rescan",
                        tint = TextMuted
                    )
                }
            }
        }

        // Search Field & Sort Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF131D31),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 1.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search songs, artists, albums, genres...", color = TextMuted, fontSize = 12.5.sp) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_field")
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sort Dropdown
            Box {
                IconButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF131D31), RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort", tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.background(Color(0xFF131D31))
                ) {
                    LibrarySortOrder.values().forEach { order ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = order.displayName,
                                    color = if (sortOrder == order) NeonCyan else TextWhite,
                                    fontWeight = if (sortOrder == order) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortOrderChange(order)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Headphone Recommendation Banner
        AnimatedVisibility(visible = showHeadphoneAlert) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2844)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "For the full binaural spatial experience, use stereo headphones or earbuds.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWhite, fontSize = 11.sp),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismissHeadphoneAlert, modifier = Modifier.size(22.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Smart Filter Chips Row (Recently Played, Favorites, Most Played, etc.)
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SmartFilterChip("All Songs", SmartLibraryFilter.ALL, smartFilter == SmartLibraryFilter.ALL) { onSmartFilterChange(SmartLibraryFilter.ALL) }
            SmartFilterChip("Favorites", SmartLibraryFilter.FAVORITES, smartFilter == SmartLibraryFilter.FAVORITES) { onSmartFilterChange(SmartLibraryFilter.FAVORITES) }
            SmartFilterChip("Recently Played", SmartLibraryFilter.RECENTLY_PLAYED, smartFilter == SmartLibraryFilter.RECENTLY_PLAYED) { onSmartFilterChange(SmartLibraryFilter.RECENTLY_PLAYED) }
            SmartFilterChip("Most Played", SmartLibraryFilter.MOST_PLAYED, smartFilter == SmartLibraryFilter.MOST_PLAYED) { onSmartFilterChange(SmartLibraryFilter.MOST_PLAYED) }
            SmartFilterChip("Recently Added", SmartLibraryFilter.RECENTLY_ADDED, smartFilter == SmartLibraryFilter.RECENTLY_ADDED) { onSmartFilterChange(SmartLibraryFilter.RECENTLY_ADDED) }
            SmartFilterChip("Duplicates", SmartLibraryFilter.DUPLICATES, smartFilter == SmartLibraryFilter.DUPLICATES) { onSmartFilterChange(SmartLibraryFilter.DUPLICATES) }
            SmartFilterChip("Corrupted", SmartLibraryFilter.CORRUPTED, smartFilter == SmartLibraryFilter.CORRUPTED) { onSmartFilterChange(SmartLibraryFilter.CORRUPTED) }
        }

        // Sub Tabs: Songs, Albums, Artists, Genres, Folders, Playlists, 8D/24D Spatial, History
        val subTabs = LibrarySubTab.values()
        ScrollableTabRow(
            selectedTabIndex = subTabs.indexOf(subTab),
            containerColor = Color.Transparent,
            contentColor = NeonCyan,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[subTabs.indexOf(subTab)]),
                    color = NeonCyan,
                    height = 2.5.dp
                )
            },
            divider = {}
        ) {
            subTabs.forEach { tabItem ->
                val isSelected = tabItem == subTab
                val label = when (tabItem) {
                    LibrarySubTab.SONGS -> "Songs (${tracks.size})"
                    LibrarySubTab.ALBUMS -> "Albums"
                    LibrarySubTab.ARTISTS -> "Artists"
                    LibrarySubTab.GENRES -> "Genres"
                    LibrarySubTab.FOLDERS -> "Folders"
                    LibrarySubTab.PLAYLISTS -> "Playlists (${playlists.size})"
                    LibrarySubTab.CONVERTED -> "Spatial Converted (${spatialTracks.size})"
                    LibrarySubTab.HISTORY -> "Export History (${historyItems.size})"
                }
                Tab(
                    selected = isSelected,
                    onClick = { onSubTabChange(tabItem) },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextMuted,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Content Area Based on Selected SubTab
        when (subTab) {
            LibrarySubTab.SONGS -> {
                TrackListContent(
                    tracks = tracks,
                    currentTrack = currentTrack,
                    isPlaying = isPlaying,
                    onTrackSelect = onTrackSelect,
                    onAddToPlaylist = { trackForPlaylist = it }
                )
            }
            LibrarySubTab.CONVERTED -> {
                TrackListContent(
                    tracks = spatialTracks,
                    currentTrack = currentTrack,
                    isPlaying = isPlaying,
                    emptyMessage = "No spatial converted tracks yet. Use MKx STUDIO or Spatial Converter to export any song into 8D, 16D, or 24D!",
                    onTrackSelect = onTrackSelect,
                    onAddToPlaylist = { trackForPlaylist = it }
                )
            }
            LibrarySubTab.ALBUMS -> {
                AlbumsGridContent(tracks = tracks, onTrackSelect = onTrackSelect)
            }
            LibrarySubTab.ARTISTS -> {
                ArtistsListContent(tracks = tracks, onTrackSelect = onTrackSelect)
            }
            LibrarySubTab.GENRES -> {
                GenresListContent(tracks = tracks, onTrackSelect = onTrackSelect)
            }
            LibrarySubTab.FOLDERS -> {
                FoldersListContent(tracks = tracks, onTrackSelect = onTrackSelect)
            }
            LibrarySubTab.PLAYLISTS -> {
                PlaylistsContent(
                    playlists = playlists,
                    onCreatePlaylistClick = { showNewPlaylistDialog = true },
                    onImportM3uClick = { m3uPickerLauncher.launch(arrayOf("*/*")) },
                    onExportM3u = onExportM3u,
                    onDeletePlaylist = onDeletePlaylist
                )
            }
            LibrarySubTab.HISTORY -> {
                ConversionHistoryScreen(
                    historyItems = historyItems,
                    onPlayFile = onTrackSelect,
                    onShareFile = {},
                    onDeleteHistory = {},
                    onBack = { onSubTabChange(LibrarySubTab.SONGS) }
                )
            }
        }
    }

    // New Playlist Dialog
    if (showNewPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showNewPlaylistDialog = false },
            title = { Text("Create New Playlist") },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist Name") },
                    placeholder = { Text("e.g. Late Night 8D Vibes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            onCreatePlaylist(newPlaylistName.trim())
                            showNewPlaylistDialog = false
                            newPlaylistName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showNewPlaylistDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextMuted)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF131D31),
            titleContentColor = TextWhite,
            textContentColor = TextWhite
        )
    }

    // Add Track to Playlist Dialog
    if (trackForPlaylist != null) {
        val targetTrack = trackForPlaylist!!
        AlertDialog(
            onDismissRequest = { trackForPlaylist = null },
            title = { Text("Add to Playlist") },
            text = {
                Column {
                    Text(
                        text = "Select playlist for \"${targetTrack.title}\":",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (playlists.isEmpty()) {
                        Text("No playlists yet. Create one in the Playlists tab.", color = TextMuted)
                    } else {
                        playlists.forEach { pl ->
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onAddTrackToPlaylist(pl.id, targetTrack.id)
                                        trackForPlaylist = null
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Icon(Icons.Default.PlaylistPlay, contentDescription = null, tint = NeonCyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(pl.name, color = TextWhite, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { trackForPlaylist = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Done")
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}

@Composable
private fun SmartFilterChip(
    label: String,
    filter: SmartLibraryFilter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) NeonCyan else Color(0xFF131D31),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonCyan else Color(0xFF22314E)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else TextWhite,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TrackListContent(
    tracks: List<AudioTrackItem>,
    currentTrack: AudioTrackItem?,
    isPlaying: Boolean,
    emptyMessage: String = "No audio tracks found. Use 'Import' or 'Rescan' to add music.",
    onTrackSelect: (AudioTrackItem) -> Unit,
    onAddToPlaylist: (AudioTrackItem) -> Unit
) {
    if (tracks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            items(tracks, key = { it.id }) { track ->
                val isCurrent = currentTrack?.id == track.id
                TrackRowItem(
                    track = track,
                    isCurrent = isCurrent,
                    isPlaying = isPlaying && isCurrent,
                    onSelect = { onTrackSelect(track) },
                    onAddToPlaylist = { onAddToPlaylist(track) }
                )
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun TrackRowItem(
    track: AudioTrackItem,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onSelect: () -> Unit,
    onAddToPlaylist: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = if (isCurrent) Color(0xFF162035) else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 3.dp)
            .testTag("track_item_${track.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Artwork / Spatial Tag Thumbnail
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .border(
                        1.dp,
                        if (isCurrent) NeonCyan else Color(0xFF334155),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                if (track.isCorrupted) {
                    Icon(Icons.Default.Warning, contentDescription = "Corrupted", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                } else if (track.spatialMode != SpatialMode.STEREO) {
                    Text(
                        text = track.spatialMode.dimensionLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = when (track.spatialMode) {
                                SpatialMode.SPATIAL_8D -> NeonCyan
                                SpatialMode.SPATIAL_16D -> ElectricMagenta
                                SpatialMode.SPATIAL_24D -> VortexPurple
                                SpatialMode.STEREO -> TextWhite
                            },
                            fontSize = 11.sp
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isCurrent) NeonCyan else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Track Title & Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) NeonCyan else TextWhite
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (track.isDemo) {
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, NeonCyan)
                        ) {
                            Text(
                                text = "DEMO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "${track.artist} • ${formatTime(track.durationMs)}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (track.playCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${track.playCount} plays",
                            style = MaterialTheme.typography.bodySmall.copy(color = AmberGlow, fontSize = 10.sp)
                        )
                    }
                }
            }

            // Options menu button
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = TextMuted)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFF131D31))
                ) {
                    DropdownMenuItem(
                        text = { Text("Add to Playlist", color = TextWhite) },
                        onClick = {
                            showMenu = false
                            onAddToPlaylist()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumsGridContent(
    tracks: List<AudioTrackItem>,
    onTrackSelect: (AudioTrackItem) -> Unit
) {
    val albums = tracks.groupBy { it.album }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(albums.keys.toList()) { albumName ->
            val albumTracks = albums[albumName] ?: emptyList()
            Surface(
                color = Color(0xFF131D31),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22314E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable {
                        if (albumTracks.isNotEmpty()) onTrackSelect(albumTracks.first())
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.Album, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(albumName, color = TextWhite, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text("${albumTracks.size} songs • ${albumTracks.firstOrNull()?.artist ?: ""}", color = TextMuted, fontSize = 11.5.sp)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
private fun ArtistsListContent(
    tracks: List<AudioTrackItem>,
    onTrackSelect: (AudioTrackItem) -> Unit
) {
    val artists = tracks.groupBy { it.artist }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(artists.keys.toList()) { artistName ->
            val artistTracks = artists[artistName] ?: emptyList()
            Surface(
                color = Color(0xFF131D31),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22314E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable {
                        if (artistTracks.isNotEmpty()) onTrackSelect(artistTracks.first())
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = ElectricMagenta, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(artistName, color = TextWhite, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text("${artistTracks.size} songs", color = TextMuted, fontSize = 11.5.sp)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
private fun GenresListContent(
    tracks: List<AudioTrackItem>,
    onTrackSelect: (AudioTrackItem) -> Unit
) {
    val genres = tracks.groupBy { it.genre }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(genres.keys.toList()) { genreName ->
            val genreTracks = genres[genreName] ?: emptyList()
            Surface(
                color = Color(0xFF131D31),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22314E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable {
                        if (genreTracks.isNotEmpty()) onTrackSelect(genreTracks.first())
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(genreName, color = TextWhite, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text("${genreTracks.size} songs", color = TextMuted, fontSize = 11.5.sp)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
private fun FoldersListContent(
    tracks: List<AudioTrackItem>,
    onTrackSelect: (AudioTrackItem) -> Unit
) {
    val folders = tracks.groupBy { it.folderName }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(folders.keys.toList()) { folderName ->
            val folderTracks = folders[folderName] ?: emptyList()
            Surface(
                color = Color(0xFF131D31),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22314E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable {
                        if (folderTracks.isNotEmpty()) onTrackSelect(folderTracks.first())
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(folderName, color = TextWhite, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text("${folderTracks.size} audio files", color = TextMuted, fontSize = 11.5.sp)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
private fun PlaylistsContent(
    playlists: List<PlaylistEntity>,
    onCreatePlaylistClick: () -> Unit,
    onImportM3uClick: () -> Unit,
    onExportM3u: (PlaylistEntity) -> Unit,
    onDeletePlaylist: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCreatePlaylistClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Playlist", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = onImportM3uClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = TextWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Import .M3U", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(playlists) { playlist ->
                Surface(
                    color = Color(0xFF131D31),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22314E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(Icons.Default.PlaylistPlay, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(playlist.name, color = TextWhite, fontWeight = FontWeight.Bold)
                            Text("Playlist", color = TextMuted, fontSize = 11.sp)
                        }

                        IconButton(onClick = { onExportM3u(playlist) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Download, contentDescription = "Export M3U", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }

                        IconButton(onClick = { onDeletePlaylist(playlist.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}
