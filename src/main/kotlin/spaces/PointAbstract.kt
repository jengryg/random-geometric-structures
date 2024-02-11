package spaces

/**
 * Simple base class to represent a Point in a specific space [S].
 *
 * @param space the underlying space this point belongs to
 * @param absolute the absolute position inside the space this point is located at as [DoubleArray]
 */
abstract class PointAbstract<S : SpaceAbstract>(
    val space: S,
    val absolute: DoubleArray
) {
    init {
        assert(space.dimension == absolute.size) { "The point does not belong to the given space." }
    }
}