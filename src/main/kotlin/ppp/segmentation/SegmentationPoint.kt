package ppp.segmentation

import spaces.spaces.PointAbstract

/**
 * Represents a point in the underlying space with respect to the [Segmentation] of the space.
 *
 * @param id a unique integer identifying this point (sequential enumeration should be used)
 * @param uniform the coordinates of this point relative to the [Segment]
 * @param segment the segment this point is located in
 */
class SegmentationPoint(
    val id: Int,
    val uniform: DoubleArray,
    val segment: Segment,
) : PointAbstract<Segmentation>(
    space = segment.segmentation,
    absolute = segment.absolute(uniform)
)