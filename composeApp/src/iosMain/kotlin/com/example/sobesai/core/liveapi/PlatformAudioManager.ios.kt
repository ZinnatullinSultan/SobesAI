package com.example.sobesai.core.liveapi

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.set
import kotlinx.coroutines.channels.Channel
import platform.AVFAudio.AVAudioBuffer
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioFormat
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioPlayerNode
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDefaultToSpeaker
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.setActive
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalForeignApi::class)
class IosAudioManager : PlatformAudioManager {

    private var audioEngine: AVAudioEngine? = null
    private var playerNode: AVAudioPlayerNode? = null
    private var isCapturing = false
    private var isPlaying = false

    override suspend fun startCapture(
        audioChannel: Channel<ByteArray>,
        config: AudioCaptureConfig
    ): Boolean {
        if (!hasMicrophonePermission()) {
            return false
        }

        if (isCapturing) {
            stopCapture()
        }

        try {
            configureAudioSession()

            audioEngine = AVAudioEngine()

            val inputNode = audioEngine?.inputNode ?: return false
            val format = inputNode.outputFormatForBus(0u)

            inputNode.installTapOnBus(
                bus = 0u,
                bufferSize = 1024u,
                format = format
            ) { buffer: AVAudioBuffer?, _ ->
                if (buffer != null && isCapturing) {
                    val pcmBuffer = buffer as? AVAudioPCMBuffer
                    if (pcmBuffer != null) {
                        val audioData = convertBufferToPCM16(pcmBuffer)
                        if (audioData.isNotEmpty()) {
                            audioChannel.trySend(audioData)
                        }
                    }
                }
            }

            audioEngine?.prepare()
            val started = audioEngine?.startAndReturnError(null) ?: false

            if (!started) {
                stopCapture()
                return false
            }

            isCapturing = true
            return true
        } catch (e: Exception) {
            stopCapture()
            return false
        }
    }

    private fun convertBufferToPCM16(buffer: AVAudioPCMBuffer): ByteArray {
        val frameLength = buffer.frameLength.toInt()
        if (frameLength == 0) return ByteArray(0)

        val floatChannelData = buffer.floatChannelData
        if (floatChannelData == null) return ByteArray(0)

        val outputSize = frameLength * 2
        val output = ByteArray(outputSize)

        // Access the float data using the CPointer
        val floatPtr = floatChannelData[0]
        if (floatPtr == null) return ByteArray(0)

        for (i in 0 until frameLength) {
            val sample = floatPtr[i]
            val clipped = max(-1.0f, min(1.0f, sample))
            val intSample = (clipped * 32767.0f).toInt().toShort()
            output[i * 2] = (intSample.toInt() and 0xFF).toByte()
            output[i * 2 + 1] = (intSample.toInt() shr 8 and 0xFF).toByte()
        }

        return output
    }

    override fun stopCapture() {
        isCapturing = false
        audioEngine?.inputNode?.removeTapOnBus(0u)
        audioEngine?.stop()
        audioEngine = null
    }

    override suspend fun startPlayback(config: AudioPlaybackConfig): Boolean {
        if (isPlaying) {
            stopPlayback()
        }

        try {
            configureAudioSession()

            audioEngine = AVAudioEngine()
            playerNode = AVAudioPlayerNode()

            audioEngine?.attachNode(playerNode!!)

            // Use standard PCM float format for playback (more compatible)
            val format = AVAudioFormat(
                standardFormatWithSampleRate = config.sampleRate.toDouble(),
                channels = 1u
            ) ?: return false

            audioEngine?.connect(playerNode!!, to = audioEngine?.mainMixerNode!!, format = format)

            audioEngine?.prepare()
            val started = audioEngine?.startAndReturnError(null) ?: false

            if (!started) {
                stopPlayback()
                return false
            }

            isPlaying = true
            return true
        } catch (e: Exception) {
            stopPlayback()
            return false
        }
    }

    override fun writeAudio(audioData: ByteArray) {
        if (!isPlaying || playerNode == null) return

        try {
            val sampleCount = audioData.size / 2
            val format = playerNode?.outputFormatForBus(0u) ?: return

            val buffer = AVAudioPCMBuffer(pcmFormat = format, frameCapacity = sampleCount.toUInt())
                ?: return
            buffer.frameLength = sampleCount.toUInt()

            val floatChannelData = buffer.floatChannelData
            if (floatChannelData != null) {
                val floatPtr = floatChannelData[0]
                if (floatPtr != null) {
                    for (i in 0 until sampleCount) {
                        val low = (audioData[i * 2].toInt() and 0xFF)
                        val high = (audioData[i * 2 + 1].toInt() and 0xFF)
                        val sample = ((high shl 8) or low).toShort()
                        floatPtr[i] = sample.toFloat() / 32768.0f
                    }
                }
            }

            if (playerNode?.isPlaying() == false) {
                playerNode?.play()
            }

            playerNode?.scheduleBuffer(buffer, completionHandler = null)
        } catch (e: Exception) {
            // Ignore playback errors
        }
    }

    override fun stopPlayback() {
        isPlaying = false
        playerNode?.stop()

        if (playerNode != null && audioEngine != null) {
            audioEngine?.disconnectNodeOutput(playerNode!!)
        }

        audioEngine?.stop()
        audioEngine = null
        playerNode = null
    }

    private fun configureAudioSession() {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(
            category = AVAudioSessionCategoryPlayAndRecord,
            mode = AVAudioSessionModeDefault,
            options = AVAudioSessionCategoryOptionDefaultToSpeaker,
            error = null
        )
        session.setActive(true, error = null)
    }

    override fun hasMicrophonePermission(): Boolean {
        return try {
            val status = AVAudioSession.sharedInstance().recordPermission
            // AVAudioSessionRecordPermission: 1735550278 = 'grnt' (granted)
            status == 1735550278L
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun requestMicrophonePermission(): Boolean {
        return hasMicrophonePermission()
    }

    override fun isCaptureAvailable(): Boolean = true
    override fun isPlaybackAvailable(): Boolean = true

    override fun release() {
        stopCapture()
        stopPlayback()
        try {
            AVAudioSession.sharedInstance().setActive(false, error = null)
        } catch (e: Exception) {
            // Ignore
        }
    }
}

actual fun createPlatformAudioManager(): PlatformAudioManager {
    return IosAudioManager()
}
