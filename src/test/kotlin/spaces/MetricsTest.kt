package spaces

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MetricsTest {
    private val errThreshold = within(0.00000005)

    @ParameterizedTest
    @MethodSource("provideUniformMetricTestData")
    fun `check uniform metric calculation`(x: DoubleArray, y: DoubleArray, d: Double) {
        assertThat(
            Metrics.uniform(
                mockk { every { absolute } returns x },
                mockk { every { absolute } returns y },
            )
        ).isEqualTo(d, errThreshold)
    }

    private fun provideUniformMetricTestData(): Stream<Arguments> {
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
    @MethodSource("provideEuclideanMetricTestData")
    fun `check Euclidean metric calculation`(x: DoubleArray, y: DoubleArray, d: Double) {
        assertThat(
            Metrics.euclidean(
                mockk { every { absolute } returns x },
                mockk { every { absolute } returns y },
            )
        ).isEqualTo(d, errThreshold)
    }

    private fun provideEuclideanMetricTestData(): Stream<Arguments> {
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
    @MethodSource("provideSquaredEuclideanMetricTestData")
    fun `check squared Euclidean metric calculation`(x: DoubleArray, y: DoubleArray, d: Double) {
        assertThat(
            Metrics.squaredEuclidean(
                mockk { every { absolute } returns x },
                mockk { every { absolute } returns y },
            )
        ).isEqualTo(d, errThreshold)
    }

    private fun provideSquaredEuclideanMetricTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                doubleArrayOf(), doubleArrayOf(), 0.0
            ),
            Arguments.of(
                doubleArrayOf(1.0), doubleArrayOf(-1.5), 6.25
            ),
            Arguments.of(
                doubleArrayOf(0.0, 0.0), doubleArrayOf(3.0, 4.0), 25.0
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123), doubleArrayOf(3.457, -1.4156, 0.454), 72.36452839975608
            ),
        )
    }
}