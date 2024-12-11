package com.bignerdranch.android.tictactoe

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var soundEffectsSwitch: Switch
    private lateinit var volumeLabel: TextView
    private lateinit var themeRadioGroup: RadioGroup
    private lateinit var soundEffectHelper: SoundEffectHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TicTacToePreferences", MODE_PRIVATE)

        // Initialize SoundEffectHelper
        soundEffectHelper = SoundEffectHelper()
        soundEffectHelper.loadSound(this, R.raw.button_click)

        // Initialize UI elements
        volumeSeekBar = findViewById(R.id.seekBarVolume)
        soundEffectsSwitch = findViewById(R.id.switchSoundEffects)
        volumeLabel = findViewById(R.id.tvVolumeLabel)
        themeRadioGroup = findViewById(R.id.radioGroupTheme)

        // Load saved preferences
        val savedVolume = sharedPreferences.getInt("Volume", 50) // Default volume is 50
        val soundEffectsEnabled = sharedPreferences.getBoolean("SoundEffects", true)
        val savedTheme = sharedPreferences.getString("Theme", "Light") ?: "Light"

        // Set initial values for UI elements
        volumeSeekBar.progress = savedVolume
        soundEffectsSwitch.isChecked = soundEffectsEnabled
        volumeLabel.text = "Volume: $savedVolume%"
        setRadioGroupSelection(savedTheme)

        // Volume adjustment listener
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                saveVolume(progress)
                val volume = progress / 100.0f
                BackgroundMusicHelper.setVolume(volume) // Adjust music volume in real-time
                volumeLabel.text = "Volume: $progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                soundEffectHelper.playSound(R.raw.button_click) // Play button click sound
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Sound effects toggle listener
        soundEffectsSwitch.setOnCheckedChangeListener { _, isChecked ->
            soundEffectHelper.playSound(R.raw.button_click) // Play button click sound
            saveSoundEffects(isChecked)
        }

        // Theme selection listener
        themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            soundEffectHelper.playSound(R.raw.button_click) // Play button click sound
            val theme = when (checkedId) {
                R.id.radioLight -> "Light"
                R.id.radioDark -> "Dark"
                R.id.radioHighContrast -> "High Contrast"
                else -> "Light"
            }
            saveTheme(theme)
            //applyTheme(theme) // Apply the selected theme
        }
    }

    override fun onStart() {
        super.onStart()
        // Start or resume the background music
        BackgroundMusicHelper.play(this, R.raw.background)
    }

    override fun onStop() {
        super.onStop()
        // Allow music to keep playing when leaving the activity
    }

    override fun onDestroy() {
        super.onDestroy()
        soundEffectHelper.release() // Release resources to avoid memory leaks
    }

    private fun saveVolume(volume: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("Volume", volume)
        editor.apply()
    }

    private fun saveSoundEffects(isEnabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("SoundEffects", isEnabled)
        editor.apply()
    }

    private fun saveTheme(theme: String) {
        val editor = sharedPreferences.edit()
        editor.putString("Theme", theme)
        editor.apply()
    }

    private fun applyTheme(theme: String) {
        when (theme) {
            //"Light" -> setTheme(R.style.LightTheme)
           // "Dark" -> setTheme(R.style.DarkTheme)
           // "High Contrast" -> setTheme(R.style.HighContrastTheme)
        }
        recreate() // Recreate the activity to apply the theme
    }

    private fun setRadioGroupSelection(theme: String) {
        val selectedId = when (theme) {
            "Light" -> R.id.radioLight
            "Dark" -> R.id.radioDark
            "High Contrast" -> R.id.radioHighContrast
            else -> R.id.radioLight
        }
        themeRadioGroup.check(selectedId)
    }
}
