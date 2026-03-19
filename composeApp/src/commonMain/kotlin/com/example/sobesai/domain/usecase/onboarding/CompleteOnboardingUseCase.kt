package com.example.sobesai.domain.usecase.onboarding

import com.example.sobesai.domain.repository.SettingsRepository

class CompleteOnboardingUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        settingsRepository.setOnboardingCompleted()
    }
}