package com.example.multiplayergame.screenUtil

import com.example.multiplayergame.Model
import com.example.multiplayergame.card
import kotlin.random.Random


data class CardGameModel(
    var gameId: String = "",
    var users: MutableList<String> = mutableListOf(),
    var chaal: String = "",
    var kaat:String ="",
    var gameStarter:String = users[Random.nextInt(4)],
    var currentUser: String = gameStarter,
    var gameStarter1:String = gameStarter,
    var userChal:MutableMap<String, Model> = mutableMapOf(),
    var scoreToAchieve: MutableMap<String, Int> = mutableMapOf(),
    var result:MutableMap<String,Int> = mutableMapOf(),
    var userCards: MutableMap<String, List<Model>> = mutableMapOf(),
)

data class TicToeGameModel (
    var users : MutableList<String> = mutableListOf(),
    var gameId : String = "-1",
    var filledPos : MutableList<String> = mutableListOf("","","","","","","","",""),
    var winner : String ="",
    var gameStatus : GameStatus = GameStatus.CREATED,
    var currentPlayer : String = (arrayOf("X","O"))[Random.nextInt(2)]
)

data class MatchingCardModel (
    var users : MutableList<String> = mutableListOf(),
    var gameId : String = "-1",
    var notMatchedCard:MutableList<Model> = mutableListOf(),
    var userCard:MutableMap<String, List<Model>> = mutableMapOf(),
    var winner : String ="",
    var currentTurn:MutableMap<String,Model> = mutableMapOf(),
    var gameStatus: GameStatus = GameStatus.CREATED,
    var currentPlayer : String = users[Random.nextInt(until = 2)]
)

data class GuessCard(
    var gameId: String = "",
    var users:MutableList<String> = mutableListOf(),
    var userCards: MutableMap<String, card> = mutableMapOf(),
    var score: MutableMap<String, Int> = mutableMapOf(),
)

enum class GameStatus {
    CREATED,
    JOINED,
    INPROGRESS,
    FINISHED
}

