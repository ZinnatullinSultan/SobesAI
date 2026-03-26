package com.example.sobesai.core

fun Throwable.toNormalMessage(): String {
    val message = this.message ?: ""
    return when {
        message.contains("401") -> "Ошибка авторизации"
        message.contains("connect", ignoreCase = true) -> "Нет подключения к интернету"
        message.contains("timeout", ignoreCase = true) -> "Сервер отвечает слишком долго"
        else -> "Что-то пошло не так, попробуйте позже"
    }
}
