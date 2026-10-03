package com.example

import com.example.engine.SpatialAudioProcessor
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPcmBlockProcessing() {
        val processor = SpatialAudioProcessor()
        val buffer = ShortArray(100) { 1000 }
        val nextTime = processor.processPcmBlock(
            buffer = buffer,
            length = buffer.size,
            sampleRate = 44100,
            startTimeSec = 0.0,
            mode = SpatialMode.SPATIAL_8D,
            params = SpatialParams.DEFAULT_8D
        )
        assertTrue(nextTime > 0.0)
    }

    @Test
    fun testSpatialModesDefinitions() {
        assertEquals("8D", SpatialMode.SPATIAL_8D.dimensionLabel)
        assertEquals("16D", SpatialMode.SPATIAL_16D.dimensionLabel)
        assertEquals("24D", SpatialMode.SPATIAL_24D.dimensionLabel)
    }
}
