package com.example.sobesai.presentation.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val error: String? = null
) {
    val isLoginButtonActive: Boolean
        get() = username.isNotBlank() && password.isNotBlank()
}
