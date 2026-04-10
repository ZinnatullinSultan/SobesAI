package com.example.sobesai.core.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.aakira.napier.Napier
import org.koin.java.KoinJavaComponent.get

private const val LOG_TAG_PUSH = "FCM_SERVICE"

class AppFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Napier.d(tag = LOG_TAG_PUSH) { "Получен новый FCM token" }

        val pushRegistrar: PushRegistrar = get(PushRegistrar::class.java)
        pushRegistrar.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Napier.d(tag = LOG_TAG_PUSH) { "Получено Push-сообщение: ${message.data}" }
    }
}
