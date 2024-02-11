package ppp

import Logging
import logger
import org.apache.commons.math3.distribution.AbstractRealDistribution
import ppp.filter.PointFilter
import spaces.segmentation.Segment
import spaces.segmentation.SegmentationPoint

/**
 * The [PoissonSegment] is constructed over the cube given by the [segment].
 *
 * It will generate [numberOfPoints] points inside the cube using the [positionDistribution] to determine
 * the coordinates of the points inside the cube, i.e. the relative position to the [Segment.basePosition].
 *
 * For each point, the [pointFilter] decides if the generated point is accepted or rejected.
 *
 * @param segment the underlying [Segment] to use for the construction of this cube
 *
 * @param numberOfPoints the number of points to distribute in this [Segment]
 *
 * @param firstPointId used in the enumeration of the generated points
 *
 * @param positionDistribution the distribution to use for the position sampling of the points
 *
 * @param pointFilter the [PointFilter] that accepts or rejects the points
 */
class PoissonSegment(
    val segment: Segment,
    val numberOfPoints: Int,
    val firstPointId: Int,
    val positionDistribution: AbstractRealDistribution,
    val pointFilter: PointFilter
) : Logging {
    private val log = logger()

    /**
     * All generated points on this [segment] without [pointFilter] applied.
     */
    val allPoints: MutableMap<Int, SegmentationPoint> = mutableMapOf()

    /**
     * The points generated on this [segment] that satisfied the conditions given by [pointFilter].
     */
    val acceptedPoints: MutableMap<Int, SegmentationPoint> = mutableMapOf()

    /**
     * The points generated on this [segment] that failed the conditions given by [pointFilter].
     */
    val rejectedPoints: MutableMap<Int, SegmentationPoint> = mutableMapOf()

    private fun reset() {
        allPoints.clear()
        acceptedPoints.clear()
        rejectedPoints.clear()
    }

    /**
     * Generate the [SegmentationPoint] on this [segment].
     */
    fun generate() {
        reset()

        var pointIdSequence = firstPointId

        val unrestricted = Array(numberOfPoints) {
            SegmentationPoint(
                id = pointIdSequence++,
                uniform = positionDistribution.sample(segment.dimension),
                // Generate DoubleArray using position distribution samples.
                // These are the coordinates of the point inside the segment.
                segment = segment
            ).also {
                allPoints[it.id] = it
                // Add point to the map of all points inside this segment.
                log.atTrace()
                    .setMessage("SegmentationPoint generated")
                    .addKeyValue("segmentId", segment.basePosition.contentToString())
                    .addKeyValue("pointId", it.id)
                    .addKeyValue("absolute", it.absolute.contentToString())
                    .log()
            }
        }

        unrestricted.forEach {
            // Check the filter for this point.
            pointFilter.evaluate(it).let { accepted ->
                log.atTrace()
                    .setMessage("Point Filter")
                    .addKeyValue("pointId", it.id)
                    .addKeyValue("accepted", accepted)
                    .log()

                when (accepted) {
                    true -> acceptedPoints[it.id] = it
                    // Add point to the map of all points that were accepted by the filter.
                    false -> rejectedPoints[it.id] = it
                }
            }
        }

        log.atDebug()
            .setMessage("Segment generation completed")
            .addKeyValue("segmentId", segment.basePosition.contentToString())
            .addKeyValue("acceptedPoints.size", acceptedPoints.size)
            .addKeyValue("rejectedPoints.size", rejectedPoints.size)
            .log()
    }
}