fun genCombos(c1: Char, c2: Char, suited: Boolean): List<List<Card>> {
    val toReturn = mutableListOf<List<Card>>()
    val cardList1 = ArrayList<Card>()
    val cardList2 = ArrayList<Card>()
    for (suit in "hdsc") {
        cardList1.add(Card(c1, suit))
        cardList2.add(Card(c2, suit))
    }
    if (suited) {
        for (i in 0..3) {
            toReturn.add(listOf(cardList1[i], cardList2[i]))
        }
    } else {
        for (card1 in cardList1) {
            for (card2 in cardList2) {
                if (card1 != card2 && card1.suit != card2.suit) {
                    toReturn.add(listOf(card1, card2))
                }
            }
        }
    }
    return toReturn
}

fun handListToInt(hand: List<Card>): List<Int> {
    val toReturn = mutableListOf<Int>()
    for (card in hand) {
        toReturn.add(card.cardToNum())
    }
    return toReturn
}

//takes in a string with comma separated shorthand notation and returns
fun genCombosFromString(notation: String): List<List<Card>> {
    val toDoList = notation.split(", ".toRegex())
    val comboList = mutableListOf<List<Card>>()
    for (combo in toDoList) {
        var suited: Boolean? = null
        var card1: Char? = null
        var card2: Char? = null
        for (letter in combo) {
            if (letter in "23456789TJQKA") {
                if (card1 == null) {
                    card1 = letter
                } else {
                    card2 = letter
                }
            } else if (letter == 's') {
                suited = true
            } else if (letter == 'o') {
                suited = false
            }
        }
        if (card1 == card2) {
            suited = false
        }
        comboList += genCombos(card1!!, card2!!, suited!!)
    }
    return comboList
}

fun main() {
    println(genCombosFromString("KQo, KJs, KTs"))
}