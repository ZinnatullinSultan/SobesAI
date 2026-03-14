package com.example.sobesai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.sobesai.navigation.LoginRoute
import com.example.sobesai.navigation.MainRoute
import com.example.sobesai.navigation.SpecializationRoute
import com.example.sobesai.navigation.WelcomeRoute
import com.example.sobesai.presentation.MainViewModel
import com.example.sobesai.presentation.Specialization.SpecializationScreen
import com.example.sobesai.presentation.login.LoginScreen
import com.example.sobesai.presentation.main.MainScreen
import com.example.sobesai.presentation.theme.AppTheme
import com.example.sobesai.presentation.welcome.WelcomeScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.appState.collectAsState()

    AppTheme {
        if (state is MainViewModel.AppState.Loading) {
            return@AppTheme
        }

        val navController = rememberNavController()

        val startDestination = remember {
            when (state) {
                is MainViewModel.AppState.OnBoarding -> WelcomeRoute
                is MainViewModel.AppState.Login -> LoginRoute
                else -> MainRoute
            }
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable<WelcomeRoute> {
                WelcomeScreen(
                    onNavigateToLogin = {
                        navController.navigate(LoginRoute) {
                            popUpTo(WelcomeRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<LoginRoute> {
                LoginScreen(
                    onNavigateToMain = {
                        navController.navigate(MainRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<MainRoute> {
                MainScreen(
                    onSpecializationClick = { id ->
                        navController.navigate(SpecializationRoute(id))
                    }
                )
            }
            composable<SpecializationRoute> { backStackEntry ->
                val route: SpecializationRoute = backStackEntry.toRoute()

                SpecializationScreen(
                    id = route.id,
                    onBackClick = { navController.popBackStack() },
                    onStartInterview = { id, level ->
                        // В будущем здесь будет навигация в чат
                    }
                )
            }
        }
    }
}
