package com.example.sobesai.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sobesai.data.local.database.entity.SpecializationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecializationDao {

    @Query("SELECT * FROM specializations ORDER BY isPinned DESC, pinOrder DESC, id ASC")
    fun getAllSpecializations(): Flow<List<SpecializationEntity>>

    @Query("SELECT * FROM specializations WHERE id = :id")
    suspend fun getSpecializationById(id: Long): SpecializationEntity?

    @Query("SELECT * FROM specializations WHERE title LIKE '%' || :query || '%' ORDER BY isPinned DESC, pinOrder DESC, id ASC")
    suspend fun searchSpecializations(query: String): List<SpecializationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecializations(specializations: List<SpecializationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecialization(specialization: SpecializationEntity)

    @Query("DELETE FROM specializations")
    suspend fun deleteAllSpecializations()

    @Query("SELECT * FROM specializations ORDER BY isPinned DESC, pinOrder DESC, id ASC LIMIT :limit OFFSET :offset")
    suspend fun getSpecializationsPaginated(offset: Int, limit: Int): List<SpecializationEntity>
}
