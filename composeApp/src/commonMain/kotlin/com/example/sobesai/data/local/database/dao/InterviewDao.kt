package com.example.sobesai.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sobesai.data.local.database.entity.ChatMessageEntity

@Dao
interface InterviewDao {
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE specId = :specId AND difficulty = :difficulty ORDER BY timestamp ASC")
    suspend fun getMessages(specId: Long, difficulty: String): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages WHERE specId = :specId AND difficulty = :difficulty")
    suspend fun clearHistory(specId: Long, difficulty: String)
}
