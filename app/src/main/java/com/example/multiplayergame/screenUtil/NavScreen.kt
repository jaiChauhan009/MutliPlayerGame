package com.example.multiplayergame.screenUtil

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.multiplayergame.GamingScreen.PlayCard
import com.example.multiplayergame.GamingScreen.PlayGuessCard
import com.example.multiplayergame.GamingScreen.PlayMatchingCard
import com.example.multiplayergame.GamingScreen.PlayTicToe
import com.example.multiplayergame.JoinGame.CardGameJoin
import com.example.multiplayergame.JoinGame.CardGuessGameJoin
import com.example.multiplayergame.JoinGame.MainScreen
import com.example.multiplayergame.JoinGame.MatchingCard
import com.example.multiplayergame.JoinGame.TicToeGameJoin

@Composable
fun JoinGame(activity: Activity, navController: NavController){
    NavHost(navController = navController as NavHostController, startDestination = "MainScreen"){
        composable("CardGame"){
            CardGameJoin(activity = activity,navController)
        }
        composable("MainScreen"){
            MainScreen(activity = activity,navController)
        }
        composable("MatchingCard"){
            MatchingCard(activity,navController)
        }
        composable("TicToe"){
            TicToeGameJoin(activity = activity,navController)
        }
        composable("GuessCard"){
            CardGuessGameJoin(activity = activity,navController)
        }
        composable("more"){
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center){
                Text(text = "More Game")
            }
        }
        composable("playCard"){
            val name = it.arguments?.getString("name")?:""
            PlayCard(name,navController)
        }
        composable("playTicToe"){
            PlayTicToe()
        }
        composable("playMatchingCard"){
            val name = it.arguments?.getString("name")?:""
            PlayMatchingCard(name)
        }
        composable("playGuessCard"){
            PlayGuessCard()
        }
    }
}