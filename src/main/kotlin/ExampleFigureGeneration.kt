import figures.PoissonPointProcessStepConstruction
import org.apache.commons.math3.distribution.PoissonDistribution
import ppp.PoissonPointProcess
import ppp.filter.AllowAllPointFilter
import ppp.segmentation.Segmentation

object ExampleFigureGeneration {
    suspend fun examplePoissonPointProcessStepConstruction() {
        val segmentation = Segmentation(
            rangeLimits = arrayOf(
                (0..16), (0..9)
            )
        )

        val pointDistribution = PoissonDistribution(5.0)
        val pointFilter = AllowAllPointFilter()

        val ppp = PoissonPointProcess(
            segmentation = segmentation,
            countDistributionAssigner = { pointDistribution },
            pointFilterAssigner = { pointFilter }
        )

        ppp.generate()

        PoissonPointProcessStepConstruction(
            poissonPointProcess = ppp,
        ).constructFigure()
    }
}