package sc

import sc.aspects.Connectivity
import sc.model.AdjacencyMatrix
import sc.model.SimplexModel
import spaces.Space
import spaces.segmentation.Segmentation
import spaces.segmentation.SegmentationPoint

/**
 * A [GraphAbstract] is a simple base class used to ensure that some basic properties are available.
 *
 * @param vertices the list of vertices of this graph.
 */
abstract class GraphAbstract(
    val vertices: List<SegmentationPoint>
) : Space by vertices.first().space {
    /**
     * The [Segmentation] the [vertices] of this graph are generated on.
     */
    val segmentation: Segmentation = vertices.first().segment.segmentation

    /**
     * Map the [SegmentationPoint.id] to each [SegmentationPoint] for faster lookup of points using their id.
     */
    val pointsById = vertices.associateBy { it.id }

    /**
     * Map the [SegmentationPoint.segment] to each [SegmentationPoint] for fast lookup.
     * Pre-sort the [SegmentationPoint] list along the [SegmentationPoint.id].
     */
    val pointsBySegment =
        vertices.groupBy { it.segment }.map { (key, value) -> key to value.sortedBy { it.id } }.toMap()

    /**
     * The adjacency Matrix of the vertices of this [GraphAbstract].
     * For each pair of vertices, the [AdjacencyMatrix] contains a [Boolean] that indicates if these two vertices
     * are considered adjacent or not. This [Boolean] is invariant in the order of the vertices in the pair, since
     * we consider only undirected graphs here.
     */
    val adjacencyMatrix = AdjacencyMatrix(vertices.map { it.id }.toSet())

    /**
     * The [Connectivity] model of this [SimplexModel].
     */
    val connectivity: Lazy<Connectivity> = lazy { Connectivity(this) }
}