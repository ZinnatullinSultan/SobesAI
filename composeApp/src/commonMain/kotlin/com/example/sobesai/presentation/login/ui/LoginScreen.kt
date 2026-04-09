package com.example.sobesai.presentation.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobesai.core.auth.rememberAuthManager
import com.example.sobesai.presentation.login.model.LoginUiEvent
import com.example.sobesai.presentation.login.model.LoginUiState
import com.example.sobesai.presentation.login.LoginViewModel
import com.example.sobesai.presentation.login.ui.widgets.LoginActions
import com.example.sobesai.presentation.login.ui.widgets.LoginFormFields
import com.example.sobesai.presentation.login.ui.widgets.LoginHeader
import com.example.sobesai.presentation.theme.AppDimens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val authManager = rememberAuthManager()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.StartOAuthEvent -> {
                    authManager.startOAuthFlow(event.provider)
                }

                is LoginUiEvent.LoginSuccessEvent -> {}
            }
        }
    }
    LoginScreenContent(
        state = state,
        onUsernameChanged = { viewModel.onUsernameChanged(it) },
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onLoginClicked = { viewModel.onLoginClicked() },
        onGitHubLoginClicked = { viewModel.onGitHubLoginClicked() },
        onToggleMode = { viewModel.toggleMode() },
        onDisplayNameChanged = { viewModel.onDisplayNameChanged(it) }
    )
}

@Composable
private fun LoginScreenContent(
    state: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onGitHubLoginClicked: () -> Unit,
    onToggleMode: () -> Unit,
    onDisplayNameChanged: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = AppDimens.Padding.Large)
                .verticalScroll(scrollState)
                .imePadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoginHeader()
            LoginFormFields(
                state = state,
                onUsernameChanged = onUsernameChanged,
                onPasswordChanged = onPasswordChanged,
                onLoginClicked = onLoginClicked,
                onDisplayNameChanged = onDisplayNameChanged
            )
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))
            LoginActions(
                state = state,
                onLoginClicked = onLoginClicked,
                onGitHubLoginClicked = onGitHubLoginClicked,
                onToggleMode = onToggleMode
            )
        }
    }
}

@Preview(locale = "en")
@Composable
fun PreviewLoginScreen() {
    LoginScreenContent(
        state = LoginUiState(
            username = "test_user",
            password = "password123"
        ),
        onUsernameChanged = {},
        onPasswordChanged = {},
        onLoginClicked = {},
        onGitHubLoginClicked = {},
        onDisplayNameChanged = {},
        onToggleMode = {}
    )
}
