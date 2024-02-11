package spaces.segmentation

import Logging
import logger
import spaces.Space
import spaces.minus
import spaces.plus
import kotlin.math.floor

/**
 * Represents the segmentation of a box in the `d`-dimensional [Double]-vector space into cubes of unit width.
 * The box is defined as the cross product of the intervals `[a,b)` where a and b are given by [IntRange.first] resp.
 * [IntRange.last] from [rangeLimits].
 *
 * The dimension `d` of underlying space is given by the size of [rangeLimits] array, i.e. each [IntRange] represents
 * one dimension of the space.
 */
class Segmentation(
    private val rangeLimits: Array<IntRange>,
) : Space, Logging {
    private val log = logger()

    override val dimension: Int
        get() = rangeLimits.size.also {
            require(it > 0) { "The dimension of the underlying space must be at least 1!" }
        }

    /**
     * The segments that form the complete [Segmentation] defined by [rangeLimits].
     *
     * Each [Segment] is a cube of width 1 that has its vertices on the integer coordinates.
     * The union of all [Segment] forms the box that is the cross product of the real number intervals corresponding
     * to the given [rangeLimits].
     */
    val segments: Map<String, Segment> = LatticeGenerator.lattice(
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

    /**
     * Find the [Segment] of this [Segmentation] that contains the [absolute] coordinates given.
     *
     * @param absolute the absolute position coordinates
     *
     * @return the segment of this segmentation that contains the [absolute] coordinates if there is any, otherwise null.
     */
    fun segmentOf(absolute: DoubleArray): Segment? {
        return segments[absolute.map { floor(it).toInt() }.toIntArray().contentToString()]
    }

    /**
     * Find the [Segment] of this [Segmentation] that contains the [absolute] coordinates given.
     * Since [absolute] is an [IntArray] this will always return the segment with [Segment.basePosition] = [absolute].
     *
     * @param absolute the absolute position coordinates
     *
     * @return the segment of this segmentation that contains the [absolute] coordinates if there is any, otherwise null.
     */
    fun segmentOf(absolute: IntArray): Segment? {
        return segments[absolute.contentToString()]
    }

    /**
     * Construct the neighborhood [Cluster] by collecting all [Segment] of the [Segmentation] that have a base position
     * that is at most [distance] away in the uniform distance.
     *
     * @param segment the center of the expansion calculation
     *
     * @param distance the limit for the uniform distance of the segments inside the neighborhood
     *
     * @return the cluster representing the expansion intersected with the segmentation
     */
    fun neighborhood(segment: Segment, distance: Int): Cluster {
        val expandArray = IntArray(dimension) { distance }

        val lattice = LatticeGenerator.lattice(
            lowerCorner = segment.basePosition - expandArray,
            upperCorner = segment.basePosition + expandArray
        ).also {
            log.atTrace()
                .addKeyValue("basePosition") { segment.basePosition.contentToString() }
                .addKeyValue("expandArray") { expandArray.contentToString() }
                .addKeyValue("lattice") {
                    it.map { it.contentToString() }
                }.log("Cluster lattice created.")
        }

        return Cluster(
            segmentation = this,
            segments = lattice.mapNotNull {
                segments[it.contentToString()]
            }
        )
    }
}