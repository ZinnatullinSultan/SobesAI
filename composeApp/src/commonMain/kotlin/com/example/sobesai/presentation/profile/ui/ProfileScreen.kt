package com.example.sobesai.presentation.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sobesai.presentation.profile.ProfileUiState
import com.example.sobesai.presentation.profile.ProfileViewModel
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import com.example.sobesai.presentation.widgets.AppButton
import com.example.sobesai.presentation.widgets.AppTopBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.profile_login_label
import sobesai.composeapp.generated.resources.profile_quit_button
import sobesai.composeapp.generated.resources.profile_screen_title
import sobesai.composeapp.generated.resources.profile_user_name_default

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreenContent(
        state = state,
        onBackClick = onBackClick,
        logout = { viewModel.logout() }
    )
}

@Composable
private fun ProfileScreenContent(
    state: ProfileUiState,
    onBackClick: () -> Unit,
    logout: () -> Unit
) {
    val defaultUserName = stringResource(Res.string.profile_user_name_default)
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AppTopBar(
                onBackClick = onBackClick,
                onProfileClick = null,
                onClearClick = null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = AppDimens.Padding.Normal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(Res.string.profile_screen_title),
                style = AppTypography.displaySmall
            )
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.ExtraLarge))
            Card(
                modifier = Modifier
                    .widthIn(max = AppDimens.Components.TextFieldMaxWidth)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Column(modifier = Modifier.padding(AppDimens.Padding.Normal)) {
                    Text(
                        text = stringResource(Res.string.profile_login_label) + (state.displayName
                            ?: defaultUserName)
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppDimens.SpacerHeight.Normal))
            AppButton(
                text = stringResource(Res.string.profile_quit_button),
                onClick = logout
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreenContent(
        state = ProfileUiState(displayName = "Иван Иванов"),
        onBackClick = {},
        logout = {}
    )
}
