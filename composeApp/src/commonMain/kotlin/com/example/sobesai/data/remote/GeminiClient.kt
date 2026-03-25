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

private const val BASE_URL = "https://api.sobes-api.ru/"
private const val REQUEST_TIMEOUT_MILLIS = 60000L
private const val CONNECT_TIMEOUT_MILLIS = 15000L
private const val SOCKET_TIMEOUT_MILLIS = 60000L
fun createGeminiClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            })
        }
        install(DefaultRequest) {
            url(BASE_URL)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
            socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
        }
    }
}
