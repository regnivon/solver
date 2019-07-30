class Card(val rank: Char, val suit: Char) {
    private val ranks = "23456789TJQKA"
    private val suits = "hdsc"
    private val cardValMap = mapOf(
        "2c" to 1,
        "2d" to 2,
        "2h" to 3,
        "2s" to 4,
        "3c" to 5,
        "3d" to 6,
        "3h" to 7,
        "3s" to 8,
        "4c" to 9,
        "4d" to 10,
        "4h" to 11,
        "4s" to 12,
        "5c" to 13,
        "5d" to 14,
        "5h" to 15,
        "5s" to 16,
        "6c" to 17,
        "6d" to 18,
        "6h" to 19,
        "6s" to 20,
        "7c" to 21,
        "7d" to 22,
        "7h" to 23,
        "7s" to 24,
        "8c" to 25,
        "8d" to 26,
        "8h" to 27,
        "8s" to 28,
        "9c" to 29,
        "9d" to 30,
        "9h" to 31,
        "9s" to 32,
        "Tc" to 33,
        "Td" to 34,
        "Th" to 35,
        "Ts" to 36,
        "Jc" to 37,
        "Jd" to 38,
        "Jh" to 39,
        "Js" to 40,
        "Qc" to 41,
        "Qd" to 42,
        "Qh" to 43,
        "Qs" to 44,
        "Kc" to 45,
        "Kd" to 46,
        "Kh" to 47,
        "Ks" to 48,
        "Ac" to 49,
        "Ad" to 50,
        "Ah" to 51,
        "As" to 52
    )

    init {
        if (!(this.rank in ranks && this.suit in suits)) {
                throw IllegalArgumentException("Ranks must be one of $ranks, suit must be one of $suits")
            }
    }

    fun rankToNum(): Int {
        for(r in ranks) {
            if (this.rank == r) {
                return ranks.indexOf(r)
            }
        }
        return -1
    }

    fun cardToNum(): Int {
        val card = toString()
        return cardValMap[card] as Int
    }

    override fun equals(other: Any?): Boolean {
        if (other is Card) {
            return this.rank == other.rank && this.suit == other.suit
        }
        return false
    }

    override fun hashCode(): Int {
        return this.rank.hashCode() + this.suit.hashCode()
    }

    override fun toString(): String {
        return String(charArrayOf(this.rank, this.suit))
    }

}




