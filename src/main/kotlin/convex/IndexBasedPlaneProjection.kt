package convex

import spaces.segmentation.Point

/**
 * Simple implementation for the conversion of a [Point] to the 2-dimensional x,y plane.
 * The x-coordinate in the plane is the [xIndex]-coordinate of the [Point].
 * The y-coordinate in the plane is the [yIndex]-coordinate of the [Point].
 *
 * @param xIndex the coordinate index to use as x-coordinate
 * @param yIndex the coordinate index to use as y-coordinate
 */
class IndexBasedPlaneProjection(
    private val xIndex: Int = 0,
    private val yIndex: Int = 1,
) : PlaneProjection() {
    override fun x(point: Point): Double {
        return point.absolute[xIndex]
    }

    override fun y(point: Point): Double {
        return point.absolute[yIndex]
    }
}