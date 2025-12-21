package utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SimpleLineChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    pointColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    areaColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
    gridColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.10f),
    chartHeight: Dp = 140.dp,
    animateReveal: Boolean = true,
    animationDurationMillis: Int = 900,
    smooth: Boolean = false
) {
    if (values.isEmpty()) return

    val max = values.maxOrNull() ?: 1f
    val min = values.minOrNull() ?: 0f
    val range = (max - min).takeIf { it > 0f } ?: 1f

    // animatable fraction from 0f -> 1f used to clip the drawing, revealing left-to-right
    val reveal = remember { Animatable(0f) }
    LaunchedEffect(values.hashCode(), animateReveal) {
        if (animateReveal) {
            reveal.snapTo(0f)
            reveal.animateTo(1f, animationSpec = tween(durationMillis = animationDurationMillis))
        } else {
            reveal.snapTo(1f)
        }
    }

    Box(modifier = modifier.fillMaxWidth().height(chartHeight).padding(6.dp)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(chartHeight)) {
            val w = size.width
            val h = size.height
            val count = values.size.coerceAtLeast(1)
            val stepX = if (count > 1) w / (count - 1) else w

            // draw subtle grid lines
            val lines = 3
            for (i in 0..lines) {
                val y = h * i / lines
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
            }

            // Build points
            val points = List(count) { index ->
                val value = values[index]
                val x = index * stepX
                val normalized = (value - min) / range
                val y = h - normalized * h
                Offset(x, y)
            }

            // Build either straight or smoothed paths
            val fullLinePath = Path()
            val fullAreaPath = Path()

            if (smooth && points.size >= 3) {
                // Cubic smoothing using Catmull-Rom to Bezier conversion
                fun catmullRomToBezier(p0: Offset, p1: Offset, p2: Offset, p3: Offset, out: Path) {
                    val c1x = p1.x + (p2.x - p0.x) / 6f
                    val c1y = p1.y + (p2.y - p0.y) / 6f
                    val c2x = p2.x - (p3.x - p1.x) / 6f
                    val c2y = p2.y - (p3.y - p1.y) / 6f
                    out.cubicTo(c1x, c1y, c2x, c2y, p2.x, p2.y)
                }

                // move to first
                fullLinePath.moveTo(points[0].x, points[0].y)
                fullAreaPath.moveTo(points[0].x, h)
                fullAreaPath.lineTo(points[0].x, points[0].y)

                for (i in 1 until points.size - 2) {
                    val p0 = points[i - 1]
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val p3 = points[i + 2]
                    // convert segment p1->p2
                    catmullRomToBezier(p0, p1, p2, p3, fullLinePath)
                    // replicate into area path
                    // area must follow same segments
                    // For area path we just add lineTo for the same p2 point
                    fullAreaPath.lineTo(p2.x, p2.y)
                }

                // handle ending segment if needed
                if (points.size >= 2) {
                    fullLinePath.lineTo(points.last().x, points.last().y)
                    fullAreaPath.lineTo(points.last().x, points.last().y)
                }

                fullAreaPath.lineTo(points.last().x, h)
                fullAreaPath.close()
            } else {
                // straight lines
                points.forEachIndexed { index, p ->
                    if (index == 0) {
                        fullLinePath.moveTo(p.x, p.y)
                        fullAreaPath.moveTo(p.x, h)
                        fullAreaPath.lineTo(p.x, p.y)
                    } else {
                        fullLinePath.lineTo(p.x, p.y)
                        fullAreaPath.lineTo(p.x, p.y)
                    }
                }
                fullAreaPath.lineTo(points.last().x, h)
                fullAreaPath.close()
            }

            // compute reveal width and build clipped paths by stopping/interpolating at revealX
            val revealWidth = w * reveal.value.coerceIn(0f, 1f)

            val clippedLinePath = Path()
            val clippedAreaPath = Path()

            fun addPointToPaths(x: Float, y: Float, isFirst: Boolean) {
                if (isFirst) {
                    clippedLinePath.moveTo(x, y)
                    clippedAreaPath.moveTo(x, h)
                    clippedAreaPath.lineTo(x, y)
                } else {
                    clippedLinePath.lineTo(x, y)
                    clippedAreaPath.lineTo(x, y)
                }
            }

            var addedAny = false
            for (i in points.indices) {
                val p = points[i]
                if (p.x <= revealWidth) {
                    addPointToPaths(p.x, p.y, !addedAny)
                    addedAny = true
                } else {
                    if (i > 0) {
                        val prev = points[i - 1]
                        val t = ((revealWidth - prev.x) / (p.x - prev.x)).coerceIn(0f, 1f)
                        val interpY = prev.y + (p.y - prev.y) * t
                        addPointToPaths(revealWidth, interpY, !addedAny)
                        addedAny = true
                    }
                    break
                }
            }

            if (addedAny) {
                val lastClipX = if (clippedAreaPath.isEmpty) 0f else revealWidth
                clippedAreaPath.lineTo(lastClipX, h)
                clippedAreaPath.close()

                drawPath(path = clippedAreaPath, color = areaColor, style = Fill)
                drawPath(path = clippedLinePath, color = lineColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

                points.forEach { pt ->
                    if (pt.x <= revealWidth + 0.001f) {
                        drawCircle(color = lineColor.copy(alpha = 0.12f), radius = 10f, center = pt)
                        drawCircle(color = pointColor, radius = 6f, center = pt)
                    }
                }
            }

            if (reveal.value in 0.01f..0.99f) {
                val edgeX = revealWidth
                drawLine(
                    color = lineColor.copy(alpha = 0.9f),
                    start = Offset(edgeX, 0f),
                    end = Offset(edgeX, h),
                    strokeWidth = 1f
                )
            }
        }
    }
}
