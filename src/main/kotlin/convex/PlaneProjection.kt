package convex

import spaces.segmentation.Point

/**
 * Base class for the projection of [Point] onto a 2-dimensional x,y plane.
 * Extend this class and implement the abstract method below to define how an arbitrary dimensional [Point] should be
 * projected to a 2-dimensional point (x,y) that lies in a plane.
 *
 * Note: This is needed to use the [ConvexHull] implementations, since we only support algorithms in the plane.
 */
abstract class PlaneProjection {
    /**
     * @return the double value representing the x value of the projection of [point]
     */
    abstract fun x(point: Point): Double

    /**
     * @return the double value representing the y value of the projection of [point]
     */
    abstract fun y(point: Point): Double

    /**
     * @return the x and y coordinate of the projection as a [DoubleArray] with 2 entries
     */
    fun xy(point: Point): DoubleArray {
        return doubleArrayOf(x(point), y(point))
    }
}
