package graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import spaces.segmentation.Point
import spaces.segmentation.Segmentation

class ConnectivityTest {
    private val segmentation = Segmentation(
        rangeLimits = arrayOf(-1..1, -1..1)
    )

    private val vertices = listOf(
        doubleArrayOf(-0.5, -0.5),
        doubleArrayOf(-0.5, 0.0),
        doubleArrayOf(-0.5, 0.5),
        doubleArrayOf(0.0, -0.5),
        doubleArrayOf(0.0, 0.0),
        doubleArrayOf(0.0, 0.5),
        doubleArrayOf(0.5, -0.5),
        doubleArrayOf(0.5, 0.0),
        doubleArrayOf(0.5, 0.5),
    ).mapIndexed { index, value ->
        segmentation.segmentOf(value)!!.let { segment ->
            Point(
                id = index,
                uniform = segment.relative(value),
                segment = segment
            )
        }
    }

    private val pointsByIds = vertices.associateBy { it.id }

    @Test
    fun `graph without connections`() {

        val connectivity = Connectivity(
            vertices = vertices,
            adjacencyMatrix = AdjacencyMatrix(pointsByIds.keys),
            pointsByIds = pointsByIds
        ).apply {
            calculateConnectedComponents()
        }

        assertThat(connectivity.connectedComponents).hasSize(9)
    }

    @Test
    fun `graph with one connection`() {
        val connectivity = Connectivity(
            vertices = vertices,
            adjacencyMatrix = AdjacencyMatrix(pointsByIds.keys).apply { connect(0, 1) },
            pointsByIds = pointsByIds
        ).apply {
            calculateConnectedComponents()
        }

        assertThat(connectivity.connectedComponents).hasSize(8)
    }

    @Test
    fun `graph with 2 components`() {
        val connectivity = Connectivity(
            vertices = vertices,
            adjacencyMatrix = AdjacencyMatrix(pointsByIds.keys).apply {
                // connect even ids
                connect(0, 2)
                connect(2, 8)
                connect(8, 4)
                connect(4, 6)
                // connect odd ids
                connect(1, 3)
                connect(3, 7)
                connect(7, 5)
            },
            pointsByIds = pointsByIds
        ).apply {
            calculateConnectedComponents()
        }

        assertThat(connectivity.connectedComponents).hasSize(2)
    }

    @Test
    fun `complete graph`() {
        val connectivity = Connectivity(
            vertices = vertices,
            adjacencyMatrix = AdjacencyMatrix(pointsByIds.keys).apply {
                (0..8).forEach { i ->
                    (i + 1..8).forEach { j ->
                        connect(i, j)
                    }
                }
            },
            pointsByIds = pointsByIds
        ).apply {
            calculateConnectedComponents()
        }

        assertThat(connectivity.connectedComponents).hasSize(1)
    }
}