package com.example.sobesai.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.sobesai.core.rememberAuthManager
import com.example.sobesai.presentation.login.components.LoginActions
import com.example.sobesai.presentation.login.components.LoginFormFields
import com.example.sobesai.presentation.login.components.LoginHeader
import com.example.sobesai.presentation.theme.AppDimens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsState()
    val authManager = rememberAuthManager()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.StartOAuthEvent -> {
                    authManager.startOAuthFlow(event.provider)
                }

                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LoginHeader()
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = AppDimens.Padding.Large)
                            .verticalScroll(scrollState)
                            .imePadding()
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(AppDimens.Padding.Large),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            LoginFormFields(
                                onUsernameChanged = { viewModel.onUsernameChanged(it) },
                                onPasswordChanged = { viewModel.onPasswordChanged(it) },
                                onLoginClicked = { viewModel.onLoginClicked() },
                                state = state
                            )

                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LoginActions(
                                onLoginClicked = { viewModel.onLoginClicked() },
                                onGitHubLoginClicked = { viewModel.onGitHubLoginClicked() },
                                state = state
                            )
                        }
                    }
                }

            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AppDimens.Padding.Large)
                        .verticalScroll(scrollState)
                        .imePadding()
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LoginHeader()
                    LoginFormFields(
                        onUsernameChanged = { viewModel.onUsernameChanged(it) },
                        onPasswordChanged = { viewModel.onPasswordChanged(it) },
                        onLoginClicked = { viewModel.onLoginClicked() },
                        state = state
                    )

                    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))

                    LoginActions(
                        onLoginClicked = { viewModel.onLoginClicked() },
                        onGitHubLoginClicked = { viewModel.onGitHubLoginClicked() },
                        state = state
                    )
                }
            }
        }
    }
}