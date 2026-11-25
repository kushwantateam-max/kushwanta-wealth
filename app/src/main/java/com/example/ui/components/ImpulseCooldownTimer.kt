package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.SavingsGoalEntity
import com.example.domain.util.SoundEffectManager
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.theme.StateYellow
import com.example.ui.viewmodel.KoshvantaViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// -----------------------------------------------------------------------------
// Self-Contained Domain Calculations & Local Persistence for 24h Impulse Timer
// -----------------------------------------------------------------------------

data class ImpulseSavingsRateInfo(
    val dailyRateUsd: Double,
    val isLiveLedgerRate: Boolean,
    val sourceLabel: String
)

data class ActiveImpulseCooldown(
    val itemName: String,
    val priceUsd: Double,
    val durationHours: Int,
    val startTimeMillis: Long,
    val endTimeMillis: Long
)

data class ImpulseCooldownStats(
    val totalSavedUsd: Double = 0.0,
    val rescuedCount: Int = 0,
    val lastRescuedName: String = ""
)

object ImpulseCooldownCalculator {

    fun computeDailySavingsRateUsd(
        monthlyIncomeUsd: Double,
        monthlyExpenseUsd: Double,
        monthlyBudgetUsd: Double,
        fallbackDailyLimitUsd: Double = 25.0
    ): ImpulseSavingsRateInfo {
        val surplus = monthlyIncomeUsd - monthlyExpenseUsd
        return when {
            monthlyIncomeUsd > 0.0 && surplus > 0.0 -> ImpulseSavingsRateInfo(
                dailyRateUsd = (surplus / 30.0).coerceAtLeast(1.0),
                isLiveLedgerRate = true,
                sourceLabel = "Based on your monthly net savings ÷ 30"
            )
            monthlyBudgetUsd > 0.0 -> ImpulseSavingsRateInfo(
                dailyRateUsd = (monthlyBudgetUsd / 30.0).coerceAtLeast(1.0),
                isLiveLedgerRate = false,
                sourceLabel = "Estimated from monthly budget ÷ 30"
            )
            else -> ImpulseSavingsRateInfo(
                dailyRateUsd = fallbackDailyLimitUsd.coerceAtLeast(10.0),
                isLiveLedgerRate = false,
                sourceLabel = "Baseline daily savings guardrail"
            )
        }
    }

    fun computeDaysOfSavings(itemPriceUsd: Double, dailySavingsRateUsd: Double): Double {
        if (itemPriceUsd <= 0.0 || dailySavingsRateUsd <= 0.0) return 0.0
        return itemPriceUsd / dailySavingsRateUsd
    }

    fun formatDaysCostLabel(daysCost: Double): String {
        return when {
            daysCost <= 0.0 -> "0.0 days"
            daysCost < 1.0 -> {
                val hrs = (daysCost * 24.0).coerceAtLeast(1.0)
                String.format(Locale.US, "%.1f hrs (%.2f days)", hrs, daysCost)
            }
            daysCost < 10.0 -> String.format(Locale.US, "%.1f days", daysCost)
            else -> String.format(Locale.US, "%.0f days", daysCost)
        }
    }

    fun formatCountdownHms(remainingMillis: Long): String {
        val clampedSec = (remainingMillis.coerceAtLeast(0L)) / 1000L
        val hours = clampedSec / 3600L
        val mins = (clampedSec % 3600L) / 60L
        val secs = clampedSec % 60L
        return String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)
    }

    fun computeProgressFraction(startMillis: Long, endMillis: Long, nowMillis: Long): Float {
        val total = (endMillis - startMillis).coerceAtLeast(1L).toDouble()
        val elapsed = (nowMillis - startMillis).coerceIn(0L, endMillis - startMillis).toDouble()
        return (elapsed / total).toFloat().coerceIn(0f, 1f)
    }
}

