package com.example.multiplayergame.GamingScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.multiplayergame.JoinGame.game
import com.example.multiplayergame.MainActivity
import com.example.multiplayergame.Model
import com.example.multiplayergame.controller.MatchingCardCotroller
import com.example.multiplayergame.screenUtil.GameStatus
import com.example.multiplayergame.screenUtil.MatchingCardModel

@Composable
fun PlayMatchingCard(name:String) {
    val context = LocalContext.current as MainActivity
    MatchingCardCotroller.fetchMatchingCardModel()
    var gameModel:MatchingCardModel? = null
    MatchingCardCotroller.matchingCard.observe(context){
        gameModel = it
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (gameModel?.gameStatus) {
                GameStatus.INPROGRESS -> "Current Player: ${gameModel?.currentPlayer}"
                GameStatus.FINISHED -> if (gameModel!!.winner.isNotEmpty()) "Winner: ${gameModel!!.winner}" else "Draw"
                else -> "Game Status: ${gameModel?.gameStatus}"
            },
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                letterSpacing = 0.04.sp,
                color = Color(0xFF009688)
            ),
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = {
                if (gameModel?.gameStatus == GameStatus.INPROGRESS){

                } else {
                    Toast.makeText(context, "Action not allowed", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(16.dp),
            enabled = gameModel?.gameStatus == GameStatus.CREATED || gameModel?.gameStatus == GameStatus.JOINED
        ) {
            Text(text = "Start Game")
        }
        LazyColumn (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(gameModel!!.users){
                Card(modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
                    .background(Color.LightGray),
                    elevation = CardDefaults.cardElevation(10.dp)){
                    gameModel!!.currentTurn[it]?.let {
                        Image(painter = painterResource(id = it.image), contentDescription = null )
                        Text(text = it.rank.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

class MatchingCardViewModel : ViewModel() {
    lateinit var gameModel: MatchingCardModel

    private fun saveGameModel(model: MatchingCardModel) {
        if (model.gameId != "-1") {
            MatchingCardCotroller.saveMatchingCardModel(model)
        }
    }

    fun handleCardSelection(selectedCard: Model, activity: Context) {
        gameModel.apply {
            // Ensure the game is in progress
            if (gameStatus != GameStatus.INPROGRESS) {
                Toast.makeText(activity, "Game not in progress", Toast.LENGTH_SHORT).show()
                return
            }

            // Ensure the player is the one whose turn it is
            if (currentPlayer != MatchingCardCotroller.myMatchingId) {
                Toast.makeText(activity, "It's not your turn", Toast.LENGTH_SHORT).show()
                return
            }

            // Ensure the selected card belongs to the current player
            val userCards = userCard[currentPlayer] ?: emptyList()
            if (!userCards.contains(selectedCard)) {
                Toast.makeText(activity, "Invalid card selected", Toast.LENGTH_SHORT).show()
                return
            }

            // Remove the selected card from the current player's cards
            val updatedUserCards = userCards - selectedCard
            userCard[currentPlayer] = updatedUserCards
            currentTurn[currentPlayer] = selectedCard

            // Find the opponent
            val opponent = users.find { it != currentPlayer } ?: return
            val opponentCards = userCard[opponent] ?: emptyList()

            if (opponentCards.isNotEmpty()) {
                // Get the opponent's first card for comparison
                val opponentCard = opponentCards.first()
                if (selectedCard.rank == opponentCard.rank) {
                    // Cards match: move cards to the winner and update current player
                    userCard[opponent] = opponentCards - opponentCard
                    notMatchedCard.addAll(listOf(selectedCard, opponentCard))
                    userCard[currentPlayer] = userCard[currentPlayer]!! + listOf(selectedCard, opponentCard)
                    currentPlayer = opponent
                } else {
                    // Cards don't match: add to not matched cards
                    notMatchedCard.addAll(listOf(selectedCard, opponentCard))
                }
                // Remove the opponent's card from their deck
                userCard[opponent] = opponentCards - opponentCard
            } else {
                // If the opponent has no cards, current player wins
                winner = currentPlayer
                gameStatus = GameStatus.FINISHED
            }

            // Check if the game is over
            if (userCard.values.any { it.isEmpty() }) {
                // If any player has no cards, they lose
                winner = if (userCard[currentPlayer]?.isEmpty() == true) {
                    users.find { it != currentPlayer } ?: ""
                } else {
                    currentPlayer
                }
                gameStatus = GameStatus.FINISHED
            }

            // Save the updated game model
            saveGameModel(this)
        }
    }

    fun onStartGameClicked(activity: Context) {
        gameModel.apply {
            when (gameStatus) {
                GameStatus.CREATED -> {
                    // Ensure that the game is started only if both players have joined
                    if (isBothPlayersJoined()) {
                        gameStatus = GameStatus.INPROGRESS
                        currentPlayer = users.random() // Randomly select a starting player
                        saveGameModel(this)
                    } else {
                        // Notify the user that both players need to join before starting
                        Toast.makeText(
                            activity,
                            "Both players need to join before starting the game",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                GameStatus.JOINED -> {
                    // Optionally handle the case where the game is in the JOINED state
                    Toast.makeText(
                        activity,
                        "The game is still in the joined state. Ensure all players are ready.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    // Optionally handle other game statuses
                    Toast.makeText(
                        activity,
                        "Action not allowed in the current game state",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun isBothPlayersJoined(): Boolean {
        return gameModel.users.size == 2
    }
}







