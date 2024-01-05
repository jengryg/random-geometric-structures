package spaces.metrics

import spaces.spaces.PointAbstract
import kotlin.math.sqrt

/**
 * Use the Euclidean metric calculates as the square root of the sum of the squared component wise differences.
 */
class EuclideanMetric : Metric {
    /**
     * @return the Euclidean distance from [x] to [y].
     */
    override fun distance(x: PointAbstract<*>, y: PointAbstract<*>): Double {
        return sqrt(
            x.absolute.indices.sumOf {
                (x.absolute[it] - y.absolute[it]) * (x.absolute[it] - y.absolute[it])
            }
        )
    }
}