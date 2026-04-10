package com.example.sobesai.presentation.interview.ui.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import org.jetbrains.compose.resources.stringResource
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioSession
import platform.Foundation.NSError
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.interview_mic_start
import sobesai.composeapp.generated.resources.interview_mic_stop

private const val BUFFER_SIZE = 1024u

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformVoiceInputButton(
    enabled: Boolean,
    onListeningStarted: () -> Unit,
    onVoiceText: (text: String, isFinal: Boolean) -> Unit,
    onListeningStopped: () -> Unit,
    onError: (VoiceInputError) -> Unit,
    modifier: Modifier
) {
    var isListening by remember { mutableStateOf(false) }
    val controller = remember {
        IosVoiceInputController(
            onListeningStarted = {
                isListening = true
                onListeningStarted()
            },
            onListeningStopped = {
                isListening = false
                onListeningStopped()
            },
            onVoiceText = onVoiceText,
            onError = onError
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.stopListening()
        }
    }

    FilledIconButton(
        onClick = {
            if (isListening) {
                controller.stopListening()
            } else {
                controller.requestPermissionsAndStart(enabled = enabled)
            }
        },
        enabled = enabled,
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
}

@OptIn(ExperimentalForeignApi::class)
private class IosVoiceInputController(
    private val onListeningStarted: () -> Unit,
    private val onListeningStopped: () -> Unit,
    private val onVoiceText: (String, Boolean) -> Unit,
    private val onError: (VoiceInputError) -> Unit
) {
    private val speechRecognizer = SFSpeechRecognizer()
    private val audioEngine = AVAudioEngine()
    private var recognitionTask: SFSpeechRecognitionTask? = null
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? = null

    fun requestPermissionsAndStart(enabled: Boolean) {
        if (!enabled) return
        SFSpeechRecognizer.requestAuthorization { status ->
            if (status != SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized) {
                runOnMain { onError(VoiceInputError.PERMISSION_DENIED) }
            } else {
                requestMicrophonePermissionAndStart()
            }
        }
    }

    fun stopListening() {
        recognitionTask?.cancel()
        recognitionTask = null
        recognitionRequest?.endAudio()
        recognitionRequest = null
        audioEngine.stop()
        audioEngine.inputNode.removeTapOnBus(0u)
        onListeningStopped()
    }

    private fun requestMicrophonePermissionAndStart() {
        AVAudioSession.sharedInstance().requestRecordPermission { granted ->
            runOnMain {
                if (granted) {
                    startListeningInternal()
                } else {
                    onError(VoiceInputError.PERMISSION_DENIED)
                }
            }
        }
    }

    private fun startListeningInternal() {
        val recognizer = speechRecognizer
        if (recognizer == null || !recognizer.available) {
            onError(VoiceInputError.UNAVAILABLE)
            return
        }

        stopListening()
        val request = SFSpeechAudioBufferRecognitionRequest().apply {
            shouldReportPartialResults = true
        }
        recognitionRequest = request
        setupAudioTap()

        if (!startAudioEngine()) {
            stopListening()
            onError(VoiceInputError.UNKNOWN)
            return
        }

        onListeningStarted()
        recognitionTask = recognizer.recognitionTaskWithRequest(request) { result, error ->
            handleRecognitionResult(result, error)
        }
    }

    private fun setupAudioTap() {
        val inputNode = audioEngine.inputNode
        val recordingFormat = inputNode.outputFormatForBus(0u)
        inputNode.removeTapOnBus(0u)
        inputNode.installTapOnBus(0u, BUFFER_SIZE, recordingFormat) { buffer, _ ->
            if (buffer != null) recognitionRequest?.appendAudioPCMBuffer(buffer)
        }
    }

    private fun startAudioEngine(): Boolean {
        audioEngine.prepare()
        return memScoped {
            val startError = alloc<kotlinx.cinterop.ObjCObjectVar<NSError?>>()
            audioEngine.startAndReturnError(startError.ptr)
        }
    }

    private fun handleRecognitionResult(result: Any?, error: NSError?) {
        runOnMain {
            if (result is platform.Speech.SFSpeechRecognitionResult) {
                val text = result.bestTranscription.formattedString
                if (!text.isNullOrBlank()) onVoiceText(text, result.isFinal())
                if (result.isFinal()) stopListening()
            }
            if (error != null) {
                stopListening()
                onError(VoiceInputError.UNKNOWN)
            }
        }
    }
}

private fun runOnMain(action: () -> Unit) {
    dispatch_async(dispatch_get_main_queue()) { action() }
}
