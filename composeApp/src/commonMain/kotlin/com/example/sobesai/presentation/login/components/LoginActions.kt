package com.example.sobesai.presentation.login.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sobesai.presentation.components.AppButton
import com.example.sobesai.presentation.login.LoginUiState
import com.example.sobesai.presentation.theme.AppDimens
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.login_button
import sobesai.composeapp.generated.resources.login_button_git

@Composable
fun LoginActions(
    onLoginClicked: () -> Unit,
    onGitHubLoginClicked: () -> Unit,
    state: LoginUiState
) {
    AppButton(
        onClick = onLoginClicked,
        enabled = state.isLoginButtonActive,
        text = stringResource(Res.string.login_button)
    )

    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))

    OutlinedButton(
        onClick = onGitHubLoginClicked,
        modifier = Modifier
            .widthIn(max = AppDimens.Components.ButtonMaxWidth)
            .fillMaxWidth()
    ) {
        Text(stringResource(Res.string.login_button_git))
    }
}