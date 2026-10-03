package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("about_screen")
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
            Text(
                text = "About MKxPLAYER",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Brand Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF090D16), CircleShape)
                        .border(2.dp, NeonCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MKxPLAYER",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        letterSpacing = 2.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Your music, in every dimension.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = CyanGlow,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Developed by Mk Hossain",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Core Architecture & Feature Details
        AboutFeatureCard(
            title = "8D, 16D, and 24D Spatial Engine",
            description = "Binaural Head-Related Transfer Function (HRTF) simulator with microsecond Interaural Time Difference (ITD) delays, head-shadow filtering, and multi-axis 3D orbital trajectory automation.",
            icon = Icons.Default.SurroundSound,
            accentColor = NeonCyan
        )

        Spacer(modifier = Modifier.height(12.dp))

        AboutFeatureCard(
            title = "100% On-Device Privacy",
            description = "Zero server uploads. All audio decoding, spatial DSP processing, and 320kbps MP3 / Lossless 16-bit WAV file conversions execute strictly on your device.",
            icon = Icons.Default.Security,
            accentColor = ElectricMagenta
        )

        Spacer(modifier = Modifier.height(12.dp))

        AboutFeatureCard(
            title = "Headphone Requirement",
            description = "For authentic 8D/16D/24D binaural separation, wear stereo headphones, earbuds, or AirPods. Standard mono phone speakers cannot reproduce spatial acoustic panning.",
            icon = Icons.Default.Headphones,
            accentColor = AmberGlow
        )

        Spacer(modifier = Modifier.height(12.dp))

        AboutFeatureCard(
            title = "Hardware Equalizer & Gapless Playback",
            description = "Native 5-band graphic equalizer with bass boost, virtualizer, background playback foreground service, and sleep timer.",
            icon = Icons.Default.MusicNote,
            accentColor = VortexPurple
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "POWERED BY BIZ FACTORY",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TextMuted
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Version 1.0 • Build com.bizft.mkxplayer",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 10.sp)
            )
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun AboutFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
