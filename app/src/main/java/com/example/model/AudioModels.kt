package com.example.model

enum class SpatialMode(
    val title: String,
    val subtitle: String,
    val dimensionLabel: String,
    val trajectoryDescription: String
) {
    STEREO(
        title = "Standard Stereo",
        subtitle = "Original Audio",
        dimensionLabel = "2D",
        trajectoryDescription = "Fixed dual-channel left/right soundstage without binaural panning."
    ),
    SPATIAL_8D(
        title = "8D Classic",
        subtitle = "Horizontal Orbit",
        dimensionLabel = "8D",
        trajectoryDescription = "Smooth 360° circular orbit rotating continuously around the listener's head using microsecond ITD delays."
    ),
    SPATIAL_16D(
        title = "16D Dynamic",
        subtitle = "Figure-8 Infinity",
        dimensionLabel = "16D",
        trajectoryDescription = "Multi-axis infinity (∞) trajectory undulating vertically above and below ears with depth modulation."
    ),
    SPATIAL_24D(
        title = "24D Hyper-Spatial",
        subtitle = "Spherical Vortex",
        dimensionLabel = "24D",
        trajectoryDescription = "Hyper-spatial 3D vortex combining orbital rotation, acoustic room reverb, phase shifting, and distance expansion."
    )
}

enum class OrbitDirection(val displayName: String) {
    CLOCKWISE("Clockwise ↻"),
    COUNTER_CLOCKWISE("Anti-Clockwise ↺")
}

enum class CustomSpatialShape(val displayName: String) {
    CIRCLE("Circular Orbit"),
    FIGURE_8("Figure-8 (Infinity ∞)"),
    SPIRAL("Spherical Spiral"),
    RANDOM("Dynamic Random"),
    CUSTOM_PATH("Custom Path")
}

enum class HrtfProfile(val displayName: String, val pinnaElevationGain: Float, val shadowFilterAlpha: Float) {
    BALANCED("Balanced Natural", 1.0f, 0.45f),
    NEAR_FIELD("Near-Field Intimate", 1.25f, 0.65f),
    DIFFUSE_FIELD("Diffuse-Field Concert", 0.85f, 0.35f)
}

enum class RepeatMode {
    OFF, ALL, ONE
}

enum class ExportFormat(val displayName: String, val extension: String, val mimeType: String, val bitrateKbps: Int = 320) {
    WAV_16BIT("Studio Lossless 16-bit WAV", "wav", "audio/wav", 1411),
    MP3_128K("Standard MP3 (128 kbps)", "mp3", "audio/mpeg", 128),
    MP3_192K("Medium MP3 (192 kbps)", "mp3", "audio/mpeg", 192),
    MP3_256K("High MP3 (256 kbps)", "mp3", "audio/mpeg", 256),
    MP3_320K("Studio MP3 (320 kbps)", "mp3", "audio/mpeg", 320)
}

enum class VisualizerMode(val displayName: String) {
    SPECTRUM("Frequency Spectrum"),
    WAVEFORM("Live Oscilloscope"),
    CIRCULAR_SPECTRUM("Circular Bloom"),
    ORBIT_8D("8D Celestial Orbit"),
    INFINITY_16D("16D Figure-8 Ribbon"),
    VORTEX_24D("24D Spherical Vortex"),
    GALAXY_PARTICLES("Cosmic Galaxy"),
    MINIMAL("Minimalist Pulse"),
    CUSTOM_TRAJECTORY("Custom Spatial Path")
}

enum class LibrarySortOrder(val displayName: String) {
    TITLE_ASC("Title (A-Z)"),
    ARTIST_ASC("Artist (A-Z)"),
    ALBUM_ASC("Album (A-Z)"),
    DURATION_DESC("Duration (Longest)"),
    DATE_ADDED_DESC("Recently Added"),
    PLAY_COUNT_DESC("Most Played")
}

enum class ReverbType(val displayName: String, val delayMs: Int, val feedback: Float) {
    STUDIO("Studio Acoustic", 45, 0.40f),
    ROOM("Warm Living Room", 75, 0.55f),
    HALL("Concert Hall", 120, 0.70f),
    PLATE("Shimmer Plate", 90, 0.65f)
}

