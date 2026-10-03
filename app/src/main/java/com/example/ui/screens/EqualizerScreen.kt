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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Waves
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EqualizerBand
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EqualizerScreen(
    bands: List<EqualizerBand>,
    onBandChange: (index: Int, levelMilliBels: Short) -> Unit,
    onBassBoostChange: (Short) -> Unit,
    onBack: () -> Unit
) {
    var bassBoostValue by remember { mutableFloatStateOf(400f) }
    var virtualizerValue by remember { mutableFloatStateOf(500f) }
    var selectedPreset by remember { mutableStateOf("Electronic") }

    val presets = listOf(
        "Flat" to listOf(0, 0, 0, 0, 0),
        "Bass Boost" to listOf(600, 400, 0, 0, -200),
        "Rock" to listOf(400, 200, -100, 300, 500),
        "Pop" to listOf(-100, 200, 400, 200, -100),
        "Electronic" to listOf(500, 300, 0, 200, 400),
        "Jazz" to listOf(300, 100, -200, 200, 300),
        "Vocal" to listOf(-200, 0, 500, 300, 0),
        "Classical" to listOf(400, 300, -200, 300, 400)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("equalizer_screen")
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Acoustic Equalizer",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
                Text(
                    text = "Hardware DSP 5-Band Audio Shaper",
                    style = MaterialTheme.typography.bodySmall.copy(color = CyanGlow, fontSize = 11.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Preset Selector Chips
        Text(
            text = "EQUALIZER PRESETS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextMuted
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
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Graphic Equalizer Bands Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FREQUENCY BANDS (dB)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))

                val displayBands = if (bands.isNotEmpty()) bands else listOf(
                    EqualizerBand(0, 60, -1500, 1500, 300),
                    EqualizerBand(1, 230, -1500, 1500, 200),
                    EqualizerBand(2, 910, -1500, 1500, 0),
                    EqualizerBand(3, 3600, -1500, 1500, 300),
                    EqualizerBand(4, 14000, -1500, 1500, 400)
                )

                displayBands.forEach { band ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "${band.centerFreqHz} Hz",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                fontSize = 12.sp
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
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.width(46.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hardware Bass Boost & 3D Spatial Effects
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Bass Boost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hardware Bass Boost", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                    }
                    Text("${(bassBoostValue / 10).toInt()}%", color = AmberGlow, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = bassBoostValue,
                    onValueChange = {
                        bassBoostValue = it
                        onBassBoostChange(it.toInt().toShort())
                    },
                    valueRange = 0f..1000f,
                    colors = SliderDefaults.colors(
                        thumbColor = AmberGlow,
                        activeTrackColor = AmberGlow,
                        inactiveTrackColor = Color(0xFF1E293B)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3D Virtualizer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SurroundSound, contentDescription = null, tint = VortexPurple, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("3D Soundstage Width", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                    }
                    Text("${(virtualizerValue / 10).toInt()}%", color = VortexPurple, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = virtualizerValue,
                    onValueChange = { virtualizerValue = it },
                    valueRange = 0f..1000f,
                    colors = SliderDefaults.colors(
                        thumbColor = VortexPurple,
                        activeTrackColor = VortexPurple,
                        inactiveTrackColor = Color(0xFF1E293B)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
