package com.example.sobesai.presentation.interview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.model.MessageRole
import com.example.sobesai.presentation.components.AppTopBar
import com.example.sobesai.presentation.interview.components.AnimatedMessageItem
import com.example.sobesai.presentation.interview.components.MessageInput
import com.example.sobesai.presentation.interview.components.TypingIndicator
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.interview_difficult
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
        onClearClick = { viewModel.handleIntent(InterviewIntent.ClearHistory) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InterviewContent(
    state: InterviewState,
    lazyListState: LazyListState,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            AppTopBar(
                onBackClick = onBackClick,
                onProfileClick = null,
                onClearClick = onClearClick
            )
        },
        bottomBar = {
            MessageInput(
                onSendMessage = onSendMessage,
                isSending = state.isTyping,
                isLoading = state.isLoading
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.messages.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
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
                }
            }
        }
    }
}

@Preview
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
            onClearClick = {}
        )
    }
}
