package com.example.bukukita

import android.app.Application
import com.example.bukukita.data.model.AppDatabase
import com.example.bukukita.data.repository.ChatRepository
import com.example.bukukita.data.repository.AuthRepository
import com.example.bukukita.data.remote.ApiClient

class BukuKitaApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val chatRepository by lazy { ChatRepository(database.chatMessageDao()) }
    val authRepository by lazy { AuthRepository(ApiClient.supabaseClient) }
}