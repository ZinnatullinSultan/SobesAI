package com.example.sobesai.domain.usecase.subscription

import com.example.sobesai.domain.repository.SubscriptionRepository

class CheckInterviewLimitUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.canStartInterview()
    }
}
