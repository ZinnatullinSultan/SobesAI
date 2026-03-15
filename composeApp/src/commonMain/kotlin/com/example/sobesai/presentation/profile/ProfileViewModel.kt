package com.example.sobesai.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.data.local.LocalDataSource
import com.example.sobesai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val displayName: String?
)

class ProfileViewModel(
    private val settingsRepository: SettingsRepository,
    private val localDataSource: LocalDataSource
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = settingsRepository.displayName
        .map { displayName ->
            ProfileUiState(
                displayName = displayName?.takeIf { it.isNotBlank() }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState(displayName = null)
        )

    fun logout() {
        viewModelScope.launch {
            runCatching { localDataSource.clearAllSpecializations() }
            settingsRepository.clearData()
        }
    }
}
