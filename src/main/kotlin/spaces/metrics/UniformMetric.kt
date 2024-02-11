package spaces.metrics

import spaces.PointAbstract
import kotlin.math.abs

/**
 * Use the uniform metric given as the maximum of the component wise calculated absolute differences.
 */
class UniformMetric : Metric {
    /**
     * @return the uniform distance from [x] to [y].
     */
    override fun distance(x: PointAbstract<*>, y: PointAbstract<*>): Double {
        return x.absolute.indices.maxOfOrNull {
            abs(x.absolute[it] - y.absolute[it])
        } ?: 0.0
    }

    /**
     * @return the uniform length of [p].
     */
    override fun length(p: PointAbstract<*>): Double {
        return p.absolute.indices.maxOfOrNull {
            abs(p.absolute[it])
        } ?: 0.0
    }
}