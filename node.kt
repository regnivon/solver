import java.util.Arrays

class Node(val numActions: Int, val infoSet: String, val actions: Map<Int, String>/*, val pot: Double, val ipStack: Double, val oopStack: Double*/) {
    //if strategy is less than this value, set to 0
    val threshold = 0.001
    val regretSum = DoubleArray(numActions)
    private val strategy = DoubleArray(numActions)
    private val strategySum = DoubleArray(numActions)

    fun getStrategy(weight: Double): DoubleArray {
        var normalizingSum = 0.0
        var normalize = false

        for (i in 0 until strategy.size) {
            strategy[i] = maxOf(regretSum[i], 0.0)
            normalizingSum += strategy[i]
        }
        for (i in 0 until strategy.size) {
            if (normalizingSum > 0) {
                strategy[i] /= normalizingSum
            } else {
                strategy[i] = 1.0/strategy.size
            }
            if (strategy[i] < threshold) {
                normalize = true
                strategy[i] = 0.0
            }
        }
        for (i in 0 until strategy.size) {
            strategySum[i] += weight * strategy[i]
        }
        if (normalize) {
            normalizingSum = 0.0
            for (i in 0 until strategy.size) {
                normalizingSum += strategy[i]
            }
            for (i in 0 until strategy.size) {
                strategy[i] /= normalizingSum
            }
        }
        return strategy
    }

    fun getAverageStrategy(): DoubleArray {
        var normalizingSum = 0.0
        for (i in 0 until strategy.size) {
            normalizingSum += strategySum[i]
        }
        for (i in 0 until strategy.size) {
            if (normalizingSum > 0) {
                strategySum[i] /= normalizingSum
            } else {
                strategySum[i] = 1.0/strategySum.size
            }
        }
        return strategySum
    }

    fun resetSum() {
        for (i in 0 until strategySum.size) {
            strategySum[i] = 0.0
        }
    }

    override fun toString(): String {
        return "$infoSet ${Arrays.toString(this.getAverageStrategy())}"
    }
}