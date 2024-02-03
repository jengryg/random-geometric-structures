package spaces

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CombinatoricsGeneratorTest {
    @Test
    fun `check lattice generation from Array of IntRange`() {
        val lattice = CombinatoricsGenerator.lattice(
            rangeLimits = arrayOf(
                -1..1, 0..2
            )
        )

        Assertions.assertThat(lattice).hasSize(9)

        Assertions.assertThat(
            lattice.map {
                it.contentToString()
            }
        ).containsExactlyInAnyOrder(
            "[-1, 0]", "[-1, 1]", "[-1, 2]",
            "[0, 0]", "[0, 1]", "[0, 2]",
            "[1, 0]", "[1, 1]", "[1, 2]"
        )
    }

    @Test
    fun `check lattice generation from lowerCorner and upperCorner`() {
        val lattice = CombinatoricsGenerator.lattice(
            lowerCorner = intArrayOf(-1, 0, -3),
            upperCorner = intArrayOf(1, 2, -1)
        )

        Assertions.assertThat(lattice).hasSize(27)
    }
}
