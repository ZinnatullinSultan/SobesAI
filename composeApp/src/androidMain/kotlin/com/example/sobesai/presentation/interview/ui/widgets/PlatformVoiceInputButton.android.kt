package com.example.sobesai.presentation.interview.ui.widgets

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.interview_mic_start
import sobesai.composeapp.generated.resources.interview_mic_stop

@Composable
actual fun PlatformVoiceInputButton(
    enabled: Boolean,
    onListeningStarted: () -> Unit,
    onVoiceText: (text: String, isFinal: Boolean) -> Unit,
    onListeningStopped: () -> Unit,
    onError: (VoiceInputError) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val isRecognitionAvailable = remember {
        SpeechRecognizer.isRecognitionAvailable(context)
    }

    var isListening by remember { mutableStateOf(false) }
    var wasStoppedManually by remember { mutableStateOf(false) }

    val speechRecognizer = remember(isRecognitionAvailable) {
        if (!isRecognitionAvailable) null else SpeechRecognizer.createSpeechRecognizer(context)
    }

    fun startListening() {
        val recognizer = speechRecognizer ?: return
        wasStoppedManually = false
        recognizer.setRecognitionListener(
            createRecognitionListener(
                onListeningStopped = {
                    isListening = false
                    onListeningStopped()
                },
                onVoiceText = onVoiceText,
                shouldIgnoreClientError = { wasStoppedManually },
                clearIgnoredClientError = { wasStoppedManually = false },
                onError = onError
            )
        )
        isListening = true
        onListeningStarted()
        recognizer.startListening(createRecognitionIntent())
    }

    fun stopListening() {
        wasStoppedManually = true
        speechRecognizer?.stopListening()
        isListening = false
        onListeningStopped()
    }

    val requestAudioPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted && enabled) {
                startListening()
            } else if (!isGranted) {
                onError(VoiceInputError.PERMISSION_DENIED)
            }
        }
    )

    DisposableEffect(speechRecognizer) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    val hasRecordPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    FilledIconButton(
        onClick = {
            handleMicrophoneClick(
                isListening = isListening,
                hasRecordPermission = hasRecordPermission,
                stopListening = ::stopListening,
                startListening = ::startListening,
                requestPermission = {
                    requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
                }
            )
        },
        enabled = enabled && isRecognitionAvailable,
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (isListening) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.secondary
            },
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Icon(
            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = if (isListening) {
                stringResource(Res.string.interview_mic_stop)
            } else {
                stringResource(Res.string.interview_mic_start)
            }
        )
    }

    LaunchedEffect(isRecognitionAvailable) {
        if (!isRecognitionAvailable) {
            onError(VoiceInputError.UNAVAILABLE)
        }
    }
}

private fun handleMicrophoneClick(
    isListening: Boolean,
    hasRecordPermission: Boolean,
    stopListening: () -> Unit,
    startListening: () -> Unit,
    requestPermission: () -> Unit
) {
    when {
        isListening -> stopListening()
        hasRecordPermission -> startListening()
        else -> requestPermission()
    }
}

private fun createRecognitionIntent(): Intent = Intent(
    RecognizerIntent.ACTION_RECOGNIZE_SPEECH
).apply {
    putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
}

private fun createRecognitionListener(
    onListeningStopped: () -> Unit,
    onVoiceText: (String, Boolean) -> Unit,
    shouldIgnoreClientError: () -> Boolean,
    clearIgnoredClientError: () -> Unit,
    onError: (VoiceInputError) -> Unit
): RecognitionListener = object : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) = Unit
    override fun onBeginningOfSpeech() = Unit
    override fun onRmsChanged(rmsdB: Float) = Unit
    override fun onBufferReceived(buffer: ByteArray?) = Unit
    override fun onEvent(eventType: Int, params: Bundle?) = Unit

    override fun onEndOfSpeech() {
        onListeningStopped()
    }

    override fun onError(error: Int) {
        onListeningStopped()
        if (shouldIgnoreClientError() && error == SpeechRecognizer.ERROR_CLIENT) {
            clearIgnoredClientError()
            return
        }
        onError(error.toVoiceInputError())
    }

    override fun onResults(results: Bundle?) {
        emitText(results, isFinal = true, onVoiceText = onVoiceText)
        onListeningStopped()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        emitText(partialResults, isFinal = false, onVoiceText = onVoiceText)
    }
}

private fun emitText(
    results: Bundle?,
    isFinal: Boolean,
    onVoiceText: (String, Boolean) -> Unit
) {
    val text = results
        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        ?.firstOrNull()
        .orEmpty()

    if (text.isNotBlank()) {
        onVoiceText(text, isFinal)
    }
}

private fun Int.toVoiceInputError(): VoiceInputError = when (this) {
    SpeechRecognizer.ERROR_NETWORK,
    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> VoiceInputError.NETWORK
    SpeechRecognizer.ERROR_NO_MATCH -> VoiceInputError.NO_MATCH
    else -> VoiceInputError.UNKNOWN
}
