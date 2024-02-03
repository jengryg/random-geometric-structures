package svg

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import svg.DVIPSColors.withAlpha
import java.awt.Color

class DVIPSColorsTest {
    @Test
    fun `check the number of colors in the palette`() {
        Assertions.assertThat(DVIPSColors.palette).hasSize(68)
    }

    @Test
    fun `check the withAlpha extension function on color`() {
        val color = Color(0, 0, 0, 0)
        val colorWithAlpha = color.withAlpha(128)

        Assertions.assertThat(colorWithAlpha.red).isEqualTo(color.red)
        Assertions.assertThat(colorWithAlpha.blue).isEqualTo(color.blue)
        Assertions.assertThat(colorWithAlpha.green).isEqualTo(color.green)

        Assertions.assertThat(colorWithAlpha.alpha).isEqualTo(128)
    }
}