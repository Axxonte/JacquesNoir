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
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.axxonte.jacquesnoirremastered.R
import xyz.axxonte.jacquesnoirremastered.data.Carte
import xyz.axxonte.jacquesnoirremastered.data.GameState
import xyz.axxonte.jacquesnoirremastered.ui.theme.JacquesNoirRemasteredTheme

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    // Reset game Button
    IngameDisplay(
        playerCards = gameUiState.playerCards,
        dealerCards = gameUiState.dealerCards,
        playerScore = gameUiState.playerScore.toString(),
        dealerScore = gameUiState.dealerScore.toString(),
        onStayClick = { gameViewModel.dealerPlay() },
        onDrawClick = { gameViewModel.playerDraw(1) }
    )

    when (gameUiState.gameState) {

        GameState.LOSE -> {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(onClick = { gameViewModel.resetGame() }) {
                        Text(text = "Rejouer")
                    }
                },
                title = {
                    Text(text = "Perdu !")
                },
                text = {
                    Text(text = "Le dealer a gagné !")
                },
                icon = {
                    Image(painter = painterResource(id = R.drawable.lose), contentDescription = null)
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }

        GameState.WIN -> {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(onClick = { gameViewModel.resetGame() }) {
                        Text(text = "Rejouer")
                    }
                },
                title = {
                    Text(text = "Gagné !")
                },
                text = {
                    Text(text = "Vous avez gagné !")
                },
                icon = {
                    Image(painter = painterResource(id = R.drawable.win), contentDescription = null)
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }

        GameState.DRAW -> {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(onClick = { gameViewModel.resetGame() }) {
                        Text(text = "Rejouer")
                    }
                },
                title = {
                    Text(text = "Egalité !")
                },
                text = {
                    Text(text = "Egalité !")
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }

        GameState.BLACKJACK -> {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = { gameViewModel.resetGame() },
                title = {
                    Text(text = "Jacques Noir !")
                },
                text = {
                    Text(text = "BlackJack :)")
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }

        GameState.INGAME -> {} /* Ingame state should not display anything so let's keep it empty */
        GameState.BUGGED -> { /* Appear only if i missed a game end case */

            AlertDialog(onDismissRequest = {},
                confirmButton = { gameViewModel.resetGame() },
                title = {
                    Text(text = "Error in game end !")
                },
                text = {
                    Text(text = "Unregistered end game case : ")
                    Text(text = "Player Score : ${gameUiState.playerScore}")
                    Text(text = "Dealer Score : ${gameUiState.dealerScore}")
                },
                icon = {
                    Icon(imageVector = Icons.Outlined.Warning, contentDescription = null)
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        IconButton(onClick = { gameViewModel.resetGame() }) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null
            )
        }
    }
}

@Composable
fun IngameDisplay(
    playerCards: List<Carte>,
    dealerCards: List<Carte>,
    playerScore: String,
    dealerScore: String,
    onDrawClick: () -> Unit,
    onStayClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        HandDisplay(
            modifier = Modifier,
            cards = dealerCards
        )

        InteractionsDisplay(
            modifier = Modifier.weight(0.5f),
            onDrawClick = { onDrawClick() },
            onStayClick = { onStayClick() },
            playerScore = playerScore,
            dealerScore = dealerScore
        )
        HandDisplay(cards = playerCards)
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
    playerScore: String,
    dealerScore: String
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
fun ScoreDisplay(modifier: Modifier = Modifier, score: String) {
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