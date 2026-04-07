package com.example.sobesai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.sobesai.domain.model.AppState
import com.example.sobesai.navigation.AppRoutes
import com.example.sobesai.presentation.MainViewModel
import com.example.sobesai.presentation.interview.ui.InterviewScreen
import com.example.sobesai.presentation.login.ui.LoginScreen
import com.example.sobesai.presentation.main.ui.MainScreen
import com.example.sobesai.presentation.profile.ui.ProfileScreen
import com.example.sobesai.presentation.specialization.ui.SpecializationScreen
import com.example.sobesai.presentation.theme.AppTheme
import com.example.sobesai.presentation.welcome.ui.WelcomeScreen
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel

private const val LOG_TAG_NAVIGATION = "APP_NAVIGATION"

@Composable
fun App(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.appState.collectAsState()
    val startDestination by viewModel.startDestination.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(state) {
        Napier.d(tag = LOG_TAG_NAVIGATION) { "LaunchedEffect triggered, state=$state" }
        val currentRoute = navController.currentDestination?.route
        when (state) {
            is AppState.Login -> {
                Napier.d(tag = LOG_TAG_NAVIGATION) { "Navigating to Login" }
                if (currentRoute != AppRoutes.LoginRoute::class.qualifiedName) {
                    navController.navigate(AppRoutes.LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            is AppState.Main -> {
                val shouldResetToMain = currentRoute == null ||
                        currentRoute == AppRoutes.LoginRoute::class.qualifiedName ||
                        currentRoute == AppRoutes.WelcomeRoute::class.qualifiedName

                if (shouldResetToMain) {
                    Napier.d(tag = LOG_TAG_NAVIGATION) { "Navigating to Main" }
                    navController.navigate(AppRoutes.MainRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            is AppState.OnBoarding -> {
                Napier.d(tag = LOG_TAG_NAVIGATION) { "Navigating to Welcome" }
                if (currentRoute != AppRoutes.WelcomeRoute::class.qualifiedName) {
                    navController.navigate(AppRoutes.WelcomeRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            else -> {}
        }
    }

    AppTheme {
        val destination = startDestination ?: return@AppTheme

        NavHost(
            navController = navController,
            startDestination = destination,
        ) {
            composable<AppRoutes.WelcomeRoute> {
                WelcomeScreen()
            }
            composable<AppRoutes.LoginRoute> {
                LoginScreen()
            }
            composable<AppRoutes.MainRoute> {
                MainScreen(
                    onSpecializationClick = { id ->
                        navController.navigate(AppRoutes.SpecializationRoute(id))
                    },
                    onProfileClick = {
                        navController.navigate(AppRoutes.ProfileRoute)
                    }
                )
            }
            composable<AppRoutes.ProfileRoute> {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<AppRoutes.SpecializationRoute> { backStackEntry ->
                val route: AppRoutes.SpecializationRoute = backStackEntry.toRoute()
                SpecializationScreen(
                    id = route.id,
                    onBackClick = { navController.popBackStack() },
                    onProfileClick = {
                        navController.navigate(AppRoutes.ProfileRoute)
                    },
                    onStartInterview = { id, level ->
                        navController.navigate(AppRoutes.InterviewRoute(id, level.name))
                    }
                )
            }
            composable<AppRoutes.InterviewRoute> { backStackEntry ->
                val route: AppRoutes.InterviewRoute = backStackEntry.toRoute()
                InterviewScreen(
                    specId = route.specId,
                    difficulty = route.difficulty,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
