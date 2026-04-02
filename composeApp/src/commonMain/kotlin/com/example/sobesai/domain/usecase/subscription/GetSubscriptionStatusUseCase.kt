package com.example.sobesai.domain.usecase.subscription

import com.example.sobesai.domain.model.SubscriptionStatus
import com.example.sobesai.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

class GetSubscriptionStatusUseCase(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<SubscriptionStatus> {
        return repository.subscriptionStatus
    }
}
