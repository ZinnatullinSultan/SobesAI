package com.example.sobesai.data.mapper

import com.example.sobesai.data.local.database.entity.SpecializationEntity
import com.example.sobesai.data.remote.model.SpecializationDto
import com.example.sobesai.domain.model.Specialization
import kotlin.time.Clock

fun SpecializationDto.toDomain(): Specialization {
    return Specialization(
        id = id,
        title = title,
        description = description.orEmpty(),
        isPinned = isPinned,
        pinOrder = pinOrder,
        imageUrl = imageUrl.normalizeImageUrl()
    )
}

fun SpecializationEntity.toDomain(): Specialization {
    return Specialization(
        id = id,
        title = title,
        description = description,
        isPinned = isPinned,
        pinOrder = pinOrder,
        imageUrl = imageUrl.normalizeImageUrl()
    )
}

fun Specialization.toEntity(): SpecializationEntity {
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

private fun String?.normalizeImageUrl(): String? {
    if (this.isNullOrBlank()) return null
    val trimmed = this.trim()

    return when {
        trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
        else -> "https://$trimmed"
    }
}
