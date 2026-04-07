package com.example.sobesai.data.repository

import com.example.sobesai.data.local.LocalDataSource
import com.example.sobesai.data.mapper.toDomain
import com.example.sobesai.data.remote.api.SpecializationsApi
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.repository.SpecializationsRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

private const val LOG_TAG_SPECIALIZATIONS = "SPECIALIZATIONS_REPO"

class SpecializationsRepositoryImpl(
    private val api: SpecializationsApi,
    private val localDataSource: LocalDataSource
) : SpecializationsRepository {
    override suspend fun getSpecializations(
        query: String,
        offset: Int,
        limit: Int
    ): Result<List<Specialization>> {
        return try {
            val response = api.getSpecializations(query, offset, limit)
            val specializations = response.map { it.toDomain() }
            localDataSource.saveSpecializations(specializations)
            Napier.d(tag = LOG_TAG_SPECIALIZATIONS) { "Специализации сохранены в кэш: ${specializations.size} шт." }
            Result.success(specializations)
        } catch (error: Exception) {
            Napier.e(
                tag = LOG_TAG_SPECIALIZATIONS,
                throwable = error
            ) { "Ошибка при загрузке специализаций с сервера, пробуем кэш" }
            try {
                val cachedData = if (query.isNotEmpty()) {
                    localDataSource.searchSpecializations(query)
                } else {
                    localDataSource.getSpecializationsPaginated(offset, limit)
                }
                if (cachedData.isNotEmpty()) {
                    Napier.d(tag = LOG_TAG_SPECIALIZATIONS) { "Возвращаем ${cachedData.size} специализаций из кэша" }
                    Result.success(cachedData)
                } else {
                    Result.failure(error)
                }
            } catch (cacheError: Exception) {
                Napier.e(
                    tag = LOG_TAG_SPECIALIZATIONS,
                    throwable = cacheError
                ) { "Кэш также недоступен" }
                Result.failure(error)
            }
        }
    }

    override fun observeSpecializations(): Flow<List<Specialization>> {
        return localDataSource.observeAllSpecializations()
    }

    override suspend fun updatePinStatus(
        id: Long,
        isPinned: Boolean,
        pinOrder: Int?
    ): Result<Unit> {
        return runCatching {
            api.updatePinStatus(id, isPinned, pinOrder)
        }.onFailure { error ->
            Napier.e(
                tag = LOG_TAG_SPECIALIZATIONS,
                throwable = error
            ) { "Не удалось сохранить закрепление" }
        }.onSuccess {
            try {
                val updated = getSpecializationById(id).getOrNull()
                updated?.let {
                    localDataSource.saveSpecialization(
                        it.copy(isPinned = isPinned, pinOrder = pinOrder)
                    )
                }
            } catch (e: Exception) {
                Napier.w(tag = LOG_TAG_SPECIALIZATIONS) { "Не удалось обновить кэш после изменения закрепления: $e" }
            }
        }
    }

    override suspend fun getSpecializationById(id: Long): Result<Specialization> {
        return try {
            val response = api.getSpecializationById(id)
            val specialization = response.toDomain()
            localDataSource.saveSpecialization(specialization)
            Result.success(specialization)
        } catch (error: Exception) {
            Napier.e(
                tag = LOG_TAG_SPECIALIZATIONS,
                throwable = error
            ) { "Ошибка при загрузке специализации $id с сервера, пробуем кэш" }

            localDataSource.getSpecializationById(id)?.let {
                Napier.d(tag = LOG_TAG_SPECIALIZATIONS) { "Возвращаем специализацию $id из кэша" }
                Result.success(it)
            } ?: Result.failure(error)
        }
    }
}
