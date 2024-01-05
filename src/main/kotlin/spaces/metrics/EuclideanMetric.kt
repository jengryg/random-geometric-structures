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
            x.position.indices.sumOf {
                (x.position[it] - y.position[it]) * (x.position[it] - y.position[it])
            }
        )
    }
}