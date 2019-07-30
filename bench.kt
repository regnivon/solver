import kotlin.system.measureTimeMillis


fun main(args: Array<String>) {
    val e = Eval()
    var count = 0
    val time = measureTimeMillis {
        for (c1 in 1..46) {
            var h1 = listOf(c1)
            for (c2 in c1+1..47) {
                var h2 = h1 + c2
                for (c3 in c2+1..48) {
                    var h3 = h2+c3
                    for (c4 in c3+1..49) {
                        var h4 = h3+c4
                        for (c5 in c4+1..50) {
                            var h5 = h4+c5
                            for (c6 in c5+1..51) {
                                var h6 = h5+c6
                                for (c7 in c6+1..52) {
                                    e.eval(h6+c7)
                                    count +=1
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    println(time)
    println(count)


}