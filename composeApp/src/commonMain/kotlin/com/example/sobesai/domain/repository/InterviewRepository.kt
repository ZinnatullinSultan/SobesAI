package com.example.sobesai.domain.repository

import com.example.sobesai.domain.model.ChatMessage

interface InterviewRepository {
    suspend fun getInterviewHistory(specId: Long, difficulty: String): List<ChatMessage>
    suspend fun clearInterviewHistory(specId: Long, difficulty: String)

    suspend fun sendMessage(
        specId: Long,
        specializationTitle: String,
        difficulty: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Result<ChatMessage>

    suspend fun startInterview(
        specId: Long,
        specializationTitle: String,
        difficulty: String
    ): Result<List<ChatMessage>>
}
