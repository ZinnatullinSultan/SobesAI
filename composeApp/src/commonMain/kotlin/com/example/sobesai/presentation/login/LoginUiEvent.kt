package com.example.sobesai.presentation.login

sealed class LoginUiEvent {
    object LoginSuccessEvent : LoginUiEvent()
}