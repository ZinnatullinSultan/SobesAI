package com.example.sobesai.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<LoginUiEvent>()
    val events: SharedFlow<LoginUiEvent> = _events.asSharedFlow()

    fun onUsernameChanged(newValue: String) {
        _state.update {
            it.copy(username = newValue, error = null)
        }
        checkButtonActive()
    }

    fun onPasswordChanged(newValue: String) {
        _state.update {
            it.copy(password = newValue, error = null)
        }
        checkButtonActive()
    }

    private fun checkButtonActive() {
        val isActive = _state.value.username.isNotBlank() && _state.value.password.isNotBlank()
        _state.update {
            it.copy(isLoginButtonActive = isActive)
        }
    }

    fun onLoginClicked() {
        val currentUsername = _state.value.username
        val currentPassword = _state.value.password

        viewModelScope.launch {
            val result = loginUseCase(currentUsername, currentPassword)

            result.onSuccess {
                _events.emit(LoginUiEvent.LoginSuccessEvent)
            }
            result.onFailure { exception ->
                _state.update {
                    it.copy(error = exception.message)
                }
            }
        }
    }

    fun onGitHubLoginClicked() {
        viewModelScope.launch {
            _events.emit(LoginUiEvent.StartOAuthEvent("github"))
        }
    }
}
