package com.example.sobesai.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val specId: Long,
    val difficulty: String,
    val role: String,
    val text: String,
    val timestamp: Long
)
