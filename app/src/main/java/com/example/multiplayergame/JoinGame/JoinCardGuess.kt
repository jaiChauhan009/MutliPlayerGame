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
import com.example.multiplayergame.screenUtil.GuessCard
import com.example.multiplayergame.MainActivity
import com.example.multiplayergame.card
import com.example.multiplayergame.controller.GuessGameController
import com.example.multiplayergame.generateCard
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CardGuessGameJoin(activity: Activity,navController: NavController){
    GuessGameController.fetchGuessGameModel()
    Box(modifier = Modifier.fillMaxSize().padding(5.dp)){
        Column {
            var name by remember { mutableStateOf("") }
            var gameId by remember { mutableStateOf("") }
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
                onClick = { createOnlineGuessGame(name, gameId,activity)
                          navController.navigate("playGuessCard")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Create Online Game")
            }

            Button(
                onClick = { joinOnlineGuessGame(name, gameId, activity)
                          navController.navigate("playGuessCard")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Join Online Game")
            }
        }
    }
}

fun createOnlineGuessGame(name: String, gameId: String,activity: Activity) {
    GuessGameController.myGuessId = gameId
    GuessGameController.saveGuessGameModel(
        GuessCard(
            gameId = gameId,
            users = mutableListOf(name)
        )
    )
    val intent = Intent(activity, MainActivity::class.java)
    intent.putExtra("user", name)
}

fun joinOnlineGuessGame(name: String, gameId: String,activity: Activity) {
    if (gameId.isEmpty()) {
        Toast.makeText(activity, "Please enter a game ID", Toast.LENGTH_SHORT).show()
        return
    }
    GuessGameController.myGuessId = gameId
    Firebase.firestore.collection("playGuessCard")
        .document(gameId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()){
                val model = document.toObject(GuessCard::class.java)
                if (model == null) {
                    Toast.makeText(activity, "Invalid game ID", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                } else {
                    if (model.users.contains(name)) {
                        Toast.makeText(activity, "You have already joined this game", Toast.LENGTH_SHORT).show()
                    } else {
                        model.users.add(name)
                        if (model.users.size == 4) {
                            Toast.makeText(activity, "Game started", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.putExtra("user", name)
                        } else {
                            GuessGameController.saveGuessGameModel(model)
                            Toast.makeText(activity, "Joined game as $name", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
}