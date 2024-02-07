package sc.model

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ComponentModelTest {
    @Test
    fun `check basic functionality`() {
        val component = ComponentModel(id = 0)

        assertThat(component.vertices).isEmpty()

        component.addPoint(mockk())

        assertThat(component.vertices).hasSize(1)
        assertThat(component.size).isEqualTo(1)

        component.addPoint(mockk())

        assertThat(component.vertices).hasSize(2)
        assertThat(component.size).isEqualTo(2)
    }
}