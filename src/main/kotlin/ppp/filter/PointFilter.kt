package ppp.filter

import ppp.segmentation.SegmentationPoint

/**
 * Restrict the space that our points are generated on by indicating for each point if it belongs to the restricted
 * space or not.
 */
interface PointFilter {
    /**
     * Given an [SegmentationPoint], this method should indicate if the point is allowed by this filter or not.
     * Points that are not allowed are discarded in the generation process.
     *
     * @return true if the point is accepted by this filter, false if it is rejected
     */
    fun evaluate(point: SegmentationPoint): Boolean
}