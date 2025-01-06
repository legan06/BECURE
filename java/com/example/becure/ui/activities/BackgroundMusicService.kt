package com.example.becure.ui.activities

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.becure.R

class BackgroundMusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // MediaPlayer'ı başlat
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music) // raw klasöründeki dosya
        mediaPlayer?.isLooping = true // Döngüye alma
        mediaPlayer?.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
