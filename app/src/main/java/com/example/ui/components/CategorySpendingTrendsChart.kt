package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.theme.StateYellow

data class CategoryTrendItem(
    val categoryId: Long,
    val categoryName: String,
    val amount: Double,
    val color: Color,
    val percentChange: Double, // positive = spent more, negative = spent less
    val iconName: String,
    val transactionCount: Int
)

@Composable
fun CategorySpendingTrendsChart(
    trendItems: List<CategoryTrendItem>,
    formattedTotalSpend: String,
    formatAmount: (Double) -> String,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableIntStateOf(0) } // 0: 7 Days, 1: 30 Days, 2: 90 Days
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(trendItems, selectedTimeframe) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
    }

    val totalSpend = remember(trendItems) { trendItems.sumOf { it.amount }.coerceAtLeast(1.0) }
    val maxSpend = remember(trendItems) { trendItems.maxOfOrNull { it.amount }?.coerceAtLeast(10.0) ?: 100.0 }

    val activeItem = trendItems.getOrNull(selectedCategoryIndex) ?: trendItems.firstOrNull()
    val accent = LocalAccentColor.current

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_spending_trends_card"),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Title & Timeframe Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Category Spending Trends",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Interactive breakdown & velocity",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Timeframe Chips
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf("7D", "30D", "90D").forEachIndexed { idx, label ->
                        val isSelected = selectedTimeframe == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) accent else Color.Transparent)
                                .clickable { selectedTimeframe = idx }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Category Inspected Highlight Card
            if (activeItem != null) {
                val ratio = (activeItem.amount / totalSpend) * 100.0
                val isIncrease = activeItem.percentChange > 0

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(activeItem.color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = activeItem.categoryName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.1f", ratio)}% of total • ${activeItem.transactionCount} transactions",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatAmount(activeItem.amount),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = accent
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isIncrease) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (isIncrease) StateRed else StateGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${if (isIncrease) "+" else ""}${String.format(java.util.Locale.US, "%.1f", activeItem.percentChange)}% vs prev",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isIncrease) StateRed else StateGreen
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Compose Canvas Interactive Trend Bar Chart
            if (trendItems.isNotEmpty()) {
                val gridLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .pointerInput(trendItems) {
                                detectTapGestures { tapOffset ->
                                    val count = trendItems.size
                                    if (count > 0) {
                                        val barWidth = size.width / count
                                        val tappedIndex = (tapOffset.x / barWidth).toInt().coerceIn(0, count - 1)
                                        selectedCategoryIndex = tappedIndex
                                    }
                                }
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val bottomAxisY = canvasHeight - 20.dp.toPx()
                        val chartHeight = bottomAxisY - 10.dp.toPx()

                        // Draw horizontal dotted grid lines
                        val gridLevels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                        gridLevels.forEach { level ->
                            val lineY = bottomAxisY - (chartHeight * level)
                            drawLine(
                                color = gridLineColor,
                                start = Offset(0f, lineY),
                                end = Offset(canvasWidth, lineY),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )
                        }

                        val barCount = trendItems.size
                        val totalSlotWidth = canvasWidth / barCount
                        val barWidth = (totalSlotWidth * 0.55f).coerceAtMost(32.dp.toPx())

                        trendItems.forEachIndexed { index, item ->
                            val centerX = (index * totalSlotWidth) + (totalSlotWidth / 2f)
                            val normalizedHeight = (item.amount / maxSpend).toFloat().coerceIn(0.05f, 1.0f)
                            val targetBarHeight = chartHeight * normalizedHeight
                            val currentBarHeight = targetBarHeight * animProgress.value

                            val isSelected = index == selectedCategoryIndex
                            val barColor = if (isSelected) item.color else item.color.copy(alpha = 0.55f)

                            val barLeft = centerX - (barWidth / 2f)
                            val barTop = bottomAxisY - currentBarHeight

                            // Draw rounded bar
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(barLeft, barTop),
                                size = Size(barWidth, currentBarHeight),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )

                            // If selected, draw highlight glowing ring at top
                            if (isSelected) {
                                drawCircle(
                                    color = Color.White,
                                    radius = 3.5.dp.toPx(),
                                    center = Offset(centerX, barTop)
                                )
                                drawCircle(
                                    color = item.color,
                                    radius = 5.5.dp.toPx(),
                                    center = Offset(centerX, barTop),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                                )
                            }
                        }

                        // Baseline axis
                        drawLine(
                            color = gridLineColor.copy(alpha = 0.4f),
                            start = Offset(0f, bottomAxisY),
                            end = Offset(canvasWidth, bottomAxisY),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }

                // Category labels below canvas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    trendItems.forEachIndexed { idx, item ->
                        val isSelected = idx == selectedCategoryIndex
                        Text(
                            text = item.categoryName.take(6),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable { selectedCategoryIndex = idx }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No spending logs found for trend calculation",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Velocity Breakdown List
            Text(
                text = "Ranked Category Velocity",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                trendItems.sortedByDescending { it.amount }.take(4).forEachIndexed { rank, item ->
                    val ratio = (item.amount / totalSpend).toFloat()
                    val iconVector = CategoryIconHelper.iconFor(item.iconName)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedCategoryIndex = trendItems.indexOfFirst { it.categoryId == item.categoryId }.coerceAtLeast(0)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "#${rank + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(22.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = null,
                                    tint = item.color,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.categoryName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                LinearProgressIndicator(
                                    progress = { ratio.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = item.color,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatAmount(item.amount),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(ratio * 100).toInt()}% share",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

