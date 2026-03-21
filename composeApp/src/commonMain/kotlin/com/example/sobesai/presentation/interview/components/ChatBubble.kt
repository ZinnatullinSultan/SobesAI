package com.example.sobesai.presentation.interview.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sobesai.domain.model.ChatMessage
import com.example.sobesai.domain.model.MessageRole
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val color =
        if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val shape = if (isUser) {
        RoundedCornerShape(
            topStart = AppDimens.CornerShape.Small,
            topEnd = AppDimens.CornerShape.Small,
            bottomEnd = AppDimens.CornerShape.None,
            bottomStart = AppDimens.CornerShape.Small
        )
    } else {
        RoundedCornerShape(
            topStart = AppDimens.CornerShape.Small,
            topEnd = AppDimens.CornerShape.Small,
            bottomEnd = AppDimens.CornerShape.Small,
            bottomStart = AppDimens.CornerShape.None
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = color,
            shape = shape,
            tonalElevation = AppDimens.Elevation.Small,
            modifier = Modifier.widthIn(max = AppDimens.Components.MessageCardMaxWidth)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(AppDimens.Padding.Normal),
                style = AppTypography.bodySmall
            )
        }
    }
}
