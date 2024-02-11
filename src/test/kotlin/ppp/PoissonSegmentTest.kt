package ppp

import org.apache.commons.math3.distribution.UniformRealDistribution
import org.apache.commons.math3.random.Well19937c
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.filter.AcceptAllPointFilter
import ppp.filter.RejectAllPointFilter
import spaces.segmentation.Segmentation

class PoissonSegmentTest {
    private val segmentation = Segmentation(rangeLimits = arrayOf(0..1, 0..1))
    private val segment = segmentation.segments.values.first()

    @Test
    fun `verify generation method for segment with allow all`() {
        val ps = PoissonSegment(
            segment = segment,
            numberOfPoints = 10,
            firstPointId = 17,
            positionDistribution = UniformRealDistribution(
                Well19937c(0),
                0.0,
                1.0
            ),
            pointFilter = AcceptAllPointFilter()
        )

        ps.generate()

        assertThat(ps.allPoints).hasSize(10)
        assertThat(ps.acceptedPoints).hasSize(10)
        assertThat(ps.rejectedPoints).isEmpty()

        assertThat(ps.allPoints.map { it.key }).containsExactly(17, 18, 19, 20, 21, 22, 23, 24, 25, 26)

        assertThat(ps.acceptedPoints.values.map { it.absolute.contentToString() }).containsExactlyInAnyOrder(
            "[0.9775554539871507, 0.15595809931982174]",
            "[0.9570892990599607, 0.5462789726707353]",
            "[0.6021314430201985, 0.6948631695591223]",
            "[0.9138123496039838, 0.5576165625634504]",
            "[0.7756027394488849, 0.4461879284980619]",
            "[0.3534621260742632, 0.15714203171370622]",
            "[0.28520429218773224, 0.09112684518827407]",
            "[0.7369473493233778, 0.2253609470934268]",
            "[0.8869459290538819, 0.1287631689128934]",
            "[0.23041616310769242, 0.19403852504039354]"
        )
    }

    @Test
    fun `verify generation method for segment with reject all`() {
        val ps = PoissonSegment(
            segment = segment,
            numberOfPoints = 7,
            firstPointId = 9,
            positionDistribution =  UniformRealDistribution(
                Well19937c(0),
                0.0,
                1.0
            ),
            pointFilter = RejectAllPointFilter()
        )

        ps.generate()

        assertThat(ps.allPoints).hasSize(7)
        assertThat(ps.acceptedPoints).isEmpty()
        assertThat(ps.rejectedPoints).hasSize(7)

        assertThat(ps.allPoints.map { it.key }).containsExactly(9, 10, 11, 12, 13, 14, 15)

        assertThat(ps.rejectedPoints.values.map { it.absolute.contentToString() }).containsExactlyInAnyOrder(
            "[0.9775554539871507, 0.15595809931982174]",
            "[0.9570892990599607, 0.5462789726707353]",
            "[0.6021314430201985, 0.6948631695591223]",
            "[0.9138123496039838, 0.5576165625634504]",
            "[0.7756027394488849, 0.4461879284980619]",
            "[0.3534621260742632, 0.15714203171370622]",
            "[0.28520429218773224, 0.09112684518827407]",
        )
    }
}