package com.example.multiplayergame.JoinGame

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.multiplayergame.MainActivity
import com.example.multiplayergame.R


data class game(
    val name:String,
    val image:Int
)

val gameList = listOf(
    game("TicToe", R.drawable.tictoe),
    game("CardGame", R.drawable.card),
    game("MatchingCard", R.drawable.images),
    game("GuessCard", R.drawable.guess),
    game("More", R.drawable.more)
)

@Composable
fun MainScreen(activity: Activity,navController: NavController){
    var visible by remember { mutableStateOf(true) }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(15.dp),
        contentAlignment = Alignment.Center){
        if(visible){
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(gameList.size){
                    Box(modifier = Modifier
                        .padding(8.dp).fillMaxWidth(0.5f)
                        .clickable {
                            visible = false
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.putExtra("gameName", gameList[it].name)
                            navController.navigate(gameList[it].name)
                        }
                        .size(100.dp)) {
                        Column {
                            Card(
                                elevation = CardDefaults.cardElevation(5.dp)
                            ){
                                Image(painter = painterResource(id = gameList[it].image),
                                    contentDescription = "null",
                                    contentScale = ContentScale.Crop)
                            }
                            Text(text = gameList[it].name)
                        }
                    }
                }
            }
        }
    }
}