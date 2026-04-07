package com.example.sobesai.presentation.interview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.model.MessageRole
import com.example.sobesai.domain.repository.InterviewRepository
import com.example.sobesai.domain.usecase.interview.SendChatMessageUseCase
import com.example.sobesai.domain.usecase.interview.StartInterviewUseCase
import com.example.sobesai.domain.usecase.specialization.GetSpecializationUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InterviewViewModel(
    private val startInterviewUseCase: StartInterviewUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val getSpecializationUseCase: GetSpecializationUseCase,
    private val interviewRepository: InterviewRepository
) : ViewModel() {
    private val _state = MutableStateFlow(InterviewState())
    val state = _state.asStateFlow()

    private val _effects = Channel<InterviewEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var currentSpecId: Long? = null
    private var lastUserMessage: String? = null
    private var lastInitParams: Pair<Long, String>? = null

    fun handleIntent(intent: InterviewIntent) {
        when (intent) {
            is InterviewIntent.Init -> initInterview(intent.specId, intent.difficulty)
            is InterviewIntent.SendMessage -> sendMessage(intent.text)
            InterviewIntent.ClearHistory -> clearHistory()
            InterviewIntent.Retry -> retryLastAction()
            InterviewIntent.BackClicked -> {
                viewModelScope.launch { _effects.send(InterviewEffect.NavigateBack) }
            }
        }
    }

    private fun initInterview(specId: Long, difficulty: String) {
        currentSpecId = specId
        lastInitParams = specId to difficulty

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, difficultyLevel = difficulty, error = null) }

            val spec = getSpecializationUseCase(specId).getOrNull()
            val title = spec?.title ?: ""
            _state.update { it.copy(specializationTitle = title) }

            startInterviewUseCase(specId, title, difficulty)
                .onSuccess { messages ->
                    _state.update {
                        it.copy(
                            messages = messages,
                            isLoading = false
                        )
                    }
                    _effects.send(InterviewEffect.ScrollToBottom)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun sendMessage(text: String) {
        val specId = currentSpecId ?: return
        if (text.isBlank() || _state.value.isTyping) return

        _state.update { it.copy(error = null) }

        val userMessage = ChatMessage(MessageRole.USER, text)
        _state.update { it.copy(messages = it.messages + userMessage, isTyping = true) }
        lastUserMessage = text

        viewModelScope.launch {
            _effects.send(InterviewEffect.ScrollToBottom)
            sendChatMessageUseCase(
                specId = specId,
                specializationTitle = _state.value.specializationTitle,
                difficulty = _state.value.difficultyLevel,
                history = _state.value.messages.dropLast(1),
                text = text
            )
                .onSuccess { modelMessage ->
                    _state.update {
                        it.copy(
                            messages = it.messages + modelMessage,
                            isTyping = false
                        )
                    }
                    _effects.send(InterviewEffect.ScrollToBottom)
                    lastUserMessage = null
                }
                .onFailure { e ->
                    _state.update { it.copy(isTyping = false, error = e.message) }
                }
        }
    }

    private fun clearHistory() {
        val specId = currentSpecId ?: return
        val difficulty = _state.value.difficultyLevel
        val title = _state.value.specializationTitle

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, messages = emptyList()) }
            interviewRepository.clearInterviewHistory(specId, difficulty)
            startInterviewUseCase(specId, title, difficulty)
                .onSuccess { messages ->
                    _state.update {
                        it.copy(
                            messages = messages,
                            isLoading = false
                        )
                    }
                    _effects.send(InterviewEffect.ScrollToBottom)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun retryLastAction() {
        val currentLastUserMessage = lastUserMessage
        val currentInitParams = lastInitParams
        when {
            currentLastUserMessage != null -> {
                sendMessage(currentLastUserMessage)
            }

            currentInitParams != null -> {
                initInterview(currentInitParams.first, currentInitParams.second)
            }
        }
    }
}
