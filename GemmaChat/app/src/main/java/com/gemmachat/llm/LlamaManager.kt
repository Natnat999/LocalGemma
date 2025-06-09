package com.gemmachat.llm

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

class LlamaManager(private val context: Context) {
    
    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded: StateFlow<Boolean> = _isModelLoaded.asStateFlow()
    
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    
    private var modelHandle: Long = 0
    
    companion object {
        private const val MODEL_FILENAME = "gemma-1b-it-q4_k_m.gguf"
        private const val MAX_TOKENS = 512
        
        @Volatile
        private var INSTANCE: LlamaManager? = null
        
        fun getInstance(context: Context): LlamaManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LlamaManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    suspend fun loadModel(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (_isModelLoaded.value) {
                return@withContext Result.success(Unit)
            }
            
            // Check if model exists in assets
            val modelPath = File(context.filesDir, MODEL_FILENAME).absolutePath
            
            // Copy model from assets if not exists
            if (!File(modelPath).exists()) {
                copyModelFromAssets()
            }
            
            // Load the model
            modelHandle = LlamaJNI.loadModel(modelPath)
            
            if (modelHandle != 0L) {
                _isModelLoaded.value = true
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to load model"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun generateResponse(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!_isModelLoaded.value) {
                return@withContext Result.failure(Exception("Model not loaded"))
            }
            
            _isGenerating.value = true
            
            val response = LlamaJNI.generateResponse(prompt, MAX_TOKENS)
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isGenerating.value = false
        }
    }
    
    fun resetContext() {
        if (_isModelLoaded.value) {
            LlamaJNI.resetContext()
        }
    }
    
    fun unloadModel() {
        if (_isModelLoaded.value) {
            LlamaJNI.freeModel()
            _isModelLoaded.value = false
            modelHandle = 0
        }
    }
    
    private suspend fun copyModelFromAssets() = withContext(Dispatchers.IO) {
        try {
            context.assets.open("models/$MODEL_FILENAME").use { input ->
                File(context.filesDir, MODEL_FILENAME).outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to copy model from assets: ${e.message}")
        }
    }
}