package com.example.bukukita.data.repository

import com.example.bukukita.data.model.Book
import com.example.bukukita.data.remote.ApiClient
import com.example.bukukita.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookRepository {
    private val apiService = ApiClient.supabaseApiService

    suspend fun getAllBooks(): List<Book> {
        return withContext(Dispatchers.IO) {
            apiService.getAllBooks(
                apiKey = ApiConfig.SUPABASE_ANON_KEY,
                authorization = "Bearer ${ApiConfig.SUPABASE_ANON_KEY}"
            )
        }
    }

    suspend fun getBookById(id: String): Book? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getBookById(
                apiKey = ApiConfig.SUPABASE_ANON_KEY,
                authorization = "Bearer ${ApiConfig.SUPABASE_ANON_KEY}",
                idQuery = "eq.$id"
            )
            response.firstOrNull()
        }
    }

    suspend fun searchBooks(query: String): List<Book> {
        return withContext(Dispatchers.IO) {
            apiService.searchBooksTitle(
                apiKey = ApiConfig.SUPABASE_ANON_KEY,
                authorization = "Bearer ${ApiConfig.SUPABASE_ANON_KEY}",
                titleQuery = "ilike.%$query%"
            )
        }
    }

    suspend fun searchBooksGeneral(query: String): List<Book> {
        return withContext(Dispatchers.IO) {
            // Pecah kata kunci untuk mencari setiap kata di judul atau penulis
            // Hal ini meningkatkan recall (menemukan lebih banyak buku potensial)
            val words = query.trim().split("\\s+".toRegex()).filter { it.length > 2 }
            if (words.isEmpty()) {
                // Jika kata terlalu pendek, coba cari secara utuh
                val orQuery = "(title.ilike.%$query%,author.ilike.%$query%)"
                return@withContext apiService.searchBooksGeneral(
                    apiKey = ApiConfig.SUPABASE_ANON_KEY,
                    authorization = "Bearer ${ApiConfig.SUPABASE_ANON_KEY}",
                    orQuery = orQuery
                )
            }

            // Bangun query OR yang sangat inklusif
            val orParts = mutableListOf<String>()
            words.forEach { word ->
                orParts.add("title.ilike.%$word%")
                orParts.add("author.ilike.%$word%")
            }
            
            val orQuery = "(${orParts.joinToString(",")})"

            apiService.searchBooksGeneral(
                apiKey = ApiConfig.SUPABASE_ANON_KEY,
                authorization = "Bearer ${ApiConfig.SUPABASE_ANON_KEY}",
                orQuery = orQuery
            )
        }
    }

    suspend fun getBooksByCategory(category: String): List<Book> {
        return withContext(Dispatchers.IO) {
            apiService.getBooksByCategory(
                apiKey = ApiConfig.SUPABASE_ANON_KEY,
                authorization = "Bearer ${ApiConfig.SUPABASE_ANON_KEY}",
                categoryQuery = "eq.$category"
            )
        }
    }

    suspend fun searchAndFilterBooks(query: String, category: String): List<Book> {
        return withContext(Dispatchers.IO) {
            apiService.searchAndFilterBooks(
                apiKey = ApiConfig.SUPABASE_ANON_KEY,
                authorization = "Bearer ${ApiConfig.SUPABASE_ANON_KEY}",
                titleQuery = "ilike.%$query%",
                categoryQuery = "eq.$category"
            )
        }
    }
}
