package com.bignerdranch.android.tictactoe

import android.content.Context
import android.media.MediaPlayer

class SoundEffectHelper {
    private val sounds = mutableMapOf<Int, MediaPlayer>()

    fun loadSound(context: Context, resId: Int) {
        if (!sounds.containsKey(resId)) {
            sounds[resId] = MediaPlayer.create(context, resId)
        }
    }

    fun playSound(resId: Int, volumeScale: Float = 1.0f) {
        val mediaPlayer = sounds[resId]
        if (mediaPlayer != null) {
            val scaledVolume = volumeScale.coerceIn(0.0f, 1.0f)
            mediaPlayer.setVolume(scaledVolume, scaledVolume)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                it.stop()
                it.prepareAsync() // Reset the MediaPlayer for future use
            }
        }
    }

    fun release() {
        for (mediaPlayer in sounds.values) {
            mediaPlayer.release()
        }
        sounds.clear()
    }
}

