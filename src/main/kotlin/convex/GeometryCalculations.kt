package convex

object GeometryCalculations {
    enum class TurnDirection {
        CLOCKWISE, COUNTER_CLOCKWISE, COLLINEAR
    }

    /**
     * Assume [a], [b] and [c] are spanning a triangle in the plane, the sign of the determinant of (b-a) and (c-a)
     * indicates the direction of the turn we need to make if we come from [a] to [b] and then turn to [c], since the
     * determinant is the signed area of the oriented parallelogram constructed from a,b,c and the point b + (c-a).
     *
     * @return the direction of the turn coming from point [a] into [b] and turning to [c].
     */
    fun turnDirection(a: DoubleArray, b: DoubleArray, c: DoubleArray): TurnDirection {
        val det = (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0])

        return when {
            det < 0 -> TurnDirection.CLOCKWISE
            det > 0 -> TurnDirection.COUNTER_CLOCKWISE
            else -> TurnDirection.COLLINEAR
        }
    }

    /**
     * Given coordinates (x,y), this array contains the quadrant of the 2-dimensional coordinate system (x,y) is
     * contained in at the index calculated by `j = 2 * I{x >= 0} + I{y >= 0}`, where `I` is the indicator.
     *
     * x >= 0 && y >= 0 implies j = 3 gives quadrant 0
     * x <  0 && y >= 0 implies j = 1 gives quadrant 1
     * x <  0 && y <  0 implies j = 0 gives quadrant 2
     * x >= 0 && y <  0 implies j = 2 gives quadrant 3
     */
    private val quadrants = arrayOf(2, 1, 3, 0)

    /**
     * Assume [vec1] and [vec2] are 2-dimensional vectors, this method compares them using their polar angle.
     *
     * Note: The polar angle is measured in counter-clockwise direction starting from the positive half of the x-axis,
     * i.e. at (3 o'clock). See [polar coordinate system](https://en.wikipedia.org/wiki/Polar_coordinate_system) for
     * more information.
     *
     * Uses [quadrants] first for simple determination. If the points are in the same quadrant, it uses the determinant
     * to determine the orientation of the points that allows to compare the polar angle without explicitly calculating
     * it.
     *
     * @param vec1 a 2-dimensional vector (only the first two entries are used)
     * @param vec2 a 2-dimensional vector second vector (only the first two entries are used)
     *
     * @return -1 iff [vec1] has smaller polar angle than [vec2], +1 iff [vec1] has greater polar angle than [vec2],
     * 0 iff the polar angles are equal.
     */
    fun comparePolarAngle(vec1: DoubleArray, vec2: DoubleArray): Int {
        val q1 = vec1.map { if (it >= 0) 1 else 0 }.let { quadrants[2 * it[0] + it[1]] }
        val q2 = vec2.map { if (it >= 0) 1 else 0 }.let { quadrants[2 * it[0] + it[1]] }
        return when {
            q1 != q2 -> q1.compareTo(q2) // if the quadrants are different, the smaller quadrant number implies the smaller polar angle.
            else -> (vec1[1] * vec2[0]).compareTo(vec1[0] * vec2[1])
        }
    }

    /**
     * Assume [vec1] and [vec2] are 2-dimensional vectors, this method compares them using their polar radius.
     *
     * Note: The polar radius is measured by the 2-dimensional Euclidean length of the vectors.
     *
     * @param vec1 a 2-dimensional vector (only the first two entries are used)
     * @param vec2 a 2-dimensional vector (only the first two entries are used)
     *
     * @return -1 iff [vec1] has a smaller polar radius than [vec2], +1 iff [vec1] has greater polar radius than [vec2],
     * 0 iff the polar radius are equal.
     */
    fun comparePolarRadius(vec1: DoubleArray, vec2: DoubleArray): Int {
        return (vec1[0] * vec1[0] + vec1[1] * vec1[1]).compareTo(vec2[0] * vec2[0] + vec2[1] * vec2[1])
    }

    /**
     * Assume [vec1] and [vec2] are 2-dimensional vectors, this method compares them using their y-coordinate first and
     * their x-coordinate second.
     *
     * @param vec1 a 2-dimensional vector (only the first two entries are used)
     * @param vec2 a 2-dimensional vector (only the first two entries are used)
     *
     * @return -1 iff ([vec1] has smaller y-coordinate than [vec2] or y-coordinates are equal and [vec1] has smaller
     * x-coordinate than [vec2]), +1 iff ([vec1] has larger y-coordinate than [vec2] or y-coordinates are equal and
     * [vec2] has greater x-coordinate. 0 iff the y-coordinates and x-coordinates are equal.
     */
    fun compareYXCoordinates(vec1: DoubleArray, vec2: DoubleArray): Int {
        return vec1[1].compareTo(vec2[1]).let {
            if (it == 0) vec1[0].compareTo(vec2[0]) else it
        }
    }
}