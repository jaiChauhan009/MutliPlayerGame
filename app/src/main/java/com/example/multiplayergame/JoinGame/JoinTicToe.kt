package com.example.multiplayergame.JoinGame

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.multiplayergame.controller.TicToeViewModel
import com.example.multiplayergame.screenUtil.TicToeGameModel

@Composable
fun TicToeGameJoin(activity: Activity,navController: NavController){
    val viewModel: TicToeViewModel = viewModel()
    var gameId by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Button(onClick = {
                viewModel.createOnlineGame()
                navController.navigate("playTicToe")
            }) {
                Text("Create Online Game")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = gameId,
                onValueChange = {  gameId = it  },
                label = { Text("Game ID") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (gameId.isNotEmpty()) {
                    viewModel.joinOnlineGame(gameId)
                    navController.navigate("playTicToe")
                } else {

                }
            }) {
                Text("Join Online Game")
            }
        }
    }
}
