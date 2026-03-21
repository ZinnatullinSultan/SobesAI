package com.example.sobesai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sobesai.data.local.dao.InterviewDao
import com.example.sobesai.data.local.dao.SpecializationDao
import com.example.sobesai.data.local.entity.ChatMessageEntity
import com.example.sobesai.data.local.entity.SpecializationEntity

@Database(
    entities = [SpecializationEntity::class, ChatMessageEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun specializationDao(): SpecializationDao
    abstract fun interviewDao(): InterviewDao

    companion object {
        const val DATABASE_NAME = "sobesai_database"
    }
}
