package spaces.segmentation

import spaces.Space

/**
 * Represents a point in the underlying space with respect to the [Segmentation] of the space.
 *
 * The point is constructed using a [uniform] position inside the unit-cube and the [segment] it is assigned to.
 *
 * @param id a unique integer identifying this point (sequential enumeration should be used)
 *
 * @param uniform the coordinates of this point relative to the [Segment]
 *
 * @param segment the segment this point is located in
 */
class Point(
    val id: Int,
    val uniform: DoubleArray,
    val segment: Segment
) : Space by segment {
    /**
     * The points absolute position in the space is its [uniform] position translated by the [segment] base position.
     */
    val absolute: DoubleArray = segment.absolute(uniform)
}