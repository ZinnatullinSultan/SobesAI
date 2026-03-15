package com.example.sobesai.navigation

import kotlinx.serialization.Serializable

@Serializable
object WelcomeRoute

@Serializable
object LoginRoute

@Serializable
object MainRoute

@Serializable
data class SpecializationRoute(val id: Long)

@Serializable
object ProfileRoute