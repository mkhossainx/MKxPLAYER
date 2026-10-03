package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.media.session.MediaSession
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.playback.AudioPlayerEngine

class SpatialPlaybackService : Service() {

    private val binder = LocalBinder()
    private var mediaSession: MediaSession? = null
    var playerEngine: AudioPlayerEngine? = null

    inner class LocalBinder : Binder() {
        fun getService(): SpatialPlaybackService = this@SpatialPlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        mediaSession = MediaSession(this, "MKxPLAYER_MediaSession").apply {
            setCallback(object : MediaSession.Callback() {
                override fun onPlay() {
                    playerEngine?.togglePlayPause()
                }
                override fun onPause() {
                    playerEngine?.togglePlayPause()
                }
                override fun onSkipToNext() {
                    playerEngine?.skipNext()
                }
                override fun onSkipToPrevious() {
                    playerEngine?.skipPrevious()
                }
            })
            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> playerEngine?.togglePlayPause()
            ACTION_NEXT -> playerEngine?.skipNext()
            ACTION_PREV -> playerEngine?.skipPrevious()
            ACTION_STOP -> stopForeground(STOP_FOREGROUND_REMOVE)
        }
        return START_NOT_STICKY
    }

    fun updateNotification(title: String, artist: String, modeLabel: String, isPlaying: Boolean) {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = Intent(this, SpatialPlaybackService::class.java).apply { action = ACTION_PREV }
        val prevPendingIntent = PendingIntent.getService(this, 1, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val playPauseIntent = Intent(this, SpatialPlaybackService::class.java).apply { action = ACTION_PLAY_PAUSE }
        val playPausePendingIntent = PendingIntent.getService(this, 2, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, SpatialPlaybackService::class.java).apply { action = ACTION_NEXT }
        val nextPendingIntent = PendingIntent.getService(this, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Custom cyber album art placeholder icon
        val artBitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(artBitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.parseColor("#0F172A")
            isAntiAlias = true
        }
        canvas.drawCircle(64f, 64f, 64f, paint)
        paint.color = android.graphics.Color.parseColor("#00F2FE")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        canvas.drawCircle(64f, 64f, 48f, paint)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$artist • [$modeLabel Spatial]")
            .setSubText("MKxPLAYER")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setLargeIcon(artBitmap)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (isPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.release()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MKxPLAYER Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Audio playback controls and 8D/16D/24D spatial processing"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "mkxplayer_playback_channel"
        const val NOTIFICATION_ID = 8801
        const val ACTION_PLAY_PAUSE = "com.bizft.mkxplayer.action.PLAY_PAUSE"
        const val ACTION_NEXT = "com.bizft.mkxplayer.action.NEXT"
        const val ACTION_PREV = "com.bizft.mkxplayer.action.PREV"
        const val ACTION_STOP = "com.bizft.mkxplayer.action.STOP"
    }
}
