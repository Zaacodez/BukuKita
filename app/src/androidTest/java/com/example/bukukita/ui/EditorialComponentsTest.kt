package com.example.bukukita.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.example.bukukita.data.model.Book
import com.example.bukukita.ui.components.CategoryFilterRow
import com.example.bukukita.ui.components.EditorialBookCard
import com.example.bukukita.ui.components.StockStatusBadge
import com.example.bukukita.ui.theme.BukuKitaTheme
import com.example.bukukita.ui.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EditorialComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun categoryFilterRow_deduplicatesSemuaAndSelectsCategory() {
        var selected = "Semua"
        composeRule.setContent {
            BukuKitaTheme(themeMode = ThemeMode.LIGHT) {
                CategoryFilterRow(
                    categories = listOf("Semua", "Semua", "Fiksi", "Sastra"),
                    selectedCategory = selected,
                    onCategorySelected = { selected = it }
                )
            }
        }

        composeRule.onAllNodesWithText("Semua").assertCountEquals(1)
        composeRule.onNode(hasText("Semua") and hasClickAction())
            .assertIsSelected()
            .assertHeightIsAtLeast(48.dp)
        composeRule.onNode(hasText("Fiksi") and hasClickAction())
            .assertIsDisplayed()
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assertEquals("Fiksi", selected)
    }

    @Test
    fun editorialBookCard_handlesNullCoverUrlWithoutCrashing() {
        val sample = Book(
            id = "b1",
            title = "Laut Bercerita",
            author = "Leila S. Chudori",
            price = 109000.0,
            description = "Novel sejarah.",
            category = "Fiksi",
            coverUrl = null,
            stock = 14,
            createdAt = null
        )

        composeRule.setContent {
            BukuKitaTheme(themeMode = ThemeMode.DARK) {
                EditorialBookCard(
                    book = sample,
                    showCategory = true,
                    onClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Laut Bercerita").assertIsDisplayed()
        composeRule.onNodeWithText("Leila S. Chudori").assertIsDisplayed()
        composeRule.onNodeWithText("Fiksi").assertIsDisplayed()
    }

    @Test
    fun stockStatusBadge_displaysOutOfStockWhenZero() {
        composeRule.setContent {
            BukuKitaTheme(themeMode = ThemeMode.LIGHT) {
                StockStatusBadge(stock = 0)
            }
        }

        composeRule.onNodeWithText("Stok habis").assertIsDisplayed()
    }
}
