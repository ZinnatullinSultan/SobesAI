package com.example.sobesai.domain.usecase.interview

import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.repository.InterviewRepository

class StartInterviewUseCase(private val repository: InterviewRepository) {
    suspend operator fun invoke(
        specId: Long,
        specializationTitle: String,
        difficulty: String
    ): Result<List<ChatMessage>> {
        return repository.startInterview(specId, specializationTitle, difficulty)
    }
}