object ImpulseCooldownStore {
    private const val PREF_NAME = "koshvanta_impulse_timer_prefs"
    private const val KEY_ACTIVE_NAME = "active_item_name"
    private const val KEY_ACTIVE_PRICE_USD = "active_price_usd"
    private const val KEY_ACTIVE_HOURS = "active_duration_hours"
    private const val KEY_ACTIVE_START = "active_start_ms"
    private const val KEY_ACTIVE_END = "active_end_ms"
    private const val KEY_TOTAL_SAVED_USD = "total_saved_usd"
    private const val KEY_RESCUED_COUNT = "rescued_count"
    private const val KEY_LAST_RESCUED_NAME = "last_rescued_name"

    fun loadActive(context: Context): ActiveImpulseCooldown? = runCatching {
        val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_ACTIVE_NAME, null).orEmpty()
        val start = prefs.getLong(KEY_ACTIVE_START, 0L)
        val end = prefs.getLong(KEY_ACTIVE_END, 0L)
        val priceUsd = java.lang.Double.longBitsToDouble(prefs.getLong(KEY_ACTIVE_PRICE_USD, 0L))
        val hours = prefs.getInt(KEY_ACTIVE_HOURS, 24)
        if (name.isBlank() || start <= 0L || end <= start || priceUsd <= 0.0) {
            null
        } else {
            ActiveImpulseCooldown(
                itemName = name,
                priceUsd = priceUsd,
                durationHours = hours,
                startTimeMillis = start,
                endTimeMillis = end
            )
        }
    }.getOrNull()

    fun saveActive(context: Context, item: ActiveImpulseCooldown) {
        runCatching {
            val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_ACTIVE_NAME, item.itemName)
                .putLong(KEY_ACTIVE_PRICE_USD, java.lang.Double.doubleToRawLongBits(item.priceUsd))
                .putInt(KEY_ACTIVE_HOURS, item.durationHours)
                .putLong(KEY_ACTIVE_START, item.startTimeMillis)
                .putLong(KEY_ACTIVE_END, item.endTimeMillis)
                .apply()
        }
    }

    fun clearActive(context: Context) {
        runCatching {
            val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .remove(KEY_ACTIVE_NAME)
                .remove(KEY_ACTIVE_PRICE_USD)
                .remove(KEY_ACTIVE_HOURS)
                .remove(KEY_ACTIVE_START)
                .remove(KEY_ACTIVE_END)
                .apply()
        }
    }

    fun loadStats(context: Context): ImpulseCooldownStats = runCatching {
        val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val total = java.lang.Double.longBitsToDouble(prefs.getLong(KEY_TOTAL_SAVED_USD, 0L))
        val count = prefs.getInt(KEY_RESCUED_COUNT, 0)
        val last = prefs.getString(KEY_LAST_RESCUED_NAME, null).orEmpty()
        ImpulseCooldownStats(
            totalSavedUsd = if (total.isFinite() && total >= 0.0) total else 0.0,
            rescuedCount = count.coerceAtLeast(0),
            lastRescuedName = last
        )
    }.getOrDefault(ImpulseCooldownStats())

    fun recordSaved(context: Context, itemName: String, priceUsd: Double): ImpulseCooldownStats {
        val current = loadStats(context)
        val updated = ImpulseCooldownStats(
            totalSavedUsd = current.totalSavedUsd + priceUsd.coerceAtLeast(0.0),
            rescuedCount = current.rescuedCount + 1,
            lastRescuedName = itemName
        )
        runCatching {
            val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putLong(KEY_TOTAL_SAVED_USD, java.lang.Double.doubleToRawLongBits(updated.totalSavedUsd))
                .putInt(KEY_RESCUED_COUNT, updated.rescuedCount)
                .putString(KEY_LAST_RESCUED_NAME, updated.lastRescuedName)
                .apply()
        }
        clearActive(context)
        return updated
    }
}

// -----------------------------------------------------------------------------
// 1. Symmetric Dark Neon Glassmorphic Entry Card for Tools Hub Grid
// -----------------------------------------------------------------------------