data class SpatialParams(
    val intensity: Float = 0.85f,          // 0.0 .. 1.0 (spatial depth)
    val speedHz: Float = 0.12f,            // 0.03 .. 0.50 Hz (orbit speed)
    val direction: OrbitDirection = OrbitDirection.CLOCKWISE,
    val depth: Float = 0.70f,              // 0.0 .. 1.0 (z-axis undulation)
    val distance: Float = 1.0f,            // 0.5 .. 2.0 (listener radius)
    val stereoWidth: Float = 1.0f,         // 0.0 .. 2.0 (channel spread)
    val reverbAmount: Float = 0.35f,       // 0.0 .. 1.0 (acoustic room depth)
    val echoAmount: Float = 0.20f,         // 0.0 .. 1.0 (spatial reflections)
    val bassBoost: Float = 0.40f,          // 0.0 .. 1.0 (low-end weight)
    val effectMix: Float = 0.90f,          // 0.0 .. 1.0 (dry/wet mix)
    val wetDryMix: Float = 0.90f,          // 0.0 .. 1.0
    val hrtfProfile: HrtfProfile = HrtfProfile.BALANCED,
    val customShape: CustomSpatialShape = CustomSpatialShape.CIRCLE,
    val isBypassed: Boolean = false        // A/B Audio comparison: bypasses DSP to hear original
) {
    companion object {
        val CLASSIC_8D = SpatialParams(
            intensity = 0.88f,
            speedHz = 0.10f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.60f,
            distance = 1.0f,
            stereoWidth = 1.0f,
            reverbAmount = 0.30f,
            echoAmount = 0.15f,
            bassBoost = 0.35f,
            effectMix = 0.92f,
            wetDryMix = 0.92f,
            customShape = CustomSpatialShape.CIRCLE
        )
        val DEEP_BASS_8D = SpatialParams(
            intensity = 0.85f,
            speedHz = 0.08f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.55f,
            distance = 0.95f,
            stereoWidth = 1.1f,
            reverbAmount = 0.25f,
            echoAmount = 0.10f,
            bassBoost = 0.85f,
            effectMix = 0.90f,
            wetDryMix = 0.90f,
            customShape = CustomSpatialShape.CIRCLE
        )
        val CONCERT_8D = SpatialParams(
            intensity = 0.95f,
            speedHz = 0.09f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.75f,
            distance = 1.25f,
            stereoWidth = 1.3f,
            reverbAmount = 0.65f,
            echoAmount = 0.35f,
            bassBoost = 0.45f,
            effectMix = 0.95f,
            wetDryMix = 0.95f,
            hrtfProfile = HrtfProfile.DIFFUSE_FIELD,
            customShape = CustomSpatialShape.CIRCLE
        )
        val CINEMA = SpatialParams(
            intensity = 0.92f,
            speedHz = 0.07f,
            direction = OrbitDirection.COUNTER_CLOCKWISE,
            depth = 0.80f,
            distance = 1.30f,
            stereoWidth = 1.4f,
            reverbAmount = 0.70f,
            echoAmount = 0.40f,
            bassBoost = 0.60f,
            effectMix = 0.95f,
            wetDryMix = 0.95f,
            customShape = CustomSpatialShape.FIGURE_8
        )
        val STUDIO = SpatialParams(
            intensity = 0.75f,
            speedHz = 0.10f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.40f,
            distance = 0.85f,
            stereoWidth = 1.0f,
            reverbAmount = 0.15f,
            echoAmount = 0.05f,
            bassBoost = 0.20f,
            effectMix = 0.80f,
            wetDryMix = 0.80f,
            hrtfProfile = HrtfProfile.NEAR_FIELD,
            customShape = CustomSpatialShape.CIRCLE
        )
        val WIDE_STEREO = SpatialParams(
            intensity = 0.80f,
            speedHz = 0.05f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.50f,
            distance = 1.10f,
            stereoWidth = 1.8f,
            reverbAmount = 0.30f,
            echoAmount = 0.15f,
            bassBoost = 0.30f,
            effectMix = 0.85f,
            wetDryMix = 0.85f,
            customShape = CustomSpatialShape.FIGURE_8
        )
        val DEEP_SPACE = SpatialParams(
            intensity = 0.95f,
            speedHz = 0.06f,
            direction = OrbitDirection.COUNTER_CLOCKWISE,
            depth = 0.90f,
            distance = 1.40f,
            stereoWidth = 1.5f,
            reverbAmount = 0.80f,
            echoAmount = 0.50f,
            bassBoost = 0.40f,
            effectMix = 0.95f,
            wetDryMix = 0.95f,
            customShape = CustomSpatialShape.SPIRAL
        )
        val GALAXY = SpatialParams(
            intensity = 0.90f,
            speedHz = 0.14f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.75f,
            distance = 1.15f,
            stereoWidth = 1.3f,
            reverbAmount = 0.55f,
            echoAmount = 0.30f,
            bassBoost = 0.50f,
            effectMix = 0.90f,
            wetDryMix = 0.90f,
            customShape = CustomSpatialShape.SPIRAL
        )
        val VORTEX = SpatialParams(
            intensity = 1.00f,
            speedHz = 0.18f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.85f,
            distance = 1.10f,
            stereoWidth = 1.35f,
            reverbAmount = 0.60f,
            echoAmount = 0.35f,
            bassBoost = 0.60f,
            effectMix = 1.00f,
            wetDryMix = 1.00f,
            customShape = CustomSpatialShape.SPIRAL
        )
        val VORTEX_EXTREME = SpatialParams(
            intensity = 1.00f,
            speedHz = 0.26f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.95f,
            distance = 1.25f,
            stereoWidth = 1.50f,
            reverbAmount = 0.75f,
            echoAmount = 0.50f,
            bassBoost = 0.70f,
            effectMix = 1.00f,
            wetDryMix = 1.00f,
            customShape = CustomSpatialShape.SPIRAL
        )
        val VOCAL_FOCUS = SpatialParams(
            intensity = 0.70f,
            speedHz = 0.08f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.40f,
            distance = 0.90f,
            stereoWidth = 0.95f,
            reverbAmount = 0.25f,
            echoAmount = 0.10f,
            bassBoost = 0.15f,
            effectMix = 0.80f,
            wetDryMix = 0.80f,
            hrtfProfile = HrtfProfile.NEAR_FIELD,
            customShape = CustomSpatialShape.CIRCLE
        )
        val DEFAULT_8D = CLASSIC_8D
        val DEFAULT_16D = SpatialParams(
            intensity = 0.92f,
            speedHz = 0.15f,
            direction = OrbitDirection.CLOCKWISE,
            depth = 0.70f,
            distance = 1.05f,
            stereoWidth = 1.25f,
            reverbAmount = 0.45f,
            echoAmount = 0.25f,
            bassBoost = 0.50f,
            effectMix = 0.95f,
            wetDryMix = 0.95f,
            customShape = CustomSpatialShape.FIGURE_8
        )
        val DEFAULT_24D = VORTEX
    }
}

