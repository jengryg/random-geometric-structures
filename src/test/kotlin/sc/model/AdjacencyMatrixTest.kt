package sc.model

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import spaces.segmentation.SegmentationPoint

class AdjacencyMatrixTest {
    /**
     * A non-consecutive set of indexes, since this is a core functionality we want from our implementation.
     */
    private val indexes = setOf(1, 2, 3, 5, 8, 13, 21, 34)

    private val mockedPoints = indexes.map {
        mockk<SegmentationPoint> {
            every { id } returns it
        }
    }

    @Test
    fun `connect everything and then reset using segmentation points`() {
        val adjacencyMatrix = AdjacencyMatrix(
            indexes = indexes
        )

        mockedPoints.forEach { i ->
            mockedPoints.forEach { j ->
                adjacencyMatrix.connect(i, j)
            }
        }

        mockedPoints.forEach { i ->
            mockedPoints.forEach { j ->
                assertThat(
                    adjacencyMatrix.get(i, j)
                ).isTrue
            }
        }

        assertThat(adjacencyMatrix.count()).isEqualTo(28)

        adjacencyMatrix.disconnect(1, 1)
        assertThat(adjacencyMatrix.get(1, 1)).isFalse

        adjacencyMatrix.disconnect(
            mockk<SegmentationPoint> { every { id } returns 8 },
            mockk<SegmentationPoint> { every { id } returns 5 }
        )
        assertThat(adjacencyMatrix.get(5, 8)).isFalse

        adjacencyMatrix.reset()

        mockedPoints.forEach { i ->
            mockedPoints.forEach { j ->
                assertThat(
                    adjacencyMatrix.get(i, j)
                ).isFalse
            }
        }

        assertThat(adjacencyMatrix.count()).isEqualTo(0)
    }

    @Test
    fun `establish some connections and get connected indices`() {
        val adjacencyMatrix = AdjacencyMatrix(
            indexes = indexes
        )

        indexes.forEach { i ->
            adjacencyMatrix.connect(i, 8)
            // connect everything to index 8
        }

        assertThat(adjacencyMatrix.connections(8)).containsExactly(1, 2, 3, 5, 8, 13, 21, 34)

        assertThat(adjacencyMatrix.connections(1)).containsExactly(8)
        assertThat(adjacencyMatrix.connections(2)).containsExactly(8)
        assertThat(adjacencyMatrix.connections(3)).containsExactly(8)
        assertThat(adjacencyMatrix.connections(13)).containsExactly(8)
        assertThat(adjacencyMatrix.connections(21)).containsExactly(8)
        assertThat(adjacencyMatrix.connections(34)).containsExactly(8)
    }

    @Test
    fun `set some connections and validate them`() {
        val adjacencyMatrix = AdjacencyMatrix(
            indexes = indexes
        )

        adjacencyMatrix.set(1, 2, true)
        assertThat(adjacencyMatrix.get(1, 2)).isTrue

        adjacencyMatrix.set(
            mockk<SegmentationPoint> {
                every { id } returns 1
            },
            mockk<SegmentationPoint> {
                every { id } returns 2
            },
            false
        )

        assertThat(adjacencyMatrix.get(1, 2)).isFalse
    }

    @Test
    fun `check the all connected method`() {
        val adjacencyMatrix = AdjacencyMatrix(
            indexes = indexes
        )

        adjacencyMatrix.connect(1, 2)
        adjacencyMatrix.connect(1, 3)
        adjacencyMatrix.connect(1, 5)

        val m1 = mockk<SegmentationPoint> {
            every { id } returns 1
        }
        val m2 = mockk<SegmentationPoint> {
            every { id } returns 2
        }
        val m3 = mockk<SegmentationPoint> {
            every { id } returns 3
        }
        val m5 = mockk<SegmentationPoint> {
            every { id } returns 5
        }
        val m8 = mockk<SegmentationPoint> {
            every { id } returns 8
        }

        assertThat(adjacencyMatrix.all(m1, listOf(m2, m3, m5))).isTrue
        assertThat(adjacencyMatrix.all(m1, listOf(m2, m3, m5, m8))).isFalse
    }

    @Test
    fun `retrieve unknown index should be always disconnected`() {
        val adjacencyMatrix = AdjacencyMatrix(
            indexes = setOf(1, 2)
        )
        assertThatCode {
            assertThat(adjacencyMatrix.get(1, 3)).isFalse
        }.doesNotThrowAnyException()

        assertThatCode {
            assertThat(adjacencyMatrix.connections(0)).isEmpty()
        }
    }

    @Test
    fun `setting unknown index should not throw exceptions`() {
        val adjacencyMatrix = AdjacencyMatrix(
            indexes = setOf()
        )


        assertThatCode {
            adjacencyMatrix.set(0, 0, true)
            adjacencyMatrix.connect(0, 0)
            adjacencyMatrix.disconnect(0, 0)
        }.doesNotThrowAnyException()
    }
}