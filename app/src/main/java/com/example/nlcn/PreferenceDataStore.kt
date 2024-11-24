package com.example.nlcn

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
/**
 * This property delegate creates a DataStore instance for storing preferences
 * It's accessible throughout the application via the Context
 * "settings" is the name of the preferences file
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceDataStore(private val context: Context) {
    // Keys for accessing language and theme preferences in the DataStore
    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val THEME_KEY = stringPreferencesKey("theme")
    }

    // Exposes the current language preference as a Flow
    val getLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en" // Defaults to english
        }

    // Exposes the current theme preference as a Flow
    val getTheme: Flow<String> = context.dataStore.data  // Add theme getter
        .map { preferences ->
            preferences[THEME_KEY] ?: "dark"  // Default to dark theme
        }

    // Suspending function to save the selected language to DataStore
    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    // Suspending function to save the selected theme to DataStore.
    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}