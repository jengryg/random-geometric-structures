package convex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeometryCalculationsTest {
    @ParameterizedTest
    @MethodSource("supplyCollinearPoints")
    fun `turn direction of collinear triplets`(a: DoubleArray, b: DoubleArray, c: DoubleArray) {
        assertThat(
            GeometryCalculations.turnDirection(a = a, b = b, c = c)
        ).isEqualTo(GeometryCalculations.TurnDirection.COLLINEAR)
    }

    private fun supplyCollinearPoints(): Stream<Arguments> {
        return Stream.of(
            tdArgument(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            tdArgument(1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
            tdArgument(0.0, -1.0, 0.0, 1.0, 0.0, 0.0),
            tdArgument(1.0, 1.0, 0.0, 0.0, -1.0, -1.0),
            tdArgument(-1.2, -3.34, 0.5, 3.8, 4.2, 19.34)
        )
    }

    @ParameterizedTest
    @MethodSource("supplyClockwiseTurns")
    fun `turn direction of clockwise triplets`(a: DoubleArray, b: DoubleArray, c: DoubleArray) {
        assertThat(
            GeometryCalculations.turnDirection(a = a, b = b, c = c)
        ).isEqualTo(GeometryCalculations.TurnDirection.CLOCKWISE)
    }

    private fun supplyClockwiseTurns(): Stream<Arguments> {
        return Stream.of(
            tdArgument(0.0, 0.0, 2.0, 2.0, 3.0, 1.0),
            tdArgument(-5.0, -2.0, -5.0, 5.0, 5.0, -5.0),
            tdArgument(3.21, -3.66, 5.25, -2.77, 0.53, -5.43)
        )
    }

    @ParameterizedTest
    @MethodSource("supplyCounterClockwiseTurns")
    fun `turn direction of counter clockwise triplets`(a: DoubleArray, b: DoubleArray, c: DoubleArray) {
        assertThat(
            GeometryCalculations.turnDirection(a = a, b = b, c = c)
        ).isEqualTo(GeometryCalculations.TurnDirection.COUNTER_CLOCKWISE)
    }

    private fun supplyCounterClockwiseTurns(): Stream<Arguments> {
        return Stream.of(
            tdArgument(3.0, 1.0, 2.0, 2.0, 0.0, 0.0),
            tdArgument(5.0, -5.0, -5.0, 5.0, -5.0, -2.0),
            tdArgument(0.53, -5.43, 5.25, -2.77, 3.21, -3.66)
        )
    }

    private fun tdArgument(ax: Double, ay: Double, bx: Double, by: Double, cx: Double, cy: Double): Arguments {
        return Arguments.of(
            doubleArrayOf(ax, ay),
            doubleArrayOf(bx, by),
            doubleArrayOf(cx, cy)
        )
    }

    @ParameterizedTest
    @MethodSource("supplyPolarAngleCompareTestData")
    fun `check polar angle compare`(vec1: DoubleArray, vec2: DoubleArray, expected: Int) {
        assertThat(
            GeometryCalculations.comparePolarAngle(vec1, vec2)
        ).isEqualTo(expected)
    }

    private fun supplyPolarAngleCompareTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0, 0.0), 0),
            Arguments.of(doubleArrayOf(1.0, 0.0), doubleArrayOf(1.0, 0.0), 0),
            Arguments.of(doubleArrayOf(0.0, 1.0), doubleArrayOf(1.0, 0.0), 1),
            Arguments.of(doubleArrayOf(0.5, 0.5), doubleArrayOf(-1.0, -1.0), -1),
            Arguments.of(doubleArrayOf(2.0, 2.0), doubleArrayOf(2.0, -2.0), -1),
            Arguments.of(doubleArrayOf(-5.0, -5.0), doubleArrayOf(-5.0, 5.0), 1),
            Arguments.of(doubleArrayOf(0.1, 0.0), doubleArrayOf(-0.1, 0.0), -1),
            Arguments.of(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0, -0.00001), -1),
        )
    }

    @ParameterizedTest
    @MethodSource("supplyPolarRadiusCompareTestData")
    fun `check polar radius compare`(vec1: DoubleArray, vec2: DoubleArray, expected: Int) {
        assertThat(
            GeometryCalculations.comparePolarRadius(vec1, vec2)
        ).isEqualTo(expected)
    }

    private fun supplyPolarRadiusCompareTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0, 0.0), 0),
            Arguments.of(doubleArrayOf(2.0, 1.0), doubleArrayOf(-1.0, -2.0), 0),
            Arguments.of(doubleArrayOf(0.0, 1.0), doubleArrayOf(0.0, -2.0), -1),
            Arguments.of(doubleArrayOf(1.0, 1.0), doubleArrayOf(0.5, 0.5), 1),
            Arguments.of(doubleArrayOf(2.5, 2.5), doubleArrayOf(-10.0, 0.0), -1),
            Arguments.of(doubleArrayOf(8.0, 7.9), doubleArrayOf(0.0, -10.0), 1),
        )
    }

    @ParameterizedTest
    @MethodSource("supplyYXCoordinateCompareTestData")
    fun `check yx coordinate compare`(vec1: DoubleArray, vec2: DoubleArray, expected: Int) {
        assertThat(
            GeometryCalculations.compareYXCoordinates(vec1, vec2)
        ).isEqualTo(expected)
    }

    private fun supplyYXCoordinateCompareTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(doubleArrayOf(0.0, 0.0), doubleArrayOf(0.0, 0.0), 0),
            Arguments.of(doubleArrayOf(0.0, 1.0), doubleArrayOf(0.0, 0.0), 1),
            Arguments.of(doubleArrayOf(1.0, 1.0), doubleArrayOf(0.0, 2.0), -1),
            Arguments.of(doubleArrayOf(1.0, 1.0), doubleArrayOf(0.9, 1.0), 1),
            Arguments.of(doubleArrayOf(0.5, 3.2), doubleArrayOf(0.7, 3.1), 1),
            Arguments.of(doubleArrayOf(0.9, 0.0), doubleArrayOf(10.0, 0.1), -1)
        )
    }
}