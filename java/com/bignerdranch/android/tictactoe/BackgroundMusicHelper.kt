package com.bignerdranch.android.tictactoe

import android.content.Context
import android.media.MediaPlayer

object BackgroundMusicHelper {
    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null

    /**
     * Plays the background music if not already playing.
     * @param context: Context required to create MediaPlayer
     * @param resId: Resource ID of the audio file to play
     * @param loop: Whether the music should loop
     */
    @Synchronized
    fun play(context: Context, resId: Int, loop: Boolean = true) {
        try {
            if (mediaPlayer == null || currentResId != resId) {
                stop()
                currentResId = resId
                mediaPlayer = MediaPlayer.create(context, resId).apply {
                    isLooping = loop
                    start()
                }
            } else if (!mediaPlayer!!.isPlaying) {
                mediaPlayer!!.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Pauses the currently playing music.
     */
    @Synchronized
    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    /**
     * Stops the background music and releases MediaPlayer resources.
     */
    @Synchronized
    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        currentResId = null
    }

    /**
     * Adjusts the volume of the background music.
     * @param volume: Float value between 0.0 (mute) and 1.0 (full volume)
     */
    @Synchronized
    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    /**
     * Releases MediaPlayer resources explicitly.
     */
    @Synchronized
    fun release() {
        stop()
    }

    /**
     * Checks if the background music is currently playing.
     * @return Boolean indicating whether music is playing
     */
    @Synchronized
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
