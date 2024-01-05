package spaces.metrics

import spaces.spaces.PointAbstract
import kotlin.math.abs

/**
 * Use the uniform metric given as the maximum of the component wise calculated absolute differences.
 */
class UniformMetric : Metric {
    /**
     * @return the uniform distance from [x] to [y].
     */
    override fun distance(x: PointAbstract<*>, y: PointAbstract<*>): Double {
        return x.position.indices.maxOf {
            abs(x.position[it] - y.position[it])
        }
    }
}