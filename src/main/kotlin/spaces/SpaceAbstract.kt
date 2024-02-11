package spaces

/**
 * The basic interface for representation of a [dimension]-dimensional space, usually `\R^d`, i.e. the d-dimensional
 * vector space constructed over the real numbers.
 *
 * @param dimension the dimension of this space
 */
abstract class SpaceAbstract(
    final override val dimension: Int
) : Space {
    init {
        assert(dimension > 0) { "The dimension of the underlying space must be greater than 0." }
    }
}