package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.SpatialMode
import com.example.model.SpatialParams

@Entity(tableName = "spatial_presets")
data class SpatialPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mode: String,
    val intensity: Float,
    val speedHz: Float,
    val reverbAmount: Float,
    val echoAmount: Float,
    val bassBoost: Float,
    val wetDryMix: Float,
    val isSystemDefault: Boolean = false
) {
    fun toSpatialParams(): SpatialParams {
        return SpatialParams(
            intensity = intensity,
            speedHz = speedHz,
            reverbAmount = reverbAmount,
            echoAmount = echoAmount,
            bassBoost = bassBoost,
            wetDryMix = wetDryMix
        )
    }

    fun getSpatialMode(): SpatialMode {
        return try {
            SpatialMode.valueOf(mode)
        } catch (e: Exception) {
            SpatialMode.SPATIAL_8D
        }
    }
}
