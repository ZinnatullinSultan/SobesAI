package com.example.sobesai.core.speech

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

enum class VoiceInputError {
    Unavailable,
    PermissionDenied,
    Unknown
}

interface VoiceInputController {
    val isListening: StateFlow<Boolean>
    val isAvailable: Boolean

    fun startListening(
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (VoiceInputError) -> Unit
    )

    fun stopListening()
}

@Composable
expect fun rememberVoiceInputController(): VoiceInputController
