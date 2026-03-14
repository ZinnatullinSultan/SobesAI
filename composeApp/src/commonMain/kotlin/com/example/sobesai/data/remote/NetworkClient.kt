package com.example.sobesai.data.remote

import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

fun createHttpClient(settingsRepository: SettingsRepository): HttpClient {
    val BASE_URL = "https://rrhykitzjowtpbikkjkz.supabase.co/rest/v1/"
    val ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJyaHlraXR6am93dHBiaWtramt6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI3ODc0NTgsImV4cCI6MjA4ODM2MzQ1OH0.RMGHGL4QKsHtmOkLOZxzj_wFhBs-B0bLeUS3rhDiKTU"
    return HttpClient {
        defaultRequest {
            url(BASE_URL)
            header("apikey", ANON_KEY)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = settingsRepository.authToken.first()
                    if (!token.isNullOrBlank()) {
                        BearerTokens(token, "")
                    } else {
                        BearerTokens(ANON_KEY, "")
                    }
                }
                sendWithoutRequest { true }
            }
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
        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    Napier.e(tag = "AUTH_ERROR") { "Сессия истекла (401), сброс токена" }
                    settingsRepository.clearData()
                }
            }
        }
    }
}
