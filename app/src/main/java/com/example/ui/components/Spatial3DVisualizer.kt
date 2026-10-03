package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
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
import com.example.model.SpatialMode
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.MagentaGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VortexPurple
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-Performance Canvas-based 3D Spatial Audio Visualizer.
 * Dynamically reacts in real-time to:
 * 1. Audio Amplitude (pulsing node glow, acoustic shockwave ripples, reactive beam thickness)
 * 2. Pan Position (-1.0 Left to +1.0 Right binaural ear illumination, stereo meter needle, directional laser)
 * 3. 3D Elevation & Azimuth coordinates (8D Orbit, 16D Infinity, 24D Spherical Vortex)
 */
@Composable
fun Spatial3DVisualizer(
    mode: SpatialMode,
    trajectoryPoint: SpatialAudioProcessor.SpatialTrajectoryPoint,
    spectrumBars: FloatArray,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    audioAmplitude: Float = ((trajectoryPoint.leftGain + trajectoryPoint.rightGain) * 0.5f).coerceIn(0.1f, 1.0f),
    panPosition: Float = trajectoryPoint.x.coerceIn(-1.0f, 1.0f)
) {
    val vortexRotation = remember { Animatable(0f) }
    val ripplePulse = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            vortexRotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(7000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            ripplePulse.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFF080C17), RoundedCornerShape(20.dp))
            .border(1.5.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            .padding(10.dp)
            .testTag("spatial_3d_visualizer")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.43f)
            val maxR = size.width * 0.36f

            // Dynamic amplitude scaling factor
            val amp = if (isPlaying) audioAmplitude.coerceIn(0.1f, 1.0f) else 0.05f
            val pan = panPosition.coerceIn(-1.0f, 1.0f)

            // 1. 3D Depth Isometric Wireframe & Spherical Grid
            val baseGridColor = Color(0x1500F2FE)
            val activeGridColor = NeonCyan.copy(alpha = 0.08f + amp * 0.12f)

            // Concentric orbital distance rings
            drawCircle(baseGridColor, radius = maxR * 0.35f, center = center, style = Stroke(1f))
            drawCircle(baseGridColor, radius = maxR * 0.70f, center = center, style = Stroke(1f))
            drawCircle(activeGridColor, radius = maxR * 1.00f, center = center, style = Stroke(1.5f))

            // Crosshair 3D Axes (X = Left/Right Pan Axis, Y = Front/Back Depth Axis)
            drawLine(
                color = Color(0x22FFFFFF),
                start = Offset(center.x - maxR * 1.15f, center.y),
                end = Offset(center.x + maxR * 1.15f, center.y),
                strokeWidth = 1f
            )
            drawLine(
                color = Color(0x18FFFFFF),
                start = Offset(center.x, center.y - maxR * 1.05f),
                end = Offset(center.x, center.y + maxR * 1.05f),
                strokeWidth = 1f
            )

            // 2. Trajectory Guides by Spatial Mode
            when (mode) {
                SpatialMode.STEREO -> {
                    // Fixed Left (Cyan) and Right (Magenta) Channel Nodes
                    val speakerLeft = Offset(center.x - maxR * 0.85f, center.y)
                    val speakerRight = Offset(center.x + maxR * 0.85f, center.y)

                    val leftPwr = ((1f - pan) * 0.5f * amp).coerceIn(0.1f, 1f)
                    val rightPwr = ((1f + pan) * 0.5f * amp).coerceIn(0.1f, 1f)

                    drawCircle(NeonCyan.copy(alpha = 0.2f * leftPwr), radius = 24f * leftPwr, center = speakerLeft)
                    drawCircle(NeonCyan, radius = 9f + 4f * leftPwr, center = speakerLeft)

                    drawCircle(ElectricMagenta.copy(alpha = 0.2f * rightPwr), radius = 24f * rightPwr, center = speakerRight)
                    drawCircle(ElectricMagenta, radius = 9f + 4f * rightPwr, center = speakerRight)
                }

                SpatialMode.SPATIAL_8D -> {
                    // 8D Smooth 360° Horizontal Circular Orbit Ring
                    drawOval(
                        brush = Brush.sweepGradient(
                            colors = listOf(NeonCyan, VortexPurple, ElectricMagenta, AmberGlow, NeonCyan),
                            center = center
                        ),
                        topLeft = Offset(center.x - maxR, center.y - maxR * 0.56f),
                        size = androidx.compose.ui.geometry.Size(maxR * 2, maxR * 1.12f),
                        style = Stroke(width = 2.5f + amp * 1.8f, cap = StrokeCap.Round)
                    )

                    // Secondary depth acoustic halo
                    drawOval(
                        color = NeonCyan.copy(alpha = 0.12f + amp * 0.1f),
                        topLeft = Offset(center.x - maxR * 1.06f, center.y - maxR * 0.60f),
                        size = androidx.compose.ui.geometry.Size(maxR * 2.12f, maxR * 1.20f),
                        style = Stroke(width = 1f)
                    )
                }

                SpatialMode.SPATIAL_16D -> {
                    // 16D Figure-8 (Infinity ∞) 3D Trajectory Ribbon
                    val path16D = Path()
                    val steps = 120
                    for (i in 0..steps) {
                        val t = (i.toFloat() / steps) * 2f * PI.toFloat()
                        val scale = maxR * 0.95f
                        val denom = 1f + sin(t) * sin(t)
                        val px = center.x + (scale * cos(t)) / denom
                        val py = center.y + (scale * sin(t) * cos(t) * 1.15f) / denom
                        if (i == 0) path16D.moveTo(px, py) else path16D.lineTo(px, py)
                    }
                    drawPath(
                        path = path16D,
                        brush = Brush.linearGradient(
                            colors = listOf(NeonCyan, ElectricMagenta, VortexPurple, NeonCyan)
                        ),
                        style = Stroke(width = 2.8f + amp * 2.0f, cap = StrokeCap.Round)
                    )
                }

                SpatialMode.SPATIAL_24D -> {
                    // 24D Spherical Vortex Multi-axis Orbits
                    val rotRad = Math.toRadians(vortexRotation.value.toDouble()).toFloat()
                    val rings = 3
                    for (r in 1..rings) {
                        val ringR = maxR * (r.toFloat() / rings)
                        drawOval(
                            brush = Brush.sweepGradient(
                                colors = listOf(NeonCyan, VortexPurple, ElectricMagenta, AmberGlow, NeonCyan),
                                center = center
                            ),
                            topLeft = Offset(center.x - ringR, center.y - ringR * 0.70f),
                            size = androidx.compose.ui.geometry.Size(ringR * 2f, ringR * 1.40f),
                            style = Stroke(width = 1.4f + (amp * 1.2f))
                        )
                    }

                    // Rotating vortex arms
                    for (arm in 0 until 4) {
                        val armAngle = rotRad + (arm * PI.toFloat() / 2f)
                        val armEndX = center.x + maxR * cos(armAngle)
                        val armEndY = center.y + maxR * 0.70f * sin(armAngle)
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Transparent, NeonCyan.copy(alpha = 0.35f + amp * 0.3f))
                            ),
                            start = center,
                            end = Offset(armEndX, armEndY),
                            strokeWidth = 1.5f + amp * 1.5f
                        )
                    }
                }
            }

            // 3. Central Binaural Listener Head (Reacts to Pan Position & Amplitude)
            val headRadius = 18f
            // Left & Right ear illumination levels derived from panPosition
            val leftEarIntensity = if (isPlaying) ((1.0f - pan) * 0.5f * (0.4f + amp * 0.6f)).coerceIn(0.1f, 1.0f) else 0.1f
            val rightEarIntensity = if (isPlaying) ((1.0f + pan) * 0.5f * (0.4f + amp * 0.6f)).coerceIn(0.1f, 1.0f) else 0.1f

            // Head core
            drawCircle(color = Color(0xFF131D31), radius = headRadius, center = center)
            drawCircle(
                color = if (isPlaying) NeonCyan.copy(alpha = 0.8f) else Color(0xFF334155),
                radius = headRadius,
                center = center,
                style = Stroke(2f)
            )

            // Listener pulsing aura when amplitude spikes
            if (amp > 0.45f) {
                drawCircle(
                    color = NeonCyan.copy(alpha = (amp - 0.45f) * 0.4f),
                    radius = headRadius + (amp * 16f),
                    center = center,
                    style = Stroke(1.5f)
                )
            }

            // Left Earcup (Glows brighter when sound pans to Left)
            val leftEarCenter = Offset(center.x - 22f, center.y)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.8f * leftEarIntensity), Color.Transparent),
                    center = leftEarCenter,
                    radius = 20f * leftEarIntensity
                ),
                radius = 20f * leftEarIntensity,
                center = leftEarCenter
            )
            drawRoundRect(
                color = if (leftEarIntensity > 0.4f) NeonCyan else Color(0xFF007A87),
                topLeft = Offset(center.x - 26f, center.y - 10f),
                size = androidx.compose.ui.geometry.Size(7f, 20f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.5f, 3.5f)
            )

            // Right Earcup (Glows brighter when sound pans to Right)
            val rightEarCenter = Offset(center.x + 22f, center.y)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricMagenta.copy(alpha = 0.8f * rightEarIntensity), Color.Transparent),
                    center = rightEarCenter,
                    radius = 20f * rightEarIntensity
                ),
                radius = 20f * rightEarIntensity,
                center = rightEarCenter
            )
            drawRoundRect(
                color = if (rightEarIntensity > 0.4f) ElectricMagenta else Color(0xFF8B0045),
                topLeft = Offset(center.x + 19f, center.y - 10f),
                size = androidx.compose.ui.geometry.Size(7f, 20f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.5f, 3.5f)
            )

            // Headphone headband arch
            val headbandPath = Path()
            headbandPath.moveTo(center.x - 22f, center.y - 8f)
            headbandPath.quadraticTo(center.x, center.y - 28f, center.x + 22f, center.y - 8f)
            drawPath(
                path = headbandPath,
                color = Color(0xFF475569),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // 4. Moving Sound Source Node (3D Isometric Coordinates reacting to Pan & Amplitude)
            if (mode != SpatialMode.STEREO) {
                // Map 3D coordinates (x: -1..1, y: -1..1, z: -1..1) to screen
                // x controls pan position directly!
                val nodeX = center.x + pan * maxR
                val nodeY = center.y + (trajectoryPoint.y * maxR * 0.54f) - (trajectoryPoint.z * maxR * 0.42f)
                val nodeOffset = Offset(nodeX, nodeY)

                // Depth scaling (sound closer to front y>0 is slightly larger)
                val depthFactor = (1.0f + trajectoryPoint.y * 0.25f).coerceIn(0.7f, 1.3f)
                val baseNodeRadius = (8f + amp * 8f) * depthFactor

                // Dynamic beam connecting listener to sound source
                val beamColor = when {
                    pan < -0.2f -> NeonCyan
                    pan > 0.2f -> ElectricMagenta
                    else -> VortexPurple
                }
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            beamColor.copy(alpha = 0.7f * amp),
                            beamColor.copy(alpha = 0.1f)
                        ),
                        start = nodeOffset,
                        end = center
                    ),
                    start = nodeOffset,
                    end = center,
                    strokeWidth = (1.5f + amp * 3.0f) * depthFactor,
                    cap = StrokeCap.Round
                )

                // Audio reactive shockwave ripple expanding from the sound source
                if (isPlaying) {
                    val rippleR = (ripplePulse.value * 48f * amp) * depthFactor
                    val rippleAlpha = ((1f - ripplePulse.value) * 0.65f * amp).coerceIn(0f, 1f)
                    drawCircle(
                        color = beamColor.copy(alpha = rippleAlpha),
                        radius = rippleR,
                        center = nodeOffset,
                        style = Stroke(1.8f)
                    )
                }

                // Outer radiant glow halo pulsing with amplitude
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            beamColor.copy(alpha = 0.7f * amp),
                            AmberGlow.copy(alpha = 0.25f * amp),
                            Color.Transparent
                        ),
                        center = nodeOffset,
                        radius = (32f + amp * 28f) * depthFactor
                    ),
                    radius = (32f + amp * 28f) * depthFactor,
                    center = nodeOffset
                )

                // Core audio node
                drawCircle(
                    color = Color.White,
                    radius = baseNodeRadius,
                    center = nodeOffset
                )
                drawCircle(
                    color = beamColor,
                    radius = baseNodeRadius * 0.65f,
                    center = nodeOffset
                )
            }

            // 5. Real-Time Stereo Pan Position Meter Bar (Under the orbital arena)
            val meterWidth = size.width * 0.65f
            val meterHeight = 6f
            val meterTop = size.height * 0.76f
            val meterLeft = (size.width - meterWidth) / 2f
            val meterCenter = meterLeft + meterWidth / 2f

            // Meter track background
            drawRoundRect(
                color = Color(0xFF162035),
                topLeft = Offset(meterLeft, meterTop),
                size = androidx.compose.ui.geometry.Size(meterWidth, meterHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
            )

            // Center zero tick
            drawLine(
                color = Color(0xFF64748B),
                start = Offset(meterCenter, meterTop - 3f),
                end = Offset(meterCenter, meterTop + meterHeight + 3f),
                strokeWidth = 2f
            )

            // Active pan needle indicator reacting to real-time panPosition
            val needleX = meterCenter + (pan * (meterWidth / 2f))
            val needleColor = if (pan < -0.1f) NeonCyan else if (pan > 0.1f) ElectricMagenta else AmberGlow
            drawCircle(
                color = needleColor,
                radius = 6.5f,
                center = Offset(needleX, meterTop + meterHeight / 2f)
            )
            drawCircle(
                color = Color.White,
                radius = 2.5f,
                center = Offset(needleX, meterTop + meterHeight / 2f)
            )

            // 6. Bottom Frequency Spectrum Bars with reflection
            val barCount = 20
            val barAreaWidth = size.width - 24f
            val barWidth = (barAreaWidth / barCount) * 0.65f
            val gap = (barAreaWidth / barCount) * 0.35f
            val barBaseY = size.height - 4f
            val maxBarHeight = size.height * 0.15f

            for (b in 0 until barCount) {
                val rawBar = spectrumBars.getOrElse(b % spectrumBars.size) { 0.2f }
                // Weight bars with real-time amplitude
                val barAmp = (rawBar * amp * 1.25f).coerceIn(0.06f, 1.0f)
                val barH = barAmp * maxBarHeight
                val bx = 12f + b * (barWidth + gap)

                val barColor = when {
                    b < 6 -> Brush.verticalGradient(listOf(NeonCyan, VortexPurple))
                    b < 14 -> Brush.verticalGradient(listOf(CyanGlow, ElectricMagenta))
                    else -> Brush.verticalGradient(listOf(ElectricMagenta, AmberGlow))
                }

                drawRoundRect(
                    brush = barColor,
                    topLeft = Offset(bx, barBaseY - barH),
                    size = androidx.compose.ui.geometry.Size(barWidth, barH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f, 2.5f)
                )
            }
        }

        // Top HUD Overlay: Real-time Telemetry Readout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF0F172A).copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                if (isPlaying) NeonCyan else Color.Gray,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${mode.dimensionLabel} 3D REAL-TIME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Surface(
                color = Color(0xFF0F172A).copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                val panPercent = (panPosition * 100).toInt()
                val panText = when {
                    panPercent < -5 -> "PAN: L ${abs(panPercent)}%"
                    panPercent > 5 -> "PAN: R $panPercent%"
                    else -> "PAN: CENTER"
                }
                val ampPercent = (audioAmplitude * 100).toInt().coerceIn(0, 100)

                Text(
                    text = "$panText | AMP: $ampPercent%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AmberGlow,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
