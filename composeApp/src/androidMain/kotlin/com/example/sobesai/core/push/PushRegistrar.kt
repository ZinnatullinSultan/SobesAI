package com.example.sobesai.core.push

import com.google.firebase.messaging.FirebaseMessaging
import io.github.aakira.napier.Napier

private const val LOG_TAG_PUSH = "PUSH_REGISTRAR"

class PushRegistrar {
    fun registerPushToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token: String ->
                saveToken(token)
            }
            .addOnFailureListener { error: Exception ->
                Napier.e(tag = LOG_TAG_PUSH, throwable = error) { "Не удалось получить FCM token" }
            }
    }

    @Suppress("unused")
    fun onNewToken(token: String) {
        saveToken(token)
    }

    private fun saveToken(token: String) {
        if (token.isBlank()) return
        Napier.d(tag = LOG_TAG_PUSH) { "FCM token получен" }

        FirebaseMessaging.getInstance().subscribeToTopic("general")
            .addOnSuccessListener {
                Napier.d(tag = LOG_TAG_PUSH) { "Подписка на topic 'general' успешна" }
            }
            .addOnFailureListener { error: Exception ->
                Napier.e(
                    tag = LOG_TAG_PUSH,
                    throwable = error
                ) { "Ошибка подписки на topic 'general'" }
            }
    }
}
