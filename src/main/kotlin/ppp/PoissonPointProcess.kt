package ppp

import Logging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import logger
import org.apache.commons.math3.distribution.AbstractIntegerDistribution
import org.apache.commons.math3.distribution.AbstractRealDistribution
import org.apache.commons.math3.distribution.PoissonDistribution
import org.apache.commons.math3.distribution.UniformRealDistribution
import ppp.filter.AcceptAllPointFilter
import ppp.filter.PointFilter
import spaces.segmentation.Point
import spaces.segmentation.Segment
import spaces.segmentation.Segmentation

/**
 * The [ppp.PoissonPointProcess] is constructed over the [segmentation] where each segment is assigned
 * an [AbstractIntegerDistribution] to sample the number of points for that segment from,
 * an [AbstractRealDistribution] to sample each coordinate entry for the position of the points in that segment from.
 *
 * The [pointFilterAssigner] assigns a [PointFilter] to each [Segment] and operates on the absolute coordinate of the
 * sampled points in the corresponding segment to accept or reject points.
 *
 * @param segmentation the underlying [Segmentation] to use for the cube based construction
 *
 * @param countDistributionAssigner callable to assign an [AbstractIntegerDistribution] to the given [Segment]
 *
 * @param positionDistributionAssigner callable to assign an [AbstractRealDistribution] to the given [Segment]
 *
 * @param pointFilterAssigner callable to assign a [PointFilter] to the given [Segment]
 */
class PoissonPointProcess(
    val segmentation: Segmentation,
    val countDistributionAssigner: (Segment) -> AbstractIntegerDistribution,
    val positionDistributionAssigner: (Segment) -> AbstractRealDistribution,
    val pointFilterAssigner: (Segment) -> PointFilter
) : Logging {
    private val log = logger()

    /**
     * All generated points on the complete [segmentation] without [PointFilter] applied.
     */
    val allPoints: MutableMap<Int, Point> = mutableMapOf()

    /**
     * The points generated on the complete [segmentation] that satisfied the conditions given by the respective
     * [PointFilter] for each [Segment].
     */
    val acceptedPoints: MutableMap<Int, Point> = mutableMapOf()

    /**
     * The points generated on the complete [segmentation] that failed the conditions given by the respective
     * [PointFilter] for each [Segment].
     */
    val rejectedPoints: MutableMap<Int, Point> = mutableMapOf()

    /**
     * All [PoissonSegment] used in the construction of this [PoissonPointProcess] indexed by their [Segment]
     * using the base [Segment.basePosition] as key.
     */
    val segments: MutableMap<String, PoissonSegment> = mutableMapOf()

    private fun reset() {
        allPoints.clear()
        acceptedPoints.clear()
        rejectedPoints.clear()
        segments.clear()
    }

    /**
     * Generate the points for all segments in the segmentation using [async] to parallelize with kotlin coroutines.
     */
    suspend fun generate() {
        reset()

        var pointIdSequence = 0

        segmentation.segments.values.forEach {
            val distribution = countDistributionAssigner(it)
            // get the distribution that determines the number of points for this sample

            val numberOfPoints = distribution.sample()
            // random number of points to generate in this segment

            log.atDebug()
                .setMessage("Creating PoissonSegment")
                .addKeyValue("segmentId", it.basePosition.contentToString())
                .addKeyValue("numberOfPoints", numberOfPoints)
                .addKeyValue("firstPointId", pointIdSequence)
                .log()

            segments[it.basePosition.contentToString()] = PoissonSegment(
                segment = it,
                numberOfPoints = numberOfPoints,
                firstPointId = pointIdSequence,
                positionDistribution = positionDistributionAssigner(it),
                pointFilter = pointFilterAssigner(it)
            )

            pointIdSequence += numberOfPoints
            // Increase the pointIdSequence by the numberOfPoints. Thus, the next PoissonSegment
        }

        val generations = withContext(Dispatchers.Default) {
            segments.values.map { async { it.generate() } }
            // Use async to run the generation in parallel on multiple worker threads.
        }

        generations.awaitAll()

        segments.values.forEach {
            allPoints += it.allPoints
            acceptedPoints += it.acceptedPoints
            rejectedPoints += it.rejectedPoints
        }

        log.atInfo()
            .setMessage("PoissonPointProcess generation completed")
            .addKeyValue("allPoints.size", allPoints.size)
            .addKeyValue("acceptedPoints.size", acceptedPoints.size)
            .addKeyValue("rejectedPoints.size", rejectedPoints.size)
            .log()
    }

    companion object {
        /**
         * Generates a [PoissonPointProcess] with constant [intensity] for all segments in [Segmentation], using the
         * [PoissonDistribution] for the point numbers and the [UniformRealDistribution] for the positions.
         *
         * @param segmentation the underlying [Segmentation] this process should be generated on
         * @param intensity the average number of points per segment used as parameter for the [PoissonDistribution]
         *
         * @return initialized but not yet generated [PoissonPointProcess] on [Segmentation] with [intensity]
         */
        fun simple(segmentation: Segmentation, intensity: Double): PoissonPointProcess {
            val pointDistribution = PoissonDistribution(intensity)
            val uniformRealDistribution = UniformRealDistribution()
            val pointFilter = AcceptAllPointFilter()

            return PoissonPointProcess(
                segmentation = segmentation,
                countDistributionAssigner = { pointDistribution },
                positionDistributionAssigner = { uniformRealDistribution },
                pointFilterAssigner = { pointFilter }
            )
        }
    }
}