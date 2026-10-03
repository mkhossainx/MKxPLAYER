package com.example.ui.components

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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SpatialPresetEntity
import com.example.model.AudioTrackItem
import com.example.model.ExportFormat
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple
import com.example.viewmodel.ConversionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SpatialConverterPanel(
    currentTrack: AudioTrackItem?,
    currentMode: SpatialMode,
    currentParams: SpatialParams,
    presets: List<SpatialPresetEntity>,
    conversionState: ConversionState,
    onModeChange: (SpatialMode) -> Unit,
    onParamsChange: (SpatialParams) -> Unit,
    onApplyPreset: (SpatialPresetEntity) -> Unit,
    onSavePreset: (name: String) -> Unit,
    onStartConversion: (track: AudioTrackItem, mode: SpatialMode, params: SpatialParams, format: ExportFormat) -> Unit,
    onShareExportedFile: (file: java.io.File) -> Unit,
    onPlayTrack: (AudioTrackItem) -> Unit,
    onDismissConversionState: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSavePresetDialog by remember { mutableStateOf(false) }
    var presetNameInput by remember { mutableStateOf("") }
    var selectedExportFormat by remember { mutableStateOf(ExportFormat.WAV_16BIT) }
    var showExportConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F172A),
        contentColor = TextWhite,
        dragHandle = null,
        modifier = Modifier.testTag("spatial_converter_panel")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MKx Spatial Converter",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )
                        Text(
                            text = "Binaural HRTF 3D Audio Engine",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyanGlow)
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Mode Selector Cards
            Text(
                text = "SPATIAL DIMENSION MODE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpatialModeCard(
                    mode = SpatialMode.SPATIAL_8D,
                    isSelected = currentMode == SpatialMode.SPATIAL_8D,
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onModeChange(SpatialMode.SPATIAL_8D) }
                )
                SpatialModeCard(
                    mode = SpatialMode.SPATIAL_16D,
                    isSelected = currentMode == SpatialMode.SPATIAL_16D,
                    accentColor = ElectricMagenta,
                    modifier = Modifier.weight(1f),
                    onClick = { onModeChange(SpatialMode.SPATIAL_16D) }
                )
                SpatialModeCard(
                    mode = SpatialMode.SPATIAL_24D,
                    isSelected = currentMode == SpatialMode.SPATIAL_24D,
                    accentColor = VortexPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onModeChange(SpatialMode.SPATIAL_24D) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trajectory Description Banner
            Surface(
                color = Color(0xFF162035),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SurroundSound,
                        contentDescription = null,
                        tint = when (currentMode) {
                            SpatialMode.SPATIAL_8D -> NeonCyan
                            SpatialMode.SPATIAL_16D -> ElectricMagenta
                            SpatialMode.SPATIAL_24D -> VortexPurple
                            SpatialMode.STEREO -> TextMuted
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentMode.trajectoryDescription,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Presets Quick Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACOUSTIC PRESETS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )
                )
                Text(
                    text = "+ Save Custom",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    ),
                    modifier = Modifier.clickable { showSavePresetDialog = true }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    Surface(
                        color = if (preset.mode == currentMode.name &&
                            (preset.intensity - currentParams.intensity < 0.05f)) Color(0xFF1E293B) else Color(0xFF131D31),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (preset.mode == currentMode.name) NeonCyan.copy(alpha = 0.6f) else Color(0xFF22314E)
                        ),
                        modifier = Modifier.clickable { onApplyPreset(preset) }
                    ) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                color = if (preset.mode == currentMode.name) NeonCyan else TextWhite,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Converter Fine-Tuning Sliders
            Text(
                text = "DSP CUSTOMIZATION CONTROLS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            // 1. Intensity Slider
            ConverterSliderItem(
                label = "Spatial Intensity",
                valueDisplay = "${(currentParams.intensity * 100).toInt()}%",
                icon = Icons.Default.SurroundSound,
                value = currentParams.intensity,
                valueRange = 0.1f..1.0f,
                accentColor = NeonCyan,
                onValueChange = { onParamsChange(currentParams.copy(intensity = it)) }
            )

            // 2. Orbit Movement Speed Slider
            ConverterSliderItem(
                label = "Orbit Speed",
                valueDisplay = "${String.format("%.2f", currentParams.speedHz)} Hz",
                icon = Icons.Default.Speed,
                value = currentParams.speedHz,
                valueRange = 0.04f..0.45f,
                accentColor = ElectricMagenta,
                onValueChange = { onParamsChange(currentParams.copy(speedHz = it)) }
            )

            // 3. Acoustic Reverb Depth Slider
            ConverterSliderItem(
                label = "Reverb Amount",
                valueDisplay = "${(currentParams.reverbAmount * 100).toInt()}%",
                icon = Icons.Default.Waves,
                value = currentParams.reverbAmount,
                valueRange = 0.0f..1.0f,
                accentColor = VortexPurple,
                onValueChange = { onParamsChange(currentParams.copy(reverbAmount = it)) }
            )

            // 4. Echo Reflection Slider
            ConverterSliderItem(
                label = "Echo / Delay Amount",
                valueDisplay = "${(currentParams.echoAmount * 100).toInt()}%",
                icon = Icons.Default.Hearing,
                value = currentParams.echoAmount,
                valueRange = 0.0f..1.0f,
                accentColor = CyanGlow,
                onValueChange = { onParamsChange(currentParams.copy(echoAmount = it)) }
            )

            // 5. Bass Boost Slider
            ConverterSliderItem(
                label = "Bass Boost",
                valueDisplay = "${(currentParams.bassBoost * 100).toInt()}%",
                icon = Icons.Default.GraphicEq,
                value = currentParams.bassBoost,
                valueRange = 0.0f..1.0f,
                accentColor = AmberGlow,
                onValueChange = { onParamsChange(currentParams.copy(bassBoost = it)) }
            )

            // 6. Wet/Dry Effect Mix Slider
            ConverterSliderItem(
                label = "Effect Mix (Wet / Dry)",
                valueDisplay = "${(currentParams.wetDryMix * 100).toInt()}%",
                icon = Icons.Default.Tune,
                value = currentParams.wetDryMix,
                valueRange = 0.1f..1.0f,
                accentColor = NeonCyan,
                onValueChange = { onParamsChange(currentParams.copy(wetDryMix = it)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Export & Convert Button
            Button(
                onClick = {
                    if (currentTrack != null) {
                        showExportConfirmDialog = true
                    }
                },
                enabled = currentTrack != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("export_spatial_track_button")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EXPORT ${currentMode.dimensionLabel} SPATIAL TRACK",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "100% on-device DSP processing. Zero server uploads. Fully private.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TextMuted
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Save Preset Dialog
    if (showSavePresetDialog) {
        AlertDialog(
            onDismissRequest = { showSavePresetDialog = false },
            title = { Text("Save Custom Preset") },
            text = {
                Column {
                    Text(
                        text = "Save current ${currentMode.dimensionLabel} settings to quick presets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = presetNameInput,
                        onValueChange = { presetNameInput = it },
                        label = { Text("Preset Name") },
                        placeholder = { Text("e.g. My Cosmic 8D") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (presetNameInput.isNotBlank()) {
                            onSavePreset(presetNameInput.trim())
                            showSavePresetDialog = false
                            presetNameInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showSavePresetDialog = false },
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

    // Export Format Confirmation Dialog
    if (showExportConfirmDialog && currentTrack != null) {
        AlertDialog(
            onDismissRequest = { showExportConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Spatial Audio")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Convert \"${currentTrack.title}\" to ${currentMode.dimensionLabel} spatial audio on this device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("SELECT OUTPUT FORMAT:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    FormatOption(
                        format = ExportFormat.WAV_16BIT,
                        isSelected = selectedExportFormat == ExportFormat.WAV_16BIT,
                        onSelect = { selectedExportFormat = ExportFormat.WAV_16BIT }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FormatOption(
                        format = ExportFormat.MP3_320K,
                        isSelected = selectedExportFormat == ExportFormat.MP3_320K,
                        onSelect = { selectedExportFormat = ExportFormat.MP3_320K }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExportConfirmDialog = false
                        onStartConversion(currentTrack, currentMode, currentParams, selectedExportFormat)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Start Conversion")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showExportConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextMuted)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF131D31),
            titleContentColor = TextWhite
        )
    }

    // Active Conversion Progress & Share Dialog
    if (conversionState.isConverting || conversionState.exportedFile != null || conversionState.error != null) {
        AlertDialog(
            onDismissRequest = {
                if (!conversionState.isConverting) onDismissConversionState()
            },
            title = {
                Text(
                    text = when {
                        conversionState.isConverting -> "Converting to ${currentMode.dimensionLabel}..."
                        conversionState.exportedFile != null -> "Export Completed!"
                        else -> "Conversion Error"
                    },
                    color = TextWhite
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (conversionState.isConverting) {
                        LinearProgressIndicator(
                            progress = { conversionState.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = NeonCyan,
                            trackColor = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${conversionState.progressPercent}%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = conversionState.statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else if (conversionState.exportedFile != null) {
                        Icon(
                            imageVector = Icons.Default.SurroundSound,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = conversionState.exportedFile.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Saved to Music / MKxPLAYER_Spatial. Ready to play or share.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else if (conversionState.error != null) {
                        Text(
                            text = conversionState.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            },
            confirmButton = {
                if (conversionState.exportedFile != null) {
                    Row {
                        Button(
                            onClick = {
                                onShareExportedFile(conversionState.exportedFile)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = NeonCyan)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onDismissConversionState()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                        ) {
                            Text("Done")
                        }
                    }
                } else if (!conversionState.isConverting) {
                    Button(
                        onClick = onDismissConversionState,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                    ) {
                        Text("Close")
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}

@Composable
private fun SpatialModeCard(
    mode: SpatialMode,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1B2844) else Color(0xFF131D31)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) accentColor else Color(0xFF22314E)
        ),
        modifier = modifier
            .clickable { onClick() }
            .testTag("mode_card_${mode.name}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            Text(
                text = mode.dimensionLabel,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) accentColor else TextWhite
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = mode.subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = if (isSelected) TextWhite else TextMuted
                )
            )
        }
    }
}

@Composable
private fun ConverterSliderItem(
    label: String,
    valueDisplay: String,
    icon: ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                )
            }
            Text(
                text = valueDisplay,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontSize = 12.sp
                )
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
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
private fun FormatOption(
    format: ExportFormat,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonCyan else Color(0xFF334155)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(if (isSelected) NeonCyan else Color.Transparent, CircleShape)
                    .border(1.5.dp, if (isSelected) NeonCyan else Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = format.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = TextWhite
                    )
                )
                Text(
                    text = if (format == ExportFormat.WAV_16BIT) "Lossless PCM, zero compression artifacts" else "Universal playback, ultra-compact size",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, color = TextMuted)
                )
            }
        }
    }
}
