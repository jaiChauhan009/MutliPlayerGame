package com.example.multiplayergame.controller

import androidx.lifecycle.MutableLiveData
import com.example.multiplayergame.screenUtil.MatchingCardModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object MatchingCardCotroller {
    private var _matchingCard: MutableLiveData<MatchingCardModel?> = MutableLiveData()
    var matchingCard: MutableLiveData<MatchingCardModel?> = _matchingCard
    var myMatchingId =""

    fun saveMatchingCardModel(model: MatchingCardModel){
        _matchingCard.postValue(model)
        if (model.gameId!= "-1"){
            Firebase.firestore.collection("MatchingCard")
                .document(model.gameId)
                .set(model)
        }
    }

    fun fetchMatchingCardModel(){
        matchingCard.value?.apply {
            Firebase.firestore.collection("MatchingCard")
                .document(gameId)
                .addSnapshotListener { value, _ ->
                    val model = value?.toObject(MatchingCardModel::class.java)
                    _matchingCard.postValue(model)
                }
        }
    }
}