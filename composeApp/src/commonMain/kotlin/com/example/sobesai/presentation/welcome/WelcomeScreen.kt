package com.example.sobesai.presentation.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.example.sobesai.presentation.components.AppButton
import com.example.sobesai.presentation.theme.AppDimens
import com.example.sobesai.presentation.theme.AppTypography
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.app_description
import sobesai.composeapp.generated.resources.app_title
import sobesai.composeapp.generated.resources.start_button
import sobesai.composeapp.generated.resources.welcome_image
import sobesai.composeapp.generated.resources.welcome_image_desc

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = koinViewModel()
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight
        val imageSize = if (isLandscape) AppDimens.Components.WelcomeImageSizeSmall
        else AppDimens.Components.WelcomeImageSize

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = AppDimens.Padding.Large),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.app_title).uppercase(),
                    style = AppTypography.displayLarge
                )

                AsyncImage(
                    model = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
                    contentDescription = stringResource(Res.string.welcome_image_desc),
                    placeholder = painterResource(Res.drawable.welcome_image),
                    error = painterResource(Res.drawable.welcome_image),
                    modifier = Modifier.size(imageSize)
                )

                Text(
                    text = stringResource(Res.string.app_description),
                    style = AppTypography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                AppButton(
                    text = stringResource(Res.string.start_button),
                    onClick = {
                        viewModel.onStartClicked()
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewWelcome() {
    WelcomeScreen()
}