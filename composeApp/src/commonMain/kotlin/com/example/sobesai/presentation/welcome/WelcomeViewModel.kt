package com.example.sobesai.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.repository.SettingsRepository
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    fun onStartClicked() {
        viewModelScope.launch {
            repository.setOnboardingCompleted()
        }
    }
}