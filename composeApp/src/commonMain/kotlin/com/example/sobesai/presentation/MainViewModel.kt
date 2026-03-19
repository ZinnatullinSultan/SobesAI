package com.example.sobesai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.usecase.onboarding.GetInitialAppStateUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

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
            Napier.d(tag = "MAIN_VIEW_MODEL") { "appState changed to: $state" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppState.Loading
        )
}