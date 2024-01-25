package svg

import Logging
import OUTPUT_DIRECTORY
import logger
import org.apache.batik.dom.GenericDOMImplementation
import org.apache.batik.svggen.SVGGraphics2D
import ppp.segmentation.Segmentation
import sc.model.SimplexModel
import spaces.spaces.PointAbstract
import spaces.spaces.SpaceAbstract
import java.awt.Color
import java.awt.Font
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.writer
import kotlin.math.roundToInt

/**
 * Representation of the svg image using a 2-dimensional Cartesian coordinate system, where the canvas is given by the
 * [xRange] and [yRange]. The coordinates are mapped to the integer pixel values of the underlying [SVGGraphics2D]
 * coordinates using [pixelPerUnit] as scaling factor and the transformations defined by [transformationX] and
 * [transformationY].
 *
 * @param pixelPerUnit The factor to map floating point coordinates to integer pixel values.
 * The interval [0,1] in double is mapped to the integer set {0,..., pixelPerUnit} by multiplying and rounding.
 *
 * @param xIndex Customize the index of the x coordinate in the coordinate arrays given.
 *
 * @param yIndex Customize the index of the y coordinate in the coordinate arrays given.
 *
 * @param baseName The prefix to use for the svg files created by the [export] method.
 *
 * @param xRange The minimum and maximum coordinate values for the x-Axis.
 *
 * @param yRange The minimum and maximum coordinate values for the y-Axis.
 */
