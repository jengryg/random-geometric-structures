import figures.PoissonPointProcessStepConstruction
import figures.VietorisRipsComplexStepConstruction
import org.apache.commons.math3.distribution.PoissonDistribution
import ppp.PoissonPointProcess
import ppp.filter.AllowAllPointFilter
import ppp.segmentation.Segmentation
import sc.VietorisRipsComplex
import spaces.metrics.EuclideanMetric

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

    suspend fun exampleVietorisRipsComplexStepConstruction() {
        val segmentation = Segmentation(
            rangeLimits = arrayOf(
                (0..3), (0..3)
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

        val vietorisRipsComplex = VietorisRipsComplex(
            vertices = ppp.acceptedPoints.values.toList(),
            metric = EuclideanMetric(),
            delta = 0.75
        )

        vietorisRipsComplex.calculateAdjacencyMatrix()
        vietorisRipsComplex.generate()

        VietorisRipsComplexStepConstruction(
            vietorisRipsComplex = vietorisRipsComplex
        ).constructFigure()
    }
}