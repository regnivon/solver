/*
class outlining Texas Hold'em hand ranges for all possible hands a player might have
 */

class Range(val hands: List<List<Card>>) {
    var allHands = mutableListOf<List<Int>>()

    init {
        for (hand in hands) {
            if (hand.size != 2) {
                throw IllegalArgumentException("Hands passed must contain 2 cards.")
            } else {
                allHands.add(handListToInt(hand).sorted())
            }
        }
    }

    fun contains(hand: List<Card>): Boolean {
        return allHands.contains(handListToInt(hand).sorted())
    }

    override fun toString(): String {
        return allHands.toString()
    }


}

