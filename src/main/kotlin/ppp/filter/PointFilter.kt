package ppp.filter

import spaces.segmentation.Point

/**
 * Restrict the space that our points are generated on by indicating for each point if it belongs to the restricted
 * space or not. The [ppp.PoissonPointProcess] generator will sort the points into accepted and rejected during
 * the generation process.
 */
interface PointFilter {
    /**
     * Given an [Point], this method should indicate if the point is accepted by this filter or not.
     *
     * @return true if the point is accepted by this filter, false if it is rejected
     */
    fun evaluate(point: Point): Boolean
}