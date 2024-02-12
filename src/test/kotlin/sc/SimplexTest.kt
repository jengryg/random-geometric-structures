package sc

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import spaces.segmentation.Cluster
import spaces.segmentation.Point
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimplexTest {
    private val vertices = listOf<Point>(
        mockk {
            every { dimension } returns 3
            every { id } returns 17
            every { absolute } returns doubleArrayOf(0.0, 0.0, 1.0)
        },
        mockk {
            every { dimension } returns 3
            every { id } returns 18
            every { absolute } returns doubleArrayOf(-1.0, 4.0, 5.0)
        },
        mockk {
            every { dimension } returns 3
            every { id } returns 21
            every { absolute } returns doubleArrayOf(2.0, 0.0, 4.0)
        },
        mockk {
            every { dimension } returns 3
            every { id } returns 23
            every { absolute } returns doubleArrayOf(3.0, 2.0, 3.0)
        },
        mockk {
            every { dimension } returns 3
            every { id } returns 27
            every { absolute } returns doubleArrayOf(1.0, 1.0, 2.0)
        },
        mockk {
            every { dimension } returns 3
            every { id } returns 29
            every { absolute } returns doubleArrayOf(0.0, 1.0, 3.0)
        },
    )

    private val simplexModel = Simplex(
        id = 0,
        vertices = vertices
    )

    @Test
    fun `simplex model should require at least one vertex`() {
        assertThatThrownBy {
            Simplex(
                id = 0,
                vertices = listOf()
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("Simplex must have at least one vertex!")
    }

    @Test
    fun `check basic functionality`() {
        assertThat(simplexModel.identifier).isEqualTo("17,18,21,23,27,29")
        assertThat(simplexModel.simplexDimension).isEqualTo(vertices.size - 1)
        assertThat(simplexModel.spaceDimension).isEqualTo(3)
        assertThat(simplexModel.lowestVertexId).isEqualTo(17)

        assertThat(simplexModel.containsVertex(mockk { every { id } returns 17 })).isTrue
        assertThat(simplexModel.containsVertex(mockk { every { id } returns 35 })).isFalse

        simplexModel.boundingBox.intervals.let {
            assertThat(it[0].start).isEqualTo(-1.0)
            assertThat(it[1].start).isEqualTo(0.0)
            assertThat(it[2].start).isEqualTo(1.0)

            assertThat(it[0].endInclusive).isEqualTo(3.0)
            assertThat(it[1].endInclusive).isEqualTo(4.0)
            assertThat(it[2].endInclusive).isEqualTo(5.0)
        }
    }

    @ParameterizedTest
    @MethodSource("provideDifferentClusterBounds")
    fun `evaluate containment in cluster`(
        cLowerBound: IntArray,
        cUpperBound: IntArray,
        expected: Boolean
    ) {
        val mockCluster = mockk<Cluster> {
            every { dimension } returns 3
            every { lowerBound } returns cLowerBound
            every { upperBound } returns cUpperBound
        }

        val simplexModel = Simplex(
            id = 0,
            vertices = vertices
        )

        assertThat(simplexModel.containedIn(mockCluster)).isEqualTo(expected)
    }

    private fun provideDifferentClusterBounds(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                intArrayOf(0, 0, 0), intArrayOf(0, 0, 0), false
            ),
            Arguments.of(
                intArrayOf(-50, -50, -50), intArrayOf(50, 50, 50), true
            ),
            Arguments.of(
                intArrayOf(-2, -3, -4), intArrayOf(2, 4, 5), false
            ),
            Arguments.of(
                intArrayOf(-1, -3, -4), intArrayOf(2, 3, 4), false
            ),
            Arguments.of(
                intArrayOf(-2, -2, -4), intArrayOf(2, 3, 4), false
            ),
            Arguments.of(
                intArrayOf(-2, -3, -3), intArrayOf(2, 3, 4), false
            ),
            Arguments.of(
                intArrayOf(-2, -3, -4), intArrayOf(1, 3, 4), false
            ),
            Arguments.of(
                intArrayOf(-2, -3, -4), intArrayOf(2, 2, 4), false
            ),
            Arguments.of(
                intArrayOf(-2, -3, -4), intArrayOf(2, 3, 3), false
            ),
            Arguments.of(
                intArrayOf(5, 5, 5), intArrayOf(10, 10, 10), false
            ),
        )
    }
}