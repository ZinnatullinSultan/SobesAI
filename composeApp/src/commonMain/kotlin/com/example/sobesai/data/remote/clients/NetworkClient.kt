package com.example.sobesai.data.remote.clients

import com.example.sobesai.core.utils.PATH_AUTH
import com.example.sobesai.core.utils.SUPABASE_URL
import com.example.sobesai.data.remote.model.RefreshTokenRequest
import com.example.sobesai.data.remote.model.RefreshTokenResponse
import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private const val HEADER_API_KEY = "apikey"
private const val REFRESH_TOKEN_PATH = "token?grant_type=refresh_token"
private const val ANON_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6In" +
            "JyaHlraXR6am93dHBiaWtramt6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI3ODc0NTgsImV4cCI6Mj" +
            "A4ODM2MzQ1OH0.RMGHGL4QKsHtmOkLOZxzj_wFhBs-B0bLeUS3rhDiKTU"

private const val TIMEOUT_MILLIS = 15000L
private const val LOG_TAG_HTTP = "HTTP_CLIENT"

fun createHttpClient(settingsRepository: SettingsRepository): HttpClient {
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
                            BearerTokens(ANON_KEY, "")
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
                            client.post("${PATH_AUTH}${REFRESH_TOKEN_PATH}") {
                                markAsRefreshTokenRequest()
                                contentType(ContentType.Application.Json)
                                header(HEADER_API_KEY, ANON_KEY)
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
                    Napier.v(tag = LOG_TAG_HTTP, message = message)
                }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MILLIS
        }
        defaultRequest {
            url(SUPABASE_URL)
            if (!url.pathSegments.contains("")) url.pathSegments
            header(HEADER_API_KEY, ANON_KEY)
        }
    }
}
