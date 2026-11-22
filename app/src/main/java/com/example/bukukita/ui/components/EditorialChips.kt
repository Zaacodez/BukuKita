package com.example.bukukita.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bukukita.R
import com.example.bukukita.ui.theme.BukuKitaMotion
import com.example.bukukita.ui.theme.BukuKitaSpacing

data class StockStatus(
    val isAvailable: Boolean,
    val count: Int
)

fun resolveStockLabel(stock: Int?): StockStatus {
    val safeCount = (stock ?: 0).coerceAtLeast(0)
    return StockStatus(isAvailable = safeCount > 0, count = safeCount)
}

fun deduplicateCategories(
    categories: List<String>,
    allLabel: String
): List<String> {
    val cleaned = LinkedHashSet<String>()
    cleaned.add(allLabel)
    categories.forEach { raw ->
        val trimmed = raw.trim()
        if (trimmed.isNotEmpty() && !trimmed.equals(allLabel, ignoreCase = true)) {
            cleaned.add(trimmed)
        }
    }
    return cleaned.toList()
}

@Composable
fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    allLabel: String = stringResource(R.string.all_categories)
) {
    val uniqueCategories = remember(categories, allLabel) {
        deduplicateCategories(categories, allLabel)
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = BukuKitaSpacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(BukuKitaSpacing.xs)
    ) {
        items(uniqueCategories, key = { it }) { category ->
            val isSelected = if (selectedCategory.isBlank()) {
                category == allLabel
            } else {
                category == selectedCategory
            }
            EditorialCategoryChip(
                label = category,
                selected = isSelected,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun EditorialCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(
            durationMillis = BukuKitaMotion.StateChangeMs,
            easing = BukuKitaMotion.StandardEasing
        ),
        label = "chipContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(
            durationMillis = BukuKitaMotion.StateChangeMs,
            easing = BukuKitaMotion.StandardEasing
        ),
        label = "chipContent"
    )

    val interactionSource = remember { MutableInteractionSource() }

    // Full 48dp minimum touch zone with unified selected + clickable semantics
    Box(
        modifier = modifier
            .heightIn(min = BukuKitaSpacing.touchTargetMin)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) { this.selected = selected },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = containerColor,
            border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .clip(CircleShape)
                .indication(interactionSource, ripple())
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = BukuKitaSpacing.md, vertical = 9.dp)
            )
        }
    }
}

@Composable
fun StockStatusBadge(
    stock: Int?,
    modifier: Modifier = Modifier
) {
    val status = remember(stock) { resolveStockLabel(stock) }
    val bgColor = if (status.isAvailable) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    val fgColor = if (status.isAvailable) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }
    val label = if (status.isAvailable) {
        stringResource(R.string.stock_available, status.count)
    } else {
        stringResource(R.string.stock_empty)
    }

    Surface(
        shape = CircleShape,
        color = bgColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (status.isAvailable) Icons.Outlined.Check else Icons.Outlined.RemoveCircleOutline,
                contentDescription = null,
                tint = fgColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFeatureSettings = "tnum"
                ),
                fontWeight = FontWeight.SemiBold,
                color = fgColor,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
