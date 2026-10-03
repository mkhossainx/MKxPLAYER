package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun PrivacyCenterDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF0F172A), CircleShape)
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("MKx Privacy Center", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Zero Cloud • 100% On-Device", color = NeonCyan, fontSize = 10.5.sp)
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "MKxPLAYER processes your audio exclusively inside your device's memory. No telemetry, audio samples, or playlists leave your phone.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.5.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                PrivacyAuditRow(
                    label = "Audio Processing",
                    status = "On-Device DSP",
                    icon = Icons.Default.Memory,
                    isPositive = true
                )
                PrivacyAuditRow(
                    label = "Audio Upload",
                    status = "Never (0 Bytes)",
                    icon = Icons.Default.CloudOff,
                    isPositive = true
                )
                PrivacyAuditRow(
                    label = "Account / Sign-in",
                    status = "Not Required",
                    icon = Icons.Default.NoAccounts,
                    isPositive = true
                )
                PrivacyAuditRow(
                    label = "Cloud Processing",
                    status = "Disabled By Design",
                    icon = Icons.Default.CloudOff,
                    isPositive = true
                )
                PrivacyAuditRow(
                    label = "Network for Playback",
                    status = "None (Offline Native)",
                    icon = Icons.Default.WifiOff,
                    isPositive = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Got It", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF131D31)
    )
}

@Composable
private fun PrivacyAuditRow(
    label: String,
    status: String,
    icon: ImageVector,
    isPositive: Boolean
) {
    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(status, color = Color(0xFF10B981), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
            }
        }
    }
}
