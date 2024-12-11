package com.bignerdranch.android.tictactoe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class TicTacToeBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val gridPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private val xPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private val oPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private var cellSize: Float = 0f
    private var board = Array(3) { arrayOfNulls<String>(3) } // Empty board with null values
    private var currentPlayer = "X"

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        cellSize = width / 3f

        // Draw grid lines
        for (i in 1..2) {
            // Vertical lines
            canvas.drawLine(i * cellSize, 0f, i * cellSize, height.toFloat(), gridPaint)
            // Horizontal lines
            canvas.drawLine(0f, i * cellSize, width.toFloat(), i * cellSize, gridPaint)
        }

        // Draw X and O
        for (row in 0..2) {
            for (col in 0..2) {
                val centerX = (col * cellSize) + cellSize / 2
                val centerY = (row * cellSize) + cellSize / 2

                when (board[row][col]) {
                    "X" -> drawX(canvas, centerX, centerY)
                    "O" -> drawO(canvas, centerX, centerY)
                }
            }
        }
    }

    private fun drawX(canvas: Canvas, centerX: Float, centerY: Float) {
        val offset = cellSize / 4
        canvas.drawLine(
            centerX - offset, centerY - offset,
            centerX + offset, centerY + offset, xPaint
        )
        canvas.drawLine(
            centerX - offset, centerY + offset,
            centerX + offset, centerY - offset, xPaint
        )
    }

    private fun drawO(canvas: Canvas, centerX: Float, centerY: Float) {
        val radius = cellSize / 4
        canvas.drawCircle(centerX, centerY, radius, oPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Handle touch event in GameActivity
        if (event.action == MotionEvent.ACTION_DOWN) {
            performClick()
            return true
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun resetBoard() {
        board = Array(3) { arrayOfNulls<String>(3) } // Reset board to nulls
        currentPlayer = "X"
        invalidate()
    }

    fun checkWin(player: String): Boolean {
        // Check rows, columns, and diagonals for a win
        for (i in 0..2) {
            // Check rows
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true
            // Check columns
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true
        }
        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true

        return false
    }

    fun isBoardFull(): Boolean {
        return board.all { row -> row.all { it != null } }
    }

    fun handleTouchEvent(event: MotionEvent, player: String): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val row = (event.y / cellSize).toInt()
            val col = (event.x / cellSize).toInt()

            if (row in 0..2 && col in 0..2 && board[row][col] == null) {
                board[row][col] = player
                invalidate() // Redraw the board
                return true
            }
        }
        return false
    }
    fun getBoard(): Array<Array<String?>> {
        return board
    }

    // Method to place move programmatically
    fun placeMove(row: Int, col: Int, player: String) {
        if (board[row][col] == null) {
            board[row][col] = player
            invalidate()
        }
    }
}