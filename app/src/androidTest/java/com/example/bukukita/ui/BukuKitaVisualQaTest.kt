package com.example.bukukita.ui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.navigation.NavDestination
import androidx.test.platform.app.InstrumentationRegistry
import com.example.bukukita.data.model.Book
import com.example.bukukita.data.model.UserProfile
import com.example.bukukita.ui.chatbot.ChatbotScreenContent
import com.example.bukukita.ui.components.BukuKitaBottomBar
import com.example.bukukita.ui.detail.BookDetailScreenContent
import com.example.bukukita.ui.home.HomeScreenContent
import com.example.bukukita.ui.profile.EditProfileScreenContent
import com.example.bukukita.ui.profile.ProfileScreenContent
import com.example.bukukita.ui.search.SearchScreenContent
import com.example.bukukita.ui.theme.BukuKitaTheme
import com.example.bukukita.ui.theme.ThemeMode
import com.example.bukukita.viewmodel.BookDetailUiState
import com.example.bukukita.viewmodel.BookUiState
import com.example.bukukita.viewmodel.ChatMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

/**
 * End-to-end visual & behavioral QA suite running on Android device/emulator.
 * Renders every redesigned screen with real Compose components and saves PNG captures
 * for direct visual comparison against design/mockups/.
 */
class BukuKitaVisualQaTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sampleBooks = listOf(
        Book(
            id = "1",
            title = "Bumi Manusia",
            author = "Pramoedya Ananta Toer",
            price = 115000.0,
            description = "Roman sejarah klasik Indonesia berlatar akhir abad ke-19.",
            category = "Sastra",
            coverUrl = null,
            stock = 9,
            createdAt = null
        ),
        Book(
            id = "2",
            title = "Laut Bercerita",
            author = "Leila S. Chudori",
            price = 109000.0,
            description = "Sebuah novel sejarah yang memotret persahabatan, cinta, keluarga, dan rasa kehilangan para aktivis mahasiswa di penghujung era 1990-an. Ditulis dengan prosa yang jernih dan menyentuh, kisah ini mengajak pembaca menyusuri ingatan yang tak pernah padam.",
            category = "Fiksi Sastra",
            coverUrl = null,
            stock = 14,
            createdAt = null
        ),
        Book(
            id = "3",
            title = "Pulang",
            author = "Tere Liye",
            price = 95000.0,
            description = "Kisah perjalanan pulang menembus batas waktu dan janji.",
            category = "Fiksi",
            coverUrl = null,
            stock = 7,
            createdAt = null
        ),
        Book(
            id = "4",
            title = "Filosofi Teras",
            author = "Henry Manampiring",
            price = 98000.0,
            description = "Pengantar filsafat Stoisisme yang praktis untuk ketenangan mental masa kini.",
            category = "Pengembangan Diri",
            coverUrl = null,
            stock = 18,
            createdAt = null
        )
    )

    private val sampleCategories = listOf("Semua", "Fiksi", "Sastra", "Sejarah", "Non-Fiksi")

    private val sampleProfile = UserProfile(
        id = "u1",
        username = "nadiakusuma",
        fullName = "Nadia Kusuma",
        avatarUrl = null
    )

    private fun saveScreenshot(fileName: String, useWindowScreenshot: Boolean = false) {
        composeRule.waitForIdle()
        val bitmap = if (useWindowScreenshot) {
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        } else {
            composeRule.onRoot().captureToImage().asAndroidBitmap()
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = File(context.getExternalFilesDir(null), "qa_shots").apply { mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        assertTrue("Screenshot $fileName must be non-empty", file.exists() && file.length() > 1000)
    }

    @Composable
    private fun TabShell(
        activeRoute: String,
        themeMode: ThemeMode = ThemeMode.LIGHT,
        fontScale: Float? = null,
        content: @Composable () -> Unit
    ) {
        val currentDensity = LocalDensity.current
        val targetDensity = if (fontScale != null) {
            Density(currentDensity.density, fontScale)
        } else {
            currentDensity
        }
        val fakeDest = remember(activeRoute) {
            NavDestination("composable").apply { route = activeRoute }
        }
        CompositionLocalProvider(LocalDensity provides targetDensity) {
            BukuKitaTheme(themeMode = themeMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        BukuKitaBottomBar(
                            currentDestination = fakeDest,
                            onNavigate = {}
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        content()
                    }
                }
            }
        }
    }

    @Test
    fun qa01_homeScreen_lightAndDarkAndLargeFont() {
        composeRule.setContent {
            TabShell(activeRoute = "home", themeMode = ThemeMode.LIGHT) {
                HomeScreenContent(
                    uiState = BookUiState.Success(sampleBooks),
                    selectedCategory = "Semua",
                    categories = sampleCategories,
                    onCategorySelected = {},
                    onRetry = {},
                    onResetFilters = {},
                    onNavigateToDetail = {},
                    onNavigateToSearch = {},
                    onNavigateToChatbot = {}
                )
            }
        }
        composeRule.onNodeWithText("BukuKita").assertIsDisplayed()
        composeRule.onNodeWithText("Rekomendasi Buku").assertIsDisplayed()
        composeRule.onNodeWithText("Semua Buku").assertIsDisplayed()
        saveScreenshot("qa_01_home_light.png")
    }

    @Test
    fun qa02_homeScreen_darkMode() {
        composeRule.setContent {
            TabShell(activeRoute = "home", themeMode = ThemeMode.DARK) {
                HomeScreenContent(
                    uiState = BookUiState.Success(sampleBooks),
                    selectedCategory = "Semua",
                    categories = sampleCategories,
                    onCategorySelected = {},
                    onRetry = {},
                    onResetFilters = {},
                    onNavigateToDetail = {},
                    onNavigateToSearch = {},
                    onNavigateToChatbot = {}
                )
            }
        }
        composeRule.onNodeWithText("BukuKita").assertIsDisplayed()
        saveScreenshot("qa_09_home_dark.png")
    }

    @Test
    fun qa03_homeScreen_largeFontScale130() {
        composeRule.setContent {
            TabShell(activeRoute = "home", themeMode = ThemeMode.LIGHT, fontScale = 1.3f) {
                HomeScreenContent(
                    uiState = BookUiState.Success(sampleBooks),
                    selectedCategory = "Semua",
                    categories = sampleCategories,
                    onCategorySelected = {},
                    onRetry = {},
                    onResetFilters = {},
                    onNavigateToDetail = {},
                    onNavigateToSearch = {},
                    onNavigateToChatbot = {}
                )
            }
        }
        composeRule.onNodeWithText("BukuKita").assertIsDisplayed()
        saveScreenshot("qa_11_home_font130.png")
    }

    @Test
    fun qa04_searchScreen_resultsAndEmptyState() {
        composeRule.setContent {
            TabShell(activeRoute = "search", themeMode = ThemeMode.LIGHT) {
                SearchScreenContent(
                    uiState = BookUiState.Success(sampleBooks),
                    searchQuery = "Laut",
                    categories = listOf("Semua", "Semua", "Fiksi", "Sastra", "Sejarah"),
                    selectedCategory = "Fiksi",
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onCategorySelected = {},
                    onRetry = {},
                    onResetFilters = {},
                    onNavigateBack = {},
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("Cari Buku").assertIsDisplayed()
        composeRule.onAllNodesWithText("Semua").assertCountEquals(1)
        composeRule.onNodeWithText("Menampilkan 4 hasil").assertIsDisplayed()
        saveScreenshot("qa_02_search_light.png")
    }

    @Test
    fun qa05_searchScreen_emptyIllustration() {
        composeRule.setContent {
            TabShell(activeRoute = "search", themeMode = ThemeMode.LIGHT) {
                SearchScreenContent(
                    uiState = BookUiState.Success(emptyList()),
                    searchQuery = "xyz",
                    categories = sampleCategories,
                    selectedCategory = "Semua",
                    onSearchQueryChange = {},
                    onClearSearch = {},
                    onCategorySelected = {},
                    onRetry = {},
                    onResetFilters = {},
                    onNavigateBack = {},
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("Buku tidak ditemukan.").assertIsDisplayed()
        composeRule.onNodeWithText("Reset Pencarian").assertIsDisplayed()
        saveScreenshot("qa_10_search_empty.png")
    }

    @Test
    fun qa06_bookDetailScreen_noCommerceControls() {
        var askedQuestion = ""
        composeRule.setContent {
            BukuKitaTheme(themeMode = ThemeMode.LIGHT) {
                BookDetailScreenContent(
                    uiState = BookDetailUiState.Success(sampleBooks[1]),
                    onNavigateBack = {},
                    onRetry = {},
                    onAskAssistant = { askedQuestion = it }
                )
            }
        }
        composeRule.onNodeWithText("Laut Bercerita").assertIsDisplayed()
        composeRule.onNodeWithText("Tersedia • 14 stok").assertIsDisplayed()
        composeRule.onNodeWithText("Deskripsi").assertIsDisplayed()
        composeRule.onAllNodesWithText("Beli Sekarang").assertCountEquals(0)
        composeRule.onAllNodesWithText("Tambah ke Keranjang").assertCountEquals(0)
        saveScreenshot("qa_03_book_detail_light.png")

        composeRule.onNodeWithText("Tanya Asisten tentang Buku Ini").performClick()
        assertTrue(askedQuestion.contains("Laut Bercerita"))
    }

    @Test
    fun qa07_chatbotScreen_conversationAndWelcome() {
        val now = System.currentTimeMillis()
        val messages = listOf(
            ChatMessage(
                text = "Selamat datang di Toko Buku Semut Alas. Saya Asisten BukuKita. Ada buku atau penulis yang ingin Anda cari dari katalog kami hari ini?",
                isUser = false,
                timestamp = now - 120_000L
            ),
            ChatMessage(
                text = "Halo, apakah buku Laut Bercerita masih tersedia? Berapa harganya?",
                isUser = true,
                timestamp = now - 60_000L
            ),
            ChatMessage(
                text = "Halo! Berikut informasi dari katalog kami:\n\n• Laut Bercerita — Leila S. Chudori\n  Harga: Rp 109.000 | Stok: 14 tersedia\n\nAnda dapat berkunjung langsung ke Toko Buku Semut Alas di Jl. Raya Tlogomas No.18, Malang (09:00 – 21:00).",
                isUser = false,
                timestamp = now
            )
        )
        var cleared = false
        composeRule.setContent {
            TabShell(activeRoute = "chatbot", themeMode = ThemeMode.LIGHT) {
                ChatbotScreenContent(
                    messages = messages,
                    isLoading = false,
                    onSendMessage = {},
                    onClearHistory = { cleared = true },
                    onNavigateBack = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Asisten BukuKita").assertIsDisplayed()
        composeRule.onNodeWithText("Halo, apakah buku Laut Bercerita masih tersedia? Berapa harganya?").assertIsDisplayed()
        saveScreenshot("qa_04_chatbot_light.png")

        composeRule.onNodeWithContentDescription("Hapus riwayat percakapan").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Hapus riwayat percakapan?").assertIsDisplayed()
        composeRule.onNodeWithText("Hapus").performClick()
        assertTrue(cleared)
    }

    @Test
    fun qa08_chatbotScreen_welcomeEmptyState() {
        composeRule.setContent {
            TabShell(activeRoute = "chatbot", themeMode = ThemeMode.LIGHT) {
                ChatbotScreenContent(
                    messages = emptyList(),
                    isLoading = false,
                    onSendMessage = {},
                    onClearHistory = {},
                    onNavigateBack = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Selamat datang di Asisten BukuKita").assertIsDisplayed()
        saveScreenshot("qa_04b_chatbot_welcome.png")
    }

    @Test
    fun qa09_profileScreen_andSettingsSheet() {
        var selectedTheme = ThemeMode.SYSTEM
        composeRule.setContent {
            var mode by remember { mutableStateOf(ThemeMode.LIGHT) }
            TabShell(activeRoute = "profile", themeMode = mode) {
                ProfileScreenContent(
                    profile = sampleProfile,
                    isLoading = false,
                    currentThemeMode = mode,
                    onThemeModeChange = {
                        mode = it
                        selectedTheme = it
                    },
                    onNavigateToEditProfile = {},
                    onLogout = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Nadia Kusuma").assertIsDisplayed()
        composeRule.onNodeWithText("@nadiakusuma").assertIsDisplayed()
        composeRule.onNodeWithText("Ubah Profil").assertIsDisplayed()
        composeRule.onNodeWithText("Pengaturan").assertIsDisplayed()
        composeRule.onNodeWithText("Keluar").assertIsDisplayed()
        saveScreenshot("qa_07_profile_light.png")

        // Open SettingsSheet and verify theme toggle works
        composeRule.onNodeWithText("Pengaturan").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Mode Gelap").assertIsDisplayed()
        saveScreenshot("qa_07b_settings_sheet.png", useWindowScreenshot = true)
        composeRule.onNodeWithText("Mode Gelap").performClick()
        composeRule.waitForIdle()
        assertEquals(ThemeMode.DARK, selectedTheme)
    }

    @Test
    fun qa10_editProfileScreen() {
        composeRule.setContent {
            BukuKitaTheme(themeMode = ThemeMode.LIGHT) {
                EditProfileScreenContent(
                    initialUsername = "nadiakusuma",
                    initialFullName = "Nadia Kusuma",
                    initialEmail = "nadia.kusuma@email.com",
                    avatarUrl = null,
                    isUpdating = false,
                    errorMessage = null,
                    onClearError = {},
                    onSaveChanges = { _, _, _, _ -> },
                    onNavigateBack = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Ubah Profil").assertIsDisplayed()
        composeRule.onNodeWithText("Simpan Perubahan").assertIsDisplayed()
        saveScreenshot("qa_08_edit_profile_light.png")
    }
}
