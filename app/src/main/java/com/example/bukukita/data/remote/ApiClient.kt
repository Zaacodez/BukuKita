package com.example.bukukita.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Supabase native Kotlin client (for Auth & direct SDK features)
    val supabaseClient = createSupabaseClient(
        supabaseUrl = ApiConfig.SUPABASE_URL,
        supabaseKey = ApiConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

    // Retrofit client (for custom database endpoints if needed)
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.SUPABASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val supabaseApiService: SupabaseApiService by lazy {
        retrofit.create(SupabaseApiService::class.java)
    }

    private val geminiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geminiApiService: GeminiApiService by lazy {
        geminiRetrofit.create(GeminiApiService::class.java)
    }
}
