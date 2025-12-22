package com.alrubaye.tictactoeapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alrubaye.tictactoeapp.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Game State
    private var activePlayer = 1
    private var player1Moves = ArrayList<Int>()
    private var player2Moves = ArrayList<Int>()
    private var isVsBot = true
    private var gameActive = true

    // Scores
    private var player1Score = 0
    private var player2Score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Mode Selection
        updateModeUI()
        binding.btn2Players.setOnClickListener {
            isVsBot = false
            resetGame(fullReset = true)
            updateModeUI()
        }
        binding.btnVsBot.setOnClickListener {
            isVsBot = true
            resetGame(fullReset = true)
            updateModeUI()
        }

        // Restart Button
        binding.btnRestart.setOnClickListener {
            resetGame()
        }

        // Grid Buttons
        val buttons = listOf(
            binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9
        )

        for ((index, button) in buttons.withIndex()) {
            val cellId = index + 1
            button.setOnClickListener {
                if (gameActive) {
                    playGame(cellId, button)
                }
            }
        }
    }

    private fun updateModeUI() {
        if (isVsBot) {
            binding.tvPlayer2Label.text = "BOT (O)"
            binding.btnVsBot.setBackgroundResource(R.drawable.bg_button_rounded)
            binding.btnVsBot.background.setTint(ContextCompat.getColor(this, R.color.purple_500))
            binding.btn2Players.setBackgroundResource(R.drawable.bg_button_rounded)
            binding.btn2Players.background.setTintList(null) // clear tint
        } else {
            binding.tvPlayer2Label.text = "PLAYER O"
            binding.btn2Players.setBackgroundResource(R.drawable.bg_button_rounded)
            binding.btn2Players.background.setTint(ContextCompat.getColor(this, R.color.purple_500))
            binding.btnVsBot.setBackgroundResource(R.drawable.bg_button_rounded)
            binding.btnVsBot.background.setTintList(null)
        }
        resetScores()
    }

    private fun resetScores() {
        player1Score = 0
        player2Score = 0
        updateScoreBoard()
    }

    private fun updateScoreBoard() {
        binding.tvPlayer1Score.text = player1Score.toString()
        binding.tvPlayer2Score.text = player2Score.toString()
        
        // Highlight active player
        if (activePlayer == 1) {
             binding.player1Layout.setBackgroundResource(R.drawable.bg_button_rounded)
             // Transparent for inactive
             binding.player2Layout.background = null
        } else {
             binding.player2Layout.setBackgroundResource(R.drawable.bg_button_rounded)
             binding.player1Layout.background = null
        }
    }

    private fun playGame(cellId: Int, buSelected: Button) {
        if (activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setTextColor(ContextCompat.getColor(this, R.color.player_x_color))
            player1Moves.add(cellId)
            buSelected.isEnabled = false
            
            if (checkWinner()) return
            
            activePlayer = 2
            updateScoreBoard()
            
            if (isVsBot && gameActive) {
                autoPlay()
            }
        } else {
            buSelected.text = "O"
            buSelected.setTextColor(ContextCompat.getColor(this, R.color.player_o_color))
            player2Moves.add(cellId)
            buSelected.isEnabled = false
            
            if (checkWinner()) return
            
            activePlayer = 1
            updateScoreBoard()
        }
    }

    private fun checkWinner(): Boolean {
        var winner = -1

        // Winning combinations
        val winningSets = listOf(
            listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9), // Rows
            listOf(1, 4, 7), listOf(2, 5, 8), listOf(3, 6, 9), // Cols
            listOf(1, 5, 9), listOf(3, 5, 7)                   // Diagonals
        )

        for (set in winningSets) {
            if (player1Moves.containsAll(set)) {
                winner = 1
                break
            }
            if (player2Moves.containsAll(set)) {
                winner = 2
                break
            }
        }

        if (winner != -1) {
            gameActive = false
            if (winner == 1) {
                player1Score++
                Toast.makeText(this, "Player X Wins!", Toast.LENGTH_SHORT).show()
            } else {
                player2Score++
                val winnerName = if (isVsBot) "Bot" else "Player O"
                Toast.makeText(this, "$winnerName Wins!", Toast.LENGTH_SHORT).show()
            }
            updateScoreBoard()
            return true
        }
        
        // Draw Check
        if (player1Moves.size + player2Moves.size == 9) {
             gameActive = false
             Toast.makeText(this, "It's a Draw!", Toast.LENGTH_SHORT).show()
             return true
        }

        return false
    }

    private fun autoPlay() {
        val emptyCells = ArrayList<Int>()
        for (cellId in 1..9) {
            if (!player1Moves.contains(cellId) && !player2Moves.contains(cellId)) {
                emptyCells.add(cellId)
            }
        }

        if (emptyCells.isEmpty()) return

        // Simple AI: Random move
        // Possible improvement: Minimax algorithm or strategic blocking
        val r = Random
        val randIndex = r.nextInt(emptyCells.size)
        val cellId = emptyCells[randIndex]

        val buSelected: Button = when (cellId) {
            1 -> binding.btn1
            2 -> binding.btn2
            3 -> binding.btn3
            4 -> binding.btn4
            5 -> binding.btn5
            6 -> binding.btn6
            7 -> binding.btn7
            8 -> binding.btn8
            9 -> binding.btn9
            else -> binding.btn1
        }

        playGame(cellId, buSelected)
    }

    private fun resetGame(fullReset: Boolean = false) {
        activePlayer = 1
        player1Moves.clear()
        player2Moves.clear()
        gameActive = true

        val buttons = listOf(
            binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9
        )

        for (bu in buttons) {
            bu.text = ""
            bu.isEnabled = true
        }
        
        if (fullReset) {
            resetScores()
        }
        updateScoreBoard()
    }
}
