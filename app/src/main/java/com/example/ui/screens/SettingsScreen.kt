package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HrtfProfile
import com.example.model.LibrarySortOrder
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import com.example.model.StudioAudioEffects
import com.example.model.VisualizerMode
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple
import com.example.viewmodel.ScreenTab

@Composable
fun SettingsScreen(
    studioEffects: StudioAudioEffects,
    spatialMode: SpatialMode,
    spatialParams: SpatialParams,
    visualizerMode: VisualizerMode,
    isAmoledBlack: Boolean,
    sortOrder: LibrarySortOrder,
    onStudioEffectsChange: (StudioAudioEffects) -> Unit,
    onSpatialModeChange: (SpatialMode) -> Unit,
    onParamsChange: (SpatialParams) -> Unit,
    onVisualizerModeChange: (VisualizerMode) -> Unit,
    onToggleAmoled: () -> Unit,
    onSortOrderChange: (LibrarySortOrder) -> Unit,
    onRescanLibrary: () -> Unit,
    onOpenPrivacyCenter: () -> Unit,
    onClearConversionHistory: () -> Unit,
    onNavigateTab: (ScreenTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        // Top Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                    .border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Settings & Preferences",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
                Text(
                    text = "Configure Audio Engine, Spatial DSP, and Library",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. PLAYBACK SECTION
        SettingsSectionHeader("PLAYBACK ENGINE")
        SettingsCard {
            SettingsSwitchRow(
                title = "Gapless Playback",
                subtitle = "Eliminate silence between consecutive album tracks",
                checked = studioEffects.gaplessPlayback,
                onCheckedChange = { onStudioEffectsChange(studioEffects.copy(gaplessPlayback = it)) }
            )
            SettingsSwitchRow(
                title = "Volume Normalization",
                subtitle = "ReplayGain dynamic target loudness matching",
                checked = studioEffects.volumeNormalization,
                onCheckedChange = { onStudioEffectsChange(studioEffects.copy(volumeNormalization = it)) }
            )
            SettingsSwitchRow(
                title = "Downmix to Mono",
                subtitle = "Single-channel accessibility mixing",
                checked = studioEffects.isMono,
                onCheckedChange = { onStudioEffectsChange(studioEffects.copy(isMono = it)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. AUDIO & EQUALIZER SECTION
        SettingsSectionHeader("AUDIO & STUDIO")
        SettingsCard {
            SettingsNavRow(
                title = "10-Band Graphic Equalizer",
                subtitle = "Preamp, Bass Boost, Treble & Loudness",
                icon = Icons.Default.Equalizer,
                onClick = { onNavigateTab(ScreenTab.STUDIO) }
            )
            SettingsNavRow(
                title = "Audio Effects & Limiter",
                subtitle = "Stereo Width, Exciter, Studio Reverb Chamber",
                icon = Icons.Default.GraphicEq,
                onClick = { onNavigateTab(ScreenTab.STUDIO) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. SPATIAL ENGINE SECTION
        SettingsSectionHeader("SPATIAL AUDIO DSP")
        SettingsCard {
            // HRTF Profile Selection
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Binaural HRTF Acoustic Profile",
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HrtfProfile.values().forEach { profile ->
                        val isSelected = spatialParams.hrtfProfile == profile
                        Surface(
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onParamsChange(spatialParams.copy(hrtfProfile = profile)) }
                        ) {
                            Text(
                                text = profile.displayName.substringBefore(" "),
                                color = if (isSelected) NeonCyan else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. LIBRARY SECTION
        SettingsSectionHeader("LIBRARY & STORAGE")
        SettingsCard {
            SettingsNavRow(
                title = "Rescan Device Audio Files",
                subtitle = "Scan MP3, WAV, FLAC, M4A, AAC and OGG",
                icon = Icons.Default.Refresh,
                onClick = onRescanLibrary
            )
            SettingsNavRow(
                title = "Clear Conversion History",
                subtitle = "Remove exported spatial audio logs",
                icon = Icons.Default.Delete,
                onClick = onClearConversionHistory
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. APPEARANCE SECTION
        SettingsSectionHeader("APPEARANCE")
        SettingsCard {
            SettingsSwitchRow(
                title = "Pure AMOLED Black Theme",
                subtitle = "Max contrast and battery savings on OLED screens",
                checked = isAmoledBlack,
                onCheckedChange = { onToggleAmoled() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. PRIVACY SECTION
        SettingsSectionHeader("PRIVACY & SECURITY")
        SettingsCard {
            SettingsNavRow(
                title = "MKx Privacy Center",
                subtitle = "Verify 100% on-device DSP & zero server uploads",
                icon = Icons.Default.Security,
                onClick = onOpenPrivacyCenter
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7. ABOUT SECTION
        SettingsSectionHeader("ABOUT MKxPLAYER")
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("MKxPLAYER", color = TextWhite, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text("Your music, in every dimension.", color = CyanGlow, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Developed by Mk Hossain", color = NeonCyan, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                Text("POWERED BY BIZ FACTORY", color = TextMuted, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Package: com.bizft.mkxplayer • Version 2.0 Pro", color = Color(0xFF64748B), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp,
            fontSize = 10.5.sp
        ),
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text(subtitle, color = TextMuted, fontSize = 11.sp)
        }
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

@Composable
private fun SettingsNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text(subtitle, color = TextMuted, fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
    }
}
