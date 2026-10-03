package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ConversionHistoryItem

@Entity(tableName = "conversion_history")
data class ConversionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalTitle: String,
    val originalPath: String,
    val outputFilename: String,
    val outputPath: String,
    val spatialMode: String,
    val presetName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L,
    val format: String = "WAV",
    val fileSizeBytes: Long = 0L
) {
    fun toItem(): ConversionHistoryItem {
        return ConversionHistoryItem(
            id = id,
            originalTitle = originalTitle,
            originalPath = originalPath,
            outputFilename = outputFilename,
            outputPath = outputPath,
            spatialMode = spatialMode,
            presetName = presetName,
            timestamp = timestamp,
            durationMs = durationMs,
            format = format,
            fileSizeBytes = fileSizeBytes
        )
    }

    companion object {
        fun fromItem(item: ConversionHistoryItem): ConversionHistoryEntity {
            return ConversionHistoryEntity(
                id = item.id,
                originalTitle = item.originalTitle,
                originalPath = item.originalPath,
                outputFilename = item.outputFilename,
                outputPath = item.outputPath,
                spatialMode = item.spatialMode,
                presetName = item.presetName,
                timestamp = item.timestamp,
                durationMs = item.durationMs,
                format = item.format,
                fileSizeBytes = item.fileSizeBytes
            )
        }
    }
}
