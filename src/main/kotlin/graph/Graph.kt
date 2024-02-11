package graph

import spaces.Space
import spaces.segmentation.Point

open class Graph(
    val vertices: List<Point>
) : Space by vertices.firstOrNull() ?: throw IllegalArgumentException("Graph must have at least one vertex!") {

    /**
     * Map the [Point.id] to each [Point] for faster lookup of points using their id.
     */
    val pointsById = vertices.associateBy { it.id }

    /**
     * Map the [Point.segment] to each [Point] for fast lookup.
     * Pre-sort the [Point] list along the [Point.id].
     */
    val pointsBySegment =
        vertices.groupBy { it.segment }.map { (key, value) -> key to value.sortedBy { it.id } }.toMap()

    /**
     * The adjacency Matrix of the vertices of this [Graph].
     * For each pair of vertices, the [AdjacencyMatrix] contains a [Boolean] that indicates if these two vertices
     * are considered adjacent or not. This [Boolean] is invariant in the order of the vertices in the pair, since
     * we consider only undirected graphs here.
     */
    val adjacencyMatrix = AdjacencyMatrix(vertices.map { it.id }.toSet())

    /**
     * The [Connectivity] model of this [Graph].
     */
    val connectivity: Connectivity = Connectivity(
        vertices = vertices,
        adjacencyMatrix = adjacencyMatrix,
        pointsByIds = pointsById
    )
}