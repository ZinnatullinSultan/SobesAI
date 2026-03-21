package com.example.sobesai.data.remote

import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object NetworkConstants {
    const val SUPABASE_REST_URL = "https://rrhykitzjowtpbikkjkz.supabase.co/rest/v1/"
    const val SUPABASE_AUTH_URL = "https://rrhykitzjowtpbikkjkz.supabase.co/auth/v1/"
}

@Serializable
private data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
private data class RefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)

fun createHttpClient(settingsRepository: SettingsRepository): HttpClient {
    val anonKey =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJyaHlraXR6am93dHBiaWtramt6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI3ODc0NTgsImV4cCI6MjA4ODM2MzQ1OH0.RMGHGL4QKsHtmOkLOZxzj_wFhBs-B0bLeUS3rhDiKTU"

    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = settingsRepository.authToken.first()
                    val refreshToken = settingsRepository.refreshToken.first()

                    when {
                        !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank() -> {
                            BearerTokens(accessToken, refreshToken)
                        }

                        !accessToken.isNullOrBlank() -> {
                            BearerTokens(accessToken, "")
                        }

                        else -> {
                            BearerTokens(anonKey, "")
                        }
                    }
                }

                refreshTokens {
                    val storedRefreshToken = settingsRepository.refreshToken.first()
                    if (storedRefreshToken.isNullOrBlank()) {
                        settingsRepository.clearData()
                        return@refreshTokens null
                    }

                    runCatching {
                        val refreshResponse: RefreshTokenResponse =
                            client.post("${NetworkConstants.SUPABASE_AUTH_URL}token?grant_type=refresh_token") {
                                markAsRefreshTokenRequest()
                                contentType(ContentType.Application.Json)
                                header("apikey", anonKey)
                                setBody(RefreshTokenRequest(storedRefreshToken))
                            }.body()

                        settingsRepository.saveTokens(
                            accessToken = refreshResponse.accessToken,
                            refreshToken = refreshResponse.refreshToken
                        )

                        BearerTokens(
                            accessToken = refreshResponse.accessToken,
                            refreshToken = refreshResponse.refreshToken
                        )
                    }.getOrElse {
                        settingsRepository.clearData()
                        null
                    }
                }
            }
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

        defaultRequest {
            url(NetworkConstants.SUPABASE_REST_URL)
            header("apikey", anonKey)
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    settingsRepository.clearData()
                }
            }
        }
    }
}
