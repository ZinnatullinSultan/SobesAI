package com.example.sobesai.domain.usecase.auth

import com.example.sobesai.data.local.datasource.LocalDataSource
import com.example.sobesai.domain.repository.SettingsRepository

class LogoutUseCase(
    private val settingsRepository: SettingsRepository,
    private val localDataSource: LocalDataSource
) {
    suspend operator fun invoke() {
        runCatching { localDataSource.clearAllSpecializations() }
        settingsRepository.clearData()
    }
}
