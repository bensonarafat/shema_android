package com.pulsereality.shema.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary Brand Colors (Exact match with iOS Shema ThemeColors)
val PrimaryBlue = Color(0xFF288AD5)
val PrimaryBlueHover = Color(0xFF2078BC)
val PrimaryBlueSubtleLight = Color(0xFFE8F3FB)
val PrimaryBlueSubtleDark = Color(0xFF153A56)

val BrandGreen = Color(0xFF12B76A)
val BrandGreenSubtle = Color(0xFF32D583)
val GoldAccent = Color(0xFFFFB800)
val AmberWarning = Color(0xFFF79009)
val WarningAmber = AmberWarning
val ErrorRed = Color(0xFFF04438)

// Background & Surface - Dark Mode (Exact iOS Dark ColorTheme)
val BackgroundDark = Color(0xFF0B1118)
val SurfaceDark = Color(0xFF121B24)
val SurfaceSecondaryDark = Color(0xFF192530)
val BorderDark = Color(0xFF263442)
val DividerDark = Color(0xFF1E2D3D)
val TertiarySystemFillDark = Color(0x3D767680) // Apple tertiarySystemFill dark (24% alpha)
val SecondarySystemGroupedBgDark = Color(0xFF1C1C1E)

// Text Colors - Dark Mode
val TextPrimaryDark = Color(0xFFF5F7FA)
val TextSecondaryDark = Color(0xFFAAB4C0)
val TextMutedDark = Color(0xFF66727F)

// Background & Surface - Light Mode (Exact iOS Light ColorTheme)
val BackgroundLight = Color(0xFFF7F9FC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceSecondaryLight = Color(0xFFF1F5F9)
val BorderLight = Color(0xFFE4E7EC)
val DividerLight = Color(0xFFE4E7EC)
val TertiarySystemFillLight = Color(0x1F767680) // Apple tertiarySystemFill light (12% alpha)
val SecondarySystemGroupedBgLight = Color(0xFFFFFFFF)

// Text Colors - Light Mode
val TextPrimaryLight = Color(0xFF101828)
val TextSecondaryLight = Color(0xFF667085)
val TextMutedLight = Color(0xFF98A2B3)

// Semantic Theme Extension for exact iOS parity
@Immutable
data class ExtendedColors(
    val primary: Color,
    val primarySubtle: Color,
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val tertiaryFill: Color,
    val secondaryGroupedBg: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val gold: Color,
    val green: Color,
    val red: Color,
    val isDark: Boolean
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        primary = PrimaryBlue,
        primarySubtle = PrimaryBlueSubtleDark,
        background = BackgroundDark,
        surface = SurfaceDark,
        surfaceSecondary = SurfaceSecondaryDark,
        tertiaryFill = TertiarySystemFillDark,
        secondaryGroupedBg = SecondarySystemGroupedBgDark,
        border = BorderDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textMuted = TextMutedDark,
        gold = GoldAccent,
        green = BrandGreen,
        red = ErrorRed,
        isDark = true
    )
}
