package com.example.bukukita.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.bukukita.R
import com.example.bukukita.data.model.Book
import com.example.bukukita.ui.theme.BukuKitaSpacing
import com.example.bukukita.ui.theme.editorialPressScale
import com.example.bukukita.utils.formatRupiah

@Composable
fun EditorialBookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showCategory: Boolean = false
) {
    val formattedPrice = remember(book.price) { formatRupiah(book.price) }
    val interactionSource = remember { MutableInteractionSource() }
    val isLightSurface = MaterialTheme.colorScheme.surface.luminance() > 0.5f

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = if (isLightSurface) 1.dp else 0.dp,
        modifier = modifier
            .editorialPressScale(interactionSource)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(BukuKitaSpacing.cardPadding)
        ) {
            // Curated portrait cover stage matching the editorial mockups
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BukuKitaSpacing.sm, vertical = BukuKitaSpacing.xxs),
                contentAlignment = Alignment.Center
            ) {
                BookCoverArt(
                    coverUrl = book.coverUrl,
                    title = book.title,
                    isLightSurface = isLightSurface,
                    modifier = Modifier
                        .fillMaxWidth(0.84f)
                        .aspectRatio(0.67f)
                )
            }

            Spacer(modifier = Modifier.height(BukuKitaSpacing.sm))

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                minLines = if (showCategory) 1 else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(BukuKitaSpacing.xs))

            if (showCategory && !book.category.isNullOrBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(end = BukuKitaSpacing.xs)
                    )
                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFeatureSettings = "tnum"
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1
                    )
                }
            } else {
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFeatureSettings = "tnum"
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun BookCoverArt(
    coverUrl: String?,
    title: String,
    isLightSurface: Boolean,
    modifier: Modifier = Modifier,
    cornerRadiusDp: Int = 10,
    elevationDp: Int = 4
) {
    val shape = RoundedCornerShape(cornerRadiusDp.dp)
    val shadowMod = if (isLightSurface && elevationDp > 0) {
        Modifier.shadow(elevation = elevationDp.dp, shape = shape, clip = false)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(shadowMod)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (coverUrl.isNullOrBlank()) {
            CoverFallbackIcon()
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverUrl)
                    .crossfade(200)
                    .build(),
                contentDescription = stringResource(R.string.cover_for, title),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(rememberEditorialShimmerBrush())
                    )
                },
                error = { CoverFallbackIcon() }
            )
        }
    }
}

@Composable
private fun CoverFallbackIcon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
            modifier = Modifier.size(30.dp)
        )
    }
}
