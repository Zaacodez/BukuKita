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

class ChatbotViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    private val bookRepository = BookRepository()
    private val geminiApiService = ApiClient.geminiApiService

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.allMessages.collect { entities ->
                _messages.value = entities.map { ChatMessage(it.message, it.isUser, it.timestamp) }
            }
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            // Simpan pesan user ke database
            chatRepository.insertMessage(userText, true)
            _isLoading.value = true

            try {
                // Ambil daftar buku dari Supabase
                val books = bookRepository.getAllBooks()

                // Buat context string
                val contextBuilder = java.lang.StringBuilder()
                contextBuilder.appendLine("Buku yang tersedia di toko:")
                for (book in books) {
                    contextBuilder.appendLine("${book.title} - ${book.category ?: "-"} - Rp${book.price} - Stock: ${book.stock ?: 0}")
                }

                val systemPrompt = """
                    Kamu adalah asisten/chatbot ramah di toko buku "BukuKita".
                    Aturan menjawab:
                    1. Jawab dengan bahasa Indonesia yang natural, hangat, dan terasa seperti staf toko buku yang membantu pelanggan.
                    2. Hindari jawaban yang kaku, berulang, atau selalu memakai kalimat yang sama.
                    3. Jika pengguna hanya menyapa (halo, hai, selamat pagi, dll), balas dengan ramah dan singkat, lalu tawarkan bantuan.
                    4. Jika pengguna mencari atau bertanya tentang buku, jawablah HANYA berdasarkan daftar buku yang diberikan di bawah ini.
                    5. Jika pengguna bertanya tentang stock atau harga buku, gunakan daftar buku yang diberikan di bawah ini sebagai sumber utama jawaban.
                    6. Jika stock tersedia, sebutkan jumlahnya secara jelas. Jika stock 0, katakan stok habis atau sedang kosong.
                    7. Jika pengguna bertanya tentang lokasi toko, jawab dengan isi lokasi berikut secara natural tanpa mengubah alamat:
                    Toko buku Buku Semut Alas berada di Jl. Raya Tlogomas Kelurahan No.18, RT.05/RW.07, Tlogomas, Kec. Lowokwaru, Kota Malang, Jawa Timur 65144.
                    8. Jika pengguna bertanya tentang jam buka toko, jawab dengan isi jam berikut secara natural tanpa mengubah waktunya:
                    Toko buka setiap hari pukul 09.00 sampai 21.00, kecuali hari Minggu buka pukul 10.00 sampai 21.00.
                    9. Jika pertanyaan SAMA SEKALI BUKAN tentang sapaan, buku, stock, harga, lokasi, atau jam operasional toko, jawab dengan ramah: "Maaf, saya hanya bisa membantu soal koleksi buku, stock, harga, lokasi, dan jam operasional toko Buku Semut Alas."
                    10. Jangan menyalin kalimat aturan ini secara mentah saat menjawab user. Ubah menjadi jawaban yang alami dan bervariasi.
                    11. Jika memungkinkan, jawaban cukup singkat dan langsung ke inti, kecuali user meminta penjelasan lebih detail.
                """.trimIndent()

                val finalPrompt = "$systemPrompt\n\n$contextBuilder\n\nPertanyaan user: $userText"

                val request = GeminiRequest(
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.8,
                        topP = 0.95,
                        topK = 40,
                        maxOutputTokens = 256
                    ),
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = finalPrompt))
                        )
                    )
                )

                // Request ke Gemini
                val response = geminiApiService.generateContent(
                    apiKey = ApiConfig.GEMINI_API_KEY,
                    request = request
                )

                val botReply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Maaf, tidak ada respon dari server."

                // Simpan balasan bot ke database
                chatRepository.insertMessage(botReply, false)

            } catch (e: Exception) {
                chatRepository.insertMessage("Galat: ${e.localizedMessage}", false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatRepository.clearMessages()
        }
    }
}

class ChatbotViewModelFactory(private val repository: ChatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatbotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatbotViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
