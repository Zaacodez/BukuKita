package com.example.bukukita.data.repository

import com.example.bukukita.data.model.ChatMessageDao
import com.example.bukukita.data.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatMessageDao: ChatMessageDao) {

    val allMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()

    suspend fun insertMessage(message: String, isUser: Boolean) {
        val chatEntity = ChatMessageEntity(
            message = message,
            isUser = isUser,
            timestamp = System.currentTimeMillis()
        )
        chatMessageDao.insertMessage(chatEntity)
    }

    suspend fun clearMessages() {
        chatMessageDao.clearMessages()
    }
}
