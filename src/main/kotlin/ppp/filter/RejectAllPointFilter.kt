package ppp.filter

import spaces.segmentation.Point

/**
 * This filter will reject all points.
 */
class RejectAllPointFilter : PointFilter {
    /**
     * @return always returns false
     */
    override fun evaluate(point: Point): Boolean {
        return false
    }
}