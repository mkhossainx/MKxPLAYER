# MKxPLAYER - Spatial 8D, 16D, and 24D Audio Engine

> **Your music, in every dimension.**  
> *Developed by Mk Hossain*  
> *POWERED BY BIZ FACTORY*

---

## Overview

**MKxPLAYER** is a high-performance modern Android local audio player and powerful on-device spatial audio converter. It transforms standard stereo tracks into immersive **8D**, **16D**, and **24D** audio experiences using binaural spatial panning, Interaural Time Difference (ITD), Interaural Level Difference (ILD), and head-shadow acoustic filtering.

Processing runs **100% on-device** using native DSP algorithms. Zero server uploads. Complete privacy.

---

## Key Features

### 🎧 Spatial Audio Modes
* **8D Classic Mode**: Smooth 360° horizontal circular orbit revolving continuously around the listener's head with sub-millisecond ITD delays.
* **16D Dynamic Spatial Mode**: Multi-axis figure-8 (infinity $\infty$) trajectory undulating vertically above and below the ears with dynamic acoustic depth.
* **24D Hyper-Spatial Vortex Mode**: Spherical 3D vortex combining orbital rotation, distance expansion, acoustic comb reverb, and low-frequency resonance.

### 🎛️ Spatial Converter & Customization Controls
* **Intensity Slider**: Adjust the spatial width and binaural depth (0% to 100%).
* **Orbit Speed**: Control the orbital frequency from ultra-slow (0.04 Hz) to rapid (0.45 Hz).
* **Reverb Amount**: Simulate room and hall acoustics.
* **Echo Amount**: Control early spatial reflections.
* **Bass Boost**: Low-shelf filter for powerful low-end.
* **Wet/Dry Mix**: Blend the original dry signal with the spatialized audio.
* **Acoustic Presets**: Quick access to "Classic 8D Orbit", "Deep Bass 8D", "Concert Stage 16D", "Vortex Extreme 24D", and "Cinema Echo 24D", plus custom user presets.

### 💾 100% On-Device Export & Share
* **Studio Lossless 16-bit WAV**: Professional uncompressed audio format.
* **Direct Share**: Export and share converted 8D/16D/24D audio files directly to messaging apps, social media, or cloud storage via Android's secure `FileProvider`.
* **Zero Network Uploads**: Safe, offline, private audio processing.

### 📊 Real-Time 60fps 3D Visualizer
* Responsive 3D celestial canvas rendering live trajectory orbits and audio nodes.
* 16-band real-time audio reactive spectrum analyzer.
* Real-time HUD displaying azimuth angle, elevation, distance, and ITD delay metrics.

### 🎵 Local Music Player Functionality
* **Broad Audio Support**: MP3, WAV, FLAC, M4A, AAC, and OGG formats.
* **Library Navigation**: Tabs for Songs, Albums, Artists, Folders, Custom Playlists, and Spatial Converted tracks.
* **Synchronized LRC Lyrics**: Live lyric scrolling with active line highlight.
* **Queue Management**: "Up Next" queue with reordering and removal.
* **Background Playback**: Continuous playback with persistent Android Foreground Service notification and lockscreen controls.
* **5-Band Equalizer**: Hardware equalizer with bass boost, virtualizer, and presets.
* **Sleep Timer**: Auto-pause playback after 15, 30, 45, 60, or 90 minutes.

---

## Technical Specifications
* **Language**: Kotlin 2.2.10
* **UI Toolkit**: Jetpack Compose with Material Design 3 (M3 Expressive)
* **Architecture**: Clean Architecture / MVVM (`ViewModel` + `StateFlow`)
* **Local Persistence**: Room Database 2.7.0 (KSP)
* **Audio Engine**: Android `MediaPlayer`, `AudioTrack`, `MediaCodec`, `MediaExtractor`, `Equalizer`, `BassBoost`, `Virtualizer`, and custom DSP `SpatialAudioProcessor`.
* **Minimum SDK**: API 24 (Android 7.0+)
* **Target SDK**: API 36 (Android 15+)
* **Package Name**: `com.bizft.mkxplayer`

---

## Setup & Build Instructions

1. **Clone or Open Project**:
   Open the root project in Android Studio (Ladybug / Iguana or later).
2. **Build the Debug APK**:
   ```bash
   gradle assembleDebug
   ```
3. **Run Unit & Robolectric Tests**:
   ```bash
   gradle testDebugUnitTest
   ```
4. **Install on Device/Emulator**:
   ```bash
   gradle installDebug
   ```

*Note: For the full binaural effect, wear stereo headphones, earbuds, or AirPods.*
