package sc

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import spaces.segmentation.Point

class SimplicialComplexTest {
    private val vertices = (0..19).map { i ->
        mockk<Point> {
            every { dimension } returns 2
            every { id } returns i
            every { segment } returns mockk {
                every { segmentation } returns mockk()
            }
            every { absolute } returns doubleArrayOf(0.0, 0.0)
        }
    }

    @Test
    fun `prevent construction of empty simplicial complex`() {
        assertThatThrownBy {
            SimplicialComplex(vertices = listOf())
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("SimplicialComplex must have at least one vertex!")
    }

    @Test
    fun `simplicial complex adds the vertices automatically`() {
        val sc = SimplicialComplex(vertices = vertices)

        sc.simplexCollection(0).also {
            assertThat(it).hasSize(20)
        }.map {
            assertThat(it.vertices).hasSize(1)
            it.vertices.single()
        }.also {
            assertThat(it).containsExactlyInAnyOrderElementsOf(vertices)
        }
    }

    @Test
    fun `simplex collection method ensures singleton for the collections`() {
        val sc = SimplicialComplex(vertices = vertices)

        assertThat(sc.simplexCollection(1)).isEqualTo(sc.simplexCollection(1))
    }

    @Test
    fun `check f-vector`() {
        val sc = SimplicialComplex(vertices = vertices).apply {
            (1..17).forEach { dim ->
                (0..19 - dim).forEach { count ->
                    addSimplex(
                        /**
                         * The dimension of this simplex is given by [dim] and we generate [count] simplicies of each
                         */
                        Simplex(
                            id = dim * 1000 + count * 100,
                            vertices.filter { it.id in (count..count + dim).map { j -> j % 20 } })
                    )
                }
            }
        }

        sc.fVector.also {
            assertThat(it).hasSize(18)
            assertThat(it).containsExactlyInAnyOrderEntriesOf(
                (0..17).associateWith { i -> 20 - i }
            )
        }
    }

    @Test
    fun `adjacency matrix is calculated from one dimensional simplicies`() {
        val sc = SimplicialComplex(vertices = vertices).apply {
            addSimplex(Simplex(id = 100, vertices = vertices.filter { it.id in listOf(3, 7) }))
            addSimplex(Simplex(id = 100, vertices = vertices.filter { it.id in listOf(8, 7) }))
            addSimplex(Simplex(id = 100, vertices = vertices.filter { it.id in listOf(9, 3) }))
            addSimplex(Simplex(id = 100, vertices = vertices.filter { it.id in listOf(1, 2) }))
            addSimplex(Simplex(id = 100, vertices = vertices.filter { it.id in listOf(9, 17) }))
            addSimplex(Simplex(id = 100, vertices = vertices.filter { it.id in listOf(19, 6) }))
        }

        sc.calculateAdjacencyMatrix()

        sc.adjacencyMatrix.also {
            assertThat(it.count()).isEqualTo(6)
            assertThat(it.get(3, 7)).isTrue
            assertThat(it.get(7, 8)).isTrue
            assertThat(it.get(3, 9)).isTrue
            assertThat(it.get(1, 2)).isTrue
            assertThat(it.get(9, 17)).isTrue
            assertThat(it.get(6, 19)).isTrue
        }
    }
}