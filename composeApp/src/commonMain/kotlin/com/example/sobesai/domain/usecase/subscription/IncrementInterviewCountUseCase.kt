package com.example.sobesai.domain.usecase.subscription

import com.example.sobesai.domain.repository.SubscriptionRepository

class IncrementInterviewCountUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke() {
        repository.incrementInterviewCount()
    }
}
