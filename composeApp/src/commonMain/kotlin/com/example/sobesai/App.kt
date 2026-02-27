package com.example.sobesai

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sobesai.presentation.login.LoginScreen
import com.example.sobesai.presentation.login.LoginViewModel
import com.example.sobesai.presentation.main.MainScreen
import com.example.sobesai.presentation.theme.AppTheme
import com.example.sobesai.presentation.welcome.WelcomeScreen

@Preview
@Composable
fun App() {
    AppTheme {
        Scaffold { padding ->
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "welcome", modifier = Modifier.padding(padding)) {

                composable("welcome") {
                    WelcomeScreen(
                        onNavigateToLogin = { navController.navigate("login") }
                    )
                }
                composable("login") {
                    val loginViewModel = remember { LoginViewModel() }
                    LoginScreen(viewModel = loginViewModel, onNavigateToMain = {
                        navController.navigate("main"){
                            popUpTo("welcome") { inclusive = true }
                        }
                        })
                }
                composable("main") {
                    MainScreen()
                }
            }
        }
    }
}