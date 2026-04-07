package com.example.sobesai.presentation.interview.ui.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sobesai.presentation.theme.AppDimens
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.interview_ai_typing

private const val BACKGROUND_ALPHA = 0.5f

@Composable
fun TypingIndicator() {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = BACKGROUND_ALPHA),
        shape = RoundedCornerShape(
            topStart = AppDimens.CornerShape.Small,
            topEnd = AppDimens.CornerShape.Small,
            bottomEnd = AppDimens.CornerShape.Small,
            bottomStart = AppDimens.CornerShape.None
        ),
        modifier = Modifier.padding(start = AppDimens.Padding.Small)
    ) {
        Text(
            text = stringResource(Res.string.interview_ai_typing),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = AppDimens.Padding.Normal,
                vertical = AppDimens.Padding.Small
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
