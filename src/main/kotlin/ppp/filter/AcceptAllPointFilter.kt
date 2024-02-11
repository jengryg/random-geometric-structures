package ppp.filter

import spaces.segmentation.Point

/**
 * This filter will accept all points.
 */
class AcceptAllPointFilter : PointFilter {
    /**
     * @return always returns true
     */
    override fun evaluate(point: Point): Boolean {
        return true
    }

}