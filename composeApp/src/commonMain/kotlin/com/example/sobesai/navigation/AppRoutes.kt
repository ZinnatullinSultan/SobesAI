package com.example.sobesai.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoutes {
    @Serializable
    object WelcomeRoute : AppRoutes

    @Serializable
    object LoginRoute : AppRoutes

    @Serializable
    object MainRoute : AppRoutes

    @Serializable
    data class SpecializationRoute(val id: Long) : AppRoutes

    @Serializable
    object ProfileRoute : AppRoutes

    @Serializable
    data class InterviewRoute(val specId: Long, val difficulty: String) : AppRoutes

}
