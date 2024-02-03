package spaces.metrics

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import spaces.spaces.PointAbstract
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EuclideanMetricTest {

    private val metric = EuclideanMetric()

    private val pointX = mockk<PointAbstract<*>>()
    private val pointY = mockk<PointAbstract<*>>()

    /**
     * Euclidean metric includes [kotlin.math.sqrt] usage to calculate the square root with [Double], thus we allow
     * for a tiny error here do to floating point limitations.
     */
    private val errThreshold = within(0.000000001)

    @ParameterizedTest
    @MethodSource("provideDataForDistanceTest")
    fun `test euclidean distance calculation`(x: DoubleArray, y: DoubleArray, distance: Double) {
        every { pointX.absolute } returns x
        every { pointY.absolute } returns y

        assertThat(metric.distance(pointX, pointY)).isEqualTo(distance, errThreshold)
        assertThat(metric.distance(pointY, pointX)).isEqualTo(distance, errThreshold)
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
                doubleArrayOf(0.0, 0.0), doubleArrayOf(3.0, 4.0), 5.0
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123), doubleArrayOf(3.457, -1.4156, 0.454), 8.506734297
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("provideDataForLengthTest")
    fun `test euclidean length calculation`(point: DoubleArray, length: Double) {
        every { pointX.absolute } returns point

        assertThat(metric.length(pointX)).isEqualTo(length, errThreshold)
    }

    private fun provideDataForLengthTest(): Stream<Arguments> {
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
                doubleArrayOf(3.0, 4.0), 5.0
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123), 6.901423059
            ),
            Arguments.of(
                doubleArrayOf(3.457, -1.4156, 0.454), 3.763095582
            )
        )
    }
}