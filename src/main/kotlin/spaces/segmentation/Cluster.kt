package spaces.segmentation

import Logging
import logger
import spaces.Space


/**
 * The [Cluster] represents a box inside the underlying [Segmentation] that can be represented as the union of the
 * given [segments].
 */
class Cluster(
    val segmentation: Segmentation,
    val segments: List<Segment>
) : Space by segmentation, Logging {
    private val log = logger()

    /**
     * Component wise minimum of the [Segment.basePosition] calculated over all [segments].
     * This is the lowest corner of the box that is formed by this cluster.
     */
    val lowerBound: IntArray

    /**
     * Component wise maximum of the [Segment.basePosition] calculated over all [segments].
     * This is the highest corner of the box that is formed by this cluster.
     */
    val upperBound: IntArray

    init {
        assert(segments.isNotEmpty()) { "Cluster must be created with at least one segment." }

        lowerBound = segments.let {
            IntArray(dimension) { coordinateIndex ->
                it.minOf { it.basePosition[coordinateIndex] }
            }
        }

        upperBound = segments.let {
            IntArray(dimension) { coordinateIndex ->
                segments.maxOf { it.basePosition[coordinateIndex] } + 1
                // We need to add 1 to the position to account for the size of the segment cube itself.
            }
        }

        log.atDebug()
            .setMessage("Cluster created")
            .addKeyValue("lowerBound", lowerBound.contentToString())
            .addKeyValue("upperBound", upperBound.contentToString())
            .addKeyValue("segmentCount", segments.size)
            .log()
    }

    /**
     * The given [absolute] position is contained in this cluster if and only if this method returns true.
     * The [lowerBound] and [upperBound] are pre-calculated and stored in this object to speed up this method
     * by removing the need for repeated iteration over [segments].
     */
    fun contains(absolute: DoubleArray): Boolean {
        repeat(dimension) {
            if (lowerBound[it] > absolute[it] || upperBound[it] <= absolute[it]) {
                return false
            }
        }

        return true
    }
}