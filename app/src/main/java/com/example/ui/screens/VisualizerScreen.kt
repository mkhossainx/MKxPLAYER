package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SpatialAudioProcessor
import com.example.model.AudioTrackItem
import com.example.model.CustomSpatialShape
import com.example.model.OrbitDirection
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import com.example.model.VisualizerMode
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualizerScreen(
    currentTrack: AudioTrackItem?,
    isPlaying: Boolean,
    spatialMode: SpatialMode,
    spatialParams: SpatialParams,
    visualizerMode: VisualizerMode,
    trajectoryPoint: SpatialAudioProcessor.SpatialTrajectoryPoint,
    spectrumBars: FloatArray,
    audioAmplitude: Float = ((trajectoryPoint.leftGain + trajectoryPoint.rightGain) * 0.5f).coerceIn(0.1f, 1.0f),
    panPosition: Float = trajectoryPoint.x.coerceIn(-1.0f, 1.0f),
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onVisualizerModeChange: (VisualizerMode) -> Unit,
    onSpatialModeChange: (SpatialMode) -> Unit,
    onParamsChange: (SpatialParams) -> Unit,
    onToggleBypass: () -> Unit
) {
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            rotation.animateTo(
                targetValue = 360f,
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
            .background(VoidBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("visualizer_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "3D SPATIAL VISUALIZER",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Real-Time GPU 60fps Acoustic Engine",
                    style = MaterialTheme.typography.bodySmall.copy(color = NeonCyan, fontSize = 11.sp)
                )
            }

            // A/B Audio Comparison Button
            Surface(
                color = if (!spatialParams.isBypassed) NeonCyan else Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                modifier = Modifier
                    .clickable { onToggleBypass() }
                    .testTag("visualizer_ab_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = if (!spatialParams.isBypassed) Color.Black else TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (!spatialParams.isBypassed) "PROCESSED" else "ORIGINAL",
                        color = if (!spatialParams.isBypassed) Color.Black else TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visualizer Mode Selector Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            VisualizerMode.values().forEach { vMode ->
                val isSelected = visualizerMode == vMode
                Surface(
                    color = if (isSelected) Color(0xFF1E293B) else Color(0xFF101625),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonCyan else Color(0xFF22314E)
                    ),
                    modifier = Modifier.clickable { onVisualizerModeChange(vMode) }
                ) {
                    Text(
                        text = vMode.displayName,
                        color = if (isSelected) NeonCyan else TextWhite,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Main Visualizer Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFF090D18), RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height * 0.48f)
                val maxR = size.width * 0.38f

                when (visualizerMode) {
                    VisualizerMode.SPECTRUM -> {
                        // 16-band vertical frequency spectrum
                        val count = spectrumBars.size
                        val barWidth = (size.width / count) * 0.65f
                        val gap = (size.width / count) * 0.35f
                        val baseY = size.height - 12f
                        for (i in 0 until count) {
                            val h = spectrumBars[i].coerceIn(0.05f, 1f) * size.height * 0.75f
                            val x = 12f + i * (barWidth + gap)
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(NeonCyan, VortexPurple, ElectricMagenta),
                                    startY = baseY - h,
                                    endY = baseY
                                ),
                                topLeft = Offset(x, baseY - h),
                                size = androidx.compose.ui.geometry.Size(barWidth, h),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                            )
                        }
                    }

                    VisualizerMode.WAVEFORM -> {
                        // Oscilloscope Live Audio Wave
                        val wavePath = Path()
                        val steps = 80
                        for (i in 0..steps) {
                            val x = (i.toFloat() / steps) * size.width
                            val amp = if (isPlaying) sin(i * 0.35 + rotation.value * 0.1) * 60f else 0.0
                            val y = center.y + amp.toFloat()
                            if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                        }
                        drawPath(
                            path = wavePath,
                            brush = Brush.horizontalGradient(listOf(NeonCyan, ElectricMagenta, VortexPurple)),
                            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                        )
                    }

                    VisualizerMode.CIRCULAR_SPECTRUM -> {
                        // Radial Spectrum Bloom around listener
                        val count = 28
                        for (i in 0 until count) {
                            val angle = (i.toFloat() / count) * 2f * PI.toFloat()
                            val barAmp = spectrumBars[i % spectrumBars.size]
                            val r1 = maxR * 0.45f
                            val r2 = r1 + barAmp * maxR * 0.65f
                            val p1 = Offset(center.x + r1 * cos(angle), center.y + r1 * sin(angle))
                            val p2 = Offset(center.x + r2 * cos(angle), center.y + r2 * sin(angle))
                            drawLine(
                                brush = Brush.linearGradient(listOf(NeonCyan, ElectricMagenta), p1, p2),
                                start = p1,
                                end = p2,
                                strokeWidth = 4f,
                                cap = StrokeCap.Round
                            )
                        }
                        drawCircle(Color(0xFF0F172A), radius = maxR * 0.40f, center = center)
                        drawCircle(NeonCyan, radius = maxR * 0.40f, center = center, style = Stroke(2f))
                    }

                    VisualizerMode.GALAXY_PARTICLES -> {
                        // Cosmic Particle Galaxy reacting to audio
                        val rot = Math.toRadians(rotation.value.toDouble())
                        for (p in 0 until 40) {
                            val angle = rot + p * 0.3
                            val pr = (p / 40f) * maxR * 1.1f
                            val px = center.x + (pr * cos(angle)).toFloat()
                            val py = center.y + (pr * sin(angle) * 0.65f).toFloat()
                            val pColor = if (p % 2 == 0) NeonCyan else ElectricMagenta
                            drawCircle(pColor.copy(alpha = 0.75f), radius = 3.5f, center = Offset(px, py))
                        }
                        drawCircle(Color.White, radius = 6f, center = center)
                    }

                    VisualizerMode.MINIMAL -> {
                        // Minimalist glowing pulse ring
                        val pulse = spectrumBars.maxOrNull() ?: 0.5f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonCyan.copy(alpha = 0.4f), Color.Transparent),
                                center = center,
                                radius = maxR * pulse
                            ),
                            radius = maxR * pulse,
                            center = center
                        )
                        drawCircle(NeonCyan, radius = maxR * 0.5f, center = center, style = Stroke(2f))
                    }

                    else -> {
                        // 8D Orbit, 16D Infinity, 24D Vortex, or Custom Trajectory
                        drawCircle(Color(0x1800F2FE), radius = maxR, center = center, style = Stroke(1.5f))

                        // Trajectory ribbon
                        when (spatialMode) {
                            SpatialMode.SPATIAL_8D -> {
                                drawOval(
                                    brush = Brush.sweepGradient(listOf(NeonCyan, VortexPurple, ElectricMagenta, NeonCyan), center),
                                    topLeft = Offset(center.x - maxR, center.y - maxR * 0.55f),
                                    size = androidx.compose.ui.geometry.Size(maxR * 2, maxR * 1.1f),
                                    style = Stroke(3f)
                                )
                            }
                            SpatialMode.SPATIAL_16D -> {
                                val path16 = Path()
                                val steps = 100
                                for (i in 0..steps) {
                                    val t = (i.toFloat() / steps) * 2f * PI.toFloat()
                                    val d = 1f + sin(t) * sin(t)
                                    val px = center.x + (maxR * cos(t)) / d
                                    val py = center.y + (maxR * sin(t) * cos(t) * 1.1f) / d
                                    if (i == 0) path16.moveTo(px, py) else path16.lineTo(px, py)
                                }
                                drawPath(path16, brush = Brush.linearGradient(listOf(NeonCyan, ElectricMagenta)), style = Stroke(3f))
                            }
                            SpatialMode.SPATIAL_24D, SpatialMode.STEREO -> {
                                for (r in 1..3) {
                                    val rr = maxR * (r / 3f)
                                    drawOval(
                                        brush = Brush.sweepGradient(listOf(NeonCyan, ElectricMagenta, VortexPurple, NeonCyan), center),
                                        topLeft = Offset(center.x - rr, center.y - rr * 0.7f),
                                        size = androidx.compose.ui.geometry.Size(rr * 2, rr * 1.4f),
                                        style = Stroke(1.8f)
                                    )
                                }
                            }
                        }

                        // Central binaural listener reacting to pan position
                        val leftEarColor = if (panPosition < -0.1f) NeonCyan else Color(0xFF007A87)
                        val rightEarColor = if (panPosition > 0.1f) ElectricMagenta else Color(0xFF8B0045)
                        val earGlow = (0.3f + audioAmplitude * 0.7f).coerceIn(0.2f, 1f)

                        drawCircle(Color(0xFF1E293B), radius = 16f, center = center)
                        drawCircle(NeonCyan, radius = 16f, center = center, style = Stroke(2f))

                        // Left ear cushion
                        drawRoundRect(
                            color = leftEarColor.copy(alpha = if (panPosition < 0f) earGlow else 0.5f),
                            topLeft = Offset(center.x - 22f, center.y - 8f),
                            size = androidx.compose.ui.geometry.Size(6f, 16f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                        )
                        // Right ear cushion
                        drawRoundRect(
                            color = rightEarColor.copy(alpha = if (panPosition > 0f) earGlow else 0.5f),
                            topLeft = Offset(center.x + 16f, center.y - 8f),
                            size = androidx.compose.ui.geometry.Size(6f, 16f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                        )

                        // Animated live audio node (reacting to real-time panPosition and audioAmplitude)
                        val nodeX = center.x + panPosition * maxR
                        val nodeY = center.y + trajectoryPoint.y * maxR * 0.55f - trajectoryPoint.z * maxR * 0.45f
                        val nodeOffset = Offset(nodeX, nodeY)
                        val nodeRadius = 8f + audioAmplitude * 10f
                        val auraRadius = 20f + audioAmplitude * 25f

                        val beamColor = when {
                            panPosition < -0.15f -> NeonCyan
                            panPosition > 0.15f -> ElectricMagenta
                            else -> VortexPurple
                        }

                        // Radiant aura pulsing with audio amplitude
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(beamColor.copy(alpha = 0.65f * audioAmplitude), Color.Transparent),
                                center = nodeOffset,
                                radius = auraRadius
                            ),
                            radius = auraRadius,
                            center = nodeOffset
                        )

                        // Core audio node
                        drawCircle(Color.White, radius = nodeRadius, center = nodeOffset)
                        drawCircle(beamColor, radius = nodeRadius * 0.65f, center = nodeOffset)

                        // Connecting sonic beam with width reacting to amplitude
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(beamColor.copy(alpha = 0.7f * audioAmplitude), Color.Transparent),
                                start = nodeOffset,
                                end = center
                            ),
                            start = nodeOffset,
                            end = center,
                            strokeWidth = 2f + audioAmplitude * 3.5f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // HUD Overlays
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(color = Color(0xCC131D31), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "${spatialMode.dimensionLabel} REAL-TIME DSP",
                        color = modeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(color = Color(0xCC131D31), shape = RoundedCornerShape(8.dp)) {
                    val panPercent = (panPosition * 100).toInt()
                    val panLabel = when {
                        panPercent < -5 -> "PAN: L ${abs(panPercent)}%"
                        panPercent > 5 -> "PAN: R $panPercent%"
                        else -> "PAN: 0%"
                    }
                    val ampLabel = "AMP: ${(audioAmplitude * 100).toInt().coerceIn(0, 100)}%"
                    Text(
                        text = "$panLabel | $ampLabel",
                        color = AmberGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Custom Trajectory Editor Module
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CUSTOM SPATIAL TRAJECTORY EDITOR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Shape Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CustomSpatialShape.values().forEach { shape ->
                        val isSelected = spatialParams.customShape == shape
                        Surface(
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else Color(0xFF22314E)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onParamsChange(spatialParams.copy(customShape = shape)) }
                        ) {
                            Text(
                                text = shape.displayName.substringBefore(" "),
                                color = if (isSelected) NeonCyan else TextWhite,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direction: Clockwise / Anti-Clockwise
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Orbit Direction", style = MaterialTheme.typography.bodyMedium, color = TextWhite)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OrbitDirection.values().forEach { dir ->
                            val isSelected = spatialParams.direction == dir
                            Surface(
                                color = if (isSelected) NeonCyan else Color(0xFF162035),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { onParamsChange(spatialParams.copy(direction = dir)) }
                            ) {
                                Text(
                                    text = dir.displayName,
                                    color = if (isSelected) Color.Black else TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Speed Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Movement Speed", color = TextWhite, fontSize = 12.sp)
                    Text("${String.format("%.2f", spatialParams.speedHz)} Hz", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Slider(
                    value = spatialParams.speedHz,
                    onValueChange = { onParamsChange(spatialParams.copy(speedHz = it)) },
                    valueRange = 0.04f..0.45f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                // Depth Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Vertical 3D Depth", color = TextWhite, fontSize = 12.sp)
                    Text("${(spatialParams.depth * 100).toInt()}%", color = ElectricMagenta, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Slider(
                    value = spatialParams.depth,
                    onValueChange = { onParamsChange(spatialParams.copy(depth = it)) },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = ElectricMagenta, activeTrackColor = ElectricMagenta)
                )

                // Distance Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Orbit Distance", color = TextWhite, fontSize = 12.sp)
                    Text("${String.format("%.1f", spatialParams.distance)}x", color = AmberGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Slider(
                    value = spatialParams.distance,
                    onValueChange = { onParamsChange(spatialParams.copy(distance = it)) },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = AmberGlow, activeTrackColor = AmberGlow)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Transport Mini Controls Bar
        Surface(
            color = Color(0xFF131D31),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22314E)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack?.title ?: "No track playing",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentTrack?.artist ?: "Select a song from Library",
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSkipPrevious) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", tint = TextWhite)
                    }
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier.background(NeonCyan, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = onSkipNext) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = TextWhite)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}
