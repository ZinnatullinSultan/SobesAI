package com.example.sobesai.presentation.interview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.model.MessageRole
import com.example.sobesai.presentation.interview.InterviewViewModel
import com.example.sobesai.presentation.interview.ui.widgets.AnimatedMessageItem
import com.example.sobesai.presentation.interview.ui.widgets.MessageInput
import com.example.sobesai.presentation.interview.ui.widgets.TypingIndicator
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.widgets.AppTopBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.interview_dialog_clear_confirm
import sobesai.composeapp.generated.resources.interview_dialog_clear_dismiss
import sobesai.composeapp.generated.resources.interview_dialog_clear_text
import sobesai.composeapp.generated.resources.interview_dialog_clear_title
import sobesai.composeapp.generated.resources.interview_difficult
import sobesai.composeapp.generated.resources.interview_error_retry
import sobesai.composeapp.generated.resources.interview_error_title
import sobesai.composeapp.generated.resources.interview_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewScreen(
    specId: Long,
    difficulty: String,
    onBackClick: () -> Unit,
    viewModel: InterviewViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(InterviewIntent.Init(specId, difficulty))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                InterviewEffect.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        lazyListState.animateScrollToItem(state.messages.size - 1)
                    }
                }

                InterviewEffect.NavigateBack -> onBackClick()
            }
        }
    }

    LaunchedEffect(state.messages.size, state.isTyping) {
        if (state.messages.isNotEmpty()) {
            val targetIndex = if (state.isTyping) state.messages.size else state.messages.size - 1
            if (targetIndex >= 0) {
                lazyListState.animateScrollToItem(targetIndex)
            }
        }
    }

    InterviewContent(
        state = state,
        lazyListState = lazyListState,
        onBackClick = onBackClick,
        onSendMessage = { viewModel.handleIntent(InterviewIntent.SendMessage(it)) },
        onClearClick = { viewModel.handleIntent(InterviewIntent.ShowClearHistoryDialog) },
        onConfirmClear = { viewModel.handleIntent(InterviewIntent.ClearHistory) },
        onDismissDialog = { viewModel.handleIntent(InterviewIntent.HideClearHistoryDialog) },
        onRetry = { viewModel.handleIntent(InterviewIntent.Retry) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
private fun InterviewContent(
    state: InterviewState,
    lazyListState: LazyListState,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onClearClick: () -> Unit,
    onConfirmClear: () -> Unit,
    onDismissDialog: () -> Unit,
    onRetry: () -> Unit
) {
    if (state.showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text(stringResource(Res.string.interview_dialog_clear_title)) },
            text = { Text(stringResource(Res.string.interview_dialog_clear_text)) },
            confirmButton = {
                TextButton(onClick = onConfirmClear) {
                    Text(
                        text = stringResource(Res.string.interview_dialog_clear_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) {
                    Text(stringResource(Res.string.interview_dialog_clear_dismiss))
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            containerColor = Color.Transparent,
            topBar = {
                AppTopBar(
                    onBackClick = onBackClick,
                    onProfileClick = null,
                    onClearClick = onClearClick
                )
            },
            bottomBar = {
                if (!(state.error != null && state.messages.isEmpty())) {
                    MessageInput(
                        onSendMessage = onSendMessage,
                        isSending = state.isTyping,
                        isLoading = state.isLoading
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    state.isLoading && state.messages.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    state.error != null && state.messages.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppDimens.Padding.Normal),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Small))
                            Text(
                                text = stringResource(Res.string.interview_error_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
                            Text(
                                text = stringResource(state.error),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))
                            TextButton(
                                onClick = onRetry,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(stringResource(Res.string.interview_error_retry))
                            }
                        }
                    }

                    else ->
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(AppDimens.Padding.Normal),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Components.ArrangementSpaceSmall)
                        ) {
                            item {
                                Column {
                                    Text(
                                        text = stringResource(Res.string.interview_title)
                                                + state.specializationTitle,
                                        style = AppTypography.titleLarge
                                    )
                                    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
                                    Text(
                                        text = stringResource(Res.string.interview_difficult)
                                                + state.difficultyLevel,
                                        style = AppTypography.labelMedium
                                    )
                                    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
                                }
                            }
                            items(
                                items = state.messages,
                                key = { it.timestamp.toString() + it.role.name }
                            ) { message ->
                                AnimatedMessageItem(message)
                            }

                            if (state.isTyping) {
                                item(key = "typing_indicator") {
                                    TypingIndicator()
                                }
                            }

                            if (state.error != null && state.messages.isNotEmpty()) {
                                item(key = "error_banner") {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = AppDimens.Padding.Normal),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp),
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
                                        Text(
                                            text = stringResource(state.error),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Small))
                                        TextButton(onClick = onRetry) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.size(8.dp))
                                            Text(stringResource(Res.string.interview_error_retry))
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewInterviewScreen() {
    MaterialTheme {
        InterviewContent(
            state = InterviewState(
                specializationTitle = "Android Developer",
                difficultyLevel = "Middle",
                messages = listOf(
                    ChatMessage(MessageRole.MODEL, "Привет! Расскажи про SOLID?"),
                    ChatMessage(MessageRole.USER, "Привет! Это пять принципов...")
                )
            ),
            lazyListState = rememberLazyListState(),
            onBackClick = {},
            onSendMessage = {},
            onClearClick = {},
            onConfirmClear = {},
            onDismissDialog = {},
            onRetry = {}
        )
    }
}
