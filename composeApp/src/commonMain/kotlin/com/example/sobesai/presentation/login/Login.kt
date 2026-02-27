package com.example.sobesai.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sobesai.presentation.theme.DancingScriptFontFamily
import com.example.sobesai.presentation.theme.TitleGradient
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.app_title
import sobesai.composeapp.generated.resources.email_label
import sobesai.composeapp.generated.resources.email_placeholder
import sobesai.composeapp.generated.resources.login_button
import sobesai.composeapp.generated.resources.login_error_text
import sobesai.composeapp.generated.resources.login_title
import sobesai.composeapp.generated.resources.main_title
import sobesai.composeapp.generated.resources.password_label
import sobesai.composeapp.generated.resources.password_placeholder

@Composable
fun LoginScreen(viewModel: LoginViewModel, onNavigateToMain: () -> Unit) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit){
        viewModel.events.collect{ event ->
            when(event){
                is LoginUiEvent.LoginSuccessEvent -> onNavigateToMain()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(
            rememberScrollState()
        ),
        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.app_title),
            fontFamily = DancingScriptFontFamily,
            style = TextStyle(
                fontSize = 42.sp,
                letterSpacing = 1.sp,
                brush = Brush.linearGradient(colors = TitleGradient)
            )
        )
        Spacer(modifier = Modifier.height(26.dp))
        Text(
            text = stringResource(Res.string.login_title),
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onUsernameChanged(it) },
            label = { Text(stringResource(Res.string.email_label)) },
            placeholder = { Text(stringResource(Res.string.email_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text(stringResource(Res.string.password_label)) },
            placeholder = { Text(stringResource(Res.string.password_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.error != null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if(state.error != null){
            Text(
                text = state.error ?: stringResource(Res.string.login_error_text),
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 8.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onLoginClicked()},
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = state.isLoginButtonActive
        ) {
            Text(stringResource(Res.string.login_button), fontSize = 20.sp)
        }
    }
}