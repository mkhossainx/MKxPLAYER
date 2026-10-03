package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.SpatialPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpatialPresetDao {
    @Query("SELECT * FROM spatial_presets ORDER BY isSystemDefault DESC, id ASC")
    fun getAllPresets(): Flow<List<SpatialPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: SpatialPresetEntity): Long

    @Query("DELETE FROM spatial_presets WHERE id = :id AND isSystemDefault = 0")
    suspend fun deleteCustomPreset(id: Long)
}
