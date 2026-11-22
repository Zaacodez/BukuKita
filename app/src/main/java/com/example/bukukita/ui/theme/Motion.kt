package com.example.bukukita.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext

/**
 * Centralized motion tokens for restrained editorial transitions.
 * Exit transitions run ~65% faster than enters per Material motion guidance.
 */
object BukuKitaMotion {
    const val TapFeedbackMs = 100
    const val StateChangeMs = 200
    const val ContentCrossfadeMs = 240
    const val ScreenEnterMs = 280
    const val ScreenExitMs = 180

    val StandardEasing = FastOutSlowInEasing
    val ExitEasing = FastOutLinearInEasing
    val DecelerateEasing = LinearOutSlowInEasing
}

@Composable
fun rememberReducedMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        } catch (_: Exception) {
            false
        }
    }
}

@Composable
fun Modifier.editorialPressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.98f
): Modifier {
    val reducedMotion = rememberReducedMotionEnabled()
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (!reducedMotion && isPressed) pressedScale else 1f,
        animationSpec = tween(
            durationMillis = BukuKitaMotion.TapFeedbackMs,
            easing = BukuKitaMotion.StandardEasing
        ),
        label = "editorialPressScale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
