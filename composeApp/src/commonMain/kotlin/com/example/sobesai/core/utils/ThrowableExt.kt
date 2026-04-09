package com.example.sobesai.core.utils

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException
import org.jetbrains.compose.resources.StringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.error_email_not_confirmed
import sobesai.composeapp.generated.resources.error_email_validation_failed
import sobesai.composeapp.generated.resources.error_generic
import sobesai.composeapp.generated.resources.error_invalid_credentials
import sobesai.composeapp.generated.resources.error_invalid_email
import sobesai.composeapp.generated.resources.error_no_internet
import sobesai.composeapp.generated.resources.error_password_too_short
import sobesai.composeapp.generated.resources.error_rate_limit
import sobesai.composeapp.generated.resources.error_timeout
import sobesai.composeapp.generated.resources.error_user_already_registered
import sobesai.composeapp.generated.resources.interview_error_empty_response
import sobesai.composeapp.generated.resources.interview_error_server
import sobesai.composeapp.generated.resources.interview_error_session
import sobesai.composeapp.generated.resources.interview_error_too_many_requests

private const val HTTP_BAD_REQUEST = 400
private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_FORBIDDEN = 403
private const val HTTP_NOT_FOUND = 404
private const val HTTP_TOO_MANY_REQUESTS = 429
private const val HTTP_SERVER_ERROR_START = 500
private const val HTTP_SERVER_ERROR_END = 599

// Экран авторизации
fun Throwable.toNormalMessage(): StringResource {
    val message = this.message ?: ""
    return when {
        message.contains("valid email", ignoreCase = true) ->
            Res.string.error_invalid_email

        message.contains("Unable to validate email address", ignoreCase = true) ->
            Res.string.error_email_validation_failed

        message.contains("Email not confirmed", ignoreCase = true) ->
            Res.string.error_email_not_confirmed

        // --- Регистрация и логин ---
        message.contains("User already registered", ignoreCase = true) ||
                message.contains("already exists", ignoreCase = true) ->
            Res.string.error_user_already_registered

        message.contains("Invalid login credentials", ignoreCase = true) ||
                message.contains("invalid_grant", ignoreCase = true) ->
            Res.string.error_invalid_credentials

        // --- Пароль ---
        message.contains("at least 6 characters", ignoreCase = true) ||
                message.contains("Password should be at least", ignoreCase = true) ->
            Res.string.error_password_too_short

        // --- Лимиты и сеть ---
        message.contains("rate limit exceeded", ignoreCase = true) || message.contains("429") ->
            Res.string.error_rate_limit

        message.contains("connect", ignoreCase = true) ->
            Res.string.error_no_internet

        message.contains("timeout", ignoreCase = true) ->
            Res.string.error_timeout

        else -> Res.string.error_generic
    }
}

// Экран собеседования
fun Throwable.toInterviewErrorMessage(): StringResource {
    return when (this) {
        is IOException -> toIoInterviewErrorMessage()
        is ClientRequestException -> response.status.value.toInterviewHttpErrorMessage()
        is ServerResponseException,
        is NoTransformationFoundException -> Res.string.interview_error_server

        is IllegalStateException -> toIllegalStateInterviewErrorMessage()
        is com.example.sobesai.domain.model.EmptyAiResponseException -> Res.string.interview_error_empty_response
        else -> Res.string.error_generic
    }
}

private fun Throwable.toIoInterviewErrorMessage(): StringResource {
    return when {
        message?.contains("timeout", ignoreCase = true) == true -> Res.string.error_timeout
        message?.contains("Unable to resolve host", ignoreCase = true) == true -> Res.string.error_no_internet
        message?.contains("connect", ignoreCase = true) == true -> Res.string.error_no_internet
        else -> Res.string.error_no_internet
    }
}

private fun Int.toInterviewHttpErrorMessage(): StringResource {
    return when (this) {
        HTTP_UNAUTHORIZED -> Res.string.interview_error_session
        HTTP_TOO_MANY_REQUESTS -> Res.string.interview_error_too_many_requests
        HTTP_BAD_REQUEST,
        HTTP_FORBIDDEN,
        HTTP_NOT_FOUND,
        in HTTP_SERVER_ERROR_START..HTTP_SERVER_ERROR_END -> Res.string.interview_error_server

        else -> Res.string.interview_error_server
    }
}

private fun IllegalStateException.toIllegalStateInterviewErrorMessage(): StringResource {
    val errorMessage = message.orEmpty()
    return if (
        errorMessage.contains("Google API", ignoreCase = true) ||
        errorMessage.contains("API", ignoreCase = true)
    ) {
        Res.string.interview_error_server
    } else {
        Res.string.error_generic
    }
}
