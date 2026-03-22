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

const val REQUESTTIMEOUT = 60000L
const val CONNECTTIMEOUT = 15000L
const val SOCKETTIMEOUT = 60000L
fun createGeminiClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            })
        }

        install(DefaultRequest) {
            url("https://api.sobes-api.ru/")

            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = REQUESTTIMEOUT
            connectTimeoutMillis = CONNECTTIMEOUT
            socketTimeoutMillis = SOCKETTIMEOUT
        }
    }
}
