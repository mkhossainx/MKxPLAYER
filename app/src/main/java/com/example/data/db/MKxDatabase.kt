package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ConversionHistoryDao
import com.example.data.dao.ExcludedFolderDao
import com.example.data.dao.PlaylistDao
import com.example.data.dao.SpatialPresetDao
import com.example.data.dao.TrackDao
import com.example.data.entity.ConversionHistoryEntity
import com.example.data.entity.ExcludedFolderEntity
import com.example.data.entity.PlaylistEntity
import com.example.data.entity.PlaylistTrackCrossRef
import com.example.data.entity.SpatialPresetEntity
import com.example.data.entity.TrackEntity
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        SpatialPresetEntity::class,
        ConversionHistoryEntity::class,
        ExcludedFolderEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MKxDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun spatialPresetDao(): SpatialPresetDao
    abstract fun conversionHistoryDao(): ConversionHistoryDao
    abstract fun excludedFolderDao(): ExcludedFolderDao

    companion object {
        @Volatile
        private var INSTANCE: MKxDatabase? = null

        fun getDatabase(context: Context): MKxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MKxDatabase::class.java,
                    "mkxplayer_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDefaultPresets(database.spatialPresetDao())
                        populateDefaultPlaylists(database.playlistDao())
                    }
                }
            }

            private suspend fun populateDefaultPresets(dao: SpatialPresetDao) {
                val defaultPresets = listOf(
                    SpatialPresetEntity(
                        name = "Classic 8D Orbit",
                        mode = SpatialMode.SPATIAL_8D.name,
                        intensity = SpatialParams.CLASSIC_8D.intensity,
                        speedHz = SpatialParams.CLASSIC_8D.speedHz,
                        reverbAmount = SpatialParams.CLASSIC_8D.reverbAmount,
                        echoAmount = SpatialParams.CLASSIC_8D.echoAmount,
                        bassBoost = SpatialParams.CLASSIC_8D.bassBoost,
                        wetDryMix = SpatialParams.CLASSIC_8D.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Deep Bass 8D",
                        mode = SpatialMode.SPATIAL_8D.name,
                        intensity = SpatialParams.DEEP_BASS_8D.intensity,
                        speedHz = SpatialParams.DEEP_BASS_8D.speedHz,
                        reverbAmount = SpatialParams.DEEP_BASS_8D.reverbAmount,
                        echoAmount = SpatialParams.DEEP_BASS_8D.echoAmount,
                        bassBoost = SpatialParams.DEEP_BASS_8D.bassBoost,
                        wetDryMix = SpatialParams.DEEP_BASS_8D.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Concert 8D Stage",
                        mode = SpatialMode.SPATIAL_8D.name,
                        intensity = SpatialParams.CONCERT_8D.intensity,
                        speedHz = SpatialParams.CONCERT_8D.speedHz,
                        reverbAmount = SpatialParams.CONCERT_8D.reverbAmount,
                        echoAmount = SpatialParams.CONCERT_8D.echoAmount,
                        bassBoost = SpatialParams.CONCERT_8D.bassBoost,
                        wetDryMix = SpatialParams.CONCERT_8D.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Cinema Soundstage",
                        mode = SpatialMode.SPATIAL_16D.name,
                        intensity = SpatialParams.CINEMA.intensity,
                        speedHz = SpatialParams.CINEMA.speedHz,
                        reverbAmount = SpatialParams.CINEMA.reverbAmount,
                        echoAmount = SpatialParams.CINEMA.echoAmount,
                        bassBoost = SpatialParams.CINEMA.bassBoost,
                        wetDryMix = SpatialParams.CINEMA.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Studio Precision",
                        mode = SpatialMode.SPATIAL_8D.name,
                        intensity = SpatialParams.STUDIO.intensity,
                        speedHz = SpatialParams.STUDIO.speedHz,
                        reverbAmount = SpatialParams.STUDIO.reverbAmount,
                        echoAmount = SpatialParams.STUDIO.echoAmount,
                        bassBoost = SpatialParams.STUDIO.bassBoost,
                        wetDryMix = SpatialParams.STUDIO.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Wide Stereo",
                        mode = SpatialMode.SPATIAL_16D.name,
                        intensity = SpatialParams.WIDE_STEREO.intensity,
                        speedHz = SpatialParams.WIDE_STEREO.speedHz,
                        reverbAmount = SpatialParams.WIDE_STEREO.reverbAmount,
                        echoAmount = SpatialParams.WIDE_STEREO.echoAmount,
                        bassBoost = SpatialParams.WIDE_STEREO.bassBoost,
                        wetDryMix = SpatialParams.WIDE_STEREO.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Deep Space Cosmos",
                        mode = SpatialMode.SPATIAL_24D.name,
                        intensity = SpatialParams.DEEP_SPACE.intensity,
                        speedHz = SpatialParams.DEEP_SPACE.speedHz,
                        reverbAmount = SpatialParams.DEEP_SPACE.reverbAmount,
                        echoAmount = SpatialParams.DEEP_SPACE.echoAmount,
                        bassBoost = SpatialParams.DEEP_SPACE.bassBoost,
                        wetDryMix = SpatialParams.DEEP_SPACE.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Galaxy 24D",
                        mode = SpatialMode.SPATIAL_24D.name,
                        intensity = SpatialParams.GALAXY.intensity,
                        speedHz = SpatialParams.GALAXY.speedHz,
                        reverbAmount = SpatialParams.GALAXY.reverbAmount,
                        echoAmount = SpatialParams.GALAXY.echoAmount,
                        bassBoost = SpatialParams.GALAXY.bassBoost,
                        wetDryMix = SpatialParams.GALAXY.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Vortex Spherical",
                        mode = SpatialMode.SPATIAL_24D.name,
                        intensity = SpatialParams.VORTEX.intensity,
                        speedHz = SpatialParams.VORTEX.speedHz,
                        reverbAmount = SpatialParams.VORTEX.reverbAmount,
                        echoAmount = SpatialParams.VORTEX.echoAmount,
                        bassBoost = SpatialParams.VORTEX.bassBoost,
                        wetDryMix = SpatialParams.VORTEX.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Vortex Extreme",
                        mode = SpatialMode.SPATIAL_24D.name,
                        intensity = SpatialParams.VORTEX_EXTREME.intensity,
                        speedHz = SpatialParams.VORTEX_EXTREME.speedHz,
                        reverbAmount = SpatialParams.VORTEX_EXTREME.reverbAmount,
                        echoAmount = SpatialParams.VORTEX_EXTREME.echoAmount,
                        bassBoost = SpatialParams.VORTEX_EXTREME.bassBoost,
                        wetDryMix = SpatialParams.VORTEX_EXTREME.wetDryMix,
                        isSystemDefault = true
                    ),
                    SpatialPresetEntity(
                        name = "Vocal Focus",
                        mode = SpatialMode.SPATIAL_8D.name,
                        intensity = SpatialParams.VOCAL_FOCUS.intensity,
                        speedHz = SpatialParams.VOCAL_FOCUS.speedHz,
                        reverbAmount = SpatialParams.VOCAL_FOCUS.reverbAmount,
                        echoAmount = SpatialParams.VOCAL_FOCUS.echoAmount,
                        bassBoost = SpatialParams.VOCAL_FOCUS.bassBoost,
                        wetDryMix = SpatialParams.VOCAL_FOCUS.wetDryMix,
                        isSystemDefault = true
                    )
                )
                defaultPresets.forEach { dao.insertPreset(it) }
            }

            private suspend fun populateDefaultPlaylists(dao: PlaylistDao) {
                dao.insertPlaylist(PlaylistEntity(name = "Cyber Immersion 8D/24D"))
                dao.insertPlaylist(PlaylistEntity(name = "Favorite Spatial Mix"))
            }
        }
    }
}
