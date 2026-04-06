package com.example.sobesai.core.liveapi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val LOG_TAG = "GeminiLiveController"

/**
 * Common implementation of GeminiLiveController.
 * Manages the session lifecycle, audio streaming, and state management.
 */
class GeminiLiveControllerImpl(
    private val audioManager: PlatformAudioManager
) : GeminiLiveController {

    private val _state = MutableStateFlow(GeminiLiveState.Idle)
    override val state: StateFlow<GeminiLiveState> = _state.asStateFlow()

    private val _userTranscript = MutableStateFlow("")
    override val userTranscript: StateFlow<String> = _userTranscript.asStateFlow()

    private val _aiResponse = MutableStateFlow("")
    override val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _error = MutableStateFlow<GeminiLiveError?>(null)
    override val error: StateFlow<GeminiLiveError?> = _error.asStateFlow()

    private var webSocketClient: GeminiLiveWebSocketClient? = null
    private var sessionJob: Job? = null
    private var audioChannel: Channel<ByteArray> = Channel(Channel.UNLIMITED)

    private var isMuted = false
    private var currentConfig: GeminiLiveConfig? = null

    override val isActive: Boolean
        get() = _state.value != GeminiLiveState.Idle && _state.value != GeminiLiveState.Error

    override val isAvailable: Boolean
        get() = audioManager.isCaptureAvailable() && audioManager.isPlaybackAvailable()

    override suspend fun startSession(
        config: GeminiLiveConfig,
        onError: (GeminiLiveError) -> Unit
    ) {
        println("[$LOG_TAG] startSession called")
        if (_state.value == GeminiLiveState.Connecting || _state.value == GeminiLiveState.Connected) {
            println("[$LOG_TAG] Already connecting/connected, returning")
            return
        }

        _state.value = GeminiLiveState.Connecting
        _error.value = null
        currentConfig = config

        // Check permissions
        println("[$LOG_TAG] Checking microphone permission...")
        if (!audioManager.hasMicrophonePermission()) {
            println("[$LOG_TAG] No permission, requesting...")
            val granted = audioManager.requestMicrophonePermission()
            if (!granted) {
                println("[$LOG_TAG] Permission denied!")
                _state.value = GeminiLiveState.Error
                _error.value = GeminiLiveError.PermissionDenied
                onError(GeminiLiveError.PermissionDenied)
                return
            }
        }
        println("[$LOG_TAG] Permission granted")

        // Initialize WebSocket client
        println("[$LOG_TAG] Creating WebSocket client...")
        webSocketClient = GeminiLiveWebSocketClient()

        // Connect to Gemini Live API
        println("[$LOG_TAG] Connecting to Gemini Live...")
        val connected = webSocketClient?.connect(config) ?: false
        if (!connected) {
            println("[$LOG_TAG] Connection failed!")
            _state.value = GeminiLiveState.Error
            _error.value = GeminiLiveError.ConnectionFailed
            onError(GeminiLiveError.ConnectionFailed)
            return
        }
        println("[$LOG_TAG] Connected to Gemini Live!")

        _state.value = GeminiLiveState.Connected

        // Wait for setup complete from server
        println("[$LOG_TAG] Waiting for setup complete...")
        val ready = webSocketClient?.waitForSetup() ?: false
        if (!ready) {
            println("[$LOG_TAG] Setup timeout!")
            stopSession()
            _state.value = GeminiLiveState.Error
            _error.value = GeminiLiveError.ConnectionFailed
            onError(GeminiLiveError.ConnectionFailed)
            return
        }
        println("[$LOG_TAG] Setup complete, ready for audio!")

        // Start audio capture and playback
        println("[$LOG_TAG] Starting audio capture...")
        val captureStarted = audioManager.startCapture(audioChannel)
        if (!captureStarted) {
            println("[$LOG_TAG] Audio capture failed!")
            stopSession()
            _state.value = GeminiLiveState.Error
            _error.value = GeminiLiveError.AudioCaptureFailed
            onError(GeminiLiveError.AudioCaptureFailed)
            return
        }
        println("[$LOG_TAG] Audio capture started")

        println("[$LOG_TAG] Starting audio playback...")
        val playbackStarted = audioManager.startPlayback()
        if (!playbackStarted) {
            println("[$LOG_TAG] Audio playback failed!")
            stopSession()
            _state.value = GeminiLiveState.Error
            _error.value = GeminiLiveError.AudioPlaybackFailed
            onError(GeminiLiveError.AudioPlaybackFailed)
            return
        }
        println("[$LOG_TAG] Audio playback started")

        _state.value = GeminiLiveState.Listening
        println("[$LOG_TAG] Session started! State: Listening")

        // Start streaming audio
        startAudioStreaming()
    }

    private fun startAudioStreaming() {
        println("[$LOG_TAG] Starting audio streaming...")
        var chunksSent = 0
        sessionJob = CoroutineScope(Dispatchers.Default).launch {
            // Launch audio sender
            launch {
                println("[$LOG_TAG] Audio sender coroutine started")
                while (isActive && webSocketClient?.isConnected() == true) {
                    if (!isMuted && _state.value == GeminiLiveState.Listening) {
                        val audioChunk = audioChannel.tryReceive().getOrNull()
                        if (audioChunk != null) {
                            webSocketClient?.sendAudioChunk(audioChunk)
                            chunksSent++
                            if (chunksSent % 50 == 0) {
                                println("[$LOG_TAG] Sent $chunksSent audio chunks")
                            }
                        }
                    }
                }
                println("[$LOG_TAG] Audio sender coroutine ended")
            }

            // Launch audio receiver
            launch {
                val audioOutputChannel = webSocketClient?.getAudioOutputChannel()
                if (audioOutputChannel != null) {
                    for (audioData in audioOutputChannel) {
                        if (_state.value == GeminiLiveState.Speaking) {
                            audioManager.writeAudio(audioData)
                        }
                    }
                }
            }

            // Update transcripts
            launch {
                webSocketClient?.userTranscript?.collect { text ->
                    _userTranscript.value = text
                }
            }

            launch {
                webSocketClient?.aiResponse?.collect { text ->
                    _aiResponse.value = text
                    if (text.isNotEmpty()) {
                        _state.value = GeminiLiveState.Speaking
                    }
                }
            }
        }
    }

    override fun stopSession() {
        sessionJob?.cancel()
        sessionJob = null

        audioManager.stopCapture()
        audioManager.stopPlayback()

        CoroutineScope(Dispatchers.Default).launch {
            webSocketClient?.disconnect()
            webSocketClient = null
        }

        _state.value = GeminiLiveState.Idle
        _userTranscript.value = ""
        _aiResponse.value = ""
        isMuted = false
    }

    override fun mute() {
        isMuted = true
    }

    override fun unmute() {
        isMuted = false
    }

    override fun interrupt() {
        // Stop the AI's response and return to listening
        if (_state.value == GeminiLiveState.Speaking) {
            audioManager.stopPlayback()
            CoroutineScope(Dispatchers.Default).launch {
                audioManager.startPlayback()
            }
            _state.value = GeminiLiveState.Listening
        }
    }

    fun release() {
        stopSession()
        audioManager.release()
    }
}
