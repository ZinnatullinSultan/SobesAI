package com.example.sobesai.core.speech

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val AUDIO_PERMISSION_REQUEST_CODE = 1001

@Composable
actual fun rememberVoiceInputController(): VoiceInputController {
    val context = LocalContext.current
    val controller = remember(context) { AndroidVoiceInputController(context) }

    DisposableEffect(controller) {
        onDispose { controller.release() }
    }

    return controller
}

private class AndroidVoiceInputController(
    private val context: Context
) : VoiceInputController {

    private val _isListening = MutableStateFlow(false)
    override val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    override val isAvailable: Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    private val speechRecognizer: SpeechRecognizer? = if (isAvailable) {
        SpeechRecognizer.createSpeechRecognizer(context)
    } else {
        null
    }

    private var onPartialResult: ((String) -> Unit)? = null
    private var onFinalResult: ((String) -> Unit)? = null
    private var onError: ((VoiceInputError) -> Unit)? = null
    private var isStoppingManually = false

    init {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() = Unit

            override fun onRmsChanged(rmsdB: Float) = Unit

            override fun onBufferReceived(buffer: ByteArray?) = Unit

            override fun onEndOfSpeech() = Unit

            override fun onError(error: Int) {
                _isListening.value = false
                if (isStoppingManually && error == SpeechRecognizer.ERROR_CLIENT) {
                    isStoppingManually = false
                    return
                }
                isStoppingManually = false
                onError?.invoke(mapError(error))
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                isStoppingManually = false
                val text = extractTopResult(results)
                if (!text.isNullOrBlank()) {
                    onFinalResult?.invoke(text)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val text = extractTopResult(partialResults)
                if (!text.isNullOrBlank()) {
                    onPartialResult?.invoke(text)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })
    }

    override fun startListening(
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (VoiceInputError) -> Unit
    ) {
        if (!isAvailable || speechRecognizer == null) {
            onError(VoiceInputError.Unavailable)
            return
        }

        if (!hasAudioPermission()) {
            requestAudioPermission()
            onError(VoiceInputError.PermissionDenied)
            return
        }

        this.onPartialResult = onPartialResult
        this.onFinalResult = onFinalResult
        this.onError = onError
        isStoppingManually = false

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                putExtra("android.speech.extra.LANGUAGE_SWITCH_ALLOWED", true)
                // Список поддерживаемых языков
                putExtra(
                    RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES,
                    arrayListOf("ru-RU", "en-US")
                )
            }

            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ru-RU")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)

            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        _isListening.value = true
        speechRecognizer.cancel()
        speechRecognizer.startListening(intent)
    }

    override fun stopListening() {
        if (_isListening.value) {
            isStoppingManually = true
            _isListening.value = false
            speechRecognizer?.stopListening()
        }
    }

    fun release() {
        speechRecognizer?.destroy()
    }

    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        val activity = context.findActivity() ?: return
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            AUDIO_PERMISSION_REQUEST_CODE
        )
    }

    private fun mapError(error: Int): VoiceInputError {
        return when (error) {
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> VoiceInputError.PermissionDenied
            SpeechRecognizer.ERROR_CLIENT,
            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
            SpeechRecognizer.ERROR_SERVER,
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY,
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
            SpeechRecognizer.ERROR_AUDIO -> VoiceInputError.Unknown

            else -> VoiceInputError.Unknown
        }
    }

    private fun extractTopResult(bundle: Bundle?): String? {
        return bundle
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull()
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
