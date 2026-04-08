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
import sobesai.composeapp.generated.resources.name_label
import sobesai.composeapp.generated.resources.name_placeholder

@Composable
fun NameTextField(
    state: LoginUiState,
    onDisplayNameChanged: (String) -> Unit,
    onFocusNext: () -> Unit
) {
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
        keyboardActions = KeyboardActions(onNext = { onFocusNext() })
    )
}
