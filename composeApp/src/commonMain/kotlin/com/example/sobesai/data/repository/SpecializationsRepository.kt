package com.example.sobesai.data.repository

import com.example.sobesai.data.remote.NetworkClient
import com.example.sobesai.data.remote.SpecializationDto
import com.example.sobesai.domain.model.Specialization
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class SpecializationsRepository {
    suspend fun getSpecializations(
        query: String,
        offset: Int,
        limit: Int
    ): Result<List<Specialization>> {
        return runCatching {
            val response: List<SpecializationDto> =
                NetworkClient.httpClient
                    .get("specializations") {
                        parameter("select", "*")
                        if (query.isNotEmpty()) {
                            parameter("title", "ilike.%$query%")
                        }
                        parameter("limit", limit)
                        parameter("offset", offset)
                        parameter("order", "id.asc")
                    }
                    .body()

            response.map { dto ->
                Specialization(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description ?: "",
                    isPinned = dto.isPinned
                )
            }
        }.onFailure { error ->
            Napier.e(tag = "REPOSITORY", throwable = error) { "Ошибка при загрузке специализаций" }
        }
    }
}