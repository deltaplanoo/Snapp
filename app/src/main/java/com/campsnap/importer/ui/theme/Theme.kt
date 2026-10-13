package com.campsnap.importer.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CampSnapAmberLight,
    onPrimary = DarkBackground,
    primaryContainer = CampSnapAmberDark,
    onPrimaryContainer = CampSnapAmberLight,
    secondary = CampSnapTeal,
    onSecondary = DarkBackground,
    secondaryContainer = CampSnapTealDark,
    onSecondaryContainer = CampSnapTealLight,
    background = DarkBackground,
    onBackground = LightSurface,
    surface = DarkSurface,
    onSurface = LightSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LightBorder,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = CampSnapAmber,
    onPrimary = LightSurface,
    primaryContainer = CampSnapAmberLight,
    onPrimaryContainer = DarkBackground,
    secondary = CampSnapTeal,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = DarkBackground,
    surface = LightSurface,
    onSurface = DarkBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = DarkSurfaceVariant,
    outline = LightBorder
)

@Composable
fun CampSnapImporterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinctive Camp Snap branding by default
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
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
