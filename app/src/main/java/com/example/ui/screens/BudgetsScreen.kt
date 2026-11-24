package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppStrings
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.GlassCard
import com.example.ui.components.RollingCounterText
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.theme.StateYellow
import com.example.ui.viewmodel.KoshvantaViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetsScreen(
    viewModel: KoshvantaViewModel,
    budgets: List<BudgetEntity>,
    categories: List<CategoryEntity>,
    transactions: List<TransactionWithDetails>,
    onAddBudgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    val lang by viewModel.appLanguage.collectAsState()
    val rolloverEnabled by viewModel.budgetRolloverEnabled.collectAsState()
    val dailyLimitUsd by viewModel.dailySpendingLimitUsd.collectAsState()
    val weeklyLimitUsd by viewModel.weeklyBudgetLimitUsd.collectAsState()
    val alertThreshold by viewModel.alertThresholdPercent.collectAsState()

    var showLimitsDialog by remember { mutableStateOf(false) }

    val categoryMap = categories.associateBy { it.id }
    val categorySpentMap = transactions
        .filter { it.transaction.type == TransactionType.EXPENSE }
        .groupBy { it.transaction.categoryId }
        .mapValues { entry -> entry.value.sumOf { it.transaction.amount } }

    val now = System.currentTimeMillis()
    val todaySpent = transactions
        .filter { it.transaction.type == TransactionType.EXPENSE && it.transaction.timestamp >= now - 24L * 3600 * 1000 }
        .sumOf { it.transaction.amount }
    val weekSpent = transactions
        .filter { it.transaction.type == TransactionType.EXPENSE && it.transaction.timestamp >= now - 7L * 24 * 3600 * 1000 }
        .sumOf { it.transaction.amount }

    val totalBudget = budgets.sumOf { it.monthlyLimit }
    val totalSpentOnBudgets = budgets.sumOf { categorySpentMap[it.categoryId] ?: 0.0 }
    val overallRatio = if (totalBudget > 0) (totalSpentOnBudgets / totalBudget).toFloat() else 0f

    val overallColor by animateColorAsState(
        targetValue = when {
            overallRatio > 0.90f -> StateRed
            overallRatio > (alertThreshold / 100f) -> StateYellow
            else -> StateGreen
        },
        label = "overallBudgetColor"
    )

    if (showLimitsDialog) {
        var dailyText by remember { mutableStateOf(String.format(Locale.US, "%.2f", viewModel.fromUsd(dailyLimitUsd))) }
        var weeklyText by remember { mutableStateOf(String.format(Locale.US, "%.2f", viewModel.fromUsd(weeklyLimitUsd))) }
        AlertDialog(
            onDismissRequest = { showLimitsDialog = false },
            title = { Text("Daily & Weekly Spending Guardrails", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = dailyText,
                        onValueChange = { dailyText = it },
                        label = { Text("Daily Cap (${viewModel.activeSymbol()})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = weeklyText,
                        onValueChange = { weeklyText = it },
                        label = { Text("Weekly Cap (${viewModel.activeSymbol()})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val d = dailyText.toDoubleOrNull() ?: 100.0
                        val w = weeklyText.toDoubleOrNull() ?: 600.0
                        viewModel.setDailyAndWeeklyLimits(d, w)
                        showLimitsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Save Limits", color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { showLimitsDialog = false }) { Text("Cancel") } }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.smartMonthlyBudgets(lang),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "3-state threshold monitoring • Auto alerts at $alertThreshold%",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAddBudgetClick,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("budgets_add_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Budget", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

            // Monthly Summary Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Budget Utilization", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Surface(shape = RoundedCornerShape(10.dp), color = overallColor.copy(alpha = 0.15f)) {
                                Text(
                                    text = "${(overallRatio * 100).toInt()}% Used",
                                    color = overallColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val activeSym = viewModel.activeSymbol()
                        val formatDisplay: (Double) -> String = { displayVal ->
                            val nf = NumberFormat.getNumberInstance(Locale.US).apply {
                                minimumFractionDigits = 2
                                maximumFractionDigits = 2
                            }
                            "$activeSym${nf.format(displayVal)}"
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Total Spent", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                RollingCounterText(
                                    targetValue = viewModel.fromUsd(totalSpentOnBudgets),
                                    formatText = formatDisplay,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = overallColor
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Monthly Limit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                RollingCounterText(
                                    targetValue = viewModel.fromUsd(totalBudget),
                                    formatText = formatDisplay,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { overallRatio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = overallColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Daily Limit, Weekly Cap & Budget Rollover Guardrails Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Daily & Weekly Guardrails", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "Edit Caps",
                                color = accentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showLimitsDialog = true }
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            val dailyRatio = if (dailyLimitUsd > 0) (todaySpent / dailyLimitUsd).toFloat() else 0f
                            val weeklyRatio = if (weeklyLimitUsd > 0) (weekSpent / weeklyLimitUsd).toFloat() else 0f
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("24h Daily Spend", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "${viewModel.formatAmount(todaySpent)} / ${viewModel.formatAmount(dailyLimitUsd)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (dailyRatio > 0.9f) StateRed else StateGreen
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { dailyRatio.coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                                        color = if (dailyRatio > 0.9f) StateRed else StateGreen
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("7-Day Weekly Spend", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "${viewModel.formatAmount(weekSpent)} / ${viewModel.formatAmount(weeklyLimitUsd)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (weeklyRatio > 0.9f) StateRed else accentColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { weeklyRatio.coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                                        color = if (weeklyRatio > 0.9f) StateRed else accentColor
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Budget Carry-Over (Rollover)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Carry unused budget surplus into next month", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = rolloverEnabled,
                                onCheckedChange = { viewModel.setBudgetRollover(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = accentColor)
                            )
                        }
                    }
                }
            }

            // Category Budgets List
            item {
                Text("Category Limits (${budgets.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            if (budgets.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No category budgets configured yet.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(onClick = onAddBudgetClick, colors = ButtonDefaults.buttonColors(containerColor = accentColor)) {
                                Text("Create Budget")
                            }
                        }
                    }
                }
            } else {
                items(budgets, key = { it.id }) { budget ->
                    val category = categoryMap[budget.categoryId]
                    val spent = categorySpentMap[budget.categoryId] ?: 0.0
                    val ratio = if (budget.monthlyLimit > 0) (spent / budget.monthlyLimit).toFloat() else 0f

                    val thresholdColor by animateColorAsState(
                        targetValue = when {
                            ratio > 0.90f -> StateRed
                            ratio >= (alertThreshold / 100f) -> StateYellow
                            else -> StateGreen
                        },
                        label = "catBudgetColor"
                    )

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    val catColor = try { Color(android.graphics.Color.parseColor(category?.colorHex ?: "#2563EB")) } catch (_: Exception) { accentColor }
                                    Box(
                                        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(catColor.copy(alpha = 0.16f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = CategoryIconHelper.iconFor(category?.iconName),
                                            contentDescription = null,
                                            tint = catColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(category?.name ?: "Category #${budget.categoryId}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (ratio >= (alertThreshold / 100f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = thresholdColor, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (ratio >= 1.0f) "Exceeded!" else "$alertThreshold%+ Alert",
                                                fontSize = 11.sp,
                                                color = thresholdColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteBudget(budget.id) },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .testTag("delete_budget_${budget.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Budget",
                                            tint = StateRed.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Spent: ${viewModel.formatAmount(spent)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = thresholdColor)
                                Text("Limit: ${viewModel.formatAmount(budget.monthlyLimit)}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { ratio.coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = thresholdColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}
