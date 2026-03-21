package com.example.sobesai.presentation.interview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.Border
import com.example.sobesai.presentation.theme.SurfaceLight
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.interview_input_placeholder

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    isSending: Boolean,
    isLoading: Boolean
) {
    var text by remember { mutableStateOf("") }

    val handleSend = {
        if (text.isNotBlank() && !isSending) {
            onSendMessage(text)
            text = ""
        }
    }

    Surface(
        tonalElevation = AppDimens.Elevation.Normal,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .padding(AppDimens.Padding.Small)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(Res.string.interview_input_placeholder)) },
                enabled = !isLoading,
                shape = RoundedCornerShape(AppDimens.CornerShape.Normal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Border,
                    unfocusedBorderColor = Border,
                    cursorColor = Color.White
                ),
                maxLines = 3
            )
            Spacer(modifier = Modifier.size(AppDimens.SpacerHeight.Tiny))
            FilledIconButton(
                onClick = handleSend,
                modifier = Modifier.size(AppDimens.IconSize.Huge),
                enabled = !isLoading && !isSending && text.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = SurfaceLight,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    disabledContentColor = Color.Gray
                )
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppDimens.IconSize.Normal),
                        strokeWidth = AppDimens.Components.BorderStroke,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
