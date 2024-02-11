package graph

import spaces.segmentation.Point

/**
 * Represents a group of [vertices] from a [Graph].
 *
 * @param id a unique integer identifying this component (sequential enumeration should be used)
 */
class GraphComponent(
    val id: Int,
) {
    /**
     * The vertices that are part of this [GraphComponent].
     */
    val vertices = mutableListOf<Point>()

    /**
     * The size of a component is the number of vertices in the component.
     */
    val size get() = vertices.size

    /**
     * Add the [pointModel] to the component.
     */
    fun addPoint(pointModel: Point) {
        vertices += pointModel
    }
}