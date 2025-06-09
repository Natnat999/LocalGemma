package com.gemmachat.data.repository

import android.content.Context
import com.gemmachat.data.dao.ChatDao
import com.gemmachat.data.database.ChatDatabase
import com.gemmachat.data.model.ChatMessage
import com.gemmachat.data.model.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatRepository(context: Context) {
    
    private val chatDao: ChatDao = ChatDatabase.getDatabase(context).chatDao()
    
    private val _currentConversation = MutableStateFlow(Conversation())
    val currentConversation: StateFlow<Conversation> = _currentConversation.asStateFlow()
    
    fun getMessagesByConversation(conversationId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesByConversation(conversationId)
    }
    
    fun getCurrentConversationMessages(): Flow<List<ChatMessage>> {
        return chatDao.getMessagesByConversation(_currentConversation.value.id)
    }
    
    suspend fun addMessage(content: String, isFromUser: Boolean, isError: Boolean = false) {
        val message = ChatMessage(
            conversationId = _currentConversation.value.id,
            content = content,
            isFromUser = isFromUser,
            isError = isError
        )
        chatDao.insertMessage(message)
    }
    
    suspend fun clearCurrentConversation() {
        chatDao.deleteConversationMessages(_currentConversation.value.id)
    }
    
    suspend fun clearAllHistory() {
        chatDao.deleteAllMessages()
    }
    
    fun startNewConversation() {
        _currentConversation.value = Conversation()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: ChatRepository? = null
        
        fun getInstance(context: Context): ChatRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ChatRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}