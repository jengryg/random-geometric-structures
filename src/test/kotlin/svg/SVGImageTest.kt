package svg

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SVGImageTest {

    private val xmlMapper = XmlMapper()

    private val directory = "output_test"

    /**
     * Set this to true, if you want to keep the [directory] and the contained generated svg files from the test for
     * manual inspection.
     */
    private val keepOutput = false

    @OptIn(ExperimentalPathApi::class)
    @BeforeAll
    fun `recreate the output directory for the tests and ensure it is empty`() {
        Path.of(directory).deleteRecursively()
        Path.of(directory).createDirectory()
    }

    @OptIn(ExperimentalPathApi::class)
    @AfterAll
    fun `delete the output directory for the tests and all of it contents`() {
        if (!keepOutput) Path.of(directory).deleteRecursively()
    }

    @Test
    fun `draw circles`() {
        SVGImage(xRange = 0..6, yRange = 0..6).apply {
            color(DVIPSColors.Blue)
            circle(
                center = doubleArrayOf(2.0, 2.0),
                radius = 1.0
            )

            color(DVIPSColors.Red)
            circle(
                center = doubleArrayOf(4.0, 4.0),
                radius = 2.0,
                filled = false
            )
        }.export(name = "circles", directory = directory)

        Path.of(directory, "circles.svg")
            .reader(StandardCharsets.UTF_8, StandardOpenOption.READ)
            .use { isr ->
                xmlMapper.readTree(isr)
            }.let {
                it.findPath("circle").also { jsonNode ->
                    assertThat(jsonNode.isMissingNode).isFalse
                    assertThat(jsonNode).hasSize(2)
                }
            }
    }

    @Test
    fun `draw rectangles`() {
        SVGImage(xRange = 0..6, yRange = 0..6).apply {
            color(DVIPSColors.Blue)
            rectangle(
                lowerBounds = doubleArrayOf(1.0, 1.0),
                upperBounds = doubleArrayOf(2.0, 3.0)
            )

            color(DVIPSColors.Red)
            rectangle(
                lowerBounds = doubleArrayOf(4.0, 4.0),
                upperBounds = doubleArrayOf(5.0, 4.5),
                filled = false
            )
        }.export(name = "rectangles", directory = directory)

        Path.of(directory, "rectangles.svg")
            .reader(StandardCharsets.UTF_8, StandardOpenOption.READ)
            .use { isr ->
                xmlMapper.readTree(isr)
            }.let {
                it.findPath("rect").also { jsonNode ->
                    assertThat(jsonNode.isMissingNode).isFalse
                    assertThat(jsonNode).hasSize(2)
                }
            }
    }

    @Test
    fun `draw lines`() {
        SVGImage(xRange = 0..6, yRange = 0..6).apply {
            color(DVIPSColors.Blue)
            line(
                startPoint = doubleArrayOf(1.0, 1.0),
                endPoint = doubleArrayOf(2.0, 3.0),
            )

            line(
                startX = 4.0,
                startY = 4.0,
                endX = 5.0,
                endY = 4.5,
            )
        }.export(name = "lines", directory = directory)

        Path.of(directory, "lines.svg")
            .reader(StandardCharsets.UTF_8, StandardOpenOption.READ)
            .use { isr ->
                xmlMapper.readTree(isr)
            }.let {
                it.findPath("line").also { jsonNode ->
                    assertThat(jsonNode.isMissingNode).isFalse
                    assertThat(jsonNode).hasSize(2)
                }
            }
    }

    @Test
    fun `draw polygon`() {
        SVGImage(xRange = 0..6, yRange = 0..6).apply {
            color(DVIPSColors.Blue)
            polygon(
                vertices = listOf(
                    doubleArrayOf(1.0, 1.0),
                    doubleArrayOf(5.0, 1.5),
                    doubleArrayOf(3.0, 4.0)
                )
            )

            color(DVIPSColors.Red)
            polygon(
                vertices = listOf(
                    doubleArrayOf(2.0, 2.0),
                    doubleArrayOf(4.0, 2.0),
                    doubleArrayOf(3.0, 3.0),
                ),
                filled = false
            )
        }.export(name = "polygons", directory = directory)

        Path.of(directory, "polygons.svg")
            .reader(StandardCharsets.UTF_8, StandardOpenOption.READ)
            .use { isr ->
                xmlMapper.readTree(isr)
            }.let {
                it.findPath("polygon").also { jsonNode ->
                    assertThat(jsonNode.isMissingNode).isFalse
                    assertThat(jsonNode).hasSize(2)
                }
            }
    }

    @Test
    fun `polygon throws if not at least 3 points`() {
        assertThatThrownBy {
            SVGImage(xRange = 0..6, yRange = 0..6).apply {
                color(DVIPSColors.Blue)
                polygon(
                    vertices = listOf(
                        doubleArrayOf(1.0, 1.0),
                        doubleArrayOf(5.0, 1.5),
                    )
                )
            }
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("Polygon required at least 3 vertices.")
    }

    @Test
    fun `write text`() {
        SVGImage(xRange = 0..6, yRange = 0..6).apply {
            color(DVIPSColors.Blue)
            fontSize(22)
            write(
                string = "Hello World!",
                x = 1.0,
                y = 2.0
            )

            color(DVIPSColors.Red)
            fontSize(72)
            write(
                string = "42",
                position = doubleArrayOf(4.0, 4.0)
            )
        }.export(name = "texts", directory = directory)

        Path.of(directory, "texts.svg")
            .reader(StandardCharsets.UTF_8, StandardOpenOption.READ)
            .use { isr ->
                xmlMapper.readTree(isr)
            }.let {
                it.findPath("text").also { jsonNode ->
                    assertThat(jsonNode.isMissingNode).isFalse
                    assertThat(jsonNode).hasSize(2)
                }
            }
    }

    @Test
    fun `svg file generation`() {
        val generatedNamePattern =
            "^generated_\\d_\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2}-\\d{2}.\\d*Z_[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}.svg\$"

        SVGImage(xRange = 0..6, yRange = 0..6, baseName = "generated").apply {
            assertThat(export(directory = directory).name).matches(generatedNamePattern).startsWith("generated_0")
            assertThat(export(directory = directory).name).matches(generatedNamePattern).startsWith("generated_1")
            assertThat(export(directory = directory).name).matches(generatedNamePattern).startsWith("generated_2")
        }
    }
}