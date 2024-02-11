package ppp.filter

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AcceptAllPointFilterTest {
    @Test
    fun `accept all point filter must return true`() {
        assertThat(AcceptAllPointFilter().evaluate(mockk())).isTrue
    }
}