package ppp.filter

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RejectAllPointFilterTest {
    @Test
    fun `reject all point filter must return false`() {
        assertThat(RejectAllPointFilter().evaluate(mockk())).isFalse
    }
}