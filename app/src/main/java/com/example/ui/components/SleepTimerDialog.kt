package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun SleepTimerDialog(
    remainingSeconds: Int?,
    onSetTimer: (minutes: Int) -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(15, 30, 45, 60, 90)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sleep Timer")
            }
        },
        text = {
            Column {
                if (remainingSeconds != null) {
                    val mins = remainingSeconds / 60
                    val secs = remainingSeconds % 60
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text("Playback stops in:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(
                                text = String.format("%02d:%02d", mins, secs),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onCancelTimer()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Turn Off Sleep Timer")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = "Automatically pause playback after:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                options.forEach { minutes ->
                    Surface(
                        color = Color(0xFF162035),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF24365C)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onSetTimer(minutes)
                                onDismiss()
                            }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text("$minutes minutes", color = TextWhite, fontWeight = FontWeight.Medium)
                            Text("Set", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = TextMuted)
            ) {
                Text("Dismiss")
            }
        },
        containerColor = Color(0xFF131D31),
        titleContentColor = TextWhite
    )
}
