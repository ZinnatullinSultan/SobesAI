package com.example.sobesai.core.liveapi

import kotlinx.coroutines.channels.Channel

/**
 * Configuration for audio capture
 */
data class AudioCaptureConfig(
    val sampleRate: Int = 16000,
    val channelCount: Int = 1,
    val bitsPerSample: Int = 16
)

/**
 * Configuration for audio playback
 */
data class AudioPlaybackConfig(
    val sampleRate: Int = 24000,
    val channelCount: Int = 1,
    val bitsPerSample: Int = 16
)

/**
 * Platform-specific audio manager interface.
 * Handles audio capture and playback for Gemini Live API.
 */
interface PlatformAudioManager {
    /**
     * Start capturing audio from the microphone.
     * Audio chunks are sent to the provided channel.
     *
     * @param audioChannel Channel to receive audio chunks (PCM 16-bit, 16kHz)
     * @param config Audio capture configuration
     * @return True if capture started successfully
     */
    suspend fun startCapture(
        audioChannel: Channel<ByteArray>,
        config: AudioCaptureConfig = AudioCaptureConfig()
    ): Boolean

    /**
     * Stop capturing audio
     */
    fun stopCapture()

    /**
     * Start audio playback.
     *
     * @param config Audio playback configuration
     * @return True if playback started successfully
     */
    suspend fun startPlayback(config: AudioPlaybackConfig = AudioPlaybackConfig()): Boolean

    /**
     * Write audio data to playback buffer.
     *
     * @param audioData PCM audio data (16-bit, 24kHz)
     */
    fun writeAudio(audioData: ByteArray)

    /**
     * Stop audio playback
     */
    fun stopPlayback()

    /**
     * Check if microphone permission is granted
     */
    fun hasMicrophonePermission(): Boolean

    /**
     * Request microphone permission
     */
    suspend fun requestMicrophonePermission(): Boolean

    /**
     * Check if audio capture is available
     */
    fun isCaptureAvailable(): Boolean

    /**
     * Check if audio playback is available
     */
    fun isPlaybackAvailable(): Boolean

    /**
     * Release all audio resources
     */
    fun release()
}

/**
 * Factory for creating platform-specific audio managers
 */
expect fun createPlatformAudioManager(): PlatformAudioManager
