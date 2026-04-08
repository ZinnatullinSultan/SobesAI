package com.example.sobesai.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecializationDto(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String?,
    @SerialName("is_pinned")
    val isPinned: Boolean = false,
    @SerialName("pin_order")
    val pinOrder: Int? = null,
    @SerialName("image_url")
    val imageUrl: String? = null
)
