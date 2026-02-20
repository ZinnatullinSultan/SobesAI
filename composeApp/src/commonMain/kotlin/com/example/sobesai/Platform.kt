package com.example.sobesai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform