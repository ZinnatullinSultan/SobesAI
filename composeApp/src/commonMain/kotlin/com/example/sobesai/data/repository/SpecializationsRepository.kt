package com.example.sobesai.data.repository

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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePinRequest(
    @SerialName("is_pinned") val isPinned: Boolean,
    @SerialName("pin_order") val pinOrder: Int?
)

class SpecializationsRepository(
    private val client: HttpClient
) {

    suspend fun getSpecializations(
        query: String,
        offset: Int,
        limit: Int
    ): Result<List<Specialization>> {
        return runCatching {
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

            response.map { dto ->
                Specialization(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description ?: "",
                    isPinned = dto.isPinned,
                    pinOrder = dto.pinOrder
                )
            }
        }.onFailure { error ->
            Napier.e(tag = "REPOSITORY", throwable = error) { "Ошибка при загрузке специализаций" }
        }
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
        }
    }

    suspend fun getSpecializationById(id: Long): Result<Specialization> {
        return runCatching {
            val response: List<SpecializationDto> = client
                .get("specializations") {
                    parameter("select", "*")
                    parameter("id", "eq.$id")
                }
                .body()

            val dto = response.first()
            Specialization(
                id = dto.id,
                title = dto.title,
                description = dto.description ?: "",
                isPinned = dto.isPinned,
                pinOrder = dto.pinOrder
            )
        }
    }
}
