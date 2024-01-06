package ppp.filter

import ppp.segmentation.SegmentationPoint

/**
 * This filter will accept all points.
 */
class AllowAllPointFilter : PointFilter {
    /**
     * @return always returns true
     */
    override fun evaluate(point: SegmentationPoint): Boolean {
        return true
    }

}