package spaces.segmentation

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SegmentationTest {

    private val segmentation = Segmentation(
        arrayOf((-3..3), (-3..3), (-3..3))
    )

    @Test
    fun `check segmentation construction yields expected segments`() {
        assertThat(
            Segmentation(
                rangeLimits = arrayOf(
                    (-1..1), (2..6)
                )
            ).segments
        ).hasSize(8).containsKeys(
            "[-1, 2]",
            "[-1, 3]",
            "[-1, 4]",
            "[-1, 5]",
            "[0, 2]",
            "[0, 3]",
            "[0, 4]",
            "[0, 5]",
        )
    }

    @Test
    fun `segmentation must at least have dimension 1`() {
        assertThatThrownBy {
            Segmentation(
                rangeLimits = arrayOf()
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("The dimension of the underlying space must be at least 1!")
    }

    @ParameterizedTest
    @MethodSource("provideDoubleArrayTestDataForSegmentOf")
    fun `check segmentOf with DoubleArray methods`(point: DoubleArray, segmentIdentifier: String) {
        assertThat(
            segmentation.segmentOf(point)
        ).isEqualTo(
            segmentation.segments[segmentIdentifier]
        )
    }

    private fun provideDoubleArrayTestDataForSegmentOf(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(doubleArrayOf(0.0, 0.0, 0.0), "[0, 0, 0]"),
            Arguments.of(doubleArrayOf(0.9, 0.7, 0.2), "[0, 0, 0]"),
            Arguments.of(doubleArrayOf(-0.3, 0.0, 0.0), "[-1, 0, 0]"),
            Arguments.of(doubleArrayOf(-1.2, -3.0, 1.3), "[-2, -3, 1]")
        )
    }

    @ParameterizedTest
    @MethodSource("provideIntArrayTestDataForSegmentOf")
    fun `check segmentOf with IntArray methods`(point: IntArray, segmentIdentifier: String) {
        assertThat(
            segmentation.segmentOf(point)
        ).isEqualTo(
            segmentation.segments[segmentIdentifier]
        )
    }

    private fun provideIntArrayTestDataForSegmentOf(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(intArrayOf(0, 0, 0), "[0, 0, 0]"),
            Arguments.of(intArrayOf(1, 1, 1), "[1, 1, 1]"),
            Arguments.of(intArrayOf(-1, -2, -3), "[-1, -2, -3]")
        )
    }

    @Test
    fun `check neighborhood construction for non-boundary case`() {
        val segment = Segment(
            segmentation = segmentation,
            basePosition = intArrayOf(0, 0, 0)
        )

        segmentation.neighborhood(segment, 0).segments.let { list ->
            assertThat(list).hasSize(1)
            assertThat(list.map { it.basePosition.contentToString() }).containsExactlyInAnyOrder(
                "[0, 0, 0]"
            )
        }

        segmentation.neighborhood(segment, 1).segments.let { list ->
            assertThat(list).hasSize(27)
            assertThat(list.map { it.basePosition.contentToString() }).containsExactlyInAnyOrder(
                "[-1, -1, -1]",
                "[-1, -1, 0]",
                "[-1, -1, 1]",
                "[-1, 0, -1]",
                "[-1, 0, 0]",
                "[-1, 0, 1]",
                "[-1, 1, -1]",
                "[-1, 1, 0]",
                "[-1, 1, 1]",
                "[0, -1, -1]",
                "[0, -1, 0]",
                "[0, -1, 1]",
                "[0, 0, -1]",
                "[0, 0, 0]",
                "[0, 0, 1]",
                "[0, 1, -1]",
                "[0, 1, 0]",
                "[0, 1, 1]",
                "[1, -1, -1]",
                "[1, -1, 0]",
                "[1, -1, 1]",
                "[1, 0, -1]",
                "[1, 0, 0]",
                "[1, 0, 1]",
                "[1, 1, -1]",
                "[1, 1, 0]",
                "[1, 1, 1]"
            )
        }

        segmentation.neighborhood(segment, 2).segments.let { list ->
            assertThat(list).hasSize(125)
        }
    }

    @Test
    fun `check neighborhood construction for boundary cases`() {
        val segment = Segment(
            segmentation = segmentation,
            basePosition = intArrayOf(-3, -3, -3)
        )

        segmentation.neighborhood(segment, 0).segments.let { list ->
            assertThat(list).hasSize(1)
            assertThat(list.map { it.basePosition.contentToString() }).containsExactlyInAnyOrder(
                "[-3, -3, -3]"
            )
        }

        segmentation.neighborhood(segment, 1).segments.let { list ->
            assertThat(list).hasSize(8)
            assertThat(list.map { it.basePosition.contentToString() }).containsExactlyInAnyOrder(
                "[-3, -3, -3]",
                "[-3, -3, -2]",
                "[-3, -2, -3]",
                "[-3, -2, -2]",
                "[-2, -3, -3]",
                "[-2, -3, -2]",
                "[-2, -2, -3]",
                "[-2, -2, -2]"
            )
        }

        segmentation.neighborhood(segment, 2).segments.let { list ->
            assertThat(list).hasSize(27)
        }
    }
}