package com.bignerdranch.android.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GameModeActivity : AppCompatActivity() {

    private lateinit var soundEffectHelper: SoundEffectHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_mode)

        // Initialize SoundEffectHelper
        soundEffectHelper = SoundEffectHelper()
        soundEffectHelper.loadSound(this, R.raw.button_click)

        // Button for Player vs Player mode
        val playerVsPlayerButton: Button = findViewById(R.id.btnPlayerVsPlayer)
        playerVsPlayerButton.setOnClickListener {
            soundEffectHelper.playSound(R.raw.button_click) // Play button click sound
            // Navigate to GameActivity for Player vs Player mode
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("GAME_MODE", "PVP")
            startActivity(intent)
        }

        // Button for Player vs CPU mode
        val playerVsCPUButton: Button = findViewById(R.id.btnPlayerVsCPU)
        playerVsCPUButton.setOnClickListener {
            soundEffectHelper.playSound(R.raw.button_click) // Play button click sound
            // Navigate to GameActivity for Player vs CPU mode
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("GAME_MODE", "PVC")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure background music resumes when this activity is active
        BackgroundMusicHelper.play(this, R.raw.background)
    }

    override fun onPause() {
        super.onPause()
        // Pause background music when leaving this activity
        BackgroundMusicHelper.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundEffectHelper.release() // Release sound resources
    }
}

