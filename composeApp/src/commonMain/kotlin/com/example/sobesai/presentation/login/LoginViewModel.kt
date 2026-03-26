package com.example.sobesai.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.core.toNormalMessage
import com.example.sobesai.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PROVIDER_GITHUB = "github"

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _events = Channel<LoginUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onUsernameChanged(newValue: String) {
        _state.update {
            it.copy(username = newValue, error = null)
        }
    }

    fun onPasswordChanged(newValue: String) {
        _state.update {
            it.copy(password = newValue, error = null)
        }
    }

    fun onLoginClicked() {
        val currentUsername = _state.value.username
        val currentPassword = _state.value.password

        viewModelScope.launch {
            val result = loginUseCase(currentUsername, currentPassword)
            result.onSuccess {
                _events.send(LoginUiEvent.LoginSuccessEvent)
            }
            result.onFailure { exception ->
                _state.update {
                    it.copy(error = exception.toNormalMessage())
                }
            }
        }
    }

    fun onGitHubLoginClicked() {
        viewModelScope.launch {
            _events.send(LoginUiEvent.StartOAuthEvent(PROVIDER_GITHUB))
        }
    }
}
