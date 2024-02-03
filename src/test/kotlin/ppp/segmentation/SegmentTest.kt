package ppp.segmentation

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SegmentTest {

    private val errThreshold = within(0.000000001)

    private val segmentation = Segmentation(
        arrayOf((-3..3), (-3..3), (-3..3))
    )

    @Test
    fun `creating segments for segmentation`() {
        val segment = Segment(
            segmentation = segmentation,
            basePosition = intArrayOf(0, 0, 0)
        )

        assertThat(segment.segmentation).isEqualTo(segmentation)
        assertThat(segment.dimension).isEqualTo(segmentation.dimension)
    }

    @Test
    fun `basePoint and segmentation dimension must match`() {
        assertThatThrownBy {
            Segment(
                segmentation = segmentation,
                basePosition = intArrayOf()
            )
        }.isInstanceOf(AssertionError::class.java)
            .hasMessage("The given base position is of a different dimension than the segmentation dimension!")
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForMidpoints")
    fun `check midpoint calculation of segments`(basePoint: IntArray, midPoint: DoubleArray) {
        assertThat(
            Segment(
                segmentation = segmentation,
                basePosition = basePoint
            ).midpoint
        ).containsExactly(midPoint.toTypedArray(), errThreshold)
    }

    private fun provideTestDataForMidpoints(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(intArrayOf(0, 0, 0), doubleArrayOf(0.5, 0.5, 0.5)),
            Arguments.of(intArrayOf(-1, 2, 1), doubleArrayOf(-0.5, 2.5, 1.5))
        )
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForAbsolute")
    fun `check that absolute adds the base point`(
        basePosition: IntArray,
        relative: DoubleArray,
        absolute: DoubleArray
    ) {
        assertThat(
            Segment(
                segmentation = segmentation,
                basePosition = basePosition
            ).absolute(relative)
        ).containsExactly(absolute.toTypedArray(), errThreshold)
    }

    private fun provideTestDataForAbsolute(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(0.32, 0.64, 0.128), doubleArrayOf(0.32, 0.64, 0.128)
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(-0.32, -0.64, -0.128), doubleArrayOf(-0.32, -0.64, -0.128)
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(64.32, 32.64, 16.128), doubleArrayOf(64.32, 32.64, 16.128)
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(0.32, 0.64, 0.128), doubleArrayOf(2.32, 3.64, -3.872)
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(-0.32, -0.64, -0.128), doubleArrayOf(1.68, 2.36, -4.128)
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(64.32, 32.64, 16.128), doubleArrayOf(66.32, 35.64, 12.128)
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForRelative")
    fun `check that relative subtracts the base point`(
        basePosition: IntArray,
        absolute: DoubleArray,
        relative: DoubleArray
    ) {
        assertThat(
            Segment(
                segmentation = segmentation,
                basePosition = basePosition
            ).relative(absolute)
        ).containsExactly(relative.toTypedArray(), errThreshold)
    }

    private fun provideTestDataForRelative(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(0.32, 0.64, 0.128), doubleArrayOf(0.32, 0.64, 0.128)
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(-0.32, -0.64, -0.128), doubleArrayOf(-0.32, -0.64, -0.128)
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(64.32, 32.64, 16.128), doubleArrayOf(64.32, 32.64, 16.128)
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(2.32, 3.64, -3.872), doubleArrayOf(0.32, 0.64, 0.128)
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(1.68, 2.36, -4.128), doubleArrayOf(-0.32, -0.64, -0.128)
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(66.32, 35.64, 12.128), doubleArrayOf(64.32, 32.64, 16.128)
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForContains")
    fun `validate contains method for different segments`(
        basePosition: IntArray,
        absolute: DoubleArray,
        expected: Boolean
    ) {
        assertThat(
            Segment(
                segmentation = segmentation,
                basePosition = basePosition
            ).contains(absolute)
        ).isEqualTo(expected)
    }

    private fun provideTestDataForContains(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(0.0, 0.0, 0.0), true
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(-0.0, -0.0, -0.0), true
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(1.0, 1.0, 1.0), false
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(1.0, 0.0, 0.0), false
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(0.0, 1.0, 0.0), false
            ),
            Arguments.of(
                intArrayOf(0, 0, 0), doubleArrayOf(0.0, 0.0, 1.0), false
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(2.32, 3.64, -3.872), true
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(2.68, 3.36, -3.128), true
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(2.00, 3.00, -4.00), true
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(-2.32, 3.64, -3.872), false
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(2.68, 4.36, -3.128), false
            ),
            Arguments.of(
                intArrayOf(2, 3, -4), doubleArrayOf(3.00, 4.00, -3.00), false
            ),
        )
    }

    @Test
    fun `check neighborhood construction for non-boundary case`() {
        val segment = Segment(
            segmentation = segmentation,
            basePosition = intArrayOf(0, 0, 0)
        )

        segment.neighborhood(0).segments.let { list ->
            assertThat(list).hasSize(1)
            assertThat(list.map { it.basePosition.contentToString() }).containsExactlyInAnyOrder(
                "[0, 0, 0]"
            )
        }

        segment.neighborhood(1).segments.let { list ->
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
        assertThat(segment.neighborhood(2).segments).hasSize(125)
    }

    @Test
    fun `check neighborhood construction for boundary cases`() {
        val segment = Segment(
            segmentation = segmentation,
            basePosition = intArrayOf(-3, -3, -3)
        )

        segment.neighborhood(0).segments.let { list ->
            assertThat(list).hasSize(1)
            assertThat(list.map { it.basePosition.contentToString() }).containsExactlyInAnyOrder(
                "[-3, -3, -3]"
            )
        }

        segment.neighborhood(1).segments.let { list ->
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
}