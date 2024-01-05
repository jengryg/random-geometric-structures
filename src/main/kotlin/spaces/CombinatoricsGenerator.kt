package spaces

object CombinatoricsGenerator {
    /**
     * Given [rangeLimits] to define the ranges of each coordinate, this method generates the set of all points
     * on the integer lattice that are in the given ranges. Each given [IntRange] defined the lower and upper bound
     * for one dimension of the lattice.
     */
    fun lattice(rangeLimits: Array<IntRange>): Set<IntArray> {
        return rangeLimits.map {
            (it.first until it.last).toSet()
        }.fold(listOf(listOf<Int>())) { acc, set ->
            acc.flatMap { list -> set.map { element -> list + element } }
        }.map {
            it.toIntArray()
        }.toSet()
    }

    /**
     * Given the [lowerCorner] and the [upperCorner] as limiting points, this method generated the set of all points
     * on the integer lattice that have their components that are between or equal to the given limits.
     *
     * The [IntArray.size] of the given corners is the dimension of the generated lattice.
     */
    fun lattice(lowerCorner: IntArray, upperCorner: IntArray): Set<IntArray> {
        return Array(lowerCorner.size) {
            (lowerCorner[it] until upperCorner[it]).toSet()
        }.fold(listOf(listOf<Int>())) { acc, set ->
            acc.flatMap { list -> set.map { element -> list + element } }
        }.map {
            it.toIntArray()
        }.toSet()
    }
}