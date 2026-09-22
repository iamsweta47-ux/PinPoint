package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class UserPreferencesRepository(private val context: Context) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("app_theme_mode")

        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferencesRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val themeMode: Flow<AppThemeMode> = context.dataStore.data.map { preferences ->
        val raw = preferences[THEME_KEY] ?: AppThemeMode.SYSTEM.name
        try {
            AppThemeMode.valueOf(raw)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }
}
