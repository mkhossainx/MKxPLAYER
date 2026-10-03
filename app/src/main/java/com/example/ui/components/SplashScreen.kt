package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VortexPurple
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val rotation = remember { Animatable(0f) }
    val pulse = remember { Animatable(0.9f) }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    LaunchedEffect(Unit) {
        pulse.animateTo(
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Auto-advance after 2.8 seconds
    LaunchedEffect(Unit) {
        delay(2800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF131D31),
                        VoidBlack
                    ),
                    center = Offset(500f, 600f),
                    radius = 900f
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Animated 3D Spatial Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2.3f
                    val center = Offset(size.width / 2, size.height / 2)
                    val angleRad = Math.toRadians(rotation.value.toDouble())

                    // Outer 8D orbit ring
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(NeonCyan, VortexPurple, ElectricMagenta, NeonCyan),
                            center = center
                        ),
                        radius = radius,
                        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                    )

                    // 16D Figure-8 intersecting ring
                    drawCircle(
                        color = Color(0x3300F2FE),
                        radius = radius * 0.75f,
                        style = Stroke(width = 2.0f)
                    )

                    // Orbiting spatial audio node
                    val nodeX = center.x + radius * cos(angleRad).toFloat()
                    val nodeY = center.y + radius * sin(angleRad).toFloat()

                    drawCircle(
                        color = Color(0x6600F2FE),
                        radius = 14f * pulse.value,
                        center = Offset(nodeX, nodeY)
                    )
                    drawCircle(
                        color = NeonCyan,
                        radius = 6.5f,
                        center = Offset(nodeX, nodeY)
                    )

                    // Second opposite orbital node (Magenta)
                    val node2X = center.x + radius * cos(angleRad + Math.PI).toFloat()
                    val node2Y = center.y + radius * sin(angleRad + Math.PI).toFloat()

                    drawCircle(
                        color = Color(0x66FF007F),
                        radius = 12f * pulse.value,
                        center = Offset(node2X, node2Y)
                    )
                    drawCircle(
                        color = ElectricMagenta,
                        radius = 5.5f,
                        center = Offset(node2X, node2Y)
                    )
                }

                // Center glowing headphone badge
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF0F172A),
                    shadowElevation = 12.dp,
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "MKxPLAYER Headphones",
                            tint = NeonCyan,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Brand Name
            Text(
                text = "MKxPLAYER",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = TextWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Spatial Dimension Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                DimensionBadge(label = "8D")
                Spacer(modifier = Modifier.width(6.dp))
                DimensionBadge(label = "16D")
                Spacer(modifier = Modifier.width(6.dp))
                DimensionBadge(label = "24D")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tagline
            Text(
                text = "Your music, in every dimension.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = CyanGlow
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Developer Credit
            Text(
                text = "Developed by Mk Hossain",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    color = TextMuted
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Headphone Recommendation Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31).copy(alpha = 0.85f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Stereo headphones or AirPods recommended for full 8D / 16D / 24D binaural spatial separation.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, color = TextWhite)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onFinished,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("enter_player_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ENTER SPATIAL AUDIO",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Splash Screen Footer
            Text(
                text = "POWERED BY BIZ FACTORY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = TextMuted.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DimensionBadge(label: String) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}
