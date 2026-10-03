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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EqualizerBand
import com.example.model.ReverbType
import com.example.model.StudioAudioEffects
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioScreen(
    bands: List<EqualizerBand>,
    studioEffects: StudioAudioEffects,
    onBandChange: (index: Int, levelMilliBels: Short) -> Unit,
    onStudioEffectsChange: (StudioAudioEffects) -> Unit,
    onPlaybackSpeedChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit
) {
    var selectedPreset by remember { mutableStateOf("Studio Precision") }

    val presets = listOf(
        "Studio Precision" to listOf(0, 0, 0, 0, 0),
        "Deep Bass Master" to listOf(600, 450, 100, -100, -200),
        "Concert Hall" to listOf(300, 200, 0, 300, 500),
        "Vocal Clarity" to listOf(-200, 0, 450, 350, 150),
        "Electronic / Synth" to listOf(500, 350, 0, 250, 450),
        "Acoustic Live" to listOf(200, 150, 100, 200, 300),
        "Rock / Metal" to listOf(450, 250, -100, 300, 550),
        "Flat Bypass" to listOf(0, 0, 0, 0, 0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("studio_screen")
    ) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                    .border(1.5.dp, NeonCyan, RoundedCornerShape(10.dp))
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "MKx STUDIO",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Professional Audio Processing & Preamp Suite",
                    style = MaterialTheme.typography.bodySmall.copy(color = CyanGlow, fontSize = 11.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Preamp & Clipping Protection Module
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AmberGlow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Preamp & Soft Limiter",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 15.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Clean analog-modeled input level",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = Color(0xFF062E20),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF10B981), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PROTECTED",
                                color = Color(0xFF10B981),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Input Gain",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                    Surface(
                        color = Color(0xFF162035),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${String.format("%+.1f", studioEffects.preampDb)} dB",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberGlow,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Slider(
                    value = studioEffects.preampDb,
                    onValueChange = { onStudioEffectsChange(studioEffects.copy(preampDb = it)) },
                    valueRange = -12.0f..12.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = AmberGlow,
                        activeTrackColor = AmberGlow,
                        inactiveTrackColor = Color(0xFF1E293B)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Presets Chips
        Text(
            text = "STUDIO PRESETS",
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
            presets.forEach { (name, levels) ->
                val isSelected = selectedPreset == name
                Surface(
                    color = if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonCyan else Color(0xFF22314E)
                    ),
                    modifier = Modifier.clickable {
                        selectedPreset = name
                        levels.forEachIndexed { idx, lvl ->
                            if (idx < bands.size) {
                                onBandChange(idx, lvl.toShort())
                            }
                        }
                    }
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonCyan else TextWhite,
                            fontSize = 11.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 10-Band Graphic Equalizer
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "10-BAND GRAPHIC EQUALIZER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "±15 dB Range",
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan, fontSize = 10.sp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                val defaultFreqs = listOf("31 Hz", "62 Hz", "125 Hz", "250 Hz", "500 Hz", "1 kHz", "2 kHz", "4 kHz", "8 kHz", "16 kHz")
                val activeBands = if (bands.isNotEmpty()) bands else (0 until 10).map { i ->
                    EqualizerBand(i, (i + 1) * 1000, -1500, 1500, 0)
                }

                activeBands.forEachIndexed { idx, band ->
                    val label = defaultFreqs.getOrElse(idx) { "${band.centerFreqHz} Hz" }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.width(62.dp)
                        )

                        Slider(
                            value = band.currentLevelMilliBels.toFloat(),
                            onValueChange = { onBandChange(band.index, it.toInt().toShort()) },
                            valueRange = band.minLevelMilliBels.toFloat()..band.maxLevelMilliBels.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${band.currentLevelMilliBels / 100} dB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = NeonCyan,
                                fontSize = 10.5.sp
                            ),
                            modifier = Modifier.width(44.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Studio Audio Effects Module
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "STUDIO AUDIO ENHANCEMENTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stereo Width
                StudioSliderRow(
                    label = "Stereo Width",
                    valueDisplay = "${(studioEffects.stereoWidth * 100).toInt()}%",
                    icon = Icons.Default.SurroundSound,
                    value = studioEffects.stereoWidth,
                    range = 0.0f..2.0f,
                    accentColor = NeonCyan,
                    onValueChange = { onStudioEffectsChange(studioEffects.copy(stereoWidth = it)) }
                )

                // Clarity Exciter
                StudioSliderRow(
                    label = "High Clarity Exciter",
                    valueDisplay = "${(studioEffects.clarityGain * 100).toInt()}%",
                    icon = Icons.Default.Waves,
                    value = studioEffects.clarityGain,
                    range = 0.0f..1.0f,
                    accentColor = ElectricMagenta,
                    onValueChange = { onStudioEffectsChange(studioEffects.copy(clarityGain = it)) }
                )

                // Vocal Enhancement
                StudioSliderRow(
                    label = "Vocal Boost / Intelligibility",
                    valueDisplay = "${(studioEffects.vocalBoostStrength / 10).toInt()}%",
                    icon = Icons.Default.Hearing,
                    value = studioEffects.vocalBoostStrength.toFloat(),
                    range = 0f..1000f,
                    accentColor = CyanGlow,
                    onValueChange = { onStudioEffectsChange(studioEffects.copy(vocalBoostStrength = it.toInt().toShort())) }
                )

                // Bass Enhancement
                StudioSliderRow(
                    label = "Deep Bass Harmonic Weight",
                    valueDisplay = "${(studioEffects.bassBoostStrength / 10).toInt()}%",
                    icon = Icons.Default.GraphicEq,
                    value = studioEffects.bassBoostStrength.toFloat(),
                    range = 0f..1000f,
                    accentColor = AmberGlow,
                    onValueChange = { onStudioEffectsChange(studioEffects.copy(bassBoostStrength = it.toInt().toShort())) }
                )

                // Reverb Type Selection
                Text(
                    text = "Acoustic Reverb Chamber",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextWhite)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ReverbType.values().forEach { type ->
                        val isSelected = studioEffects.reverbType == type
                        Surface(
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) VortexPurple else Color(0xFF22314E)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onStudioEffectsChange(studioEffects.copy(reverbType = type)) }
                        ) {
                            Text(
                                text = type.displayName.substringBefore(" "),
                                color = if (isSelected) VortexPurple else TextWhite,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Playback Dynamics & Modulation
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PLAYBACK ENGINE CONTROLS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Playback Speed
                StudioSliderRow(
                    label = "Playback Speed",
                    valueDisplay = "${String.format("%.2f", studioEffects.playbackSpeed)}x",
                    icon = Icons.Default.Speed,
                    value = studioEffects.playbackSpeed,
                    range = 0.5f..2.0f,
                    accentColor = NeonCyan,
                    onValueChange = { onPlaybackSpeedChange(it) }
                )

                // Pitch Control
                StudioSliderRow(
                    label = "Audio Pitch",
                    valueDisplay = "${String.format("%.2f", studioEffects.pitch)}x",
                    icon = Icons.Default.Tune,
                    value = studioEffects.pitch,
                    range = 0.5f..2.0f,
                    accentColor = ElectricMagenta,
                    onValueChange = { onPitchChange(it) }
                )

                // Left/Right Balance
                StudioSliderRow(
                    label = "Left / Right Balance",
                    valueDisplay = when {
                        studioEffects.balance < -0.05f -> "L ${(-studioEffects.balance * 100).toInt()}%"
                        studioEffects.balance > 0.05f -> "R ${(studioEffects.balance * 100).toInt()}%"
                        else -> "Center"
                    },
                    icon = Icons.Default.VolumeUp,
                    value = studioEffects.balance,
                    range = -1.0f..1.0f,
                    accentColor = AmberGlow,
                    onValueChange = { onStudioEffectsChange(studioEffects.copy(balance = it)) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Toggles Row: Mono & Gapless & Normalization
                StudioToggleRow("Mono Audio Downmix", studioEffects.isMono) {
                    onStudioEffectsChange(studioEffects.copy(isMono = it))
                }
                StudioToggleRow("Volume Normalization", studioEffects.volumeNormalization) {
                    onStudioEffectsChange(studioEffects.copy(volumeNormalization = it))
                }
                StudioToggleRow("Gapless Playback Transition", studioEffects.gaplessPlayback) {
                    onStudioEffectsChange(studioEffects.copy(gaplessPlayback = it))
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun StudioSliderRow(
    label: String,
    valueDisplay: String,
    icon: ImageVector,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    accentColor: Color,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(label, color = TextWhite, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
            }
            Text(valueDisplay, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StudioToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextWhite, fontSize = 12.5.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Color(0xFF1E293B)
            )
        )
    }
}
