package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdManager
import com.example.ads.BannerAdSlot
import com.example.data.model.AccountEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.DebtEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppStrings
import com.example.domain.calculator.FinanceCalculators
import com.example.ui.components.GlassCard
import com.example.ui.components.ImpulseCooldownHubCard
import com.example.ui.components.KushwantaMindArenaHubCard
import com.example.ui.components.KushwantaMindArenaScreen
import com.example.ui.components.ToolImpulseCooldownTimer
import com.example.ui.components.ToolWarrantyVault
import com.example.ui.components.WarrantyVaultHubCard
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.theme.StateYellow
import com.example.ui.viewmodel.KoshvantaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class FinancialToolDef(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val badge: String? = null
)

@Composable
fun CalculatorsScreen(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    transactions: List<TransactionWithDetails>,
    budgets: List<BudgetEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    debts: List<DebtEntity>,
    auditLogs: List<AuditLogEntity>,
    onAddGoalClick: () -> Unit,
    onFullScreenGameActiveChanged: (Boolean) -> Unit = {},
    initialToolId: String? = null,
    onInitialToolConsumed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    val lang by viewModel.appLanguage.collectAsState()
    var activeToolId by remember { mutableStateOf<String?>(null) }
    var isArenaGameActive by remember { mutableStateOf(false) }
    val isFullScreenGame = activeToolId == "mind_arena" && isArenaGameActive

    LaunchedEffect(initialToolId) {
        if (initialToolId != null) {
            activeToolId = initialToolId
            onInitialToolConsumed()
        }
    }
    LaunchedEffect(activeToolId) {
        val tool = activeToolId
        if (tool != null && tool != "mind_arena") {
            viewModel.recordFinancialToolExplored(tool)
        } else if (tool == null) {
            viewModel.refreshLeaderboardState()
        }
        if (tool != "mind_arena" && isArenaGameActive) {
            isArenaGameActive = false
        }
    }
    LaunchedEffect(isFullScreenGame) {
        onFullScreenGameActiveChanged(isFullScreenGame)
    }
    DisposableEffect(Unit) {
        onDispose { onFullScreenGameActiveChanged(false) }
    }

    val totalIncome = transactions.filter { it.transaction.type == TransactionType.INCOME }.sumOf { it.transaction.amount }
    val totalExpense = transactions.filter { it.transaction.type == TransactionType.EXPENSE }.sumOf { it.transaction.amount }
    val totalBudget = budgets.sumOf { it.monthlyLimit }
    val liveHealth = remember(totalIncome, totalExpense, totalBudget) {
        FinanceCalculators.calculateHealthScore(totalIncome, totalExpense, totalBudget)
    }

    val toolsList = remember(debts, savingsGoals, liveHealth, accentColor) {
        listOf(
            FinancialToolDef("emi", "Loan EMI & Flat Compare", "Monthly EMI, amortization & flat vs reducing rates", Icons.Default.Calculate, accentColor, "Popular"),
            FinancialToolDef("interest", "Simple & Compound Interest", "FD, RD & investment compound wealth growth", Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF059669)),
            FinancialToolDef("debt", "Debt Manager (उधार/कर्ज)", "Track borrowed & lent money with settlement", Icons.Default.Handshake, Color(0xFFD97706), "${debts.count { !it.isSettled }} pending"),
            FinancialToolDef("split", "Shared Bill Splitter", "Split restaurant, trip & shared living expenses", Icons.Default.Group, Color(0xFF7C3AED)),
            FinancialToolDef("inflation", "Inflation Impact Estimator", "Purchasing power loss over 5, 10, 20 years", Icons.AutoMirrored.Filled.ShowChart, Color(0xFFEF4444)),
            FinancialToolDef("health", "Financial Health Score", "Live savings rate & spending/income analytics", Icons.Default.Speed, StateGreen, "${liveHealth.score}/100"),
            FinancialToolDef("vault", "Goal Savings Vaults", "Emergency fund, milestones & daily savings rate", Icons.Default.Savings, Color(0xFF0284C7), "${savingsGoals.size} active"),
            FinancialToolDef("assistant", "Smart Wealth Intelligence & Tips", "Live ledger insights & 100+ financial tips", Icons.Default.AutoAwesome, Color(0xFF9333EA), "100+ Tips"),
            FinancialToolDef("calendar", "Spending Calendar Heatmap", "Daily intensity grid & date-based snapshot", Icons.Default.CalendarMonth, Color(0xFFF59E0B), "New"),
            FinancialToolDef("integrity", "Reports, Audit & Integrity", "Export PDF/CSV, audit logs & SHA-256 verification", Icons.Default.VerifiedUser, Color(0xFF0D9488), "Safe")
        )
    }

    val exitToolToHub: () -> Unit = {
        val currentTool = activeToolId
        if (currentTool != null && currentTool != "mind_arena") {
            AdManager.showInterstitialAd(placement = "calculator_exit_$currentTool") {
                activeToolId = null
            }
        } else {
            activeToolId = null
        }
    }

    BackHandler(enabled = activeToolId != null && !isFullScreenGame) {
        exitToolToHub()
    }

    Column(
        modifier = if (isFullScreenGame) {
            modifier.fillMaxSize()
        } else {
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        }
    ) {
        if (!isFullScreenGame) {
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (activeToolId == null) {
            Text(
                text = AppStrings.financialToolsSuite(lang),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "12 smart financial calculators, impulse & warranty vaults, heatmap & audit tools",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).clickable { activeToolId = "debt" },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFD97706).copy(alpha = 0.12f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Pending Debts", fontSize = 11.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold)
                        Text("${debts.count { !it.isSettled }} records", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f).clickable { activeToolId = "health" },
                    shape = RoundedCornerShape(14.dp),
                    color = StateGreen.copy(alpha = 0.12f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Health Score", fontSize = 11.sp, color = StateGreen, fontWeight = FontWeight.SemiBold)
                        Text("${liveHealth.score} / 100", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f).clickable { activeToolId = "vault" },
                    shape = RoundedCornerShape(14.dp),
                    color = accentColor.copy(alpha = 0.12f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Savings Vault", fontSize = 11.sp, color = accentColor, fontWeight = FontWeight.SemiBold)
                        Text("${savingsGoals.size} Goals", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 156.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 96.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                item(key = "impulse_featured") {
                    ImpulseCooldownHubCard(
                        viewModel = viewModel,
                        onClick = { activeToolId = "impulse" }
                    )
                }
                item(key = "warranty_featured") {
                    WarrantyVaultHubCard(
                        onClick = { activeToolId = "warranty" }
                    )
                }
                items(toolsList, key = { it.id }) { tool ->
                    ToolGridItem(tool = tool, onClick = { activeToolId = tool.id })
                }
                item(key = "mind_arena_featured", span = { GridItemSpan(maxLineSpan) }) {
                    KushwantaMindArenaHubCard(
                        onClick = { activeToolId = "mind_arena" }
                    )
                }
            }

            BannerAdSlot(
                placementId = "finance_tools_hub_banner",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            if (!isFullScreenGame) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = exitToolToHub) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Tools List")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    val currentTool = toolsList.firstOrNull { it.id == activeToolId }
                    Column {
                        Text(
                            text = when (activeToolId) {
                                "impulse" -> "24h Impulse Cool-Down Timer"
                                "warranty" -> "Product Warranty & Guarantee Vault"
                                "mind_arena" -> "Mind & Wealth Arena"
                                else -> currentTool?.title ?: "Tool"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text("Tap back anytime to return to Tools Suite", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            when (activeToolId) {
                "impulse" -> ToolImpulseCooldownTimer(
                    viewModel = viewModel,
                    accounts = accounts,
                    savingsGoals = savingsGoals,
                    totalIncomeUsd = totalIncome,
                    totalExpenseUsd = totalExpense,
                    totalMonthlyBudgetUsd = totalBudget
                )
                "warranty" -> ToolWarrantyVault()
                "mind_arena" -> KushwantaMindArenaScreen(
                    onFullScreenGameChanged = { isArenaGameActive = it }
                )
                "emi" -> ToolEmiAmortization(viewModel = viewModel)
                "interest" -> ToolInterestCalculator(viewModel = viewModel)
                "debt" -> ToolDebtManager(viewModel = viewModel, debts = debts)
                "split" -> ToolBillSplitter(viewModel = viewModel)
                "inflation" -> ToolInflationCalculator(viewModel = viewModel)
                "health" -> ToolHealthScore(viewModel = viewModel, totalIncome = totalIncome, totalExpense = totalExpense, totalBudget = totalBudget)
                "vault" -> ToolSavingsVault(viewModel = viewModel, accounts = accounts, savingsGoals = savingsGoals, onAddGoalClick = onAddGoalClick)
                "assistant" -> ToolAiAssistant(viewModel = viewModel, accounts = accounts, totalIncome = totalIncome, totalExpense = totalExpense)
                "calendar" -> ToolCalendarHeatmap(viewModel = viewModel, transactions = transactions)
                "integrity" -> ToolReportsAndIntegrity(viewModel = viewModel, accounts = accounts, transactions = transactions, debts = debts, savingsGoals = savingsGoals, auditLogs = auditLogs)
            }
        }
    }
}

@Composable
fun ToolGridItem(tool: FinancialToolDef, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().height(145.dp),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(tool.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(tool.icon, contentDescription = null, tint = tool.accentColor, modifier = Modifier.size(24.dp))
                }
                if (tool.badge != null) {
                    Surface(shape = RoundedCornerShape(8.dp), color = tool.accentColor.copy(alpha = 0.12f)) {
                        Text(
                            text = tool.badge,
                            color = tool.accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Column {
                Text(tool.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(2.dp))
                Text(tool.subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 13.sp)
            }
        }
    }
}

// TOOL 1: Loan EMI & Flat vs Reducing Comparison
@Composable
fun ToolEmiAmortization(viewModel: KoshvantaViewModel) {
    val accentColor = LocalAccentColor.current
    var principalInput by remember { mutableStateOf("25000") }
    var interestRate by remember { mutableDoubleStateOf(8.5) }
    var tenureYears by remember { mutableIntStateOf(3) }

    val principal = principalInput.toDoubleOrNull() ?: 0.0
    val principalUsd = viewModel.toUsd(principal)
    val emiResult = remember(principalUsd, interestRate, tenureYears) {
        FinanceCalculators.calculateEmi(principalUsd, interestRate, tenureYears)
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Loan Principal & Terms", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = principalInput,
                        onValueChange = { principalInput = it },
                        label = { Text("Loan Amount (${viewModel.activeSymbol()})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Annual Interest Rate", fontSize = 12.sp)
                        Text("${String.format(Locale.US, "%.1f", interestRate)}%", fontWeight = FontWeight.Bold, color = accentColor)
                    }
                    Slider(
                        value = interestRate.toFloat(),
                        onValueChange = { interestRate = it.toDouble() },
                        valueRange = 4.0f..24.0f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Loan Tenure", fontSize = 12.sp)
                        Text("$tenureYears Years (${tenureYears * 12} mos)", fontWeight = FontWeight.Bold, color = accentColor)
                    }
                    Slider(
                        value = tenureYears.toFloat(),
                        onValueChange = { tenureYears = it.toInt() },
                        valueRange = 1.0f..10.0f,
                        steps = 8,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassCard(modifier = Modifier.weight(1f), backgroundColor = accentColor) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Reducing Balance EMI", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        Text(viewModel.formatAmount(emiResult.monthlyEmi), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Interest: ${viewModel.formatAmount(emiResult.totalInterest)}", color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp)
                    }
                }
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Flat Rate EMI Compare", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(viewModel.formatAmount(emiResult.flatMonthlyEmi), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StateYellow)
                        Text("Flat Interest: ${viewModel.formatAmount(emiResult.flatTotalInterest)}", fontSize = 10.sp, color = StateRed)
                    }
                }
            }
        }

        item {
            val savedByReducing = (emiResult.flatTotalInterest - emiResult.totalInterest).coerceAtLeast(0.0)
            Surface(shape = RoundedCornerShape(12.dp), color = StateGreen.copy(alpha = 0.12f), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Reducing vs Flat Savings: Reducing balance saves you ${viewModel.formatAmount(savedByReducing)} in total interest!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StateGreen,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        item { Text("Amortization Schedule (First 12 Months)", fontWeight = FontWeight.Bold, fontSize = 13.sp) }

        items(emiResult.schedule.take(12)) { month ->
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Month ${month.monthNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Principal: ${viewModel.formatAmount(month.principalPaid)}", fontSize = 12.sp, color = StateGreen, fontWeight = FontWeight.SemiBold)
                        Text("Balance: ${viewModel.formatAmount(month.remainingBalance)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// TOOL 2: Simple & Compound Interest
@Composable
fun ToolInterestCalculator(viewModel: KoshvantaViewModel) {
    var principalText by remember { mutableStateOf("10000") }
    var ratePercent by remember { mutableDoubleStateOf(7.5) }
    var timeYears by remember { mutableDoubleStateOf(5.0) }

    val principalUsd = viewModel.toUsd(principalText.toDoubleOrNull() ?: 0.0)
    val result = remember(principalUsd, ratePercent, timeYears) {
        FinanceCalculators.calculateInterest(principalUsd, ratePercent, timeYears)
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Investment / Deposit Parameters", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = principalText,
                        onValueChange = { principalText = it },
                        label = { Text("Initial Deposit (${viewModel.activeSymbol()})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Annual Interest Rate: ${String.format(Locale.US, "%.1f", ratePercent)}%", fontSize = 12.sp)
                    Slider(value = ratePercent.toFloat(), onValueChange = { ratePercent = it.toDouble() }, valueRange = 1f..18f)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Duration: ${timeYears.toInt()} Years", fontSize = 12.sp)
                    Slider(value = timeYears.toFloat(), onValueChange = { timeYears = it.toDouble() }, valueRange = 1f..25f)
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassCard(modifier = Modifier.weight(1f), backgroundColor = Color(0xFF059669)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Compound Maturity", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        Text(viewModel.formatAmount(result.maturityCompound), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text("+${viewModel.formatAmount(result.compoundInterest)} interest", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    }
                }
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Simple Maturity", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(viewModel.formatAmount(result.maturitySimple), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("+${viewModel.formatAmount(result.simpleInterest)} interest", fontSize = 10.sp, color = LocalAccentColor.current)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// TOOL 3: Debt Manager
@Composable
fun ToolDebtManager(viewModel: KoshvantaViewModel, debts: List<DebtEntity>) {
    val accentColor = LocalAccentColor.current
    var personName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isLent by remember { mutableStateOf(true) }
    var noteText by remember { mutableStateOf("") }

    val lentTotal = debts.filter { it.isLent && !it.isSettled }.sumOf { it.amount }
    val borrowedTotal = debts.filter { !it.isLent && !it.isSettled }.sumOf { it.amount }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassCard(modifier = Modifier.weight(1f), backgroundColor = StateGreen.copy(alpha = 0.15f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("You Are Owed (Lent)", fontSize = 11.sp, color = StateGreen, fontWeight = FontWeight.Bold)
                        Text(viewModel.formatAmount(lentTotal), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = StateGreen)
                    }
                }
                GlassCard(modifier = Modifier.weight(1f), backgroundColor = StateRed.copy(alpha = 0.15f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("You Owe (Borrowed)", fontSize = 11.sp, color = StateRed, fontWeight = FontWeight.Bold)
                        Text(viewModel.formatAmount(borrowedTotal), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = StateRed)
                    }
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Record New Debt / Loan Entry", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isLent) StateGreen else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f).clickable { isLent = true }
                        ) {
                            Text("I Lent Money", color = if (isLent) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center)
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isLent) StateRed else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f).clickable { isLent = false }
                        ) {
                            Text("I Borrowed Money", color = if (!isLent) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = personName, onValueChange = { personName = it }, label = { Text("Person / Friend Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = amountText, onValueChange = { amountText = it }, label = { Text("Amount (${viewModel.activeSymbol()})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = noteText, onValueChange = { noteText = it }, label = { Text("Reason / Note (optional)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (personName.isNotBlank() && amt > 0.0) {
                                viewModel.addDebt(personName, amt, isLent, noteText)
                                personName = ""; amountText = ""; noteText = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) { Text("Save Debt Record", fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }
        }

        item { Text("Active Debt Records (${debts.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }

        items(debts, key = { it.id }) { debt ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(debt.personName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = if (debt.isLent) StateGreen.copy(alpha = 0.15f) else StateRed.copy(alpha = 0.15f)) {
                                Text(if (debt.isLent) "Lent" else "Borrowed", color = if (debt.isLent) StateGreen else StateRed, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        if (debt.note.isNotEmpty()) Text(debt.note, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(viewModel.formatAmount(debt.amount), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = if (debt.isSettled) Color.Gray else if (debt.isLent) StateGreen else StateRed)
                            Text(if (debt.isSettled) "Settled" else "Pending", fontSize = 10.sp, color = if (debt.isSettled) StateGreen else StateYellow, fontWeight = FontWeight.Bold)
                        }
                        Checkbox(checked = debt.isSettled, onCheckedChange = { viewModel.toggleDebtSettled(debt.id, debt.isSettled) }, colors = CheckboxDefaults.colors(checkedColor = StateGreen))
                        IconButton(onClick = { viewModel.deleteDebt(debt.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Debt", tint = StateRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// TOOL 4: Shared Expense Bill Splitter
@Composable
fun ToolBillSplitter(viewModel: KoshvantaViewModel) {
    var billAmountText by remember { mutableStateOf("120") }
    var peopleCount by remember { mutableIntStateOf(4) }
    var tipPercent by remember { mutableDoubleStateOf(10.0) }

    val totalUsd = viewModel.toUsd(billAmountText.toDoubleOrNull() ?: 0.0)
    val splitResult = remember(totalUsd, peopleCount, tipPercent) {
        FinanceCalculators.calculateBillSplit(totalUsd, peopleCount, tipPercent)
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Bill Split Details", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = billAmountText, onValueChange = { billAmountText = it }, label = { Text("Total Bill Amount (${viewModel.activeSymbol()})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Number of Friends: $peopleCount", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Slider(value = peopleCount.toFloat(), onValueChange = { peopleCount = it.toInt() }, valueRange = 2f..20f, steps = 18)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tip: ${tipPercent.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Slider(value = tipPercent.toFloat(), onValueChange = { tipPercent = it.toDouble() }, valueRange = 0f..25f)
                }
            }
        }
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color(0xFF7C3AED)) {
                Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Each Person Pays", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(viewModel.formatAmount(splitResult.perPersonShare), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total With Tip: ${viewModel.formatAmount(splitResult.totalWithTip)} (Tip: ${viewModel.formatAmount(splitResult.tipAmount)})", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// TOOL 5: Inflation Impact Estimator
@Composable
fun ToolInflationCalculator(viewModel: KoshvantaViewModel) {
    var presentValueText by remember { mutableStateOf("50000") }
    var inflationRate by remember { mutableDoubleStateOf(6.0) }
    var horizonYears by remember { mutableIntStateOf(10) }

    val pvUsd = viewModel.toUsd(presentValueText.toDoubleOrNull() ?: 0.0)
    val result = remember(pvUsd, inflationRate, horizonYears) {
        FinanceCalculators.calculateInflationImpact(pvUsd, inflationRate, horizonYears)
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Inflation Parameters", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = presentValueText, onValueChange = { presentValueText = it }, label = { Text("Current Capital (${viewModel.activeSymbol()})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Expected Inflation: ${String.format(Locale.US, "%.1f", inflationRate)}%", fontSize = 12.sp)
                    Slider(value = inflationRate.toFloat(), onValueChange = { inflationRate = it.toDouble() }, valueRange = 2f..15f)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Time Horizon: $horizonYears Years", fontSize = 12.sp)
                    Slider(value = horizonYears.toFloat(), onValueChange = { horizonYears = it.toInt() }, valueRange = 1f..30f, steps = 28)
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassCard(modifier = Modifier.weight(1f), backgroundColor = StateRed.copy(alpha = 0.15f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Future Amount Needed", fontSize = 11.sp, color = StateRed, fontWeight = FontWeight.Bold)
                        Text(viewModel.formatAmount(result.futureValueRequired), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = StateRed)
                    }
                }
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Purchasing Power Left", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(viewModel.formatAmount(result.purchasingPowerRemaining), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Loss: -${viewModel.formatAmount(result.lossOfPurchasingPower)}", fontSize = 10.sp, color = StateRed)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// TOOL 6: Live Financial Health Score & Monthly Review
@Composable
fun ToolHealthScore(
    viewModel: KoshvantaViewModel,
    totalIncome: Double,
    totalExpense: Double,
    totalBudget: Double
) {
    val result = remember(totalIncome, totalExpense, totalBudget) {
        FinanceCalculators.calculateHealthScore(totalIncome, totalExpense, totalBudget)
    }
    val savingsRate = if (totalIncome > 0) (((totalIncome - totalExpense) / totalIncome) * 100).toInt() else 0
    val spendRatio = if (totalIncome > 0) ((totalExpense / totalIncome) * 100).toInt() else 0
    val gaugeColor = when {
        result.score >= 80 -> StateGreen
        result.score >= 60 -> StateYellow
        else -> StateRed
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Live Financial Health & Savings Rate", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(150.dp)) {
                            val strokeWidth = 14.dp.toPx()
                            val arcSize = size.minDimension - strokeWidth
                            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                            drawArc(color = Color.LightGray.copy(alpha = 0.25f), startAngle = 140f, sweepAngle = 260f, useCenter = false, topLeft = topLeft, size = Size(arcSize, arcSize), style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                            drawArc(color = gaugeColor, startAngle = 140f, sweepAngle = 260f * (result.score / 100f), useCenter = false, topLeft = topLeft, size = Size(arcSize, arcSize), style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${result.score}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = gaugeColor)
                            Text(result.rating, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(12.dp), color = StateGreen.copy(alpha = 0.12f), modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Savings Rate", fontSize = 11.sp, color = StateGreen)
                                Text("$savingsRate%", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = StateGreen)
                            }
                        }
                        Surface(shape = RoundedCornerShape(12.dp), color = LocalAccentColor.current.copy(alpha = 0.12f), modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Spend / Income Ratio", fontSize = 11.sp, color = LocalAccentColor.current)
                                Text("$spendRatio%", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = LocalAccentColor.current)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)) {
                        Text(result.tip, fontSize = 12.sp, modifier = Modifier.padding(12.dp), lineHeight = 16.sp)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// TOOL 7: Goal Savings Vault + Emergency Fund + Daily Savings Calculator
@Composable
fun ToolSavingsVault(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    onAddGoalClick: () -> Unit
) {
    val accentColor = LocalAccentColor.current
    var targetDays by remember { mutableIntStateOf(90) }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Target Savings Goals (${savingsGoals.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Button(onClick = onAddGoalClick, colors = ButtonDefaults.buttonColors(containerColor = accentColor)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Goal", color = Color.White)
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Daily Savings Velocity Window: $targetDays Days", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Slider(value = targetDays.toFloat(), onValueChange = { targetDays = it.toInt() }, valueRange = 15f..365f)
                }
            }
        }

        items(savingsGoals, key = { it.id }) { goal ->
            val ratio = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f
            val goalColor = try { Color(android.graphics.Color.parseColor(goal.colorHex)) } catch (_: Exception) { StateGreen }
            val dailyNeeded = FinanceCalculators.calculateDailySavingsRequired(goal.targetAmount, goal.currentAmount, targetDays)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                val defaultAcc = accounts.firstOrNull()?.id ?: 1L
                                viewModel.depositToGoal(goal.id, viewModel.toUsd(50.0), defaultAcc)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = goalColor.copy(alpha = 0.15f))
                        ) {
                            Text("+${viewModel.activeSymbol()}50 Deposit", color = goalColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Saved: ${viewModel.formatAmount(goal.currentAmount)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = goalColor)
                        Text("Target: ${viewModel.formatAmount(goal.targetAmount)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { ratio.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = goalColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Daily savings needed ($targetDays days): ${viewModel.formatAmount(dailyNeeded)} / day", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// TOOL 8: Offline AI Assistant (Live Data Aware) + 100+ Bookmarkable Tips
@Composable
fun ToolAiAssistant(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    totalIncome: Double,
    totalExpense: Double
) {
    val accentColor = LocalAccentColor.current
    var userQuery by remember { mutableStateOf("") }
    var assistantResponse by remember {
        mutableStateOf("Hello! Ask me live questions like 'What is my balance?', 'How much did I spend?', '50/30/20 rule', 'Rule of 72', or browse 100+ financial wisdom tips below.")
    }
    val bookmarkedTips by viewModel.bookmarkedTips.collectAsState()
    val netWorth = accounts.sumOf { it.balance }
    val savingsRate = if (totalIncome > 0) (((totalIncome - totalExpense) / totalIncome) * 100).toInt() else 0

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Kushwanta Financial Intelligence (Live Data Aware)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = userQuery,
                            onValueChange = { userQuery = it },
                            placeholder = { Text("Ask: 'balance', 'expenses', 'rule of 72'...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (userQuery.isNotBlank()) {
                                    assistantResponse = FinanceCalculators.answerFinancialQuery(
                                        query = userQuery,
                                        formattedNetWorth = viewModel.formatAmount(netWorth),
                                        formattedIncome = viewModel.formatAmount(totalIncome),
                                        formattedExpense = viewModel.formatAmount(totalExpense),
                                        savingsRatePercent = savingsRate
                                    )
                                    userQuery = ""
                                }
                            },
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(accentColor)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)) {
                        Text(assistantResponse, fontSize = 13.sp, modifier = Modifier.padding(14.dp), lineHeight = 18.sp)
                    }
                }
            }
        }

        item {
            Text("100+ Global Financial Wisdom Tips (Tap to Bookmark)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        items(FinanceCalculators.OFFLINE_FINANCIAL_TIPS) { tip ->
            val isBookmarked = bookmarkedTips.contains(tip)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(tip, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.toggleTipBookmark(tip) }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

data class CalendarDayCell(
    val dateLabel: String,
    val dayNumberText: String,
    val totalSpendUsd: Double,
    val transactions: List<TransactionWithDetails>,
    val isToday: Boolean,
    val isCompletedNoSpendGolden: Boolean
)

data class NoSpendCalendarSummary(
    val days: List<CalendarDayCell>,
    val goldenStreakDays: Int,
    val totalGoldenDays: Int
)

object NoSpendCalendarCalculator {
    private const val DAY_MS = 24L * 3600_000L

    fun build35DaySummary(
        transactions: List<TransactionWithDetails>,
        nowMillis: Long = System.currentTimeMillis()
    ): NoSpendCalendarSummary {
        val baseCal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val dateFormat = SimpleDateFormat("dd MMM", Locale.US)

        val days = (34 downTo 0).map { offset ->
            val dCal = (baseCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -offset)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start = dCal.timeInMillis
            val end = start + DAY_MS
            val dayTxs = transactions.filter { it.transaction.timestamp in start until end }
            val spend = dayTxs
                .filter { it.transaction.type == TransactionType.EXPENSE }
                .sumOf { it.transaction.amount }
            val label = dateFormat.format(Date(start))
            val isToday = offset == 0
            val isGolden = !isToday && spend <= 0.0

            CalendarDayCell(
                dateLabel = label,
                dayNumberText = label.take(2),
                totalSpendUsd = spend,
                transactions = dayTxs,
                isToday = isToday,
                isCompletedNoSpendGolden = isGolden
            )
        }

        // Consecutive completed days with ₹0 expenses, counting backward from yesterday
        var streak = 0
        for (idx in (days.size - 2) downTo 0) {
            if (days[idx].isCompletedNoSpendGolden) {
                streak++
            } else {
                break
            }
        }

        return NoSpendCalendarSummary(
            days = days,
            goldenStreakDays = streak,
            totalGoldenDays = days.count { it.isCompletedNoSpendGolden }
        )
    }
}

// TOOL 9: Spending Calendar Heatmap & No-Spend Day Golden Calendar
@Composable
fun ToolCalendarHeatmap(
    viewModel: KoshvantaViewModel,
    transactions: List<TransactionWithDetails>
) {
    val accentColor = LocalAccentColor.current
    val summary = remember(transactions) {
        NoSpendCalendarCalculator.build35DaySummary(transactions)
    }
    val dayTotals = summary.days
    var selectedDayIndex by remember { mutableIntStateOf(dayTotals.lastIndex) }
    val safeSelectedIndex = selectedDayIndex.coerceIn(0, dayTotals.lastIndex)
    val maxSpend = dayTotals.maxOfOrNull { it.totalSpendUsd }?.coerceAtLeast(1.0) ?: 1.0

    val isDark = LocalIsDarkTheme.current
    val goldPrimary = if (isDark) Color(0xFFFFD700) else Color(0xFFB45309)
    val goldAmber = if (isDark) Color(0xFFF59E0B) else Color(0xFFD97706)
    val goldDeepBg = if (isDark) Color(0xFF2B2106) else Color(0xFFFFFFFF)

    val animatedStreak by animateIntAsState(
        targetValue = summary.goldenStreakDays,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "goldenStreakCounter"
    )
    val animatedTotalGolden by animateIntAsState(
        targetValue = summary.totalGoldenDays,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "goldenTotalCounter"
    )

    val glowTransition = rememberInfiniteTransition(label = "goldenCalendarGlow")
    val glowPulse by glowTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "goldenGlowAlpha"
    )
    val starScale by glowTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "goldenStarScale"
    )

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Compact, neatly aligned "Golden Days Streak" counter pill at top
        item {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = goldDeepBg,
                border = BorderStroke(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        colors = if (isDark) {
                            listOf(
                                goldPrimary.copy(alpha = glowPulse),
                                goldAmber.copy(alpha = 0.65f),
                                goldPrimary.copy(alpha = glowPulse)
                            )
                        } else {
                            listOf(
                                goldAmber.copy(alpha = 0.45f),
                                Color(0xFFE2E8F0),
                                goldAmber.copy(alpha = 0.45f)
                            )
                        }
                    )
                ),
                shadowElevation = if (isDark) 4.dp else 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("golden_days_streak_pill")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .scale(starScale)
                                .clip(CircleShape)
                                .background(goldPrimary.copy(alpha = if (isDark) 0.20f else 0.12f))
                                .border(1.dp, goldPrimary.copy(alpha = if (isDark) 0.6f else 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Golden Days Streak",
                                tint = goldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Golden Days Streak",
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "$animatedTotalGolden completed zero-spend days in view",
                                color = if (isDark) goldPrimary.copy(alpha = 0.85f) else Color(0xFF475569),
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = goldPrimary.copy(alpha = if (isDark) 0.18f else 0.12f),
                        border = BorderStroke(1.dp, goldPrimary.copy(alpha = if (isDark) 0.55f else 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = goldPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "$animatedStreak Days",
                                color = goldPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Exact 35-Day Heatmap Calendar Layout with Golden No-Spend Star/Shield Badges
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("35-Day Spending Intensity Heatmap", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                "Tap any square to inspect transactions or Golden No-Spend Days",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = goldPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "₹0 Spend",
                                color = goldAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    dayTotals.chunked(7).forEachIndexed { weekIdx, weekDays ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            weekDays.forEachIndexed { dayIdx, cell ->
                                val globalIdx = weekIdx * 7 + dayIdx
                                val isSelected = globalIdx == safeSelectedIndex
                                val intensity = (cell.totalSpendUsd / maxSpend).toFloat().coerceIn(0f, 1f)
                                val cellColor = when {
                                    cell.isCompletedNoSpendGolden -> goldAmber.copy(alpha = 0.22f)
                                    cell.totalSpendUsd <= 0.0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    intensity > 0.7f -> StateRed.copy(alpha = 0.85f)
                                    intensity > 0.35f -> StateYellow.copy(alpha = 0.8f)
                                    else -> StateGreen.copy(alpha = 0.7f)
                                }
                                val cellModifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) accentColor else cellColor)
                                    .then(
                                        if (cell.isCompletedNoSpendGolden) {
                                            Modifier.border(
                                                width = 1.1.dp,
                                                color = goldPrimary.copy(alpha = if (isSelected) 0.95f else glowPulse),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clickable { selectedDayIndex = globalIdx }
                                    .testTag("calendar_day_cell_$globalIdx")

                                Box(
                                    modifier = cellModifier,
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cell.dayNumberText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isSelected || cell.totalSpendUsd > 0.0 -> Color.White
                                            cell.isCompletedNoSpendGolden -> goldPrimary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )

                                    if (cell.isCompletedNoSpendGolden) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                                .size(13.dp)
                                                .scale(starScale)
                                                .clip(CircleShape)
                                                .background(goldPrimary.copy(alpha = 0.26f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "No-Spend Day Golden Badge",
                                                tint = goldPrimary,
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .testTag("calendar_golden_badge_$globalIdx")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val selectedCell = dayTotals[safeSelectedIndex]
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Date Snapshot: ${selectedCell.dateLabel}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = viewModel.formatAmount(selectedCell.totalSpendUsd),
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selectedCell.isCompletedNoSpendGolden) goldPrimary else StateRed,
                            fontSize = 14.sp
                        )
                    }
                    if (selectedCell.isCompletedNoSpendGolden) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = goldPrimary.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, goldPrimary.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = goldPrimary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Golden No-Spend Day • ₹0 Expenses Logged!",
                                    fontSize = 11.sp,
                                    color = goldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (selectedCell.transactions.isEmpty()) {
                        Text(
                            text = if (selectedCell.isToday) {
                                "Today: Zero expenses so far — complete the day for a Golden Badge!"
                            } else {
                                "Zero expenses logged on ${selectedCell.dateLabel} — No-Spend Day!"
                            },
                            fontSize = 12.sp,
                            color = StateGreen
                        )
                    }

                    if (selectedCell.transactions.isNotEmpty()) {
                        selectedCell.transactions.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("• ${item.transaction.note}", fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(viewModel.formatAmount(item.transaction.amount), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// TOOL 10: Reports (PDF/CSV), Audit History & Data Integrity
@Composable
fun ToolReportsAndIntegrity(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    transactions: List<TransactionWithDetails>,
    debts: List<DebtEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    auditLogs: List<AuditLogEntity>
) {
    val context = LocalContext.current
    val accentColor = LocalAccentColor.current
    val df = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.US) }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Local Database Integrity & Export Studio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("• Accounts Verified: ${accounts.size} | Transactions: ${transactions.size}", fontSize = 12.sp)
                    Text("• Debt Records: ${debts.size} | Savings Vaults: ${savingsGoals.size}", fontSize = 12.sp)
                    Text("• Storage Engine: Instant Local Vault SQLite v3 (Private & Encrypted)", fontSize = 12.sp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.exportPdfReport(context, accounts, transactions) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share PDF", fontSize = 11.sp, color = Color.White)
                        }
                        OutlinedButton(
                            onClick = { viewModel.runIntegrityCheck() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Verify SHA-256", fontSize = 11.sp) }
                    }
                }
            }
        }

        item { Text("Immutable Financial Audit Trail (${auditLogs.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }

        items(auditLogs, key = { it.id }) { log ->
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(log.action, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
                        Text(log.summary, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Text(df.format(Date(log.timestamp)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}