@Composable
fun ImpulseCooldownHubCard(
    viewModel: KoshvantaViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val themeAccent = LocalAccentColor.current
    val hubAccent = if (isDark) NeonMint else themeAccent
    val hubBgColors = if (isDark) {
        listOf(DarkSurfaceElevated, DarkCanvasBg)
    } else {
        listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))
    }
    val titleColor = if (isDark) DarkTextPrimary else Color(0xFF0F172A)
    val subtitleColor = if (isDark) DarkTextSecondary else Color(0xFF475569)

    val active = remember { mutableStateOf(ImpulseCooldownStore.loadActive(context)) }
    val stats = remember { mutableStateOf(ImpulseCooldownStore.loadStats(context)) }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(active.value) {
        while (active.value != null) {
            nowMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val currentActive = active.value
    val remainingMillis = if (currentActive != null) (currentActive.endTimeMillis - nowMillis).coerceAtLeast(0L) else 0L
    val isReadyToClaim = currentActive != null && remainingMillis <= 0L

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isDark) DarkCanvasBg else Color(0xFFFFFFFF),
        border = if (isDark) {
            BorderStroke(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(NeonMint.copy(alpha = 0.78f), ElectricCyan.copy(alpha = 0.35f))
                )
            )
        } else {
            BorderStroke(1.dp, Color(0xFFE2E8F0))
        },
        shadowElevation = if (isDark) 4.dp else 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp)
            .testTag("impulse_timer_hub_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = hubBgColors))
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(hubAccent.copy(alpha = if (isDark) 0.16f else 0.12f))
                        .border(1.dp, hubAccent.copy(alpha = if (isDark) 0.45f else 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = "24-Hour Impulse Buying Cool-Down Timer",
                        tint = hubAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = hubAccent.copy(alpha = if (isDark) 0.16f else 0.12f)
                ) {
                    Text(
                        text = if (isReadyToClaim) "Claim" else if (currentActive != null) "Live" else "24H/48H",
                        color = hubAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = "Impulse Cool-Down",
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when {
                        currentActive != null && isReadyToClaim ->
                            "${currentActive.itemName}: Claim ${viewModel.formatAmount(currentActive.priceUsd)}!"
                        currentActive != null ->
                            "${currentActive.itemName} • ${ImpulseCooldownCalculator.formatCountdownHms(remainingMillis)}"
                        stats.value.rescuedCount > 0 ->
                            "Saved ${viewModel.formatAmount(stats.value.totalSavedUsd)} (${stats.value.rescuedCount} skips)"
                        else ->
                            "24h/48h pause timer & days-cost guard"
                    },
                    color = subtitleColor,
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. Full Interactive 24-Hour Impulse Buying Cool-Down Timer Screen
// -----------------------------------------------------------------------------

@Composable
fun ToolImpulseCooldownTimer(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    totalIncomeUsd: Double,
    totalExpenseUsd: Double,
    totalMonthlyBudgetUsd: Double
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = LocalIsDarkTheme.current
    val themeAccent = LocalAccentColor.current
    val accent = if (isDark) NeonMint else themeAccent
    val arcSecondary = if (isDark) ElectricCyan else accent.copy(alpha = 0.72f)
    val cardBg = if (isDark) DarkCanvasBg else Color(0xFFFFFFFF)
    val elevatedBg = if (isDark) DarkSurfaceElevated else Color(0xFFF8FAFC)
    val bannerBorder = if (isDark) NeonMint.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
    val mainCardBorder = if (isDark) NeonMint.copy(alpha = 0.4f) else Color(0xFFE2E8F0)
    val titleColor = if (isDark) DarkTextPrimary else Color(0xFF0F172A)
    val subtitleColor = if (isDark) DarkTextSecondary else Color(0xFF475569)
    val ringTrackColor = if (isDark) Color(0xFF1A2230) else Color(0xFFE2E8F0)
    val celebrationBg = if (isDark) Color(0xFF06231E) else Color(0xFFF0FDF9)
    val ctaContentColor = if (isDark) DarkCanvasBg else Color.White
    val errColor = if (isDark) Color(0xFFFF6B6B) else StateRed
    val dailyLimitFallback by viewModel.dailySpendingLimitUsd.collectAsState()

    var activeCooldown by remember { mutableStateOf(ImpulseCooldownStore.loadActive(context)) }
    var stats by remember { mutableStateOf(ImpulseCooldownStore.loadStats(context)) }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var itemNameInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var selectedHours by remember { mutableStateOf(24) }
    var validationError by remember { mutableStateOf<String?>(null) }

    var selectedGoalId by remember(savingsGoals) {
        mutableStateOf(savingsGoals.firstOrNull()?.id)
    }

    // Celebration overlay animation state
    var celebrationBannerText by remember { mutableStateOf<String?>(null) }
    val celebrationProgress = remember { Animatable(0f) }

    LaunchedEffect(activeCooldown) {
        while (activeCooldown != null) {
            nowMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val savingsRateInfo = remember(totalIncomeUsd, totalExpenseUsd, totalMonthlyBudgetUsd, dailyLimitFallback) {
        ImpulseCooldownCalculator.computeDailySavingsRateUsd(
            monthlyIncomeUsd = totalIncomeUsd,
            monthlyExpenseUsd = totalExpenseUsd,
            monthlyBudgetUsd = totalMonthlyBudgetUsd,
            fallbackDailyLimitUsd = dailyLimitFallback
        )
    }

    val enteredPriceDisplay = priceInput.toDoubleOrNull() ?: 0.0
    val enteredPriceUsd = if (enteredPriceDisplay > 0.0) viewModel.toUsd(enteredPriceDisplay) else 0.0

    val activeItem = activeCooldown
    val displayPriceUsd = activeItem?.priceUsd ?: enteredPriceUsd
    val daysOfSavingsCost = remember(displayPriceUsd, savingsRateInfo.dailyRateUsd) {
        ImpulseCooldownCalculator.computeDaysOfSavings(displayPriceUsd, savingsRateInfo.dailyRateUsd)
    }

    val remainingMillis = if (activeItem != null) (activeItem.endTimeMillis - nowMillis).coerceAtLeast(0L) else 0L
    val isTimerComplete = activeItem != null && remainingMillis <= 0L
    val rawProgress = if (activeItem != null) {
        ImpulseCooldownCalculator.computeProgressFraction(activeItem.startTimeMillis, activeItem.endTimeMillis, nowMillis)
    } else 0f

    val animatedArcProgress by animateFloatAsState(
        targetValue = if (activeItem == null) 0f else if (isTimerComplete) 1f else rawProgress,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "cooldown_arc_progress"
    )

    val animatedTotalSavedDisplay by animateFloatAsState(
        targetValue = viewModel.fromUsd(stats.totalSavedUsd).toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "impulse_total_saved"
    )

    val pulseTransition = rememberInfiniteTransition(label = "neon_pulse")
    val neonGlowAlpha by pulseTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_glow_alpha"
    )

    LaunchedEffect(celebrationBannerText) {
        if (celebrationBannerText != null) {
            celebrationProgress.snapTo(0f)
            celebrationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
            )
            delay(2500L)
            celebrationBannerText = null
        }
    }

    val claimSavedAction: () -> Unit = {
        val current = activeCooldown
        if (current != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            val targetGoal = savingsGoals.firstOrNull { it.id == selectedGoalId } ?: savingsGoals.firstOrNull()
            val defaultAccountId = accounts.firstOrNull()?.id ?: 1L

            if (targetGoal != null) {
                viewModel.depositToGoal(
                    goalId = targetGoal.id,
                    depositAmountUsd = current.priceUsd,
                    accountId = defaultAccountId
                )
            } else {
                val savedDisplay = viewModel.fromUsd(current.priceUsd).coerceAtLeast(1.0)
                viewModel.addSavingsGoal(
                    name = "Impulse Rescue Vault",
                    targetDisplay = (savedDisplay * 5.0).coerceAtLeast(500.0),
                    initialDisplay = savedDisplay,
                    colorHex = "#00FFCC"
                )
            }

            SoundEffectManager.playAchievementChime()
            stats = ImpulseCooldownStore.recordSaved(context, current.itemName, current.priceUsd)
            celebrationBannerText = "+${viewModel.formatAmount(current.priceUsd)} added to Savings for skipping '${current.itemName}'!"
            activeCooldown = null
            itemNameInput = ""
            priceInput = ""
            validationError = null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("impulse_cooldown_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Lifetime Rescued Impulse Savings Banner
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = cardBg,
                border = BorderStroke(if (isDark) 1.2.dp else 1.dp, bannerBorder),
                shadowElevation = if (isDark) 0.dp else 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(elevatedBg, cardBg)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "IMPULSE MONEY RESCUED",
                                color = accent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${viewModel.activeSymbol()}${String.format(Locale.US, "%,.2f", animatedTotalSavedDisplay)}",
                                color = titleColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.testTag("impulse_lifetime_saved_text")
                            )
                            Text(
                                text = if (stats.rescuedCount == 0) {
                                    "First smart pause starts right here"
                                } else {
                                    "${stats.rescuedCount} impulse buy(s) resisted • Last: ${stats.lastRescuedName}"
                                },
                                color = subtitleColor,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.14f))
                                .border(1.dp, accent.copy(alpha = 0.45f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = "Impulse Savings",
                                tint = accent,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Smooth Celebration Burst Card (visible right after clicking "I Saved It!")
        item {
            AnimatedVisibility(
                visible = celebrationBannerText != null,
                enter = fadeIn(tween(250)) + scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = celebrationBg,
                    border = BorderStroke(1.5.dp, accent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("impulse_celebration_banner")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(148.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val p = celebrationProgress.value
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val maxR = size.minDimension * 0.9f
                            if (p in 0.01f..0.99f) {
                                drawCircle(
                                    color = accent.copy(alpha = (1f - p) * 0.55f),
                                    radius = maxR * p,
                                    center = center,
                                    style = Stroke(width = 4.dp.toPx())
                                )
                                val particleColors = listOf(accent, arcSecondary, StateYellow, StateGreen)
                                val count = 16
                                for (i in 0 until count) {
                                    val angle = (2.0 * PI * i / count)
                                    val dist = maxR * (0.25f + 0.75f * p)
                                    val px = center.x + (cos(angle) * dist).toFloat()
                                    val py = center.y + (sin(angle) * dist).toFloat()
                                    drawCircle(
                                        color = particleColors[i % particleColors.size].copy(alpha = (1f - p * 0.85f)),
                                        radius = (5.dp.toPx()) * (1f - p * 0.4f),
                                        center = Offset(px, py)
                                    )
                                }
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = "Saved Celebration",
                                tint = accent,
                                modifier = Modifier.size(34.dp)
                            )
                            Text(
                                text = "WILLPOWER VICTORY!",
                                color = accent,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = celebrationBannerText.orEmpty(),
                                color = titleColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // 3. Main Cool-Down Ring & Active / Setup Controls
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = cardBg,
                border = BorderStroke(if (isDark) 1.25.dp else 1.dp, mainCardBorder),
                shadowElevation = if (isDark) 0.dp else 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Animated Circular Countdown Ring Canvas
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .testTag("impulse_countdown_ring"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeW = 14.dp.toPx()
                            val pad = strokeW + 8.dp.toPx()
                            val arcSize = Size(size.width - pad * 2f, size.height - pad * 2f)
                            val topLeft = Offset(pad, pad)

                            // Outer ambient glow
                            drawCircle(
                                color = accent.copy(
                                    alpha = if (activeItem != null) neonGlowAlpha * 0.16f else 0.08f
                                ),
                                radius = (size.minDimension / 2f) - 4.dp.toPx(),
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Track arc
                            drawArc(
                                color = ringTrackColor,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )

                            // Progress arc
                            val sweep = if (activeItem == null) {
                                18f
                            } else {
                                (360f * animatedArcProgress).coerceIn(6f, 360f)
                            }
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(accent, arcSecondary, accent)
                                ),
                                startAngle = -90f,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )

                            // Glowing indicator dot at arc tip
                            val tipAngleRad = ((-90f + sweep) * PI / 180.0)
                            val radius = arcSize.width / 2f
                            val cx = size.width / 2f + (radius * cos(tipAngleRad)).toFloat()
                            val cy = size.height / 2f + (radius * sin(tipAngleRad)).toFloat()
                            drawCircle(
                                color = accent.copy(alpha = 0.4f),
                                radius = strokeW * 0.85f,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = strokeW * 0.35f,
                                center = Offset(cx, cy)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = when {
                                    isTimerComplete -> "READY TO CLAIM"
                                    activeItem != null -> "${activeItem.durationHours}H COOL-DOWN"
                                    else -> "${selectedHours}H GUARD"
                                },
                                color = accent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when {
                                    activeItem == null -> "${selectedHours}:00:00"
                                    isTimerComplete -> "00:00:00"
                                    else -> ImpulseCooldownCalculator.formatCountdownHms(remainingMillis)
                                },
                                color = titleColor,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("impulse_countdown_text")
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when {
                                    activeItem != null -> "${activeItem.itemName} • ${viewModel.formatAmount(activeItem.priceUsd)}"
                                    enteredPriceUsd > 0.0 -> "${itemNameInput.ifBlank { "Impulse Item" }} • ${viewModel.formatAmount(enteredPriceUsd)}"
                                    else -> "Enter item & price below"
                                },
                                color = subtitleColor,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Days of Savings Insight Pill (Live calculated)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = elevatedBg,
                        border = BorderStroke(1.dp, if (isDark) accent.copy(alpha = 0.25f) else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("impulse_days_cost_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (displayPriceUsd > 0.0) {
                                    "This item costs ${ImpulseCooldownCalculator.formatDaysCostLabel(daysOfSavingsCost)} of your savings"
                                } else {
                                    "Enter a price to see how many days of savings it costs"
                                },
                                color = if (displayPriceUsd > 0.0) accent else subtitleColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${savingsRateInfo.sourceLabel} (${viewModel.formatAmount(savingsRateInfo.dailyRateUsd)}/day)",
                                color = subtitleColor,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (activeItem == null) {
                        // Setup Inputs: Item Name, Price, Quick Ideas, Duration Toggle (24h / 48h)
                        OutlinedTextField(
                            value = itemNameInput,
                            onValueChange = {
                                itemNameInput = it
                                validationError = null
                            },
                            label = { Text("What are you tempted to buy?") },
                            placeholder = { Text("e.g., Wireless Headphones, Sneakers") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accent,
                                unfocusedBorderColor = if (isDark) accent.copy(alpha = 0.35f) else Color(0xFFCBD5E1),
                                focusedLabelColor = accent,
                                unfocusedLabelColor = subtitleColor,
                                focusedTextColor = titleColor,
                                unfocusedTextColor = titleColor,
                                cursorColor = accent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("impulse_item_name_input")
                        )

                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { raw ->
                                if (raw.isEmpty() || raw.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                    priceInput = raw
                                    validationError = null
                                }
                            },
                            label = { Text("Item Price (${viewModel.activeSymbol()})") },
                            placeholder = { Text("0.00") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accent,
                                unfocusedBorderColor = if (isDark) accent.copy(alpha = 0.35f) else Color(0xFFCBD5E1),
                                focusedLabelColor = accent,
                                unfocusedLabelColor = subtitleColor,
                                focusedTextColor = titleColor,
                                unfocusedTextColor = titleColor,
                                cursorColor = accent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("impulse_item_price_input")
                        )

                        // Quick Preset Impulse Ideas
                        val quickIdeas = listOf(
                            "Sneakers" to "89",
                            "Headphones" to "149",
                            "Smartwatch" to "199",
                            "Online Sale" to "45"
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickIdeas.forEach { (label, defaultAmt) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = elevatedBg,
                                    border = BorderStroke(1.dp, if (isDark) accent.copy(alpha = 0.25f) else Color(0xFFE2E8F0)),
                                    modifier = Modifier.clickable {
                                        itemNameInput = label
                                        priceInput = defaultAmt
                                        validationError = null
                                    }
                                ) {
                                    Text(
                                        text = "$label (${viewModel.activeSymbol()}$defaultAmt)",
                                        color = titleColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        // 24h / 48h Duration Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(24, 48).forEach { hrs ->
                                val selected = selectedHours == hrs
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedHours = hrs },
                                    label = {
                                        Text(
                                            text = if (hrs == 24) "24 Hours (Standard)" else "48 Hours (Big Buy)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accent.copy(alpha = if (isDark) 0.18f else 0.12f),
                                        selectedLabelColor = accent,
                                        selectedLeadingIconColor = accent,
                                        containerColor = elevatedBg,
                                        labelColor = subtitleColor
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("impulse_duration_${hrs}h")
                                )
                            }
                        }

                        if (validationError != null) {
                            Text(
                                text = validationError.orEmpty(),
                                color = errColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                val cleanName = itemNameInput.trim().ifBlank { "Impulse Item" }
                                val parsedDisplay = priceInput.toDoubleOrNull() ?: 0.0
                                if (parsedDisplay <= 0.0) {
                                    SoundEffectManager.playErrorTone()
                                    validationError = "Enter a valid item price greater than 0."
                                } else {
                                    val now = System.currentTimeMillis()
                                    val durationMs = selectedHours * 3600_000L
                                    val created = ActiveImpulseCooldown(
                                        itemName = cleanName,
                                        priceUsd = viewModel.toUsd(parsedDisplay),
                                        durationHours = selectedHours,
                                        startTimeMillis = now,
                                        endTimeMillis = now + durationMs
                                    )
                                    ImpulseCooldownStore.saveActive(context, created)
                                    activeCooldown = created
                                    nowMillis = now
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    SoundEffectManager.playSuccessChime()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accent,
                                contentColor = ctaContentColor
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("impulse_start_button")
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start ${selectedHours}h Cool-Down Timer",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        // Active Timer or Completed Cool-Down Controls
                        if (savingsGoals.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Destination Savings Vault:",
                                    color = subtitleColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    savingsGoals.forEach { goal ->
                                        val isGoalSelected = selectedGoalId == goal.id
                                        FilterChip(
                                            selected = isGoalSelected,
                                            onClick = { selectedGoalId = goal.id },
                                            label = { Text(goal.name, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = accent.copy(alpha = if (isDark) 0.2f else 0.12f),
                                                selectedLabelColor = accent,
                                                containerColor = elevatedBg,
                                                labelColor = subtitleColor
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Tapping 'I Saved It!' will automatically create an 'Impulse Rescue Vault' in Savings.",
                                color = subtitleColor,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (isTimerComplete) {
                            Button(
                                onClick = claimSavedAction,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accent,
                                    contentColor = ctaContentColor
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("impulse_i_saved_it_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "I Saved It! (+${viewModel.formatAmount(activeItem.priceUsd)})",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            // Cool-down running: allow completing early or canceling cleanly
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        ImpulseCooldownStore.clearActive(context)
                                        activeCooldown = null
                                        SoundEffectManager.playDeleteSound()
                                    },
                                    border = BorderStroke(1.dp, subtitleColor.copy(alpha = 0.45f)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("impulse_cancel_button")
                                ) {
                                    Text("Cancel", color = subtitleColor, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        // Advance timer completion so the user sees & can press "I Saved It!"
                                        val completed = activeItem.copy(endTimeMillis = System.currentTimeMillis() - 1000L)
                                        ImpulseCooldownStore.saveActive(context, completed)
                                        activeCooldown = completed
                                        nowMillis = System.currentTimeMillis()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accent.copy(alpha = if (isDark) 0.16f else 0.12f),
                                        contentColor = accent
                                    ),
                                    border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .height(48.dp)
                                        .testTag("impulse_skip_wait_button")
                                ) {
                                    Text("Urge Gone • Claim", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}
