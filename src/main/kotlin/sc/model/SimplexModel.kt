package sc.model

import ppp.segmentation.Cluster
import ppp.segmentation.SegmentationPoint

/**
 * The simplex is the convex hull of the given set of [vertices].
 *
 * @param id a unique integer identifying this point (sequential enumeration should be used)
 *
 * @param vertices the vertices that form this simplex
 */
class SimplexModel(
    val id: Int,
    val vertices: List<SegmentationPoint>
) {
    init {
        assert(vertices.isNotEmpty())
        // We can simplify a lot of the implementations in this class if we do not allow this simplex to be empty.
    }

    /**
     * The dimension of a simplex is (number of vertices - 1).
     * This has nothing to do with the dimension of the space the vertices of the simplex are located in.
     */
    val simplexDimension = vertices.size - 1

    /**
     * The dimension of the underlying space is given by the dimension of the vertices.
     */
    val spaceDimension = vertices.first().space.dimension

    /**
     * The smallest [SegmentationPoint.id] in [vertices].
     */
    val lowestVertexId = vertices.minOf { it.id }

    /**
     * Map [SegmentationPoint.id] to [SegmentationPoint] for all [vertices] in this simplex.
     */
    val vertexIds = vertices.associateBy { it.id }

    /**
     * Use the pre-build [vertexIds] map to check if the given vertex [p] is a vertex of this simplex.
     */
    fun containsVertex(p: SegmentationPoint): Boolean {
        return vertexIds.containsKey(p.id)
    }

    /**
     * The bounding box is the smallest box that contains this complete simplex.
     * We calculate it by using the minimum and maximum coordinates of the [vertices] in each coordinate dimension.
     */
    val boundingBox = BoxModel(
        (0 until spaceDimension).map { coordinateIndex ->
            (vertices.minOf { p -> p.absolute[coordinateIndex] }..vertices.maxOf { p -> p.absolute[coordinateIndex] })
        }
    )

    /**
     * The simplex is contained in the given [cluster] if and only if its [boundingBox] is contained in the cluster.
     */
    fun containedIn(cluster: Cluster): Boolean {
        return boundingBox.containedIn(cluster)
    }
}