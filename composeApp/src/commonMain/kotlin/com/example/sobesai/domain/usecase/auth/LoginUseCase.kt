package com.example.sobesai.domain.usecase.auth

import com.example.sobesai.domain.repository.LoginRepository
import com.example.sobesai.domain.repository.SettingsRepository

class LoginUseCase(
    private val loginRepository: LoginRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> {
        return loginRepository.login(username, password)
            .map { token ->
                settingsRepository.saveToken(token)
                settingsRepository.saveDisplayName(username)
            }
    }
}