import java.util.concurrent.ConcurrentHashMap

//hold game tree in a thread safe map
//TODO: add prune tree method to drop nodes with uniform strategies before output
class GameTree {

    val tree = ConcurrentHashMap<String, Node>()

    fun getNode(history: String): Node? {
        return this.tree[history]
    }


}
