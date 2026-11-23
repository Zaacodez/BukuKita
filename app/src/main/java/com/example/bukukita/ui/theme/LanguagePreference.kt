package com.example.bukukita.ui.theme

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AppLanguage(val storageValue: String, val languageTag: String) {
    INDONESIAN("id", "id"),
    ENGLISH("en", "en");

    val locale: Locale
        get() = Locale.forLanguageTag(languageTag)

    companion object {
        fun fromStorage(value: String?): AppLanguage =
            entries.firstOrNull { it.storageValue == value } ?: INDONESIAN
    }
}

class LanguagePreferenceStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _language = MutableStateFlow(
        AppLanguage.fromStorage(prefs.getString(KEY_APP_LANGUAGE, AppLanguage.INDONESIAN.storageValue))
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        prefs.edit { putString(KEY_APP_LANGUAGE, language.storageValue) }
        Locale.setDefault(language.locale)
        _language.value = language
    }

    companion object {
        private const val PREFS_NAME = "bukukita_prefs"
        private const val KEY_APP_LANGUAGE = "app_language"

        fun readSavedLanguage(context: Context): AppLanguage {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return AppLanguage.fromStorage(
                prefs.getString(KEY_APP_LANGUAGE, AppLanguage.INDONESIAN.storageValue)
            )
        }
    }
}

fun Context.withAppLanguage(language: AppLanguage): Context {
    val config = Configuration(resources.configuration).apply {
        setLocales(LocaleList(language.locale))
    }
    return createConfigurationContext(config)
}

@Composable
fun ProvideAppLanguage(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val baseContext = LocalContext.current
    val baseConfig = LocalConfiguration.current

    val localizedConfig = remember(baseConfig, language) {
        Configuration(baseConfig).apply {
            setLocales(LocaleList(language.locale))
        }
    }
    val localizedContext = remember(baseContext, localizedConfig) {
        baseContext.createConfigurationContext(localizedConfig)
    }

    SideEffect {
        if (Locale.getDefault().language != language.locale.language) {
            Locale.setDefault(language.locale)
        }
    }

    CompositionLocalProvider(
        LocalConfiguration provides localizedConfig,
        LocalContext provides localizedContext,
        content = content
    )
}
