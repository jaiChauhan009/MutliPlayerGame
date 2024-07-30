package com.example.multiplayergame.controller

import android.app.Activity
import com.example.multiplayergame.Model
import com.example.multiplayergame.screenUtil.CardGameModel
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class CardViewModel:ViewModel(){
    private val _cardGameModel = MutableLiveData<CardGameModel?>()
    val cardGameModel: LiveData<CardGameModel?> get() = _cardGameModel

    // Initialize or fetch the card game model
    fun fetchCardGameModel() {
        viewModelScope.launch {
            CardController.fetchCardGameModel()
            CardController.gameModel.observeForever {
                _cardGameModel.postValue(it)
            }
        }
    }

    // Save score to the card game model
    fun scoreSave(name: String, activity: Activity, score: Int) {
        viewModelScope.launch {
            CardController.scoreSave(name, activity, score)
        }
    }

    // Play a card in the game
    fun playGame(name: String, activity: Activity, card: Model) {
        viewModelScope.launch {
            CardController.playGame(name, activity, card)
        }
    }

    // Start the game
    fun onStartGame(activity: Activity) {
        viewModelScope.launch {
            CardController.onStartGame(activity)
        }
    }
}


object CardController {
    private var _gameModel: MutableLiveData<CardGameModel?> = MutableLiveData()
    val gameModel: LiveData<CardGameModel?> get() = _gameModel
    var myID: String = ""

    // Save the card game model to Firestore
    fun saveCardModel(model: CardGameModel) {
        _gameModel.postValue(model)
        if (model.gameId != "-1") {
            FirebaseFirestore.getInstance().collection("CardGame")
                .document(model.gameId)
                .set(model)
                .addOnSuccessListener {
                    Log.d("CardController", "Card game model saved successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("CardController", "Error saving card game model", e)
                }
        }
    }

    // Fetch the card game model from Firestore
    fun fetchCardGameModel() {
        _gameModel.value?.apply {
            if (gameId != "-1") {
                FirebaseFirestore.getInstance().collection("CardGame")
                    .document(gameId)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.e("CardController", "Error fetching card game model", error)
                            return@addSnapshotListener
                        }
                        val model = value?.toObject(CardGameModel::class.java)
                        _gameModel.postValue(model)
                    }
            }
        }
    }

    // Play a turn in the card game
    fun playGame(name: String, activity: Context, card: Model) {
        gameModel.value?.let { model ->
            if (model.userCards[name]?.isEmpty() == true) {
                Toast.makeText(activity, "GAME OVER", Toast.LENGTH_SHORT).show()
                return
            }

            if (model.chaal == model.kaat && card.category != model.kaat) {
                Toast.makeText(activity, "You have to play ${model.chaal}", Toast.LENGTH_SHORT).show()
                return
            }

            if (name == model.gameStarter) {
                model.gameStarter1 = model.gameStarter
                model.chaal = card.category
            }

            if (name == model.currentUser) {
                model.userChal[name] = card
                model.chaal = card.category
                model.userCards[name]?.minus(card)

                val previousIndex = (model.users.indexOf(name) - 1 + model.users.size) % model.users.size
                val lastUserCard = model.userChal[model.users[previousIndex]]
                val nextIndex = (model.users.indexOf(name) + 1) % model.users.size
                model.currentUser = model.users[nextIndex]

                if (model.gameStarter1 == model.currentUser) {
                    model.currentUser = model.gameStarter
                }

                if (name != model.gameStarter) {
                    if (lastUserCard != null) {
                        if (card.category == lastUserCard.category && card.rank > lastUserCard.rank) {
                            model.gameStarter = name
                        }
                        if (card.category == model.chaal && lastUserCard.category != model.kaat) {
                            model.gameStarter = name
                        }
                    }
                }

                saveCardModel(model)
            } else if (model.currentUser != name) {
                Toast.makeText(activity, "It's ${model.currentUser}'s turn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save the score in the card game
    fun scoreSave(name: String, activity: Context, score: Int) {
        gameModel.value?.let { model ->
            if (name == model.currentUser) {
                model.scoreToAchieve[name] = score
                val index = (model.users.indexOf(name) + 1) % model.users.size
                model.currentUser = model.users[index]

                if (model.scoreToAchieve[name]!! > model.scoreToAchieve[model.gameStarter]!!) {
                    model.gameStarter = name
                    Toast.makeText(activity, "$name is the new starter", Toast.LENGTH_SHORT).show()
                }

                saveCardModel(model)
            } else if (model.currentUser != name) {
                Toast.makeText(activity, "It's ${model.currentUser}'s turn", Toast.LENGTH_SHORT).show()
            } else if (model.gameStarter != model.gameStarter1 && model.gameStarter != model.currentUser) {
                if (name == model.gameStarter || score < model.scoreToAchieve[name]!!) {
                    Toast.makeText(activity, "You can't save this score", Toast.LENGTH_SHORT).show()
                } else {
                    model.scoreToAchieve[name] = score
                    saveCardModel(model)
                    Toast.makeText(activity, "Score saved, now you can start the game", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Start the card game
    fun onStartGame(activity: Context) {
        gameModel.value?.let { model ->
            if (check(model)) {
                model.currentUser = model.gameStarter
                saveCardModel(model)
            } else {
                Toast.makeText(activity, "Any player score is less than 2", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Check if the game can be started
    private fun check(model: CardGameModel): Boolean {
        val maxScore = model.scoreToAchieve[model.gameStarter] ?: return false
        return model.users.all { user ->
            val score = model.scoreToAchieve[user] ?: return false
            score >= 2 && score <= maxScore
        }
    }
}
