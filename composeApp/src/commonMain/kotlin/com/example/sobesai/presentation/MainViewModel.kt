package com.example.sobesai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.usecase.onboarding.GetInitialAppStateUseCase
import com.example.sobesai.navigation.AppRoutes
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

private const val LOG_TAG_MAIN_VM = "MAIN_VIEW_MODEL"
private const val STOP_TIMEOUT_MS = 5000L

class MainViewModel(
    getInitialAppStateUseCase: GetInitialAppStateUseCase
) : ViewModel() {
    sealed interface AppState {
        object Loading : AppState
        object OnBoarding : AppState
        object Login : AppState
        object Main : AppState
    }

    val appState: StateFlow<AppState> = getInitialAppStateUseCase()
        .onEach { state ->
            Napier.d(tag = LOG_TAG_MAIN_VM) { "appState changed to: $state" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = AppState.Loading
        )

    val startDestination: StateFlow<AppRoutes?> = appState.map { state ->
        when (state) {
            is AppState.OnBoarding -> AppRoutes.WelcomeRoute
            is AppState.Login -> AppRoutes.LoginRoute
            is AppState.Main -> AppRoutes.MainRoute
            else -> null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = null
    )
}
