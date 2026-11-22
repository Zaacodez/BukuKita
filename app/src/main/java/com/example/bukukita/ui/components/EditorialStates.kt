package com.example.bukukita.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bukukita.R
import com.example.bukukita.ui.theme.BukuKitaSpacing
import com.example.bukukita.ui.theme.Ivory50
import com.example.bukukita.ui.theme.rememberReducedMotionEnabled

@Composable
fun rememberEditorialShimmerBrush(): Brush {
    val reducedMotion = rememberReducedMotionEnabled()
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surfaceContainerHigh

    if (reducedMotion) {
        return SolidColor(base)
    }

    val transition = rememberInfiniteTransition(label = "editorialShimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(offset - 300f, 0f),
        end = Offset(offset, 260f)
    )
}

@Composable
fun BookGridShimmer(
    modifier: Modifier = Modifier,
    showSectionHeaders: Boolean = true
) {
    val brush = rememberEditorialShimmerBrush()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = BukuKitaSpacing.screenHorizontal, vertical = BukuKitaSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(BukuKitaSpacing.md)
    ) {
        if (showSectionHeaders) {
            Box(
                modifier = Modifier
                    .width(172.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )
        }
        repeat(2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BukuKitaSpacing.sm)
            ) {
                repeat(2) {
                    BookCardSkeleton(
                        brush = brush,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BookCardSkeleton(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(BukuKitaSpacing.cardPadding)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.76f)
                    .aspectRatio(0.67f)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(BukuKitaSpacing.sm))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .width(76.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}

@Composable
fun BookDetailShimmer(modifier: Modifier = Modifier) {
    val brush = rememberEditorialShimmerBrush()
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(156.dp)
                    .aspectRatio(0.67f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
        }
        Column(
            modifier = Modifier.padding(BukuKitaSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(BukuKitaSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(26.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 2) 0.65f else 1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
fun EditorialEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    @DrawableRes illustrationRes: Int? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BukuKitaSpacing.xl, vertical = BukuKitaSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (illustrationRes != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Ivory50,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Image(
                    painter = painterResource(id = illustrationRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .widthIn(max = 220.dp)
                        .aspectRatio(4f / 3f)
                )
            }
            Spacer(modifier = Modifier.height(BukuKitaSpacing.lg))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 320.dp)
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(BukuKitaSpacing.lg))
            BukuKitaOutlinedButton(
                text = actionLabel,
                onClick = onAction,
                shape = CircleShape
            )
        }
    }
}

@Composable
fun EditorialErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.error_load_title)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BukuKitaSpacing.xl, vertical = BukuKitaSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(BukuKitaSpacing.md))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 320.dp)
        )
        Spacer(modifier = Modifier.height(BukuKitaSpacing.lg))
        BukuKitaOutlinedButton(
            text = stringResource(R.string.retry),
            leadingIcon = Icons.Outlined.Refresh,
            onClick = onRetry,
            shape = CircleShape
        )
    }
}
