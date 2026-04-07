package com.example.sobesai.presentation.interview

import com.example.sobesai.domain.model.ChatMessage

data class InterviewState(
    val messages: List<ChatMessage> = emptyList(),
    val specializationTitle: String = "",
    val difficultyLevel: String = "",
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val error: String? = null
)

sealed interface InterviewIntent {
    data class Init(val specId: Long, val difficulty: String) : InterviewIntent
    data class SendMessage(val text: String) : InterviewIntent
    object ClearHistory : InterviewIntent
    object Retry : InterviewIntent
    object BackClicked : InterviewIntent
}

sealed interface InterviewEffect {
    object ScrollToBottom : InterviewEffect
    data class ShowError(val message: String) : InterviewEffect
    object NavigateBack : InterviewEffect
}
