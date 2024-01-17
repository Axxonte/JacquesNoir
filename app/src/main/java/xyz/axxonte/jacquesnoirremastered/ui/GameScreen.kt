package xyz.axxonte.jacquesnoirremastered.ui

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.axxonte.jacquesnoirremastered.R
import xyz.axxonte.jacquesnoirremastered.data.Carte
import xyz.axxonte.jacquesnoirremastered.data.GameState
import xyz.axxonte.jacquesnoirremastered.ui.theme.JacquesNoirRemasteredTheme

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    // Reset game Button
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ){
        IconButton(onClick = { gameViewModel.resetGame() }) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null
            )
        }
    }

    when (gameUiState.gameState) {
        GameState.INGAME -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {

                HandDisplay(
                    modifier = Modifier,
                    cards = gameUiState.dealerCards
                )

                InteractionsDisplay(
                    modifier = Modifier.weight(0.5f),
                    onDrawClick = {
                        gameViewModel.playerDraw(1)
                    },
                    onStayClick = { gameViewModel.dealerPlay() },
                    playerScore = gameUiState.playerScore,
                    dealerScore = gameUiState.dealerScore
                )
                HandDisplay(cards = gameUiState.playerCards)
            }
        }

        GameState.LOSE -> {
            Text(text = "T'AS PERDU FDP !")
        }
        
        GameState.WIN -> {
            Text(text = "You WINNER !")
        }
        
        GameState.DRAW -> {
            Text(text = "Egalité")
        }
        
        else -> Text(text = "Autre (╯°□°)╯︵ ┻━┻")
    }
}

@Composable
fun findCardImageId(card: Carte): Int {

    val family = card.family
    val value = card.value

    var cardValue = when (value) {
        "2" -> "two"
        "3" -> "three"
        "4" -> "four"
        "5" -> "five"
        "6" -> "six"
        "7" -> "seven"
        "8" -> "eight"
        "9" -> "nine"
        "10" -> "ten"
        else -> value
    }
    var img_name = cardValue + "_of_" + family

    val context = LocalContext.current
    val drawableId = remember(img_name) {
        context.resources.getIdentifier(
            img_name,
            "drawable",
            context.packageName
        )
    }
    return drawableId
}


@Composable
fun InteractionsDisplay(
    modifier: Modifier = Modifier,
    onDrawClick: () -> Unit,
    onStayClick: () -> Unit,
    playerScore: Int,
    dealerScore: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .height(256.dp)
                .padding(start = 16.dp),
            painter = painterResource(id = R.drawable.astronaut),
            contentDescription = null
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxHeight()
        ) {
            ScoreDisplay(score = dealerScore)

            Button(
                modifier = Modifier.width(180.dp),
                onClick = {
                    onDrawClick()
                    println("Drawed a card")
                }
            ) {
                Text(
                    text = "Piocher"
                )
            }

            Button(
                modifier = Modifier.width(180.dp),
                onClick = {
                    onStayClick()
                    println("Stayed")
                }
            ) {
                Text(
                    text = "Rester"
                )
            }

            ScoreDisplay(score = playerScore)
        }
    }
}

@Composable
fun HandDisplay(modifier: Modifier = Modifier, cards: List<Carte>) {
    LazyRow(
        modifier = modifier
            .height(160.dp)
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        items(cards.count()) { id ->
            CardDisplay(cards.elementAt(id))
        }
    }
}

@Composable
fun ScoreDisplay(modifier: Modifier = Modifier, score: Int) {
    Card(
        modifier = Modifier.padding(all = 16.dp)
    ) {
        Text(
            text = score.toString()
        )
    }
}

@Composable
fun CardDisplay(card: Carte) {
    val recto = card.isRecto
    if (recto) {
        Image(
            painter = painterResource(id = findCardImageId(card)),
            contentDescription = null,
            modifier = Modifier.height(256.dp)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.astronaut),
            contentDescription = null,
            modifier = Modifier.height(256.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GamePreview() {
    JacquesNoirRemasteredTheme {
        GameScreen()
    }
}