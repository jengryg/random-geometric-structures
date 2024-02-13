package sc

import graph.Graph
import spaces.segmentation.Point

/**
 * Represents the basic simplicial complex constructed over [vertices] that are represented by [Point]
 * and the [simplicies] represented by [Simplex]. Note: The complex will initialize the [simplexCollection] for given
 * dimension `0` with one [Simplex] for each vertex given in [vertices].
 *
 * Geometric aspects of the underlying space are not considered in the base implementation.
 *
 * @param vertices the list of vertices of this simplicial complex
 */
open class SimplicialComplex(
    vertices: List<Point>
) : Graph(
    vertices.also { require(vertices.isNotEmpty()) { "SimplicialComplex must have at least one vertex!" } }
) {
    /**
     * The simplicies that are in this simplicial complex.
     * By default, each vertex of the complex is a `0`-dimensional simplex on its on.
     */
    val simplicies: MutableMap<Int, MutableList<Simplex>> =
        mutableMapOf<Int, MutableList<Simplex>>().also { sc ->
            sc[0] = mutableListOf<Simplex>().also { zeroDims ->
                vertices.forEachIndexed { index, point ->
                    zeroDims.add(Simplex(id = index, vertices = listOf(point)))
                }
            }
        }

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
     * Access the [simplicies] map and get the list of all [Simplex] with the given [dimension].
     * If the given [dimension] is not initialized in [simplicies], this method will initialize it as new [MutableList],
     * store it in the [simplicies] map and return the freshly generated list for further usage.
     */
    fun simplexCollection(dimension: Int): MutableList<Simplex> {
        return simplicies[dimension] ?: mutableListOf<Simplex>().also {
            simplicies[dimension] = it
        }
    }

    /**
     * Adds the given [simplex] to this [SimplicialComplex].
     *
     * Note: This method is only a shortcut for [simplexCollection] called with the given simplexes dimension. It will
     * not ensure the mathematical requirements of adding also all lower dimensional simplicies of [simplex].
     */
    fun addSimplex(simplex: Simplex) {
        simplexCollection(simplex.simplexDimension).add(simplex)
    }

    /**
     * Use the edges given in [simplicies] as `1`-dimensional [Simplex] to create the adjacency matrix of this
     * simplicial complex.
     */
    open fun calculateAdjacencyMatrix() {
        adjacencyMatrix.reset()

        // iterate over the 1 dimensional simplicies and store each edge in the adjacency matrix
        simplicies[1]?.forEach { edge ->
            adjacencyMatrix.connect(edge.vertices[0], edge.vertices[1])
        }
    }
}