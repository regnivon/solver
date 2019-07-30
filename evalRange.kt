import genCombos
import kotlin.system.measureTimeMillis

/*
Class for evaluating range vs range on all Texas Hold'em streets of play
 */

var evalCounts = 0
class EvalRange(val r1: Range, val r2: Range, var street: Int, var board: MutableSet<Card>) {
    val eval = Eval()
    val allBoards = genAllBoards()

    fun evaluate(): List<Double> {
        when (this.street) {
            0 -> {
                val counters = this.evalPre()
                val toReturn = listOf(counters[0]/counters[2], counters[1]/counters[2])
                return toReturn
            }
            1 -> {
                val counters = this.evalFlop()
                val toReturn = listOf(counters[0]/counters[2], counters[1]/counters[2])
                return toReturn
            }
            2 -> {
                val counters = this.evalTurn()
                val toReturn = listOf(counters[0]/counters[2], counters[1]/counters[2])
                return toReturn
            }
            3 -> {
                val counters = this.evalRiv(handListToInt(this.board.toList()))
                val toReturn = listOf(counters[0]/counters[2], counters[1]/counters[2])
                return toReturn
            }
        }
        return emptyList()
    }

    fun evalPre(): List<Double> {
        val boards = this.allBoards
        val counters = mutableListOf(0.0, 0.0, 0.0)
        for (board in boards) {
            val ret = evalRiv(board)
            counters[0] += ret[0]
            counters[1] += ret[1]
            counters[2] += ret[2]
        }
        return counters
    }

    fun evalFlop(): List<Double> {
        return emptyList()
    }

    fun evalTurn(): List<Double> {
        val boards = genBoards()
        val counters = mutableListOf(0.0, 0.0, 0.0)
        for (board in boards) {
            val ret = evalRiv(board)
            counters[0] += ret[0]
            counters[1] += ret[1]
            counters[2] += ret[2]
        }
        return counters
    }

    fun evalRiv(board: List<Int>): List<Double> {
        val toReturn = ArrayList<Double>(3)
        var count = 0.0
        var totalWinsp1 = 0.0
        var totalWinsp2 = 0.0
        for (hand1 in r1.allHands) {
            var overlap = false
            for (card in hand1) {
                if (card in board) {
                    overlap = true
                }
            }
            if (!overlap) {
                for (hand2 in r2.allHands) {
                    overlap = false
                    for (card in hand2) {
                        if (card in board || card in hand1) {
                            overlap = true
                        }
                    }
                    if (!overlap) {
                        count += 1
                        val h1Set = board + hand1
                        val h2Set = board + hand2
                        val h1Val = this.eval.eval(h1Set)
                        val h2Val = this.eval.eval(h2Set)
                        evalCounts += 2
                        when {
                            h1Val > h2Val -> totalWinsp1 += 1
                            h1Val < h2Val -> totalWinsp2 += 1
                            h1Val == h2Val -> {
                                totalWinsp1 += 0.5
                                totalWinsp2 += 0.5
                            }
                        }
                    }
                }
            }
        }
        toReturn.add(totalWinsp1)
        toReturn.add(totalWinsp2)
        toReturn.add(count)
        return toReturn
    }

    fun genAllBoards(): ArrayList<List<Int>> {
        val boards = arrayListOf<List<Int>>()
        val d = Deck()
        for (c3 in 1..48) {
            var h3 = listOf(c3)
            for (c4 in c3+1..49) {
                var h4 = h3+c4
                for (c5 in c4+1..50) {
                    var h5 = h4+c5
                    for (c6 in c5+1..51) {
                        var h6 = h5+c6
                        for (c7 in c6+1..52) {
                            boards.add(h6+c7)
                        }
                    }
                }
            }
        }
        return boards
    }

    fun genBoards(): ArrayList<List<Int>> {
        val boards = ArrayList<List<Int>>()
        val d = Deck()
        val board = handListToInt(this.board.toList())
        when {
            this.board.size == 4 -> {
                for (card in this.board) {
                    d.remove(card)
                }
                for (i in 0 until d.cards.size) {
                    val toAdd = board.toMutableList()
                    toAdd.add(d.cards[i].cardToNum())
                    boards.add(toAdd)
                }
            }
            this.board.size == 3 -> {
                for (card in this.board) {
                    d.remove(card)
                }
                for (i in 0 until d.cards.size-1) {
                    for (j in i+1 until d.cards.size) {
                        val toAdd = board.toMutableList()
                        toAdd.add(d.cards[i].cardToNum())
                        toAdd.add(d.cards[j].cardToNum())
                        boards.add(toAdd)
                    }
                }
            }
        }
        return boards
    }

    fun newBoard(newBoard: MutableSet<Card>) {
        this.board = newBoard
        when (this.board.size) {
            0 -> this.street = 0
            3 -> this.street = 1
            4 -> this.street = 2
            5 -> this.street = 3
        }
    }

    fun addToBoard(c: Card) {
        this.board.add(c)
        this.street += 1
    }
}

fun main(args: Array<String>) {
    val board = mutableSetOf(Card('2', 'c'), Card('2', 's'),
        Card('4', 'h'),Card('6', 'h'), Card('9', 'd'))
    val l1 = genCombosFromString("KK, QQ, KQs, KJs, KK")
    val l2 = genCombosFromString("98s, T9s, AJs, AQs, AKs")
    val h1 = Range(l1)
    val h2 = Range(l2)

    val eRange = EvalRange(h1, h2, 3, board)
    val time = measureTimeMillis {
        println(eRange.evaluate())
    }
    println(time)
    println(evalCounts)
}
