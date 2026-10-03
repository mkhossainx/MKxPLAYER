package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.SpatialAudioProcessor
import com.example.model.SpatialMode
import com.example.model.SpatialParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MKxPLAYER", appName)
    }

    @Test
    fun `spatial processor calculates 8D circular trajectory properly`() {
        val processor = SpatialAudioProcessor()
        val p0 = processor.calculateTrajectory(0.0, SpatialMode.SPATIAL_8D, SpatialParams.DEFAULT_8D)
        val pQuarter = processor.calculateTrajectory(2.5, SpatialMode.SPATIAL_8D, SpatialParams.DEFAULT_8D)

        assertTrue(p0.leftGain > 0f)
        assertTrue(p0.rightGain > 0f)
        assertTrue(pQuarter.azimuthDeg != p0.azimuthDeg)
    }

    @Test
    fun `spatial processor handles 16D figure-8 and 24D vortex`() {
        val processor = SpatialAudioProcessor()
        val p16D = processor.calculateTrajectory(1.0, SpatialMode.SPATIAL_16D, SpatialParams.DEFAULT_16D)
        val p24D = processor.calculateTrajectory(1.0, SpatialMode.SPATIAL_24D, SpatialParams.DEFAULT_24D)

        assertTrue(p16D.distance > 0f)
        assertTrue(p24D.distance > 0f)
    }
}
