package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioTrackItem
import com.example.model.LyricLine
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack

@Composable
fun LyricsScreen(
    track: AudioTrackItem?,
    lyrics: List<LyricLine>,
    currentPositionMs: Long,
    offsetMs: Long,
    fontSizeSp: Int,
    isHighContrast: Boolean,
    onAdjustOffset: (Long) -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onToggleHighContrast: () -> Unit,
    onImportLrcText: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var isFullScreen by remember { mutableStateOf(false) }

    val lrcPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                if (content != null) {
                    onImportLrcText(content)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Determine active line index based on current playback timestamp with manual offset
    val effectiveTimeMs = currentPositionMs + offsetMs
    val activeIndex = lyrics.indexOfLast { it.timestampMs <= effectiveTimeMs }.coerceAtLeast(0)

    LaunchedEffect(activeIndex) {
        if (lyrics.isNotEmpty() && activeIndex in lyrics.indices) {
            val targetScroll = (activeIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        }
    }

    val backgroundColor = if (isHighContrast) Color.Black else VoidBlack
    val activeTextColor = if (isHighContrast) Color(0xFF00FFCC) else NeonCyan

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
            .testTag("lyrics_screen")
    ) {
        // Top Navigation Bar
        if (!isFullScreen) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Synchronized Lyrics",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        )
                        Text(
                            text = "${track?.title ?: ""} • ${track?.artist ?: ""}",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyanGlow, fontSize = 11.sp),
                            maxLines = 1
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { lrcPickerLauncher.launch(arrayOf("*/*")) }) {
                        Icon(Icons.Default.FileOpen, contentDescription = "Import LRC", tint = NeonCyan)
                    }
                    IconButton(onClick = { isFullScreen = true }) {
                        Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = TextWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sync Adjustment Controls
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Manual offset
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sync Offset: ", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = "${if (offsetMs >= 0) "+" else ""}${offsetMs}ms",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = { onAdjustOffset(-500L) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "-0.5s", tint = TextWhite, modifier = Modifier.size(14.dp))
                        }
                        IconButton(onClick = { onAdjustOffset(500L) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "+0.5s", tint = TextWhite, modifier = Modifier.size(14.dp))
                        }
                    }

                    // Font Size & Contrast
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF162035),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable {
                                val next = if (fontSizeSp >= 24) 14 else fontSizeSp + 2
                                onFontSizeChange(next)
                            }
                        ) {
                            Text(
                                text = "Font A±",
                                color = TextWhite,
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = if (isHighContrast) NeonCyan else Color(0xFF162035),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable { onToggleHighContrast() }
                        ) {
                            Text(
                                text = "Contrast",
                                color = if (isHighContrast) Color.Black else TextWhite,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Fullscreen Mode Exit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { isFullScreen = false }) {
                    Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Fullscreen", tint = TextMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (lyrics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lyrics,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No synchronized .lrc lyrics found for \"${track?.title ?: ""}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { lrcPickerLauncher.launch(arrayOf("*/*")) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import .LRC File", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(40.dp)) }

                itemsIndexed(lyrics) { index, line ->
                    val isActive = index == activeIndex
                    val isPast = index < activeIndex

                    val textColor = when {
                        isActive -> activeTextColor
                        isPast -> TextWhite
                        else -> TextDim
                    }
                    val currentFontSp = if (isActive) (fontSizeSp + 3).sp else fontSizeSp.sp
                    val fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium

                    Text(
                        text = line.text,
                        color = textColor,
                        fontSize = currentFontSp,
                        fontWeight = fontWeight,
                        textAlign = TextAlign.Center,
                        lineHeight = (fontSizeSp + 10).sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(140.dp)) }
            }
        }
    }
}
