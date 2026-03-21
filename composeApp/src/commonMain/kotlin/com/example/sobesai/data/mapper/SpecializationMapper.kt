package com.example.sobesai.data.mapper

import com.example.sobesai.data.local.entity.SpecializationEntity
import com.example.sobesai.data.remote.dto.SpecializationDto
import com.example.sobesai.domain.model.Specialization
import kotlin.time.Clock

fun SpecializationDto.toDomain(): Specialization {
    return Specialization(
        id = id,
        title = title,
        description = description ?: "",
        isPinned = isPinned,
        pinOrder = pinOrder
    )
}

fun SpecializationEntity.toDomain(): Specialization {
    return Specialization(
        id = id,
        title = title,
        description = description,
        isPinned = isPinned,
        pinOrder = pinOrder
    )
}

fun Specialization.toEntity(imageUrl: String? = null): SpecializationEntity {
    return SpecializationEntity(
        id = id,
        title = title,
        description = description,
        isPinned = isPinned,
        pinOrder = pinOrder,
        imageUrl = imageUrl,
        cachedAt = Clock.System.now().toEpochMilliseconds()
    )
}
