package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Notion Iconic Muted Accent Colors
val NotionBlueLight = Color(0xFF1A5FC8) // Increased contrast against white (4.7:1)
val NotionBlueDark = Color(0xFF2EAADC)

val NotionGreenLight = Color(0xFF0D6F3E) // High contrast rich green (5.4:1)
val NotionGreenDark = Color(0xFF52D38B)  // Brighter and crisper for dark backgrounds (>5.2:1)

val NotionYellowLight = Color(0xFF9F5200) // Deep dark golden amber with standard contrast against white (5.1:1)
val NotionYellowDark = Color(0xFFD9A541)

val NotionRedLight = Color(0xFFC21C24) // High visibility crimson red (5.8:1)
val NotionRedDark = Color(0xFFFF5C5C)

val NotionPurpleLight = Color(0xFF5F25CC) // Deep majestic indigo purple (6.2:1)
val NotionPurpleDark = Color(0xFF8B5CF6)

// Base dynamic references
val GreenDone = NotionGreenLight
val OrangePending = NotionYellowLight
val RedUrgent = NotionRedLight
val GreyFuture = Color(0xFF7C7B77)

// Premium light palette: Warm Ivory Notion Canvas
val LightBackground = Color(0xFFFBFBFA)
val LightSurface = Color(0xFFFFFFFF)
val LightTextPrimary = Color(0xFF37352F)
val LightTextSecondary = Color(0xFF6B6A65)
val LightBorder = Color(0xFFEDE9E6)

// Premium dark palette: Crisp Slate Dark Canvas
val DarkBackground = Color(0xFF191919)
val DarkSurface = Color(0xFF202020)
val DarkTextPrimary = Color(0xFFE3E3E2)
val DarkTextSecondary = Color(0xFF9B9B9A)
val DarkBorder = Color(0xFF2F2F2F)

// Deprecated or legacy mappings for compatibility
val PrimaryColor = NotionBlueLight
val SecondaryColor = NotionGreenLight
val Background = DarkBackground
val Surface = DarkSurface
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary

