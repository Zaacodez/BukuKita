package com.example.bukukita.data.remote

import com.example.bukukita.data.model.GeminiRequest
import com.example.bukukita.data.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.1-flash-lite:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
