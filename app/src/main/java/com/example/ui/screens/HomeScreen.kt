package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioTrackItem
import com.example.model.SpatialMode
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple
import com.example.viewmodel.ScreenTab
import com.example.viewmodel.SmartLibraryFilter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    currentTrack: AudioTrackItem?,
    isPlaying: Boolean,
    recentTracks: List<AudioTrackItem>,
    favoriteTracks: List<AudioTrackItem>,
    allTracks: List<AudioTrackItem>,
    spatialMode: SpatialMode,
    isBypassed: Boolean,
    onTrackSelect: (AudioTrackItem) -> Unit,
    onPlayPause: () -> Unit,
    onNavigateTab: (ScreenTab) -> Unit,
    onOpenSpatialPanel: () -> Unit,
    onToggleBypass: () -> Unit,
    onOpenBatchConverter: () -> Unit,
    onOpenPrivacyCenter: () -> Unit,
    onSelectFilter: (SmartLibraryFilter) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("home_screen")
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Brand Hero Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF0F172A), CircleShape)
                        .border(1.5.dp, NeonCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MKxPLAYER",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = "Your music, in every dimension.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyanGlow, fontSize = 11.sp)
                    )
                }
            }

            Surface(
                color = Color(0xFF131D31),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.clickable { onOpenPrivacyCenter() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("100% Local", fontSize = 10.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // A/B Audio Comparison Quick Banner
        Surface(
            color = if (isBypassed) Color(0xFF1E293B) else Color(0xFF162035),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (!isBypassed) NeonCyan else Color(0xFF334155)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleBypass() }
                .testTag("ab_comparison_banner")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (!isBypassed) NeonCyan.copy(alpha = 0.2f) else Color(0xFF334155), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = if (!isBypassed) NeonCyan else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBypassed) "ORIGINAL STEREO" else "${spatialMode.dimensionLabel} SPATIAL ACTIVE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (!isBypassed) NeonCyan else TextWhite,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap to toggle A/B Audio Comparison",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    color = if (!isBypassed) NeonCyan else Color(0xFF334155),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (!isBypassed) "PROCESSED" else "ORIGINAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isBypassed) Color.Black else TextWhite,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue Listening Card (if track active)
        if (currentTrack != null) {
            Text(
                text = "CONTINUE LISTENING",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab(ScreenTab.NOW_PLAYING) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF162035))
                            .border(1.5.dp, NeonCyan, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentTrack.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${currentTrack.artist} • [${spatialMode.dimensionLabel} Spatial]",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.5.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier
                            .size(46.dp)
                            .background(NeonCyan, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Mode Launchers (8D, 16D, 24D)
        Text(
            text = "SPATIAL AUDIO MODES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModeLaunchCard(
                title = "8D Classic",
                desc = "360° Horizontal Orbit",
                accent = NeonCyan,
                modifier = Modifier.weight(1f),
                onClick = onOpenSpatialPanel
            )
            ModeLaunchCard(
                title = "16D Dynamic",
                desc = "Figure-8 Infinity Loop",
                accent = ElectricMagenta,
                modifier = Modifier.weight(1f),
                onClick = onOpenSpatialPanel
            )
            ModeLaunchCard(
                title = "24D Vortex",
                desc = "Spherical 3D Vortex",
                accent = VortexPurple,
                modifier = Modifier.weight(1f),
                onClick = onOpenSpatialPanel
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Smart Library Filter Chips
        Text(
            text = "SMART MUSIC LIBRARY",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmartChip("All Songs (${allTracks.size})", Icons.Default.MusicNote) {
                onSelectFilter(SmartLibraryFilter.ALL)
                onNavigateTab(ScreenTab.LIBRARY)
            }
            SmartChip("Favorites (${favoriteTracks.size})", Icons.Default.Favorite, ElectricMagenta) {
                onSelectFilter(SmartLibraryFilter.FAVORITES)
                onNavigateTab(ScreenTab.LIBRARY)
            }
            SmartChip("Recently Played", Icons.Default.History, CyanGlow) {
                onSelectFilter(SmartLibraryFilter.RECENTLY_PLAYED)
                onNavigateTab(ScreenTab.LIBRARY)
            }
            SmartChip("Most Played", Icons.Default.AutoAwesome, AmberGlow) {
                onSelectFilter(SmartLibraryFilter.MOST_PLAYED)
                onNavigateTab(ScreenTab.LIBRARY)
            }
            SmartChip("Batch Converter", Icons.Default.QueueMusic, VortexPurple) {
                onOpenBatchConverter()
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preloaded Demos Showcase (Instant Spatial Testing)
        Text(
            text = "SYNTHETIC SPATIAL DEMOS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        val demoTracks = allTracks.filter { it.isDemo }
        if (demoTracks.isNotEmpty()) {
            demoTracks.forEach { demo ->
                Surface(
                    color = Color(0xFF131D31),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22314E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onTrackSelect(demo) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = demo.spatialMode.dimensionLabel,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(demo.title, color = TextWhite, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text("${demo.artist} • ${demo.spatialMode.subtitle}", color = TextMuted, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = NeonCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun ModeLaunchCard(
    title: String,
    desc: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.5f)),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = accent,
                    fontSize = 13.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 9.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SmartChip(
    label: String,
    icon: ImageVector,
    tint: Color = NeonCyan,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFF162035),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF24365C)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
        }
    }
}
