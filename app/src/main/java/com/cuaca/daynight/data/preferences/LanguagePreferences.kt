package com.cuaca.daynight.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.languageDataStore by preferencesDataStore(
    name = "language_preferences"
)

enum class AppLanguage {
    INDONESIAN,
    ENGLISH
}

class LanguagePreferences(private val context: Context) {

    companion object {
        private val LANGUAGE_KEY =
            stringPreferencesKey("app_language")
    }

    val language: Flow<AppLanguage> =
        context.languageDataStore.data.map { preferences ->
            when (preferences[LANGUAGE_KEY]) {
                "ENGLISH" -> AppLanguage.ENGLISH
                else -> AppLanguage.INDONESIAN
            }
        }

    suspend fun saveLanguage(language: AppLanguage) {
        context.languageDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.name
        }
    }
}
