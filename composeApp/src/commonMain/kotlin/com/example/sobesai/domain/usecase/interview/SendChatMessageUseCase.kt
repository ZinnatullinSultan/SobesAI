package com.example.sobesai.domain.usecase.interview

import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.repository.InterviewRepository

class SendChatMessageUseCase(private val repository: InterviewRepository) {
    suspend operator fun invoke(
        specId: Long,
        specializationTitle: String,
        difficulty: String,
        history: List<ChatMessage>,
        text: String
    ): Result<ChatMessage> {
        return repository.sendMessage(specId, specializationTitle, difficulty, history, text)
    }
}
