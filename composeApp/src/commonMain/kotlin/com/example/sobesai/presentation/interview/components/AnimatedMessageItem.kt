package com.example.sobesai.presentation.interview.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.sobesai.domain.model.ChatMessage

private const val ANIMATION_DURATION = 300

@Composable
fun AnimatedMessageItem(message: ChatMessage) {
    var hasAnimated by rememberSaveable() { mutableStateOf(false) }
    var visible by remember { mutableStateOf(hasAnimated) }

    LaunchedEffect(Unit) {
        if (!hasAnimated) {
            visible = true
            hasAnimated = true
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(ANIMATION_DURATION)
                )
    ) {
        ChatBubble(message)
    }
}
