package com.example.sobesai.data.repository

import com.example.sobesai.data.local.datasource.LocalDataSource
import com.example.sobesai.data.mapper.toDomain
import com.example.sobesai.data.remote.api.SpecializationsApi
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.repository.SpecializationsRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.io.IOException

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
        return runCatching {
            val response = api.getSpecializations(query, offset, limit)
            val specializations = response.map { it.toDomain() }
            localDataSource.saveSpecializations(specializations)
            Napier.d(tag = LOG_TAG_SPECIALIZATIONS) { "Специализации сохранены в кэш: ${specializations.size} шт." }
            specializations
        }.recoverCatching { error ->
            if (error is CancellationException) throw error
            Napier.e(
                tag = LOG_TAG_SPECIALIZATIONS,
                throwable = error
            ) { "Ошибка при загрузке специализаций с сервера, пробуем кэш" }
            val cachedData = if (query.isNotEmpty()) {
                localDataSource.searchSpecializations(query)
            } else {
                localDataSource.getSpecializationsPaginated(offset, limit)
            }
            if (cachedData.isNotEmpty()) {
                Napier.d(tag = LOG_TAG_SPECIALIZATIONS) { "Возвращаем ${cachedData.size} специализаций из кэша" }
                cachedData
            } else {
                Napier.e(tag = LOG_TAG_SPECIALIZATIONS) { "Кэш также недоступен" }
                throw error
            }
        }
    }

    override fun observeSpecializations(): Flow<List<Specialization>> {
        return runCatching {
            localDataSource.observeAllSpecializations()
        }.getOrElse { error ->
            Napier.e(
                tag = LOG_TAG_SPECIALIZATIONS,
                throwable = error
            ) { "Ошибка при создании Flow специализаций" }
            flowOf(emptyList())
        }
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
            } catch (e: IOException) {
                Napier.w(tag = LOG_TAG_SPECIALIZATIONS) { "Не удалось обновить кэш после изменения закрепления: $e" }
            }
        }
    }

    override suspend fun getSpecializationById(id: Long): Result<Specialization> {
        return runCatching {
            val response = api.getSpecializationById(id)
            val specialization = response.toDomain()
            localDataSource.saveSpecialization(specialization)
            specialization
        }.recoverCatching { error ->
            if (error is CancellationException) throw error
            Napier.e(
                tag = LOG_TAG_SPECIALIZATIONS,
                throwable = error
            ) { "Ошибка при загрузке специализации $id с сервера, пробуем кэш" }
            localDataSource.getSpecializationById(id)
                ?: throw error
        }.onSuccess {
            Napier.d(tag = LOG_TAG_SPECIALIZATIONS) { "Возвращаем специализацию $id из кэша" }
        }
    }
}
