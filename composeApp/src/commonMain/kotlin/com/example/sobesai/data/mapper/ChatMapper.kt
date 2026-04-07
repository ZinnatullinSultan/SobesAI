package com.example.sobesai.data.mapper

import com.example.sobesai.data.local.database.entity.ChatMessageEntity
import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.model.MessageRole

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        role = MessageRole.valueOf(this.role),
        text = this.text,
        timestamp = this.timestamp
    )
}

fun ChatMessage.toEntity(specId: Long, difficulty: String): ChatMessageEntity {
    return ChatMessageEntity(
        specId = specId,
        difficulty = difficulty,
        role = this.role.name,
        text = this.text,
        timestamp = this.timestamp
    )
}
