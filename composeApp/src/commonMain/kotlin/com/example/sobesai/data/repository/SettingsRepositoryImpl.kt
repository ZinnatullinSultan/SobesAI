package com.example.sobesai.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.sobesai.data.local.SecureTokenStorage
import com.example.sobesai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
private const val KEY_DISPLAY_NAME = "display_name"

private val PREF_IS_FIRST_LAUNCH = booleanPreferencesKey(KEY_IS_FIRST_LAUNCH)
private val PREF_DISPLAY_NAME = stringPreferencesKey(KEY_DISPLAY_NAME)

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val secureTokenStorage: SecureTokenStorage
) : SettingsRepository {
    private val authTokenState = MutableStateFlow(secureTokenStorage.getAccessToken())
    private val refreshTokenState = MutableStateFlow(secureTokenStorage.getRefreshToken())

    override val isFirstLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PREF_IS_FIRST_LAUNCH] ?: true
    }
    override val authToken: Flow<String?> = authTokenState.asStateFlow()
    override val refreshToken: Flow<String?> = refreshTokenState.asStateFlow()
    override val displayName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PREF_DISPLAY_NAME]
    }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[PREF_IS_FIRST_LAUNCH] = false
        }
    }

    override suspend fun saveToken(token: String) {
        secureTokenStorage.saveAccessToken(token)
        authTokenState.value = token
    }

    override suspend fun saveRefreshToken(token: String) {
        secureTokenStorage.saveRefreshToken(token)
        refreshTokenState.value = token
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        secureTokenStorage.saveAccessToken(accessToken)
        secureTokenStorage.saveRefreshToken(refreshToken)
        authTokenState.value = accessToken
        refreshTokenState.value = refreshToken
    }

    override suspend fun saveDisplayName(name: String) {
        dataStore.edit { preferences ->
            preferences[PREF_DISPLAY_NAME] = name
        }
    }

    override suspend fun clearData() {
        secureTokenStorage.clearTokens()
        authTokenState.value = null
        refreshTokenState.value = null
        dataStore.edit { preferences ->
            preferences.remove(PREF_DISPLAY_NAME)
        }
    }
}
