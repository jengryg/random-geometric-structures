package spaces.spaces

/**
 * Simple base class to represent a Point in a specific space [S].
 *
 * @param space the underlying space this point belongs to
 * @param position the absolute position inside the space this point is located at as [DoubleArray]
 */
abstract class PointAbstract<S : SpaceAbstract>(
    val space: S,
    val position: DoubleArray
) {
    init {
        assert(space.dimension == position.size) { "The point does not belong to the given space." }
    }
}