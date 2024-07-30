package com.example.multiplayergame.GamingScreen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.multiplayergame.Model
import com.example.multiplayergame.SmallCard
import com.example.multiplayergame.cardList1
import com.example.multiplayergame.controller.CardViewModel
import com.example.multiplayergame.screenUtil.CardGameModel

@Composable
fun PlayCard(name:String,navController: NavController){
    var selectedCard by remember { mutableStateOf<Model?>(null) }
    val viewModel: CardViewModel = viewModel()
    val activity = LocalContext.current as? Activity
    val cardGameModel by viewModel.cardGameModel.observeAsState()
    val cardList by remember {
        derivedStateOf {
            cardGameModel?.userCards?.get(name)?.take(5)?: emptyList()
        }
    }
    var visible by remember { mutableStateOf(true) }
    var scoreState by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(visible){
            LazyRow {
                cardGameModel?.users?.let {
                    items(it){
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(5.dp),
                            Alignment.Center){
                            Column {
                                Text(text = it)
                                Text(text = cardGameModel!!.scoreToAchieve[it].toString())
                            }
                        }
                    }
                }
            }
            OutlinedTextField(
                value = scoreState,
                onValueChange = { scoreState = it },
                label = { Text("Enter Score") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(onClick = {
                viewModel.scoreSave(name,activity!!,scoreState.toInt())
            }){
                Text(text = "Save Score")
            }
            Text(text = "Select your Kaat")
            LazyRow {
                items(cardList){
                    SmallCard(card = it) {}
                }
            }
            LazyRow{
                items(cardList1){
                    Box(modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Black)
                        .padding(8.dp)
                        .clickable {
                            if (cardGameModel?.gameStarter == name) {
                                cardGameModel?.kaat = it.name
                            } else {
                                Toast
                                    .makeText(
                                        activity,
                                        "you can't select kaat only ${cardGameModel?.gameStarter} can",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                        .height(50.dp),
                        contentAlignment = Alignment.Center){
                        Column {
                            Image(painter = painterResource(id = it.image),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                contentScale = ContentScale.Crop)
                            Text(text = it.name,fontSize = MaterialTheme.typography.titleMedium.fontSize)
                        }
                    }
                }
            }
            Button(onClick = { viewModel.onStartGame(activity!!)
            visible = false}) {
                Text(text = "Start Game")
            }
        }else{
            ViewCard(name,cardGameModel!!)
            ViewUserCard(name, cardGameModel!!,
                onClick = {
                    selectedCard?.let {
                        viewModel.playGame(name, activity!!, it)
                    }
                },
                onSelect = {
                    selectedCard = it // No need to modify here
                }
            )
        }
    }
}

@Composable
fun ViewCard(name: String,model: CardGameModel){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding()){
        Column {
            LazyRow {
                items(model.users){
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(5.dp),
                        Alignment.Center){
                        Column {
                            Text(text = it)
                            Text(text = "target -> ${model.scoreToAchieve[it].toString()}")
                            Text(text = "result -> ${model.result[it].toString()}")
                        }
                    }
                    Box(modifier = Modifier
                        .weight(1f)
                        .height(100.dp)){
                        model.userChal[name]?.let { it1 ->
                            SmallCard(card = it1) {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewUserCard(name: String, model: CardGameModel, onClick: () -> Unit, onSelect: (Model) -> Unit){
    Text(text = "Card")
    Spacer(modifier = Modifier.height(10.dp))
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
    ) {
        val suits = listOf("diamond", "spade", "club", "heart")

        for (suit in suits) {
            val cardsInSuit =
                model.userCards[name]?.filter { it.category == suit }
                    ?.sortedBy { it.rank }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(cardsInSuit ?: emptyList()) { card ->
                    SmallCard(
                        card = card,
                        onClick = {
                            onSelect(card)
                            onClick()
                        }
                    )
                }
            }
        }
    }
}