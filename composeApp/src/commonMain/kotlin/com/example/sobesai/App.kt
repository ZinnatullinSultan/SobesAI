package com.example.sobesai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.sobesai.navigation.InterviewRoute
import com.example.sobesai.navigation.LoginRoute
import com.example.sobesai.navigation.MainRoute
import com.example.sobesai.navigation.ProfileRoute
import com.example.sobesai.navigation.SpecializationRoute
import com.example.sobesai.navigation.WelcomeRoute
import com.example.sobesai.presentation.MainViewModel
import com.example.sobesai.presentation.interview.InterviewScreen
import com.example.sobesai.presentation.login.LoginScreen
import com.example.sobesai.presentation.main.MainScreen
import com.example.sobesai.presentation.profile.ProfileScreen
import com.example.sobesai.presentation.specialization.SpecializationScreen
import com.example.sobesai.presentation.theme.AppTheme
import com.example.sobesai.presentation.welcome.WelcomeScreen
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.appState.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(state) {
        Napier.d(tag = "APP_NAVIGATION") { "LaunchedEffect triggered, state=$state" }
        val currentRoute = navController.currentDestination?.route

        when (state) {
            is MainViewModel.AppState.Login -> {
                Napier.d(tag = "APP_NAVIGATION") { "Navigating to Login" }
                if (currentRoute != LoginRoute::class.qualifiedName) {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            is MainViewModel.AppState.Main -> {
                val shouldResetToMain = currentRoute == null ||
                        currentRoute == LoginRoute::class.qualifiedName ||
                        currentRoute == WelcomeRoute::class.qualifiedName

                if (shouldResetToMain) {
                    Napier.d(tag = "APP_NAVIGATION") { "Navigating to Main" }
                    navController.navigate(MainRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            is MainViewModel.AppState.OnBoarding -> {
                Napier.d(tag = "APP_NAVIGATION") { "Navigating to Welcome" }
                if (currentRoute != WelcomeRoute::class.qualifiedName) {
                    navController.navigate(WelcomeRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            else -> {}
        }
    }

    AppTheme {
        if (state is MainViewModel.AppState.Loading) {
            return@AppTheme
        }

        val startDestination = when (state) {
            is MainViewModel.AppState.OnBoarding -> WelcomeRoute
            is MainViewModel.AppState.Login -> LoginRoute
            else -> MainRoute
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable<WelcomeRoute> {
                WelcomeScreen()
            }
            composable<LoginRoute> {
                LoginScreen()
            }
            composable<MainRoute> {
                MainScreen(
                    onSpecializationClick = { id ->
                        navController.navigate(SpecializationRoute(id))
                    },
                    onProfileClick = {
                        navController.navigate(ProfileRoute)
                    }
                )
            }
            composable<ProfileRoute> {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<SpecializationRoute> { backStackEntry ->
                val route: SpecializationRoute = backStackEntry.toRoute()

                SpecializationScreen(
                    id = route.id,
                    onBackClick = { navController.popBackStack() },
                    onProfileClick = {
                        navController.navigate(ProfileRoute)
                    },
                    onStartInterview = { id, level ->
                        navController.navigate(InterviewRoute(id, level.name))
                    }
                )
            }
            composable<InterviewRoute> { backStackEntry ->
                val route: InterviewRoute = backStackEntry.toRoute()
                InterviewScreen(
                    specId = route.specId,
                    difficulty = route.difficulty,
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}
