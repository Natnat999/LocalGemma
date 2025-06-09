package com.gemmachat.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val LANGUAGE = stringPreferencesKey("language")
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        
        const val LANGUAGE_FRENCH = "fr"
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_SYSTEM = "system"
    }
    
    val notificationsEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: false
        }
    
    val language: Flow<String> = context.settingsDataStore.data
        .map { preferences ->
            preferences[LANGUAGE] ?: LANGUAGE_SYSTEM
        }
    
    val isFirstLaunch: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[FIRST_LAUNCH] ?: true
        }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    suspend fun setLanguage(language: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }
    
    suspend fun setFirstLaunchComplete() {
        context.settingsDataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = false
        }
    }
}