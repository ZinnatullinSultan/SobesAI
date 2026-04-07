package com.example.sobesai.presentation.main.ui.widgets.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.main_refresh_button

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = AppTypography.titleSmall
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = AppDimens.Padding.Normal)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(AppDimens.SpacerHeight.ExtraTiny))
            Text(stringResource(Res.string.main_refresh_button))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewErrorState() {
    ErrorState(
        message = "error message",
        onRetry = {}
    )
}
