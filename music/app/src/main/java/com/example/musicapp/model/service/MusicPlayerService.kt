package com.example.musicapp.model.service

import android.app.*
import android.content.*
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import android.support.v4.media.session.MediaSessionCompat
import com.example.musicapp.R
import com.example.musicapp.view.activity.MainActivity

class MusicPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat

    companion object {
        const val CHANNEL_ID = "MusicPlayerChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "START"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_STOP = "STOP"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "MusicService")
        mediaPlayer = MediaPlayer.create(this, R.raw.dancin) // Заменить на свой трек
        mediaPlayer?.isLooping = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startMusic()
                updateNotification(true)
            }
            ACTION_PAUSE -> {
                pauseMusic()
                updateNotification(false)
            }
            ACTION_STOP -> {
                stopMusic()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startMusic() {
        try {
            mediaPlayer?.start()
            startForeground(NOTIFICATION_ID, createNotification(true))
        } catch (e: Exception) {
            Log.e("MusicService", "Ошибка при запуске музыки: ${e.message}")
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        updateNotification(false)
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification(isPlaying: Boolean) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification(isPlaying))
    }

    private fun createNotification(isPlaying: Boolean): Notification {
        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playIntent = PendingIntent.getService(
            this, 1, Intent(this, MusicPlayerService::class.java).setAction(ACTION_START),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = PendingIntent.getService(
            this, 2, Intent(this, MusicPlayerService::class.java).setAction(ACTION_PAUSE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 3, Intent(this, MusicPlayerService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pauseIntent)
        } else {
            NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", playIntent)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Aaron Smith - Dancin")
            .setContentText(if (isPlaying) "Playing" else "Paused")
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentIntent(mainPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(playPauseAction)
            .addAction(NotificationCompat.Action(android.R.drawable.ic_delete, "Stop", stopIntent))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1)
            )
            .setOngoing(isPlaying)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Music Player Channel", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for music player notifications"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d("MusicService", "Notification Channel created")
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}