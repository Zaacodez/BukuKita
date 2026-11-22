package com.example.bukukita.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

val BukuKitaLightColorScheme = lightColorScheme(
    primary = Sage600,
    onPrimary = Paper0,
    primaryContainer = Sage100,
    onPrimaryContainer = Sage800,
    secondary = Eucalyptus600,
    onSecondary = Paper0,
    secondaryContainer = Eucalyptus100,
    onSecondaryContainer = Eucalyptus800,
    tertiary = Terracotta600,
    onTertiary = Paper0,
    tertiaryContainer = Terracotta100,
    onTertiaryContainer = Terracotta800,
    background = Ivory50,
    onBackground = Charcoal900,
    surface = Paper0,
    onSurface = Charcoal900,
    surfaceVariant = Linen100,
    onSurfaceVariant = Slate700,
    surfaceContainerLowest = Paper0,
    surfaceContainerLow = Linen100,
    surfaceContainer = Parchment200,
    surfaceContainerHigh = Oat300,
    surfaceContainerHighest = Rule350,
    outline = Stone500,
    outlineVariant = Rule350,
    error = Brick600,
    onError = Paper0,
    errorContainer = Brick100,
    onErrorContainer = Brick900
)

val BukuKitaDarkColorScheme = darkColorScheme(
    primary = Sage300,
    onPrimary = Sage900,
    primaryContainer = Sage700,
    onPrimaryContainer = Sage50,
    secondary = Eucalyptus300,
    onSecondary = Eucalyptus900,
    secondaryContainer = Eucalyptus700,
    onSecondaryContainer = Eucalyptus50,
    tertiary = Terracotta300,
    onTertiary = Terracotta900,
    tertiaryContainer = Terracotta700,
    onTertiaryContainer = Terracotta50,
    background = Night950,
    onBackground = Cream100,
    surface = Night900,
    onSurface = Cream100,
    surfaceVariant = Night850,
    onSurfaceVariant = Mist300,
    surfaceContainerLowest = Night950,
    surfaceContainerLow = Night900,
    surfaceContainer = Night850,
    surfaceContainerHigh = Night800,
    surfaceContainerHighest = Night750,
    outline = Ash500,
    outlineVariant = Night700,
    error = Coral300,
    onError = Coral900,
    errorContainer = Brick850,
    onErrorContainer = Coral50
)

val BukuKitaShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun BukuKitaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    },
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BukuKitaDarkColorScheme else BukuKitaLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BukuKitaTypography,
        shapes = BukuKitaShapes,
        content = content
    )
}
