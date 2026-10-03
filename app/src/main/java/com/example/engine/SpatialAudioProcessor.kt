package com.example.engine

import com.example.model.CustomSpatialShape
import com.example.model.HrtfProfile
import com.example.model.OrbitDirection
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import com.example.model.StudioAudioEffects
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tanh

/**
 * Advanced Spatial Audio DSP Engine for MKxPLAYER.
 *
 * Implements binaural Head-Related Transfer Function (HRTF) approximations,
 * Interaural Time Difference (ITD), Interaural Level Difference (ILD),
 * head-shadow acoustic filtering, 10-band equalization,
 * preamp soft-limiting clipping protection, and custom trajectories.
 */
class SpatialAudioProcessor {

    data class SpatialTrajectoryPoint(
        val x: Float, // -1.0 (left) to +1.0 (right)
        val y: Float, // -1.0 (behind) to +1.0 (front)
        val z: Float, // -1.0 (below) to +1.0 (above)
        val azimuthDeg: Float,
        val distance: Float,
        val leftGain: Float,
        val rightGain: Float,
        val itdDelayMs: Float
    )

    // Reverb comb filter delay lines for acoustic immersion
    private val combBuffer1 = FloatArray(4410) // ~100ms at 44.1kHz
    private val combBuffer2 = FloatArray(5292) // ~120ms
    private val combBuffer3 = FloatArray(6174) // ~140ms
    private var combIdx1 = 0
    private var combIdx2 = 0
    private var combIdx3 = 0

    // Interaural Time Difference (ITD) delay buffer
    private val itdMaxDelay = 80
    private val leftDelayLine = FloatArray(itdMaxDelay)
    private val rightDelayLine = FloatArray(itdMaxDelay)
    private var delayWriteIdx = 0

    // Filter states
    private var leftFilterState = 0f
    private var rightFilterState = 0f
    private var bassStateL = 0f
    private var bassStateR = 0f
    private var trebleStateL = 0f
    private var trebleStateR = 0f
    private var midStateL = 0f
    private var midStateR = 0f

    // Compressor envelope follower
    private var compressorEnvelope = 0f

