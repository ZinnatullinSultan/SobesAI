package com.example.sobesai.domain.model

data class Specialization(
    val id: Long,
    val title: String,
    val description: String,
    val isPinned: Boolean = false,
    val pinOrder: Int? = null
)
