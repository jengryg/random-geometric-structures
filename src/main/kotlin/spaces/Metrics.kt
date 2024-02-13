package spaces

import spaces.segmentation.Point
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Implementations of metrics to measure the distance from one [Point] to another.
 */
object Metrics {
    /**
     * The Euclidean distance from [x] to [y].
     */
    fun euclidean(x: Point, y: Point): Double {
        return sqrt(squaredEuclidean(x, y))
    }

    /**
     * The squared Euclidean distance from [x] to [y].
     * Note: This calculation avoids the [sqrt].
     */
    fun squaredEuclidean(x: Point, y: Point): Double {
        return (x.absolute - y.absolute).sumOf { it * it }
    }

    /**
     * The uniform distance from [x] to [y].
     */
    fun uniform(x: Point, y: Point): Double {
        return (x.absolute - y.absolute).maxOfOrNull { abs(it) } ?: 0.0
    }
}