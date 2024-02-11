package graph

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import spaces.segmentation.Point
import spaces.segmentation.Segmentation

class GraphTest {
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

    @Test
    fun `prevent construction of empty graph`() {
        assertThatThrownBy {
            Graph(vertices = listOf())
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Graph must have at least one vertex!")
    }

    @Test
    fun `check point maps`() {
        val graph = Graph(vertices)

        assertThat(graph.dimension).isEqualTo(2)
        assertThat(graph.pointsById).hasSize(9)
        assertThat(graph.pointsBySegment.keys.map { it.basePosition.contentToString() }).containsExactlyInAnyOrder(
            "[-1, -1]", "[-1, 0]", "[0, -1]", "[0, 0]"
        )
    }

    @Test
    fun `adjacency is initialized`() {
        val graph = Graph(vertices)

        assertThat(graph.adjacencyMatrix.count()).isEqualTo(0)
    }

    @Test
    fun `connectivity is initialized`() {
        val graph = Graph(vertices)

        assertThat(graph.connectivity.connectedComponents).hasSize(0)
    }
}