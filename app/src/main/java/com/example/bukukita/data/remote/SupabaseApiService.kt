package com.example.bukukita.data.remote

import com.example.bukukita.data.model.Book
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SupabaseApiService {
    @GET("rest/v1/books")
    suspend fun getAllBooks(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String
    ): List<Book>

    @GET("rest/v1/books")
    suspend fun getBookById(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("id") idQuery: String
    ): List<Book>

    @GET("rest/v1/books")
    suspend fun searchBooksTitle(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("title") titleQuery: String
    ): List<Book>

    @GET("rest/v1/books")
    suspend fun getBooksByCategory(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("category") categoryQuery: String
    ): List<Book>

    @GET("rest/v1/books")
    suspend fun searchAndFilterBooks(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("title") titleQuery: String,
        @Query("category") categoryQuery: String
    ): List<Book>
}
