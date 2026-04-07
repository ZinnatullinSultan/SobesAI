package com.example.sobesai.data.repository

import com.example.sobesai.data.local.LocalDataSource
import com.example.sobesai.data.remote.SpecializationDto
import com.example.sobesai.domain.model.Specialization
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePinRequest(
    @SerialName("is_pinned") val isPinned: Boolean,
    @SerialName("pin_order") val pinOrder: Int?
)

class SpecializationsRepository(
    private val client: HttpClient,
    private val localDataSource: LocalDataSource
) {

    /**
     * Получает специализации с сервера и кэширует их в БД.
     * При ошибке сети возвращает данные из кэша.
     */
    suspend fun getSpecializations(
        query: String,
        offset: Int,
        limit: Int
    ): Result<List<Specialization>> {
        return try {
            // Сначала пробуем загрузить с сервера
            val response: List<SpecializationDto> =
                client
                    .get("specializations") {
                        parameter("select", "*")
                        if (query.isNotEmpty()) {
                            parameter("title", "ilike.%$query%")
                        }
                        parameter("limit", limit)
                        parameter("offset", offset)
                        parameter("order", "is_pinned.desc,pin_order.desc,id.asc")
                    }
                    .body()

            val specializations = response.map { dto ->
                Specialization(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description ?: "",
                    isPinned = dto.isPinned,
                    pinOrder = dto.pinOrder
                )
            }

            // Сохраняем в кэш
            localDataSource.saveSpecializations(specializations)
            Napier.d(tag = "REPOSITORY") { "Специализации сохранены в кэш: ${specializations.size} шт." }

            Result.success(specializations)
        } catch (error: Exception) {
            Napier.e(tag = "REPOSITORY", throwable = error) { "Ошибка при загрузке специализаций с сервера, пробуем кэш" }

            // Fallback: возвращаем данные из кэша
            try {
                val cachedData = if (query.isNotEmpty()) {
                    localDataSource.searchSpecializations(query)
                } else {
                    localDataSource.getSpecializationsPaginated(offset, limit)
                }

                if (cachedData.isNotEmpty()) {
                    Napier.d(tag = "REPOSITORY") { "Возвращаем ${cachedData.size} специализаций из кэша" }
                    Result.success(cachedData)
                } else {
                    Result.failure(error)
                }
            } catch (cacheError: Exception) {
                Napier.e(tag = "REPOSITORY", throwable = cacheError) { "Кэш также недоступен" }
                Result.failure(error)
            }
        }
    }

    fun observeSpecializations(): Flow<List<Specialization>> {
        return localDataSource.observeAllSpecializations()
    }

    suspend fun updatePinStatus(
        id: Long,
        isPinned: Boolean,
        pinOrder: Int?
    ): Result<Unit> {
        return runCatching {
            client.patch("specializations") {
                parameter("id", "eq.$id")
                contentType(ContentType.Application.Json)
                setBody(UpdatePinRequest(isPinned, pinOrder))
            }
            Unit
        }.onFailure { error ->
            Napier.e(tag = "REPO", throwable = error) { "Не удалось сохранить закрепление" }
        }.onSuccess {
            // Обновляем кэш после успешного обновления на сервере
            try {
                val updated = getSpecializationById(id).getOrNull()
                updated?.let {
                    localDataSource.saveSpecialization(
                        it.copy(isPinned = isPinned, pinOrder = pinOrder)
                    )
                }
            } catch (e: Exception) {
                Napier.w(tag = "REPO") { "Не удалось обновить кэш после изменения закрепления" }
            }
        }
    }

    suspend fun getSpecializationById(id: Long): Result<Specialization> {
        return try {
            val response: List<SpecializationDto> = client
                .get("specializations") {
                    parameter("select", "*")
                    parameter("id", "eq.$id")
                }
                .body()

            val dto = response.first()
            val specialization = Specialization(
                id = dto.id,
                title = dto.title,
                description = dto.description ?: "",
                isPinned = dto.isPinned,
                pinOrder = dto.pinOrder
            )

            localDataSource.saveSpecialization(specialization)

            Result.success(specialization)
        } catch (error: Exception) {
            Napier.e(tag = "REPOSITORY", throwable = error) { "Ошибка при загрузке специализации $id с сервера, пробуем кэш" }

            localDataSource.getSpecializationById(id)?.let {
                Napier.d(tag = "REPOSITORY") { "Возвращаем специализацию $id из кэша" }
                Result.success(it)
            } ?: Result.failure(error)
        }
    }

    suspend fun refreshCache(): Result<Unit> {
        return try {
            val response: List<SpecializationDto> = client
                .get("specializations") {
                    parameter("select", "*")
                    parameter("order", "is_pinned.desc,pin_order.desc,id.asc")
                }
                .body()

            val specializations = response.map { dto ->
                Specialization(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description ?: "",
                    isPinned = dto.isPinned,
                    pinOrder = dto.pinOrder
                )
            }

            localDataSource.clearAllSpecializations()
            localDataSource.saveSpecializations(specializations)
            Napier.d(tag = "REPOSITORY") { "Кэш обновлён: ${specializations.size} специализаций" }

            Result.success(Unit)
        } catch (error: Exception) {
            Napier.e(tag = "REPOSITORY", throwable = error) { "Не удалось обновить кэш" }
            Result.failure(error)
        }
    }
}
