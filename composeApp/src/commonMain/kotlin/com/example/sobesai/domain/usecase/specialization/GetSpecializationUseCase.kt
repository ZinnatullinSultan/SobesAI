package com.example.sobesai.domain.usecase.specialization

import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.repository.SpecializationsRepository

class GetSpecializationUseCase(
    private val repository: SpecializationsRepository
) {
    suspend operator fun invoke(id: Long): Result<Specialization> {
        return repository.getSpecializationById(id)
    }
}