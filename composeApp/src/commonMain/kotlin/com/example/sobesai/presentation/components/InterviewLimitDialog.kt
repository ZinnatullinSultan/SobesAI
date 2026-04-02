package com.example.sobesai.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.limit_dialog_cancel
import sobesai.composeapp.generated.resources.limit_dialog_message
import sobesai.composeapp.generated.resources.limit_dialog_title
import sobesai.composeapp.generated.resources.limit_dialog_upgrade

@Composable
fun InterviewLimitDialog(
    interviewsUsed: Int,
    interviewsLimit: Int,
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.limit_dialog_title),
                style = AppTypography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        Res.string.limit_dialog_message,
                        interviewsUsed,
                        interviewsLimit
                    ),
                    style = AppTypography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            AppButton(
                text = stringResource(Res.string.limit_dialog_upgrade),
                onClick = onUpgrade
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.limit_dialog_cancel),
                    style = AppTypography.labelMedium
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewInterviewLimitDialog() {
    MaterialTheme {
        InterviewLimitDialog(
            interviewsUsed = 3,
            interviewsLimit = 3,
            onDismiss = {},
            onUpgrade = {}
        )
    }
}
