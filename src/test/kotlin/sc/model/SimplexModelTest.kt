package sc.model

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import spaces.segmentation.SegmentationPoint

class SimplexModelTest {
    private val vertices = listOf<SegmentationPoint>(
        mockk {
            every { space } returns mockk { every { dimension } returns 3 }
            every { id } returns 17
            every { absolute } returns doubleArrayOf(0.0, 0.0, 1.0)
        },
        mockk {
            every { space } returns mockk { every { dimension } returns 3 }
            every { id } returns 18
            every { absolute } returns doubleArrayOf(-1.0, 4.0, 5.0)
        },
        mockk {
            every { space } returns mockk { every { dimension } returns 3 }
            every { id } returns 21
            every { absolute } returns doubleArrayOf(2.0, 0.0, 4.0)
        },
        mockk {
            every { space } returns mockk { every { dimension } returns 3 }
            every { id } returns 23
            every { absolute } returns doubleArrayOf(3.0, 2.0, 3.0)
        },
        mockk {
            every { space } returns mockk { every { dimension } returns 3 }
            every { id } returns 27
            every { absolute } returns doubleArrayOf(1.0, 1.0, 2.0)
        },
        mockk {
            every { space } returns mockk { every { dimension } returns 3 }
            every { id } returns 29
            every { absolute } returns doubleArrayOf(0.0, 1.0, 3.0)
        },
    )

    @Test
    fun `simplex model should require at least one vertex`() {
        assertThatThrownBy {
            SimplexModel(
                id = 0,
                vertices = listOf()
            )
        }.isInstanceOf(AssertionError::class.java).hasMessage("SimplexModel must have at least one vertex.")
    }

    @Test
    fun `check basic functionality`() {
        val simplexModel = SimplexModel(
            id = 0,
            vertices = vertices
        )

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
}