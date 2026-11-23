package com.example.bukukita.ui

import com.example.bukukita.ui.theme.AppLanguage
import com.example.bukukita.utils.formatChatDateLabel
import com.example.bukukita.viewmodel.BookViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Locale

class LanguagePreferenceTest {

    @Test
    fun appLanguage_fromStorageMapsKnownAndDefaultsToIndonesian() {
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromStorage("id"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromStorage("en"))
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromStorage(null))
        assertEquals(AppLanguage.INDONESIAN, AppLanguage.fromStorage("fr"))
    }

    @Test
    fun appLanguage_localeMatchesExpectedLanguageTag() {
        assertEquals("id", AppLanguage.INDONESIAN.locale.language)
        assertEquals("en", AppLanguage.ENGLISH.locale.language)
    }

    @Test
    fun normalizeCategorySelection_mapsAllVariantsToSentinel() {
        assertEquals(BookViewModel.ALL_CATEGORY, BookViewModel.normalizeCategorySelection(""))
        assertEquals(BookViewModel.ALL_CATEGORY, BookViewModel.normalizeCategorySelection("Semua"))
        assertEquals(BookViewModel.ALL_CATEGORY, BookViewModel.normalizeCategorySelection("All"))
        assertEquals(BookViewModel.ALL_CATEGORY, BookViewModel.normalizeCategorySelection(" all "))
        assertEquals("Fiksi", BookViewModel.normalizeCategorySelection("Fiksi"))
    }

    @Test
    fun formatChatDateLabel_respectsActiveLocale() {
        val jan15 = Calendar.getInstance().apply {
            set(2025, Calendar.JANUARY, 15, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val englishLabel = formatChatDateLabel(jan15, "Today", Locale.ENGLISH)
        val indonesianLabel = formatChatDateLabel(jan15, "Hari ini", Locale.forLanguageTag("id-ID"))

        assertTrue("Expected January in '$englishLabel'", englishLabel.contains("January"))
        assertTrue("Expected Januari in '$indonesianLabel'", indonesianLabel.contains("Januari"))
    }
}
