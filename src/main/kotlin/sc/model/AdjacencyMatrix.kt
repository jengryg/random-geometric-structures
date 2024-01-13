package sc.model

import ppp.segmentation.SegmentationPoint

/**
 * Represents an [AdjacencyMatrix] of an undirected graph where [indexes] is the set of the vertices.
 */
class AdjacencyMatrix(
    val indexes: Set<Int>
) {
    /**
     * The [adjacency] is represented as matrix with rows and columns indexed by integers.
     * The matrix does not require the indexing to be consecutive. Every set of integers can be used as
     * indexing set.
     */
    private val adjacency = indexes.associateWith { _ ->
        indexes.associateWith { _ ->
            false
        }.toMutableMap()
    }

    /**
     * Set all values in the [adjacency] to false.
     */
    fun reset() {
        indexes.forEach { row ->
            indexes.forEach { col ->
                adjacency[row]?.set(col, false)
            }
        }
    }

    /**
     * @return The number of undirected edges a graph this AdjacencyMatrix has. Connections from a vertex to itself are
     * not counted.
     */
    fun count(): Int {
        var counter = 0
        adjacency.forEach { (x, row) ->
            row.forEach { (y, c) ->
                if (c && x < y) {
                    counter++
                }
            }
        }
        return counter
    }

    /**
     * Set the connection status for [x] <-> [y] to true.
     *
     * The order of [x] and [y] does not matter.
     *
     * @param x the id of the first point of the connection
     * @param y the id of the second point of the connection
     */
    fun connect(x: Int, y: Int) {
        set(x, y, true)
    }

    /**
     * Set the connection status for [x] <-> [y] to true.
     *
     * The order of [x] and [y] does not matter.
     *
     * @param x the first point of the connection
     * @param y the second point of the connection
     */
    fun connect(x: SegmentationPoint, y: SegmentationPoint) {
        connect(x.id, y.id)
    }

    /**
     * Set the connection status for [x] <-> [y] to false.
     *
     * The order of [x] and [y] does not matter.
     *
     * @param x the id of the first point of the connection
     * @param y the id of the second point of the connection
     */
    fun disconnect(x: Int, y: Int) {
        set(x, y, false)
    }

    /**
     * Set the connection status for [x] <-> [y] to false.
     *
     * The order of [x] and [y] does not matter.
     *
     * @param x the first point of the connection
     * @param y the second point of the connection
     */
    fun disconnect(x: SegmentationPoint, y: SegmentationPoint) {
        disconnect(x.id, y.id)
    }

    /**
     * Set the connection status for [x] <-> [y] to the given [value].
     *
     * The order of [x] and [y] does not matter.
     *
     * @param x the id of the first point of the connection
     * @param y the id of the second point of the connection
     */
    fun set(x: Int, y: Int, value: Boolean) {
        // adjacency matrix must always be symmetric (we only consider undirected connections)
        adjacency[x]?.set(y, value)
        adjacency[y]?.set(x, value)
    }

    /**
     * Set the connection status for [x] <-> [y] to the given [value].
     *
     * The order of [x] and [y] does not matter.
     *
     * @param x the first point of the connection
     * @param y the second point of the connection
     */
    fun set(x: SegmentationPoint, y: SegmentationPoint, value: Boolean) {
        set(x.id, y.id, value)
    }

    /**
     * Check the status for [x] <-> [y].
     *
     * The order of [x] and [y] does not matter.
     *
     * @param x the id of the first point
     * @param y the id of the second point
     * @return true if [x] and [y] are connected, false otherwise
     */
    fun get(x: Int, y: Int): Boolean {
        return adjacency[x]?.get(y) ?: false
    }

    /**
     * Check the status for [x] <-> [y].
     *
     * The order of [x] and [y] does not matter.
     *
     * @param x the first point
     * @param y the second point
     * @return true if [x] and [y] are connected, false otherwise
     */
    fun get(x: SegmentationPoint, y: SegmentationPoint): Boolean {
        return get(x.id, y.id)
    }

    /**
     * Check the status for [point] <-> [otherPoints].
     *
     * The order of [otherPoints] does not matter.
     *
     * @param point the single point to test
     * @param otherPoints the other points to test the given single [point] against
     * @return true if [point] is connected to each of the points in [otherPoints], false otherwise
     */
    fun all(point: SegmentationPoint, otherPoints: List<SegmentationPoint>): Boolean {
        return otherPoints.all { get(point, it) }
    }

    /**
     * Return a list of the ids of points that are connected to [x].
     * If [x] is connected to [x], this list also contains [x], otherwise not.
     *
     * @param x the id of the point to get the connections for
     * @return a list with all ids of points that are connected to [x]
     */
    fun connections(x: Int): List<Int> {
        return adjacency[x]?.mapNotNull { entry ->
            entry.key.takeIf { entry.value }
        } ?: emptyList()
    }
}