package com.barfet.gemmaassistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define custom colors
private val primaryDark = Color(0xFF90CAF9)  // Blue 200
private val secondaryDark = Color(0xFFCE93D8)  // Purple 200
private val backgroundDark = Color(0xFF121212)  // Dark gray
private val surfaceDark = Color(0xFF1E1E1E)  // Slightly lighter dark gray

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    secondary = secondaryDark,
    background = backgroundDark,
    surface = surfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

// Light theme colors (can be expanded later)
private val LightColorScheme = lightColorScheme(
    // Default light colors for now
)

/**
 * Main theme composable for the Gemma Assistant app.
 * Currently uses dark theme by default.
 */
@Composable
fun GemmaAssistantTheme(
    darkTheme: Boolean = true, // Dark theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = com.barfet.gemmaassistant.ui.theme.Typography,
        content = content
    )
} 