data class AudioTrackItem(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val path: String,
    val uriString: String,
    val isDemo: Boolean = false,
    val spatialMode: SpatialMode = SpatialMode.STEREO,
    val dateAdded: Long = System.currentTimeMillis(),
    val albumArtUri: String? = null,
    val isFavorite: Boolean = false,
    val folderName: String = "Music",
    val lrcContent: String? = null,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L,
    val genre: String = "All",
    val fileSizeBytes: Long = 0L,
    val bitrate: Int = 320,
    val isCorrupted: Boolean = false
)

data class LyricLine(
    val timestampMs: Long,
    val text: String
)

data class EqualizerBand(
    val index: Int,
    val centerFreqHz: Int,
    val minLevelMilliBels: Short = -1500,
    val maxLevelMilliBels: Short = 1500,
    val currentLevelMilliBels: Short = 0
)

data class StudioAudioEffects(
    val preampDb: Float = 0.0f,            // -12dB .. +12dB with soft limiting
    val stereoWidth: Float = 1.0f,         // 0.0 (Mono) .. 1.0 (Normal) .. 2.0 (Ultra-Wide)
    val balance: Float = 0.0f,             // -1.0 (Left) .. 0.0 (Center) .. +1.0 (Right)
    val playbackSpeed: Float = 1.0f,       // 0.5x .. 2.0x
    val pitch: Float = 1.0f,               // 0.5x .. 2.0x
    val bassBoostStrength: Short = 400,    // 0 .. 1000
    val trebleBoostStrength: Short = 200,  // 0 .. 1000
    val vocalBoostStrength: Short = 300,   // 0 .. 1000
    val loudnessStrength: Short = 250,     // 0 .. 1000
    val clarityGain: Float = 0.35f,        // High-end exciter
    val compressionRatio: Float = 2.5f,    // Soft knee limiter/compressor
    val reverbType: ReverbType = ReverbType.STUDIO,
    val isMono: Boolean = false,
    val volumeNormalization: Boolean = true,
    val gaplessPlayback: Boolean = true,
    val crossfadeDurationSec: Int = 0      // 0 = off, 1..10 seconds
)

data class BatchConversionJob(
    val id: String,
    val track: AudioTrackItem,
    val mode: SpatialMode,
    val params: SpatialParams,
    val format: ExportFormat,
    val status: JobStatus = JobStatus.QUEUED,
    val progressPercent: Int = 0,
    val statusMessage: String = "Queued",
    val outputFile: java.io.File? = null
)

enum class JobStatus {
    QUEUED, PROCESSING, COMPLETED, FAILED, CANCELLED
}

data class ConversionHistoryItem(
    val id: Long = 0,
    val originalTitle: String,
    val originalPath: String,
    val outputFilename: String,
    val outputPath: String,
    val spatialMode: String,
    val presetName: String,
    val timestamp: Long,
    val durationMs: Long,
    val format: String,
    val fileSizeBytes: Long
)
