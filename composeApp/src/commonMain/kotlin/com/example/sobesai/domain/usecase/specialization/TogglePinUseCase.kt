package com.example.sobesai.domain.usecase.specialization

import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.repository.SpecializationsRepository


class TogglePinUseCase(
    private val repository: SpecializationsRepository
) {
    suspend operator fun invoke(
        item: Specialization,
        currentPinOrderCounter: Int
    ): Result<Specialization> {
        val nextPinnedState = !item.isPinned
        val nextPinOrder = if (nextPinnedState) currentPinOrderCounter + 1 else null

        return repository.updatePinStatus(item.id, nextPinnedState, nextPinOrder).map {
            item.copy(isPinned = nextPinnedState, pinOrder = nextPinOrder)
        }
    }
}