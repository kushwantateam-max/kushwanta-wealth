package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StateRedNeon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

data class NetWorthCurvePoint(
    val label: String,
    val timestampMillis: Long,
    val netWorthUsd: Double,
    val deltaUsd: Double,
    val note: String = ""
)

object NetWorthCurveCalculator {
    private const val MAX_CHART_POINTS = 36

    /**
     * Dynamically reconstructs the chronological running Net Worth trajectory from live Room
     * transactions and current account balances (never uses static/hardcoded points).
     *
     * Working backwards from [currentNetWorthUsd] reverses each INCOME (+amount) and
     * EXPENSE (-amount) to derive the opening net worth prior to logged entries, then steps
     * forward chronologically so every income/saving pushes the curve UP and every expense
     * pulls it DOWN, terminating at [currentNetWorthUsd].
     */
    fun buildCurve(
        currentNetWorthUsd: Double,
        transactions: List<TransactionWithDetails>
    ): List<NetWorthCurvePoint> {
        val sorted = transactions.sortedWith(
            compareBy<TransactionWithDetails> { it.transaction.timestamp }
                .thenBy { it.transaction.id }
        )

        if (sorted.isEmpty()) {
            val now = System.currentTimeMillis()
            val fmt = SimpleDateFormat("dd MMM", Locale.getDefault())
            return listOf(
                NetWorthCurvePoint(
                    label = fmt.format(Date(now)),
                    timestampMillis = now,
                    netWorthUsd = currentNetWorthUsd,
                    deltaUsd = 0.0,
                    note = "Live Balance"
                )
            )
        }

        val netFlowSum = sorted.sumOf { item ->
            when (item.transaction.type) {
                TransactionType.INCOME -> item.transaction.amount
                TransactionType.EXPENSE -> -item.transaction.amount
                TransactionType.TRANSFER -> 0.0
            }
        }

        var runningUsd = currentNetWorthUsd - netFlowSum
        val timeFmt = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val firstTs = sorted.first().transaction.timestamp

        val rawPoints = ArrayList<NetWorthCurvePoint>(sorted.size + 1)
        rawPoints.add(
            NetWorthCurvePoint(
                label = timeFmt.format(Date(firstTs)),
                timestampMillis = firstTs,
                netWorthUsd = runningUsd,
                deltaUsd = 0.0,
                note = "Opening Net Worth"
            )
        )

        sorted.forEach { item ->
            val tx = item.transaction
            val delta = when (tx.type) {
                TransactionType.INCOME -> tx.amount
                TransactionType.EXPENSE -> -tx.amount
                TransactionType.TRANSFER -> 0.0
            }
            runningUsd += delta
            val fallbackLabel = item.category?.name ?: tx.type.name.lowercase().replaceFirstChar { it.uppercase() }
            rawPoints.add(
                NetWorthCurvePoint(
                    label = timeFmt.format(Date(tx.timestamp)),
                    timestampMillis = tx.timestamp,
                    netWorthUsd = runningUsd,
                    deltaUsd = delta,
                    note = tx.note.ifBlank { fallbackLabel }
                )
            )
        }

        return downsamplePreservingEnds(rawPoints, MAX_CHART_POINTS)
    }

    private fun downsamplePreservingEnds(
        source: List<NetWorthCurvePoint>,
        maxCount: Int
    ): List<NetWorthCurvePoint> {
        if (source.size <= maxCount) return source
        val result = ArrayList<NetWorthCurvePoint>(maxCount)
        val step = (source.size - 1).toDouble() / (maxCount - 1).toDouble()
        var lastIdx = -1
        for (i in 0 until maxCount) {
            val idx = (i * step).roundToInt().coerceIn(0, source.lastIndex)
            if (idx != lastIdx) {
                result.add(source[idx])
                lastIdx = idx
            }
        }
        if (result.lastOrNull() != source.last()) {
            result[result.lastIndex] = source.last()
        }
        return result
    }
}

