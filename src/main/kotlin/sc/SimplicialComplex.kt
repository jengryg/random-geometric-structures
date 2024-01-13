package sc

import Logging
import logger
import ppp.segmentation.SegmentationPoint
import sc.aspects.Connectivity
import sc.model.AdjacencyMatrix
import sc.model.SimplexModel

/**
 * Represents the basic simplicial complex constructed over [vertices] that are represented by [SegmentationPoint]
 * and the [simplicies] represented by [SimplexModel].
 *
 * Geometric aspects of the underlying space are not considered in the base implementation.
 *
 * @param vertices the list of vertices of this simplicial complex.
 */
open class SimplicialComplex(
    vertices: List<SegmentationPoint>
) : GraphAbstract(vertices), Logging {
    private val log = logger()

    /**
     * The simplicies that are in this simplicial complex.
     */
    val simplicies: MutableMap<Int, MutableList<SimplexModel>> = mutableMapOf()

    /**
     * The `f`-Vector contains the number of simplicies of the simplicial complex of each simplex dimension.
     * The key of this map is the dimension of the simplicies and the value is the number of such simplicies of this
     * simplicial complex.
     */
    val fVector
        get() = simplicies.map {
            it.key to it.value.size
        }.toMap()

    /**
     * Access the [simplicies] map and get the list of all [SimplexModel] with the given [dimension].
     * If the given [dimension] is not initialized in [simplicies], this method will initialize it as new [MutableList],
     * store it in the [simplicies] map and return the freshly generated list for further usage.
     */
    protected fun simplexCollection(dimension: Int): MutableList<SimplexModel> {
        return simplicies[dimension] ?: mutableListOf<SimplexModel>().also {
            simplicies[dimension] = it
        }
    }

    /**
     * Use the edges given in [simplicies] as `1`-dimensional [SimplexModel] to create the [AdjacencyMatrix] of this
     * simplicial complex.
     */
    open fun calculateAdjacencyMatrix() {
        adjacencyMatrix.reset()

        // iterate over the 1 dimensional simplicies and store each edge in the adjacency matrix
        simplicies[1]?.forEach { edge ->
            adjacencyMatrix.connect(edge.vertices[0], edge.vertices[1])
        }
    }

    /**
     * The [Connectivity] model of this [SimplexModel].
     */
    val connectivity: Lazy<Connectivity> = lazy { Connectivity(this) }
}