package com.example.engine

import android.content.Context
import com.example.model.AudioTrackItem
import com.example.model.SpatialMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import kotlin.math.PI
import kotlin.math.sin

/**
 * Generates synthetic harmonic audio tracks with embedded synchronized LRC lyrics
 * so MKxPLAYER can be fully tested and experienced out-of-the-box in 8D, 16D, and 24D.
 */
class DemoAudioGenerator(private val context: Context) {

    suspend fun getOrCreateDemoTracks(): List<AudioTrackItem> = withContext(Dispatchers.IO) {
        val demoDir = File(context.cacheDir, "mkx_demos")
        if (!demoDir.exists()) demoDir.mkdirs()

        val track1File = File(demoDir, "demo_8d_cyber_odyssey.wav")
        if (!track1File.exists() || track1File.length() < 1000) {
            generateHarmonicTrack(track1File, baseFreq = 220.0, durationSec = 35)
        }

        val track2File = File(demoDir, "demo_16d_infinity_resonance.wav")
        if (!track2File.exists() || track2File.length() < 1000) {
            generateHarmonicTrack(track2File, baseFreq = 293.66, durationSec = 40)
        }

        val track3File = File(demoDir, "demo_24d_hyper_vortex.wav")
        if (!track3File.exists() || track3File.length() < 1000) {
            generateHarmonicTrack(track3File, baseFreq = 174.61, durationSec = 45)
        }

        val lrc8D = """
            [00:01.00]MKxPLAYER Spatial Engine Initialized
            [00:04.00]Wear stereo headphones for full 8D orbit
            [00:08.50]Notice the sound revolving smoothly around your head
            [00:13.00]Binaural panning moving from left to right ear
            [00:18.00]Interaural Time Difference: Sub-millisecond precision
            [00:23.00]Feel the acoustic orbit 360 degrees
            [00:28.00]Your music, in every dimension
            [00:32.00]POWERED BY BIZ FACTORY
        """.trimIndent()

        val lrc16D = """
            [00:01.00]16D Dynamic Spatial Stage Activated
            [00:05.00]Traversing a multi-axis figure-8 infinity loop
            [00:10.00]Sound elevating above and undulating below ears
            [00:16.00]Concert stage acoustic depth immersion
            [00:21.00]Stereo soundstage expanding through center
            [00:27.00]Dynamic phase modulation in full effect
            [00:33.00]MKxPLAYER: Developed by Mk Hossain
            [00:38.00]Pure binaural sonic freedom
        """.trimIndent()

        val lrc24D = """
            [00:01.00]24D Hyper-Spatial Vortex Mode Engaged
            [00:06.00]Spherical 3D audio vortex rotating around listener
            [00:11.00]Acoustic room reflection & spatial comb reverb
            [00:17.00]Low frequency bass resonance expanding
            [00:23.00]Multi-angle orbital trajectory contracting & exploding
            [00:30.00]Enter the hyper-dimensional acoustic space
            [00:37.00]Your music, in every dimension
            [00:42.00]MKxPLAYER - POWERED BY BIZ FACTORY
        """.trimIndent()

        listOf(
            AudioTrackItem(
                id = 9001L,
                title = "Cyber Odyssey [8D Master]",
                artist = "Mk Hossain",
                album = "Dimensions 8D",
                durationMs = 35000L,
                path = track1File.absolutePath,
                uriString = track1File.toURI().toString(),
                isDemo = true,
                spatialMode = SpatialMode.SPATIAL_8D,
                folderName = "MKx Demos",
                lrcContent = lrc8D
            ),
            AudioTrackItem(
                id = 9002L,
                title = "Infinity Resonance [16D Stage]",
                artist = "Mk Hossain",
                album = "Dimensions 16D",
                durationMs = 40000L,
                path = track2File.absolutePath,
                uriString = track2File.toURI().toString(),
                isDemo = true,
                spatialMode = SpatialMode.SPATIAL_16D,
                folderName = "MKx Demos",
                lrcContent = lrc16D
            ),
            AudioTrackItem(
                id = 9003L,
                title = "Hyper Void [24D Vortex]",
                artist = "Mk Hossain",
                album = "Dimensions 24D",
                durationMs = 45000L,
                path = track3File.absolutePath,
                uriString = track3File.toURI().toString(),
                isDemo = true,
                spatialMode = SpatialMode.SPATIAL_24D,
                folderName = "MKx Demos",
                lrcContent = lrc24D
            )
        )
    }

    private fun generateHarmonicTrack(file: File, baseFreq: Double, durationSec: Int) {
        val sampleRate = 44100
        val numSamples = sampleRate * durationSec
        val fos = FileOutputStream(file)
        fos.write(ByteArray(44)) // WAV header placeholder

        val buffer = ByteArray(4096)
        var bufferIdx = 0
        var totalBytes = 0L

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Rich multi-harmonic chord: root, fifth, octave, minor third + low vibrato
            val harmonic1 = sin(2 * PI * baseFreq * t)
            val harmonic2 = 0.6 * sin(2 * PI * (baseFreq * 1.5) * t)
            val harmonic3 = 0.4 * sin(2 * PI * (baseFreq * 2.0) * t)
            val subBass = 0.5 * sin(2 * PI * (baseFreq * 0.5) * t)
            val chord = 0.3 * sin(2 * PI * (baseFreq * 1.25) * t + sin(2 * PI * 2.0 * t) * 0.1)

            // Dynamic pulse envelope
            val envelope = 0.75 + 0.25 * sin(2 * PI * 0.5 * t)
            val sampleVal = ((harmonic1 + harmonic2 + harmonic3 + subBass + chord) / 2.8 * envelope * 24000.0).toInt()
                .coerceIn(-32767, 32767).toShort()

            // Stereo Left
            buffer[bufferIdx++] = (sampleVal.toInt() and 0xFF).toByte()
            buffer[bufferIdx++] = ((sampleVal.toInt() shr 8) and 0xFF).toByte()
            // Stereo Right (subtle pitch chorus)
            val sampleValR = (sampleVal * 0.95).toInt().toShort()
            buffer[bufferIdx++] = (sampleValR.toInt() and 0xFF).toByte()
            buffer[bufferIdx++] = ((sampleValR.toInt() shr 8) and 0xFF).toByte()

            if (bufferIdx >= buffer.size) {
                fos.write(buffer, 0, bufferIdx)
                totalBytes += bufferIdx
                bufferIdx = 0
            }
        }

        if (bufferIdx > 0) {
            fos.write(buffer, 0, bufferIdx)
            totalBytes += bufferIdx
        }

        fos.flush()
        fos.close()

        // Patch WAV Header
        val totalDataLen = totalBytes + 36
        val byteRate = sampleRate * 2 * 2
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0 // PCM
        header[22] = 2; header[23] = 0 // 2 channels
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = 4; header[33] = 0 // block align
        header[34] = 16; header[35] = 0 // 16-bit
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (totalBytes and 0xff).toByte()
        header[41] = ((totalBytes shr 8) and 0xff).toByte()
        header[42] = ((totalBytes shr 16) and 0xff).toByte()
        header[43] = ((totalBytes shr 24) and 0xff).toByte()

        val raf = RandomAccessFile(file, "rw")
        raf.seek(0)
        raf.write(header)
        raf.close()
    }
}
