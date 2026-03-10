package com.example.sobesai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sobesai.data.local.TokenStorage
import com.example.sobesai.navigation.LoginRoute
import com.example.sobesai.navigation.MainRoute
import com.example.sobesai.navigation.WelcomeRoute
import com.example.sobesai.presentation.login.LoginScreen
import com.example.sobesai.presentation.main.MainScreen
import com.example.sobesai.presentation.theme.AppTheme
import com.example.sobesai.presentation.welcome.WelcomeScreen

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        val token by TokenStorage.token.collectAsState()
        LaunchedEffect(token) {
            if (token == null) {
                navController.navigate(WelcomeRoute) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
        val startDestination =
            remember { if (TokenStorage.getToken() != null) MainRoute else WelcomeRoute }
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {

            composable<WelcomeRoute> {
                WelcomeScreen(
                    onNavigateToLogin = { navController.navigate(LoginRoute) }
                )
            }
            composable<LoginRoute> {
                LoginScreen(
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