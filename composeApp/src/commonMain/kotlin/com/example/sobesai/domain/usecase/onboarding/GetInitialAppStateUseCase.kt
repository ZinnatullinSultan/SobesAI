package com.example.sobesai.domain.usecase.onboarding

import com.example.sobesai.domain.model.AppState
import com.example.sobesai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetInitialAppStateUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AppState> {
        return combine(
            settingsRepository.isFirstLaunch,
            settingsRepository.authToken
        ) { isFirstLaunch, token ->
            when {
                isFirstLaunch -> AppState.OnBoarding
                token == null -> AppState.Login
                else -> AppState.Main
            }
        }
    }
}