@Composable
fun NetWorthSplineChart(
    points: List<NetWorthCurvePoint>,
    formatAmount: (Double) -> String,
    modifier: Modifier = Modifier,
    lineColor: Color = NeonMint,
    height: Dp = 104.dp,
    latestDeductedExpenseUsd: Double? = null
) {
    val targetNormalized = remember(points) {
        if (points.isEmpty()) {
            listOf(0.55f)
        } else {
            val minVal = points.minOf { it.netWorthUsd }
            val maxVal = points.maxOf { it.netWorthUsd }
            val span = maxVal - minVal
            if (span < 1e-6) {
                List(points.size) { 0.55f }
            } else {
                val paddedMin = if (minVal >= 0.0) {
                    (minVal - span * 0.45).coerceAtLeast(0.0)
                } else {
                    minVal - span * 0.45
                }
                val paddedMax = maxVal + span * 0.12
                val range = (paddedMax - paddedMin).coerceAtLeast(1e-6)
                points.map { ((it.netWorthUsd - paddedMin) / range).toFloat().coerceIn(0.08f, 0.94f) }
            }
        }
    }

    var fromNormalized by remember { mutableStateOf(List(targetNormalized.size) { 0.0f }) }
    var toNormalized by remember { mutableStateOf(targetNormalized) }
    val morphProgress = remember { Animatable(0f) }
    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }
    var canvasPixelSize by remember { mutableStateOf(IntSize.Zero) }
    var badgePixelSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    LaunchedEffect(targetNormalized) {
        val currentVisual = interpolateSeries(fromNormalized, toNormalized, morphProgress.value)
        fromNormalized = resampleSeries(currentVisual, targetNormalized.size)
        toNormalized = targetNormalized
        morphProgress.snapTo(0f)
        morphProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.80f,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    val interpolatedValues = interpolateSeries(fromNormalized, toNormalized, morphProgress.value)

    Column(modifier = modifier.fillMaxWidth()) {
        val activePoint = selectedIndex?.let { idx -> points.getOrNull(idx) }
        AnimatedVisibility(
            visible = activePoint != null,
            enter = fadeIn(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            activePoint?.let { pt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCanvasBg.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, lineColor.copy(alpha = 0.75f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { selectedIndex = null }
                        .testTag("net_worth_chart_tooltip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pt.note.ifBlank { "Balance Point" },
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = pt.label,
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatAmount(pt.netWorthUsd),
                                color = lineColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            if (abs(pt.deltaUsd) >= 0.01) {
                                val sign = if (pt.deltaUsd >= 0) "+" else "-"
                                val deltaColor = if (pt.deltaUsd >= 0) lineColor else StateRedNeon
                                Text(
                                    text = "$sign${formatAmount(abs(pt.deltaUsd))}",
                                    color = deltaColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .onSizeChanged { canvasPixelSize = it }
                .semantics {
                    contentDescription = "Live Net Worth spline chart with ${points.size} points. Tap or drag to inspect values."
                }
                .testTag("net_worth_spline_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            if (points.isEmpty()) return@detectTapGestures
                            val count = points.size
                            val idx = if (count == 1) {
                                0
                            } else {
                                ((offset.x / size.width.coerceAtLeast(1)) * (count - 1))
                                    .roundToInt()
                                    .coerceIn(0, count - 1)
                            }
                            selectedIndex = if (selectedIndex == idx) null else idx
                        }
                    }
                    .pointerInput(points) {
                        detectHorizontalDragGestures(
                            onDragStart = { offset ->
                                if (points.isNotEmpty()) {
                                    val count = points.size
                                    selectedIndex = if (count == 1) {
                                        0
                                    } else {
                                        ((offset.x / size.width.coerceAtLeast(1)) * (count - 1))
                                            .roundToInt()
                                            .coerceIn(0, count - 1)
                                    }
                                }
                            },
                            onHorizontalDrag = { change, _ ->
                                if (points.isNotEmpty()) {
                                    val count = points.size
                                    selectedIndex = if (count == 1) {
                                        0
                                    } else {
                                        ((change.position.x / size.width.coerceAtLeast(1)) * (count - 1))
                                            .roundToInt()
                                            .coerceIn(0, count - 1)
                                    }
                                }
                            }
                        )
                    }
            ) {
                if (interpolatedValues.isEmpty()) return@Canvas

                val startPad = 8.dp.toPx()
                val endPad = 10.dp.toPx()
                val usableWidth = (size.width - startPad - endPad).coerceAtLeast(1f)
                val topPadding = size.height * 0.24f
                val bottomPadding = size.height * 0.14f
                val usableHeight = (size.height - topPadding - bottomPadding).coerceAtLeast(1f)

                val coordinates: List<Offset> = if (interpolatedValues.size == 1) {
                    val y = size.height - bottomPadding - (interpolatedValues[0] * usableHeight)
                    listOf(
                        Offset(startPad, y),
                        Offset(size.width - endPad, y)
                    )
                } else {
                    val spacing = usableWidth / (interpolatedValues.size - 1).coerceAtLeast(1)
                    interpolatedValues.mapIndexed { index, normY ->
                        val x = startPad + (index * spacing)
                        val y = size.height - bottomPadding - (normY * usableHeight)
                        Offset(x, y)
                    }
                }

                val strokePath = Path()
                val fillPath = Path()

                strokePath.moveTo(coordinates[0].x, coordinates[0].y)
                fillPath.moveTo(coordinates[0].x, size.height)
                fillPath.lineTo(coordinates[0].x, coordinates[0].y)

                for (i in 0 until coordinates.size - 1) {
                    val p0 = coordinates[i]
                    val p1 = coordinates[i + 1]
                    val controlX = (p0.x + p1.x) / 2f
                    strokePath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                    fillPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                }

                fillPath.lineTo(coordinates.last().x, size.height)
                fillPath.close()

                // Gradient fill underneath
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.36f),
                            lineColor.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height
                    )
                )

                // Soft outer Electric Cyan glow
                drawPath(
                    path = strokePath,
                    color = lineColor.copy(alpha = 0.20f),
                    style = Stroke(width = 8.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = strokePath,
                    color = lineColor.copy(alpha = 0.42f),
                    style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Crisp Neon #00FFCC spline stroke
                drawPath(
                    path = strokePath,
                    color = lineColor,
                    style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Final Black End Dot at the exact end of the graph line
                val endOffset = coordinates.last()
                drawCircle(
                    color = lineColor.copy(alpha = 0.35f),
                    radius = 9.dp.toPx(),
                    center = endOffset
                )
                drawCircle(
                    color = lineColor,
                    radius = 5.5.dp.toPx(),
                    center = endOffset
                )
                drawCircle(
                    color = DarkCanvasBg,
                    radius = 3.2.dp.toPx(),
                    center = endOffset
                )

                // Active / tapped inspection dot (when inspecting an earlier point)
                val tappedIndex = selectedIndex
                if (tappedIndex != null && points.size > 1) {
                    val tappedOffset = coordinates[tappedIndex.coerceIn(0, coordinates.lastIndex)]
                    drawLine(
                        color = lineColor.copy(alpha = 0.55f),
                        start = Offset(tappedOffset.x, 0f),
                        end = Offset(tappedOffset.x, size.height),
                        strokeWidth = 1.2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                    drawCircle(
                        color = lineColor.copy(alpha = 0.42f),
                        radius = 10.dp.toPx(),
                        center = tappedOffset
                    )
                    drawCircle(
                        color = lineColor,
                        radius = 5.dp.toPx(),
                        center = tappedOffset
                    )
                    drawCircle(
                        color = DarkCanvasBg,
                        radius = 2.5.dp.toPx(),
                        center = tappedOffset
                    )
                }
            }

            // Anchored badge on the final black end dot showing the latest deducted expense amount (e.g., -₹200.00)
            val endDeductedExpenseUsd = remember(points, latestDeductedExpenseUsd) {
                latestDeductedExpenseUsd?.takeIf { it > 0.005 }
                    ?: points.lastOrNull { it.deltaUsd < -0.005 }?.let { abs(it.deltaUsd) }
            }
            if (endDeductedExpenseUsd != null && canvasPixelSize.width > 0 && interpolatedValues.isNotEmpty()) {
                val endDotOffsetPx = with(density) {
                    val startPad = 8.dp.toPx()
                    val endPad = 10.dp.toPx()
                    val w = canvasPixelSize.width.toFloat()
                    val h = canvasPixelSize.height.toFloat()
                    val topPadding = h * 0.24f
                    val bottomPadding = h * 0.14f
                    val usableHeight = (h - topPadding - bottomPadding).coerceAtLeast(1f)
                    val endX = w - endPad
                    val endY = h - bottomPadding - (interpolatedValues.last() * usableHeight)
                    Offset(endX, endY)
                }
                val gapPx = with(density) { 8.dp.roundToPx() }
                val badgeX = (endDotOffsetPx.x - badgePixelSize.width * 0.88f)
                    .roundToInt()
                    .coerceIn(0, (canvasPixelSize.width - badgePixelSize.width).coerceAtLeast(0))
                val aboveY = (endDotOffsetPx.y - badgePixelSize.height - gapPx).roundToInt()
                val badgeY = if (aboveY >= 0) {
                    aboveY
                } else {
                    (endDotOffsetPx.y + gapPx).roundToInt()
                        .coerceAtMost((canvasPixelSize.height - badgePixelSize.height).coerceAtLeast(0))
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = DarkCanvasBg.copy(alpha = 0.94f),
                    border = BorderStroke(1.dp, StateRedNeon.copy(alpha = 0.85f)),
                    modifier = Modifier
                        .offset { IntOffset(badgeX, badgeY) }
                        .onSizeChanged { badgePixelSize = it }
                        .testTag("net_worth_end_dot_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(StateRedNeon)
                        )
                        Text(
                            text = "-${formatAmount(endDeductedExpenseUsd)}",
                            color = Color(0xFFFF8FA3),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }

            if (points.size <= 1) {
                Text(
                    text = "Tap point • Log Income/Expense for live curve",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 2.dp)
                )
            }
        }
    }
}

private fun resampleSeries(source: List<Float>, targetSize: Int): List<Float> {
    if (targetSize <= 0) return emptyList()
    if (source.isEmpty()) return List(targetSize) { 0f }
    if (source.size == targetSize) return source
    if (source.size == 1) return List(targetSize) { source[0] }
    if (targetSize == 1) return listOf(source.last())

    return List(targetSize) { i ->
        val pos = (i.toFloat() / (targetSize - 1).toFloat()) * (source.size - 1).toFloat()
        val low = pos.toInt().coerceIn(0, source.lastIndex)
        val high = (low + 1).coerceIn(0, source.lastIndex)
        val frac = pos - low
        source[low] + ((source[high] - source[low]) * frac)
    }
}

private fun interpolateSeries(from: List<Float>, to: List<Float>, progress: Float): List<Float> {
    if (to.isEmpty()) return emptyList()
    val t = progress.coerceIn(0f, 1.15f)
    val safeFrom = if (from.size == to.size) from else resampleSeries(from, to.size)
    return List(to.size) { i ->
        (safeFrom[i] + ((to[i] - safeFrom[i]) * t)).coerceIn(0.02f, 0.98f)
    }
}

@Composable
fun BalanceAreaChart(
    points: List<Double>,
    lineColor: Color = Color.White,
    gradientColors: List<Color> = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
    modifier: Modifier = Modifier,
    height: Dp = 90.dp
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(points) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (points.isEmpty()) return@Canvas

        val maxVal = (points.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
        val minVal = (points.minOrNull() ?: 0.0)
        val range = (maxVal - minVal).coerceAtLeast(1.0)

        val spacing = size.width / (points.size - 1).coerceAtLeast(1)
        val path = Path()
        val fillPath = Path()

        val coordinates = points.mapIndexed { index, value ->
            val normalized = (value - minVal) / range
            val x = index * spacing
            val targetY = size.height - (normalized.toFloat() * (size.height * 0.75f)) - (size.height * 0.1f)
            val animatedY = size.height - ((size.height - targetY) * animProgress.value)
            Offset(x, animatedY)
        }

        if (coordinates.isNotEmpty()) {
            path.moveTo(coordinates[0].x, coordinates[0].y)
            fillPath.moveTo(coordinates[0].x, size.height)
            fillPath.lineTo(coordinates[0].x, coordinates[0].y)

            for (i in 0 until coordinates.size - 1) {
                val p0 = coordinates[i]
                val p1 = coordinates[i + 1]
                val controlX = (p0.x + p1.x) / 2f

                path.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                fillPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }

            fillPath.lineTo(coordinates.last().x, size.height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = size.height
                )
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            val last = coordinates.last()
            drawCircle(
                color = lineColor,
                radius = 4.5.dp.toPx(),
                center = last
            )
            drawCircle(
                color = lineColor.copy(alpha = 0.4f),
                radius = 8.dp.toPx(),
                center = last
            )
        }
    }
}
