package com.example.sobesai.domain.usecase.specialization

import com.example.sobesai.domain.model.Specialization

class SortSpecializationsUseCase {
    operator fun invoke(items: List<Specialization>): List<Specialization> {
        return items.sortedWith(
            compareByDescending<Specialization> { it.isPinned }
                .thenByDescending { it.pinOrder ?: 0 }
                .thenBy { it.id }
        )
    }
}