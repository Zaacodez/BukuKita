package com.example.bukukita.ui

import androidx.compose.ui.graphics.Color
import com.example.bukukita.ui.components.deduplicateCategories
import com.example.bukukita.ui.components.resolveStockLabel
import com.example.bukukita.ui.theme.BukuKitaDarkColorScheme
import com.example.bukukita.ui.theme.BukuKitaLightColorScheme
import com.example.bukukita.utils.formatChatTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class DesignTokenContrastTest {

    private fun relativeLuminance(color: Color): Double {
        fun channel(v: Float): Double {
            val d = v.toDouble()
            return if (d <= 0.03928) d / 12.92 else ((d + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(color.red) +
            0.7152 * channel(color.green) +
            0.0722 * channel(color.blue)
    }

    private fun contrastRatio(a: Color, b: Color): Double {
        val l1 = relativeLuminance(a)
        val l2 = relativeLuminance(b)
        return (max(l1, l2) + 0.05) / (min(l1, l2) + 0.05)
    }

    private fun assertWcagAA(name: String, foreground: Color, background: Color) {
        val ratio = contrastRatio(foreground, background)
        assertTrue(
            "$name contrast ratio was ${"%.2f".format(ratio)}:1 (expected >= 4.5:1)",
            ratio >= 4.5
        )
    }

    @Test
    fun lightColorScheme_allSemanticPairsMeetWcagAA() {
        val c = BukuKitaLightColorScheme
        assertWcagAA("Light onBackground/background", c.onBackground, c.background)
        assertWcagAA("Light onSurface/surface", c.onSurface, c.surface)
        assertWcagAA("Light onSurfaceVariant/background", c.onSurfaceVariant, c.background)
        assertWcagAA("Light onSurfaceVariant/surfaceVariant", c.onSurfaceVariant, c.surfaceVariant)
        assertWcagAA("Light onPrimary/primary", c.onPrimary, c.primary)
        assertWcagAA("Light primary/background", c.primary, c.background)
        assertWcagAA("Light onPrimaryContainer/primaryContainer", c.onPrimaryContainer, c.primaryContainer)
        assertWcagAA("Light onSecondaryContainer/secondaryContainer", c.onSecondaryContainer, c.secondaryContainer)
        assertWcagAA("Light tertiary(price)/surface", c.tertiary, c.surface)
        assertWcagAA("Light tertiary(price)/background", c.tertiary, c.background)
        assertWcagAA("Light onTertiaryContainer/tertiaryContainer", c.onTertiaryContainer, c.tertiaryContainer)
        assertWcagAA("Light error/surface", c.error, c.surface)
    }

    @Test
    fun darkColorScheme_isDistinctAndMeetsWcagAA() {
        val light = BukuKitaLightColorScheme
        val dark = BukuKitaDarkColorScheme

        assertNotEquals("Dark background must differ from light background", light.background, dark.background)
        assertNotEquals("Dark surface must differ from light surface", light.surface, dark.surface)

        assertWcagAA("Dark onBackground/background", dark.onBackground, dark.background)
        assertWcagAA("Dark onSurface/surface", dark.onSurface, dark.surface)
        assertWcagAA("Dark onSurfaceVariant/background", dark.onSurfaceVariant, dark.background)
        assertWcagAA("Dark onSurfaceVariant/surfaceVariant", dark.onSurfaceVariant, dark.surfaceVariant)
        assertWcagAA("Dark onPrimary/primary", dark.onPrimary, dark.primary)
        assertWcagAA("Dark primary/background", dark.primary, dark.background)
        assertWcagAA("Dark onPrimaryContainer/primaryContainer", dark.onPrimaryContainer, dark.primaryContainer)
        assertWcagAA("Dark onSecondaryContainer/secondaryContainer", dark.onSecondaryContainer, dark.secondaryContainer)
        assertWcagAA("Dark tertiary(price)/surface", dark.tertiary, dark.surface)
        assertWcagAA("Dark onTertiaryContainer/tertiaryContainer", dark.onTertiaryContainer, dark.tertiaryContainer)
        assertWcagAA("Dark error/surface", dark.error, dark.surface)
    }

    @Test
    fun deduplicateCategories_neverProducesDuplicateAllChip() {
        val result = deduplicateCategories(
            categories = listOf("Semua", "Semua", "Fiksi", "Sastra", "Fiksi", ""),
            allLabel = "Semua"
        )
        assertEquals(listOf("Semua", "Fiksi", "Sastra"), result)
    }

    @Test
    fun resolveStockLabel_handlesZeroNullAndPositiveStock() {
        assertEquals(false, resolveStockLabel(null).isAvailable)
        assertEquals(false, resolveStockLabel(0).isAvailable)
        assertEquals(true, resolveStockLabel(14).isAvailable)
        assertEquals(14, resolveStockLabel(14).count)
    }

    @Test
    fun formatChatTime_formats24HourClock() {
        val formatted = formatChatTime(0L)
        assertTrue("Expected HH:mm pattern, got $formatted", Regex("^\\d{2}:\\d{2}$").matches(formatted))
    }
}
