package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.ConversionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionHistoryDao {
    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ConversionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: ConversionHistoryEntity): Long

    @Query("DELETE FROM conversion_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM conversion_history")
    suspend fun clearAllHistory()
}
