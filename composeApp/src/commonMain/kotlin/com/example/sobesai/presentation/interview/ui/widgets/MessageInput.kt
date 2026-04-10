package com.example.sobesai.presentation.interview.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.Border
import com.example.sobesai.presentation.theme.SurfaceLight
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.interview_input_placeholder
import sobesai.composeapp.generated.resources.interview_voice_error_network
import sobesai.composeapp.generated.resources.interview_voice_error_no_match
import sobesai.composeapp.generated.resources.interview_voice_error_permission
import sobesai.composeapp.generated.resources.interview_voice_error_unavailable
import sobesai.composeapp.generated.resources.interview_voice_error_unknown
import kotlinx.coroutines.delay

private const val DISABLED_ALPHA = 0.1f
private const val VOICE_ERROR_VISIBILITY_MS = 3000L

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    isSending: Boolean,
    isLoading: Boolean
) {
    var text by remember { mutableStateOf("") }
    var baseTextBeforeVoice by remember { mutableStateOf("") }
    var isVoiceListening by remember { mutableStateOf(false) }
    var voiceError by remember { mutableStateOf<VoiceInputError?>(null) }
    val canSend = !isLoading && !isSending && text.isNotBlank()
    val sendCurrentText = {
        if (canSend) {
            onSendMessage(text)
            text = ""
            voiceError = null
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(voiceError) {
        if (voiceError != null) {
            delay(VOICE_ERROR_VISIBILITY_MS)
            voiceError = null
        }
    }

    Surface(
        tonalElevation = AppDimens.Elevation.Normal,
        color = Color.Transparent,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .padding(AppDimens.Padding.Small)
                .fillMaxWidth()
        ) {
            MessageInputControls(
                text = text,
                isLoading = isLoading,
                isSending = isSending,
                isVoiceListening = isVoiceListening,
                onTextChange = { text = it },
                onVoiceStarted = {
                    baseTextBeforeVoice = text.trim()
                    isVoiceListening = true
                    voiceError = null
                },
                onVoiceText = { voiceText, isFinal ->
                    text = mergeVoiceWithBase(baseTextBeforeVoice, voiceText)
                    if (isFinal) isVoiceListening = false
                },
                onVoiceStopped = { isVoiceListening = false },
                onVoiceError = { error ->
                    isVoiceListening = false
                    voiceError = error
                },
                onSendClick = {
                    sendCurrentText()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )

            if (voiceError != null) {
                Spacer(modifier = Modifier.size(AppDimens.SpacerHeight.ExtraTiny))
                Text(
                    text = stringResource(voiceError.toMessageResource()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun MessageInputControls(
    text: String,
    isLoading: Boolean,
    isSending: Boolean,
    isVoiceListening: Boolean,
    onTextChange: (String) -> Unit,
    onVoiceStarted: () -> Unit,
    onVoiceText: (String, Boolean) -> Unit,
    onVoiceStopped: () -> Unit,
    onVoiceError: (VoiceInputError) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(Res.string.interview_input_placeholder)) },
            enabled = !isLoading && !isVoiceListening,
            shape = RoundedCornerShape(AppDimens.CornerShape.Normal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedBorderColor = Border,
                unfocusedBorderColor = Border,
                cursorColor = Color.White
            ),
            maxLines = 3
        )
        Spacer(modifier = Modifier.size(AppDimens.SpacerHeight.Tiny))
        PlatformVoiceInputButton(
            enabled = !isLoading && !isSending,
            onListeningStarted = onVoiceStarted,
            onVoiceText = onVoiceText,
            onListeningStopped = onVoiceStopped,
            onError = onVoiceError,
            modifier = Modifier.size(AppDimens.IconSize.Huge)
        )
        Spacer(modifier = Modifier.size(AppDimens.SpacerHeight.Tiny))
        FilledIconButton(
            onClick = onSendClick,
            modifier = Modifier.size(AppDimens.IconSize.Huge),
            enabled = !isLoading && !isSending && text.isNotBlank(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = SurfaceLight,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(DISABLED_ALPHA),
                disabledContentColor = Color.Gray
            )
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AppDimens.IconSize.Normal),
                    strokeWidth = AppDimens.Components.BorderStroke,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null
                )
            }
        }
    }
}

private fun mergeVoiceWithBase(baseText: String, voiceText: String): String {
    val normalizedPrefix = baseText.trim()
    return if (normalizedPrefix.isBlank()) voiceText else "$normalizedPrefix $voiceText"
}

private fun VoiceInputError?.toMessageResource() = when (this) {
    VoiceInputError.UNAVAILABLE -> Res.string.interview_voice_error_unavailable
    VoiceInputError.PERMISSION_DENIED -> Res.string.interview_voice_error_permission
    VoiceInputError.NO_MATCH -> Res.string.interview_voice_error_no_match
    VoiceInputError.NETWORK -> Res.string.interview_voice_error_network
    VoiceInputError.UNKNOWN, null -> Res.string.interview_voice_error_unknown
}
