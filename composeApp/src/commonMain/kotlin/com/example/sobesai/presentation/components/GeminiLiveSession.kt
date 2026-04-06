package com.example.sobesai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sobesai.core.liveapi.GeminiLiveConfig
import com.example.sobesai.core.liveapi.GeminiLiveController
import com.example.sobesai.core.liveapi.GeminiLiveError
import com.example.sobesai.core.liveapi.GeminiLiveState
import com.example.sobesai.core.liveapi.rememberGeminiLiveController
import kotlinx.coroutines.launch

/**
 * A composable UI component for Gemini Live voice conversations.
 * 
 * Usage:
 * ```
 * GeminiLiveSession(
 *     apiKey = "YOUR_API_KEY",
 *     systemInstruction = "You are a helpful interview assistant...",
 *     onTranscriptReceived = { transcript -> },
 *     onAiResponseReceived = { response -> }
 * )
 * ```
 */
@Composable
fun GeminiLiveSession(
    apiKey: String,
    modifier: Modifier = Modifier,
    modelName: String = "gemini-2.5-flash-preview-native-audio-dialog",
    voiceName: String = "Fenrir",
    systemInstruction: String? = null,
    onTranscriptReceived: (String) -> Unit = {},
    onAiResponseReceived: (String) -> Unit = {},
    onError: (GeminiLiveError) -> Unit = {}
) {
    val controller = rememberGeminiLiveController()
    val scope = rememberCoroutineScope()
    
    val state by controller.state.collectAsState()
    val userTranscript by controller.userTranscript.collectAsState()
    val aiResponse by controller.aiResponse.collectAsState()
    val error by controller.error.collectAsState()

    val config = GeminiLiveConfig(
        apiKey = apiKey,
        modelName = modelName,
        voiceName = voiceName,
        systemInstruction = systemInstruction
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status indicator
            LiveSessionStatus(
                state = state,
                error = error
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Transcripts
            if (userTranscript.isNotEmpty()) {
                TranscriptBubble(
                    text = userTranscript,
                    isUser = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (aiResponse.isNotEmpty()) {
                TranscriptBubble(
                    text = aiResponse,
                    isUser = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Controls
            LiveSessionControls(
                state = state,
                isActive = controller.isActive,
                isAvailable = controller.isAvailable,
                onStart = {
                    scope.launch {
                        controller.startSession(config, onError)
                    }
                },
                onStop = { controller.stopSession() },
                onMute = { controller.mute() },
                onUnmute = { controller.unmute() },
                onInterrupt = { controller.interrupt() }
            )
        }
    }
}

@Composable
private fun LiveSessionStatus(
    state: GeminiLiveState,
    error: GeminiLiveError?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val statusColor = when (state) {
            GeminiLiveState.Idle -> Color.Gray
            GeminiLiveState.Connecting -> Color.Yellow
            GeminiLiveState.Connected -> Color.Green
            GeminiLiveState.Listening -> Color.Green
            GeminiLiveState.Speaking -> Color.Blue
            GeminiLiveState.Error -> Color.Red
        }

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(statusColor, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        val statusText = when (state) {
            GeminiLiveState.Idle -> "Ready"
            GeminiLiveState.Connecting -> "Connecting..."
            GeminiLiveState.Connected -> "Connected"
            GeminiLiveState.Listening -> "Listening..."
            GeminiLiveState.Speaking -> "AI Speaking..."
            GeminiLiveState.Error -> error?.name ?: "Error"
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium
        )

        if (state == GeminiLiveState.Connecting) {
            Spacer(modifier = Modifier.width(8.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun TranscriptBubble(
    text: String,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Row(
        modifier = modifier,
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isUser) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = contentColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (isUser) "You" else "AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun LiveSessionControls(
    state: GeminiLiveState,
    isActive: Boolean,
    isAvailable: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMute: () -> Unit,
    onUnmute: () -> Unit,
    onInterrupt: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (state) {
            GeminiLiveState.Idle, GeminiLiveState.Error -> {
                IconButton(
                    onClick = onStart,
                    enabled = isAvailable
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Start Live Session",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            GeminiLiveState.Connecting -> {
                // Show loading state
                CircularProgressIndicator()
            }

            GeminiLiveState.Connected, GeminiLiveState.Listening, GeminiLiveState.Speaking -> {
                // Stop button
                IconButton(onClick = onStop) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Session",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Interrupt button (barge-in)
                if (state == GeminiLiveState.Speaking) {
                    TextButton(onClick = onInterrupt) {
                        Text("Interrupt")
                    }
                }
            }
        }
    }
}
