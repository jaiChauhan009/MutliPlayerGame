package com.example.multiplayergame.GamingScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multiplayergame.MainActivity
import com.example.multiplayergame.card
import com.example.multiplayergame.controller.GuessGameController
import com.example.multiplayergame.controller.MatchingCardCotroller
import com.example.multiplayergame.generateCard
import com.example.multiplayergame.screenUtil.GuessCard
import com.example.multiplayergame.screenUtil.MatchingCardModel
import kotlin.random.Random


fun generateRandomColor():Int{
    val random = Random.nextInt(0xFFFFFF)
    return random + 0xFF000000.toInt()
}

@Composable
fun PlayGuessCard(){
    val context = LocalContext.current as MainActivity
    MatchingCardCotroller.fetchMatchingCardModel()
    var gameModel: GuessCard? = null
    GuessGameController.guessGame.observe(context){
        gameModel = it
    }
    val deck: List<card> = generateCard()
    for (i in 0..3) {
        gameModel!!.userCards.values.add(deck[i])
    }
    gameModel?.let {
        it.userCards.clear()  // Clear existing cards if any
        deck.forEach { card ->
            it.userCards[card.name] = card
        }
    }
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {
        items(gameModel!!.users){
                CardToShow(gameModel = gameModel, it = it)
        }
    }
}

@Composable
fun CardToShow(gameModel: GuessCard?, it: String){
    val frontContent = @Composable {
        Column {
            gameModel!!.userCards[it]?.image?.let { it1 -> painterResource(id = it1) }
                ?.let { it2 -> Image(painter = it2, contentDescription = null ) }
            Text(text = gameModel.userCards[it]?.name.toString())
            Text(text = it)
        }
    }

    val backContent = @Composable {
        Column {
            Text(text = it)
        }
    }
    val randomColor = generateRandomColor()
    var isFront by remember { mutableStateOf(false) }
    Card(modifier = Modifier
        .background(Color(randomColor), RoundedCornerShape(8.dp))
        .clickable {
            isFront = !isFront
        }, elevation = CardDefaults.cardElevation(8.dp)){
        androidx.compose.animation.AnimatedVisibility(
            visible = if(gameModel!!.userCards[it]?.name == "Minister"){
                isFront
            }else{
                !isFront
            },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                frontContent()
            }
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = if(gameModel.userCards[it]?.name == "Minister"){
                isFront
            }else{
                !isFront
            },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                backContent()
            }
        }
    }
}


