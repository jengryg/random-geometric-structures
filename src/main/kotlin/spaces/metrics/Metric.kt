package spaces.metrics

import spaces.spaces.PointAbstract

/**
 * Define a metric that operates on the underlying space that is used to measure the distance between two points.
 */
interface Metric {
    /**
     * Given two points [x] and [y], this method should return the corresponding distance calculated according to the
     * implement metric.
     */
    fun distance(x: PointAbstract<*>, y: PointAbstract<*>): Double
}