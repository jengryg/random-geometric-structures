package ppp.segmentation

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SegmentationTest {
    @Test
    fun `segmentation initializes from given IntRanges`() {
        val segmentation = Segmentation(
            rangeLimits = arrayOf(
                (-1..1), (2..6)
            )
        )

        Assertions.assertThat(segmentation.rangeLimits).hasSize(2)

        Assertions.assertThat(segmentation.rangeLimits[0]).containsExactly(-1, 0, 1)
        Assertions.assertThat(segmentation.rangeLimits[1]).containsExactly(2, 3, 4, 5, 6)

        Assertions.assertThat(
            segmentation.segments
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
        Assertions.assertThatThrownBy {
            Segmentation(
                rangeLimits = arrayOf()
            )
        }.isInstanceOf(AssertionError::class.java)
            .hasMessage("The dimension of the underlying space must be greater than 0.")
    }
}