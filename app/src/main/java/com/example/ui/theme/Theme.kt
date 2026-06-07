package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = Color(0xFFBD93F9),
    background = Background,
    surface = Surface,
    surfaceVariant = Color(0xFF1F2638),
    onPrimary = Color(0xFF001F24),
    onSecondary = Color(0xFF002010),
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF2E3A54),
    error = RedUrgent,
    onError = Color(0xFF5F0014)
)

private val LightColorScheme = DarkColorScheme // Always maintain dark developer style as requested by the user

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark programmer aesthetics
    dynamicColor: Boolean = false, // Disable system wallpaper accents to preserve developer palette branding
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    // Force RTL for Persian application UI
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
