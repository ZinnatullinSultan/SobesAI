package com.example.sobesai.data.mapper

import com.example.sobesai.data.remote.dto.GeminiContent
import com.example.sobesai.data.remote.dto.GeminiPart
import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.model.MessageRole

fun ChatMessage.toGeminiContent(): GeminiContent {
    return GeminiContent(
        role = if (role == MessageRole.USER) GeminiContent.ROLE_USER else GeminiContent.ROLE_MODEL,
        parts = listOf(GeminiPart(text = text))
    )
}

fun List<ChatMessage>.toGeminiContentList(): List<GeminiContent> {
    return this.map { it.toGeminiContent() }
}
