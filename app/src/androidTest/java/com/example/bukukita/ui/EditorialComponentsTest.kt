package com.example.bukukita.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.test.platform.app.InstrumentationRegistry
import com.example.bukukita.data.model.Book
import com.example.bukukita.ui.auth.LoginScreenContent
import com.example.bukukita.ui.components.CategoryFilterRow
import com.example.bukukita.ui.components.EditorialBookCard
import com.example.bukukita.ui.components.StockStatusBadge
import com.example.bukukita.ui.profile.SettingsSheet
import com.example.bukukita.ui.theme.AppLanguage
import com.example.bukukita.ui.theme.BukuKitaTheme
import com.example.bukukita.ui.theme.LanguagePreferenceStore
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

    @Test
    fun loginScreen_languageSwitcherUpdatesCopyLive() {
        var selectedLanguage = AppLanguage.INDONESIAN
        composeRule.setContent {
            var language by remember { mutableStateOf(AppLanguage.INDONESIAN) }
            BukuKitaTheme(themeMode = ThemeMode.LIGHT, appLanguage = language) {
                LoginScreenContent(
                    isLoading = false,
                    errorMessage = null,
                    currentLanguage = language,
                    onLanguageChange = {
                        language = it
                        selectedLanguage = it
                    },
                    onClearError = {},
                    onLogin = { _, _ -> },
                    onNavigateToRegister = {}
                )
            }
        }

        composeRule.onNodeWithText("Selamat Datang Kembali").assertIsDisplayed()
        composeRule.onNodeWithText("English").performClick()
        composeRule.waitForIdle()
        assertEquals(AppLanguage.ENGLISH, selectedLanguage)
        composeRule.onNodeWithText("Welcome Back").assertIsDisplayed()
        composeRule.onNodeWithText("Sign In").assertIsDisplayed()

        composeRule.onNodeWithText("Bahasa Indonesia").performClick()
        composeRule.waitForIdle()
        assertEquals(AppLanguage.INDONESIAN, selectedLanguage)
        composeRule.onNodeWithText("Selamat Datang Kembali").assertIsDisplayed()
    }

    @Test
    fun settingsSheet_languageSwitcherUpdatesCopyAndPersistsAcrossSessions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = LanguagePreferenceStore(context)
        val original = store.language.value
        try {
            store.setLanguage(AppLanguage.INDONESIAN)

            composeRule.setContent {
                var language by remember { mutableStateOf(store.language.value) }
                BukuKitaTheme(themeMode = ThemeMode.LIGHT, appLanguage = language) {
                    SettingsSheet(
                        currentThemeMode = ThemeMode.LIGHT,
                        onSelectThemeMode = {},
                        currentLanguage = language,
                        onSelectLanguage = {
                            store.setLanguage(it)
                            language = it
                        },
                        onDismiss = {}
                    )
                }
            }

            composeRule.onNodeWithText("Pengaturan").assertIsDisplayed()
            composeRule.onNodeWithText("Bahasa").assertIsDisplayed()
            composeRule.onNodeWithText("English").performClick()
            composeRule.waitForIdle()

            composeRule.onNodeWithText("Settings").assertIsDisplayed()
            composeRule.onNodeWithText("Language").assertIsDisplayed()

            // Fresh store instance simulates reading persisted choice on next app session
            assertEquals(AppLanguage.ENGLISH, LanguagePreferenceStore(context).language.value)
        } finally {
            store.setLanguage(original)
        }
    }
}
