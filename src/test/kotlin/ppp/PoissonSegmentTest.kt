package ppp

import org.apache.commons.math3.distribution.UniformRealDistribution
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.filter.AllowAllPointFilter
import ppp.filter.RejectAllPointFilter
import ppp.segmentation.Segmentation

class PoissonSegmentTest {
    private val segmentation = Segmentation(rangeLimits = arrayOf(0..1, 0..1))
    private val segment = segmentation.segments.values.first()

    @Test
    fun `verify generation method for segment with allow all`() {
        val ps = PoissonSegment(
            segment = segment,
            numberOfPoints = 10,
            firstPointId = 17,
            positionDistribution = UniformRealDistribution(),
            pointFilter = AllowAllPointFilter()
        )

        ps.generate()

        assertThat(ps.allPoints).hasSize(10)
        assertThat(ps.acceptedPoints).hasSize(10)
        assertThat(ps.rejectedPoints).isEmpty()

        assertThat(ps.allPoints.map { it.key }).containsExactly(17, 18, 19, 20, 21, 22, 23, 24, 25, 26)
    }

    @Test
    fun `verify generation method for segment with reject all`() {
        val ps = PoissonSegment(
            segment = segment,
            numberOfPoints = 7,
            firstPointId = 9,
            positionDistribution = UniformRealDistribution(),
            pointFilter = RejectAllPointFilter()
        )

        ps.generate()

        assertThat(ps.allPoints).hasSize(7)
        assertThat(ps.acceptedPoints).isEmpty()
        assertThat(ps.rejectedPoints).hasSize(7)

        assertThat(ps.allPoints.map { it.key }).containsExactly(9, 10, 11, 12, 13, 14, 15)
    }
}