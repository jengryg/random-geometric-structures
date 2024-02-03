package spaces

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrimitiveArrayExtensions {
    @ParameterizedTest
    @MethodSource("provideDoubleAdditionTestData")
    fun `double array component wise addition`(x: DoubleArray, y: DoubleArray, expected: DoubleArray) {
        Assertions.assertThat(x + y).containsExactly(expected.toTypedArray())
    }

    private fun provideDoubleAdditionTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                doubleArrayOf(), doubleArrayOf(), doubleArrayOf()
            ),
            Arguments.of(
                doubleArrayOf(1.0), doubleArrayOf(-1.5), doubleArrayOf(-0.5)
            ),
            Arguments.of(
                doubleArrayOf(0.0, 0.0), doubleArrayOf(3.0, 4.0), doubleArrayOf(3.0, 4.0)
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123),
                doubleArrayOf(3.457, -1.4156, 0.454),
                doubleArrayOf(4.6915, 5.3734, 0.577)
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("provideDoubleSubtractionTestData")
    fun `double array component wise subtraction`(x: DoubleArray, y: DoubleArray, expected: DoubleArray) {
        Assertions.assertThat(x - y).containsExactly(expected.toTypedArray())
    }

    private fun provideDoubleSubtractionTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                doubleArrayOf(), doubleArrayOf(), doubleArrayOf()
            ),
            Arguments.of(
                doubleArrayOf(1.0), doubleArrayOf(-1.5), doubleArrayOf(2.5)
            ),
            Arguments.of(
                doubleArrayOf(0.0, 0.0), doubleArrayOf(3.0, 4.0), doubleArrayOf(-3.0, -4.0)
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123),
                doubleArrayOf(3.457, -1.4156, 0.454),
                doubleArrayOf(-2.2225, 8.2046, -0.331)
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("provideDoubleAdditionIntegerTestData")
    fun `double array component wise addition of integer array`(x: DoubleArray, y: IntArray, expected: DoubleArray) {
        Assertions.assertThat(x + y).containsExactly(expected.toTypedArray())
    }

    private fun provideDoubleAdditionIntegerTestData(): Stream<Arguments> {
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

    @ParameterizedTest
    @MethodSource("provideDoubleSubtractionIntegerTestData")
    fun `double array component wise subtraction of integer array`(x: DoubleArray, y: IntArray, expected: DoubleArray) {
        Assertions.assertThat(x - y).containsExactly(expected.toTypedArray())
    }

    private fun provideDoubleSubtractionIntegerTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                doubleArrayOf(), intArrayOf(), doubleArrayOf()
            ),
            Arguments.of(
                doubleArrayOf(1.0), intArrayOf(-2), doubleArrayOf(3.0)
            ),
            Arguments.of(
                doubleArrayOf(0.0, 0.0), intArrayOf(3, 4), doubleArrayOf(-3.0, -4.0)
            ),
            Arguments.of(
                doubleArrayOf(1.2345, 6.789, 0.123), intArrayOf(3, -1, 0), doubleArrayOf(-1.7655, 7.789, 0.123)
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("provideIntegerAdditionTestData")
    fun `integer array component wise addition`(x: IntArray, y: IntArray, expected: IntArray) {
        Assertions.assertThat(x + y).containsExactly(expected.toTypedArray())
    }

    private fun provideIntegerAdditionTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                intArrayOf(), intArrayOf(), intArrayOf()
            ), Arguments.of(
                intArrayOf(1), intArrayOf(-2), intArrayOf(-1)
            ), Arguments.of(
                intArrayOf(0, 0), intArrayOf(3, 4), intArrayOf(3, 4)
            ), Arguments.of(
                intArrayOf(1, 6, 2), intArrayOf(2, 5, 6), intArrayOf(3, 11, 8)
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideIntegerSubtractionTestData")
    fun `integer array component wise subtraction`(x: IntArray, y: IntArray, expected: IntArray) {
        Assertions.assertThat(x - y).containsExactly(expected.toTypedArray())
    }

    private fun provideIntegerSubtractionTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                intArrayOf(), intArrayOf(), intArrayOf()
            ), Arguments.of(
                intArrayOf(1), intArrayOf(-2), intArrayOf(3)
            ), Arguments.of(
                intArrayOf(0, 0), intArrayOf(3, 4), intArrayOf(-3, -4)
            ), Arguments.of(
                intArrayOf(1, 6, 2), intArrayOf(2, 5, 6), intArrayOf(-1, 1, -4)
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideIntegerArrayToDoubleTestData")
    fun `integer array component wise to double conversion`(given: IntArray, expected: DoubleArray) {
        Assertions.assertThat(given.toDoubleArray()).containsExactly(expected.toTypedArray())
    }

    private fun provideIntegerArrayToDoubleTestData(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                intArrayOf(), doubleArrayOf()
            ), Arguments.of(
                intArrayOf(-1), doubleArrayOf(-1.0)
            ), Arguments.of(
                intArrayOf(3, 4), doubleArrayOf(3.0, 4.0)
            ), Arguments.of(
                intArrayOf(1, 6, 2), doubleArrayOf(1.0, 6.0, 2.0)
            )
        )
    }
}