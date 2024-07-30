package com.example.multiplayergame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.multiplayergame.GamingScreen.PlayCard
import com.example.multiplayergame.GamingScreen.PlayGuessCard
import com.example.multiplayergame.GamingScreen.PlayMatchingCard
import com.example.multiplayergame.GamingScreen.PlayTicToe
import com.example.multiplayergame.JoinGame.MainScreen
import com.example.multiplayergame.screenUtil.JoinGame
import com.example.multiplayergame.ui.theme.MultiplayerGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            JoinGame(this, navController = navController )
            MainScreen(activity = this, navController = navController )
            val game = intent.getStringExtra("gameName")
            val name = intent.getStringExtra("user")
            MultiplayerGameTheme {
                when (game) {
                    "CardGame" -> {
                        if (name != null) {
                            PlayCard(name,navController)
                        }
                    }
                    "MatchingCard" -> {
                        if (name != null) {
                            PlayMatchingCard(name)
                        }
                    }
                    "TicToe" -> {
                        PlayTicToe()
                    }
                    "GuessCard" -> {
                        PlayGuessCard()
                    }
                    else -> {

                    }
                }
            }
        }
    }
}


