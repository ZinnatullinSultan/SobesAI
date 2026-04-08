package com.example.sobesai.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePinDto(
    @SerialName("is_pinned") val isPinned: Boolean,
    @SerialName("pin_order") val pinOrder: Int?
)
