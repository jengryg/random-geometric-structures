package figures

import ppp.PoissonPointProcess
import spaces.toDoubleArray
import svg.DVIPSColors
import svg.SVGImage

class PoissonPointProcessStepConstruction(
    private val poissonPointProcess: PoissonPointProcess,
) {
    private val figures = Array(4) {
        SVGImage(
            baseName = "${this::class.simpleName}_$it",
            segmentation = poissonPointProcess.segmentation
        )
    }

    /**
     * Construct 4 figures, that visualize the construction procedure of the [PoissonPointProcess].
     *
     * To ensure that all svg can be layered in the presentations,
     * draw it in [DVIPSColors.Transparent] when it should not be visible.
     */
    fun constructFigure() {
        drawSegmentation()
        writeNumberOfPoints()
        drawPoints()

        figures.forEach { it.export() }
    }

    /**
     * Draw the grid of the [PoissonPointProcess.segmentation] on figures 0, 1 and 2.
     */
    private fun drawSegmentation() {
        figures.forEachIndexed { index, svgImage ->
            if (index < 3) {
                svgImage.color(DVIPSColors.Gray)
            } else {
                svgImage.color(DVIPSColors.Transparent)
            }

            svgImage.segmentation(segmentation = poissonPointProcess.segmentation)
            // To ensure that all svg can be layered in the presentations, the grid is drawn on each figure.
            // When it should not be displayed, we draw it in Transparent.
        }
    }

    /**
     * Write the number of points that were originally generated into the corner of each segment in the grid on
     * figures 1 and 2.
     */
    private fun writeNumberOfPoints() {
        figures.forEachIndexed { index, svgImage ->
            svgImage.fontSize(26)

            if (index in 1..2) {
                svgImage.color(DVIPSColors.Blue)
            } else {
                svgImage.color(DVIPSColors.Transparent)
            }

            poissonPointProcess.segments.forEach { (_, poissonSegment) ->
                svgImage.write(
                    string = poissonSegment.numberOfPoints.toString(),
                    position = poissonSegment.segment.basePosition.toDoubleArray()
                )
            }
        }
    }

    /**
     * Draw the points of the PPP on figures 3 and 4.
     */
    private fun drawPoints() {
        figures.forEachIndexed { index, svgImage ->
            if (index < 2) {
                svgImage.color(DVIPSColors.Transparent)
            } else {
                svgImage.color(DVIPSColors.Black)
            }

            poissonPointProcess.allPoints.values.forEach {
                svgImage.circle(center = it.absolute, radius = 0.03, filled = true)
            }
        }
    }
}