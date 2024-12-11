package com.bignerdranch.android.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var soundEffectHelper: SoundEffectHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SoundEffectHelper
        soundEffectHelper = SoundEffectHelper()
        soundEffectHelper.loadSound(this, R.raw.button_click)

        // Start background music
        BackgroundMusicHelper.play(this, R.raw.background)

        // Button to start the game
        val startGameButton: Button = findViewById(R.id.btnPlayGame)
        startGameButton.setOnClickListener {
            soundEffectHelper.playSound(R.raw.button_click) // Play button click sound
            val intent = Intent(this, GameModeActivity::class.java)
            startActivity(intent)
        }

        // Settings button
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            soundEffectHelper.playSound(R.raw.button_click) // Play button click sound
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Exit button
        val exitButton: Button = findViewById(R.id.btnExit)
        exitButton.setOnClickListener {
            soundEffectHelper.playSound(R.raw.button_click) // Play button click sound
            BackgroundMusicHelper.stop() // Stop music when exiting the app
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume background music if paused
        BackgroundMusicHelper.play(this, R.raw.background)
    }

    override fun onPause() {
        super.onPause()
        // Pause background music when leaving this activity
        BackgroundMusicHelper.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop and release background music resources when the app is destroyed
        BackgroundMusicHelper.stop()
        soundEffectHelper.release() // Release sound resources
    }
}

