package ppp.segmentation

import spaces.CombinatoricsGenerator
import spaces.minus
import spaces.plus
import spaces.spaces.Space

/**
 * The segment is a unit sized box that belongs to the [segmentation] and is identified by its [basePosition] that is
 * the lower left corner of the box, i.e. it is the point of the box that has the lowest value for each coordinate entry
 * possible. The box can then be represented as the Minkowski sum of `{b} + [0,1)^d` where `{b}` is the set containing
 * only the base point and `[0,1)^d` is the left closed and right opened interval raised to the dimension `d`.
 */
class Segment(
    val segmentation: Segmentation,
    val basePosition: IntArray,
) : Space by segmentation {
    /**
     * Add the [basePosition] of this segment to the [relative] position to get the absolute position.
     *
     * @param relative the coordinates relative to the [basePosition] of this segment
     *
     * @return absolute position coordinates
     */
    fun absolute(relative: DoubleArray): DoubleArray {
        return relative + basePosition
    }

    /**
     * Subtract the [basePosition] of this segment from the [absolute] position to get the relative position.
     *
     * @param absolute the absolute position coordinates
     *
     * @return the coordinates relative to the [basePosition] of this segment
     */
    fun relative(absolute: DoubleArray): DoubleArray {
        return absolute - basePosition
    }

    /**
     * Indicated if the [absolute] position is inside this segment or not.
     * Since the upper bound is considered to be exclusive and the lower bound is inclusive, every possible absolute
     * position can only be part of at most one segment in the complete [Segmentation] by construction.
     *
     * @return true if [absolute] is in this segment, false otherwise
     */
    fun contains(absolute: DoubleArray): Boolean {
        absolute.forEachIndexed { index, d ->
            if (d < basePosition[index] || d >= basePosition[index] + 1) {
                return false
            }
        }

        return true
    }

    /**
     * Construct the cluster by taking the [basePosition] of this segment and find all other segments of the
     * [segmentation] that have a [basePosition] that is at most [expand] far away.
     *
     * This is achieved by calculating the position given by
     */
    fun neighborhood(expand: Int = 1): Cluster {
        val expandArray = IntArray(dimension) { expand }

        return Cluster(
            segmentation = segmentation,
            segments = CombinatoricsGenerator.lattice(
                lowerCorner = basePosition - expandArray,
                upperCorner = basePosition + expandArray
            ).mapNotNull {
                segmentation.segments[it]
            }
        )
    }
}