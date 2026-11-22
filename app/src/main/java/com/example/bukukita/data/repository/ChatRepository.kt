package com.example.bukukita.data.repository

import com.example.bukukita.data.model.ChatMessageDao
import com.example.bukukita.data.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatMessageDao: ChatMessageDao) {

    fun getMessagesForUser(userId: String) = chatMessageDao.getMessagesForUser(userId)

    suspend fun insertMessage(userId: String, message: String, isUser: Boolean) {
        val chatEntity = ChatMessageEntity(
            message = message,
            isUser = isUser,
            timestamp = System.currentTimeMillis(),
            userId = userId
        )
        chatMessageDao.insertMessage(chatEntity)
    }

    suspend fun clearMessagesForUser(userId: String) {
        chatMessageDao.clearMessagesForUser(userId)
    }
}
