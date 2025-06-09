package com.gemmachat.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gemmachat.data.model.ChatMessage
import com.gemmachat.data.repository.ChatRepository
import com.gemmachat.llm.LlamaManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = ChatRepository.getInstance(application)
    private val llamaManager = LlamaManager.getInstance(application)
    
    val messages = repository.getCurrentConversationMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val isModelLoaded = llamaManager.isModelLoaded
    val isGenerating = llamaManager.isGenerating
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    init {
        loadModel()
    }
    
    private fun loadModel() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingModel = true) }
            
            llamaManager.loadModel()
                .onSuccess {
                    _uiState.update { it.copy(isLoadingModel = false) }
                    // Add welcome message
                    repository.addMessage(
                        content = "Bonjour ! Je suis Gemma, votre assistant IA local. Comment puis-je vous aider ?",
                        isFromUser = false
                    )
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoadingModel = false,
                            error = error.message
                        )
                    }
                }
        }
    }
    
    fun sendMessage(message: String) {
        if (message.isBlank() || !isModelLoaded.value) return
        
        viewModelScope.launch {
            // Add user message
            repository.addMessage(
                content = message,
                isFromUser = true
            )
            
            // Generate response
            llamaManager.generateResponse(message)
                .onSuccess { response ->
                    repository.addMessage(
                        content = response,
                        isFromUser = false
                    )
                }
                .onFailure { error ->
                    repository.addMessage(
                        content = "Désolé, une erreur s'est produite : ${error.message}",
                        isFromUser = false,
                        isError = true
                    )
                }
        }
    }
    
    fun clearChat() {
        viewModelScope.launch {
            repository.clearCurrentConversation()
            llamaManager.resetContext()
            
            // Add welcome message again
            repository.addMessage(
                content = "Nouvelle conversation démarrée. Comment puis-je vous aider ?",
                isFromUser = false
            )
        }
    }
    
    fun retryLastMessage() {
        viewModelScope.launch {
            messages.value.lastOrNull { it.isFromUser }?.let { lastUserMessage ->
                sendMessage(lastUserMessage.content)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        llamaManager.unloadModel()
    }
}

data class ChatUiState(
    val isLoadingModel: Boolean = false,
    val error: String? = null
)