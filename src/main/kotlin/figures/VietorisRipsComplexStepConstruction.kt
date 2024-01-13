package figures

import sc.VietorisRipsComplex
import svg.DVIPSColors
import svg.DVIPSColors.withAlpha
import svg.SVGImage

class VietorisRipsComplexStepConstruction(
    private val vietorisRipsComplex: VietorisRipsComplex,
) {

    private val figures = Array(5) {
        SVGImage(
            baseName = "${this::class.simpleName}",
            segmentation = vietorisRipsComplex.segmentation
        )
    }

    /**
     * Construct 5 figures, that visualize the construction procedure of the [VietorisRipsComplex].
     *
     * To ensure that all svg can be layered in the presentations,
     * draw it in [DVIPSColors.Transparent] when it should not be visible.
     */
    fun constructFigure() {
        drawDeltaCircles()
        drawHighDimensionalSimplicies()
        drawEdges()
        drawPoints()
        writePointIds()

        figures.forEach { it.export() }
    }

    /**
     * Draw the circles with radius delta on figures 1, 2 and 3.
     */
    private fun drawDeltaCircles() {
        figures.forEachIndexed { index, svgImage ->
            if (index in (1..3)) {
                svgImage.color(DVIPSColors.Yellow.withAlpha(128))
            } else {
                svgImage.color(DVIPSColors.Transparent)
            }

            vietorisRipsComplex.vertices.forEach {
                svgImage.circle(it, vietorisRipsComplex.delta / 2, filled = true)
                // To ensure that all svg can be layered in the presentations, the grid is drawn on each figure.
                // When it should not be displayed, we draw it in Transparent.
            }
        }
    }

    /**
     * Draw all simplicies of dimension 2 or higher on figures 3 and 4.
     */
    private fun drawHighDimensionalSimplicies() {
        figures.forEachIndexed { index, svgImage ->
            if (index in (3..4)) {
                svgImage.color(DVIPSColors.NavyBlue.withAlpha(128))
            } else {
                svgImage.color(DVIPSColors.Transparent)
            }

            vietorisRipsComplex.simplicies.forEach { (dim, list) ->
                if (dim >= 2) {
                    list.forEach {
                        svgImage.simplex(it)
                    }
                }
            }
        }
    }

    /**
     * Draw the 1-dimensional simplicies (the edges) on figures 2,3 and 4.
     */
    private fun drawEdges() {
        figures.forEachIndexed { index, svgImage ->
            if (index in (2..4)) {
                svgImage.color(DVIPSColors.Red)
            } else {
                svgImage.color(DVIPSColors.Transparent)
            }

            vietorisRipsComplex.simplicies[1]?.forEach {
                svgImage.line(it.vertices[0], it.vertices[1])
            }
        }
    }

    /**
     * Draw the vertices on all figures.
     */
    private fun drawPoints() {
        figures.forEach { svgImage ->
            svgImage.color(DVIPSColors.Black)

            vietorisRipsComplex.vertices.forEach {
                svgImage.circle(center = it, radius = 0.03, filled = true)
            }
        }
    }

    /**
     * Label each point with its id on all figures.
     */
    private fun writePointIds() {
        figures.forEach { svgImage ->
            svgImage.fontSize(12)
            vietorisRipsComplex.vertices.forEach {
                svgImage.write(it.id.toString(), it.absolute + DoubleArray(it.absolute.size) { 0.025 })
            }
        }
    }
}