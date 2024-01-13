package sc.model

import ppp.segmentation.SegmentationPoint

/**
 * Represents a group of [vertices].
 *
 * @param id a unique integer identifying this component (sequential enumeration should be used)
 */
class ComponentModel(
    val id: Int,
) {
    /**
     * The vertices that are part of this [ComponentModel].
     */
    val vertices = mutableListOf<SegmentationPoint>()

    /**
     * The size of a component is the number of vertices in the component.
     */
    val size get() = vertices.size

    /**
     * Add the [pointModel] to the component.
     */
    fun addPoint(pointModel: SegmentationPoint) {
        vertices += pointModel
    }
}