package com.example.sobesai.data.repository

import com.example.sobesai.data.local.LocalDataSource
import com.example.sobesai.data.mapper.toDomain
import com.example.sobesai.data.remote.dto.SpecializationDto
import com.example.sobesai.data.remote.dto.UpdatePinDto
import com.example.sobesai.domain.model.Specialization
import com.example.sobesai.domain.repository.SpecializationsRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow

private const val LOG_TAG_SPECIALIZATIONS = "SPECIALIZATIONS_REPO"
private const val ENDPOINT_SPECIALIZATIONS = "specializations"
private const val PARAM_SELECT = "select"
private const val PARAM_TITLE = "title"
private const val PARAM_LIMIT = "limit"
private const val PARAM_OFFSET = "offset"
private const val PARAM_ORDER = "order"
private const val PARAM_ID = "id"
private const val VALUE_ALL_FIELDS = "*"
private const val DEFAULT_ORDER = "is_pinned.desc,pin_order.desc,id.asc"

class SpecializationsRepositoryImpl(
    private val client: HttpClient,
    private val localDataSource: LocalDataSource
) : SpecializationsRepository {
    override suspend fun getSpecializations(
        query: String,
        offset: Int,
        limit: Int
    ): Result<List<Specialization>> {
        return try {
            val response: List<SpecializationDto> =
                client
                    .get(ENDPOINT_SPECIALIZATIONS) {
                        parameter(PARAM_SELECT, VALUE_ALL_FIELDS)
                        if (query.isNotEmpty()) {
                            parameter(PARAM_TITLE, "ilike.%$query%")
                        }
                        parameter(PARAM_LIMIT, limit)
                        parameter(PARAM_OFFSET, offset)
                        parameter(PARAM_ORDER, DEFAULT_ORDER)
                    }
                    .body()
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
            client.patch(ENDPOINT_SPECIALIZATIONS) {
                parameter(PARAM_ID, "eq.$id")
                contentType(ContentType.Application.Json)
                setBody(UpdatePinDto(isPinned, pinOrder))
            }
            Unit
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
            val response: List<SpecializationDto> = client
                .get(ENDPOINT_SPECIALIZATIONS) {
                    parameter(PARAM_SELECT, VALUE_ALL_FIELDS)
                    parameter(PARAM_ID, "eq.$id")
                }
                .body()

            val specialization = response.first().toDomain()
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
