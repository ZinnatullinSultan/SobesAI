package com.example.sobesai.domain.usecase.specialization

import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.repository.SpecializationsRepository

class GetSpecializationsUseCase(
    private val repository: SpecializationsRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<Specialization>> {
        val offset = page * pageSize
        return repository.getSpecializations(query, offset, pageSize)
    }
}
