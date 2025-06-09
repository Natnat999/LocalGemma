package com.gemmachat.llm

object LlamaJNI {
    
    init {
        System.loadLibrary("gemmachat")
    }
    
    // Native method declarations
    external fun getVersion(): String
    external fun isLoaded(): Boolean
    external fun loadModel(modelPath: String): Long
    external fun freeModel()
    external fun generateResponse(prompt: String, maxTokens: Int): String
    external fun resetContext()
}