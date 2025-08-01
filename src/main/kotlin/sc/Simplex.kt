package sc

import spaces.segmentation.Box
import spaces.segmentation.Cluster
import spaces.segmentation.Point

class Simplex(
    val id: Int,
    val vertices: List<Point>
) {
    init {
        require(vertices.isNotEmpty()) { "Simplex must have at least one vertex!" }
        // We can simplify a lot of the implementations in this class if we prevent the instantiation of empty simplex.
    }

    /**
     * The identifier of the simplex is the string generated using the ids of the [Point] it has as [vertices] in
     * ascending order joined with separator `,`.
     */
    val identifier = vertices.map { it.id }.sorted().joinToString(separator = ",")

    /**
     * The dimension of a simplex is (number of vertices - 1).
     * This has nothing to do with the dimension of the space the vertices of the simplex are located in.
     */
    val simplexDimension = vertices.size - 1

    /**
     * The dimension of the underlying space is given by the dimension of the vertices.
     */
    val spaceDimension = vertices.first().dimension

    /**
     * The smallest [Point.id] in [vertices].
     */
    val lowestVertexId = vertices.minOf { it.id }

    /**
     * Map [Point.id] to [Point] for all [vertices] in this simplex.
     */
    val vertexIds = vertices.associateBy { it.id }

    /**
     * Use the pre-build [vertexIds] map to check if the given vertex [p] is a vertex of this simplex.
     */
    fun containsVertex(p: Point): Boolean {
        return vertexIds.containsKey(p.id)
    }

    /**
     * The bounding box is the smallest box that contains this complete simplex.
     * We calculate it by using the minimum and maximum coordinates of the [vertices] in each coordinate dimension.
     */
    val boundingBox = Box(
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