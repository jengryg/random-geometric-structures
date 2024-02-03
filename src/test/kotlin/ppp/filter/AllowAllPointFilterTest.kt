package ppp.filter

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AllowAllPointFilterTest {

    private val filter = AllowAllPointFilter()

    @Test
    fun `allow all point filter must return true`() {
        assertThat(filter.evaluate(mockk()))
    }
}