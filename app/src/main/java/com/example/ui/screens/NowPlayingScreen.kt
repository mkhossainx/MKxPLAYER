package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SpatialAudioProcessor
import com.example.model.AudioTrackItem
import com.example.model.RepeatMode
import com.example.model.SpatialMode
import com.example.ui.components.Spatial3DVisualizer
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple

@Composable
fun NowPlayingScreen(
    track: AudioTrackItem?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    spatialMode: SpatialMode,
    repeatMode: RepeatMode,
    isShuffle: Boolean,
    sleepTimerRemainingSec: Int?,
    trajectoryPoint: SpatialAudioProcessor.SpatialTrajectoryPoint,
    spectrumBars: FloatArray,
    audioAmplitude: Float = ((trajectoryPoint.leftGain + trajectoryPoint.rightGain) * 0.5f).coerceIn(0.1f, 1.0f),
    panPosition: Float = trajectoryPoint.x.coerceIn(-1.0f, 1.0f),
    isVisualizer3DView: Boolean,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSpatialModeChange: (SpatialMode) -> Unit,
    onRepeatToggle: () -> Unit,
    onShuffleToggle: () -> Unit,
    onFavoriteToggle: (AudioTrackItem) -> Unit,
    onOpenSpatialPanel: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onToggleVisualizerView: () -> Unit
) {
    if (track == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(VoidBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("No track selected", color = TextMuted)
        }
        return
    }

    val vinylRotation = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            vinylRotation.animateTo(
                targetValue = vinylRotation.value + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(10000, easing = LinearEasing),
                    repeatMode = AnimRepeatMode.Restart
                )
            )
        }
    }

    val modeColor = when (spatialMode) {
        SpatialMode.SPATIAL_8D -> NeonCyan
        SpatialMode.SPATIAL_16D -> ElectricMagenta
        SpatialMode.SPATIAL_24D -> VortexPurple
        SpatialMode.STEREO -> TextMuted
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF090D16),
                        VoidBlack
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("now_playing_screen")
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PLAYING IN ${spatialMode.dimensionLabel} SPATIAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = modeColor,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = spatialMode.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
                )
            }

            Row {
                IconButton(onClick = onOpenSleepTimer) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Sleep Timer",
                        tint = if (sleepTimerRemainingSec != null) NeonCyan else TextMuted
                    )
                }
                IconButton(onClick = onOpenEqualizer) {
                    Icon(Icons.Default.Equalizer, contentDescription = "Equalizer", tint = TextWhite)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center View: Visualizer or Vinyl Album Art
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isVisualizer3DView) {
                Spatial3DVisualizer(
                    mode = spatialMode,
                    trajectoryPoint = trajectoryPoint,
                    spectrumBars = spectrumBars,
                    audioAmplitude = audioAmplitude,
                    panPosition = panPosition,
                    isPlaying = isPlaying,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Vinyl Record Style Album Artwork
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF090D16))
                        .border(4.dp, Color(0xFF1E293B), CircleShape)
                        .border(1.5.dp, modeColor.copy(alpha = 0.6f), CircleShape)
                        .rotate(vinylRotation.value)
                ) {
                    // Grooves
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .border(1.dp, Color(0x22FFFFFF), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .border(1.dp, Color(0x22FFFFFF), CircleShape)
                    )

                    // Center Label
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(NeonCyan, VortexPurple, ElectricMagenta)
                                ),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // View Mode Switcher Button (3D Orbit vs Album Art)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                color = Color(0xFF162035),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF24365C)),
                modifier = Modifier.clickable { onToggleVisualizerView() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isVisualizer3DView) Icons.Default.GraphicEq else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVisualizer3DView) "3D Orbit Visualizer active" else "Switch to 3D Visualizer",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextWhite, fontSize = 11.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Track Details & Favorite
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 20.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${track.artist} • ${track.album}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { onFavoriteToggle(track) }) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (track.isFavorite) ElectricMagenta else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Spatial Mode Selector Chips + Spatial Converter Trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val modes = listOf(SpatialMode.STEREO, SpatialMode.SPATIAL_8D, SpatialMode.SPATIAL_16D, SpatialMode.SPATIAL_24D)
            modes.forEach { mode ->
                val isSelected = mode == spatialMode
                Surface(
                    color = if (isSelected) Color(0xFF1E293B) else Color(0xFF101625),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) modeColor else Color(0xFF22314E)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSpatialModeChange(mode) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = mode.dimensionLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) modeColor else TextWhite,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Converter Settings Button
            Surface(
                color = Color(0xFF1B2844),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                modifier = Modifier.clickable { onOpenSpatialPanel() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Tuning", tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "FX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Timeline Seekbar
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = currentPositionMs.toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..(durationMs.toFloat().coerceAtLeast(1f)),
                colors = SliderDefaults.colors(
                    thumbColor = modeColor,
                    activeTrackColor = modeColor,
                    inactiveTrackColor = Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .testTag("timeline_slider")
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPositionMs),
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                )
                Text(
                    text = formatTime(durationMs),
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Primary Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shuffle
            IconButton(onClick = onShuffleToggle) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffle) NeonCyan else TextMuted
                )
            }

            // Previous
            IconButton(
                onClick = onSkipPrevious,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = TextWhite,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Play / Pause (Hero Neon Button)
            Surface(
                shape = CircleShape,
                color = modeColor,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(64.dp)
                    .clickable { onPlayPause() }
                    .testTag("now_playing_play_pause")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            // Next
            IconButton(
                onClick = onSkipNext,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = TextWhite,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Repeat
            IconButton(onClick = onRepeatToggle) {
                Icon(
                    imageVector = when (repeatMode) {
                        RepeatMode.ONE -> Icons.Default.RepeatOne
                        RepeatMode.ALL -> Icons.Default.Repeat
                        RepeatMode.OFF -> Icons.Default.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode != RepeatMode.OFF) NeonCyan else TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Auxiliary Actions (Lyrics, Queue)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onOpenLyrics,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextMuted)
            ) {
                Icon(Icons.Default.Lyrics, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lyrics", fontSize = 12.sp)
            }

            Button(
                onClick = onOpenQueue,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextMuted)
            ) {
                Icon(Icons.Default.QueueMusic, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Up Next", fontSize = 12.sp)
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
