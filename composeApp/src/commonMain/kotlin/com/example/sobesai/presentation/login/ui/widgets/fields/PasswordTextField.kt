package com.example.sobesai.presentation.login.ui.widgets.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.sobesai.presentation.login.model.LoginUiState
import com.example.sobesai.presentation.theme.AppDimens
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.login_password_hide
import sobesai.composeapp.generated.resources.login_password_show
import sobesai.composeapp.generated.resources.password_label
import sobesai.composeapp.generated.resources.password_placeholder

@Composable
fun PasswordTextField(
    state: LoginUiState,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?
) {
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
            IconButton(onClick = onPasswordVisibilityToggle) {
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
}
