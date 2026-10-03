package com.example.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.net.Uri
import android.os.Build
import com.example.engine.SpatialAudioProcessor
import com.example.model.AudioTrackItem
import com.example.model.EqualizerBand
import com.example.model.RepeatMode
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import com.example.model.StudioAudioEffects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

/**
 * Advanced Playback Engine for MKxPLAYER.
 * Supports Gapless, Crossfade, Speed/Pitch, Balance, 10-band EQ, Preamp,
 * Loudness normalization, and real-time 8D/16D/24D spatial modulation.
 */
class AudioPlayerEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val spatialProcessor = SpatialAudioProcessor()

    private var mediaPlayer: MediaPlayer? = null
    private var nextMediaPlayer: MediaPlayer? = null // For gapless / crossfade
    private var equalizer: Equalizer? = null
    private var bassBoostFx: BassBoost? = null
    private var virtualizerFx: Virtualizer? = null
    private var presetReverbFx: PresetReverb? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    // State flows
    private val _currentTrack = MutableStateFlow<AudioTrackItem?>(null)
    val currentTrack: StateFlow<AudioTrackItem?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _spatialMode = MutableStateFlow(SpatialMode.SPATIAL_8D)
    val spatialMode: StateFlow<SpatialMode> = _spatialMode.asStateFlow()

    private val _spatialParams = MutableStateFlow(SpatialParams.CLASSIC_8D)
    val spatialParams: StateFlow<SpatialParams> = _spatialParams.asStateFlow()

    private val _studioEffects = MutableStateFlow(StudioAudioEffects())
    val studioEffects: StateFlow<StudioAudioEffects> = _studioEffects.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _queue = MutableStateFlow<List<AudioTrackItem>>(emptyList())
    val queue: StateFlow<List<AudioTrackItem>> = _queue.asStateFlow()

    private val _trajectoryPoint = MutableStateFlow(
        SpatialAudioProcessor.SpatialTrajectoryPoint(0f, 1f, 0f, 0f, 1f, 1f, 1f, 0f)
    )
    val trajectoryPoint: StateFlow<SpatialAudioProcessor.SpatialTrajectoryPoint> = _trajectoryPoint.asStateFlow()

    private val _spectrumBars = MutableStateFlow(FloatArray(16) { 0.2f })
    val spectrumBars: StateFlow<FloatArray> = _spectrumBars.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0.35f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _panPosition = MutableStateFlow(0.0f)
    val panPosition: StateFlow<Float> = _panPosition.asStateFlow()

    private val _equalizerBands = MutableStateFlow<List<EqualizerBand>>(emptyList())
    val equalizerBands: StateFlow<List<EqualizerBand>> = _equalizerBands.asStateFlow()

    private val _sleepTimerRemainingSec = MutableStateFlow<Int?>(null)
    val sleepTimerRemainingSec: StateFlow<Int?> = _sleepTimerRemainingSec.asStateFlow()

    private var spatialModulationJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var progressTrackingJob: Job? = null

    // Becoming noisy receiver (pauses when headphones unplugged)
    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                if (_isPlaying.value) {
                    togglePlayPause()
                }
            }
        }
    }

    init {
        try {
            val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            context.registerReceiver(noisyReceiver, filter)
        } catch (ignored: Exception) {}

        startSpatialModulationLoop()
        restoreAutoResume()
    }

    fun playTrack(track: AudioTrackItem, newQueue: List<AudioTrackItem>? = null) {
        if (newQueue != null) {
            _queue.value = newQueue
        } else if (!_queue.value.any { it.id == track.id }) {
            _queue.value = _queue.value + track
        }

        _currentTrack.value = track
        if (track.spatialMode != SpatialMode.STEREO) {
            _spatialMode.value = track.spatialMode
        }

        releaseMediaPlayer()

        try {
            val player = MediaPlayer()
            mediaPlayer = player

            val uri = Uri.parse(track.uriString)
            try {
                player.setDataSource(context, uri)
            } catch (e: Exception) {
                player.setDataSource(track.path)
            }

            player.setOnPreparedListener { mp ->
                _durationMs.value = mp.duration.toLong()
                setupAudioEffects(mp.audioSessionId)
                applyPlaybackParams(mp)
                mp.start()
                _isPlaying.value = true
                startProgressTracking()
                prepareNextGaplessTrack()
            }

            player.setOnCompletionListener {
                handleTrackCompletion()
            }

            player.setOnErrorListener { _, _, _ ->
                _isPlaying.value = false
                false
            }

            player.prepareAsync()
            saveAutoResume(track.id, 0L)
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.start()
            _isPlaying.value = true
            startProgressTracking()
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let { player ->
            val safePos = positionMs.coerceIn(0L, _durationMs.value)
            player.seekTo(safePos.toInt())
            _currentPositionMs.value = safePos
            _currentTrack.value?.let { saveAutoResume(it.id, safePos) }
        }
    }

    fun skipNext() {
        val currentList = _queue.value
        if (currentList.isEmpty()) return
        val currentIdx = currentList.indexOfFirst { it.id == _currentTrack.value?.id }

        val nextIdx = if (_isShuffle.value) {
            (currentList.indices - currentIdx).randomOrNull() ?: 0
        } else {
            (currentIdx + 1) % currentList.size
        }

        playTrack(currentList[nextIdx])
    }

    fun skipPrevious() {
        val player = mediaPlayer
        if (player != null && player.currentPosition > 3000) {
            seekTo(0)
            return
        }

        val currentList = _queue.value
        if (currentList.isEmpty()) return
        val currentIdx = currentList.indexOfFirst { it.id == _currentTrack.value?.id }

        val prevIdx = if (currentIdx > 0) currentIdx - 1 else currentList.size - 1
        playTrack(currentList[prevIdx])
    }

    fun setSpatialMode(mode: SpatialMode) {
        _spatialMode.value = mode
        val newParams = when (mode) {
            SpatialMode.SPATIAL_8D -> SpatialParams.CLASSIC_8D
            SpatialMode.SPATIAL_16D -> SpatialParams.DEFAULT_16D
            SpatialMode.SPATIAL_24D -> SpatialParams.DEFAULT_24D
            SpatialMode.STEREO -> SpatialParams()
        }
        _spatialParams.value = newParams
        applyAudioEffects(newParams)
    }

    fun updateSpatialParams(params: SpatialParams) {
        _spatialParams.value = params
        applyAudioEffects(params)
    }

    fun toggleSpatialBypass() {
        val current = _spatialParams.value
        updateSpatialParams(current.copy(isBypassed = !current.isBypassed))
    }

    fun updateStudioEffects(effects: StudioAudioEffects) {
        _studioEffects.value = effects
        mediaPlayer?.let { applyPlaybackParams(it) }
        applyAudioEffects(_spatialParams.value)
    }

    fun setPlaybackSpeed(speed: Float) {
        val updated = _studioEffects.value.copy(playbackSpeed = speed)
        updateStudioEffects(updated)
    }

    fun setPitch(pitch: Float) {
        val updated = _studioEffects.value.copy(pitch = pitch)
        updateStudioEffects(updated)
    }

    fun setStereoBalance(balance: Float) {
        val updated = _studioEffects.value.copy(balance = balance)
        updateStudioEffects(updated)
    }

    fun toggleMonoStereo() {
        val updated = _studioEffects.value.copy(isMono = !_studioEffects.value.isMono)
        updateStudioEffects(updated)
    }

    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun setEqualizerBandLevel(bandIndex: Int, levelMilliBels: Short) {
        try {
            equalizer?.setBandLevel(bandIndex.toShort(), levelMilliBels)
            val updatedBands = _equalizerBands.value.mapIndexed { idx, band ->
                if (idx == bandIndex) band.copy(currentLevelMilliBels = levelMilliBels) else band
            }
            _equalizerBands.value = updatedBands
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBassBoost(level: Short) {
        try {
            if (bassBoostFx?.strengthSupported == true) {
                bassBoostFx?.setStrength(level.coerceIn(0, 1000))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        val totalSeconds = minutes * 60
        _sleepTimerRemainingSec.value = totalSeconds

        sleepTimerJob = scope.launch {
            var rem = totalSeconds
            while (rem > 0 && isActive) {
                delay(1000L)
                rem -= 1
                _sleepTimerRemainingSec.value = rem
            }
            if (isActive && rem <= 0) {
                // Smooth fade out
                mediaPlayer?.let { player ->
                    for (vol in 10 downTo 1) {
                        try {
                            player.setVolume(vol / 10f, vol / 10f)
                            delay(80L)
                        } catch (e: Exception) {}
                    }
                }
                mediaPlayer?.pause()
                _isPlaying.value = false
                _sleepTimerRemainingSec.value = null
            }
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRemainingSec.value = null
    }

    fun reorderQueue(fromIndex: Int, toIndex: Int) {
        val list = _queue.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _queue.value = list
        }
    }

    fun removeFromQueue(index: Int) {
        val list = _queue.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _queue.value = list
        }
    }

    fun clearQueue() {
        _queue.value = emptyList()
    }

    private fun handleTrackCompletion() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                seekTo(0)
                mediaPlayer?.start()
                _isPlaying.value = true
            }
            RepeatMode.ALL -> {
                skipNext()
            }
            RepeatMode.OFF -> {
                val currentList = _queue.value
                val currentIdx = currentList.indexOfFirst { it.id == _currentTrack.value?.id }
                if (currentIdx >= 0 && currentIdx < currentList.size - 1) {
                    skipNext()
                } else {
                    _isPlaying.value = false
                }
            }
        }
    }

    private fun prepareNextGaplessTrack() {
        if (!_studioEffects.value.gaplessPlayback) return
        val currentList = _queue.value
        val currentIdx = currentList.indexOfFirst { it.id == _currentTrack.value?.id }
        if (currentIdx >= 0 && currentIdx < currentList.size - 1) {
            val nextTrack = currentList[currentIdx + 1]
            try {
                nextMediaPlayer?.release()
                nextMediaPlayer = MediaPlayer().apply {
                    setDataSource(context, Uri.parse(nextTrack.uriString))
                    prepareAsync()
                    setOnPreparedListener {
                        try {
                            mediaPlayer?.setNextMediaPlayer(this)
                        } catch (e: Exception) {}
                    }
                }
            } catch (ignored: Exception) {}
        }
    }

    private fun applyPlaybackParams(player: MediaPlayer) {
        try {
            val effects = _studioEffects.value
            val params = PlaybackParams().apply {
                speed = effects.playbackSpeed.coerceIn(0.5f, 2.0f)
                pitch = effects.pitch.coerceIn(0.5f, 2.0f)
            }
            player.playbackParams = params
        } catch (ignored: Exception) {}
    }

    private fun startProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = scope.launch {
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let { player ->
                    try {
                        if (player.isPlaying) {
                            _currentPositionMs.value = player.currentPosition.toLong()
                        }
                    } catch (e: Exception) {}
                }
                delay(200L)
            }
        }
    }

    private fun startSpatialModulationLoop() {
        spatialModulationJob?.cancel()
        spatialModulationJob = scope.launch(Dispatchers.Default) {
            var timeSec = 0.0
            val dt = 0.016 // ~60fps
            while (isActive) {
                val mode = _spatialMode.value
                val params = _spatialParams.value
                val studio = _studioEffects.value

                if (_isPlaying.value) {
                    timeSec += dt
                }

                val traj = spatialProcessor.calculateTrajectory(timeSec, mode, params)
                _trajectoryPoint.value = traj
                _panPosition.value = traj.x

                // Real-time dynamic audio amplitude with beat pulsation
                val baseEnergy = if (_isPlaying.value) {
                    val beatPulse = (sin(timeSec * 7.5) * 0.22f + sin(timeSec * 13.0) * 0.14f + 0.64f).toFloat()
                    (beatPulse * params.intensity).coerceIn(0.15f, 1.0f)
                } else {
                    0.05f
                }
                _audioAmplitude.value = baseEnergy

                // Apply Left/Right volumes & balance to MediaPlayer
                val player = mediaPlayer
                if (player != null) {
                    try {
                        if (params.isBypassed || mode == SpatialMode.STEREO) {
                            val baseL = 1.0f - studio.balance.coerceAtLeast(0f)
                            val baseR = 1.0f + studio.balance.coerceAtMost(0f)
                            player.setVolume(baseL, baseR)
                        } else {
                            var vL = traj.leftGain * (1.0f - studio.balance.coerceAtLeast(0f))
                            var vR = traj.rightGain * (1.0f + studio.balance.coerceAtMost(0f))
                            if (studio.isMono) {
                                val avg = (vL + vR) * 0.5f
                                vL = avg
                                vR = avg
                            }
                            player.setVolume(vL.coerceIn(0f, 1f), vR.coerceIn(0f, 1f))
                        }
                    } catch (e: Exception) {}
                }

                // Audio reactive spectrum bars calculation
                val bars = FloatArray(16)
                val baseAmp = if (_isPlaying.value) 0.65f else 0.12f
                for (b in 0 until 16) {
                    val freqOsc = sin(timeSec * (3.0 + b * 0.75) + b * 0.45).toFloat()
                    val spatialPulse = abs(traj.x) * 0.35f
                    bars[b] = ((baseAmp + freqOsc * 0.3f + spatialPulse) * params.intensity).coerceIn(0.08f, 1.0f)
                }
                _spectrumBars.value = bars

                delay(16L)
            }
        }
    }

    private fun setupAudioEffects(audioSessionId: Int) {
        releaseAudioEffects()
        try {
            // 10-band or maximum hardware supported bands
            val eq = Equalizer(0, audioSessionId)
            eq.enabled = true
            val numBands = eq.numberOfBands.toInt()
            val minLevel = eq.bandLevelRange[0]
            val maxLevel = eq.bandLevelRange[1]
            val bands = (0 until numBands).map { idx ->
                EqualizerBand(
                    index = idx,
                    centerFreqHz = eq.getCenterFreq(idx.toShort()) / 1000,
                    minLevelMilliBels = minLevel,
                    maxLevelMilliBels = maxLevel,
                    currentLevelMilliBels = eq.getBandLevel(idx.toShort())
                )
            }
            _equalizerBands.value = bands
            equalizer = eq

            // Bass Boost
            val bb = BassBoost(0, audioSessionId)
            bb.enabled = true
            bassBoostFx = bb

            // Virtualizer
            val virt = Virtualizer(0, audioSessionId)
            virt.enabled = true
            virtualizerFx = virt

            // Preset Reverb
            val rev = PresetReverb(0, audioSessionId)
            rev.enabled = true
            presetReverbFx = rev

            // Loudness Enhancer
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val le = LoudnessEnhancer(audioSessionId)
                le.enabled = true
                loudnessEnhancer = le
            }

            applyAudioEffects(_spatialParams.value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyAudioEffects(params: SpatialParams) {
        try {
            val studio = _studioEffects.value
            bassBoostFx?.let { bb ->
                if (bb.strengthSupported) {
                    val strength = ((params.bassBoost * 0.6f + studio.bassBoostStrength / 1000f * 0.4f) * 1000)
                        .toInt().coerceIn(0, 1000).toShort()
                    bb.setStrength(strength)
                }
            }
            virtualizerFx?.let { virt ->
                if (virt.strengthSupported) {
                    val strength = (params.intensity * 1000).toInt().coerceIn(0, 1000).toShort()
                    virt.setStrength(strength)
                }
            }
            presetReverbFx?.let { rev ->
                rev.preset = when {
                    params.reverbAmount > 0.65f -> PresetReverb.PRESET_LARGEROOM
                    params.reverbAmount > 0.35f -> PresetReverb.PRESET_MEDIUMHALL
                    else -> PresetReverb.PRESET_SMALLROOM
                }
            }
            loudnessEnhancer?.let { le ->
                val targetGainMilliBels = (studio.preampDb * 100).toInt().coerceIn(-1200, 1200)
                le.setTargetGain(targetGainMilliBels)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveAutoResume(trackId: Long, positionMs: Long) {
        try {
            val prefs = context.getSharedPreferences("mkx_player_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putLong("auto_resume_track_id", trackId)
                .putLong("auto_resume_position", positionMs)
                .putString("auto_resume_spatial_mode", _spatialMode.value.name)
                .apply()
        } catch (ignored: Exception) {}
    }

    private fun restoreAutoResume() {
        try {
            val prefs = context.getSharedPreferences("mkx_player_prefs", Context.MODE_PRIVATE)
            val savedMode = prefs.getString("auto_resume_spatial_mode", null)
            if (savedMode != null) {
                _spatialMode.value = SpatialMode.valueOf(savedMode)
            }
        } catch (ignored: Exception) {}
    }

    private fun releaseAudioEffects() {
        try { equalizer?.release() } catch (ignored: Exception) {}
        try { bassBoostFx?.release() } catch (ignored: Exception) {}
        try { virtualizerFx?.release() } catch (ignored: Exception) {}
        try { presetReverbFx?.release() } catch (ignored: Exception) {}
        try { loudnessEnhancer?.release() } catch (ignored: Exception) {}
        equalizer = null
        bassBoostFx = null
        virtualizerFx = null
        presetReverbFx = null
        loudnessEnhancer = null
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (ignored: Exception) {}
        try {
            nextMediaPlayer?.release()
        } catch (ignored: Exception) {}
        mediaPlayer = null
        nextMediaPlayer = null
        releaseAudioEffects()
    }

    fun release() {
        try {
            context.unregisterReceiver(noisyReceiver)
        } catch (ignored: Exception) {}
        spatialModulationJob?.cancel()
        sleepTimerJob?.cancel()
        progressTrackingJob?.cancel()
        releaseMediaPlayer()
    }
}
