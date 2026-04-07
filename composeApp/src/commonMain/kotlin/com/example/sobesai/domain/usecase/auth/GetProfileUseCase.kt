package com.example.sobesai.domain.usecase.auth

import com.example.sobesai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetProfileUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<String?> {
        return settingsRepository.displayName.map { displayName ->
            displayName?.takeIf { it.isNotBlank() }
        }
    }
}
