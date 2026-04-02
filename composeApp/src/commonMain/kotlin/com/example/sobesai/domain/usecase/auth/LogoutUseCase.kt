package com.example.sobesai.domain.usecase.auth

import com.example.sobesai.data.local.LocalDataSource
import com.example.sobesai.domain.repository.SettingsRepository
import com.example.sobesai.domain.repository.SubscriptionRepository

class LogoutUseCase(
    private val settingsRepository: SettingsRepository,
    private val localDataSource: LocalDataSource,
    private val subscriptionRepository: SubscriptionRepository
) {
    suspend operator fun invoke() {
        runCatching { localDataSource.clearAllSpecializations() }
        runCatching { subscriptionRepository.clearSubscriptionData() }
        settingsRepository.clearData()
    }
}
