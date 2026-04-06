package com.example.sobesai.core.liveapi

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

/**
 * Error types for Gemini Live API
 */
enum class GeminiLiveError {
    NotConnected,
    ConnectionFailed,
    PermissionDenied,
    AudioCaptureFailed,
    AudioPlaybackFailed,
    ServerError,
    Unknown
}

/**
 * State of the Gemini Live session
 */
enum class GeminiLiveState {
    Idle,
    Connecting,
    Connected,
    Listening,
    Speaking,
    Error
}

/**
 * Configuration for Gemini Live API
 */
data class GeminiLiveConfig(
    val apiKey: String,
    val modelName: String = "gemini-2.5-flash-native-audio-preview-12-2025",
    val voiceName: String = "Aoede", // Available voices: Aoede, Puck, Charon, Fenrir, Kore
    val systemInstruction: String? = null
)

/**
 * Controller interface for Gemini Live real-time voice conversations.
 * This provides bidirectional voice communication with Gemini AI.
 */
interface GeminiLiveController {
    /**
     * Current state of the live session
     */
    val state: StateFlow<GeminiLiveState>

    /**
     * Whether the live session is currently active
     */
    val isActive: Boolean

    /**
     * Transcription of user's speech (partial and final results)
     */
    val userTranscript: StateFlow<String>

    /**
     * AI response text (when available)
     */
    val aiResponse: StateFlow<String>

    /**
     * Error that occurred, if any
     */
    val error: StateFlow<GeminiLiveError?>

    /**
     * Check if the feature is available on this device
     */
    val isAvailable: Boolean

    /**
     * Start a live voice conversation session.
     * This will connect to Gemini Live API and begin listening.
     *
     * @param config Configuration for the session
     * @param onError Callback for error handling
     */
    suspend fun startSession(
        config: GeminiLiveConfig,
        onError: (GeminiLiveError) -> Unit = {}
    )

    /**
     * Stop the current session and disconnect.
     */
    fun stopSession()

    /**
     * Mute the microphone (stop sending audio)
     */
    fun mute()

    /**
     * Unmute the microphone (resume sending audio)
     */
    fun unmute()

    /**
     * Interrupt the AI's current response (barge-in)
     */
    fun interrupt()
}

/**
 * Platform-specific factory for creating GeminiLiveController
 */
@Composable
expect fun rememberGeminiLiveController(): GeminiLiveController
