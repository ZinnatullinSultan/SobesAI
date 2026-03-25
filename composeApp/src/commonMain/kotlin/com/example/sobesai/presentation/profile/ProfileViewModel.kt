package com.example.sobesai.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.usecase.auth.GetProfileUseCase
import com.example.sobesai.domain.usecase.auth.LogoutUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val FLOW_STOP_TIMEOUT_MS = 5000L

data class ProfileUiState(
    val displayName: String?
)

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase,
    getProfileUseCase: GetProfileUseCase
) : ViewModel() {
    val uiState: StateFlow<ProfileUiState> = getProfileUseCase()
        .map { displayName ->
            ProfileUiState(displayName = displayName)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS),
            initialValue = ProfileUiState(displayName = null)
        )

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
