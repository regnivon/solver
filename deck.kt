import java.util.ArrayList
import java.util.Random

class Deck {
    val ranks = "23456789TJQKA"
    val suits = "hdsc"
    var cards = ArrayList<Card>()
    val rand = Random()

    init {
        for (suit in suits) {
            for (rank in ranks) {
                cards.add(Card(rank, suit))
            }
        }
    }

    //Fisher-Yates shuffle
    fun shuffle() {
        for (i in cards.size-1 downTo 1) {
            val num = rand.nextInt(i)
            val temp = cards[i]
            cards[i] = cards[num]
            cards[num] = temp
        }
    }

    fun remove(card: Card): Card {
        val index = this.cards.indexOf(card)
        return this.cards.removeAt(index)
    }

    override fun toString(): String {
        return cards.toString()
    }
}


