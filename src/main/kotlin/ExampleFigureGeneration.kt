import figures.PoissonPointProcessStepConstruction
import figures.VietorisRipsComplexStepConstruction
import org.apache.commons.math3.distribution.PoissonDistribution
import org.apache.commons.math3.distribution.UniformRealDistribution
import ppp.PoissonPointProcess
import ppp.filter.AllowAllPointFilter
import sc.VietorisRipsComplex
import spaces.metrics.EuclideanMetric
import spaces.segmentation.Segmentation

object ExampleFigureGeneration {
    suspend fun poissonPointProcessStepConstruction() {
        val segmentation = Segmentation(
            rangeLimits = arrayOf(
                (0..16), (0..9)
            )
        )

        val pointDistribution = PoissonDistribution(5.0)
        val uniformDistribution = UniformRealDistribution()
        val pointFilter = AllowAllPointFilter()

        val ppp = PoissonPointProcess(
            segmentation = segmentation,
            countDistributionAssigner = { pointDistribution },
            positionDistributionAssigner = { uniformDistribution },
            pointFilterAssigner = { pointFilter }
        )

        ppp.generate()

        PoissonPointProcessStepConstruction(
            poissonPointProcess = ppp,
        ).constructFigure()
    }

    suspend fun vietorisRipsComplexStepConstruction() {
        val segmentation = Segmentation(
            rangeLimits = arrayOf(
                (0..3), (0..3)
            )
        )

        val pointDistribution = PoissonDistribution(5.0)
        val uniformRealDistribution = UniformRealDistribution()
        val pointFilter = AllowAllPointFilter()

        val ppp = PoissonPointProcess(
            segmentation = segmentation,
            countDistributionAssigner = { pointDistribution },
            positionDistributionAssigner = { uniformRealDistribution },
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