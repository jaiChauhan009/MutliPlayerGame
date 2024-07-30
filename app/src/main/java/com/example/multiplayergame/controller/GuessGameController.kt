package com.example.multiplayergame.controller

import androidx.lifecycle.MutableLiveData
import com.example.multiplayergame.screenUtil.GuessCard
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object GuessGameController {
    private var _guessGame: MutableLiveData<GuessCard?> = MutableLiveData()
    var guessGame: MutableLiveData<GuessCard?> = _guessGame
    var myGuessId =""

    fun saveGuessGameModel(model : GuessCard){
        _guessGame.postValue(model)
        if(model.gameId!="-1"){
            Firebase.firestore.collection("GuessGame")
                .document(model.gameId)
                .set(model)
        }
    }
    fun fetchGuessGameModel(){
        guessGame.value?.apply {
            if(gameId!="-1") {
                Firebase.firestore.collection("GuessGame")
                    .document(gameId)
                    .addSnapshotListener { value, error ->
                        val model = value?.toObject(GuessCard::class.java)
                        _guessGame.postValue(model)
                    }
            }

        }
    }
}