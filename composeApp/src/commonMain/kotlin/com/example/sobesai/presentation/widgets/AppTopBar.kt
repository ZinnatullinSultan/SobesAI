package com.example.sobesai.presentation.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobesai.presentation.theme.AppTypography
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.app_title
import sobesai.composeapp.generated.resources.interview_clear_icon_description

private const val BACKGROUND_ALPHA = 0.9f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onBackClick: () -> Unit,
    onProfileClick: (() -> Unit)?,
    onClearClick: (() -> Unit)?,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(Res.string.app_title),
                style = AppTypography.headlineLarge,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            if (onProfileClick != null) {
                ProfileIcon(
                    onProfileClick = onProfileClick,
                )
            }
            if (onClearClick != null) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.interview_clear_icon_description),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(BACKGROUND_ALPHA)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAppTopBar() {
    AppTopBar(
        onProfileClick = {},
        onBackClick = {},
        onClearClick = {}
    )
}
