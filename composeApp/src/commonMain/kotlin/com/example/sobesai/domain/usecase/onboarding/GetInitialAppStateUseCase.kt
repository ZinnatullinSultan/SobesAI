package com.example.sobesai.domain.usecase.onboarding

import com.example.sobesai.domain.repository.SettingsRepository
import com.example.sobesai.presentation.MainViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetInitialAppStateUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<MainViewModel.AppState> {
        return combine(
            settingsRepository.isFirstLaunch,
            settingsRepository.authToken
        ) { isFirstLaunch, token ->
            when {
                isFirstLaunch -> MainViewModel.AppState.OnBoarding
                token == null -> MainViewModel.AppState.Login
                else -> MainViewModel.AppState.Main
            }
        }
    }
}