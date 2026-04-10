package com.example.sobesai.presentation.interview.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformVoiceInputButton(
    enabled: Boolean,
    onListeningStarted: () -> Unit,
    onVoiceText: (text: String, isFinal: Boolean) -> Unit,
    onListeningStopped: () -> Unit,
    onError: (VoiceInputError) -> Unit,
    modifier: Modifier = Modifier
)
