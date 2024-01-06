package ppp.segmentation

import Logging
import logger
import spaces.CombinatoricsGenerator
import spaces.spaces.SpaceAbstract

/**
 * Represents the segmentation of a box in the underlying [SpaceAbstract] into unit sized `d`-dimensional cubes.
 * The box is defined as the cross product of the intervals `[x,y)` where x and y are given by [IntRange.first] and
 * [IntRange.last] from [rangeLimits].
 *
 * Example:
 *
 * The Array [IntRange(-2,4), IntRange(3,10)] creates a segmentation of the 2-dimensional rectangle given
 * by `[-2,4) x [3,10)`.
 */
class Segmentation(
    val rangeLimits: Array<IntRange>,
) : SpaceAbstract(rangeLimits.size), Logging {
    private val log = logger()

    /**
     * The segments that form the complete [Segmentation] defined by [rangeLimits].
     *
     * Each [Segment] is a cube of width 1 that has its vertices on the integer coordinates.
     * The union of all [Segment] forms the box that is the cross product of the real number intervals corresponding
     * to the given [rangeLimits].
     */
    val segments: Map<String, Segment> = CombinatoricsGenerator.lattice(
        rangeLimits = rangeLimits.map { IntRange(it.first, it.last - 1) }.toTypedArray()
        // Since a segment has width 1, and we use the lattice for the base points, we have to exclude the upper bound
        // of the ranges for the base point generation to ensure that the union space ends at the upper limit.
    ).map {
        Segment(segmentation = this, basePosition = it)
    }.associateBy {
        it.basePosition.contentToString()
    }.also {
        log.atDebug()
            .setMessage("Segmentation Created")
            .addKeyValue("segmentCount", it.size)
            .log()
    }
}