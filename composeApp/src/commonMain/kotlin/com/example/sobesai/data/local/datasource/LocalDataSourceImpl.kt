package com.example.sobesai.data.local.datasource

import com.example.sobesai.data.local.database.dao.SpecializationDao
import com.example.sobesai.data.mapper.toDomain
import com.example.sobesai.data.mapper.toEntity
import com.example.sobesai.domain.model.Specialization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataSourceImpl(
    private val specializationDao: SpecializationDao
) : LocalDataSource {
    override suspend fun saveSpecializations(specializations: List<Specialization>) {
        val entities = specializations.map { it.toEntity() }
        specializationDao.insertSpecializations(entities)
    }

    override suspend fun saveSpecialization(specialization: Specialization) {
        specializationDao.insertSpecialization(specialization.toEntity())
    }

    override fun observeAllSpecializations(): Flow<List<Specialization>> {
        return specializationDao.getAllSpecializations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSpecializationById(id: Long): Specialization? {
        return specializationDao.getSpecializationById(id)?.toDomain()
    }

    override suspend fun searchSpecializations(query: String): List<Specialization> {
        return specializationDao.searchSpecializations(query).map { it.toDomain() }
    }

    override suspend fun getSpecializationsPaginated(
        offset: Int,
        limit: Int
    ): List<Specialization> {
        return specializationDao.getSpecializationsPaginated(offset, limit).map { it.toDomain() }
    }

    override suspend fun clearAllSpecializations() {
        specializationDao.deleteAllSpecializations()
    }
}
