package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import com.example.ads.AdManager
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.KoshvantaDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.domain.util.SoundEffectManager
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonMint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =============================================================================
// KUSHWANTA MIND ARENA HUB & GAME 1: CYBER WEALTH 2048 (ULTRA-LUXURY EDITION)
// Isolated modular brain & wealth reflex training suite.
// Zero gambling, zero spin wheels, zero payout claims, zero locked clutter.
// =============================================================================

private val ObsidianBg = DarkCanvasBg
private val ObsidianElevated = DarkSurfaceElevated
private val ArenaCyan = NeonMint
private val ArenaGold = Color(0xFFFFD700)
private val ArenaAmber = Color(0xFFF59E0B)

// -----------------------------------------------------------------------------
// 1. Arena Progression, Streak & SharedPreferences Store
// -----------------------------------------------------------------------------

data class ArenaLevelInfo(
    val level: Int,
    val title: String,
    val totalXp: Long,
    val levelFloorXp: Long,
    val nextLevelXp: Long,
    val progressRatio: Float,
    val isMaxLevel: Boolean
)

data class ArenaSnapshot(
    val totalXp: Long,
    val streakDays: Int,
    val bestScore2048: Long,
    val bestTowerFloors: Int = 0,
    val bestRunnerScore: Long = 0L,
    val bestHexaScore: Long = 0L,
    val bestQuantumLevel: Int = 0,
    val bestMathDuelScore: Long = 0L,
    val bestBrickBreakerScore: Long = 0L,
    val bestBrickBreakerWave: Int = 0,
    val bestChronoRound: Int = 0,
    val bestChronoScore: Long = 0L,
    val bestOrbitScore: Long = 0L,
    val bestOrbitWave: Int = 0,
    val bestCitadelScore: Long = 0L,
    val bestCitadelTier: Int = 0,
    val effectiveStreakDays: Int = 0,
    val recordedNoSpendDays: Int = 0,
    val hasCompletedGoalOrBudget: Boolean = false,
    val isGame4Unlocked: Boolean = false,
    val shouldShowGame4UnlockFlash: Boolean = false,
    val isGame5Unlocked: Boolean = false,
    val shouldShowGame5UnlockFlash: Boolean = false,
    val isGame6Unlocked: Boolean = false,
    val shouldShowGame6UnlockFlash: Boolean = false,
    val isGame7Unlocked: Boolean = false,
    val shouldShowGame7UnlockFlash: Boolean = false,
    val isGame8Unlocked: Boolean = false,
    val shouldShowGame8UnlockFlash: Boolean = false,
    val isGame9Unlocked: Boolean = false,
    val shouldShowGame9UnlockFlash: Boolean = false,
    val isGame10Unlocked: Boolean = false,
    val shouldShowGame10UnlockFlash: Boolean = false,
    val isGrandmasterArchitectUnlocked: Boolean = false,
    val gamesPlayed: Int,
    val soundAndHapticEnabled: Boolean,
    val levelInfo: ArenaLevelInfo
)

object ArenaLevelCalculator {
    private val levelThresholds: List<Pair<Long, String>> = listOf(
        0L to "Rookie",
        150L to "Apprentice",
        400L to "Analyst",
        800L to "Strategist",
        1400L to "Quant Ace",
        2200L to "Vault Keeper",
        3300L to "Portfolio Baron",
        4800L to "Sovereign Mogul",
        6800L to "Platinum Titan",
        9500L to "Diamond Tycoon"
    )

    fun evaluate(totalXp: Long): ArenaLevelInfo {
        val safeXp = totalXp.coerceAtLeast(0L)
        var currentIndex = 0
        for (idx in levelThresholds.indices) {
            if (safeXp >= levelThresholds[idx].first) {
                currentIndex = idx
            } else {
                break
            }
        }
        val levelNum = currentIndex + 1
        val floorXp = levelThresholds[currentIndex].first
        val title = levelThresholds[currentIndex].second
        val isMax = currentIndex == levelThresholds.lastIndex
        val nextXp = if (isMax) floorXp else levelThresholds[currentIndex + 1].first
        val ratio = if (isMax) {
            1f
        } else {
            val span = (nextXp - floorXp).coerceAtLeast(1L)
            ((safeXp - floorXp).toFloat() / span.toFloat()).coerceIn(0f, 1f)
        }
        return ArenaLevelInfo(
            level = levelNum,
            title = title,
            totalXp = safeXp,
            levelFloorXp = floorXp,
            nextLevelXp = nextXp,
            progressRatio = ratio,
            isMaxLevel = isMax
        )
    }
}

object ArenaProgressStore {
    private const val PREFS_NAME = "kushwanta_mind_arena_prefs"
    private const val APP_PREFS_NAME = "koshvanta_prefs"
    private const val APP_KEY_STREAK_COUNT = "streak_count"
    private const val APP_KEY_BEST_STREAK = "best_streak"
    private const val APP_KEY_LOGIN_DATES = "active_login_dates"
    private const val APP_KEY_NO_SPEND_DAYS = "no_spend_days_count"
    private const val APP_KEY_GOAL_COMPLETED = "savings_goal_completed"
    private const val APP_KEY_BUDGET_MILESTONE = "budget_milestone_achieved"
    private const val APP_KEY_GOAL_OR_BUDGET = "has_completed_goal_or_budget"
    private const val KEY_TOTAL_XP = "arena_total_xp"
    private const val KEY_STREAK_DAYS = "arena_streak_days"
    private const val KEY_LAST_PLAY_DAY = "arena_last_play_day"
    private const val KEY_BEST_2048 = "arena_2048_best_score"
    private const val KEY_BEST_TOWER_FLOORS = "arena_tower_best_floors"
    private const val KEY_BEST_RUNNER_SCORE = "arena_runner_best_score"
    private const val KEY_BEST_HEXA_SCORE = "arena_hexa_best_score"
    private const val KEY_BEST_QUANTUM_LEVEL = "arena_quantum_best_level"
    private const val KEY_BEST_MATH_DUEL_SCORE = "arena_math_duel_best_score"
    private const val KEY_BEST_BREAKER_SCORE = "arena_breaker_best_score"
    private const val KEY_BEST_BREAKER_WAVE = "arena_breaker_best_wave"
    private const val KEY_BEST_CHRONO_SCORE = "arena_chrono_best_score"
    private const val KEY_BEST_CHRONO_ROUND = "arena_chrono_best_round"
    private const val KEY_BEST_ORBIT_SCORE = "arena_orbit_best_score"
    private const val KEY_BEST_ORBIT_WAVE = "arena_orbit_best_wave"
    private const val KEY_BEST_CITADEL_SCORE = "arena_citadel_best_score"
    private const val KEY_BEST_CITADEL_TIER = "arena_citadel_best_tier"
    private const val KEY_NO_SPEND_DAYS_CACHE = "arena_no_spend_days_count"
    private const val KEY_GOAL_OR_BUDGET_CACHE = "arena_goal_or_budget_achieved"
    private const val KEY_GAME4_UNLOCKED = "game4_unlocked"
    private const val KEY_GAME4_UNLOCK_FLASH_SEEN = "game4_unlock_flash_seen"
    private const val KEY_GAME5_UNLOCKED = "game5_unlocked"
    private const val KEY_GAME5_UNLOCK_FLASH_SEEN = "game5_unlock_flash_seen"
    private const val KEY_GAME6_UNLOCKED = "game6_unlocked"
    private const val KEY_GAME6_UNLOCK_FLASH_SEEN = "game6_unlock_flash_seen"
    private const val KEY_GAME7_UNLOCKED = "game7_unlocked"
    private const val KEY_GAME7_UNLOCK_FLASH_SEEN = "game7_unlock_flash_seen"
    private const val KEY_GAME8_UNLOCKED = "game8_unlocked"
    private const val KEY_GAME8_UNLOCK_FLASH_SEEN = "game8_unlock_flash_seen"
    private const val KEY_GAME9_UNLOCKED = "game9_unlocked"
    private const val KEY_GAME9_UNLOCK_FLASH_SEEN = "game9_unlock_flash_seen"
    private const val KEY_GAME10_UNLOCKED = "game10_unlocked"
    private const val KEY_GAME10_UNLOCK_FLASH_SEEN = "game10_unlock_flash_seen"
    private const val KEY_GRANDMASTER_ARCHITECT = "grandmaster_architect_unlocked"
    private const val APP_KEY_GRANDMASTER_ARCHITECT = "grandmaster_architect_unlocked"
    private const val APP_KEY_PROFILE_RANK = "arena_profile_rank"
    private const val KEY_GAMES_PLAYED = "arena_games_played"
    private const val KEY_FEEDBACK_ENABLED = "arena_feedback_enabled"
    private const val KEY_GAME5_LEVEL_STARS = "arena_game5_level_stars"
    private const val KEY_GAME8_ROUND_STARS = "arena_game8_round_stars"
    const val GAME4_REQUIRED_STREAK_DAYS = 3
    const val GAME5_REQUIRED_STREAK_DAYS = 7
    const val GAME6_REQUIRED_STREAK_DAYS = 10
    const val GAME7_REQUIRED_NO_SPEND_DAYS = 3
    const val GAME9_REQUIRED_STREAK_DAYS = 14
    const val GAME10_REQUIRED_STREAK_DAYS = 21
    const val GAME10_REQUIRED_ARENA_XP = 2_500L
    const val GRANDMASTER_REQUIRED_TIER = 3
    const val GRANDMASTER_REQUIRED_SCORE = 1_000L

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun appPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)

    fun countRecordedNoSpendDays(
        transactions: List<TransactionEntity>,
        activeLoginDates: Set<String> = emptySet(),
        nowMillis: Long = System.currentTimeMillis()
    ): Int {
        if (transactions.isEmpty() && activeLoginDates.isEmpty()) return 0
        val dayMs = 24L * 3_600_000L
        fun dayStart(ms: Long): Long {
            val cal = Calendar.getInstance().apply {
                timeInMillis = ms
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

        val todayStart = dayStart(nowMillis)
        val yesterdayStart = todayStart - dayMs
        val windowFloor = todayStart - 35L * dayMs

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val loginDayStarts = activeLoginDates.mapNotNull { str ->
            runCatching { df.parse(str)?.time?.let { dayStart(it) } }.getOrNull()
        }
        val earliestTxDay = transactions.minOfOrNull { dayStart(it.timestamp) }
        val earliestTracked = listOfNotNull(earliestTxDay, loginDayStarts.minOrNull()).minOrNull() ?: return 0

        val startDay = maxOf(windowFloor, earliestTracked)
        if (startDay > yesterdayStart) return 0

        val expenseDays = transactions
            .filter { it.type == TransactionType.EXPENSE && it.amount > 0.0 }
            .map { dayStart(it.timestamp) }
            .toSet()

        var goldenDays = 0
        var cursor = startDay
        while (cursor <= yesterdayStart) {
            if (cursor !in expenseDays) {
                goldenDays++
            }
            cursor += dayMs
        }
        return goldenDays
    }

    fun evaluateGoalOrBudgetMilestone(
        goals: List<SavingsGoalEntity>,
        budgets: List<BudgetEntity>,
        transactions: List<TransactionEntity>,
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean {
        if (goals.any { it.targetAmount > 0.0 && it.currentAmount >= it.targetAmount }) {
            return true
        }
        val activeBudgets = budgets.filter { it.monthlyLimit > 0.0 }
        if (activeBudgets.isEmpty()) return false

        val nowCal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val curYear = nowCal.get(Calendar.YEAR)
        val curMonth = nowCal.get(Calendar.MONTH) + 1

        val expenseByPeriodCategory = mutableMapOf<Pair<Triple<String, Int, Int>, Long>, Double>()
        val activityPeriods = mutableSetOf<Triple<String, Int, Int>>()
        val txCal = Calendar.getInstance()
        transactions.forEach { tx ->
            txCal.timeInMillis = tx.timestamp
            val period = Triple(tx.bookId, txCal.get(Calendar.YEAR), txCal.get(Calendar.MONTH) + 1)
            activityPeriods.add(period)
            if (tx.type == TransactionType.EXPENSE && tx.amount > 0.0) {
                val key = period to tx.categoryId
                expenseByPeriodCategory[key] = (expenseByPeriodCategory[key] ?: 0.0) + tx.amount
            }
        }

        val budgetsByPeriod = activeBudgets.groupBy {
            Triple(it.bookId, it.periodYear, it.periodMonth)
        }
        return budgetsByPeriod.any { (period, periodBudgets) ->
            val year = period.second
            val month = period.third
            val isCompletedPastMonth = year < curYear || (year == curYear && month < curMonth)
            val isCurrentMonthWithActivity = year == curYear && month == curMonth && period in activityPeriods
            (isCompletedPastMonth || isCurrentMonthWithActivity) && periodBudgets.all { budget ->
                val spent = expenseByPeriodCategory[period to budget.categoryId] ?: 0.0
                spent <= budget.monthlyLimit
            }
        }
    }

    suspend fun syncNoSpendDaysFromDb(
        context: Context,
        nowMillis: Long = System.currentTimeMillis()
    ): ArenaSnapshot = syncFinanceMilestonesFromDb(context, nowMillis)

    suspend fun syncFinanceMilestonesFromDb(
        context: Context,
        nowMillis: Long = System.currentTimeMillis()
    ): ArenaSnapshot {
        val dao = runCatching { KoshvantaDatabase.getInstance(context).dao() }.getOrNull()
        val transactions = runCatching { dao?.getAllTransactionsSync() }.getOrNull().orEmpty()
        val goals = runCatching { dao?.getAllGoalsSync() }.getOrNull().orEmpty()
        val budgets = runCatching { dao?.getAllBudgetsSync() }.getOrNull().orEmpty()

        val appSp = appPrefs(context)
        val loginDates = (appSp.getString(APP_KEY_LOGIN_DATES, "") ?: "")
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()
        val computedNoSpend = countRecordedNoSpendDays(transactions, loginDates, nowMillis)
        val goalOrBudgetAchieved = evaluateGoalOrBudgetMilestone(goals, budgets, transactions, nowMillis)
        syncNoSpendDays(context, computedNoSpend)
        return syncGoalOrBudgetAchieved(context, goalOrBudgetAchieved)
    }

    fun syncNoSpendDays(context: Context, noSpendDaysCount: Int): ArenaSnapshot {
        val sp = prefs(context)
        val safeDays = noSpendDaysCount.coerceAtLeast(0)
        val editor = sp.edit().putInt(KEY_NO_SPEND_DAYS_CACHE, safeDays)
        if (safeDays >= GAME7_REQUIRED_NO_SPEND_DAYS) {
            editor.putBoolean(KEY_GAME7_UNLOCKED, true)
        }
        editor.apply()
        return loadSnapshot(context)
    }

    fun syncGoalOrBudgetAchieved(context: Context, achieved: Boolean): ArenaSnapshot {
        val sp = prefs(context)
        val editor = sp.edit().putBoolean(KEY_GOAL_OR_BUDGET_CACHE, achieved)
        if (achieved) {
            editor.putBoolean(KEY_GAME8_UNLOCKED, true)
        }
        editor.apply()
        return loadSnapshot(context)
    }

    fun loadSnapshot(context: Context): ArenaSnapshot {
        val sp = prefs(context)
        val appSp = appPrefs(context)
        val xp = sp.getLong(KEY_TOTAL_XP, 0L)
        val streak = sp.getInt(KEY_STREAK_DAYS, 0)
        val appStreak = appSp.getInt(APP_KEY_STREAK_COUNT, 0)
        val appBestStreak = appSp.getInt(APP_KEY_BEST_STREAK, 0)
        val effectiveStreak = maxOf(streak, appStreak, appBestStreak)
        val recordedNoSpendDays = maxOf(
            sp.getInt(KEY_NO_SPEND_DAYS_CACHE, 0),
            appSp.getInt(APP_KEY_NO_SPEND_DAYS, 0)
        )
        val hasGoalOrBudget = sp.getBoolean(KEY_GOAL_OR_BUDGET_CACHE, false) ||
            appSp.getBoolean(APP_KEY_GOAL_COMPLETED, false) ||
            appSp.getBoolean(APP_KEY_BUDGET_MILESTONE, false) ||
            appSp.getBoolean(APP_KEY_GOAL_OR_BUDGET, false)

        var game4Unlocked = sp.getBoolean(KEY_GAME4_UNLOCKED, false)
        if (!game4Unlocked && effectiveStreak >= GAME4_REQUIRED_STREAK_DAYS) {
            sp.edit().putBoolean(KEY_GAME4_UNLOCKED, true).apply()
            game4Unlocked = true
        }
        val showUnlockFlash = game4Unlocked && !sp.getBoolean(KEY_GAME4_UNLOCK_FLASH_SEEN, false)

        var game5Unlocked = sp.getBoolean(KEY_GAME5_UNLOCKED, false)
        if (!game5Unlocked && effectiveStreak >= GAME5_REQUIRED_STREAK_DAYS) {
            sp.edit().putBoolean(KEY_GAME5_UNLOCKED, true).apply()
            game5Unlocked = true
        }
        val showGame5UnlockFlash = game5Unlocked && !sp.getBoolean(KEY_GAME5_UNLOCK_FLASH_SEEN, false)

        var game6Unlocked = sp.getBoolean(KEY_GAME6_UNLOCKED, false)
        if (!game6Unlocked && effectiveStreak >= GAME6_REQUIRED_STREAK_DAYS) {
            sp.edit().putBoolean(KEY_GAME6_UNLOCKED, true).apply()
            game6Unlocked = true
        }
        val showGame6UnlockFlash = game6Unlocked && !sp.getBoolean(KEY_GAME6_UNLOCK_FLASH_SEEN, false)

        var game7Unlocked = sp.getBoolean(KEY_GAME7_UNLOCKED, false)
        if (!game7Unlocked && recordedNoSpendDays >= GAME7_REQUIRED_NO_SPEND_DAYS) {
            sp.edit().putBoolean(KEY_GAME7_UNLOCKED, true).apply()
            game7Unlocked = true
        }
        val showGame7UnlockFlash = game7Unlocked && !sp.getBoolean(KEY_GAME7_UNLOCK_FLASH_SEEN, false)

        var game8Unlocked = sp.getBoolean(KEY_GAME8_UNLOCKED, false)
        if (!game8Unlocked && hasGoalOrBudget) {
            sp.edit().putBoolean(KEY_GAME8_UNLOCKED, true).apply()
            game8Unlocked = true
        }
        val showGame8UnlockFlash = game8Unlocked && !sp.getBoolean(KEY_GAME8_UNLOCK_FLASH_SEEN, false)

        var game9Unlocked = sp.getBoolean(KEY_GAME9_UNLOCKED, false)
        if (!game9Unlocked && effectiveStreak >= GAME9_REQUIRED_STREAK_DAYS) {
            sp.edit().putBoolean(KEY_GAME9_UNLOCKED, true).apply()
            game9Unlocked = true
        }
        val showGame9UnlockFlash = game9Unlocked && !sp.getBoolean(KEY_GAME9_UNLOCK_FLASH_SEEN, false)

        var game10Unlocked = sp.getBoolean(KEY_GAME10_UNLOCKED, false)
        if (!game10Unlocked && (effectiveStreak >= GAME10_REQUIRED_STREAK_DAYS || xp >= GAME10_REQUIRED_ARENA_XP)) {
            sp.edit().putBoolean(KEY_GAME10_UNLOCKED, true).apply()
            game10Unlocked = true
        }
        val showGame10UnlockFlash = game10Unlocked && !sp.getBoolean(KEY_GAME10_UNLOCK_FLASH_SEEN, false)

        val best2048 = sp.getLong(KEY_BEST_2048, 0L)
        val bestTower = sp.getInt(KEY_BEST_TOWER_FLOORS, 0)
        val bestRunner = sp.getLong(KEY_BEST_RUNNER_SCORE, 0L)
        val bestHexa = sp.getLong(KEY_BEST_HEXA_SCORE, 0L)
        val bestQuantumLevel = sp.getInt(KEY_BEST_QUANTUM_LEVEL, 0)
        val bestMathDuelScore = sp.getLong(KEY_BEST_MATH_DUEL_SCORE, 0L)
        val bestBreakerScore = sp.getLong(KEY_BEST_BREAKER_SCORE, 0L)
        val bestBreakerWave = sp.getInt(KEY_BEST_BREAKER_WAVE, 0)
        val bestChronoScore = sp.getLong(KEY_BEST_CHRONO_SCORE, 0L)
        val bestChronoRound = sp.getInt(KEY_BEST_CHRONO_ROUND, 0)
        val bestOrbitScore = sp.getLong(KEY_BEST_ORBIT_SCORE, 0L)
        val bestOrbitWave = sp.getInt(KEY_BEST_ORBIT_WAVE, 0)
        val bestCitadelScore = sp.getLong(KEY_BEST_CITADEL_SCORE, 0L)
        val bestCitadelTier = sp.getInt(KEY_BEST_CITADEL_TIER, 0)

        var grandmasterUnlocked = sp.getBoolean(KEY_GRANDMASTER_ARCHITECT, false) ||
            appSp.getBoolean(APP_KEY_GRANDMASTER_ARCHITECT, false)
        if (!grandmasterUnlocked && (bestCitadelTier >= GRANDMASTER_REQUIRED_TIER || bestCitadelScore >= GRANDMASTER_REQUIRED_SCORE)) {
            sp.edit().putBoolean(KEY_GRANDMASTER_ARCHITECT, true).apply()
            appSp.edit()
                .putBoolean(APP_KEY_GRANDMASTER_ARCHITECT, true)
                .putString(APP_KEY_PROFILE_RANK, "GRANDMASTER ARCHITECT")
                .apply()
            grandmasterUnlocked = true
        }

        val played = sp.getInt(KEY_GAMES_PLAYED, 0)
        val feedback = sp.getBoolean(KEY_FEEDBACK_ENABLED, true)
        return ArenaSnapshot(
            totalXp = xp,
            streakDays = streak,
            bestScore2048 = best2048,
            bestTowerFloors = bestTower,
            bestRunnerScore = bestRunner,
            bestHexaScore = bestHexa,
            bestQuantumLevel = bestQuantumLevel,
            bestMathDuelScore = bestMathDuelScore,
            bestBrickBreakerScore = bestBreakerScore,
            bestBrickBreakerWave = bestBreakerWave,
            bestChronoRound = bestChronoRound,
            bestChronoScore = bestChronoScore,
            bestOrbitScore = bestOrbitScore,
            bestOrbitWave = bestOrbitWave,
            bestCitadelScore = bestCitadelScore,
            bestCitadelTier = bestCitadelTier,
            effectiveStreakDays = effectiveStreak,
            recordedNoSpendDays = recordedNoSpendDays,
            hasCompletedGoalOrBudget = hasGoalOrBudget,
            isGame4Unlocked = game4Unlocked,
            shouldShowGame4UnlockFlash = showUnlockFlash,
            isGame5Unlocked = game5Unlocked,
            shouldShowGame5UnlockFlash = showGame5UnlockFlash,
            isGame6Unlocked = game6Unlocked,
            shouldShowGame6UnlockFlash = showGame6UnlockFlash,
            isGame7Unlocked = game7Unlocked,
            shouldShowGame7UnlockFlash = showGame7UnlockFlash,
            isGame8Unlocked = game8Unlocked,
            shouldShowGame8UnlockFlash = showGame8UnlockFlash,
            isGame9Unlocked = game9Unlocked,
            shouldShowGame9UnlockFlash = showGame9UnlockFlash,
            isGame10Unlocked = game10Unlocked,
            shouldShowGame10UnlockFlash = showGame10UnlockFlash,
            isGrandmasterArchitectUnlocked = grandmasterUnlocked,
            gamesPlayed = played,
            soundAndHapticEnabled = feedback,
            levelInfo = ArenaLevelCalculator.evaluate(xp)
        )
    }

    fun consumeGame4UnlockFlash(context: Context): ArenaSnapshot {
        prefs(context).edit().putBoolean(KEY_GAME4_UNLOCK_FLASH_SEEN, true).apply()
        return loadSnapshot(context)
    }

    fun consumeGame5UnlockFlash(context: Context): ArenaSnapshot {
        prefs(context).edit().putBoolean(KEY_GAME5_UNLOCK_FLASH_SEEN, true).apply()
        return loadSnapshot(context)
    }

    fun consumeGame6UnlockFlash(context: Context): ArenaSnapshot {
        prefs(context).edit().putBoolean(KEY_GAME6_UNLOCK_FLASH_SEEN, true).apply()
        return loadSnapshot(context)
    }

    fun consumeGame7UnlockFlash(context: Context): ArenaSnapshot {
        prefs(context).edit().putBoolean(KEY_GAME7_UNLOCK_FLASH_SEEN, true).apply()
        return loadSnapshot(context)
    }

    fun consumeGame8UnlockFlash(context: Context): ArenaSnapshot {
        prefs(context).edit().putBoolean(KEY_GAME8_UNLOCK_FLASH_SEEN, true).apply()
        return loadSnapshot(context)
    }

    fun consumeGame9UnlockFlash(context: Context): ArenaSnapshot {
        prefs(context).edit().putBoolean(KEY_GAME9_UNLOCK_FLASH_SEEN, true).apply()
        return loadSnapshot(context)
    }

    fun consumeGame10UnlockFlash(context: Context): ArenaSnapshot {
        prefs(context).edit().putBoolean(KEY_GAME10_UNLOCK_FLASH_SEEN, true).apply()
        return loadSnapshot(context)
    }

    fun awardXpAndTouchStreak(
        context: Context,
        xpDelta: Long,
        nowMillis: Long = System.currentTimeMillis()
    ): ArenaSnapshot {
        val sp = prefs(context)
        val today = dayKey(nowMillis)
        val yesterday = dayKey(nowMillis - 86_400_000L)
        val lastDay = sp.getString(KEY_LAST_PLAY_DAY, null)
        val currentStreak = sp.getInt(KEY_STREAK_DAYS, 0)

        val nextStreak = when {
            lastDay == today -> currentStreak.coerceAtLeast(1)
            lastDay == yesterday -> currentStreak + 1
            else -> 1
        }
        val nextXp = (sp.getLong(KEY_TOTAL_XP, 0L) + xpDelta.coerceAtLeast(0L)).coerceAtLeast(0L)

        val editor = sp.edit()
            .putLong(KEY_TOTAL_XP, nextXp)
            .putInt(KEY_STREAK_DAYS, nextStreak)
            .putString(KEY_LAST_PLAY_DAY, today)
        if (nextStreak >= GAME4_REQUIRED_STREAK_DAYS) {
            editor.putBoolean(KEY_GAME4_UNLOCKED, true)
        }
        if (nextStreak >= GAME5_REQUIRED_STREAK_DAYS) {
            editor.putBoolean(KEY_GAME5_UNLOCKED, true)
        }
        if (nextStreak >= GAME6_REQUIRED_STREAK_DAYS) {
            editor.putBoolean(KEY_GAME6_UNLOCKED, true)
        }
        if (nextStreak >= GAME9_REQUIRED_STREAK_DAYS) {
            editor.putBoolean(KEY_GAME9_UNLOCKED, true)
        }
        if (nextStreak >= GAME10_REQUIRED_STREAK_DAYS || nextXp >= GAME10_REQUIRED_ARENA_XP) {
            editor.putBoolean(KEY_GAME10_UNLOCKED, true)
        }
        editor.apply()

        return loadSnapshot(context)
    }

    fun recordPlaySession(context: Context, nowMillis: Long = System.currentTimeMillis()): ArenaSnapshot {
        val sp = prefs(context)
        val played = sp.getInt(KEY_GAMES_PLAYED, 0) + 1
        sp.edit().putInt(KEY_GAMES_PLAYED, played).apply()
        return awardXpAndTouchStreak(context, xpDelta = 0L, nowMillis = nowMillis)
    }

    fun updateBestScore2048IfHigher(context: Context, score: Long): ArenaSnapshot {
        val sp = prefs(context)
        val prevBest = sp.getLong(KEY_BEST_2048, 0L)
        if (score > prevBest) {
            sp.edit().putLong(KEY_BEST_2048, score).apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestTowerFloorsIfHigher(context: Context, floors: Int): ArenaSnapshot {
        val sp = prefs(context)
        val prevBest = sp.getInt(KEY_BEST_TOWER_FLOORS, 0)
        if (floors > prevBest) {
            sp.edit().putInt(KEY_BEST_TOWER_FLOORS, floors).apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestRunnerScoreIfHigher(context: Context, score: Long): ArenaSnapshot {
        val sp = prefs(context)
        val prevBest = sp.getLong(KEY_BEST_RUNNER_SCORE, 0L)
        if (score > prevBest) {
            sp.edit().putLong(KEY_BEST_RUNNER_SCORE, score).apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestHexaScoreIfHigher(context: Context, score: Long): ArenaSnapshot {
        val sp = prefs(context)
        val prevBest = sp.getLong(KEY_BEST_HEXA_SCORE, 0L)
        if (score > prevBest) {
            sp.edit().putLong(KEY_BEST_HEXA_SCORE, score).apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestQuantumLevelIfHigher(context: Context, level: Int): ArenaSnapshot {
        val sp = prefs(context)
        val prevBest = sp.getInt(KEY_BEST_QUANTUM_LEVEL, 0)
        if (level > prevBest) {
            sp.edit().putInt(KEY_BEST_QUANTUM_LEVEL, level).apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestMathDuelScoreIfHigher(context: Context, score: Long): ArenaSnapshot {
        val sp = prefs(context)
        val prevBest = sp.getLong(KEY_BEST_MATH_DUEL_SCORE, 0L)
        if (score > prevBest) {
            sp.edit().putLong(KEY_BEST_MATH_DUEL_SCORE, score).apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestBrickBreakerStatsIfHigher(context: Context, score: Long, wave: Int): ArenaSnapshot {
        val sp = prefs(context)
        val prevScore = sp.getLong(KEY_BEST_BREAKER_SCORE, 0L)
        val prevWave = sp.getInt(KEY_BEST_BREAKER_WAVE, 0)
        if (score > prevScore || wave > prevWave) {
            sp.edit().apply {
                if (score > prevScore) putLong(KEY_BEST_BREAKER_SCORE, score)
                if (wave > prevWave) putInt(KEY_BEST_BREAKER_WAVE, wave)
            }.apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestChronoStatsIfHigher(context: Context, score: Long, round: Int): ArenaSnapshot {
        val sp = prefs(context)
        val prevScore = sp.getLong(KEY_BEST_CHRONO_SCORE, 0L)
        val prevRound = sp.getInt(KEY_BEST_CHRONO_ROUND, 0)
        if (score > prevScore || round > prevRound) {
            sp.edit().apply {
                if (score > prevScore) putLong(KEY_BEST_CHRONO_SCORE, score)
                if (round > prevRound) putInt(KEY_BEST_CHRONO_ROUND, round)
            }.apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestOrbitStatsIfHigher(context: Context, score: Long, wave: Int): ArenaSnapshot {
        val sp = prefs(context)
        val prevScore = sp.getLong(KEY_BEST_ORBIT_SCORE, 0L)
        val prevWave = sp.getInt(KEY_BEST_ORBIT_WAVE, 0)
        if (score > prevScore || wave > prevWave) {
            sp.edit().apply {
                if (score > prevScore) putLong(KEY_BEST_ORBIT_SCORE, score)
                if (wave > prevWave) putInt(KEY_BEST_ORBIT_WAVE, wave)
            }.apply()
        }
        return loadSnapshot(context)
    }

    fun updateBestCitadelStatsIfHigher(context: Context, score: Long, tier: Int): ArenaSnapshot {
        val sp = prefs(context)
        val prevScore = sp.getLong(KEY_BEST_CITADEL_SCORE, 0L)
        val prevTier = sp.getInt(KEY_BEST_CITADEL_TIER, 0)
        val earnedGrandmaster = tier >= GRANDMASTER_REQUIRED_TIER || score >= GRANDMASTER_REQUIRED_SCORE
        if (score > prevScore || tier > prevTier || earnedGrandmaster) {
            sp.edit().apply {
                if (score > prevScore) putLong(KEY_BEST_CITADEL_SCORE, score)
                if (tier > prevTier) putInt(KEY_BEST_CITADEL_TIER, tier)
                if (earnedGrandmaster) putBoolean(KEY_GRANDMASTER_ARCHITECT, true)
            }.apply()
        }
        if (earnedGrandmaster) {
            appPrefs(context).edit()
                .putBoolean(APP_KEY_GRANDMASTER_ARCHITECT, true)
                .putString(APP_KEY_PROFILE_RANK, "GRANDMASTER ARCHITECT")
                .apply()
        }
        return loadSnapshot(context)
    }

    fun setSoundAndHapticEnabled(context: Context, enabled: Boolean): ArenaSnapshot {
        prefs(context).edit().putBoolean(KEY_FEEDBACK_ENABLED, enabled).apply()
        return loadSnapshot(context)
    }

    fun recordGame5LevelStars(context: Context, level: Int, stars: Int): Map<Int, Int> {
        val safeLevel = level.coerceAtLeast(1)
        val safeStars = stars.coerceIn(1, 3)
        val sp = prefs(context)
        val current = parseStarsMap(sp.getString(KEY_GAME5_LEVEL_STARS, null)).toMutableMap()
        val prev = current[safeLevel] ?: 0
        if (safeStars > prev) {
            current[safeLevel] = safeStars
            sp.edit().putString(KEY_GAME5_LEVEL_STARS, serializeStarsMap(current)).apply()
        }
        return current
    }

    fun getGame5LevelStarsMap(context: Context): Map<Int, Int> =
        parseStarsMap(prefs(context).getString(KEY_GAME5_LEVEL_STARS, null))

    fun recordGame8RoundStars(context: Context, round: Int, stars: Int): Map<Int, Int> {
        val safeRound = round.coerceAtLeast(1)
        val safeStars = stars.coerceIn(1, 3)
        val sp = prefs(context)
        val current = parseStarsMap(sp.getString(KEY_GAME8_ROUND_STARS, null)).toMutableMap()
        val prev = current[safeRound] ?: 0
        if (safeStars > prev) {
            current[safeRound] = safeStars
            sp.edit().putString(KEY_GAME8_ROUND_STARS, serializeStarsMap(current)).apply()
        }
        return current
    }

    fun getGame8RoundStarsMap(context: Context): Map<Int, Int> =
        parseStarsMap(prefs(context).getString(KEY_GAME8_ROUND_STARS, null))

    private fun parseStarsMap(raw: String?): Map<Int, Int> {
        if (raw.isNullOrBlank()) return emptyMap()
        val result = mutableMapOf<Int, Int>()
        raw.split(",").forEach { token ->
            val parts = token.split(":")
            if (parts.size == 2) {
                val lvl = parts[0].trim().toIntOrNull()
                val stars = parts[1].trim().toIntOrNull()
                if (lvl != null && stars != null && lvl > 0 && stars in 1..3) {
                    result[lvl] = stars
                }
            }
        }
        return result
    }

    private fun serializeStarsMap(map: Map<Int, Int>): String =
        map.entries.sortedBy { it.key }.joinToString(",") { "${it.key}:${it.value}" }

    private fun dayKey(timeMillis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(cal.timeInMillis))
    }
}

// -----------------------------------------------------------------------------
// 2. Modular Game Catalog (Complete 10-Game Arena Lineup — Finale Edition)
// -----------------------------------------------------------------------------

enum class ArenaGameId {
    CYBER_WEALTH_2048,
    TITANIUM_TOWER_STACKER,
    GLOBAL_CURRENCY_RUNNER,
    CYBER_VAULT_HEXA,
    QUANTUM_LASER_VAULT,
    RAPID_WEALTH_MATH_DUEL,
    NEON_BRICK_BREAKER,
    CHRONO_VAULT_MATRIX,
    GRAVITY_ORBIT_PROTECTOR,
    TITANIUM_CITADEL
}

data class ArenaGameSpec(
    val id: ArenaGameId,
    val slotNumber: Int,
    val title: String,
    val subtitle: String,
    val categoryTag: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isPlayableNow: Boolean
)

object ArenaGameCatalog {
    val allGames: List<ArenaGameSpec> = listOf(
        ArenaGameSpec(
            id = ArenaGameId.CYBER_WEALTH_2048,
            slotNumber = 1,
            title = "Cyber Wealth 2048",
            subtitle = "Merge Glass & Gold Vault Tiles • Infinite Puzzle",
            categoryTag = "FEATURED PUZZLE",
            icon = Icons.Default.Diamond,
            accentColor = ArenaCyan,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.TITANIUM_TOWER_STACKER,
            slotNumber = 2,
            title = "Titanium Wealth Tower Stacker",
            subtitle = "One-Tap Precision Stacking • Reach Orbit",
            categoryTag = "PRECISION REFLEX",
            icon = Icons.Default.Layers,
            accentColor = ArenaGold,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.GLOBAL_CURRENCY_RUNNER,
            slotNumber = 3,
            title = "Global Currency & Inflation Survival",
            subtitle = "3-Lane Cyber Reflex • Dodge Inflation & Multiply Wealth",
            categoryTag = "CYBER RUNNER",
            icon = Icons.Default.Public,
            accentColor = ArenaCyan,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.CYBER_VAULT_HEXA,
            slotNumber = 4,
            title = "Cyber Vault Hexa-Block Fit",
            subtitle = "37-Cell Honeycomb Puzzle • 3-Axis Laser Clears",
            categoryTag = "HEXAGONAL VAULT",
            icon = Icons.Default.AutoAwesome,
            accentColor = ArenaGold,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.QUANTUM_LASER_VAULT,
            slotNumber = 5,
            title = "Quantum Laser Vault Path",
            subtitle = "Ray-Traced Optical Logic • Split Beams & Charge Vaults",
            categoryTag = "OPTICAL LOGIC",
            icon = Icons.Default.Bolt,
            accentColor = ArenaCyan,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.RAPID_WEALTH_MATH_DUEL,
            slotNumber = 6,
            title = "Rapid Wealth Math & Discount Duel",
            subtitle = "45s Speed-Brain Arena • Discounts, Yields & Gold Fever",
            categoryTag = "SPEED WEALTH MATH",
            icon = Icons.Default.Bolt,
            accentColor = ArenaGold,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.NEON_BRICK_BREAKER,
            slotNumber = 7,
            title = "Neon Brick & Asset Breaker",
            subtitle = "120fps Plasma Physics • Explosive Cores & Multi-Ball",
            categoryTag = "ARCADE REFLEX",
            icon = Icons.Default.AutoAwesome,
            accentColor = ArenaCyan,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.CHRONO_VAULT_MATRIX,
            slotNumber = 8,
            title = "Chrono-Vault Memory Matrix",
            subtitle = "Quantum Grid 3x3->6x6 • Hologram Patterns & 24K Gold Nodes",
            categoryTag = "COGNITIVE MEMORY",
            icon = Icons.Default.Psychology,
            accentColor = ArenaGold,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.GRAVITY_ORBIT_PROTECTOR,
            slotNumber = 9,
            title = "Gravity Orbit: Capital Protector",
            subtitle = "360° Orbital Shield • Deflect Debt & Absorb Yield Orbs",
            categoryTag = "360° ORBITAL REFLEX",
            icon = Icons.Default.Shield,
            accentColor = ArenaCyan,
            isPlayableNow = true
        ),
        ArenaGameSpec(
            id = ArenaGameId.TITANIUM_CITADEL,
            slotNumber = 10,
            title = "Titanium Citadel: Grandmaster Challenge",
            subtitle = "3-Chamber Boss Gauntlet • Infinite Tiers & VIP Crown Rank",
            categoryTag = "VIP GRANDMASTER FINALE",
            icon = Icons.Default.EmojiEvents,
            accentColor = ArenaGold,
            isPlayableNow = true
        )
    )
}

// -----------------------------------------------------------------------------
// 3. Game 1 Engine: Cyber Wealth 2048 (Pure Kotlin Deterministic Logic)
// -----------------------------------------------------------------------------

enum class SwipeDirection {
    UP, DOWN, LEFT, RIGHT
}

@Composable
private fun ArenaRewardedButton(
    text: String,
    onClick: () -> Unit,
    accentColor: Color = ArenaGold,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor = ObsidianBg,
            disabledContainerColor = accentColor.copy(alpha = 0.22f),
            disabledContentColor = DarkTextSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(48.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PlayCircle,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

enum class UndoPowerUpState {
    FREE_AVAILABLE,
    REWARDED_AD_LOCKED,
    EXHAUSTED
}

data class WealthTile(
    val id: Long,
    val tier: Int,
    val row: Int,
    val col: Int,
    val isMerged: Boolean = false,
    val isNew: Boolean = false
)

data class MergeBurstEvent(
    val row: Int,
    val col: Int,
    val resultingTier: Int,
    val pointsEarned: Long
)

data class UndoBoardSnapshot(
    val tiles: List<WealthTile>,
    val score: Long,
    val moveCount: Int,
    val quickMergeChain: Int,
    val hasReachedGoldVault: Boolean
)

data class CyberWealthBoardState(
    val tiles: List<WealthTile>,
    val score: Long = 0L,
    val moveCount: Int = 0,
    val freeUndosRemaining: Int = 1,
    val adUndosRemaining: Int = 1,
    val undoSnapshot: UndoBoardSnapshot? = null,
    val quickMergeChain: Int = 0,
    val lastMergeTimestampMs: Long = 0L,
    val feverExpiresAtMs: Long = 0L,
    val hasReachedGoldVault: Boolean = false,
    val victoryBannerDismissed: Boolean = false,
    val isGameOver: Boolean = false,
    val nextTileId: Long = 1L,
    val lastMerges: List<MergeBurstEvent> = emptyList(),
    val lastXpAwarded: Long = 0L
) {
    fun isFeverActive(nowMs: Long = System.currentTimeMillis()): Boolean = nowMs < feverExpiresAtMs
    fun undoPowerUpState(): UndoPowerUpState = when {
        freeUndosRemaining > 0 -> UndoPowerUpState.FREE_AVAILABLE
        adUndosRemaining > 0 -> UndoPowerUpState.REWARDED_AD_LOCKED
        else -> UndoPowerUpState.EXHAUSTED
    }
}

object CyberWealth2048Engine {
    const val GRID_SIZE = 4
    const val GOLD_VAULT_TIER = 9
    private const val FEVER_COMBO_WINDOW_MS = 6500L
    private const val FEVER_DURATION_MS = 9000L

    fun labelForTier(tier: Int): String = when (tier) {
        1 -> "10"
        2 -> "20"
        3 -> "50"
        4 -> "100"
        5 -> "500"
        6 -> "1K"
        7 -> "5K"
        8 -> "10K"
        9 -> "GOLD VAULT"
        else -> "APEX $tier"
    }

    fun pointsForTier(tier: Int): Long = when (tier) {
        1 -> 10L
        2 -> 20L
        3 -> 50L
        4 -> 100L
        5 -> 500L
        6 -> 1_000L
        7 -> 5_000L
        8 -> 10_000L
        9 -> 25_000L
        else -> 50_000L
    }

    fun newGame(random: Random = Random.Default): CyberWealthBoardState {
        var nextId = 1L
        val tiles = mutableListOf<WealthTile>()
        val firstCell = randomEmptyCell(tiles, random)
        if (firstCell != null) {
            tiles += WealthTile(
                id = nextId++,
                tier = 1,
                row = firstCell.first,
                col = firstCell.second,
                isNew = true
            )
        }
        val secondCell = randomEmptyCell(tiles, random)
        if (secondCell != null) {
            tiles += WealthTile(
                id = nextId++,
                tier = if (random.nextFloat() < 0.85f) 1 else 2,
                row = secondCell.first,
                col = secondCell.second,
                isNew = true
            )
        }
        return CyberWealthBoardState(
            tiles = tiles,
            nextTileId = nextId
        )
    }

    fun swipe(
        state: CyberWealthBoardState,
        direction: SwipeDirection,
        nowMs: Long = System.currentTimeMillis(),
        random: Random = Random.Default
    ): Pair<CyberWealthBoardState, Boolean> {
        if (state.isGameOver) return state to false

        val grid: Array<Array<WealthTile?>> = Array(GRID_SIZE) { arrayOfNulls(GRID_SIZE) }
        state.tiles.forEach { t ->
            if (t.row in 0 until GRID_SIZE && t.col in 0 until GRID_SIZE) {
                grid[t.row][t.col] = t.copy(isMerged = false, isNew = false)
            }
        }

        var nextId = state.nextTileId
        var moved = false
        var pointsAdded = 0L
        val resultTiles = mutableListOf<WealthTile>()
        val mergeEvents = mutableListOf<MergeBurstEvent>()

        for (lineIdx in 0 until GRID_SIZE) {
            val line = ArrayList<WealthTile>(GRID_SIZE)
            for (step in 0 until GRID_SIZE) {
                val (r, c) = coordFor(direction, lineIdx, step)
                grid[r][c]?.let { line.add(it) }
            }

            var targetStep = 0
            var idx = 0
            while (idx < line.size) {
                val first = line[idx]
                val second = line.getOrNull(idx + 1)
                val (targetRow, targetCol) = coordFor(direction, lineIdx, targetStep)

                if (second != null && first.tier == second.tier) {
                    val newTier = first.tier + 1
                    val earned = pointsForTier(newTier)
                    pointsAdded += earned
                    val mergedTile = WealthTile(
                        id = nextId++,
                        tier = newTier,
                        row = targetRow,
                        col = targetCol,
                        isMerged = true,
                        isNew = false
                    )
                    resultTiles.add(mergedTile)
                    mergeEvents.add(
                        MergeBurstEvent(
                            row = targetRow,
                            col = targetCol,
                            resultingTier = newTier,
                            pointsEarned = earned
                        )
                    )
                    moved = true
                    idx += 2
                } else {
                    if (first.row != targetRow || first.col != targetCol) {
                        moved = true
                    }
                    resultTiles.add(
                        first.copy(
                            row = targetRow,
                            col = targetCol,
                            isMerged = false,
                            isNew = false
                        )
                    )
                    idx += 1
                }
                targetStep += 1
            }
        }

        if (!moved) {
            return state to false
        }

        val spawnCell = randomEmptyCell(resultTiles, random)
        if (spawnCell != null) {
            val spawnTier = if (random.nextFloat() < 0.88f) 1 else 2
            resultTiles.add(
                WealthTile(
                    id = nextId++,
                    tier = spawnTier,
                    row = spawnCell.first,
                    col = spawnCell.second,
                    isMerged = false,
                    isNew = true
                )
            )
        }

        val mergeCountThisMove = mergeEvents.size
        val withinQuickWindow = state.lastMergeTimestampMs > 0L &&
            (nowMs - state.lastMergeTimestampMs) <= FEVER_COMBO_WINDOW_MS

        val nextQuickChain = if (mergeCountThisMove > 0) {
            val base = if (withinQuickWindow || state.quickMergeChain > 0) state.quickMergeChain else 0
            base + mergeCountThisMove
        } else if (nowMs >= state.feverExpiresAtMs) {
            0
        } else {
            state.quickMergeChain
        }

        val nextFeverExpiry = if (mergeCountThisMove > 0 && nextQuickChain >= 3) {
            maxOf(state.feverExpiresAtMs, nowMs + FEVER_DURATION_MS)
        } else {
            state.feverExpiresAtMs
        }

        val feverActive = nowMs < nextFeverExpiry || (mergeCountThisMove > 0 && nextQuickChain >= 3)
        val baseXp = (mergeCountThisMove * 6L) + mergeEvents.sumOf { it.resultingTier * 2L } + 1L
        val xpEarned = if (feverActive) baseXp * 2L else baseXp

        val reachedGoldVaultNow = state.hasReachedGoldVault || resultTiles.any { it.tier >= GOLD_VAULT_TIER }
        val gameOverNow = !canMakeAnyMove(resultTiles)

        val snapshotForUndo = UndoBoardSnapshot(
            tiles = state.tiles.map { it.copy(isMerged = false, isNew = false) },
            score = state.score,
            moveCount = state.moveCount,
            quickMergeChain = state.quickMergeChain,
            hasReachedGoldVault = state.hasReachedGoldVault
        )

        val updatedState = state.copy(
            tiles = resultTiles,
            score = state.score + pointsAdded,
            moveCount = state.moveCount + 1,
            undoSnapshot = snapshotForUndo,
            quickMergeChain = nextQuickChain,
            lastMergeTimestampMs = if (mergeCountThisMove > 0) nowMs else state.lastMergeTimestampMs,
            feverExpiresAtMs = nextFeverExpiry,
            hasReachedGoldVault = reachedGoldVaultNow,
            isGameOver = gameOverNow,
            nextTileId = nextId,
            lastMerges = mergeEvents,
            lastXpAwarded = xpEarned
        )

        return updatedState to true
    }

    fun applyUndo(
        state: CyberWealthBoardState,
        viaRewardedAd: Boolean = false
    ): CyberWealthBoardState {
        val snap = state.undoSnapshot ?: return state
        if (!viaRewardedAd && state.freeUndosRemaining <= 0) return state
        if (viaRewardedAd && state.adUndosRemaining <= 0) return state

        return state.copy(
            tiles = snap.tiles.map { it.copy(isMerged = false, isNew = false) },
            score = snap.score,
            moveCount = snap.moveCount,
            freeUndosRemaining = if (viaRewardedAd) state.freeUndosRemaining else (state.freeUndosRemaining - 1).coerceAtLeast(0),
            adUndosRemaining = if (viaRewardedAd) (state.adUndosRemaining - 1).coerceAtLeast(0) else state.adUndosRemaining,
            undoSnapshot = null,
            quickMergeChain = snap.quickMergeChain,
            hasReachedGoldVault = snap.hasReachedGoldVault,
            isGameOver = false,
            lastMerges = emptyList(),
            lastXpAwarded = 0L
        )
    }

    fun canMakeAnyMove(tiles: List<WealthTile>): Boolean {
        if (tiles.size < GRID_SIZE * GRID_SIZE) return true
        val matrix = Array(GRID_SIZE) { IntArray(GRID_SIZE) }
        tiles.forEach { t ->
            if (t.row in 0 until GRID_SIZE && t.col in 0 until GRID_SIZE) {
                matrix[t.row][t.col] = t.tier
            }
        }
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                val tier = matrix[r][c]
                if (tier == 0) return true
                if (r + 1 < GRID_SIZE && matrix[r + 1][c] == tier) return true
                if (c + 1 < GRID_SIZE && matrix[r][c + 1] == tier) return true
            }
        }
        return false
    }

    private fun coordFor(direction: SwipeDirection, lineIdx: Int, step: Int): Pair<Int, Int> {
        return when (direction) {
            SwipeDirection.LEFT -> lineIdx to step
            SwipeDirection.RIGHT -> lineIdx to (GRID_SIZE - 1 - step)
            SwipeDirection.UP -> step to lineIdx
            SwipeDirection.DOWN -> (GRID_SIZE - 1 - step) to lineIdx
        }
    }

    private fun randomEmptyCell(tiles: List<WealthTile>, random: Random): Pair<Int, Int>? {
        val occupied = Array(GRID_SIZE) { BooleanArray(GRID_SIZE) }
        tiles.forEach { t ->
            if (t.row in 0 until GRID_SIZE && t.col in 0 until GRID_SIZE) {
                occupied[t.row][t.col] = true
            }
        }
        val empty = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (!occupied[r][c]) empty.add(r to c)
            }
        }
        if (empty.isEmpty()) return null
        return empty[random.nextInt(empty.size)]
    }
}

// -----------------------------------------------------------------------------
// 4. 120fps Canvas Particle Physics Engine
// -----------------------------------------------------------------------------

private data class MergeParticle(
    var normX: Float,
    var normY: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    var maxLife: Float,
    var radiusDp: Float,
    val color: Color
)

private data class MergeShockwave(
    val normX: Float,
    val normY: Float,
    var life: Float,
    val maxLife: Float,
    val color: Color
)

// -----------------------------------------------------------------------------
// 5. Tools Hub Bottom Entry Card (Dark Neon Glassmorphic Card)
// -----------------------------------------------------------------------------

@Composable
fun KushwantaMindArenaHubCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snapshot = remember { ArenaProgressStore.loadSnapshot(context) }

    val infiniteTransition = rememberInfiniteTransition(label = "arenaCardGlow")
    val borderPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arenaBorderGlow"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = ObsidianBg,
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    ArenaCyan.copy(alpha = 0.72f + (borderPhase * 0.24f)),
                    ArenaGold.copy(alpha = 0.62f + ((1f - borderPhase) * 0.30f))
                )
            )
        ),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 134.dp)
            .testTag("kushwanta_mind_arena_hub_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF0E131D),
                            ObsidianBg,
                            Color(0xFF13110B)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ArenaHubCoreEmblem(
                    modifier = Modifier
                        .size(80.dp)
                        .testTag("arena_hub_core_emblem")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mind & Wealth Arena",
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ArenaGold.copy(alpha = 0.16f),
                            border = BorderStroke(0.8.dp, ArenaGold.copy(alpha = 0.58f)),
                            modifier = Modifier.testTag("arena_hub_lvl_xp_pill")
                        ) {
                            Text(
                                text = "LVL ${snapshot.levelInfo.level} • ${snapshot.totalXp} XP",
                                color = ArenaGold,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "120fps Brain Reflexes, Puzzles & Wealth Challenges • Tap to Enter",
                        color = DarkTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (snapshot.streakDays > 0) "🔥 ${snapshot.streakDays}d STREAK • MULTI-GAME HUB" else "MULTI-GAME REFLEX HUB",
                            color = ArenaCyan.copy(alpha = 0.78f),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaCyan.copy(alpha = 0.16f),
                            border = BorderStroke(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(ArenaCyan, ArenaGold)
                                )
                            ),
                            modifier = Modifier.testTag("arena_hub_enter_badge")
                        ) {
                            Text(
                                text = "ARENA ▶",
                                color = ArenaCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArenaHubCoreEmblem(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arenaHubCoreEmblemAnim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arenaCorePulse"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Mind and Wealth Arena hexagonal gaming controller core and royal gold crest emblem"
        }
    ) {
        val r = size.minDimension * 0.5f
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f

        // 1. Subtle Breathing Neon Pulse Halo & Concentric Energy Ring
        val haloRadius = r * (0.86f + 0.08f * pulse)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ArenaCyan.copy(alpha = 0.22f + 0.10f * pulse),
                    ArenaGold.copy(alpha = 0.10f * pulse),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = haloRadius
            ),
            radius = haloRadius,
            center = Offset(cx, cy)
        )
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    ArenaCyan.copy(alpha = 0.30f + 0.45f * pulse),
                    ArenaGold.copy(alpha = 0.35f + 0.45f * (1f - pulse)),
                    ArenaCyan.copy(alpha = 0.25f + 0.35f * pulse),
                    ArenaCyan.copy(alpha = 0.30f + 0.45f * pulse)
                ),
                center = Offset(cx, cy)
            ),
            radius = r * (0.90f + 0.03f * pulse),
            center = Offset(cx, cy),
            style = Stroke(width = 1.2.dp.toPx())
        )

        // 2. Sculptural Hexagonal Energy Core
        val hexCenterY = cy + r * 0.07f
        val hexRadius = r * 0.71f
        val hexVertices = (0 until 6).map { idx ->
            val angleRad = (-90.0 + idx * 60.0) * (PI / 180.0)
            Offset(
                x = cx + (cos(angleRad) * hexRadius).toFloat(),
                y = hexCenterY + (sin(angleRad) * hexRadius).toFloat()
            )
        }
        val hexPath = Path().apply {
            moveTo(hexVertices[0].x, hexVertices[0].y)
            for (i in 1 until hexVertices.size) {
                lineTo(hexVertices[i].x, hexVertices[i].y)
            }
            close()
        }

        drawPath(
            path = hexPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF162033), Color(0xFF080B12)),
                startY = hexCenterY - hexRadius,
                endY = hexCenterY + hexRadius
            )
        )
        drawPath(
            path = hexPath,
            brush = Brush.linearGradient(
                colors = listOf(ArenaCyan, ArenaGold.copy(alpha = 0.90f), ArenaCyan),
                start = Offset(cx - hexRadius, hexCenterY - hexRadius),
                end = Offset(cx + hexRadius, hexCenterY + hexRadius)
            ),
            style = Stroke(width = 1.7.dp.toPx(), join = StrokeJoin.Round)
        )

        // Inner bevel hex ring
        val innerHexRadius = hexRadius * 0.85f
        val innerHexPath = Path().apply {
            for (idx in 0 until 6) {
                val angleRad = (-90.0 + idx * 60.0) * (PI / 180.0)
                val px = cx + (cos(angleRad) * innerHexRadius).toFloat()
                val py = hexCenterY + (sin(angleRad) * innerHexRadius).toFloat()
                if (idx == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        drawPath(
            path = innerHexPath,
            color = ArenaCyan.copy(alpha = 0.24f),
            style = Stroke(width = 0.8.dp.toPx(), join = StrokeJoin.Round)
        )

        // 3. Glowing Electric Cyan (#00FFCC) Circuitry Traces
        val traceAlpha = 0.55f + 0.35f * pulse
        val traceOffsets = listOf(
            Offset(-hexRadius * 0.70f, -hexRadius * 0.28f),
            Offset(hexRadius * 0.70f, -hexRadius * 0.28f),
            Offset(-hexRadius * 0.66f, hexRadius * 0.32f),
            Offset(hexRadius * 0.66f, hexRadius * 0.32f)
        )
        traceOffsets.forEach { endDelta ->
            val startPt = Offset(cx + endDelta.x * 0.36f, hexCenterY + endDelta.y * 0.36f)
            val kneePt = Offset(cx + endDelta.x * 0.68f, hexCenterY + endDelta.y * 0.78f)
            val tipPt = Offset(cx + endDelta.x, hexCenterY + endDelta.y)
            val tracePath = Path().apply {
                moveTo(startPt.x, startPt.y)
                lineTo(kneePt.x, kneePt.y)
                lineTo(tipPt.x, tipPt.y)
            }
            drawPath(
                path = tracePath,
                color = ArenaCyan.copy(alpha = traceAlpha),
                style = Stroke(width = 1.0.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawCircle(
                color = ArenaCyan.copy(alpha = 0.85f),
                radius = 1.6.dp.toPx(),
                center = tipPt
            )
        }

        // Vertical lower circuitry spine
        drawLine(
            color = ArenaCyan.copy(alpha = traceAlpha),
            start = Offset(cx, hexCenterY + hexRadius * 0.32f),
            end = Offset(cx, hexCenterY + hexRadius * 0.72f),
            strokeWidth = 1.1.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = ArenaGold.copy(alpha = 0.88f),
            radius = 1.8.dp.toPx(),
            center = Offset(cx, hexCenterY + hexRadius * 0.74f)
        )

        // 4. Sleek Futuristic Neon Gaming Controller Silhouette
        val ctrlY = hexCenterY + r * 0.04f
        val ctrlW = hexRadius * 0.56f
        val ctrlH = hexRadius * 0.34f
        val controllerPath = Path().apply {
            moveTo(cx - ctrlW * 0.44f, ctrlY - ctrlH * 0.58f)
            lineTo(cx + ctrlW * 0.44f, ctrlY - ctrlH * 0.58f)
            quadraticTo(cx + ctrlW * 0.94f, ctrlY - ctrlH * 0.52f, cx + ctrlW * 1.02f, ctrlY + ctrlH * 0.32f)
            quadraticTo(cx + ctrlW * 1.04f, ctrlY + ctrlH * 0.96f, cx + ctrlW * 0.64f, ctrlY + ctrlH * 0.92f)
            lineTo(cx + ctrlW * 0.38f, ctrlY + ctrlH * 0.42f)
            lineTo(cx - ctrlW * 0.38f, ctrlY + ctrlH * 0.42f)
            lineTo(cx - ctrlW * 0.64f, ctrlY + ctrlH * 0.92f)
            quadraticTo(cx - ctrlW * 1.04f, ctrlY + ctrlH * 0.96f, cx - ctrlW * 1.02f, ctrlY + ctrlH * 0.32f)
            quadraticTo(cx - ctrlW * 0.94f, ctrlY - ctrlH * 0.52f, cx - ctrlW * 0.44f, ctrlY - ctrlH * 0.58f)
            close()
        }

        drawPath(
            path = controllerPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF22314A), Color(0xFF0E1522)),
                startY = ctrlY - ctrlH,
                endY = ctrlY + ctrlH
            )
        )
        drawPath(
            path = controllerPath,
            color = ArenaCyan.copy(alpha = 0.92f),
            style = Stroke(width = 1.35.dp.toPx(), join = StrokeJoin.Round)
        )

        // Controller Left D-Pad (Electric Cyan)
        val dPadX = cx - ctrlW * 0.48f
        val dPadY = ctrlY - ctrlH * 0.04f
        val dPadArm = ctrlH * 0.28f
        drawLine(
            color = ArenaCyan,
            start = Offset(dPadX - dPadArm, dPadY),
            end = Offset(dPadX + dPadArm, dPadY),
            strokeWidth = 2.0.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ArenaCyan,
            start = Offset(dPadX, dPadY - dPadArm),
            end = Offset(dPadX, dPadY + dPadArm),
            strokeWidth = 2.0.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Controller Right Action Matrix (Gold + Cyan Nodes)
        val actX = cx + ctrlW * 0.48f
        val actY = ctrlY - ctrlH * 0.04f
        val actSpread = ctrlH * 0.24f
        val btnRadius = 1.6.dp.toPx()
        drawCircle(color = ArenaGold, radius = btnRadius, center = Offset(actX, actY - actSpread))
        drawCircle(color = ArenaGold, radius = btnRadius, center = Offset(actX, actY + actSpread))
        drawCircle(color = ArenaCyan, radius = btnRadius, center = Offset(actX - actSpread, actY))
        drawCircle(color = ArenaCyan, radius = btnRadius, center = Offset(actX + actSpread, actY))

        // Controller Center Core Spark
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = 1.6.dp.toPx(),
            center = Offset(cx, ctrlY - ctrlH * 0.08f)
        )

        // 5. Royal Gold (#FFD700) Apex Crest Crown at Top Vertex
        val crownBaseY = hexCenterY - hexRadius * 0.72f
        val crownW = hexRadius * 0.46f
        val crownH = r * 0.28f
        val crownPath = Path().apply {
            moveTo(cx - crownW * 0.72f, crownBaseY + crownH * 0.20f)
            lineTo(cx - crownW, crownBaseY - crownH * 0.42f)
            lineTo(cx - crownW * 0.36f, crownBaseY - crownH * 0.06f)
            lineTo(cx, crownBaseY - crownH * 0.88f)
            lineTo(cx + crownW * 0.36f, crownBaseY - crownH * 0.06f)
            lineTo(cx + crownW, crownBaseY - crownH * 0.42f)
            lineTo(cx + crownW * 0.72f, crownBaseY + crownH * 0.20f)
            close()
        }
        drawPath(
            path = crownPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFF4B8), ArenaGold, ArenaAmber),
                startY = crownBaseY - crownH,
                endY = crownBaseY + crownH * 0.25f
            )
        )
        drawPath(
            path = crownPath,
            color = Color.White.copy(alpha = 0.85f),
            style = Stroke(width = 0.85.dp.toPx(), join = StrokeJoin.Round)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.65f + 0.35f * pulse),
            radius = 1.4.dp.toPx(),
            center = Offset(cx, crownBaseY - crownH * 0.88f)
        )
    }
}

// -----------------------------------------------------------------------------
// 6. Full Kushwanta Mind Arena Screen (Active Playable Hero Banners Only)
// -----------------------------------------------------------------------------

@Composable
fun KushwantaMindArenaScreen(
    onFullScreenGameChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var arenaSnapshot by remember { mutableStateOf(ArenaProgressStore.loadSnapshot(context)) }
    var activeGameId by remember { mutableStateOf<ArenaGameId?>(null) }

    LaunchedEffect(activeGameId) {
        onFullScreenGameChanged(activeGameId != null)
    }
    DisposableEffect(Unit) {
        onDispose { onFullScreenGameChanged(false) }
    }

    // Sync real No-Spend Days (Game 7) & Goal/Budget Milestones (Game 8) from Room DB in background
    LaunchedEffect(Unit) {
        val synced = withContext(Dispatchers.IO) {
            ArenaProgressStore.syncFinanceMilestonesFromDb(context)
        }
        arenaSnapshot = synced
    }

    BackHandler(enabled = activeGameId != null) {
        activeGameId = null
        arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
    }

    when (activeGameId) {
        ArenaGameId.CYBER_WEALTH_2048 -> {
            CyberWealth2048GameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.TITANIUM_TOWER_STACKER -> {
            TitaniumTowerGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.GLOBAL_CURRENCY_RUNNER -> {
            CurrencyRunnerGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.CYBER_VAULT_HEXA -> {
            HexaVaultBlockGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.QUANTUM_LASER_VAULT -> {
            QuantumLaserVaultGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.RAPID_WEALTH_MATH_DUEL -> {
            RapidWealthMathDuelGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.NEON_BRICK_BREAKER -> {
            NeonBrickBreakerGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.CHRONO_VAULT_MATRIX -> {
            ChronoVaultMatrixGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.GRAVITY_ORBIT_PROTECTOR -> {
            GravityOrbitProtectorGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        ArenaGameId.TITANIUM_CITADEL -> {
            TitaniumCitadelGameView(
                snapshot = arenaSnapshot,
                onSnapshotUpdated = { arenaSnapshot = it },
                onExitToHub = {
                    arenaSnapshot = ArenaProgressStore.loadSnapshot(context)
                    activeGameId = null
                },
                modifier = modifier
            )
            return
        }
        null -> Unit
    }

    val scrollState = rememberScrollState()
    val game1Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.CYBER_WEALTH_2048 } }
    val game2Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.TITANIUM_TOWER_STACKER } }
    val game3Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.GLOBAL_CURRENCY_RUNNER } }
    val game4Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.CYBER_VAULT_HEXA } }
    val game5Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.QUANTUM_LASER_VAULT } }
    val game6Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.RAPID_WEALTH_MATH_DUEL } }
    val game7Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.NEON_BRICK_BREAKER } }
    val game8Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.CHRONO_VAULT_MATRIX } }
    val game9Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.GRAVITY_ORBIT_PROTECTOR } }
    val game10Spec = remember { ArenaGameCatalog.allGames.first { it.id == ArenaGameId.TITANIUM_CITADEL } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .verticalScroll(scrollState)
            .navigationBarsPadding()
            .padding(top = 4.dp, bottom = 96.dp)
            .testTag("arena_hub_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ArenaHubHeaderCard(
            snapshot = arenaSnapshot,
            onToggleFeedback = {
                val nextEnabled = !arenaSnapshot.soundAndHapticEnabled
                arenaSnapshot = ArenaProgressStore.setSoundAndHapticEnabled(context, nextEnabled)
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game1Spec.categoryTag,
                color = ArenaCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = ArenaCyan.copy(alpha = 0.14f),
                border = BorderStroke(0.8.dp, ArenaCyan.copy(alpha = 0.45f))
            ) {
                Text(
                    text = "120FPS ENGINE",
                    color = ArenaCyan,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 1 Hero Banner Card
        CyberWealthHeroBannerCard(
            spec = game1Spec,
            bestScore = arenaSnapshot.bestScore2048,
            onPlayClick = {
                arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                activeGameId = game1Spec.id
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game2Spec.categoryTag,
                color = ArenaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = ArenaGold.copy(alpha = 0.14f),
                border = BorderStroke(0.8.dp, ArenaGold.copy(alpha = 0.48f))
            ) {
                Text(
                    text = "SYNTH FX • ORBIT",
                    color = ArenaGold,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 2 Hero Banner Card (Titanium Wealth Tower Stacker)
        TitaniumTowerHeroBannerCard(
            spec = game2Spec,
            bestFloors = arenaSnapshot.bestTowerFloors,
            onPlayClick = {
                arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                activeGameId = game2Spec.id
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game3Spec.categoryTag,
                color = ArenaCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = ArenaCyan.copy(alpha = 0.14f),
                border = BorderStroke(0.8.dp, ArenaCyan.copy(alpha = 0.48f))
            ) {
                Text(
                    text = "3-LANE 120FPS",
                    color = ArenaCyan,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 3 Hero Banner Card (Global Currency & Inflation Survival)
        CurrencyRunnerHeroBannerCard(
            spec = game3Spec,
            bestScore = arenaSnapshot.bestRunnerScore,
            onPlayClick = {
                arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                activeGameId = game3Spec.id
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game4Spec.categoryTag,
                color = ArenaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (arenaSnapshot.isGame4Unlocked) ArenaGold.copy(alpha = 0.14f) else ArenaCyan.copy(alpha = 0.14f),
                border = BorderStroke(
                    0.8.dp,
                    if (arenaSnapshot.isGame4Unlocked) ArenaGold.copy(alpha = 0.48f) else ArenaCyan.copy(alpha = 0.48f)
                )
            ) {
                Text(
                    text = if (arenaSnapshot.isGame4Unlocked) "37-CELL • UNLOCKED" else "3-DAY STREAK REWARD",
                    color = if (arenaSnapshot.isGame4Unlocked) ArenaGold else ArenaCyan,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 4 Hero Banner Card (Cyber Vault Hexa-Block Fit + Permanent 3-Day Streak Unlock)
        HexaVaultHeroBannerCard(
            spec = game4Spec,
            snapshot = arenaSnapshot,
            onUnlockFlashShown = {
                arenaSnapshot = ArenaProgressStore.consumeGame4UnlockFlash(context)
            },
            onPlayClick = {
                if (arenaSnapshot.isGame4Unlocked) {
                    arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                    activeGameId = game4Spec.id
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game5Spec.categoryTag,
                color = ArenaCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (arenaSnapshot.isGame5Unlocked) ArenaCyan.copy(alpha = 0.14f) else ArenaGold.copy(alpha = 0.14f),
                border = BorderStroke(
                    0.8.dp,
                    if (arenaSnapshot.isGame5Unlocked) ArenaCyan.copy(alpha = 0.48f) else ArenaGold.copy(alpha = 0.48f)
                )
            ) {
                Text(
                    text = if (arenaSnapshot.isGame5Unlocked) "RAY-TRACED • UNLOCKED" else "7-DAY STREAK REWARD",
                    color = if (arenaSnapshot.isGame5Unlocked) ArenaCyan else ArenaGold,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 5 Hero Banner Card (Quantum Laser Vault Path + Permanent 7-Day Streak Unlock)
        QuantumLaserHeroBannerCard(
            spec = game5Spec,
            snapshot = arenaSnapshot,
            onUnlockFlashShown = {
                arenaSnapshot = ArenaProgressStore.consumeGame5UnlockFlash(context)
            },
            onPlayClick = {
                if (arenaSnapshot.isGame5Unlocked) {
                    arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                    activeGameId = game5Spec.id
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game6Spec.categoryTag,
                color = ArenaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (arenaSnapshot.isGame6Unlocked) ArenaCyan.copy(alpha = 0.14f) else ArenaGold.copy(alpha = 0.14f),
                border = BorderStroke(
                    0.8.dp,
                    if (arenaSnapshot.isGame6Unlocked) ArenaCyan.copy(alpha = 0.48f) else ArenaGold.copy(alpha = 0.48f)
                )
            ) {
                Text(
                    text = if (arenaSnapshot.isGame6Unlocked) "45S DUEL • UNLOCKED" else "10-DAY STREAK REWARD",
                    color = if (arenaSnapshot.isGame6Unlocked) ArenaCyan else ArenaGold,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 6 Hero Banner Card (Rapid Wealth Math & Discount Duel + Permanent 10-Day Streak Unlock)
        MathDuelHeroBannerCard(
            spec = game6Spec,
            snapshot = arenaSnapshot,
            onUnlockFlashShown = {
                arenaSnapshot = ArenaProgressStore.consumeGame6UnlockFlash(context)
            },
            onPlayClick = {
                if (arenaSnapshot.isGame6Unlocked) {
                    arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                    activeGameId = game6Spec.id
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game7Spec.categoryTag,
                color = ArenaCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (arenaSnapshot.isGame7Unlocked) ArenaCyan.copy(alpha = 0.14f) else ArenaGold.copy(alpha = 0.14f),
                border = BorderStroke(
                    0.8.dp,
                    if (arenaSnapshot.isGame7Unlocked) ArenaCyan.copy(alpha = 0.48f) else ArenaGold.copy(alpha = 0.48f)
                )
            ) {
                Text(
                    text = if (arenaSnapshot.isGame7Unlocked) "PLASMA 120FPS • UNLOCKED" else "3 NO-SPEND DAYS REWARD",
                    color = if (arenaSnapshot.isGame7Unlocked) ArenaCyan else ArenaGold,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 7 Hero Banner Card (Neon Brick & Asset Breaker + Permanent 3 No-Spend Days Unlock)
        BreakerHeroBannerCard(
            spec = game7Spec,
            snapshot = arenaSnapshot,
            onUnlockFlashShown = {
                arenaSnapshot = ArenaProgressStore.consumeGame7UnlockFlash(context)
            },
            onPlayClick = {
                if (arenaSnapshot.isGame7Unlocked) {
                    arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                    activeGameId = game7Spec.id
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game8Spec.categoryTag,
                color = ArenaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (arenaSnapshot.isGame8Unlocked) ArenaCyan.copy(alpha = 0.14f) else ArenaGold.copy(alpha = 0.14f),
                border = BorderStroke(
                    0.8.dp,
                    if (arenaSnapshot.isGame8Unlocked) ArenaCyan.copy(alpha = 0.48f) else ArenaGold.copy(alpha = 0.48f)
                )
            ) {
                Text(
                    text = if (arenaSnapshot.isGame8Unlocked) "QUANTUM 6x6 • UNLOCKED" else "GOAL / BUDGET REWARD",
                    color = if (arenaSnapshot.isGame8Unlocked) ArenaCyan else ArenaGold,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 8 Hero Banner Card (Chrono-Vault Memory Matrix + Permanent Goal/Budget Unlock)
        ChronoVaultHeroBannerCard(
            spec = game8Spec,
            snapshot = arenaSnapshot,
            onUnlockFlashShown = {
                arenaSnapshot = ArenaProgressStore.consumeGame8UnlockFlash(context)
            },
            onPlayClick = {
                if (arenaSnapshot.isGame8Unlocked) {
                    arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                    activeGameId = game8Spec.id
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game9Spec.categoryTag,
                color = ArenaCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (arenaSnapshot.isGame9Unlocked) ArenaCyan.copy(alpha = 0.14f) else ArenaGold.copy(alpha = 0.14f),
                border = BorderStroke(
                    0.8.dp,
                    if (arenaSnapshot.isGame9Unlocked) ArenaCyan.copy(alpha = 0.48f) else ArenaGold.copy(alpha = 0.48f)
                )
            ) {
                Text(
                    text = if (arenaSnapshot.isGame9Unlocked) "ORBITAL 120FPS • UNLOCKED" else "14-DAY STREAK REWARD",
                    color = if (arenaSnapshot.isGame9Unlocked) ArenaCyan else ArenaGold,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 9 Hero Banner Card (Gravity Orbit: Capital Protector + Permanent 14-Day Streak Unlock)
        OrbitProtectorHeroBannerCard(
            spec = game9Spec,
            snapshot = arenaSnapshot,
            onUnlockFlashShown = {
                arenaSnapshot = ArenaProgressStore.consumeGame9UnlockFlash(context)
            },
            onPlayClick = {
                if (arenaSnapshot.isGame9Unlocked) {
                    arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                    activeGameId = game9Spec.id
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = game10Spec.categoryTag,
                color = ArenaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (arenaSnapshot.isGame10Unlocked) ArenaGold.copy(alpha = 0.16f) else ArenaCyan.copy(alpha = 0.14f),
                border = BorderStroke(
                    0.8.dp,
                    if (arenaSnapshot.isGame10Unlocked) ArenaGold.copy(alpha = 0.72f) else ArenaCyan.copy(alpha = 0.50f)
                )
            ) {
                Text(
                    text = if (arenaSnapshot.isGame10Unlocked) "VIP FINALE • UNLOCKED" else "21D STREAK OR 2,500 XP",
                    color = if (arenaSnapshot.isGame10Unlocked) ArenaGold else ArenaCyan,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Game 10 VIP Hero Banner Card (Titanium Citadel: Grandmaster Challenge + Dual VIP Unlock)
        CitadelHeroBannerCard(
            spec = game10Spec,
            snapshot = arenaSnapshot,
            onUnlockFlashShown = {
                arenaSnapshot = ArenaProgressStore.consumeGame10UnlockFlash(context)
            },
            onPlayClick = {
                if (arenaSnapshot.isGame10Unlocked) {
                    arenaSnapshot = ArenaProgressStore.recordPlaySession(context)
                    activeGameId = game10Spec.id
                }
            }
        )
    }
}

@Composable
private fun ArenaHubHeaderCard(
    snapshot: ArenaSnapshot,
    onToggleFeedback: () -> Unit
) {
    val levelInfo = snapshot.levelInfo
    val animatedProgress by animateFloatAsState(
        targetValue = levelInfo.progressRatio,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessLow),
        label = "arenaXpProgress"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "arenaHeaderShimmer")
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "headerShimmerX"
    )

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = ObsidianElevated,
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(ArenaCyan.copy(alpha = 0.72f), ArenaGold.copy(alpha = 0.72f))
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hub_header_card")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val x = size.width * shimmerX
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            ArenaCyan.copy(alpha = 0.07f),
                            ArenaGold.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        start = Offset(x - size.width * 0.25f, 0f),
                        end = Offset(x + size.width * 0.25f, size.height)
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ArenaGold.copy(alpha = 0.18f),
                                border = BorderStroke(0.8.dp, ArenaGold.copy(alpha = 0.65f))
                            ) {
                                Text(
                                    text = "LVL ${levelInfo.level} • ${levelInfo.title.uppercase()}",
                                    color = ArenaGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.6.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                        .testTag("arena_level_badge")
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFF6B00).copy(alpha = 0.16f),
                                border = BorderStroke(0.8.dp, ArenaAmber.copy(alpha = 0.55f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                        .testTag("arena_streak_badge"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Arena Play Streak",
                                        tint = ArenaAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "${snapshot.streakDays}d Streak",
                                        color = Color(0xFFFDE68A),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        if (snapshot.isGrandmasterArchitectUnlocked) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = ArenaGold.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.88f)),
                                modifier = Modifier.testTag("arena_grandmaster_architect_badge")
                            ) {
                                Text(
                                    text = "👑 GRANDMASTER ARCHITECT",
                                    color = ArenaGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.9.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Kushwanta Mind Arena",
                            color = DarkTextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Skill & wealth reflex training • Kushwanta Wealth OS",
                            color = DarkTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onToggleFeedback,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ObsidianBg.copy(alpha = 0.7f))
                            .border(1.dp, ArenaCyan.copy(alpha = 0.35f), CircleShape)
                            .testTag("arena_feedback_toggle")
                    ) {
                        Icon(
                            imageVector = if (snapshot.soundAndHapticEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = if (snapshot.soundAndHapticEnabled) "Mute Arena Audio & Haptics" else "Enable Arena Audio & Haptics",
                            tint = if (snapshot.soundAndHapticEnabled) ArenaCyan else DarkTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (levelInfo.isMaxLevel) "MAX TIER ACHIEVED" else "ARENA XP PROGRESS",
                            color = ArenaCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            text = if (levelInfo.isMaxLevel) {
                                "${levelInfo.totalXp} XP"
                            } else {
                                "${levelInfo.totalXp} / ${levelInfo.nextLevelXp} XP"
                            },
                            color = DarkTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.testTag("arena_xp_label")
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(ObsidianBg)
                            .border(0.8.dp, ArenaCyan.copy(alpha = 0.25f), RoundedCornerShape(999.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedProgress.coerceIn(0.03f, 1f))
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(ArenaCyan, ElectricCyan, ArenaGold)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CyberWealthHeroBannerCard(
    spec: ArenaGameSpec,
    bestScore: Long,
    onPlayClick: () -> Unit
) {
    val formattedBest = remember(bestScore) {
        NumberFormat.getNumberInstance(Locale.US).format(bestScore)
    }

    Surface(
        onClick = onPlayClick,
        shape = RoundedCornerShape(16.dp),
        color = ObsidianBg,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(ArenaCyan, ArenaGold.copy(alpha = 0.90f), ArenaCyan.copy(alpha = 0.75f))
            )
        ),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_1")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0E1522),
                            ObsidianBg,
                            Color(0xFF15120A)
                        )
                    )
                )
        ) {
            // Subtle radial rim glow behind Canvas artwork & trophy
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.height * 0.5f, size.height * 0.5f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.5f, size.height * 0.5f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.25f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left: Custom Canvas 4x4 Neon Glass & Gold Puzzle Emblem
                CyberWealth2048LogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("cyber_wealth_2048_logo_canvas")
                )

                // Right: Game Info, Live Best Pill & Electric Cyan PLAY CTA
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    // Glowing Best Score Pill
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_best_score_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Score",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: $formattedBest",
                                color = ArenaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Prominent Tactile Electric Cyan PLAY Button
                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaCyan,
                            contentColor = ObsidianBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_play_button")
                    ) {
                        Text(
                            text = "PLAY ▶",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CyberWealth2048LogoArtwork(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoTilePulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "goldLogoPulse"
    )

    Canvas(
        modifier = modifier
            .semantics {
                contentDescription = "Cyber Wealth 2048 4 by 4 neon glass and gold tile emblem"
            }
    ) {
        val trayCorner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        // Outer recessed Obsidian frame with dual neon rim
        drawRoundRect(
            brush = Brush.linearGradient(listOf(Color(0xFF131926), Color(0xFF07090D))),
            cornerRadius = trayCorner
        )
        drawRoundRect(
            brush = Brush.linearGradient(
                listOf(ArenaCyan.copy(alpha = 0.78f), ArenaGold.copy(alpha = 0.82f))
            ),
            cornerRadius = trayCorner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val pad = size.minDimension * 0.10f
        val gap = size.minDimension * 0.055f
        val innerSize = size.minDimension - (pad * 2f)
        val cell = (innerSize - (gap * 3f)) / 4f
        val tileRadius = CornerRadius(cell * 0.24f, cell * 0.24f)

        // Curated 4x4 recognizable 2048 mosaic: dark sockets + glowing cyan/indigo/bronze/gold tiles
        val mosaicTiers = listOf(
            listOf(1, 2, 0, 1),
            listOf(3, 5, 2, 0),
            listOf(2, 6, 7, 4),
            listOf(0, 4, 8, 9)
        )

        for (r in 0 until 4) {
            for (c in 0 until 4) {
                val x = pad + (c * (cell + gap))
                val y = pad + (r * (cell + gap))
                val tier = mosaicTiers[r][c]

                if (tier == 0) {
                    drawRoundRect(
                        color = Color(0xFF141B29),
                        topLeft = Offset(x, y),
                        size = Size(cell, cell),
                        cornerRadius = tileRadius
                    )
                } else {
                    val palette = when (tier) {
                        1 -> Color(0xFF1E293B) to Color(0xFF0F172A)
                        2 -> Color(0xFF0E444D) to Color(0xFF0B2229)
                        3 -> Color(0xFF00C8A0) to Color(0xFF0B3D3E)
                        4 -> Color(0xFF0284C7) to Color(0xFF0F172A)
                        5 -> Color(0xFF4F46E5) to Color(0xFF1E1B4B)
                        6 -> Color(0xFF9333EA) to Color(0xFF3B0764)
                        7 -> Color(0xFFD97706) to Color(0xFF451A03)
                        8 -> ArenaAmber to Color(0xFF78350F)
                        else -> ArenaGold to Color(0xFFFFA500)
                    }

                    // Gold Apex corner aura
                    if (tier == 9) {
                        drawRoundRect(
                            color = ArenaGold.copy(alpha = 0.36f * pulse),
                            topLeft = Offset(x - 1.5.dp.toPx(), y - 1.5.dp.toPx()),
                            size = Size(cell + 3.dp.toPx(), cell + 3.dp.toPx()),
                            cornerRadius = tileRadius
                        )
                    }

                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(palette.first, palette.second),
                            start = Offset(x, y),
                            end = Offset(x + cell, y + cell)
                        ),
                        topLeft = Offset(x, y),
                        size = Size(cell, cell),
                        cornerRadius = tileRadius
                    )

                    // Inner top glass highlight sheen
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.30f), Color.Transparent),
                            startY = y,
                            endY = y + cell * 0.5f
                        ),
                        topLeft = Offset(x, y),
                        size = Size(cell, cell * 0.5f),
                        cornerRadius = tileRadius
                    )

                    val rimColor = when {
                        tier == 9 -> Color.White.copy(alpha = 0.90f * pulse)
                        tier >= 7 -> ArenaGold.copy(alpha = 0.85f)
                        tier >= 3 -> ArenaCyan.copy(alpha = 0.72f)
                        else -> Color.White.copy(alpha = 0.16f)
                    }
                    drawRoundRect(
                        color = rimColor,
                        topLeft = Offset(x, y),
                        size = Size(cell, cell),
                        cornerRadius = tileRadius,
                        style = Stroke(width = if (tier == 9) 1.2.dp.toPx() else 0.7.dp.toPx())
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 7. Ultra-Luxury Game 1 Playable View: Cyber Wealth 2048
// -----------------------------------------------------------------------------

@Composable
fun CyberWealth2048GameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    var boardState by remember { mutableStateOf(CyberWealth2048Engine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var nowClockMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var undoAdHookMessage by remember { mutableStateOf<String?>(null) }
    var moveCounterTick by remember { mutableIntStateOf(0) }
    var roundTotalXp by remember { mutableLongStateOf(0L) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }

    val particles = remember { mutableStateListOf<MergeParticle>() }
    val shockwaves = remember { mutableStateListOf<MergeShockwave>() }

    val isFeverActive = boardState.isFeverActive(nowClockMs)

    LaunchedEffect(boardState.feverExpiresAtMs) {
        while (System.currentTimeMillis() < boardState.feverExpiresAtMs) {
            nowClockMs = System.currentTimeMillis()
            delay(180L)
        }
        nowClockMs = System.currentTimeMillis()
    }

    LaunchedEffect(undoAdHookMessage) {
        if (undoAdHookMessage != null) {
            delay(2400L)
            undoAdHookMessage = null
        }
    }

    // 120fps frame-synced particle simulation loop (active strictly while bursts exist)
    LaunchedEffect(particles.size, shockwaves.size) {
        if (particles.isEmpty() && shockwaves.isEmpty()) return@LaunchedEffect
        var lastNanos = 0L
        while (particles.isNotEmpty() || shockwaves.isNotEmpty()) {
            withFrameNanos { frameNanos ->
                val dt = if (lastNanos == 0L) 0.016f else ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = frameNanos

                val pIt = particles.listIterator()
                while (pIt.hasNext()) {
                    val p = pIt.next()
                    p.life += dt
                    if (p.life >= p.maxLife) {
                        pIt.remove()
                    } else {
                        p.normX += p.vx * dt
                        p.normY += p.vy * dt
                        p.vy += 0.35f * dt
                        p.vx *= 0.96f
                    }
                }

                val sIt = shockwaves.listIterator()
                while (sIt.hasNext()) {
                    val s = sIt.next()
                    s.life += dt
                    if (s.life >= s.maxLife) {
                        sIt.remove()
                    }
                }
            }
        }
    }

    val performSwipe by rememberUpdatedState { dir: SwipeDirection ->
        val swipeNow = System.currentTimeMillis()
        nowClockMs = swipeNow
        val (nextState, didMove) = CyberWealth2048Engine.swipe(boardState, dir, nowMs = swipeNow)
        if (didMove) {
            boardState = nextState
            moveCounterTick++

            var latestSnap = ArenaProgressStore.updateBestScore2048IfHigher(context, nextState.score)
            if (nextState.lastXpAwarded > 0L) {
                roundTotalXp += nextState.lastXpAwarded
                latestSnap = ArenaProgressStore.awardXpAndTouchStreak(context, nextState.lastXpAwarded, swipeNow)
            }
            onSnapshotUpdated(latestSnap)

            nextState.lastMerges.forEach { ev ->
                val cx = (ev.col + 0.5f) / CyberWealth2048Engine.GRID_SIZE.toFloat()
                val cy = (ev.row + 0.5f) / CyberWealth2048Engine.GRID_SIZE.toFloat()
                val isHighTier = ev.resultingTier >= 6
                val count = if (isHighTier) 22 else 14
                val waveColor = if (ev.resultingTier >= 7 || nextState.isFeverActive(swipeNow)) ArenaGold else ArenaCyan
                shockwaves.add(
                    MergeShockwave(
                        normX = cx,
                        normY = cy,
                        life = 0f,
                        maxLife = 0.42f,
                        color = waveColor
                    )
                )
                for (i in 0 until count) {
                    val angle = ((2.0 * PI * i) / count) + Random.nextDouble(-0.22, 0.22)
                    val speed = Random.nextFloat() * 0.42f + 0.16f
                    val sparkColor = when {
                        i % 3 == 0 -> ArenaGold
                        i % 3 == 1 -> ArenaCyan
                        else -> Color.White
                    }
                    particles.add(
                        MergeParticle(
                            normX = cx,
                            normY = cy,
                            vx = (cos(angle) * speed).toFloat(),
                            vy = (sin(angle) * speed).toFloat() - 0.08f,
                            life = 0f,
                            maxLife = Random.nextFloat() * 0.25f + 0.36f,
                            radiusDp = Random.nextFloat() * 2.4f + 1.8f,
                            color = sparkColor
                        )
                    )
                }
            }

            if (feedbackEnabled) {
                if (nextState.lastMerges.isNotEmpty()) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (nextState.lastMerges.any { it.resultingTier >= CyberWealth2048Engine.GOLD_VAULT_TIER }) {
                        SoundEffectManager.playAchievementChime()
                    } else {
                        SoundEffectManager.playSuccessChime()
                    }
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    SoundEffectManager.playSoftKeyTick()
                }
            }
        }
    }

    val scrollState = rememberScrollState()
    val formattedScore = remember(boardState.score) {
        NumberFormat.getNumberInstance(Locale.US).format(boardState.score)
    }
    val effectiveBest = maxOf(snapshot.bestScore2048, boardState.score)
    val formattedBest = remember(effectiveBest) {
        NumberFormat.getNumberInstance(Locale.US).format(effectiveBest)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .verticalScroll(scrollState)
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Sleek Top HUD Navigation & Arena Level / XP Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onExitToHub,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("game_2048_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Cyber Wealth 2048",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaCyan.copy(alpha = 0.14f),
                        border = BorderStroke(0.75.dp, ArenaCyan.copy(alpha = 0.45f)),
                        modifier = Modifier.testTag("game_2048_level_xp_pill")
                    ) {
                        Text(
                            text = "LVL ${snapshot.levelInfo.level} ${snapshot.levelInfo.title} • ${snapshot.totalXp} XP",
                            color = ArenaCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    feedbackEnabled = !feedbackEnabled
                    onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObsidianElevated)
                    .border(1.dp, ArenaCyan.copy(alpha = 0.35f), CircleShape)
                    .testTag("game_2048_sound_toggle")
            ) {
                Icon(
                    imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (feedbackEnabled) "Mute Sound & Haptics" else "Unmute Sound & Haptics",
                    tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Glassmorphic SCORE & BEST HUD Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ScoreTelemetryCard(
                label = "SCORE",
                value = formattedScore,
                accentColor = ArenaCyan,
                modifier = Modifier
                    .weight(1f)
                    .testTag("game_2048_score_card")
            )
            ScoreTelemetryCard(
                label = "BEST",
                value = formattedBest,
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(1f)
                    .testTag("game_2048_best_card")
            )
        }

        // Gold Fever Mode Banner or Wealth Ladder Pill
        if (isFeverActive) {
            val remainingSec = ((boardState.feverExpiresAtMs - nowClockMs) / 1000L).coerceAtLeast(1L)
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = ArenaGold.copy(alpha = 0.18f),
                border = BorderStroke(1.2.dp, ArenaGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .testTag("game_2048_fever_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = ArenaGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GOLD FEVER MODE • 2X XP BONUS (${remainingSec}s)",
                        color = ArenaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = ObsidianElevated,
                border = BorderStroke(0.8.dp, ArenaCyan.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
            ) {
                Text(
                    text = "10 → 20 → 50 → 100 → 500 → 1K → 5K → 10K → GOLD VAULT",
                    color = DarkTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // 3. 4x4 Neon Glass & Gold Interactive Board (Inset horizontally away from OS edge-back strip)
        val swipeThresholdPx = with(density) { 22.dp.toPx() }
        val feverPulseTransition = rememberInfiniteTransition(label = "feverBoardPulse")
        val feverPulse by feverPulseTransition.animateFloat(
            initialValue = 0.45f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "feverPulseAlpha"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .padding(horizontal = 6.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0E131E), ObsidianBg)
                    )
                )
                .border(
                    width = if (isFeverActive) 2.5.dp else 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = if (isFeverActive) {
                            listOf(
                                ArenaGold.copy(alpha = feverPulse),
                                Color(0xFFFFA500).copy(alpha = feverPulse),
                                ArenaGold.copy(alpha = feverPulse)
                            )
                        } else {
                            listOf(ArenaCyan.copy(alpha = 0.70f), ArenaGold.copy(alpha = 0.42f))
                        }
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .pointerInput(moveCounterTick, boardState.isGameOver) {
                    var totalX = 0f
                    var totalY = 0f
                    var handledThisGesture = false
                    detectDragGestures(
                        onDragStart = {
                            totalX = 0f
                            totalY = 0f
                            handledThisGesture = false
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (handledThisGesture) return@detectDragGestures
                            totalX += dragAmount.x
                            totalY += dragAmount.y
                            if (abs(totalX) >= swipeThresholdPx || abs(totalY) >= swipeThresholdPx) {
                                handledThisGesture = true
                                val dir = if (abs(totalX) > abs(totalY)) {
                                    if (totalX > 0f) SwipeDirection.RIGHT else SwipeDirection.LEFT
                                } else {
                                    if (totalY > 0f) SwipeDirection.DOWN else SwipeDirection.UP
                                }
                                performSwipe(dir)
                            }
                        }
                    )
                }
                .semantics {
                    contentDescription = "Cyber Wealth 2048 4 by 4 game board. Swipe up, down, left, or right to merge tiles."
                }
                .testTag("cyber_wealth_2048_board")
                .padding(11.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val cellGap = 8.dp
                val cellWidthDp = (maxWidth - (cellGap * 3)) / 4f
                val cellHeightDp = (maxHeight - (cellGap * 3)) / 4f

                // Static recessed 4x4 beveled glass sockets
                for (r in 0 until CyberWealth2048Engine.GRID_SIZE) {
                    for (c in 0 until CyberWealth2048Engine.GRID_SIZE) {
                        val offsetX = (cellWidthDp + cellGap) * c
                        val offsetY = (cellHeightDp + cellGap) * r
                        Box(
                            modifier = Modifier
                                .offset(x = offsetX, y = offsetY)
                                .size(width = cellWidthDp, height = cellHeightDp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF121826).copy(alpha = 0.88f))
                                .border(0.75.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                        )
                    }
                }

                // 120fps Spring-Sliding 3D Beveled Canvas Tiles
                boardState.tiles.forEach { tile ->
                    key(tile.id) {
                        AnimatedWealthTile(
                            tile = tile,
                            cellWidth = cellWidthDp,
                            cellHeight = cellHeightDp,
                            cellGap = cellGap
                        )
                    }
                }

                // 120fps Canvas Particle Bursts & Shockwaves overlaid cleanly on merge coordinates
                Canvas(modifier = Modifier.fillMaxSize()) {
                    shockwaves.forEach { sw ->
                        val p = (sw.life / sw.maxLife).coerceIn(0f, 1f)
                        val cx = sw.normX * size.width
                        val cy = sw.normY * size.height
                        val radius = 14.dp.toPx() + (p * 42.dp.toPx())
                        drawCircle(
                            color = sw.color.copy(alpha = (1f - p) * 0.55f),
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = (3.dp.toPx() * (1f - p)).coerceAtLeast(0.5f))
                        )
                    }

                    particles.forEach { pt ->
                        val alpha = (1f - (pt.life / pt.maxLife)).coerceIn(0f, 1f)
                        val px = pt.normX * size.width
                        val py = pt.normY * size.height
                        drawCircle(
                            color = pt.color.copy(alpha = alpha),
                            radius = pt.radiusDp.dp.toPx() * (0.4f + alpha * 0.6f),
                            center = Offset(px, py)
                        )
                    }
                }

                // Gold Vault Milestone Victory Overlay or Game Over Overlay
                val showVictoryOverlay = boardState.hasReachedGoldVault && !boardState.victoryBannerDismissed
                if (showVictoryOverlay || boardState.isGameOver) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(18.dp))
                            .background(ObsidianBg.copy(alpha = 0.88f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (showVictoryOverlay) Icons.Default.EmojiEvents else Icons.Default.Psychology,
                                contentDescription = null,
                                tint = if (showVictoryOverlay) ArenaGold else ArenaCyan,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = if (showVictoryOverlay) "GOLD VAULT UNLOCKED!" else "VAULT GRID LOCKED",
                                color = if (showVictoryOverlay) ArenaGold else DarkTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Score: $formattedScore • Best: $formattedBest • +$roundTotalXp XP",
                                color = DarkTextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            if (boardState.isGameOver && boardState.undoSnapshot != null && boardState.undoPowerUpState() == UndoPowerUpState.REWARDED_AD_LOCKED) {
                                ArenaRewardedButton(
                                    text = "Undo Last Move",
                                    onClick = {
                                        AdManager.showRewardedAd("2048_undo_move") {
                                            boardState = CyberWealth2048Engine.applyUndo(boardState, viaRewardedAd = true)
                                            if (feedbackEnabled) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                SoundEffectManager.playSoftKeyTick()
                                            }
                                        }
                                    },
                                    accentColor = ArenaGold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("game_2048_rewarded_undo_overlay")
                                )
                            }
                            if (roundTotalXp > 0L) {
                                ArenaRewardedButton(
                                    text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                    enabled = !doubleXpClaimedThisRound,
                                    onClick = {
                                        AdManager.showRewardedAd("2048_double_xp") {
                                            val bonusXp = roundTotalXp
                                            if (bonusXp > 0L) {
                                                onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonusXp))
                                                roundTotalXp += bonusXp
                                            }
                                            doubleXpClaimedThisRound = true
                                        }
                                    },
                                    accentColor = ArenaGold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("game_2048_double_xp_button")
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (showVictoryOverlay) {
                                    OutlinedButton(
                                        onClick = {
                                            boardState = boardState.copy(victoryBannerDismissed = true)
                                        },
                                        border = BorderStroke(1.dp, ArenaGold),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ArenaGold),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        Text("Keep Playing", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                Button(
                                    onClick = {
                                        AdManager.recordGameRoundCompleted("2048_round_complete") {
                                            boardState = CyberWealth2048Engine.newGame()
                                            particles.clear()
                                            shockwaves.clear()
                                            roundTotalXp = 0L
                                            doubleXpClaimedThisRound = false
                                            onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ArenaCyan,
                                        contentColor = ObsidianBg
                                    ),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text("Play Again", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Clean Bottom Action Dock (Undo 1 Free + Rewarded Undo + New Game)
        val undoState = boardState.undoPowerUpState()
        val canTriggerUndo = boardState.undoSnapshot != null &&
            (undoState == UndoPowerUpState.FREE_AVAILABLE || undoState == UndoPowerUpState.REWARDED_AD_LOCKED)

        AnimatedVisibility(visible = undoAdHookMessage != null) {
            undoAdHookMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ArenaGold.copy(alpha = 0.14f),
                    border = BorderStroke(0.9.dp, ArenaGold.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 460.dp)
                ) {
                    Text(
                        text = msg,
                        color = ArenaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = ObsidianElevated,
            border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.25f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .testTag("game_2048_bottom_dock")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        when (undoState) {
                            UndoPowerUpState.FREE_AVAILABLE -> {
                                if (boardState.undoSnapshot != null) {
                                    boardState = CyberWealth2048Engine.applyUndo(boardState, viaRewardedAd = false)
                                    if (feedbackEnabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        SoundEffectManager.playSoftKeyTick()
                                    }
                                }
                            }
                            UndoPowerUpState.REWARDED_AD_LOCKED -> {
                                if (boardState.undoSnapshot != null) {
                                    AdManager.showRewardedAd("2048_undo_move") {
                                        boardState = CyberWealth2048Engine.applyUndo(boardState, viaRewardedAd = true)
                                        if (feedbackEnabled) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            SoundEffectManager.playSoftKeyTick()
                                        }
                                    }
                                } else {
                                    undoAdHookMessage = "Make a move first to undo"
                                }
                            }
                            UndoPowerUpState.EXHAUSTED -> {
                                undoAdHookMessage = "Match undo limit reached"
                            }
                        }
                    },
                    enabled = boardState.undoSnapshot != null && undoState != UndoPowerUpState.EXHAUSTED,
                    border = BorderStroke(
                        width = 1.1.dp,
                        color = if (canTriggerUndo) ArenaGold else ArenaCyan.copy(alpha = 0.30f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (canTriggerUndo) ArenaGold else DarkTextSecondary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("game_2048_undo_button")
                ) {
                    Icon(
                        imageVector = if (undoState == UndoPowerUpState.REWARDED_AD_LOCKED) Icons.Default.PlayCircle else Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo Move",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (undoState) {
                            UndoPowerUpState.FREE_AVAILABLE -> "Undo (1 Free)"
                            UndoPowerUpState.REWARDED_AD_LOCKED -> "Undo Last Move"
                            UndoPowerUpState.EXHAUSTED -> "Undo Used"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = {
                        boardState = CyberWealth2048Engine.newGame()
                        particles.clear()
                        shockwaves.clear()
                        roundTotalXp = 0L
                        doubleXpClaimedThisRound = false
                        onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArenaCyan,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("game_2048_restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "New Game",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "New Game",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreTelemetryCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ObsidianElevated,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.48f)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.09f), Color.Transparent)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = label,
                    color = DarkTextSecondary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = accentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AnimatedWealthTile(
    tile: WealthTile,
    cellWidth: Dp,
    cellHeight: Dp,
    cellGap: Dp
) {
    val density = LocalDensity.current

    val animCol by animateFloatAsState(
        targetValue = tile.col.toFloat(),
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMedium),
        label = "tileCol_${tile.id}"
    )
    val animRow by animateFloatAsState(
        targetValue = tile.row.toFloat(),
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMedium),
        label = "tileRow_${tile.id}"
    )

    val scaleAnim = remember { Animatable(if (tile.isNew) 0.25f else 1f) }
    LaunchedEffect(tile.id, tile.isNew, tile.isMerged, tile.tier) {
        if (tile.isNew) {
            scaleAnim.snapTo(0.25f)
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium)
            )
        } else if (tile.isMerged) {
            scaleAnim.snapTo(1.18f)
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.50f, stiffness = Spring.StiffnessMediumLow)
            )
        }
    }

    val xPx = with(density) { ((cellWidth + cellGap) * animCol).roundToPx() }
    val yPx = with(density) { ((cellHeight + cellGap) * animRow).roundToPx() }
    val style = remember(tile.tier) { tileStyleForTier(tile.tier) }
    val label = remember(tile.tier) { CyberWealth2048Engine.labelForTier(tile.tier) }

    Box(
        modifier = Modifier
            .offset { IntOffset(xPx, yPx) }
            .size(width = cellWidth, height = cellHeight)
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
            .testTag("wealth_tile_${tile.row}_${tile.col}"),
        contentAlignment = Alignment.Center
    ) {
        // 3D Beveled Glass & Metallic Tile Drawn Purely on Compose Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radiusPx = 14.dp.toPx()
            val corner = CornerRadius(radiusPx, radiusPx)
            val bevelInset = 2.dp.toPx()

            // 1. Subtle ambient recessed drop shadow
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.55f),
                topLeft = Offset(0f, 2.5.dp.toPx()),
                size = Size(size.width, size.height),
                cornerRadius = corner
            )

            // 2. Outer metallic base body
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = style.gradientColors,
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                ),
                cornerRadius = corner
            )

            // 3. Lower 3D darker bevel lip
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.34f)),
                    startY = size.height * 0.68f,
                    endY = size.height
                ),
                cornerRadius = corner
            )

            // 4. Inner raised 3D beveled face
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = style.innerFaceColors,
                    start = Offset(bevelInset, bevelInset),
                    end = Offset(size.width - bevelInset, size.height - bevelInset)
                ),
                topLeft = Offset(bevelInset, bevelInset),
                size = Size(
                    (size.width - bevelInset * 2f).coerceAtLeast(1f),
                    (size.height - bevelInset * 2f).coerceAtLeast(1f)
                ),
                cornerRadius = CornerRadius((radiusPx - bevelInset).coerceAtLeast(4f), (radiusPx - bevelInset).coerceAtLeast(4f))
            )

            // 5. Diagonal frosted glass specular reflection
            val glassPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width * 0.85f, 0f)
                lineTo(size.width * 0.25f, size.height * 0.52f)
                lineTo(0f, size.height * 0.52f)
                close()
            }
            drawPath(
                path = glassPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.22f), Color.Transparent),
                    startY = 0f,
                    endY = size.height * 0.52f
                )
            )

            // 6. Glowing neon metallic outer border rim
            drawRoundRect(
                brush = Brush.linearGradient(colors = style.borderColors),
                cornerRadius = corner,
                style = Stroke(width = if (tile.tier >= 7) 1.7.dp.toPx() else 1.2.dp.toPx())
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = style.textColor,
                fontSize = when {
                    label.length >= 6 -> 11.sp
                    label.length >= 4 -> 15.sp
                    label.length == 3 -> 18.sp
                    else -> 21.sp
                },
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (tile.tier >= CyberWealth2048Engine.GOLD_VAULT_TIER) {
                Text(
                    text = "★ APEX",
                    color = ObsidianBg,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

private data class WealthTileStyle(
    val gradientColors: List<Color>,
    val innerFaceColors: List<Color>,
    val borderColors: List<Color>,
    val textColor: Color
)

private fun tileStyleForTier(tier: Int): WealthTileStyle = when (tier) {
    1 -> WealthTileStyle(
        gradientColors = listOf(Color(0xFF1A2232), ObsidianBg),
        innerFaceColors = listOf(Color(0xFF1E293B), Color(0xFF111622)),
        borderColors = listOf(ArenaCyan.copy(alpha = 0.38f), Color(0xFF334155)),
        textColor = Color(0xFFE2E8F0)
    )
    2 -> WealthTileStyle(
        gradientColors = listOf(Color(0xFF10333D), Color(0xFF0A1622)),
        innerFaceColors = listOf(Color(0xFF14424E), Color(0xFF0D1D2B)),
        borderColors = listOf(ArenaCyan.copy(alpha = 0.65f), ElectricCyan.copy(alpha = 0.38f)),
        textColor = ArenaCyan
    )
    3 -> WealthTileStyle(
        gradientColors = listOf(Color(0xFF0E4D4E), Color(0xFF0A2029)),
        innerFaceColors = listOf(Color(0xFF115E59), Color(0xFF0D2D34)),
        borderColors = listOf(ArenaCyan, ElectricCyan),
        textColor = Color(0xFFA7F3D0)
    )
    4 -> WealthTileStyle(
        gradientColors = listOf(Color(0xFF0369A1), Color(0xFF0F172A)),
        innerFaceColors = listOf(Color(0xFF0284C7), Color(0xFF0C2A4D)),
        borderColors = listOf(ElectricCyan, ArenaCyan),
        textColor = Color.White
    )
    5 -> WealthTileStyle(
        gradientColors = listOf(Color(0xFF4338CA), Color(0xFF111827)),
        innerFaceColors = listOf(Color(0xFF4F46E5), Color(0xFF1E1B4B)),
        borderColors = listOf(Color(0xFF818CF8), ArenaCyan),
        textColor = Color.White
    )
    6 -> WealthTileStyle(
        gradientColors = listOf(Color(0xFF7E22CE), Color(0xFF1E1B4B)),
        innerFaceColors = listOf(Color(0xFF9333EA), Color(0xFF3B0764)),
        borderColors = listOf(Color(0xFFE879F9), ArenaGold.copy(alpha = 0.75f)),
        textColor = Color(0xFFFDF4FF)
    )
    7 -> WealthTileStyle(
        gradientColors = listOf(Color(0xFF92400E), Color(0xFF291408)),
        innerFaceColors = listOf(Color(0xFFB45309), Color(0xFF451A03)),
        borderColors = listOf(ArenaGold, ArenaAmber),
        textColor = Color(0xFFFEF3C7)
    )
    8 -> WealthTileStyle(
        gradientColors = listOf(Color(0xFFD97706), Color(0xFF451A03)),
        innerFaceColors = listOf(Color(0xFFF59E0B), Color(0xFF78350F)),
        borderColors = listOf(ArenaGold, Color.White.copy(alpha = 0.85f)),
        textColor = ArenaGold
    )
    else -> WealthTileStyle(
        gradientColors = listOf(ArenaGold, Color(0xFFF59E0B), Color(0xFFFFEC8B)),
        innerFaceColors = listOf(Color(0xFFFFEA70), Color(0xFFD97706)),
        borderColors = listOf(Color.White, ArenaCyan, ArenaGold),
        textColor = ObsidianBg
    )
}

// =============================================================================
// 8. GAME 2: TITANIUM WEALTH TOWER STACKER (ENGINE, AUDIO SYNTH & 120FPS VIEW)
// =============================================================================

private object ArenaSynthAudio {
    private const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Major + Upper Extension intervals in semitones above C5 (523.25 Hz)
    private val scaleSemitones = intArrayOf(0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23, 24)

    fun frequencyForPerfectCombo(combo: Int): Double {
        val idx = (combo - 1).coerceIn(0, scaleSemitones.lastIndex)
        val semitones = scaleSemitones[idx]
        return 523.25 * 2.0.pow(semitones / 12.0)
    }

    fun playPerfectDropScale(combo: Int, isGoldMilestone: Boolean) {
        val baseFreq = frequencyForPerfectCombo(combo)
        playSweepAndBell(
            startFreqHz = baseFreq,
            endFreqHz = baseFreq,
            durationMs = if (isGoldMilestone) 240 else 145,
            amplitude = 0.34,
            harmonicGain = if (isGoldMilestone) 0.42 else 0.25,
            overtoneRatio = if (isGoldMilestone) 1.5 else 2.0
        )
    }

    fun playLaserSlice() {
        playSweepAndBell(
            startFreqHz = 1480.0,
            endFreqHz = 360.0,
            durationMs = 95,
            amplitude = 0.28,
            harmonicGain = 0.16,
            overtoneRatio = 2.0
        )
    }

    fun playGoldMilestoneChime() {
        playSweepAndBell(
            startFreqHz = 1046.5,
            endFreqHz = 1567.98,
            durationMs = 260,
            amplitude = 0.36,
            harmonicGain = 0.38,
            overtoneRatio = 1.5
        )
    }

    fun playPowerUpActivate() {
        playSweepAndBell(
            startFreqHz = 660.0,
            endFreqHz = 1320.0,
            durationMs = 150,
            amplitude = 0.30,
            harmonicGain = 0.20,
            overtoneRatio = 2.0
        )
    }

    fun playTowerMissTone() {
        playSweepAndBell(
            startFreqHz = 280.0,
            endFreqHz = 115.0,
            durationMs = 210,
            amplitude = 0.34,
            harmonicGain = 0.18,
            overtoneRatio = 1.5
        )
    }

    fun playLaneSwitchWhoosh() {
        playSweepAndBell(
            startFreqHz = 320.0,
            endFreqHz = 690.0,
            durationMs = 55,
            amplitude = 0.22,
            harmonicGain = 0.12,
            overtoneRatio = 2.0
        )
    }

    fun playWealthGateChime(multiplier: Int) {
        val boost = (multiplier - 1).coerceIn(0, 6)
        val base = 784.0 * 2.0.pow(boost / 12.0)
        playSweepAndBell(
            startFreqHz = base,
            endFreqHz = base * 1.5,
            durationMs = 120,
            amplitude = 0.30,
            harmonicGain = 0.28,
            overtoneRatio = 2.0
        )
    }

    fun playInflationTrapBuzz() {
        playSweepAndBell(
            startFreqHz = 190.0,
            endFreqHz = 88.0,
            durationMs = 155,
            amplitude = 0.34,
            harmonicGain = 0.30,
            overtoneRatio = 1.41
        )
    }

    fun playHexCrystalPickup() {
        playSweepAndBell(
            startFreqHz = 720.0,
            endFreqHz = 960.0,
            durationMs = 45,
            amplitude = 0.22,
            harmonicGain = 0.16,
            overtoneRatio = 2.0
        )
    }

    fun playHexCrystalSnap() {
        playSweepAndBell(
            startFreqHz = 540.0,
            endFreqHz = 680.0,
            durationMs = 70,
            amplitude = 0.26,
            harmonicGain = 0.20,
            overtoneRatio = 1.5
        )
    }

    fun playHexLineClearArpeggio(linesCleared: Int, comboMultiplier: Int) {
        val tierBoost = ((linesCleared - 1) + (comboMultiplier - 1)).coerceIn(0, 8)
        val base = 659.25 * 2.0.pow(tierBoost / 12.0)
        playSweepAndBell(
            startFreqHz = base,
            endFreqHz = base * 1.5,
            durationMs = if (linesCleared >= 2) 230 else 155,
            amplitude = 0.34,
            harmonicGain = 0.36,
            overtoneRatio = 2.0
        )
    }

    fun playHexTrayRefresh() {
        playSweepAndBell(
            startFreqHz = 880.0,
            endFreqHz = 1320.0,
            durationMs = 125,
            amplitude = 0.28,
            harmonicGain = 0.24,
            overtoneRatio = 1.5
        )
    }

    fun playOpticalMirrorClick() {
        playSweepAndBell(
            startFreqHz = 1120.0,
            endFreqHz = 620.0,
            durationMs = 38,
            amplitude = 0.24,
            harmonicGain = 0.14,
            overtoneRatio = 2.0
        )
    }

    fun playStarNodeChime(starCount: Int) {
        val boost = (starCount - 1).coerceIn(0, 6)
        val freq = 987.77 * 2.0.pow((boost * 2) / 12.0)
        playSweepAndBell(
            startFreqHz = freq,
            endFreqHz = freq * 1.25,
            durationMs = 130,
            amplitude = 0.30,
            harmonicGain = 0.32,
            overtoneRatio = 2.0
        )
    }

    fun playVaultChargedChord(isThreeStar: Boolean) {
        val base = if (isThreeStar) 1046.5 else 783.99
        playSweepAndBell(
            startFreqHz = base,
            endFreqHz = base * 1.5,
            durationMs = if (isThreeStar) 320 else 230,
            amplitude = 0.36,
            harmonicGain = 0.44,
            overtoneRatio = 1.5
        )
    }

    fun playFirewallHitSpark() {
        playSweepAndBell(
            startFreqHz = 210.0,
            endFreqHz = 95.0,
            durationMs = 90,
            amplitude = 0.24,
            harmonicGain = 0.22,
            overtoneRatio = 1.41
        )
    }

    fun playDuelCorrectStreakChime(streakCount: Int) {
        val boost = (streakCount - 1).coerceIn(0, 10)
        val base = 659.25 * 2.0.pow(boost / 12.0)
        playSweepAndBell(
            startFreqHz = base,
            endFreqHz = base * 1.25,
            durationMs = if (streakCount >= 4) 170 else 115,
            amplitude = 0.30,
            harmonicGain = if (streakCount >= 4) 0.36 else 0.22,
            overtoneRatio = 2.0
        )
    }

    fun playDuelWrongBuzz() {
        playSweepAndBell(
            startFreqHz = 175.0,
            endFreqHz = 92.0,
            durationMs = 150,
            amplitude = 0.28,
            harmonicGain = 0.25,
            overtoneRatio = 1.41
        )
    }

    fun playDuelHeartbeatTick() {
        playSweepAndBell(
            startFreqHz = 880.0,
            endFreqHz = 520.0,
            durationMs = 45,
            amplitude = 0.22,
            harmonicGain = 0.12,
            overtoneRatio = 1.5
        )
    }

    fun playBreakerBrickHit(comboStreak: Int) {
        val boost = (comboStreak - 1).coerceIn(0, 12)
        val base = 587.33 * 2.0.pow(boost / 12.0)
        playSweepAndBell(
            startFreqHz = base,
            endFreqHz = base * 1.18,
            durationMs = 68,
            amplitude = 0.26,
            harmonicGain = 0.26,
            overtoneRatio = 2.0
        )
    }

    fun playBreakerPlasmaExplosion() {
        playSweepAndBell(
            startFreqHz = 145.0,
            endFreqHz = 44.0,
            durationMs = 240,
            amplitude = 0.42,
            harmonicGain = 0.34,
            overtoneRatio = 1.5
        )
    }

    fun playBreakerCapsuleCatch() {
        playSweepAndBell(
            startFreqHz = 880.0,
            endFreqHz = 1760.0,
            durationMs = 155,
            amplitude = 0.32,
            harmonicGain = 0.36,
            overtoneRatio = 1.5
        )
    }

    fun playBreakerPaddleBounce() {
        playSweepAndBell(
            startFreqHz = 420.0,
            endFreqHz = 560.0,
            durationMs = 42,
            amplitude = 0.22,
            harmonicGain = 0.14,
            overtoneRatio = 2.0
        )
    }

    fun playBreakerLaserShot() {
        playSweepAndBell(
            startFreqHz = 1220.0,
            endFreqHz = 540.0,
            durationMs = 42,
            amplitude = 0.18,
            harmonicGain = 0.14,
            overtoneRatio = 2.0
        )
    }

    fun playChronoFlashPulse() {
        playSweepAndBell(
            startFreqHz = 523.25,
            endFreqHz = 783.99,
            durationMs = 150,
            amplitude = 0.25,
            harmonicGain = 0.34,
            overtoneRatio = 1.5
        )
    }

    fun playChronoTileNote(stepIndex: Int, isGoldBonus: Boolean) {
        val pentatonicScale = doubleArrayOf(523.25, 587.33, 659.25, 783.99, 880.0, 1046.50, 1174.66, 1318.51, 1567.98)
        val base = pentatonicScale[stepIndex.coerceAtLeast(0) % pentatonicScale.size]
        val octaveMul = if (isGoldBonus) 1.5 else 1.0
        playSweepAndBell(
            startFreqHz = base * octaveMul,
            endFreqHz = base * octaveMul * if (isGoldBonus) 1.12 else 1.02,
            durationMs = if (isGoldBonus) 155 else 110,
            amplitude = if (isGoldBonus) 0.32 else 0.26,
            harmonicGain = if (isGoldBonus) 0.42 else 0.28,
            overtoneRatio = 2.0
        )
    }

    fun playChronoRoundClearChord() {
        val notes = listOf(523.25, 659.25, 783.99, 1046.50)
        scope.launch {
            notes.forEachIndexed { idx, freq ->
                playSweepAndBell(
                    startFreqHz = freq,
                    endFreqHz = freq,
                    durationMs = 140,
                    amplitude = 0.24 + idx * 0.02,
                    harmonicGain = 0.30,
                    overtoneRatio = 1.5
                )
                delay(48L)
            }
        }
    }

    fun playChronoWrongBuzz() {
        playSweepAndBell(
            startFreqHz = 185.0,
            endFreqHz = 112.0,
            durationMs = 180,
            amplitude = 0.34,
            harmonicGain = 0.26,
            overtoneRatio = 1.414
        )
    }

    fun playOrbitLaserDeflect(combo: Int) {
        val boost = (combo.coerceAtLeast(1) - 1).coerceIn(0, 10)
        val freq = 760.0 * 2.0.pow(boost / 14.0)
        playSweepAndBell(
            startFreqHz = freq * 1.35,
            endFreqHz = freq * 0.72,
            durationMs = 85,
            amplitude = 0.28,
            harmonicGain = 0.26,
            overtoneRatio = 2.0
        )
    }

    fun playOrbitYieldAbsorb(multiplier: Int) {
        val scale = doubleArrayOf(659.25, 783.99, 987.77, 1174.66, 1318.51)
        val base = scale[(multiplier - 1).coerceIn(0, scale.lastIndex)]
        playSweepAndBell(
            startFreqHz = base,
            endFreqHz = base * 1.25,
            durationMs = 150,
            amplitude = 0.30,
            harmonicGain = 0.38,
            overtoneRatio = 1.5
        )
    }

    fun playOrbitOverdriveChime() {
        val notes = listOf(783.99, 987.77, 1174.66, 1567.98)
        scope.launch {
            notes.forEachIndexed { idx, freq ->
                playSweepAndBell(
                    startFreqHz = freq,
                    endFreqHz = freq * 1.06,
                    durationMs = 135,
                    amplitude = 0.25 + idx * 0.02,
                    harmonicGain = 0.36,
                    overtoneRatio = 1.5
                )
                delay(42L)
            }
        }
    }

    fun playOrbitEmpShockwave() {
        playSweepAndBell(
            startFreqHz = 320.0,
            endFreqHz = 48.0,
            durationMs = 310,
            amplitude = 0.44,
            harmonicGain = 0.34,
            overtoneRatio = 1.5
        )
    }

    fun playOrbitLaserWarningPing() {
        playSweepAndBell(
            startFreqHz = 1420.0,
            endFreqHz = 1120.0,
            durationMs = 55,
            amplitude = 0.16,
            harmonicGain = 0.12,
            overtoneRatio = 2.0
        )
    }

    fun playOrbitCoreHitDamage() {
        playSweepAndBell(
            startFreqHz = 155.0,
            endFreqHz = 62.0,
            durationMs = 210,
            amplitude = 0.38,
            harmonicGain = 0.28,
            overtoneRatio = 1.414
        )
    }

    fun playCitadelLockShatter(isGoldBullseye: Boolean) {
        val start = if (isGoldBullseye) 1174.66 else 880.0
        playSweepAndBell(
            startFreqHz = start,
            endFreqHz = start * 1.5,
            durationMs = if (isGoldBullseye) 175 else 110,
            amplitude = if (isGoldBullseye) 0.34 else 0.28,
            harmonicGain = if (isGoldBullseye) 0.42 else 0.26,
            overtoneRatio = 1.5
        )
    }

    fun playCitadelChamberSurge() {
        playSweepAndBell(
            startFreqHz = 440.0,
            endFreqHz = 1320.0,
            durationMs = 210,
            amplitude = 0.32,
            harmonicGain = 0.34,
            overtoneRatio = 2.0
        )
    }

    fun playCitadelConduitTone(conduitIndex: Int) {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        val freq = notes[conduitIndex.coerceIn(0, notes.lastIndex)]
        playSweepAndBell(
            startFreqHz = freq,
            endFreqHz = freq * 1.05,
            durationMs = 115,
            amplitude = 0.28,
            harmonicGain = 0.32,
            overtoneRatio = 1.5
        )
    }

    fun playCitadelTierRoyalChord() {
        val chord = listOf(523.25, 659.25, 783.99, 1046.50, 1567.98)
        scope.launch {
            chord.forEachIndexed { idx, freq ->
                playSweepAndBell(
                    startFreqHz = freq,
                    endFreqHz = freq * 1.04,
                    durationMs = 175,
                    amplitude = 0.25 + idx * 0.02,
                    harmonicGain = 0.38,
                    overtoneRatio = 1.5
                )
                delay(45L)
            }
        }
    }

    fun playCitadelAegisRevive() {
        val chord = listOf(659.25, 987.77, 1318.51, 1975.53)
        scope.launch {
            chord.forEachIndexed { idx, freq ->
                playSweepAndBell(
                    startFreqHz = freq,
                    endFreqHz = freq,
                    durationMs = 150,
                    amplitude = 0.28 + idx * 0.02,
                    harmonicGain = 0.40,
                    overtoneRatio = 1.5
                )
                delay(38L)
            }
        }
    }

    fun playCitadelDamageBuzz() {
        playSweepAndBell(
            startFreqHz = 170.0,
            endFreqHz = 72.0,
            durationMs = 195,
            amplitude = 0.36,
            harmonicGain = 0.26,
            overtoneRatio = 1.414
        )
    }

    internal fun triggerTone(
        startFreqHz: Double,
        endFreqHz: Double,
        durationMs: Int,
        amplitude: Double = 0.30,
        harmonicGain: Double = 0.25,
        overtoneRatio: Double = 2.0
    ) {
        playSweepAndBell(startFreqHz, endFreqHz, durationMs, amplitude, harmonicGain, overtoneRatio)
    }

    internal fun triggerChord(freqs: List<Double>, stepDelayMs: Long = 42L, durationMs: Int = 175) {
        scope.launch {
            freqs.forEachIndexed { idx, freq ->
                playSweepAndBell(
                    startFreqHz = freq,
                    endFreqHz = freq * 1.03,
                    durationMs = durationMs,
                    amplitude = (0.24 + idx * 0.02).coerceAtMost(0.36),
                    harmonicGain = 0.36,
                    overtoneRatio = 1.5
                )
                delay(stepDelayMs)
            }
        }
    }

    private fun playSweepAndBell(
        startFreqHz: Double,
        endFreqHz: Double,
        durationMs: Int,
        amplitude: Double,
        harmonicGain: Double,
        overtoneRatio: Double
    ) {
        scope.launch {
            runCatching {
                val totalSamples = ((durationMs / 1000.0) * SAMPLE_RATE).toInt().coerceAtLeast(1)
                val attackSamples = (0.006 * SAMPLE_RATE).toInt().coerceIn(1, totalSamples / 3 + 1)
                val pcm = ShortArray(totalSamples)
                var phase = 0.0
                var harmPhase = 0.0

                for (i in 0 until totalSamples) {
                    val progress = i.toDouble() / totalSamples.toDouble()
                    val freq = startFreqHz + (endFreqHz - startFreqHz) * progress
                    phase += 2.0 * PI * freq / SAMPLE_RATE
                    harmPhase += 2.0 * PI * (freq * overtoneRatio) / SAMPLE_RATE

                    val attack = if (i < attackSamples) i.toDouble() / attackSamples.toDouble() else 1.0
                    val decay = kotlin.math.exp(-3.4 * progress) * (1.0 - progress * progress * 0.20)
                    val env = attack * decay

                    val sample = (sin(phase) + harmonicGain * sin(harmPhase)) * 0.72 * amplitude * env
                    pcm[i] = (sample.coerceIn(-0.95, 0.95) * Short.MAX_VALUE).toInt().toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioContentTypeHelper.sonification())
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(pcm.size * 2)
                    .setSessionId(AudioManager.AUDIO_SESSION_ID_GENERATE)
                    .build()

                try {
                    track.write(pcm, 0, pcm.size)
                    track.play()
                    Thread.sleep(durationMs + 45L)
                } finally {
                    runCatching { track.stop() }
                    runCatching { track.release() }
                }
            }
        }
    }
}

private object AudioContentTypeHelper {
    fun sonification(): Int = AudioAttributes.CONTENT_TYPE_SONIFICATION
}

// =============================================================================
// 8A. AAA HARDWARE HAPTIC ENGINE, SYNTH SOUND FX ENGINE & SHARED FULL-SCREEN UI
// =============================================================================

object ArenaHapticEngine {
    private fun getVibrator(context: Context): Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }.getOrNull()

    fun lightTap(context: Context, enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val v = getVibrator(context) ?: return
            if (!v.hasVibrator()) return
            v.vibrate(VibrationEffect.createOneShot(15L, 95))
        }
    }

    fun collectDoublePulse(context: Context, enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val v = getVibrator(context) ?: return
            if (!v.hasVibrator()) return
            v.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0L, 25L, 30L, 25L),
                    intArrayOf(0, 145, 0, 185),
                    -1
                )
            )
        }
    }

    fun comboVictoryWave(context: Context, enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val v = getVibrator(context) ?: return
            if (!v.hasVibrator()) return
            v.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0L, 20L, 25L, 35L, 25L, 55L),
                    intArrayOf(0, 130, 0, 185, 0, 240),
                    -1
                )
            )
        }
    }

    fun heavyCrashBuzz(context: Context, enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val v = getVibrator(context) ?: return
            if (!v.hasVibrator()) return
            v.vibrate(VibrationEffect.createOneShot(250L, 255))
        }
    }
}

object ArenaSoundEngine {
    fun playCrystalPickup(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            ArenaSynthAudio.triggerTone(
                startFreqHz = 880.0,
                endFreqHz = 1480.0,
                durationMs = 110,
                amplitude = 0.28,
                harmonicGain = 0.34,
                overtoneRatio = 2.0
            )
        }
    }

    fun playAscendingComboNote(combo: Int, enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val f = ArenaSynthAudio.frequencyForPerfectCombo(combo.coerceAtLeast(1))
            ArenaSynthAudio.triggerTone(
                startFreqHz = f,
                endFreqHz = f * 1.04,
                durationMs = 140,
                amplitude = 0.31,
                harmonicGain = 0.32,
                overtoneRatio = 1.5
            )
        }
    }

    fun playPowerUpSurge(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            ArenaSynthAudio.triggerTone(
                startFreqHz = 420.0,
                endFreqHz = 1560.0,
                durationMs = 185,
                amplitude = 0.32,
                harmonicGain = 0.28,
                overtoneRatio = 2.0
            )
        }
    }

    fun playMechanicalClick(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            ArenaSynthAudio.triggerTone(
                startFreqHz = 1280.0,
                endFreqHz = 760.0,
                durationMs = 38,
                amplitude = 0.24,
                harmonicGain = 0.16,
                overtoneRatio = 1.5
            )
        }
    }

    fun playLowBassImpactBuzz(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            ArenaSynthAudio.triggerTone(
                startFreqHz = 160.0,
                endFreqHz = 56.0,
                durationMs = 220,
                amplitude = 0.38,
                harmonicGain = 0.28,
                overtoneRatio = 1.414
            )
        }
    }

    fun playTriumphantRoyalChord(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            ArenaSynthAudio.triggerChord(
                freqs = listOf(523.25, 659.25, 783.99, 1046.50, 1567.98),
                stepDelayMs = 42L,
                durationMs = 185
            )
        }
    }
}

private data class ArenaAtmosphereStyle(
    val skyTop: Color,
    val skyMid: Color,
    val skyBottom: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val gridDensity: Int = 8
)

private fun atmosphereForGame(gameId: ArenaGameId): ArenaAtmosphereStyle = when (gameId) {
    ArenaGameId.CYBER_WEALTH_2048 -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF050B14),
        skyMid = Color(0xFF091221),
        skyBottom = Color(0xFF0A0D14),
        primaryAccent = ArenaCyan,
        secondaryAccent = ArenaGold
    )
    ArenaGameId.TITANIUM_TOWER_STACKER -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF070B12),
        skyMid = Color(0xFF0E1726),
        skyBottom = Color(0xFF14110B),
        primaryAccent = ArenaGold,
        secondaryAccent = ArenaCyan
    )
    ArenaGameId.GLOBAL_CURRENCY_RUNNER -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF040914),
        skyMid = Color(0xFF091628),
        skyBottom = Color(0xFF07111A),
        primaryAccent = ArenaCyan,
        secondaryAccent = Color(0xFF38BDF8)
    )
    ArenaGameId.CYBER_VAULT_HEXA -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF080714),
        skyMid = Color(0xFF120F26),
        skyBottom = Color(0xFF080B12),
        primaryAccent = Color(0xFFA855F7),
        secondaryAccent = ArenaCyan
    )
    ArenaGameId.QUANTUM_LASER_VAULT -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF040D12),
        skyMid = Color(0xFF091D26),
        skyBottom = Color(0xFF060B10),
        primaryAccent = ArenaCyan,
        secondaryAccent = ArenaGold
    )
    ArenaGameId.RAPID_WEALTH_MATH_DUEL -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF100B06),
        skyMid = Color(0xFF18120B),
        skyBottom = Color(0xFF090C12),
        primaryAccent = ArenaGold,
        secondaryAccent = ArenaCyan
    )
    ArenaGameId.NEON_BRICK_BREAKER -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF060814),
        skyMid = Color(0xFF0D142B),
        skyBottom = Color(0xFF13091B),
        primaryAccent = Color(0xFF38BDF8),
        secondaryAccent = ArenaGold
    )
    ArenaGameId.CHRONO_VAULT_MATRIX -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF070C17),
        skyMid = Color(0xFF0E1629),
        skyBottom = Color(0xFF141008),
        primaryAccent = ArenaGold,
        secondaryAccent = ArenaCyan
    )
    ArenaGameId.GRAVITY_ORBIT_PROTECTOR -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF04060E),
        skyMid = Color(0xFF0A1324),
        skyBottom = Color(0xFF030509),
        primaryAccent = ArenaCyan,
        secondaryAccent = Color(0xFF60A5FA)
    )
    ArenaGameId.TITANIUM_CITADEL -> ArenaAtmosphereStyle(
        skyTop = Color(0xFF0E0B06),
        skyMid = Color(0xFF17130A),
        skyBottom = Color(0xFF080A10),
        primaryAccent = ArenaGold,
        secondaryAccent = ArenaCyan
    )
}

@Composable
private fun ArenaAmbientBackdrop(
    gameId: ArenaGameId,
    isPaused: Boolean = false,
    modifier: Modifier = Modifier
) {
    val style = remember(gameId) { atmosphereForGame(gameId) }
    var phase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPaused, gameId) {
        if (isPaused) return@LaunchedEffect
        var lastNs = 0L
        while (true) {
            withFrameNanos { ns ->
                if (lastNs != 0L) {
                    val dt = ((ns - lastNs).coerceAtMost(40_000_000L)) / 1_000_000_000f
                    phase = (phase + dt * 0.45f) % 100f
                }
                lastNs = ns
            }
        }
    }

    val specks = remember(gameId) {
        val rng = Random(gameId.ordinal * 97 + 13)
        List(24) {
            Triple(rng.nextFloat(), rng.nextFloat(), 1.1f + rng.nextFloat() * 1.9f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(style.skyTop, style.skyMid, style.skyBottom)
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(style.primaryAccent.copy(alpha = 0.13f), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * 0.24f),
                radius = size.maxDimension * 0.55f
            ),
            radius = size.maxDimension * 0.55f,
            center = Offset(size.width * 0.5f, size.height * 0.24f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(style.secondaryAccent.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * 0.80f),
                radius = size.maxDimension * 0.48f
            ),
            radius = size.maxDimension * 0.48f,
            center = Offset(size.width * 0.5f, size.height * 0.80f)
        )

        val stepX = size.width / style.gridDensity.toFloat()
        for (i in 1 until style.gridDensity) {
            drawLine(
                color = style.primaryAccent.copy(alpha = 0.055f),
                start = Offset(i * stepX, 0f),
                end = Offset(i * stepX, size.height),
                strokeWidth = 0.8.dp.toPx()
            )
        }
        val rowCount = 12
        val rowStep = size.height / rowCount.toFloat()
        val scrollOff = (phase * rowStep * 0.6f) % rowStep
        for (r in 0..rowCount) {
            val ry = r * rowStep + scrollOff
            drawLine(
                color = style.secondaryAccent.copy(alpha = 0.045f),
                start = Offset(0f, ry),
                end = Offset(size.width, ry),
                strokeWidth = 0.8.dp.toPx()
            )
        }

        specks.forEachIndexed { idx, (sx, sy, sr) ->
            val yFloat = ((sy - phase * (0.04f + (idx % 3) * 0.02f)) % 1f + 1f) % 1f
            val alpha = 0.18f + 0.32f * abs(sin(phase * 2.2f + idx))
            val col = if (idx % 3 == 0) style.secondaryAccent else style.primaryAccent
            drawCircle(
                color = col.copy(alpha = alpha),
                radius = sr.dp.toPx(),
                center = Offset(sx * size.width, yFloat * size.height)
            )
        }
    }
}

private data class ArenaFloatingPopup(
    val id: Long,
    val text: String,
    val normX: Float,
    val normY: Float,
    val color: Color,
    var life: Float = 0f,
    val maxLife: Float = 0.85f
)

@Composable
private fun ArenaFloatingPopupsOverlay(
    popups: MutableList<ArenaFloatingPopup>,
    isPaused: Boolean = false,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(popups.size, isPaused) {
        if (popups.isEmpty() || isPaused) return@LaunchedEffect
        var lastNs = 0L
        while (popups.isNotEmpty() && !isPaused) {
            withFrameNanos { ns ->
                val dt = if (lastNs == 0L) 0.016f else ((ns - lastNs).coerceAtMost(40_000_000L)) / 1_000_000_000f
                lastNs = ns
                val it = popups.listIterator()
                while (it.hasNext()) {
                    val item = it.next()
                    item.life += dt
                    if (item.life >= item.maxLife) {
                        it.remove()
                    }
                }
            }
        }
    }

    if (popups.isEmpty()) return
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier.fillMaxSize()) {
        popups.forEach { pop ->
            val p = (pop.life / pop.maxLife).coerceIn(0f, 1f)
            val alpha = (1f - p * p).coerceIn(0f, 1f)
            val px = pop.normX.coerceIn(0.12f, 0.88f) * size.width
            val py = (pop.normY.coerceIn(0.15f, 0.88f) * size.height) - p * 48.dp.toPx()
            val measured = textMeasurer.measure(
                text = pop.text,
                style = TextStyle(
                    color = pop.color.copy(alpha = alpha),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            )
            drawRoundRect(
                color = ObsidianBg.copy(alpha = 0.65f * alpha),
                topLeft = Offset(px - measured.size.width * 0.5f - 8.dp.toPx(), py - 3.dp.toPx()),
                size = Size(measured.size.width + 16.dp.toPx(), measured.size.height + 6.dp.toPx()),
                cornerRadius = CornerRadius(999f, 999f)
            )
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(px - measured.size.width * 0.5f, py)
            )
        }
    }
}

private class ArenaShakeState(
    private val scope: CoroutineScope
) {
    val anim = Animatable(0f)
    var intensityPx by mutableFloatStateOf(0f)

    fun trigger(strengthDp: Float = 8f) {
        intensityPx = strengthDp
        scope.launch {
            anim.snapTo(1f)
            anim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            )
        }
    }
}

@Composable
private fun rememberArenaShakeState(): ArenaShakeState {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    return remember(scope) { ArenaShakeState(scope) }
}

private fun Modifier.arenaCameraShake(shakeState: ArenaShakeState): Modifier =
    this.graphicsLayer {
        val v = shakeState.anim.value
        if (v > 0.001f) {
            val amp = v * shakeState.intensityPx.dp.toPx()
            translationX = sin(v * 34f) * amp
            translationY = cos(v * 29f) * amp * 0.65f
        }
    }

@Composable
private fun ArenaFloatingTopHeader(
    title: String,
    subtitle: String,
    accentColor: Color,
    feedbackEnabled: Boolean,
    onBackClick: () -> Unit,
    onToggleFeedback: () -> Unit,
    backTestTag: String,
    soundTestTag: String,
    onPauseClick: (() -> Unit)? = null,
    pauseTestTag: String? = null,
    subtitleTestTag: String? = null,
    trailingBadgeText: String? = null
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = ObsidianBg.copy(alpha = 0.78f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.34f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag(backTestTag)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        color = DarkTextPrimary,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = accentColor.copy(alpha = 0.14f),
                        border = BorderStroke(0.75.dp, accentColor.copy(alpha = 0.45f)),
                        modifier = if (subtitleTestTag != null) Modifier.testTag(subtitleTestTag) else Modifier
                    ) {
                        Text(
                            text = subtitle,
                            color = accentColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (trailingBadgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.16f),
                        border = BorderStroke(0.8.dp, ArenaGold.copy(alpha = 0.58f))
                    ) {
                        Text(
                            text = trailingBadgeText,
                            color = ArenaGold,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                if (onPauseClick != null) {
                    IconButton(
                        onClick = onPauseClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ObsidianElevated.copy(alpha = 0.86f))
                            .border(1.dp, ArenaGold.copy(alpha = 0.42f), CircleShape)
                            .let { if (pauseTestTag != null) it.testTag(pauseTestTag) else it }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause Game",
                            tint = ArenaGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onToggleFeedback,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ObsidianElevated.copy(alpha = 0.86f))
                        .border(1.dp, ArenaCyan.copy(alpha = 0.38f), CircleShape)
                        .testTag(soundTestTag)
                ) {
                    Icon(
                        imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = if (feedbackEnabled) "Mute Sound & Haptics" else "Enable Sound & Haptics",
                        tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ArenaPauseOverlay(
    gameTitle: String,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExitToHub: () -> Unit,
    onLevelMap: (() -> Unit)? = null,
    testTagPrefix: String = "arena"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg.copy(alpha = 0.88f))
            .padding(24.dp)
            .testTag("${testTagPrefix}_pause_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ObsidianElevated,
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(ArenaCyan, ArenaGold, ArenaCyan))
            ),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f))
                ) {
                    Text(
                        text = "GAME PAUSED",
                        color = ArenaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = gameTitle,
                    color = DarkTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArenaCyan,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("${testTagPrefix}_resume_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Resume", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = onRestart,
                    border = BorderStroke(1.1.dp, ArenaGold.copy(alpha = 0.72f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ArenaGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("${testTagPrefix}_pause_restart_button")
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restart Run", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }
                if (onLevelMap != null) {
                    OutlinedButton(
                        onClick = onLevelMap,
                        border = BorderStroke(1.1.dp, ArenaCyan.copy(alpha = 0.62f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ArenaCyan),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Level Roadmap", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                }
                OutlinedButton(
                    onClick = onExitToHub,
                    border = BorderStroke(1.dp, DarkTextSecondary.copy(alpha = 0.45f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkTextSecondary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Exit to Mind Arena Hub", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ArenaCountdownOverlay(
    countdownStep: Int?,
    title: String,
    accentColor: Color = ArenaCyan
) {
    if (countdownStep == null) return
    val scale = remember(countdownStep) { Animatable(0.55f) }
    LaunchedEffect(countdownStep) {
        scale.snapTo(0.55f)
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium)
        )
    }

    val label = if (countdownStep > 0) "$countdownStep" else "GO!"
    val badgeColor = if (countdownStep > 0) ArenaGold else accentColor

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg.copy(alpha = 0.52f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title.uppercase(),
                color = DarkTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.6.sp
            )
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .clip(CircleShape)
                    .background(ObsidianElevated.copy(alpha = 0.92f))
                    .border(2.5.dp, badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = badgeColor,
                    fontSize = if (countdownStep > 0) 46.sp else 34.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

private data class ArenaRoadmapNodeSpec(
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val starsEarned: Int,
    val isUnlocked: Boolean,
    val isCurrentActive: Boolean
)

@Composable
private fun ArenaLevelRoadmapView(
    gameId: ArenaGameId,
    gameTitle: String,
    nodes: List<ArenaRoadmapNodeSpec>,
    totalStarsEarned: Int,
    feedbackEnabled: Boolean,
    onToggleFeedback: () -> Unit,
    onBackToHub: () -> Unit,
    onSelectLevel: (Int) -> Unit,
    backButtonTestTag: String,
    soundButtonTestTag: String,
    roadmapTestTag: String,
    nodeTagPrefix: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentActiveNode = nodes.firstOrNull { it.isCurrentActive } ?: nodes.first()
    val infiniteTransition = rememberInfiniteTransition(label = "roadmapPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 820, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "roadmapNodePulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag(roadmapTestTag)
    ) {
        ArenaAmbientBackdrop(gameId = gameId)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArenaFloatingTopHeader(
                title = gameTitle,
                subtitle = "CYBER-NEON LEVEL ROADMAP • $totalStarsEarned ★ EARNED",
                accentColor = ArenaCyan,
                feedbackEnabled = feedbackEnabled,
                onBackClick = onBackToHub,
                onToggleFeedback = onToggleFeedback,
                backTestTag = backButtonTestTag,
                soundTestTag = soundButtonTestTag
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(nodes, key = { _, item -> item.levelNumber }) { index, node ->
                    val curveX = sin(index * 0.85f) * 0.28f // -0.28..+0.28 S-curve shift
                    val nextCurveX = if (index < nodes.lastIndex) sin((index + 1) * 0.85f) * 0.28f else curveX
                    val hasNext = index < nodes.lastIndex

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(112.dp)
                    ) {
                        // Winding Glowing Neon Spline Track
                        if (hasNext) {
                            val lineColor = when {
                                node.starsEarned > 0 -> ArenaGold
                                node.isCurrentActive -> ArenaCyan
                                else -> Color(0xFF334155)
                            }
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val startX = size.width * (0.5f + curveX)
                                val startY = size.height * 0.48f
                                val endX = size.width * (0.5f + nextCurveX)
                                val endY = size.height * 1.48f
                                val midY = (startY + endY) * 0.5f
                                val path = Path().apply {
                                    moveTo(startX, startY)
                                    cubicTo(startX, midY, endX, midY, endX, endY)
                                }
                                drawPath(
                                    path = path,
                                    color = lineColor.copy(alpha = 0.24f),
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawPath(
                                    path = path,
                                    color = lineColor.copy(alpha = 0.85f),
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        pathEffect = if (node.isUnlocked) null else PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                                    )
                                )
                            }
                        }

                        // Positioned Interactive Roadmap Node + Label Card
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val leftWeight = (1f + curveX * 1.8f).coerceIn(0.25f, 1.75f)
                            val rightWeight = (2f - leftWeight).coerceIn(0.25f, 1.75f)

                            Box(
                                modifier = Modifier.weight(leftWeight),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (curveX >= 0f) {
                                    RoadmapLabelChip(node = node)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            val nodeAccent = when {
                                node.isCurrentActive -> ArenaCyan
                                node.starsEarned > 0 -> ArenaGold
                                node.isUnlocked -> ArenaCyan.copy(alpha = 0.78f)
                                else -> DarkTextSecondary.copy(alpha = 0.45f)
                            }

                            Surface(
                                onClick = {
                                    if (node.isUnlocked) {
                                        ArenaHapticEngine.lightTap(context, feedbackEnabled)
                                        ArenaSoundEngine.playMechanicalClick(feedbackEnabled)
                                        onSelectLevel(node.levelNumber)
                                    } else {
                                        ArenaHapticEngine.lightTap(context, feedbackEnabled)
                                    }
                                },
                                shape = CircleShape,
                                color = when {
                                    node.isCurrentActive -> ArenaCyan.copy(alpha = 0.22f * pulse)
                                    node.starsEarned > 0 -> ArenaGold.copy(alpha = 0.16f)
                                    node.isUnlocked -> ObsidianElevated
                                    else -> Color(0xFF0E131D)
                                },
                                border = BorderStroke(
                                    width = if (node.isCurrentActive) 2.5.dp else 1.6.dp,
                                    color = if (node.isCurrentActive) nodeAccent.copy(alpha = pulse) else nodeAccent
                                ),
                                modifier = Modifier
                                    .size(if (node.isCurrentActive) 68.dp else 60.dp)
                                    .testTag("${nodeTagPrefix}_${node.levelNumber}")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (!node.isUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked Stage ${node.levelNumber}",
                                            tint = DarkTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${node.levelNumber}",
                                            color = nodeAccent,
                                            fontSize = if (node.isCurrentActive) 20.sp else 17.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        if (node.isCurrentActive) {
                                            Text(
                                                text = "PLAY",
                                                color = ArenaCyan,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.8.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier.weight(rightWeight),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (curveX < 0f) {
                                    RoadmapLabelChip(node = node)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    ArenaHapticEngine.collectDoublePulse(context, feedbackEnabled)
                    ArenaSoundEngine.playPowerUpSurge(feedbackEnabled)
                    onSelectLevel(currentActiveNode.levelNumber)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArenaCyan,
                    contentColor = ObsidianBg
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .height(52.dp)
                    .testTag("${nodeTagPrefix}_play_active_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PLAY ${currentActiveNode.title.uppercase()} ▶",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun RoadmapLabelChip(node: ArenaRoadmapNodeSpec) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = ObsidianElevated.copy(alpha = if (node.isUnlocked) 0.88f else 0.56f),
        border = BorderStroke(
            0.9.dp,
            when {
                node.isCurrentActive -> ArenaCyan.copy(alpha = 0.72f)
                node.starsEarned > 0 -> ArenaGold.copy(alpha = 0.65f)
                else -> Color.White.copy(alpha = 0.12f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = node.title,
                color = if (node.isUnlocked) DarkTextPrimary else DarkTextSecondary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = node.subtitle,
                color = if (node.isCurrentActive) ArenaCyan else DarkTextSecondary,
                fontSize = 9.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (node.starsEarned > 0 || node.isCurrentActive) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (s in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (s <= node.starsEarned) ArenaGold else DarkTextSecondary.copy(alpha = 0.28f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

enum class TowerAltitudeBiome(
    val displayName: String,
    val badgeColor: Color,
    val skyTop: Color,
    val skyBottom: Color
) {
    CYBER_CITY_GRID(
        displayName = "Cyber Obsidian Grid",
        badgeColor = ArenaCyan,
        skyTop = Color(0xFF070B12),
        skyBottom = Color(0xFF0E1726)
    ),
    GOLD_STRATOSPHERE(
        displayName = "Royal Gold Stratosphere",
        badgeColor = ArenaGold,
        skyTop = Color(0xFF14110B),
        skyBottom = Color(0xFF231B0E)
    ),
    QUANTUM_NEBULA(
        displayName = "Quantum Star Nebula",
        badgeColor = Color(0xFF38BDF8),
        skyTop = Color(0xFF0A0D1E),
        skyBottom = Color(0xFF1A1132)
    ),
    DIAMOND_COSMOS(
        displayName = "Diamond Citadel Cosmos",
        badgeColor = Color(0xFFE0F2FE),
        skyTop = Color(0xFF050811),
        skyBottom = Color(0xFF0E2238)
    )
}

enum class TowerDropOutcome {
    PERFECT,
    SLICED,
    MISSED
}

data class TowerSlab(
    val floor: Int,
    val left: Float,
    val width: Float,
    val isGoldMilestone: Boolean,
    val isPerfectPlaced: Boolean = false
)

data class TowerDropResult(
    val nextState: TitaniumTowerGameState,
    val outcome: TowerDropOutcome,
    val cutLeft: Float? = null,
    val cutWidth: Float? = null,
    val cutEdgeX: Float? = null,
    val cutOnLeft: Boolean = false,
    val xpEarned: Long = 0L,
    val placedFloor: Int = 0,
    val isGoldMilestone: Boolean = false
)

data class TitaniumTowerGameState(
    val placedSlabs: List<TowerSlab>,
    val currentFloor: Int = 0,
    val activeLeft: Float = 0.02f,
    val activeWidth: Float = TitaniumTowerEngine.INITIAL_SLAB_WIDTH,
    val direction: Int = 1,
    val perfectCombo: Int = 0,
    val maxComboThisRun: Int = 0,
    val isGoldFeverActive: Boolean = false,
    val laserSightRemaining: Int = 1,
    val laserSightExpiresAtMs: Long = 0L,
    val revivesRemaining: Int = 1,
    val isPendingRevive: Boolean = false,
    val reviveDeadlineMs: Long = 0L,
    val isGameOver: Boolean = false,
    val totalXpThisRun: Long = 0L
) {
    fun currentBiome(): TowerAltitudeBiome = TitaniumTowerEngine.biomeForFloor(currentFloor)
    fun hasActiveLaserSight(nowMs: Long = System.currentTimeMillis()): Boolean = nowMs < laserSightExpiresAtMs
}

private data class TowerSliceDebris(
    var left: Float,
    val width: Float,
    var floorY: Float,
    var vx: Float,
    var vy: Float,
    var rotationDeg: Float,
    val vRot: Float,
    var life: Float,
    val maxLife: Float,
    val isGold: Boolean,
    val slabFloor: Int
)

private data class TowerShockwave(
    val floor: Int,
    val centerX: Float,
    val width: Float,
    var life: Float,
    val maxLife: Float,
    val isGoldFever: Boolean
)

private data class LaserCutFlash(
    val cutX: Float,
    val floor: Int,
    var life: Float,
    val maxLife: Float
)

object TitaniumTowerEngine {
    const val INITIAL_SLAB_WIDTH = 0.54f
    const val BASE_SLAB_LEFT = (1f - INITIAL_SLAB_WIDTH) / 2f
    const val PERFECT_TOLERANCE = 0.022f
    private const val REGROWTH_STEP = 0.036f
    private const val MIN_OVERLAP_WIDTH = 0.006f
    const val LASER_SIGHT_DURATION_MS = 3000L
    const val REVIVE_COUNTDOWN_MS = 3000L

    fun biomeForFloor(floor: Int): TowerAltitudeBiome = when {
        floor <= 15 -> TowerAltitudeBiome.CYBER_CITY_GRID
        floor <= 35 -> TowerAltitudeBiome.GOLD_STRATOSPHERE
        floor <= 60 -> TowerAltitudeBiome.QUANTUM_NEBULA
        else -> TowerAltitudeBiome.DIAMOND_COSMOS
    }

    fun speedForFloor(floor: Int): Float =
        (0.46f + (floor * 0.015f)).coerceAtMost(1.38f)

    fun newGame(): TitaniumTowerGameState {
        val foundation = TowerSlab(
            floor = 0,
            left = BASE_SLAB_LEFT,
            width = INITIAL_SLAB_WIDTH,
            isGoldMilestone = false,
            isPerfectPlaced = true
        )
        return TitaniumTowerGameState(
            placedSlabs = listOf(foundation),
            currentFloor = 0,
            activeLeft = 0.02f,
            activeWidth = INITIAL_SLAB_WIDTH,
            direction = 1
        )
    }

    fun stepMotion(state: TitaniumTowerGameState, dtSec: Float): TitaniumTowerGameState {
        if (state.isGameOver || state.isPendingRevive) return state
        val safeDt = dtSec.coerceIn(0f, 0.05f)
        val maxLeft = (1f - state.activeWidth).coerceAtLeast(0f)
        var nextLeft = state.activeLeft + (state.direction * speedForFloor(state.currentFloor + 1) * safeDt)
        var nextDir = state.direction

        if (nextLeft >= maxLeft) {
            nextLeft = maxLeft
            nextDir = -1
        } else if (nextLeft <= 0f) {
            nextLeft = 0f
            nextDir = 1
        }

        return state.copy(activeLeft = nextLeft, direction = nextDir)
    }

    fun dropActiveSlab(
        state: TitaniumTowerGameState,
        nowMs: Long = System.currentTimeMillis()
    ): TowerDropResult {
        if (state.isGameOver || state.isPendingRevive) {
            return TowerDropResult(nextState = state, outcome = TowerDropOutcome.MISSED)
        }

        val topSlab = state.placedSlabs.last()
        val prevLeft = topSlab.left
        val prevRight = topSlab.left + topSlab.width
        val currLeft = state.activeLeft
        val currRight = state.activeLeft + state.activeWidth

        val overlapLeft = max(prevLeft, currLeft)
        val overlapRight = min(prevRight, currRight)
        val overlapWidth = overlapRight - overlapLeft

        if (overlapWidth < MIN_OVERLAP_WIDTH) {
            val canOfferRevive = state.revivesRemaining > 0
            val missedState = state.copy(
                perfectCombo = 0,
                isGoldFeverActive = false,
                isPendingRevive = canOfferRevive,
                reviveDeadlineMs = if (canOfferRevive) nowMs + REVIVE_COUNTDOWN_MS else 0L,
                isGameOver = !canOfferRevive
            )
            return TowerDropResult(
                nextState = missedState,
                outcome = TowerDropOutcome.MISSED,
                cutLeft = currLeft,
                cutWidth = state.activeWidth,
                cutEdgeX = currLeft + state.activeWidth / 2f,
                cutOnLeft = currLeft < prevLeft
            )
        }

        val nextFloor = state.currentFloor + 1
        val isGoldMilestone = (nextFloor % 10 == 0)
        val isPerfect = abs(currLeft - prevLeft) <= PERFECT_TOLERANCE

        if (isPerfect) {
            val nextCombo = state.perfectCombo + 1
            val feverActive = nextCombo >= 3
            val grownWidth = if (feverActive && topSlab.width < INITIAL_SLAB_WIDTH) {
                (topSlab.width + REGROWTH_STEP).coerceAtMost(INITIAL_SLAB_WIDTH)
            } else {
                topSlab.width
            }
            val widthDelta = grownWidth - topSlab.width
            val placedLeft = (prevLeft - widthDelta * 0.5f).coerceIn(0f, (1f - grownWidth).coerceAtLeast(0f))

            val placed = TowerSlab(
                floor = nextFloor,
                left = placedLeft,
                width = grownWidth,
                isGoldMilestone = isGoldMilestone,
                isPerfectPlaced = true
            )
            val baseXp = 8L + (nextCombo * 2L) + (if (isGoldMilestone) 20L else 0L)
            val xpEarned = if (feverActive) baseXp * 2L else baseXp

            val spawnFromRight = (nextFloor % 2 == 1)
            val nextSpawnLeft = if (spawnFromRight) (1f - grownWidth - 0.02f).coerceAtLeast(0f) else 0.02f
            val nextSpawnDir = if (spawnFromRight) -1 else 1

            val updated = state.copy(
                placedSlabs = state.placedSlabs + placed,
                currentFloor = nextFloor,
                activeLeft = nextSpawnLeft,
                activeWidth = grownWidth,
                direction = nextSpawnDir,
                perfectCombo = nextCombo,
                maxComboThisRun = max(state.maxComboThisRun, nextCombo),
                isGoldFeverActive = feverActive,
                totalXpThisRun = state.totalXpThisRun + xpEarned
            )
            return TowerDropResult(
                nextState = updated,
                outcome = TowerDropOutcome.PERFECT,
                xpEarned = xpEarned,
                placedFloor = nextFloor,
                isGoldMilestone = isGoldMilestone
            )
        }

        // Real-time overhang laser slice
        val cutOnLeft = currLeft < prevLeft
        val cutLeft = if (cutOnLeft) currLeft else prevRight
        val cutWidth = if (cutOnLeft) (prevLeft - currLeft) else (currRight - prevRight)
        val cutEdgeX = if (cutOnLeft) prevLeft else prevRight

        val placed = TowerSlab(
            floor = nextFloor,
            left = overlapLeft,
            width = overlapWidth,
            isGoldMilestone = isGoldMilestone,
            isPerfectPlaced = false
        )
        val xpEarned = 5L + (if (isGoldMilestone) 15L else 0L)
        val spawnFromRight = (nextFloor % 2 == 1)
        val nextSpawnLeft = if (spawnFromRight) (1f - overlapWidth - 0.02f).coerceAtLeast(0f) else 0.02f
        val nextSpawnDir = if (spawnFromRight) -1 else 1

        val updated = state.copy(
            placedSlabs = state.placedSlabs + placed,
            currentFloor = nextFloor,
            activeLeft = nextSpawnLeft,
            activeWidth = overlapWidth,
            direction = nextSpawnDir,
            perfectCombo = 0,
            isGoldFeverActive = false,
            totalXpThisRun = state.totalXpThisRun + xpEarned
        )
        return TowerDropResult(
            nextState = updated,
            outcome = TowerDropOutcome.SLICED,
            cutLeft = cutLeft,
            cutWidth = cutWidth,
            cutEdgeX = cutEdgeX,
            cutOnLeft = cutOnLeft,
            xpEarned = xpEarned,
            placedFloor = nextFloor,
            isGoldMilestone = isGoldMilestone
        )
    }

    fun activateLaserSight(
        state: TitaniumTowerGameState,
        nowMs: Long = System.currentTimeMillis()
    ): TitaniumTowerGameState {
        if (state.laserSightRemaining <= 0 || state.isGameOver || state.isPendingRevive) return state
        return state.copy(
            laserSightRemaining = state.laserSightRemaining - 1,
            laserSightExpiresAtMs = nowMs + LASER_SIGHT_DURATION_MS
        )
    }

    fun useSecondChanceRevive(
        state: TitaniumTowerGameState,
        nowMs: Long = System.currentTimeMillis()
    ): TitaniumTowerGameState {
        if (!state.isPendingRevive || state.revivesRemaining <= 0) return state
        if (state.reviveDeadlineMs > 0L && nowMs > state.reviveDeadlineMs) {
            return declineOrExpireRevive(state)
        }
        val topSlab = state.placedSlabs.last()
        return state.copy(
            isPendingRevive = false,
            reviveDeadlineMs = 0L,
            revivesRemaining = state.revivesRemaining - 1,
            activeWidth = topSlab.width,
            activeLeft = (topSlab.left - 0.14f).coerceIn(0f, (1f - topSlab.width).coerceAtLeast(0f)),
            direction = 1
        )
    }

    fun declineOrExpireRevive(state: TitaniumTowerGameState): TitaniumTowerGameState {
        if (!state.isPendingRevive) return state
        return state.copy(
            isPendingRevive = false,
            reviveDeadlineMs = 0L,
            isGameOver = true
        )
    }
}

@Composable
private fun TitaniumTowerHeroBannerCard(
    spec: ArenaGameSpec,
    bestFloors: Int,
    onPlayClick: () -> Unit
) {
    Surface(
        onClick = onPlayClick,
        shape = RoundedCornerShape(16.dp),
        color = ObsidianBg,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(ArenaGold, ArenaCyan.copy(alpha = 0.90f), ArenaGold.copy(alpha = 0.78f))
            )
        ),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_2")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF14110B),
                            ObsidianBg,
                            Color(0xFF0A161F)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.height * 0.5f, size.height * 0.55f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.5f, size.height * 0.55f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.25f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left: Custom Canvas 3D Stacked Titanium / Cyan / Gold Slabs Emblem
                TitaniumTowerLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("titanium_tower_logo_canvas")
                )

                // Right: Game Info, Best Floors Pill & Royal Gold/Cyan PLAY Button
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    // Glowing Best Floors Pill
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_tower_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Tower Floors",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: $bestFloors Floors",
                                color = ArenaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaGold,
                            contentColor = ObsidianBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_tower_play_button")
                    ) {
                        Text(
                            text = "PLAY ▶",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TitaniumTowerLogoArtwork(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "towerLogoAnim")
    val hoverPhase by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "towerTopSlabSlide"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Titanium Wealth Tower 3D stacked metallic and gold slab emblem"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0A0F1A), Color(0xFF151926))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaGold.copy(alpha = 0.85f), ArenaCyan.copy(alpha = 0.75f))),
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val padX = size.width * 0.16f
        val slabHeight = size.height * 0.105f
        val depthH = slabHeight * 0.42f
        val baseY = size.height * 0.84f

        val slabSpecs = listOf(
            Triple(0.68f, listOf(Color(0xFF334155), Color(0xFF1E293B)), ArenaCyan.copy(alpha = 0.65f)),
            Triple(0.60f, listOf(Color(0xFF0E7490), Color(0xFF164E63)), ArenaCyan),
            Triple(0.52f, listOf(Color(0xFF00E5B4), Color(0xFF0F766E)), Color.White.copy(alpha = 0.8f)),
            Triple(0.46f, listOf(ArenaGold, Color(0xFFD97706)), Color.White),
            Triple(0.40f, listOf(Color(0xFF38BDF8), Color(0xFF0284C7)), ArenaCyan)
        )

        slabSpecs.forEachIndexed { idx, (widthRatio, colors, rimColor) ->
            val w = size.width * widthRatio
            val drift = if (idx == slabSpecs.lastIndex) hoverPhase * (size.width * 0.08f) else 0f
            val x = ((size.width - w) * 0.5f + drift).coerceIn(padX * 0.4f, size.width - w - padX * 0.4f)
            val y = baseY - (idx + 1) * (slabHeight + 2.dp.toPx())

            // Extruded front 3D bevel face
            drawRoundRect(
                color = colors.last().copy(alpha = 0.92f),
                topLeft = Offset(x, y + slabHeight - depthH),
                size = Size(w, depthH + 1.5.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
            // Brushed top face
            drawRoundRect(
                brush = Brush.horizontalGradient(colors),
                topLeft = Offset(x, y),
                size = Size(w, slabHeight * 0.72f),
                cornerRadius = CornerRadius(3.5.dp.toPx(), 3.5.dp.toPx())
            )
            // Rim stroke
            drawRoundRect(
                color = rimColor,
                topLeft = Offset(x, y),
                size = Size(w, slabHeight * 0.72f),
                cornerRadius = CornerRadius(3.5.dp.toPx(), 3.5.dp.toPx()),
                style = Stroke(width = 0.8.dp.toPx())
            )
        }
    }
}

@Composable
fun TitaniumTowerGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val textMeasurer = rememberTextMeasurer()

    var towerState by remember { mutableStateOf(TitaniumTowerEngine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var nowClockMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var calloutText by remember { mutableStateOf<String?>(null) }
    var calloutColor by remember { mutableStateOf(ArenaGold) }
    var laserHookNotice by remember { mutableStateOf<String?>(null) }
    var secondChanceUsedThisRun by remember { mutableStateOf(false) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }
    var extraBonusXpThisRun by remember { mutableLongStateOf(0L) }

    val debrisList = remember { mutableStateListOf<TowerSliceDebris>() }
    val shockwaves = remember { mutableStateListOf<TowerShockwave>() }
    val cutFlashes = remember { mutableStateListOf<LaserCutFlash>() }

    // Smooth camera scale/offset tracking active slabs + full-skyscraper Game Over zoom-out
    val totalTargetFloor = towerState.currentFloor.toFloat()
    val targetCameraBaseFloor = if (towerState.isGameOver) {
        0f
    } else {
        (totalTargetFloor - 6.5f).coerceAtLeast(0f)
    }
    val smoothCameraBaseFloor by animateFloatAsState(
        targetValue = targetCameraBaseFloor,
        animationSpec = spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow),
        label = "towerCameraBase"
    )
    val targetVisibleSpan = if (towerState.isGameOver) {
        max(14f, (towerState.placedSlabs.size + 2).toFloat())
    } else {
        13.5f
    }
    val smoothVisibleSpan by animateFloatAsState(
        targetValue = targetVisibleSpan,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = Spring.StiffnessLow),
        label = "towerZoomOutSpan"
    )

    // Smoothly blend sky colors across altitude biomes
    val currentBiome = towerState.currentBiome()
    var prevBiome by remember { mutableStateOf(currentBiome) }
    var skyBlend by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(currentBiome) {
        if (currentBiome != prevBiome) {
            skyBlend = 0f
            val steps = 18
            for (s in 1..steps) {
                skyBlend = s.toFloat() / steps.toFloat()
                delay(24L)
            }
            prevBiome = currentBiome
            skyBlend = 1f
        }
    }

    // Revive 3-second countdown + Laser sight countdown clock
    val isLaserActive = towerState.hasActiveLaserSight(nowClockMs)
    LaunchedEffect(towerState.laserSightExpiresAtMs, towerState.isPendingRevive, towerState.reviveDeadlineMs) {
        while (System.currentTimeMillis() < towerState.laserSightExpiresAtMs || towerState.isPendingRevive) {
            val now = System.currentTimeMillis()
            nowClockMs = now
            if (towerState.isPendingRevive && towerState.reviveDeadlineMs > 0L && now >= towerState.reviveDeadlineMs) {
                towerState = TitaniumTowerEngine.declineOrExpireRevive(towerState)
                break
            }
            delay(50L)
        }
        nowClockMs = System.currentTimeMillis()
    }

    LaunchedEffect(calloutText) {
        if (calloutText != null) {
            delay(1150L)
            calloutText = null
        }
    }
    LaunchedEffect(laserHookNotice) {
        if (laserHookNotice != null) {
            delay(2300L)
            laserHookNotice = null
        }
    }

    // 120fps master physics loop (slab oscillation + gravity debris + shockwaves + laser flashes)
    LaunchedEffect(towerState.isGameOver, towerState.isPendingRevive) {
        var lastNanos = 0L
        while (!towerState.isGameOver || debrisList.isNotEmpty() || shockwaves.isNotEmpty() || cutFlashes.isNotEmpty()) {
            withFrameNanos { frameNanos ->
                val dt = if (lastNanos == 0L) 0.016f else ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = frameNanos

                if (!towerState.isGameOver && !towerState.isPendingRevive) {
                    towerState = TitaniumTowerEngine.stepMotion(towerState, dt)
                }

                val dIt = debrisList.listIterator()
                while (dIt.hasNext()) {
                    val d = dIt.next()
                    d.life += dt
                    if (d.life >= d.maxLife) {
                        dIt.remove()
                    } else {
                        d.left += d.vx * dt
                        d.floorY += d.vy * dt
                        d.vy -= 14.5f * dt
                        d.rotationDeg += d.vRot * dt
                    }
                }

                val sIt = shockwaves.listIterator()
                while (sIt.hasNext()) {
                    val s = sIt.next()
                    s.life += dt
                    if (s.life >= s.maxLife) sIt.remove()
                }

                val cIt = cutFlashes.listIterator()
                while (cIt.hasNext()) {
                    val c = cIt.next()
                    c.life += dt
                    if (c.life >= c.maxLife) cIt.remove()
                }
            }
        }
    }

    val performDrop by rememberUpdatedState {
        val dropNow = System.currentTimeMillis()
        nowClockMs = dropNow
        val result = TitaniumTowerEngine.dropActiveSlab(towerState, dropNow)
        towerState = result.nextState

        when (result.outcome) {
            TowerDropOutcome.PERFECT -> {
                val placed = result.nextState.placedSlabs.last()
                shockwaves.add(
                    TowerShockwave(
                        floor = placed.floor,
                        centerX = placed.left + placed.width * 0.5f,
                        width = placed.width,
                        life = 0f,
                        maxLife = 0.50f,
                        isGoldFever = result.nextState.isGoldFeverActive
                    )
                )
                calloutText = when {
                    result.isGoldMilestone -> "24K GOLD MILESTONE • FLOOR ${result.placedFloor}!"
                    result.nextState.isGoldFeverActive -> "GOLD FEVER x${result.nextState.perfectCombo} • 2X XP!"
                    else -> "PERFECT! x${result.nextState.perfectCombo}"
                }
                calloutColor = if (result.nextState.isGoldFeverActive || result.isGoldMilestone) ArenaGold else ArenaCyan

                if (feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    ArenaSynthAudio.playPerfectDropScale(
                        combo = result.nextState.perfectCombo,
                        isGoldMilestone = result.isGoldMilestone
                    )
                }
            }
            TowerDropOutcome.SLICED -> {
                val cutL = result.cutLeft
                val cutW = result.cutWidth
                val cutEdge = result.cutEdgeX
                if (cutL != null && cutW != null && cutW > 0.002f) {
                    debrisList.add(
                        TowerSliceDebris(
                            left = cutL,
                            width = cutW,
                            floorY = result.placedFloor.toFloat(),
                            vx = if (result.cutOnLeft) -0.18f else 0.18f,
                            vy = -1.2f,
                            rotationDeg = 0f,
                            vRot = if (result.cutOnLeft) -165f else 165f,
                            life = 0f,
                            maxLife = 1.25f,
                            isGold = result.isGoldMilestone,
                            slabFloor = result.placedFloor
                        )
                    )
                }
                if (cutEdge != null) {
                    cutFlashes.add(LaserCutFlash(cutX = cutEdge, floor = result.placedFloor, life = 0f, maxLife = 0.28f))
                }
                if (result.isGoldMilestone) {
                    calloutText = "24K GOLD MILESTONE • FLOOR ${result.placedFloor}!"
                    calloutColor = ArenaGold
                }
                if (feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (result.isGoldMilestone) {
                        ArenaSynthAudio.playGoldMilestoneChime()
                    } else {
                        ArenaSynthAudio.playLaserSlice()
                    }
                }
            }
            TowerDropOutcome.MISSED -> {
                val missedL = result.cutLeft
                val missedW = result.cutWidth
                if (missedL != null && missedW != null) {
                    debrisList.add(
                        TowerSliceDebris(
                            left = missedL,
                            width = missedW,
                            floorY = (towerState.currentFloor + 1).toFloat(),
                            vx = if (result.cutOnLeft) -0.12f else 0.12f,
                            vy = -2.2f,
                            rotationDeg = 0f,
                            vRot = if (result.cutOnLeft) -95f else 95f,
                            life = 0f,
                            maxLife = 1.35f,
                            isGold = false,
                            slabFloor = towerState.currentFloor + 1
                        )
                    )
                }
                if (feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    ArenaSynthAudio.playTowerMissTone()
                }
            }
        }

        // Persist XP, Streak & Best Floors immediately when earned
        var latestSnap = snapshot
        if (result.placedFloor > 0) {
            latestSnap = ArenaProgressStore.updateBestTowerFloorsIfHigher(context, result.placedFloor)
        }
        if (result.xpEarned > 0L) {
            latestSnap = ArenaProgressStore.awardXpAndTouchStreak(context, result.xpEarned, dropNow)
        }
        if (latestSnap !== snapshot) {
            onSnapshotUpdated(latestSnap)
        }
    }

    val effectiveBest = max(snapshot.bestTowerFloors, towerState.currentFloor)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .navigationBarsPadding()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Sleek Top Navigation & Sound Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onExitToHub,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("game_tower_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Titanium Tower",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = currentBiome.badgeColor.copy(alpha = 0.14f),
                        border = BorderStroke(0.75.dp, currentBiome.badgeColor.copy(alpha = 0.48f))
                    ) {
                        Text(
                            text = "${currentBiome.displayName} • LVL ${snapshot.levelInfo.level}",
                            color = currentBiome.badgeColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    feedbackEnabled = !feedbackEnabled
                    onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObsidianElevated)
                    .border(1.dp, ArenaGold.copy(alpha = 0.35f), CircleShape)
                    .testTag("game_tower_sound_toggle")
            ) {
                Icon(
                    imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (feedbackEnabled) "Mute Sound & Haptics" else "Enable Sound & Haptics",
                    tint = if (feedbackEnabled) ArenaGold else DarkTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Clean Glass HUD Pills: FLOOR | BEST | COMBO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TowerHudPill(
                label = "FLOOR",
                value = "${towerState.currentFloor}",
                accentColor = ArenaCyan,
                modifier = Modifier
                    .weight(1f)
                    .testTag("tower_floor_pill")
            )
            TowerHudPill(
                label = "BEST",
                value = "$effectiveBest",
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(1f)
                    .testTag("tower_best_pill")
            )
            TowerHudPill(
                label = if (towerState.isGoldFeverActive) "FEVER 2X" else "COMBO",
                value = "x${towerState.perfectCombo}",
                accentColor = if (towerState.isGoldFeverActive) ArenaGold else ElectricCyan,
                modifier = Modifier
                    .weight(1f)
                    .testTag("tower_combo_pill")
            )
        }

        // 3. Main 120fps Interactive 3D Canvas Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .weight(1f)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(
                    width = if (towerState.isGoldFeverActive) 2.dp else 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = if (towerState.isGoldFeverActive) {
                            listOf(ArenaGold, Color(0xFFFFA500), ArenaCyan)
                        } else {
                            listOf(ArenaCyan.copy(alpha = 0.65f), ArenaGold.copy(alpha = 0.45f))
                        }
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .clipToBounds()
                .pointerInput(towerState.isGameOver, towerState.isPendingRevive) {
                    detectTapGestures(
                        onPress = {
                            if (!towerState.isGameOver && !towerState.isPendingRevive) {
                                performDrop()
                            }
                        }
                    )
                }
                .semantics {
                    contentDescription = "Titanium Wealth Tower viewport. Tap anywhere to drop the moving slab onto the tower."
                }
                .testTag("titanium_tower_canvas_stage")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val skyTop = lerp(prevBiome.skyTop, currentBiome.skyTop, skyBlend)
                val skyBottom = lerp(prevBiome.skyBottom, currentBiome.skyBottom, skyBlend)

                // A. Altitude Sky Background
                drawRect(
                    brush = Brush.verticalGradient(colors = listOf(skyTop, skyBottom))
                )

                // Decorative atmospheric stars / cyber grid lines scaled gently by camera altitude
                val gridAlpha = if (towerState.currentFloor <= 20) 0.14f else 0.06f
                val stepX = size.width / 8f
                for (i in 1 until 8) {
                    drawLine(
                        color = ArenaCyan.copy(alpha = gridAlpha),
                        start = Offset(i * stepX, 0f),
                        end = Offset(i * stepX, size.height),
                        strokeWidth = 0.7.dp.toPx()
                    )
                }

                // Twinkling starlight & nebula dots above floor 12
                if (towerState.currentFloor >= 10) {
                    val starStrength = ((towerState.currentFloor - 10) / 25f).coerceIn(0.2f, 1f)
                    for (s in 0 until 26) {
                        val sx = ((s * 73 + 19) % 100) / 100f * size.width
                        val sy = (((s * 41 + 7) % 100) / 100f * size.height + smoothCameraBaseFloor * 14f) % size.height
                        val starRadius = ((s % 3) + 1) * 0.85.dp.toPx()
                        val starColor = when {
                            s % 5 == 0 -> ArenaGold.copy(alpha = 0.65f * starStrength)
                            s % 3 == 0 -> ArenaCyan.copy(alpha = 0.55f * starStrength)
                            else -> Color.White.copy(alpha = 0.50f * starStrength)
                        }
                        drawCircle(color = starColor, radius = starRadius, center = Offset(sx, sy))
                    }
                }

                // Coordinate projection helpers
                val slabPitchPx = (size.height * 0.80f) / smoothVisibleSpan.coerceAtLeast(10f)
                val slabFaceHeightPx = (slabPitchPx * 0.56f).coerceIn(6.dp.toPx(), 26.dp.toPx())
                val slabDepthPx = (slabPitchPx * 0.34f).coerceIn(3.dp.toPx(), 16.dp.toPx())
                val baseBottomY = size.height * 0.91f
                fun screenTopY(floorValue: Float): Float =
                    baseBottomY - ((floorValue - smoothCameraBaseFloor) * slabPitchPx) - slabPitchPx

                // B. Live "All-Time Best" Ghost Line across previous record floor
                if (snapshot.bestTowerFloors > 0) {
                    val recordY = screenTopY(snapshot.bestTowerFloors.toFloat())
                    if (recordY in 12f..(size.height - 12f)) {
                        drawLine(
                            color = ArenaGold.copy(alpha = 0.82f),
                            start = Offset(12.dp.toPx(), recordY),
                            end = Offset(size.width - 12.dp.toPx(), recordY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                        val recordLabel = "🏆 YOUR RECORD (${snapshot.bestTowerFloors})"
                        val textLayout = textMeasurer.measure(
                            text = recordLabel,
                            style = TextStyle(
                                color = ArenaGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        val pillPadH = 8.dp.toPx()
                        val pillW = textLayout.size.width + pillPadH * 2f
                        val pillH = textLayout.size.height + 6.dp.toPx()
                        val pillX = size.width - pillW - 12.dp.toPx()
                        val pillY = (recordY - pillH - 4.dp.toPx()).coerceAtLeast(6.dp.toPx())
                        drawRoundRect(
                            color = ObsidianBg.copy(alpha = 0.88f),
                            topLeft = Offset(pillX, pillY),
                            size = Size(pillW, pillH),
                            cornerRadius = CornerRadius(999f, 999f)
                        )
                        drawRoundRect(
                            color = ArenaGold.copy(alpha = 0.72f),
                            topLeft = Offset(pillX, pillY),
                            size = Size(pillW, pillH),
                            cornerRadius = CornerRadius(999f, 999f),
                            style = Stroke(width = 0.9.dp.toPx())
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(pillX + pillPadH, pillY + 3.dp.toPx())
                        )
                    }
                }

                // C. Placed 3D Beveled Metallic Tower Slabs
                towerState.placedSlabs.forEach { slab ->
                    val topY = screenTopY(slab.floor.toFloat())
                    if (topY > -slabPitchPx * 2f && topY < size.height + slabPitchPx) {
                        val x = slab.left * size.width
                        val w = slab.width * size.width
                        draw3DSlab(
                            topLeft = Offset(x, topY),
                            width = w,
                            faceHeight = slabFaceHeightPx,
                            extrudeDepth = slabDepthPx,
                            floor = slab.floor,
                            isGoldMilestone = slab.isGoldMilestone,
                            isGoldFever = towerState.isGoldFeverActive && slab.floor == towerState.currentFloor
                        )
                    }
                }

                // D. Tactical Laser Aim Sight (vertical neon alignment beams from top slab)
                if (isLaserActive && !towerState.isGameOver && !towerState.isPendingRevive) {
                    val topSlab = towerState.placedSlabs.last()
                    val leftEdgeX = topSlab.left * size.width
                    val rightEdgeX = (topSlab.left + topSlab.width) * size.width
                    val guideTopY = screenTopY((towerState.currentFloor + 3).toFloat()).coerceAtLeast(0f)
                    val guideBottomY = screenTopY(towerState.currentFloor.toFloat()) + slabFaceHeightPx

                    listOf(leftEdgeX, rightEdgeX).forEach { beamX ->
                        drawLine(
                            color = ArenaCyan.copy(alpha = 0.25f),
                            start = Offset(beamX, guideTopY),
                            end = Offset(beamX, guideBottomY),
                            strokeWidth = 6.dp.toPx()
                        )
                        drawLine(
                            color = ArenaCyan.copy(alpha = 0.95f),
                            start = Offset(beamX, guideTopY),
                            end = Offset(beamX, guideBottomY),
                            strokeWidth = 1.6.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                        )
                    }
                }

                // E. Active Moving Slab Hovering Above Top Floor
                if (!towerState.isGameOver && !towerState.isPendingRevive) {
                    val activeFloor = towerState.currentFloor + 1
                    val topY = screenTopY(activeFloor.toFloat())
                    val x = towerState.activeLeft * size.width
                    val w = towerState.activeWidth * size.width
                    draw3DSlab(
                        topLeft = Offset(x, topY),
                        width = w,
                        faceHeight = slabFaceHeightPx,
                        extrudeDepth = slabDepthPx,
                        floor = activeFloor,
                        isGoldMilestone = (activeFloor % 10 == 0),
                        isGoldFever = towerState.isGoldFeverActive
                    )
                }

                // F. Overhanging Falling Slice Debris with Gravity & Rotation
                debrisList.forEach { d ->
                    val alpha = (1f - (d.life / d.maxLife)).coerceIn(0f, 1f)
                    val dx = d.left * size.width
                    val dy = screenTopY(d.floorY)
                    val dw = (d.width * size.width).coerceAtLeast(2f)
                    val pivot = Offset(dx + dw * 0.5f, dy + slabFaceHeightPx * 0.5f)
                    rotate(degrees = d.rotationDeg, pivot = pivot) {
                        draw3DSlab(
                            topLeft = Offset(dx, dy),
                            width = dw,
                            faceHeight = slabFaceHeightPx,
                            extrudeDepth = slabDepthPx,
                            floor = d.slabFloor,
                            isGoldMilestone = d.isGold,
                            isGoldFever = false,
                            alpha = alpha
                        )
                    }
                }

                // G. Real-time Neon Laser Cut Flash
                cutFlashes.forEach { cf ->
                    val p = (cf.life / cf.maxLife).coerceIn(0f, 1f)
                    val alpha = (1f - p).coerceIn(0f, 1f)
                    val fx = cf.cutX * size.width
                    val fy = screenTopY(cf.floor.toFloat())
                    drawLine(
                        color = Color(0xFFFF2E93).copy(alpha = alpha * 0.55f),
                        start = Offset(fx, fy - 24.dp.toPx()),
                        end = Offset(fx, fy + slabPitchPx + 18.dp.toPx()),
                        strokeWidth = 6.dp.toPx() * (1f - p * 0.5f)
                    )
                    drawLine(
                        color = Color.White.copy(alpha = alpha),
                        start = Offset(fx, fy - 20.dp.toPx()),
                        end = Offset(fx, fy + slabPitchPx + 12.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // H. Perfect Drop Shockwave Rings
                shockwaves.forEach { sw ->
                    val p = (sw.life / sw.maxLife).coerceIn(0f, 1f)
                    val alpha = (1f - p) * 0.78f
                    val sy = screenTopY(sw.floor.toFloat()) + slabFaceHeightPx * 0.5f
                    val baseW = sw.width * size.width
                    val expandPx = p * 56.dp.toPx()
                    val cx = sw.centerX * size.width
                    val waveColor = if (sw.isGoldFever) ArenaGold else ArenaCyan
                    drawRoundRect(
                        color = waveColor.copy(alpha = alpha),
                        topLeft = Offset(cx - (baseW * 0.5f + expandPx), sy - slabFaceHeightPx * (0.55f + p * 0.45f)),
                        size = Size(baseW + expandPx * 2f, slabFaceHeightPx * (1.1f + p * 0.9f)),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                        style = Stroke(width = (3.dp.toPx() * (1f - p)).coerceAtLeast(0.8f))
                    )
                }
            }

            // Floating Perfect / Fever / Milestone Callout
            androidx.compose.animation.AnimatedVisibility(
                visible = calloutText != null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ObsidianBg.copy(alpha = 0.88f),
                    border = BorderStroke(1.2.dp, calloutColor)
                ) {
                    Text(
                        text = calloutText.orEmpty(),
                        color = calloutColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // Tap Hint on floor 0
            if (towerState.currentFloor == 0 && !towerState.isGameOver && !towerState.isPendingRevive) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ObsidianBg.copy(alpha = 0.76f),
                    border = BorderStroke(0.9.dp, ArenaCyan.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "TAP ANYWHERE TO DROP SLAB • PERFECT = COMBO + XP",
                        color = ArenaCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            // Second Chance Revive 3-Second Countdown Overlay
            if (towerState.isPendingRevive) {
                val remainingMs = (towerState.reviveDeadlineMs - nowClockMs).coerceIn(0L, TitaniumTowerEngine.REVIVE_COUNTDOWN_MS)
                val progress = remainingMs.toFloat() / TitaniumTowerEngine.REVIVE_COUNTDOWN_MS.toFloat()
                val secLabel = ((remainingMs / 1000L) + 1L).coerceIn(1L, 3L)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBg.copy(alpha = 0.78f)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = ObsidianElevated,
                        border = BorderStroke(1.5.dp, ArenaGold),
                        modifier = Modifier
                            .fillMaxWidth(0.86f)
                            .testTag("tower_revive_overlay")
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                tint = ArenaGold,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "SECOND CHANCE REVIVE!",
                                color = ArenaGold,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Missed at Floor ${towerState.currentFloor} • Auto-ending in ${secLabel}s",
                                color = DarkTextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            LinearProgressIndicator(
                                progress = { progress },
                                color = ArenaGold,
                                trackColor = ObsidianBg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(999.dp))
                            )
                            Button(
                                onClick = {
                                    towerState = TitaniumTowerEngine.useSecondChanceRevive(towerState, System.currentTimeMillis())
                                    if (feedbackEnabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        ArenaSynthAudio.playPowerUpActivate()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ArenaGold,
                                    contentColor = ObsidianBg
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("tower_use_revive_button")
                            ) {
                                Text(
                                    text = "REVIVE TOWER (1 FREE)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    towerState = TitaniumTowerEngine.declineOrExpireRevive(towerState)
                                },
                                border = BorderStroke(1.dp, DarkTextSecondary.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkTextSecondary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("View Final Tower", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // End-of-Game Summary Card (compact bottom overlay so zoomed-out skyscraper remains visible above!)
            if (towerState.isGameOver) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ObsidianElevated.copy(alpha = 0.94f),
                    border = BorderStroke(1.4.dp, ArenaGold.copy(alpha = 0.85f)),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(12.dp)
                        .testTag("tower_game_over_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = ArenaGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "TOWER COMPLETE • ${towerState.currentFloor} FLOORS",
                                color = ArenaGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        val displayedTowerXp = towerState.totalXpThisRun + extraBonusXpThisRun
                        Text(
                            text = "Best: $effectiveBest Floors • Max Combo: x${towerState.maxComboThisRun} • +$displayedTowerXp XP",
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center
                        )
                        if (!secondChanceUsedThisRun) {
                            ArenaRewardedButton(
                                text = "Second Chance — Continue",
                                onClick = {
                                    AdManager.showRewardedAd("tower_second_chance") {
                                        val topSlab = towerState.placedSlabs.last()
                                        towerState = towerState.copy(
                                            isGameOver = false,
                                            isPendingRevive = false,
                                            reviveDeadlineMs = 0L,
                                            activeWidth = topSlab.width,
                                            activeLeft = (topSlab.left - 0.14f).coerceIn(0f, (1f - topSlab.width).coerceAtLeast(0f)),
                                            direction = 1
                                        )
                                        secondChanceUsedThisRun = true
                                    }
                                },
                                accentColor = ArenaGold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("tower_rewarded_second_chance_button")
                            )
                        }
                        if (towerState.totalXpThisRun > 0L) {
                            ArenaRewardedButton(
                                text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                enabled = !doubleXpClaimedThisRound,
                                onClick = {
                                    AdManager.showRewardedAd("tower_double_xp") {
                                        val bonus = towerState.totalXpThisRun
                                        if (bonus > 0L) {
                                            onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                            extraBonusXpThisRun += bonus
                                        }
                                        doubleXpClaimedThisRound = true
                                    }
                                },
                                accentColor = ArenaCyan,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("tower_double_xp_button")
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    AdManager.recordGameRoundCompleted("tower_round_complete") {
                                        onExitToHub()
                                    }
                                },
                                border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ArenaCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Arena Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    AdManager.recordGameRoundCompleted("tower_round_complete") {
                                        towerState = TitaniumTowerEngine.newGame()
                                        debrisList.clear()
                                        shockwaves.clear()
                                        cutFlashes.clear()
                                        calloutText = null
                                        secondChanceUsedThisRun = false
                                        doubleXpClaimedThisRound = false
                                        extraBonusXpThisRun = 0L
                                        onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ArenaCyan,
                                    contentColor = ObsidianBg
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("tower_play_again_button")
                            ) {
                                Text("Play Again", fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = laserHookNotice != null) {
            laserHookNotice?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ArenaGold.copy(alpha = 0.14f),
                    border = BorderStroke(0.9.dp, ArenaGold.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                ) {
                    Text(
                        text = msg,
                        color = ArenaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 4. Bottom Tactical Power-Up & Restart Dock
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ObsidianElevated,
            border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.28f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .testTag("game_tower_bottom_dock")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val laserSecondsLeft = if (isLaserActive) {
                    val tenths = ((towerState.laserSightExpiresAtMs - nowClockMs) / 100L).coerceAtLeast(1L)
                    "${tenths / 10}.${tenths % 10}s"
                } else {
                    null
                }
                val canActivateLaser = towerState.laserSightRemaining > 0 && !towerState.isGameOver && !towerState.isPendingRevive

                OutlinedButton(
                    onClick = {
                        when {
                            isLaserActive -> Unit
                            canActivateLaser -> {
                                val t = System.currentTimeMillis()
                                nowClockMs = t
                                towerState = TitaniumTowerEngine.activateLaserSight(towerState, t)
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    ArenaSynthAudio.playPowerUpActivate()
                                }
                            }
                            else -> {
                                laserHookNotice = "Free Laser Sight used this match"
                            }
                        }
                    },
                    border = BorderStroke(
                        width = 1.1.dp,
                        color = when {
                            isLaserActive -> ArenaCyan
                            canActivateLaser -> ArenaGold
                            else -> ArenaCyan.copy(alpha = 0.28f)
                        }
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = when {
                            isLaserActive -> ArenaCyan
                            canActivateLaser -> ArenaGold
                            else -> DarkTextSecondary
                        }
                    ),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("tower_laser_sight_button")
                ) {
                    Icon(
                        imageVector = if (towerState.isGoldFeverActive) Icons.Default.Bolt else Icons.Default.GpsFixed,
                        contentDescription = "Laser Aim Sight",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            laserSecondsLeft != null -> "Laser Sight $laserSecondsLeft"
                            towerState.laserSightRemaining > 0 -> "Laser Aim Sight (1 Free)"
                            else -> "Laser Sight Used"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = {
                        towerState = TitaniumTowerEngine.newGame()
                        debrisList.clear()
                        shockwaves.clear()
                        cutFlashes.clear()
                        calloutText = null
                        secondChanceUsedThisRun = false
                        doubleXpClaimedThisRound = false
                        extraBonusXpThisRun = 0L
                        onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArenaCyan,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("tower_restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Tower",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Restart",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TowerHudPill(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = ObsidianElevated,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = DarkTextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.9.sp,
                maxLines = 1
            )
            Text(
                text = value,
                color = accentColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.draw3DSlab(
    topLeft: Offset,
    width: Float,
    faceHeight: Float,
    extrudeDepth: Float,
    floor: Int,
    isGoldMilestone: Boolean,
    isGoldFever: Boolean,
    alpha: Float = 1f
) {
    val safeW = width.coerceAtLeast(2f)
    val radius = CornerRadius(4.dp.toPx(), 4.dp.toPx())

    val topColors: List<Color>
    val frontColor: Color
    val rimColor: Color

    if (isGoldMilestone) {
        topColors = listOf(Color(0xFFFFF3B0).copy(alpha = alpha), ArenaGold.copy(alpha = alpha), Color(0xFFF59E0B).copy(alpha = alpha))
        frontColor = Color(0xFFB45309).copy(alpha = alpha)
        rimColor = Color.White.copy(alpha = 0.92f * alpha)
    } else {
        // Metallic titanium -> cyan -> cobalt gradient cycle every 24 floors
        val phase = ((floor % 24) / 24f) * (2f * PI.toFloat())
        val mix = (sin(phase) * 0.5f + 0.5f)
        val c1 = lerp(Color(0xFF94A3B8), Color(0xFF00FFCC), mix).copy(alpha = alpha)
        val c2 = lerp(Color(0xFF475569), Color(0xFF0284C7), mix).copy(alpha = alpha)
        topColors = listOf(c1, c2)
        frontColor = lerp(Color(0xFF1E293B), Color(0xFF0F3E52), mix).copy(alpha = alpha)
        rimColor = if (isGoldFever) {
            ArenaGold.copy(alpha = 0.90f * alpha)
        } else {
            lerp(Color(0xFFCBD5E1), ArenaCyan, mix).copy(alpha = 0.72f * alpha)
        }
    }

    // Gold Fever subtle outer glow behind active / top slab
    if (isGoldFever || isGoldMilestone) {
        drawRoundRect(
            color = ArenaGold.copy(alpha = 0.24f * alpha),
            topLeft = Offset(topLeft.x - 2.dp.toPx(), topLeft.y - 2.dp.toPx()),
            size = Size(safeW + 4.dp.toPx(), faceHeight + extrudeDepth + 4.dp.toPx()),
            cornerRadius = radius
        )
    }

    // 1. Extruded 3D front thickness bevel
    drawRoundRect(
        color = frontColor,
        topLeft = Offset(topLeft.x, topLeft.y + faceHeight * 0.45f),
        size = Size(safeW, faceHeight * 0.55f + extrudeDepth),
        cornerRadius = radius
    )

    // 2. Top brushed titanium / 24K gold surface
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = topColors,
            startX = topLeft.x,
            endX = topLeft.x + safeW
        ),
        topLeft = topLeft,
        size = Size(safeW, faceHeight),
        cornerRadius = radius
    )

    // 3. Specular light band along upper edge
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.34f * alpha), Color.Transparent),
            startY = topLeft.y,
            endY = topLeft.y + faceHeight * 0.48f
        ),
        topLeft = topLeft,
        size = Size(safeW, faceHeight * 0.48f),
        cornerRadius = radius
    )

    // 4. Crisp neon / metallic rim
    drawRoundRect(
        color = rimColor,
        topLeft = topLeft,
        size = Size(safeW, faceHeight),
        cornerRadius = radius,
        style = Stroke(width = if (isGoldMilestone) 1.3.dp.toPx() else 0.9.dp.toPx())
    )
}

// =============================================================================
// 9. GAME 3: GLOBAL CURRENCY & INFLATION SURVIVAL (120FPS 3-LANE CYBER RUNNER)
// =============================================================================

private val RunnerTrapRed = Color(0xFFFF3366)

enum class RunnerCurrencyZone(
    val label: String,
    val symbol: String,
    val accentColor: Color
) {
    USD(label = "USD $", symbol = "$", accentColor = ArenaCyan),
    EUR(label = "EUR €", symbol = "€", accentColor = Color(0xFF38BDF8)),
    GBP(label = "GBP £", symbol = "£", accentColor = ArenaGold),
    JPY(label = "JPY ¥", symbol = "¥", accentColor = Color(0xFFA78BFA)),
    INR(label = "INR ₹", symbol = "₹", accentColor = Color(0xFF10B981))
}

enum class RunnerGateKind {
    WEALTH_GATE,
    INFLATION_TRAP,
    CURRENCY_CRYSTAL
}

data class RunnerGate(
    val id: Long,
    val lane: Int,
    val depthProgress: Float,
    val kind: RunnerGateKind,
    val label: String,
    val magnetLaneOffset: Float = 0f,
    val resolved: Boolean = false
)

enum class RunnerCollisionType {
    WEALTH_BOOST,
    CRYSTAL_COLLECTED,
    TRAP_BLOCKED_BY_SHIELD,
    TRAP_DAMAGE,
    TRIGGERED_GAME_OVER
}

data class RunnerCollisionEvent(
    val lane: Int,
    val type: RunnerCollisionType,
    val label: String,
    val pointsDelta: Long,
    val xpDelta: Long
)

data class CurrencyRunnerState(
    val playerLane: Int = 1,
    val score: Long = 0L,
    val distanceAccumulator: Float = 0f,
    val wealthCombo: Int = 0,
    val multiplier: Int = 1,
    val maxMultiplierReached: Int = 1,
    val shieldIntegrity: Int = 100,
    val currencyZoneIndex: Int = 0,
    val gates: List<RunnerGate> = emptyList(),
    val nextGateId: Long = 1L,
    val spawnCooldownSec: Float = 0.25f,
    val gatesSpawnedCount: Int = 0,
    val goldenShieldRemaining: Int = 1,
    val goldenShieldExpiresAtMs: Long = 0L,
    val magnetRemaining: Int = 1,
    val magnetExpiresAtMs: Long = 0L,
    val revivesRemaining: Int = 1,
    val isPendingRevive: Boolean = false,
    val reviveDeadlineMs: Long = 0L,
    val isGameOver: Boolean = false,
    val totalXpThisRun: Long = 0L
) {
    fun currentZone(): RunnerCurrencyZone {
        val entries = RunnerCurrencyZone.entries
        return entries[(currencyZoneIndex % entries.size + entries.size) % entries.size]
    }

    fun isGoldenShieldActive(nowMs: Long = System.currentTimeMillis()): Boolean = nowMs < goldenShieldExpiresAtMs
    fun isMagnetActive(nowMs: Long = System.currentTimeMillis()): Boolean = nowMs < magnetExpiresAtMs
}

data class RunnerStepOutput(
    val nextState: CurrencyRunnerState,
    val events: List<RunnerCollisionEvent> = emptyList(),
    val xpEarned: Long = 0L
)

private data class RunnerParticle(
    var normX: Float,
    var normY: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val radiusDp: Float,
    val color: Color
)

object CurrencyRunnerEngine {
    const val MAX_SHIELD = 100
    const val TRAP_DAMAGE = 35
    const val GOLDEN_SHIELD_DURATION_MS = 5000L
    const val WEALTH_MAGNET_DURATION_MS = 6000L
    const val REVIVE_COUNTDOWN_MS = 3000L

    private val wealthLabels = listOf("+25% SIP", "2x Compound", "Tax Saver", "Gold Surge")
    private val trapLabels = listOf("-30% Inflation", "Hidden Fee", "Debt Spike", "Impulse Trap")

    fun newGame(): CurrencyRunnerState = CurrencyRunnerState()

    fun shiftLane(state: CurrencyRunnerState, delta: Int): Pair<CurrencyRunnerState, Boolean> {
        if (state.isGameOver || state.isPendingRevive) return state to false
        val target = (state.playerLane + delta).coerceIn(0, 2)
        if (target == state.playerLane) return state to false
        return state.copy(playerLane = target) to true
    }

    fun speedForScore(score: Long): Float =
        (0.56f + (score / 4500f).coerceAtMost(0.68f))

    fun stepSimulation(
        state: CurrencyRunnerState,
        dtSec: Float,
        nowMs: Long = System.currentTimeMillis(),
        random: Random = Random.Default
    ): RunnerStepOutput {
        if (state.isGameOver || state.isPendingRevive) {
            return RunnerStepOutput(nextState = state)
        }

        val safeDt = dtSec.coerceIn(0f, 0.05f)
        val trackSpeed = speedForScore(state.score)
        val magnetOn = state.isMagnetActive(nowMs)
        val shieldOn = state.isGoldenShieldActive(nowMs)

        // Advance distance points smoothly
        val rawDistance = state.distanceAccumulator + (safeDt * 42f * (0.85f + trackSpeed))
        val wholeDistPoints = rawDistance.toLong()
        val remDist = rawDistance - wholeDistPoints
        var score = state.score + (wholeDistPoints * state.multiplier)

        var combo = state.wealthCombo
        var multiplier = state.multiplier
        var maxMult = state.maxMultiplierReached
        var shield = state.shieldIntegrity
        var pendingRevive = false
        var reviveDeadline = state.reviveDeadlineMs
        var gameOver = false
        var xpDeltaTotal = 0L
        val events = mutableListOf<RunnerCollisionEvent>()

        // Advance incoming gates toward player plane (depth >= 0.92f triggers collision check)
        val advancedGates = state.gates.mapNotNull { gate ->
            val nextDepth = gate.depthProgress + (trackSpeed * safeDt)
            if (nextDepth > 1.12f) {
                null
            } else {
                val pulledOffset = if (magnetOn && gate.kind == RunnerGateKind.CURRENCY_CRYSTAL && !gate.resolved) {
                    val targetOffset = (state.playerLane - gate.lane).toFloat()
                    gate.magnetLaneOffset + (targetOffset - gate.magnetLaneOffset) * (safeDt * 9f).coerceAtMost(1f)
                } else {
                    gate.magnetLaneOffset
                }
                gate.copy(depthProgress = nextDepth, magnetLaneOffset = pulledOffset)
            }
        }.toMutableList()

        for (idx in advancedGates.indices) {
            val g = advancedGates[idx]
            if (!g.resolved && g.depthProgress >= 0.90f) {
                val collectedByMagnet = magnetOn && g.kind == RunnerGateKind.CURRENCY_CRYSTAL
                if (g.lane == state.playerLane || collectedByMagnet) {
                    advancedGates[idx] = g.copy(resolved = true)
                    when (g.kind) {
                        RunnerGateKind.WEALTH_GATE -> {
                            combo += 1
                            multiplier = (1 + combo / 2).coerceAtMost(8)
                            maxMult = max(maxMult, multiplier)
                            val pts = 150L * multiplier
                            val xp = 6L * multiplier
                            score += pts
                            xpDeltaTotal += xp
                            shield = (shield + 4).coerceAtMost(MAX_SHIELD)
                            events += RunnerCollisionEvent(
                                lane = g.lane,
                                type = RunnerCollisionType.WEALTH_BOOST,
                                label = g.label,
                                pointsDelta = pts,
                                xpDelta = xp
                            )
                        }
                        RunnerGateKind.CURRENCY_CRYSTAL -> {
                            val pts = 90L * multiplier
                            val xp = 3L * multiplier
                            score += pts
                            xpDeltaTotal += xp
                            events += RunnerCollisionEvent(
                                lane = g.lane,
                                type = RunnerCollisionType.CRYSTAL_COLLECTED,
                                label = g.label,
                                pointsDelta = pts,
                                xpDelta = xp
                            )
                        }
                        RunnerGateKind.INFLATION_TRAP -> {
                            if (shieldOn) {
                                val bonus = 75L * multiplier
                                score += bonus
                                xpDeltaTotal += 4L
                                events += RunnerCollisionEvent(
                                    lane = g.lane,
                                    type = RunnerCollisionType.TRAP_BLOCKED_BY_SHIELD,
                                    label = "SHIELD BASH +$bonus",
                                    pointsDelta = bonus,
                                    xpDelta = 4L
                                )
                            } else {
                                shield = (shield - TRAP_DAMAGE).coerceAtLeast(0)
                                combo = 0
                                multiplier = 1
                                events += RunnerCollisionEvent(
                                    lane = g.lane,
                                    type = RunnerCollisionType.TRAP_DAMAGE,
                                    label = g.label,
                                    pointsDelta = 0L,
                                    xpDelta = 0L
                                )
                                if (shield <= 0) {
                                    if (state.revivesRemaining > 0) {
                                        pendingRevive = true
                                        reviveDeadline = nowMs + REVIVE_COUNTDOWN_MS
                                    } else {
                                        gameOver = true
                                    }
                                    events += RunnerCollisionEvent(
                                        lane = g.lane,
                                        type = RunnerCollisionType.TRIGGERED_GAME_OVER,
                                        label = "SHIELD DEPLETED",
                                        pointsDelta = 0L,
                                        xpDelta = 0L
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Procedural multi-lane wave spawning (always guarantees at least 1 safe lane!)
        var spawnTimer = state.spawnCooldownSec - safeDt
        var nextId = state.nextGateId
        var spawnedCount = state.gatesSpawnedCount
        var zoneIdx = state.currencyZoneIndex

        if (!pendingRevive && !gameOver && spawnTimer <= 0f) {
            spawnTimer = (1.02f / (0.85f + trackSpeed)).coerceIn(0.46f, 0.92f)
            spawnedCount += 1
            if (spawnedCount % 7 == 0) {
                zoneIdx = (zoneIdx + 1) % RunnerCurrencyZone.entries.size
            }
            val zone = RunnerCurrencyZone.entries[zoneIdx]

            val safeLane = random.nextInt(3)
            val otherLanes = listOf(0, 1, 2).filter { it != safeLane }

            // Always spawn either a Wealth Gate or Currency Crystal on the safe lane
            val spawnWealth = random.nextFloat() < 0.62f
            advancedGates += RunnerGate(
                id = nextId++,
                lane = safeLane,
                depthProgress = 0.02f,
                kind = if (spawnWealth) RunnerGateKind.WEALTH_GATE else RunnerGateKind.CURRENCY_CRYSTAL,
                label = if (spawnWealth) wealthLabels[random.nextInt(wealthLabels.size)] else "${zone.symbol} +90"
            )

            // Spawn 1 or 2 Inflation Traps (never blocking all 3 lanes!)
            val trapCount = if (score > 1800L && random.nextFloat() < 0.58f) 2 else 1
            val shuffledOthers = otherLanes.shuffled(random)
            for (i in 0 until trapCount) {
                advancedGates += RunnerGate(
                    id = nextId++,
                    lane = shuffledOthers[i],
                    depthProgress = 0.02f,
                    kind = RunnerGateKind.INFLATION_TRAP,
                    label = trapLabels[random.nextInt(trapLabels.size)]
                )
            }
            if (trapCount == 1 && random.nextFloat() < 0.45f) {
                advancedGates += RunnerGate(
                    id = nextId++,
                    lane = shuffledOthers[1],
                    depthProgress = 0.02f,
                    kind = RunnerGateKind.CURRENCY_CRYSTAL,
                    label = "${zone.symbol} BONUS"
                )
            }
        }

        val updatedState = state.copy(
            score = score,
            distanceAccumulator = remDist,
            wealthCombo = combo,
            multiplier = multiplier,
            maxMultiplierReached = maxMult,
            shieldIntegrity = shield,
            currencyZoneIndex = zoneIdx,
            gates = advancedGates,
            nextGateId = nextId,
            spawnCooldownSec = spawnTimer,
            gatesSpawnedCount = spawnedCount,
            isPendingRevive = pendingRevive,
            reviveDeadlineMs = reviveDeadline,
            isGameOver = gameOver,
            totalXpThisRun = state.totalXpThisRun + xpDeltaTotal
        )

        return RunnerStepOutput(
            nextState = updatedState,
            events = events,
            xpEarned = xpDeltaTotal
        )
    }

    fun activateGoldenShield(
        state: CurrencyRunnerState,
        nowMs: Long = System.currentTimeMillis()
    ): CurrencyRunnerState {
        if (state.goldenShieldRemaining <= 0 || state.isGameOver || state.isPendingRevive) return state
        return state.copy(
            goldenShieldRemaining = state.goldenShieldRemaining - 1,
            goldenShieldExpiresAtMs = nowMs + GOLDEN_SHIELD_DURATION_MS
        )
    }

    fun activateWealthMagnet(
        state: CurrencyRunnerState,
        nowMs: Long = System.currentTimeMillis()
    ): CurrencyRunnerState {
        if (state.magnetRemaining <= 0 || state.isGameOver || state.isPendingRevive) return state
        return state.copy(
            magnetRemaining = state.magnetRemaining - 1,
            magnetExpiresAtMs = nowMs + WEALTH_MAGNET_DURATION_MS
        )
    }

    fun useSecondChanceRevive(
        state: CurrencyRunnerState,
        nowMs: Long = System.currentTimeMillis()
    ): CurrencyRunnerState {
        if (!state.isPendingRevive || state.revivesRemaining <= 0) return state
        if (state.reviveDeadlineMs > 0L && nowMs > state.reviveDeadlineMs) {
            return declineOrExpireRevive(state)
        }
        // Clear any traps immediately overlapping the impact zone so player never gets insta-hit on resume
        val safeGates = state.gates.filterNot { it.kind == RunnerGateKind.INFLATION_TRAP && it.depthProgress >= 0.65f }
        return state.copy(
            shieldIntegrity = 75,
            gates = safeGates,
            revivesRemaining = state.revivesRemaining - 1,
            isPendingRevive = false,
            reviveDeadlineMs = 0L,
            goldenShieldExpiresAtMs = max(state.goldenShieldExpiresAtMs, nowMs + 1600L)
        )
    }

    fun declineOrExpireRevive(state: CurrencyRunnerState): CurrencyRunnerState {
        if (!state.isPendingRevive) return state
        return state.copy(
            isPendingRevive = false,
            reviveDeadlineMs = 0L,
            isGameOver = true
        )
    }
}

@Composable
private fun CurrencyRunnerHeroBannerCard(
    spec: ArenaGameSpec,
    bestScore: Long,
    onPlayClick: () -> Unit
) {
    val formattedBest = remember(bestScore) {
        NumberFormat.getNumberInstance(Locale.US).format(bestScore)
    }

    Surface(
        onClick = onPlayClick,
        shape = RoundedCornerShape(16.dp),
        color = ObsidianBg,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(ArenaCyan, ArenaGold.copy(alpha = 0.88f), ArenaCyan.copy(alpha = 0.78f))
            )
        ),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_3")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0A1420),
                            ObsidianBg,
                            Color(0xFF14110A)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.height * 0.5f, size.height * 0.5f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.5f, size.height * 0.5f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.11f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.25f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CurrencyRunnerLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("currency_runner_logo_canvas")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_runner_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Runner Score",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: $formattedBest",
                                color = ArenaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaCyan,
                            contentColor = ObsidianBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_runner_play_button")
                    ) {
                        Text(
                            text = "PLAY ▶",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyRunnerLogoArtwork(
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "runnerLogoOrbit")
    val orbitDeg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "currencyOrbitAngle"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Global Currency Survival neon shield surrounded by orbiting currency symbols"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0A111C), Color(0xFF131926))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaCyan.copy(alpha = 0.85f), ArenaGold.copy(alpha = 0.78f))),
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val r = size.minDimension * 0.5f

        // Dynamic diagonal speed streaks across background
        for (i in 0 until 5) {
            val offsetY = (i - 2) * r * 0.34f
            drawLine(
                color = ArenaCyan.copy(alpha = 0.18f + i * 0.04f),
                start = Offset(cx - r * 0.78f, cy + offsetY + r * 0.18f),
                end = Offset(cx + r * 0.78f, cy + offsetY - r * 0.18f),
                strokeWidth = 1.1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Orbiting ring path
        val orbitR = r * 0.64f
        drawCircle(
            color = ArenaCyan.copy(alpha = 0.28f),
            radius = orbitR,
            center = Offset(cx, cy),
            style = Stroke(width = 0.9.dp.toPx())
        )

        // Central Gleaming Neon Energy Shield
        val sw = r * 0.42f
        val sh = r * 0.48f
        val shieldPath = Path().apply {
            moveTo(cx, cy - sh)
            lineTo(cx + sw, cy - sh * 0.55f)
            quadraticTo(cx + sw * 0.94f, cy + sh * 0.42f, cx, cy + sh)
            quadraticTo(cx - sw * 0.94f, cy + sh * 0.42f, cx - sw, cy - sh * 0.55f)
            close()
        }
        drawPath(
            path = shieldPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF15384B), Color(0xFF0A1B2E))
            )
        )
        drawPath(
            path = shieldPath,
            brush = Brush.linearGradient(listOf(ArenaCyan, ArenaGold)),
            style = Stroke(width = 1.6.dp.toPx(), join = StrokeJoin.Round)
        )
        // Shield inner star/core spark
        drawCircle(
            color = ArenaGold,
            radius = 3.2.dp.toPx(),
            center = Offset(cx, cy - sh * 0.08f)
        )

        // Orbiting Global Currency Symbols ($, €, £, ¥, ₹)
        val symbols = listOf("$", "€", "£", "¥", "₹")
        symbols.forEachIndexed { idx, sym ->
            val angleRad = (orbitDeg + idx * 72f) * (PI / 180.0)
            val sx = cx + (cos(angleRad) * orbitR).toFloat()
            val sy = cy + (sin(angleRad) * orbitR).toFloat()
            val badgeColor = if (idx % 2 == 0) ArenaCyan else ArenaGold

            drawCircle(
                color = ObsidianBg,
                radius = 7.5.dp.toPx(),
                center = Offset(sx, sy)
            )
            drawCircle(
                color = badgeColor.copy(alpha = 0.85f),
                radius = 7.5.dp.toPx(),
                center = Offset(sx, sy),
                style = Stroke(width = 0.9.dp.toPx())
            )
            val measured = textMeasurer.measure(
                text = sym,
                style = TextStyle(
                    color = badgeColor,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(sx - measured.size.width * 0.5f, sy - measured.size.height * 0.5f)
            )
        }
    }
}

@Composable
fun CurrencyRunnerGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    var runnerState by remember { mutableStateOf(CurrencyRunnerEngine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var nowClockMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var gridScrollPhase by remember { mutableFloatStateOf(0f) }
    var calloutBanner by remember { mutableStateOf<String?>(null) }
    var calloutColor by remember { mutableStateOf(ArenaCyan) }
    var powerUpHookMessage by remember { mutableStateOf<String?>(null) }
    var secondChanceUsedThisRun by remember { mutableStateOf(false) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }
    var extraBonusXpThisRun by remember { mutableLongStateOf(0L) }

    val particles = remember { mutableStateListOf<RunnerParticle>() }

    // Smooth 120fps orb lane interpolation + slight banking lean
    val smoothLane by animateFloatAsState(
        targetValue = runnerState.playerLane.toFloat(),
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "runnerOrbLane"
    )
    val leanAngleDeg = ((runnerState.playerLane.toFloat() - smoothLane) * 18f).coerceIn(-18f, 18f)

    // Timers for Shield (5s), Magnet (6s), and Revive Countdown (3s)
    val goldenShieldActive = runnerState.isGoldenShieldActive(nowClockMs)
    val magnetActive = runnerState.isMagnetActive(nowClockMs)

    LaunchedEffect(
        runnerState.goldenShieldExpiresAtMs,
        runnerState.magnetExpiresAtMs,
        runnerState.isPendingRevive,
        runnerState.reviveDeadlineMs
    ) {
        while (
            System.currentTimeMillis() < runnerState.goldenShieldExpiresAtMs ||
            System.currentTimeMillis() < runnerState.magnetExpiresAtMs ||
            runnerState.isPendingRevive
        ) {
            val now = System.currentTimeMillis()
            nowClockMs = now
            if (runnerState.isPendingRevive && runnerState.reviveDeadlineMs > 0L && now >= runnerState.reviveDeadlineMs) {
                runnerState = CurrencyRunnerEngine.declineOrExpireRevive(runnerState)
                break
            }
            delay(50L)
        }
        nowClockMs = System.currentTimeMillis()
    }

    LaunchedEffect(calloutBanner) {
        if (calloutBanner != null) {
            delay(950L)
            calloutBanner = null
        }
    }
    LaunchedEffect(powerUpHookMessage) {
        if (powerUpHookMessage != null) {
            delay(2300L)
            powerUpHookMessage = null
        }
    }

    val currentZone = runnerState.currentZone()

    // 120fps Master Simulation Loop (Perspective track scrolling, gate advancement, collisions, XP & Best sync)
    LaunchedEffect(runnerState.isGameOver, runnerState.isPendingRevive) {
        var lastNanos = 0L
        while (!runnerState.isGameOver || particles.isNotEmpty()) {
            withFrameNanos { frameNanos ->
                val dt = if (lastNanos == 0L) 0.016f else ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = frameNanos
                val now = System.currentTimeMillis()
                nowClockMs = now

                if (!runnerState.isGameOver && !runnerState.isPendingRevive) {
                    val speed = CurrencyRunnerEngine.speedForScore(runnerState.score)
                    gridScrollPhase = (gridScrollPhase + dt * speed * 2.2f) % 1f

                    val prevZoneIdx = runnerState.currencyZoneIndex
                    val prevBest = max(snapshot.bestRunnerScore, runnerState.score)
                    val step = CurrencyRunnerEngine.stepSimulation(runnerState, dt, now)
                    runnerState = step.nextState

                    if (step.nextState.currencyZoneIndex != prevZoneIdx) {
                        val nextZone = step.nextState.currentZone()
                        calloutBanner = "ZONE SHIFT → ${nextZone.label}"
                        calloutColor = nextZone.accentColor
                    }

                    // Emit particle bursts & sound/haptic feedback on collisions
                    step.events.forEach { ev ->
                        val normX = 0.22f + ev.lane * 0.28f
                        val burstColor = when (ev.type) {
                            RunnerCollisionType.WEALTH_BOOST -> ArenaCyan
                            RunnerCollisionType.CRYSTAL_COLLECTED -> ArenaGold
                            RunnerCollisionType.TRAP_BLOCKED_BY_SHIELD -> ArenaGold
                            RunnerCollisionType.TRAP_DAMAGE,
                            RunnerCollisionType.TRIGGERED_GAME_OVER -> RunnerTrapRed
                        }
                        for (i in 0 until 14) {
                            val ang = (2.0 * PI * i / 14.0) + Random.nextDouble(-0.25, 0.25)
                            val vel = Random.nextFloat() * 0.45f + 0.14f
                            particles.add(
                                RunnerParticle(
                                    normX = normX,
                                    normY = 0.80f,
                                    vx = (cos(ang) * vel).toFloat(),
                                    vy = (sin(ang) * vel).toFloat() - 0.18f,
                                    life = 0f,
                                    maxLife = Random.nextFloat() * 0.25f + 0.30f,
                                    radiusDp = Random.nextFloat() * 2.2f + 1.8f,
                                    color = burstColor
                                )
                            )
                        }

                        when (ev.type) {
                            RunnerCollisionType.WEALTH_BOOST,
                            RunnerCollisionType.CRYSTAL_COLLECTED -> {
                                if (ev.type == RunnerCollisionType.WEALTH_BOOST) {
                                    calloutBanner = "${ev.label} • +${ev.pointsDelta}"
                                    calloutColor = ArenaCyan
                                }
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    ArenaSynthAudio.playWealthGateChime(step.nextState.multiplier)
                                }
                            }
                            RunnerCollisionType.TRAP_BLOCKED_BY_SHIELD -> {
                                calloutBanner = ev.label
                                calloutColor = ArenaGold
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    ArenaSynthAudio.playGoldMilestoneChime()
                                }
                            }
                            RunnerCollisionType.TRAP_DAMAGE,
                            RunnerCollisionType.TRIGGERED_GAME_OVER -> {
                                calloutBanner = ev.label
                                calloutColor = RunnerTrapRed
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    ArenaSynthAudio.playInflationTrapBuzz()
                                }
                            }
                        }
                    }

                    // Persist Best Score & Arena XP smoothly on increments
                    var updatedSnap = snapshot
                    if (step.nextState.score > prevBest && (step.events.isNotEmpty() || step.nextState.isGameOver || step.nextState.isPendingRevive || step.nextState.score % 50L == 0L)) {
                        updatedSnap = ArenaProgressStore.updateBestRunnerScoreIfHigher(context, step.nextState.score)
                    }
                    if (step.xpEarned > 0L) {
                        updatedSnap = ArenaProgressStore.awardXpAndTouchStreak(context, step.xpEarned, now)
                    }
                    if (updatedSnap !== snapshot) {
                        onSnapshotUpdated(updatedSnap)
                    }
                }

                // Step active collision particles
                val pIt = particles.listIterator()
                while (pIt.hasNext()) {
                    val p = pIt.next()
                    p.life += dt
                    if (p.life >= p.maxLife) {
                        pIt.remove()
                    } else {
                        p.normX += p.vx * dt
                        p.normY += p.vy * dt
                        p.vy += 0.48f * dt
                    }
                }
            }
        }
    }

    val triggerLaneShift by rememberUpdatedState { delta: Int ->
        val (shifted, moved) = CurrencyRunnerEngine.shiftLane(runnerState, delta)
        if (moved) {
            runnerState = shifted
            if (feedbackEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                ArenaSynthAudio.playLaneSwitchWhoosh()
            }
        }
    }

    val effectiveBest = max(snapshot.bestRunnerScore, runnerState.score)
    val formattedScore = remember(runnerState.score) {
        NumberFormat.getNumberInstance(Locale.US).format(runnerState.score)
    }
    val formattedBest = remember(effectiveBest) {
        NumberFormat.getNumberInstance(Locale.US).format(effectiveBest)
    }
    val swipeThresholdPx = with(density) { 20.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .navigationBarsPadding()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Sleek Top Navigation & Sound Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        onSnapshotUpdated(ArenaProgressStore.updateBestRunnerScoreIfHigher(context, runnerState.score))
                        onExitToHub()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("game_runner_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Currency Survival",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = currentZone.accentColor.copy(alpha = 0.15f),
                        border = BorderStroke(0.75.dp, currentZone.accentColor.copy(alpha = 0.55f))
                    ) {
                        Text(
                            text = "ZONE ${currentZone.label} • LVL ${snapshot.levelInfo.level}",
                            color = currentZone.accentColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    feedbackEnabled = !feedbackEnabled
                    onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObsidianElevated)
                    .border(1.dp, ArenaCyan.copy(alpha = 0.35f), CircleShape)
                    .testTag("game_runner_sound_toggle")
            ) {
                Icon(
                    imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (feedbackEnabled) "Mute Sound & Haptics" else "Enable Sound & Haptics",
                    tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Telemetry Row: SCORE | MULTIPLIER | ALL-TIME BEST + SHIELD INTEGRITY BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TowerHudPill(
                    label = "SCORE",
                    value = formattedScore,
                    accentColor = ArenaCyan,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("runner_score_pill")
                )
                TowerHudPill(
                    label = "MULTIPLIER",
                    value = "x${runnerState.multiplier}",
                    accentColor = if (runnerState.multiplier >= 3) ArenaGold else ElectricCyan,
                    modifier = Modifier
                        .weight(0.9f)
                        .testTag("runner_multiplier_pill")
                )
                TowerHudPill(
                    label = "BEST",
                    value = formattedBest,
                    accentColor = ArenaGold,
                    modifier = Modifier
                        .weight(1.1f)
                        .testTag("runner_best_pill")
                )
            }

            // Vault Shield Integrity Bar
            val shieldRatio = (runnerState.shieldIntegrity.toFloat() / CurrencyRunnerEngine.MAX_SHIELD.toFloat()).coerceIn(0f, 1f)
            val shieldBarColor = when {
                goldenShieldActive -> ArenaGold
                runnerState.shieldIntegrity <= 35 -> RunnerTrapRed
                else -> ArenaCyan
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
                    .background(ObsidianElevated)
                    .border(0.8.dp, shieldBarColor.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Vault Shield Integrity",
                    tint = shieldBarColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (goldenShieldActive) "GOLDEN SHIELD INVINCIBLE" else "VAULT SHIELD ${runnerState.shieldIntegrity}%",
                    color = shieldBarColor,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp
                )
                LinearProgressIndicator(
                    progress = { if (goldenShieldActive) 1f else shieldRatio },
                    color = shieldBarColor,
                    trackColor = ObsidianBg,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .testTag("runner_shield_bar")
                )
            }
        }

        // 3. 120fps 3-Lane 3D Perspective Cyber Highway Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .weight(1f)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(
                    width = if (goldenShieldActive) 2.dp else 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = if (goldenShieldActive) {
                            listOf(ArenaGold, Color.White, ArenaGold)
                        } else {
                            listOf(ArenaCyan.copy(alpha = 0.72f), ArenaGold.copy(alpha = 0.42f))
                        }
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .clipToBounds()
                .pointerInput(runnerState.isGameOver, runnerState.isPendingRevive) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        if (runnerState.isGameOver || runnerState.isPendingRevive) return@awaitEachGesture
                        val startX = down.position.x
                        var swiped = false
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) {
                                if (!swiped) {
                                    // Instant left/right tap lane switch
                                    triggerLaneShift(if (startX < size.width * 0.5f) -1 else 1)
                                }
                                break
                            }
                            val dx = change.position.x - startX
                            if (!swiped && abs(dx) >= swipeThresholdPx) {
                                swiped = true
                                triggerLaneShift(if (dx > 0f) 1 else -1)
                            }
                        }
                    }
                }
                .semantics {
                    contentDescription = "Global Currency 3-lane cyber runner track. Swipe or tap left or right to switch lanes."
                }
                .testTag("currency_runner_highway_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val horizonY = h * 0.18f
                val bottomY = h * 0.96f
                val trackTopW = w * 0.22f
                val trackBottomW = w * 0.96f
                val centerX = w * 0.5f

                // Sky & cyber horizon gradient (#0B0C10 Obsidian base)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF05080F), ObsidianBg, Color(0xFF0B1420))
                    )
                )

                // Perspective highway surface trapezoid
                val roadPath = Path().apply {
                    moveTo(centerX - trackTopW * 0.5f, horizonY)
                    lineTo(centerX + trackTopW * 0.5f, horizonY)
                    lineTo(centerX + trackBottomW * 0.5f, bottomY)
                    lineTo(centerX - trackBottomW * 0.5f, bottomY)
                    close()
                }
                drawPath(
                    path = roadPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0B111C), ObsidianBg),
                        startY = horizonY,
                        endY = bottomY
                    )
                )

                // Perspective horizontal depth grid rungs scrolling smoothly toward player
                val rungCount = 10
                for (i in 0 until rungCount) {
                    val rawD = ((i.toFloat() / rungCount.toFloat()) + gridScrollPhase) % 1f
                    val persp = rawD.pow(1.75f)
                    val ry = horizonY + (bottomY - horizonY) * persp
                    val rw = trackTopW + (trackBottomW - trackTopW) * persp
                    drawLine(
                        color = ArenaCyan.copy(alpha = 0.08f + persp * 0.28f),
                        start = Offset(centerX - rw * 0.5f, ry),
                        end = Offset(centerX + rw * 0.5f, ry),
                        strokeWidth = (0.7f + persp * 1.4f).dp.toPx()
                    )
                }

                // Helper to project (laneFloat 0..2, depthProgress 0..1) into screen (Offset, laneWidthPx, perspScale)
                fun projectTrackPoint(lanePos: Float, depth: Float): Triple<Offset, Float, Float> {
                    val clampedD = depth.coerceAtLeast(0f)
                    val persp = clampedD.pow(1.65f)
                    val y = horizonY + (bottomY - horizonY) * persp
                    val trackW = trackTopW + (trackBottomW - trackTopW) * persp
                    val laneW = trackW / 3f
                    val laneNormalized = lanePos - 1f // -1 (Left), 0 (Center), +1 (Right)
                    val x = centerX + laneNormalized * laneW
                    return Triple(Offset(x, y), laneW, persp)
                }

                // 3-Lane vertical perspective laser dividers & outer rails
                val railOffsets = listOf(-1.5f, -0.5f, 0.5f, 1.5f)
                railOffsets.forEachIndexed { idx, normEdge ->
                    val isOuter = idx == 0 || idx == railOffsets.lastIndex
                    val topX = centerX + normEdge * (trackTopW / 3f)
                    val botX = centerX + normEdge * (trackBottomW / 3f)
                    drawLine(
                        color = if (isOuter) ArenaCyan.copy(alpha = 0.78f) else ArenaCyan.copy(alpha = 0.34f),
                        start = Offset(topX, horizonY),
                        end = Offset(botX, bottomY),
                        strokeWidth = if (isOuter) 2.dp.toPx() else 1.2.dp.toPx()
                    )
                }

                // Subtle horizon glow dome & active currency emblem watermark
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(currentZone.accentColor.copy(alpha = 0.16f), Color.Transparent),
                        center = Offset(centerX, horizonY),
                        radius = w * 0.36f
                    ),
                    radius = w * 0.36f,
                    center = Offset(centerX, horizonY)
                )

                // Draw incoming gates sorted back-to-front (far horizon first)
                runnerState.gates
                    .filter { !it.resolved }
                    .sortedBy { it.depthProgress }
                    .forEach { gate ->
                        val effectiveLane = gate.lane.toFloat() + gate.magnetLaneOffset
                        val (pos, laneW, persp) = projectTrackPoint(effectiveLane, gate.depthProgress)
                        val alpha = (gate.depthProgress * 2.4f).coerceIn(0.18f, 1f)

                        when (gate.kind) {
                            RunnerGateKind.WEALTH_GATE -> {
                                val gw = laneW * 0.84f
                                val gh = (16.dp.toPx() + 34.dp.toPx() * persp)
                                val topLeft = Offset(pos.x - gw * 0.5f, pos.y - gh)
                                drawRoundRect(
                                    color = ArenaCyan.copy(alpha = 0.22f * alpha),
                                    topLeft = topLeft,
                                    size = Size(gw, gh),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                                drawRoundRect(
                                    color = ArenaCyan.copy(alpha = 0.92f * alpha),
                                    topLeft = topLeft,
                                    size = Size(gw, gh),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                    style = Stroke(width = (1.1f + persp * 1.4f).dp.toPx())
                                )
                                if (persp > 0.12f) {
                                    val textLayout = textMeasurer.measure(
                                        text = gate.label,
                                        style = TextStyle(
                                            color = Color.White.copy(alpha = alpha),
                                            fontSize = (7.sp.value + persp * 4.5f).sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    drawText(
                                        textLayoutResult = textLayout,
                                        topLeft = Offset(
                                            pos.x - textLayout.size.width * 0.5f,
                                            pos.y - gh * 0.5f - textLayout.size.height * 0.5f
                                        )
                                    )
                                }
                            }
                            RunnerGateKind.INFLATION_TRAP -> {
                                val gw = laneW * 0.84f
                                val gh = (14.dp.toPx() + 32.dp.toPx() * persp)
                                val topLeft = Offset(pos.x - gw * 0.5f, pos.y - gh)
                                drawRoundRect(
                                    color = RunnerTrapRed.copy(alpha = 0.26f * alpha),
                                    topLeft = topLeft,
                                    size = Size(gw, gh),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                                drawRoundRect(
                                    color = RunnerTrapRed.copy(alpha = 0.95f * alpha),
                                    topLeft = topLeft,
                                    size = Size(gw, gh),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                    style = Stroke(width = (1.2f + persp * 1.4f).dp.toPx())
                                )
                                if (persp > 0.12f) {
                                    val textLayout = textMeasurer.measure(
                                        text = gate.label,
                                        style = TextStyle(
                                            color = Color(0xFFFFD6E0).copy(alpha = alpha),
                                            fontSize = (7.sp.value + persp * 4.2f).sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    )
                                    drawText(
                                        textLayoutResult = textLayout,
                                        topLeft = Offset(
                                            pos.x - textLayout.size.width * 0.5f,
                                            pos.y - gh * 0.5f - textLayout.size.height * 0.5f
                                        )
                                    )
                                }
                            }
                            RunnerGateKind.CURRENCY_CRYSTAL -> {
                                val cRadius = (7.dp.toPx() + 12.dp.toPx() * persp)
                                val cyCrystal = pos.y - cRadius * 1.25f
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(ArenaGold.copy(alpha = 0.95f * alpha), ArenaAmber.copy(alpha = 0.7f * alpha)),
                                        center = Offset(pos.x, cyCrystal),
                                        radius = cRadius
                                    ),
                                    radius = cRadius,
                                    center = Offset(pos.x, cyCrystal)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.85f * alpha),
                                    radius = cRadius,
                                    center = Offset(pos.x, cyCrystal),
                                    style = Stroke(width = 1.1.dp.toPx())
                                )
                                val symLayout = textMeasurer.measure(
                                    text = currentZone.symbol,
                                    style = TextStyle(
                                        color = ObsidianBg,
                                        fontSize = (8f + persp * 5f).sp,
                                        fontWeight = FontWeight.Black
                                    )
                                )
                                drawText(
                                    textLayoutResult = symLayout,
                                    topLeft = Offset(
                                        pos.x - symLayout.size.width * 0.5f,
                                        cyCrystal - symLayout.size.height * 0.5f
                                    )
                                )
                            }
                        }
                    }

                // Player Platinum-Gold Vault Core Orb at depth 0.88f
                val (orbBasePos, orbLaneW, _) = projectTrackPoint(smoothLane, 0.88f)
                val orbRadius = (orbLaneW * 0.22f).coerceIn(18.dp.toPx(), 28.dp.toPx())
                val orbCenter = Offset(orbBasePos.x, orbBasePos.y - orbRadius * 0.7f)

                // Magnet aura waves when Wealth Magnet active
                if (magnetActive) {
                    drawCircle(
                        color = ArenaCyan.copy(alpha = 0.22f),
                        radius = orbRadius * 2.15f,
                        center = orbCenter,
                        style = Stroke(width = 1.6.dp.toPx())
                    )
                }

                // Golden Inflation Shield dome around orb when active
                if (goldenShieldActive) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ArenaGold.copy(alpha = 0.34f), Color.Transparent),
                            center = orbCenter,
                            radius = orbRadius * 1.85f
                        ),
                        radius = orbRadius * 1.85f,
                        center = orbCenter
                    )
                    drawCircle(
                        color = ArenaGold,
                        radius = orbRadius * 1.48f,
                        center = orbCenter,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Orb shadow & Platinum-Gold sphere
                drawCircle(
                    color = ArenaCyan.copy(alpha = 0.24f),
                    radius = orbRadius * 1.35f,
                    center = orbCenter
                )
                rotate(degrees = leanAngleDeg, pivot = orbCenter) {
                    drawCircle(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFF8FAFC), ArenaGold, Color(0xFF0EA5E9)),
                            start = Offset(orbCenter.x - orbRadius, orbCenter.y - orbRadius),
                            end = Offset(orbCenter.x + orbRadius, orbCenter.y + orbRadius)
                        ),
                        radius = orbRadius,
                        center = orbCenter
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.90f),
                        radius = orbRadius,
                        center = orbCenter,
                        style = Stroke(width = 1.6.dp.toPx())
                    )
                    drawCircle(
                        color = ObsidianBg,
                        radius = orbRadius * 0.42f,
                        center = orbCenter
                    )
                    drawCircle(
                        color = ArenaCyan,
                        radius = orbRadius * 0.24f,
                        center = orbCenter
                    )
                }

                // Collision particle bursts
                particles.forEach { pt ->
                    val alpha = (1f - (pt.life / pt.maxLife)).coerceIn(0f, 1f)
                    drawCircle(
                        color = pt.color.copy(alpha = alpha),
                        radius = pt.radiusDp.dp.toPx() * (0.4f + alpha * 0.6f),
                        center = Offset(pt.normX * w, pt.normY * h)
                    )
                }
            }

            // Floating Wealth / Trap / Zone Shift Callout
            androidx.compose.animation.AnimatedVisibility(
                visible = calloutBanner != null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ObsidianBg.copy(alpha = 0.88f),
                    border = BorderStroke(1.2.dp, calloutColor)
                ) {
                    Text(
                        text = calloutBanner.orEmpty(),
                        color = calloutColor,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.7.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                }
            }

            if (runnerState.score < 120L && !runnerState.isGameOver && !runnerState.isPendingRevive) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ObsidianBg.copy(alpha = 0.76f),
                    border = BorderStroke(0.9.dp, ArenaCyan.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = "SWIPE OR TAP LEFT / RIGHT TO DODGE INFLATION",
                        color = ArenaCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            // Second Chance Revive 3-Second Countdown Overlay
            if (runnerState.isPendingRevive) {
                val remainingMs = (runnerState.reviveDeadlineMs - nowClockMs).coerceIn(0L, CurrencyRunnerEngine.REVIVE_COUNTDOWN_MS)
                val progress = remainingMs.toFloat() / CurrencyRunnerEngine.REVIVE_COUNTDOWN_MS.toFloat()
                val secLabel = ((remainingMs / 1000L) + 1L).coerceIn(1L, 3L)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBg.copy(alpha = 0.80f)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = ObsidianElevated,
                        border = BorderStroke(1.5.dp, ArenaGold),
                        modifier = Modifier
                            .fillMaxWidth(0.86f)
                            .testTag("runner_revive_overlay")
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                tint = ArenaGold,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "SHIELD OVERLOAD! REVIVE?",
                                color = ArenaGold,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Score $formattedScore preserved • Auto-ending in ${secLabel}s",
                                color = DarkTextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            LinearProgressIndicator(
                                progress = { progress },
                                color = ArenaGold,
                                trackColor = ObsidianBg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(999.dp))
                            )
                            Button(
                                onClick = {
                                    runnerState = CurrencyRunnerEngine.useSecondChanceRevive(runnerState, System.currentTimeMillis())
                                    if (feedbackEnabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        ArenaSynthAudio.playPowerUpActivate()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ArenaGold,
                                    contentColor = ObsidianBg
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("runner_use_revive_button")
                            ) {
                                Text(
                                    text = "SECOND CHANCE REVIVE (1 FREE)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    runnerState = CurrencyRunnerEngine.declineOrExpireRevive(runnerState)
                                },
                                border = BorderStroke(1.dp, DarkTextSecondary.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkTextSecondary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("End Run", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Game Over Summary Overlay
            if (runnerState.isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBg.copy(alpha = 0.84f)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = ObsidianElevated,
                        border = BorderStroke(1.5.dp, ArenaCyan),
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .testTag("runner_game_over_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = ArenaGold,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "RUN COMPLETE • $formattedScore",
                                color = ArenaGold,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            val displayedRunnerXp = runnerState.totalXpThisRun + extraBonusXpThisRun
                            Text(
                                text = "Best: $formattedBest • Max Multi: x${runnerState.maxMultiplierReached} • +$displayedRunnerXp XP",
                                color = DarkTextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            if (!secondChanceUsedThisRun) {
                                ArenaRewardedButton(
                                    text = "Second Chance — Continue",
                                    onClick = {
                                        AdManager.showRewardedAd("runner_second_chance") {
                                            val now = System.currentTimeMillis()
                                            nowClockMs = now
                                            val safeGates = runnerState.gates.filterNot {
                                                it.kind == RunnerGateKind.INFLATION_TRAP && it.depthProgress >= 0.65f
                                            }
                                            runnerState = runnerState.copy(
                                                shieldIntegrity = 75,
                                                gates = safeGates,
                                                isPendingRevive = false,
                                                reviveDeadlineMs = 0L,
                                                isGameOver = false,
                                                goldenShieldExpiresAtMs = max(runnerState.goldenShieldExpiresAtMs, now + 2200L)
                                            )
                                            secondChanceUsedThisRun = true
                                        }
                                    },
                                    accentColor = ArenaGold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("runner_rewarded_second_chance_button")
                                )
                            }
                            if (runnerState.totalXpThisRun > 0L) {
                                ArenaRewardedButton(
                                    text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                    enabled = !doubleXpClaimedThisRound,
                                    onClick = {
                                        AdManager.showRewardedAd("runner_double_xp") {
                                            val bonus = runnerState.totalXpThisRun
                                            if (bonus > 0L) {
                                                onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                                extraBonusXpThisRun += bonus
                                            }
                                            doubleXpClaimedThisRound = true
                                        }
                                    },
                                    accentColor = ArenaCyan,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("runner_double_xp_button")
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        AdManager.recordGameRoundCompleted("runner_round_complete") {
                                            onSnapshotUpdated(ArenaProgressStore.updateBestRunnerScoreIfHigher(context, runnerState.score))
                                            onExitToHub()
                                        }
                                    },
                                    border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ArenaCyan),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Text("Arena Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        AdManager.recordGameRoundCompleted("runner_round_complete") {
                                            runnerState = CurrencyRunnerEngine.newGame()
                                            particles.clear()
                                            calloutBanner = null
                                            secondChanceUsedThisRun = false
                                            doubleXpClaimedThisRound = false
                                            extraBonusXpThisRun = 0L
                                            onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ArenaCyan,
                                        contentColor = ObsidianBg
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("runner_play_again_button")
                                ) {
                                    Text("Play Again", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = powerUpHookMessage != null) {
            powerUpHookMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ArenaGold.copy(alpha = 0.14f),
                    border = BorderStroke(0.9.dp, ArenaGold.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                ) {
                    Text(
                        text = msg,
                        color = ArenaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 4. Bottom Tactical Power-Ups Dock (Golden Shield + Wealth Magnet + Restart)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ObsidianElevated,
            border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.28f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .testTag("game_runner_bottom_dock")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val shieldSecLeft = if (goldenShieldActive) {
                    val tenths = ((runnerState.goldenShieldExpiresAtMs - nowClockMs) / 100L).coerceAtLeast(1L)
                    "${tenths / 10}.${tenths % 10}s"
                } else {
                    null
                }
                val canShield = runnerState.goldenShieldRemaining > 0 && !runnerState.isGameOver && !runnerState.isPendingRevive

                OutlinedButton(
                    onClick = {
                        when {
                            goldenShieldActive -> Unit
                            canShield -> {
                                val t = System.currentTimeMillis()
                                nowClockMs = t
                                runnerState = CurrencyRunnerEngine.activateGoldenShield(runnerState, t)
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    ArenaSynthAudio.playPowerUpActivate()
                                }
                            }
                            else -> {
                                powerUpHookMessage = "Free Golden Shield used this run"
                            }
                        }
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = when {
                            goldenShieldActive -> ArenaGold
                            canShield -> ArenaGold.copy(alpha = 0.78f)
                            else -> ArenaCyan.copy(alpha = 0.25f)
                        }
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (goldenShieldActive || canShield) ArenaGold else DarkTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("runner_shield_powerup")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Golden Inflation Shield",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when {
                            shieldSecLeft != null -> "Shield $shieldSecLeft"
                            runnerState.goldenShieldRemaining > 0 -> "Shield (5s)"
                            else -> "Used"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val magnetSecLeft = if (magnetActive) {
                    val tenths = ((runnerState.magnetExpiresAtMs - nowClockMs) / 100L).coerceAtLeast(1L)
                    "${tenths / 10}.${tenths % 10}s"
                } else {
                    null
                }
                val canMagnet = runnerState.magnetRemaining > 0 && !runnerState.isGameOver && !runnerState.isPendingRevive

                OutlinedButton(
                    onClick = {
                        when {
                            magnetActive -> Unit
                            canMagnet -> {
                                val t = System.currentTimeMillis()
                                nowClockMs = t
                                runnerState = CurrencyRunnerEngine.activateWealthMagnet(runnerState, t)
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    ArenaSynthAudio.playPowerUpActivate()
                                }
                            }
                            else -> {
                                powerUpHookMessage = "Free Wealth Magnet used this run"
                            }
                        }
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = when {
                            magnetActive -> ArenaCyan
                            canMagnet -> ArenaCyan.copy(alpha = 0.78f)
                            else -> ArenaCyan.copy(alpha = 0.25f)
                        }
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (magnetActive || canMagnet) ArenaCyan else DarkTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("runner_magnet_powerup")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Wealth Magnet",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when {
                            magnetSecLeft != null -> "Magnet $magnetSecLeft"
                            runnerState.magnetRemaining > 0 -> "Magnet"
                            else -> "Used"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = {
                        runnerState = CurrencyRunnerEngine.newGame()
                        particles.clear()
                        calloutBanner = null
                        secondChanceUsedThisRun = false
                        doubleXpClaimedThisRound = false
                        extraBonusXpThisRun = 0L
                        onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaCyan)
                        .testTag("runner_restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Currency Runner",
                        tint = ObsidianBg,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// 10. GAME 4: CYBER VAULT HEXA-BLOCK FIT (37-CELL HONEYCOMB & 3-AXIS LASERS)
// =============================================================================

enum class HexaCrystalColor(
    val label: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val rimColor: Color
) {
    ELECTRIC_CYAN(
        label = "Cyan",
        primaryColor = Color(0xFF00FFCC),
        secondaryColor = Color(0xFF0284C7),
        rimColor = Color(0xFFA5F3FC)
    ),
    ROYAL_GOLD(
        label = "Gold",
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFD97706),
        rimColor = Color(0xFFFFF3B0)
    ),
    EMERALD_MINT(
        label = "Emerald",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF047857),
        rimColor = Color(0xFF6EE7B7)
    ),
    NEON_VIOLET(
        label = "Violet",
        primaryColor = Color(0xFFA855F7),
        secondaryColor = Color(0xFF6D28D9),
        rimColor = Color(0xFFE9D5FF)
    )
}

data class HexCoord(val q: Int, val r: Int) {
    val s: Int get() = -q - r
}

data class HexaPiece(
    val id: Long,
    val name: String,
    val offsets: List<HexCoord>,
    val color: HexaCrystalColor
)

data class HexaPlacementPreview(
    val anchor: HexCoord,
    val placedCoords: List<HexCoord>,
    val wouldClearLines: List<List<HexCoord>>,
    val wouldClearCoords: Set<HexCoord>
)

data class HexaVaultState(
    val occupied: Map<HexCoord, HexaCrystalColor> = emptyMap(),
    val trayPieces: List<HexaPiece?> = emptyList(),
    val nextPieceId: Long = 1L,
    val score: Long = 0L,
    val lastComboMultiplier: Int = 1,
    val maxMultiplierReached: Int = 1,
    val totalLinesCleared: Int = 0,
    val consecutiveClearStreak: Int = 0,
    val laserZapRemaining: Int = 1,
    val refreshTrayRemaining: Int = 1,
    val isZapTargeting: Boolean = false,
    val isGameOver: Boolean = false
)

data class HexaActionResult(
    val nextState: HexaVaultState,
    val applied: Boolean,
    val placedCoords: List<HexCoord> = emptyList(),
    val clearedLines: List<List<HexCoord>> = emptyList(),
    val clearedCellsWithColor: Map<HexCoord, HexaCrystalColor> = emptyMap(),
    val comboMultiplier: Int = 1,
    val pointsAwarded: Long = 0L,
    val xpAwarded: Long = 0L
)

object HexaVaultEngine {
    const val BOARD_RADIUS = 3

    val allBoardCoords: List<HexCoord> = buildList {
        for (r in -BOARD_RADIUS..BOARD_RADIUS) {
            for (q in -BOARD_RADIUS..BOARD_RADIUS) {
                if (abs(q + r) <= BOARD_RADIUS) {
                    add(HexCoord(q, r))
                }
            }
        }
    }

    val boardCoordSet: Set<HexCoord> = allBoardCoords.toSet()

    // 3 Hexagonal Line Axes (7 Q-axis + 7 R-axis + 7 S-axis = 21 total lines)
    val qAxisLines: List<List<HexCoord>> = (-BOARD_RADIUS..BOARD_RADIUS).map { qVal ->
        allBoardCoords.filter { it.q == qVal }.sortedBy { it.r }
    }

    val rAxisLines: List<List<HexCoord>> = (-BOARD_RADIUS..BOARD_RADIUS).map { rVal ->
        allBoardCoords.filter { it.r == rVal }.sortedBy { it.q }
    }

    val sAxisLines: List<List<HexCoord>> = (-BOARD_RADIUS..BOARD_RADIUS).map { sVal ->
        allBoardCoords.filter { it.s == sVal }.sortedBy { it.q }
    }

    val allAxisLines: List<List<HexCoord>> = qAxisLines + rAxisLines + sAxisLines

    private val shapeTemplates: List<Pair<String, List<HexCoord>>> = listOf(
        // 1-Cell Core Crystal (anchor-centered)
        "Core Shard" to listOf(HexCoord(0, 0)),
        // 2-Cell Dominoes along all 3 hex axes
        "Cyan Link Q" to listOf(HexCoord(0, 0), HexCoord(0, 1)),
        "Cyan Link R" to listOf(HexCoord(0, 0), HexCoord(1, 0)),
        "Cyan Link S" to listOf(HexCoord(0, 0), HexCoord(1, -1)),
        // 3-Cell Straight Beams along all 3 hex axes
        "Beam Q-3" to listOf(HexCoord(0, -1), HexCoord(0, 0), HexCoord(0, 1)),
        "Beam R-3" to listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(1, 0)),
        "Beam S-3" to listOf(HexCoord(-1, 1), HexCoord(0, 0), HexCoord(1, -1)),
        // 3-Cell Triad Clusters
        "Triad Up" to listOf(HexCoord(0, 0), HexCoord(1, 0), HexCoord(0, 1)),
        "Triad Down" to listOf(HexCoord(0, 0), HexCoord(-1, 0), HexCoord(0, -1)),
        // 3-Cell Chevron Corners
        "Chevron A" to listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(0, 1)),
        "Chevron B" to listOf(HexCoord(1, -1), HexCoord(0, 0), HexCoord(1, 0)),
        // 4-Cell Diamond & Vault Clusters
        "Diamond Q-R" to listOf(HexCoord(0, 0), HexCoord(1, 0), HexCoord(0, 1), HexCoord(1, -1)),
        "Diamond R-S" to listOf(HexCoord(0, 0), HexCoord(-1, 0), HexCoord(0, -1), HexCoord(-1, 1)),
        "Vault Hook" to listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(1, 0), HexCoord(0, 1)),
        "Y-Prism" to listOf(HexCoord(0, 0), HexCoord(1, 0), HexCoord(0, -1), HexCoord(-1, 1)),
        "Beam R-4" to listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(1, 0), HexCoord(2, 0))
    )

    fun comboMultiplierForLines(linesCleared: Int): Int = when {
        linesCleared >= 4 -> 5
        linesCleared == 3 -> 3
        linesCleared == 2 -> 2
        else -> 1
    }

    fun newGame(random: Random = Random.Default): HexaVaultState {
        val (tray, nextId) = generateTray(startId = 1L, random = random)
        return HexaVaultState(
            occupied = emptyMap(),
            trayPieces = tray,
            nextPieceId = nextId
        )
    }

    fun generateTray(startId: Long, random: Random = Random.Default): Pair<List<HexaPiece?>, Long> {
        var nextId = startId
        val colors = HexaCrystalColor.entries
        val smallTemplates = shapeTemplates.filter { it.second.size <= 3 }
        val pieces = List(3) { slotIdx ->
            val pool = if (slotIdx == 0) smallTemplates else shapeTemplates
            val template = pool[random.nextInt(pool.size)]
            val color = colors[random.nextInt(colors.size)]
            HexaPiece(
                id = nextId++,
                name = template.first,
                offsets = template.second,
                color = color
            )
        }
        return pieces to nextId
    }

    fun canPlacePiece(
        occupied: Map<HexCoord, HexaCrystalColor>,
        piece: HexaPiece,
        anchor: HexCoord
    ): Boolean {
        for (offset in piece.offsets) {
            val target = HexCoord(anchor.q + offset.q, anchor.r + offset.r)
            if (target !in boardCoordSet || target in occupied) {
                return false
            }
        }
        return true
    }

    fun canPieceFitAnywhere(
        occupied: Map<HexCoord, HexaCrystalColor>,
        piece: HexaPiece
    ): Boolean = allBoardCoords.any { anchor -> canPlacePiece(occupied, piece, anchor) }

    fun hasAnyValidMove(
        occupied: Map<HexCoord, HexaCrystalColor>,
        trayPieces: List<HexaPiece?>
    ): Boolean = trayPieces.filterNotNull().any { piece -> canPieceFitAnywhere(occupied, piece) }

    fun previewPlacement(
        occupied: Map<HexCoord, HexaCrystalColor>,
        piece: HexaPiece,
        anchor: HexCoord
    ): HexaPlacementPreview? {
        if (!canPlacePiece(occupied, piece, anchor)) return null
        val placed = piece.offsets.map { HexCoord(anchor.q + it.q, anchor.r + it.r) }
        val simulated = HashMap<HexCoord, HexaCrystalColor>(occupied.size + placed.size)
        simulated.putAll(occupied)
        placed.forEach { simulated[it] = piece.color }

        val clearLines = allAxisLines.filter { line -> line.all { it in simulated } }
        val clearCoords = clearLines.flatten().toSet()
        return HexaPlacementPreview(
            anchor = anchor,
            placedCoords = placed,
            wouldClearLines = clearLines,
            wouldClearCoords = clearCoords
        )
    }

    fun placePiece(
        state: HexaVaultState,
        slotIndex: Int,
        anchor: HexCoord,
        random: Random = Random.Default
    ): HexaActionResult {
        if (state.isGameOver || slotIndex !in state.trayPieces.indices) {
            return HexaActionResult(nextState = state, applied = false)
        }
        val piece = state.trayPieces[slotIndex]
            ?: return HexaActionResult(nextState = state, applied = false)
        if (!canPlacePiece(state.occupied, piece, anchor)) {
            return HexaActionResult(nextState = state, applied = false)
        }

        val placed = piece.offsets.map { HexCoord(anchor.q + it.q, anchor.r + it.r) }
        val working = HashMap<HexCoord, HexaCrystalColor>(state.occupied.size + placed.size)
        working.putAll(state.occupied)
        placed.forEach { working[it] = piece.color }

        val clearedLines = allAxisLines.filter { line -> line.all { it in working } }
        val clearedCoords = clearedLines.flatten().toSet()
        val clearedWithColor = HashMap<HexCoord, HexaCrystalColor>(clearedCoords.size)
        clearedCoords.forEach { coord ->
            working.remove(coord)?.let { col -> clearedWithColor[coord] = col }
        }

        val linesCount = clearedLines.size
        val comboMult = if (linesCount > 0) comboMultiplierForLines(linesCount) else 1
        val nextStreak = if (linesCount > 0) state.consecutiveClearStreak + 1 else 0
        val streakBonus = if (linesCount > 0 && nextStreak > 1) (nextStreak - 1) * 40L else 0L

        val placePoints = placed.size * 15L
        val clearPoints = if (clearedCoords.isNotEmpty()) clearedCoords.size * 30L * comboMult else 0L
        val totalPoints = placePoints + clearPoints + streakBonus
        val xp = 5L + if (linesCount > 0) (linesCount * 14L * comboMult) else 0L

        val mutatedTray = state.trayPieces.toMutableList()
        mutatedTray[slotIndex] = null
        var nextId = state.nextPieceId
        val finalTray = if (mutatedTray.all { it == null }) {
            val (freshTray, idAfter) = generateTray(nextId, random)
            nextId = idAfter
            freshTray
        } else {
            mutatedTray
        }

        val nextGameOver = !hasAnyValidMove(working, finalTray)
        val nextState = state.copy(
            occupied = working,
            trayPieces = finalTray,
            nextPieceId = nextId,
            score = state.score + totalPoints,
            lastComboMultiplier = if (linesCount > 0) comboMult else 1,
            maxMultiplierReached = max(state.maxMultiplierReached, comboMult),
            totalLinesCleared = state.totalLinesCleared + linesCount,
            consecutiveClearStreak = nextStreak,
            isZapTargeting = false,
            isGameOver = nextGameOver
        )

        return HexaActionResult(
            nextState = nextState,
            applied = true,
            placedCoords = placed,
            clearedLines = clearedLines,
            clearedCellsWithColor = clearedWithColor,
            comboMultiplier = comboMult,
            pointsAwarded = totalPoints,
            xpAwarded = xp
        )
    }

    fun toggleZapTargeting(state: HexaVaultState): HexaVaultState {
        if (state.isGameOver || state.laserZapRemaining <= 0 || state.occupied.isEmpty()) return state
        return state.copy(isZapTargeting = !state.isZapTargeting)
    }

    fun applyLaserZap(state: HexaVaultState, target: HexCoord): HexaActionResult {
        if (state.isGameOver || state.laserZapRemaining <= 0 || target !in state.occupied) {
            return HexaActionResult(nextState = state, applied = false)
        }
        val removedColor = state.occupied[target] ?: HexaCrystalColor.ELECTRIC_CYAN
        val nextOccupied = state.occupied - target
        val nextGameOver = !hasAnyValidMove(nextOccupied, state.trayPieces)
        val nextState = state.copy(
            occupied = nextOccupied,
            score = state.score + 35L,
            laserZapRemaining = state.laserZapRemaining - 1,
            isZapTargeting = false,
            isGameOver = nextGameOver
        )
        return HexaActionResult(
            nextState = nextState,
            applied = true,
            clearedCellsWithColor = mapOf(target to removedColor),
            pointsAwarded = 35L,
            xpAwarded = 8L
        )
    }

    fun applyRefreshTray(
        state: HexaVaultState,
        random: Random = Random.Default
    ): HexaVaultState {
        if (state.isGameOver || state.refreshTrayRemaining <= 0) return state
        val (freshTray, nextId) = generateTray(state.nextPieceId, random)
        val nextGameOver = !hasAnyValidMove(state.occupied, freshTray)
        return state.copy(
            trayPieces = freshTray,
            nextPieceId = nextId,
            refreshTrayRemaining = state.refreshTrayRemaining - 1,
            isZapTargeting = false,
            isGameOver = nextGameOver
        )
    }
}

private object HexaGridGeometry {
    private val SQRT3 = sqrt(3f)

    fun computeHexRadius(canvasSize: Size): Float {
        // Pointy-top radius-3 honeycomb spans ~7*sqrt(3)*R horizontally and ~10.5*R vertically
        val maxRByWidth = (canvasSize.width * 0.90f) / (7f * SQRT3)
        val maxRByHeight = (canvasSize.height * 0.88f) / 10.6f
        return min(maxRByWidth, maxRByHeight).coerceAtLeast(8f)
    }

    fun hexCenter(coord: HexCoord, boardCenter: Offset, hexRadius: Float): Offset {
        val x = boardCenter.x + hexRadius * SQRT3 * (coord.q + coord.r * 0.5f)
        val y = boardCenter.y + hexRadius * 1.5f * coord.r
        return Offset(x, y)
    }

    fun buildHexPath(center: Offset, radius: Float): Path {
        val path = Path()
        for (i in 0 until 6) {
            val angleRad = ((60f * i) - 30f) * (PI / 180.0)
            val vx = center.x + (radius * cos(angleRad)).toFloat()
            val vy = center.y + (radius * sin(angleRad)).toFloat()
            if (i == 0) path.moveTo(vx, vy) else path.lineTo(vx, vy)
        }
        path.close()
        return path
    }

    fun pixelToNearestCoord(point: Offset, boardCenter: Offset, hexRadius: Float): HexCoord? {
        val dx = point.x - boardCenter.x
        val dy = point.y - boardCenter.y
        val qf = ((SQRT3 / 3f) * dx - (1f / 3f) * dy) / hexRadius
        val rf = ((2f / 3f) * dy) / hexRadius
        val rounded = cubeRound(qf, rf)
        if (rounded !in HexaVaultEngine.boardCoordSet) return null
        val cellCenter = hexCenter(rounded, boardCenter, hexRadius)
        val dist = kotlin.math.hypot(point.x - cellCenter.x, point.y - cellCenter.y)
        return if (dist <= hexRadius * 1.25f) rounded else null
    }

    fun findMagneticSnap(
        point: Offset,
        boardCenter: Offset,
        hexRadius: Float,
        occupied: Map<HexCoord, HexaCrystalColor>,
        piece: HexaPiece
    ): HexCoord? {
        val exact = pixelToNearestCoord(point, boardCenter, hexRadius)
        if (exact != null && HexaVaultEngine.canPlacePiece(occupied, piece, exact)) {
            return exact
        }
        // Forgiving magnetic search among adjacent cells within 1.45*R
        var bestCoord: HexCoord? = null
        var bestDist = hexRadius * 1.45f
        for (candidate in HexaVaultEngine.allBoardCoords) {
            if (!HexaVaultEngine.canPlacePiece(occupied, piece, candidate)) continue
            val cPos = hexCenter(candidate, boardCenter, hexRadius)
            val dist = kotlin.math.hypot(point.x - cPos.x, point.y - cPos.y)
            if (dist < bestDist) {
                bestDist = dist
                bestCoord = candidate
            }
        }
        return bestCoord
    }

    private fun cubeRound(qf: Float, rf: Float): HexCoord {
        val sf = -qf - rf
        var rq = qf.roundToInt()
        var rr = rf.roundToInt()
        val rs = sf.roundToInt()

        val qDiff = abs(rq - qf)
        val rDiff = abs(rr - rf)
        val sDiff = abs(rs - sf)

        if (qDiff > rDiff && qDiff > sDiff) {
            rq = -rr - rs
        } else if (rDiff > sDiff) {
            rr = -rq - rs
        }
        return HexCoord(rq, rr)
    }
}

private data class HexLaserBeamFx(
    val start: HexCoord,
    val end: HexCoord,
    val color: Color,
    var life: Float = 0f,
    val maxLife: Float = 0.36f
)

private data class HexCrystalParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val radiusPx: Float,
    val color: Color
)

private data class ActiveHexDrag(
    val slotIndex: Int,
    val piece: HexaPiece,
    val pointerInOverlay: Offset
)

@Composable
private fun HexaVaultHeroBannerCard(
    spec: ArenaGameSpec,
    snapshot: ArenaSnapshot,
    onUnlockFlashShown: () -> Unit,
    onPlayClick: () -> Unit
) {
    val isUnlocked = snapshot.isGame4Unlocked
    val progressDays = snapshot.effectiveStreakDays.coerceIn(0, ArenaProgressStore.GAME4_REQUIRED_STREAK_DAYS)
    val formattedBest = remember(snapshot.bestHexaScore) {
        NumberFormat.getNumberInstance(Locale.US).format(snapshot.bestHexaScore)
    }

    var unlockBannerVisible by remember(snapshot.shouldShowGame4UnlockFlash) {
        mutableStateOf(snapshot.shouldShowGame4UnlockFlash)
    }
    val unlockFlashAlpha = remember { Animatable(0f) }

    LaunchedEffect(snapshot.shouldShowGame4UnlockFlash) {
        if (snapshot.shouldShowGame4UnlockFlash) {
            unlockBannerVisible = true
            unlockFlashAlpha.snapTo(0.95f)
            unlockFlashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing)
            )
            delay(1400L)
            unlockBannerVisible = false
            onUnlockFlashShown()
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B0C10),
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.linearGradient(
                colors = if (isUnlocked) {
                    listOf(ArenaCyan.copy(alpha = 0.85f), ArenaGold.copy(alpha = 0.88f), ArenaCyan.copy(alpha = 0.65f))
                } else {
                    listOf(ArenaGold.copy(alpha = 0.55f), ArenaCyan.copy(alpha = 0.45f))
                }
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_4")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0B1420),
                            ObsidianBg,
                            Color(0xFF151020)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.height * 0.5f, size.height * 0.5f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.5f, size.height * 0.5f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.25f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HexaVaultLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("hexa_vault_logo_canvas")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_hexa_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Hexa Score",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: $formattedBest",
                                color = ArenaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        enabled = isUnlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaCyan,
                            contentColor = ObsidianBg,
                            disabledContainerColor = Color(0xFF1F2937),
                            disabledContentColor = DarkTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_hexa_play_button")
                    ) {
                        Text(
                            text = if (isUnlocked) "PLAY ▶" else "LOCKED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Frosted Glass 3-Day Streak Lock Overlay when locked
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xDD0B0C10),
                                    Color(0xE80F172A),
                                    Color(0xDD0B0C10)
                                )
                            )
                        )
                        .border(1.2.dp, ArenaGold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                        .testTag("hexa_streak_lock_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.72f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked by 3-Day Streak",
                                    tint = ArenaGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "🔒 Unlock at 3-Day Streak • $progressDays/3 Days",
                                    color = ArenaGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progressDays / 3f },
                            color = ArenaCyan,
                            trackColor = Color(0xFF1F2937),
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )

                        Text(
                            text = "Keep your daily Koshvanta or Arena streak alive to unlock permanently!",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Celebratory Gold Unlock Flash Overlay on first 3-day milestone reveal
            if (unlockFlashAlpha.value > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(ArenaGold.copy(alpha = unlockFlashAlpha.value * 0.48f))
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = unlockBannerVisible && isUnlocked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f))
                ) {
                    Text(
                        text = "✨ 3-DAY STREAK UNLOCKED!",
                        color = ObsidianBg,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HexaVaultLogoArtwork(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hexaLogoPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hexaCrystalGlow"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Cyber Vault Hexa-Block Fit glowing honeycomb crystal emblem"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0A111D), Color(0xFF151226))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaCyan.copy(alpha = 0.88f), ArenaGold.copy(alpha = 0.82f))),
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val r = size.minDimension * 0.14f
        val cluster = listOf(
            HexCoord(0, 0) to HexaCrystalColor.ROYAL_GOLD,
            HexCoord(0, -1) to HexaCrystalColor.ELECTRIC_CYAN,
            HexCoord(1, -1) to HexaCrystalColor.EMERALD_MINT,
            HexCoord(1, 0) to HexaCrystalColor.ELECTRIC_CYAN,
            HexCoord(0, 1) to HexaCrystalColor.NEON_VIOLET,
            HexCoord(-1, 1) to HexaCrystalColor.ROYAL_GOLD,
            HexCoord(-1, 0) to HexaCrystalColor.EMERALD_MINT
        )
        cluster.forEach { (coord, crystal) ->
            val hCenter = HexaGridGeometry.hexCenter(coord, Offset(cx, cy), r)
            val hexPath = HexaGridGeometry.buildHexPath(hCenter, r * 0.90f)
            drawPath(
                path = hexPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        crystal.primaryColor.copy(alpha = 0.90f * pulse),
                        crystal.secondaryColor.copy(alpha = 0.95f)
                    ),
                    start = Offset(hCenter.x - r, hCenter.y - r),
                    end = Offset(hCenter.x + r, hCenter.y + r)
                )
            )
            drawPath(
                path = hexPath,
                color = crystal.rimColor.copy(alpha = 0.92f),
                style = Stroke(width = 1.1.dp.toPx(), join = StrokeJoin.Round)
            )
        }
    }
}

@Composable
fun HexaVaultBlockGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    var gameState by remember { mutableStateOf(HexaVaultEngine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var calloutBanner by remember { mutableStateOf<String?>(null) }
    var calloutColor by remember { mutableStateOf(ArenaCyan) }
    var powerUpHookMessage by remember { mutableStateOf<String?>(null) }
    var roundTotalXp by remember { mutableLongStateOf(0L) }
    var secondChanceUsedThisRun by remember { mutableStateOf(false) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }
    var rewardedTrayUsed by remember { mutableStateOf(false) }

    var overlayRootPos by remember { mutableStateOf(Offset.Zero) }
    var boardOriginInOverlay by remember { mutableStateOf(Offset.Zero) }
    var boardPixelSize by remember { mutableStateOf(Size.Zero) }
    val trayOriginsInOverlay = remember { mutableStateListOf(Offset.Zero, Offset.Zero, Offset.Zero) }

    var activeDrag by remember { mutableStateOf<ActiveHexDrag?>(null) }
    val dragVerticalLiftPx = with(density) { 64.dp.toPx() }

    val activeLaserBeams = remember { mutableStateListOf<HexLaserBeamFx>() }
    val particles = remember { mutableStateListOf<HexCrystalParticle>() }

    // Clear banner after brief display
    LaunchedEffect(calloutBanner) {
        if (calloutBanner != null) {
            delay(1300L)
            calloutBanner = null
        }
    }
    LaunchedEffect(powerUpHookMessage) {
        if (powerUpHookMessage != null) {
            delay(2200L)
            powerUpHookMessage = null
        }
    }

    // 120fps Laser Beam & Crystal Particle Animation Loop
    LaunchedEffect(activeLaserBeams.size, particles.size) {
        if (activeLaserBeams.isEmpty() && particles.isEmpty()) return@LaunchedEffect
        var lastNs = 0L
        while (activeLaserBeams.isNotEmpty() || particles.isNotEmpty()) {
            withFrameNanos { frameNs ->
                if (lastNs == 0L) {
                    lastNs = frameNs
                    return@withFrameNanos
                }
                val dt = ((frameNs - lastNs) / 1_000_000_000f).coerceIn(0.001f, 0.04f)
                lastNs = frameNs

                val bIt = activeLaserBeams.listIterator()
                while (bIt.hasNext()) {
                    val beam = bIt.next()
                    beam.life += dt
                    if (beam.life >= beam.maxLife) bIt.remove()
                }

                val pIt = particles.listIterator()
                while (pIt.hasNext()) {
                    val p = pIt.next()
                    p.life += dt
                    if (p.life >= p.maxLife) {
                        pIt.remove()
                    } else {
                        p.x += p.vx * dt
                        p.y += p.vy * dt
                        p.vy += 420f * dt
                    }
                }
            }
        }
    }

    // Compute real-time magnetic Ghost Preview while dragging
    val ghostPreview: HexaPlacementPreview? = remember(activeDrag, boardOriginInOverlay, boardPixelSize, gameState.occupied) {
        val drag = activeDrag ?: return@remember null
        if (boardPixelSize.width <= 1f || boardPixelSize.height <= 1f) return@remember null
        val boardLocalPoint = Offset(
            x = drag.pointerInOverlay.x - boardOriginInOverlay.x,
            y = drag.pointerInOverlay.y - boardOriginInOverlay.y - dragVerticalLiftPx
        )
        val boardCenter = Offset(boardPixelSize.width * 0.5f, boardPixelSize.height * 0.5f)
        val hexRadius = HexaGridGeometry.computeHexRadius(boardPixelSize)
        val anchor = HexaGridGeometry.findMagneticSnap(
            point = boardLocalPoint,
            boardCenter = boardCenter,
            hexRadius = hexRadius,
            occupied = gameState.occupied,
            piece = drag.piece
        ) ?: return@remember null
        HexaVaultEngine.previewPlacement(gameState.occupied, drag.piece, anchor)
    }

    fun emitClearEffects(result: HexaActionResult) {
        if (boardPixelSize.width <= 1f || boardPixelSize.height <= 1f) return
        val boardCenter = Offset(boardPixelSize.width * 0.5f, boardPixelSize.height * 0.5f)
        val hexRadius = HexaGridGeometry.computeHexRadius(boardPixelSize)

        result.clearedLines.forEach { line ->
            if (line.size >= 2) {
                val beamColor = if (result.comboMultiplier >= 3) ArenaGold else ArenaCyan
                activeLaserBeams.add(HexLaserBeamFx(start = line.first(), end = line.last(), color = beamColor))
            }
        }
        result.clearedCellsWithColor.forEach { (coord, crystal) ->
            val cPos = HexaGridGeometry.hexCenter(coord, boardCenter, hexRadius)
            repeat(8) { idx ->
                val angle = (idx / 8.0) * 2.0 * PI + Random.nextFloat() * 0.5f
                val speed = 130f + Random.nextFloat() * 240f
                particles.add(
                    HexCrystalParticle(
                        x = cPos.x,
                        y = cPos.y,
                        vx = (cos(angle) * speed).toFloat(),
                        vy = (sin(angle) * speed).toFloat() - 60f,
                        life = 0f,
                        maxLife = 0.45f + Random.nextFloat() * 0.25f,
                        radiusPx = 4f + Random.nextFloat() * 5f,
                        color = if (idx % 2 == 0) crystal.primaryColor else ArenaGold
                    )
                )
            }
        }
    }

    val effectiveBest = max(snapshot.bestHexaScore, gameState.score)
    val formattedScore = remember(gameState.score) {
        NumberFormat.getNumberInstance(Locale.US).format(gameState.score)
    }
    val formattedBest = remember(effectiveBest) {
        NumberFormat.getNumberInstance(Locale.US).format(effectiveBest)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .navigationBarsPadding()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Sleek Top Navigation & Sound Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        onSnapshotUpdated(ArenaProgressStore.updateBestHexaScoreIfHigher(context, gameState.score))
                        onExitToHub()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("game_hexa_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Cyber Vault Hexa",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaCyan.copy(alpha = 0.14f),
                        border = BorderStroke(0.75.dp, ArenaCyan.copy(alpha = 0.52f))
                    ) {
                        Text(
                            text = "37 CELLS • LVL ${snapshot.levelInfo.level}",
                            color = ArenaCyan,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    feedbackEnabled = !feedbackEnabled
                    onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObsidianElevated)
                    .border(1.dp, ArenaCyan.copy(alpha = 0.35f), CircleShape)
                    .testTag("game_hexa_sound_toggle")
            ) {
                Icon(
                    imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (feedbackEnabled) "Mute Sound & Haptics" else "Enable Sound & Haptics",
                    tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Clean Telemetry HUD: SCORE | COMBO | ALL-TIME BEST
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TowerHudPill(
                label = "SCORE",
                value = formattedScore,
                accentColor = ArenaCyan,
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("hexa_score_pill")
            )
            TowerHudPill(
                label = "COMBO",
                value = "${gameState.lastComboMultiplier}x",
                accentColor = if (gameState.lastComboMultiplier >= 2) ArenaGold else ElectricCyan,
                modifier = Modifier
                    .weight(0.9f)
                    .testTag("hexa_combo_pill")
            )
            TowerHudPill(
                label = "BEST",
                value = formattedBest,
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("hexa_best_pill")
            )
        }

        // 3. Interactive Drag & Board Overlay Zone (37-Cell Honeycomb Board + 3-Piece Neon Tray)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .onGloballyPositioned { coords ->
                    overlayRootPos = coords.positionInRoot()
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 37-Cell Hexagonal Cyber Vault Board
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF07090E),
                    border = BorderStroke(
                        width = 1.3.dp,
                        color = if (gameState.isZapTargeting) ArenaGold else ArenaCyan.copy(alpha = 0.42f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coords ->
                                boardOriginInOverlay = coords.positionInRoot() - overlayRootPos
                                boardPixelSize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                            }
                    ) {
                        val currentState by rememberUpdatedState(gameState)
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(gameState.isZapTargeting, gameState.isGameOver) {
                                    detectTapGestures { tapOffset ->
                                        val st = currentState
                                        if (!st.isZapTargeting || st.isGameOver) return@detectTapGestures
                                        val canvasSz = Size(size.width.toFloat(), size.height.toFloat())
                                        val bCenter = Offset(canvasSz.width * 0.5f, canvasSz.height * 0.5f)
                                        val hexR = HexaGridGeometry.computeHexRadius(canvasSz)
                                        val tapped = HexaGridGeometry.pixelToNearestCoord(tapOffset, bCenter, hexR)
                                        if (tapped != null && tapped in st.occupied) {
                                            val zapRes = HexaVaultEngine.applyLaserZap(st, tapped)
                                            if (zapRes.applied) {
                                                gameState = zapRes.nextState
                                                emitClearEffects(zapRes)
                                                calloutBanner = "⚡ LASER ZAP CLEARED!"
                                                calloutColor = ArenaGold
                                                if (feedbackEnabled) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    ArenaSynthAudio.playLaserSlice()
                                                }
                                                roundTotalXp += zapRes.xpAwarded
                                                var snap = ArenaProgressStore.updateBestHexaScoreIfHigher(context, zapRes.nextState.score)
                                                snap = ArenaProgressStore.awardXpAndTouchStreak(context, zapRes.xpAwarded)
                                                onSnapshotUpdated(snap)
                                            }
                                        }
                                    }
                                }
                                .testTag("hexa_board_canvas")
                        ) {
                            val bCenter = Offset(size.width * 0.5f, size.height * 0.5f)
                            val hexR = HexaGridGeometry.computeHexRadius(size)
                            val previewCoords = ghostPreview?.placedCoords?.toSet().orEmpty()
                            val preClearCoords = ghostPreview?.wouldClearCoords.orEmpty()
                            val previewColor = activeDrag?.piece?.color

                            // Subtle Cyber Radial Backlight
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(ArenaCyan.copy(alpha = 0.08f), Color.Transparent),
                                    center = bCenter,
                                    radius = hexR * 5.2f
                                ),
                                radius = hexR * 5.2f,
                                center = bCenter
                            )

                            // Draw all 37 hexagonal cells
                            HexaVaultEngine.allBoardCoords.forEach { coord ->
                                val cellCenter = HexaGridGeometry.hexCenter(coord, bCenter, hexR)
                                val cellPath = HexaGridGeometry.buildHexPath(cellCenter, hexR * 0.90f)
                                val placedCrystal = gameState.occupied[coord]
                                val isGhost = coord in previewCoords
                                val isPreClearHighlight = coord in preClearCoords

                                when {
                                    placedCrystal != null -> {
                                        drawPath(
                                            path = cellPath,
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    placedCrystal.primaryColor,
                                                    placedCrystal.secondaryColor
                                                ),
                                                start = Offset(cellCenter.x - hexR, cellCenter.y - hexR),
                                                end = Offset(cellCenter.x + hexR, cellCenter.y + hexR)
                                            )
                                        )
                                        // Inner crystal facet gleam
                                        val innerPath = HexaGridGeometry.buildHexPath(cellCenter, hexR * 0.52f)
                                        drawPath(
                                            path = innerPath,
                                            color = Color.White.copy(alpha = 0.20f)
                                        )
                                        drawPath(
                                            path = cellPath,
                                            color = if (isPreClearHighlight) {
                                                Color.White
                                            } else if (gameState.isZapTargeting) {
                                                ArenaGold
                                            } else {
                                                placedCrystal.rimColor.copy(alpha = 0.92f)
                                            },
                                            style = Stroke(
                                                width = if (isPreClearHighlight || gameState.isZapTargeting) 2.2.dp.toPx() else 1.2.dp.toPx(),
                                                join = StrokeJoin.Round
                                            )
                                        )
                                    }
                                    isGhost && previewColor != null -> {
                                        // Translucent Magnetic Ghost Shadow Preview
                                        drawPath(
                                            path = cellPath,
                                            color = previewColor.primaryColor.copy(alpha = 0.34f)
                                        )
                                        drawPath(
                                            path = cellPath,
                                            color = previewColor.rimColor.copy(alpha = 0.92f),
                                            style = Stroke(
                                                width = 1.6.dp.toPx(),
                                                join = StrokeJoin.Round,
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f), 0f)
                                            )
                                        )
                                    }
                                    else -> {
                                        // Empty Obsidian-Glass Honeycomb Slot
                                        drawPath(
                                            path = cellPath,
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color(0xFF141B29), Color(0xFF0B101B)),
                                                startY = cellCenter.y - hexR,
                                                endY = cellCenter.y + hexR
                                            )
                                        )
                                        drawPath(
                                            path = cellPath,
                                            color = ArenaCyan.copy(alpha = 0.24f),
                                            style = Stroke(width = 1.0.dp.toPx(), join = StrokeJoin.Round)
                                        )
                                    }
                                }

                                // Pulsing Gold rim on pre-cleared line cells before release
                                if (isPreClearHighlight) {
                                    drawPath(
                                        path = cellPath,
                                        color = ArenaGold.copy(alpha = 0.22f)
                                    )
                                }
                            }

                            // 3-Axis Sweeping Laser Line-Clear Beams
                            activeLaserBeams.forEach { beam ->
                                val t = (beam.life / beam.maxLife).coerceIn(0f, 1f)
                                val alpha = 1f - t
                                val startPx = HexaGridGeometry.hexCenter(beam.start, bCenter, hexR)
                                val endPx = HexaGridGeometry.hexCenter(beam.end, bCenter, hexR)
                                drawLine(
                                    color = beam.color.copy(alpha = 0.45f * alpha),
                                    start = startPx,
                                    end = endPx,
                                    strokeWidth = (18.dp.toPx() * (1f - t * 0.45f)),
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = Color.White.copy(alpha = 0.95f * alpha),
                                    start = startPx,
                                    end = endPx,
                                    strokeWidth = (5.dp.toPx() * (1f - t * 0.5f)),
                                    cap = StrokeCap.Round
                                )
                            }

                            // Crystal Shatter Particles
                            particles.forEach { p ->
                                val alpha = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
                                drawCircle(
                                    color = p.color.copy(alpha = alpha),
                                    radius = p.radiusPx * (1f - 0.35f * (p.life / p.maxLife)),
                                    center = Offset(p.x, p.y)
                                )
                            }
                        }

                        // Laser Zap Targeting Instruction Pill
                        androidx.compose.animation.AnimatedVisibility(
                            visible = gameState.isZapTargeting && !gameState.isGameOver,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = ArenaGold,
                                border = BorderStroke(1.dp, Color.White)
                            ) {
                                Text(
                                    text = "⚡ TAP ANY OCCUPIED CELL TO ZAP",
                                    color = ObsidianBg,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.6.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Line-Clear Combo Callout Banner
                        androidx.compose.animation.AnimatedVisibility(
                            visible = calloutBanner != null && !gameState.isGameOver,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = ObsidianBg.copy(alpha = 0.92f),
                                border = BorderStroke(1.2.dp, calloutColor)
                            ) {
                                Text(
                                    text = calloutBanner.orEmpty(),
                                    color = calloutColor,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.7.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                                )
                            }
                        }

                        // Game Over Overlay with Instant Play Again
                        if (gameState.isGameOver) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ObsidianBg.copy(alpha = 0.86f))
                                    .padding(20.dp)
                                    .testTag("hexa_game_over_overlay"),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = ObsidianElevated,
                                    border = BorderStroke(1.3.dp, ArenaGold.copy(alpha = 0.85f)),
                                    modifier = Modifier.fillMaxWidth(0.92f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "VAULT GRID SEALED",
                                            color = ArenaGold,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = "Score: $formattedScore  •  Lines: ${gameState.totalLinesCleared}  •  +$roundTotalXp XP",
                                            color = DarkTextSecondary,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        if (!secondChanceUsedThisRun) {
                                            ArenaRewardedButton(
                                                text = "Get Free Hint & Refresh",
                                                onClick = {
                                                    AdManager.showRewardedAd("hexa_second_chance") {
                                                        // Clear 3 outer occupied cells if crowded and deal a fresh playable tray
                                                        val reducedOccupied = if (gameState.occupied.size > 4) {
                                                            val trimmed = gameState.occupied.toMutableMap()
                                                            trimmed.keys.take(4).forEach { trimmed.remove(it) }
                                                            trimmed
                                                        } else {
                                                            gameState.occupied
                                                        }
                                                        val refreshed = HexaVaultEngine.applyRefreshTray(
                                                            gameState.copy(
                                                                occupied = reducedOccupied,
                                                                refreshTrayRemaining = 1,
                                                                isGameOver = false
                                                            )
                                                        )
                                                        gameState = refreshed.copy(isGameOver = !HexaVaultEngine.hasAnyValidMove(refreshed.occupied, refreshed.trayPieces))
                                                        secondChanceUsedThisRun = true
                                                    }
                                                },
                                                accentColor = ArenaGold,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("hexa_rewarded_second_chance_button")
                                            )
                                        }
                                        if (roundTotalXp > 0L) {
                                            ArenaRewardedButton(
                                                text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                                enabled = !doubleXpClaimedThisRound,
                                                onClick = {
                                                    AdManager.showRewardedAd("hexa_double_xp") {
                                                        val bonus = roundTotalXp
                                                        if (bonus > 0L) {
                                                            onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                                            roundTotalXp += bonus
                                                        }
                                                        doubleXpClaimedThisRound = true
                                                    }
                                                },
                                                accentColor = ArenaCyan,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("hexa_double_xp_button")
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                AdManager.recordGameRoundCompleted("hexa_round_complete") {
                                                    gameState = HexaVaultEngine.newGame()
                                                    activeLaserBeams.clear()
                                                    particles.clear()
                                                    calloutBanner = null
                                                    roundTotalXp = 0L
                                                    secondChanceUsedThisRun = false
                                                    doubleXpClaimedThisRound = false
                                                    rewardedTrayUsed = false
                                                    onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ArenaCyan,
                                                contentColor = ObsidianBg
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("hexa_play_again_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Replay,
                                                contentDescription = "Play Again",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Play Again",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3-Piece Luminous Neon Glass Tray
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(106.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    gameState.trayPieces.forEachIndexed { slotIdx, piece ->
                        val isBeingDragged = activeDrag?.slotIndex == slotIdx
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0E131F),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (piece != null && !isBeingDragged) {
                                    piece.color.primaryColor.copy(alpha = 0.45f)
                                } else {
                                    Color(0xFF1F2937)
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .onGloballyPositioned { coords ->
                                    if (slotIdx in trayOriginsInOverlay.indices) {
                                        trayOriginsInOverlay[slotIdx] = coords.positionInRoot() - overlayRootPos
                                    }
                                }
                                .pointerInput(slotIdx, piece?.id, gameState.isGameOver, gameState.isZapTargeting) {
                                    val slotPiece = piece ?: return@pointerInput
                                    if (gameState.isGameOver || gameState.isZapTargeting) return@pointerInput
                                    awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        down.consume()
                                        val slotOrigin = trayOriginsInOverlay[slotIdx]
                                        activeDrag = ActiveHexDrag(
                                            slotIndex = slotIdx,
                                            piece = slotPiece,
                                            pointerInOverlay = slotOrigin + down.position
                                        )
                                        if (feedbackEnabled) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            ArenaSynthAudio.playHexCrystalPickup()
                                        }

                                        var lastPointerPos = down.position
                                        var completedUp = false
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull { it.id == down.id }
                                                ?: event.changes.firstOrNull()
                                                ?: break
                                            lastPointerPos = change.position
                                            if (change.pressed) {
                                                change.consume()
                                                activeDrag = ActiveHexDrag(
                                                    slotIndex = slotIdx,
                                                    piece = slotPiece,
                                                    pointerInOverlay = slotOrigin + lastPointerPos
                                                )
                                            } else {
                                                change.consume()
                                                completedUp = true
                                                break
                                            }
                                        }

                                        if (completedUp) {
                                            val dropInOverlay = slotOrigin + lastPointerPos
                                            if (boardPixelSize.width > 1f && boardPixelSize.height > 1f) {
                                                val dropInBoard = Offset(
                                                    x = dropInOverlay.x - boardOriginInOverlay.x,
                                                    y = dropInOverlay.y - boardOriginInOverlay.y - dragVerticalLiftPx
                                                )
                                                val bCenter = Offset(boardPixelSize.width * 0.5f, boardPixelSize.height * 0.5f)
                                                val hexR = HexaGridGeometry.computeHexRadius(boardPixelSize)
                                                val snap = HexaGridGeometry.findMagneticSnap(
                                                    point = dropInBoard,
                                                    boardCenter = bCenter,
                                                    hexRadius = hexR,
                                                    occupied = gameState.occupied,
                                                    piece = slotPiece
                                                )
                                                if (snap != null) {
                                                    val placeRes = HexaVaultEngine.placePiece(gameState, slotIdx, snap)
                                                    if (placeRes.applied) {
                                                        gameState = placeRes.nextState
                                                        if (placeRes.clearedLines.isNotEmpty()) {
                                                            emitClearEffects(placeRes)
                                                            val linesN = placeRes.clearedLines.size
                                                            val mult = placeRes.comboMultiplier
                                                            calloutBanner = when {
                                                                mult >= 5 -> "🔥 5x MEGA CLEAR!"
                                                                mult >= 3 -> "⚡ 3x TRIPLE AXIS CLEAR!"
                                                                mult == 2 -> "✨ 2x DOUBLE CLEAR!"
                                                                else -> "✦ LINE CLEARED +${placeRes.pointsAwarded}"
                                                            }
                                                            calloutColor = if (mult >= 2) ArenaGold else ArenaCyan
                                                            if (feedbackEnabled) {
                                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                ArenaSynthAudio.playHexLineClearArpeggio(linesN, mult)
                                                            }
                                                        } else if (feedbackEnabled) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            ArenaSynthAudio.playHexCrystalSnap()
                                                        }

                                                        roundTotalXp += placeRes.xpAwarded
                                                        var snap = ArenaProgressStore.updateBestHexaScoreIfHigher(context, placeRes.nextState.score)
                                                        snap = ArenaProgressStore.awardXpAndTouchStreak(context, placeRes.xpAwarded)
                                                        onSnapshotUpdated(snap)
                                                    }
                                                }
                                            }
                                        }
                                        activeDrag = null
                                    }
                                }
                                .testTag("hexa_tray_slot_$slotIdx")
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (piece != null && !isBeingDragged) {
                                    Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                                        val miniCenter = Offset(size.width * 0.5f, size.height * 0.5f)
                                        val miniR = (size.minDimension / 5.4f).coerceAtLeast(8f)
                                        val rawPts = piece.offsets.map {
                                            HexaGridGeometry.hexCenter(it, Offset.Zero, miniR)
                                        }
                                        val avgX = rawPts.map { it.x }.average().toFloat()
                                        val avgY = rawPts.map { it.y }.average().toFloat()
                                        rawPts.forEach { pt ->
                                            val drawPt = Offset(miniCenter.x + pt.x - avgX, miniCenter.y + pt.y - avgY)
                                            val path = HexaGridGeometry.buildHexPath(drawPt, miniR * 0.88f)
                                            drawPath(
                                                path = path,
                                                brush = Brush.linearGradient(
                                                    colors = listOf(piece.color.primaryColor, piece.color.secondaryColor),
                                                    start = Offset(drawPt.x - miniR, drawPt.y - miniR),
                                                    end = Offset(drawPt.x + miniR, drawPt.y + miniR)
                                                )
                                            )
                                            drawPath(
                                                path = path,
                                                color = piece.color.rimColor.copy(alpha = 0.90f),
                                                style = Stroke(width = 1.dp.toPx(), join = StrokeJoin.Round)
                                            )
                                        }
                                    }
                                } else if (piece == null) {
                                    Text(
                                        text = "•",
                                        color = DarkTextSecondary.copy(alpha = 0.35f),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating Dragged Piece Rendered Directly Above Finger (+Magnetic Shadow Snap on Board)
            val dragNow = activeDrag
            if (dragNow != null && boardPixelSize.width > 1f) {
                val dragRadius = HexaGridGeometry.computeHexRadius(boardPixelSize)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val anchorPos = Offset(
                        x = dragNow.pointerInOverlay.x,
                        y = dragNow.pointerInOverlay.y - dragVerticalLiftPx
                    )
                    dragNow.piece.offsets.forEach { offset ->
                        val hexPos = HexaGridGeometry.hexCenter(offset, anchorPos, dragRadius)
                        val hexPath = HexaGridGeometry.buildHexPath(hexPos, dragRadius * 0.92f)
                        drawPath(
                            path = hexPath,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    dragNow.piece.color.primaryColor.copy(alpha = 0.96f),
                                    dragNow.piece.color.secondaryColor.copy(alpha = 0.96f)
                                ),
                                start = Offset(hexPos.x - dragRadius, hexPos.y - dragRadius),
                                end = Offset(hexPos.x + dragRadius, hexPos.y + dragRadius)
                            )
                        )
                        drawPath(
                            path = hexPath,
                            color = Color.White.copy(alpha = 0.85f),
                            style = Stroke(width = 1.4.dp.toPx(), join = StrokeJoin.Round)
                        )
                    }
                }
            }
        }

        // Power-Up Hook Info Banner
        androidx.compose.animation.AnimatedVisibility(visible = powerUpHookMessage != null) {
            Text(
                text = powerUpHookMessage.orEmpty(),
                color = ArenaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 8.dp)
            )
        }

        // 4. Tactical Power-Up Dock (1 Free Laser Zap + 1 Free Refresh Tray + Instant Restart)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val canZap = gameState.laserZapRemaining > 0 && gameState.occupied.isNotEmpty() && !gameState.isGameOver
            OutlinedButton(
                onClick = {
                    when {
                        gameState.isZapTargeting -> gameState = HexaVaultEngine.toggleZapTargeting(gameState)
                        canZap -> {
                            gameState = HexaVaultEngine.toggleZapTargeting(gameState)
                            if (feedbackEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        gameState.laserZapRemaining <= 0 -> {
                            powerUpHookMessage = "Free Laser Zap used this match"
                        }
                        else -> {
                            powerUpHookMessage = "Place a crystal first to target Laser Zap"
                        }
                    }
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        gameState.isZapTargeting -> ArenaGold
                        canZap -> ArenaGold.copy(alpha = 0.78f)
                        else -> ArenaGold.copy(alpha = 0.25f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (gameState.isZapTargeting || canZap) ArenaGold else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("hexa_laser_zap_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Laser Zap Power-Up",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        gameState.isZapTargeting -> "Tap Cell"
                        gameState.laserZapRemaining > 0 -> "Laser Zap"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val canRefreshFree = gameState.refreshTrayRemaining > 0 && !gameState.isGameOver
            val canRefreshRewarded = gameState.refreshTrayRemaining <= 0 && !rewardedTrayUsed && !gameState.isGameOver
            OutlinedButton(
                onClick = {
                    if (canRefreshFree) {
                        gameState = HexaVaultEngine.applyRefreshTray(gameState)
                        if (feedbackEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            ArenaSynthAudio.playHexTrayRefresh()
                        }
                    } else if (canRefreshRewarded) {
                        AdManager.showRewardedAd("hexa_refresh_tray") {
                            gameState = HexaVaultEngine.applyRefreshTray(gameState.copy(refreshTrayRemaining = 1))
                            rewardedTrayUsed = true
                            if (feedbackEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                ArenaSynthAudio.playHexTrayRefresh()
                            }
                        }
                    } else {
                        powerUpHookMessage = "Tray refresh already used this match"
                    }
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        canRefreshFree -> ArenaCyan.copy(alpha = 0.78f)
                        canRefreshRewarded -> ArenaGold.copy(alpha = 0.85f)
                        else -> ArenaCyan.copy(alpha = 0.25f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = when {
                        canRefreshFree -> ArenaCyan
                        canRefreshRewarded -> ArenaGold
                        else -> DarkTextSecondary
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("hexa_refresh_tray_button")
            ) {
                Icon(
                    imageVector = if (canRefreshRewarded) Icons.Default.PlayCircle else Icons.Default.Refresh,
                    contentDescription = "Refresh Tray Power-Up",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        canRefreshFree -> "Refresh"
                        canRefreshRewarded -> "New Pieces"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    gameState = HexaVaultEngine.newGame()
                    activeLaserBeams.clear()
                    particles.clear()
                    calloutBanner = null
                    roundTotalXp = 0L
                    secondChanceUsedThisRun = false
                    doubleXpClaimedThisRound = false
                    rewardedTrayUsed = false
                    onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaCyan)
                    .testTag("hexa_restart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Restart Cyber Vault Hexa",
                    tint = ObsidianBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =============================================================================
// 11. GAME 5: QUANTUM LASER VAULT PATH (RAY-TRACED OPTICAL LOGIC & 7-DAY UNLOCK)
// =============================================================================

enum class LaserDir(val dr: Int, val dc: Int) {
    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1);

    fun turnLeft(): LaserDir = when (this) {
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
        RIGHT -> UP
    }

    fun turnRight(): LaserDir = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }
}

enum class MirrorSlashOrientation {
    /** '/' reflects UP<->RIGHT and DOWN<->LEFT */
    SLASH_POS,
    /** '\' reflects UP<->LEFT and DOWN<->RIGHT */
    BACKSLASH
}

enum class QuantumCellKind {
    EMPTY,
    EMITTER,
    MIRROR,
    SPLITTER,
    FIREWALL,
    STAR_NODE,
    VAULT_RECEIVER
}

data class GridPos(val row: Int, val col: Int)

data class QuantumCell(
    val row: Int,
    val col: Int,
    val kind: QuantumCellKind,
    val emitDir: LaserDir = LaserDir.RIGHT,
    val rotationCount: Int = 0,
    val solutionOrientation: Int? = null,
    val isBypassed: Boolean = false
) {
    fun currentOrientation(): MirrorSlashOrientation =
        if (rotationCount % 2 == 0) MirrorSlashOrientation.SLASH_POS else MirrorSlashOrientation.BACKSLASH
}

data class LaserBeamSegment(
    val fromRow: Float,
    val fromCol: Float,
    val toRow: Float,
    val toCol: Float,
    val dir: LaserDir,
    val terminatesAtFirewall: Boolean = false
)

data class QuantumTraceResult(
    val segments: List<LaserBeamSegment>,
    val chargedVaults: Set<GridPos>,
    val litStars: Set<GridPos>,
    val firewallHits: Set<GridPos>,
    val totalVaults: Int,
    val totalStars: Int
) {
    val isVaultsCharged: Boolean get() = totalVaults > 0 && chargedVaults.size == totalVaults
    val earnedStarRating: Int
        get() = when {
            !isVaultsCharged -> 0
            totalStars == 0 || litStars.size >= totalStars -> 3
            litStars.isNotEmpty() -> 2
            else -> 1
        }
    val isThreeStarPerfect: Boolean get() = earnedStarRating == 3
}

data class QuantumLevelState(
    val levelNumber: Int,
    val gridSize: Int,
    val cells: List<List<QuantumCell>>,
    val movesCount: Int = 0,
    val hintRemaining: Int = 1,
    val firewallBypassRemaining: Int = 1,
    val isBypassTargeting: Boolean = false,
    val hintedCell: GridPos? = null,
    val initialSnapshotCells: List<List<QuantumCell>> = cells
) {
    fun cellAt(row: Int, col: Int): QuantumCell? =
        cells.getOrNull(row)?.getOrNull(col)
}

object QuantumLaserEngine {

    fun gridSizeForLevel(levelNumber: Int): Int = when {
        levelNumber <= 3 -> 6
        levelNumber <= 7 -> 7
        else -> 8
    }

    fun reflectMirror(dir: LaserDir, slash: MirrorSlashOrientation): LaserDir = when (slash) {
        MirrorSlashOrientation.SLASH_POS -> when (dir) {
            LaserDir.RIGHT -> LaserDir.UP
            LaserDir.UP -> LaserDir.RIGHT
            LaserDir.DOWN -> LaserDir.LEFT
            LaserDir.LEFT -> LaserDir.DOWN
        }
        MirrorSlashOrientation.BACKSLASH -> when (dir) {
            LaserDir.RIGHT -> LaserDir.DOWN
            LaserDir.DOWN -> LaserDir.RIGHT
            LaserDir.UP -> LaserDir.LEFT
            LaserDir.LEFT -> LaserDir.UP
        }
    }

    fun requiredSlash(inDir: LaserDir, outDir: LaserDir): MirrorSlashOrientation? {
        if (reflectMirror(inDir, MirrorSlashOrientation.SLASH_POS) == outDir) {
            return MirrorSlashOrientation.SLASH_POS
        }
        if (reflectMirror(inDir, MirrorSlashOrientation.BACKSLASH) == outDir) {
            return MirrorSlashOrientation.BACKSLASH
        }
        return null
    }

    fun traceLaser(level: QuantumLevelState): QuantumTraceResult = traceLaser(level.gridSize, level.cells)

    fun traceLaser(gridSize: Int, cells: List<List<QuantumCell>>): QuantumTraceResult {
        val segments = mutableListOf<LaserBeamSegment>()
        val chargedVaults = mutableSetOf<GridPos>()
        val litStars = mutableSetOf<GridPos>()
        val firewallHits = mutableSetOf<GridPos>()

        var totalVaults = 0
        var totalStars = 0
        val queue = ArrayDeque<Pair<GridPos, LaserDir>>()
        val visited = HashSet<Int>()

        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val cell = cells[r][c]
                when (cell.kind) {
                    QuantumCellKind.EMITTER -> queue.addLast(GridPos(r, c) to cell.emitDir)
                    QuantumCellKind.VAULT_RECEIVER -> totalVaults++
                    QuantumCellKind.STAR_NODE -> totalStars++
                    else -> Unit
                }
            }
        }

        while (queue.isNotEmpty() && segments.size < 256) {
            val (origin, dir) = queue.removeFirst()
            val stateKey = ((origin.row * 16 + origin.col) * 4) + dir.ordinal
            if (!visited.add(stateKey)) continue

            var curR = origin.row + dir.dr
            var curC = origin.col + dir.dc
            var steppedOutside = false

            while (curR in 0 until gridSize && curC in 0 until gridSize) {
                val hitCell = cells[curR][curC]
                when (hitCell.kind) {
                    QuantumCellKind.EMPTY -> {
                        curR += dir.dr
                        curC += dir.dc
                    }
                    QuantumCellKind.STAR_NODE -> {
                        litStars.add(GridPos(curR, curC))
                        curR += dir.dr
                        curC += dir.dc
                    }
                    QuantumCellKind.FIREWALL -> {
                        if (hitCell.isBypassed) {
                            curR += dir.dr
                            curC += dir.dc
                        } else {
                            firewallHits.add(GridPos(curR, curC))
                            segments.add(
                                LaserBeamSegment(
                                    fromRow = origin.row.toFloat(),
                                    fromCol = origin.col.toFloat(),
                                    toRow = curR.toFloat(),
                                    toCol = curC.toFloat(),
                                    dir = dir,
                                    terminatesAtFirewall = true
                                )
                            )
                            steppedOutside = true
                            break
                        }
                    }
                    QuantumCellKind.VAULT_RECEIVER -> {
                        chargedVaults.add(GridPos(curR, curC))
                        segments.add(
                            LaserBeamSegment(
                                fromRow = origin.row.toFloat(),
                                fromCol = origin.col.toFloat(),
                                toRow = curR.toFloat(),
                                toCol = curC.toFloat(),
                                dir = dir
                            )
                        )
                        steppedOutside = true
                        break
                    }
                    QuantumCellKind.EMITTER -> {
                        segments.add(
                            LaserBeamSegment(
                                fromRow = origin.row.toFloat(),
                                fromCol = origin.col.toFloat(),
                                toRow = curR.toFloat(),
                                toCol = curC.toFloat(),
                                dir = dir
                            )
                        )
                        steppedOutside = true
                        break
                    }
                    QuantumCellKind.MIRROR -> {
                        segments.add(
                            LaserBeamSegment(
                                fromRow = origin.row.toFloat(),
                                fromCol = origin.col.toFloat(),
                                toRow = curR.toFloat(),
                                toCol = curC.toFloat(),
                                dir = dir
                            )
                        )
                        val reflected = reflectMirror(dir, hitCell.currentOrientation())
                        queue.addLast(GridPos(curR, curC) to reflected)
                        steppedOutside = true
                        break
                    }
                    QuantumCellKind.SPLITTER -> {
                        segments.add(
                            LaserBeamSegment(
                                fromRow = origin.row.toFloat(),
                                fromCol = origin.col.toFloat(),
                                toRow = curR.toFloat(),
                                toCol = curC.toFloat(),
                                dir = dir
                            )
                        )
                        queue.addLast(GridPos(curR, curC) to dir.turnLeft())
                        queue.addLast(GridPos(curR, curC) to dir.turnRight())
                        steppedOutside = true
                        break
                    }
                }
            }

            if (!steppedOutside) {
                val endR = (curR - dir.dr) + dir.dr * 0.5f
                val endC = (curC - dir.dc) + dir.dc * 0.5f
                segments.add(
                    LaserBeamSegment(
                        fromRow = origin.row.toFloat(),
                        fromCol = origin.col.toFloat(),
                        toRow = endR,
                        toCol = endC,
                        dir = dir
                    )
                )
            }
        }

        return QuantumTraceResult(
            segments = segments,
            chargedVaults = chargedVaults,
            litStars = litStars,
            firewallHits = firewallHits,
            totalVaults = totalVaults,
            totalStars = totalStars
        )
    }

    fun rotateMirror(level: QuantumLevelState, row: Int, col: Int): Pair<QuantumLevelState, Boolean> {
        val target = level.cellAt(row, col) ?: return level to false
        if (target.kind != QuantumCellKind.MIRROR) return level to false
        val currentTrace = traceLaser(level)
        if (currentTrace.isVaultsCharged) return level to false

        val mutableRows = level.cells.map { it.toMutableList() }
        mutableRows[row][col] = target.copy(rotationCount = target.rotationCount + 1)
        val nextLevel = level.copy(
            cells = mutableRows,
            movesCount = level.movesCount + 1,
            isBypassTargeting = false,
            hintedCell = if (level.hintedCell == GridPos(row, col)) null else level.hintedCell
        )
        return nextLevel to true
    }

    fun applyAutoAlignHint(level: QuantumLevelState): Pair<QuantumLevelState, GridPos?> {
        if (level.hintRemaining <= 0) return level to null
        val misaligned = buildList {
            for (r in 0 until level.gridSize) {
                for (c in 0 until level.gridSize) {
                    val cell = level.cells[r][c]
                    val sol = cell.solutionOrientation
                    if (cell.kind == QuantumCellKind.MIRROR && sol != null && (cell.rotationCount % 2) != sol) {
                        add(cell)
                    }
                }
            }
        }
        val target = misaligned.firstOrNull() ?: return level to null
        val mutableRows = level.cells.map { it.toMutableList() }
        // Advance rotationCount by +1 so animation sweeps clockwise cleanly to exact solution orientation
        mutableRows[target.row][target.col] = target.copy(rotationCount = target.rotationCount + 1)
        val pos = GridPos(target.row, target.col)
        return level.copy(
            cells = mutableRows,
            hintRemaining = level.hintRemaining - 1,
            isBypassTargeting = false,
            hintedCell = pos
        ) to pos
    }

    fun toggleFirewallBypassTargeting(level: QuantumLevelState): QuantumLevelState {
        if (level.firewallBypassRemaining <= 0) return level
        val hasActiveFirewall = level.cells.flatten().any { it.kind == QuantumCellKind.FIREWALL && !it.isBypassed }
        if (!hasActiveFirewall) return level
        return level.copy(isBypassTargeting = !level.isBypassTargeting)
    }

    fun applyFirewallBypass(level: QuantumLevelState, row: Int, col: Int): Pair<QuantumLevelState, Boolean> {
        if (level.firewallBypassRemaining <= 0) return level to false
        val target = level.cellAt(row, col) ?: return level to false
        if (target.kind != QuantumCellKind.FIREWALL || target.isBypassed) return level to false
        val mutableRows = level.cells.map { it.toMutableList() }
        mutableRows[row][col] = target.copy(isBypassed = true)
        return level.copy(
            cells = mutableRows,
            firewallBypassRemaining = level.firewallBypassRemaining - 1,
            isBypassTargeting = false
        ) to true
    }

    fun replayLevel(level: QuantumLevelState): QuantumLevelState {
        val resetCells = level.initialSnapshotCells.map { rowList ->
            rowList.map { it.copy(isBypassed = false) }
        }
        return level.copy(
            cells = resetCells,
            movesCount = 0,
            hintRemaining = 1,
            firewallBypassRemaining = 1,
            isBypassTargeting = false,
            hintedCell = null
        )
    }

    fun xpForVictory(levelNumber: Int, trace: QuantumTraceResult): Long {
        val baseXp = 45L + levelNumber * 12L
        return if (trace.isThreeStarPerfect) baseXp * 2L else baseXp
    }

    fun generateLevel(
        levelNumber: Int,
        seed: Long = 0x51A7F9C2L
    ): QuantumLevelState {
        val n = gridSizeForLevel(levelNumber)
        val rng = Random(seed xor (levelNumber.toLong() * 100_003L + 31_337L))
        val requireSplitter = levelNumber >= 4

        repeat(120) {
            val candidate = tryBuildProceduralLevel(
                levelNumber = levelNumber,
                gridSize = n,
                requireSplitter = requireSplitter,
                rng = rng
            )
            if (candidate != null) return candidate
        }
        return buildGuaranteedCanonicalLevel(levelNumber = levelNumber, gridSize = n, requireSplitter = requireSplitter, rng = rng)
    }

    private fun tryBuildProceduralLevel(
        levelNumber: Int,
        gridSize: Int,
        requireSplitter: Boolean,
        rng: Random
    ): QuantumLevelState? {
        val nodes = mutableMapOf<GridPos, QuantumCell>()
        val allCorridorEmptyCells = linkedSetOf<GridPos>()

        // Pick edge emitter firing inward
        val edgeChoice = rng.nextInt(4)
        val margin = 1
        val (emitPos, emitDir) = when (edgeChoice) {
            0 -> GridPos(rng.nextInt(margin, gridSize - margin), 0) to LaserDir.RIGHT
            1 -> GridPos(0, rng.nextInt(margin, gridSize - margin)) to LaserDir.DOWN
            2 -> GridPos(rng.nextInt(margin, gridSize - margin), gridSize - 1) to LaserDir.LEFT
            else -> GridPos(gridSize - 1, rng.nextInt(margin, gridSize - margin)) to LaserDir.UP
        }
        nodes[emitPos] = QuantumCell(emitPos.row, emitPos.col, QuantumCellKind.EMITTER, emitDir = emitDir)

        fun stepRayUntil(
            start: GridPos,
            dir: LaserDir,
            minDist: Int,
            maxDist: Int,
            keepInnerMargin: Int
        ): Pair<GridPos, List<GridPos>>? {
            val options = mutableListOf<Pair<GridPos, List<GridPos>>>()
            val stepped = mutableListOf<GridPos>()
            for (d in 1..maxDist) {
                val nr = start.row + dir.dr * d
                val nc = start.col + dir.dc * d
                if (nr !in keepInnerMargin until (gridSize - keepInnerMargin) ||
                    nc !in keepInnerMargin until (gridSize - keepInnerMargin)
                ) {
                    break
                }
                val pos = GridPos(nr, nc)
                if (pos in nodes) break
                if (d >= minDist && pos !in allCorridorEmptyCells) {
                    options.add(pos to stepped.toList())
                }
                stepped.add(pos)
            }
            if (options.isEmpty()) return null
            return options[rng.nextInt(options.size)]
        }

        if (!requireSplitter) {
            // Single-Vault zig-zag path with 2..4 mirrors
            val bendsTarget = (2 + (levelNumber / 2)).coerceAtMost(4)
            var curPos = emitPos
            var curDir = emitDir
            repeat(bendsTarget) {
                val picked = stepRayUntil(curPos, curDir, minDist = 2, maxDist = gridSize - 2, keepInnerMargin = 1)
                    ?: return null
                val (bendPos, empties) = picked
                allCorridorEmptyCells.addAll(empties)
                val turnChoices = listOf(curDir.turnLeft(), curDir.turnRight()).shuffled(rng)
                val nextDir = turnChoices.firstOrNull { cand ->
                    val r1 = bendPos.row + cand.dr * 2
                    val c1 = bendPos.col + cand.dc * 2
                    r1 in 0 until gridSize && c1 in 0 until gridSize
                } ?: return null
                val reqSlash = requiredSlash(curDir, nextDir) ?: return null
                val solVal = if (reqSlash == MirrorSlashOrientation.SLASH_POS) 0 else 1
                nodes[bendPos] = QuantumCell(
                    row = bendPos.row,
                    col = bendPos.col,
                    kind = QuantumCellKind.MIRROR,
                    rotationCount = solVal,
                    solutionOrientation = solVal
                )
                curPos = bendPos
                curDir = nextDir
            }
            val vaultPick = stepRayUntil(curPos, curDir, minDist = 2, maxDist = gridSize - 1, keepInnerMargin = 0)
                ?: return null
            val (vaultPos, vaultEmpties) = vaultPick
            allCorridorEmptyCells.addAll(vaultEmpties)
            nodes[vaultPos] = QuantumCell(vaultPos.row, vaultPos.col, QuantumCellKind.VAULT_RECEIVER)
        } else {
            // Dual-Vault path using 1..2 lead mirrors -> Crystal Beam Splitter -> 2 perpendicular Vault branches
            var curPos = emitPos
            var curDir = emitDir
            val leadBends = if (gridSize >= 8 && rng.nextBoolean()) 2 else 1
            repeat(leadBends) {
                val picked = stepRayUntil(curPos, curDir, minDist = 2, maxDist = gridSize - 2, keepInnerMargin = 1)
                    ?: return null
                val (bendPos, empties) = picked
                allCorridorEmptyCells.addAll(empties)
                val turnChoices = listOf(curDir.turnLeft(), curDir.turnRight()).shuffled(rng)
                val nextDir = turnChoices.firstOrNull { cand ->
                    val r1 = bendPos.row + cand.dr * 2
                    val c1 = bendPos.col + cand.dc * 2
                    r1 in 1 until (gridSize - 1) && c1 in 1 until (gridSize - 1)
                } ?: return null
                val reqSlash = requiredSlash(curDir, nextDir) ?: return null
                val solVal = if (reqSlash == MirrorSlashOrientation.SLASH_POS) 0 else 1
                nodes[bendPos] = QuantumCell(
                    row = bendPos.row,
                    col = bendPos.col,
                    kind = QuantumCellKind.MIRROR,
                    rotationCount = solVal,
                    solutionOrientation = solVal
                )
                curPos = bendPos
                curDir = nextDir
            }

            // Place Splitter at inner cell with room on BOTH left and right perpendicular directions
            val splitPick = stepRayUntil(curPos, curDir, minDist = 1, maxDist = gridSize - 2, keepInnerMargin = 2)
                ?: return null
            val (splitPos, splitEmpties) = splitPick
            allCorridorEmptyCells.addAll(splitEmpties)
            nodes[splitPos] = QuantumCell(splitPos.row, splitPos.col, QuantumCellKind.SPLITTER)

            // Build both branches cleanly from Splitter
            val branchDirs = listOf(curDir.turnLeft(), curDir.turnRight())
            branchDirs.forEach { bDir ->
                var bPos = splitPos
                var dirNow = bDir
                val branchMirrors = 1
                repeat(branchMirrors) {
                    val mPick = stepRayUntil(bPos, dirNow, minDist = 1, maxDist = gridSize - 2, keepInnerMargin = 1)
                        ?: return null
                    val (mPos, mEmpties) = mPick
                    allCorridorEmptyCells.addAll(mEmpties)
                    val outChoices = listOf(dirNow.turnLeft(), dirNow.turnRight()).shuffled(rng)
                    val nextD = outChoices.firstOrNull { cand ->
                        val vr = mPos.row + cand.dr * 2
                        val vc = mPos.col + cand.dc * 2
                        vr in 0 until gridSize && vc in 0 until gridSize
                    } ?: return null
                    val reqSlash = requiredSlash(dirNow, nextD) ?: return null
                    val solVal = if (reqSlash == MirrorSlashOrientation.SLASH_POS) 0 else 1
                    nodes[mPos] = QuantumCell(
                        row = mPos.row,
                        col = mPos.col,
                        kind = QuantumCellKind.MIRROR,
                        rotationCount = solVal,
                        solutionOrientation = solVal
                    )
                    bPos = mPos
                    dirNow = nextD
                }
                val vPick = stepRayUntil(bPos, dirNow, minDist = 2, maxDist = gridSize - 1, keepInnerMargin = 0)
                    ?: return null
                val (vPos, vEmpties) = vPick
                allCorridorEmptyCells.addAll(vEmpties)
                nodes[vPos] = QuantumCell(vPos.row, vPos.col, QuantumCellKind.VAULT_RECEIVER)
            }
        }

        // Place Golden Energy Star Nodes strictly on clean empty corridor cells
        val candidateStars = allCorridorEmptyCells.filter { it !in nodes }.shuffled(rng)
        val starTargetCount = if (gridSize == 6) 2 else 3
        if (candidateStars.size < starTargetCount) return null
        candidateStars.take(starTargetCount).forEach { sPos ->
            nodes[sPos] = QuantumCell(sPos.row, sPos.col, QuantumCellKind.STAR_NODE)
        }

        // Verify unscrambled grid is 100% solved with ALL Vaults & ALL Stars lit!
        val unscrambledGrid = List(gridSize) { r ->
            List(gridSize) { c ->
                nodes[GridPos(r, c)] ?: QuantumCell(r, c, QuantumCellKind.EMPTY)
            }
        }
        val verifyTrace = traceLaser(gridSize, unscrambledGrid)
        if (!verifyTrace.isThreeStarPerfect) return null

        return finalizeWithObstaclesAndScramble(
            levelNumber = levelNumber,
            gridSize = gridSize,
            nodes = nodes,
            solutionPathEmptyCells = allCorridorEmptyCells,
            rng = rng
        )
    }

    private fun buildGuaranteedCanonicalLevel(
        levelNumber: Int,
        gridSize: Int,
        requireSplitter: Boolean,
        rng: Random
    ): QuantumLevelState {
        val nodes = mutableMapOf<GridPos, QuantumCell>()
        val corridor = linkedSetOf<GridPos>()
        if (!requireSplitter) {
            // Canonical 6x6 zigzag solved layout
            nodes[GridPos(1, 0)] = QuantumCell(1, 0, QuantumCellKind.EMITTER, emitDir = LaserDir.RIGHT)
            nodes[GridPos(1, 4)] = QuantumCell(1, 4, QuantumCellKind.MIRROR, rotationCount = 1, solutionOrientation = 1)
            nodes[GridPos(4, 4)] = QuantumCell(4, 4, QuantumCellKind.MIRROR, rotationCount = 0, solutionOrientation = 0)
            nodes[GridPos(4, 1)] = QuantumCell(4, 1, QuantumCellKind.VAULT_RECEIVER)
            nodes[GridPos(1, 2)] = QuantumCell(1, 2, QuantumCellKind.STAR_NODE)
            nodes[GridPos(4, 2)] = QuantumCell(4, 2, QuantumCellKind.STAR_NODE)
            listOf(GridPos(1, 1), GridPos(1, 3), GridPos(2, 4), GridPos(3, 4), GridPos(4, 3)).forEach { corridor.add(it) }
        } else {
            // Canonical 7x7 / 8x8 dual-vault splitter layout
            val midC = gridSize / 2
            nodes[GridPos(1, 0)] = QuantumCell(1, 0, QuantumCellKind.EMITTER, emitDir = LaserDir.RIGHT)
            nodes[GridPos(1, midC)] = QuantumCell(1, midC, QuantumCellKind.MIRROR, rotationCount = 1, solutionOrientation = 1)
            nodes[GridPos(3, midC)] = QuantumCell(3, midC, QuantumCellKind.SPLITTER)
            // Left branch -> (3, 1) '\' -> DOWN to (gridSize - 1, 1)
            nodes[GridPos(3, 1)] = QuantumCell(3, 1, QuantumCellKind.MIRROR, rotationCount = 0, solutionOrientation = 0)
            nodes[GridPos(gridSize - 1, 1)] = QuantumCell(gridSize - 1, 1, QuantumCellKind.VAULT_RECEIVER)
            // Right branch -> (3, gridSize - 2) '/' -> DOWN to (gridSize - 1, gridSize - 2)
            nodes[GridPos(3, gridSize - 2)] = QuantumCell(3, gridSize - 2, QuantumCellKind.MIRROR, rotationCount = 1, solutionOrientation = 1)
            nodes[GridPos(gridSize - 1, gridSize - 2)] = QuantumCell(gridSize - 1, gridSize - 2, QuantumCellKind.VAULT_RECEIVER)
            // 3 Star Nodes along rays
            nodes[GridPos(1, 1)] = QuantumCell(1, 1, QuantumCellKind.STAR_NODE)
            nodes[GridPos(3, midC - 1)] = QuantumCell(3, midC - 1, QuantumCellKind.STAR_NODE)
            nodes[GridPos(3, midC + 1)] = QuantumCell(3, midC + 1, QuantumCellKind.STAR_NODE)
            for (c in 1 until midC) corridor.add(GridPos(1, c))
            corridor.add(GridPos(2, midC))
            for (r in 4 until gridSize - 1) {
                corridor.add(GridPos(r, 1))
                corridor.add(GridPos(r, gridSize - 2))
            }
        }
        return finalizeWithObstaclesAndScramble(
            levelNumber = levelNumber,
            gridSize = gridSize,
            nodes = nodes,
            solutionPathEmptyCells = corridor,
            rng = rng
        )
    }

    private fun finalizeWithObstaclesAndScramble(
        levelNumber: Int,
        gridSize: Int,
        nodes: MutableMap<GridPos, QuantumCell>,
        solutionPathEmptyCells: Set<GridPos>,
        rng: Random
    ): QuantumLevelState {
        // Add Neon Red Firewalls strictly outside solution nodes & corridors
        val freeCells = buildList {
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    val p = GridPos(r, c)
                    if (p !in nodes && p !in solutionPathEmptyCells) add(p)
                }
            }
        }.shuffled(rng)

        val firewallCount = when {
            gridSize <= 6 -> 3
            gridSize == 7 -> 5
            else -> 7
        }.coerceAtMost(freeCells.size)
        freeCells.take(firewallCount).forEach { fw ->
            nodes[fw] = QuantumCell(fw.row, fw.col, QuantumCellKind.FIREWALL)
        }

        // Optional decoy mirrors outside the true path in levels 3+
        if (levelNumber >= 3 && freeCells.size > firewallCount + 1) {
            val decoyCount = (1 + levelNumber / 4).coerceAtMost(2)
            freeCells.drop(firewallCount).take(decoyCount).forEach { dm ->
                nodes[dm] = QuantumCell(
                    row = dm.row,
                    col = dm.col,
                    kind = QuantumCellKind.MIRROR,
                    rotationCount = rng.nextInt(2),
                    solutionOrientation = null
                )
            }
        }

        // Scramble solution mirrors so initial puzzle requires rotation
        val solutionMirrors = nodes.values.filter { it.kind == QuantumCellKind.MIRROR && it.solutionOrientation != null }
        val scrambledNodes = nodes.toMutableMap()
        if (solutionMirrors.isNotEmpty()) {
            val shuffledMirrors = solutionMirrors.shuffled(rng)
            val flipCount = max(2, (shuffledMirrors.size + 1) / 2).coerceAtMost(shuffledMirrors.size)
            shuffledMirrors.take(flipCount).forEach { m ->
                scrambledNodes[GridPos(m.row, m.col)] = m.copy(rotationCount = (m.rotationCount + 1))
            }
        }

        val initialCells = List(gridSize) { r ->
            List(gridSize) { c ->
                scrambledNodes[GridPos(r, c)] ?: QuantumCell(r, c, QuantumCellKind.EMPTY)
            }
        }
        // Guarantee initial board isn't accidentally already solved by a lucky cross-beam
        val initialTrace = traceLaser(gridSize, initialCells)
        val finalCells = if (initialTrace.isVaultsCharged && solutionMirrors.isNotEmpty()) {
            val m = solutionMirrors.first()
            val mut = initialCells.map { it.toMutableList() }
            mut[m.row][m.col] = mut[m.row][m.col].copy(rotationCount = mut[m.row][m.col].rotationCount + 1)
            mut
        } else {
            initialCells
        }

        return QuantumLevelState(
            levelNumber = levelNumber,
            gridSize = gridSize,
            cells = finalCells,
            initialSnapshotCells = finalCells
        )
    }
}

private data class QuantumParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val radiusPx: Float,
    val color: Color
)

@Composable
private fun QuantumLaserHeroBannerCard(
    spec: ArenaGameSpec,
    snapshot: ArenaSnapshot,
    onUnlockFlashShown: () -> Unit,
    onPlayClick: () -> Unit
) {
    val isUnlocked = snapshot.isGame5Unlocked
    val progressDays = snapshot.effectiveStreakDays.coerceIn(0, ArenaProgressStore.GAME5_REQUIRED_STREAK_DAYS)

    var unlockBannerVisible by remember(snapshot.shouldShowGame5UnlockFlash) {
        mutableStateOf(snapshot.shouldShowGame5UnlockFlash)
    }
    val unlockFlashAlpha = remember { Animatable(0f) }

    LaunchedEffect(snapshot.shouldShowGame5UnlockFlash) {
        if (snapshot.shouldShowGame5UnlockFlash) {
            unlockBannerVisible = true
            unlockFlashAlpha.snapTo(0.95f)
            unlockFlashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing)
            )
            delay(1400L)
            unlockBannerVisible = false
            onUnlockFlashShown()
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B0C10),
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.linearGradient(
                colors = if (isUnlocked) {
                    listOf(ArenaCyan.copy(alpha = 0.88f), ArenaGold.copy(alpha = 0.85f), ArenaCyan.copy(alpha = 0.72f))
                } else {
                    listOf(ArenaCyan.copy(alpha = 0.45f), ArenaGold.copy(alpha = 0.55f))
                }
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_5")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF091422),
                            ObsidianBg,
                            Color(0xFF12101F)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.height * 0.5f, size.height * 0.5f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.5f, size.height * 0.5f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.25f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuantumLaserLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("quantum_laser_logo_canvas")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_quantum_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Quantum Level",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: Level ${snapshot.bestQuantumLevel}",
                                color = ArenaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        enabled = isUnlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaCyan,
                            contentColor = ObsidianBg,
                            disabledContainerColor = Color(0xFF1F2937),
                            disabledContentColor = DarkTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_quantum_play_button")
                    ) {
                        Text(
                            text = if (isUnlocked) "PLAY ▶" else "LOCKED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Frosted Glass 7-Day Streak Lock Overlay when locked
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xDD0B0C10),
                                    Color(0xEB0E1726),
                                    Color(0xDD0B0C10)
                                )
                            )
                        )
                        .border(1.2.dp, ArenaGold.copy(alpha = 0.48f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                        .testTag("quantum_streak_lock_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.75f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked by 7-Day Streak",
                                    tint = ArenaGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "🔒 Unlock at 7-Day Streak • $progressDays/7 Days",
                                    color = ArenaGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progressDays / 7f },
                            color = ArenaCyan,
                            trackColor = Color(0xFF1F2937),
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )

                        Text(
                            text = "Reach a 7-Day Koshvanta or Arena streak to unlock Quantum Optics forever!",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Celebratory Gold Unlock Flash Overlay on first 7-day milestone reveal
            if (unlockFlashAlpha.value > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(ArenaGold.copy(alpha = unlockFlashAlpha.value * 0.50f))
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = unlockBannerVisible && isUnlocked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f))
                ) {
                    Text(
                        text = "✨ 7-DAY STREAK UNLOCKED!",
                        color = ObsidianBg,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantumLaserLogoArtwork(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "quantumLogoBeam")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "prismPulse"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Quantum Laser Vault Path glowing optical prism and golden laser beam emblem"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0A1220), Color(0xFF141128))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaCyan.copy(alpha = 0.88f), ArenaGold.copy(alpha = 0.85f))),
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val w = size.width
        val h = size.height

        // Incoming Electric Cyan laser beam from left -> prism
        val inStart = Offset(w * 0.10f, cy + h * 0.14f)
        drawLine(
            color = ArenaCyan.copy(alpha = 0.32f * pulse),
            start = inStart,
            end = Offset(cx, cy),
            strokeWidth = 7.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ArenaCyan,
            start = inStart,
            end = Offset(cx, cy),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Dual Refracted Gold & Cyan Exit Beams -> top-right and bottom-right
        val outTop = Offset(w * 0.90f, h * 0.20f)
        val outBot = Offset(w * 0.90f, h * 0.80f)
        drawLine(
            color = ArenaGold.copy(alpha = 0.34f * pulse),
            start = Offset(cx, cy),
            end = outTop,
            strokeWidth = 7.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ArenaGold,
            start = Offset(cx, cy),
            end = outTop,
            strokeWidth = 2.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ArenaCyan.copy(alpha = 0.28f * pulse),
            start = Offset(cx, cy),
            end = outBot,
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ArenaCyan,
            start = Offset(cx, cy),
            end = outBot,
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Glowing 24K Gold Vault Target Node at top-right
        drawCircle(
            color = ArenaGold.copy(alpha = 0.35f * pulse),
            radius = 9.dp.toPx(),
            center = outTop
        )
        drawCircle(
            color = ArenaGold,
            radius = 5.dp.toPx(),
            center = outTop
        )

        // Central Luminous Optical Crystal Prism
        val pR = size.minDimension * 0.25f
        val prismPath = Path().apply {
            moveTo(cx, cy - pR)
            lineTo(cx + pR * 0.92f, cy + pR * 0.72f)
            lineTo(cx - pR * 0.92f, cy + pR * 0.72f)
            close()
        }
        drawPath(
            path = prismPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0F3E52), Color(0xFF0A1D32))
            )
        )
        drawPath(
            path = prismPath,
            brush = Brush.linearGradient(listOf(ArenaCyan, ArenaGold)),
            style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.92f),
            radius = 2.8.dp.toPx(),
            center = Offset(cx, cy)
        )
    }
}

@Composable
fun QuantumLaserVaultGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val startLevelNum = remember { max(1, snapshot.bestQuantumLevel) }
    var levelState by remember {
        mutableStateOf(QuantumLaserEngine.generateLevel(levelNumber = startLevelNum))
    }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var powerUpHookMessage by remember { mutableStateOf<String?>(null) }
    var awardedVictoryForLevel by remember { mutableIntStateOf(0) }
    var lastXpAwarded by remember { mutableLongStateOf(0L) }
    var doubleXpClaimedThisLevel by remember { mutableStateOf(false) }
    var rewardedHintUsedThisLevel by remember { mutableStateOf(false) }
    var prevLitStarsCount by remember { mutableIntStateOf(0) }
    var prevFirewallHitCount by remember { mutableIntStateOf(0) }

    var beamFlowPhase by remember { mutableFloatStateOf(0f) }
    val particles = remember { mutableStateListOf<QuantumParticle>() }
    var boardPixelSize by remember { mutableStateOf(Size.Zero) }

    val trace = remember(levelState.cells) { QuantumLaserEngine.traceLaser(levelState) }
    val isVictory = trace.isVaultsCharged

    // Record starting level as reached immediately on mount / level load
    LaunchedEffect(levelState.levelNumber) {
        onSnapshotUpdated(ArenaProgressStore.updateBestQuantumLevelIfHigher(context, levelState.levelNumber))
    }

    // Track newly lit Gold Star Nodes & Victory Vault Charge FX
    LaunchedEffect(trace.litStars.size, trace.isVaultsCharged, trace.firewallHits.size) {
        if (trace.litStars.size > prevLitStarsCount) {
            if (feedbackEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                ArenaSynthAudio.playStarNodeChime(trace.litStars.size)
            }
        }
        prevLitStarsCount = trace.litStars.size

        if (trace.firewallHits.size > prevFirewallHitCount && !trace.isVaultsCharged) {
            if (feedbackEnabled) {
                ArenaSynthAudio.playFirewallHitSpark()
            }
        }
        prevFirewallHitCount = trace.firewallHits.size

        if (trace.isVaultsCharged && awardedVictoryForLevel != levelState.levelNumber) {
            awardedVictoryForLevel = levelState.levelNumber
            val xpEarned = QuantumLaserEngine.xpForVictory(levelState.levelNumber, trace)
            lastXpAwarded = xpEarned
            if (feedbackEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                ArenaSynthAudio.playVaultChargedChord(trace.isThreeStarPerfect)
            }
            // Emit celebratory 24K Gold particle bursts at every Vault Receiver
            if (boardPixelSize.width > 1f) {
                val cellSpan = min(boardPixelSize.width, boardPixelSize.height) / levelState.gridSize.toFloat()
                trace.chargedVaults.forEach { vp ->
                    val vx = (vp.col + 0.5f) * cellSpan
                    val vy = (vp.row + 0.5f) * cellSpan
                    repeat(18) { idx ->
                        val angle = (idx / 18.0) * 2.0 * PI + Random.nextFloat() * 0.35f
                        val speed = 120f + Random.nextFloat() * 240f
                        particles.add(
                            QuantumParticle(
                                x = vx,
                                y = vy,
                                vx = (cos(angle) * speed).toFloat(),
                                vy = (sin(angle) * speed).toFloat() - 50f,
                                life = 0f,
                                maxLife = 0.65f + Random.nextFloat() * 0.35f,
                                radiusPx = 4f + Random.nextFloat() * 5f,
                                color = if (idx % 3 == 0) ArenaCyan else ArenaGold
                            )
                        )
                    }
                }
            }
            var snap = ArenaProgressStore.updateBestQuantumLevelIfHigher(context, levelState.levelNumber + 1)
            snap = ArenaProgressStore.awardXpAndTouchStreak(context, xpEarned)
            onSnapshotUpdated(snap)
        }
    }

    LaunchedEffect(powerUpHookMessage) {
        if (powerUpHookMessage != null) {
            delay(2200L)
            powerUpHookMessage = null
        }
    }

    // 120fps Optical Beam Particle Flow & Victory Burst Animator
    LaunchedEffect(Unit) {
        var lastNs = 0L
        while (true) {
            withFrameNanos { frameNs ->
                if (lastNs == 0L) {
                    lastNs = frameNs
                    return@withFrameNanos
                }
                val dt = ((frameNs - lastNs) / 1_000_000_000f).coerceIn(0.001f, 0.04f)
                lastNs = frameNs
                beamFlowPhase = (beamFlowPhase + dt * 1.45f) % 1f

                val pIt = particles.listIterator()
                while (pIt.hasNext()) {
                    val p = pIt.next()
                    p.life += dt
                    if (p.life >= p.maxLife) {
                        pIt.remove()
                    } else {
                        p.x += p.vx * dt
                        p.y += p.vy * dt
                        p.vy += 320f * dt
                    }
                }
            }
        }
    }

    // Smooth per-cell animated mirror rotation angles so tapping triggers a 90° spring spin
    val mirrorAngles: Map<GridPos, Float> = buildMap {
        for (r in 0 until levelState.gridSize) {
            for (c in 0 until levelState.gridSize) {
                val cell = levelState.cells[r][c]
                if (cell.kind == QuantumCellKind.MIRROR) {
                    val pos = GridPos(r, c)
                    val animAngle by animateFloatAsState(
                        targetValue = cell.rotationCount * 90f,
                        animationSpec = spring(
                            dampingRatio = 0.62f,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "mirrorRot_${levelState.levelNumber}_${r}_${c}"
                    )
                    put(pos, animAngle)
                }
            }
        }
    }

    val effectiveBestLevel = max(snapshot.bestQuantumLevel, levelState.levelNumber)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .navigationBarsPadding()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Sleek Top Bar + Sound Mute/Unmute Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        onSnapshotUpdated(ArenaProgressStore.updateBestQuantumLevelIfHigher(context, levelState.levelNumber))
                        onExitToHub()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("game_quantum_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Quantum Laser Vault",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaCyan.copy(alpha = 0.14f),
                        border = BorderStroke(0.75.dp, ArenaCyan.copy(alpha = 0.52f))
                    ) {
                        Text(
                            text = "${levelState.gridSize}x${levelState.gridSize} OPTICS • LVL ${snapshot.levelInfo.level}",
                            color = ArenaCyan,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    feedbackEnabled = !feedbackEnabled
                    onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObsidianElevated)
                    .border(1.dp, ArenaCyan.copy(alpha = 0.35f), CircleShape)
                    .testTag("game_quantum_sound_toggle")
            ) {
                Icon(
                    imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (feedbackEnabled) "Mute Sound & Haptics" else "Enable Sound & Haptics",
                    tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Clean 4-Pill Telemetry HUD: LEVEL | MOVES | STARS | BEST LEVEL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TowerHudPill(
                label = "LEVEL",
                value = "${levelState.levelNumber}",
                accentColor = ArenaCyan,
                modifier = Modifier
                    .weight(0.95f)
                    .testTag("quantum_level_pill")
            )
            TowerHudPill(
                label = "MOVES",
                value = "${levelState.movesCount}",
                accentColor = ElectricCyan,
                modifier = Modifier
                    .weight(0.95f)
                    .testTag("quantum_moves_pill")
            )
            TowerHudPill(
                label = "STARS",
                value = "${trace.litStars.size}/${trace.totalStars}",
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(0.95f)
                    .testTag("quantum_stars_pill")
            )
            TowerHudPill(
                label = "BEST LVL",
                value = "$effectiveBestLevel",
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(1.05f)
                    .testTag("quantum_best_pill")
            )
        }

        // 3. Interactive Ray-Traced Optical Grid Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF06080E),
                border = BorderStroke(
                    width = 1.4.dp,
                    color = when {
                        isVictory -> ArenaGold
                        levelState.isBypassTargeting -> RunnerTrapRed
                        else -> ArenaCyan.copy(alpha = 0.45f)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val currentLevelState by rememberUpdatedState(levelState)
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coords ->
                                boardPixelSize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                            }
                            .pointerInput(levelState.levelNumber) {
                                detectTapGestures { offset ->
                                    val st = currentLevelState
                                    val side = min(size.width.toFloat(), size.height.toFloat())
                                    if (side <= 1f) return@detectTapGestures
                                    val cellSize = side / st.gridSize.toFloat()
                                    val col = (offset.x / cellSize).toInt().coerceIn(0, st.gridSize - 1)
                                    val row = (offset.y / cellSize).toInt().coerceIn(0, st.gridSize - 1)

                                    if (st.isBypassTargeting) {
                                        val (nextLv, applied) = QuantumLaserEngine.applyFirewallBypass(st, row, col)
                                        if (applied) {
                                            levelState = nextLv
                                            if (feedbackEnabled) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                ArenaSynthAudio.playLaserSlice()
                                            }
                                        }
                                        return@detectTapGestures
                                    }

                                    val (nextLv, rotated) = QuantumLaserEngine.rotateMirror(st, row, col)
                                    if (rotated) {
                                        levelState = nextLv
                                        if (feedbackEnabled) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            ArenaSynthAudio.playOpticalMirrorClick()
                                        }
                                    }
                                }
                            }
                            .testTag("quantum_grid_canvas")
                    ) {
                        val side = min(size.width, size.height)
                        val n = levelState.gridSize
                        val cellPx = side / n.toFloat()
                        val ox = (size.width - side) * 0.5f
                        val oy = (size.height - side) * 0.5f

                        fun cellCenter(r: Float, c: Float): Offset =
                            Offset(ox + (c + 0.5f) * cellPx, oy + (r + 0.5f) * cellPx)

                        // Subtle cyber optical grid tiles
                        for (r in 0 until n) {
                            for (c in 0 until n) {
                                val topLeft = Offset(ox + c * cellPx + 2.dp.toPx(), oy + r * cellPx + 2.dp.toPx())
                                val tileSize = Size(cellPx - 4.dp.toPx(), cellPx - 4.dp.toPx())
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color(0xFF101724), Color(0xFF0A0F18)),
                                        startY = topLeft.y,
                                        endY = topLeft.y + tileSize.height
                                    ),
                                    topLeft = topLeft,
                                    size = tileSize,
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                                drawRoundRect(
                                    color = ArenaCyan.copy(alpha = 0.14f),
                                    topLeft = topLeft,
                                    size = tileSize,
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                    style = Stroke(width = 0.8.dp.toPx())
                                )
                            }
                        }

                        // Continuous Ray-Traced Neon Laser Beams & Energy Flow Particles
                        trace.segments.forEachIndexed { segIdx, seg ->
                            val startPx = cellCenter(seg.fromRow, seg.fromCol)
                            val endPx = cellCenter(seg.toRow, seg.toCol)

                            drawLine(
                                color = ArenaCyan.copy(alpha = 0.24f),
                                start = startPx,
                                end = endPx,
                                strokeWidth = 10.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = ArenaCyan.copy(alpha = 0.88f),
                                start = startPx,
                                end = endPx,
                                strokeWidth = 3.8.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.94f),
                                start = startPx,
                                end = endPx,
                                strokeWidth = 1.4.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Animated optical photon pulses travelling along segment
                            val segLen = kotlin.math.hypot(endPx.x - startPx.x, endPx.y - startPx.y)
                            val pulseCount = (segLen / (cellPx * 0.9f)).toInt().coerceIn(1, 5)
                            for (p in 0 until pulseCount) {
                                val t = (beamFlowPhase + (p.toFloat() / pulseCount) + segIdx * 0.13f) % 1f
                                val px = startPx.x + (endPx.x - startPx.x) * t
                                val py = startPx.y + (endPx.y - startPx.y) * t
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.6.dp.toPx(),
                                    center = Offset(px, py)
                                )
                            }

                            if (seg.terminatesAtFirewall) {
                                drawCircle(
                                    color = RunnerTrapRed.copy(alpha = 0.65f),
                                    radius = 6.5.dp.toPx(),
                                    center = endPx
                                )
                            }
                        }

                        // Render Interactive Optical Elements over Beams cleanly
                        for (r in 0 until n) {
                            for (c in 0 until n) {
                                val cell = levelState.cells[r][c]
                                val center = cellCenter(r.toFloat(), c.toFloat())
                                val pos = GridPos(r, c)

                                when (cell.kind) {
                                    QuantumCellKind.EMPTY -> Unit

                                    QuantumCellKind.EMITTER -> {
                                        val rad = cellPx * 0.34f
                                        drawCircle(
                                            color = ArenaCyan.copy(alpha = 0.28f),
                                            radius = rad * 1.25f,
                                            center = center
                                        )
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(Color.White, ArenaCyan, Color(0xFF0284C7)),
                                                center = center,
                                                radius = rad
                                            ),
                                            radius = rad,
                                            center = center
                                        )
                                        // Barrel nozzle in emit direction
                                        drawLine(
                                            color = ArenaCyan,
                                            start = center,
                                            end = Offset(
                                                center.x + cell.emitDir.dc * rad * 1.32f,
                                                center.y + cell.emitDir.dr * rad * 1.32f
                                            ),
                                            strokeWidth = 5.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    }

                                    QuantumCellKind.MIRROR -> {
                                        val isHinted = levelState.hintedCell == pos
                                        val rotDeg = mirrorAngles[pos] ?: (cell.rotationCount * 90f)
                                        val halfSpan = cellPx * 0.34f

                                        if (isHinted) {
                                            drawCircle(
                                                color = ArenaGold.copy(alpha = 0.34f),
                                                radius = cellPx * 0.42f,
                                                center = center
                                            )
                                            drawCircle(
                                                color = ArenaGold,
                                                radius = cellPx * 0.42f,
                                                center = center,
                                                style = Stroke(width = 1.6.dp.toPx())
                                            )
                                        }

                                        rotate(degrees = rotDeg, pivot = center) {
                                            // Titanium reflective mount + bright silver-cyan mirror blade ('/' at 0°)
                                            drawLine(
                                                color = Color(0xFF475569),
                                                start = Offset(center.x - halfSpan, center.y + halfSpan),
                                                end = Offset(center.x + halfSpan, center.y - halfSpan),
                                                strokeWidth = 7.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                            drawLine(
                                                brush = Brush.linearGradient(
                                                    colors = if (isHinted) {
                                                        listOf(ArenaGold, Color.White, ArenaGold)
                                                    } else {
                                                        listOf(ArenaCyan, Color.White, Color(0xFFCBD5E1))
                                                    },
                                                    start = Offset(center.x - halfSpan, center.y + halfSpan),
                                                    end = Offset(center.x + halfSpan, center.y - halfSpan)
                                                ),
                                                start = Offset(center.x - halfSpan, center.y + halfSpan),
                                                end = Offset(center.x + halfSpan, center.y - halfSpan),
                                                strokeWidth = 3.2.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }
                                        drawCircle(
                                            color = if (isHinted) ArenaGold else ArenaCyan,
                                            radius = 3.dp.toPx(),
                                            center = center
                                        )
                                    }

                                    QuantumCellKind.SPLITTER -> {
                                        val pR = cellPx * 0.34f
                                        val diamond = Path().apply {
                                            moveTo(center.x, center.y - pR)
                                            lineTo(center.x + pR, center.y)
                                            lineTo(center.x, center.y + pR)
                                            lineTo(center.x - pR, center.y)
                                            close()
                                        }
                                        drawPath(
                                            path = diamond,
                                            brush = Brush.radialGradient(
                                                colors = listOf(Color.White.copy(alpha = 0.85f), ArenaCyan.copy(alpha = 0.55f), Color(0xFF1E1B4B)),
                                                center = center,
                                                radius = pR
                                            )
                                        )
                                        drawPath(
                                            path = diamond,
                                            brush = Brush.linearGradient(listOf(ArenaCyan, ArenaGold)),
                                            style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round)
                                        )
                                        // Internal cross crystal facet lines
                                        drawLine(
                                            color = Color.White.copy(alpha = 0.72f),
                                            start = Offset(center.x - pR * 0.55f, center.y - pR * 0.55f),
                                            end = Offset(center.x + pR * 0.55f, center.y + pR * 0.55f),
                                            strokeWidth = 1.1.dp.toPx()
                                        )
                                        drawLine(
                                            color = Color.White.copy(alpha = 0.72f),
                                            start = Offset(center.x - pR * 0.55f, center.y + pR * 0.55f),
                                            end = Offset(center.x + pR * 0.55f, center.y - pR * 0.55f),
                                            strokeWidth = 1.1.dp.toPx()
                                        )
                                    }

                                    QuantumCellKind.FIREWALL -> {
                                        val half = cellPx * 0.34f
                                        val tl = Offset(center.x - half, center.y - half)
                                        val sz = Size(half * 2f, half * 2f)
                                        if (cell.isBypassed) {
                                            drawRoundRect(
                                                color = ArenaCyan.copy(alpha = 0.16f),
                                                topLeft = tl,
                                                size = sz,
                                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                                style = Stroke(
                                                    width = 1.5.dp.toPx(),
                                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f), 0f)
                                                )
                                            )
                                        } else {
                                            drawRoundRect(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(RunnerTrapRed.copy(alpha = 0.48f), Color(0xFF450A1E))
                                                ),
                                                topLeft = tl,
                                                size = sz,
                                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                            )
                                            drawRoundRect(
                                                color = if (levelState.isBypassTargeting) ArenaGold else RunnerTrapRed,
                                                topLeft = tl,
                                                size = sz,
                                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                                style = Stroke(width = if (levelState.isBypassTargeting) 2.2.dp.toPx() else 1.5.dp.toPx())
                                            )
                                            val xOff = half * 0.45f
                                            drawLine(
                                                color = Color.White.copy(alpha = 0.85f),
                                                start = Offset(center.x - xOff, center.y - xOff),
                                                end = Offset(center.x + xOff, center.y + xOff),
                                                strokeWidth = 1.8.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                            drawLine(
                                                color = Color.White.copy(alpha = 0.85f),
                                                start = Offset(center.x + xOff, center.y - xOff),
                                                end = Offset(center.x - xOff, center.y + xOff),
                                                strokeWidth = 1.8.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }
                                    }

                                    QuantumCellKind.STAR_NODE -> {
                                        val isLit = pos in trace.litStars
                                        val outerR = cellPx * 0.28f
                                        val innerR = outerR * 0.45f
                                        val starPath = Path()
                                        for (pt in 0 until 10) {
                                            val radius = if (pt % 2 == 0) outerR else innerR
                                            val angle = (pt * 36.0 - 90.0) * (PI / 180.0)
                                            val sx = center.x + (cos(angle) * radius).toFloat()
                                            val sy = center.y + (sin(angle) * radius).toFloat()
                                            if (pt == 0) starPath.moveTo(sx, sy) else starPath.lineTo(sx, sy)
                                        }
                                        starPath.close()
                                        if (isLit) {
                                            drawCircle(
                                                color = ArenaGold.copy(alpha = 0.34f),
                                                radius = cellPx * 0.38f,
                                                center = center
                                            )
                                        }
                                        drawPath(
                                            path = starPath,
                                            color = if (isLit) ArenaGold else ArenaGold.copy(alpha = 0.25f)
                                        )
                                        drawPath(
                                            path = starPath,
                                            color = if (isLit) Color.White else ArenaGold.copy(alpha = 0.78f),
                                            style = Stroke(width = 1.3.dp.toPx(), join = StrokeJoin.Round)
                                        )
                                    }

                                    QuantumCellKind.VAULT_RECEIVER -> {
                                        val isCharged = pos in trace.chargedVaults
                                        val rad = cellPx * 0.35f
                                        drawCircle(
                                            color = ArenaGold.copy(alpha = if (isCharged) 0.42f else 0.15f),
                                            radius = rad * 1.25f,
                                            center = center
                                        )
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = if (isCharged) {
                                                    listOf(Color.White, ArenaGold, ArenaAmber)
                                                } else {
                                                    listOf(Color(0xFF422E09), Color(0xFF1A1408))
                                                },
                                                center = center,
                                                radius = rad
                                            ),
                                            radius = rad,
                                            center = center
                                        )
                                        drawCircle(
                                            color = if (isCharged) Color.White else ArenaGold.copy(alpha = 0.85f),
                                            radius = rad,
                                            center = center,
                                            style = Stroke(width = if (isCharged) 2.2.dp.toPx() else 1.5.dp.toPx())
                                        )
                                        drawCircle(
                                            color = if (isCharged) ObsidianBg else ArenaGold,
                                            radius = rad * 0.32f,
                                            center = center
                                        )
                                    }
                                }
                            }
                        }

                        // Vault Charge Victory Particle Burst
                        particles.forEach { p ->
                            val alpha = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.radiusPx * (1f - 0.35f * (p.life / p.maxLife)),
                                center = Offset(p.x, p.y)
                            )
                        }
                    }

                    // Firewall Bypass Targeting Callout Pill
                    androidx.compose.animation.AnimatedVisibility(
                        visible = levelState.isBypassTargeting && !isVictory,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaGold,
                            border = BorderStroke(1.dp, Color.White)
                        ) {
                            Text(
                                text = "🛡 TAP A RED FIREWALL TO BYPASS",
                                color = ObsidianBg,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Celebratory Level Complete Overlay
                    if (isVictory) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ObsidianBg.copy(alpha = 0.85f))
                                .padding(20.dp)
                                .testTag("quantum_victory_overlay"),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = ObsidianElevated,
                                border = BorderStroke(1.4.dp, ArenaGold.copy(alpha = 0.88f)),
                                modifier = Modifier.fillMaxWidth(0.92f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = if (trace.isThreeStarPerfect) "24K VAULT OVERCHARGED!" else "VAULT CHARGED!",
                                        color = ArenaGold,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.9.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    // 3-Star Rating Row
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        for (sIdx in 1..3) {
                                            val earned = sIdx <= trace.earnedStarRating
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Star $sIdx",
                                                tint = if (earned) ArenaGold else DarkTextSecondary.copy(alpha = 0.30f),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = buildString {
                                            append("Moves: ${levelState.movesCount}  •  +${lastXpAwarded} XP")
                                            if (trace.isThreeStarPerfect) append(" (2x PERFECT)")
                                        },
                                        color = DarkTextSecondary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    if (lastXpAwarded > 0L) {
                                        ArenaRewardedButton(
                                            text = if (doubleXpClaimedThisLevel) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                            enabled = !doubleXpClaimedThisLevel,
                                            onClick = {
                                                AdManager.showRewardedAd("quantum_double_xp") {
                                                    val bonus = lastXpAwarded
                                                    if (bonus > 0L) {
                                                        onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                                        lastXpAwarded += bonus
                                                    }
                                                    doubleXpClaimedThisLevel = true
                                                }
                                            },
                                            accentColor = ArenaGold,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("quantum_double_xp_button")
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                AdManager.recordGameRoundCompleted("quantum_round_complete") {
                                                    awardedVictoryForLevel = 0
                                                    prevLitStarsCount = 0
                                                    prevFirewallHitCount = 0
                                                    doubleXpClaimedThisLevel = false
                                                    rewardedHintUsedThisLevel = false
                                                    particles.clear()
                                                    levelState = QuantumLaserEngine.replayLevel(levelState)
                                                }
                                            },
                                            border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.65f)),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ArenaCyan),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("quantum_victory_replay_button")
                                        ) {
                                            Text(
                                                text = "Replay",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                AdManager.recordGameRoundCompleted("quantum_round_complete") {
                                                    val nextNum = levelState.levelNumber + 1
                                                    awardedVictoryForLevel = 0
                                                    prevLitStarsCount = 0
                                                    prevFirewallHitCount = 0
                                                    doubleXpClaimedThisLevel = false
                                                    rewardedHintUsedThisLevel = false
                                                    particles.clear()
                                                    levelState = QuantumLaserEngine.generateLevel(levelNumber = nextNum)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ArenaCyan,
                                                contentColor = ObsidianBg
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1.3f)
                                                .height(48.dp)
                                                .testTag("quantum_next_level_button")
                                        ) {
                                            Text(
                                                text = "Next Level ▶",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Black
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

        // Power-Up Hook Notice Banner
        androidx.compose.animation.AnimatedVisibility(visible = powerUpHookMessage != null) {
            Text(
                text = powerUpHookMessage.orEmpty(),
                color = ArenaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 8.dp)
            )
        }

        // 4. Tactical Power-Up Dock: (1) Auto-Align Hint (2) Firewall Bypass (3) Replay Level
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val canHintFree = levelState.hintRemaining > 0 && !isVictory
            val canHintRewarded = levelState.hintRemaining <= 0 && !rewardedHintUsedThisLevel && !isVictory
            OutlinedButton(
                onClick = {
                    if (canHintFree) {
                        val (nextLv, hinted) = QuantumLaserEngine.applyAutoAlignHint(levelState)
                        if (hinted != null) {
                            levelState = nextLv
                            if (feedbackEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                ArenaSynthAudio.playPowerUpActivate()
                            }
                        } else {
                            powerUpHookMessage = "Key mirrors are already aligned!"
                        }
                    } else if (canHintRewarded) {
                        AdManager.showRewardedAd("quantum_free_hint") {
                            val (nextLv, hinted) = QuantumLaserEngine.applyAutoAlignHint(levelState.copy(hintRemaining = 1))
                            if (hinted != null) {
                                levelState = nextLv
                                rewardedHintUsedThisLevel = true
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    ArenaSynthAudio.playPowerUpActivate()
                                }
                            } else {
                                powerUpHookMessage = "Key mirrors are already aligned!"
                            }
                        }
                    } else if (!isVictory) {
                        powerUpHookMessage = "Auto-Align hints used for this level"
                    }
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = if (canHintFree || canHintRewarded) ArenaGold.copy(alpha = 0.82f) else ArenaGold.copy(alpha = 0.25f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (canHintFree || canHintRewarded) ArenaGold else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("quantum_hint_button")
            ) {
                Icon(
                    imageVector = if (canHintRewarded) Icons.Default.PlayCircle else Icons.Default.AutoAwesome,
                    contentDescription = "Quantum Auto-Align Hint",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        canHintFree -> "Auto-Align"
                        canHintRewarded -> "Get Free Hint"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val hasFirewall = remember(levelState.cells) {
                levelState.cells.flatten().any { it.kind == QuantumCellKind.FIREWALL && !it.isBypassed }
            }
            val canBypass = levelState.firewallBypassRemaining > 0 && hasFirewall && !isVictory
            OutlinedButton(
                onClick = {
                    when {
                        levelState.isBypassTargeting -> levelState = QuantumLaserEngine.toggleFirewallBypassTargeting(levelState)
                        canBypass -> {
                            levelState = QuantumLaserEngine.toggleFirewallBypassTargeting(levelState)
                            if (feedbackEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        levelState.firewallBypassRemaining <= 0 -> {
                            powerUpHookMessage = "Free Firewall Bypass used this level"
                        }
                        else -> {
                            powerUpHookMessage = "No active Firewall blocks on this grid"
                        }
                    }
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        levelState.isBypassTargeting -> ArenaGold
                        canBypass -> ArenaCyan.copy(alpha = 0.78f)
                        else -> ArenaCyan.copy(alpha = 0.25f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = when {
                        levelState.isBypassTargeting -> ArenaGold
                        canBypass -> ArenaCyan
                        else -> DarkTextSecondary
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("quantum_bypass_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Firewall Bypass Power-Up",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        levelState.isBypassTargeting -> "Tap Red"
                        levelState.firewallBypassRemaining > 0 -> "Bypass"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    awardedVictoryForLevel = 0
                    prevLitStarsCount = 0
                    prevFirewallHitCount = 0
                    doubleXpClaimedThisLevel = false
                    rewardedHintUsedThisLevel = false
                    particles.clear()
                    levelState = QuantumLaserEngine.replayLevel(levelState)
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaCyan)
                    .testTag("quantum_replay_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Replay Quantum Laser Level",
                    tint = ObsidianBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =============================================================================
// 12. GAME 6: RAPID WEALTH MATH & DISCOUNT DUEL (120FPS 45S DUEL & 10-DAY UNLOCK)
// =============================================================================

enum class MathDuelMode(val badgeLabel: String) {
    SMART_SHOPPING_DISCOUNT("SMART DISCOUNT"),
    COMPOUND_YIELD_SPEED("YIELD SPEED"),
    INSTANT_BUDGET_DELTA("BUDGET DELTA"),
    BETTER_DEAL_COMPARE("DEAL COMPARE")
}

data class MathDuelQuestion(
    val id: Long,
    val mode: MathDuelMode,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val difficultyTier: Int
)

data class MathDuelState(
    val score: Long = 0L,
    val timeRemainingMs: Long = WealthMathDuelEngine.ROUND_DURATION_MS,
    val freezeRemainingMs: Long = 0L,
    val currentQuestion: MathDuelQuestion,
    val questionActiveElapsedMs: Long = 0L,
    val questionCounter: Int = 1,
    val currentStreak: Int = 0,
    val maxStreakReached: Int = 0,
    val multiplier: Int = 1,
    val maxMultiplierReached: Int = 1,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val eliminatedIndices: Set<Int> = emptySet(),
    val fiftyFiftyRemaining: Int = 1,
    val freezeShieldRemaining: Int = 1,
    val lastAnswerWasCorrect: Boolean? = null,
    val lastPointsDelta: Long = 0L,
    val isGameOver: Boolean = false
) {
    val isGoldFeverActive: Boolean get() = currentStreak >= WealthMathDuelEngine.FEVER_STREAK_THRESHOLD
    val isFreezeActive: Boolean get() = freezeRemainingMs > 0L
    val totalAnswered: Int get() = correctCount + wrongCount
    val accuracyPercent: Int
        get() = if (totalAnswered == 0) 0 else ((correctCount * 100f) / totalAnswered).roundToInt()
}

data class MathDuelAnswerResult(
    val nextState: MathDuelState,
    val wasCorrect: Boolean,
    val pointsDelta: Long,
    val xpEarned: Long
)

object WealthMathDuelEngine {
    const val ROUND_DURATION_MS = 45_000L
    const val TIME_FREEZE_DURATION_MS = 5_000L
    const val FEVER_STREAK_THRESHOLD = 4

    fun multiplierForStreak(streak: Int): Int = when {
        streak >= 9 -> 5
        streak >= 6 -> 3
        streak >= FEVER_STREAK_THRESHOLD -> 2
        else -> 1
    }

    fun newGame(random: Random = Random.Default): MathDuelState {
        val q1 = generateQuestion(questionNumber = 1, difficultyTier = 1, random = random)
        return MathDuelState(currentQuestion = q1)
    }

    fun stepTimer(state: MathDuelState, dtMs: Long): MathDuelState {
        if (state.isGameOver || dtMs <= 0L) return state
        var remainingDt = dtMs
        var nextFreeze = state.freezeRemainingMs
        if (nextFreeze > 0L) {
            val consumedFreeze = min(nextFreeze, remainingDt)
            nextFreeze -= consumedFreeze
            remainingDt -= consumedFreeze
        }
        val nextTime = (state.timeRemainingMs - remainingDt).coerceAtLeast(0L)
        val nextElapsed = state.questionActiveElapsedMs + remainingDt
        return state.copy(
            timeRemainingMs = nextTime,
            freezeRemainingMs = nextFreeze,
            questionActiveElapsedMs = nextElapsed,
            isGameOver = nextTime <= 0L
        )
    }

    fun submitAnswer(
        state: MathDuelState,
        selectedIndex: Int,
        random: Random = Random.Default
    ): MathDuelAnswerResult {
        if (state.isGameOver || selectedIndex !in 0..3 || selectedIndex in state.eliminatedIndices) {
            return MathDuelAnswerResult(state, wasCorrect = false, pointsDelta = 0L, xpEarned = 0L)
        }
        val q = state.currentQuestion
        val isCorrect = selectedIndex == q.correctIndex

        val nextCorrect: Int
        val nextWrong: Int
        val nextStreak: Int
        val nextMult: Int
        val pointsDelta: Long
        val xpEarned: Long

        if (isCorrect) {
            nextCorrect = state.correctCount + 1
            nextWrong = state.wrongCount
            nextStreak = state.currentStreak + 1
            nextMult = multiplierForStreak(nextStreak)
            val basePts = 100L + q.difficultyTier * 20L
            val speedBonus = ((5_000L - state.questionActiveElapsedMs).coerceIn(0L, 5_000L) / 25L)
            pointsDelta = (basePts + speedBonus) * nextMult
            xpEarned = 8L * nextMult
        } else {
            nextCorrect = state.correctCount
            nextWrong = state.wrongCount + 1
            nextStreak = 0
            nextMult = 1
            pointsDelta = 0L
            xpEarned = 0L
        }

        val nextNum = state.questionCounter + 1
        val nextTier = (1 + nextCorrect / 3).coerceAtMost(5)
        val nextQuestion = generateQuestion(questionNumber = nextNum, difficultyTier = nextTier, random = random)

        val nextState = state.copy(
            score = state.score + pointsDelta,
            currentQuestion = nextQuestion,
            questionActiveElapsedMs = 0L,
            questionCounter = nextNum,
            currentStreak = nextStreak,
            maxStreakReached = max(state.maxStreakReached, nextStreak),
            multiplier = nextMult,
            maxMultiplierReached = max(state.maxMultiplierReached, nextMult),
            correctCount = nextCorrect,
            wrongCount = nextWrong,
            eliminatedIndices = emptySet(),
            lastAnswerWasCorrect = isCorrect,
            lastPointsDelta = pointsDelta
        )

        return MathDuelAnswerResult(
            nextState = nextState,
            wasCorrect = isCorrect,
            pointsDelta = pointsDelta,
            xpEarned = xpEarned
        )
    }

    fun applyFiftyFiftyEliminate(
        state: MathDuelState,
        random: Random = Random.Default
    ): MathDuelState {
        if (state.isGameOver || state.fiftyFiftyRemaining <= 0) return state
        val wrongCandidates = (0..3).filter {
            it != state.currentQuestion.correctIndex && it !in state.eliminatedIndices
        }.shuffled(random)
        if (wrongCandidates.size < 2) return state
        return state.copy(
            eliminatedIndices = state.eliminatedIndices + wrongCandidates.take(2),
            fiftyFiftyRemaining = state.fiftyFiftyRemaining - 1
        )
    }

    fun activateTimeFreeze(state: MathDuelState): MathDuelState {
        if (state.isGameOver || state.freezeShieldRemaining <= 0 || state.isFreezeActive) return state
        return state.copy(
            freezeRemainingMs = TIME_FREEZE_DURATION_MS,
            freezeShieldRemaining = state.freezeShieldRemaining - 1
        )
    }

    fun generateQuestion(
        questionNumber: Int,
        difficultyTier: Int,
        random: Random = Random.Default
    ): MathDuelQuestion {
        val modes = MathDuelMode.entries
        val mode = modes[(questionNumber - 1 + random.nextInt(modes.size)) % modes.size]
        val qId = questionNumber.toLong() * 1000L + random.nextInt(999)

        return when (mode) {
            MathDuelMode.SMART_SHOPPING_DISCOUNT -> buildDiscountQuestion(qId, difficultyTier, random)
            MathDuelMode.COMPOUND_YIELD_SPEED -> buildYieldQuestion(qId, difficultyTier, random)
            MathDuelMode.INSTANT_BUDGET_DELTA -> buildBudgetQuestion(qId, difficultyTier, random)
            MathDuelMode.BETTER_DEAL_COMPARE -> buildDealCompareQuestion(qId, difficultyTier, random)
        }
    }

    private fun fmtUsd(amount: Int): String =
        "$" + NumberFormat.getNumberInstance(Locale.US).format(amount)

    private fun buildNumericOptions(
        correctValue: Int,
        stepDeltas: List<Int>,
        random: Random
    ): Pair<List<String>, Int> {
        val wrongSet = linkedSetOf<Int>()
        for (delta in stepDeltas.shuffled(random)) {
            val cand = correctValue + delta
            if (cand > 0 && cand != correctValue) wrongSet.add(cand)
            if (wrongSet.size >= 3) break
        }
        var fallbackStep = 10
        while (wrongSet.size < 3) {
            val cand = correctValue + fallbackStep
            if (cand != correctValue) wrongSet.add(cand)
            fallbackStep += 10
        }
        val combinedValues = (wrongSet.take(3) + correctValue).shuffled(random)
        val correctIdx = combinedValues.indexOf(correctValue)
        return combinedValues.map { fmtUsd(it) } to correctIdx
    }

    private fun buildDiscountQuestion(
        id: Long,
        tier: Int,
        random: Random
    ): MathDuelQuestion {
        val discounts = if (tier <= 1) listOf(10, 20, 25, 50) else listOf(10, 15, 20, 25, 30, 40)
        val pct = discounts[random.nextInt(discounts.size)]
        val baseMultiplier = when {
            pct % 20 == 0 -> 50
            pct == 25 -> 40
            pct == 10 || pct == 30 || pct == 40 -> 50
            else -> 100
        }
        val unitCount = random.nextInt(2, (8 + tier * 4)).coerceAtLeast(2)
        val price = baseMultiplier * unitCount
        val savings = (price * pct) / 100
        val finalPrice = price - savings

        val (opts, idx) = buildNumericOptions(
            correctValue = finalPrice,
            stepDeltas = listOf(-40, -25, -20, -15, -10, 10, 15, 20, 25, 40, savings - finalPrice, 50, -50),
            random = random
        )
        return MathDuelQuestion(
            id = id,
            mode = MathDuelMode.SMART_SHOPPING_DISCOUNT,
            prompt = "${fmtUsd(price)} with $pct% OFF = ?",
            options = opts,
            correctIndex = idx,
            difficultyTier = tier
        )
    }

    private fun buildYieldQuestion(
        id: Long,
        tier: Int,
        random: Random
    ): MathDuelQuestion {
        return if (tier >= 3 && random.nextBoolean()) {
            // Crisp 2-year compounding (10% -> x1.21 or 20% -> x1.44)
            val use20 = random.nextBoolean()
            val principal = random.nextInt(2, 12) * 500
            val rate = if (use20) 20 else 10
            val finalVal = if (use20) (principal * 144) / 100 else (principal * 121) / 100
            val simpleTrap = principal + (principal * rate * 2) / 100
            val (opts, idx) = buildNumericOptions(
                correctValue = finalVal,
                stepDeltas = listOf(simpleTrap - finalVal, -50, 50, -100, 100, 120, -120, 25),
                random = random
            )
            MathDuelQuestion(
                id = id,
                mode = MathDuelMode.COMPOUND_YIELD_SPEED,
                prompt = "${fmtUsd(principal)} at $rate% Yield (2 Yrs Compound) = ?",
                options = opts,
                correctIndex = idx,
                difficultyTier = tier
            )
        } else {
            val rates = listOf(5, 8, 10, 12, 15, 20)
            val rate = rates[random.nextInt(rates.size)]
            val principal = random.nextInt(2, 12 + tier * 2) * 100
            val yieldGain = (principal * rate) / 100
            val total = principal + yieldGain
            val (opts, idx) = buildNumericOptions(
                correctValue = total,
                stepDeltas = listOf(-40, -20, -15, -10, 10, 15, 20, 40, 50, -50, 100),
                random = random
            )
            MathDuelQuestion(
                id = id,
                mode = MathDuelMode.COMPOUND_YIELD_SPEED,
                prompt = "${fmtUsd(principal)} + $rate% Annual Yield = ?",
                options = opts,
                correctIndex = idx,
                difficultyTier = tier
            )
        }
    }

    private fun buildBudgetQuestion(
        id: Long,
        tier: Int,
        random: Random
    ): MathDuelQuestion {
        val budget = random.nextInt(3, 18 + tier * 4) * 50
        return if (tier >= 2 && random.nextBoolean()) {
            val spendA = random.nextInt(1, (budget / 50).coerceAtLeast(3) - 1) * 25
            val remainingAfterA = budget - spendA
            val maxB = ((remainingAfterA - 25) / 25).coerceAtLeast(1)
            val spendB = random.nextInt(1, maxB + 1) * 25
            val left = budget - spendA - spendB
            val (opts, idx) = buildNumericOptions(
                correctValue = left,
                stepDeltas = listOf(-50, -25, -20, -15, 15, 20, 25, 50, 75, 100),
                random = random
            )
            MathDuelQuestion(
                id = id,
                mode = MathDuelMode.INSTANT_BUDGET_DELTA,
                prompt = "Budget ${fmtUsd(budget)} − (${fmtUsd(spendA)} + ${fmtUsd(spendB)}) = ?",
                options = opts,
                correctIndex = idx,
                difficultyTier = tier
            )
        } else {
            val maxSpendUnits = ((budget - 20) / 15).coerceAtLeast(2)
            val spend = random.nextInt(1, maxSpendUnits) * 15
            val left = budget - spend
            val (opts, idx) = buildNumericOptions(
                correctValue = left,
                stepDeltas = listOf(-45, -30, -15, -10, 10, 15, 30, 45, 60, -60),
                random = random
            )
            MathDuelQuestion(
                id = id,
                mode = MathDuelMode.INSTANT_BUDGET_DELTA,
                prompt = "${fmtUsd(budget)} Wallet − ${fmtUsd(spend)} Expense = ?",
                options = opts,
                correctIndex = idx,
                difficultyTier = tier
            )
        }
    }

    private fun buildDealCompareQuestion(
        id: Long,
        tier: Int,
        random: Random
    ): MathDuelQuestion {
        val pctOptions = listOf(10, 15, 20, 25, 30)
        val pct = pctOptions[random.nextInt(pctOptions.size)]
        val price = random.nextInt(2, 9 + tier) * 100
        val pctSave = (price * pct) / 100
        val delta = listOf(-25, -20, -15, 15, 20, 25, 30).random(random)
        val flatSave = (pctSave + delta).coerceAtLeast(15).let {
            if (it == pctSave) pctSave + 20 else it
        }

        val pctWins = pctSave > flatSave
        val correctText = if (pctWins) "$pct% OFF (${fmtUsd(pctSave)})" else "${fmtUsd(flatSave)} OFF (Flat)"
        val otherRealText = if (pctWins) "${fmtUsd(flatSave)} OFF (Flat)" else "$pct% OFF (${fmtUsd(pctSave)})"
        val decoyA = "$pct% OFF (${fmtUsd((pctSave + 15).coerceAtLeast(20))})"
        val decoyB = "Both Save Exact Same"

        val distinctOptions = linkedSetOf(correctText, otherRealText, decoyA, decoyB).toList().take(4)
        val shuffled = distinctOptions.shuffled(random)
        val correctIdx = shuffled.indexOf(correctText)

        return MathDuelQuestion(
            id = id,
            mode = MathDuelMode.BETTER_DEAL_COMPARE,
            prompt = "Better Deal on ${fmtUsd(price)}: $pct% OFF vs ${fmtUsd(flatSave)} OFF?",
            options = shuffled,
            correctIndex = correctIdx,
            difficultyTier = tier
        )
    }
}

private data class MathDuelParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val radiusPx: Float,
    val color: Color
)

@Composable
private fun MathDuelHeroBannerCard(
    spec: ArenaGameSpec,
    snapshot: ArenaSnapshot,
    onUnlockFlashShown: () -> Unit,
    onPlayClick: () -> Unit
) {
    val isUnlocked = snapshot.isGame6Unlocked
    val progressDays = snapshot.effectiveStreakDays.coerceIn(0, ArenaProgressStore.GAME6_REQUIRED_STREAK_DAYS)
    val formattedBest = remember(snapshot.bestMathDuelScore) {
        NumberFormat.getNumberInstance(Locale.US).format(snapshot.bestMathDuelScore)
    }

    var unlockBannerVisible by remember(snapshot.shouldShowGame6UnlockFlash) {
        mutableStateOf(snapshot.shouldShowGame6UnlockFlash)
    }
    val unlockFlashAlpha = remember { Animatable(0f) }

    LaunchedEffect(snapshot.shouldShowGame6UnlockFlash) {
        if (snapshot.shouldShowGame6UnlockFlash) {
            unlockBannerVisible = true
            unlockFlashAlpha.snapTo(0.95f)
            unlockFlashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing)
            )
            delay(1400L)
            unlockBannerVisible = false
            onUnlockFlashShown()
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B0C10),
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.linearGradient(
                colors = if (isUnlocked) {
                    listOf(ArenaGold.copy(alpha = 0.90f), ArenaCyan.copy(alpha = 0.85f), ArenaGold.copy(alpha = 0.72f))
                } else {
                    listOf(ArenaGold.copy(alpha = 0.55f), ArenaCyan.copy(alpha = 0.45f))
                }
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_6")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF14110A),
                            ObsidianBg,
                            Color(0xFF0A1420)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.height * 0.5f, size.height * 0.5f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.5f, size.height * 0.5f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.25f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MathDuelLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("math_duel_logo_canvas")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_math_duel_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Math Duel Score",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: $formattedBest",
                                color = ArenaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        enabled = isUnlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaGold,
                            contentColor = ObsidianBg,
                            disabledContainerColor = Color(0xFF1F2937),
                            disabledContentColor = DarkTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_math_duel_play_button")
                    ) {
                        Text(
                            text = if (isUnlocked) "PLAY ▶" else "LOCKED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Frosted Glass 10-Day Streak Lock Overlay when locked
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xDD0B0C10),
                                    Color(0xEB101724),
                                    Color(0xDD0B0C10)
                                )
                            )
                        )
                        .border(1.2.dp, ArenaGold.copy(alpha = 0.50f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                        .testTag("math_duel_streak_lock_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.78f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked by 10-Day Streak",
                                    tint = ArenaGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "🔒 Unlock at 10-Day Streak • $progressDays/10 Days",
                                    color = ArenaGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progressDays / 10f },
                            color = ArenaCyan,
                            trackColor = Color(0xFF1F2937),
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )

                        Text(
                            text = "Reach a 10-Day Koshvanta or Arena streak to unlock Speed Wealth Duel forever!",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Celebratory Gold Unlock Flash Overlay on first 10-day milestone reveal
            if (unlockFlashAlpha.value > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(ArenaGold.copy(alpha = unlockFlashAlpha.value * 0.50f))
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = unlockBannerVisible && isUnlocked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f))
                ) {
                    Text(
                        text = "✨ 10-DAY STREAK UNLOCKED!",
                        color = ObsidianBg,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MathDuelLogoArtwork(
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "mathDuelLogoGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.68f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "calcBoltPulse"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Rapid Wealth Math Duel cyber calculator core and neon lightning bolt emblem"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0B1322), Color(0xFF1A140A))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaGold.copy(alpha = 0.88f), ArenaCyan.copy(alpha = 0.85f))),
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.5f

        // Sleek Cyber Calculator Body
        val calcW = w * 0.56f
        val calcH = h * 0.68f
        val calcTl = Offset(cx - calcW * 0.52f, cy - calcH * 0.48f)
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF152033), Color(0xFF0C121D))),
            topLeft = calcTl,
            size = Size(calcW, calcH),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )
        drawRoundRect(
            color = ArenaCyan.copy(alpha = 0.72f * pulse),
            topLeft = calcTl,
            size = Size(calcW, calcH),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = 1.4.dp.toPx())
        )

        // Glowing Hologram Screen inside Calculator
        val scrTl = Offset(calcTl.x + 6.dp.toPx(), calcTl.y + 6.dp.toPx())
        val scrSize = Size(calcW - 12.dp.toPx(), calcH * 0.28f)
        drawRoundRect(
            color = ArenaCyan.copy(alpha = 0.22f),
            topLeft = scrTl,
            size = scrSize,
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        val screenText = textMeasurer.measure(
            text = "%+$",
            style = TextStyle(
                color = ArenaCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        )
        drawText(
            textLayoutResult = screenText,
            topLeft = Offset(
                scrTl.x + (scrSize.width - screenText.size.width) * 0.5f,
                scrTl.y + (scrSize.height - screenText.size.height) * 0.5f
            )
        )

        // Tactile Mini Matrix Keypad Dots
        val dotStartY = scrTl.y + scrSize.height + 8.dp.toPx()
        val dotGapX = (calcW - 18.dp.toPx()) / 2f
        val dotGapY = (calcH * 0.52f) / 2.2f
        for (row in 0..1) {
            for (col in 0..2) {
                drawCircle(
                    color = if (row == 1 && col == 2) ArenaGold else ArenaCyan.copy(alpha = 0.58f),
                    radius = 3.2.dp.toPx(),
                    center = Offset(calcTl.x + 9.dp.toPx() + col * dotGapX, dotStartY + row * dotGapY)
                )
            }
        }

        // Striking 24K Gold Neon Lightning Bolt Accent
        val boltPath = Path().apply {
            moveTo(cx + calcW * 0.28f, cy - calcH * 0.44f)
            lineTo(cx + calcW * 0.02f, cy + calcH * 0.02f)
            lineTo(cx + calcW * 0.24f, cy + calcH * 0.02f)
            lineTo(cx - calcW * 0.02f, cy + calcH * 0.48f)
            lineTo(cx + calcW * 0.44f, cy - calcH * 0.05f)
            lineTo(cx + calcW * 0.20f, cy - calcH * 0.05f)
            close()
        }
        drawPath(
            path = boltPath,
            brush = Brush.verticalGradient(listOf(Color.White, ArenaGold, ArenaAmber))
        )
        drawPath(
            path = boltPath,
            color = Color.White.copy(alpha = 0.85f),
            style = Stroke(width = 1.dp.toPx(), join = StrokeJoin.Round)
        )
    }
}

@Composable
fun RapidWealthMathDuelGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var duelState by remember { mutableStateOf(WealthMathDuelEngine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var powerUpHookMessage by remember { mutableStateOf<String?>(null) }
    var calloutBanner by remember { mutableStateOf<String?>(null) }
    var calloutColor by remember { mutableStateOf(ArenaCyan) }
    var lastHeartbeatSec by remember { mutableIntStateOf(6) }
    var bestUpdatedOnFinish by remember { mutableStateOf(false) }
    var roundTotalXp by remember { mutableLongStateOf(0L) }
    var secondChanceUsedThisRun by remember { mutableStateOf(false) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }

    val particles = remember { mutableStateListOf<MathDuelParticle>() }
    var arenaCanvasSize by remember { mutableStateOf(Size.Zero) }

    // Smooth Odometer Score Roll Animation
    val animatedScore by animateFloatAsState(
        targetValue = duelState.score.toFloat(),
        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
        label = "duelOdometerScore"
    )

    // Gold Fever Screen Rim Glow
    val feverBorderAlpha by animateFloatAsState(
        targetValue = if (duelState.isGoldFeverActive && !duelState.isGameOver) 0.92f else 0f,
        animationSpec = tween(durationMillis = 320),
        label = "duelFeverBorder"
    )

    LaunchedEffect(calloutBanner) {
        if (calloutBanner != null) {
            delay(1100L)
            calloutBanner = null
        }
    }
    LaunchedEffect(powerUpHookMessage) {
        if (powerUpHookMessage != null) {
            delay(2100L)
            powerUpHookMessage = null
        }
    }

    // 120fps Countdown Ring Timer, Final-5s Heartbeat Tick & Particle Loop
    LaunchedEffect(duelState.isGameOver) {
        if (duelState.isGameOver) return@LaunchedEffect
        var lastNs = 0L
        var accMs = 0L
        while (!duelState.isGameOver) {
            withFrameNanos { frameNs ->
                if (lastNs == 0L) {
                    lastNs = frameNs
                    return@withFrameNanos
                }
                val dtNs = (frameNs - lastNs).coerceAtMost(50_000_000L)
                lastNs = frameNs
                val dtSec = dtNs / 1_000_000_000f
                accMs += dtNs / 1_000_000L

                if (accMs >= 16L) {
                    val stepMs = accMs
                    accMs = 0L
                    val next = WealthMathDuelEngine.stepTimer(duelState, stepMs)
                    duelState = next

                    val secLeft = kotlin.math.ceil(next.timeRemainingMs / 1000.0).toInt()
                    if (!next.isFreezeActive && secLeft in 1..5 && secLeft < lastHeartbeatSec) {
                        lastHeartbeatSec = secLeft
                        if (feedbackEnabled) {
                            ArenaSynthAudio.playDuelHeartbeatTick()
                        }
                    }
                }

                // Update celebration particles
                val pIt = particles.listIterator()
                while (pIt.hasNext()) {
                    val p = pIt.next()
                    p.life += dtSec
                    if (p.life >= p.maxLife) {
                        pIt.remove()
                    } else {
                        p.x += p.vx * dtSec
                        p.y += p.vy * dtSec
                        p.vy += 360f * dtSec
                    }
                }
            }
        }
    }

    // Persist All-Time Best Math Duel Score when round finishes
    LaunchedEffect(duelState.isGameOver) {
        if (duelState.isGameOver && !bestUpdatedOnFinish) {
            bestUpdatedOnFinish = true
            onSnapshotUpdated(ArenaProgressStore.updateBestMathDuelScoreIfHigher(context, duelState.score))
        }
    }

    val effectiveBest = max(snapshot.bestMathDuelScore, duelState.score)
    val formattedAnimatedScore = remember(animatedScore.roundToInt()) {
        NumberFormat.getNumberInstance(Locale.US).format(animatedScore.roundToInt())
    }
    val formattedBest = remember(effectiveBest) {
        NumberFormat.getNumberInstance(Locale.US).format(effectiveBest)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .then(
                if (feverBorderAlpha > 0.02f) {
                    Modifier.border(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                ArenaGold.copy(alpha = feverBorderAlpha),
                                ArenaCyan.copy(alpha = feverBorderAlpha * 0.7f),
                                ArenaGold.copy(alpha = feverBorderAlpha)
                            )
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Sleek Top Navigation & Sound Mute/Unmute Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = {
                            onSnapshotUpdated(ArenaProgressStore.updateBestMathDuelScoreIfHigher(context, duelState.score))
                            onExitToHub()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("game_math_duel_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Mind Arena Hub",
                            tint = DarkTextPrimary
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Rapid Wealth Duel",
                            color = DarkTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = (if (duelState.isGoldFeverActive) ArenaGold else ArenaCyan).copy(alpha = 0.14f),
                            border = BorderStroke(
                                0.75.dp,
                                (if (duelState.isGoldFeverActive) ArenaGold else ArenaCyan).copy(alpha = 0.52f)
                            )
                        ) {
                            Text(
                                text = when {
                                    duelState.isFreezeActive -> "❄ FROST SHIELD ACTIVE"
                                    duelState.isGoldFeverActive -> "🔥 GOLD FEVER x${duelState.multiplier}"
                                    else -> "45S SPEED BRAIN • LVL ${snapshot.levelInfo.level}"
                                },
                                color = if (duelState.isGoldFeverActive) ArenaGold else ArenaCyan,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        feedbackEnabled = !feedbackEnabled
                        onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ObsidianElevated)
                        .border(1.dp, ArenaCyan.copy(alpha = 0.35f), CircleShape)
                        .testTag("game_math_duel_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = if (feedbackEnabled) "Mute Sound & Haptics" else "Enable Sound & Haptics",
                        tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 2. Clean 3-Pill Telemetry HUD: SCORE | STREAK (x2/x5) | ALL-TIME BEST
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TowerHudPill(
                    label = "SCORE",
                    value = formattedAnimatedScore,
                    accentColor = ArenaCyan,
                    modifier = Modifier
                        .weight(1.15f)
                        .testTag("math_duel_score_pill")
                )
                TowerHudPill(
                    label = "STREAK",
                    value = "${duelState.currentStreak} (x${duelState.multiplier})",
                    accentColor = if (duelState.isGoldFeverActive) ArenaGold else ElectricCyan,
                    modifier = Modifier
                        .weight(1.05f)
                        .testTag("math_duel_streak_pill")
                )
                TowerHudPill(
                    label = "BEST",
                    value = formattedBest,
                    accentColor = ArenaGold,
                    modifier = Modifier
                        .weight(1.0f)
                        .testTag("math_duel_best_pill")
                )
            }

            // 3. Central 120fps Countdown Ring + Glowing Hologram Challenge Card + 4 Answer Pills
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .onGloballyPositioned { coords ->
                        arenaCanvasSize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Countdown Ring + Mode Badge + Timer Text
                    val timeRatio = (duelState.timeRemainingMs.toFloat() / WealthMathDuelEngine.ROUND_DURATION_MS.toFloat()).coerceIn(0f, 1f)
                    val secDisplay = kotlin.math.ceil(duelState.timeRemainingMs / 1000.0).toInt()
                    val ringColor = when {
                        duelState.isFreezeActive -> ArenaCyan
                        duelState.timeRemainingMs > 15_000L -> ArenaCyan
                        duelState.timeRemainingMs > 5_000L -> ArenaGold
                        else -> RunnerTrapRed
                    }

                    Box(
                        modifier = Modifier.size(104.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeW = 7.dp.toPx()
                            drawArc(
                                color = Color(0xFF1E293B),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = ringColor.copy(alpha = 0.25f),
                                startAngle = -90f,
                                sweepAngle = 360f * timeRatio,
                                useCenter = false,
                                style = Stroke(width = strokeW + 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = ringColor,
                                startAngle = -90f,
                                sweepAngle = 360f * timeRatio,
                                useCenter = false,
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (duelState.isFreezeActive) "❄${secDisplay}s" else "${secDisplay}s",
                                color = ringColor,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Q#${duelState.questionCounter}",
                                color = DarkTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Glowing Hologram Challenge Card
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (duelState.isFreezeActive) Color(0xFF0A1D2B) else Color(0xFF0B101B),
                        border = BorderStroke(
                            width = 1.4.dp,
                            color = when {
                                duelState.isFreezeActive -> ArenaCyan
                                duelState.isGoldFeverActive -> ArenaGold
                                else -> ArenaCyan.copy(alpha = 0.55f)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f)
                            .testTag("math_duel_question_card")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = ArenaGold.copy(alpha = 0.14f),
                                    border = BorderStroke(0.8.dp, ArenaGold.copy(alpha = 0.55f))
                                ) {
                                    Text(
                                        text = duelState.currentQuestion.mode.badgeLabel,
                                        color = ArenaGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.9.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = duelState.currentQuestion.prompt,
                                    color = DarkTextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 26.sp
                                )

                                androidx.compose.animation.AnimatedVisibility(visible = calloutBanner != null) {
                                    Text(
                                        text = calloutBanner.orEmpty(),
                                        color = calloutColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                            }
                        }
                    }

                    // 4 Tactile Dark Neon Glass Answer Pills (2x2 Grid for fast thumb reflex)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (rowIdx in 0..1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (colIdx in 0..1) {
                                    val optIdx = rowIdx * 2 + colIdx
                                    val optText = duelState.currentQuestion.options.getOrElse(optIdx) { "" }
                                    val isEliminated = optIdx in duelState.eliminatedIndices
                                    Surface(
                                        onClick = {
                                            if (!isEliminated && !duelState.isGameOver) {
                                                val res = WealthMathDuelEngine.submitAnswer(duelState, optIdx)
                                                duelState = res.nextState
                                                if (res.wasCorrect) {
                                                    calloutBanner = if (res.nextState.isGoldFeverActive) {
                                                        "⚡ +${res.pointsDelta} • FEVER x${res.nextState.multiplier}!"
                                                    } else {
                                                        "✓ +${res.pointsDelta} PTS"
                                                    }
                                                    calloutColor = if (res.nextState.isGoldFeverActive) ArenaGold else ArenaCyan
                                                    if (feedbackEnabled) {
                                                        haptic.performHapticFeedback(
                                                            if (res.nextState.isGoldFeverActive) HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove
                                                        )
                                                        ArenaSynthAudio.playDuelCorrectStreakChime(res.nextState.currentStreak)
                                                    }
                                                    if (res.nextState.isGoldFeverActive && arenaCanvasSize.width > 1f) {
                                                        val cx = arenaCanvasSize.width * 0.5f
                                                        val cy = arenaCanvasSize.height * 0.42f
                                                        repeat(10) { pIdx ->
                                                            val angle = (pIdx / 10.0) * 2.0 * PI + Random.nextFloat() * 0.4f
                                                            val speed = 130f + Random.nextFloat() * 210f
                                                            particles.add(
                                                                MathDuelParticle(
                                                                    x = cx,
                                                                    y = cy,
                                                                    vx = (cos(angle) * speed).toFloat(),
                                                                    vy = (sin(angle) * speed).toFloat() - 50f,
                                                                    life = 0f,
                                                                    maxLife = 0.55f,
                                                                    radiusPx = 4f + Random.nextFloat() * 4f,
                                                                    color = if (pIdx % 2 == 0) ArenaGold else ArenaCyan
                                                                )
                                                            )
                                                        }
                                                    }
                                                    roundTotalXp += res.xpEarned
                                                    var snap = ArenaProgressStore.updateBestMathDuelScoreIfHigher(context, res.nextState.score)
                                                    snap = ArenaProgressStore.awardXpAndTouchStreak(context, res.xpEarned)
                                                    onSnapshotUpdated(snap)
                                                } else {
                                                    calloutBanner = "✕ Streak Reset"
                                                    calloutColor = RunnerTrapRed
                                                    if (feedbackEnabled) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        ArenaSynthAudio.playDuelWrongBuzz()
                                                    }
                                                }
                                            }
                                        },
                                        enabled = !isEliminated && !duelState.isGameOver,
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isEliminated) Color(0xFF0A0C12) else Color(0xFF0E1624),
                                        border = BorderStroke(
                                            width = 1.2.dp,
                                            color = when {
                                                isEliminated -> Color(0xFF1F2937)
                                                duelState.isGoldFeverActive -> ArenaGold.copy(alpha = 0.72f)
                                                else -> ArenaCyan.copy(alpha = 0.55f)
                                            }
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .heightIn(min = 48.dp)
                                            .testTag("math_duel_option_$optIdx")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isEliminated) "⚡ ZAP" else optText,
                                                color = if (isEliminated) DarkTextSecondary.copy(alpha = 0.35f) else DarkTextPrimary,
                                                fontSize = 15.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace,
                                                textAlign = TextAlign.Center,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Fever Particle Burst Overlay Canvas
                if (particles.isNotEmpty()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        particles.forEach { p ->
                            val alpha = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.radiusPx * (1f - 0.35f * (p.life / p.maxLife)),
                                center = Offset(p.x, p.y)
                            )
                        }
                    }
                }

                // End-of-Round Mental Speed & Accuracy Summary Overlay
                if (duelState.isGameOver) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ObsidianBg.copy(alpha = 0.90f))
                            .padding(18.dp)
                            .testTag("math_duel_summary_overlay"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = ObsidianElevated,
                            border = BorderStroke(1.4.dp, ArenaGold.copy(alpha = 0.88f)),
                            modifier = Modifier.fillMaxWidth(0.95f)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "45S DUEL COMPLETE",
                                    color = ArenaGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )

                                val formattedFinal = remember(duelState.score) {
                                    NumberFormat.getNumberInstance(Locale.US).format(duelState.score)
                                }
                                Text(
                                    text = formattedFinal,
                                    color = ArenaCyan,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )

                                Text(
                                    text = "Correct: ${duelState.correctCount}/${duelState.totalAnswered} (${duelState.accuracyPercent}% Accuracy)\nPeak Streak: ${duelState.maxStreakReached} • Max Multiplier: x${duelState.maxMultiplierReached} • +$roundTotalXp XP",
                                    color = DarkTextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 17.sp
                                )

                                if (!secondChanceUsedThisRun) {
                                    ArenaRewardedButton(
                                        text = "Second Chance — +15s Bonus",
                                        onClick = {
                                            AdManager.showRewardedAd("duel_second_chance") {
                                                duelState = duelState.copy(
                                                    isGameOver = false,
                                                    timeRemainingMs = 15_000L
                                                )
                                                bestUpdatedOnFinish = false
                                                lastHeartbeatSec = 6
                                                secondChanceUsedThisRun = true
                                            }
                                        },
                                        accentColor = ArenaGold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("duel_rewarded_second_chance_button")
                                    )
                                }

                                if (roundTotalXp > 0L) {
                                    ArenaRewardedButton(
                                        text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                        enabled = !doubleXpClaimedThisRound,
                                        onClick = {
                                            AdManager.showRewardedAd("duel_double_xp") {
                                                val bonus = roundTotalXp
                                                if (bonus > 0L) {
                                                    onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                                    roundTotalXp += bonus
                                                }
                                                doubleXpClaimedThisRound = true
                                            }
                                        },
                                        accentColor = ArenaCyan,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("duel_double_xp_button")
                                    )
                                }

                                Button(
                                    onClick = {
                                        AdManager.recordGameRoundCompleted("duel_round_complete") {
                                            duelState = WealthMathDuelEngine.newGame()
                                            particles.clear()
                                            calloutBanner = null
                                            lastHeartbeatSec = 6
                                            bestUpdatedOnFinish = false
                                            roundTotalXp = 0L
                                            secondChanceUsedThisRun = false
                                            doubleXpClaimedThisRound = false
                                            onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ArenaCyan,
                                        contentColor = ObsidianBg
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("duel_play_again_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Replay,
                                        contentDescription = "Play Again",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Play Again",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Power-Up Hook Info Banner
            androidx.compose.animation.AnimatedVisibility(visible = powerUpHookMessage != null) {
                Text(
                    text = powerUpHookMessage.orEmpty(),
                    color = ArenaGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(horizontal = 8.dp)
                )
            }

            // 4. Tactical Power-Up Dock: (1) 50:50 Quantum Eliminate (2) Time Freeze Shield (3) Restart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val canFifty = duelState.fiftyFiftyRemaining > 0 && !duelState.isGameOver
                OutlinedButton(
                    onClick = {
                        if (canFifty) {
                            duelState = WealthMathDuelEngine.applyFiftyFiftyEliminate(duelState)
                            if (feedbackEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                ArenaSynthAudio.playLaserSlice()
                            }
                        } else if (!duelState.isGameOver) {
                            powerUpHookMessage = "Free 50:50 Eliminate used this duel"
                        }
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (canFifty) ArenaGold.copy(alpha = 0.78f) else ArenaGold.copy(alpha = 0.25f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (canFifty) ArenaGold else DarkTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("duel_fifty_fifty_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "50:50 Quantum Eliminate",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (duelState.fiftyFiftyRemaining > 0) "50:50 Zap" else "Used",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val canFreeze = duelState.freezeShieldRemaining > 0 && !duelState.isFreezeActive && !duelState.isGameOver
                OutlinedButton(
                    onClick = {
                        when {
                            duelState.isFreezeActive -> Unit
                            canFreeze -> {
                                duelState = WealthMathDuelEngine.activateTimeFreeze(duelState)
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    ArenaSynthAudio.playPowerUpActivate()
                                }
                            }
                            !duelState.isGameOver -> {
                                powerUpHookMessage = "Free Time Freeze used this duel"
                            }
                        }
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = when {
                            duelState.isFreezeActive -> ArenaCyan
                            canFreeze -> ArenaCyan.copy(alpha = 0.78f)
                            else -> ArenaCyan.copy(alpha = 0.25f)
                        }
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (duelState.isFreezeActive || canFreeze) ArenaCyan else DarkTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("duel_freeze_shield_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Time Freeze Shield",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when {
                            duelState.isFreezeActive -> {
                                val fs = kotlin.math.ceil(duelState.freezeRemainingMs / 1000.0).toInt()
                                "Frost ${fs}s"
                            }
                            duelState.freezeShieldRemaining > 0 -> "Freeze 5s"
                            else -> "Used"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = {
                        duelState = WealthMathDuelEngine.newGame()
                        particles.clear()
                        calloutBanner = null
                        lastHeartbeatSec = 6
                        bestUpdatedOnFinish = false
                        roundTotalXp = 0L
                        secondChanceUsedThisRun = false
                        doubleXpClaimedThisRound = false
                        onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaCyan)
                        .testTag("duel_restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Restart Rapid Wealth Math Duel",
                        tint = ObsidianBg,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// 13. GAME 7: NEON BRICK & ASSET BREAKER (120FPS & 3 NO-SPEND DAYS UNLOCK)
// =============================================================================

enum class BreakerBrickType {
    CYAN_GLASS,
    TITANIUM_SHIELD,
    GOLD_BONUS,
    PLASMA_CORE
}

enum class BreakerCapsuleType(val shortLabel: String) {
    MULTI_BALL("3X"),
    WIDE_PADDLE("WIDE"),
    HYPER_FIREBALL("FIRE")
}

data class BreakerBrick(
    val id: Long,
    val row: Int,
    val col: Int,
    val type: BreakerBrickType,
    val hp: Int,
    val maxHp: Int,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val centerX: Float get() = (left + right) * 0.5f
    val centerY: Float get() = (top + bottom) * 0.5f
    val isCracked: Boolean get() = hp < maxHp
}

data class BreakerBall(
    val id: Long,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val trail: List<Pair<Float, Float>> = emptyList()
)

data class BreakerCapsule(
    val id: Long,
    val type: BreakerCapsuleType,
    val x: Float,
    val y: Float,
    val vy: Float = 0.44f
)

data class BreakerLaserShot(
    val id: Long,
    val x: Float,
    val y: Float,
    val vy: Float = -1.65f
)

data class BreakerShockwaveEvent(
    val cx: Float,
    val cy: Float
)

data class BreakerShardEvent(
    val cx: Float,
    val cy: Float,
    val brickType: BreakerBrickType
)

data class BreakerStepResult(
    val nextState: BreakerGameState,
    val pointsGained: Long = 0L,
    val xpGained: Long = 0L,
    val brokenBricks: List<BreakerShardEvent> = emptyList(),
    val explosions: List<BreakerShockwaveEvent> = emptyList(),
    val wallSparks: List<Pair<Float, Float>> = emptyList(),
    val caughtCapsules: List<BreakerCapsuleType> = emptyList(),
    val hitPaddle: Boolean = false,
    val firedLasers: Boolean = false,
    val waveCleared: Boolean = false,
    val lostLife: Boolean = false
)

data class BreakerGameState(
    val wave: Int = 1,
    val score: Long = 0L,
    val lives: Int = NeonBreakerEngine.INITIAL_LIVES,
    val paddleX: Float = NeonBreakerEngine.BOARD_W * 0.5f,
    val bricks: List<BreakerBrick> = emptyList(),
    val balls: List<BreakerBall> = emptyList(),
    val capsules: List<BreakerCapsule> = emptyList(),
    val laserShots: List<BreakerLaserShot> = emptyList(),
    val comboHits: Int = 0,
    val widePaddleRemainingMs: Long = 0L,
    val fireballRemainingMs: Long = 0L,
    val laserCannonRemainingMs: Long = 0L,
    val laserCooldownMs: Long = 0L,
    val laserPowerUpRemaining: Int = 1,
    val secondChanceRemaining: Int = 1,
    val revivePromptRemainingMs: Long = 0L,
    val nextEntityId: Long = 100L,
    val totalBricksDestroyed: Int = 0,
    val isGameOver: Boolean = false
) {
    val isWidePaddleActive: Boolean get() = widePaddleRemainingMs > 0L
    val isFireballActive: Boolean get() = fireballRemainingMs > 0L
    val isLaserCannonActive: Boolean get() = laserCannonRemainingMs > 0L
    val isRevivePromptActive: Boolean get() = revivePromptRemainingMs > 0L && !isGameOver
    val paddleWidth: Float
        get() = if (isWidePaddleActive) NeonBreakerEngine.WIDE_PADDLE_W else NeonBreakerEngine.DEFAULT_PADDLE_W
}

object NeonBreakerEngine {
    const val BOARD_W = 1.0f
    const val BOARD_H = 1.25f
    const val COLS = 8
    const val INITIAL_LIVES = 3
    const val PADDLE_Y = 1.15f
    const val PADDLE_HALF_H = 0.017f
    const val DEFAULT_PADDLE_W = 0.24f
    const val WIDE_PADDLE_W = 0.44f
    const val BALL_RADIUS = 0.018f
    const val WIDE_DURATION_MS = 10_000L
    const val FIREBALL_DURATION_MS = 6_000L
    const val LASER_DURATION_MS = 5_000L
    const val REVIVE_WINDOW_MS = 3_000L
    const val MAX_BALLS_ON_SCREEN = 6

    private const val MARGIN_X = 0.045f
    private const val TOP_START_Y = 0.09f
    private const val BRICK_GAP = 0.012f
    private const val BRICK_H = 0.048f

    fun waveSpeedFactor(wave: Int): Float =
        (1.0f + (wave - 1).coerceAtLeast(0) * 0.06f).coerceAtMost(1.55f)

    fun launchVelocity(wave: Int, angleSign: Float = 1f): Pair<Float, Float> {
        val speed = 0.86f * waveSpeedFactor(wave)
        val vx = speed * 0.42f * angleSign
        val vy = -sqrt((speed * speed - vx * vx).coerceAtLeast(0.16f))
        return vx to vy
    }

    fun generateWave(
        wave: Int,
        startId: Long = 100L,
        random: Random = Random.Default
    ): Pair<List<BreakerBrick>, Long> {
        val rows = when {
            wave <= 2 -> 5
            wave <= 5 -> 6
            else -> 7
        }
        val usableW = BOARD_W - 2f * MARGIN_X
        val cellW = (usableW - (COLS - 1) * BRICK_GAP) / COLS.toFloat()
        val titaniumMaxHp = if (wave >= 4) 3 else 2
        val formation = (wave - 1) % 5

        val bricks = mutableListOf<BreakerBrick>()
        var nextId = startId

        for (r in 0 until rows) {
            for (c in 0 until COLS) {
                val include = when (formation) {
                    0 -> r == 0 || r + c >= 2 && (COLS - 1 - c) + r >= 2
                    1 -> abs(c - 3.5f) + abs(r - (rows - 1) * 0.5f) <= 4.1f
                    2 -> c <= 2 || c >= 5 || r == 1 || r == rows - 1
                    3 -> (r + c) % 2 == 0 || r == 0 || r == rows - 1
                    else -> r != rows - 1 || c % 2 == 0
                }
                if (!include) continue

                val isCenter = (r == rows / 2) && (c == 3 || c == 4)
                val isCornerCore = (r == 2 && (c == 1 || c == COLS - 2))
                val type = when {
                    isCenter || (wave >= 2 && isCornerCore && random.nextFloat() < 0.65f) ->
                        BreakerBrickType.PLASMA_CORE
                    (r == 0 && (c == 2 || c == 5)) || random.nextFloat() < 0.14f ->
                        BreakerBrickType.GOLD_BONUS
                    r == 0 || (r + wave) % 3 == 0 || random.nextFloat() < (0.20f + wave * 0.02f).coerceAtMost(0.45f) ->
                        BreakerBrickType.TITANIUM_SHIELD
                    else -> BreakerBrickType.CYAN_GLASS
                }
                val hp = if (type == BreakerBrickType.TITANIUM_SHIELD) titaniumMaxHp else 1

                val left = MARGIN_X + c * (cellW + BRICK_GAP)
                val top = TOP_START_Y + r * (BRICK_H + BRICK_GAP)
                bricks.add(
                    BreakerBrick(
                        id = nextId++,
                        row = r,
                        col = c,
                        type = type,
                        hp = hp,
                        maxHp = hp,
                        left = left,
                        top = top,
                        right = left + cellW,
                        bottom = top + BRICK_H
                    )
                )
            }
        }

        // Guarantee variety: ensure at least 1 GOLD_BONUS and 1 PLASMA_CORE in every wave
        if (bricks.isNotEmpty() && bricks.none { it.type == BreakerBrickType.PLASMA_CORE }) {
            val midIdx = bricks.size / 2
            bricks[midIdx] = bricks[midIdx].copy(type = BreakerBrickType.PLASMA_CORE, hp = 1, maxHp = 1)
        }
        if (bricks.size > 1 && bricks.none { it.type == BreakerBrickType.GOLD_BONUS }) {
            val idx = if (bricks.first().type != BreakerBrickType.PLASMA_CORE) 0 else 1
            bricks[idx] = bricks[idx].copy(type = BreakerBrickType.GOLD_BONUS, hp = 1, maxHp = 1)
        }

        return bricks to nextId
    }

    fun newGame(random: Random = Random.Default): BreakerGameState {
        val (bricks, nextId) = generateWave(wave = 1, startId = 100L, random = random)
        val (vx, vy) = launchVelocity(wave = 1, angleSign = if (random.nextBoolean()) 1f else -1f)
        val ball = BreakerBall(
            id = nextId,
            x = BOARD_W * 0.5f,
            y = PADDLE_Y - PADDLE_HALF_H - BALL_RADIUS - 0.004f,
            vx = vx,
            vy = vy
        )
        return BreakerGameState(
            wave = 1,
            bricks = bricks,
            balls = listOf(ball),
            nextEntityId = nextId + 1L
        )
    }

    fun movePaddle(state: BreakerGameState, targetNormalizedX: Float): BreakerGameState {
        if (state.isGameOver) return state
        val halfW = state.paddleWidth * 0.5f
        val clamped = targetNormalizedX.coerceIn(halfW + 0.01f, BOARD_W - halfW - 0.01f)
        return state.copy(paddleX = clamped)
    }

    fun activateLaserCannon(state: BreakerGameState): BreakerGameState {
        if (state.isGameOver || state.isRevivePromptActive || state.laserPowerUpRemaining <= 0 || state.isLaserCannonActive) {
            return state
        }
        return state.copy(
            laserCannonRemainingMs = LASER_DURATION_MS,
            laserCooldownMs = 0L,
            laserPowerUpRemaining = state.laserPowerUpRemaining - 1
        )
    }

    fun activateSecondChanceRevive(
        state: BreakerGameState,
        random: Random = Random.Default
    ): BreakerGameState {
        if (!state.isRevivePromptActive || state.secondChanceRemaining <= 0) return state
        val (vx, vy) = launchVelocity(state.wave, angleSign = if (random.nextBoolean()) 1f else -1f)
        val id = state.nextEntityId
        val ball = BreakerBall(
            id = id,
            x = state.paddleX,
            y = PADDLE_Y - PADDLE_HALF_H - BALL_RADIUS - 0.005f,
            vx = vx,
            vy = vy
        )
        return state.copy(
            lives = 1,
            balls = listOf(ball),
            revivePromptRemainingMs = 0L,
            secondChanceRemaining = state.secondChanceRemaining - 1,
            comboHits = 0,
            nextEntityId = id + 1L
        )
    }

    fun dismissReviveToGameOver(state: BreakerGameState): BreakerGameState {
        if (!state.isRevivePromptActive) return state
        return state.copy(revivePromptRemainingMs = 0L, isGameOver = true)
    }

    fun applyCapsuleEffect(
        state: BreakerGameState,
        capsuleType: BreakerCapsuleType
    ): BreakerGameState {
        var nextId = state.nextEntityId
        return when (capsuleType) {
            BreakerCapsuleType.MULTI_BALL -> {
                val baseBall = state.balls.firstOrNull()
                if (baseBall == null) {
                    val (vx, vy) = launchVelocity(state.wave)
                    val fresh = BreakerBall(nextId++, state.paddleX, PADDLE_Y - 0.05f, vx, vy)
                    state.copy(balls = listOf(fresh), nextEntityId = nextId)
                } else {
                    val speed = sqrt(baseBall.vx * baseBall.vx + baseBall.vy * baseBall.vy).coerceAtLeast(0.7f)
                    val baseAngle = kotlin.math.atan2(baseBall.vy.toDouble(), baseBall.vx.toDouble())
                    val spawned = mutableListOf<BreakerBall>().apply { addAll(state.balls) }
                    for (offsetDeg in listOf(-24.0, 24.0)) {
                        if (spawned.size >= MAX_BALLS_ON_SCREEN) break
                        val rad = baseAngle + Math.toRadians(offsetDeg)
                        var nvx = (cos(rad) * speed).toFloat()
                        var nvy = (sin(rad) * speed).toFloat()
                        if (abs(nvy) < speed * 0.30f) {
                            nvy = if (nvy <= 0f) -speed * 0.45f else speed * 0.45f
                            nvx = (if (nvx >= 0f) 1f else -1f) * sqrt((speed * speed - nvy * nvy).coerceAtLeast(0.05f))
                        }
                        spawned.add(
                            BreakerBall(
                                id = nextId++,
                                x = baseBall.x,
                                y = baseBall.y,
                                vx = nvx,
                                vy = nvy
                            )
                        )
                    }
                    state.copy(balls = spawned, nextEntityId = nextId)
                }
            }
            BreakerCapsuleType.WIDE_PADDLE -> {
                val halfWide = WIDE_PADDLE_W * 0.5f
                state.copy(
                    widePaddleRemainingMs = WIDE_DURATION_MS,
                    paddleX = state.paddleX.coerceIn(halfWide + 0.01f, BOARD_W - halfWide - 0.01f)
                )
            }
            BreakerCapsuleType.HYPER_FIREBALL -> {
                state.copy(fireballRemainingMs = FIREBALL_DURATION_MS)
            }
        }
    }

    fun step(
        state: BreakerGameState,
        dtSec: Float,
        random: Random = Random.Default
    ): BreakerStepResult {
        if (state.isGameOver || dtSec <= 0f) return BreakerStepResult(state)
        val dtMs = (dtSec * 1000f).roundToInt().toLong().coerceAtLeast(1L)

        // Handle 3-second Second Chance Revive countdown cleanly
        if (state.isRevivePromptActive) {
            val nextRevive = (state.revivePromptRemainingMs - dtMs).coerceAtLeast(0L)
            return BreakerStepResult(
                nextState = state.copy(
                    revivePromptRemainingMs = nextRevive,
                    isGameOver = nextRevive <= 0L
                )
            )
        }

        val wideLeft = (state.widePaddleRemainingMs - dtMs).coerceAtLeast(0L)
        val fireLeft = (state.fireballRemainingMs - dtMs).coerceAtLeast(0L)
        val laserLeft = (state.laserCannonRemainingMs - dtMs).coerceAtLeast(0L)
        val isFireActive = fireLeft > 0L

        var nextId = state.nextEntityId
        val paddleHalfW = (if (wideLeft > 0L) WIDE_PADDLE_W else DEFAULT_PADDLE_W) * 0.5f
        val paddleX = state.paddleX.coerceIn(paddleHalfW + 0.01f, BOARD_W - paddleHalfW - 0.01f)

        val brickMap = state.bricks.associateBy { it.id }.toMutableMap()
        val capsules = state.capsules.toMutableList()
        val lasers = state.laserShots.toMutableList()
        val brokenEvents = mutableListOf<BreakerShardEvent>()
        val explosionEvents = mutableListOf<BreakerShockwaveEvent>()
        val wallSparks = mutableListOf<Pair<Float, Float>>()
        val caughtCapsules = mutableListOf<BreakerCapsuleType>()

        var scoreGained = 0L
        var xpGained = 0L
        var combo = state.comboHits
        var bricksDestroyedDelta = 0
        var hitPaddle = false
        var firedLasers = false

        fun destroyBrickAndChain(
            rootId: Long,
            instantKill: Boolean
        ) {
            val queue = ArrayDeque<Pair<Long, Boolean>>()
            queue.addLast(rootId to instantKill)

            while (queue.isNotEmpty()) {
                val (targetId, forceKill) = queue.removeFirst()
                val brick = brickMap[targetId] ?: continue
                val nextHp = if (forceKill || brick.type != BreakerBrickType.TITANIUM_SHIELD) 0 else (brick.hp - 1)

                if (nextHp > 0) {
                    brickMap[targetId] = brick.copy(hp = nextHp)
                    combo++
                    scoreGained += 15L
                    xpGained += 1L
                } else {
                    brickMap.remove(targetId)
                    combo++
                    bricksDestroyedDelta++
                    brokenEvents.add(BreakerShardEvent(brick.centerX, brick.centerY, brick.type))

                    val basePts = when (brick.type) {
                        BreakerBrickType.CYAN_GLASS -> 25L
                        BreakerBrickType.TITANIUM_SHIELD -> 40L
                        BreakerBrickType.GOLD_BONUS -> 50L // 2x Cyan points + capsule!
                        BreakerBrickType.PLASMA_CORE -> 60L
                    }
                    scoreGained += basePts
                    xpGained += when (brick.type) {
                        BreakerBrickType.GOLD_BONUS -> 5L
                        BreakerBrickType.PLASMA_CORE -> 6L
                        BreakerBrickType.TITANIUM_SHIELD -> 3L
                        BreakerBrickType.CYAN_GLASS -> 2L
                    }

                    if (brick.type == BreakerBrickType.GOLD_BONUS) {
                        val capType = BreakerCapsuleType.entries[random.nextInt(BreakerCapsuleType.entries.size)]
                        capsules.add(
                            BreakerCapsule(
                                id = nextId++,
                                type = capType,
                                x = brick.centerX,
                                y = brick.centerY
                            )
                        )
                    }

                    if (brick.type == BreakerBrickType.PLASMA_CORE) {
                        explosionEvents.add(BreakerShockwaveEvent(brick.centerX, brick.centerY))
                        // Chain-reaction shockwave destroys all 8 surrounding grid neighbors
                        val neighbors = brickMap.values.filter { other ->
                            abs(other.row - brick.row) <= 1 && abs(other.col - brick.col) <= 1
                        }.map { it.id }
                        neighbors.forEach { nId -> queue.addLast(nId to true) }
                    }
                }
            }
        }

        // 1. Fire & advance Laser Cannon bolts
        var nextLaserCd = state.laserCooldownMs - dtMs
        if (laserLeft > 0L && nextLaserCd <= 0L) {
            nextLaserCd = 230L
            firedLasers = true
            val muzzleOffset = paddleHalfW * 0.72f
            lasers.add(BreakerLaserShot(nextId++, paddleX - muzzleOffset, PADDLE_Y - PADDLE_HALF_H - 0.01f))
            lasers.add(BreakerLaserShot(nextId++, paddleX + muzzleOffset, PADDLE_Y - PADDLE_HALF_H - 0.01f))
        }

        val survivingLasers = mutableListOf<BreakerLaserShot>()
        for (shot in lasers) {
            val ny = shot.y + shot.vy * dtSec
            if (ny < 0f) continue
            val hitBrick = brickMap.values.firstOrNull { b ->
                shot.x in b.left..b.right && ny <= b.bottom && shot.y >= b.top - 0.02f
            }
            if (hitBrick != null) {
                destroyBrickAndChain(hitBrick.id, instantKill = false)
            } else {
                survivingLasers.add(shot.copy(y = ny))
            }
        }

        // 2. Move & sub-step all active plasma balls
        val survivingBalls = mutableListOf<BreakerBall>()
        for (ball in state.balls) {
            val speed = sqrt(ball.vx * ball.vx + ball.vy * ball.vy).coerceAtLeast(0.45f)
            val stepCount = kotlin.math.ceil((speed * dtSec) / (BALL_RADIUS * 0.45f)).toInt().coerceIn(1, 8)
            val subDt = dtSec / stepCount.toFloat()

            var bx = ball.x
            var by = ball.y
            var bvx = ball.vx
            var bvy = ball.vy
            var lost = false

            for (stepIdx in 0 until stepCount) {
                bx += bvx * subDt
                by += bvy * subDt

                // Left & Right Walls
                if (bx - BALL_RADIUS < 0f) {
                    bx = BALL_RADIUS
                    bvx = abs(bvx)
                    wallSparks.add(0.005f to by)
                } else if (bx + BALL_RADIUS > BOARD_W) {
                    bx = BOARD_W - BALL_RADIUS
                    bvx = -abs(bvx)
                    wallSparks.add((BOARD_W - 0.005f) to by)
                }

                // Ceiling
                if (by - BALL_RADIUS < 0f) {
                    by = BALL_RADIUS
                    bvy = abs(bvy)
                    wallSparks.add(bx to 0.005f)
                }

                // Bottom Out
                if (by - BALL_RADIUS > BOARD_H) {
                    lost = true
                    break
                }

                // Paddle Collision (moving downward)
                val pTop = PADDLE_Y - PADDLE_HALF_H
                val pBottom = PADDLE_Y + PADDLE_HALF_H
                if (bvy > 0f &&
                    by + BALL_RADIUS >= pTop &&
                    by - BALL_RADIUS <= pBottom &&
                    bx in (paddleX - paddleHalfW - BALL_RADIUS)..(paddleX + paddleHalfW + BALL_RADIUS)
                ) {
                    by = pTop - BALL_RADIUS - 0.001f
                    val rel = ((bx - paddleX) / paddleHalfW).coerceIn(-1f, 1f)
                    val maxAngle = Math.toRadians(62.0)
                    val bounceAngle = rel * maxAngle
                    val nextSpeed = speed.coerceAtLeast(0.82f * waveSpeedFactor(state.wave))
                    bvx = (sin(bounceAngle) * nextSpeed).toFloat()
                    bvy = -(cos(bounceAngle) * nextSpeed).toFloat().coerceAtLeast(0.28f)
                    combo = 0
                    hitPaddle = true
                }

                // Brick Collision
                val hitCandidates = brickMap.values.filter { b ->
                    val closestX = bx.coerceIn(b.left, b.right)
                    val closestY = by.coerceIn(b.top, b.bottom)
                    val dx = bx - closestX
                    val dy = by - closestY
                    dx * dx + dy * dy <= BALL_RADIUS * BALL_RADIUS
                }
                if (hitCandidates.isNotEmpty()) {
                    val primary = hitCandidates.first()
                    if (!isFireActive) {
                        val overlapLeft = abs((bx + BALL_RADIUS) - primary.left)
                        val overlapRight = abs(primary.right - (bx - BALL_RADIUS))
                        val overlapTop = abs((by + BALL_RADIUS) - primary.top)
                        val overlapBottom = abs(primary.bottom - (by - BALL_RADIUS))
                        val minH = min(overlapLeft, overlapRight)
                        val minV = min(overlapTop, overlapBottom)
                        if (minH < minV) {
                            bvx = if (bx < primary.centerX) -abs(bvx) else abs(bvx)
                        } else {
                            bvy = if (by < primary.centerY) -abs(bvy) else abs(bvy)
                        }
                    }
                    // Hyper Fireball pierces & shatters immediately without bouncing
                    for (b in hitCandidates) {
                        if (brickMap.containsKey(b.id)) {
                            destroyBrickAndChain(b.id, instantKill = isFireActive)
                        }
                    }
                }
            }

            if (!lost) {
                val updatedTrail = (ball.trail + (bx to by)).takeLast(12)
                survivingBalls.add(
                    ball.copy(x = bx, y = by, vx = bvx, vy = bvy, trail = updatedTrail)
                )
            }
        }

        // 3. Move & catch Falling Capsules
        val remainingCapsules = mutableListOf<BreakerCapsule>()
        var tempState = state.copy(
            score = state.score + scoreGained,
            bricks = brickMap.values.toList(),
            balls = survivingBalls,
            laserShots = survivingLasers,
            paddleX = paddleX,
            comboHits = combo,
            widePaddleRemainingMs = wideLeft,
            fireballRemainingMs = fireLeft,
            laserCannonRemainingMs = laserLeft,
            laserCooldownMs = nextLaserCd,
            totalBricksDestroyed = state.totalBricksDestroyed + bricksDestroyedDelta,
            nextEntityId = nextId
        )

        for (cap in capsules) {
            val ny = cap.y + cap.vy * dtSec
            val inPaddleY = ny >= PADDLE_Y - PADDLE_HALF_H - 0.025f && ny <= PADDLE_Y + PADDLE_HALF_H + 0.025f
            val inPaddleX = abs(cap.x - tempState.paddleX) <= (tempState.paddleWidth * 0.5f + 0.025f)
            if (inPaddleY && inPaddleX) {
                caughtCapsules.add(cap.type)
                tempState = applyCapsuleEffect(tempState, cap.type)
                scoreGained += 75L
                xpGained += 8L
                tempState = tempState.copy(score = tempState.score + 75L)
            } else if (ny <= BOARD_H + 0.04f) {
                remainingCapsules.add(cap.copy(y = ny))
            }
        }
        tempState = tempState.copy(capsules = remainingCapsules)

        // 4. Wave Clear Check -> Spawn Next Procedural Wave!
        if (tempState.bricks.isEmpty()) {
            val nextWave = tempState.wave + 1
            val waveBonus = 200L * tempState.wave
            val (nextBricks, nid1) = generateWave(nextWave, startId = tempState.nextEntityId, random = random)
            val (nvx, nvy) = launchVelocity(nextWave, angleSign = if (random.nextBoolean()) 1f else -1f)
            val freshBall = BreakerBall(
                id = nid1,
                x = tempState.paddleX,
                y = PADDLE_Y - PADDLE_HALF_H - BALL_RADIUS - 0.005f,
                vx = nvx,
                vy = nvy
            )
            return BreakerStepResult(
                nextState = tempState.copy(
                    wave = nextWave,
                    score = tempState.score + waveBonus,
                    bricks = nextBricks,
                    balls = listOf(freshBall),
                    capsules = emptyList(),
                    laserShots = emptyList(),
                    nextEntityId = nid1 + 1L
                ),
                pointsGained = scoreGained + waveBonus,
                xpGained = xpGained + 25L,
                brokenBricks = brokenEvents,
                explosions = explosionEvents,
                wallSparks = wallSparks,
                caughtCapsules = caughtCapsules,
                hitPaddle = hitPaddle,
                firedLasers = firedLasers,
                waveCleared = true
            )
        }

        // 5. All Balls Lost -> Deduct Life, Launch Fresh Ball, or Trigger 3s Second Chance Revive Window
        var lostLife = false
        if (tempState.balls.isEmpty()) {
            lostLife = true
            val remainingLives = (tempState.lives - 1).coerceAtLeast(0)
            if (remainingLives > 0) {
                val nid = tempState.nextEntityId
                val (nvx, nvy) = launchVelocity(tempState.wave, angleSign = if (random.nextBoolean()) 1f else -1f)
                val respawnBall = BreakerBall(
                    id = nid,
                    x = tempState.paddleX,
                    y = PADDLE_Y - PADDLE_HALF_H - BALL_RADIUS - 0.005f,
                    vx = nvx,
                    vy = nvy
                )
                tempState = tempState.copy(
                    lives = remainingLives,
                    balls = listOf(respawnBall),
                    comboHits = 0,
                    nextEntityId = nid + 1L
                )
            } else if (tempState.secondChanceRemaining > 0) {
                tempState = tempState.copy(
                    lives = 0,
                    comboHits = 0,
                    revivePromptRemainingMs = REVIVE_WINDOW_MS
                )
            } else {
                tempState = tempState.copy(
                    lives = 0,
                    comboHits = 0,
                    isGameOver = true
                )
            }
        }

        return BreakerStepResult(
            nextState = tempState,
            pointsGained = scoreGained,
            xpGained = xpGained,
            brokenBricks = brokenEvents,
            explosions = explosionEvents,
            wallSparks = wallSparks,
            caughtCapsules = caughtCapsules,
            hitPaddle = hitPaddle,
            firedLasers = firedLasers,
            waveCleared = false,
            lostLife = lostLife
        )
    }
}

private data class BreakerParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val radiusPx: Float,
    val color: Color
)

private data class BreakerShockwaveVisual(
    val x: Float,
    val y: Float,
    var progress: Float = 0f
)

@Composable
private fun BreakerHeroBannerCard(
    spec: ArenaGameSpec,
    snapshot: ArenaSnapshot,
    onUnlockFlashShown: () -> Unit,
    onPlayClick: () -> Unit
) {
    val isUnlocked = snapshot.isGame7Unlocked
    val progressDays = snapshot.recordedNoSpendDays.coerceIn(0, ArenaProgressStore.GAME7_REQUIRED_NO_SPEND_DAYS)
    val formattedBest = remember(snapshot.bestBrickBreakerScore) {
        NumberFormat.getNumberInstance(Locale.US).format(snapshot.bestBrickBreakerScore)
    }

    var unlockBannerVisible by remember(snapshot.shouldShowGame7UnlockFlash) {
        mutableStateOf(snapshot.shouldShowGame7UnlockFlash)
    }
    val unlockFlashAlpha = remember { Animatable(0f) }

    LaunchedEffect(snapshot.shouldShowGame7UnlockFlash) {
        if (snapshot.shouldShowGame7UnlockFlash) {
            unlockBannerVisible = true
            unlockFlashAlpha.snapTo(0.95f)
            unlockFlashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing)
            )
            delay(1400L)
            unlockBannerVisible = false
            onUnlockFlashShown()
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B0C10),
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.linearGradient(
                colors = if (isUnlocked) {
                    listOf(ArenaCyan.copy(alpha = 0.90f), ArenaGold.copy(alpha = 0.85f), ArenaCyan.copy(alpha = 0.72f))
                } else {
                    listOf(ArenaCyan.copy(alpha = 0.55f), ArenaGold.copy(alpha = 0.45f))
                }
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_7")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF091622),
                            ObsidianBg,
                            Color(0xFF171209)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.height * 0.5f, size.height * 0.5f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.5f, size.height * 0.5f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.25f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BreakerLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("breaker_logo_canvas")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_breaker_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Brick Breaker Stats",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: $formattedBest • W${snapshot.bestBrickBreakerWave}",
                                color = ArenaGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        enabled = isUnlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaCyan,
                            contentColor = ObsidianBg,
                            disabledContainerColor = Color(0xFF1F2937),
                            disabledContentColor = DarkTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_breaker_play_button")
                    ) {
                        Text(
                            text = if (isUnlocked) "PLAY ▶" else "LOCKED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Frosted Glass 3 No-Spend Days Lock Overlay when locked
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xDD0B0C10),
                                    Color(0xEB0E1A26),
                                    Color(0xDD0B0C10)
                                )
                            )
                        )
                        .border(1.2.dp, ArenaCyan.copy(alpha = 0.50f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                        .testTag("breaker_nospend_lock_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.78f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked by 3 No-Spend Days",
                                    tint = ArenaGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "🔒 Unlock with 3 No-Spend Days • $progressDays/3 Days",
                                    color = ArenaGold,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progressDays / 3f },
                            color = ArenaCyan,
                            trackColor = Color(0xFF1F2937),
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )

                        Text(
                            text = "Log zero expenses on 3 calendar days to unlock Neon Brick Breaker forever!",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (unlockFlashAlpha.value > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(ArenaGold.copy(alpha = unlockFlashAlpha.value * 0.50f))
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = unlockBannerVisible && isUnlocked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f))
                ) {
                    Text(
                        text = "✨ 3 NO-SPEND DAYS UNLOCKED!",
                        color = ObsidianBg,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakerLogoArtwork(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breakerLogoAnim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plasmaOrbPulse"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Neon Brick Breaker titanium paddle, cyan plasma orb, and shattering crystal bricks emblem"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0A1422), Color(0xFF141118))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaCyan.copy(alpha = 0.88f), ArenaGold.copy(alpha = 0.85f))),
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val w = size.width
        val h = size.height

        // Crystal & 24K Gold Bricks Row near Top
        val brickW = w * 0.22f
        val brickH = h * 0.12f
        val topColors = listOf(ArenaCyan, ArenaGold, RunnerTrapRed)
        for (i in 0 until 3) {
            val bx = w * (0.12f + i * 0.27f)
            val by = h * 0.17f
            drawRoundRect(
                color = topColors[i].copy(alpha = 0.88f),
                topLeft = Offset(bx, by),
                size = Size(brickW, brickH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.65f),
                topLeft = Offset(bx, by),
                size = Size(brickW, brickH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Shattering Crystal Shards below center brick
        val shardOffsets = listOf(
            Offset(w * 0.44f, h * 0.35f),
            Offset(w * 0.57f, h * 0.34f),
            Offset(w * 0.62f, h * 0.42f),
            Offset(w * 0.39f, h * 0.44f)
        )
        shardOffsets.forEachIndexed { idx, off ->
            drawCircle(
                color = if (idx % 2 == 0) ArenaCyan else ArenaGold,
                radius = 2.6.dp.toPx() * pulse,
                center = off
            )
        }

        // Luminous Plasma Orb Comet Trail + Core
        val orbCenter = Offset(w * 0.56f, h * 0.52f)
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, ArenaCyan.copy(alpha = 0.85f)),
                start = Offset(w * 0.30f, h * 0.72f),
                end = orbCenter
            ),
            start = Offset(w * 0.30f, h * 0.72f),
            end = orbCenter,
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = ArenaCyan.copy(alpha = 0.35f * pulse),
            radius = 12.dp.toPx(),
            center = orbCenter
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(Color.White, ArenaCyan), center = orbCenter, radius = 7.dp.toPx()),
            radius = 7.dp.toPx(),
            center = orbCenter
        )

        // Glowing Brushed-Titanium & Cyan Paddle at Bottom
        val padW = w * 0.58f
        val padH = 9.dp.toPx()
        val padTl = Offset((w - padW) * 0.5f, h * 0.78f)
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(ArenaCyan, Color(0xFF94A3B8), ArenaCyan)),
            topLeft = padTl,
            size = Size(padW, padH),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.85f),
            topLeft = padTl,
            size = Size(padW, padH),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
fun NeonBrickBreakerGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var gameState by remember { mutableStateOf(NeonBreakerEngine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var powerUpHookMessage by remember { mutableStateOf<String?>(null) }
    var bestUpdatedOnFinish by remember { mutableStateOf(false) }
    var roundTotalXp by remember { mutableLongStateOf(0L) }
    var secondChanceUsedThisRun by remember { mutableStateOf(false) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }
    var canvasPixelSize by remember { mutableStateOf(Size.Zero) }

    val particles = remember { mutableStateListOf<BreakerParticle>() }
    val shockwaves = remember { mutableStateListOf<BreakerShockwaveVisual>() }

    LaunchedEffect(powerUpHookMessage) {
        if (powerUpHookMessage != null) {
            delay(2100L)
            powerUpHookMessage = null
        }
    }

    // Main 120fps Physics, Particle & Shockwave Frame Loop
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) return@LaunchedEffect
        var lastNs = 0L
        while (!gameState.isGameOver) {
            withFrameNanos { frameNs ->
                if (lastNs == 0L) {
                    lastNs = frameNs
                    return@withFrameNanos
                }
                val dtSec = ((frameNs - lastNs).coerceAtMost(35_000_000L)) / 1_000_000_000f
                lastNs = frameNs

                val stepRes = NeonBreakerEngine.step(gameState, dtSec)
                gameState = stepRes.nextState

                val cw = canvasPixelSize.width
                val ch = canvasPixelSize.height

                if (stepRes.hitPaddle && feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    ArenaSynthAudio.playBreakerPaddleBounce()
                }
                if (stepRes.firedLasers && feedbackEnabled) {
                    ArenaSynthAudio.playBreakerLaserShot()
                }
                if (stepRes.brokenBricks.isNotEmpty()) {
                    if (feedbackEnabled) {
                        ArenaSynthAudio.playBreakerBrickHit(stepRes.nextState.comboHits)
                    }
                    if (cw > 1f && ch > 1f) {
                        stepRes.brokenBricks.forEach { ev ->
                            val sx = (ev.cx / NeonBreakerEngine.BOARD_W) * cw
                            val sy = (ev.cy / NeonBreakerEngine.BOARD_H) * ch
                            val shardColor = when (ev.brickType) {
                                BreakerBrickType.CYAN_GLASS -> ArenaCyan
                                BreakerBrickType.TITANIUM_SHIELD -> Color(0xFFCBD5E1)
                                BreakerBrickType.GOLD_BONUS -> ArenaGold
                                BreakerBrickType.PLASMA_CORE -> RunnerTrapRed
                            }
                            repeat(8) { idx ->
                                val angle = (idx / 8.0) * 2.0 * PI + Random.nextFloat() * 0.45f
                                val spd = 110f + Random.nextFloat() * 210f
                                particles.add(
                                    BreakerParticle(
                                        x = sx,
                                        y = sy,
                                        vx = (cos(angle) * spd).toFloat(),
                                        vy = (sin(angle) * spd).toFloat(),
                                        life = 0f,
                                        maxLife = 0.48f,
                                        radiusPx = 3.5f + Random.nextFloat() * 3.5f,
                                        color = shardColor
                                    )
                                )
                            }
                        }
                    }
                }

                if (stepRes.explosions.isNotEmpty()) {
                    if (feedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        ArenaSynthAudio.playBreakerPlasmaExplosion()
                    }
                    if (cw > 1f && ch > 1f) {
                        stepRes.explosions.forEach { ex ->
                            shockwaves.add(
                                BreakerShockwaveVisual(
                                    x = (ex.cx / NeonBreakerEngine.BOARD_W) * cw,
                                    y = (ex.cy / NeonBreakerEngine.BOARD_H) * ch
                                )
                            )
                        }
                    }
                }

                if (stepRes.wallSparks.isNotEmpty() && cw > 1f && ch > 1f) {
                    stepRes.wallSparks.forEach { (wx, wy) ->
                        val sx = (wx / NeonBreakerEngine.BOARD_W) * cw
                        val sy = (wy / NeonBreakerEngine.BOARD_H) * ch
                        repeat(4) {
                            val angle = Random.nextFloat() * 2f * PI.toFloat()
                            val spd = 70f + Random.nextFloat() * 110f
                            particles.add(
                                BreakerParticle(
                                    x = sx,
                                    y = sy,
                                    vx = cos(angle) * spd,
                                    vy = sin(angle) * spd,
                                    life = 0f,
                                    maxLife = 0.28f,
                                    radiusPx = 2.4f,
                                    color = if (stepRes.nextState.isFireballActive) ArenaGold else ArenaCyan
                                )
                            )
                        }
                    }
                }

                if (stepRes.caughtCapsules.isNotEmpty() && feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    ArenaSynthAudio.playBreakerCapsuleCatch()
                }

                if (stepRes.waveCleared && feedbackEnabled) {
                    ArenaSynthAudio.playGoldMilestoneChime()
                }

                if (stepRes.xpGained > 0L || stepRes.waveCleared) {
                    roundTotalXp += stepRes.xpGained
                    var snap = ArenaProgressStore.updateBestBrickBreakerStatsIfHigher(
                        context,
                        stepRes.nextState.score,
                        stepRes.nextState.wave
                    )
                    if (stepRes.xpGained > 0L) {
                        snap = ArenaProgressStore.awardXpAndTouchStreak(context, stepRes.xpGained)
                    }
                    onSnapshotUpdated(snap)
                }

                // Advance active particles & explosion shockwaves
                val pIt = particles.listIterator()
                while (pIt.hasNext()) {
                    val p = pIt.next()
                    p.life += dtSec
                    if (p.life >= p.maxLife) {
                        pIt.remove()
                    } else {
                        p.x += p.vx * dtSec
                        p.y += p.vy * dtSec
                        p.vy += 260f * dtSec
                    }
                }

                val sIt = shockwaves.listIterator()
                while (sIt.hasNext()) {
                    val sw = sIt.next()
                    sw.progress += dtSec * 2.4f
                    if (sw.progress >= 1f) {
                        sIt.remove()
                    }
                }
            }
        }
    }

    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver && !bestUpdatedOnFinish) {
            bestUpdatedOnFinish = true
            onSnapshotUpdated(
                ArenaProgressStore.updateBestBrickBreakerStatsIfHigher(context, gameState.score, gameState.wave)
            )
        }
    }

    val effectiveBest = max(snapshot.bestBrickBreakerScore, gameState.score)
    val formattedScore = remember(gameState.score) {
        NumberFormat.getNumberInstance(Locale.US).format(gameState.score)
    }
    val formattedBest = remember(effectiveBest) {
        NumberFormat.getNumberInstance(Locale.US).format(effectiveBest)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .navigationBarsPadding()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Top Navigation & Sound Mute/Unmute Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        onSnapshotUpdated(
                            ArenaProgressStore.updateBestBrickBreakerStatsIfHigher(context, gameState.score, gameState.wave)
                        )
                        onExitToHub()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("game_breaker_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Neon Brick Breaker",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = (if (gameState.isFireballActive) ArenaGold else ArenaCyan).copy(alpha = 0.14f),
                        border = BorderStroke(
                            0.75.dp,
                            (if (gameState.isFireballActive) ArenaGold else ArenaCyan).copy(alpha = 0.52f)
                        )
                    ) {
                        val badgeText = when {
                            gameState.isFireballActive -> "🔥 HYPER FIREBALL ${kotlin.math.ceil(gameState.fireballRemainingMs / 1000.0).toInt()}s"
                            gameState.isLaserCannonActive -> "⚡ LASERS ACTIVE ${kotlin.math.ceil(gameState.laserCannonRemainingMs / 1000.0).toInt()}s"
                            gameState.isWidePaddleActive -> "↔ WIDE PADDLE ${kotlin.math.ceil(gameState.widePaddleRemainingMs / 1000.0).toInt()}s"
                            gameState.balls.size > 1 -> "● ${gameState.balls.size}X PLASMA MULTI-BALL"
                            else -> "WAVE ${gameState.wave} • LVL ${snapshot.levelInfo.level}"
                        }
                        Text(
                            text = badgeText,
                            color = if (gameState.isFireballActive) ArenaGold else ArenaCyan,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    feedbackEnabled = !feedbackEnabled
                    onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObsidianElevated)
                    .border(1.dp, ArenaCyan.copy(alpha = 0.35f), CircleShape)
                    .testTag("game_breaker_sound_toggle")
            ) {
                Icon(
                    imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (feedbackEnabled) "Mute Sound & Haptics" else "Enable Sound & Haptics",
                    tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Clean 4-Pill Telemetry HUD: SCORE | WAVE | LIVES | BEST
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TowerHudPill(
                label = "SCORE",
                value = formattedScore,
                accentColor = ArenaCyan,
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("breaker_score_pill")
            )
            TowerHudPill(
                label = "WAVE",
                value = "${gameState.wave}",
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(0.85f)
                    .testTag("breaker_wave_pill")
            )
            TowerHudPill(
                label = "LIVES",
                value = "${gameState.lives}",
                accentColor = if (gameState.lives <= 1) RunnerTrapRed else ElectricCyan,
                modifier = Modifier
                    .weight(0.85f)
                    .testTag("breaker_lives_pill")
            )
            TowerHudPill(
                label = "BEST",
                value = formattedBest,
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("breaker_best_pill")
            )
        }

        // 3. 120fps Interactive Plasma Brick Breaker Canvas
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            contentAlignment = Alignment.Center
        ) {
            val targetAspect = 0.80f
            val boardW: Dp
            val boardH: Dp
            if (maxWidth / maxHeight > targetAspect) {
                boardH = maxHeight
                boardW = maxHeight * targetAspect
            } else {
                boardW = maxWidth
                boardH = maxWidth / targetAspect
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF060911),
                border = BorderStroke(
                    width = 1.4.dp,
                    color = when {
                        gameState.isFireballActive -> ArenaGold
                        gameState.isLaserCannonActive -> RunnerTrapRed
                        else -> ArenaCyan.copy(alpha = 0.55f)
                    }
                ),
                modifier = Modifier
                    .size(width = boardW, height = boardH)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val latestState by rememberUpdatedState(gameState)
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coords ->
                                canvasPixelSize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                            }
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val wPx = size.width.toFloat().coerceAtLeast(1f)
                                    gameState = NeonBreakerEngine.movePaddle(
                                        latestState,
                                        (down.position.x / wPx) * NeonBreakerEngine.BOARD_W
                                    )
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val activePtr = event.changes.firstOrNull { it.pressed } ?: break
                                        gameState = NeonBreakerEngine.movePaddle(
                                            latestState,
                                            (activePtr.position.x / wPx) * NeonBreakerEngine.BOARD_W
                                        )
                                        activePtr.consume()
                                    }
                                }
                            }
                            .testTag("breaker_play_canvas")
                    ) {
                        val sx = size.width / NeonBreakerEngine.BOARD_W
                        val sy = size.height / NeonBreakerEngine.BOARD_H

                        // Subtle cyber grid backdrop
                        val gridStepX = size.width / 8f
                        for (i in 1 until 8) {
                            drawLine(
                                color = ArenaCyan.copy(alpha = 0.06f),
                                start = Offset(i * gridStepX, 0f),
                                end = Offset(i * gridStepX, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        val gridStepY = size.height / 10f
                        for (j in 1 until 10) {
                            drawLine(
                                color = ArenaCyan.copy(alpha = 0.05f),
                                start = Offset(0f, j * gridStepY),
                                end = Offset(size.width, j * gridStepY),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // 4 Crystal Brick Types
                        gameState.bricks.forEach { brick ->
                            val tl = Offset(brick.left * sx, brick.top * sy)
                            val bSize = Size((brick.right - brick.left) * sx, (brick.bottom - brick.top) * sy)
                            val cr = CornerRadius(5.dp.toPx(), 5.dp.toPx())

                            val (topCol, botCol, rimCol) = when (brick.type) {
                                BreakerBrickType.CYAN_GLASS -> Triple(
                                    ArenaCyan.copy(alpha = 0.82f),
                                    Color(0xFF0284C7).copy(alpha = 0.72f),
                                    Color.White.copy(alpha = 0.70f)
                                )
                                BreakerBrickType.TITANIUM_SHIELD -> Triple(
                                    Color(0xFF94A3B8),
                                    Color(0xFF334155),
                                    if (brick.isCracked) ArenaCyan else Color(0xFFE2E8F0)
                                )
                                BreakerBrickType.GOLD_BONUS -> Triple(
                                    Color(0xFFFEF08A),
                                    ArenaGold,
                                    Color.White.copy(alpha = 0.90f)
                                )
                                BreakerBrickType.PLASMA_CORE -> Triple(
                                    Color(0xFFFB7185),
                                    RunnerTrapRed,
                                    Color(0xFFFFE4E6)
                                )
                            }

                            drawRoundRect(
                                brush = Brush.verticalGradient(listOf(topCol, botCol), startY = tl.y, endY = tl.y + bSize.height),
                                topLeft = tl,
                                size = bSize,
                                cornerRadius = cr
                            )
                            drawRoundRect(
                                color = rimCol,
                                topLeft = tl,
                                size = bSize,
                                cornerRadius = cr,
                                style = Stroke(width = 1.2.dp.toPx())
                            )

                            // Distinct micro-details per brick type
                            if (brick.type == BreakerBrickType.TITANIUM_SHIELD && brick.isCracked) {
                                // Glowing fracture lines on Titanium Hit
                                drawLine(
                                    color = ArenaCyan,
                                    start = Offset(tl.x + bSize.width * 0.20f, tl.y + 2.dp.toPx()),
                                    end = Offset(tl.x + bSize.width * 0.55f, tl.y + bSize.height * 0.58f),
                                    strokeWidth = 1.6.dp.toPx()
                                )
                                drawLine(
                                    color = Color.White,
                                    start = Offset(tl.x + bSize.width * 0.55f, tl.y + bSize.height * 0.58f),
                                    end = Offset(tl.x + bSize.width * 0.78f, tl.y + bSize.height - 2.dp.toPx()),
                                    strokeWidth = 1.4.dp.toPx()
                                )
                            } else if (brick.type == BreakerBrickType.PLASMA_CORE) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.88f),
                                    radius = min(bSize.width, bSize.height) * 0.22f,
                                    center = Offset(tl.x + bSize.width * 0.5f, tl.y + bSize.height * 0.5f)
                                )
                            } else if (brick.type == BreakerBrickType.GOLD_BONUS) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.72f),
                                    start = Offset(tl.x + 4.dp.toPx(), tl.y + 3.dp.toPx()),
                                    end = Offset(tl.x + bSize.width - 4.dp.toPx(), tl.y + 3.dp.toPx()),
                                    strokeWidth = 1.6.dp.toPx()
                                )
                            }
                        }

                        // Falling Power-Up Capsules
                        gameState.capsules.forEach { cap ->
                            val cx = cap.x * sx
                            val cy = cap.y * sy
                            val cw = 26.dp.toPx()
                            val ch = 13.dp.toPx()
                            val capColor = when (cap.type) {
                                BreakerCapsuleType.MULTI_BALL -> ArenaCyan
                                BreakerCapsuleType.WIDE_PADDLE -> ElectricCyan
                                BreakerCapsuleType.HYPER_FIREBALL -> ArenaGold
                            }
                            drawRoundRect(
                                color = capColor.copy(alpha = 0.28f),
                                topLeft = Offset(cx - cw * 0.6f, cy - ch * 0.65f),
                                size = Size(cw * 1.2f, ch * 1.3f),
                                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                            )
                            drawRoundRect(
                                color = capColor,
                                topLeft = Offset(cx - cw * 0.5f, cy - ch * 0.5f),
                                size = Size(cw, ch),
                                cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
                            )
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.88f),
                                topLeft = Offset(cx - cw * 0.5f, cy - ch * 0.5f),
                                size = Size(cw, ch),
                                cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx()),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }

                        // Upward Laser Cannon Shots
                        gameState.laserShots.forEach { shot ->
                            val lx = shot.x * sx
                            val ly = shot.y * sy
                            drawLine(
                                color = RunnerTrapRed.copy(alpha = 0.45f),
                                start = Offset(lx, ly),
                                end = Offset(lx, ly + 14.dp.toPx()),
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(lx, ly),
                                end = Offset(lx, ly + 12.dp.toPx()),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // Glowing Plasma / Fireball Orbs & Comet Trails
                        gameState.balls.forEach { ball ->
                            val isFire = gameState.isFireballActive
                            val glowCol = if (isFire) ArenaGold else ArenaCyan
                            val trailSize = ball.trail.size
                            for (i in 1 until trailSize) {
                                val tPrev = ball.trail[i - 1]
                                val tCurr = ball.trail[i]
                                val ratio = i.toFloat() / trailSize.toFloat()
                                drawLine(
                                    color = glowCol.copy(alpha = ratio * 0.48f),
                                    start = Offset(tPrev.first * sx, tPrev.second * sy),
                                    end = Offset(tCurr.first * sx, tCurr.second * sy),
                                    strokeWidth = (NeonBreakerEngine.BALL_RADIUS * sx * 1.4f) * ratio,
                                    cap = StrokeCap.Round
                                )
                            }

                            val bCenter = Offset(ball.x * sx, ball.y * sy)
                            val bRad = NeonBreakerEngine.BALL_RADIUS * sx
                            drawCircle(
                                color = glowCol.copy(alpha = 0.34f),
                                radius = bRad * 1.75f,
                                center = bCenter
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.White, glowCol, if (isFire) RunnerTrapRed else Color(0xFF0284C7)),
                                    center = bCenter,
                                    radius = bRad
                                ),
                                radius = bRad,
                                center = bCenter
                            )
                        }

                        // Brushed-Titanium & Electric Cyan Paddle + Laser Hardpoints
                        val padW = gameState.paddleWidth * sx
                        val padH = NeonBreakerEngine.PADDLE_HALF_H * 2f * sy
                        val padLeft = gameState.paddleX * sx - padW * 0.5f
                        val padTop = NeonBreakerEngine.PADDLE_Y * sy - padH * 0.5f

                        drawRoundRect(
                            color = (if (gameState.isFireballActive) ArenaGold else ArenaCyan).copy(alpha = 0.25f),
                            topLeft = Offset(padLeft - 3.dp.toPx(), padTop - 3.dp.toPx()),
                            size = Size(padW + 6.dp.toPx(), padH + 6.dp.toPx()),
                            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                        )
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(ArenaCyan, Color(0xFF94A3B8), Color.White, Color(0xFF94A3B8), ArenaCyan)
                            ),
                            topLeft = Offset(padLeft, padTop),
                            size = Size(padW, padH),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.90f),
                            topLeft = Offset(padLeft, padTop),
                            size = Size(padW, padH),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                            style = Stroke(width = 1.2.dp.toPx())
                        )
                        if (gameState.isLaserCannonActive) {
                            val leftMuzzleX = gameState.paddleX * sx - padW * 0.36f
                            val rightMuzzleX = gameState.paddleX * sx + padW * 0.36f
                            for (mx in listOf(leftMuzzleX, rightMuzzleX)) {
                                drawCircle(color = RunnerTrapRed, radius = 4.dp.toPx(), center = Offset(mx, padTop))
                            }
                        }

                        // Plasma Core Shockwaves
                        shockwaves.forEach { sw ->
                            val rad = (12.dp.toPx() + sw.progress * 96.dp.toPx())
                            drawCircle(
                                color = RunnerTrapRed.copy(alpha = (1f - sw.progress).coerceIn(0f, 1f) * 0.85f),
                                radius = rad,
                                center = Offset(sw.x, sw.y),
                                style = Stroke(width = 3.5.dp.toPx() * (1f - sw.progress * 0.6f))
                            )
                        }

                        // Shard & Wall Spark Particles
                        particles.forEach { p ->
                            val alpha = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.radiusPx * (1f - 0.35f * (p.life / p.maxLife)),
                                center = Offset(p.x, p.y)
                            )
                        }
                    }

                    // 3-Second Second Chance Extra Ball Revive Overlay
                    if (gameState.isRevivePromptActive) {
                        val secLeft = kotlin.math.ceil(gameState.revivePromptRemainingMs / 1000.0).toInt().coerceIn(1, 3)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ObsidianBg.copy(alpha = 0.82f))
                                .padding(18.dp)
                                .testTag("breaker_revive_overlay"),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = ObsidianElevated,
                                border = BorderStroke(1.5.dp, ArenaGold),
                                modifier = Modifier.fillMaxWidth(0.90f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "SECOND CHANCE? ${secLeft}s",
                                        color = ArenaGold,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Claim 1 Free Extra Plasma Ball to keep your Wave ${gameState.wave} run alive!",
                                        color = DarkTextSecondary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = {
                                            gameState = NeonBreakerEngine.activateSecondChanceRevive(gameState)
                                            if (feedbackEnabled) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                ArenaSynthAudio.playPowerUpActivate()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ArenaGold,
                                            contentColor = ObsidianBg
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("breaker_revive_claim_button")
                                    ) {
                                        Text(
                                            text = "CLAIM EXTRA BALL (1 FREE)",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            gameState = NeonBreakerEngine.dismissReviveToGameOver(gameState)
                                        },
                                        border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.45f)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkTextSecondary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Text("End Run", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Game Over Overlay
                    if (gameState.isGameOver) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ObsidianBg.copy(alpha = 0.88f))
                                .padding(18.dp)
                                .testTag("breaker_gameover_overlay"),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = ObsidianElevated,
                                border = BorderStroke(1.4.dp, ArenaGold.copy(alpha = 0.88f)),
                                modifier = Modifier.fillMaxWidth(0.92f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "BREAKER RUN COMPLETE",
                                        color = ArenaGold,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = formattedScore,
                                        color = ArenaCyan,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Wave Reached: ${gameState.wave} • Bricks Shattered: ${gameState.totalBricksDestroyed}\nEarned +$roundTotalXp Arena XP",
                                        color = DarkTextSecondary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 17.sp
                                    )
                                    if (!secondChanceUsedThisRun) {
                                        ArenaRewardedButton(
                                            text = "Second Chance — Continue",
                                            onClick = {
                                                AdManager.showRewardedAd("breaker_second_chance") {
                                                    val (vx, vy) = NeonBreakerEngine.launchVelocity(gameState.wave, angleSign = 1f)
                                                    val ball = BreakerBall(
                                                        id = gameState.nextEntityId,
                                                        x = gameState.paddleX,
                                                        y = NeonBreakerEngine.PADDLE_Y - NeonBreakerEngine.PADDLE_HALF_H - NeonBreakerEngine.BALL_RADIUS - 0.005f,
                                                        vx = vx,
                                                        vy = vy
                                                    )
                                                    gameState = gameState.copy(
                                                        lives = 1,
                                                        balls = listOf(ball),
                                                        isGameOver = false,
                                                        revivePromptRemainingMs = 0L,
                                                        nextEntityId = gameState.nextEntityId + 1L
                                                    )
                                                    bestUpdatedOnFinish = false
                                                    secondChanceUsedThisRun = true
                                                }
                                            },
                                            accentColor = ArenaGold,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("breaker_rewarded_second_chance_button")
                                        )
                                    }

                                    if (roundTotalXp > 0L) {
                                        ArenaRewardedButton(
                                            text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                            enabled = !doubleXpClaimedThisRound,
                                            onClick = {
                                                AdManager.showRewardedAd("breaker_double_xp") {
                                                    val bonus = roundTotalXp
                                                    if (bonus > 0L) {
                                                        onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                                        roundTotalXp += bonus
                                                    }
                                                    doubleXpClaimedThisRound = true
                                                }
                                            },
                                            accentColor = ArenaCyan,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("breaker_double_xp_button")
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            AdManager.recordGameRoundCompleted("breaker_round_complete") {
                                                gameState = NeonBreakerEngine.newGame()
                                                particles.clear()
                                                shockwaves.clear()
                                                bestUpdatedOnFinish = false
                                                roundTotalXp = 0L
                                                secondChanceUsedThisRun = false
                                                doubleXpClaimedThisRound = false
                                                onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ArenaCyan,
                                            contentColor = ObsidianBg
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("breaker_play_again_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Replay,
                                            contentDescription = "Play Again",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Play Again",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Power-Up Hook Info Banner
        androidx.compose.animation.AnimatedVisibility(visible = powerUpHookMessage != null) {
            Text(
                text = powerUpHookMessage.orEmpty(),
                color = ArenaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 8.dp)
            )
        }

        // 4. Tactical Power-Up Dock: (1) Laser Cannon Burst (2) Second Chance Extra Ball (3) Instant Replay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val canLaser = gameState.laserPowerUpRemaining > 0 &&
                !gameState.isLaserCannonActive &&
                !gameState.isRevivePromptActive &&
                !gameState.isGameOver
            OutlinedButton(
                onClick = {
                    when {
                        gameState.isLaserCannonActive -> Unit
                        canLaser -> {
                            gameState = NeonBreakerEngine.activateLaserCannon(gameState)
                            if (feedbackEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                ArenaSynthAudio.playPowerUpActivate()
                            }
                        }
                        gameState.laserPowerUpRemaining <= 0 && !gameState.isGameOver -> {
                            powerUpHookMessage = "Free Laser Cannon used this match"
                        }
                    }
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        gameState.isLaserCannonActive -> RunnerTrapRed
                        canLaser -> ArenaCyan.copy(alpha = 0.78f)
                        else -> ArenaCyan.copy(alpha = 0.25f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = when {
                        gameState.isLaserCannonActive -> RunnerTrapRed
                        canLaser -> ArenaCyan
                        else -> DarkTextSecondary
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("breaker_laser_cannon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Laser Cannon Burst",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        gameState.isLaserCannonActive -> "Firing"
                        gameState.laserPowerUpRemaining > 0 -> "Laser 5s"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val canReviveNow = gameState.isRevivePromptActive && gameState.secondChanceRemaining > 0
            val reviveReady = gameState.secondChanceRemaining > 0 && !gameState.isGameOver
            OutlinedButton(
                onClick = {
                    when {
                        canReviveNow -> {
                            gameState = NeonBreakerEngine.activateSecondChanceRevive(gameState)
                            if (feedbackEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                ArenaSynthAudio.playPowerUpActivate()
                            }
                        }
                        gameState.secondChanceRemaining > 0 && !gameState.isGameOver -> {
                            powerUpHookMessage = "Second Chance auto-triggers when your last ball falls!"
                        }
                        !gameState.isGameOver -> {
                            powerUpHookMessage = "Free Extra Ball used this match"
                        }
                    }
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = if (canReviveNow || reviveReady) ArenaGold.copy(alpha = 0.80f) else ArenaGold.copy(alpha = 0.25f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (canReviveNow || reviveReady) ArenaGold else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("breaker_second_chance_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Second Chance Extra Ball",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        canReviveNow -> "Revive!"
                        gameState.secondChanceRemaining > 0 -> "Extra Ball"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    gameState = NeonBreakerEngine.newGame()
                    particles.clear()
                    shockwaves.clear()
                    bestUpdatedOnFinish = false
                    roundTotalXp = 0L
                    secondChanceUsedThisRun = false
                    doubleXpClaimedThisRound = false
                    onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaCyan)
                    .testTag("breaker_restart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Restart Neon Brick Breaker",
                    tint = ObsidianBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =============================================================================
// 14. Game 8: Chrono-Vault Memory Matrix (120fps Quantum Grid 3x3->6x6)
// =============================================================================

enum class ChronoVaultMode {
    CLASSIC_PATTERN,
    CHRONO_SEQUENCE,
    GOLDEN_BONUS_MIX
}

enum class ChronoPhase {
    MEMORIZING,
    RECALL,
    ROUND_CLEAR,
    GAME_OVER
}

data class ChronoTileTarget(
    val row: Int,
    val col: Int,
    val orderIndex: Int,
    val isGoldBonus: Boolean = false
)

data class ChronoVaultState(
    val round: Int = 1,
    val gridSize: Int = 3,
    val mode: ChronoVaultMode = ChronoVaultMode.CLASSIC_PATTERN,
    val phase: ChronoPhase = ChronoPhase.MEMORIZING,
    val targets: List<ChronoTileTarget> = emptyList(),
    val recalledTargetOrders: Set<Int> = emptySet(),
    val score: Long = 0L,
    val shields: Int = ChronoVaultEngine.INITIAL_SHIELDS,
    val comboHits: Int = 0,
    val flashElapsedMs: Long = 0L,
    val flashDurationMs: Long = ChronoVaultEngine.FLASH_DURATION_MS,
    val replayFlashRemainingMs: Long = 0L,
    val neuralReplayRemaining: Int = 1,
    val errorShieldRemaining: Int = 1,
    val isErrorShieldArmed: Boolean = false,
    val wrongTapCell: Pair<Int, Int>? = null,
    val forgivenTapCell: Pair<Int, Int>? = null,
    val feedbackRemainingMs: Long = 0L,
    val goldBonusHitCount: Int = 0,
    val isGameOver: Boolean = false
) {
    val isFlashingPattern: Boolean
        get() = phase == ChronoPhase.MEMORIZING || replayFlashRemainingMs > 0L

    val nextExpectedOrder: Int
        get() = recalledTargetOrders.size + 1

    fun activeSequentialStepOrder(): Int? {
        if (mode != ChronoVaultMode.CHRONO_SEQUENCE || targets.isEmpty() || !isFlashingPattern) return null
        val progress = if (phase == ChronoPhase.MEMORIZING) {
            (flashElapsedMs.toFloat() / flashDurationMs.coerceAtLeast(1L).toFloat()).coerceIn(0f, 0.9999f)
        } else {
            val replayDur = ChronoVaultEngine.REPLAY_FLASH_DURATION_MS.coerceAtLeast(1L).toFloat()
            (1f - replayFlashRemainingMs.toFloat() / replayDur).coerceIn(0f, 0.9999f)
        }
        return (progress * targets.size).toInt().coerceIn(0, targets.lastIndex) + 1
    }
}

data class ChronoTapOutcome(
    val nextState: ChronoVaultState,
    val isCorrect: Boolean = false,
    val tappedGoldBonus: Boolean = false,
    val absorbedByShield: Boolean = false,
    val pointsGained: Long = 0L,
    val xpGained: Long = 0L,
    val roundCleared: Boolean = false,
    val wrongCell: Pair<Int, Int>? = null
)

object ChronoVaultEngine {
    const val INITIAL_SHIELDS = 3
    const val FLASH_DURATION_MS = 2_000L
    const val REPLAY_FLASH_DURATION_MS = 1_500L

    fun gridSizeForRound(round: Int): Int = when {
        round <= 2 -> 3
        round <= 5 -> 4
        round <= 9 -> 5
        else -> 6
    }

    fun modeForRound(round: Int): ChronoVaultMode = when ((round - 1).coerceAtLeast(0) % 3) {
        0 -> ChronoVaultMode.CLASSIC_PATTERN
        1 -> ChronoVaultMode.CHRONO_SEQUENCE
        else -> ChronoVaultMode.GOLDEN_BONUS_MIX
    }

    fun targetCountForRound(round: Int, gridSize: Int, mode: ChronoVaultMode): Int {
        val totalCells = (gridSize * gridSize).coerceAtLeast(9)
        return when (mode) {
            ChronoVaultMode.CHRONO_SEQUENCE -> {
                val base = 4 + (round - 2).coerceAtLeast(0) / 3
                base.coerceIn(4, minOf(9, totalCells - 2))
            }
            ChronoVaultMode.CLASSIC_PATTERN,
            ChronoVaultMode.GOLDEN_BONUS_MIX -> {
                val maxCap = (totalCells * 0.55f).toInt().coerceAtLeast(4)
                val scaled = 3 + (round - 1).coerceAtLeast(0) / 2 + (gridSize - 3)
                scaled.coerceIn(3, minOf(maxCap, totalCells - 2))
            }
        }
    }

    fun generateTargets(
        round: Int,
        gridSize: Int,
        mode: ChronoVaultMode,
        previousSignature: Set<Pair<Int, Int>> = emptySet(),
        random: Random = Random.Default
    ): List<ChronoTileTarget> {
        val allCoords = buildList {
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    add(r to c)
                }
            }
        }
        val count = targetCountForRound(round, gridSize, mode).coerceAtMost(allCoords.size)
        var picked = allCoords.shuffled(random).take(count)
        if (previousSignature.isNotEmpty() && picked.toSet() == previousSignature && allCoords.size > count) {
            picked = allCoords.shuffled(random).take(count)
        }

        val goldIndices = when (mode) {
            ChronoVaultMode.GOLDEN_BONUS_MIX -> {
                val goldCount = if (round >= 6 && count >= 5) 2 else 1
                picked.indices.shuffled(random).take(goldCount).toSet()
            }
            ChronoVaultMode.CLASSIC_PATTERN -> {
                if (round >= 4 && random.nextFloat() < 0.6f) {
                    setOf(random.nextInt(picked.size))
                } else {
                    emptySet()
                }
            }
            ChronoVaultMode.CHRONO_SEQUENCE -> {
                if (round >= 5 && random.nextFloat() < 0.5f) {
                    setOf(picked.lastIndex)
                } else {
                    emptySet()
                }
            }
        }

        return picked.mapIndexed { idx, (r, c) ->
            ChronoTileTarget(
                row = r,
                col = c,
                orderIndex = idx + 1,
                isGoldBonus = idx in goldIndices
            )
        }
    }

    fun newGame(random: Random = Random.Default): ChronoVaultState =
        newRound(
            round = 1,
            score = 0L,
            shields = INITIAL_SHIELDS,
            neuralReplayRemaining = 1,
            errorShieldRemaining = 1,
            random = random
        )

    fun startFromRound(round: Int, random: Random = Random.Default): ChronoVaultState =
        newRound(
            round = round.coerceAtLeast(1),
            score = 0L,
            shields = INITIAL_SHIELDS,
            neuralReplayRemaining = 1,
            errorShieldRemaining = 1,
            random = random
        )

    fun nextRound(
        current: ChronoVaultState,
        forcedMode: ChronoVaultMode? = null,
        random: Random = Random.Default
    ): ChronoVaultState {
        val nextR = current.round + 1
        val prevSig = current.targets.map { it.row to it.col }.toSet()
        return newRound(
            round = nextR,
            score = current.score,
            shields = current.shields.coerceAtLeast(1),
            neuralReplayRemaining = current.neuralReplayRemaining,
            errorShieldRemaining = current.errorShieldRemaining,
            isErrorShieldArmed = current.isErrorShieldArmed,
            goldBonusHitCount = current.goldBonusHitCount,
            forcedMode = forcedMode,
            previousSignature = prevSig,
            random = random
        )
    }

    fun newRound(
        round: Int,
        score: Long = 0L,
        shields: Int = INITIAL_SHIELDS,
        neuralReplayRemaining: Int = 1,
        errorShieldRemaining: Int = 1,
        isErrorShieldArmed: Boolean = false,
        goldBonusHitCount: Int = 0,
        forcedMode: ChronoVaultMode? = null,
        previousSignature: Set<Pair<Int, Int>> = emptySet(),
        random: Random = Random.Default
    ): ChronoVaultState {
        val safeRound = round.coerceAtLeast(1)
        val gridSize = gridSizeForRound(safeRound)
        val mode = forcedMode ?: modeForRound(safeRound)
        val targets = generateTargets(safeRound, gridSize, mode, previousSignature, random)
        return ChronoVaultState(
            round = safeRound,
            gridSize = gridSize,
            mode = mode,
            phase = ChronoPhase.MEMORIZING,
            targets = targets,
            recalledTargetOrders = emptySet(),
            score = score,
            shields = shields,
            comboHits = 0,
            flashElapsedMs = 0L,
            flashDurationMs = FLASH_DURATION_MS,
            replayFlashRemainingMs = 0L,
            neuralReplayRemaining = neuralReplayRemaining,
            errorShieldRemaining = errorShieldRemaining,
            isErrorShieldArmed = isErrorShieldArmed,
            wrongTapCell = null,
            forgivenTapCell = null,
            feedbackRemainingMs = 0L,
            goldBonusHitCount = goldBonusHitCount,
            isGameOver = false
        )
    }

    fun stepTime(state: ChronoVaultState, dtMs: Long): ChronoVaultState {
        if (state.isGameOver || dtMs <= 0L) return state
        var next = state

        if (next.phase == ChronoPhase.MEMORIZING) {
            val elapsed = next.flashElapsedMs + dtMs
            if (elapsed >= next.flashDurationMs) {
                next = next.copy(
                    phase = ChronoPhase.RECALL,
                    flashElapsedMs = next.flashDurationMs
                )
            } else {
                next = next.copy(flashElapsedMs = elapsed)
            }
        }

        if (next.replayFlashRemainingMs > 0L) {
            val rem = (next.replayFlashRemainingMs - dtMs).coerceAtLeast(0L)
            next = next.copy(replayFlashRemainingMs = rem)
        }

        if (next.feedbackRemainingMs > 0L) {
            val rem = (next.feedbackRemainingMs - dtMs).coerceAtLeast(0L)
            next = next.copy(
                feedbackRemainingMs = rem,
                wrongTapCell = if (rem == 0L) null else next.wrongTapCell,
                forgivenTapCell = if (rem == 0L) null else next.forgivenTapCell
            )
        }

        return next
    }

    fun tapCell(state: ChronoVaultState, row: Int, col: Int): ChronoTapOutcome {
        if (state.isGameOver || state.phase != ChronoPhase.RECALL) {
            return ChronoTapOutcome(state)
        }
        if (row !in 0 until state.gridSize || col !in 0 until state.gridSize) {
            return ChronoTapOutcome(state)
        }

        val matchingTarget = state.targets.firstOrNull { it.row == row && it.col == col }
        if (matchingTarget != null && matchingTarget.orderIndex in state.recalledTargetOrders) {
            return ChronoTapOutcome(state)
        }

        val isExpectedHit = when (state.mode) {
            ChronoVaultMode.CLASSIC_PATTERN,
            ChronoVaultMode.GOLDEN_BONUS_MIX -> matchingTarget != null
            ChronoVaultMode.CHRONO_SEQUENCE ->
                matchingTarget != null && matchingTarget.orderIndex == state.nextExpectedOrder
        }

        if (isExpectedHit && matchingTarget != null) {
            val nextCombo = state.comboHits + 1
            val baseTilePts = 45L + (state.round - 1) * 15L + (nextCombo - 1) * 10L
            val points = if (matchingTarget.isGoldBonus) baseTilePts * 2L else baseTilePts
            val tileXp = if (matchingTarget.isGoldBonus) 10L else 3L
            val nextRecalled = state.recalledTargetOrders + matchingTarget.orderIndex
            val allCleared = nextRecalled.size >= state.targets.size
            val clearBonus = if (allCleared) 120L + state.round * 40L else 0L
            val clearXp = if (allCleared) 18L + state.round * 2L else 0L

            val nextState = state.copy(
                recalledTargetOrders = nextRecalled,
                score = state.score + points + clearBonus,
                comboHits = nextCombo,
                goldBonusHitCount = state.goldBonusHitCount + if (matchingTarget.isGoldBonus) 1 else 0,
                phase = if (allCleared) ChronoPhase.ROUND_CLEAR else ChronoPhase.RECALL,
                wrongTapCell = null,
                forgivenTapCell = null,
                feedbackRemainingMs = 0L
            )
            return ChronoTapOutcome(
                nextState = nextState,
                isCorrect = true,
                tappedGoldBonus = matchingTarget.isGoldBonus,
                pointsGained = points + clearBonus,
                xpGained = tileXp + clearXp,
                roundCleared = allCleared
            )
        }

        // Wrong tile or wrong Chrono-Code sequence order
        if (state.isErrorShieldArmed) {
            val shieldedState = state.copy(
                isErrorShieldArmed = false,
                forgivenTapCell = row to col,
                wrongTapCell = null,
                feedbackRemainingMs = 550L
            )
            return ChronoTapOutcome(
                nextState = shieldedState,
                isCorrect = false,
                absorbedByShield = true,
                wrongCell = row to col
            )
        }

        val nextShields = (state.shields - 1).coerceAtLeast(0)
        val outOfShields = nextShields <= 0
        val penalizedState = state.copy(
            shields = nextShields,
            comboHits = 0,
            wrongTapCell = row to col,
            forgivenTapCell = null,
            feedbackRemainingMs = 520L,
            phase = if (outOfShields) ChronoPhase.GAME_OVER else ChronoPhase.RECALL,
            isGameOver = outOfShields
        )
        return ChronoTapOutcome(
            nextState = penalizedState,
            isCorrect = false,
            wrongCell = row to col
        )
    }

    fun activateNeuralReplay(state: ChronoVaultState): ChronoVaultState {
        if (state.isGameOver || state.phase != ChronoPhase.RECALL || state.neuralReplayRemaining <= 0) {
            return state
        }
        return state.copy(
            neuralReplayRemaining = state.neuralReplayRemaining - 1,
            replayFlashRemainingMs = REPLAY_FLASH_DURATION_MS
        )
    }

    fun activateErrorShield(state: ChronoVaultState): ChronoVaultState {
        if (state.isGameOver || state.phase == ChronoPhase.GAME_OVER || state.errorShieldRemaining <= 0 || state.isErrorShieldArmed) {
            return state
        }
        return state.copy(
            errorShieldRemaining = state.errorShieldRemaining - 1,
            isErrorShieldArmed = true
        )
    }
}

private data class ChronoParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val radiusPx: Float,
    val color: Color
)

@Composable
private fun ChronoVaultHeroBannerCard(
    spec: ArenaGameSpec,
    snapshot: ArenaSnapshot,
    onUnlockFlashShown: () -> Unit,
    onPlayClick: () -> Unit
) {
    val isUnlocked = snapshot.isGame8Unlocked
    val formattedBestScore = remember(snapshot.bestChronoScore) {
        NumberFormat.getNumberInstance(Locale.US).format(snapshot.bestChronoScore)
    }

    var unlockBannerVisible by remember(snapshot.shouldShowGame8UnlockFlash) {
        mutableStateOf(snapshot.shouldShowGame8UnlockFlash)
    }
    val unlockFlashAlpha = remember { Animatable(0f) }

    LaunchedEffect(snapshot.shouldShowGame8UnlockFlash) {
        if (snapshot.shouldShowGame8UnlockFlash) {
            unlockBannerVisible = true
            unlockFlashAlpha.snapTo(0.95f)
            unlockFlashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing)
            )
            delay(1400L)
            unlockBannerVisible = false
            onUnlockFlashShown()
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B0C10),
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.linearGradient(
                colors = if (isUnlocked) {
                    listOf(ArenaGold.copy(alpha = 0.92f), ArenaCyan.copy(alpha = 0.86f), ArenaGold.copy(alpha = 0.78f))
                } else {
                    listOf(ArenaCyan.copy(alpha = 0.55f), ArenaGold.copy(alpha = 0.50f))
                }
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_8")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0A1424),
                            ObsidianBg,
                            Color(0xFF191306)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.13f), Color.Transparent),
                        center = Offset(size.height * 0.45f, size.height * 0.52f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.45f, size.height * 0.52f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.25f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ChronoVaultLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("chrono_vault_logo_canvas")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_chrono_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Chrono-Vault Stats",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 R${snapshot.bestChronoRound} • $formattedBestScore",
                                color = ArenaGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        enabled = isUnlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaGold,
                            contentColor = ObsidianBg,
                            disabledContainerColor = Color(0xFF1F2937),
                            disabledContentColor = DarkTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_chrono_play_button")
                    ) {
                        Text(
                            text = if (isUnlocked) "PLAY ▶" else "LOCKED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Frosted Glass Savings Goal / Monthly Budget Lock Overlay
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xDD0B0C10),
                                    Color(0xEB101726),
                                    Color(0xDD0B0C10)
                                )
                            )
                        )
                        .border(1.2.dp, ArenaGold.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                        .testTag("chrono_goal_budget_lock_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.78f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked by Savings Goal or Monthly Budget",
                                    tint = ArenaGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "🔒 Unlock by Completing 1 Savings Goal or Monthly Budget",
                                    color = ArenaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Text(
                            text = "Hit 100% of any Savings Goal or stay under a Monthly Budget to unlock forever!",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (unlockFlashAlpha.value > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(ArenaGold.copy(alpha = unlockFlashAlpha.value * 0.50f))
                )
            }
            AnimatedVisibility(
                visible = unlockBannerVisible && isUnlocked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f))
                ) {
                    Text(
                        text = "✨ GOAL / BUDGET UNLOCKED!",
                        color = ObsidianBg,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChronoVaultLogoArtwork(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chronoVaultLogoAnim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.68f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "quantumMatrixGlow"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Chrono-Vault Memory Matrix glowing 3D quantum memory grid and golden vault key emblem"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0A1426), Color(0xFF151008))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaCyan.copy(alpha = 0.90f), ArenaGold.copy(alpha = 0.90f))),
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val w = size.width
        val h = size.height
        val pad = w * 0.14f
        val gap = w * 0.045f
        val cell = (w - pad * 2f - gap * 2f) / 3f

        // 3x3 Glowing 3D Quantum Memory Matrix Grid
        val cyanLit = setOf(0 to 0, 1 to 1, 2 to 0, 1 to 2)
        val goldNode = 0 to 2
        for (r in 0 until 3) {
            for (c in 0 until 3) {
                val x = pad + c * (cell + gap)
                val y = pad + r * (cell + gap)
                val isGold = (r to c) == goldNode
                val isLit = (r to c) in cyanLit
                val tileColor = when {
                    isGold -> ArenaGold
                    isLit -> ArenaCyan
                    else -> Color(0xFF192234)
                }
                val tileAlpha = when {
                    isGold -> 0.92f * pulse
                    isLit -> 0.85f * pulse
                    else -> 0.90f
                }
                // 3D depth bevel base
                drawRoundRect(
                    color = Color(0xFF05080F),
                    topLeft = Offset(x, y + 2.dp.toPx()),
                    size = Size(cell, cell),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                drawRoundRect(
                    color = tileColor.copy(alpha = tileAlpha),
                    topLeft = Offset(x, y),
                    size = Size(cell, cell),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = if (isLit || isGold) 0.72f else 0.16f),
                    topLeft = Offset(x, y),
                    size = Size(cell, cell),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Golden Vault Key Silhouette in Bottom-Right Overlay
        val keyHeadCenter = Offset(w * 0.73f, h * 0.74f)
        drawCircle(
            color = Color(0xCC090B10),
            radius = w * 0.17f,
            center = keyHeadCenter
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFF4B8), ArenaGold, Color(0xFFB45309)),
                center = keyHeadCenter,
                radius = w * 0.12f
            ),
            radius = w * 0.115f,
            center = keyHeadCenter
        )
        drawCircle(
            color = Color(0xFF0B0C10),
            radius = w * 0.042f,
            center = keyHeadCenter
        )
        // Key shaft + teeth
        drawLine(
            color = ArenaGold,
            start = Offset(keyHeadCenter.x - w * 0.09f, keyHeadCenter.y),
            end = Offset(w * 0.36f, keyHeadCenter.y),
            strokeWidth = 3.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ArenaGold,
            start = Offset(w * 0.42f, keyHeadCenter.y),
            end = Offset(w * 0.42f, keyHeadCenter.y + w * 0.075f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ArenaGold,
            start = Offset(w * 0.50f, keyHeadCenter.y),
            end = Offset(w * 0.50f, keyHeadCenter.y + w * 0.055f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ChronoVaultMatrixGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val textMeasurer = rememberTextMeasurer()

    var gameState by remember { mutableStateOf(ChronoVaultEngine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var powerUpHookMessage by remember { mutableStateOf<String?>(null) }
    var bestUpdatedOnFinish by remember { mutableStateOf(false) }
    var roundTotalXp by remember { mutableLongStateOf(0L) }
    var secondChanceUsedThisRun by remember { mutableStateOf(false) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }
    var rewardedHintUsed by remember { mutableStateOf(false) }
    var boardPixelSize by remember { mutableStateOf(Size.Zero) }
    var shimmerPhase by remember { mutableFloatStateOf(0f) }

    val particles = remember { mutableStateListOf<ChronoParticle>() }

    LaunchedEffect(powerUpHookMessage) {
        if (powerUpHookMessage != null) {
            delay(2100L)
            powerUpHookMessage = null
        }
    }

    // Play Hologram flash pulse whenever a new round starts flashing
    LaunchedEffect(gameState.round, gameState.phase == ChronoPhase.MEMORIZING) {
        if (gameState.phase == ChronoPhase.MEMORIZING && feedbackEnabled) {
            ArenaSynthAudio.playChronoFlashPulse()
        }
    }

    // Auto-advance seamlessly after celebratory ROUND_CLEAR burst
    LaunchedEffect(gameState.phase, gameState.round) {
        if (gameState.phase == ChronoPhase.ROUND_CLEAR) {
            delay(1_050L)
            gameState = ChronoVaultEngine.nextRound(gameState)
        }
    }

    // Main 120fps Animation, Flash Timer & Particle Loop
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) return@LaunchedEffect
        var lastNs = 0L
        while (!gameState.isGameOver) {
            withFrameNanos { frameNs ->
                if (lastNs == 0L) {
                    lastNs = frameNs
                    return@withFrameNanos
                }
                val dtNs = (frameNs - lastNs).coerceAtMost(35_000_000L)
                val dtMs = (dtNs / 1_000_000L).coerceAtLeast(1L)
                val dtSec = dtNs / 1_000_000_000f
                lastNs = frameNs

                shimmerPhase = (shimmerPhase + dtSec * 1.8f) % 100f
                gameState = ChronoVaultEngine.stepTime(gameState, dtMs)

                val it = particles.listIterator()
                while (it.hasNext()) {
                    val p = it.next()
                    p.life += dtSec
                    if (p.life >= p.maxLife) {
                        it.remove()
                    } else {
                        p.x += p.vx * dtSec
                        p.y += p.vy * dtSec
                        p.vy += 220f * dtSec
                    }
                }
            }
        }
    }

    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver && !bestUpdatedOnFinish) {
            bestUpdatedOnFinish = true
            onSnapshotUpdated(
                ArenaProgressStore.updateBestChronoStatsIfHigher(context, gameState.score, gameState.round)
            )
        }
    }

    val effectiveBestRound = max(snapshot.bestChronoRound, gameState.round)
    val effectiveBestScore = max(snapshot.bestChronoScore, gameState.score)
    val formattedScore = remember(gameState.score) {
        NumberFormat.getNumberInstance(Locale.US).format(gameState.score)
    }
    val formattedBestScore = remember(effectiveBestScore) {
        NumberFormat.getNumberInstance(Locale.US).format(effectiveBestScore)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .navigationBarsPadding()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Top Navigation & Sound Mute/Unmute Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        onSnapshotUpdated(
                            ArenaProgressStore.updateBestChronoStatsIfHigher(context, gameState.score, gameState.round)
                        )
                        onExitToHub()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("game_chrono_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Chrono-Vault Matrix",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val modeColor = when (gameState.mode) {
                        ChronoVaultMode.GOLDEN_BONUS_MIX -> ArenaGold
                        ChronoVaultMode.CHRONO_SEQUENCE -> Color(0xFF38BDF8)
                        ChronoVaultMode.CLASSIC_PATTERN -> ArenaCyan
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = modeColor.copy(alpha = 0.14f),
                        border = BorderStroke(0.75.dp, modeColor.copy(alpha = 0.52f))
                    ) {
                        val badgeLabel = when (gameState.mode) {
                            ChronoVaultMode.CLASSIC_PATTERN -> "CLASSIC PATTERN • ${gameState.gridSize}x${gameState.gridSize}"
                            ChronoVaultMode.CHRONO_SEQUENCE -> "CHRONO-CODE 1→${gameState.targets.size} • ${gameState.gridSize}x${gameState.gridSize}"
                            ChronoVaultMode.GOLDEN_BONUS_MIX -> "24K GOLD BONUS 2X • ${gameState.gridSize}x${gameState.gridSize}"
                        }
                        Text(
                            text = badgeLabel,
                            color = modeColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    feedbackEnabled = !feedbackEnabled
                    onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObsidianElevated)
                    .testTag("chrono_sound_toggle_button")
            ) {
                Icon(
                    imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (feedbackEnabled) "Mute Game Audio" else "Unmute Game Audio",
                    tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary
                )
            }
        }

        // 2. Top 4 Glass HUD Pills: ROUND, SCORE, SHIELDS, BEST
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TowerHudPill(
                label = "ROUND",
                value = "${gameState.round}",
                accentColor = ArenaCyan,
                modifier = Modifier
                    .weight(0.85f)
                    .testTag("chrono_round_pill")
            )
            TowerHudPill(
                label = "SCORE",
                value = formattedScore,
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("chrono_score_pill")
            )
            TowerHudPill(
                label = "SHIELDS",
                value = "🛡 ${gameState.shields}",
                accentColor = if (gameState.shields <= 1) RunnerTrapRed else ArenaCyan,
                modifier = Modifier
                    .weight(0.95f)
                    .testTag("chrono_shields_pill")
            )
            TowerHudPill(
                label = "BEST",
                value = "R$effectiveBestRound",
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(0.9f)
                    .testTag("chrono_best_pill")
            )
        }

        // Phase / Memorize Countdown Status Banner
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = when {
                gameState.phase == ChronoPhase.ROUND_CLEAR -> ArenaGold.copy(alpha = 0.18f)
                gameState.isFlashingPattern -> ArenaCyan.copy(alpha = 0.14f)
                gameState.isErrorShieldArmed -> ArenaGold.copy(alpha = 0.16f)
                else -> Color(0xFF131A26)
            },
            border = BorderStroke(
                1.dp,
                when {
                    gameState.phase == ChronoPhase.ROUND_CLEAR -> ArenaGold.copy(alpha = 0.85f)
                    gameState.isFlashingPattern -> ArenaCyan.copy(alpha = 0.72f)
                    gameState.isErrorShieldArmed -> ArenaGold.copy(alpha = 0.80f)
                    else -> Color.White.copy(alpha = 0.18f)
                }
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            val statusText = when {
                gameState.phase == ChronoPhase.ROUND_CLEAR ->
                    "✨ VAULT UNLOCKED! ADVANCING TO ROUND ${gameState.round + 1}..."
                gameState.phase == ChronoPhase.MEMORIZING -> {
                    val secLeft = kotlin.math.ceil((gameState.flashDurationMs - gameState.flashElapsedMs) / 1000.0).toInt().coerceAtLeast(1)
                    if (gameState.mode == ChronoVaultMode.CHRONO_SEQUENCE) {
                        "MEMORIZE SEQUENCE 1→${gameState.targets.size} • ${secLeft}s"
                    } else {
                        "MEMORIZE HOLOGRAM MATRIX • ${secLeft}s"
                    }
                }
                gameState.replayFlashRemainingMs > 0L ->
                    "NEURAL REPLAY ACTIVE • RE-FLASHING MATRIX"
                gameState.isErrorShieldArmed ->
                    "🛡 ERROR SHIELD ARMED • TAP SAFELY (${gameState.recalledTargetOrders.size}/${gameState.targets.size})"
                gameState.mode == ChronoVaultMode.CHRONO_SEQUENCE ->
                    "TAP ORDER STEP #${gameState.nextExpectedOrder} OF ${gameState.targets.size}"
                else ->
                    "UNLOCK VAULT CELLS • ${gameState.recalledTargetOrders.size}/${gameState.targets.size} FOUND"
            }
            Text(
                text = statusText,
                color = when {
                    gameState.phase == ChronoPhase.ROUND_CLEAR || gameState.isErrorShieldArmed -> ArenaGold
                    gameState.isFlashingPattern -> ArenaCyan
                    else -> DarkTextPrimary
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        // 3. Interactive 120fps Quantum Vault Matrix Canvas (3x3 -> 6x6)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .widthIn(max = 560.dp)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            val currentGameState by rememberUpdatedState(gameState)
            val currentFeedback by rememberUpdatedState(feedbackEnabled)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF070C16), Color(0xFF0D1422), Color(0xFF120F0A))
                        )
                    )
                    .border(
                        width = 1.6.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(ArenaCyan.copy(alpha = 0.72f), ArenaGold.copy(alpha = 0.65f))
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { coords ->
                            boardPixelSize = Size(
                                coords.size.width.toFloat(),
                                coords.size.height.toFloat()
                            )
                        }
                        .pointerInput(currentGameState.gridSize, currentGameState.phase, currentGameState.isGameOver) {
                            detectTapGestures { offset ->
                                val st = currentGameState
                                if (st.isGameOver || st.phase != ChronoPhase.RECALL) return@detectTapGestures
                                val w = size.width.toFloat()
                                val h = size.height.toFloat()
                                val pad = w * 0.055f
                                val gap = w * 0.025f
                                val n = st.gridSize
                                val cellSpan = (w - 2f * pad - (n - 1) * gap) / n.toFloat()
                                if (cellSpan <= 1f) return@detectTapGestures

                                var tappedRow = -1
                                var tappedCol = -1
                                for (r in 0 until n) {
                                    val top = pad + r * (cellSpan + gap)
                                    if (offset.y in top..(top + cellSpan)) {
                                        for (c in 0 until n) {
                                            val left = pad + c * (cellSpan + gap)
                                            if (offset.x in left..(left + cellSpan)) {
                                                tappedRow = r
                                                tappedCol = c
                                                break
                                            }
                                        }
                                        break
                                    }
                                }
                                if (tappedRow < 0 || tappedCol < 0) return@detectTapGestures

                                val outcome = ChronoVaultEngine.tapCell(st, tappedRow, tappedCol)
                                gameState = outcome.nextState

                                val cx = pad + tappedCol * (cellSpan + gap) + cellSpan * 0.5f
                                val cy = pad + tappedRow * (cellSpan + gap) + cellSpan * 0.5f

                                if (outcome.isCorrect) {
                                    if (currentFeedback) {
                                        haptic.performHapticFeedback(
                                            if (outcome.tappedGoldBonus || outcome.roundCleared) {
                                                HapticFeedbackType.LongPress
                                            } else {
                                                HapticFeedbackType.TextHandleMove
                                            }
                                        )
                                        if (outcome.roundCleared) {
                                            ArenaSynthAudio.playChronoRoundClearChord()
                                        } else {
                                            ArenaSynthAudio.playChronoTileNote(
                                                stepIndex = outcome.nextState.recalledTargetOrders.size - 1,
                                                isGoldBonus = outcome.tappedGoldBonus
                                            )
                                        }
                                    }

                                    // Tile burst particles
                                    val burstCount = if (outcome.roundCleared) 24 else if (outcome.tappedGoldBonus) 14 else 8
                                    val pColor = if (outcome.tappedGoldBonus || outcome.roundCleared) ArenaGold else ArenaCyan
                                    repeat(burstCount) { idx ->
                                        val angle = (idx / burstCount.toDouble()) * 2.0 * PI + Random.nextFloat() * 0.35f
                                        val spd = (90f + Random.nextFloat() * 210f) * if (outcome.roundCleared) 1.35f else 1f
                                        particles.add(
                                            ChronoParticle(
                                                x = cx,
                                                y = cy,
                                                vx = (cos(angle) * spd).toFloat(),
                                                vy = (sin(angle) * spd).toFloat(),
                                                life = 0f,
                                                maxLife = if (outcome.roundCleared) 0.72f else 0.45f,
                                                radiusPx = if (outcome.tappedGoldBonus || outcome.roundCleared) 5f else 3.5f,
                                                color = if (idx % 3 == 0 && outcome.roundCleared) ArenaCyan else pColor
                                            )
                                        )
                                    }

                                    if (outcome.roundCleared && h > 1f) {
                                        // Center celebration fountain burst
                                        repeat(28) { idx ->
                                            val angle = (idx / 28.0) * 2.0 * PI
                                            val spd = 140f + Random.nextFloat() * 260f
                                            particles.add(
                                                ChronoParticle(
                                                    x = w * 0.5f,
                                                    y = h * 0.5f,
                                                    vx = (cos(angle) * spd).toFloat(),
                                                    vy = (sin(angle) * spd).toFloat() - 60f,
                                                    life = 0f,
                                                    maxLife = 0.85f,
                                                    radiusPx = 4.5f + Random.nextFloat() * 3f,
                                                    color = if (idx % 2 == 0) ArenaGold else Color.White
                                                )
                                            )
                                        }
                                    }

                                    if (outcome.xpGained > 0L) {
                                        roundTotalXp += outcome.xpGained
                                        var snap = ArenaProgressStore.updateBestChronoStatsIfHigher(
                                            context,
                                            outcome.nextState.score,
                                            outcome.nextState.round
                                        )
                                        snap = ArenaProgressStore.awardXpAndTouchStreak(context, outcome.xpGained)
                                        onSnapshotUpdated(snap)
                                    }
                                } else if (outcome.wrongCell != null) {
                                    if (currentFeedback) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        if (outcome.absorbedByShield) {
                                            ArenaSynthAudio.playChronoFlashPulse()
                                        } else {
                                            ArenaSynthAudio.playChronoWrongBuzz()
                                        }
                                    }
                                    val shieldBurstColor = if (outcome.absorbedByShield) ArenaGold else RunnerTrapRed
                                    repeat(10) { idx ->
                                        val angle = (idx / 10.0) * 2.0 * PI
                                        val spd = 95f + Random.nextFloat() * 140f
                                        particles.add(
                                            ChronoParticle(
                                                x = cx,
                                                y = cy,
                                                vx = (cos(angle) * spd).toFloat(),
                                                vy = (sin(angle) * spd).toFloat(),
                                                life = 0f,
                                                maxLife = 0.42f,
                                                radiusPx = 3.8f,
                                                color = shieldBurstColor
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        .semantics {
                            contentDescription = "Chrono-Vault ${gameState.gridSize} by ${gameState.gridSize} quantum memory matrix"
                        }
                        .testTag("chrono_vault_matrix_canvas")
                ) {
                    val w = size.width
                    val n = gameState.gridSize
                    val pad = w * 0.055f
                    val gap = w * 0.025f
                    val cellSpan = (w - 2f * pad - (n - 1) * gap) / n.toFloat()
                    val cornerR = CornerRadius((cellSpan * 0.18f).coerceIn(8f, 22f))
                    val activeSeqOrder = gameState.activeSequentialStepOrder()
                    val isFlash = gameState.isFlashingPattern
                    val goldPulse = 0.78f + 0.22f * sin(shimmerPhase * 2.6f)

                    for (r in 0 until n) {
                        for (c in 0 until n) {
                            val x = pad + c * (cellSpan + gap)
                            val y = pad + r * (cellSpan + gap)
                            val target = gameState.targets.firstOrNull { it.row == r && it.col == c }
                            val isRecalled = target != null && target.orderIndex in gameState.recalledTargetOrders
                            val isWrongTap = gameState.wrongTapCell == (r to c)
                            val isForgivenTap = gameState.forgivenTapCell == (r to c)
                            val isSeqStepFocus = isFlash && target != null && activeSeqOrder == target.orderIndex
                            val isShownByFlash = isFlash && target != null && (
                                gameState.mode != ChronoVaultMode.CHRONO_SEQUENCE ||
                                    activeSeqOrder == null ||
                                    target.orderIndex <= activeSeqOrder
                            )

                            val litGold = (isRecalled || isShownByFlash) && target?.isGoldBonus == true
                            val litCyan = (isRecalled || isShownByFlash) && target?.isGoldBonus == false

                            // 3D beveled shadow base
                            drawRoundRect(
                                color = Color(0xFF04060A),
                                topLeft = Offset(x, y + 4.dp.toPx()),
                                size = Size(cellSpan, cellSpan),
                                cornerRadius = cornerR
                            )

                            // Tile Face Gradient
                            val fillBrush = when {
                                isWrongTap -> Brush.verticalGradient(listOf(RunnerTrapRed, Color(0xFF7F1D1D)))
                                isForgivenTap -> Brush.verticalGradient(listOf(ArenaGold, Color(0xFF92400E)))
                                litGold -> Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFF3B0).copy(alpha = goldPulse),
                                        ArenaGold,
                                        Color(0xFFD97706)
                                    ),
                                    start = Offset(x, y),
                                    end = Offset(x + cellSpan, y + cellSpan)
                                )
                                isSeqStepFocus -> Brush.linearGradient(
                                    listOf(Color.White, ArenaCyan, Color(0xFF0284C7)),
                                    start = Offset(x, y),
                                    end = Offset(x + cellSpan, y + cellSpan)
                                )
                                litCyan -> Brush.linearGradient(
                                    listOf(
                                        ArenaCyan.copy(alpha = if (isRecalled) 0.96f else 0.84f),
                                        Color(0xFF0891B2)
                                    ),
                                    start = Offset(x, y),
                                    end = Offset(x + cellSpan, y + cellSpan)
                                )
                                else -> Brush.verticalGradient(
                                    listOf(Color(0xFF1B2436), Color(0xFF101622))
                                )
                            }

                            if (litGold || litCyan || isSeqStepFocus) {
                                drawRoundRect(
                                    color = (if (litGold) ArenaGold else ArenaCyan).copy(
                                        alpha = if (isSeqStepFocus) 0.42f else 0.24f * goldPulse
                                    ),
                                    topLeft = Offset(x - 3.dp.toPx(), y - 3.dp.toPx()),
                                    size = Size(cellSpan + 6.dp.toPx(), cellSpan + 6.dp.toPx()),
                                    cornerRadius = cornerR
                                )
                            }

                            drawRoundRect(
                                brush = fillBrush,
                                topLeft = Offset(x, y),
                                size = Size(cellSpan, cellSpan),
                                cornerRadius = cornerR
                            )

                            // Crisp 3D rim border
                            val strokeColor = when {
                                isWrongTap -> Color.White.copy(alpha = 0.85f)
                                isForgivenTap -> Color.White.copy(alpha = 0.90f)
                                litGold -> Color(0xFFFFF9DB)
                                isSeqStepFocus -> Color.White
                                litCyan -> Color.White.copy(alpha = 0.78f)
                                else -> ArenaCyan.copy(alpha = 0.20f)
                            }
                            drawRoundRect(
                                color = strokeColor,
                                topLeft = Offset(x, y),
                                size = Size(cellSpan, cellSpan),
                                cornerRadius = cornerR,
                                style = Stroke(width = if (litGold || isSeqStepFocus) 2.2.dp.toPx() else 1.1.dp.toPx())
                            )

                            // Draw Chrono-Code sequence numeral (1, 2, 3...) or 24K Gold star glyph
                            if (target != null && (isShownByFlash || isRecalled)) {
                                val badgeText = when {
                                    gameState.mode == ChronoVaultMode.CHRONO_SEQUENCE -> "${target.orderIndex}"
                                    target.isGoldBonus -> "2X"
                                    else -> ""
                                }
                                if (badgeText.isNotEmpty()) {
                                    val measured = textMeasurer.measure(
                                        text = badgeText,
                                        style = TextStyle(
                                            color = ObsidianBg,
                                            fontSize = (cellSpan * 0.14f).coerceIn(13f, 24f).sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                    drawText(
                                        textLayoutResult = measured,
                                        topLeft = Offset(
                                            x + (cellSpan - measured.size.width) * 0.5f,
                                            y + (cellSpan - measured.size.height) * 0.5f
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Render golden & cyan burst particles
                    particles.forEach { p ->
                        val alpha = (1f - p.life / p.maxLife).coerceIn(0f, 1f)
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.radiusPx * (0.65f + 0.35f * alpha),
                            center = Offset(p.x, p.y)
                        )
                    }
                }

                // Game Over Frosted Obsidian Overlay + Instant Replay
                if (gameState.isGameOver) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0xE6080A0F))
                            .padding(20.dp)
                            .testTag("chrono_game_over_overlay"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "VAULT LOCKDOWN",
                                color = ArenaGold,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.6.sp
                            )
                            Text(
                                text = "Round ${gameState.round} • $formattedScore Pts",
                                color = DarkTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Best: R$effectiveBestRound ($formattedBestScore) • +$roundTotalXp XP • ${gameState.goldBonusHitCount} Gold Nodes",
                                color = ArenaCyan,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (!secondChanceUsedThisRun) {
                                ArenaRewardedButton(
                                    text = "Second Chance — Continue",
                                    onClick = {
                                        AdManager.showRewardedAd("chrono_second_chance") {
                                            gameState = ChronoVaultEngine.newRound(
                                                round = gameState.round,
                                                score = gameState.score,
                                                shields = 1,
                                                neuralReplayRemaining = gameState.neuralReplayRemaining,
                                                errorShieldRemaining = gameState.errorShieldRemaining,
                                                isErrorShieldArmed = gameState.isErrorShieldArmed,
                                                goldBonusHitCount = gameState.goldBonusHitCount
                                            )
                                            bestUpdatedOnFinish = false
                                            secondChanceUsedThisRun = true
                                        }
                                    },
                                    accentColor = ArenaGold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("chrono_rewarded_second_chance_button")
                                )
                            }

                            if (roundTotalXp > 0L) {
                                ArenaRewardedButton(
                                    text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                    enabled = !doubleXpClaimedThisRound,
                                    onClick = {
                                        AdManager.showRewardedAd("chrono_double_xp") {
                                            val bonus = roundTotalXp
                                            if (bonus > 0L) {
                                                onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                                roundTotalXp += bonus
                                            }
                                            doubleXpClaimedThisRound = true
                                        }
                                    },
                                    accentColor = ArenaCyan,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("chrono_double_xp_button")
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        AdManager.recordGameRoundCompleted("chrono_round_complete") {
                                            onSnapshotUpdated(
                                                ArenaProgressStore.updateBestChronoStatsIfHigher(context, gameState.score, gameState.round)
                                            )
                                            onExitToHub()
                                        }
                                    },
                                    border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.65f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("chrono_gameover_hub_button")
                                ) {
                                    Text("Hub", color = ArenaCyan, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        AdManager.recordGameRoundCompleted("chrono_round_complete") {
                                            gameState = ChronoVaultEngine.newGame()
                                            particles.clear()
                                            bestUpdatedOnFinish = false
                                            roundTotalXp = 0L
                                            secondChanceUsedThisRun = false
                                            doubleXpClaimedThisRound = false
                                            rewardedHintUsed = false
                                            onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ArenaGold,
                                        contentColor = ObsidianBg
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.35f)
                                        .height(48.dp)
                                        .testTag("chrono_play_again_button")
                                ) {
                                    Text("Play Again", fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (powerUpHookMessage != null) {
            Text(
                text = powerUpHookMessage ?: "",
                color = ArenaGold,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 4. Bottom Tactical HUD: (1) Neural Replay (1.5s), (2) Error Shield Forgive, (3) Restart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val canReFlashFree = gameState.neuralReplayRemaining > 0 && gameState.phase == ChronoPhase.RECALL && !gameState.isGameOver
            val canReFlashRewarded = gameState.neuralReplayRemaining <= 0 && !rewardedHintUsed && gameState.phase == ChronoPhase.RECALL && !gameState.isGameOver
            OutlinedButton(
                onClick = {
                    when {
                        canReFlashFree -> {
                            gameState = ChronoVaultEngine.activateNeuralReplay(gameState)
                            if (feedbackEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                ArenaSynthAudio.playChronoFlashPulse()
                            }
                        }
                        canReFlashRewarded -> {
                            AdManager.showRewardedAd("chrono_hint_reflash") {
                                gameState = gameState.copy(
                                    replayFlashRemainingMs = ChronoVaultEngine.REPLAY_FLASH_DURATION_MS
                                )
                                rewardedHintUsed = true
                                if (feedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    ArenaSynthAudio.playChronoFlashPulse()
                                }
                            }
                        }
                        rewardedHintUsed -> {
                            powerUpHookMessage = "Re-Flash hint already used this match"
                        }
                        else -> {
                            powerUpHookMessage = "Wait for Recall phase to trigger Re-Flash"
                        }
                    }
                },
                border = BorderStroke(
                    1.dp,
                    if (gameState.neuralReplayRemaining > 0 || canReFlashRewarded) ArenaCyan else DarkTextSecondary.copy(alpha = 0.35f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (gameState.neuralReplayRemaining > 0 || canReFlashRewarded) ArenaCyan else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("chrono_neural_replay_button")
            ) {
                Icon(
                    imageVector = if (canReFlashRewarded) Icons.Default.PlayCircle else Icons.Default.Refresh,
                    contentDescription = "Neural Replay 1.5s Re-Flash",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        gameState.replayFlashRemainingMs > 0L -> "Flashing..."
                        gameState.neuralReplayRemaining > 0 -> "Re-Flash (1)"
                        canReFlashRewarded -> "Get Free Hint"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = {
                    if (gameState.errorShieldRemaining > 0 && !gameState.isErrorShieldArmed && !gameState.isGameOver) {
                        gameState = ChronoVaultEngine.activateErrorShield(gameState)
                        if (feedbackEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            ArenaSynthAudio.playGoldMilestoneChime()
                        }
                    } else if (gameState.errorShieldRemaining <= 0 && !gameState.isErrorShieldArmed) {
                        powerUpHookMessage = "Error Shield Forgive used this match"
                    }
                },
                border = BorderStroke(
                    1.dp,
                    when {
                        gameState.isErrorShieldArmed -> ArenaGold
                        gameState.errorShieldRemaining > 0 -> ArenaGold.copy(alpha = 0.85f)
                        else -> DarkTextSecondary.copy(alpha = 0.35f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (gameState.isErrorShieldArmed) ArenaGold.copy(alpha = 0.16f) else Color.Transparent,
                    contentColor = if (gameState.errorShieldRemaining > 0 || gameState.isErrorShieldArmed) ArenaGold else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("chrono_error_shield_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Error Shield Forgive Power-Up",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        gameState.isErrorShieldArmed -> "Armed!"
                        gameState.errorShieldRemaining > 0 -> "Shield (1)"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    gameState = ChronoVaultEngine.newGame()
                    particles.clear()
                    bestUpdatedOnFinish = false
                    roundTotalXp = 0L
                    secondChanceUsedThisRun = false
                    doubleXpClaimedThisRound = false
                    rewardedHintUsed = false
                    onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaGold)
                    .testTag("chrono_restart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Restart Chrono-Vault Memory Matrix",
                    tint = ObsidianBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =============================================================================
// 15. Game 9: Gravity Orbit: Capital Protector (120fps 360° Orbital Reflex)
// =============================================================================

enum class OrbitEntityKind {
    DEBT_ASTEROID,
    YIELD_ORB,
    FEE_LASER_DART,
    GOLD_OVERDRIVE_STAR
}

enum class OrbitSparkColor {
    RED_CYAN,
    EMERALD_CYAN,
    GOLD_OVERDRIVE,
    CORE_DAMAGE,
    EMP_VAPORIZE
}

data class OrbitEntity(
    val id: Long,
    val kind: OrbitEntityKind,
    val angleRad: Float,
    val distance: Float,
    val speed: Float,
    val telegraphRemainingMs: Long = 0L,
    val spinOffset: Float = 0f
)

data class OrbitSparkEvent(
    val angleRad: Float,
    val distance: Float,
    val colorKind: OrbitSparkColor
)

data class OrbitStepResult(
    val nextState: GravityOrbitState,
    val pointsGained: Long = 0L,
    val xpGained: Long = 0L,
    val deflectedHazards: Int = 0,
    val absorbedYieldOrbs: Int = 0,
    val triggeredOverdrive: Boolean = false,
    val coreDamageHits: Int = 0,
    val newDartsSpawned: Int = 0,
    val waveAdvanced: Boolean = false,
    val sparks: List<OrbitSparkEvent> = emptyList()
)

data class GravityOrbitState(
    val shieldAngleRad: Float = (-PI * 0.5).toFloat(),
    val lives: Int = GravityOrbitEngine.INITIAL_LIVES,
    val score: Long = 0L,
    val wave: Int = 1,
    val waveElapsedMs: Long = 0L,
    val orbStreak: Int = 0,
    val multiplier: Int = 1,
    val overdriveRemainingMs: Long = 0L,
    val novaShieldRemainingMs: Long = 0L,
    val novaPowerUpRemaining: Int = 1,
    val empBlastPowerUpRemaining: Int = 1,
    val empShockwaveVisualRemainingMs: Long = 0L,
    val spawnCooldownMs: Long = 520L,
    val formationStep: Int = 0,
    val nextEntityId: Long = 100L,
    val entities: List<OrbitEntity> = emptyList(),
    val yieldOrbsAbsorbedCount: Int = 0,
    val hazardsDeflectedCount: Int = 0,
    val coreDamageFlashMs: Long = 0L,
    val isGameOver: Boolean = false
) {
    val isNovaShieldActive: Boolean get() = novaShieldRemainingMs > 0L
    val isOverdriveActive: Boolean get() = overdriveRemainingMs > 0L
    val effectiveScoreMultiplier: Int get() = multiplier * (if (isOverdriveActive) 2 else 1)
}

object GravityOrbitEngine {
    const val INITIAL_LIVES = 3
    const val CORE_RADIUS = 0.14f
    const val ORBIT_RADIUS = 0.27f
    const val SPAWN_RADIUS = 0.96f
    const val SHIELD_HALF_ARC_RAD = 0.48f
    const val WAVE_DURATION_MS = 20_000L
    const val OVERDRIVE_DURATION_MS = 5_000L
    const val NOVA_DURATION_MS = 5_000L
    const val EMP_VISUAL_DURATION_MS = 650L
    const val DART_TELEGRAPH_MS = 720L

    fun normalizeAngle(angle: Float): Float {
        val twoPi = (2.0 * PI).toFloat()
        var a = angle % twoPi
        if (a > PI.toFloat()) a -= twoPi
        if (a <= -PI.toFloat()) a += twoPi
        return a
    }

    fun angleDiffAbs(a: Float, b: Float): Float = abs(normalizeAngle(a - b))

    fun multiplierForOrbStreak(streak: Int): Int = when {
        streak >= 9 -> 5
        streak >= 5 -> 3
        streak >= 2 -> 2
        else -> 1
    }

    fun waveSpeedScale(wave: Int): Float =
        (1.0f + (wave - 1).coerceAtLeast(0) * 0.11f).coerceAtMost(2.15f)

    fun newGame(random: Random = Random.Default): GravityOrbitState {
        val baseAngle = random.nextFloat() * (2f * PI.toFloat()) - PI.toFloat()
        val initialEntities = listOf(
            OrbitEntity(
                id = 100L,
                kind = OrbitEntityKind.DEBT_ASTEROID,
                angleRad = normalizeAngle(baseAngle),
                distance = 0.88f,
                speed = 0.32f,
                spinOffset = 0.4f
            ),
            OrbitEntity(
                id = 101L,
                kind = OrbitEntityKind.YIELD_ORB,
                angleRad = normalizeAngle(baseAngle + PI.toFloat()),
                distance = 0.94f,
                speed = 0.27f,
                spinOffset = 1.1f
            )
        )
        return GravityOrbitState(
            entities = initialEntities,
            nextEntityId = 102L
        )
    }

    fun rotateShieldBy(state: GravityOrbitState, deltaRad: Float): GravityOrbitState {
        if (state.isGameOver) return state
        return state.copy(shieldAngleRad = normalizeAngle(state.shieldAngleRad + deltaRad))
    }

    fun setShieldAngle(state: GravityOrbitState, angleRad: Float): GravityOrbitState {
        if (state.isGameOver) return state
        return state.copy(shieldAngleRad = normalizeAngle(angleRad))
    }

    fun activateNovaShield(state: GravityOrbitState): GravityOrbitState {
        if (state.isGameOver || state.novaPowerUpRemaining <= 0 || state.isNovaShieldActive) {
            return state
        }
        return state.copy(
            novaPowerUpRemaining = state.novaPowerUpRemaining - 1,
            novaShieldRemainingMs = NOVA_DURATION_MS
        )
    }

    fun triggerEmpBlast(state: GravityOrbitState): Triple<GravityOrbitState, List<OrbitSparkEvent>, Long> {
        if (state.isGameOver || state.empBlastPowerUpRemaining <= 0) {
            return Triple(state, emptyList(), 0L)
        }
        val hazards = state.entities.filter {
            it.kind == OrbitEntityKind.DEBT_ASTEROID || it.kind == OrbitEntityKind.FEE_LASER_DART
        }
        val surviving = state.entities.filter {
            it.kind == OrbitEntityKind.YIELD_ORB || it.kind == OrbitEntityKind.GOLD_OVERDRIVE_STAR
        }
        val effMult = state.effectiveScoreMultiplier
        val pts = hazards.size * 45L * effMult
        val xp = (hazards.size * 3L).coerceAtLeast(if (hazards.isNotEmpty()) 5L else 0L)
        val sparks = hazards.map {
            OrbitSparkEvent(angleRad = it.angleRad, distance = it.distance, colorKind = OrbitSparkColor.EMP_VAPORIZE)
        }
        val next = state.copy(
            empBlastPowerUpRemaining = state.empBlastPowerUpRemaining - 1,
            empShockwaveVisualRemainingMs = EMP_VISUAL_DURATION_MS,
            entities = surviving,
            score = state.score + pts,
            hazardsDeflectedCount = state.hazardsDeflectedCount + hazards.size
        )
        return Triple(next, sparks, xp)
    }

    fun step(
        state: GravityOrbitState,
        dtSec: Float,
        random: Random = Random.Default
    ): OrbitStepResult {
        if (state.isGameOver || dtSec <= 0f) {
            return OrbitStepResult(state)
        }

        val dtMs = (dtSec * 1000f).roundToInt().toLong().coerceAtLeast(1L)
        var lives = state.lives
        var score = state.score
        var wave = state.wave
        var waveElapsedMs = state.waveElapsedMs + dtMs
        var waveAdvanced = false
        if (waveElapsedMs >= WAVE_DURATION_MS) {
            waveElapsedMs -= WAVE_DURATION_MS
            wave += 1
            waveAdvanced = true
        }

        var orbStreak = state.orbStreak
        var multiplier = state.multiplier
        var overdriveRem = (state.overdriveRemainingMs - dtMs).coerceAtLeast(0L)
        val novaRem = (state.novaShieldRemainingMs - dtMs).coerceAtLeast(0L)
        val empVisualRem = (state.empShockwaveVisualRemainingMs - dtMs).coerceAtLeast(0L)
        var coreFlashRem = (state.coreDamageFlashMs - dtMs).coerceAtLeast(0L)
        var spawnCooldown = (state.spawnCooldownMs - dtMs).coerceAtLeast(0L)
        var formationStep = state.formationStep
        var nextId = state.nextEntityId
        var orbsAbsorbedCount = state.yieldOrbsAbsorbedCount
        var hazardsDeflectedCount = state.hazardsDeflectedCount

        var pointsGained = 0L
        var xpGained = 0L
        var deflectedHazards = 0
        var absorbedYieldOrbs = 0
        var triggeredOverdrive = false
        var coreDamageHits = 0
        var newDartsSpawned = 0
        val sparks = mutableListOf<OrbitSparkEvent>()

        val novaActive = novaRem > 0L
        val updatedEntities = mutableListOf<OrbitEntity>()

        for (entity in state.entities) {
            if (entity.telegraphRemainingMs > 0L) {
                val remTel = (entity.telegraphRemainingMs - dtMs).coerceAtLeast(0L)
                updatedEntities.add(entity.copy(telegraphRemainingMs = remTel))
                continue
            }

            val prevDist = entity.distance
            val newDist = prevDist - entity.speed * dtSec
            val effMult = multiplier * (if (overdriveRem > 0L) 2 else 1)

            // 1. Check Orbital Shield Ring Crossing
            val crossedShieldBand = prevDist > ORBIT_RADIUS - 0.03f && newDist <= ORBIT_RADIUS + 0.035f
            if (crossedShieldBand) {
                val withinArc = angleDiffAbs(entity.angleRad, state.shieldAngleRad) <= SHIELD_HALF_ARC_RAD
                when (entity.kind) {
                    OrbitEntityKind.DEBT_ASTEROID,
                    OrbitEntityKind.FEE_LASER_DART -> {
                        if (novaActive || withinArc) {
                            val hitPts = (if (entity.kind == OrbitEntityKind.FEE_LASER_DART) 75L else 45L) * effMult
                            score += hitPts
                            pointsGained += hitPts
                            xpGained += 3L
                            deflectedHazards++
                            hazardsDeflectedCount++
                            sparks.add(OrbitSparkEvent(entity.angleRad, ORBIT_RADIUS, OrbitSparkColor.RED_CYAN))
                            continue
                        }
                    }
                    OrbitEntityKind.YIELD_ORB,
                    OrbitEntityKind.GOLD_OVERDRIVE_STAR -> {
                        // Beneficial orbs pass cleanly through 360° Nova Dome, but normal shield blocks them if in the way
                        if (!novaActive && withinArc) {
                            orbStreak = 0
                            multiplier = 1
                            sparks.add(OrbitSparkEvent(entity.angleRad, ORBIT_RADIUS, OrbitSparkColor.EMERALD_CYAN))
                            continue
                        }
                    }
                }
            }

            // 2. Check Central 24K Gold Capital Core Arrival
            if (newDist <= CORE_RADIUS) {
                when (entity.kind) {
                    OrbitEntityKind.DEBT_ASTEROID,
                    OrbitEntityKind.FEE_LASER_DART -> {
                        lives = (lives - 1).coerceAtLeast(0)
                        orbStreak = 0
                        multiplier = 1
                        coreDamageHits++
                        coreFlashRem = 360L
                        sparks.add(OrbitSparkEvent(entity.angleRad, CORE_RADIUS, OrbitSparkColor.CORE_DAMAGE))
                        continue
                    }
                    OrbitEntityKind.YIELD_ORB -> {
                        orbStreak += 1
                        multiplier = multiplierForOrbStreak(orbStreak)
                        val orbEffMult = multiplier * (if (overdriveRem > 0L) 2 else 1)
                        val pts = 90L * orbEffMult
                        score += pts
                        pointsGained += pts
                        xpGained += 5L
                        absorbedYieldOrbs++
                        orbsAbsorbedCount++
                        sparks.add(OrbitSparkEvent(entity.angleRad, CORE_RADIUS, OrbitSparkColor.EMERALD_CYAN))
                        continue
                    }
                    OrbitEntityKind.GOLD_OVERDRIVE_STAR -> {
                        overdriveRem = OVERDRIVE_DURATION_MS
                        val starPts = 160L * (multiplier * 2)
                        score += starPts
                        pointsGained += starPts
                        xpGained += 12L
                        triggeredOverdrive = true
                        sparks.add(OrbitSparkEvent(entity.angleRad, CORE_RADIUS, OrbitSparkColor.GOLD_OVERDRIVE))
                        continue
                    }
                }
            }

            updatedEntities.add(
                entity.copy(
                    distance = newDist,
                    spinOffset = entity.spinOffset + dtSec * 2.8f
                )
            )
        }

        // 3. Infinite Procedural Wave Formation Spawner (Zero-boredom escalating patterns)
        if (lives > 0 && spawnCooldown <= 0L) {
            val scale = waveSpeedScale(wave)
            val baseAngle = normalizeAngle(random.nextFloat() * (2f * PI.toFloat()) - PI.toFloat())
            val slot = formationStep % 6
            formationStep += 1

            when (slot) {
                0 -> {
                    // Single or paired Debt Asteroid + opposite Emerald Yield Orb window
                    updatedEntities.add(
                        OrbitEntity(
                            id = nextId++,
                            kind = OrbitEntityKind.DEBT_ASTEROID,
                            angleRad = baseAngle,
                            distance = SPAWN_RADIUS,
                            speed = 0.31f * scale,
                            spinOffset = random.nextFloat() * 6f
                        )
                    )
                    updatedEntities.add(
                        OrbitEntity(
                            id = nextId++,
                            kind = OrbitEntityKind.YIELD_ORB,
                            angleRad = normalizeAngle(baseAngle + PI.toFloat() * 0.85f),
                            distance = SPAWN_RADIUS + 0.06f,
                            speed = 0.27f * scale,
                            spinOffset = random.nextFloat() * 6f
                        )
                    )
                }
                1 -> {
                    // Rapid Fee Laser Dart with warning trajectory telegraph
                    updatedEntities.add(
                        OrbitEntity(
                            id = nextId++,
                            kind = OrbitEntityKind.FEE_LASER_DART,
                            angleRad = baseAngle,
                            distance = SPAWN_RADIUS,
                            speed = 0.72f * scale,
                            telegraphRemainingMs = (DART_TELEGRAPH_MS - (wave - 1) * 28L).coerceIn(420L, DART_TELEGRAPH_MS),
                            spinOffset = 0f
                        )
                    )
                    newDartsSpawned += 1
                    if (wave >= 3) {
                        updatedEntities.add(
                            OrbitEntity(
                                id = nextId++,
                                kind = OrbitEntityKind.DEBT_ASTEROID,
                                angleRad = normalizeAngle(baseAngle + 1.35f),
                                distance = SPAWN_RADIUS,
                                speed = 0.33f * scale,
                                spinOffset = random.nextFloat() * 6f
                            )
                        )
                    }
                }
                2 -> {
                    // Spiral Debt Volley (2–3 asteroids separated cleanly in angle)
                    val burstCount = if (wave >= 4) 3 else 2
                    for (i in 0 until burstCount) {
                        updatedEntities.add(
                            OrbitEntity(
                                id = nextId++,
                                kind = OrbitEntityKind.DEBT_ASTEROID,
                                angleRad = normalizeAngle(baseAngle + i * 1.15f),
                                distance = SPAWN_RADIUS + i * 0.08f,
                                speed = 0.30f * scale,
                                spinOffset = random.nextFloat() * 6f
                            )
                        )
                    }
                }
                3 -> {
                    // Yield Orb Duo + flank hazard
                    updatedEntities.add(
                        OrbitEntity(
                            id = nextId++,
                            kind = OrbitEntityKind.YIELD_ORB,
                            angleRad = baseAngle,
                            distance = SPAWN_RADIUS,
                            speed = 0.28f * scale,
                            spinOffset = random.nextFloat() * 6f
                        )
                    )
                    updatedEntities.add(
                        OrbitEntity(
                            id = nextId++,
                            kind = OrbitEntityKind.DEBT_ASTEROID,
                            angleRad = normalizeAngle(baseAngle + 1.75f),
                            distance = SPAWN_RADIUS + 0.05f,
                            speed = 0.34f * scale,
                            spinOffset = random.nextFloat() * 6f
                        )
                    )
                }
                4 -> {
                    // Golden Overdrive Star (periodic trigger) + Dart escort on higher waves
                    updatedEntities.add(
                        OrbitEntity(
                            id = nextId++,
                            kind = OrbitEntityKind.GOLD_OVERDRIVE_STAR,
                            angleRad = baseAngle,
                            distance = SPAWN_RADIUS,
                            speed = 0.29f * scale,
                            spinOffset = random.nextFloat() * 6f
                        )
                    )
                    updatedEntities.add(
                        OrbitEntity(
                            id = nextId++,
                            kind = OrbitEntityKind.DEBT_ASTEROID,
                            angleRad = normalizeAngle(baseAngle - 1.9f),
                            distance = SPAWN_RADIUS + 0.04f,
                            speed = 0.32f * scale,
                            spinOffset = random.nextFloat() * 6f
                        )
                    )
                }
                else -> {
                    // Opposing Pincer Hazards + optional dual Laser Dart on wave 2+
                    updatedEntities.add(
                        OrbitEntity(
                            id = nextId++,
                            kind = OrbitEntityKind.DEBT_ASTEROID,
                            angleRad = baseAngle,
                            distance = SPAWN_RADIUS,
                            speed = 0.33f * scale,
                            spinOffset = random.nextFloat() * 6f
                        )
                    )
                    if (wave >= 2) {
                        updatedEntities.add(
                            OrbitEntity(
                                id = nextId++,
                                kind = OrbitEntityKind.FEE_LASER_DART,
                                angleRad = normalizeAngle(baseAngle + PI.toFloat()),
                                distance = SPAWN_RADIUS,
                                speed = 0.74f * scale,
                                telegraphRemainingMs = (DART_TELEGRAPH_MS - (wave - 1) * 25L).coerceIn(420L, DART_TELEGRAPH_MS),
                                spinOffset = 0f
                            )
                        )
                        newDartsSpawned += 1
                    }
                }
            }

            spawnCooldown = (1_420L - (wave - 1) * 95L).coerceIn(580L, 1_420L)
        }

        val gameOver = lives <= 0
        val nextState = state.copy(
            lives = lives,
            score = score,
            wave = wave,
            waveElapsedMs = waveElapsedMs,
            orbStreak = orbStreak,
            multiplier = multiplier,
            overdriveRemainingMs = overdriveRem,
            novaShieldRemainingMs = novaRem,
            empShockwaveVisualRemainingMs = empVisualRem,
            spawnCooldownMs = spawnCooldown,
            formationStep = formationStep,
            nextEntityId = nextId,
            entities = updatedEntities,
            yieldOrbsAbsorbedCount = orbsAbsorbedCount,
            hazardsDeflectedCount = hazardsDeflectedCount,
            coreDamageFlashMs = coreFlashRem,
            isGameOver = gameOver
        )
        return OrbitStepResult(
            nextState = nextState,
            pointsGained = pointsGained,
            xpGained = xpGained,
            deflectedHazards = deflectedHazards,
            absorbedYieldOrbs = absorbedYieldOrbs,
            triggeredOverdrive = triggeredOverdrive,
            coreDamageHits = coreDamageHits,
            newDartsSpawned = newDartsSpawned,
            waveAdvanced = waveAdvanced,
            sparks = sparks
        )
    }
}

private data class OrbitParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val radiusPx: Float,
    val color: Color
)

@Composable
private fun OrbitProtectorHeroBannerCard(
    spec: ArenaGameSpec,
    snapshot: ArenaSnapshot,
    onUnlockFlashShown: () -> Unit,
    onPlayClick: () -> Unit
) {
    val isUnlocked = snapshot.isGame9Unlocked
    val progressDays = snapshot.effectiveStreakDays.coerceIn(0, ArenaProgressStore.GAME9_REQUIRED_STREAK_DAYS)
    val formattedBest = remember(snapshot.bestOrbitScore) {
        NumberFormat.getNumberInstance(Locale.US).format(snapshot.bestOrbitScore)
    }

    var unlockBannerVisible by remember(snapshot.shouldShowGame9UnlockFlash) {
        mutableStateOf(snapshot.shouldShowGame9UnlockFlash)
    }
    val unlockFlashAlpha = remember { Animatable(0f) }

    LaunchedEffect(snapshot.shouldShowGame9UnlockFlash) {
        if (snapshot.shouldShowGame9UnlockFlash) {
            unlockBannerVisible = true
            unlockFlashAlpha.snapTo(0.95f)
            unlockFlashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing)
            )
            delay(1400L)
            unlockBannerVisible = false
            onUnlockFlashShown()
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B0C10),
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.linearGradient(
                colors = if (isUnlocked) {
                    listOf(ArenaCyan.copy(alpha = 0.92f), ArenaGold.copy(alpha = 0.88f), ArenaCyan.copy(alpha = 0.78f))
                } else {
                    listOf(ArenaCyan.copy(alpha = 0.55f), ArenaGold.copy(alpha = 0.50f))
                }
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_9")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF061322),
                            ObsidianBg,
                            Color(0xFF171107)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.height * 0.45f, size.height * 0.5f),
                        radius = size.height * 0.85f
                    ),
                    radius = size.height * 0.85f,
                    center = Offset(size.height * 0.45f, size.height * 0.5f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.24f),
                        radius = size.height * 0.75f
                    ),
                    radius = size.height * 0.75f,
                    center = Offset(size.width * 0.88f, size.height * 0.24f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OrbitProtectorLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("orbit_protector_logo_canvas")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.65f)),
                        modifier = Modifier.testTag("hero_orbit_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Gravity Orbit Stats",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: $formattedBest • W${snapshot.bestOrbitWave}",
                                color = ArenaGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        enabled = isUnlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaCyan,
                            contentColor = ObsidianBg,
                            disabledContainerColor = Color(0xFF1F2937),
                            disabledContentColor = DarkTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_orbit_play_button")
                    ) {
                        Text(
                            text = if (isUnlocked) "PLAY ▶" else "LOCKED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Frosted Glass 14-Day Streak Lock Overlay when locked
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xDD0B0C10),
                                    Color(0xEB0B1726),
                                    Color(0xDD0B0C10)
                                )
                            )
                        )
                        .border(1.2.dp, ArenaCyan.copy(alpha = 0.52f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                        .testTag("orbit_streak14_lock_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.78f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked by 14-Day Streak",
                                    tint = ArenaGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "🔒 Unlock at 14-Day Streak • $progressDays/14 Days",
                                    color = ArenaGold,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progressDays / 14f },
                            color = ArenaCyan,
                            trackColor = Color(0xFF1F2937),
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )

                        Text(
                            text = "Keep logging daily finance discipline for 14 days to unlock Gravity Orbit forever!",
                            color = DarkTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (unlockFlashAlpha.value > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(ArenaGold.copy(alpha = unlockFlashAlpha.value * 0.50f))
                )
            }
            AnimatedVisibility(
                visible = unlockBannerVisible && isUnlocked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f))
                ) {
                    Text(
                        text = "✨ 14-DAY STREAK UNLOCKED!",
                        color = ObsidianBg,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrbitProtectorLogoArtwork(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbitLogoAnim")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = -135f,
        targetValue = 225f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitalArcSpin"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Gravity Orbit glowing 24K gold planet core surrounded by cyan orbital laser arc shield and incoming asteroids emblem"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF071122), Color(0xFF141108))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaCyan.copy(alpha = 0.90f), ArenaGold.copy(alpha = 0.90f))),
            cornerRadius = corner,
            style = Stroke(width = 1.5.dp.toPx())
        )

        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.5f)
        val orbitR = w * 0.30f

        // Deep-Space Subtle Star Points
        listOf(
            Offset(w * 0.16f, h * 0.18f),
            Offset(w * 0.82f, h * 0.22f),
            Offset(w * 0.20f, h * 0.82f),
            Offset(w * 0.84f, h * 0.78f)
        ).forEach { pt ->
            drawCircle(color = Color.White.copy(alpha = 0.55f), radius = 1.4.dp.toPx(), center = pt)
        }

        // Orbit Guide Ring
        drawCircle(
            color = ArenaCyan.copy(alpha = 0.22f),
            radius = orbitR,
            center = center,
            style = Stroke(
                width = 1.2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
            )
        )

        // Glowing Electric Cyan Orbital Laser Arc Shield
        val arcTopLeft = Offset(center.x - orbitR, center.y - orbitR)
        val arcSize = Size(orbitR * 2f, orbitR * 2f)
        drawArc(
            color = ArenaCyan.copy(alpha = 0.28f),
            startAngle = sweepAngle,
            sweepAngle = 78f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = ArenaCyan,
            startAngle = sweepAngle,
            sweepAngle = 78f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // 24K Gold Planet Capital Core
        drawCircle(
            color = ArenaGold.copy(alpha = 0.25f),
            radius = w * 0.20f,
            center = center
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFF8CC), ArenaGold, Color(0xFFB45309)),
                center = Offset(center.x - 3.dp.toPx(), center.y - 3.dp.toPx()),
                radius = w * 0.16f
            ),
            radius = w * 0.15f,
            center = center
        )

        // Incoming Neon Red Debt Asteroids
        val ast1 = Offset(w * 0.22f, h * 0.26f)
        val ast2 = Offset(w * 0.78f, h * 0.68f)
        listOf(ast1, ast2).forEach { ast ->
            drawCircle(color = RunnerTrapRed.copy(alpha = 0.35f), radius = 7.dp.toPx(), center = ast)
            drawCircle(color = RunnerTrapRed, radius = 4.2.dp.toPx(), center = ast)
        }
    }
}

@Composable
fun GravityOrbitProtectorGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var gameState by remember { mutableStateOf(GravityOrbitEngine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var powerUpHookMessage by remember { mutableStateOf<String?>(null) }
    var bestUpdatedOnFinish by remember { mutableStateOf(false) }
    var roundTotalXp by remember { mutableLongStateOf(0L) }
    var secondChanceUsedThisRun by remember { mutableStateOf(false) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }
    var orbitSpinPhase by remember { mutableFloatStateOf(0f) }
    var canvasPixelSize by remember { mutableStateOf(Size.Zero) }

    val particles = remember { mutableStateListOf<OrbitParticle>() }
    val stars = remember {
        val rng = Random(901)
        List(44) {
            Triple(rng.nextFloat(), rng.nextFloat(), 0.7f + rng.nextFloat() * 1.9f)
        }
    }

    LaunchedEffect(powerUpHookMessage) {
        if (powerUpHookMessage != null) {
            delay(2100L)
            powerUpHookMessage = null
        }
    }

    // Main 120fps Physics, Orbital Entities & Particle Frame Loop
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) return@LaunchedEffect
        var lastNs = 0L
        while (!gameState.isGameOver) {
            withFrameNanos { frameNs ->
                if (lastNs == 0L) {
                    lastNs = frameNs
                    return@withFrameNanos
                }
                val dtSec = ((frameNs - lastNs).coerceAtMost(35_000_000L)) / 1_000_000_000f
                lastNs = frameNs

                orbitSpinPhase = (orbitSpinPhase + dtSec) % 360f
                val stepRes = GravityOrbitEngine.step(gameState, dtSec)
                gameState = stepRes.nextState

                val cw = canvasPixelSize.width
                val ch = canvasPixelSize.height
                val minDim = min(cw, ch)
                val maxR = minDim * 0.45f
                val cxCenter = cw * 0.5f
                val cyCenter = ch * 0.5f

                if (stepRes.deflectedHazards > 0 && feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    ArenaSynthAudio.playOrbitLaserDeflect(stepRes.nextState.orbStreak + 1)
                }
                if (stepRes.absorbedYieldOrbs > 0 && feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    ArenaSynthAudio.playOrbitYieldAbsorb(stepRes.nextState.multiplier)
                }
                if (stepRes.triggeredOverdrive && feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    ArenaSynthAudio.playOrbitOverdriveChime()
                }
                if (stepRes.newDartsSpawned > 0 && feedbackEnabled) {
                    ArenaSynthAudio.playOrbitLaserWarningPing()
                }
                if (stepRes.coreDamageHits > 0 && feedbackEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    ArenaSynthAudio.playOrbitCoreHitDamage()
                }

                if (stepRes.sparks.isNotEmpty() && minDim > 1f) {
                    stepRes.sparks.forEach { ev ->
                        val ex = cxCenter + cos(ev.angleRad) * (ev.distance * maxR)
                        val ey = cyCenter + sin(ev.angleRad) * (ev.distance * maxR)
                        val count = when (ev.colorKind) {
                            OrbitSparkColor.GOLD_OVERDRIVE,
                            OrbitSparkColor.CORE_DAMAGE -> 14
                            OrbitSparkColor.EMP_VAPORIZE -> 12
                            else -> 8
                        }
                        repeat(count) { idx ->
                            val ang = (idx / count.toDouble()) * 2.0 * PI + Random.nextFloat() * 0.45f
                            val spd = 95f + Random.nextFloat() * 185f
                            val pColor = when (ev.colorKind) {
                                OrbitSparkColor.RED_CYAN -> if (idx % 2 == 0) RunnerTrapRed else ArenaCyan
                                OrbitSparkColor.EMERALD_CYAN -> if (idx % 2 == 0) ArenaCyan else Color.White
                                OrbitSparkColor.GOLD_OVERDRIVE -> ArenaGold
                                OrbitSparkColor.CORE_DAMAGE -> RunnerTrapRed
                                OrbitSparkColor.EMP_VAPORIZE -> if (idx % 2 == 0) ArenaCyan else RunnerTrapRed
                            }
                            particles.add(
                                OrbitParticle(
                                    x = ex,
                                    y = ey,
                                    vx = (cos(ang) * spd).toFloat(),
                                    vy = (sin(ang) * spd).toFloat(),
                                    life = 0f,
                                    maxLife = 0.48f,
                                    radiusPx = 3.2f + Random.nextFloat() * 3.0f,
                                    color = pColor
                                )
                            )
                        }
                    }
                }

                if (stepRes.xpGained > 0L || stepRes.waveAdvanced) {
                    roundTotalXp += stepRes.xpGained
                    var snap = ArenaProgressStore.updateBestOrbitStatsIfHigher(
                        context,
                        stepRes.nextState.score,
                        stepRes.nextState.wave
                    )
                    if (stepRes.xpGained > 0L) {
                        snap = ArenaProgressStore.awardXpAndTouchStreak(context, stepRes.xpGained)
                    }
                    onSnapshotUpdated(snap)
                }

                val pIt = particles.listIterator()
                while (pIt.hasNext()) {
                    val p = pIt.next()
                    p.life += dtSec
                    if (p.life >= p.maxLife) {
                        pIt.remove()
                    } else {
                        p.x += p.vx * dtSec
                        p.y += p.vy * dtSec
                    }
                }
            }
        }
    }

    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver && !bestUpdatedOnFinish) {
            bestUpdatedOnFinish = true
            onSnapshotUpdated(
                ArenaProgressStore.updateBestOrbitStatsIfHigher(context, gameState.score, gameState.wave)
            )
        }
    }

    val effectiveBestScore = max(snapshot.bestOrbitScore, gameState.score)
    val effectiveBestWave = max(snapshot.bestOrbitWave, gameState.wave)
    val formattedScore = remember(gameState.score) {
        NumberFormat.getNumberInstance(Locale.US).format(gameState.score)
    }
    val formattedBest = remember(effectiveBestScore) {
        NumberFormat.getNumberInstance(Locale.US).format(effectiveBestScore)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .navigationBarsPadding()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Top Navigation & Sound Mute/Unmute Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        onSnapshotUpdated(
                            ArenaProgressStore.updateBestOrbitStatsIfHigher(context, gameState.score, gameState.wave)
                        )
                        onExitToHub()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("game_orbit_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Gravity Orbit",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val badgeColor = when {
                        gameState.isOverdriveActive -> ArenaGold
                        gameState.isNovaShieldActive -> ArenaCyan
                        else -> ArenaCyan
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = badgeColor.copy(alpha = 0.14f),
                        border = BorderStroke(0.75.dp, badgeColor.copy(alpha = 0.52f))
                    ) {
                        val badgeLabel = when {
                            gameState.isOverdriveActive ->
                                "⭐ GOLD OVERDRIVE 2X • ${kotlin.math.ceil(gameState.overdriveRemainingMs / 1000.0).toInt()}s"
                            gameState.isNovaShieldActive ->
                                "🛡 NOVA 360° DOME • ${kotlin.math.ceil(gameState.novaShieldRemainingMs / 1000.0).toInt()}s"
                            else ->
                                "WAVE ${gameState.wave} • LVL ${snapshot.levelInfo.level}"
                        }
                        Text(
                            text = badgeLabel,
                            color = badgeColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    feedbackEnabled = !feedbackEnabled
                    onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ObsidianElevated)
                    .testTag("orbit_sound_toggle_button")
            ) {
                Icon(
                    imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (feedbackEnabled) "Mute Game Audio" else "Unmute Game Audio",
                    tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary
                )
            }
        }

        // 2. Top 4 Glass HUD Pills: SCORE, CORE SHIELD (3 Lives), MULTIPLIER, ALL-TIME BEST
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TowerHudPill(
                label = "SCORE",
                value = formattedScore,
                accentColor = ArenaCyan,
                modifier = Modifier
                    .weight(1.1f)
                    .testTag("orbit_score_pill")
            )
            TowerHudPill(
                label = "CORE",
                value = "🛡 ${gameState.lives}",
                accentColor = if (gameState.lives <= 1) RunnerTrapRed else ArenaGold,
                modifier = Modifier
                    .weight(0.82f)
                    .testTag("orbit_lives_pill")
            )
            TowerHudPill(
                label = "MULT",
                value = "x${gameState.effectiveScoreMultiplier}",
                accentColor = if (gameState.isOverdriveActive) ArenaGold else ArenaCyan,
                modifier = Modifier
                    .weight(0.80f)
                    .testTag("orbit_multiplier_pill")
            )
            TowerHudPill(
                label = "BEST",
                value = "W$effectiveBestWave",
                accentColor = ArenaGold,
                modifier = Modifier
                    .weight(0.85f)
                    .testTag("orbit_best_pill")
            )
        }

        // 3. Deep-Space 120fps Interactive 360° Orbital Playfield Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .widthIn(max = 600.dp)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0B1426),
                            Color(0xFF060912),
                            Color(0xFF040508)
                        )
                    )
                )
                .border(
                    width = 1.6.dp,
                    brush = Brush.linearGradient(
                        colors = if (gameState.isOverdriveActive) {
                            listOf(ArenaGold, Color.White.copy(alpha = 0.7f), ArenaGold)
                        } else {
                            listOf(ArenaCyan.copy(alpha = 0.68f), ArenaGold.copy(alpha = 0.58f))
                        }
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
        ) {
            val currentGameState by rememberUpdatedState(gameState)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coords ->
                        canvasPixelSize = Size(
                            coords.size.width.toFloat(),
                            coords.size.height.toFloat()
                        )
                    }
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            if (currentGameState.isGameOver) return@awaitEachGesture
                            val cxCenter = size.width * 0.5f
                            val cyCenter = size.height * 0.5f
                            var prevPos = down.position
                            var prevAng = atan2(prevPos.y - cyCenter, prevPos.x - cxCenter)
                            // If tap is far enough from center and clean single tap, subtly orient shield toward tap quadrant
                            val tapDist = hypot(prevPos.x - cxCenter, prevPos.y - cyCenter)
                            if (tapDist > 36f) {
                                gameState = GravityOrbitEngine.setShieldAngle(currentGameState, prevAng)
                            }

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) break
                                val curPos = change.position
                                val curDist = hypot(curPos.x - cxCenter, curPos.y - cyCenter)
                                if (curDist > 24f) {
                                    val curAng = atan2(curPos.y - cyCenter, curPos.x - cxCenter)
                                    val delta = GravityOrbitEngine.normalizeAngle(curAng - prevAng)
                                    gameState = GravityOrbitEngine.rotateShieldBy(currentGameState, delta)
                                    prevAng = curAng
                                } else {
                                    val dx = curPos.x - prevPos.x
                                    val swDelta = (dx / size.width.toFloat().coerceAtLeast(1f)) * (2f * PI.toFloat())
                                    gameState = GravityOrbitEngine.rotateShieldBy(currentGameState, swDelta)
                                }
                                prevPos = curPos
                                change.consume()
                            }
                        }
                    }
                    .semantics {
                        contentDescription = "Gravity Orbit 360 degree deep space shield and capital core arena"
                    }
                    .testTag("gravity_orbit_canvas")
            ) {
                val cw = size.width
                val ch = size.height
                val minDim = min(cw, ch)
                val maxR = minDim * 0.45f
                val center = Offset(cw * 0.5f, ch * 0.5f)
                val corePxR = GravityOrbitEngine.CORE_RADIUS * maxR
                val orbitPxR = GravityOrbitEngine.ORBIT_RADIUS * maxR

                // Twinkling Deep-Space Cyber Starfield
                stars.forEachIndexed { idx, (sx, sy, sr) ->
                    val twinkle = 0.35f + 0.55f * abs(sin(orbitSpinPhase * 1.4f + idx * 0.7f))
                    drawCircle(
                        color = (if (idx % 5 == 0) ArenaCyan else Color.White).copy(alpha = twinkle),
                        radius = sr.dp.toPx() * 0.7f,
                        center = Offset(sx * cw, sy * ch)
                    )
                }

                // Concentric Radar Grid Rings
                listOf(0.46f, 0.72f, 0.96f).forEach { ringRatio ->
                    drawCircle(
                        color = ArenaCyan.copy(alpha = 0.08f),
                        radius = ringRatio * maxR,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Core Damage Shock Flash
                if (gameState.coreDamageFlashMs > 0L) {
                    val flashAlpha = (gameState.coreDamageFlashMs / 360f).coerceIn(0f, 1f)
                    drawCircle(
                        color = RunnerTrapRed.copy(alpha = flashAlpha * 0.32f),
                        radius = orbitPxR * 1.5f,
                        center = center
                    )
                }

                // Gold Overdrive Aura Ring
                if (gameState.isOverdriveActive) {
                    val pulse = 0.72f + 0.28f * sin(orbitSpinPhase * 8f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ArenaGold.copy(alpha = 0.36f * pulse), Color.Transparent),
                            center = center,
                            radius = orbitPxR * 1.55f
                        ),
                        radius = orbitPxR * 1.55f,
                        center = center
                    )
                }

                // EMP Shockwave Expanding Pulse Ring
                if (gameState.empShockwaveVisualRemainingMs > 0L) {
                    val progress = (1f - gameState.empShockwaveVisualRemainingMs / GravityOrbitEngine.EMP_VISUAL_DURATION_MS.toFloat()).coerceIn(0f, 1f)
                    val empRadius = (GravityOrbitEngine.CORE_RADIUS + progress * 0.95f) * maxR
                    drawCircle(
                        color = ArenaCyan.copy(alpha = (1f - progress) * 0.85f),
                        radius = empRadius,
                        center = center,
                        style = Stroke(width = (8f * (1f - progress) + 2f).dp.toPx())
                    )
                }

                // Incoming Entities & Laser Trajectory Warning Lines
                gameState.entities.forEach { entity ->
                    val cosA = cos(entity.angleRad)
                    val sinA = sin(entity.angleRad)
                    val ex = center.x + cosA * (entity.distance * maxR)
                    val ey = center.y + sinA * (entity.distance * maxR)
                    val entPos = Offset(ex, ey)

                    if (entity.kind == OrbitEntityKind.FEE_LASER_DART && entity.telegraphRemainingMs > 0L) {
                        val warnPulse = 0.4f + 0.6f * abs(sin(orbitSpinPhase * 16f))
                        drawLine(
                            color = RunnerTrapRed.copy(alpha = 0.55f * warnPulse),
                            start = Offset(center.x + cosA * corePxR, center.y + sinA * corePxR),
                            end = Offset(center.x + cosA * (GravityOrbitEngine.SPAWN_RADIUS * maxR), center.y + sinA * (GravityOrbitEngine.SPAWN_RADIUS * maxR)),
                            strokeWidth = 1.6.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                        )
                    }

                    when (entity.kind) {
                        OrbitEntityKind.DEBT_ASTEROID -> {
                            val rPx = 11.dp.toPx()
                            drawCircle(
                                color = RunnerTrapRed.copy(alpha = 0.28f),
                                radius = rPx * 1.45f,
                                center = entPos
                            )
                            val poly = Path().apply {
                                val pts = 7
                                for (i in 0 until pts) {
                                    val a = entity.spinOffset + (i / pts.toFloat()) * (2f * PI.toFloat())
                                    val spike = if (i % 2 == 0) rPx else rPx * 0.68f
                                    val px = ex + cos(a) * spike
                                    val py = ey + sin(a) * spike
                                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                                }
                                close()
                            }
                            drawPath(
                                path = poly,
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFF99B3), RunnerTrapRed, Color(0xFF881337)),
                                    center = entPos,
                                    radius = rPx
                                )
                            )
                            drawPath(
                                path = poly,
                                color = Color.White.copy(alpha = 0.72f),
                                style = Stroke(width = 1.2.dp.toPx(), join = StrokeJoin.Round)
                            )
                        }
                        OrbitEntityKind.YIELD_ORB -> {
                            val rPx = 10.dp.toPx()
                            drawCircle(
                                color = ArenaCyan.copy(alpha = 0.32f),
                                radius = rPx * 1.55f,
                                center = entPos
                            )
                            val diamond = Path().apply {
                                moveTo(ex, ey - rPx)
                                lineTo(ex + rPx * 0.85f, ey)
                                lineTo(ex, ey + rPx)
                                lineTo(ex - rPx * 0.85f, ey)
                                close()
                            }
                            drawPath(
                                path = diamond,
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.White, ArenaCyan, Color(0xFF047857)),
                                    center = entPos,
                                    radius = rPx
                                )
                            )
                            drawPath(
                                path = diamond,
                                color = Color.White.copy(alpha = 0.88f),
                                style = Stroke(width = 1.2.dp.toPx())
                            )
                        }
                        OrbitEntityKind.FEE_LASER_DART -> {
                            val tailX = ex + cosA * 18.dp.toPx()
                            val tailY = ey + sinA * 18.dp.toPx()
                            val tipX = ex - cosA * 10.dp.toPx()
                            val tipY = ey - sinA * 10.dp.toPx()
                            drawLine(
                                brush = Brush.linearGradient(
                                    colors = listOf(RunnerTrapRed.copy(alpha = 0.2f), Color(0xFFFF6B8B), Color.White),
                                    start = Offset(tailX, tailY),
                                    end = Offset(tipX, tipY)
                                ),
                                start = Offset(tailX, tailY),
                                end = Offset(tipX, tipY),
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                        OrbitEntityKind.GOLD_OVERDRIVE_STAR -> {
                            val rPx = 12.5.dp.toPx()
                            drawCircle(
                                color = ArenaGold.copy(alpha = 0.38f),
                                radius = rPx * 1.65f,
                                center = entPos
                            )
                            val starPath = Path().apply {
                                val pts = 10
                                for (i in 0 until pts) {
                                    val a = entity.spinOffset + (i / pts.toFloat()) * (2f * PI.toFloat())
                                    val rad = if (i % 2 == 0) rPx else rPx * 0.45f
                                    val px = ex + cos(a) * rad
                                    val py = ey + sin(a) * rad
                                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                                }
                                close()
                            }
                            drawPath(
                                path = starPath,
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFF8CC), ArenaGold, Color(0xFFD97706)),
                                    center = entPos,
                                    radius = rPx
                                )
                            )
                            drawPath(
                                path = starPath,
                                color = Color.White,
                                style = Stroke(width = 1.1.dp.toPx())
                            )
                        }
                    }
                }

                // Orbital Dashed Guide Ring
                drawCircle(
                    color = ArenaCyan.copy(alpha = 0.25f),
                    radius = orbitPxR,
                    center = center,
                    style = Stroke(
                        width = 1.3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )
                )

                // Electric Cyan 360° Laser Arc Shield (or 360° Nova Full-Circle Protective Dome)
                val arcTl = Offset(center.x - orbitPxR, center.y - orbitPxR)
                val arcSz = Size(orbitPxR * 2f, orbitPxR * 2f)
                if (gameState.isNovaShieldActive) {
                    drawCircle(
                        color = ArenaCyan.copy(alpha = 0.22f),
                        radius = orbitPxR,
                        center = center,
                        style = Stroke(width = 16.dp.toPx())
                    )
                    drawCircle(
                        color = ArenaCyan,
                        radius = orbitPxR,
                        center = center,
                        style = Stroke(width = 5.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = orbitPxR,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                } else {
                    val halfDeg = (GravityOrbitEngine.SHIELD_HALF_ARC_RAD * 180.0 / PI).toFloat()
                    val centerDeg = (gameState.shieldAngleRad * 180.0 / PI).toFloat()
                    val startDeg = centerDeg - halfDeg
                    val sweepDeg = halfDeg * 2f

                    drawArc(
                        color = ArenaCyan.copy(alpha = 0.28f),
                        startAngle = startDeg,
                        sweepAngle = sweepDeg,
                        useCenter = false,
                        topLeft = arcTl,
                        size = arcSz,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(ArenaCyan, Color.White, ArenaCyan),
                            center = center
                        ),
                        startAngle = startDeg,
                        sweepAngle = sweepDeg,
                        useCenter = false,
                        topLeft = arcTl,
                        size = arcSz,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Rotating 24K Gold Capital Core in Center
                drawCircle(
                    color = (if (gameState.coreDamageFlashMs > 0L) RunnerTrapRed else ArenaGold).copy(alpha = 0.28f),
                    radius = corePxR * 1.45f,
                    center = center
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFF8CC), ArenaGold, Color(0xFFB45309)),
                        center = Offset(center.x - corePxR * 0.22f, center.y - corePxR * 0.22f),
                        radius = corePxR
                    ),
                    radius = corePxR,
                    center = center
                )
                rotate(degrees = orbitSpinPhase * 52f, pivot = center) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.65f),
                        radius = corePxR * 0.66f,
                        center = center,
                        style = Stroke(
                            width = 1.6.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                        )
                    )
                }

                // Particle Explosion Sparks
                particles.forEach { p ->
                    val alpha = (1f - p.life / p.maxLife).coerceIn(0f, 1f)
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.radiusPx * (0.65f + 0.35f * alpha),
                        center = Offset(p.x, p.y)
                    )
                }
            }

            // Subtle Control Hint Pill at Top of Canvas
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xAA0B0C10),
                border = BorderStroke(0.75.dp, Color.White.copy(alpha = 0.14f)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "DRAG OR SWIPE 360° • BLOCK RED • ABSORB CYAN ORBS",
                    color = DarkTextSecondary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // Game Over Obsidian Overlay + Instant Replay
            if (gameState.isGameOver) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xE6080A0F))
                        .padding(22.dp)
                        .testTag("orbit_game_over_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "CAPITAL CORE BREACHED",
                            color = RunnerTrapRed,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.4.sp
                        )
                        Text(
                            text = "Wave ${gameState.wave} • $formattedScore Pts",
                            color = DarkTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Best: $formattedBest (W$effectiveBestWave) • ${gameState.hazardsDeflectedCount} Deflected • +$roundTotalXp XP",
                            color = ArenaGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!secondChanceUsedThisRun) {
                            ArenaRewardedButton(
                                text = "Second Chance — Continue",
                                onClick = {
                                    AdManager.showRewardedAd("orbit_second_chance") {
                                        gameState = gameState.copy(
                                            lives = 1,
                                            isGameOver = false,
                                            novaShieldRemainingMs = 3_000L,
                                            entities = gameState.entities.filter { it.distance > 0.45f }
                                        )
                                        bestUpdatedOnFinish = false
                                        secondChanceUsedThisRun = true
                                    }
                                },
                                accentColor = ArenaGold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("orbit_rewarded_second_chance_button")
                            )
                        }

                        if (roundTotalXp > 0L) {
                            ArenaRewardedButton(
                                text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                enabled = !doubleXpClaimedThisRound,
                                onClick = {
                                    AdManager.showRewardedAd("orbit_double_xp") {
                                        val bonus = roundTotalXp
                                        if (bonus > 0L) {
                                            onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                            roundTotalXp += bonus
                                        }
                                        doubleXpClaimedThisRound = true
                                    }
                                },
                                accentColor = ArenaCyan,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("orbit_double_xp_button")
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    AdManager.recordGameRoundCompleted("orbit_round_complete") {
                                        onSnapshotUpdated(
                                            ArenaProgressStore.updateBestOrbitStatsIfHigher(context, gameState.score, gameState.wave)
                                        )
                                        onExitToHub()
                                    }
                                },
                                border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.65f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("orbit_gameover_hub_button")
                            ) {
                                Text("Hub", color = ArenaCyan, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    AdManager.recordGameRoundCompleted("orbit_round_complete") {
                                        gameState = GravityOrbitEngine.newGame()
                                        particles.clear()
                                        bestUpdatedOnFinish = false
                                        roundTotalXp = 0L
                                        secondChanceUsedThisRun = false
                                        doubleXpClaimedThisRound = false
                                        onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ArenaCyan,
                                    contentColor = ObsidianBg
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.35f)
                                    .height(48.dp)
                                    .testTag("orbit_play_again_button")
                            ) {
                                Text("Play Again", fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        if (powerUpHookMessage != null) {
            Text(
                text = powerUpHookMessage ?: "",
                color = ArenaGold,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 4. Bottom Tactical HUD: (1) 360° Nova Shield, (2) EMP Shockwave Blast, (3) Restart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    if (gameState.novaPowerUpRemaining > 0 && !gameState.isNovaShieldActive && !gameState.isGameOver) {
                        gameState = GravityOrbitEngine.activateNovaShield(gameState)
                        if (feedbackEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            ArenaSynthAudio.playOrbitOverdriveChime()
                        }
                    } else if (gameState.novaPowerUpRemaining <= 0 && !gameState.isNovaShieldActive) {
                        powerUpHookMessage = "Nova 360° Shield used this match"
                    }
                },
                border = BorderStroke(
                    1.dp,
                    when {
                        gameState.isNovaShieldActive -> ArenaCyan
                        gameState.novaPowerUpRemaining > 0 -> ArenaCyan
                        else -> DarkTextSecondary.copy(alpha = 0.35f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (gameState.isNovaShieldActive) ArenaCyan.copy(alpha = 0.16f) else Color.Transparent,
                    contentColor = if (gameState.novaPowerUpRemaining > 0 || gameState.isNovaShieldActive) ArenaCyan else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("orbit_nova_shield_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "360 Degree Nova Full-Circle Shield",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        gameState.isNovaShieldActive -> "Nova ${kotlin.math.ceil(gameState.novaShieldRemainingMs / 1000.0).toInt()}s"
                        gameState.novaPowerUpRemaining > 0 -> "Nova 360°"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = {
                    if (gameState.empBlastPowerUpRemaining > 0 && !gameState.isGameOver) {
                        val (nextState, empSparks, empXp) = GravityOrbitEngine.triggerEmpBlast(gameState)
                        gameState = nextState
                        val cw = canvasPixelSize.width
                        val ch = canvasPixelSize.height
                        val maxR = min(cw, ch) * 0.45f
                        if (maxR > 1f && empSparks.isNotEmpty()) {
                            empSparks.forEach { ev ->
                                val ex = cw * 0.5f + cos(ev.angleRad) * (ev.distance * maxR)
                                val ey = ch * 0.5f + sin(ev.angleRad) * (ev.distance * maxR)
                                repeat(10) { idx ->
                                    val ang = (idx / 10.0) * 2.0 * PI
                                    val spd = 120f + Random.nextFloat() * 190f
                                    particles.add(
                                        OrbitParticle(
                                            x = ex,
                                            y = ey,
                                            vx = (cos(ang) * spd).toFloat(),
                                            vy = (sin(ang) * spd).toFloat(),
                                            life = 0f,
                                            maxLife = 0.52f,
                                            radiusPx = 4f,
                                            color = if (idx % 2 == 0) ArenaCyan else RunnerTrapRed
                                        )
                                    )
                                }
                            }
                        }
                        if (empXp > 0L) {
                            roundTotalXp += empXp
                            var snap = ArenaProgressStore.updateBestOrbitStatsIfHigher(context, nextState.score, nextState.wave)
                            snap = ArenaProgressStore.awardXpAndTouchStreak(context, empXp)
                            onSnapshotUpdated(snap)
                        }
                        if (feedbackEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            ArenaSynthAudio.playOrbitEmpShockwave()
                        }
                    } else if (gameState.empBlastPowerUpRemaining <= 0) {
                        powerUpHookMessage = "EMP Shockwave Blast used this match"
                    }
                },
                border = BorderStroke(
                    1.dp,
                    if (gameState.empBlastPowerUpRemaining > 0) ArenaGold else DarkTextSecondary.copy(alpha = 0.35f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (gameState.empBlastPowerUpRemaining > 0) ArenaGold else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("orbit_emp_blast_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "EMP Shockwave Blast Power-Up",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (gameState.empBlastPowerUpRemaining > 0) "EMP Blast" else "Used",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    gameState = GravityOrbitEngine.newGame()
                    particles.clear()
                    bestUpdatedOnFinish = false
                    roundTotalXp = 0L
                    secondChanceUsedThisRun = false
                    doubleXpClaimedThisRound = false
                    onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaCyan)
                    .testTag("orbit_restart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Restart Gravity Orbit",
                    tint = ObsidianBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =============================================================================
// 16. Game 10: Titanium Citadel: Grandmaster Challenge (120fps 3-Chamber Finale)
// =============================================================================

enum class CitadelChamber(val chamberIndex: Int, val title: String, val badgeLabel: String) {
    CHAMBER_1_LOCK_PICK(1, "Chamber 1 • Laser Lock-Pick", "LOCK-PICK"),
    CHAMBER_2_FIREWALL_DODGE(2, "Chamber 2 • Cyber Firewall", "FIREWALL"),
    CHAMBER_3_QUANTUM_OVERRIDE(3, "Chamber 3 • Quantum Code", "OVERRIDE")
}

enum class OverrideSubPhase {
    FLASHING,
    INPUT,
    SOLVED_PAUSE
}

enum class CitadelFirewallKind {
    LASER_WALL,
    GOLD_DIAMOND
}

data class CitadelFirewallEntity(
    val id: Long,
    val kind: CitadelFirewallKind,
    val x: Float,
    val y: Float,
    val gapHalfWidth: Float = 0.19f,
    val speed: Float = 0.38f,
    val resolved: Boolean = false
)

enum class CitadelSparkStyle {
    CYAN_SHATTER,
    GOLD_BULLSEYE,
    RED_IMPACT,
    ROYAL_TIER_BURST,
    AEGIS_SHIELD
}

data class CitadelSparkEvent(
    val normX: Float,
    val normY: Float,
    val style: CitadelSparkStyle
)

data class TitaniumCitadelState(
    val chamber: CitadelChamber = CitadelChamber.CHAMBER_1_LOCK_PICK,
    val tier: Int = 1,
    val chamberElapsedMs: Long = 0L,
    val transitionBannerMs: Long = 1_200L,
    val score: Long = 0L,
    val lives: Int = 3,
    val combo: Int = 1,
    // Chamber 1: Laser Vault Lock-Pick
    val needleAngleDeg: Float = 0f,
    val needleDirection: Float = 1f,
    val targetArcCenterDeg: Float = 90f,
    val targetArcHalfSpanDeg: Float = 28f,
    val goldBullseyeHalfSpanDeg: Float = 10f,
    val locksRemainingInStage: Int = 3,
    // Chamber 2: Cyber Firewall Dodge
    val playerX: Float = 0.5f,
    val firewallEntities: List<CitadelFirewallEntity> = emptyList(),
    val firewallSpawnCooldownMs: Long = 450L,
    val firewallStepCounter: Int = 0,
    val nextEntityId: Long = 1L,
    // Chamber 3: Quantum Code Override
    val overrideSequence: List<Int> = listOf(0, 2, 1),
    val overrideSubPhase: OverrideSubPhase = OverrideSubPhase.FLASHING,
    val overrideFlashStepIndex: Int = 0,
    val overrideFlashStepMs: Long = 0L,
    val overrideInputIndex: Int = 0,
    val overrideSolvedPauseMs: Long = 0L,
    val overrideRoundsClearedInChamber: Int = 0,
    // 2 Ultimate Power-Ups (Future Rewarded Ad Ready)
    val chronoSlowPowerUpRemaining: Int = 1,
    val chronoSlowRemainingMs: Long = 0L,
    val aegisRevivePowerUpRemaining: Int = 1,
    val aegisShieldRemainingMs: Long = 0L,
    // Visual & Rank Progression
    val damageFlashMs: Long = 0L,
    val goldDiamondsCollected: Int = 0,
    val hasUnlockedGrandmaster: Boolean = false,
    val isGameOver: Boolean = false
) {
    val isChronoSlowActive: Boolean get() = chronoSlowRemainingMs > 0L
    val isAegisShieldActive: Boolean get() = aegisShieldRemainingMs > 0L
}

data class CitadelStepResult(
    val nextState: TitaniumCitadelState,
    val pointsGained: Long = 0L,
    val xpGained: Long = 0L,
    val chamberTransitioned: Boolean = false,
    val tierCompleted: Boolean = false,
    val aegisReviveTriggered: Boolean = false,
    val damageTaken: Boolean = false,
    val diamondCollected: Boolean = false,
    val unlockedGrandmasterNow: Boolean = false,
    val activeFlashConduit: Int? = null,
    val sparks: List<CitadelSparkEvent> = emptyList()
)

data class CitadelActionResult(
    val nextState: TitaniumCitadelState,
    val hit: Boolean = false,
    val isGoldBullseye: Boolean = false,
    val stageCleared: Boolean = false,
    val tierCompleted: Boolean = false,
    val aegisReviveTriggered: Boolean = false,
    val damageTaken: Boolean = false,
    val unlockedGrandmasterNow: Boolean = false,
    val pointsGained: Long = 0L,
    val xpGained: Long = 0L,
    val sparks: List<CitadelSparkEvent> = emptyList()
)

private data class CitadelParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val radiusPx: Float,
    val color: Color
)

object TitaniumCitadelEngine {
    const val CHAMBER_DURATION_MS = 15_000L
    const val CHRONO_SLOW_DURATION_MS = 5_000L
    const val AEGIS_SHIELD_DURATION_MS = 3_000L
    const val OVERRIDE_FLASH_ON_MS = 420L
    const val OVERRIDE_FLASH_GAP_MS = 110L
    const val OVERRIDE_STEP_MS = OVERRIDE_FLASH_ON_MS + OVERRIDE_FLASH_GAP_MS
    const val CORE_Y = 0.84f
    const val CORE_HALF_WIDTH = 0.055f
    const val DIAMOND_CATCH_HALF_WIDTH = 0.095f

    fun tierSpeedScale(tier: Int): Float = (1f + (tier.coerceAtLeast(1) - 1) * 0.14f).coerceAtMost(2.5f)

    fun normalizeDeg(deg: Float): Float {
        var d = deg % 360f
        if (d < 0f) d += 360f
        return d
    }

    fun angleDiffDeg(aDeg: Float, bDeg: Float): Float {
        val diff = abs(normalizeDeg(aDeg) - normalizeDeg(bDeg))
        return if (diff > 180f) 360f - diff else diff
    }

    fun newGame(random: Random = Random.Default): TitaniumCitadelState {
        return TitaniumCitadelState(
            chamber = CitadelChamber.CHAMBER_1_LOCK_PICK,
            tier = 1,
            targetArcCenterDeg = 70f + random.nextFloat() * 220f,
            overrideSequence = generateOverrideSequence(tier = 1, random = random)
        )
    }

    fun generateOverrideSequence(tier: Int, random: Random = Random.Default): List<Int> {
        val len = (3 + ((tier - 1) / 2)).coerceIn(3, 6)
        return List(len) { random.nextInt(4) }
    }

    fun activateChronoSlow(state: TitaniumCitadelState): TitaniumCitadelState {
        if (state.isGameOver || state.chronoSlowPowerUpRemaining <= 0 || state.isChronoSlowActive) {
            return state
        }
        return state.copy(
            chronoSlowPowerUpRemaining = 0,
            chronoSlowRemainingMs = CHRONO_SLOW_DURATION_MS
        )
    }

    fun activateAegisShield(state: TitaniumCitadelState): TitaniumCitadelState {
        if (state.isGameOver || state.aegisRevivePowerUpRemaining <= 0 || state.isAegisShieldActive) {
            return state
        }
        return state.copy(
            aegisRevivePowerUpRemaining = 0,
            aegisShieldRemainingMs = AEGIS_SHIELD_DURATION_MS
        )
    }

    fun slideCoreTo(state: TitaniumCitadelState, normX: Float): TitaniumCitadelState {
        if (state.isGameOver) return state
        return state.copy(playerX = normX.coerceIn(0.08f, 0.92f))
    }

    private fun applyDamageInternal(
        lives: Int,
        aegisRemainingMs: Long,
        aegisPowerUpRemaining: Int
    ): Triple<Int, Int, Pair<Long, Boolean>> {
        if (aegisRemainingMs > 0L) {
            return Triple(lives, aegisPowerUpRemaining, aegisRemainingMs to false)
        }
        val nextLivesRaw = lives - 1
        if (nextLivesRaw <= 0 && aegisPowerUpRemaining > 0) {
            return Triple(1, 0, AEGIS_SHIELD_DURATION_MS to true)
        }
        return Triple(nextLivesRaw.coerceAtLeast(0), aegisPowerUpRemaining, 0L to false)
    }

    private fun checkGrandmaster(tier: Int, score: Long, already: Boolean): Boolean =
        already || tier >= ArenaProgressStore.GRANDMASTER_REQUIRED_TIER || score >= ArenaProgressStore.GRANDMASTER_REQUIRED_SCORE

    private fun prepareNextChamberState(
        base: TitaniumCitadelState,
        nextChamber: CitadelChamber,
        nextTier: Int,
        random: Random
    ): TitaniumCitadelState {
        val arcSpan = (28f - (nextTier - 1) * 1.4f).coerceIn(18f, 28f)
        val bullSpan = (10f - (nextTier - 1) * 0.4f).coerceIn(6.5f, 10f)
        return when (nextChamber) {
            CitadelChamber.CHAMBER_1_LOCK_PICK -> base.copy(
                chamber = nextChamber,
                tier = nextTier,
                chamberElapsedMs = 0L,
                transitionBannerMs = 1_300L,
                targetArcCenterDeg = normalizeDeg(random.nextFloat() * 360f),
                targetArcHalfSpanDeg = arcSpan,
                goldBullseyeHalfSpanDeg = bullSpan,
                locksRemainingInStage = 3 + ((nextTier - 1) / 3).coerceAtMost(2),
                firewallEntities = emptyList()
            )
            CitadelChamber.CHAMBER_2_FIREWALL_DODGE -> base.copy(
                chamber = nextChamber,
                tier = nextTier,
                chamberElapsedMs = 0L,
                transitionBannerMs = 1_300L,
                playerX = 0.5f,
                firewallEntities = emptyList(),
                firewallSpawnCooldownMs = 350L
            )
            CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE -> base.copy(
                chamber = nextChamber,
                tier = nextTier,
                chamberElapsedMs = 0L,
                transitionBannerMs = 1_300L,
                firewallEntities = emptyList(),
                overrideSequence = generateOverrideSequence(nextTier, random),
                overrideSubPhase = OverrideSubPhase.FLASHING,
                overrideFlashStepIndex = 0,
                overrideFlashStepMs = 0L,
                overrideInputIndex = 0,
                overrideSolvedPauseMs = 0L,
                overrideRoundsClearedInChamber = 0
            )
        }
    }

    fun activeFlashConduitForState(state: TitaniumCitadelState): Int? {
        if (state.chamber != CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE) return null
        if (state.overrideSubPhase != OverrideSubPhase.FLASHING) return null
        val step = state.overrideFlashStepIndex
        if (step !in state.overrideSequence.indices) return null
        return if (state.overrideFlashStepMs < OVERRIDE_FLASH_ON_MS) {
            state.overrideSequence[step]
        } else {
            null
        }
    }

    fun tapLockPick(
        state: TitaniumCitadelState,
        random: Random = Random.Default
    ): CitadelActionResult {
        if (state.isGameOver || state.chamber != CitadelChamber.CHAMBER_1_LOCK_PICK) {
            return CitadelActionResult(state)
        }
        val diff = angleDiffDeg(state.needleAngleDeg, state.targetArcCenterDeg)
        val hitCyan = diff <= state.targetArcHalfSpanDeg
        val isGold = diff <= state.goldBullseyeHalfSpanDeg
        val sparks = mutableListOf<CitadelSparkEvent>()

        if (hitCyan) {
            val pts = (if (isGold) 150L else 90L) * state.combo
            val xp = if (isGold) 8L else 5L
            val nextScore = state.score + pts
            val nextCombo = (state.combo + 1).coerceAtMost(8)
            val nextLocks = state.locksRemainingInStage - 1
            val arcAngleRad = state.targetArcCenterDeg * (PI / 180.0)
            val sx = (0.5 + 0.30 * cos(arcAngleRad)).toFloat()
            val sy = (0.52 + 0.30 * sin(arcAngleRad)).toFloat()
            sparks.add(
                CitadelSparkEvent(
                    sx,
                    sy,
                    if (isGold) CitadelSparkStyle.GOLD_BULLSEYE else CitadelSparkStyle.CYAN_SHATTER
                )
            )
            val unlockedGmNow = !state.hasUnlockedGrandmaster && checkGrandmaster(state.tier, nextScore, false)
            val stageCleared = nextLocks <= 0
            val totalScore = if (stageCleared) nextScore + 150L * state.tier else nextScore
            val updatedBase = state.copy(
                score = totalScore,
                combo = nextCombo,
                targetArcCenterDeg = normalizeDeg(state.targetArcCenterDeg + 95f + random.nextFloat() * 175f),
                locksRemainingInStage = nextLocks.coerceAtLeast(0),
                needleDirection = -state.needleDirection,
                hasUnlockedGrandmaster = checkGrandmaster(state.tier, totalScore, state.hasUnlockedGrandmaster)
            )
            val finalState = if (stageCleared) {
                prepareNextChamberState(updatedBase, CitadelChamber.CHAMBER_2_FIREWALL_DODGE, state.tier, random)
            } else {
                updatedBase
            }
            return CitadelActionResult(
                nextState = finalState,
                hit = true,
                isGoldBullseye = isGold,
                stageCleared = stageCleared,
                unlockedGrandmasterNow = unlockedGmNow || (!state.hasUnlockedGrandmaster && finalState.hasUnlockedGrandmaster),
                pointsGained = totalScore - state.score,
                xpGained = xp + if (stageCleared) 10L else 0L,
                sparks = sparks
            )
        } else {
            val (nextLives, nextAegisRem, aegisPair) = applyDamageInternal(
                state.lives,
                state.aegisShieldRemainingMs,
                state.aegisRevivePowerUpRemaining
            )
            val (nextAegisMs, aegisRevived) = aegisPair
            val damageIgnoredByShield = state.isAegisShieldActive
            sparks.add(
                CitadelSparkEvent(
                    0.5f,
                    0.52f,
                    if (aegisRevived) CitadelSparkStyle.AEGIS_SHIELD else CitadelSparkStyle.RED_IMPACT
                )
            )
            val nextState = state.copy(
                lives = nextLives,
                combo = if (damageIgnoredByShield) state.combo else 1,
                aegisRevivePowerUpRemaining = nextAegisRem,
                aegisShieldRemainingMs = nextAegisMs,
                damageFlashMs = if (damageIgnoredByShield) 0L else 360L,
                isGameOver = nextLives <= 0
            )
            return CitadelActionResult(
                nextState = nextState,
                hit = false,
                aegisReviveTriggered = aegisRevived,
                damageTaken = !damageIgnoredByShield,
                sparks = sparks
            )
        }
    }

    fun tapConduit(
        state: TitaniumCitadelState,
        conduitIndex: Int,
        random: Random = Random.Default
    ): CitadelActionResult {
        if (state.isGameOver ||
            state.chamber != CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE ||
            state.overrideSubPhase != OverrideSubPhase.INPUT
        ) {
            return CitadelActionResult(state)
        }
        val expected = state.overrideSequence.getOrNull(state.overrideInputIndex) ?: return CitadelActionResult(state)
        val cx = 0.22f + (conduitIndex % 2) * 0.56f
        val cy = 0.42f + (conduitIndex / 2) * 0.28f

        if (conduitIndex == expected) {
            val pts = 70L * state.combo
            val nextScore = state.score + pts
            val nextInput = state.overrideInputIndex + 1
            val sequenceDone = nextInput >= state.overrideSequence.size
            val sparks = mutableListOf(CitadelSparkEvent(cx, cy, CitadelSparkStyle.CYAN_SHATTER))

            if (sequenceDone) {
                val tierClearBonus = 260L * state.tier
                val totalScore = nextScore + tierClearBonus
                val nextTier = state.tier + 1
                val unlockedGmNow = !state.hasUnlockedGrandmaster && checkGrandmaster(nextTier, totalScore, false)
                sparks.add(CitadelSparkEvent(0.5f, 0.45f, CitadelSparkStyle.ROYAL_TIER_BURST))

                val updated = state.copy(
                    score = totalScore,
                    combo = (state.combo + 1).coerceAtMost(8),
                    overrideRoundsClearedInChamber = state.overrideRoundsClearedInChamber + 1,
                    hasUnlockedGrandmaster = checkGrandmaster(nextTier, totalScore, state.hasUnlockedGrandmaster)
                )
                val nextChamberState = prepareNextChamberState(
                    base = updated,
                    nextChamber = CitadelChamber.CHAMBER_1_LOCK_PICK,
                    nextTier = nextTier,
                    random = random
                )
                return CitadelActionResult(
                    nextState = nextChamberState,
                    hit = true,
                    stageCleared = true,
                    tierCompleted = true,
                    unlockedGrandmasterNow = unlockedGmNow,
                    pointsGained = totalScore - state.score,
                    xpGained = 24L,
                    sparks = sparks
                )
            } else {
                val gmUnlocked = checkGrandmaster(state.tier, nextScore, state.hasUnlockedGrandmaster)
                val unlockedGmNow = !state.hasUnlockedGrandmaster && gmUnlocked
                return CitadelActionResult(
                    nextState = state.copy(
                        score = nextScore,
                        overrideInputIndex = nextInput,
                        hasUnlockedGrandmaster = gmUnlocked
                    ),
                    hit = true,
                    unlockedGrandmasterNow = unlockedGmNow,
                    pointsGained = pts,
                    xpGained = 4L,
                    sparks = sparks
                )
            }
        } else {
            val (nextLives, nextAegisRem, aegisPair) = applyDamageInternal(
                state.lives,
                state.aegisShieldRemainingMs,
                state.aegisRevivePowerUpRemaining
            )
            val (nextAegisMs, aegisRevived) = aegisPair
            val damageIgnored = state.isAegisShieldActive
            val sparks = listOf(
                CitadelSparkEvent(
                    cx,
                    cy,
                    if (aegisRevived) CitadelSparkStyle.AEGIS_SHIELD else CitadelSparkStyle.RED_IMPACT
                )
            )
            val nextState = state.copy(
                lives = nextLives,
                combo = if (damageIgnored) state.combo else 1,
                overrideSubPhase = OverrideSubPhase.FLASHING,
                overrideFlashStepIndex = 0,
                overrideFlashStepMs = 0L,
                overrideInputIndex = 0,
                aegisRevivePowerUpRemaining = nextAegisRem,
                aegisShieldRemainingMs = nextAegisMs,
                damageFlashMs = if (damageIgnored) 0L else 360L,
                isGameOver = nextLives <= 0
            )
            return CitadelActionResult(
                nextState = nextState,
                hit = false,
                aegisReviveTriggered = aegisRevived,
                damageTaken = !damageIgnored,
                sparks = sparks
            )
        }
    }

    fun step(
        state: TitaniumCitadelState,
        dtSec: Float,
        random: Random = Random.Default
    ): CitadelStepResult {
        if (state.isGameOver) return CitadelStepResult(state)
        val dtMs = (dtSec * 1000f).roundToInt().toLong().coerceIn(1L, 1_500L)

        val nextChronoMs = (state.chronoSlowRemainingMs - dtMs).coerceAtLeast(0L)
        val nextAegisMsStart = (state.aegisShieldRemainingMs - dtMs).coerceAtLeast(0L)
        val nextBannerMs = (state.transitionBannerMs - dtMs).coerceAtLeast(0L)
        val nextDmgFlashMs = (state.damageFlashMs - dtMs).coerceAtLeast(0L)

        val speedFactor = if (state.isChronoSlowActive) 0.5f else 1f
        val tierScale = tierSpeedScale(state.tier)

        var nextState = state.copy(
            chronoSlowRemainingMs = nextChronoMs,
            aegisShieldRemainingMs = nextAegisMsStart,
            transitionBannerMs = nextBannerMs,
            damageFlashMs = nextDmgFlashMs
        )
        var pointsGained = 0L
        var xpGained = 0L
        var aegisRevivedAny = false
        var damageTakenAny = false
        var diamondCollectedAny = false
        val sparks = mutableListOf<CitadelSparkEvent>()

        when (state.chamber) {
            CitadelChamber.CHAMBER_1_LOCK_PICK -> {
                val angularSpeed = 165f * tierScale * speedFactor
                val nextNeedle = normalizeDeg(state.needleAngleDeg + state.needleDirection * angularSpeed * dtSec)
                nextState = nextState.copy(needleAngleDeg = nextNeedle)
            }
            CitadelChamber.CHAMBER_2_FIREWALL_DODGE -> {
                var lives = nextState.lives
                var score = nextState.score
                var combo = nextState.combo
                var aegisPowerUp = nextState.aegisRevivePowerUpRemaining
                var aegisMs = nextState.aegisShieldRemainingMs
                var dmgFlash = nextState.damageFlashMs
                var diamondsCount = nextState.goldDiamondsCollected
                var spawnCooldown = nextState.firewallSpawnCooldownMs - dtMs
                var stepCounter = nextState.firewallStepCounter
                var nextId = nextState.nextEntityId

                val updatedEntities = mutableListOf<CitadelFirewallEntity>()
                for (ent in nextState.firewallEntities) {
                    val prevY = ent.y
                    val nextY = prevY + ent.speed * speedFactor * dtSec
                    var resolved = ent.resolved

                    if (!resolved && nextY >= CORE_Y - 0.04f && prevY <= CORE_Y + 0.05f) {
                        when (ent.kind) {
                            CitadelFirewallKind.LASER_WALL -> {
                                val inSafeGap = abs(nextState.playerX - ent.x) <= (ent.gapHalfWidth - CORE_HALF_WIDTH * 0.35f)
                                if (inSafeGap) {
                                    resolved = true
                                    val pts = 75L * combo
                                    score += pts
                                    pointsGained += pts
                                    xpGained += 4L
                                    sparks.add(CitadelSparkEvent(nextState.playerX, CORE_Y, CitadelSparkStyle.CYAN_SHATTER))
                                } else {
                                    resolved = true
                                    val (nLives, nAegisPow, aPair) = applyDamageInternal(lives, aegisMs, aegisPowerUp)
                                    lives = nLives
                                    aegisPowerUp = nAegisPow
                                    aegisMs = aPair.first
                                    if (aPair.second) {
                                        aegisRevivedAny = true
                                        sparks.add(CitadelSparkEvent(nextState.playerX, CORE_Y, CitadelSparkStyle.AEGIS_SHIELD))
                                    } else if (aegisMs <= 0L) {
                                        combo = 1
                                        dmgFlash = 380L
                                        damageTakenAny = true
                                        sparks.add(CitadelSparkEvent(nextState.playerX, CORE_Y, CitadelSparkStyle.RED_IMPACT))
                                    }
                                }
                            }
                            CitadelFirewallKind.GOLD_DIAMOND -> {
                                if (abs(nextState.playerX - ent.x) <= DIAMOND_CATCH_HALF_WIDTH) {
                                    resolved = true
                                    diamondsCount += 1
                                    combo = (combo + 1).coerceAtMost(8)
                                    val pts = 120L * combo
                                    score += pts
                                    pointsGained += pts
                                    xpGained += 5L
                                    diamondCollectedAny = true
                                    sparks.add(CitadelSparkEvent(ent.x, CORE_Y, CitadelSparkStyle.GOLD_BULLSEYE))
                                    continue
                                }
                            }
                        }
                    }

                    if (nextY <= 1.08f) {
                        updatedEntities.add(ent.copy(y = nextY, resolved = resolved))
                    }
                }

                if (lives > 0 && spawnCooldown <= 0L) {
                    val gapX = 0.22f + random.nextFloat() * 0.56f
                    val gapHalf = (0.19f - (state.tier - 1) * 0.01f).coerceIn(0.135f, 0.19f)
                    val wallSpeed = 0.38f * tierScale
                    updatedEntities.add(
                        CitadelFirewallEntity(
                            id = nextId++,
                            kind = CitadelFirewallKind.LASER_WALL,
                            x = gapX,
                            y = -0.05f,
                            gapHalfWidth = gapHalf,
                            speed = wallSpeed
                        )
                    )
                    if (stepCounter % 2 == 0) {
                        updatedEntities.add(
                            CitadelFirewallEntity(
                                id = nextId++,
                                kind = CitadelFirewallKind.GOLD_DIAMOND,
                                x = gapX,
                                y = -0.14f,
                                speed = wallSpeed
                            )
                        )
                    }
                    stepCounter += 1
                    spawnCooldown = (1_180L - (state.tier - 1) * 75L).coerceIn(580L, 1_180L)
                }

                nextState = nextState.copy(
                    lives = lives,
                    score = score,
                    combo = combo,
                    aegisRevivePowerUpRemaining = aegisPowerUp,
                    aegisShieldRemainingMs = aegisMs,
                    damageFlashMs = dmgFlash,
                    goldDiamondsCollected = diamondsCount,
                    firewallEntities = updatedEntities,
                    firewallSpawnCooldownMs = spawnCooldown,
                    firewallStepCounter = stepCounter,
                    nextEntityId = nextId,
                    isGameOver = lives <= 0
                )
            }
            CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE -> {
                if (nextState.overrideSubPhase == OverrideSubPhase.FLASHING) {
                    var stepIdx = nextState.overrideFlashStepIndex
                    var stepMs = nextState.overrideFlashStepMs + dtMs
                    var subPhase = nextState.overrideSubPhase
                    while (stepMs >= OVERRIDE_STEP_MS) {
                        stepMs -= OVERRIDE_STEP_MS
                        stepIdx += 1
                        if (stepIdx >= nextState.overrideSequence.size) {
                            subPhase = OverrideSubPhase.INPUT
                            stepIdx = 0
                            stepMs = 0L
                            break
                        }
                    }
                    nextState = nextState.copy(
                        overrideSubPhase = subPhase,
                        overrideFlashStepIndex = stepIdx,
                        overrideFlashStepMs = stepMs
                    )
                }
            }
        }

        // 15-Second Dynamic Chamber Rotation & Tier Escalation
        var chamberTransitioned = false
        var tierCompleted = false
        var finalElapsed = nextState.chamberElapsedMs + dtMs

        if (!nextState.isGameOver && finalElapsed >= CHAMBER_DURATION_MS) {
            chamberTransitioned = true
            val nextChamber = when (nextState.chamber) {
                CitadelChamber.CHAMBER_1_LOCK_PICK -> CitadelChamber.CHAMBER_2_FIREWALL_DODGE
                CitadelChamber.CHAMBER_2_FIREWALL_DODGE -> CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE
                CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE -> CitadelChamber.CHAMBER_1_LOCK_PICK
            }
            val nextTier = if (nextChamber == CitadelChamber.CHAMBER_1_LOCK_PICK) {
                tierCompleted = true
                val tierBonus = 220L * nextState.tier
                pointsGained += tierBonus
                xpGained += 20L
                sparks.add(CitadelSparkEvent(0.5f, 0.45f, CitadelSparkStyle.ROYAL_TIER_BURST))
                nextState = nextState.copy(score = nextState.score + tierBonus)
                nextState.tier + 1
            } else {
                pointsGained += 80L
                xpGained += 6L
                nextState = nextState.copy(score = nextState.score + 80L)
                nextState.tier
            }
            nextState = prepareNextChamberState(nextState, nextChamber, nextTier, random)
            finalElapsed = 0L
        } else {
            nextState = nextState.copy(chamberElapsedMs = finalElapsed)
        }

        val gmNow = !state.hasUnlockedGrandmaster &&
            checkGrandmaster(nextState.tier, nextState.score, false)
        nextState = nextState.copy(
            hasUnlockedGrandmaster = checkGrandmaster(nextState.tier, nextState.score, state.hasUnlockedGrandmaster)
        )

        return CitadelStepResult(
            nextState = nextState,
            pointsGained = pointsGained,
            xpGained = xpGained,
            chamberTransitioned = chamberTransitioned,
            tierCompleted = tierCompleted,
            aegisReviveTriggered = aegisRevivedAny,
            damageTaken = damageTakenAny,
            diamondCollected = diamondCollectedAny,
            unlockedGrandmasterNow = gmNow,
            activeFlashConduit = activeFlashConduitForState(nextState),
            sparks = sparks
        )
    }
}

@Composable
private fun CitadelHeroBannerCard(
    spec: ArenaGameSpec,
    snapshot: ArenaSnapshot,
    onUnlockFlashShown: () -> Unit,
    onPlayClick: () -> Unit
) {
    val isUnlocked = snapshot.isGame10Unlocked
    val progressStreak = snapshot.effectiveStreakDays.coerceIn(0, ArenaProgressStore.GAME10_REQUIRED_STREAK_DAYS)
    val progressXp = snapshot.totalXp.coerceIn(0L, ArenaProgressStore.GAME10_REQUIRED_ARENA_XP)
    val formattedBest = remember(snapshot.bestCitadelScore) {
        NumberFormat.getNumberInstance(Locale.US).format(snapshot.bestCitadelScore)
    }
    val formattedXp = remember(progressXp) {
        NumberFormat.getNumberInstance(Locale.US).format(progressXp)
    }

    var unlockBannerVisible by remember(snapshot.shouldShowGame10UnlockFlash) {
        mutableStateOf(snapshot.shouldShowGame10UnlockFlash)
    }
    val unlockFlashAlpha = remember { Animatable(0f) }

    LaunchedEffect(snapshot.shouldShowGame10UnlockFlash) {
        if (snapshot.shouldShowGame10UnlockFlash) {
            unlockBannerVisible = true
            unlockFlashAlpha.snapTo(1f)
            unlockFlashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1750, easing = FastOutSlowInEasing)
            )
            delay(1500L)
            unlockBannerVisible = false
            onUnlockFlashShown()
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B0C10),
        border = BorderStroke(
            width = 1.8.dp,
            brush = Brush.linearGradient(
                colors = if (isUnlocked) {
                    listOf(ArenaGold, ArenaCyan, ArenaGold)
                } else {
                    listOf(ArenaGold.copy(alpha = 0.65f), ArenaCyan.copy(alpha = 0.55f))
                }
            )
        ),
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arena_hero_game_card_10")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = if (isUnlocked) {
                            listOf(ArenaCyan.copy(alpha = 0.90f), ArenaGold.copy(alpha = 0.85f))
                        } else {
                            listOf(ArenaCyan.copy(alpha = 0.35f), ArenaGold.copy(alpha = 0.35f))
                        }
                    ),
                    shape = RoundedCornerShape(13.dp)
                )
                .clip(RoundedCornerShape(13.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF171206),
                            Color(0xFF0B0C10),
                            Color(0xFF071622)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaGold.copy(alpha = 0.16f), Color.Transparent),
                        center = Offset(size.height * 0.45f, size.height * 0.5f),
                        radius = size.height * 0.90f
                    ),
                    radius = size.height * 0.90f,
                    center = Offset(size.height * 0.45f, size.height * 0.5f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ArenaCyan.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.24f),
                        radius = size.height * 0.80f
                    ),
                    radius = size.height * 0.80f,
                    center = Offset(size.width * 0.88f, size.height * 0.24f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CitadelLogoArtwork(
                    modifier = Modifier
                        .size(104.dp)
                        .testTag("citadel_logo_canvas")
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = spec.title,
                            color = DarkTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = spec.subtitle,
                            color = DarkTextSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = ArenaGold.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.72f)),
                        modifier = Modifier.testTag("hero_citadel_best_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Best Titanium Citadel Stats",
                                tint = ArenaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "🏆 Best: $formattedBest • T${snapshot.bestCitadelTier}",
                                color = ArenaGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Button(
                        onClick = onPlayClick,
                        enabled = isUnlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaGold,
                            contentColor = ObsidianBg,
                            disabledContainerColor = Color(0xFF1F2937),
                            disabledContentColor = DarkTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("arena_hero_citadel_play_button")
                    ) {
                        Text(
                            text = if (isUnlocked) "ENTER CITADEL ▶" else "VIP LOCKED",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.9.sp
                        )
                    }
                }
            }

            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xE00B0C10),
                                    Color(0xEE14110A),
                                    Color(0xE00B0C10)
                                )
                            )
                        )
                        .border(1.2.dp, ArenaGold.copy(alpha = 0.65f), RoundedCornerShape(13.dp))
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                        .testTag("citadel_vip_lock_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    val streakFraction = (progressStreak / 21f).coerceIn(0f, 1f)
                    val xpFraction = (progressXp / 2500f).coerceIn(0f, 1f)
                    val bestPathFraction = max(streakFraction, xpFraction)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ArenaGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.85f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "VIP Dual-Path Lock",
                                    tint = ArenaGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "👑 Unlock at 21-Day Streak OR 2,500 Arena XP • $progressStreak/21d | $formattedXp/2500 XP",
                                    color = ArenaGold,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { bestPathFraction },
                            color = ArenaGold,
                            trackColor = Color(0xFF1F2937),
                            modifier = Modifier
                                .fillMaxWidth(0.82f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )

                        Text(
                            text = "Reach either milestone for permanent free VIP access (early Rewarded Ad pass ready later).",
                            color = DarkTextSecondary,
                            fontSize = 10.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (unlockFlashAlpha.value > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ArenaGold.copy(alpha = unlockFlashAlpha.value * 0.65f),
                                    ArenaCyan.copy(alpha = unlockFlashAlpha.value * 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            AnimatedVisibility(
                visible = unlockBannerVisible && isUnlocked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f))
                ) {
                    Text(
                        text = "👑 VIP CITADEL UNLOCKED!",
                        color = ObsidianBg,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CitadelLogoArtwork(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "citadelLogoAnim")
    val crownPulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "citadelCrownPulse"
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Titanium Citadel 3D diamond gold crown over illuminated titanium citadel towers emblem"
        }
    ) {
        val corner = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF171206), Color(0xFF071320))),
            cornerRadius = corner
        )
        drawRoundRect(
            brush = Brush.linearGradient(listOf(ArenaGold, ArenaCyan, ArenaGold)),
            cornerRadius = corner,
            style = Stroke(width = 1.6.dp.toPx())
        )

        val w = size.width
        val h = size.height
        val cx = w * 0.5f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(ArenaGold.copy(alpha = 0.24f), ArenaCyan.copy(alpha = 0.08f), Color.Transparent),
                center = Offset(cx, h * 0.52f),
                radius = w * 0.44f * crownPulse
            ),
            radius = w * 0.44f * crownPulse,
            center = Offset(cx, h * 0.52f)
        )

        val towerBrush = Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF0F172A)))
        drawRoundRect(
            brush = towerBrush,
            topLeft = Offset(w * 0.19f, h * 0.52f),
            size = Size(w * 0.14f, h * 0.34f),
            cornerRadius = CornerRadius(3.dp.toPx())
        )
        drawRoundRect(
            brush = towerBrush,
            topLeft = Offset(w * 0.67f, h * 0.52f),
            size = Size(w * 0.14f, h * 0.34f),
            cornerRadius = CornerRadius(3.dp.toPx())
        )
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF0B0C10))),
            topLeft = Offset(w * 0.34f, h * 0.40f),
            size = Size(w * 0.32f, h * 0.46f),
            cornerRadius = CornerRadius(4.dp.toPx())
        )
        drawRoundRect(
            color = ArenaCyan.copy(alpha = 0.72f),
            topLeft = Offset(w * 0.34f, h * 0.40f),
            size = Size(w * 0.32f, h * 0.46f),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = Stroke(width = 1.1.dp.toPx())
        )

        drawRoundRect(
            brush = Brush.verticalGradient(listOf(ArenaCyan, ArenaGold)),
            topLeft = Offset(w * 0.45f, h * 0.62f),
            size = Size(w * 0.10f, h * 0.24f),
            cornerRadius = CornerRadius(6.dp.toPx())
        )

        val crownPath = Path().apply {
            moveTo(w * 0.26f, h * 0.35f)
            lineTo(w * 0.31f, h * 0.16f)
            lineTo(w * 0.41f, h * 0.25f)
            lineTo(cx, h * 0.11f)
            lineTo(w * 0.59f, h * 0.25f)
            lineTo(w * 0.69f, h * 0.16f)
            lineTo(w * 0.74f, h * 0.35f)
            close()
        }
        drawPath(
            path = crownPath,
            brush = Brush.linearGradient(listOf(Color(0xFFFFF3B0), ArenaGold, Color(0xFFB45309)))
        )
        drawPath(
            path = crownPath,
            color = Color.White.copy(alpha = 0.7f),
            style = Stroke(width = 1.dp.toPx(), join = StrokeJoin.Round)
        )

        val gemPath = Path().apply {
            moveTo(cx, h * 0.21f)
            lineTo(cx + w * 0.055f, h * 0.27f)
            lineTo(cx, h * 0.33f)
            lineTo(cx - w * 0.055f, h * 0.27f)
            close()
        }
        drawPath(
            path = gemPath,
            brush = Brush.linearGradient(listOf(Color.White, ArenaCyan))
        )
    }
}

@Composable
private fun TitaniumCitadelGameView(
    snapshot: ArenaSnapshot,
    onSnapshotUpdated: (ArenaSnapshot) -> Unit,
    onExitToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val textMeasurer = rememberTextMeasurer()

    var gameState by remember { mutableStateOf(TitaniumCitadelEngine.newGame()) }
    var feedbackEnabled by remember(snapshot.soundAndHapticEnabled) { mutableStateOf(snapshot.soundAndHapticEnabled) }
    var roundTotalXp by remember { mutableLongStateOf(0L) }
    var bestUpdatedOnFinish by remember { mutableStateOf(false) }
    var secondChanceUsedThisRun by remember { mutableStateOf(false) }
    var doubleXpClaimedThisRound by remember { mutableStateOf(false) }
    var activeFlashConduit by remember { mutableStateOf<Int?>(null) }
    var canvasPixelSize by remember { mutableStateOf(Size(800f, 800f)) }
    var statusBannerMessage by remember { mutableStateOf<String?>(null) }
    val particles = remember { mutableStateListOf<CitadelParticle>() }

    fun spawnBurst(normX: Float, normY: Float, style: CitadelSparkStyle) {
        val cw = canvasPixelSize.width
        val ch = canvasPixelSize.height
        if (cw <= 1f || ch <= 1f) return
        val px = normX.coerceIn(0.05f, 0.95f) * cw
        val py = normY.coerceIn(0.05f, 0.95f) * ch
        val count = when (style) {
            CitadelSparkStyle.ROYAL_TIER_BURST -> 28
            CitadelSparkStyle.AEGIS_SHIELD -> 22
            CitadelSparkStyle.GOLD_BULLSEYE -> 16
            else -> 12
        }
        repeat(count) { i ->
            val angle = (i / count.toFloat()) * (2f * PI.toFloat()) + Random.nextFloat() * 0.28f
            val speed = 130f + Random.nextFloat() * 260f
            val col = when (style) {
                CitadelSparkStyle.CYAN_SHATTER -> if (i % 2 == 0) ArenaCyan else Color.White
                CitadelSparkStyle.GOLD_BULLSEYE -> if (i % 2 == 0) ArenaGold else Color(0xFFFFF3B0)
                CitadelSparkStyle.RED_IMPACT -> if (i % 2 == 0) RunnerTrapRed else Color(0xFFFCA5A5)
                CitadelSparkStyle.ROYAL_TIER_BURST -> if (i % 2 == 0) ArenaGold else ArenaCyan
                CitadelSparkStyle.AEGIS_SHIELD -> if (i % 2 == 0) ArenaGold else Color.White
            }
            particles.add(
                CitadelParticle(
                    x = px,
                    y = py,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    life = 0f,
                    maxLife = if (style == CitadelSparkStyle.ROYAL_TIER_BURST) 0.72f else 0.48f,
                    radiusPx = if (style == CitadelSparkStyle.ROYAL_TIER_BURST) 5.5f else 4f,
                    color = col
                )
            )
        }
    }

    // 120fps Master Game Loop
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) return@LaunchedEffect
        var lastFrameNanos = 0L
        while (!gameState.isGameOver) {
            val frameNanos = withFrameNanos { it }
            if (lastFrameNanos != 0L) {
                val rawDt = ((frameNanos - lastFrameNanos) / 1_000_000_000.0f).coerceIn(0.001f, 0.05f)
                val stepRes = TitaniumCitadelEngine.step(gameState, rawDt)
                gameState = stepRes.nextState
                activeFlashConduit = stepRes.activeFlashConduit

                stepRes.sparks.forEach { sp -> spawnBurst(sp.normX, sp.normY, sp.style) }

                if (stepRes.xpGained > 0L || stepRes.pointsGained > 0L || stepRes.tierCompleted || stepRes.unlockedGrandmasterNow) {
                    roundTotalXp += stepRes.xpGained
                    var snap = ArenaProgressStore.updateBestCitadelStatsIfHigher(
                        context,
                        stepRes.nextState.score,
                        stepRes.nextState.tier
                    )
                    if (stepRes.xpGained > 0L) {
                        snap = ArenaProgressStore.awardXpAndTouchStreak(context, stepRes.xpGained)
                    }
                    onSnapshotUpdated(snap)
                }

                if (stepRes.tierCompleted || stepRes.unlockedGrandmasterNow) {
                    if (stepRes.unlockedGrandmasterNow) {
                        statusBannerMessage = "👑 GRANDMASTER ARCHITECT UNLOCKED!"
                    }
                    if (feedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        ArenaSynthAudio.playCitadelTierRoyalChord()
                    }
                } else if (stepRes.chamberTransitioned) {
                    if (feedbackEnabled) {
                        ArenaSynthAudio.playCitadelChamberSurge()
                    }
                } else if (stepRes.aegisReviveTriggered) {
                    statusBannerMessage = "🛡️ AEGIS CITADEL REVIVE! 3s SHIELD ACTIVE"
                    if (feedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        ArenaSynthAudio.playCitadelAegisRevive()
                    }
                } else if (stepRes.damageTaken) {
                    if (feedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        ArenaSynthAudio.playCitadelDamageBuzz()
                    }
                } else if (stepRes.diamondCollected) {
                    if (feedbackEnabled) {
                        ArenaSynthAudio.playCitadelLockShatter(isGoldBullseye = true)
                    }
                }

                var idx = particles.lastIndex
                while (idx >= 0) {
                    val p = particles[idx]
                    p.x += p.vx * rawDt
                    p.y += p.vy * rawDt
                    p.vx *= 0.94f
                    p.vy *= 0.94f
                    p.life += rawDt
                    if (p.life >= p.maxLife) {
                        particles.removeAt(idx)
                    }
                    idx--
                }
            }
            lastFrameNanos = frameNanos
        }
    }

    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver && !bestUpdatedOnFinish) {
            bestUpdatedOnFinish = true
            onSnapshotUpdated(
                ArenaProgressStore.updateBestCitadelStatsIfHigher(context, gameState.score, gameState.tier)
            )
        }
    }

    val formattedScore = remember(gameState.score) {
        NumberFormat.getNumberInstance(Locale.US).format(gameState.score)
    }
    val liveBestScore = max(snapshot.bestCitadelScore, gameState.score)
    val formattedBest = remember(liveBestScore) {
        NumberFormat.getNumberInstance(Locale.US).format(liveBestScore)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .navigationBarsPadding()
            .padding(top = 6.dp, bottom = 12.dp)
            .testTag("titanium_citadel_game_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        onSnapshotUpdated(
                            ArenaProgressStore.updateBestCitadelStatsIfHigher(context, gameState.score, gameState.tier)
                        )
                        onExitToHub()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ObsidianElevated)
                        .border(1.dp, ArenaGold.copy(alpha = 0.55f), CircleShape)
                        .testTag("citadel_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mind Arena Hub",
                        tint = DarkTextPrimary
                    )
                }

                Column {
                    Text(
                        text = "Titanium Citadel",
                        color = DarkTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = gameState.chamber.title,
                        color = ArenaCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val badgeColor = when {
                    gameState.isAegisShieldActive -> ArenaGold
                    gameState.isChronoSlowActive -> ArenaCyan
                    gameState.hasUnlockedGrandmaster -> ArenaGold
                    else -> ArenaCyan
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = badgeColor.copy(alpha = 0.16f),
                    border = BorderStroke(0.9.dp, badgeColor.copy(alpha = 0.65f))
                ) {
                    val label = when {
                        gameState.isAegisShieldActive -> "AEGIS SHIELD"
                        gameState.isChronoSlowActive -> "CHRONO 50%"
                        gameState.hasUnlockedGrandmaster -> "👑 GRANDMASTER"
                        else -> "${gameState.combo}x COMBO"
                    }
                    Text(
                        text = label,
                        color = badgeColor,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = {
                        feedbackEnabled = !feedbackEnabled
                        onSnapshotUpdated(ArenaProgressStore.setSoundAndHapticEnabled(context, feedbackEnabled))
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ObsidianElevated)
                        .border(1.dp, ArenaCyan.copy(alpha = 0.35f), CircleShape)
                        .testTag("citadel_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (feedbackEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = if (feedbackEnabled) "Mute Citadel Audio" else "Enable Citadel Audio",
                        tint = if (feedbackEnabled) ArenaCyan else DarkTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 4 Clean Glass HUD Pills (CITADEL TIER, SCORE, AEGIS LIVES, ALL-TIME BEST)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TowerHudPill(
                label = "TIER",
                value = "T${gameState.tier}",
                accentColor = ArenaGold,
                modifier = Modifier.weight(0.8f)
            )
            TowerHudPill(
                label = "SCORE",
                value = formattedScore,
                accentColor = ArenaCyan,
                modifier = Modifier.weight(1.1f)
            )
            TowerHudPill(
                label = "AEGIS",
                value = "${gameState.lives}",
                accentColor = if (gameState.lives <= 1) RunnerTrapRed else ArenaCyan,
                modifier = Modifier.weight(0.75f)
            )
            TowerHudPill(
                label = "BEST",
                value = formattedBest,
                accentColor = ArenaGold,
                modifier = Modifier.weight(1.1f)
            )
        }

        val chamberRemainingSec = kotlin.math.ceil(
            (TitaniumCitadelEngine.CHAMBER_DURATION_MS - gameState.chamberElapsedMs) / 1000.0
        ).toInt().coerceIn(0, 15)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LinearProgressIndicator(
                progress = {
                    (gameState.chamberElapsedMs.toFloat() / TitaniumCitadelEngine.CHAMBER_DURATION_MS.toFloat())
                        .coerceIn(0f, 1f)
                },
                color = if (gameState.isChronoSlowActive) ArenaCyan else ArenaGold,
                trackColor = Color(0xFF1F2937),
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
            )
            Text(
                text = "NEXT ${chamberRemainingSec}s",
                color = DarkTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        AnimatedVisibility(visible = statusBannerMessage != null) {
            statusBannerMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ArenaGold.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, ArenaGold.copy(alpha = 0.72f)),
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = msg,
                        color = ArenaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // 120fps Interactive 3-Chamber Citadel Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .weight(1f)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A0D16), Color(0xFF0B0C10), Color(0xFF141109))
                    )
                )
                .border(
                    width = if (gameState.isAegisShieldActive) 2.2.dp else 1.4.dp,
                    brush = Brush.linearGradient(
                        colors = when {
                            gameState.damageFlashMs > 0L -> listOf(RunnerTrapRed, ArenaGold)
                            gameState.isAegisShieldActive -> listOf(ArenaGold, Color.White, ArenaGold)
                            gameState.isChronoSlowActive -> listOf(ArenaCyan, Color.White, ArenaCyan)
                            else -> listOf(ArenaGold.copy(alpha = 0.78f), ArenaCyan.copy(alpha = 0.78f))
                        }
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .onGloballyPositioned { coords ->
                    canvasPixelSize = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                }
                .pointerInput(gameState.chamber, gameState.isGameOver) {
                    if (gameState.isGameOver) return@pointerInput
                    when (gameState.chamber) {
                        CitadelChamber.CHAMBER_1_LOCK_PICK -> {
                            detectTapGestures {
                                val res = TitaniumCitadelEngine.tapLockPick(gameState)
                                gameState = res.nextState
                                res.sparks.forEach { sp -> spawnBurst(sp.normX, sp.normY, sp.style) }
                                if (res.pointsGained > 0L || res.xpGained > 0L || res.unlockedGrandmasterNow) {
                                    roundTotalXp += res.xpGained
                                    var snap = ArenaProgressStore.updateBestCitadelStatsIfHigher(
                                        context,
                                        res.nextState.score,
                                        res.nextState.tier
                                    )
                                    if (res.xpGained > 0L) {
                                        snap = ArenaProgressStore.awardXpAndTouchStreak(context, res.xpGained)
                                    }
                                    onSnapshotUpdated(snap)
                                    if (res.unlockedGrandmasterNow) {
                                        statusBannerMessage = "👑 GRANDMASTER ARCHITECT UNLOCKED!"
                                    }
                                }
                                if (feedbackEnabled) {
                                    if (res.hit) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        if (res.stageCleared) {
                                            ArenaSynthAudio.playCitadelChamberSurge()
                                        } else {
                                            ArenaSynthAudio.playCitadelLockShatter(res.isGoldBullseye)
                                        }
                                    } else if (res.aegisReviveTriggered) {
                                        statusBannerMessage = "🛡️ AEGIS CITADEL REVIVE! 3s SHIELD ACTIVE"
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        ArenaSynthAudio.playCitadelAegisRevive()
                                    } else if (res.damageTaken) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        ArenaSynthAudio.playCitadelDamageBuzz()
                                    }
                                }
                            }
                        }
                        CitadelChamber.CHAMBER_2_FIREWALL_DODGE -> {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val w = size.width.toFloat().coerceAtLeast(1f)
                                gameState = TitaniumCitadelEngine.slideCoreTo(gameState, down.position.x / w)
                                do {
                                    val event = awaitPointerEvent()
                                    event.changes.forEach { change ->
                                        change.consume()
                                        gameState = TitaniumCitadelEngine.slideCoreTo(
                                            gameState,
                                            change.position.x / w
                                        )
                                    }
                                } while (event.changes.any { it.pressed })
                            }
                        }
                        CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE -> Unit
                    }
                }
                .testTag("citadel_stage_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                if (gameState.isChronoSlowActive) {
                    drawRect(color = ArenaCyan.copy(alpha = 0.08f))
                }
                if (gameState.isAegisShieldActive) {
                    drawRect(color = ArenaGold.copy(alpha = 0.10f))
                }
                if (gameState.damageFlashMs > 0L) {
                    drawRect(color = RunnerTrapRed.copy(alpha = 0.14f))
                }

                val gridStep = min(w, h) / 7f
                var gx = gridStep
                while (gx < w) {
                    drawLine(
                        color = ArenaCyan.copy(alpha = 0.05f),
                        start = Offset(gx, 0f),
                        end = Offset(gx, h),
                        strokeWidth = 1f
                    )
                    gx += gridStep
                }
                var gy = gridStep
                while (gy < h) {
                    drawLine(
                        color = ArenaGold.copy(alpha = 0.05f),
                        start = Offset(0f, gy),
                        end = Offset(w, gy),
                        strokeWidth = 1f
                    )
                    gy += gridStep
                }

                when (gameState.chamber) {
                    CitadelChamber.CHAMBER_1_LOCK_PICK -> {
                        val center = Offset(w * 0.5f, h * 0.52f)
                        val dialR = min(w, h) * 0.34f
                        val arcTopLeft = Offset(center.x - dialR, center.y - dialR)
                        val arcSize = Size(dialR * 2f, dialR * 2f)

                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = dialR + 12.dp.toPx(),
                            center = center,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        drawCircle(
                            color = ArenaCyan.copy(alpha = 0.25f),
                            radius = dialR,
                            center = center,
                            style = Stroke(width = 14.dp.toPx())
                        )

                        drawArc(
                            color = ArenaCyan,
                            startAngle = gameState.targetArcCenterDeg - gameState.targetArcHalfSpanDeg,
                            sweepAngle = gameState.targetArcHalfSpanDeg * 2f,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = ArenaGold,
                            startAngle = gameState.targetArcCenterDeg - gameState.goldBullseyeHalfSpanDeg,
                            sweepAngle = gameState.goldBullseyeHalfSpanDeg * 2f,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                        )

                        val needleRad = gameState.needleAngleDeg * (PI / 180.0)
                        val tip = Offset(
                            center.x + (cos(needleRad) * (dialR + 10.dp.toPx())).toFloat(),
                            center.y + (sin(needleRad) * (dialR + 10.dp.toPx())).toFloat()
                        )
                        drawLine(
                            color = ArenaGold.copy(alpha = 0.35f),
                            start = center,
                            end = tip,
                            strokeWidth = 10.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.White,
                            start = center,
                            end = tip,
                            strokeWidth = 3.2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(color = ArenaGold, radius = 14.dp.toPx(), center = center)
                        drawCircle(color = Color.White, radius = 6.dp.toPx(), center = center)

                        val promptLayout = textMeasurer.measure(
                            text = "TAP TO SHATTER (${gameState.locksRemainingInStage} LOCKS)",
                            style = TextStyle(
                                color = ArenaGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        )
                        drawText(
                            textLayoutResult = promptLayout,
                            topLeft = Offset(w * 0.5f - promptLayout.size.width * 0.5f, h * 0.08f)
                        )
                    }
                    CitadelChamber.CHAMBER_2_FIREWALL_DODGE -> {
                        drawLine(
                            color = ArenaCyan.copy(alpha = 0.25f),
                            start = Offset(w * 0.06f, h * TitaniumCitadelEngine.CORE_Y),
                            end = Offset(w * 0.94f, h * TitaniumCitadelEngine.CORE_Y),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                        )

                        gameState.firewallEntities.forEach { ent ->
                            val ey = ent.y * h
                            when (ent.kind) {
                                CitadelFirewallKind.LASER_WALL -> {
                                    val gapLeft = ((ent.x - ent.gapHalfWidth).coerceIn(0f, 1f)) * w
                                    val gapRight = ((ent.x + ent.gapHalfWidth).coerceIn(0f, 1f)) * w
                                    if (gapLeft > 4f) {
                                        drawRoundRect(
                                            color = RunnerTrapRed.copy(alpha = 0.28f),
                                            topLeft = Offset(0f, ey - 9.dp.toPx()),
                                            size = Size(gapLeft, 18.dp.toPx()),
                                            cornerRadius = CornerRadius(6.dp.toPx())
                                        )
                                        drawRoundRect(
                                            color = RunnerTrapRed,
                                            topLeft = Offset(0f, ey - 4.dp.toPx()),
                                            size = Size(gapLeft, 8.dp.toPx()),
                                            cornerRadius = CornerRadius(4.dp.toPx())
                                        )
                                    }
                                    if (w - gapRight > 4f) {
                                        drawRoundRect(
                                            color = RunnerTrapRed.copy(alpha = 0.28f),
                                            topLeft = Offset(gapRight, ey - 9.dp.toPx()),
                                            size = Size(w - gapRight, 18.dp.toPx()),
                                            cornerRadius = CornerRadius(6.dp.toPx())
                                        )
                                        drawRoundRect(
                                            color = RunnerTrapRed,
                                            topLeft = Offset(gapRight, ey - 4.dp.toPx()),
                                            size = Size(w - gapRight, 8.dp.toPx()),
                                            cornerRadius = CornerRadius(4.dp.toPx())
                                        )
                                    }
                                }
                                CitadelFirewallKind.GOLD_DIAMOND -> {
                                    val dx = ent.x * w
                                    val dr = 13.dp.toPx()
                                    val dPath = Path().apply {
                                        moveTo(dx, ey - dr)
                                        lineTo(dx + dr * 0.8f, ey)
                                        lineTo(dx, ey + dr)
                                        lineTo(dx - dr * 0.8f, ey)
                                        close()
                                    }
                                    drawPath(
                                        path = dPath,
                                        brush = Brush.linearGradient(listOf(Color.White, ArenaGold))
                                    )
                                }
                            }
                        }

                        val px = gameState.playerX * w
                        val py = TitaniumCitadelEngine.CORE_Y * h
                        val coreR = 19.dp.toPx()
                        if (gameState.isAegisShieldActive) {
                            drawCircle(
                                color = ArenaGold.copy(alpha = 0.35f),
                                radius = coreR * 1.75f,
                                center = Offset(px, py),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White, ArenaCyan, Color(0xFF0284C7)),
                                center = Offset(px - coreR * 0.25f, py - coreR * 0.25f),
                                radius = coreR * 1.2f
                            ),
                            radius = coreR,
                            center = Offset(px, py)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.85f),
                            radius = coreR,
                            center = Offset(px, py),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        val slideHint = textMeasurer.measure(
                            text = "SLIDE TO WEAVE LASER WALLS & COLLECT GOLD",
                            style = TextStyle(
                                color = ArenaCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.7.sp
                            )
                        )
                        drawText(
                            textLayoutResult = slideHint,
                            topLeft = Offset(w * 0.5f - slideHint.size.width * 0.5f, h * 0.08f)
                        )
                    }
                    CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE -> {
                        val headerText = if (gameState.overrideSubPhase == OverrideSubPhase.FLASHING) {
                            "OBSERVE QUANTUM SEQUENCE (${gameState.overrideSequence.size})"
                        } else {
                            "REPEAT OVERRIDE CODE (${gameState.overrideInputIndex}/${gameState.overrideSequence.size})"
                        }
                        val headerLayout = textMeasurer.measure(
                            text = headerText,
                            style = TextStyle(
                                color = if (gameState.overrideSubPhase == OverrideSubPhase.FLASHING) ArenaGold else ArenaCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        )
                        drawText(
                            textLayoutResult = headerLayout,
                            topLeft = Offset(w * 0.5f - headerLayout.size.width * 0.5f, h * 0.08f)
                        )
                    }
                }

                particles.forEach { p ->
                    val alpha = (1f - p.life / p.maxLife).coerceIn(0f, 1f)
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.radiusPx * (0.45f + 0.55f * alpha),
                        center = Offset(p.x, p.y)
                    )
                }
            }

            if (gameState.chamber == CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE && !gameState.isGameOver) {
                val conduitMeta = listOf(
                    Triple("α CYAN", ArenaCyan, 0),
                    Triple("β GOLD", ArenaGold, 1),
                    Triple("γ EMERALD", NeonMint, 2),
                    Triple("Ω TITAN", Color(0xFF38BDF8), 3)
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.86f)
                        .padding(top = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    for (rowIdx in 0..1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            for (colIdx in 0..1) {
                                val (label, accent, idx) = conduitMeta[rowIdx * 2 + colIdx]
                                val isLit = activeFlashConduit == idx
                                val canTap = gameState.overrideSubPhase == OverrideSubPhase.INPUT
                                Surface(
                                    onClick = {
                                        if (canTap) {
                                            val res = TitaniumCitadelEngine.tapConduit(gameState, idx)
                                            gameState = res.nextState
                                            res.sparks.forEach { sp -> spawnBurst(sp.normX, sp.normY, sp.style) }
                                            if (res.pointsGained > 0L || res.xpGained > 0L || res.unlockedGrandmasterNow) {
                                                roundTotalXp += res.xpGained
                                                var snap = ArenaProgressStore.updateBestCitadelStatsIfHigher(
                                                    context,
                                                    res.nextState.score,
                                                    res.nextState.tier
                                                )
                                                if (res.xpGained > 0L) {
                                                    snap = ArenaProgressStore.awardXpAndTouchStreak(context, res.xpGained)
                                                }
                                                onSnapshotUpdated(snap)
                                                if (res.unlockedGrandmasterNow) {
                                                    statusBannerMessage = "👑 GRANDMASTER ARCHITECT UNLOCKED!"
                                                }
                                            }
                                            if (feedbackEnabled) {
                                                if (res.tierCompleted) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    ArenaSynthAudio.playCitadelTierRoyalChord()
                                                } else if (res.hit) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    ArenaSynthAudio.playCitadelConduitTone(idx)
                                                } else if (res.aegisReviveTriggered) {
                                                    statusBannerMessage = "🛡️ AEGIS CITADEL REVIVE! 3s SHIELD ACTIVE"
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    ArenaSynthAudio.playCitadelAegisRevive()
                                                } else if (res.damageTaken) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    ArenaSynthAudio.playCitadelDamageBuzz()
                                                }
                                            }
                                        }
                                    },
                                    enabled = canTap,
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (isLit) accent.copy(alpha = 0.58f) else ObsidianElevated.copy(alpha = 0.90f),
                                    border = BorderStroke(
                                        width = if (isLit) 2.5.dp else 1.4.dp,
                                        color = if (isLit) Color.White else accent.copy(alpha = if (canTap) 0.90f else 0.45f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(86.dp)
                                        .testTag("citadel_conduit_$idx")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            color = if (isLit) Color.White else accent,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = gameState.transitionBannerMs > 0L && !gameState.isGameOver,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = ObsidianElevated.copy(alpha = 0.92f),
                    border = BorderStroke(1.2.dp, ArenaGold.copy(alpha = 0.78f))
                ) {
                    Text(
                        text = "${gameState.chamber.title.uppercase()} • TIER ${gameState.tier}",
                        color = ArenaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            if (gameState.isGameOver) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xDD0B0C10))
                        .padding(20.dp)
                        .testTag("citadel_game_over_overlay"),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ObsidianElevated,
                        border = BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(listOf(ArenaGold, ArenaCyan, ArenaGold))
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "CITADEL BREACHED",
                                color = RunnerTrapRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "Score: $formattedScore",
                                color = ArenaGold,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Citadel Tier ${gameState.tier} • +$roundTotalXp XP Earned",
                                color = ArenaCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (gameState.hasUnlockedGrandmaster) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = ArenaGold.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, ArenaGold)
                                ) {
                                    Text(
                                        text = "👑 GRANDMASTER ARCHITECT RANK EARNED",
                                        color = ArenaGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            if (!secondChanceUsedThisRun) {
                                ArenaRewardedButton(
                                    text = "Second Chance — Continue",
                                    onClick = {
                                        AdManager.showRewardedAd("citadel_second_chance") {
                                            gameState = gameState.copy(
                                                lives = 1,
                                                isGameOver = false,
                                                aegisShieldRemainingMs = 3_000L,
                                                firewallEntities = emptyList()
                                            )
                                            bestUpdatedOnFinish = false
                                            secondChanceUsedThisRun = true
                                        }
                                    },
                                    accentColor = ArenaGold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("citadel_rewarded_second_chance_button")
                                )
                            }

                            if (roundTotalXp > 0L) {
                                ArenaRewardedButton(
                                    text = if (doubleXpClaimedThisRound) "2X Arena XP Claimed ✓" else "Double Arena XP (2X)",
                                    enabled = !doubleXpClaimedThisRound,
                                    onClick = {
                                        AdManager.showRewardedAd("citadel_double_xp") {
                                            val bonus = roundTotalXp
                                            if (bonus > 0L) {
                                                onSnapshotUpdated(ArenaProgressStore.awardXpAndTouchStreak(context, bonus))
                                                roundTotalXp += bonus
                                            }
                                            doubleXpClaimedThisRound = true
                                        }
                                    },
                                    accentColor = ArenaCyan,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("citadel_double_xp_button")
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        AdManager.recordGameRoundCompleted("citadel_round_complete") {
                                            onExitToHub()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, ArenaCyan.copy(alpha = 0.6f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("citadel_gameover_hub_button")
                                ) {
                                    Text("Hub", color = ArenaCyan, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        AdManager.recordGameRoundCompleted("citadel_round_complete") {
                                            gameState = TitaniumCitadelEngine.newGame()
                                            particles.clear()
                                            bestUpdatedOnFinish = false
                                            roundTotalXp = 0L
                                            secondChanceUsedThisRun = false
                                            doubleXpClaimedThisRound = false
                                            statusBannerMessage = null
                                            onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ArenaGold,
                                        contentColor = ObsidianBg
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .height(48.dp)
                                        .testTag("citadel_play_again_button")
                                ) {
                                    Text("Play Again", fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Power-Up HUD Bar: Chrono-Slow Matrix, Aegis Citadel Revive & Restart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    if (gameState.chronoSlowPowerUpRemaining > 0 && !gameState.isChronoSlowActive && !gameState.isGameOver) {
                        gameState = TitaniumCitadelEngine.activateChronoSlow(gameState)
                        statusBannerMessage = "⏳ CHRONO-SLOW MATRIX • 50% SPEED FOR 5s"
                        if (feedbackEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            ArenaSynthAudio.playPowerUpActivate()
                        }
                    } else if (gameState.chronoSlowPowerUpRemaining <= 0 && !gameState.isChronoSlowActive) {
                        statusBannerMessage = "Chrono-Slow used this match"
                    }
                },
                border = BorderStroke(
                    1.dp,
                    when {
                        gameState.isChronoSlowActive -> ArenaCyan
                        gameState.chronoSlowPowerUpRemaining > 0 -> ArenaCyan
                        else -> DarkTextSecondary.copy(alpha = 0.35f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (gameState.isChronoSlowActive) ArenaCyan.copy(alpha = 0.16f) else Color.Transparent,
                    contentColor = if (gameState.chronoSlowPowerUpRemaining > 0 || gameState.isChronoSlowActive) ArenaCyan else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("citadel_chrono_slow_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Chrono-Slow Matrix Power-Up",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        gameState.isChronoSlowActive -> "Slow ${kotlin.math.ceil(gameState.chronoSlowRemainingMs / 1000.0).toInt()}s"
                        gameState.chronoSlowPowerUpRemaining > 0 -> "Chrono-Slow"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = {
                    if (gameState.aegisRevivePowerUpRemaining > 0 && !gameState.isAegisShieldActive && !gameState.isGameOver) {
                        gameState = TitaniumCitadelEngine.activateAegisShield(gameState)
                        statusBannerMessage = "🛡️ AEGIS SHIELD ENGAGED FOR 3s!"
                        if (feedbackEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            ArenaSynthAudio.playCitadelAegisRevive()
                        }
                    } else if (gameState.aegisRevivePowerUpRemaining <= 0 && !gameState.isAegisShieldActive) {
                        statusBannerMessage = "Aegis Citadel Revive used this match — auto-recharges next match!"
                    }
                },
                border = BorderStroke(
                    1.dp,
                    when {
                        gameState.isAegisShieldActive -> ArenaGold
                        gameState.aegisRevivePowerUpRemaining > 0 -> ArenaGold
                        else -> DarkTextSecondary.copy(alpha = 0.35f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (gameState.isAegisShieldActive) ArenaGold.copy(alpha = 0.16f) else Color.Transparent,
                    contentColor = if (gameState.aegisRevivePowerUpRemaining > 0 || gameState.isAegisShieldActive) ArenaGold else DarkTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("citadel_aegis_revive_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Aegis Citadel Revive Power-Up",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        gameState.isAegisShieldActive -> "Aegis ${kotlin.math.ceil(gameState.aegisShieldRemainingMs / 1000.0).toInt()}s"
                        gameState.aegisRevivePowerUpRemaining > 0 -> "Aegis Revive"
                        else -> "Used"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    gameState = TitaniumCitadelEngine.newGame()
                    particles.clear()
                    bestUpdatedOnFinish = false
                    roundTotalXp = 0L
                    secondChanceUsedThisRun = false
                    doubleXpClaimedThisRound = false
                    statusBannerMessage = null
                    onSnapshotUpdated(ArenaProgressStore.recordPlaySession(context))
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaGold)
                    .testTag("citadel_restart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Restart Titanium Citadel",
                    tint = ObsidianBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}









