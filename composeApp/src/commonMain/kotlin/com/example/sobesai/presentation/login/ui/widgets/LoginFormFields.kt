package com.example.sobesai.presentation.login.ui.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.sobesai.presentation.login.model.LoginUiState
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.TextError
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.email_label
import sobesai.composeapp.generated.resources.email_placeholder
import sobesai.composeapp.generated.resources.login_password_hide
import sobesai.composeapp.generated.resources.login_password_show
import sobesai.composeapp.generated.resources.name_label
import sobesai.composeapp.generated.resources.name_placeholder
import sobesai.composeapp.generated.resources.password_label
import sobesai.composeapp.generated.resources.password_placeholder

@Composable
fun LoginFormFields(
    state: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onDisplayNameChanged: (String) -> Unit,
    onLoginClicked: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = state.username,
        onValueChange = onUsernameChanged,
        label = { Text(stringResource(Res.string.email_label)) },
        placeholder = {
            Text(stringResource(Res.string.email_placeholder))
        },
        modifier = Modifier
            .widthIn(max = AppDimens.Components.TextFieldMaxWidth)
            .fillMaxWidth(),
        singleLine = true,
        isError = state.error != null,
        enabled = !state.isLoading,
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
    if (state.isRegisterMode) {
        OutlinedTextField(
            value = state.displayName,
            onValueChange = onDisplayNameChanged,
            label = { Text(stringResource(Res.string.name_label)) },
            placeholder = {
                Text(stringResource(Res.string.name_placeholder))
            },
            modifier = Modifier
                .widthIn(max = AppDimens.Components.TextFieldMaxWidth)
                .fillMaxWidth(),
            singleLine = true,
            isError = state.error != null,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
    }
    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
    OutlinedTextField(
        value = state.password,
        onValueChange = onPasswordChanged,
        label = { Text(stringResource(Res.string.password_label)) },
        placeholder = {
            Text(stringResource(Res.string.password_placeholder))
        },
        modifier = Modifier
            .widthIn(max = AppDimens.Components.TextFieldMaxWidth)
            .fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = state.error != null,
        enabled = !state.isLoading,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                onLoginClicked()
            }
        ),
        trailingIcon = {
            IconButton(onClick = {
                isPasswordVisible = !isPasswordVisible
            }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (isPasswordVisible) {
                        stringResource(Res.string.login_password_hide)
                    } else {
                        stringResource(Res.string.login_password_show)
                    }
                )
            }
        }
    )
    if (state.error != null) {
        AuthHintMessage(
            message = state.error,
            color = TextError
        )
    }
    if (state.successMessage != null) {
        AuthHintMessage(
            message = state.successMessage,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
