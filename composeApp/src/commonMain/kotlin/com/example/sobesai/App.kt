package com.example.sobesai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sobesai.navigation.LoginRoute
import com.example.sobesai.navigation.MainRoute
import com.example.sobesai.navigation.WelcomeRoute
import com.example.sobesai.presentation.login.LoginScreen
import com.example.sobesai.presentation.login.LoginViewModel
import com.example.sobesai.presentation.main.MainScreen
import com.example.sobesai.presentation.theme.AppTheme
import com.example.sobesai.presentation.welcome.WelcomeScreen

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = WelcomeRoute,
        ) {

            composable<WelcomeRoute> {
                WelcomeScreen(
                    onNavigateToLogin = { navController.navigate(LoginRoute) }
                )
            }
            composable<LoginRoute> {
                val loginViewModel = remember { LoginViewModel() }
                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToMain = {
                        navController.navigate(MainRoute) {
                            popUpTo(WelcomeRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<MainRoute> {
                MainScreen()
            }
        }

    }
}