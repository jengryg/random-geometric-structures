package spaces.metrics

import spaces.spaces.PointAbstract

/**
 * Define a metric that operates on the underlying space that is used to measure the distance between two points.
 */
interface Metric {
    /**
     * Given two points [x] and [y], this method should return the corresponding distance calculated according to the
     * implement metric.
     *
     * @param x the first point to measure the distance from
     * @param y the second point to measure the distance to
     * @return the distance from x to y according the metric implementing this interface
     */
    fun distance(x: PointAbstract<*>, y: PointAbstract<*>): Double

    /**
     * Given a point [p], this method should return the length of the corresponding vector according to this metric,
     * i.e. this should be defined as [distance] from [p] to the origin (0, ..., 0).
     *
     * @param p the point to measure the length
     * @return the length of p, i.e. the distance from p to 0 according to the metric implementing this interface
     */
    fun length(p: PointAbstract<*>): Double
}