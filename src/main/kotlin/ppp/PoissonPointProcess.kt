package ppp

import Logging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import logger
import org.apache.commons.math3.distribution.PoissonDistribution
import org.apache.commons.math3.distribution.UniformRealDistribution
import ppp.filter.PointFilter
import ppp.segmentation.Segment
import ppp.segmentation.Segmentation
import ppp.segmentation.SegmentationPoint

/**
 * The [PoissonPointProcess] is constructed over the [segmentation] where [countDistributionAssigner] is used to assign
 * an instance of [PoissonDistribution] to each [Segment].
 *
 * @param segmentation the underlying [Segmentation] to use for the cube based construction
 *
 * @param countDistributionAssigner callable to assign a [PoissonPointProcess] to the given [Segment]
 *
 * @param pointFilterAssigner callable to assign a [PointFilter] to the given [Segment]
 */
class PoissonPointProcess(
    val segmentation: Segmentation,
    val countDistributionAssigner: (Segment) -> PoissonDistribution,
    val pointFilterAssigner: (Segment) -> PointFilter
) : Logging {
    private val log = logger()

    /**
     * All generated points on the complete [segmentation] without [PointFilter] applied.
     */
    val allPoints: MutableMap<Int, SegmentationPoint> = mutableMapOf()

    /**
     * The points generated on the complete [segmentation] that satisfied the conditions given by the respective
     * [PointFilter] for each [Segment].
     */
    val acceptedPoints: MutableMap<Int, SegmentationPoint> = mutableMapOf()

    /**
     * The points generated on the complete [segmentation] that failed the conditions given by the respective
     * [PointFilter] for each [Segment].
     */
    val rejectedPoints: MutableMap<Int, SegmentationPoint> = mutableMapOf()

    /**
     * All [PoissonSegment] used in the construction of this [PoissonPointProcess].
     */
    val segments: MutableMap<Segment, PoissonSegment> = mutableMapOf()

    private fun reset() {
        allPoints.clear()
        acceptedPoints.clear()
        rejectedPoints.clear()
        segments.clear()
    }

    suspend fun generate() {
        reset()

        val uniformRealDistribution = UniformRealDistribution()
        var pointIdSequence = 0

        segmentation.segments.values.forEach {
            val distribution = countDistributionAssigner(it)
            // get the distribution that determines the number of points for this sample

            val numberOfPoints = distribution.sample()
            // random number of points to generate in this segment

            log.atDebug()
                .setMessage("Creating PoissonSegment")
                .addKeyValue("segmentId", it.basePosition.contentToString())
                .addKeyValue("intensity", distribution.mean)
                .addKeyValue("numberOfPoints", numberOfPoints)
                .addKeyValue("firstPointId", pointIdSequence)
                .log()

            segments[it] = PoissonSegment(
                segment = it,
                intensity = distribution.mean,
                numberOfPoints = numberOfPoints,
                firstPointId = pointIdSequence,
                positionDistribution = uniformRealDistribution,
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
}