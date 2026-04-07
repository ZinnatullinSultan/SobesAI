package com.example.sobesai.domain.repository

import com.example.sobesai.domain.model.Specialization
import kotlinx.coroutines.flow.Flow

interface SpecializationsRepository {
    suspend fun getSpecializations(
        query: String,
        offset: Int,
        limit: Int
    ): Result<List<Specialization>>

    fun observeSpecializations(): Flow<List<Specialization>>
    suspend fun updatePinStatus(
        id: Long,
        isPinned: Boolean,
        pinOrder: Int?
    ): Result<Unit>

    suspend fun getSpecializationById(id: Long): Result<Specialization>
}
