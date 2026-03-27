package com.example.sobesai.core

fun Throwable.toNormalMessage(): String {
    val message = this.message ?: ""
    return when {
        message.contains("valid email", ignoreCase = true) ->
            "Введите корректный адрес электронной почты"

        message.contains("Unable to validate email address", ignoreCase = true) ->
            "Не удалось проверить этот email. Проверьте правильность написания"

        message.contains("Email not confirmed", ignoreCase = true) ->
            "Почта не подтверждена. Проверьте ваш почтовый ящик"

        // --- Регистрация и логин ---
        message.contains("User already registered", ignoreCase = true) ||
                message.contains("already exists", ignoreCase = true) ->
            "Этот email уже занят другим пользователем"

        message.contains("Invalid login credentials", ignoreCase = true) ||
                message.contains("invalid_grant", ignoreCase = true) ->
            "Неверный логин или пароль"

        // --- Пароль ---
        message.contains("at least 6 characters", ignoreCase = true) ||
                message.contains("Password should be at least", ignoreCase = true) ->
            "Пароль слишком короткий (минимум 6 символов)"

        // --- Лимиты и сеть ---
        message.contains("rate limit exceeded", ignoreCase = true) || message.contains("429") ->
            "Слишком много попыток. Попробуйте через 5 минут"

        message.contains("connect", ignoreCase = true) ->
            "Нет подключения к интернету"

        message.contains("timeout", ignoreCase = true) ->
            "Сервер отвечает слишком долго"

        else -> "Произошла ошибка. Попробуйте позже"
    }
}
