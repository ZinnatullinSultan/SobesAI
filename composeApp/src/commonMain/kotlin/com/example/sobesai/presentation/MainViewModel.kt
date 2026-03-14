package com.example.sobesai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    sealed interface AppState {
        object Loading : AppState
        object OnBoarding : AppState
        object Login : AppState
        object Main : AppState
    }

    val appState: StateFlow<AppState> = combine(
        settingsRepository.isFirstLaunch,
        settingsRepository.authToken
    ) { isFirstLaunch, token ->
        when {
            isFirstLaunch -> AppState.OnBoarding
            token == null -> AppState.Login
            else -> AppState.Main
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppState.Loading
    )
}