package xyz.axxonte.jacquesnoirremastered.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import xyz.axxonte.jacquesnoirremastered.R

class Deck {

    var deck = mutableListOf<Carte>()

    init{
        var family: String
        for (i in 1..4) {
            family = when {
                i == 1 -> "clubs"
                i == 2 -> "diamonds"
                i == 3 -> "hearts"
                else -> "spades"
            }
            for (j in 1 .. 13) {
                val value = when {
                    j == 13 -> "king"
                    j == 12 -> "queen"
                    j == 11 -> "jack"
                    j == 1 -> "ace"
                    else -> j.toString()
                }

                val card = Carte(
                    family = family,
                    value = value,
                    isRecto = true
                )

                deck.add(card)
            }
        }

        deck.shuffle()
    }


// Same as the init in case the deck isn't ceated correctly
    fun createDeck(){
        var family: String
        for (i in 1..4) {
            family = when {
                i == 1 -> "clubs"
                i == 2 -> "diamonds"
                i == 3 -> "hearts"
                else -> "spades"
            }
            for (j in 1 .. 13) {
                val value = when {
                    j == 13 -> "king"
                    j == 12 -> "queen"
                    j == 11 -> "jack"
                    j == 1 -> "ace"
                    else -> j.toString()
                }

                val card = Carte(
                    family = family,
                    value = value,
                    isRecto = true
                )

                deck.add(card)
            }
        }

        deck.shuffle()
    }

    fun draw(amount: Int) : List<Carte>{
        var result = mutableListOf<Carte>()
        for (i in 1..amount) {
            if (deck.isEmpty()){
                createDeck()
            }
            result.add(deck.first())
            deck.removeFirst()
        }
        return result.toList()
    }

    fun drawVerso(amount: Int) : List<Carte>{
        var result = mutableListOf<Carte>()
        for (i in 1 .. amount) {
            result.add(deck.first())
            result.first().isRecto = false
            deck.removeFirst()
        }
        return result
    }
}