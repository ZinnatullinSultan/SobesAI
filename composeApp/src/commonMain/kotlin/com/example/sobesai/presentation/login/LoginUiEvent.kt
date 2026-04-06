package com.example.sobesai.presentation.login

sealed interface LoginUiEvent {
    object LoginSuccessEvent : LoginUiEvent
    data class StartOAuthEvent(val provider: String) : LoginUiEvent
}