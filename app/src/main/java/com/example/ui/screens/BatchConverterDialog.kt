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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioTrackItem
import com.example.model.BatchConversionJob
import com.example.model.ExportFormat
import com.example.model.JobStatus
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple

@Composable
fun BatchConverterDialog(
    allTracks: List<AudioTrackItem>,
    batchQueue: List<BatchConversionJob>,
    isBatchRunning: Boolean,
    onAddJobs: (tracks: List<AudioTrackItem>, mode: SpatialMode, params: SpatialParams, format: ExportFormat) -> Unit,
    onStartBatch: () -> Unit,
    onCancelJob: (String) -> Unit,
    onClearBatchQueue: () -> Unit,
    onDismiss: () -> Unit
) {
    val selectedTracks = remember { mutableStateListOf<AudioTrackItem>() }
    var selectedMode by remember { mutableStateOf(SpatialMode.SPATIAL_8D) }
    var selectedFormat by remember { mutableStateOf(ExportFormat.WAV_16BIT) }
    var isConfigMode by remember { mutableStateOf(batchQueue.isEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QueueMusic, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Batch Spatial Converter", color = TextWhite)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(440.dp)) {
                // Tab switcher between "New Batch" and "Live Queue"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (isConfigMode) NeonCyan else Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).clickable { isConfigMode = true }
                    ) {
                        Text(
                            text = "Select Tracks",
                            color = if (isConfigMode) Color.Black else TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    Surface(
                        color = if (!isConfigMode) NeonCyan else Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).clickable { isConfigMode = false }
                    ) {
                        Text(
                            text = "Queue (${batchQueue.size})",
                            color = if (!isConfigMode) Color.Black else TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isConfigMode) {
                    // Step 1: Select Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(SpatialMode.SPATIAL_8D, SpatialMode.SPATIAL_16D, SpatialMode.SPATIAL_24D).forEach { m ->
                            val isSel = selectedMode == m
                            Surface(
                                color = if (isSel) Color(0xFF1E293B) else Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NeonCyan else Color(0xFF334155)),
                                modifier = Modifier.weight(1f).clickable { selectedMode = m }
                            ) {
                                Text(
                                    text = m.dimensionLabel,
                                    color = if (isSel) NeonCyan else TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step 2: Format selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(ExportFormat.WAV_16BIT, ExportFormat.MP3_320K, ExportFormat.MP3_256K).forEach { f ->
                            val isSel = selectedFormat == f
                            Surface(
                                color = if (isSel) Color(0xFF1E293B) else Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ElectricMagenta else Color(0xFF334155)),
                                modifier = Modifier.weight(1f).clickable { selectedFormat = f }
                            ) {
                                Text(
                                    text = if (f == ExportFormat.WAV_16BIT) "WAV 16-bit" else f.extension.uppercase() + " " + f.bitrateKbps + "k",
                                    color = if (isSel) ElectricMagenta else TextWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("SELECT SONGS TO CONVERT (${selectedTracks.size}):", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(allTracks) { track ->
                            val isChecked = selectedTracks.contains(track)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedTracks.remove(track)
                                        else selectedTracks.add(track)
                                    }
                                    .padding(vertical = 3.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        if (it) selectedTracks.add(track) else selectedTracks.remove(track)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(track.title, color = TextWhite, fontSize = 12.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(track.artist, color = TextMuted, fontSize = 10.5.sp, maxLines = 1)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (selectedTracks.isNotEmpty()) {
                                onAddJobs(selectedTracks.toList(), selectedMode, SpatialParams.CLASSIC_8D, selectedFormat)
                                selectedTracks.clear()
                                isConfigMode = false
                                onStartBatch()
                            }
                        },
                        enabled = selectedTracks.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Add to Queue & Start (${selectedTracks.size})", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Queue List
                    if (batchQueue.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No jobs in batch queue", color = TextMuted)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(batchQueue) { job ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(job.track.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                                Text("${job.mode.dimensionLabel} • ${job.format.displayName}", color = TextMuted, fontSize = 10.sp)
                                            }
                                            if (job.status == JobStatus.QUEUED || job.status == JobStatus.PROCESSING) {
                                                IconButton(onClick = { onCancelJob(job.id) }, modifier = Modifier.size(28.dp)) {
                                                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TextMuted, modifier = Modifier.size(16.dp))
                                                }
                                            } else if (job.status == JobStatus.COMPLETED) {
                                                Icon(Icons.Default.Check, contentDescription = "Done", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                            }
                                        }

                                        if (job.status == JobStatus.PROCESSING) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            LinearProgressIndicator(
                                                progress = { job.progressPercent / 100f },
                                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                                color = NeonCyan
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(job.statusMessage, color = NeonCyan, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = onClearBatchQueue,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155), contentColor = TextWhite),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Clear Queue")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onStartBatch,
                                enabled = !isBatchRunning && batchQueue.any { it.status == JobStatus.QUEUED },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (isBatchRunning) "Running..." else "Start Batch")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = Color(0xFF131D31)
    )
}
