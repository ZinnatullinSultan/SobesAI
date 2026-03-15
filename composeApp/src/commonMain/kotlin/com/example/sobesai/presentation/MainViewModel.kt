package com.example.sobesai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
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
        Napier.d(tag = "MAIN_VIEW_MODEL") { "combine: isFirstLaunch=$isFirstLaunch, token=${token?.take(20)}..." }
        when {
            isFirstLaunch -> AppState.OnBoarding
            token == null -> AppState.Login
            else -> AppState.Main
        }
    }
        .onEach { state ->
            Napier.d(tag = "MAIN_VIEW_MODEL") { "appState changed to: $state" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppState.Loading
        )
}