package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.ExcludedFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExcludedFolderDao {
    @Query("SELECT * FROM excluded_folders")
    fun getAllExcludedFolders(): Flow<List<ExcludedFolderEntity>>

    @Query("SELECT folderPath FROM excluded_folders")
    suspend fun getExcludedFolderPaths(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExcludedFolder(folder: ExcludedFolderEntity)

    @Query("DELETE FROM excluded_folders WHERE folderPath = :folderPath")
    suspend fun deleteExcludedFolder(folderPath: String)
}
