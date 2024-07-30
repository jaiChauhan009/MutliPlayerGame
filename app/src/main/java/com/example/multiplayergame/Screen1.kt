package com.example.multiplayergame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class CardItem(val id: Int, var isSelected: Boolean = false)
data class JoystickState(
    val outerRadius: Float,
    var innerCirclePosition: Offset
)



@Composable
fun SquareCard(cardItem: CardItem, onCardClicked: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onCardClicked(cardItem.id) },
        shape = RoundedCornerShape(8.dp),
        CardDefaults.cardColors(if (cardItem.isSelected) Color.Blue else Color.White),
        CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cardItem.id.toString(),
                style = TextStyle(fontSize = 20.sp),
                color = if (cardItem.isSelected) Color.White else Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SquareCardList() {
    val cardItems = remember { (1..5).map { CardItem(it) }.toMutableList() }
    LazyVerticalGrid(columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(cardItems) { cardItem ->
            SquareCard(cardItem = cardItem) { selectedId ->
                cardItems.forEach { it.isSelected = (it.id == selectedId) }
            }
        }
    }
}


@Composable
fun MovingObject(
    x: Dp,
    y: Dp,
    size: Dp = 50.dp
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(x.value.roundToInt(), y.value.roundToInt())
            }
            .size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawRect(
                color = Color.Blue,
                topLeft = Offset(x.toPx(), y.toPx()),
                size = Size(size.toPx(), size.toPx())
            )
        }
    }
}

@Composable
fun JoyStick(
    modifier: Modifier = Modifier,
    size: Dp = 170.dp,
    dotSize: Dp = 40.dp,
    backgroundImage: Int = R.drawable.joystick_background_1,
    dotImage: Int = R.drawable.joystick_dot_1,
    moved: (x: Float, y: Float) -> Unit = {_,_ ->}
) {
    val objectPositionX = remember { mutableStateOf(size/2f) }
    val objectPositionY = remember { mutableStateOf(size/2f) }


    Box(
        modifier = modifier
            .size(size)
    ) {
        val maxRadius = with(LocalDensity.current) { (size / 2).toPx() }
        val centerX = with(LocalDensity.current) { ((size - dotSize) / 2).toPx() }
        val centerY = with(LocalDensity.current) { ((size - dotSize) / 2).toPx() }

        var offsetX by remember { mutableStateOf(centerX) }
        var offsetY by remember { mutableStateOf(centerY) }

        var radius by remember { mutableStateOf(0f) }
        var theta by remember { mutableStateOf(0f) }

        var positionX by remember { mutableStateOf(0f) }
        var positionY by remember { mutableStateOf(0f) }

        Image(
            painterResource(id = backgroundImage),
            "JoyStickBackground",
            modifier = Modifier.size(size),
        )

        Image(
            painterResource(id = dotImage),
            "JoyStickDot",
            modifier = Modifier
                .offset {
                    IntOffset(
                        (positionX + centerX).roundToInt(),
                        (positionY + centerY).roundToInt()
                    )
                }
                .size(dotSize)
                .pointerInput(Unit) {
                    detectDragGestures(onDragEnd = {
                        offsetX = centerX
                        offsetY = centerY
                        radius = 0f
                        theta = 0f
                        positionX = 0f
                        positionY = 0f
                    }) { pointerInputChange: PointerInputChange, offset: Offset ->
                        val x = offsetX + offset.x - centerX
                        val y = offsetY + offset.y - centerY

                        pointerInputChange.consume()

                        theta = if (x >= 0 && y >= 0) {
                            atan(y / x)
                        } else if (x < 0 && y >= 0) {
                            (Math.PI).toFloat() + atan(y / x)
                        } else if (x < 0 && y < 0) {
                            -(Math.PI).toFloat() + atan(y / x)
                        } else {
                            atan(y / x)
                        }

                        radius = sqrt((x.pow(2)) + (y.pow(2)))

                        offsetX += offset.x
                        offsetY += offset.y

                        if (radius > maxRadius) {
                            polarToCartesian(maxRadius, theta)
                        } else {
                            polarToCartesian(radius, theta)
                        }.apply {
                            positionX = first
                            positionY = second
                        }
                        val clampedX = (positionX + centerX).coerceIn(0f, size.toPx())
                        val clampedY = (positionY + centerY).coerceIn(0f, size.toPx())

                        // Calculate normalized coordinates for callback
                        val normalizedX = (clampedX - centerX) / maxRadius
                        val normalizedY = -(clampedY - centerY) / maxRadius

                        // Update object position
                        objectPositionX.value = clampedX.toDp()
                        objectPositionY.value = clampedY.toDp()

                        // Invoke callback with normalized coordinates
                        moved(normalizedX, normalizedY)
                    }
                }
                .onGloballyPositioned { coordinates ->
//                    moved(
//                        (coordinates.positionInParent().x - centerX) / maxRadius,
//                        -(coordinates.positionInParent().y - centerY) / maxRadius
//                    )
                    positionX = coordinates.positionInParent().x - centerX
                    positionY = coordinates.positionInParent().y - centerY
                },
        )
    }
    MovingObject(
        x = objectPositionX.value,
        y = objectPositionY.value
    )
}

private fun polarToCartesian(radius: Float, theta: Float): Pair<Float, Float> =
    Pair(radius * cos(theta), radius * sin(theta))




data class Model(
    val rank:Int,
    val category:String,
    val cardName:String,
    val image: Int
)

data class card(
    val name: String,
    val image: Int
)

val cardList = listOf(
    card("King",R.drawable.king),
    card("Minister",R.drawable.minister),
    card("Soldier",R.drawable.soldier),
    card("Thief",R.drawable.thief),
)

fun generateCard():List<card>{
    return cardList.shuffled()
}

val cardList1 = listOf(
    card("diamond",R.drawable.diamond),
    card("spade",R.drawable.spade),
    card("club",R.drawable.tree),
    card("heart",R.drawable.heart)
)

fun generateDeck(): List<Model> {
    val suits = listOf("diamond", "spade", "club", "heart")
    val ranks = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")
    val cardList = mutableListOf<Model>()
    for (suit in suits) {
        val imageResId = when (suit) {
            "diamond" -> R.drawable.diamond
            "spade" -> R.drawable.spade
            "club" -> R.drawable.tree
            "heart" -> R.drawable.heart
            else -> throw IllegalArgumentException("Unknown suit: $suit")
        }
        for ((index, rank) in ranks.withIndex()) {
            val model = Model(index + 1, suit, rank, imageResId)
            cardList.add(model)
        }
    }
    return cardList.shuffled()
}


@Composable
fun SmallCard(card: Model, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, Color.Black)
            .padding(8.dp)
            .clickable(onClick = onClick)
            .background(Color.Yellow)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = card.image),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = card.cardName,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