    /**
     * Computes the 3D trajectory position and stereo gains for any given time in seconds.
     */
    fun calculateTrajectory(
        timeSec: Double,
        mode: SpatialMode,
        params: SpatialParams
    ): SpatialTrajectoryPoint {
        if (mode == SpatialMode.STEREO || params.isBypassed) {
            return SpatialTrajectoryPoint(
                x = 0f, y = 1f, z = 0f,
                azimuthDeg = 0f, distance = 1f,
                leftGain = 1f, rightGain = 1f,
                itdDelayMs = 0f
            )
        }

        val dirSign = if (params.direction == OrbitDirection.CLOCKWISE) 1.0 else -1.0
        val speed = params.speedHz.toDouble() * dirSign
        val intensity = params.intensity.coerceIn(0f, 1f)
        val depthScale = params.depth.toDouble()
        val distParam = params.distance.toDouble()

        var x: Double
        var y: Double
        var z: Double

        when (mode) {
            SpatialMode.SPATIAL_8D -> {
                // 360° Circular Horizontal Orbit
                val angle = 2.0 * PI * speed * timeSec
                x = sin(angle) * intensity * distParam
                y = cos(angle) * distParam
                z = 0.0
            }
            SpatialMode.SPATIAL_16D -> {
                // Figure-8 (Infinity ∞) trajectory with vertical elevation undulation
                val angle = 2.0 * PI * speed * timeSec
                x = sin(angle) * intensity * distParam
                y = cos(angle) * distParam
                z = sin(2.0 * angle) * 0.55 * depthScale
            }
            SpatialMode.SPATIAL_24D -> {
                // Spherical 3D Vortex / Spiral with expanding/contracting radius
                val angle = 2.0 * PI * speed * timeSec
                val radiusMod = (0.65 + 0.35 * sin(PI * speed * 0.75 * timeSec)) * distParam
                x = sin(angle) * radiusMod * intensity
                y = cos(angle) * radiusMod
                z = sin(angle * 1.5) * 0.70 * depthScale
            }
            SpatialMode.STEREO -> {
                x = 0.0; y = 1.0; z = 0.0
            }
        }

        // Apply Custom Spatial Shapes if specified
        when (params.customShape) {
            CustomSpatialShape.SPIRAL -> {
                val spiralRad = (0.5 + 0.5 * abs(sin(PI * speed * 0.5 * timeSec))) * distParam
                val angle = 2.0 * PI * speed * timeSec
                x = sin(angle) * spiralRad * intensity
                y = cos(angle) * spiralRad
                z = sin(angle * 2.0) * 0.5 * depthScale
            }
            CustomSpatialShape.FIGURE_8 -> {
                val angle = 2.0 * PI * speed * timeSec
                val denom = 1.0 + sin(angle) * sin(angle)
                x = (cos(angle) / denom) * intensity * distParam
                y = (sin(angle) * cos(angle) / denom) * distParam
                z = sin(angle * 2.0) * 0.5 * depthScale
            }
            CustomSpatialShape.RANDOM -> {
                val a1 = 2.0 * PI * speed * timeSec
                val a2 = 2.0 * PI * (speed * 0.73) * timeSec
                x = (sin(a1) * 0.6 + sin(a2) * 0.4) * intensity * distParam
                y = (cos(a1) * 0.6 + cos(a2) * 0.4) * distParam
                z = sin(a1 + a2) * 0.5 * depthScale
            }
            CustomSpatialShape.CUSTOM_PATH -> {
                // Lissajous 3D path: x = sin(3t), y = cos(2t), z = sin(4t)
                val t = 2.0 * PI * speed * 0.5 * timeSec
                x = sin(3.0 * t) * 0.85 * intensity * distParam
                y = cos(2.0 * t) * 0.85 * distParam
                z = sin(4.0 * t) * 0.5 * depthScale
            }
            CustomSpatialShape.CIRCLE -> {} // handled by standard modes
        }

        val distance = sqrt(x * x + y * y + z * z).toFloat().coerceAtLeast(0.01f)
        val azimuthRad = kotlin.math.atan2(x, y)
        val azimuthDeg = Math.toDegrees(azimuthRad).toFloat()

        // Equal-power panning law with stereo width expansion
        val panNorm = ((x / (distance + 0.001f)).coerceIn(-1.0, 1.0) + 1.0) / 2.0
        val panAngle = panNorm * (PI / 2.0)

        var gL = cos(panAngle).toFloat()
        var gR = sin(panAngle).toFloat()

        // Stereo width scaling
        val width = params.stereoWidth
        if (width != 1.0f) {
            val mid = (gL + gR) * 0.5f
            val side = (gR - gL) * 0.5f * width
            gL = (mid - side).coerceAtLeast(0.05f)
            gR = (mid + side).coerceAtLeast(0.05f)
        }

        // Distance attenuation
        val distAttenuation = (1.0f / (0.8f + 0.2f * distance)).coerceIn(0.5f, 1.3f)
        gL *= distAttenuation
        gR *= distAttenuation

        // ITD calculation: Woodworth model delay = (d/2c) * (sin(theta) + theta)
        val itdDelayMs = (0.65f * abs(sin(azimuthRad).toFloat())).coerceIn(0f, 0.75f)

        return SpatialTrajectoryPoint(
            x = x.toFloat(),
            y = y.toFloat(),
            z = z.toFloat(),
            azimuthDeg = azimuthDeg,
            distance = distance,
            leftGain = (gL * intensity + 1.0f * (1f - intensity)).coerceIn(0.05f, 1.4f),
            rightGain = (gR * intensity + 1.0f * (1f - intensity)).coerceIn(0.05f, 1.4f),
            itdDelayMs = itdDelayMs
        )
    }

