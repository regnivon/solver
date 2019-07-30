import kotlin.math.min
import kotlin.random.Random
/*
Finds the Nash equilibrium on the river between two hand ranges, returns out of position players
expected value, prints all node strategies. Assuming sufficient sampling if a strategy
node is a uniform strategy, then that means it is never reached in the Nash Equilibrium
*/

// TODO: add multithreading, maybe solve from the turn if fast enough
//sizing is in % of pot, initial pot size must be > 0
class Solve(val outOfPosition: Range, val inPosition: Range, val board: List<Int>, val potSize: Double,
            val inPositionStack: Double, val outOfPositionStack: Double, val sizing: Double) {

    private val gameTree = GameTree()
    private val rand = Random
    private val evaluator = Eval()
    private val openMap = mapOf(
        0 to "c",
        1 to "b"
    )

    private val responseMap = mapOf(
        0 to "f",
        1 to "c",
        2 to "b"
    )

    fun trainer(iterations: Int) {
        //check if hands overlap with board, if so remove
        val toRemove1 = mutableListOf<List<Int>>()
        for (hand in outOfPosition.allHands) {
            for (card in hand) {
                if (card in board) {
                    toRemove1.add(hand)
                }
            }
        }
        val toRemove2 = mutableListOf<List<Int>>()
        for (hand in inPosition.allHands) {
            for (card in hand) {
                if (card in board) {
                    toRemove2.add(hand)
                }
            }
        }
        for (hand in toRemove1) {
            outOfPosition.allHands.remove(hand)
        }
        for (hand in toRemove2) {
            inPosition.allHands.remove(hand)
        }
        //start sampling
        var util = 0.0
        for (i in 0..iterations) {
            //remove earliest strategy sum contributions to equilibrate faster
            if (i == iterations/10000) {
                for (node in gameTree.tree.values) {
                    node.resetSum()
                }
            }
            //sample two hands for each player
            //TODO: I think this takes into account removal on both ends? might need to take turns sampling first
            val ipHand = inPosition.allHands[rand.nextInt(inPosition.allHands.size)]
            var oopHand = outOfPosition.allHands[rand.nextInt(outOfPosition.allHands.size)]
            var same = false
            for (card in oopHand) {
                if (ipHand.contains(card)) {
                    same = true
                }
            }

            while (same) {
                oopHand = outOfPosition.allHands[rand.nextInt(outOfPosition.allHands.size)]
                same = false
                for (card in oopHand) {
                    if (ipHand.contains(card)) {
                        same = true
                    }
                }
            }
            val handList = listOf(oopHand, ipHand)
            util += cfr(0, "", inPositionStack, outOfPositionStack, potSize, 0.0,
                1.0, 1.0, handList)
        }
        println(util/iterations)
        //output strategy
        for (node in gameTree.tree.values) {
            println(node)
        }
    }

    //recursive counterfactual regret minimization function
    fun cfr(toAct: Int, info: String, ipStack: Double, oopStack: Double, pot: Double, betAmount: Double,
            ipProb: Double, oopProb: Double, cards: List<List<Int>>): Double {
        if (isTerminal(info, ipStack, oopStack)) {
            //find payoff
            if (info.takeLast(1) == "f") {
                val startingStack = (if (toAct == 0) this.outOfPositionStack else this.inPositionStack)
                val currentStack = (if (toAct == 0) oopStack else ipStack)
                return pot - (startingStack - currentStack)
            }
            val oopRank = evaluator.eval(cards[0] + this.board)
            val ipRank = evaluator.eval(cards[1] + this.board)
            when {
                ipRank == oopRank -> return 0.0
                oopRank > ipRank -> {
                    val startingStack = (if (toAct == 0) this.outOfPositionStack else this.inPositionStack)
                    val currentStack = (if (toAct == 0) oopStack else ipStack)
                    return (if (toAct == 0) (pot-(startingStack - currentStack)) else -(pot - (startingStack - currentStack)))
                }
                ipRank > oopRank -> {
                    val startingStack = (if (toAct == 0) this.outOfPositionStack else this.inPositionStack)
                    val currentStack = (if (toAct == 0) oopStack else ipStack)
                    return (if (toAct == 0) -(pot-(startingStack - currentStack)) else (pot - (startingStack - currentStack)))
                }
            }
        } else {
            val curInfo = cards[toAct][0].toString() + cards[toAct][1].toString() + info
            var node = gameTree.getNode(curInfo)
            if (node == null) {
                // TODO: HARDCODED
                node = if (info.takeLast(1) == "b") {
                    Node(responseMap.size, curInfo, responseMap)
                } else {
                    Node(openMap.size, curInfo, openMap)
                }

                gameTree.tree[curInfo] = node
            }
            val numActions = node.numActions
            // TODO: add bet tracking to cfr calls, track pot and stack sizes here
            val strategy = node.getStrategy(if (toAct == 0) oopProb else ipProb)
            val util = DoubleArray(numActions)
            var nodeUtil = 0.0
            for (i in 0 until numActions) {
                var betSize = 0.0
                val nextHistory = info + node.actions[i]
                if (node.actions[i] == "b") {
                    betSize = min(sizing * pot, if (toAct == 0) oopStack else ipStack)
                }
                if (node.actions[i] == "c") {
                    betSize = min(betAmount, if (toAct == 0) oopStack else ipStack)
                }
                if (node.actions[i] == "f") {
                    util[i] = if (toAct == 0)
                        - cfr(1, nextHistory, ipStack, oopStack, pot ,
                            betSize,  ipProb, oopProb * strategy[i], cards)
                    else
                        - cfr(0, nextHistory, ipStack, oopStack, pot,
                            betSize, ipProb * strategy[i], oopProb, cards)
                    nodeUtil += strategy[i] * util[i]
                } else {
                    util[i] = if (toAct == 0)
                        - cfr(1, nextHistory, ipStack, oopStack - betSize, pot + betSize,
                            betSize,  ipProb, oopProb * strategy[i], cards)
                    else
                        - cfr(0, nextHistory, ipStack - betSize, oopStack, pot + betSize,
                            betSize, ipProb * strategy[i], oopProb, cards)
                    nodeUtil += strategy[i] * util[i]
                }
            }
            for (i in 0 until numActions) {
                val regret = util[i] - nodeUtil
                node.regretSum[i] += (if (toAct == 0) ipProb else oopProb) * regret
            }
            return nodeUtil
        }
        return 0.0
    }

    //decide if the node is game ending
    private fun isTerminal(info: String, ipStack: Double, oopStack: Double): Boolean {
        if (info.takeLast(1) == "f") {
            return true
        } else if (ipStack <= 0.0 || oopStack <= 0.0) {
            return true
        } else if (info.takeLast(2) == "cc" || info.takeLast(2) == "bc") {
            return true
        }
        return false
    }
}

fun main() {
    val board = handListToInt(listOf(Card('2', 'c'), Card('2', 's'),
        Card('4', 'h'),Card('6', 'h'), Card('9', 'd')))
    //from misc
    val l1 = genCombosFromString("KQs, KJs, KK")
    val l2 = genCombosFromString("98s, T9s, AJs, AQs, AKs")
    val h1 = Range(l1)
    val h2 = Range(l2)
    val s = Solve(h1, h2, board, 40.0, 100.0, 100.0, 0.75)
    s.trainer(100000000)
}