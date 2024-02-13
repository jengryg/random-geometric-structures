package convex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import spaces.segmentation.Point
import spaces.segmentation.Segmentation
import kotlin.math.abs

class ConvexHullTest {

    private val segmentation = Segmentation(rangeLimits = arrayOf(-10..10, -10..10))
    private val projection = IndexBasedPlaneProjection(0, 1)

    private val triangle =
        listOf(
            doubleArrayOf(-5.0, -5.0),
            doubleArrayOf(5.0, -5.0),
            doubleArrayOf(0.0, 5.0)
        ).mapIndexed { index, absolute ->
            segmentation.segmentOf(absolute)!!.let { segment ->
                Point(
                    id = index,
                    segment = segment,
                    uniform = segment.relative(absolute)
                )
            }
        }

    private val collinear =
        listOf(
            doubleArrayOf(8.0, 8.0),
            doubleArrayOf(-7.5, -7.5),
            doubleArrayOf(5.0, 5.0),
            doubleArrayOf(-5.0, -5.0),
            doubleArrayOf(0.0, 0.0),
            doubleArrayOf(-7.0, -7.0),
        ).mapIndexed { index, absolute ->
            segmentation.segmentOf(absolute)!!.let { segment ->
                Point(
                    id = index,
                    segment = segment,
                    uniform = segment.relative(absolute)
                )
            }
        }

    private val grid =
        doubleArrayOf(-5.0, -4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0).flatMap { x ->
            doubleArrayOf(-5.0, -4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0).map { y ->
                doubleArrayOf(x, y)
            }
        }.mapIndexed { index, absolute ->
            segmentation.segmentOf(absolute)!!.let { segment ->
                Point(
                    id = index,
                    segment = segment,
                    uniform = segment.relative(absolute)
                )
            }
        }

    @Test
    fun `generate hull of trivial cases`() {
        ConvexHull(
            points = triangle.slice(0..0),
            planeProjection = projection
        ).apply {
            generate()
            assertThat(hullPoints).containsExactly(triangle[0])
        }

        ConvexHull(
            points = triangle.slice(0..1),
            planeProjection = projection
        ).apply {
            generate()
            assertThat(hullPoints).containsExactly(triangle[0], triangle[1])
        }
    }

    @Test
    fun `generate hull of triangle`() {
        ConvexHull(
            points = triangle,
            planeProjection = projection
        ).apply {
            generate()
            assertThat(hullPoints).containsExactly(triangle[0], triangle[1], triangle[2])
        }
    }

    @Test
    fun `generate hull collinear points`() {
        ConvexHull(
            points = collinear,
            planeProjection = projection
        ).apply {
            generate()
            assertThat(hullPoints).containsExactly(collinear[1], collinear[0])
        }
    }

    @Test
    fun `generate hull of a grid`() {
        ConvexHull(
            points = grid,
            planeProjection = projection
        ).apply {
            generate()
            assertThat(hullPoints).containsExactly(
                grid.single { it.absolute[0] == -5.0 && it.absolute[1] == -5.0 },
                grid.single { it.absolute[0] == 5.0 && it.absolute[1] == -5.0 },
                grid.single { it.absolute[0] == 5.0 && it.absolute[1] == 5.0 },
                grid.single { it.absolute[0] == -5.0 && it.absolute[1] == 5.0 },
            )
        }
    }

    @Test
    fun `generate hull of diamond grid`() {
        ConvexHull(
            points = grid.filter { abs(it.absolute[0]) + abs(it.absolute[1]) <= 5.0 },
            planeProjection = projection
        ).apply {
            generate()
            assertThat(hullPoints).containsExactly(
                grid.single { it.absolute[0] == 0.0 && it.absolute[1] == -5.0 },
                grid.single { it.absolute[0] == 5.0 && it.absolute[1] == 0.0 },
                grid.single { it.absolute[0] == 0.0 && it.absolute[1] == 5.0 },
                grid.single { it.absolute[0] == -5.0 && it.absolute[1] == 0.0 },
            )
        }
    }
}