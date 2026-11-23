package com.example.bukukita

import android.app.Application
import com.example.bukukita.data.model.AppDatabase
import com.example.bukukita.data.repository.ChatRepository
import com.example.bukukita.data.repository.AuthRepository
import com.example.bukukita.data.remote.ApiClient
import com.example.bukukita.ui.theme.LanguagePreferenceStore

class BukuKitaApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val chatRepository by lazy { ChatRepository(database.chatMessageDao()) }
    val languagePreferenceStore by lazy { LanguagePreferenceStore(this) }
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
    }

    val authRepository by lazy { AuthRepository(ApiClient.supabaseClient, this) }
}