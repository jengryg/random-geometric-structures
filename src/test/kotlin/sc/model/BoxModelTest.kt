package sc.model

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import spaces.segmentation.Cluster
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BoxModelTest {
    val box = BoxModel(
        intervals = listOf(
            -1.1..1.2,
            -2.5..2.1,
            -3.9..3.7
        )
    )

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 4, 5, 7])
    fun `the box can never be contained in a different dimension cluster`(clusterDimension: Int) {

        val mockCluster = mockk<Cluster> {
            every { dimension } returns clusterDimension
        }
        assertThat(box.containedIn(mockCluster)).isFalse
    }

    @ParameterizedTest
    @MethodSource("provideDifferentClusterBounds")
    fun `evaluate containment using cluster lower and upper bounds`(
        cLowerBound: IntArray,
        cUpperBound: IntArray,
        expected: Boolean
    ) {
        val mockCluster = mockk<Cluster> {
            every { dimension } returns 3
            every { lowerBound } returns cLowerBound
            every { upperBound } returns cUpperBound
        }

        assertThat(box.containedIn(mockCluster)).isEqualTo(expected)
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
                intArrayOf(-2, -3, -4), intArrayOf(2, 3, 4), true
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