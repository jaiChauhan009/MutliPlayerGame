package com.example.multiplayergame.GamingScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multiplayergame.controller.TicTocController
import com.example.multiplayergame.controller.TicToeViewModel
import com.example.multiplayergame.screenUtil.GameStatus
import com.example.multiplayergame.screenUtil.TicToeGameModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayTicToe() {
    val viewModel: TicToeViewModel = viewModel()
    val gameModel by TicTocController.gameModel.observeAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tic Tac Toe ${gameModel?.gameId ?: ""}") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            GameScreen(viewModel = viewModel)  // Pass viewModel to GameScreen
        }
    }
}


@Composable
fun GameScreen(viewModel: TicToeViewModel) {
    val gameModel by TicTocController.gameModel.observeAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        gameModel?.let { model ->
            GameGrid(gameModel = model, onButtonClick = { pos ->
                viewModel.onButtonClick(pos)  // Use viewModel to interact with game logic
            })
            Spacer(modifier = Modifier.height(16.dp))
            if (model.gameStatus == GameStatus.JOINED) {
                Button(onClick = { viewModel.startGame() }) {
                    Text("Start Game")
                }
            }
        }
    }
}


@Composable
fun GameGrid(gameModel: TicToeGameModel, onButtonClick: (Int) -> Unit) {
    Column {
        for (row in 0..2) {
            Row {
                for (col in 0..2) {
                    val index = row * 3 + col
                    Button(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(60.dp),
                        onClick = { onButtonClick(index) }
                    ) {
                        Text(text = gameModel.filledPos[index])
                    }
                }
            }
        }
    }
}
