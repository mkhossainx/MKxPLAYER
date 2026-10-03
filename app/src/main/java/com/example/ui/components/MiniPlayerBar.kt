package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioTrackItem
import com.example.model.SpatialMode
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VortexPurple

@Composable
fun MiniPlayerBar(
    track: AudioTrackItem?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    spatialMode: SpatialMode,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (track == null) return

    val progress = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val modeColor = when (spatialMode) {
        SpatialMode.SPATIAL_8D -> NeonCyan
        SpatialMode.SPATIAL_16D -> ElectricMagenta
        SpatialMode.SPATIAL_24D -> VortexPurple
        SpatialMode.STEREO -> TextMuted
    }

    Surface(
        color = Color(0xFF131D31),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22314E)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("mini_player_bar")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Animated thin progress line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = modeColor,
                trackColor = Color(0xFF1E293B)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini spinning vinyl / spatial indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                        .border(1.5.dp, modeColor, CircleShape)
                ) {
                    Text(
                        text = spatialMode.dimensionLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = modeColor,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Track Title & Artist
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${spatialMode.subtitle}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = modeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Play / Pause Button
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(38.dp)
                        .background(modeColor.copy(alpha = 0.15f), CircleShape)
                        .testTag("mini_player_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = modeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Skip Next Button
                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = TextWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
