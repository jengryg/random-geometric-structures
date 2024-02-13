package convex

import Logging
import logger
import spaces.Space
import spaces.minus
import spaces.segmentation.Point
import spaces.segmentation.Segmentation

/**
 * Representation of the 2-dimensional convex hull of a given set of [points].
 *
 * See [Convex Hull](https://en.wikipedia.org/wiki/Convex_hull) for more information.
 *
 * Note: The implemented algorithm operates only in the plane. The [Point] supplied to this class can be
 * higher-dimensional thus we use a [PlaneProjection] to map each point to its associated (x,y) position in a
 * 2-dimensional plane.
 *
 * Note: This algorithm operates on the [DoubleArray] coordinates. Thus, it is susceptible to floating-point errors,
 * i.e. (0.1, 0.4), (0.2, 0.3), (0.3,0.2), (0.4, 0.1) are solutions to the equation 0.5 - x = y and thus collinear, but
 * for they are not collinear for the [ConvexHull] calculation due to floating-point errors.
 *
 * @param points the points that should be considered for the convex hull
 * @param planeProjection the converter that maps [Point] to its (x,y) position in the plane
 */
class ConvexHull(
    val points: List<Point>,
    private val planeProjection: PlaneProjection,
) : Space by points.first(), Logging {
    private val log = logger()

    /**
     * The [Segmentation] the [points] supplied for this convex hull are generated on.
     */
    val segmentation: Segmentation = points.first().segment.segmentation

    /**
     * Map the [Point.id] to each x,y coordinate result from the [planeProjection] for faster lookup using
     * their id.
     */
    private val projectionById = points.associateBy { it.id }.map {
        it.key to planeProjection.xy(it.value)
    }.distinctBy {
        it.second.joinToString("|")
        // Remove conversion results that turned out to be duplicates.
    }.toMap()

    /**
     * Map the [Point.id] to each [Point] for faster lookup of points using their id.
     * If [projectionById] removed duplicated conversion results, these points are also removed here.
     */
    private val pointsById = points.filter { it.id in projectionById }.associateBy { it.id }

    /**
     * The vertices that form the polytope that is the convex hull of the given set of points.
     */
    val hullPoints = mutableListOf<Point>()

    /**
     * Use the Graham scan algorithm in the 2-dimensional plane to construct the convex hull from [projectionById].
     * See [Graham scan - Wikipedia](https://en.wikipedia.org/wiki/Graham_scan) for more details on the algorithm.
     */
    fun generate() {
        if (checkTrivialCase()) {
            return
        }

        /**
         * Sort the (x,y) points such that the points are ordered by their y-coordinate in ascending order.
         * Ties are broken using the x-coordinate in ascending order as second sorting order.
         */
        val sortedIds = orderByYXCoordinates(projectionById.toList())

        if (checkForCollinear(projectionById.values.toList())) {
            /*
             * Since all points are yx-sorted and collinear, the first one is the bottom-left-most and the last is the
             * top-right-most point. All other points lay on the straight line between these two points.
             */
            hullPoints.clear()
            hullPoints.addAll(listOf(sortedIds.first(), sortedIds.last()).map { pointsById[it]!! })

            log.atInfo()
                .setMessage("All points are collinear. Convex Hull is given by only two points.")
                .addKeyValue("id1") { sortedIds.first() }
                .addKeyValue("xy1") { projectionById[sortedIds.first()]!!.joinToString(separator = " ") }
                .addKeyValue("id2") { sortedIds.last() }
                .addKeyValue("xy2") { projectionById[sortedIds.last()]!!.joinToString(separator = " ") }
                .log()

            return
        } else {
            log.atInfo()
                .setMessage("Points are not all collinear. Using Graham scan algorithm.")
                .log()
        }

        /**
         * For type convenience during the algorithm below, we use this list to track the hull construction using the
         * ids of the points.
         */
        val hullIds = mutableListOf<Int>()

        /**
         * Choose the point with the lowest y-coordinate (and x-coordinate if a tie-breaker is needed) as the first
         * point of the convex hull. This point is guaranteed to be a hull point due to the ordering done before on
         * [sorted].
         */
        val basePoint = sortedIds[0].let { id ->
            hullIds.add(id)

            projectionById[id]!!.also { xy ->
                log.atDebug()
                    .setMessage("Chosen basePoint for convex hull.")
                    .addKeyValue("id") { id }
                    .addKeyValue("xy") { xy.joinToString(separator = " ") }
                    .log()
            }
        }

        /**
         * Sort the remaining points (we drop the already chosen base point) by the angle they form with the line
         * that goes through the basePoint and is parallel to the x-axis (horizontal). The angle is calculated in
         * counter-clockwise direction. Break ties by using the Euclidean distance to the basePoint.
         */
        val orderedIds = orderByAngleAndLength(
            sortedIds.drop(1) // first element is the basePoint and already part of the hull, we drop it
                .map { it to (projectionById[it]!! - basePoint) } // sorting needs to be relative to basePoint
        )

        val tmpPoint = projectionById[orderedIds[0]]!!.also {
            log.atDebug()
                .setMessage("Initialized hull search selecting tmpPoint from polar order.")
                .addKeyValue("id") { orderedIds[0] }
                .addKeyValue("xy") { it.joinToString(separator = " ") }
                .log()
        }

        val startIndex = orderedIds.drop(1) // we ignore the tmpPoint
            .indexOfFirst {
                GeometryCalculations.turnDirection(
                    basePoint,
                    tmpPoint,
                    projectionById[it]!!
                ) != GeometryCalculations.TurnDirection.COLLINEAR
                // find the first index that is not collinear with basePoint and tmpPoint
            }.also {
                log.atDebug()
                    .setMessage("Found starting index as first point not collinear to basePoint and tmpPoint.")
                    .addKeyValue("index") { it }
                    .addKeyValue("point") { orderedIds[it] }
                    .addKeyValue("xy") { projectionById[orderedIds[it]]!!.joinToString(separator = " ") }
                    .log()
            }

        hullIds.add(orderedIds[startIndex])
        // add this starting point to the hull. We skip collinear points between basePoint and this selected startPoint

        for (i in startIndex..orderedIds.lastIndex) {
            // using Grahams scan algorithm based on the ordered ids

            log.atDebug()
                .setMessage("Running graham Scan for next index.")
                .addKeyValue("index") { i }
                .addKeyValue("point") { orderedIds[i] }
                .addKeyValue("xy") { projectionById[orderedIds[i]]!!.joinToString(separator = " ") }
                .log()

            var last = hullIds.removeLast()
            // remove the last point we have chosen for the hull and store it temporarily

            /**
             * The last two points of the hull must form a counter-clockwise turn with the candidate point.
             * If not, we remove the last point from the hull and repeat the check.
             */
            while (
                hullIds.size > 1 && GeometryCalculations.turnDirection(
                    projectionById[hullIds.last()]!!,
                    projectionById[last]!!,
                    projectionById[orderedIds[i]]!!
                ) != GeometryCalculations.TurnDirection.COUNTER_CLOCKWISE
            ) {
                log.atTrace()
                    .setMessage("Backtracking in Graham scan removing point from hull.")
                    .addKeyValue("id") { last }
                    .log()
                // logging first, since the overwriting value below could be added again later

                last = hullIds.removeLast()
            }

            hullIds.add(last)
            // we do not need to throw away the last point, since we removed it before, we add it here back
            hullIds.add(orderedIds[i])
            // add the candidate point to the hull as new last point
        }

        val mod = hullIds.size.also {
            log.atDebug()
                .setMessage("Testing convex hull turn directions.")
                .addKeyValue("size") { it }
                .log()
        }

        hullIds.indices.forEach { i ->
            (i..i + 2).map {
                hullIds[it % mod] // use mod for circular list access on the i-th, (i+1)-th and (i+2)-th entry
            }.let { ids ->
                ids.map { id -> projectionById[id]!! }.let { (a, b, c) ->
                    val turn = GeometryCalculations.turnDirection(a, b, c)

                    require(turn == GeometryCalculations.TurnDirection.COUNTER_CLOCKWISE) {
                        log.atError()
                            .setMessage("Generated convex hull has invalid turn direction.")
                            .addKeyValue("turnIds") { ids.joinToString(separator = " -> ") }
                            .addKeyValue("axy") { a.joinToString(separator = " ") }
                            .addKeyValue("bxy") { b.joinToString(separator = " ") }
                            .addKeyValue("cxy") { c.joinToString(separator = " ") }
                            .addKeyValue("turnDirection") { turn }
                            .log()

                        "Hull verification failed."
                    }
                }
            }
        }

        hullPoints.clear()
        hullPoints.addAll(hullIds.map { pointsById[it]!! })

        log.atInfo()
            .setMessage("Graham scan finished calculation of convex hull.")
            .addKeyValue("size") { hullPoints.size }
            .addKeyValue("ids") { hullPoints.joinToString(separator = " ") { it.id.toString() } }
            .log()
    }

    /**
     * Checks if we construct the convex hull of 2 points or fewer. In that case, the hull must contain all given
     * points. No need to run Graham scan.
     *
     * @return true if this is a trivial case, no further computation needed. Otherwise, false.
     */
    private fun checkTrivialCase(): Boolean {
        if (projectionById.size <= 2) {
            hullPoints.addAll(projectionById.keys.map { pointsById[it]!! })

            log.atInfo()
                .setMessage("Convex hull of 2 or less points must contain all given points.")
                .addKeyValue("size") { projectionById.size }
                .log()
            return true
        }
        return false
    }

    /**
     * @return true if all given [points] are collinear, i.e. all of them lay on one straight line.
     */
    private fun checkForCollinear(points: List<DoubleArray>): Boolean {
        for (i in 2..points.lastIndex) {
            if (GeometryCalculations.turnDirection(
                    points[0],
                    points[1],
                    points[i]
                ) != GeometryCalculations.TurnDirection.COLLINEAR
            ) {
                return false
            }
        }

        return true
    }

    /**
     * Given a list of 2-dimensional vectors represented by [DoubleArray] and labeled with the given keys of type [Int],
     * this method returns a sorted list containing the ids ordered by the polar angle the vectors form with the x-axis
     * in ascending order and ties are broken using the Euclidean length of the vectors in ascending order.
     *
     * Note: The polar angle is measured in counter-clockwise direction starting from the positive half of the x-axis,
     * i.e. at (3 o'clock). See [polar coordinate system](https://en.wikipedia.org/wiki/Polar_coordinate_system) for
     * more information.
     *
     * @param toSort the pairs of ids and xy positions to sort
     * @return the ids in the order according to the angle and length ordering
     */
    private fun orderByAngleAndLength(
        toSort: List<Pair<Int, DoubleArray>>
    ): List<Int> {
        return toSort.sortedWith { o1, o2 ->
            GeometryCalculations.comparePolarAngle(o1.second, o2.second).let {
                if (it == 0) GeometryCalculations.comparePolarRadius(o1.second, o2.second) else it
            }
        }.map { it.first }.also {
            log.atDebug()
                .setMessage("Ordered points by polar angle and length.")
                .addKeyValue("ids") { it.joinToString(separator = " ") }
                .log()
        }
    }

    /**
     * Given a list of 2-dimensional vectors represented by [DoubleArray] and labeled with the given keys of type [Int],
     * this method returns a sorted list containing the ids ordered by the y-coordinate in ascending order where ties
     * are broken using the x-coordinate in ascending order.
     *
     * @param toSort the pairs of ids and xy position to sort
     * @return the ids in order according to the y-coordinate and x-coordinate ordering
     */
    private fun orderByYXCoordinates(
        toSort: List<Pair<Int, DoubleArray>>
    ): List<Int> {
        return toSort.sortedWith { o1, o2 ->
            GeometryCalculations.compareYXCoordinates(o1.second, o2.second)
        }.map { it.first }.also {
            log.atDebug()
                .setMessage("Ordered points by y-coordinate and x-coordinate.")
                .addKeyValue("ids") { it.joinToString(separator = " ") }
                .log()
        }
    }
}