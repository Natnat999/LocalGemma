package com.gemmachat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import com.gemmachat.utils.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GemmaChatApplication : Application(), Configuration.Provider {
    
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "gemma_chat_channel"
        const val NOTIFICATION_CHANNEL_NAME = "GemmaChat Notifications"
        
        @Volatile
        private var INSTANCE: GemmaChatApplication? = null
        
        fun getInstance(): GemmaChatApplication {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: throw IllegalStateException("Application not created yet")
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        
        // Initialize theme manager
        ThemeManager.init(this)
        
        // Create notification channel
        createNotificationChannel()
        
        // Initialize WorkManager
        WorkManager.initialize(this, workManagerConfiguration)
        
        // Load native library
        try {
            System.loadLibrary("gemmachat")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for GemmaChat responses"
                enableVibration(true)
                setShowBadge(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}