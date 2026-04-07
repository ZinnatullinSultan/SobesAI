package com.example.sobesai.data.remote.api

import com.example.sobesai.data.remote.dto.GeminiRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

//private const val MODEL_ID = "gemini-3.1-flash-lite-preview"

private const val MODEL_ID = "gemini-2.5-flash-lite"
private const val GENERATE_CONTENT_ENDPOINT = "v1beta/models/$MODEL_ID:generateContent"

class InterviewApi(private val client: HttpClient) {
    suspend fun generateContent(request: GeminiRequest): HttpResponse {
        return client.post(GENERATE_CONTENT_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
