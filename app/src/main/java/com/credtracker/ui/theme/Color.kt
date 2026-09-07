package com.credtracker.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BgObsidian = Color(0xFF08080C)
val BgSurface = Color(0xFF12121A)
val BgCard = Color(0xFF181824)
val BgCardElevated = Color(0xFF222232)
val BorderSubtle = Color(0xFF2A2A3D)
val BorderGlowing = Color(0xFF3E3E58)

val NeonCyan = Color(0xFF00F5D4)
val NeonEmerald = Color(0xFF00E676)
val ElectricPurple = Color(0xFF7928CA)
val CyberGold = Color(0xFFFFB703)
val CrimsonAlert = Color(0xFFFF0055)
val AmberWarning = Color(0xFFFF9100)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9E9EA7)
val TextMuted = Color(0xFF636374)

// Gradient Brushes
val CredGoldGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF2E2413), Color(0xFF1B1711), Color(0xFF0F0E0A))
)

val CredPurpleGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF33164D), Color(0xFF1A112B), Color(0xFF0E0B17))
)

val CredCyanGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0B3338), Color(0xFF0D1E24), Color(0xFF091114))
)

val CredEmeraldGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0E3821), Color(0xFF0A2114), Color(0xFF06120B))
)

val CredCrimsonGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF3E121E), Color(0xFF230D14), Color(0xFF12070A))
)

val HeroGlowBrush = Brush.radialGradient(
    colors = listOf(Color(0x3300F5D4), Color(0x0008080C))
)
