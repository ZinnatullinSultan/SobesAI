package com.example.sobesai.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkClient {
    private const val BASE_URL = "https://rrhykitzjowtpbikkjkz.supabase.co/rest/v1/"
    private const val API_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJyaHlraXR6am93dHBiaWtramt6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI3ODc0NTgsImV4cCI6MjA4ODM2MzQ1OH0.RMGHGL4QKsHtmOkLOZxzj_wFhBs-B0bLeUS3rhDiKTU"
    val httpClient = HttpClient {
        defaultRequest {
            url(BASE_URL)
            header("apikey", API_KEY)
            header("Authorization", "Bearer $API_KEY")
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(tag = "HTTP_CLIENT", message = message)
                }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
    }
}