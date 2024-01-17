package xyz.axxonte.jacquesnoirremastered.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import xyz.axxonte.jacquesnoirremastered.data.Carte
import xyz.axxonte.jacquesnoirremastered.data.Deck
import xyz.axxonte.jacquesnoirremastered.data.GameState

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState(gameState = GameState.INGAME))
    val uiState = _uiState.asStateFlow()

    var deck = Deck()

    init {
        gameInit()
    }

    fun resetGame(){
        _uiState.update {
            it.copy(
                playerScore = 0,
                dealerScore = 0,
                playerCards = listOf(),
                dealerCards = listOf(),
                gameState = GameState.INGAME
            )
        }

        gameInit()
    }

    fun gameInit(){
        playerDraw(2)
        dealerInit()
        calculatePlayerPoints()
        calculateDealerPoints()

        if (uiState.value.playerScore == 21){
            _uiState.update {
                it.copy(gameState = GameState.BLACKJACK)
            }
            Log.d("DEBUG", "BLACKJACK !")
        }

    }



    fun playerDraw(amount: Int) {
        var oldCards = if (uiState.value.playerCards.isEmpty()){
            listOf<Carte>()
        } else {
            uiState.value.playerCards
        }

        val newCards = deck.draw(amount = amount)
        val newHand = oldCards + newCards

        _uiState.update {
            it.copy(
                playerCards = newHand
            )
        }
        calculatePlayerPoints()

        if (uiState.value.playerScore > 21) {
            _uiState.update {
                it.copy(
                    gameState = GameState.LOSE
                )
            }
        }

    }

    fun dealerInit(){
        dealerDraw(1, isRecto = true)
        dealerDraw(1, isRecto = false)
    }

    fun dealerPlay(){
        // Turn up the second card of the dealer cards
        var cards = uiState.value.dealerCards
        for (card in cards) {
            if (!card.isRecto) {
                card.isRecto = true
            }
        }
        _uiState.update {
            it.copy(dealerCards = cards)
        }
        calculateDealerPoints()

        while (uiState.value.dealerScore < 17) {
            dealerDraw(amount = 1, isRecto = true)
            calculateDealerPoints()
        }

        determineWinner()
    }

    fun determineWinner(){
        val playerScore = uiState.value.playerScore
        val dealerScore = uiState.value.dealerScore

        if (uiState.value.gameState == GameState.BLACKJACK){
            /* TODO */
        }

        val result = when {
            dealerScore > 21 -> GameState.WIN
            playerScore <= 21 && dealerScore > playerScore -> GameState.LOSE
            dealerScore < 21 && dealerScore == playerScore -> GameState.DRAW
            else -> GameState.BUGGED
        }

        _uiState.update {
            it.copy(
                gameState = result
            )
        }

        Log.d("DEBUG", "Result of the game : ${uiState.value.gameState}")
    }

    fun dealerDraw(amount: Int, isRecto : Boolean){
        var oldCards = if (uiState.value.dealerCards.isEmpty()){
            listOf<Carte>()
        } else {
            uiState.value.dealerCards
        }
        val newCards = if (isRecto){
             deck.draw(amount = amount)
        } else {
            deck.drawVerso(amount = amount)
        }

        val newHand = oldCards + newCards

        _uiState.update {
            it.copy(
                dealerCards = newHand
            )
        }

        Log.d("DEBUG", deck.deck.size.toString())
    }

    fun calculatePlayerPoints(){
        val hand = uiState.value.playerCards
        var aceCounter: Int = 0
        var score = 0
        for (card in hand) {
            score = when (card.value) {
                "ace" -> aceCounter++
                "king" -> score + 10
                "queen" -> score + 10
                "jack" -> score + 10
                "ten" -> score + 10
                "nine" -> score + 9
                "eight" -> score + 8
                "seven" -> score + 7
                "six" -> score + 6
                "five" -> score + 5
                "four" -> score + 4
                "three" -> score + 3
                "two" -> score + 2
                else -> score + card.value.toInt()
            }
        }

        if (aceCounter > 0) {
            for (i in 1..aceCounter) {
                if (score + 11 <= 21) {
                    score += 11
                } else {
                    score += 1
                }
            }
        }

        _uiState.update {
            it.copy(
                playerScore = score
            )
        }

        Log.d("DEBUG", "PlayerScore : ${uiState.value.playerScore}")
    }
    fun calculateDealerPoints(){
        val hand = uiState.value.dealerCards
        var aceCounter: Int = 0
        var score = 0
        for (card in hand) {
            if (card.isRecto) {
                score = when (card.value) {
                    "ace" -> aceCounter++
                    "king" -> score + 10
                    "queen" -> score + 10
                    "jack" -> score + 10
                    "ten" -> score + 10
                    "nine" -> score + 9
                    "eight" -> score + 8
                    "seven" -> score + 7
                    "six" -> score + 6
                    "five" -> score + 5
                    "four" -> score + 4
                    "three" -> score + 3
                    "two" -> score + 2
                    else -> score + card.value.toInt()
                }
            }
        }

        if (aceCounter > 0) {
            for (i in 1..aceCounter) {
                if (score + 11 <= 21) {
                    score += 11
                } else {
                    score += 1
                }
            }
        }

        _uiState.update {
            it.copy(
                dealerScore = score
            )
        }

        Log.d("DEBUG", "DealerScore : ${uiState.value.dealerScore}")
    }
}

data class GameUiState(
    val dealerCards: List<Carte> = listOf(),
    val playerCards: List<Carte> = listOf(),
    val playerScore: Int = 0,
    val dealerScore: Int = 0,
    val gameState: GameState
)