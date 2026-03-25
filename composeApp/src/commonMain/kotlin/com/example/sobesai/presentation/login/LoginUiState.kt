package com.example.sobesai.presentation.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoginButtonActive: Boolean = false,
    val error: String? = null
)
