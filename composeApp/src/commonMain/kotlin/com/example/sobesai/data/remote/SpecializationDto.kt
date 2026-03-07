package com.example.sobesai.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecializationDto(
    val id: Long,
    val title: String,
    val description: String?,
    @SerialName("is_pinned")
    val isPinned: Boolean = false,
    @SerialName("image_url")
    val imageUrl: String? = null
)

