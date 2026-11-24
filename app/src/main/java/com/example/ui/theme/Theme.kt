package com.example.ui.theme

import android.graphics.Color as AndroidColor
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.data.model.AccentPalette
import com.example.data.model.ThemeMode

val LocalAccentColor = compositionLocalOf { CobaltBlue }
val LocalIsDarkTheme = compositionLocalOf { true }
val LocalGoldAccentColor = compositionLocalOf { RoyalGold }

@Composable
fun KoshvantaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentPalette: AccentPalette = AccentPalette.COBALT,
    largeTextMode: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val accentPrimary = Color(accentPalette.primaryHex)
    val accentSecondary = Color(accentPalette.secondaryHex)

    // High-contrast editorial accents for Light Mode (prevents washed-out neon on white surfaces)
    val lightEditorialAccent = when (accentPalette) {
        AccentPalette.PURPLE -> Color(0xFF6D28D9)
        AccentPalette.ROSE -> Color(0xFFBE123C)
        AccentPalette.COBALT,
        AccentPalette.NEON_MINT,
        AccentPalette.CYAN,
        AccentPalette.EMERALD -> LightEmeraldTeal
    }

    val effectiveAccent = if (darkTheme) accentPrimary else lightEditorialAccent
    val effectiveGoldAccent = if (darkTheme) RoyalGold else LightAmberBronze

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = accentSecondary,
            onPrimary = Color.Black,
            primaryContainer = accentPrimary,
            onPrimaryContainer = Color.White,
            secondary = accentPrimary,
            onSecondary = Color.White,
            tertiary = StateGreen,
            background = DarkCanvasBg,
            onBackground = DarkTextPrimary,
            surface = DarkSurfaceElevated,
            onSurface = DarkTextPrimary,
            surfaceVariant = DarkSurfaceGlass,
            onSurfaceVariant = DarkTextSecondary,
            outline = DarkCardBorder,
            error = StateRed,
            onError = Color.White
        )
        else -> lightColorScheme(
            primary = lightEditorialAccent,
            onPrimary = Color.White,
            primaryContainer = LightEmeraldTealSoft,
            onPrimaryContainer = LightEmeraldTeal,
            secondary = LightAmberBronze,
            onSecondary = Color.White,
            secondaryContainer = LightAmberBronzeSoft,
            onSecondaryContainer = LightAmberBronze,
            tertiary = LightEmeraldTeal,
            background = LightCanvasBg,
            onBackground = LightTextPrimary,
            surface = LightSurfaceWhite,
            onSurface = LightTextPrimary,
            surfaceVariant = LightSurfaceGlass,
            onSurfaceVariant = LightTextSecondary,
            outline = LightCardBorder,
            outlineVariant = LightDivider,
            error = StateRed,
            onError = Color.White
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        LaunchedEffect(darkTheme, view) {
            val activity = view.context as? ComponentActivity
            if (activity != null) {
                val barStyle = if (darkTheme) {
                    SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                } else {
                    SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                }
                activity.enableEdgeToEdge(
                    statusBarStyle = barStyle,
                    navigationBarStyle = barStyle
                )
            }
            val window = (view.context as? android.app.Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    val scaledTypography = if (largeTextMode) {
        Typography(
            displayLarge = Typography.displayLarge.copy(fontSize = Typography.displayLarge.fontSize * 1.15f),
            headlineSmall = Typography.headlineSmall.copy(fontSize = Typography.headlineSmall.fontSize * 1.15f),
            titleMedium = Typography.titleMedium.copy(fontSize = Typography.titleMedium.fontSize * 1.15f),
            titleSmall = Typography.titleSmall.copy(fontSize = Typography.titleSmall.fontSize * 1.15f),
            bodyLarge = Typography.bodyLarge.copy(fontSize = Typography.bodyLarge.fontSize * 1.15f),
            bodyMedium = Typography.bodyMedium.copy(fontSize = Typography.bodyMedium.fontSize * 1.15f),
            bodySmall = Typography.bodySmall.copy(fontSize = Typography.bodySmall.fontSize * 1.15f),
            labelSmall = Typography.labelSmall.copy(fontSize = Typography.labelSmall.fontSize * 1.15f)
        )
    } else {
        Typography
    }

    CompositionLocalProvider(
        LocalAccentColor provides effectiveAccent,
        LocalIsDarkTheme provides darkTheme,
        LocalGoldAccentColor provides effectiveGoldAccent
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            content = content
        )
    }
}