class SVGImage(
    val pixelPerUnit: Int = 100,
    val xIndex: Int = 0,
    val yIndex: Int = 1,
    val baseName: String = "",
    val xRange: IntRange,
    val yRange: IntRange,
) : Logging {
    private val log = logger()

    constructor(
        pixelPerUnit: Int = 100,
        xIndex: Int = 0,
        yIndex: Int = 1,
        baseName: String = "",
        segmentation: Segmentation,
    ) : this(
        pixelPerUnit = pixelPerUnit,
        xIndex = xIndex,
        yIndex = yIndex,
        baseName = baseName,
        xRange = segmentation.rangeLimits[xIndex],
        yRange = segmentation.rangeLimits[yIndex]
    )

    private val doc = GenericDOMImplementation
        .getDOMImplementation()
        .createDocument("https://www.w3.org/2000/svg", "svg", null)

    private val svg: SVGGraphics2D = SVGGraphics2D(doc)

    private val creationStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replace(":", "-")

    /**
     * Set the color of the underlying [svg] to [DVIPSColors.Black].
     * Set the font of the underlying [svg] to size 26 plain TimesRoman.
     */
    fun defaults() {
        svg.color = DVIPSColors.Black
        svg.font = Font("TimesRoman", Font.PLAIN, 26)
    }

    /**
     * Set the font size of the underlying [svg] to the given [size].
     */
    fun fontSize(size: Int) {
        svg.font = Font(svg.font.name, svg.font.style, size)
    }

    init {
        defaults()

        log.atDebug()
            .setMessage("Initialized SvgImage.")
            .addKeyValue("pixelPerUnit", pixelPerUnit)
            .addKeyValue("xIndex", xIndex)
            .addKeyValue("yIndex", yIndex)
            .addKeyValue("baseName", baseName)
            .addKeyValue("xRange", xRange)
            .addKeyValue("yRange", yRange)
            .addKeyValue("creationTimeStamp", creationStamp)
            .log()
    }

    /**
     * Incremented every time the [export] method is used to enumerate the files when no specific filename is given to
     * the [export] method.
     */
    private var exportId = 0

    /**
     * Create a svg file with the current state of this image.
     *
     * Use this method with [name] set to null, for auto-enumeration of the exported files using the default file name
     * ```kotlin
     * "${baseName}_${creationStamp}_${exportId++}.svg"
     * ```
     *
     * @param name the name for the file without .svg extension, or null to use the default.
     *
     * @return chainable this
     */
    fun export(name: String? = null) {
        when {
            name.isNullOrBlank() -> "${baseName}_${exportId++}_${creationStamp}_${UUID.randomUUID()}.svg"
            else -> "${name}.svg"
        }.let { fileName ->
            Path.of(OUTPUT_DIRECTORY, fileName)
                .writer(StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)
                .use { osw ->
                    svg.stream(osw, false)
                }

            log.atDebug()
                .setMessage("Saved SvgImage")
                .addKeyValue("fileName", fileName)
                .log()
        }
    }

    fun color(color: Color) {
        svg.color = color
    }

    fun font(font: Font) {
        svg.font = font
    }

    /**
     * Value conversion from [Double] to the [Int] based pixel value the [svg] uses.
     *
     * @return the pixel value of the given [value] by applying [pixelPerUnit] scaling.
     */
    private fun pV(value: Double) = (value * pixelPerUnit).roundToInt()

    /**
     * The x-coordinate linear transformation to map a cartesian coordinate system to the svg grid.
     * The svg grid has `(0,0)` in the top left corner and `(width, height)` in the bottom right corner.
     *
     * Since we want to have a linear transformation for the x coordinate, that is independent of the y coordinate, this
     * map has the form `Tx(v) = v * m + b`, where `m` is the slope and `b` is the offset in `v = 0`.
     *
     * The positions of the corners require that `Tx(minX) = 0` and `Tx(maxX) = maxX - minX`, thus it follows that
     * `Tx(v) = v * 1 - minX`.
     */
    private fun transformationX(value: Double): Double {
        return value - xRange.first
    }

    /**
     * The y-coordinate linear transformation to map a cartesian coordinate system to the svg grid.
     * The svg grid has `(0,0)` in the top left corner and `(width, height)` in the bottom right corner.
     *
     * Since we want to have a linear transformation for the y coordinate, that is independent of the x coordinate, this
     * map has the form `Ty(v) = v * m + b`, where `m` is the slope and `b` is the offset in `v = 0`.
     *
     * The positions of the corners require that `Ty(maxY) = 0` and `Ty(minY) = maxY - minY`, thus it follows that
     * `Ty(v) = v * (-1) + maxX`.
     */
    private fun transformationY(value: Double): Double {
        return yRange.last - value
    }

    /**
     * Draw a circle with midpoint in [center] and radius given by [radius].
     *
     * If [filled] is set to true, the circle is filled with the current drawing color of the [svg],
     * otherwise only the outline is drawn in that color and the shape itself is transparent.
     */
    fun circle(center: DoubleArray, radius: Double, filled: Boolean = true) {
        if (filled) {
            svg.fillOval(
                pV(transformationX(center[xIndex] - radius)),
                pV(transformationY(center[yIndex] + radius)),
                pV(2 * radius),
                pV(2 * radius)
            )
        } else {
            svg.drawOval(
                pV(transformationX(center[xIndex] - radius)),
                pV(transformationY(center[yIndex] + radius)),
                pV(2 * radius),
                pV(2 * radius)
            )
        }
    }

    /**
     * Draw a circle with midpoint in [center] and radius given by [radius].
     *
     * If [filled] is set to true, the circle is filled with the current drawing color of the [svg],
     * otherwise only the outline is drawn in that color and the shape itself is transparent.
     */
    fun <S : SpaceAbstract> circle(center: PointAbstract<S>, radius: Double, filled: Boolean = true) {
        circle(center = center.absolute, radius = radius, filled = filled)
    }

    /**
     * Draw a rectangle with [lowerBounds] and [upperBounds].
     *
     * If [filled] is set to true, the rectangle is filled with the current drawing color of the [svg],
     * otherwise only the outline is drawn in that color and the shape itself is transparent.
     */
    fun rectangle(lowerBounds: DoubleArray, upperBounds: DoubleArray, filled: Boolean = true) {
        if (filled) {
            svg.fillRect(
                pV(transformationX(lowerBounds[xIndex])),
                pV(transformationY(lowerBounds[yIndex])),
                pV(upperBounds[xIndex] - lowerBounds[xIndex]),
                pV(upperBounds[yIndex] - lowerBounds[yIndex])
            )
        } else {
            svg.drawRect(
                pV(transformationX(lowerBounds[xIndex])),
                pV(transformationY(lowerBounds[yIndex])),
                pV(upperBounds[xIndex] - lowerBounds[xIndex]),
                pV(upperBounds[yIndex] - lowerBounds[yIndex])
            )
        }
    }

    /**
     * Draw a rectangle with [lowerBounds] and [upperBounds].
     *
     * If [filled] is set to true, the rectangle is filled with the current drawing color of the [svg],
     * otherwise only the outline is drawn in that color and the shape itself is transparent.
     */
    fun <S : SpaceAbstract> rectangle(
        lowerBounds: PointAbstract<S>,
        upperBounds: PointAbstract<S>,
        filled: Boolean = true
    ) {
        rectangle(lowerBounds = lowerBounds.absolute, upperBounds = upperBounds.absolute, filled = filled)
    }

    /**
     * Draws a line from [startPoint] to [endPoint].
     */
    fun line(startPoint: DoubleArray, endPoint: DoubleArray) {
        svg.drawLine(
            pV(transformationX(startPoint[xIndex])),
            pV(transformationY(startPoint[yIndex])),
            pV(transformationX(endPoint[xIndex])),
            pV(transformationY(endPoint[yIndex]))
        )
    }

    /**
     * Draws a line from [startPoint] to [endPoint].
     */
    fun <S : SpaceAbstract> line(startPoint: PointAbstract<S>, endPoint: PointAbstract<S>) {
        line(startPoint = startPoint.absolute, endPoint = endPoint.absolute)
    }

    /**
     * Draw a line from the start point given by its x,y coordinates [startX], [startY] to the end point given by
     * its x,y coordinates [endX], [endY]. The x and y coordinate must correspond to [xIndex] resp. [yIndex] in the
     * original points coordinates.
     */
    fun line(startX: Double, startY: Double, endX: Double, endY: Double) {
        svg.drawLine(
            pV(transformationX(startX)),
            pV(transformationY(startY)),
            pV(transformationX(endX)),
            pV(transformationY(endY))
        )
    }

    /**
     * Draw the grid for the given [segmentation].
     */
    fun segmentation(segmentation: Segmentation) {
        xRange.forEach {
            line(
                startX = it.toDouble(),
                startY = yRange.first.toDouble(),
                endX = it.toDouble(),
                endY = yRange.last.toDouble()
            )
        }
        yRange.forEach {
            line(
                startX = xRange.first.toDouble(),
                startY = it.toDouble(),
                endX = xRange.last.toDouble(),
                endY = it.toDouble()
            )
        }
    }

    /**
     * Write the given [string] at the given position.
     * The baseline of the first character is at the position ([x], [y]).
     */
    fun write(string: String, x: Double, y: Double) {
        svg.drawString(
            string,
            pV(transformationX(x)),
            pV(transformationY(y))
        )
    }

    /**
     * Write the given [string] at the given [position].
     * The baseline of the first character is at the position (x,y), where x and y are selected from [position] using
     * [xIndex] resp. [yIndex].
     */
    fun write(string: String, position: DoubleArray) {
        write(string = string, x = position[xIndex], y = position[yIndex])
    }

    /**
     * Draw a polygon given by its [vertices].
     *
     * If [filled] is set to true, the polygon is filled with the current drawing color of the [svg],
     * otherwise only the outline is drawn in that color and the shape itself is transparent.
     */
    fun polygon(vertices: List<DoubleArray>, filled: Boolean = true) {
        if (vertices.size <= 2) {
            throw IllegalArgumentException("Polygon required at least 3 vertices.")
        }

        val xPoints = vertices.map { pV(transformationX(it[xIndex])) }.toIntArray()
        val yPoints = vertices.map { pV(transformationY(it[yIndex])) }.toIntArray()

        if (filled) {
            svg.fillPolygon(xPoints, yPoints, xPoints.size)
        } else {
            svg.drawPolygon(xPoints, yPoints, xPoints.size)
        }
    }

    /**
     * Draw a polygon given by the vertices of the [simplex].
     *
     * If [filled] is set to true, the polygon is filled with the current drawing color of the [svg],
     * otherwise only the outline is drawn in that color and the shape itself is transparent.
     */
    fun simplex(simplex: SimplexModel, filled: Boolean = true) {
        polygon(simplex.vertices.map { it.absolute }, filled)
    }
}