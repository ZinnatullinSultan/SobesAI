package com.example.sobesai.presentation.login.ui.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.app_title
import sobesai.composeapp.generated.resources.login_title

@Composable
fun LoginHeader() {
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
}
