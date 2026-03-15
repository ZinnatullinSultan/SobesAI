package com.example.sobesai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "specializations")
data class SpecializationEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String,
    val isPinned: Boolean = false,
    val pinOrder: Int? = null,
    val imageUrl: String? = null,
    val cachedAt: Long
)
