package com.example.bukukita.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bukukita.data.model.GeminiContent
import com.example.bukukita.data.model.GeminiGenerationConfig
import com.example.bukukita.data.model.GeminiPart
import com.example.bukukita.data.model.GeminiRequest
import com.example.bukukita.data.remote.ApiClient
import com.example.bukukita.data.remote.ApiConfig
import com.example.bukukita.data.repository.BookRepository
import com.example.bukukita.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean, val timestamp: Long = System.currentTimeMillis())

class ChatbotViewModel(
    private val chatRepository: ChatRepository,
    private val authRepository: com.example.bukukita.data.repository.AuthRepository
) : ViewModel() {
    private val bookRepository = BookRepository()
    private val geminiApiService = ApiClient.geminiApiService

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: authRepository.getSavedUserId()
            if (userId != null) {
                chatRepository.getMessagesForUser(userId).collect { entities ->
                    _messages.value = entities.map { ChatMessage(it.message, it.isUser, it.timestamp) }
                }
            } else {
                _messages.value = emptyList()
            }
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isLoading.value) return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val currentUserId = authRepository.getCurrentUser()?.id ?: ""
                chatRepository.insertMessage(currentUserId, userText, true)

                if (!isIndonesianInput(userText)) {
                    chatRepository.insertMessage(
                        currentUserId,
                        "Maaf, saya hanya dapat membantu dalam bahasa Indonesia. Silakan ajukan pertanyaan dalam bahasa Indonesia.",
                        false
                    )
                    return@launch
                }
                // 1. Pembersihan Keyword Pencarian
                // Menghapus sapaan umum agar pencarian lebih fokus
                val greetingRegex = Regex("(?i)\\b(halo|hai|selamat|pagi|siang|sore|malam|assalamualaikum|permisi|p|oi)\\b")
                val searchKeyword = userText.replace(greetingRegex, "").trim()

                // 2. Pencarian Database
                // Menggunakan pencarian general yang mencari di judul dan penulis
                val books = if (searchKeyword.length >= 2) {
                    bookRepository.searchBooksGeneral(searchKeyword).take(20)
                } else {
                    emptyList()
                }

                // 3. Bangun Konteks Database yang Jelas
                val contextBuilder = StringBuilder()
                if (books.isNotEmpty()) {
                    contextBuilder.appendLine("KONTEKS DATABASE (Data buku yang ditemukan):")
                    books.forEach { book ->
                        contextBuilder.appendLine("- Judul: ${book.title} | Penulis: ${book.author} | Harga: Rp${book.price} | Stok: ${book.stock ?: 0}")
                    }
                } else if (searchKeyword.length >= 2) {
                    contextBuilder.appendLine("KONTEKS DATABASE: Tidak ditemukan hasil yang cocok untuk kata kunci '$searchKeyword' di database kami.")
                }

                // 4. Instruksi Sistem Profesional
                val systemInstructions = """
                    Anda adalah asisten layanan pelanggan resmi dari Toko Buku Semut Alas.
                    
                    GAYA KOMUNIKASI:
                    - Gunakan Bahasa Indonesia yang formal, profesional, dan sopan.
                    - Berikan jawaban yang jelas, akurat, dan sangat membantu.
                    
                    PEDOMAN JAWABAN:
                    1. Jika pengguna memberikan sapaan, balaslah dengan salam formal dan tawarkan bantuan terkait katalog buku kami.
                    2. Berikan informasi ketersediaan, harga, dan penulis buku HANYA berdasarkan data yang ada di bagian 'KONTEKS DATABASE' di atas.
                    3. Jika terdapat buku di database yang memiliki kemiripan dengan kata kunci pengguna (misalnya pencarian judul atau penulis), informasikan detail buku tersebut secara akurat.
                    4. Jika buku yang ditanyakan sama sekali tidak ada di database, sampaikan dengan sopan bahwa koleksi tersebut saat ini belum tersedia di katalog kami.
                    5. JANGAN merekomendasikan atau menyebutkan judul buku yang tidak terdaftar dalam database.
                    6. Lokasi Toko: Jl. Raya Tlogomas No.18, Malang. Jam Operasional: 09:00 - 21:00.
                """.trimIndent()

                // 5. Kelola Riwayat Percakapan (6 pesan terakhir)
                val history = _messages.value.takeLast(6).map { 
                    GeminiContent(
                        role = if (it.isUser) "user" else "model",
                        parts = listOf(GeminiPart(text = it.text))
                    )
                }.toMutableList()

                val promptWithContext = "$systemInstructions\n\n$contextBuilder\n\nPertanyaan Pengguna: $userText"
                
                val contentsToSend = mutableListOf<GeminiContent>()
                if (history.size > 1) {
                    contentsToSend.addAll(history.dropLast(1))
                }
                contentsToSend.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = promptWithContext))))

                val request = GeminiRequest(
                    contents = contentsToSend,
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.1, // Temperature rendah untuk kepatuhan data maksimal
                        maxOutputTokens = 500
                    )
                )

                val response = geminiApiService.generateContent(
                    apiKey = ApiConfig.GEMINI_API_KEY,
                    request = request
                )

                val botReply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Mohon maaf, saat ini layanan kami sedang mengalami kendala teknis. Silakan mencoba kembali beberapa saat lagi."

                chatRepository.insertMessage(currentUserId, botReply, false)

            } catch (e: Exception) {
                val fallbackUserId = authRepository.getCurrentUser()?.id ?: ""
                chatRepository.insertMessage(fallbackUserId, "Kesalahan sistem: ${e.localizedMessage}", false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun isIndonesianInput(text: String): Boolean {
        val normalized = text.lowercase().trim()
        if (normalized.isBlank()) return false

        val indonesianMarkers = listOf(
            "halo", "hai", "selamat", "pagi", "siang", "sore", "malam",
            "tolong", "buku", "harga", "stok", "judul", "penulis", "cari",
            "apakah", "berapa", "ada", "saya", "kami", "silakan", "terima",
            "kasih", "mau", "ini", "itu", "katalog", "toko", "beli", "pesan",
            "nama", "sudah", "belum", "bagaimana", "apa"
        )

        val englishMarkers = listOf(
            "hello", "hi", "please", "thanks", "thank you", "can you", "how",
            "what", "where", "when", "why", "who", "which", "the", "and",
            "is", "are", "do", "does", "my", "your", "book", "price", "stock",
            "author", "title", "search"
        )

        val hasIndonesianMarker = indonesianMarkers.any { normalized.contains(it) }
        val hasEnglishMarker = englishMarkers.any { normalized.contains(it) }

        return if (hasEnglishMarker && !hasIndonesianMarker) {
            false
        } else {
            true
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUser()?.id ?: ""
            if (currentUserId.isNotEmpty()) {
                chatRepository.clearMessagesForUser(currentUserId)
            }
        }
    }
}

class ChatbotViewModelFactory(
    private val repository: ChatRepository,
    private val authRepository: com.example.bukukita.data.repository.AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatbotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatbotViewModel(repository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
