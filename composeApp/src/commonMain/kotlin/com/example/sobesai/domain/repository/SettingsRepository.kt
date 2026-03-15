package com.example.sobesai.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isFirstLaunch: Flow<Boolean>
    val authToken: Flow<String?>
    val displayName: Flow<String?>

    suspend fun setOnboardingCompleted()

    suspend fun saveToken(token: String)

    suspend fun saveDisplayName(name: String)

    suspend fun clearData()
}