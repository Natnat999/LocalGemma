package com.gemmachat.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object ThemeManager {
    
    private val THEME_KEY = intPreferencesKey("theme_mode")
    
    const val THEME_SYSTEM = 0
    const val THEME_LIGHT = 1
    const val THEME_DARK = 2
    
    fun init(context: Context) {
        // Apply saved theme on app start
        context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: THEME_SYSTEM
        }.map { theme ->
            applyTheme(theme)
        }
    }
    
    fun applyTheme(theme: Int) {
        val mode = when (theme) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
    
    suspend fun setTheme(context: Context, theme: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
        applyTheme(theme)
    }
    
    fun getThemeFlow(context: Context): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: THEME_SYSTEM
        }
    }
}