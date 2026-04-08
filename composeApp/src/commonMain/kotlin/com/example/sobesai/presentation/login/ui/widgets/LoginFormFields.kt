package com.example.sobesai.presentation.login.ui.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.sobesai.presentation.login.model.LoginUiState
import com.example.sobesai.presentation.login.ui.widgets.fields.EmailTextField
import com.example.sobesai.presentation.login.ui.widgets.fields.NameTextField
import com.example.sobesai.presentation.login.ui.widgets.fields.PasswordTextField
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.TextError

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

    EmailTextField(
        state = state,
        onUsernameChanged = onUsernameChanged,
        onFocusNext = { focusManager.moveFocus(FocusDirection.Down) }
    )
    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
    if (state.isRegisterMode) {
        NameTextField(
            state = state,
            onDisplayNameChanged = onDisplayNameChanged,
            onFocusNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    }
    Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Tiny))
    PasswordTextField(
        state = state,
        onPasswordChanged = onPasswordChanged,
        onLoginClicked = onLoginClicked,
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
        keyboardController = keyboardController
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
