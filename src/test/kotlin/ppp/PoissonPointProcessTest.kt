package ppp

import kotlinx.coroutines.test.runTest
import org.apache.commons.math3.distribution.PoissonDistribution
import org.apache.commons.math3.distribution.UniformRealDistribution
import org.apache.commons.math3.random.Well19937c
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ppp.filter.AllowAllPointFilter
import spaces.segmentation.Segmentation

class PoissonPointProcessTest {
    @Test
    fun `generate a sample poisson point process with seeded rng`() {
        val segmentation = Segmentation(
            rangeLimits = arrayOf(0..3, 0..3)
            // using only positive coordinates to easily get a unique integer for each segment as seed below
        )

        val ppp = PoissonPointProcess(
            segmentation = segmentation,
            countDistributionAssigner = {
                PoissonDistribution(
                    Well19937c(it.basePosition.let { pos -> 10 * pos[0] + pos[1] }),
                    // seed = 10 * x + y, where (x,y) is the base position of the segment
                    5.0,
                    PoissonDistribution::DEFAULT_EPSILON.get(),
                    PoissonDistribution::DEFAULT_MAX_ITERATIONS.get()
                )
            },
            positionDistributionAssigner = {
                UniformRealDistribution(
                    Well19937c(it.basePosition.let { pos -> (10 * pos[0] + pos[1]) * 1000 }),
                    // seed = (10 * x + y) * 1000, where (x,y) is the base position of the segment
                    0.0,
                    1.0
                )
            },
            pointFilterAssigner = {
                AllowAllPointFilter()
            }
        )

        runTest {
            ppp.generate()
            // using the seeded rng for the distributions removes the randomness from this test between runs
            // the generated number of points and positions is the same for each run
        }

        assertThat(ppp.acceptedPoints).hasSize(44)
        assertThat(ppp.allPoints).hasSize(44)
        assertThat(ppp.rejectedPoints).hasSize(0)

        assertThat(ppp.acceptedPoints.values.map { it.absolute.contentToString() }).containsExactlyInAnyOrder(
            "[0.9775554539871507, 0.15595809931982174]",
            "[0.9570892990599607, 0.5462789726707353]",
            "[0.6021314430201985, 0.6948631695591223]",
            "[0.9138123496039838, 0.5576165625634504]",
            "[0.7756027394488849, 0.4461879284980619]",
            "[0.3534621260742632, 0.15714203171370622]",
            "[0.28520429218773224, 0.09112684518827407]",
            "[0.7369473493233778, 0.2253609470934268]",
            "[0.8869459290538819, 0.1287631689128934]",
            "[0.8215027188156714, 1.836685484306794]",
            "[0.7002370655724124, 1.7871621300285196]",
            "[0.8286149152771598, 1.474126310987577]",
            "[0.3607971524343292, 1.6104812294929283]",
            "[0.9897275411368733, 1.640278343933152]",
            "[0.36907960352183244, 1.3066851458844584]",
            "[0.4857129167252747, 2.40870917860522]",
            "[0.267535958706945, 2.353024586883552]",
            "[0.3476390159066556, 2.964507821563572]",
            "[0.3088743117363988, 2.31623280214325]",
            "[0.5645808616272638, 2.735055391374096]",
            "[0.2619842709419671, 2.1745671849336077]",
            "[0.49200002675541055, 2.3336652408775382]",
            "[0.3217251299012114, 2.6573096470081357]",
            "[1.8888138013181062, 0.69521689044647]",
            "[1.7309319126011806, 0.6767660121802841]",
            "[1.3596715789875622, 0.2563241646228964]",
            "[1.878562109154765, 1.0873617208022448]",
            "[1.0453664128318205, 1.6285544470624975]",
            "[1.0329593787475424, 1.0827237355286923]",
            "[1.9314401446854936, 2.7295129859488645]",
            "[1.7005997256835796, 2.915228354986996]",
            "[1.784376312714011, 2.0083462965678507]",
            "[1.2113524399268174, 2.970084205548388]",
            "[1.1847045753598866, 2.430757456569813]",
            "[1.8025652031743216, 2.192513012893718]",
            "[1.8188181728089983, 2.071534321297248]",
            "[2.4415946995010778, 0.18504466368553874]",
            "[2.0556631320067575, 0.6552586792662962]",
            "[2.8438754737818357, 2.872231141795223]",
            "[2.589463886028901, 2.003799317362339]",
            "[2.1771342876583875, 2.15878858454513]",
            "[2.942370131350054, 2.8928405677002957]",
            "[2.6634289074151214, 2.6608694600164906]",
            "[2.716426937148205, 2.960166970665843]"
        )
    }
}