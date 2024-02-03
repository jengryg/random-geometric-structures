package ppp.filter

import ppp.segmentation.SegmentationPoint

/**
 * This filter will reject all points.
 */
class RejectAllPointFilter : PointFilter {
    /**
     * @return always returns false
     */
    override fun evaluate(point: SegmentationPoint): Boolean {
        return false
    }
}