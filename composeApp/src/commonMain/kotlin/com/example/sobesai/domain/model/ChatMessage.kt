package com.example.sobesai.domain.model

import kotlin.time.Clock

enum class MessageRole {
    USER, MODEL, SYSTEM
}

data class ChatMessage(
    val role: MessageRole,
    val text: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)
