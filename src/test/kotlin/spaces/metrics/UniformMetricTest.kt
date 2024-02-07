package spaces.metrics

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import spaces.spaces.PointAbstract
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UniformMetricTest {
    private val metric = UniformMetric()

    private val pointX = mockk<PointAbstract<*>>()
    private val pointY = mockk<PointAbstract<*>>()

    @ParameterizedTest
    @MethodSource("provideDataForDistanceTest")
    fun `test uniform distance calculation`(x: DoubleArray, y: DoubleArray, distance: Double) {
        every { pointX.absolute } returns x
        every { pointY.absolute } returns y

        assertThat(metric.distance(pointX, pointY)).isEqualTo(distance)
        assertThat(metric.distance(pointY, pointX)).isEqualTo(distance)
    }

    private fun provideDataForDistanceTest(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                doubleArrayOf(), doubleArrayOf(), 0.0
            ),
            Arguments.of(
                doubleArrayOf(1.0), doubleArrayOf(-1.5), 2.5
            ),
            Arguments.of(
                doubleArrayOf(0.0, 0.0), doubleArrayOf(3.0, 4.0), 4.0
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123), doubleArrayOf(3.457, -1.4156, 0.454), 8.2046
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("provideDataLengthTest")
    fun `test uniform length calculation`(point: DoubleArray, length: Double) {
        every { pointX.absolute } returns point

        assertThat(metric.length(pointX)).isEqualTo(length)
    }

    private fun provideDataLengthTest(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                doubleArrayOf(), 0.0
            ),
            Arguments.of(
                doubleArrayOf(-1.0), 1.0
            ),
            Arguments.of(
                doubleArrayOf(0.0, 0.0), 0.0
            ),
            Arguments.of(
                doubleArrayOf(3.0, 4.0), 4.0
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123), 6.789
            ),
            Arguments.of(
                doubleArrayOf(3.457, -1.4156, 0.454), 3.457
            )
        )
    }


}