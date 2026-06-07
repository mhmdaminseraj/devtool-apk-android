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
    primary = NotionBlueDark,
    secondary = NotionGreenDark,
    tertiary = NotionPurpleDark,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = Color(0xFF2F2F2F),
    onPrimary = Color(0xFF191919),
    onSecondary = Color(0xFF191919),
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = NotionRedDark,
    onError = Color(0xFF191919)
)

private val LightColorScheme = lightColorScheme(
    primary = NotionBlueLight,
    secondary = NotionGreenLight,
    tertiary = NotionPurpleLight,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = Color(0xFFF1F1EF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = NotionRedLight,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Automatic responsive support for Light/Dark Notion style representation
    dynamicColor: Boolean = false, // Disable dynamic wallpaper colors to pin precise neutral Notion color identity
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Force RTL for Persian application UI and set standard Notion spacing tokens
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
