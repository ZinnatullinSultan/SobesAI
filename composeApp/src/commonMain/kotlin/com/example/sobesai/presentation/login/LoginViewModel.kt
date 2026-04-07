package com.example.sobesai.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.core.toNormalMessage
import com.example.sobesai.domain.usecase.auth.LoginUseCase
import com.example.sobesai.domain.usecase.auth.RegisterUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.login_register_success

private const val PROVIDER_GITHUB = "github"
private const val LOG_TAG = "LOGIN_VIEW_MODEL"

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
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

    fun toggleMode() {
        _state.update {
            it.copy(
                isRegisterMode = !it.isRegisterMode,
                error = null,
                successMessage = null
            )
        }
    }

    fun onLoginClicked() {
        val currentUsername = _state.value.username
        val currentPassword = _state.value.password
        val currentName = _state.value.displayName
        val isRegisterMode = _state.value.isRegisterMode

        Napier.d(tag = LOG_TAG) {
            "onLoginClicked: mode=${if (isRegisterMode) "REGISTER" else "LOGIN"}, user=$currentUsername"
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            if (isRegisterMode) {
                Napier.d(tag = LOG_TAG) { "Отправление запроса на регистрацию..." }
                val result = registerUseCase(currentUsername, currentPassword, currentName)
                result.onSuccess {
                    Napier.d(tag = LOG_TAG) { "Регистрация прошла успешно" }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = Res.string.login_register_success
                        )
                    }
                }
                result.onFailure { exception ->
                    Napier.e(
                        tag = LOG_TAG,
                        throwable = exception
                    ) { "Не удалось выполнить регистрацию" }
                    _state.update {
                        it.copy(error = exception.toNormalMessage(), isLoading = false)
                    }
                }
            } else {
                Napier.d(tag = LOG_TAG) { "Отправление запроса на авторизацию..." }
                val result = loginUseCase(currentUsername, currentPassword)
                result.onSuccess {
                    Napier.d(tag = LOG_TAG) { "Авторизация прошла успешно" }
                    _events.send(LoginUiEvent.LoginSuccessEvent)
                }
                result.onFailure { exception ->
                    Napier.e(
                        tag = LOG_TAG,
                        throwable = exception
                    ) { "Не удалось выполнить авторизацию" }
                    _state.update {
                        it.copy(error = exception.toNormalMessage(), isLoading = false)
                    }
                }
            }
        }
    }

    fun onDisplayNameChanged(newValue: String) {
        _state.update { it.copy(displayName = newValue, error = null) }
    }

    fun onGitHubLoginClicked() {
        Napier.d(tag = LOG_TAG) { "onGitHubLoginClicked" }
        viewModelScope.launch {
            _events.send(LoginUiEvent.StartOAuthEvent(PROVIDER_GITHUB))
        }
    }
}
