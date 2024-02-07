package svg

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import svg.DVIPSColors.withAlpha
import java.awt.Color

class DVIPSColorsTest {
    @Test
    fun `check the number of colors in the palette`() {
        assertThat(DVIPSColors.palette).hasSize(68)
    }

    @Test
    fun `check the withAlpha extension function on color`() {
        val color = Color(0, 0, 0, 0)
        val colorWithAlpha = color.withAlpha(128)

        assertThat(colorWithAlpha.red).isEqualTo(color.red)
        assertThat(colorWithAlpha.blue).isEqualTo(color.blue)
        assertThat(colorWithAlpha.green).isEqualTo(color.green)

        assertThat(colorWithAlpha.alpha).isEqualTo(128)
    }
}