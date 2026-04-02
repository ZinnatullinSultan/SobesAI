package com.example.sobesai.domain.usecase.subscription

import com.example.sobesai.domain.repository.SubscriptionRepository

@Suppress("TooGenericExceptionCaught")
class PurchasePremiumUseCase(
    private val repository: SubscriptionRepository
) {
    /**
     * Simulates a premium purchase.
     * In a real app, this would integrate with billing (Google Play, App Store, etc.)
     */
    suspend operator fun invoke(): Result<Unit> {
        return try {
            repository.upgradeToPremium()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
