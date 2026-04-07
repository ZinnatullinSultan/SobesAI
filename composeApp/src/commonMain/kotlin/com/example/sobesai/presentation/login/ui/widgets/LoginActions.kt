package com.example.sobesai.presentation.login.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import com.example.sobesai.presentation.login.model.LoginUiState
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.widgets.AppButton
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.login_already_registered
import sobesai.composeapp.generated.resources.login_auth_button
import sobesai.composeapp.generated.resources.login_button_git
import sobesai.composeapp.generated.resources.login_not_registered
import sobesai.composeapp.generated.resources.login_register_button

@Composable
fun LoginActions(
    state: LoginUiState,
    onLoginClicked: () -> Unit,
    onGitHubLoginClicked: () -> Unit,
    onToggleMode: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(
            text = if (state.isRegisterMode) stringResource(Res.string.login_register_button) else
                stringResource(Res.string.login_auth_button),
            onClick = onLoginClicked,
            enabled = state.isLoginButtonActive,
            isLoading = state.isLoading
        )
        Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Small))
        OutlinedButton(
            onClick = onGitHubLoginClicked,
            modifier = Modifier
                .widthIn(max = AppDimens.Components.ButtonMaxWidth)
                .fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(stringResource(Res.string.login_button_git))
        }
        Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.ExtraTiny))
        TextButton(onClick = onToggleMode) {
            Text(
                text = if (state.isRegisterMode)
                    stringResource(Res.string.login_already_registered)
                else
                    stringResource(Res.string.login_not_registered),
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
