package com.example.sobesai.presentation.liveinterview

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sobesai.core.liveapi.GeminiLiveState
import com.example.sobesai.core.liveapi.rememberGeminiLiveController
import com.example.sobesai.presentation.components.AppTopBar
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveInterviewScreen(
    specId: Long,
    difficulty: String,
    apiKey: String,
    onBackClick: () -> Unit,
    viewModel: LiveInterviewViewModel = org.koin.compose.viewmodel.koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val controller = rememberGeminiLiveController()
    val liveState by controller.state.collectAsState()

    LaunchedEffect(specId, difficulty) {
        viewModel.init(specId, difficulty, controller, apiKey)
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LiveInterviewContent(
        state = state,
        liveState = liveState,
        isAvailable = controller.isAvailable,
        onBackClick = onBackClick,
        onStartClick = { viewModel.handleIntent(LiveInterviewIntent.StartSession) },
        onStopClick = { viewModel.handleIntent(LiveInterviewIntent.StopSession) },
        onInterruptClick = { viewModel.handleIntent(LiveInterviewIntent.Interrupt) },
        onDismissError = { viewModel.handleIntent(LiveInterviewIntent.DismissError) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LiveInterviewContent(
    state: LiveInterviewState,
    liveState: GeminiLiveState,
    isAvailable: Boolean,
    onBackClick: () -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onInterruptClick: () -> Unit,
    onDismissError: () -> Unit
) {
    // Error dialog
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            title = { Text("Ошибка") },
            text = { Text(state.error ?: "Неизвестная ошибка") },
            confirmButton = {
                TextButton(onClick = onDismissError) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                onBackClick = onBackClick,
                onProfileClick = null,
                onClearClick = null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppDimens.Padding.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Голосовое собеседование",
                style = AppTypography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))

            Text(
                text = "${state.specializationTitle} • ${state.difficultyLevel}",
                style = AppTypography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.ExtraLarge))

            // Status indicator
            LiveSessionStatusIndicator(
                state = liveState,
                modifier = Modifier.padding(vertical = AppDimens.Padding.Normal)
            )

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Large))

            // Conversation area
            ConversationArea(
                userTranscript = state.userTranscript,
                aiResponse = state.aiResponse,
                state = liveState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Large))

            // Controls
            LiveSessionControls(
                state = liveState,
                isAvailable = isAvailable,
                onStart = onStartClick,
                onStop = onStopClick,
                onInterrupt = onInterruptClick
            )
        }
    }
}

@Composable
private fun LiveSessionStatusIndicator(
    state: GeminiLiveState,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (state) {
            GeminiLiveState.Idle -> Color.Gray
            GeminiLiveState.Connecting -> Color(0xFFFFC107)
            GeminiLiveState.Connected -> Color(0xFF4CAF50)
            GeminiLiveState.Listening -> Color(0xFF4CAF50)
            GeminiLiveState.Speaking -> Color(0xFF2196F3)
            GeminiLiveState.Error -> Color(0xFFF44336)
        },
        label = "status_color"
    )

    val statusText = when (state) {
        GeminiLiveState.Idle -> "Нажмите для начала"
        GeminiLiveState.Connecting -> "Подключение..."
        GeminiLiveState.Connected -> "Подключено"
        GeminiLiveState.Listening -> "Слушаю..."
        GeminiLiveState.Speaking -> "AI отвечает..."
        GeminiLiveState.Error -> "Ошибка"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .scale(if (state == GeminiLiveState.Listening || state == GeminiLiveState.Speaking) scale else 1f)
                .background(statusColor, CircleShape)
        )

        Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Small))

        Text(
            text = statusText,
            style = AppTypography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConversationArea(
    userTranscript: String,
    aiResponse: String,
    state: GeminiLiveState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AppDimens.Radius.Large))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(AppDimens.Padding.Normal),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Padding.Normal)
    ) {
        if (userTranscript.isNotEmpty()) {
            ConversationBubble(
                text = userTranscript,
                isUser = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (aiResponse.isNotEmpty()) {
            ConversationBubble(
                text = aiResponse,
                isUser = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (userTranscript.isEmpty() && aiResponse.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (state) {
                        GeminiLiveState.Listening -> "Говорите..."
                        GeminiLiveState.Speaking -> "AI отвечает..."
                        else -> "Начните разговор"
                    },
                    style = AppTypography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ConversationBubble(
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
        if (!isUser) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = backgroundColor
        ) {
            Text(
                text = text,
                style = AppTypography.bodyMedium,
                color = contentColor,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun LiveSessionControls(
    state: GeminiLiveState,
    isAvailable: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onInterrupt: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (state) {
            GeminiLiveState.Idle, GeminiLiveState.Error -> {
                Button(
                    onClick = onStart,
                    enabled = isAvailable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Начать собеседование")
                }
            }

            GeminiLiveState.Connecting -> {
                CircularProgressIndicator()
            }

            GeminiLiveState.Connected, GeminiLiveState.Listening, GeminiLiveState.Speaking -> {
                // Stop button
                IconButton(
                    onClick = onStop,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Остановить",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }

                // Interrupt button (only when AI is speaking)
                if (state == GeminiLiveState.Speaking) {
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(onClick = onInterrupt) {
                        Text("Прервать")
                    }
                }
            }
        }
    }
}
