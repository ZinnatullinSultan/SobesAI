package com.example.sobesai.data.local

import com.example.sobesai.domain.model.Specialization
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun saveSpecializations(specializations: List<Specialization>)
    suspend fun saveSpecialization(specialization: Specialization)
    fun observeAllSpecializations(): Flow<List<Specialization>>
    suspend fun getSpecializationById(id: Long): Specialization?
    suspend fun searchSpecializations(query: String): List<Specialization>
    suspend fun getSpecializationsPaginated(offset: Int, limit: Int): List<Specialization>
    suspend fun clearAllSpecializations()
}
