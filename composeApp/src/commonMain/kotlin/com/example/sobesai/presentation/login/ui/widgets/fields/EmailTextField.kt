package com.example.sobesai.presentation.login.ui.widgets.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.sobesai.presentation.login.model.LoginUiState
import com.example.sobesai.presentation.theme.AppDimens
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.email_label
import sobesai.composeapp.generated.resources.email_placeholder

@Composable
fun EmailTextField(
    state: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onFocusNext: () -> Unit
) {
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
        keyboardActions = KeyboardActions(onNext = { onFocusNext() })
    )
}
