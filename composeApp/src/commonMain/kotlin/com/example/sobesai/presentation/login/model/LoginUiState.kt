package com.example.sobesai.presentation.login.model

import org.jetbrains.compose.resources.StringResource

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val displayName: String = "",
    val error: StringResource? = null,
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: StringResource? = null
) {
    val isLoginButtonActive: Boolean
        get() = username.isNotBlank() &&
                password.isNotBlank() &&
                (if (isRegisterMode) displayName.isNotBlank() else true) &&
                !isLoading
}
