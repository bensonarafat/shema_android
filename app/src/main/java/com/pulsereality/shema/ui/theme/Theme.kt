package com.pulsereality.shema.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextPrimaryDark,
    primaryContainer = PrimaryBlueSubtleDark,
    onPrimaryContainer = PrimaryBlue,
    secondary = BrandGreen,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceSecondaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    error = ErrorRed,
    onError = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextPrimaryLight,
    primaryContainer = PrimaryBlueSubtleLight,
    onPrimaryContainer = PrimaryBlue,
    secondary = BrandGreen,
    onSecondary = SurfaceLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceSecondaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = ErrorRed,
    onError = SurfaceLight
)

private val DarkExtendedColors = ExtendedColors(
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

private val LightExtendedColors = ExtendedColors(
    primary = PrimaryBlue,
    primarySubtle = PrimaryBlueSubtleLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceSecondary = SurfaceSecondaryLight,
    tertiaryFill = TertiarySystemFillLight,
    secondaryGroupedBg = SecondarySystemGroupedBgLight,
    border = BorderLight,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    textMuted = TextMutedLight,
    gold = GoldAccent,
    green = BrandGreen,
    red = ErrorRed,
    isDark = false
)

object ShemaTheme {
    val colors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}

@Composable
fun ShemaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ShemaTypography,
            content = content
        )
    }
}
