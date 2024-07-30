package com.example.multiplayergame.JoinGame

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.multiplayergame.MainActivity
import com.example.multiplayergame.Model
import com.example.multiplayergame.controller.GuessGameController
import com.example.multiplayergame.controller.MatchingCardCotroller
import com.example.multiplayergame.generateDeck
import com.example.multiplayergame.screenUtil.MatchingCardModel
import com.example.multiplayergame.screenUtil.TicToeGameModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun MatchingCard(activity: Activity,navController: NavController){
    MatchingCardCotroller.fetchMatchingCardModel()
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)) {
        Column {
            var name by remember { mutableStateOf("") }
            var gameId by remember { mutableStateOf("") }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = gameId,
                onValueChange = { gameId = it },
                label = { Text("Game ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Button(
                onClick = { createOnlineMatchingGame(name, gameId, activity)
                          navController.navigate("playMatchingCard/$name")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Create Online Game")
            }

            Button(
                onClick = { joinOnlineMatchingGame(name, gameId, activity)
                          navController.navigate("playMatchingCard/$name")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Join Online Game")
            }
        }
    }
}

fun createOnlineMatchingGame(name: String, gameId: String,activity: Activity) {
    MatchingCardCotroller.myMatchingId = name
    MatchingCardCotroller.saveMatchingCardModel(
        MatchingCardModel(
            gameId = gameId,
            users = mutableListOf(name)
        )
    )
    val intent = Intent(activity, MainActivity::class.java)
    intent.putExtra("user", name)
}

fun joinOnlineMatchingGame(name: String,gameId: String,activity: Activity) {
    if (gameId.isEmpty()) {
        Toast.makeText(activity, "Please enter a game ID", Toast.LENGTH_SHORT).show()
        return
    }
    MatchingCardCotroller.myMatchingId = name
    Firebase.firestore.collection("MatchingCard")
        .document(gameId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val model = document.toObject(MatchingCardModel::class.java)
                if (model == null) {
                    Toast.makeText(activity, "Invalid game ID", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                } else {
                    if (model.users.contains(name)) {
                        Toast.makeText(
                            activity,
                            "You have already joined this game",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        model.users.add(name)
                        val deck: List<Model> = generateDeck()
                        for ((index, user) in model.users.withIndex()) {
                            val startIndex = index * 13
                            val endIndex = startIndex + 26
                            model.userCard[user] = deck.subList(startIndex, endIndex)
                        }

                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra("user", name)
                    }
                }
            }
        }
}