package com.example.sobesai.presentation.liveinterview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sobesai.core.liveapi.GeminiLiveConfig
import com.example.sobesai.core.liveapi.GeminiLiveController
import com.example.sobesai.core.liveapi.GeminiLiveState
import com.example.sobesai.domain.usecase.specialization.GetSpecializationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LiveInterviewState(
    val specializationTitle: String = "",
    val difficultyLevel: String = "",
    val isSessionActive: Boolean = false,
    val userTranscript: String = "",
    val aiResponse: String = "",
    val error: String? = null,
    val isLoading: Boolean = true
)

sealed class LiveInterviewIntent {
    object StartSession : LiveInterviewIntent()
    object StopSession : LiveInterviewIntent()
    object DismissError : LiveInterviewIntent()
    object Mute : LiveInterviewIntent()
    object Unmute : LiveInterviewIntent()
    object Interrupt : LiveInterviewIntent()
}

class LiveInterviewViewModel(
    private val getSpecializationUseCase: GetSpecializationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LiveInterviewState())
    val state = _state.asStateFlow()

    private var currentSpecId: Long? = null
    private var controller: GeminiLiveController? = null
    private var apiKey: String? = null

    fun init(specId: Long, difficulty: String, liveController: GeminiLiveController, apiKeyValue: String) {
        currentSpecId = specId
        controller = liveController
        apiKey = apiKeyValue
        _state.update { it.copy(difficultyLevel = difficulty) }

        viewModelScope.launch {
            val spec = getSpecializationUseCase(specId).getOrNull()
            _state.update { 
                it.copy(
                    specializationTitle = spec?.title ?: "",
                    isLoading = false
                )
            }
        }

        // Observe controller state
        viewModelScope.launch {
            liveController.state.collect { liveState ->
                _state.update { 
                    it.copy(isSessionActive = liveState != GeminiLiveState.Idle) 
                }
            }
        }

        // Observe user transcript
        viewModelScope.launch {
            liveController.userTranscript.collect { transcript ->
                _state.update { it.copy(userTranscript = transcript) }
            }
        }

        // Observe AI response
        viewModelScope.launch {
            liveController.aiResponse.collect { response ->
                _state.update { it.copy(aiResponse = response) }
            }
        }

        // Observe errors
        viewModelScope.launch {
            liveController.error.collect { error ->
                _state.update { 
                    it.copy(error = error?.name) 
                }
            }
        }
    }

    fun handleIntent(intent: LiveInterviewIntent) {
        when (intent) {
            is LiveInterviewIntent.StartSession -> startSession()
            is LiveInterviewIntent.StopSession -> stopSession()
            is LiveInterviewIntent.DismissError -> dismissError()
            is LiveInterviewIntent.Mute -> controller?.mute()
            is LiveInterviewIntent.Unmute -> controller?.unmute()
            is LiveInterviewIntent.Interrupt -> controller?.interrupt()
        }
    }

    private fun startSession() {
        val liveController = controller ?: return
        val currentApiKey = apiKey ?: return

        viewModelScope.launch {
            val config = GeminiLiveConfig(
                apiKey = currentApiKey,
                systemInstruction = """
                    Ты — опытный интервьюер на собеседовании по специальности "${_state.value.specializationTitle}".
                    Уровень сложности: ${_state.value.difficultyLevel}.
                    
                    Твоя задача:
                    1. Задавай вопросы по теме собеседования
                    2. Оценивай ответы кандидата
                    3. Давай конструктивную обратную связь
                    4. Поддерживай профессиональный и дружелюбный тон
                    5. Адаптируй сложность вопросов в зависимости от ответов
                    
                    Говори на русском языке. Будь краток и конкретен.
                """.trimIndent()
            )

            liveController.startSession(
                config = config,
                onError = { error ->
                    _state.update { it.copy(error = error.name) }
                }
            )
        }
    }

    private fun stopSession() {
        controller?.stopSession()
        _state.update { 
            it.copy(
                userTranscript = "",
                aiResponse = ""
            )
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        controller?.stopSession()
    }
}
