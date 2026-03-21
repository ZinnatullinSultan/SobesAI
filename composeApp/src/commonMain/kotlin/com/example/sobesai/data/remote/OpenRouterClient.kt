package com.example.sobesai.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createOpenRouterClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        install(DefaultRequest) {
            url("https://openrouter.ai/api/v1/chat/completions")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(
                "Authorization",
                "Bearer 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"
            )
            header("HTTP-Referer", "https://github.com/ZinnatullinSultan/SobesAI")
            header("X-Title", "SobesAI Interviewer")

        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60000
        }
    }
}