package spaces.segmentation

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClusterTest {
    private val segmentation = Segmentation(
        arrayOf((-3..3), (-3..3), (-3..3))
    )

    private val cluster = Cluster(
        segmentation = segmentation,
        segments = segmentation.segments.values.toList()
    )

    @Test
    fun `check that cluster can not be created empty`() {
        assertThatThrownBy {
            Cluster(
                segmentation = segmentation,
                segments = listOf()
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("Cluster must contain at least one segment!")
    }

    @Test
    fun `checking the lower and upper bound`() {
        assertThat(cluster.lowerBound).containsExactly(-3, -3, -3)
        assertThat(cluster.upperBound).containsExactly(3, 3, 3)
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForContains")
    fun `validate contains method for different points`(
        point: DoubleArray,
        expected: Boolean
    ) {
        assertThat(cluster.contains(point)).isEqualTo(expected)
    }

    private fun provideTestDataForContains(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                doubleArrayOf(0.32, 0.64, 0.128), true
            ),
            Arguments.of(
                doubleArrayOf(-0.32, -0.64, -0.128), true
            ),
            Arguments.of(
                doubleArrayOf(64.32, 32.64, 16.128), false
            ),
            Arguments.of(
                doubleArrayOf(2.32, 3.64, -3.872), false
            ),
            Arguments.of(
                doubleArrayOf(1.68, 2.36, -4.128), false
            ),
            Arguments.of(
                doubleArrayOf(66.32, 35.64, 12.128), false
            )
        )
    }
}