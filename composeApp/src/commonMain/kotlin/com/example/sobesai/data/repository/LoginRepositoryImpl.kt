package com.example.sobesai.data.repository

import com.example.sobesai.data.remote.api.AuthApi
import com.example.sobesai.data.remote.dto.LoginRequest
import com.example.sobesai.data.remote.dto.LoginResponse
import com.example.sobesai.data.remote.dto.RegisterRequest
import com.example.sobesai.data.remote.dto.UserMetadataDto
import com.example.sobesai.domain.model.SupabaseApiException
import com.example.sobesai.domain.repository.LoginRepository
import com.example.sobesai.domain.repository.SettingsRepository
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

private const val LOG_TAG = "LOGIN_REPO"

class LoginRepositoryImpl(
    private val authApi: AuthApi,
    private val settingsRepository: SettingsRepository
) : LoginRepository {
    override suspend fun login(username: String, password: String): Result<String> {
        Napier.d(tag = LOG_TAG) { "Попытка входа в систему для $username" }
        return runCatching {
            val response = authApi.login(
                LoginRequest(
                    email = username,
                    password = password
                )
            )
            if (response.status != HttpStatusCode.OK) {
                val errorBody = response.bodyAsText()
                Napier.e(tag = LOG_TAG) { "Ошибка входа в систему со статусом ${response.status}: $errorBody" }
                throw SupabaseApiException(errorBody, response.status)
            }
            val loginResponse: LoginResponse = response.body()
            Napier.d(tag = LOG_TAG) { "Вход в систему прошел успешно, токены сохранены" }
            val nameToSave = loginResponse.user?.metadata?.displayName
                ?: loginResponse.user?.email
                ?: ""

            settingsRepository.saveTokens(
                accessToken = loginResponse.accessToken,
                refreshToken = loginResponse.refreshToken
            )
            settingsRepository.saveDisplayName(nameToSave)
            loginResponse.accessToken
        }.onFailure {
            if (it !is kotlinx.coroutines.CancellationException) {
                Napier.e(tag = LOG_TAG, throwable = it) { "Login repository ошибка" }
            }
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<Unit> {
        Napier.d(tag = LOG_TAG) { "Попытка регистрации для $email" }
        return runCatching {
            val response = authApi.register(
                RegisterRequest(
                    email = email,
                    password = password,
                    data = UserMetadataDto(displayName = displayName)
                )
            )
            if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.Created) {
                val errorBody = response.bodyAsText()
                Napier.e(tag = LOG_TAG) { "Ошибка регистрации со статусом ${response.status}: $errorBody" }
                throw SupabaseApiException(errorBody, response.status)
            }
            settingsRepository.saveDisplayName(displayName)
            Napier.d(tag = LOG_TAG) { "Запрос на регистрацию успешно отправлен" }
        }.onFailure {
            if (it !is kotlinx.coroutines.CancellationException) {
                Napier.e(tag = LOG_TAG, throwable = it) { "Registration repository ошибка" }
            }
        }
    }
}
