import java.io.*


/*
algorithm using large lookup table, which is generated to rank 7 card poker hands, lookup table code
borrowed from the pokerbot-ai competition, all java files were written by the original evaluator
authors. Table only needs to be generated on a machine once then can be read into an array on startup
 */

class Eval {
    private val tablePath = "src/HandRanks.dat"
    private val tableFile = File(tablePath)
    private var lookUp = IntArray(0)
    val handVals = mapOf(
        0 to "Invalid",
        1 to "High card",
        2 to "One pair",
        3 to "Two pairs",
        4 to "Three of a kind",
        5 to "Straight",
        6 to "Flush",
        7 to "Full house",
        8 to "Four of a kind",
        9 to "Straight flush"
    )

    init {
        if (tableFile.exists()) {
            val inStream = ObjectInputStream(FileInputStream(tablePath) as InputStream?)
            lookUp = inStream.readObject() as IntArray
        } else {
            Generator.generateTables()
            lookUp = Generator.handRanks
            val filename = "src/handRanks.dat"
            val oos = ObjectOutputStream(FileOutputStream(filename) as OutputStream?)
            oos.writeObject(Generator.handRanks)
        }
    }

    // basically lookup table is a massive linked list that you travel through, and eventually
    // reach a final ranking after inputting the values of all 7 cards held
    // this final evaluation also holds information about the type of hand i.e. pair,
    // two pair available through a right bit shift
    fun eval(hand: List<Int>): Int {
        var p = 53
        for (item in hand) {
            p = lookUp[p + item]
        }
        return p
    }

    fun evalToRank(eval: Int): String? {
        val numRank = eval shr 12
        return handVals[numRank]
    }
}




