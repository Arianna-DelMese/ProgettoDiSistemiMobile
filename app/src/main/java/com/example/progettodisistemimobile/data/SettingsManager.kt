package com.example.progettodisistemimobile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    private val themeKey = stringPreferencesKey("theme_mode")
    private val userKey = stringPreferencesKey("current_user")

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[themeKey] ?: "Sistema"
    }

    val currentUser: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[userKey] ?: "MarioRossi"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = mode
        }
    }

    suspend fun setCurrentUser(username: String) {
        context.dataStore.edit { preferences ->
            preferences[userKey] = username
        }
    }
}
