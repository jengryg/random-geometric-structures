package sc.model

import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ComponentModelTest {
    @Test
    fun `check basic functionality`() {
        val component = ComponentModel(id = 0)

        Assertions.assertThat(component.vertices).isEmpty()

        component.addPoint(mockk())

        Assertions.assertThat(component.vertices).hasSize(1)
        Assertions.assertThat(component.size).isEqualTo(1)

        component.addPoint(mockk())

        Assertions.assertThat(component.vertices).hasSize(2)
        Assertions.assertThat(component.size).isEqualTo(2)
    }
}