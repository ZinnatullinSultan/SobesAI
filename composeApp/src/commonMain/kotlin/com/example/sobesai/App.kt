package com.example.sobesai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage

@Preview(showSystemUi = true)
@Composable
fun App() {
    MaterialTheme {
        Scaffold { padding ->
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "welcome", modifier = Modifier.background(BackgroundLight).padding(padding)) {
                composable("welcome") {
                    WelcomeScreen(
                        onNavigateToLogin = { navController.navigate("login") }
                    )
                }
                composable("login") {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = AppStrings.appTitle.uppercase(),
            style = TextStyle(
                fontSize = 62.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                brush = Brush.linearGradient(colors = TitleGradient)
            )
        )

        AsyncImage(
            model = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png",
            contentDescription = AppStrings.welcomeImageDesc,
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = AppStrings.appDescription,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = TextColorDark
        )

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(AppStrings.startButton, fontSize = 20.sp)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreen() {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(
            rememberScrollState()
        ),
        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = AppStrings.appTitle.uppercase(),
            fontFamily = DancingScriptFontFamily,
            style = TextStyle(
                fontSize = 42.sp,
                letterSpacing = 1.sp,
                brush = Brush.linearGradient(colors = TitleGradient)
            )
        )
        Spacer(modifier = Modifier.height(26.dp))
        Text(
            text = AppStrings.loginTitle,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(AppStrings.emailLabel) },
            placeholder = { Text(AppStrings.emailPlaceholder) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(AppStrings.passwordLabel) },
            placeholder = { Text(AppStrings.passwordPlaceholder) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(AppStrings.loginButton, fontSize = 20.sp)
        }
    }
}