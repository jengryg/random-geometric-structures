package graph

import Logging
import logger
import spaces.segmentation.Point

/**
 * The [Connectivity] represents the analysis of the connected components in the [Graph].
 *
 * A connected component is the [GraphComponent] containing all vertices that are directly or indirectly connected
 * with each other according to the [Graph.adjacencyMatrix].
 */
class Connectivity(
    private val vertices: List<Point>,
    private val adjacencyMatrix: AdjacencyMatrix,
    private val pointsByIds: Map<Int, Point>
) : Logging {
    private val log = logger()

    /**
     * All connected components indexed by their id.
     */
    val connectedComponents = mutableMapOf<Int, GraphComponent>()

    /**
     * If a component with the given [id] already exists in [connectedComponents] it is returned.
     * Otherwise, a new [GraphComponent] is initialized, added to the list [connectedComponents] of this simplicial
     * complexes and returned for further usage.
     */
    fun component(id: Int): GraphComponent {
        return connectedComponents[id] ?: GraphComponent(id).also {
            connectedComponents[id] = it
        }
    }

    /**
     * Use the id of the point as key to get the number of the component that contains the point.
     * If the value is null, the point is not assigned to a component.
     *
     * [calculateConnectedComponents] must be executed before this map is available.
     */
    val componentsByPoint = mutableMapOf<Int, GraphComponent?>().apply {
        vertices.forEach { set(it.id, null) }
        // initialize by assigning null to each point
    }

    /**
     * Determine the connected components of the [Graph].
     * This method requires the [Graph.adjacencyMatrix] to be known.
     *
     * This method executes a walker algorithm that will travel along the vertices of the graph aiming to determine the
     * connected components of the graph by ensuring that it visited each vertex.
     */
    fun calculateConnectedComponents() {
        if (vertices.isEmpty()) {
            return
        }

        vertices.forEach {
            componentsByPoint[it.id] = null
            // reset the assignment of all points
        }

        connectedComponents.clear()
        // reset the map of all known connected components

        var componentId = 0
        // each component gets its own id, this is the counter to keep track of them

        val visited = vertices.map { it.id }.associateWith { false }.toMutableMap()
        // Create a map from the ids of all vertices to the initialized value false that records the visiting status.
        // This map traces the state of the search algorithm travelling through the graph.

        var remaining = vertices.size
        // the number of unvisited vertices in the complex

        while (remaining > 0) {
            // Find the next available vertex that is not part of a component already.
            // The point with this id is the root of a new ComponentModel we create.
            val rootPointId = visited.keys.first {
                !(visited[it]!!)
                // only points that we have not yet visited can be the root of a new component
            }.also {
                log.atDebug()
                    .setMessage("Next root vertex for ComponentModel found")
                    .addKeyValue("rootId", it)
                    .log()
            }
            // Note: There should be keys left to be used as rootPointId as long as the remaining counter is > 0.

            val currentComponent = component(componentId++).apply {
                addPoint(pointsByIds[rootPointId]!!)
            }
            // initialize a new component with the current rootPointId as its first vertex

            var currentVertexId = rootPointId
            // initialize the currentVertexId for the next loop in the algorithm

            while (true) {
                log.atDebug()
                    .setMessage("Scanning Component")
                    .addKeyValue("componentId", currentComponent.id)
                    .addKeyValue("currentNodeId", currentVertexId)
                    .log()

                visited[currentVertexId] = true
                // mark the current vertex as visited
                remaining--
                // reduce the counter of unvisited vertices to track the overall progress
                componentsByPoint[currentVertexId] = currentComponent
                // keep track of the assignments of points to components

                adjacencyMatrix.connections(currentVertexId).filter {
                    componentsByPoint[it] == null
                    // we only care about points that are not already assigned to a component
                }.forEach {
                    componentsByPoint[it] = currentComponent
                    currentComponent.addPoint(pointsByIds[it]!!)
                    // assign the point to the component

                    log.atTrace()
                        .setMessage("Connected Component assigned")
                        .addKeyValue("componentId", currentComponent.id)
                        .addKeyValue("vertexId", it)
                        .log()
                }

                val nextVertex = currentComponent.vertices.firstOrNull {
                    !(visited[it.id]!!)
                }

                if (nextVertex == null) {
                    // There is no unvisited vertex in the current component left.
                    log.atDebug()
                        .setMessage("Finished Connected Component")
                        .addKeyValue("componentId", currentComponent.id)
                        .addKeyValue("componentSize", currentComponent.size)
                        .addKeyValue("Remaining vertices", remaining)
                        .log()

                    break
                }

                currentVertexId = nextVertex.id
                // if there is a nextVertex, we need to visit it in the next iteration of the inner loop
            }
        }
    }
}