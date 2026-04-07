package com.example.sobesai.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sobesai.data.local.database.dao.InterviewDao
import com.example.sobesai.data.local.database.dao.SpecializationDao
import com.example.sobesai.data.local.database.entity.ChatMessageEntity
import com.example.sobesai.data.local.database.entity.SpecializationEntity

@Database(
    entities = [SpecializationEntity::class, ChatMessageEntity::class],
    version = AppDatabase.DATABASE_VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun specializationDao(): SpecializationDao
    abstract fun interviewDao(): InterviewDao

    companion object {
        const val DATABASE_NAME = "sobesai_database"
        const val DATABASE_VERSION = 2
    }
}
