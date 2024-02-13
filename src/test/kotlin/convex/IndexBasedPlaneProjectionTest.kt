package convex

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import spaces.segmentation.Point

class IndexBasedPlaneProjectionTest {
    val point = mockk<Point> {
        every { absolute } returns doubleArrayOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
    }

    @Test
    fun `check coordinates x and y`() {
        assertThat(IndexBasedPlaneProjection().x(point)).isEqualTo(0.0)
        assertThat(IndexBasedPlaneProjection().y(point)).isEqualTo(1.0)

        assertThat(IndexBasedPlaneProjection(0, 1).x(point)).isEqualTo(0.0)
        assertThat(IndexBasedPlaneProjection(0, 1).y(point)).isEqualTo(1.0)

        assertThat(IndexBasedPlaneProjection(1, 7).x(point)).isEqualTo(1.0)
        assertThat(IndexBasedPlaneProjection(1, 7).y(point)).isEqualTo(7.0)

        assertThat(IndexBasedPlaneProjection(3, 3).x(point)).isEqualTo(3.0)
        assertThat(IndexBasedPlaneProjection(3, 3).y(point)).isEqualTo(3.0)

        assertThat(IndexBasedPlaneProjection(9, 5).x(point)).isEqualTo(9.0)
        assertThat(IndexBasedPlaneProjection(9, 5).y(point)).isEqualTo(5.0)
    }

    @Test
    fun `check coordinate xy combination in DoubleArray`() {
        assertThat(IndexBasedPlaneProjection().xy(point)).containsExactly(0.0, 1.0)
        assertThat(IndexBasedPlaneProjection(0, 1).xy(point)).containsExactly(0.0, 1.0)
        assertThat(IndexBasedPlaneProjection(1, 7).xy(point)).containsExactly(1.0, 7.0)
        assertThat(IndexBasedPlaneProjection(3, 3).xy(point)).containsExactly(3.0, 3.0)
        assertThat(IndexBasedPlaneProjection(9, 5).xy(point)).containsExactly(9.0, 5.0)
    }
}