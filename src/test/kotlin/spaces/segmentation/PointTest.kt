package spaces.segmentation

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PointTest {
    @ParameterizedTest
    @MethodSource("provideAbsolutePositionTestData")
    fun `check correct absolute position calculation`(
        uniform: DoubleArray,
        basePosition: IntArray,
        expected: DoubleArray
    ) {
        Point(
            id = 17,
            uniform = uniform,
            segment = Segment(
                segmentation = mockk {
                    every { dimension } returns uniform.size
                },
                basePosition = basePosition
            )
        ).let {
            assertThat(it.id).isEqualTo(17)
            assertThat(it.dimension).isEqualTo(uniform.size)
            assertThat(it.segment.basePosition).containsExactly(basePosition.toTypedArray())

            assertThat(it.uniform).containsExactly(uniform.toTypedArray())
            assertThat(it.absolute).containsExactly(expected.toTypedArray())
        }
    }

    private fun provideAbsolutePositionTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                doubleArrayOf(), intArrayOf(), doubleArrayOf()
            ),
            Arguments.of(
                doubleArrayOf(1.0), intArrayOf(-2), doubleArrayOf(-1.0)
            ),
            Arguments.of(
                doubleArrayOf(0.0, 0.0), intArrayOf(3, 4), doubleArrayOf(3.0, 4.0)
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123), intArrayOf(3, -1, 0), doubleArrayOf(4.2345, 5.789, 0.123)
            ),
        )
    }
}