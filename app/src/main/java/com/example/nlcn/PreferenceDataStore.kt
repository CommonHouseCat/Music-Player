package com.example.nlcn

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceDataStore(private val context: Context) {
    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val THEME_KEY = stringPreferencesKey("theme")
    }

    val getLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en"
        }

    val getTheme: Flow<String> = context.dataStore.data  // Add theme getter
        .map { preferences ->
            preferences[THEME_KEY] ?: "dark"  // Default to dark theme
        }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun saveTheme(theme: String) {  // Add theme setter
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}