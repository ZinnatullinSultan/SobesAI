package com.example.sobesai.core.liveapi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AndroidAudioManager(
    private val context: Context
) : PlatformAudioManager {

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var captureJob: Job? = null
    private var isCapturing = false
    private var isPlaying = false

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1002
    }

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

        val bufferSize = AudioRecord.getMinBufferSize(
            config.sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(1024)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                config.sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize * 2
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                return false
            }

            audioRecord?.startRecording()
            isCapturing = true

            captureJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(bufferSize / 2)

                while (isActive && isCapturing) {
                    val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: -1

                    if (bytesRead > 0) {
                        val chunk = buffer.copyOf(bytesRead)
                        audioChannel.trySend(chunk)
                    }
                }
            }

            return true
        } catch (e: SecurityException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }

    override fun stopCapture() {
        isCapturing = false
        captureJob?.cancel()
        captureJob = null

        try {
            audioRecord?.stop()
        } catch (e: Exception) {
            // Ignore
        }

        audioRecord?.release()
        audioRecord = null
    }

    override suspend fun startPlayback(config: AudioPlaybackConfig): Boolean {
        if (isPlaying) {
            stopPlayback()
        }

        val bufferSize = AudioTrack.getMinBufferSize(
            config.sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(config.sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            isPlaying = true

            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun writeAudio(audioData: ByteArray) {
        if (!isPlaying || audioTrack == null) return

        try {
            audioTrack?.write(audioData, 0, audioData.size)
        } catch (e: Exception) {
            // Ignore write errors
        }
    }

    override fun stopPlayback() {
        isPlaying = false

        try {
            audioTrack?.stop()
        } catch (e: Exception) {
            // Ignore
        }

        audioTrack?.release()
        audioTrack = null
    }

    override fun hasMicrophonePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestMicrophonePermission(): Boolean {
        // This requires an Activity context for requesting permissions
        // The actual permission request should be handled by the UI layer
        // This method just checks the current state
        return hasMicrophonePermission()
    }

    override fun isCaptureAvailable(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    override fun isPlaybackAvailable(): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        return audioManager != null
    }

    override fun release() {
        stopCapture()
        stopPlayback()
    }
}

actual fun createPlatformAudioManager(): PlatformAudioManager {
    val context = getApplicationContext()
    return AndroidAudioManager(context)
}

private var applicationContext: Context? = null

fun initAudioManager(context: Context) {
    applicationContext = context.applicationContext
}

private fun getApplicationContext(): Context {
    return applicationContext ?: throw IllegalStateException("AudioManager not initialized. Call initAudioManager(context) first.")
}
