package com.example.sobesai.presentation.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.sobesai.presentation.theme.DancingScriptFontFamily
import com.example.sobesai.presentation.theme.TitleGradient
import org.jetbrains.compose.resources.stringResource
import sobesai.composeapp.generated.resources.Res
import sobesai.composeapp.generated.resources.app_description
import sobesai.composeapp.generated.resources.app_title
import sobesai.composeapp.generated.resources.start_button
import sobesai.composeapp.generated.resources.welcome_image_desc


@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.app_title).uppercase(),
            style = TextStyle(
                fontSize = 62.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = DancingScriptFontFamily,
                letterSpacing = 1.sp,
                brush = Brush.linearGradient(colors = TitleGradient)
            )
        )

        AsyncImage(
            model = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
            contentDescription = stringResource(Res.string.welcome_image_desc),
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = stringResource(Res.string.app_description),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Button(
            onClick = onNavigateToLogin,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                disabledContentColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(stringResource(Res.string.start_button), fontSize = 20.sp)
        }
    }
}