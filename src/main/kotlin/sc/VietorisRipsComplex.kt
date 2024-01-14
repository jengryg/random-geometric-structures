package sc

import Logging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import logger
import ppp.segmentation.Cluster
import ppp.segmentation.Segment
import ppp.segmentation.SegmentationPoint
import sc.model.SimplexModel
import spaces.metrics.Metric
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil

/**
 * Representation of the Vietoris-Rips (simplicial) complex.
 *
 * Provides an algorithm to construct the Vietoris-Rips complex of a given list of [SegmentationPoint] that are used as
 * vertices using [delta] as distance limiting parameter and the [metric] to calculate the distance of two vertices.
 *
 * See [Vietoris-Rips Complex](https://en.wikipedia.org/wiki/Vietoris%E2%80%93Rips_complex) fore more information.
 */
class VietorisRipsComplex(
    vertices: List<SegmentationPoint>,
    val metric: Metric,
    val delta: Double
) : SimplicialComplex(vertices), Logging {
    private val log = logger()

    /**
     * The segments of the underlying segmentation are cubes of width 1.
     * We [ceil] the distance parameter [delta] to get the number of segments we need to extend for the algorithm.
     */
    private val clusterExtension = ceil(delta).toInt()

    /**
     * Map the [Segment] to its neighborhood [Cluster] to avoid the recalculation of the neighborhoods in the [generate]
     * algorithm.
     *
     * The neighborhood [Cluster] of a given [Segment] is composed of all segments in the segmentation that are at most
     * clusterExtension far away from the given [Segment]. If we select a point from the given [Segment] all points
     * outside of this neighborhood cluster are already rejected as possible candidates to form an edge with the
     * selected point by construction, since their distance is least [clusterExtension] far away.
     */
    private val neighbourhoodOfSegment: Map<Segment, Cluster> =
        pointsBySegment.keys.associateWith { it.neighborhood(clusterExtension) }

    /**
     * In the Vietoris-Rips complex, two vertices are considered to be adjacent if their distance is smaller than the
     * given [delta]. The distance is calculated using the [metric] assigned to this [VietorisRipsComplex].
     */
    override fun calculateAdjacencyMatrix() {
        adjacencyMatrix.reset()

        pointsBySegment.forEach { (coreSegment, corePoints) ->
            /**
             * Get the list of points that can form an edge with the points from [coreSegment].
             * The usage of the [Segment] can reduce the amount of potential candidates greatly, depending on
             * the size of [delta] and the number of [Segment] the points are distributed across.
             */
            val candidates = getCandidatePointsForSegment(coreSegment)

            corePoints.forEach { x ->
                candidates.forEach { y ->
                    if (x.id < y.id) {
                        // Since we only consider undirected connections, we only need to check the x.id < y.id
                        // combinations.
                        adjacencyMatrix.set(x, y, metric.distance(x, y) < delta)
                    }
                }
            }
        }

        log.atDebug()
            .setMessage("Vietoris-Rips AdjacencyMatrix calculated")
            .addKeyValue("connections") { adjacencyMatrix.count() }
            .log()
    }

    /**
     * Determine the simplicies in this [VietorisRipsComplex] up to the given [upToDimension] limit for the dimension
     * of the simplicies. The limit is optional, use null if you want to generate all simplicies possible.
     *
     * Example dimensions:
     * - a vertex has dimension `0`
     * - an edge has dimension `1`
     * - a filled triangle has dimension `2`
     * - a simplex with `n+1` vertices has dimension `n`
     *
     * This method requires the [adjacencyMatrix] to be known.
     *
     * Note: Depending on the connectivity indicated by the [adjacencyMatrix], the number of simplicies can grow really
     * fast in the number of vertices. Since we store all simplicies that were calculated in [simplicies], the RAM
     * consumption can quickly exceed the available memory.
     *
     * @param upToDimension the optional limit on the dimension of what simplicies to calculate
     */
    suspend fun generate(upToDimension: Int? = null) {
        val maxDimension = when (upToDimension) {
            null -> vertices.size - 1
            // since you need `n+1` vertices to build a simplex with dimension `n`, the total number of vertices minus
            // one is the limit for the dimension possible in the simplicial complex
            else -> upToDimension
        }

        log.atDebug()
            .setMessage("Generating Vietoris Rips")
            .addKeyValue("maxDimension", maxDimension)
            .addKeyValue("vertexCount", vertices.size)
            .log()

        var totalSimplexCount = findZeroDimensionalSimplicies()
        // initialize total simplex count with the returned count of 0-dimensional simplicies

        if (maxDimension == 0) {
            return
        }

        totalSimplexCount = findTwoDimensionalSimplicies(totalSimplexCount)

        if (maxDimension == 1) {
            return
        }

        var currentDimension = 2
        var countDimensionBefore = simplexCollection(1).size


        // Use shouldSimplexSearchContinue to check if the next iteration of the algorithm for simplex construction
        // should be run.
        while (shouldSimplexSearchContinue(maxDimension, currentDimension, countDimensionBefore)) {
            totalSimplexCount = constructHigherDimensionalSimplicies(currentDimension, totalSimplexCount)

            // update vars for next iteration
            countDimensionBefore = simplexCollection(currentDimension).size
            currentDimension++
        }
    }

    /**
     * The method initializes an internal counter at 0 and returns the state of the counter at the end. The counter is
     * used for the [SimplexModel.id] enumeration.
     *
     * @return the value to use for the next [SimplexModel.id]
     */
    private fun findZeroDimensionalSimplicies(): Int {
        // Running counter to assign a unique id to each simplex we find.
        // Since the zero dimensional simplicies are the first ones we will add, the counter is initialized with 0.
        var simplexId = 0

        // 0-dim simplicies are the vertices themselves
        val collection = simplexCollection(0)

        vertices.forEach { point ->
            collection.add(
                SimplexModel(id = simplexId++, vertices = listOf(point)).also {
                    log.atTrace()
                        .setMessage("Simplex constructed")
                        .addKeyValue("id", it.id)
                        .addKeyValue("dim", 0)
                        .addKeyValue("vertices") {
                            it.vertices.map { v -> v.id }
                        }
                        .log()
                }
            )
        }

        log.atDebug()
            .setMessage("Generating Vietoris Rips")
            .addKeyValue("SimplexDimension", 0)
            .addKeyValue("SimplexCount", collection.size)
            .log()

        return simplexId
    }

    /**
     * The [initialCounterValue] is used as starting value for an internal counter in this method and the state of the
     * counter is returned at the end. The counter is used for the [SimplexModel.id] enumeration.
     *
     * @param initialCounterValue the value to use for the next [SimplexModel.id]
     *
     * @return the value to use for the next [SimplexModel.id]
     */
    private suspend fun findTwoDimensionalSimplicies(initialCounterValue: Int): Int {
        // Running counter to assign a unique id to each simplex we find.
        // Since this is shared between the coroutines that increment and use it below, we need AtomicInteger here:
        val simplexId = AtomicInteger(initialCounterValue)

        val generation = withContext(Dispatchers.Default) {
            pointsBySegment.map { (coreSegment, corePoints) ->
                // Note: corePoints is already pre-sorted by id to simplify the handling of different permutations for
                // the vertices of a simplex by sorting the points using their id.
                async {
                    // We iterate over each segment since we can use the cluster extension to reduce the number of possible
                    // candidates that can form a simplex with the selected point.

                    val collector = mutableListOf<SimplexModel>()

                    /**
                     * Get the list of points that can form an edge with the points from [coreSegment].
                     * The usage of the [Segment] can reduce the amount of potential candidates greatly, depending on
                     * the size of [delta] and the number of [Segment] the points are distributed across.
                     */
                    val candidates = getCandidatePointsForSegment(coreSegment)

                    corePoints.forEach { x ->
                        val startingIndex = candidates.indexOf(x).also {
                            assert(it < 0) { "Could not determine the starting index of point #${x.id}" }
                            // If x can not be found in candidates, the found index is -1.
                            // By construction, x must be found in the candidates list itself.
                        }

                        for (idx in startingIndex + 1 until candidates.size) {
                            candidates[idx].let { y ->
                                // Since the candidates list is sorted by the SegmentationPoint ids, the loop for idx
                                // ensures that x.id < y.id is always true.
                                if (adjacencyMatrix.get(x, y)) {
                                    // If these vertices are adjacent, they form an edge, which is a 1-dimensional simplex.
                                    collector.add(
                                        SimplexModel(
                                            id = simplexId.incrementAndGet(),
                                            vertices = listOf(x, y)
                                        ).also {
                                            log.atTrace()
                                                .setMessage("Simplex constructed")
                                                .addKeyValue("id", it.id)
                                                .addKeyValue("dim", 1)
                                                .addKeyValue("vertices") {
                                                    it.vertices.map { v -> v.id }
                                                }
                                                .log()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    return@async collector
                }
            }
        }

        val collection = simplexCollection(1)
        generation.awaitAll().forEach {
            collection.addAll(it)
        }

        log.atDebug()
            .setMessage("Generated Vietoris Rips")
            .addKeyValue("SimplexDimension", 1)
            .addKeyValue("SimplexCount", collection.size)
            .log()

        return simplexId.get()
    }

    /**
     * The [initialCounterValue] is used as starting value for an internal counter this method and the state of the
     * counter is returned at the end. The counter is used for the [SimplexModel.id] enumeration.
     *
     * @param initialCounterValue the value to use for the next [SimplexModel.id]
     *
     * @return the value to use for the next [SimplexModel.id]
     */
    private suspend fun constructHigherDimensionalSimplicies(targetDimension: Int, initialCounterValue: Int): Int {
        val simplexId = AtomicInteger(initialCounterValue)
        val supply = simplexCollection(targetDimension - 1)

        val generation = withContext(Dispatchers.Default) {
            pointsBySegment.map { (coreSegment, corePoints) ->
                // Note: corePoints is already pre-sorted by id to simplify the handling of different permutations for
                // the vertices of a simplex by sorting the points using their id.
                async {
                    // We iterate over each segment since we can use the cluster extension to reduce the number of possible
                    // candidates that can form a simplex with the selected point.

                    val collector = mutableListOf<SimplexModel>()

                    /**
                     * Get the list of simplicies that can form a larger simplex with the points from [coreSegment].
                     * The usage of the [Segment] can reduce the amount of potential candidates greatly, depending on
                     * the size of [delta] and the number of [Segment] the points are distributed across.
                     */
                    val candidates = getCandidateSimpliciesForSegment(coreSegment, supply)

                    corePoints.forEach { p ->
                        candidates.forEach { simplex ->
                            if (p.id < simplex.lowestVertexId && adjacencyMatrix.all(p, simplex.vertices)) {
                                // The vertex is not part of the simplex already, and it is connected to each vertex of the
                                // simplex.
                                collector.add(
                                    SimplexModel(
                                        id = simplexId.incrementAndGet(),
                                        vertices = mutableListOf(p).also { it.addAll(simplex.vertices) }.toList()
                                            .sortedBy { it.id }
                                    ).also {
                                        log.atTrace()
                                            .setMessage("Simplex constructed")
                                            .addKeyValue("id", it.id)
                                            .addKeyValue("dim", targetDimension)
                                            .addKeyValue("vertices") {
                                                it.vertices.map { v -> v.id }
                                            }
                                            .log()
                                    }
                                )
                            }
                        }
                    }

                    return@async collector
                }
            }
        }

        val collection = simplexCollection(targetDimension)
        generation.awaitAll().forEach {
            collection.addAll(it)
        }

        log.atDebug()
            .setMessage("Generated Vietoris Rips")
            .addKeyValue("SimplexDimension", targetDimension)
            .addKeyValue("SimplexCount", collection.size)
            .log()

        return simplexId.get()
    }

    /**
     * Use the segmentation construction with the [neighbourhoodOfSegment] to determine all points that are close enough
     * to the given [segment] so that they could form an edge with the points inside it. The points returned by this
     * method are not guaranteed to form an edge, but **all points that are not returned by this method can never form
     * an edge since they are too far away by construction**.
     *
     * @param segment the candidate points can form an edge with the points in this given [Segment]
     *
     * @return a list of points that can (but not must) form an edge with the points from [segment]
     */
    private fun getCandidatePointsForSegment(segment: Segment): List<SegmentationPoint> {
        return neighbourhoodOfSegment[segment]!!.segments.mapNotNull {
            pointsBySegment[it]
        }.flatten().sortedBy { it.id }.also {
            log.atTrace()
                .setMessage("Candidate points in neighborhood")
                .addKeyValue("neighborhoodOf", segment.basePosition.contentToString())
                .addKeyValue("candidates.size", it.size)
                .log()
        }
    }

    /**
     * Use the segmentation construction with the [neighbourhoodOfSegment] to determine all simplicies of the given
     * [supply] that could form a larger simplex with the one of the points inside the given [segment]. The simplicies
     * returned by this method are not guaranteed to form a new larger simplex, but all simplicies that are not returned
     * by this method can never form a larger simplex since they are too far away.
     *
     * @param segment the candidate simplicies can form a larger simplex with the points in this given [segment]
     * @param supply the list of simplicies that the candidates are taken from
     *
     * @return a list of [SimplexModel] that can (but not must) form a larger simplex with the points from [segment]
     */
    private fun getCandidateSimpliciesForSegment(segment: Segment, supply: List<SimplexModel>): List<SimplexModel> {
        return neighbourhoodOfSegment[segment]!!.let { cluster ->
            supply.filter { it.containedIn(cluster) }
            // All simplicies that have all their points in the neighborhood of this segment are candidates to form an
            // edge. Simplicies that are not completely(!) in this neighborhood can never form a larger simplex, since
            // they contain at least one point that can not be connected to the new from the current segment.
        }.also {
            log.atTrace()
                .setMessage("Candidate simplicies in neighborhood")
                .addKeyValue("neighborhoodOf", segment.basePosition.contentToString())
                .addKeyValue("candidates.size", it.size)
                .log()
        }
    }

    /**
     * A simplex with dimension `n` in a Vietoris-Rips complex is a complete graph with `n+1` vertices.
     * A complete graph with `n+1` vertices contains `(n+1) choose n` different complete sub-graphs with `n` vertices.
     * Thus, we need at least `(n+1) choose n = n+1` simplicies of dimension `n-1` to construct a simplex of dimension
     * `n` here.
     *
     * The algorithm can stop if one of the following conditions is true:
     * - It has reached the [maxDimension] that should be achieved.
     * - There are not enough simplicies in the previous dimension to construct a simplex of higher dimension, i.e.
     * `countDimensionBefore < nextDimension + 1`.
     *
     * @param maxDimension the given limit for the dimension of the simplicies to construct
     * @param nextDimension the next dimension we would construct in the algorithm
     * @param countDimensionBefore how many simplicies were constructed in the last iteration of the algorithm
     *
     * @return true if the algorithm should continue, false if not
     */
    private fun shouldSimplexSearchContinue(
        maxDimension: Int,
        nextDimension: Int,
        countDimensionBefore: Int
    ): Boolean {
        if (maxDimension < nextDimension) {
            log.atInfo()
                .setMessage("Vietoris-Rips generation reached maxDimension")
                .addKeyValue("maxDimension", maxDimension)
                .addKeyValue("nextDimension", nextDimension)
                .addKeyValue("countDimensionBefore", countDimensionBefore)
                .log()
            return false
        }

        if (countDimensionBefore < nextDimension + 1) {
            log.atInfo()
                .setMessage("Vietoris-Rips generation exhausted all possible simplicies")
                .addKeyValue("maxDimension", maxDimension)
                .addKeyValue("nextDimension", nextDimension)
                .addKeyValue("countDimensionBefore", countDimensionBefore)
                .log()
            return false
        }

        return true
    }
}