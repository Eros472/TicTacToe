package com.bignerdranch.android.tictactoe

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var aiOpponent = true // True = AI opponent, false = player vs player
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var soundEffectHelper: SoundEffectHelper

    // Game variables
    private lateinit var boardView: TicTacToeBoardView
    private lateinit var resetButton: Button
    private lateinit var exitButton: Button
    private lateinit var statusTextView: TextView
    private var currentPlayer = "X"
    private var gameOver = false
    private var aiDifficulty = 3 // Default difficulty level (Medium)
    private var soundEffectsEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize SoundEffectHelper
        soundEffectHelper = SoundEffectHelper()
        soundEffectHelper.loadSound(this, R.raw.player_pop)
        soundEffectHelper.loadSound(this, R.raw.cpu_pop)
        soundEffectHelper.loadSound(this, R.raw.winning)
        soundEffectHelper.loadSound(this, R.raw.draw)
        soundEffectHelper.loadSound(this, R.raw.reset)
        soundEffectHelper.loadSound(this, R.raw.failure)
        soundEffectHelper.loadSound(this, R.raw.button_click)

        // Retrieve the game mode (PvP or PvCPU)
        aiOpponent = intent.getStringExtra("GAME_MODE") == "PVC"

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TicTacToePreferences", MODE_PRIVATE)
        loadPreferences()

        // Initialize views
        boardView = findViewById(R.id.ticTacToeBoard)
        resetButton = findViewById(R.id.btnReset)
        exitButton = findViewById(R.id.btnExit)
        statusTextView = findViewById(R.id.tvStatus)

        // Set initial status
        statusTextView.text = getString(R.string.status_player_x_turn)

        resetButton.setOnClickListener {
            soundEffectHelper.playSound(R.raw.reset) // Reset sound effect
            resetGame()
        }

        exitButton.setOnClickListener {
            soundEffectHelper.playSound(R.raw.button_click, 1.5f) // Exit sound effect
            finish()
        }

        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }



        boardView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !gameOver) {
                handlePlayerMove(event)
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        // Adjust music volume for gameplay
        val volume = sharedPreferences.getInt("Volume", 50) / 100.0f
        BackgroundMusicHelper.setVolume(volume * 0.2f) // Lower for gameplay
        BackgroundMusicHelper.play(this, R.raw.background)
    }

    override fun onResume() {
        super.onResume()
        // Reload preferences when returning from settings
        loadPreferences()
    }

    override fun onStop() {
        super.onStop()
        // Restore full volume when leaving the game
        val volume = sharedPreferences.getInt("Volume", 50) / 100.0f
        BackgroundMusicHelper.setVolume(volume)
    }

    private fun loadPreferences() {
        soundEffectsEnabled = sharedPreferences.getBoolean("SoundEffects", true)
        val difficulty = sharedPreferences.getString("CpuDifficulty", "Medium") ?: "Medium"
        configureAIDifficulty(difficulty)
    }

    private fun resetGame() {
        boardView.resetBoard()
        currentPlayer = "X"
        statusTextView.text = getString(R.string.status_player_x_turn)
        gameOver = false
    }

    private fun configureAIDifficulty(difficulty: String) {
        aiDifficulty = when (difficulty) {
            "Easy" -> 1
            "Medium" -> 3
            "Hard" -> 9
            else -> 3
        }
    }

    private fun playSound(resId: Int, volumeScale: Float = 1.0f) {
        soundEffectHelper.playSound(resId, volumeScale)
    }

    private fun handlePlayerMove(event: MotionEvent) {
        if (currentPlayer == "X") {
            playSound(R.raw.player_pop, 3.0f)
        } else if (!aiOpponent) {
            playSound(R.raw.cpu_pop) // O player sound
        }

        if (boardView.handleTouchEvent(event, currentPlayer)) {
            if (boardView.checkWin(currentPlayer)) {
                playSound(if (currentPlayer == "X") R.raw.winning else R.raw.failure)
                statusTextView.text = if (currentPlayer == "X") {
                    getString(R.string.status_player_x_wins)
                } else {
                    getString(R.string.status_player_o_wins)
                }
                gameOver = true
            } else if (isBoardFull(boardView.getBoard())) {
                playSound(R.raw.draw) // Draw sound effect
                statusTextView.text = getString(R.string.status_draw)
                gameOver = true
            } else {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                statusTextView.text = if (currentPlayer == "X") {
                    getString(R.string.status_player_x_turn)
                } else {
                    getString(R.string.status_player_o_turn)
                }

                // AI makes a move
                if (aiOpponent && currentPlayer == "O") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        playSound(R.raw.cpu_pop)
                        makeAIMove()
                    }, 500)
                }
            }
            boardView.performClick()
        }
    }

    private fun makeAIMove() {
        if (gameOver) return

        when (aiDifficulty) {
            1 -> makeRandomMove()
            else -> {
                val board = boardView.getBoard()
                val bestMove = findBestMove(board)
                if (bestMove != null) {
                    boardView.placeMove(bestMove.first, bestMove.second, currentPlayer)
                    if (boardView.checkWin(currentPlayer)) {
                        playSound(R.raw.failure)
                        statusTextView.text = getString(R.string.status_player_o_wins)
                        gameOver = true
                    } else if (isBoardFull(board)) {
                        playSound(R.raw.draw)
                        statusTextView.text = getString(R.string.status_draw)
                        gameOver = true
                    } else {
                        currentPlayer = "X"
                        statusTextView.text = getString(R.string.status_player_x_turn)
                    }
                }
            }
        }
    }

    private fun makeRandomMove() {
        val board = boardView.getBoard()
        val availableMoves = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == null) availableMoves.add(Pair(i, j))
            }
        }
        if (availableMoves.isNotEmpty()) {
            val (row, col) = availableMoves.random()
            boardView.placeMove(row, col, currentPlayer)
            if (boardView.checkWin(currentPlayer)) {
                playSound(R.raw.failure)
                statusTextView.text = getString(R.string.status_player_o_wins)
                gameOver = true
            } else if (isBoardFull(board)) {
                playSound(R.raw.draw)
                statusTextView.text = getString(R.string.status_draw)
                gameOver = true
            } else {
                currentPlayer = "X"
                statusTextView.text = getString(R.string.status_player_x_turn)
            }
        }
    }


    private fun findBestMove(board: Array<Array<String?>>): Pair<Int, Int>? {
        var bestVal = Int.MIN_VALUE
        var bestMove: Pair<Int, Int>? = null
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == null) {
                    board[i][j] = "O"
                    val moveVal = minimax(board, 0, false, aiDifficulty)
                    board[i][j] = null
                    if (moveVal > bestVal) {
                        bestMove = Pair(i, j)
                        bestVal = moveVal
                    }
                }
            }
        }
        return bestMove
    }

    private fun minimax(
        board: Array<Array<String?>>,
        depth: Int,
        isMaximizing: Boolean,
        maxDepth: Int
    ): Int {
        val score = evaluateBoard(board)
        if (score == 10 || score == -10 || isBoardFull(board) || depth >= maxDepth) return score

        return if (isMaximizing) {
            var best = Int.MIN_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == null) {
                        board[i][j] = "O"
                        best = maxOf(best, minimax(board, depth + 1, false, maxDepth))
                        board[i][j] = null
                    }
                }
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == null) {
                        board[i][j] = "X"
                        best = minOf(best, minimax(board, depth + 1, true, maxDepth))
                        board[i][j] = null
                    }
                }
            }
            best
        }
    }

    private fun evaluateBoard(board: Array<Array<String?>>): Int {
        for (i in 0..2) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != null) {
                return if (board[i][0] == "O") 10 else -10
            }
        }
        for (i in 0..2) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != null) {
                return if (board[0][i] == "O") 10 else -10
            }
        }
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != null) {
            return if (board[0][0] == "O") 10 else -10
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != null) {
            return if (board[0][2] == "O") 10 else -10
        }
        return 0
    }

    private fun isBoardFull(board: Array<Array<String?>>): Boolean {
        for (row in board) {
            for (cell in row) {
                if (cell == null) return false
            }
        }
        return true
    }
}

