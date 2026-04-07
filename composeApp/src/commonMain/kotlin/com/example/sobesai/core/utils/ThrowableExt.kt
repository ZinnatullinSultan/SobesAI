package com.example.sobesai.core.utils

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
