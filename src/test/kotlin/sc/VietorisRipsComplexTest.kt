package sc

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import spaces.Metrics
import spaces.segmentation.Point
import spaces.segmentation.Segmentation

class VietorisRipsComplexTest {
    private val segmentation = Segmentation(
        rangeLimits = arrayOf(1..7, 1..7)
    )

    private val vertices = listOf(
        doubleArrayOf(3.0, 3.0),
        doubleArrayOf(2.0, 5.0),
        doubleArrayOf(4.0, 5.0),
        doubleArrayOf(6.0, 2.0),
        doubleArrayOf(4.0, 2.0),
        doubleArrayOf(5.2, 2.8),
        doubleArrayOf(5.5, 3.5),
    ).mapIndexed { index, doubles ->
        segmentation.segmentOf(doubles)!!.let { segment ->
            Point(
                id = index,
                uniform = segment.relative(doubles),
                segment = segment
            )
        }
    }

    @Test
    fun `create vietoris rips complex`() {
        val vc = VietorisRipsComplex(
            vertices = vertices,
            distance = Metrics::squaredEuclidean,
            delta = 5.76, // = 2.4 squared
            clusterExtension = 3 // ceil(2.4)
        )

        vc.calculateAdjacencyMatrix()
        runTest { vc.generate() }

        assertThat(vc.fVector).containsExactlyInAnyOrderEntriesOf(
            mapOf(0 to 7, 1 to 12, 2 to 6, 3 to 1)
        )
    }

    @Test
    fun `create vietoris complex with all possible simplicies from 7 vertices`() {
        val vc = VietorisRipsComplex(
            vertices = vertices,
            distance = Metrics::squaredEuclidean,
            delta = 100.0, // ensure that everything is connected to everything
            clusterExtension = 10 // ensure that we always use the complete segmentation
        )

        vc.calculateAdjacencyMatrix()
        runTest { vc.generate() }

        assertThat(vc.fVector).containsExactlyInAnyOrderEntriesOf(
            mapOf(0 to 7, 1 to 21, 2 to 35, 3 to 35, 4 to 21, 5 to 7, 6 to 1)
        )
    }

    @Test
    fun `create vietoris complex with all possible simplicies from 7 vertices up to dimension 3`() {
        val vc = VietorisRipsComplex(
            vertices = vertices,
            distance = Metrics::squaredEuclidean,
            delta = 100.0, // ensure that everything is connected to everything
            clusterExtension = 10 // ensure that we always use the complete segmentation
        )

        vc.calculateAdjacencyMatrix()
        runTest { vc.generate(3) }

        assertThat(vc.fVector).containsExactlyInAnyOrderEntriesOf(
            mapOf(0 to 7, 1 to 21, 2 to 35, 3 to 35)
        )
    }

    @Test
    fun `create gilbert graph`() {
        val vc = VietorisRipsComplex(
            vertices = vertices,
            distance = Metrics::squaredEuclidean,
            delta = 5.76, // = 2.4 squared
            clusterExtension = 3 // ceil(2.4)
        )

        vc.calculateAdjacencyMatrix()
        runTest { vc.generate(1) }

        assertThat(vc.fVector).containsExactlyInAnyOrderEntriesOf(
            mapOf(0 to 7, 1 to 12)
        )
    }

    @Test
    fun `ensure that complex will contain vertices`() {
        val vc = VietorisRipsComplex(
            vertices = vertices,
            distance = Metrics::squaredEuclidean,
            delta = 5.76, // = 2.4 squared
            clusterExtension = 3 // ceil(2.4)
        )

        assertThat(vc.fVector).containsExactlyInAnyOrderEntriesOf(
            mapOf(0 to 7)
        )

        vc.calculateAdjacencyMatrix()
        runTest { vc.generate(0) }

        assertThat(vc.fVector).containsExactlyInAnyOrderEntriesOf(
            mapOf(0 to 7)
        )
    }

    @Test
    fun `generate with negative upToDimension throws exception`() {
        val vc = VietorisRipsComplex(
            vertices = vertices,
            distance = Metrics::squaredEuclidean,
            delta = 5.76, // = 2.4 squared
            clusterExtension = 3 // ceil(2.4)
        )

        assertThatThrownBy {
            runTest { vc.generate(-1) }
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("VietorisRipsComplex generation upToDimension can not be negative.")
    }
}