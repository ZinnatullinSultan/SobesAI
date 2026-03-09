package com.example.sobesai.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sobesai.presentation.components.AppButton
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.theme.TextError
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.app_title
import sobesai.composeapp.generated.resources.email_label
import sobesai.composeapp.generated.resources.email_placeholder
import sobesai.composeapp.generated.resources.login_button
import sobesai.composeapp.generated.resources.login_error_text
import sobesai.composeapp.generated.resources.login_password_hide
import sobesai.composeapp.generated.resources.login_password_show
import sobesai.composeapp.generated.resources.login_title
import sobesai.composeapp.generated.resources.password_label
import sobesai.composeapp.generated.resources.password_placeholder

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToMain: () -> Unit
) {
    val scrollState = rememberScrollState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.LoginSuccessEvent -> onNavigateToMain()
            }
        }
    }
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
            Text(
                text = stringResource(Res.string.app_title),
                style = AppTypography.displaySmall,
            )
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))
            Text(
                text = stringResource(Res.string.login_title),
                style = AppTypography.titleLarge
            )
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))
            OutlinedTextField(
                value = state.username,
                onValueChange = { viewModel.onUsernameChanged(it) },
                label = {
                    Text(stringResource(Res.string.email_label))
                },
                placeholder = {
                    Text(stringResource(Res.string.email_placeholder))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.error != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = {
                    Text(stringResource(Res.string.password_label))
                },
                placeholder = {
                    Text(stringResource(Res.string.password_placeholder))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.error != null,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        viewModel.onLoginClicked()
                    }
                ),
                trailingIcon = {
                    val image = if (isPasswordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }

                    val description = if (isPasswordVisible) {
                        stringResource(Res.string.login_password_hide)
                    } else {
                        stringResource(Res.string.login_password_show)
                    }

                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = description
                        )
                    }
                }
            )
            if (state.error != null) {
                Text(
                    text = state.error ?: stringResource(Res.string.login_error_text),
                    color = TextError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppDimens.Padding.Small, start = AppDimens.Padding.Small),
                    style = AppTypography.labelSmall,
                )
            }

            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))

            AppButton(
                onClick = { viewModel.onLoginClicked() },
                enabled = state.isLoginButtonActive,
                text = stringResource(Res.string.login_button)
            )

        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        viewModel = LoginViewModel(),
        onNavigateToMain = {}
    )
}