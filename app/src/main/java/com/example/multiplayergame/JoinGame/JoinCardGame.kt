package com.example.multiplayergame.JoinGame

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.multiplayergame.screenUtil.CardGameModel
import com.example.multiplayergame.MainActivity
import com.example.multiplayergame.Model
import com.example.multiplayergame.controller.CardController
import com.example.multiplayergame.generateDeck
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CardGameJoin(activity: Activity,navController: NavController){
    CardController.fetchCardGameModel()
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp)){
        var name by remember { mutableStateOf("") }
        var gameId by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Create Game or Join Game")
            OutlinedTextField(
                value = name,
                onValueChange = { name =  it},
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
                onClick = { createOnlineGame(name, gameId,activity)
                    navController.navigate("playCard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Create Online Game")
            }

            Button(
                onClick = { joinOnlineGame(name, gameId,activity)
                    navController.navigate("playCard")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Join Online Game")
            }
        }
    }
}

fun joinOnlineGame(name: String, gameId: String,activity: Activity) {
    if (gameId.isEmpty()) {
        Toast.makeText(activity, "Please enter a game ID", Toast.LENGTH_SHORT).show()
        return
    }
    Firebase.firestore.collection("CardGame")
        .document(gameId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()){
                val model = document.toObject(CardGameModel::class.java)
                if (model == null) {
                    Toast.makeText(activity, "Invalid game ID", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                } else {
                    if (model.users.contains(name)) {
                        Toast.makeText(activity, "You have already joined this game", Toast.LENGTH_SHORT).show()
                    } else {
                        model.users.add(name)
                        if (model.users.size == 4) {
                            model.currentUser = model.users.random()
                            val deck: List<Model> = generateDeck()
                            for ((index, user) in model.users.withIndex()) {
                                val startIndex = index * 13
                                val endIndex = startIndex + 13
                                model.userCards[user] = deck.subList(startIndex, endIndex)
                                model.scoreToAchieve[user] = 0
                            }
                            CardController.saveCardModel(model)
                            Toast.makeText(activity, "Game started", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.putExtra("user", name)
                        } else {
                            CardController.saveCardModel(model)
                            Toast.makeText(activity, "Joined game as $name", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
}

fun createOnlineGame(name: String, gameId: String,activity: Activity) {
    CardController.saveCardModel(
        CardGameModel(
            gameId = gameId,
            users = mutableListOf(name)
        )
    )
    val intent = Intent(activity, MainActivity::class.java)
    intent.putExtra("user", name)
}