package com.example.sobesai.data.repository

import com.example.sobesai.data.local.storage.OnboardingStorage
import com.example.sobesai.data.local.storage.ProfileStorage
import com.example.sobesai.data.local.storage.SecureTokenStorage
import com.example.sobesai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val onboardingStorage: OnboardingStorage,
    private val profileStorage: ProfileStorage,
    private val secureTokenStorage: SecureTokenStorage
) : SettingsRepository {
    private val authTokenState = MutableStateFlow(secureTokenStorage.getAccessToken())
    private val refreshTokenState = MutableStateFlow(secureTokenStorage.getRefreshToken())

    override val isFirstLaunch: Flow<Boolean> = onboardingStorage.isFirstLaunch
    override val authToken: Flow<String?> = authTokenState.asStateFlow()
    override val refreshToken: Flow<String?> = refreshTokenState.asStateFlow()
    override val displayName: Flow<String?> = profileStorage.displayName

    override suspend fun setOnboardingCompleted() {
        onboardingStorage.setOnboardingCompleted()
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
        profileStorage.saveDisplayName(name)
    }

    override suspend fun clearData() {
        secureTokenStorage.clearTokens()
        authTokenState.value = null
        refreshTokenState.value = null
        profileStorage.clearDisplayName()
    }
}
