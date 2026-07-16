package com.example.bukukita.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = BookstorePrimary,
    secondary = BookstoreSecondary,
    background = BookstoreBackground,
    surface = BookstoreSurface,
    onPrimary = BookstoreOnPrimary,
    onSecondary = BookstoreOnSecondary,
    onBackground = BookstoreOnBackground,
    onSurface = BookstoreOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = BookstorePrimary,
    secondary = BookstoreSecondary,
    background = BookstoreBackground,
    surface = BookstoreSurface,
    onPrimary = BookstoreOnPrimary,
    onSecondary = BookstoreOnSecondary,
    onBackground = BookstoreOnBackground,
    onSurface = BookstoreOnSurface
)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(16.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(16.dp)
)

@Composable
fun BukuKitaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}