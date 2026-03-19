package com.example.sobesai.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.usecase.onboarding.CompleteOnboardingUseCase
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {
    fun onStartClicked() {
        viewModelScope.launch {
            completeOnboardingUseCase()
        }
    }
}