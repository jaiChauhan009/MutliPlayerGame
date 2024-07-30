package com.example.multiplayergame.controller

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt
import androidx.compose.runtime.State
import com.example.multiplayergame.screenUtil.GameStatus
import com.example.multiplayergame.screenUtil.TicToeGameModel

class TicToeViewModel : ViewModel() {
    val gameModel: LiveData<TicToeGameModel?> = TicTocController.gameModel

    private val _gameId = mutableStateOf("")
    val gameId: State<String> = _gameId

    fun createOnlineGame() {
        viewModelScope.launch {
            TicTocController.createOnlineGame()
        }
    }

    fun joinOnlineGame(gameId: String) {
        _gameId.value = gameId
        viewModelScope.launch {
            TicTocController.joinOnlineGame(gameId)
        }
    }

    fun startGame() {
        viewModelScope.launch {
            TicTocController.startGame()
        }
    }

    fun onButtonClick(pos: Int) {
        viewModelScope.launch {
            TicTocController.onButtonClick(pos)
        }
    }
}

object TicTocController {
    private val firestore =  FirebaseFirestore.getInstance()
    private var _gameModel: MutableLiveData<TicToeGameModel?> = MutableLiveData()
    var gameModel: MutableLiveData<TicToeGameModel?> = _gameModel
    var myID: String = ""

    fun saveGameModel(model: TicToeGameModel) {
        _gameModel.postValue(model)
        if (model.gameId != "-1") {
            firestore.collection("games")
                .document(model.gameId)
                .set(model)
        }
    }

    fun fetchGameModel(){
        gameModel.value?.apply {
            if(gameId!="-1"){
                firestore.collection("games")
                    .document(gameId)
                    .addSnapshotListener { value, error ->
                        val model = value?.toObject(TicToeGameModel::class.java)
                        _gameModel.postValue(model)
                    }
            }
        }
    }

    fun onButtonClick(pos: Int) {
        _gameModel.value?.let { currentModel ->
            if (currentModel.gameStatus == GameStatus.INPROGRESS) {
                if (currentModel.currentPlayer != myID) return

                val updatedPositions = currentModel.filledPos.toMutableList()
                if (updatedPositions[pos].isEmpty()) {
                    updatedPositions[pos] = currentModel.currentPlayer
                    val nextPlayer = if (currentModel.currentPlayer == "X") "O" else "X"
                    val updatedModel = currentModel.copy(
                        filledPos = updatedPositions,
                        currentPlayer = nextPlayer
                    )
                    checkForWinner(updatedPositions, updatedModel)
                    _gameModel.value = updatedModel
                    saveGameModel(updatedModel)
                }
            }
        }
    }
    fun startGame() {
        _gameModel.value?.let { model ->
            if (model.gameId != "-1") {
                firestore.collection("games")
                    .document(model.gameId)
                    .set(model)
            }
        }
    }

    private fun checkForWinner(filledPos: List<String>, updatedModel: TicToeGameModel) {
        val winningPos = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        for (i in winningPos) {
            if (filledPos[i[0]] == filledPos[i[1]] && filledPos[i[1]] == filledPos[i[2]] && filledPos[i[0]].isNotEmpty()) {
                _gameModel.value = updatedModel.copy(
                    gameStatus = GameStatus.FINISHED,
                    winner = filledPos[i[0]]
                )
                return
            }
        }

        if (filledPos.none { it.isEmpty() }) {
            _gameModel.value = updatedModel.copy(gameStatus = GameStatus.FINISHED)
        }
    }
    fun createOnlineGame() {
        val newGameModel = TicToeGameModel(
            gameStatus = GameStatus.CREATED,
            gameId = Random.nextInt(1000..9999).toString()
        )
        Log.d("GameViewModel", "Creating game with ID: ${newGameModel.gameId}")
        _gameModel.value = newGameModel
        saveGameModel(newGameModel)
    }

    fun joinOnlineGame(gameId: String) {
        firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model = it.toObject(TicToeGameModel::class.java)
                if (model == null) {
                    Log.d("GameViewModel", "No game found with ID: $gameId")
                    _gameModel.value = TicToeGameModel(gameId = "-1")
                } else {
                    Log.d("GameViewModel", "Joining game with ID: $gameId")
                    val updatedModel = model.copy(gameStatus = GameStatus.JOINED)
                    _gameModel.value = updatedModel
                    saveGameModel(updatedModel)
                }
            }
    }
}