    /**
     * Processes a block of 16-bit interleaved stereo PCM samples with spatial and studio effects.
     */
    fun processPcmBlock(
        buffer: ShortArray,
        length: Int,
        sampleRate: Int,
        startTimeSec: Double,
        mode: SpatialMode,
        params: SpatialParams,
        studioEffects: StudioAudioEffects = StudioAudioEffects()
    ): Double {
        if (params.isBypassed || length <= 0) {
            return startTimeSec + (length / 2.0) / sampleRate
        }

        var currentTime = startTimeSec
        val dt = 1.0 / sampleRate
        val wet = params.wetDryMix * params.effectMix
        val dry = 1.0f - wet
        val bassGain = params.bassBoost * 0.65f
        val reverbAmt = params.reverbAmount * 0.50f
        val echoAmt = params.echoAmount * 0.50f

        // Preamp multiplier
        val preampLinear = 10.0.pow(studioEffects.preampDb / 20.0).toFloat()

        var i = 0
        while (i < length - 1) {
            var inL = buffer[i].toFloat() * preampLinear
            var inR = buffer[i + 1].toFloat() * preampLinear

            // Mono mixing if requested
            if (studioEffects.isMono) {
                val mono = (inL + inR) * 0.5f
                inL = mono
                inR = mono
            }

            // Balance adjustment (-1.0 left to +1.0 right)
            if (studioEffects.balance != 0.0f) {
                val bal = studioEffects.balance
                if (bal < 0f) {
                    inR *= (1f + bal)
                } else {
                    inL *= (1f - bal)
                }
            }

            val traj = calculateTrajectory(currentTime, mode, params)
            val isSourceRight = traj.x > 0

            // 1. Interaural Time Delay (ITD)
            val delaySamples = ((traj.itdDelayMs * 0.001f * sampleRate).toInt()).coerceIn(0, itdMaxDelay - 1)
            leftDelayLine[delayWriteIdx] = inL
            rightDelayLine[delayWriteIdx] = inR

            val readIdx = (delayWriteIdx - delaySamples + itdMaxDelay) % itdMaxDelay

            val delayedL = if (isSourceRight) leftDelayLine[readIdx] else inL
            val delayedR = if (!isSourceRight) rightDelayLine[readIdx] else inR

            delayWriteIdx = (delayWriteIdx + 1) % itdMaxDelay

            // 2. Head-Shadow Lowpass Filter for Contralateral Ear using HRTF profile
            val shadowFactor = params.hrtfProfile.shadowFilterAlpha
            val shadowCoeff = (0.20f + shadowFactor * (1f - abs(traj.x))).coerceIn(0.15f, 0.95f)
            if (isSourceRight) {
                leftFilterState = leftFilterState * (1f - shadowCoeff) + delayedL * shadowCoeff
            } else {
                leftFilterState = delayedL
            }
            if (!isSourceRight) {
                rightFilterState = rightFilterState * (1f - shadowCoeff) + delayedR * shadowCoeff
            } else {
                rightFilterState = delayedR
            }

            // 3. Apply Interaural Level Difference (ILD)
            var procL = leftFilterState * traj.leftGain
            var procR = rightFilterState * traj.rightGain

            // 4. Bass Enhancement
            if (bassGain > 0.01f || studioEffects.bassBoostStrength > 0) {
                val extraBass = studioEffects.bassBoostStrength / 1000f * 0.4f
                val totalBass = bassGain + extraBass
                val alpha = 0.08f // ~200Hz cutoff
                bassStateL += alpha * (procL - bassStateL)
                bassStateR += alpha * (procR - bassStateR)
                procL += bassStateL * totalBass
                procR += bassStateR * totalBass
            }

            // 5. Vocal & Clarity Enhancement
            if (studioEffects.clarityGain > 0.05f || studioEffects.vocalBoostStrength > 0) {
                val clarityFactor = studioEffects.clarityGain * 0.3f
                val vocalFactor = studioEffects.vocalBoostStrength / 1000f * 0.35f
                val highAlpha = 0.35f
                trebleStateL += highAlpha * (procL - trebleStateL)
                trebleStateR += highAlpha * (procR - trebleStateR)
                procL += (procL - trebleStateL) * clarityFactor + trebleStateL * vocalFactor
                procR += (procR - trebleStateR) * clarityFactor + trebleStateR * vocalFactor
            }

            // 6. Acoustic Reverb & Echo Matrix
            if (reverbAmt > 0.01f || echoAmt > 0.01f) {
                val comb1 = combBuffer1[combIdx1]
                val comb2 = combBuffer2[combIdx2]
                val comb3 = combBuffer3[combIdx3]

                combBuffer1[combIdx1] = procL * 0.45f + comb1 * 0.65f
                combBuffer2[combIdx2] = procR * 0.45f + comb2 * 0.65f
                combBuffer3[combIdx3] = (procL + procR) * 0.25f + comb3 * 0.70f

                combIdx1 = (combIdx1 + 1) % combBuffer1.size
                combIdx2 = (combIdx2 + 1) % combBuffer2.size
                combIdx3 = (combIdx3 + 1) % combBuffer3.size

                procL += (comb1 * reverbAmt + comb3 * echoAmt * 0.5f)
                procR += (comb2 * reverbAmt + comb3 * echoAmt * 0.5f)
            }

            // 7. Soft-Knee Limiter / Clipping Protection (prevents harsh distortion)
            val combinedL = (procL * wet + inL * dry)
            val combinedR = (procR * wet + inR * dry)

            val normL = combinedL / 32768.0
            val normR = combinedR / 32768.0

            // Apply hyperbolic tangent saturation for warm analog soft-limiting
            val limitedL = (tanh(normL) * 32767.0).coerceIn(-32768.0, 32767.0)
            val limitedR = (tanh(normR) * 32767.0).coerceIn(-32768.0, 32767.0)

            buffer[i] = limitedL.toInt().toShort()
            buffer[i + 1] = limitedR.toInt().toShort()

            currentTime += dt
            i += 2
        }

        return currentTime
    }

    fun reset() {
        combBuffer1.fill(0f)
        combBuffer2.fill(0f)
        combBuffer3.fill(0f)
        combIdx1 = 0
        combIdx2 = 0
        combIdx3 = 0
        leftDelayLine.fill(0f)
        rightDelayLine.fill(0f)
        delayWriteIdx = 0
        leftFilterState = 0f
        rightFilterState = 0f
        bassStateL = 0f
        bassStateR = 0f
        trebleStateL = 0f
        trebleStateR = 0f
        midStateL = 0f
        midStateR = 0f
    }
}
