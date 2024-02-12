package spaces.segmentation

/**
 * Represents a box that is defined as the cross product of the given real number intervals represented by [ClosedRange]
 * in [intervals]. The [ClosedRange] instances use [Double] as underlying type.
 */
class Box(
    val intervals: List<ClosedRange<Double>>
) {
    /**
     * The [Box] is contained in a cluster if and only if this method returns true.
     */
    fun containedIn(cluster: Cluster): Boolean {
        if (cluster.dimension != intervals.size) {
            return false
        }

        intervals.forEachIndexed { index, intervalDouble ->
            if (intervalDouble.start < cluster.lowerBound[index]) {
                return false
            }
            if (intervalDouble.endInclusive >= cluster.upperBound[index]) {
                return false
            }
        }

        return true
    }
}