package com.llmnotebook.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    // Main colors
    primary = Color(0xFF89B4FA),      // Catppuccin Blue
    onPrimary = Color(0xFFCDD6F4),    // Catppuccin Text
    
    // Secondary colors
    secondary = Color(0xFFF5C2E7),    // Catppuccin Pink
    onSecondary = Color(0xFFCDD6F4),  // Catppuccin Text
    
    // Tertiary colors
    tertiary = Color(0xFFFAB387),     // Catppuccin Peach
    onTertiary = Color(0xFFCDD6F4),   // Catppuccin Text
    
    // Background colors
    background = Color(0xFF11111B),   // Catppuccin Crust (darkest)
    onBackground = Color(0xFFCDD6F4), // Catppuccin Text
    
    // Surface colors
    surface = Color(0xFF11111B),      // Catppuccin Crust (darkest)
    onSurface = Color(0xFF6C7086),    // Catppuccin Overlay0 (grey)
    surfaceVariant = Color(0xFF181825), // Catppuccin Mantle
    onSurfaceVariant = Color(0xFF6C7086), // Catppuccin Overlay0 (grey)
    
    // Container colors
    primaryContainer = Color(0xFF181825), // Catppuccin Mantle
    onPrimaryContainer = Color(0xFFCDD6F4), // Catppuccin Text
    secondaryContainer = Color(0xFF181825), // Catppuccin Mantle
    onSecondaryContainer = Color(0xFFCDD6F4), // Catppuccin Text
    tertiaryContainer = Color(0xFF181825), // Catppuccin Mantle
    onTertiaryContainer = Color(0xFFCDD6F4), // Catppuccin Text,
    
    // Input field colors
    surfaceTint = Color(0xFF11111B),  // Catppuccin Crust
    inverseSurface = Color(0xFFCDD6F4), // Catppuccin Text
    inverseOnSurface = Color(0xFF11111B), // Catppuccin Crust
    
    // Other colors
    error = Color(0xFFF38BA8),        // Catppuccin Red
    onError = Color(0xFFCDD6F4),      // Catppuccin Text
    errorContainer = Color(0xFFEBA0AC), // Catppuccin Maroon
    onErrorContainer = Color(0xFFCDD6F4), // Catppuccin Text
    outline = Color(0xFF6C7086),      // Catppuccin Overlay0
    outlineVariant = Color(0xFF7F849C) // Catppuccin Overlay1
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface
)

@Composable
fun LLMNotebookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = (if (darkTheme) Color(0xFF11111B) else colorScheme.primary).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
