package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MKxDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = CyanGlow.copy(alpha = 0.2f),
    onPrimaryContainer = NeonCyan,
    secondary = ElectricMagenta,
    onSecondary = Color.White,
    secondaryContainer = MagentaGlow.copy(alpha = 0.2f),
    onSecondaryContainer = ElectricMagenta,
    tertiary = VortexPurple,
    onTertiary = Color.White,
    tertiaryContainer = SpatialViolet.copy(alpha = 0.25f),
    onTertiaryContainer = VortexPurple,
    background = VoidBlack,
    onBackground = TextWhite,
    surface = DarkNavy,
    onSurface = TextWhite,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextMuted,
    surfaceTint = NeonCyan,
    outline = SurfaceBorder,
    outlineVariant = SurfaceElevated
)

private val MKxLightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    secondary = Color(0xFFD946EF),
    onSecondary = Color.White,
    tertiary = Color(0xFF8B5CF6),
    onTertiary = Color.White,
    background = Color(0xFF0F172A), // Dark music player by design
    onBackground = TextWhite,
    surface = Color(0xFF1E293B),
    onSurface = TextWhite,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = TextMuted
)

@Composable
fun MKxPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Music player stays predominantly in high-contrast dark aesthetic for spatial visualization
    val colorScheme = if (darkTheme) MKxDarkColorScheme else MKxLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MKxPlayerTheme(darkTheme = darkTheme, content = content)
}
