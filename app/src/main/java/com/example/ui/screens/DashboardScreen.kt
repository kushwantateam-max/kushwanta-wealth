package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ads.AdManager
import com.example.ads.BannerAdSlot
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.BillReminderEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.DebtEntity
import com.example.data.model.FinancialBook
import com.example.data.model.NotificationHistoryEntity
import com.example.data.model.QuickPresetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppPreferences
import com.example.data.prefs.AppStrings
import com.example.domain.calculator.FinanceCalculators
import com.example.ui.components.AchievementsModalSheet
import com.example.ui.components.AddBillDialog
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.CategorySpendingTrendsChart
import com.example.ui.components.CategoryTrendItem
import com.example.ui.components.DonutSlice
import com.example.ui.components.GlassCard
import com.example.ui.components.GlobalCurrencyPickerDialog
import com.example.ui.components.MetalCardVaultCalculator
import com.example.ui.components.MetalCardVaultStore
import com.example.ui.components.Mini3DNeonMetalCard
import com.example.ui.components.NetWorthCurveCalculator
import com.example.ui.components.NetWorthSplineChart
import com.example.ui.components.NotificationsHistorySheet
import com.example.ui.components.PresetEditDialog
import com.example.ui.components.ReportsAnalyticsSheet
import com.example.ui.components.RollingCounterText
import com.example.ui.components.SpendingDonutChart
import com.example.ui.components.TransactionDetailEditDialog
import com.example.ui.theme.CardGradientBlueEnd
import com.example.ui.theme.CardGradientBlueStart
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.viewmodel.KoshvantaViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    transactions: List<TransactionWithDetails>,
    bills: List<BillReminderEntity>,
    presets: List<QuickPresetEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    debts: List<DebtEntity>,
    notifications: List<NotificationHistoryEntity>,
    currentBook: FinancialBook,
    currentCurrency: CurrencyCode,
    onNavigateToAccounts: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    modifier: Modifier = Modifier,
    budgets: List<BudgetEntity> = emptyList(),
    onOpenAccountInVault: (Long) -> Unit = { onNavigateToAccounts() },
    onOpenLeaderboard: () -> Unit = {}
) {
    val accentColor = LocalAccentColor.current
    val lang by viewModel.appLanguage.collectAsState()
    val customSymbol by viewModel.customCurrencySymbol.collectAsState()
    val enabledWidgets by viewModel.enabledWidgets.collectAsState()
    val streakDays by viewModel.streakCount.collectAsState()
    val bestStreakDays by viewModel.personalBestStreak.collectAsState()
    val activeLoginDates by viewModel.activeLoginDates.collectAsState()
    val lastLoginDate by viewModel.lastLoginDate.collectAsState()
    val thresholdPct by viewModel.alertThresholdPercent.collectAsState()
    val vaultVisualVersion by viewModel.vaultVisualVersion.collectAsState()
    val liveLeaderboardRank by viewModel.userLeaderboardRank.collectAsStateWithLifecycle()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsState()

    var showCurrencyPicker by remember { mutableStateOf(false) }
    var showBookMenu by remember { mutableStateOf(false) }
    var showAchievementsSheet by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showReportsSheet by remember { mutableStateOf(false) }
    var showAddBillDialog by remember { mutableStateOf(false) }
    var editingPreset by remember { mutableStateOf<QuickPresetEntity?>(null) }
    var creatingNewPreset by remember { mutableStateOf(false) }
    var selectedTransactionForDetail by remember { mutableStateOf<TransactionWithDetails?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val haptic = LocalHapticFeedback.current
    val dashboardLifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.refreshLeaderboardState()
    }

    DisposableEffect(dashboardLifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshLeaderboardState()
            }
        }
        dashboardLifecycleOwner.lifecycle.addObserver(observer)
        onDispose { dashboardLifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showCurrencyPicker) {
        GlobalCurrencyPickerDialog(
            currentCurrency = currentCurrency,
            customSymbol = customSymbol,
            onSelectCurrency = { viewModel.switchCurrency(it) },
            onSaveCustomSymbol = { viewModel.setCustomCurrencySymbol(it) },
            onDismiss = { showCurrencyPicker = false }
        )
    }

    if (creatingNewPreset || editingPreset != null) {
        PresetEditDialog(
            viewModel = viewModel,
            existingPreset = editingPreset,
            categories = uiState.categories,
            onDismiss = {
                creatingNewPreset = false
                editingPreset = null
            }
        )
    }

    if (showAchievementsSheet) {
        AchievementsModalSheet(
            streakDays = streakDays,
            bestStreakDays = bestStreakDays,
            activeLoginDates = activeLoginDates,
            lastLoginDate = lastLoginDate,
            budgets = budgets,
            savingsGoals = savingsGoals,
            debts = debts,
            transactions = transactions,
            onDismiss = { showAchievementsSheet = false }
        )
    }

    if (showNotificationsSheet) {
        NotificationsHistorySheet(
            notifications = notifications,
            thresholdPercent = thresholdPct,
            onSetThreshold = { viewModel.setAlertThreshold(it) },
            onSendTestNotification = { viewModel.sendTestNotification(context) },
            onClearAll = { viewModel.clearNotificationHistory() },
            onDismiss = {
                viewModel.markNotificationsRead()
                showNotificationsSheet = false
            }
        )
    }

    if (showReportsSheet) {
        ReportsAnalyticsSheet(
            viewModel = viewModel,
            accounts = accounts,
            transactions = transactions,
            onDismiss = {
                AdManager.showInterstitialAd(placement = "reports_exit") {
                    showReportsSheet = false
                }
            }
        )
    }

    if (showAddBillDialog) {
        AddBillDialog(viewModel = viewModel, onDismiss = { showAddBillDialog = false })
    }

    selectedTransactionForDetail?.let { txItem ->
        TransactionDetailEditDialog(
            viewModel = viewModel,
            item = txItem,
            categories = uiState.categories,
            onDismiss = { selectedTransactionForDetail = null }
        )
    }

    val totalBalance = accounts.sumOf { it.balance }
    val incomeTotal = transactions.filter { it.transaction.type == TransactionType.INCOME }.sumOf { it.transaction.amount }
    val expenseTotal = transactions.filter { it.transaction.type == TransactionType.EXPENSE }.sumOf { it.transaction.amount }
    val latestDeductedExpenseUsd = remember(transactions) {
        transactions
            .filter { it.transaction.type == TransactionType.EXPENSE }
            .maxWithOrNull(compareBy<TransactionWithDetails> { it.transaction.timestamp }.thenBy { it.transaction.id })
            ?.transaction?.amount
    }
    val liveNetWorthCurve by viewModel.netWorthCurve.collectAsStateWithLifecycle()
    val chartPoints = remember(liveNetWorthCurve, totalBalance, transactions) {
        if (liveNetWorthCurve.isNotEmpty()) {
            liveNetWorthCurve
        } else {
            NetWorthCurveCalculator.buildCurve(totalBalance, transactions)
        }
    }

    val categoryExpenses = transactions
        .filter { it.transaction.type == TransactionType.EXPENSE && it.category != null }
        .groupBy { it.category!! }
        .mapValues { entry -> entry.value.sumOf { it.transaction.amount } }

    val donutSlices = categoryExpenses.entries.take(4).map { (category, amount) ->
        val color = try { Color(android.graphics.Color.parseColor(category.colorHex)) } catch (_: Exception) { accentColor }
        DonutSlice(label = category.name, value = amount, color = color)
    }

    val categoryTrendItems = remember(transactions) {
        val expenseTx = transactions.filter { it.transaction.type == TransactionType.EXPENSE && it.category != null }
        val grouped = expenseTx.groupBy { it.category!! }
        grouped.map { (cat, txs) ->
            val amount = txs.sumOf { it.transaction.amount }
            val color = try { Color(android.graphics.Color.parseColor(cat.colorHex)) } catch (_: Exception) { accentColor }
            val recentHalf = txs.filter { it.transaction.timestamp > (System.currentTimeMillis() - 7L * 24 * 3600 * 1000) }.sumOf { it.transaction.amount }
            val olderHalf = (amount - recentHalf).coerceAtLeast(1.0)
            val percentChange = ((recentHalf - olderHalf) / olderHalf) * 100.0
            CategoryTrendItem(cat.id, cat.name, amount, color, percentChange, cat.iconName, txs.size)
        }.sortedByDescending { it.amount }
    }

    val unpaidBillsTotal = bills.filter { !it.isPaid }.sumOf { it.amount }
    val spendableBalance = (totalBalance - unpaidBillsTotal).coerceAtLeast(0.0)
    val unreadAlertsCount = notifications.count { !it.isRead }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val typeFilter by viewModel.transactionTypeFilter.collectAsState()
    val isDemoDataPermanentlyDisabled by viewModel.isDemoDataPermanentlyDisabled.collectAsState()

    val filteredTransactions = remember(transactions, searchQuery, typeFilter) {
        transactions.filter { item ->
            val matchesType = when (typeFilter) {
                "EXPENSE" -> item.transaction.type == TransactionType.EXPENSE
                "INCOME" -> item.transaction.type == TransactionType.INCOME
                "TRANSFER" -> item.transaction.type == TransactionType.TRANSFER
                else -> true
            }
            val matchesQuery = if (searchQuery.isBlank()) true else {
                item.transaction.note.contains(searchQuery, ignoreCase = true) ||
                    (item.category?.name?.contains(searchQuery, ignoreCase = true) == true) ||
                    (item.account?.name?.contains(searchQuery, ignoreCase = true) == true)
            }
            matchesType && matchesQuery
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // --- 1. Frameless Flagship Brand Header, Book Switcher, Notification Bell & Currency Pill ---
        item {
            val isDarkHeader = LocalIsDarkTheme.current
            val wordmarkBrush = remember(isDarkHeader) {
                Brush.linearGradient(
                    if (isDarkHeader) {
                        listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
                    } else {
                        listOf(Color(0xFF00796B), Color(0xFF0F172A))
                    }
                )
            }

            val formattedLiveRank = remember(liveLeaderboardRank) {
                NumberFormat.getNumberInstance(Locale.US).format(liveLeaderboardRank)
            }
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 0.dp)
            ) {
                val isCompactHeader = maxWidth < 375.dp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isCompactHeader) 38.dp else 42.dp)
                                .clip(CircleShape)
                                .background(DarkCanvasBg)
                                .border(
                                    width = 1.8.dp,
                                    brush = Brush.linearGradient(
                                        if (isDarkHeader) listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
                                        else listOf(Color(0xFF00695C), Color(0xFFB45309))
                                    ),
                                    shape = CircleShape
                                )
                                .testTag("dashboard_official_logo_badge"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (profilePhotoUri.isNotBlank()) {
                                ProfileAvatarImage(
                                    photoUri = profilePhotoUri,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(1.5.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.koshvanta_official_logo),
                                    contentDescription = "Kushwanta Official Logo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(if (isCompactHeader) 7.dp else 10.dp))
                        Box(modifier = Modifier.weight(1f, fill = false)) {
                            Column(
                                modifier = Modifier
                                    .clickable { showBookMenu = true }
                                    .testTag("header_book_switcher")
                            ) {
                                Text(
                                    text = "KUSHWANTA",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        brush = wordmarkBrush,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = if (isCompactHeader) 14.5.sp else 16.sp,
                                        letterSpacing = if (isCompactHeader) 1.0.sp else 1.4.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "WEALTH OS",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.1.sp,
                                        color = if (isDarkHeader) Color(0xFFFFD700).copy(alpha = 0.85f) else Color(0xFF00796B),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = " • ${currentBook.title}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 9.5.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Switch Ledger Book",
                                        tint = accentColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            DropdownMenu(expanded = showBookMenu, onDismissRequest = { showBookMenu = false }) {
                                FinancialBook.entries.forEach { b ->
                                    DropdownMenuItem(
                                        text = { Text("${b.title} Ledger") },
                                        onClick = {
                                            viewModel.switchBook(b)
                                            showBookMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        // Clean, minimal 3D Golden-Cyan Trophy Leaderboard Icon Button
                        val trophyBorderBrush = remember(isDarkHeader) {
                            Brush.linearGradient(
                                if (isDarkHeader) {
                                    listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
                                } else {
                                    listOf(Color(0xFFD97706), Color(0xFF00695C))
                                }
                            )
                        }
                        val trophyFillBrush = remember(isDarkHeader) {
                            Brush.verticalGradient(
                                if (isDarkHeader) {
                                    listOf(Color(0xFF1A2233), Color(0xFF0B111C))
                                } else {
                                    listOf(Color(0xFFFFFFFF), Color(0xFFFEF3C7).copy(alpha = 0.55f))
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(trophyFillBrush)
                                .border(1.5.dp, trophyBorderBrush, CircleShape)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.playAchievementChime()
                                    onOpenLeaderboard()
                                }
                                .testTag("home_leaderboard_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Leaderboard Rank #$formattedLiveRank",
                                tint = if (isDarkHeader) Color(0xFFFFD700) else Color(0xFFB45309),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Compact Fire Streak Pill (opens Achievement Locker)
                        if (enabledWidgets.contains(AppPreferences.WIDGET_STREAK)) {
                            val streakTint = if (isDarkHeader) Color(0xFFFFD700) else Color(0xFFB45309)
                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.playAchievementChime()
                                    showAchievementsSheet = true
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isDarkHeader) Color(0xFF131720) else Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDarkHeader) Color(0xFFFFD700).copy(alpha = 0.32f) else Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.testTag("home_streak_achievement_banner")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Daily Login Streak",
                                        tint = streakTint,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${streakDays}d",
                                        color = streakTint,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Notification Bell with unread badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDarkHeader) Color(0xFF131720) else Color(0xFFF8FAFC)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isDarkHeader) Color.White.copy(alpha = 0.10f) else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                                .clickable { showNotificationsSheet = true }
                                .testTag("dashboard_alerts_bell"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Smart Alerts",
                                tint = if (unreadAlertsCount > 0) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                            if (unreadAlertsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(StateRed)
                                )
                            }
                        }

                        // Worldwide Currency Picker Chip (36+ countries)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isDarkHeader) Color(0xFF131720) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDarkHeader) Color.White.copy(alpha = 0.10f) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .clickable { showCurrencyPicker = true }
                                .testTag("dashboard_currency_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = currentCurrency.flag, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = viewModel.activeSymbol(),
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    fontSize = 11.5.sp,
                                    maxLines = 1
                                )
                                if (!isCompactHeader) {
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = currentCurrency.code,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 1b. Sleek Non-Intrusive Smart Sample Portfolio Pill (only while sample data is active) ---
        if (!isDemoDataPermanentlyDisabled) {
            item {
                val isDarkPill = LocalIsDarkTheme.current
                val pillBg = if (isDarkPill) Color(0xFF131720) else Color(0xFFF8FAFC)
                val pillBorder = if (isDarkPill) Color(0xFF00FFCC).copy(alpha = 0.25f) else Color(0xFFCBD5E1)
                val pillActionColor = if (isDarkPill) Color(0xFF00FFCC) else Color(0xFF00695C)

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = pillBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, pillBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_sample_vault_pill")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨ Sample Portfolio Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            onClick = { viewModel.startFreshPersonalVault() },
                            shape = RoundedCornerShape(999.dp),
                            color = pillActionColor.copy(alpha = if (isDarkPill) 0.14f else 0.10f),
                            modifier = Modifier.testTag("dashboard_start_fresh_button")
                        ) {
                            Text(
                                text = "Start Fresh →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = pillActionColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Reference UI Blue Mesh Balance Card with Wave Chart (directly below top bar) ---
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.Transparent,
                borderWidth = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CardGradientBlueStart, CardGradientBlueEnd, accentColor)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = AppStrings.totalNetWorth(lang),
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = if (incomeTotal >= expenseTotal) "+Surplus" else "-Deficit",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val activeSym = viewModel.activeSymbol()
                            val formatFromDisplay: (Double) -> String = { displayVal ->
                                val nf = NumberFormat.getNumberInstance(Locale.US).apply {
                                    minimumFractionDigits = 2
                                    maximumFractionDigits = 2
                                }
                                "$activeSym${nf.format(displayVal)}"
                            }
                            RollingCounterText(
                                targetValue = viewModel.fromUsd(totalBalance),
                                formatText = formatFromDisplay,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${AppStrings.spendable(lang)}: ${viewModel.formatAmount(spendableBalance)}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        NetWorthSplineChart(
                            points = chartPoints,
                            formatAmount = { viewModel.formatAmount(it) },
                            lineColor = NeonMint,
                            height = 104.dp,
                            latestDeductedExpenseUsd = latestDeductedExpenseUsd
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val activeSymRow = viewModel.activeSymbol()
                        val formatDisplayRow: (Double) -> String = { displayVal ->
                            val nf = NumberFormat.getNumberInstance(Locale.US).apply {
                                minimumFractionDigits = 2
                                maximumFractionDigits = 2
                            }
                            "$activeSymRow${nf.format(displayVal)}"
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = StateGreen, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = AppStrings.income(lang), color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                                    RollingCounterText(
                                        targetValue = viewModel.fromUsd(incomeTotal),
                                        formatText = formatDisplayRow,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = StateRed, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = AppStrings.expense(lang), color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                                    RollingCounterText(
                                        targetValue = viewModel.fromUsd(expenseTotal),
                                        formatText = formatDisplayRow,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Quick Action Buttons: Transfer, Add Expense, Reports (PDF/CSV) ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.openTransferDialog() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("dashboard_transfer_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.transfer(lang), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.openQuickEntry(TransactionType.EXPENSE) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("dashboard_quick_expense_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.addExpense(lang), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }

                Button(
                    onClick = { showReportsSheet = true },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("dashboard_reports_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = "Reports & PDF Export", tint = accentColor, modifier = Modifier.size(20.dp))
                }
            }
        }

        // --- 5. Editable Quick 1-Tap Presets with Pencil Icon & Auto-Symbol ---
        if (enabledWidgets.contains(AppPreferences.WIDGET_PRESETS)) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.quickPresets(lang),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "+ Add Preset",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { creatingNewPreset = true }
                                .testTag("add_preset_button")
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                modifier = Modifier.clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.logFromPreset(
                                        preset = preset,
                                        availableAccounts = accounts,
                                        availableCategories = uiState.categories
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${preset.emoji} ${preset.title}",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = viewModel.formatAmount(preset.amountUsd),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = accentColor
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    // Pencil Edit Button on every Preset!
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(accentColor.copy(alpha = 0.12f))
                                            .clickable { editingPreset = preset }
                                            .testTag("edit_preset_${preset.id}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit ${preset.title} preset",
                                            tint = accentColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. Connected Accounts & Cards Carousel ---
        if (enabledWidgets.contains(AppPreferences.WIDGET_ACCOUNTS)) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.connectedAccounts(lang),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "View All (${accounts.size})",
                            color = accentColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateToAccounts() }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_connected_accounts_lazy_row"),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        itemsIndexed(accounts, key = { _, acc -> acc.id }) { index, account ->
                            val finish = remember(account.id, vaultVisualVersion, index, accounts) {
                                MetalCardVaultStore.getFinish(context, account, index)
                            }
                            val cardCurrency = remember(account.id, vaultVisualVersion, currentCurrency) {
                                MetalCardVaultStore.getCardCurrencyOverride(context, account.id) ?: currentCurrency
                            }
                            val effectiveSym = if (cardCurrency == currentCurrency) customSymbol else ""
                            val formattedBal = MetalCardVaultCalculator.formatUsdInCurrency(
                                usdAmount = account.balance,
                                currency = cardCurrency,
                                customSymbol = effectiveSym
                            )
                            Mini3DNeonMetalCard(
                                account = account,
                                cardCurrency = cardCurrency,
                                finish = finish,
                                formattedBalance = formattedBal,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onOpenAccountInVault(account.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- 7. Circular Donut Spending Chart Card ---
        if (enabledWidgets.contains(AppPreferences.WIDGET_DONUT)) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Account Spendings",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Budgets",
                                color = accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onNavigateToBudgets() }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            SpendingDonutChart(
                                slices = donutSlices,
                                totalText = viewModel.formatAmount(expenseTotal),
                                chartSize = 142.dp,
                                strokeWidth = 18.dp
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                donutSlices.forEach { slice ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(slice.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = slice.label,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = viewModel.formatAmount(slice.value),
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
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

        // --- 7B. Symmetrical 2-Column Smart Categories & Auto-Created Sections Grid ---
        if (uiState.categories.isNotEmpty()) {
            item {
                val categoryTotals = remember(transactions) {
                    transactions.groupBy { it.transaction.categoryId }
                        .mapValues { entry -> entry.value.sumOf { it.transaction.amount } }
                }
                val categoryCounts = remember(transactions) {
                    transactions.groupBy { it.transaction.categoryId }
                        .mapValues { entry -> entry.value.size }
                }
                val orderedCategories = remember(uiState.categories, categoryCounts) {
                    uiState.categories
                        .filter { it.name != "Transfer" }
                        .sortedWith(
                            compareByDescending<com.example.data.model.CategoryEntity> { it.id > 10L }
                                .thenByDescending { categoryCounts[it.id] ?: 0 }
                        )
                        .take(8)
                }
                val activeSymGrid = viewModel.activeSymbol()
                val formatGridAmount: (Double) -> String = { displayVal ->
                    val nf = NumberFormat.getNumberInstance(Locale.US).apply {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 2
                    }
                    "$activeSymGrid${nf.format(displayVal)}"
                }

                val isDarkSection = LocalIsDarkTheme.current
                val sectionAccent = if (isDarkSection) NeonMint else accentColor
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = sectionAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Smart Sections & Categories",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "${uiState.categories.size} active",
                            fontSize = 12.sp,
                            color = sectionAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    orderedCategories.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { cat ->
                                val catColor = try {
                                    Color(android.graphics.Color.parseColor(cat.colorHex))
                                } catch (_: Exception) {
                                    sectionAccent
                                }
                                val totalAmtUsd = categoryTotals[cat.id] ?: 0.0
                                val txCount = categoryCounts[cat.id] ?: 0
                                val isAutoSection = cat.id > 10L

                                GlassCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("section_grid_card_${cat.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    onClick = { viewModel.searchQuery.value = cat.name }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(catColor.copy(alpha = 0.16f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = CategoryIconHelper.iconFor(cat.iconName),
                                                    contentDescription = cat.name,
                                                    tint = catColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            if (isAutoSection) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = sectionAccent.copy(alpha = 0.16f)
                                                ) {
                                                    Text(
                                                        text = "NEW",
                                                        color = sectionAccent,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = "$txCount tx",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                            text = cat.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        RollingCounterText(
                                            targetValue = viewModel.fromUsd(totalAmtUsd),
                                            formatText = formatGridAmount,
                                            color = catColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // --- 8. Category Spending Trends Data Visualization ---
        if (enabledWidgets.contains(AppPreferences.WIDGET_TRENDS)) {
            item {
                CategorySpendingTrendsChart(
                    trendItems = categoryTrendItems,
                    formattedTotalSpend = viewModel.formatAmount(expenseTotal),
                    formatAmount = { viewModel.formatAmount(it) }
                )
            }
        }

        // --- 9. Upcoming Bills & Subscriptions (+ Add Bill) ---
        if (enabledWidgets.contains(AppPreferences.WIDGET_BILLS)) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Upcoming Bills & Subscriptions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "+ Add Bill",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showAddBillDialog = true }
                        )
                    }
                    if (bills.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            bills.forEach { bill ->
                                SubscriptionMiniCard(
                                    bill = bill,
                                    formattedAmount = viewModel.formatAmount(bill.amount),
                                    onTogglePaid = { viewModel.toggleBillPaid(bill.id, bill.isPaid) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 10. Recent Transactions Stream ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.recentTransactions(lang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${filteredTransactions.size} logs (tap to edit)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Search transactions, notes, categories...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = accentColor) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "EXPENSE", "INCOME", "TRANSFER").forEach { filterKey ->
                    val isSelected = typeFilter == filterKey
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { viewModel.transactionTypeFilter.value = filterKey }
                    ) {
                        Text(
                            text = filterKey.lowercase().replaceFirstChar { it.uppercase() },
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        items(filteredTransactions, key = { it.transaction.id }) { item ->
            TransactionRowItem(
                item = item,
                formattedAmount = viewModel.formatAmount(item.transaction.amount),
                onClick = { selectedTransactionForDetail = item },
                onDelete = { viewModel.deleteTransaction(item.transaction) }
            )
        }

        item { Spacer(modifier = Modifier.height(96.dp)) }
        }

        BannerAdSlot(
            placementId = "home_dashboard_banner",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SubscriptionMiniCard(
    bill: BillReminderEntity,
    formattedAmount: String,
    onTogglePaid: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(bill.dueDate))
    val accent = LocalAccentColor.current
    GlassCard(
        modifier = Modifier.width(150.dp),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(bill.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(formattedAmount, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = accent)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Due $dateStr", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (bill.isPaid) StateGreen.copy(alpha = 0.15f) else Color(0xFFB45309).copy(alpha = 0.14f),
                    modifier = Modifier
                        .testTag("bill_toggle_paid_${bill.id}")
                        .clickable { onTogglePaid() }
                ) {
                    Text(
                        text = if (bill.isPaid) "Paid" else "Pay",
                        color = if (bill.isPaid) StateGreen else Color(0xFFB45309),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    item: TransactionWithDetails,
    formattedAmount: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val tx = item.transaction
    val category = item.category
    val isIncome = tx.type == TransactionType.INCOME
    val isTransfer = tx.type == TransactionType.TRANSFER
    val accent = LocalAccentColor.current

    val categoryColor = try { Color(android.graphics.Color.parseColor(category?.colorHex ?: "#00796B")) } catch (_: Exception) { accent }
    val icon: ImageVector = CategoryIconHelper.iconFor(category?.iconName)
    val dateFormatted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(tx.timestamp))

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = categoryColor, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isTransfer) "Transfer: ${item.account?.name ?: "Acc"} → ${item.toAccount?.name ?: "Acc"}" else (category?.name ?: "General"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (tx.isRecurring) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = accent.copy(alpha = 0.14f)) {
                                Text("↻", fontSize = 10.sp, color = accent, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                            }
                        }
                    }
                    Text(
                        text = tx.note.ifBlank { dateFormatted } + if (tx.receiptUri != null) " • 📎Receipt" else "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isIncome) "+$formattedAmount" else "-$formattedAmount",
                    color = if (isIncome) StateGreen else if (isTransfer) accent else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete transaction",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
