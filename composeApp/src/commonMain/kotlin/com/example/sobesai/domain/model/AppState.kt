package com.example.sobesai.domain.model

sealed interface AppState {
    object Loading : AppState
    object OnBoarding : AppState
    object Login : AppState
    object Main : AppState
}
