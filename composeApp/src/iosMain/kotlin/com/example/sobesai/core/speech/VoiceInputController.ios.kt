package com.example.sobesai.core.speech

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionResult
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberVoiceInputController(): VoiceInputController {
    return remember { IosVoiceInputController() }
}

@OptIn(ExperimentalForeignApi::class)
private class IosVoiceInputController : VoiceInputController {
    private val _isListening = MutableStateFlow(false)
    override val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val speechRecognizer = SFSpeechRecognizer()
    private val audioEngine = AVAudioEngine()

    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? = null
    private var recognitionTask: SFSpeechRecognitionTask? = null

    private var onPartialResult: ((String) -> Unit)? = null
    private var onFinalResult: ((String) -> Unit)? = null
    private var onError: ((VoiceInputError) -> Unit)? = null

    override val isAvailable: Boolean
        get() = speechRecognizer.isAvailable()

    override fun startListening(
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (VoiceInputError) -> Unit
    ) {
        this.onPartialResult = onPartialResult
        this.onFinalResult = onFinalResult
        this.onError = onError

        stopListening()

        SFSpeechRecognizer.requestAuthorization { status ->
            if (status != SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized) {
                dispatch_async(dispatch_get_main_queue()) {
                    this.onError?.invoke(VoiceInputError.PermissionDenied)
                }
                return@requestAuthorization
            }

            AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                if (!granted) {
                    dispatch_async(dispatch_get_main_queue()) {
                        this.onError?.invoke(VoiceInputError.PermissionDenied)
                    }
                    return@requestRecordPermission
                }

                dispatch_async(dispatch_get_main_queue()) {
                    startRecognitionInternal()
                }
            }
        }
    }

    override fun stopListening() {
        if (_isListening.value) {
            _isListening.value = false
        }

        audioEngine.stop()
        audioEngine.inputNode.removeTapOnBus(0u)

        recognitionRequest?.endAudio()
        recognitionTask?.cancel()

        recognitionRequest = null
        recognitionTask = null
    }

    private fun startRecognitionInternal() {
        val audioSession = AVAudioSession.sharedInstance()
        val categorySet = audioSession.setCategory(
            category = AVAudioSessionCategoryRecord,
            mode = AVAudioSessionModeMeasurement,
            options = 0u,
            error = null
        )
        val modeSet = audioSession.setMode(AVAudioSessionModeMeasurement, null)

        if (!categorySet || !modeSet) {
            onError?.invoke(VoiceInputError.Unknown)
            return
        }

        val request = SFSpeechAudioBufferRecognitionRequest().apply {
            shouldReportPartialResults = true
        }
        recognitionRequest = request

        recognitionTask = speechRecognizer.recognitionTaskWithRequest(
            request = request,
            resultHandler = { result: SFSpeechRecognitionResult?, error ->
                handleRecognitionResult(result, error != null)
            }
        )

        val inputNode = audioEngine.inputNode
        inputNode.removeTapOnBus(0u)
        val format = inputNode.outputFormatForBus(0u)
        inputNode.installTapOnBus(
            bus = 0u,
            bufferSize = 1024u,
            format = format
        ) { buffer, _ ->
            buffer?.let { recognitionRequest?.appendAudioPCMBuffer(it) }
        }

        audioEngine.prepare()
        val started = audioEngine.startAndReturnError(null)
        if (!started) {
            stopListening()
            onError?.invoke(VoiceInputError.Unknown)
            return
        }

        _isListening.value = true
    }

    private fun handleRecognitionResult(
        result: SFSpeechRecognitionResult?,
        hasError: Boolean
    ) {
        if (hasError) {
            stopListening()
            onError?.invoke(VoiceInputError.Unknown)
            return
        }

        val text = result?.bestTranscription?.formattedString.orEmpty()
        if (text.isBlank()) {
            return
        }

        onPartialResult?.invoke(text)

        if (result != null && result.isFinal()) {
            onFinalResult?.invoke(text)
            stopListening()
        }
    }
}
