package com.example.sobesai.data.remote.api

import com.example.sobesai.core.utils.PATH_REST
import com.example.sobesai.data.remote.dto.SpecializationDto
import com.example.sobesai.data.remote.dto.UpdatePinDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val ENDPOINT_SPECIALIZATIONS = "specializations"
private const val PARAM_SELECT = "select"
private const val PARAM_TITLE = "title"
private const val PARAM_LIMIT = "limit"
private const val PARAM_OFFSET = "offset"
private const val PARAM_ORDER = "order"
private const val PARAM_ID = "id"
private const val VALUE_ALL_FIELDS = "*"
private const val DEFAULT_ORDER = "is_pinned.desc,pin_order.desc,id.asc"

class SpecializationsApi(private val client: HttpClient) {
    private val fullPath = "${PATH_REST}$ENDPOINT_SPECIALIZATIONS"
    suspend fun getSpecializations(
        query: String,
        offset: Int,
        limit: Int
    ): List<SpecializationDto> {
        return client.get(fullPath) {
            parameter(PARAM_SELECT, VALUE_ALL_FIELDS)
            if (query.isNotEmpty()) {
                parameter(PARAM_TITLE, "ilike.%$query%")
            }
            parameter(PARAM_LIMIT, limit)
            parameter(PARAM_OFFSET, offset)
            parameter(PARAM_ORDER, DEFAULT_ORDER)
        }.body()
    }

    suspend fun getSpecializationById(id: Long): SpecializationDto {
        val response: List<SpecializationDto> = client.get(fullPath) {
            parameter(PARAM_SELECT, VALUE_ALL_FIELDS)
            parameter(PARAM_ID, "eq.$id")
        }.body()
        return response.first()
    }

    suspend fun updatePinStatus(id: Long, isPinned: Boolean, pinOrder: Int?) {
        client.patch(fullPath) {
            parameter(PARAM_ID, "eq.$id")
            contentType(ContentType.Application.Json)
            setBody(UpdatePinDto(isPinned, pinOrder))
        }
    }
}
