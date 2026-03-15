package com.example.sobesai.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.sobesai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    private companion object {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
    }

    override val isFirstLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_FIRST_LAUNCH] ?: true
    }
    override val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }
    override val displayName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[DISPLAY_NAME]
    }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }

    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    override suspend fun saveDisplayName(name: String) {
        dataStore.edit { preferences ->
            preferences[DISPLAY_NAME] = name
        }
    }

    override suspend fun clearData() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)
            preferences.remove(DISPLAY_NAME)
        }
    }
}