package com.example

import android.Manifest
import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.KoshvantaDatabase
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.BillReminderEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.FinancialBook
import com.example.data.model.QuickPresetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppPreferences
import com.example.data.repository.BillToggleResult
import com.example.data.repository.KoshvantaRepository
import com.example.domain.calculator.WorkHourCostCalculator
import com.example.domain.util.BackupPayload
import com.example.domain.util.BiometricHelper
import com.example.domain.util.GoogleSignInOutcome
import com.example.domain.util.NotificationHelper
import com.example.domain.util.ProfileSyncManager
import com.example.domain.util.ReportExporter
import com.example.domain.util.SoundEffectManager
import com.example.domain.voice.VoiceTransactionParser
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.ui.components.ActiveImpulseCooldown
import com.example.ui.components.ArenaGameCatalog
import com.example.ui.components.ArenaGameId
import com.example.ui.components.ArenaLevelCalculator
import com.example.ui.components.ArenaProgressStore
import com.example.ui.components.BreakerBall
import com.example.ui.components.BreakerBrick
import com.example.ui.components.BreakerBrickType
import com.example.ui.components.BreakerCapsule
import com.example.ui.components.BreakerCapsuleType
import com.example.ui.components.BreakerGameState
import com.example.ui.components.ChronoPhase
import com.example.ui.components.ChronoVaultEngine
import com.example.ui.components.ChronoVaultMode
import com.example.ui.components.CountryCardEngine
import com.example.ui.components.CurrencyRunnerEngine
import com.example.ui.components.CitadelChamber
import com.example.ui.components.CitadelFirewallEntity
import com.example.ui.components.CitadelFirewallKind
import com.example.ui.components.CyberWealth2048Engine
import com.example.ui.components.CyberWealthBoardState
import com.example.ui.components.GravityOrbitEngine
import com.example.ui.components.GravityOrbitState
import com.example.ui.components.GridPos
import com.example.ui.components.HexCoord
import com.example.ui.components.HexaCrystalColor
import com.example.ui.components.HexaPiece
import com.example.ui.components.HexaVaultEngine
import com.example.ui.components.HexaVaultState
import com.example.ui.components.ImpulseCooldownCalculator
import com.example.ui.components.ImpulseCooldownStore
import com.example.ui.components.OrbitEntity
import com.example.ui.components.OrbitEntityKind
import com.example.ui.components.OverrideSubPhase
import com.example.ui.components.TitaniumCitadelEngine
import com.example.ui.components.TitaniumCitadelState
import com.example.ui.components.LaserDir
import com.example.ui.components.MathDuelMode
import com.example.ui.components.MathDuelState
import com.example.ui.components.MetalCardFinish
import com.example.ui.components.MetalCardVaultCalculator
import com.example.ui.components.MetalCardVaultStore
import com.example.ui.components.NeonBreakerEngine
import com.example.ui.components.NetWorthCurveCalculator
import com.example.ui.components.QuantumCell
import com.example.ui.components.QuantumCellKind
import com.example.ui.components.QuantumLaserEngine
import com.example.ui.components.QuantumLevelState
import com.example.ui.components.RunnerCollisionType
import com.example.ui.components.RunnerCurrencyZone
import com.example.ui.components.RunnerGate
import com.example.ui.components.RunnerGateKind
import com.example.ui.components.SafeSpeechWrapper
import com.example.ui.components.SwipeDirection
import com.example.ui.components.TitaniumTowerEngine
import com.example.ui.components.TowerAltitudeBiome
import com.example.ui.components.TowerDropOutcome
import com.example.ui.components.UndoPowerUpState
import com.example.ui.components.WarrantyAlertScheduler
import com.example.ui.components.WarrantyCalculator
import com.example.ui.components.WarrantyItem
import com.example.ui.components.WarrantySeverity
import com.example.ui.components.WarrantyVaultStore
import com.example.ui.components.WealthMathDuelEngine
import com.example.ui.components.WealthTile
import com.example.ui.screens.NoSpendCalendarCalculator
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.LightAmberBronze
import com.example.ui.theme.LightCanvasBg
import com.example.ui.theme.LightCardBorder
import com.example.ui.theme.LightEmeraldTeal
import com.example.ui.theme.LightSurfaceWhite
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.NeonMint
import com.example.ui.theme.RoyalGold
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Koshvanta", appName)
    }

    @Test
    fun `backup json export and sha256 validation roundtrip succeeds`() {
        val payload = BackupPayload(
            accounts = listOf(AccountEntity(id = 1L, name = "Main Bank", type = AccountType.BANK, balance = 500.0)),
            categories = emptyList(),
            transactions = emptyList(),
            budgets = emptyList(),
            goals = emptyList(),
            bills = emptyList(),
            debts = emptyList(),
            presets = listOf(QuickPresetEntity(id = 1L, title = "Coffee", emoji = "☕", amountUsd = 4.5, categoryName = "Food & Drinks"))
        )
        val json = ReportExporter.generateJsonBackupString(payload)
        val parsed = ReportExporter.validateAndParseBackupJson(json)
        assertTrue(parsed.isSuccess)
        assertEquals("Main Bank", parsed.getOrThrow().accounts.first().name)
        assertEquals("☕", parsed.getOrThrow().presets.first().emoji)

        // Tampered JSON fails SHA-256 integrity validation
        val tampered = json.replace("Main Bank", "Fake Bank")
        val tamperedResult = ReportExporter.validateAndParseBackupJson(tampered)
        assertTrue(tamperedResult.isFailure)
    }

    @Test
    fun `sound preference syncs with SoundEffectManager and notification channels initialize`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)
        assertTrue(prefs.soundEffectsEnabled.value)

        prefs.setSoundEffectsEnabled(false)
        assertFalse(prefs.soundEffectsEnabled.value)
        assertFalse(SoundEffectManager.isEnabled)

        prefs.setSoundEffectsEnabled(true)
        assertTrue(prefs.soundEffectsEnabled.value)
        assertTrue(SoundEffectManager.isEnabled)

        NotificationHelper.ensureChannels(context)
    }

    @Test
    fun `daily login streak tracks consecutive days accurately and resets on missed day`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)

        // Fresh install starts at 0 before first login check-in
        assertEquals(0, prefs.streakCount.value)

        val day1Ms = 1_762_992_000_000L // Fixed UTC timestamp Day 1
        val oneDayMs = 24L * 3600 * 1000

        // Day 1 check-in -> streak becomes 1
        assertTrue(prefs.recordDailyActivityForTime(day1Ms))
        assertEquals(1, prefs.streakCount.value)
        assertEquals(1, prefs.personalBestStreak.value)

        // Same day re-open -> idempotent, stays 1
        assertFalse(prefs.recordDailyActivityForTime(day1Ms + 3600_000L))
        assertEquals(1, prefs.streakCount.value)

        // Consecutive Day 2 -> increments to 2
        assertTrue(prefs.recordDailyActivityForTime(day1Ms + oneDayMs))
        assertEquals(2, prefs.streakCount.value)
        assertEquals(2, prefs.personalBestStreak.value)

        // Missed Day 3, logged in Day 4 -> active streak resets to 1, best stays 2
        assertTrue(prefs.recordDailyActivityForTime(day1Ms + (3 * oneDayMs)))
        assertEquals(1, prefs.streakCount.value)
        assertEquals(2, prefs.personalBestStreak.value)
        assertEquals(3, prefs.activeLoginDates.value.size)
        assertTrue(prefs.lastLoginDate.value.isNotBlank())

        // Continue 14 more consecutive days (Days 5..18 -> streak reaches 15 = Level 2!)
        for (offset in 4..17) {
            assertTrue(prefs.recordDailyActivityForTime(day1Ms + (offset * oneDayMs)))
        }
        assertEquals(15, prefs.streakCount.value)
        assertEquals(15, prefs.personalBestStreak.value)
        assertEquals(17, prefs.activeLoginDates.value.size)
    }

    @Test
    fun `voice auto-creates missing UPI wallet and custom neon section in Room DB`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, KoshvantaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repo = KoshvantaRepository(db.dao())
            repo.checkAndPreloadInitialData()

            val accountsBefore = repo.getAccounts(FinancialBook.PERSONAL.id).first()
            val categoriesBefore = repo.getCategories().first()

            val parsed = VoiceTransactionParser.parse("Paid 250 for Gym via GPay UPI")
            assertNull(VoiceTransactionParser.resolveMatchingAccount(parsed, accountsBefore))
            assertNull(VoiceTransactionParser.resolveMatchingCategory(parsed, categoriesBefore))

            val newWalletId = repo.insertAccountAndReturnId(
                AccountEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    name = parsed.paymentModeLabel,
                    type = parsed.preferredAccountType,
                    balance = 0.0,
                    cardColorHex = "#00B894"
                )
            )
            val newCategoryId = repo.insertCategory(
                CategoryEntity(
                    name = parsed.categoryHint,
                    iconName = parsed.suggestedIconName,
                    colorHex = parsed.suggestedColorHex,
                    isExpense = true
                )
            )
            repo.addTransaction(
                TransactionEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    accountId = newWalletId,
                    categoryId = newCategoryId,
                    amount = parsed.amountDisplay,
                    type = parsed.transactionType,
                    note = parsed.note,
                    timestamp = System.currentTimeMillis()
                )
            )

            val accountsAfter = repo.getAccounts(FinancialBook.PERSONAL.id).first()
            val categoriesAfter = repo.getCategories().first()
            val detailsAfter = repo.getTransactionsWithDetails(FinancialBook.PERSONAL.id).first()

            val gpayWallet = accountsAfter.firstOrNull { it.name == "GPay UPI" }
            assertNotNull(gpayWallet)
            assertEquals(-250.0, gpayWallet!!.balance, 0.01)

            val gymCat = categoriesAfter.firstOrNull { it.id == newCategoryId }
            assertNotNull(gymCat)
            assertEquals("fitness_center", gymCat!!.iconName)
            assertEquals("#00FFCC", gymCat.colorHex)

            val savedTx = detailsAfter.firstOrNull { it.transaction.categoryId == newCategoryId }
            assertNotNull(savedTx)
            assertEquals("GPay UPI", savedTx!!.account?.name)
        } finally {
            db.close()
        }
    }

    @Test
    fun `safe speech wrapper never crashes when speech hardware is unavailable`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var fallbackMessage: String? = null
        val wrapper = SafeSpeechWrapper(
            context = context,
            onReady = {},
            onRmsChanged = {},
            onPartialResult = {},
            onFinalResult = {},
            onUnavailableOrFallback = { fallbackMessage = it }
        )
        // Must execute safely without throwing even when Android SpeechRecognizer service is absent
        wrapper.startListening()
        wrapper.stopListening()
        wrapper.stopAndDestroy()
        assertFalse(wrapper.isAvailable())
        assertTrue(!fallbackMessage.isNullOrBlank())
    }

    @Test
    fun `MainActivity inherits FragmentActivity and BiometricHelper never throws`() {
        assertTrue(FragmentActivity::class.java.isAssignableFrom(MainActivity::class.java))
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Robolectric SDK 36 may report available=true or false depending on shadow config;
        // either way, isBiometricAvailable and authenticate must never throw.
        BiometricHelper.isBiometricAvailable(context)
        BiometricHelper.authenticate(
            context = context,
            onSuccess = {},
            onError = {}
        )
    }

    @Test
    fun `async coroutine PIN save verify null guards and corrupted hash recovery succeed`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)

        // Initial load has PIN disabled safely
        val initial = prefs.loadSecurityStateAsync()
        assertFalse(initial.isPinEnabled)
        assertFalse(prefs.verifyPinAsync(null))
        assertFalse(prefs.verifyPinAsync(""))
        assertFalse(prefs.verifyPinAsync("12ab"))

        // Save 4-digit PIN asynchronously and verify
        assertTrue(prefs.saveAndEnablePinAsync("2580"))
        prefs.setBiometricEnabledAsync(true)
        val loaded = prefs.loadSecurityStateAsync()
        assertTrue(loaded.isPinEnabled)
        assertTrue(loaded.isBiometricEnabled)
        assertTrue(loaded.hasValidPinHash)
        assertTrue(prefs.verifyPinAsync("2580"))
        assertFalse(prefs.verifyPinAsync("0000"))

        // Corrupt scenario: pin_enabled=true while pin_hash cleared -> auto-recovers safely
        context.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)
            .edit()
            .remove("pin_hash")
            .putBoolean("pin_enabled", true)
            .commit()
        val recovered = prefs.loadSecurityStateAsync()
        assertFalse(recovered.isPinEnabled)
        assertFalse(recovered.hasValidPinHash)

        // Clean disable
        prefs.disablePinAsync()
        assertFalse(prefs.isPinEnabled.value)
    }

    @Test
    fun `impulse cooldown calculator and store compute days of savings and rescue stats`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Monthly income $4500, expense $1500 -> surplus $3000 -> $100/day savings rate
        val rateInfo = ImpulseCooldownCalculator.computeDailySavingsRateUsd(
            monthlyIncomeUsd = 4500.0,
            monthlyExpenseUsd = 1500.0,
            monthlyBudgetUsd = 2000.0
        )
        assertTrue(rateInfo.isLiveLedgerRate)
        assertEquals(100.0, rateInfo.dailyRateUsd, 0.01)

        // Item priced at $250 costs 2.5 days of savings
        val daysCost = ImpulseCooldownCalculator.computeDaysOfSavings(250.0, rateInfo.dailyRateUsd)
        assertEquals(2.5, daysCost, 0.01)
        assertEquals("2.5 days", ImpulseCooldownCalculator.formatDaysCostLabel(daysCost))

        // 24h and 48h countdown formatting
        assertEquals("24:00:00", ImpulseCooldownCalculator.formatCountdownHms(24 * 3600_000L))
        assertEquals("48:00:00", ImpulseCooldownCalculator.formatCountdownHms(48 * 3600_000L))

        // Persist active cooldown and record completion
        val now = 1_762_992_000_000L
        val item = ActiveImpulseCooldown(
            itemName = "Headphones",
            priceUsd = 250.0,
            durationHours = 24,
            startTimeMillis = now,
            endTimeMillis = now + 24 * 3600_000L
        )
        ImpulseCooldownStore.saveActive(context, item)
        val loaded = ImpulseCooldownStore.loadActive(context)
        assertNotNull(loaded)
        assertEquals("Headphones", loaded!!.itemName)
        assertEquals(24, loaded.durationHours)

        val statsAfter = ImpulseCooldownStore.recordSaved(context, "Headphones", 250.0)
        assertEquals(250.0, statsAfter.totalSavedUsd, 0.01)
        assertEquals(1, statsAfter.rescuedCount)
        assertNull(ImpulseCooldownStore.loadActive(context))
    }

    @Test
    fun `safe speech wrapper checks permission and survives repeated reboot-style restarts`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val fallbackMessages = mutableListOf<String>()
        val wrapper = SafeSpeechWrapper(
            context = context,
            onReady = {},
            onRmsChanged = {},
            onPartialResult = {},
            onFinalResult = {},
            onUnavailableOrFallback = { fallbackMessages.add(it) }
        )
        assertFalse(wrapper.hasRecordAudioPermission())
        // Repeated start/stop cycles never throw even when permission/service unavailable
        repeat(3) {
            wrapper.startListening()
            wrapper.stopListening()
            wrapper.stopAndDestroy()
        }
        assertEquals(3, fallbackMessages.size)
    }

    @Test
    fun `warranty vault countdown math store and WorkManager 15-day alert scheduling work`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)

        val dayMs = 24L * 3600_000L
        val now = 1_762_992_000_000L

        // > 45 days -> PROTECTED (#00FFCC)
        val safeInfo = WarrantyCalculator.computeCountdown(now + 90 * dayMs, now)
        assertEquals(WarrantySeverity.PROTECTED, safeInfo.severity)
        assertEquals(0xFF00FFCC, safeInfo.accentColorHex)

        // 16..45 days -> CAUTION (#F59E0B)
        val cautionInfo = WarrantyCalculator.computeCountdown(now + 30 * dayMs, now)
        assertEquals(WarrantySeverity.CAUTION, cautionInfo.severity)

        // <= 15 days -> ALERT_15_DAY (#FF3366)
        val alertInfo = WarrantyCalculator.computeCountdown(now + 10 * dayMs, now)
        assertEquals(WarrantySeverity.ALERT_15_DAY, alertInfo.severity)
        assertEquals(0xFFFF3366, alertInfo.accentColorHex)

        // <= 0 days -> EXPIRED (#FF3366)
        val expiredInfo = WarrantyCalculator.computeCountdown(now - 2 * dayMs, now)
        assertEquals(WarrantySeverity.EXPIRED, expiredInfo.severity)
        assertNull(WarrantyCalculator.compute15DayAlertDelayMillis(now - 2 * dayMs, now))

        // 15-day alert delay for an expiry 60 days out should equal 45 days in ms
        val delay45d = WarrantyCalculator.compute15DayAlertDelayMillis(now + 60 * dayMs, now)
        assertNotNull(delay45d)
        assertEquals(45 * dayMs, delay45d!!)

        // Persist item & enqueue unique WorkManager job
        val item = WarrantyItem(
            id = "test-warranty-1",
            productName = "MacBook Pro 16",
            purchaseDateMillis = now,
            warrantyMonths = 12,
            expiryDateMillis = WarrantyCalculator.computeExpiryDateMillis(now, 12)
        )
        val savedList = WarrantyVaultStore.upsertItem(context, item)
        assertEquals(1, savedList.size)
        assertTrue(WarrantyAlertScheduler.schedule15DayAlert(context, item, now))

        val infos = WorkManager.getInstance(context).getWorkInfosForUniqueWork("warranty_alert_${item.id}").get()
        assertEquals(1, infos.size)
        assertEquals(WorkInfo.State.ENQUEUED, infos.first().state)

        // Delete item & cancel WorkManager alert
        WarrantyAlertScheduler.cancelAlert(context, item.id)
        val afterDelete = WarrantyVaultStore.deleteItem(context, item.id)
        assertTrue(afterDelete.isEmpty())
    }

    @Test
    fun `MainActivity FragmentActivity handles 32-bit ActivityResult requestCodes without crashing`() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        // Prior fragment:1.1.0 threw "IllegalArgumentException: Can only use lower 16 bits for requestCode"
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 0x0001_0001)
    }

    @Test
    fun `net worth curve dynamically moves UP on income and DOWN on expense with no fake points`() {
        // 1. Empty transactions -> single live point matching current balance (no static fake points)
        val emptyCurve = NetWorthCurveCalculator.buildCurve(
            currentNetWorthUsd = 250.0,
            transactions = emptyList()
        )
        assertEquals(1, emptyCurve.size)
        assertEquals(250.0, emptyCurve.first().netWorthUsd, 0.01)

        // 2. Chronological Income (+1000) and Expense (-350) starting from 500 -> ends at 1150
        val t1 = 1_762_992_000_000L
        val t2 = t1 + 3_600_000L
        val txList = listOf(
            TransactionWithDetails(
                transaction = TransactionEntity(
                    id = 2L,
                    bookId = FinancialBook.PERSONAL.id,
                    accountId = 1L,
                    categoryId = 1L,
                    amount = 350.0,
                    type = TransactionType.EXPENSE,
                    note = "Rent & Bills",
                    timestamp = t2
                ),
                category = null,
                account = null
            ),
            TransactionWithDetails(
                transaction = TransactionEntity(
                    id = 1L,
                    bookId = FinancialBook.PERSONAL.id,
                    accountId = 1L,
                    categoryId = 5L,
                    amount = 1000.0,
                    type = TransactionType.INCOME,
                    note = "Salary",
                    timestamp = t1
                ),
                category = null,
                account = null
            )
        )

        val curve = NetWorthCurveCalculator.buildCurve(
            currentNetWorthUsd = 1150.0,
            transactions = txList
        )
        assertEquals(3, curve.size)
        // Opening balance before (+1000 - 350 = +650) -> 1150 - 650 = 500
        assertEquals(500.0, curve[0].netWorthUsd, 0.01)
        // After Income (+1000) -> moves UP to 1500
        assertEquals(1500.0, curve[1].netWorthUsd, 0.01)
        assertTrue(curve[1].netWorthUsd > curve[0].netWorthUsd)
        assertEquals(1000.0, curve[1].deltaUsd, 0.01)
        // After Expense (-350) -> moves DOWN to 1150
        assertEquals(1150.0, curve[2].netWorthUsd, 0.01)
        assertTrue(curve[2].netWorthUsd < curve[1].netWorthUsd)
        assertEquals(-350.0, curve[2].deltaUsd, 0.01)
    }

    @Test
    fun `demo_wiped persists in SharedPreferences and prevents demo reload on restart`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs1 = AppPreferences(context)
        val db = Room.inMemoryDatabaseBuilder(context, KoshvantaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repo = KoshvantaRepository(db.dao())

            // Initial boot loads demo data
            prefs1.setDemoWiped(false)
            repo.checkAndPreloadInitialData(isDemoWiped = prefs1.isDemoWiped())
            assertTrue(repo.getAccounts(FinancialBook.PERSONAL.id).first().isNotEmpty())

            // User wipes demo data -> persists demo_wiped=true
            prefs1.setDemoWipedAsync(true)
            repo.wipeAllUserData()
            assertTrue(repo.getAccounts(FinancialBook.PERSONAL.id).first().isEmpty())

            // Simulate full app restart with new AppPreferences instance
            val prefsAfterRestart = AppPreferences(context)
            assertTrue(prefsAfterRestart.isDemoWiped())
            repo.checkAndPreloadInitialData(isDemoWiped = prefsAfterRestart.isDemoWiped())

            // Demo data MUST NOT come back; categories remain available for user's real entries
            assertTrue(repo.getAccounts(FinancialBook.PERSONAL.id).first().isEmpty())
            assertTrue(repo.getTransactions(FinancialBook.PERSONAL.id).first().isEmpty())
            assertTrue(repo.getCategories().first().isNotEmpty())
        } finally {
            db.close()
        }
    }

    @Test
    fun `no-spend golden calendar detects completed zero-expense days and computes streak`() {
        val dayMs = 24L * 3600_000L
        val cal = java.util.Calendar.getInstance().apply {
            set(2026, java.util.Calendar.NOVEMBER, 14, 12, 0, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val now = cal.timeInMillis

        // Expense 4 days ago breaks streak beyond 3 days; Income 2 days ago does NOT break golden no-spend badge!
        val txs = listOf(
            TransactionWithDetails(
                transaction = TransactionEntity(
                    id = 1L,
                    bookId = FinancialBook.PERSONAL.id,
                    accountId = 1L,
                    categoryId = 1L,
                    amount = 75.0,
                    type = TransactionType.EXPENSE,
                    note = "Dinner",
                    timestamp = now - (4 * dayMs)
                ),
                category = null,
                account = null
            ),
            TransactionWithDetails(
                transaction = TransactionEntity(
                    id = 2L,
                    bookId = FinancialBook.PERSONAL.id,
                    accountId = 1L,
                    categoryId = 5L,
                    amount = 500.0,
                    type = TransactionType.INCOME,
                    note = "Freelance payout",
                    timestamp = now - (2 * dayMs)
                ),
                category = null,
                account = null
            )
        )

        val summary = NoSpendCalendarCalculator.build35DaySummary(txs, nowMillis = now)
        assertEquals(35, summary.days.size)
        // Today (index 34) is not completed yet -> not golden
        assertTrue(summary.days.last().isToday)
        assertFalse(summary.days.last().isCompletedNoSpendGolden)
        // Completed days -1, -2 (with Income only), -3 have ₹0 expenses -> Golden Streak = 3!
        assertEquals(3, summary.goldenStreakDays)
        // Out of 34 completed past days, only 1 had an expense -> 33 Golden Days
        assertEquals(33, summary.totalGoldenDays)
        // Day -4 had an expense -> not golden
        assertFalse(summary.days[34 - 4].isCompletedNoSpendGolden)
        // Day -2 had income only -> still Golden No-Spend Day
        assertTrue(summary.days[34 - 2].isCompletedNoSpendGolden)
    }

    @Test
    fun `marking upcoming bill Paid auto-records expense, resolves or creates category, prevents duplicates, and reverses on Unpay`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context).apply { clearAllBillTransactionLinks() }
        val db = Room.inMemoryDatabaseBuilder(context, KoshvantaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repo = KoshvantaRepository(db.dao(), prefs)
            repo.checkAndPreloadInitialData(isDemoWiped = true) // loads default categories, zero accounts/bills

            // Setup user bank account with $1,000.00 balance
            val bankId = repo.insertAccountAndReturnId(
                AccountEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    name = "Main Checking",
                    type = AccountType.BANK,
                    balance = 1000.0,
                    isDefault = true
                )
            )

            // Bill 1: Matches existing "Entertainment" category via title smart resolver
            val netflixId = db.dao().insertBill(
                BillReminderEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    title = "Netflix Ultra HD",
                    amount = 20.0,
                    dueDate = System.currentTimeMillis() + 86_400_000L,
                    categoryName = "Subscriptions",
                    isPaid = false
                )
            )

            // Bill 2: Blank categoryName with unmatched title -> must safely fallback & auto-create "Bills & Other"
            val fallbackBillId = db.dao().insertBill(
                BillReminderEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    title = "Annual Locker Fee",
                    amount = 30.0,
                    dueDate = System.currentTimeMillis() + 172_800_000L,
                    categoryName = "",
                    isPaid = false
                )
            )

            // Mark Bill 1 Paid -> links existing "Entertainment", deducts $20 -> balance $980
            val res1 = repo.toggleBillPaidWithSync(netflixId, isPaid = true)
            assertTrue(res1 is BillToggleResult.Paid)
            val paid1 = res1 as BillToggleResult.Paid
            assertEquals("Entertainment", paid1.category.name)
            assertFalse(paid1.categoryAutoCreated)
            assertEquals(TransactionType.EXPENSE, paid1.transaction.type)
            assertEquals(20.0, paid1.transaction.amount, 0.01)
            assertEquals("Bill Paid: Netflix Ultra HD", paid1.transaction.note)

            val balAfterBill1 = db.dao().getAccountById(bankId)!!.balance
            assertEquals(980.0, balAfterBill1, 0.01)

            // Duplicate tap guard: calling mark Paid again must be a strict NoOp without double deduction
            val dupRes = repo.toggleBillPaidWithSync(netflixId, isPaid = true)
            assertTrue(dupRes is BillToggleResult.NoOpAlreadySynced)
            assertEquals(980.0, db.dao().getAccountById(bankId)!!.balance, 0.01)
            assertEquals(1, repo.getTransactions(FinancialBook.PERSONAL.id).first().size)

            // Mark Bill 2 Paid -> blank category safely falls back to auto-created "Bills & Other" -> balance $950
            val fallbackRes = repo.toggleBillPaidWithSync(fallbackBillId, isPaid = true) as BillToggleResult.Paid
            assertEquals("Bills & Other", fallbackRes.category.name)
            assertTrue(fallbackRes.categoryAutoCreated)
            assertEquals(950.0, db.dao().getAccountById(bankId)!!.balance, 0.01)

            // Bill 3: Custom unmatched category -> auto-creates "Fitness & Wellness" -> balance $870
            val gymBillId = db.dao().insertBill(
                BillReminderEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    title = "Equinox Gym Membership",
                    amount = 80.0,
                    dueDate = System.currentTimeMillis() + 259_200_000L,
                    categoryName = "Fitness & Wellness",
                    isPaid = false
                )
            )
            val gymRes = repo.toggleBillPaidWithSync(gymBillId, isPaid = true) as BillToggleResult.Paid
            assertEquals("Fitness & Wellness", gymRes.category.name)
            assertTrue(gymRes.categoryAutoCreated)
            assertEquals(870.0, db.dao().getAccountById(bankId)!!.balance, 0.01)

            // Verify live reactive flows & Net Worth curve reflect all 3 expenses (-$20, -$30, -$80)
            val details = repo.getTransactionsWithDetails(FinancialBook.PERSONAL.id).first()
            assertEquals(3, details.size)
            val curve = NetWorthCurveCalculator.buildCurve(
                currentNetWorthUsd = db.dao().getAccountById(bankId)!!.balance,
                transactions = details
            )
            assertEquals(4, curve.size)
            assertEquals(1000.0, curve.first().netWorthUsd, 0.01)
            assertEquals(870.0, curve.last().netWorthUsd, 0.01)

            // Undo / Unmark Bill 1 Paid across simulated new repository instance (prefs persistence check)
            val repoAfterRestart = KoshvantaRepository(db.dao(), AppPreferences(context))
            val unpayRes = repoAfterRestart.toggleBillPaidWithSync(netflixId, isPaid = false)
            assertTrue(unpayRes is BillToggleResult.Unpaid)
            assertFalse(db.dao().getBillById(netflixId)!!.isPaid)
            assertEquals(890.0, db.dao().getAccountById(bankId)!!.balance, 0.01)
            assertNull(db.dao().getTransactionById(paid1.transaction.id))
            assertEquals(2, repoAfterRestart.getTransactions(FinancialBook.PERSONAL.id).first().size)
        } finally {
            db.close()
        }
    }

    @Test
    fun `metal card vault filters transactions by account, computes telemetry, tilt bounds, and persists finish`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val hdfc = AccountEntity(
            id = 10L,
            name = "HDFC Bank",
            type = AccountType.BANK,
            balance = 2000.0,
            isDefault = true
        )
        val credit = AccountEntity(
            id = 20L,
            name = "World Elite",
            type = AccountType.CREDIT_CARD,
            balance = 500.0,
            creditLimit = 1000.0,
            isDefault = false
        )

        val txs = listOf(
            TransactionWithDetails(
                transaction = TransactionEntity(
                    id = 1L,
                    accountId = 10L,
                    categoryId = 1L,
                    amount = 300.0,
                    type = TransactionType.INCOME,
                    note = "Freelance"
                ),
                category = null,
                account = hdfc
            ),
            TransactionWithDetails(
                transaction = TransactionEntity(
                    id = 2L,
                    accountId = 20L,
                    categoryId = 2L,
                    amount = 250.0,
                    type = TransactionType.EXPENSE,
                    note = "Hotel"
                ),
                category = null,
                account = credit
            ),
            TransactionWithDetails(
                transaction = TransactionEntity(
                    id = 3L,
                    accountId = 10L,
                    toAccountId = 20L,
                    categoryId = 3L,
                    amount = 150.0,
                    type = TransactionType.TRANSFER,
                    note = "Card Payoff"
                ),
                category = null,
                account = hdfc,
                toAccount = credit
            )
        )

        // Filtering isolates each account cleanly (including transfers to/from)
        val hdfcFiltered = MetalCardVaultCalculator.filterTransactionsForAccount(10L, txs)
        assertEquals(2, hdfcFiltered.size)
        val creditFiltered = MetalCardVaultCalculator.filterTransactionsForAccount(20L, txs)
        assertEquals(2, creditFiltered.size)

        // Telemetry calculates inflow, outflow, and credit utilization accurately
        val creditTelemetry = MetalCardVaultCalculator.computeAccountTelemetry(credit, creditFiltered)
        assertEquals(150.0, creditTelemetry.inflowUsd, 0.01)
        assertEquals(250.0, creditTelemetry.outflowUsd, 0.01)
        assertEquals(0.25f, creditTelemetry.utilizationRatio, 0.01f)
        assertEquals("Credit Limit Pulse", creditTelemetry.limitLabel)

        // 3D tilt physics stays bounded for corner drag inputs
        val (topRightRotX, topRightRotY) = MetalCardVaultCalculator.computeTiltAngles(
            touchX = 400f,
            touchY = 0f,
            width = 400f,
            height = 200f
        )
        assertEquals(10f, topRightRotX, 0.01f)
        assertEquals(14f, topRightRotY, 0.01f)

        // Luxury finish persistence per account including Platinum Silver & Emerald
        MetalCardVaultStore.saveFinish(context, credit.id, MetalCardFinish.ELECTRIC_CYAN)
        assertEquals(MetalCardFinish.ELECTRIC_CYAN, MetalCardVaultStore.getFinish(context, credit, 1))
        MetalCardVaultStore.saveFinish(context, hdfc.id, MetalCardFinish.PLATINUM_SILVER)
        assertEquals(MetalCardFinish.PLATINUM_SILVER, MetalCardVaultStore.getFinish(context, hdfc, 0))
        MetalCardVaultStore.saveFinish(context, hdfc.id, MetalCardFinish.EMERALD)
        assertEquals(MetalCardFinish.EMERALD, MetalCardVaultStore.getFinish(context, hdfc, 0))
        MetalCardVaultStore.saveCardCurrencyOverride(context, credit.id, CurrencyCode.EUR)
        assertEquals(CurrencyCode.EUR, MetalCardVaultStore.getCardCurrencyOverride(context, credit.id))
    }

    @Test
    fun `country card engine preloads 3 regional cards on currency switch until custom cards lock`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context).apply {
            setDemoWiped(false)
            setCustomCardsLocked(false)
            setStarterCardIds(emptyList())
        }
        val db = Room.inMemoryDatabaseBuilder(context, KoshvantaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repo = KoshvantaRepository(db.dao(), prefs)

            // 1. Preload India (INR) starter cards when unlocked
            repo.syncCountryStarterCards(FinancialBook.PERSONAL.id, CurrencyCode.INR, context, force = false)
            val indiaCards = repo.getAccounts(FinancialBook.PERSONAL.id).first()
            assertEquals(3, indiaCards.size)
            assertEquals(
                listOf("Primary Bank Debit", "UPI / Local Digital", "Platinum Credit Card"),
                indiaCards.map { it.name }
            )

            // Quick-add pills for India include regional UPI / Local Digital + global trademark-safe set
            val inrPills = CountryCardEngine.getQuickAddPills(CurrencyCode.INR).map { it.pillLabel }
            assertTrue(inrPills.contains("UPI / Local Digital"))
            assertTrue(inrPills.contains("Black Card"))
            assertTrue(inrPills.contains("Forex Travel Card"))
            assertTrue(inrPills.contains("Prepaid Virtual"))
            assertTrue(inrPills.contains("Business Corporate"))

            // 2. Switch currency to USA while unlocked -> updates cleanly to US 3 starter cards
            repo.syncCountryStarterCards(FinancialBook.PERSONAL.id, CurrencyCode.USD, context, force = false)
            val usdCards = repo.getAccounts(FinancialBook.PERSONAL.id).first()
            assertEquals(3, usdCards.size)
            assertEquals(
                listOf("Checking Debit Card", "Rewards Credit Card", "Black Metal Card"),
                usdCards.map { it.name }
            )

            // 3. Lock custom cards after user edits/adds a card -> subsequent currency switches MUST preserve user cards
            prefs.setCustomCardsLocked(true)
            repo.insertAccountAndReturnId(
                AccountEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    name = "My Custom Forex Card",
                    type = AccountType.BANK,
                    balance = 500.0
                )
            )
            repo.syncCountryStarterCards(FinancialBook.PERSONAL.id, CurrencyCode.GBP, context, force = false)
            val preservedCards = repo.getAccounts(FinancialBook.PERSONAL.id).first()
            assertEquals(4, preservedCards.size)
            assertTrue(preservedCards.any { it.name == "My Custom Forex Card" })
        } finally {
            db.close()
        }
    }

    @Test
    fun `work hour cost calculator hides small expenses, flags half-day plus purchases, and persists settings`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)
        assertEquals(8.0, prefs.dailyWorkHours.value, 0.01)

        // Monthly income 44,000 across 22 days x 8 hrs/day => 2,000/day, 250/hr, half-day threshold = 1,000
        val monthlyIncome = 44_000.0
        val rates = WorkHourCostCalculator.computeRates(monthlyIncome)
        assertEquals(2000.0, rates.dailyRate, 0.01)
        assertEquals(250.0, rates.hourlyRate, 0.01)
        assertEquals(1000.0, rates.halfDayThresholdAmount, 0.01)

        // Small daily expenses (< 50% of 1-day earning) MUST remain 100% hidden (null)
        assertNull(WorkHourCostCalculator.evaluatePurchase(20.0, monthlyIncome))
        assertNull(WorkHourCostCalculator.evaluatePurchase(500.0, monthlyIncome))
        assertNull(WorkHourCostCalculator.evaluatePurchase(999.0, monthlyIncome))
        assertNull(WorkHourCostCalculator.evaluatePurchase(5000.0, 0.0))

        // At exact half-day threshold (4 hours = 1,000) -> visible
        val halfDay = WorkHourCostCalculator.evaluatePurchase(1000.0, monthlyIncome)
        assertNotNull(halfDay)
        assertEquals("4 Hours", halfDay!!.durationLabel)
        assertEquals("⏳ Life-Energy Cost: 4 Hours of your hard work", halfDay.message)

        // Exact 1 full day (8 hours = 2,000) rolls cleanly to "1 Day"
        val fullDay = WorkHourCostCalculator.evaluatePurchase(2000.0, monthlyIncome)
        assertEquals("1 Day", fullDay!!.durationLabel)

        // 2 Days, 4 Hours (20 hours = 5,000)
        val bigBuy = WorkHourCostCalculator.evaluatePurchase(5000.0, monthlyIncome)
        assertNotNull(bigBuy)
        assertTrue(bigBuy!!.isHeavyImpact)
        assertEquals(2, bigBuy.days)
        assertEquals(4, bigBuy.hours)
        assertEquals("⏳ Life-Energy Cost: 2 Days, 4 Hours of your hard work", bigBuy.message)

        // Live expression evaluation supports keypad expressions cleanly
        assertEquals(5000.0, WorkHourCostCalculator.evaluateQuickExpression("1500+3500"), 0.01)
        assertEquals(5000.0, WorkHourCostCalculator.evaluateQuickExpression("5000+"), 0.01)

        // Settings persistence across new AppPreferences instance
        prefs.setWorkMonthlyIncomeUsd(1200.0)
        prefs.setDailyWorkHours(10.0)
        val reloadedPrefs = AppPreferences(context)
        assertEquals(1200.0, reloadedPrefs.workMonthlyIncomeUsd.value, 0.01)
        assertEquals(10.0, reloadedPrefs.dailyWorkHours.value, 0.01)
    }

    @Test
    fun `net worth and connected accounts reflect exact remaining balance and curve end dot tracks latest expense`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)
        val db = Room.inMemoryDatabaseBuilder(context, KoshvantaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repo = KoshvantaRepository(db.dao(), prefs)
            val catId = db.dao().insertCategory(CategoryEntity(name = "Shopping", iconName = "bag", colorHex = "#00FFCC", isExpense = true))
            val salaryCatId = db.dao().insertCategory(CategoryEntity(name = "Salary", iconName = "work", colorHex = "#10B981", isExpense = false))

            val upiId = repo.insertAccountAndReturnId(
                AccountEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    name = "UPI / Digital Wallet",
                    type = AccountType.BANK,
                    balance = 1000.0
                )
            )
            val cashId = repo.insertAccountAndReturnId(
                AccountEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    name = "Cash Vault",
                    type = AccountType.CASH,
                    balance = 500.0
                )
            )

            val baseTs = 1_700_000_000_000L
            // Add Income +800 to UPI -> UPI remaining becomes 1800, Total Net Worth becomes 2300
            repo.addTransaction(
                TransactionEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    accountId = upiId,
                    categoryId = salaryCatId,
                    amount = 800.0,
                    type = TransactionType.INCOME,
                    note = "Freelance Payout",
                    timestamp = baseTs + 1000L
                )
            )
            // Log Expense -250 from UPI -> UPI remaining becomes 1550, Total Net Worth becomes 2050
            repo.addTransaction(
                TransactionEntity(
                    bookId = FinancialBook.PERSONAL.id,
                    accountId = upiId,
                    categoryId = catId,
                    amount = 250.0,
                    type = TransactionType.EXPENSE,
                    note = "Electronics",
                    timestamp = baseTs + 2000L
                )
            )

            val accountsAfter = repo.getAccounts(FinancialBook.PERSONAL.id).first()
            val upiAccount = accountsAfter.first { it.id == upiId }
            val cashAccount = accountsAfter.first { it.id == cashId }
            val totalRemainingNetWorth = accountsAfter.sumOf { it.balance }

            // Connected Account shows remaining balance (1000 + 800 - 250 = 1550), never -250
            assertEquals(1550.0, upiAccount.balance, 0.01)
            assertEquals(500.0, cashAccount.balance, 0.01)
            // Total Net Worth = (1000 + 500 + 800 - 250) = 2050
            assertEquals(2050.0, totalRemainingNetWorth, 0.01)

            val txDetails = repo.getTransactionsWithDetails(FinancialBook.PERSONAL.id).first()
            val curve = NetWorthCurveCalculator.buildCurve(totalRemainingNetWorth, txDetails)
            assertEquals(3, curve.size)
            assertEquals(1500.0, curve[0].netWorthUsd, 0.01) // Opening initial sum
            assertEquals(2300.0, curve[1].netWorthUsd, 0.01) // Rises sharply after +800 Income
            assertEquals(2050.0, curve[2].netWorthUsd, 0.01) // Steps down to 2050 after -250 Expense
            assertEquals(-250.0, curve.last().deltaUsd, 0.01) // Final dot tracks latest deducted expense
        } finally {
            db.close()
        }
    }

    @Test
    fun `kushwanta mind arena tracks level 1 rookie to level 10 diamond tycoon, streak, and cyber wealth 2048 merges`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Level Ladder & Catalog Readiness (1..10)
        assertEquals("Rookie", ArenaLevelCalculator.evaluate(0L).title)
        assertEquals(1, ArenaLevelCalculator.evaluate(0L).level)
        val maxLvl = ArenaLevelCalculator.evaluate(10_000L)
        assertEquals(10, maxLvl.level)
        assertEquals("Diamond Tycoon", maxLvl.title)
        assertTrue(maxLvl.isMaxLevel)

        assertEquals(10, ArenaGameCatalog.allGames.size)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.CYBER_WEALTH_2048 }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.TITANIUM_TOWER_STACKER }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.GLOBAL_CURRENCY_RUNNER }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.CYBER_VAULT_HEXA }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.QUANTUM_LASER_VAULT }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.RAPID_WEALTH_MATH_DUEL }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.NEON_BRICK_BREAKER }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.CHRONO_VAULT_MATRIX }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.GRAVITY_ORBIT_PROTECTOR }.isPlayableNow)
        assertTrue(ArenaGameCatalog.allGames.first { it.id == ArenaGameId.TITANIUM_CITADEL }.isPlayableNow)
        assertEquals(0, ArenaGameCatalog.allGames.count { !it.isPlayableNow })

        // 2. Luxury Tile Progression Ladder (10 -> GOLD VAULT)
        val labels = (1..9).map { CyberWealth2048Engine.labelForTier(it) }
        assertEquals(listOf("10", "20", "50", "100", "500", "1K", "5K", "10K", "GOLD VAULT"), labels)

        // 3. Deterministic Swipe Merge + Gold Fever Mode (3+ rapid merges => 2x XP) + Undo Power-Up
        val initialTiles = listOf(
            WealthTile(id = 1L, tier = 1, row = 0, col = 0),
            WealthTile(id = 2L, tier = 1, row = 0, col = 1),
            WealthTile(id = 3L, tier = 2, row = 1, col = 0),
            WealthTile(id = 4L, tier = 2, row = 1, col = 1),
            WealthTile(id = 5L, tier = 8, row = 2, col = 0),
            WealthTile(id = 6L, tier = 8, row = 2, col = 1)
        )
        val startState = CyberWealthBoardState(tiles = initialTiles, nextTileId = 10L)
        val t0 = 1_700_000_000_000L

        val (afterLeft, moved) = CyberWealth2048Engine.swipe(startState, SwipeDirection.LEFT, nowMs = t0)
        assertTrue(moved)
        assertEquals(3, afterLeft.lastMerges.size)
        assertTrue(afterLeft.isFeverActive(t0 + 100L))
        assertTrue(afterLeft.hasReachedGoldVault) // two 10K (tier 8) merged into GOLD VAULT (tier 9)
        assertTrue(afterLeft.score > 25_000L)
        assertTrue(afterLeft.lastXpAwarded > 0L)

        // Undo Power-Up: 1 free use restores pre-move score & board cleanly and locks ad-slot hook
        assertEquals(UndoPowerUpState.FREE_AVAILABLE, afterLeft.undoPowerUpState())
        val undone = CyberWealth2048Engine.applyUndo(afterLeft, viaRewardedAd = false)
        assertEquals(0L, undone.score)
        assertEquals(6, undone.tiles.size)
        assertEquals(UndoPowerUpState.REWARDED_AD_LOCKED, undone.undoPowerUpState())

        // 4. SharedPreferences persistence for XP, Streak across consecutive days & Best Score
        val day1 = ArenaProgressStore.awardXpAndTouchStreak(context, 160L, nowMillis = t0)
        assertEquals(2, day1.levelInfo.level)
        assertEquals("Apprentice", day1.levelInfo.title)
        assertEquals(1, day1.streakDays)

        val day2 = ArenaProgressStore.awardXpAndTouchStreak(context, 50L, nowMillis = t0 + 86_400_000L)
        assertEquals(2, day2.streakDays)
        assertEquals(210L, day2.totalXp)

        val bestUpdated = ArenaProgressStore.updateBestScore2048IfHigher(context, 25_170L)
        assertEquals(25_170L, bestUpdated.bestScore2048)
    }

    @Test
    fun `kushwanta mind arena game 2 titanium tower handles slicing, fever regrowth, 24k milestones, biomes, power-ups, and best floors`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val t0 = 1_700_100_000_000L

        // 1. Dynamic Altitude Biomes progression (1-15, 16-35, 36-60, 61+)
        assertEquals(TowerAltitudeBiome.CYBER_CITY_GRID, TitaniumTowerEngine.biomeForFloor(1))
        assertEquals(TowerAltitudeBiome.GOLD_STRATOSPHERE, TitaniumTowerEngine.biomeForFloor(20))
        assertEquals(TowerAltitudeBiome.QUANTUM_NEBULA, TitaniumTowerEngine.biomeForFloor(45))
        assertEquals(TowerAltitudeBiome.DIAMOND_COSMOS, TitaniumTowerEngine.biomeForFloor(70))

        // 2. Foundation & Laser Aim Sight (1 free, 3s alignment window)
        var state = TitaniumTowerEngine.newGame()
        assertEquals(0, state.currentFloor)
        assertEquals(1, state.laserSightRemaining)
        state = TitaniumTowerEngine.activateLaserSight(state, nowMs = t0)
        assertEquals(0, state.laserSightRemaining)
        assertTrue(state.hasActiveLaserSight(t0 + 1500L))
        assertFalse(state.hasActiveLaserSight(t0 + 3500L))

        // 3. Offset drop trims overhang cleanly
        val shiftedState = state.copy(activeLeft = TitaniumTowerEngine.BASE_SLAB_LEFT + 0.14f)
        val slicedRes = TitaniumTowerEngine.dropActiveSlab(shiftedState, nowMs = t0 + 100L)
        assertEquals(TowerDropOutcome.SLICED, slicedRes.outcome)
        assertEquals(1, slicedRes.nextState.currentFloor)
        assertEquals(0.40f, slicedRes.nextState.activeWidth, 0.002f)
        assertEquals(0.14f, slicedRes.cutWidth ?: 0f, 0.002f)

        // 4. 3+ Consecutive Perfect Drops activate Gold Fever Mode (2x XP + Block Width Regrowth)
        var comboState = slicedRes.nextState
        for (i in 1..3) {
            val topLeft = comboState.placedSlabs.last().left
            val perfectDrop = TitaniumTowerEngine.dropActiveSlab(comboState.copy(activeLeft = topLeft), nowMs = t0 + 500L * i)
            assertEquals(TowerDropOutcome.PERFECT, perfectDrop.outcome)
            comboState = perfectDrop.nextState
        }
        assertEquals(3, comboState.perfectCombo)
        assertTrue(comboState.isGoldFeverActive)
        // Narrowed 0.40f width expanded cleanly on Gold Fever 3rd perfect drop
        assertTrue(comboState.activeWidth > 0.40f)

        // 5. 10th floor awards 24K Gold Milestone Slab
        var preTen = comboState.copy(currentFloor = 9)
        val topAt9 = preTen.placedSlabs.last()
        val floor10Res = TitaniumTowerEngine.dropActiveSlab(preTen.copy(activeLeft = topAt9.left), nowMs = t0 + 3000L)
        assertTrue(floor10Res.isGoldMilestone)
        assertTrue(floor10Res.nextState.placedSlabs.last().isGoldMilestone)
        assertEquals(10, floor10Res.nextState.currentFloor)

        // 6. Complete miss triggers 1 Free Second Chance Revive, then Game Over if missed again
        val missedOnce = TitaniumTowerEngine.dropActiveSlab(
            floor10Res.nextState.copy(activeLeft = 0.90f, activeWidth = 0.08f),
            nowMs = t0 + 4000L
        )
        assertEquals(TowerDropOutcome.MISSED, missedOnce.outcome)
        assertTrue(missedOnce.nextState.isPendingRevive)
        assertFalse(missedOnce.nextState.isGameOver)

        val revived = TitaniumTowerEngine.useSecondChanceRevive(missedOnce.nextState, nowMs = t0 + 5000L)
        assertFalse(revived.isPendingRevive)
        assertEquals(0, revived.revivesRemaining)
        assertEquals(10, revived.currentFloor)

        val missedTwice = TitaniumTowerEngine.dropActiveSlab(
            revived.copy(activeLeft = 0.90f, activeWidth = 0.08f),
            nowMs = t0 + 6000L
        )
        assertEquals(TowerDropOutcome.MISSED, missedTwice.outcome)
        assertTrue(missedTwice.nextState.isGameOver)

        // 7. SharedPreferences persistence keeps highest Tower floors strictly
        val saved18 = ArenaProgressStore.updateBestTowerFloorsIfHigher(context, 18)
        assertEquals(18, saved18.bestTowerFloors)
        val ignoredLower = ArenaProgressStore.updateBestTowerFloorsIfHigher(context, 9)
        assertEquals(18, ignoredLower.bestTowerFloors)
    }

    @Test
    fun `kushwanta mind arena game 3 global currency runner handles 3-lane dodging, wealth multiplier, shield, magnet, revive, and best score`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val t0 = 1_700_200_000_000L

        // 1. Initial lane & bounded 3-lane switching (0, 1, 2)
        var state = CurrencyRunnerEngine.newGame()
        assertEquals(1, state.playerLane)
        assertEquals(RunnerCurrencyZone.USD, state.currentZone())
        val (leftState, movedLeft) = CurrencyRunnerEngine.shiftLane(state, -1)
        assertTrue(movedLeft)
        assertEquals(0, leftState.playerLane)
        val (clampedLeft, movedAgain) = CurrencyRunnerEngine.shiftLane(leftState, -1)
        assertFalse(movedAgain)
        assertEquals(0, clampedLeft.playerLane)

        // 2. Emerald-Cyan Wealth Gates boost score, multiplier & XP
        val gatesBeforeBoost = listOf(
            RunnerGate(id = 1L, lane = 1, depthProgress = 0.89f, kind = RunnerGateKind.WEALTH_GATE, label = "+25% SIP"),
            RunnerGate(id = 2L, lane = 1, depthProgress = 0.89f, kind = RunnerGateKind.WEALTH_GATE, label = "2x Compound")
        )
        val boostStep = CurrencyRunnerEngine.stepSimulation(
            state.copy(gates = gatesBeforeBoost, spawnCooldownSec = 10f),
            dtSec = 0.04f,
            nowMs = t0
        )
        assertEquals(2, boostStep.nextState.wealthCombo)
        assertEquals(2, boostStep.nextState.multiplier)
        assertTrue(boostStep.nextState.score >= 450L)
        assertTrue(boostStep.xpEarned > 0L)

        // 3. Golden Inflation Shield (5s invincibility) blocks Red Trap with 0 damage & bonus points
        val shielded = CurrencyRunnerEngine.activateGoldenShield(boostStep.nextState, nowMs = t0)
        assertTrue(shielded.isGoldenShieldActive(t0 + 2500L))
        assertFalse(shielded.isGoldenShieldActive(t0 + 5500L))
        val shieldTrapStep = CurrencyRunnerEngine.stepSimulation(
            shielded.copy(
                gates = listOf(RunnerGate(id = 3L, lane = 1, depthProgress = 0.90f, kind = RunnerGateKind.INFLATION_TRAP, label = "Debt Spike")),
                spawnCooldownSec = 10f
            ),
            dtSec = 0.04f,
            nowMs = t0 + 100L
        )
        assertEquals(100, shieldTrapStep.nextState.shieldIntegrity)
        assertEquals(RunnerCollisionType.TRAP_BLOCKED_BY_SHIELD, shieldTrapStep.events.first().type)

        // 4. Wealth Magnet (6s) automatically collects off-lane Currency Crystals
        val magnetized = CurrencyRunnerEngine.activateWealthMagnet(shieldTrapStep.nextState, nowMs = t0 + 6000L)
        val magnetStep = CurrencyRunnerEngine.stepSimulation(
            magnetized.copy(
                gates = listOf(RunnerGate(id = 4L, lane = 2, depthProgress = 0.90f, kind = RunnerGateKind.CURRENCY_CRYSTAL, label = "$ +90")),
                spawnCooldownSec = 10f
            ),
            dtSec = 0.04f,
            nowMs = t0 + 6100L
        )
        assertEquals(RunnerCollisionType.CRYSTAL_COLLECTED, magnetStep.events.first().type)

        // 5. Unshielded Red Trap damage depletes shield -> triggers 1 Free Second Chance Revive -> second depletion Game Over
        val fatalState = magnetStep.nextState.copy(
            shieldIntegrity = 30,
            goldenShieldExpiresAtMs = 0L,
            gates = listOf(RunnerGate(id = 5L, lane = 1, depthProgress = 0.90f, kind = RunnerGateKind.INFLATION_TRAP, label = "-30% Inflation")),
            spawnCooldownSec = 10f
        )
        val firstFatal = CurrencyRunnerEngine.stepSimulation(fatalState, dtSec = 0.04f, nowMs = t0 + 7000L)
        assertTrue(firstFatal.nextState.isPendingRevive)
        assertFalse(firstFatal.nextState.isGameOver)

        val revived = CurrencyRunnerEngine.useSecondChanceRevive(firstFatal.nextState, nowMs = t0 + 8000L)
        assertFalse(revived.isPendingRevive)
        assertEquals(75, revived.shieldIntegrity)
        assertEquals(0, revived.revivesRemaining)

        val secondFatal = CurrencyRunnerEngine.stepSimulation(
            revived.copy(
                shieldIntegrity = 30,
                goldenShieldExpiresAtMs = 0L,
                gates = listOf(RunnerGate(id = 6L, lane = 1, depthProgress = 0.90f, kind = RunnerGateKind.INFLATION_TRAP, label = "Impulse Trap")),
                spawnCooldownSec = 10f
            ),
            dtSec = 0.04f,
            nowMs = t0 + 10_000L
        )
        assertTrue(secondFatal.nextState.isGameOver)

        // 6. Currency zones cycle across all 5 global regions & Best Runner Score persists strictly
        assertEquals(RunnerCurrencyZone.INR, secondFatal.nextState.copy(currencyZoneIndex = 4).currentZone())
        val savedBest = ArenaProgressStore.updateBestRunnerScoreIfHigher(context, 4_850L)
        assertEquals(4_850L, savedBest.bestRunnerScore)
        val ignoredLowerBest = ArenaProgressStore.updateBestRunnerScoreIfHigher(context, 1_200L)
        assertEquals(4_850L, ignoredLowerBest.bestRunnerScore)
    }

    @Test
    fun `kushwanta mind arena game 4 hexa block fit handles 37 cells, 3-axis clears, laser zap, tray refresh, game over, and 3-day streak permanent unlock`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. 37-Cell Radius-3 Honeycomb Geometry & 21 3-Axis Clear Lines (Q, R, S)
        assertEquals(37, HexaVaultEngine.allBoardCoords.size)
        assertEquals(7, HexaVaultEngine.qAxisLines.size)
        assertEquals(7, HexaVaultEngine.rAxisLines.size)
        assertEquals(7, HexaVaultEngine.sAxisLines.size)
        assertEquals(21, HexaVaultEngine.allAxisLines.size)

        // 2. Smart 3-Day Streak Lock & Permanent Unlock System
        val arenaPrefs = context.getSharedPreferences("kushwanta_mind_arena_prefs", Context.MODE_PRIVATE)
        val appPrefs = context.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)
        arenaPrefs.edit().clear().commit()
        appPrefs.edit().putInt("streak_count", 2).putInt("best_streak", 2).commit()

        val lockedSnap = ArenaProgressStore.loadSnapshot(context)
        assertFalse(lockedSnap.isGame4Unlocked)
        assertEquals(2, lockedSnap.effectiveStreakDays)

        // Reaching 3-day streak permanently writes game4_unlocked = true & triggers unlock flash once
        appPrefs.edit().putInt("streak_count", 3).putInt("best_streak", 3).commit()
        val unlockedSnap = ArenaProgressStore.loadSnapshot(context)
        assertTrue(unlockedSnap.isGame4Unlocked)
        assertTrue(unlockedSnap.shouldShowGame4UnlockFlash)
        assertTrue(arenaPrefs.getBoolean("game4_unlocked", false))

        // Consuming unlock flash hides banner while preserving permanent unlock
        val afterFlashSnap = ArenaProgressStore.consumeGame4UnlockFlash(context)
        assertFalse(afterFlashSnap.shouldShowGame4UnlockFlash)
        assertTrue(afterFlashSnap.isGame4Unlocked)

        // Even if streak resets back to 1 later, Game 4 stays unlocked forever!
        appPrefs.edit().putInt("streak_count", 1).putInt("best_streak", 1).commit()
        arenaPrefs.edit().putInt("arena_streak_days", 1).commit()
        val postResetSnap = ArenaProgressStore.loadSnapshot(context)
        assertTrue(postResetSnap.isGame4Unlocked)

        // 3. Multi-Axis Laser Line Clears (Q-axis + R-axis simultaneous 2x Combo) & Ghost Preview
        val q0LineExceptOrigin = HexaVaultEngine.allBoardCoords
            .filter { it.q == 0 && it != HexCoord(0, 0) }
            .associateWith { HexaCrystalColor.ELECTRIC_CYAN }
        val r0LineExceptOrigin = HexaVaultEngine.allBoardCoords
            .filter { it.r == 0 && it != HexCoord(0, 0) }
            .associateWith { HexaCrystalColor.EMERALD_MINT }
        val combinedOccupied = q0LineExceptOrigin + r0LineExceptOrigin
        val singleCorePiece = HexaPiece(
            id = 900L,
            name = "Core Shard",
            offsets = listOf(HexCoord(0, 0)),
            color = HexaCrystalColor.ROYAL_GOLD
        )

        val preview = HexaVaultEngine.previewPlacement(combinedOccupied, singleCorePiece, HexCoord(0, 0))
        assertNotNull(preview)
        assertEquals(2, preview!!.wouldClearLines.size)
        assertTrue(HexCoord(0, 0) in preview.wouldClearCoords)

        val twoAxisState = HexaVaultState(
            occupied = combinedOccupied,
            trayPieces = listOf(singleCorePiece, singleCorePiece.copy(id = 901L), singleCorePiece.copy(id = 902L)),
            nextPieceId = 903L
        )
        val doubleClearRes = HexaVaultEngine.placePiece(twoAxisState, slotIndex = 0, anchor = HexCoord(0, 0))
        assertTrue(doubleClearRes.applied)
        assertEquals(2, doubleClearRes.clearedLines.size)
        assertEquals(2, doubleClearRes.comboMultiplier)
        assertTrue(doubleClearRes.nextState.occupied.isEmpty())
        assertTrue(doubleClearRes.pointsAwarded > 700L)

        // Also test 5x MEGA CLEAR threshold (4+ simultaneous lines)
        assertEquals(5, HexaVaultEngine.comboMultiplierForLines(4))
        assertEquals(3, HexaVaultEngine.comboMultiplierForLines(3))

        // 4. Refresh Tray Power-Up + Laser Zap + Game Over Detection
        val almostFullBoard = HexaVaultEngine.allBoardCoords
            .filter { it != HexCoord(-1, 0) && it != HexCoord(0, 0) }
            .associateWith { HexaCrystalColor.NEON_VIOLET }
        val wideBeam = HexaPiece(
            id = 950L,
            name = "Beam R-3",
            offsets = listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(1, 0)),
            color = HexaCrystalColor.ELECTRIC_CYAN
        )
        val stuckState = HexaVaultState(
            occupied = almostFullBoard,
            trayPieces = listOf(wideBeam, wideBeam.copy(id = 951L), wideBeam.copy(id = 952L)),
            nextPieceId = 960L,
            isGameOver = false
        )
        assertFalse(HexaVaultEngine.hasAnyValidMove(stuckState.occupied, stuckState.trayPieces))

        // Zap blocking cell (1,0) -> opens (-1,0),(0,0),(1,0), rescues board, drops laserZapRemaining to 0, awards +35
        val zapRes = HexaVaultEngine.applyLaserZap(stuckState, HexCoord(1, 0))
        assertTrue(zapRes.applied)
        assertFalse(zapRes.nextState.isGameOver)
        assertEquals(0, zapRes.nextState.laserZapRemaining)
        assertEquals(35L, zapRes.nextState.score)

        // Refresh Tray swaps pieces and decrements refreshTrayRemaining to 0
        val refreshed = HexaVaultEngine.applyRefreshTray(zapRes.nextState, random = kotlin.random.Random(42))
        assertEquals(0, refreshed.refreshTrayRemaining)
        assertEquals(3, refreshed.trayPieces.filterNotNull().size)

        // Placing last available piece on full board triggers Game Over cleanly
        val fullExceptOne = HexaVaultEngine.allBoardCoords
            .filter { it != HexCoord(0, 0) }
            .associateWith { HexaCrystalColor.ROYAL_GOLD }
        // Make sure tray has only wideBeam so after placing a piece on (0,0) or checking full board it ends game
        val noFitState = HexaVaultState(
            occupied = fullExceptOne,
            trayPieces = listOf(wideBeam, null, null),
            nextPieceId = 980L
        )
        assertFalse(HexaVaultEngine.hasAnyValidMove(noFitState.occupied, noFitState.trayPieces))

        // 5. SharedPreferences Best Hexa Score keeps highest strictly
        val best1 = ArenaProgressStore.updateBestHexaScoreIfHigher(context, 3_420L)
        assertEquals(3_420L, best1.bestHexaScore)
        val best2 = ArenaProgressStore.updateBestHexaScoreIfHigher(context, 900L)
        assertEquals(3_420L, best2.bestHexaScore)
    }

    @Test
    fun `kushwanta mind arena game 5 quantum laser vault handles raytracing, mirror reflections, beam splitter, firewall bypass, procedural levels, hints, and 7-day permanent unlock`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Smart 7-Day Streak Lock & Permanent Unlock System
        val arenaPrefs = context.getSharedPreferences("kushwanta_mind_arena_prefs", Context.MODE_PRIVATE)
        val appPrefs = context.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)
        arenaPrefs.edit().clear().commit()
        appPrefs.edit().putInt("streak_count", 6).putInt("best_streak", 6).commit()

        val lockedSnap = ArenaProgressStore.loadSnapshot(context)
        assertTrue(lockedSnap.isGame4Unlocked)
        assertFalse(lockedSnap.isGame5Unlocked)
        assertEquals(6, lockedSnap.effectiveStreakDays)

        // Reaching 7-day streak permanently writes game5_unlocked = true & triggers unlock flash once
        appPrefs.edit().putInt("streak_count", 7).putInt("best_streak", 7).commit()
        val unlockedSnap = ArenaProgressStore.loadSnapshot(context)
        assertTrue(unlockedSnap.isGame5Unlocked)
        assertTrue(unlockedSnap.shouldShowGame5UnlockFlash)
        assertTrue(arenaPrefs.getBoolean("game5_unlocked", false))

        val afterFlashSnap = ArenaProgressStore.consumeGame5UnlockFlash(context)
        assertFalse(afterFlashSnap.shouldShowGame5UnlockFlash)
        assertTrue(afterFlashSnap.isGame5Unlocked)

        // Even if streak later resets to 1, Game 5 remains unlocked forever
        appPrefs.edit().putInt("streak_count", 1).putInt("best_streak", 1).commit()
        arenaPrefs.edit().putInt("arena_streak_days", 1).commit()
        assertTrue(ArenaProgressStore.loadSnapshot(context).isGame5Unlocked)

        // 2. Dynamic Grid Scaling & Endless Solvable Procedural Generator (Levels 1..12)
        assertEquals(6, QuantumLaserEngine.gridSizeForLevel(1))
        assertEquals(7, QuantumLaserEngine.gridSizeForLevel(4))
        assertEquals(8, QuantumLaserEngine.gridSizeForLevel(8))

        for (lvl in 1..12) {
            val generated = QuantumLaserEngine.generateLevel(levelNumber = lvl)
            val initialTrace = QuantumLaserEngine.traceLaser(generated)
            assertFalse("Level $lvl must start unsolved", initialTrace.isVaultsCharged)
            if (lvl >= 4) {
                assertTrue("Level $lvl must include Crystal Beam Splitter", generated.cells.flatten().any { it.kind == QuantumCellKind.SPLITTER })
                assertEquals("Level $lvl must require Dual Gold Vaults", 2, initialTrace.totalVaults)
            }

            // Aligning solution mirrors solves every generated level with 3 Stars & 2x XP
            val solvedCells = generated.cells.map { row ->
                row.map { cell ->
                    val sol = cell.solutionOrientation
                    if (cell.kind == QuantumCellKind.MIRROR && sol != null) {
                        cell.copy(rotationCount = sol)
                    } else {
                        cell
                    }
                }
            }
            val solvedTrace = QuantumLaserEngine.traceLaser(generated.gridSize, solvedCells)
            assertTrue("Level $lvl solution must charge all vaults", solvedTrace.isVaultsCharged)
            assertTrue("Level $lvl solution must achieve 3-Star Perfect Rating", solvedTrace.isThreeStarPerfect)
        }

        // 3. Optical Splitter Dual Beams, Firewall Blocking & Bypass Power-Up, Auto-Align Hint
        val opticalGrid = List(6) { r ->
            List(6) { c ->
                when {
                    r == 2 && c == 0 -> QuantumCell(2, 0, QuantumCellKind.EMITTER, emitDir = LaserDir.RIGHT)
                    r == 2 && c == 1 -> QuantumCell(2, 1, QuantumCellKind.FIREWALL) // Blocks beam initially!
                    r == 2 && c == 2 -> QuantumCell(2, 2, QuantumCellKind.STAR_NODE)
                    r == 2 && c == 3 -> QuantumCell(2, 3, QuantumCellKind.SPLITTER) // Splits RIGHT -> UP & DOWN
                    r == 0 && c == 3 -> QuantumCell(0, 3, QuantumCellKind.VAULT_RECEIVER)
                    r == 5 && c == 3 -> QuantumCell(5, 3, QuantumCellKind.VAULT_RECEIVER)
                    else -> QuantumCell(r, c, QuantumCellKind.EMPTY)
                }
            }
        }
        val firewallTestState = QuantumLevelState(levelNumber = 5, gridSize = 6, cells = opticalGrid)
        val blockedTrace = QuantumLaserEngine.traceLaser(firewallTestState)
        assertFalse(blockedTrace.isVaultsCharged)
        assertTrue(GridPos(2, 1) in blockedTrace.firewallHits)

        val (bypassedState, didBypass) = QuantumLaserEngine.applyFirewallBypass(firewallTestState, 2, 1)
        assertTrue(didBypass)
        assertEquals(0, bypassedState.firewallBypassRemaining)
        val solvedAfterBypass = QuantumLaserEngine.traceLaser(bypassedState)
        assertTrue(solvedAfterBypass.isVaultsCharged)
        assertEquals(2, solvedAfterBypass.chargedVaults.size)
        assertEquals(3, solvedAfterBypass.earnedStarRating)
        assertTrue(QuantumLaserEngine.xpForVictory(5, solvedAfterBypass) > QuantumLaserEngine.xpForVictory(5, solvedAfterBypass.copy(litStars = emptySet(), totalStars = 1)))

        // Auto-Align Hint aligns a misaligned solution mirror cleanly
        val level1 = QuantumLaserEngine.generateLevel(1)
        val (hintedLevel, hintedPos) = QuantumLaserEngine.applyAutoAlignHint(level1)
        assertNotNull(hintedPos)
        assertEquals(0, hintedLevel.hintRemaining)
        val hintedCell = hintedLevel.cellAt(hintedPos!!.row, hintedPos.col)!!
        assertEquals(hintedCell.solutionOrientation, hintedCell.rotationCount % 2)

        // 4. SharedPreferences Highest Level Reached persists strictly highest level
        val bestLvl1 = ArenaProgressStore.updateBestQuantumLevelIfHigher(context, 7)
        assertEquals(7, bestLvl1.bestQuantumLevel)
        val ignoredLowerLvl = ArenaProgressStore.updateBestQuantumLevelIfHigher(context, 3)
        assertEquals(7, ignoredLowerLvl.bestQuantumLevel)
    }

    @Test
    fun `kushwanta mind arena game 6 rapid wealth math duel handles 4 modes, timer freeze, 50-50 zap, fever multiplier, best score, and 10-day permanent unlock`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val arenaPrefs = context.getSharedPreferences("kushwanta_mind_arena_prefs", Context.MODE_PRIVATE)
        val appPrefs = context.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)
        arenaPrefs.edit().clear().commit()
        appPrefs.edit().clear().commit()

        // 1. Smart 10-Day Streak Lock & Permanent Unlock persistence (survives later streak drop!)
        appPrefs.edit().putInt("streak_count", 9).putInt("best_streak", 9).commit()
        val lockedSnapshot = ArenaProgressStore.loadSnapshot(context)
        assertEquals(9, lockedSnapshot.effectiveStreakDays)
        assertFalse(lockedSnapshot.isGame6Unlocked)

        appPrefs.edit().putInt("streak_count", 10).putInt("best_streak", 10).commit()
        val unlockedSnapshot = ArenaProgressStore.loadSnapshot(context)
        assertEquals(10, unlockedSnapshot.effectiveStreakDays)
        assertTrue(unlockedSnapshot.isGame6Unlocked)
        assertTrue(unlockedSnapshot.shouldShowGame6UnlockFlash)
        assertTrue(arenaPrefs.getBoolean("game6_unlocked", false))

        val afterConsumeFlash = ArenaProgressStore.consumeGame6UnlockFlash(context)
        assertFalse(afterConsumeFlash.shouldShowGame6UnlockFlash)
        assertTrue(afterConsumeFlash.isGame6Unlocked)

        // Reset app & arena streak below 10 — Game 6 MUST remain permanently unlocked!
        arenaPrefs.edit().putInt("arena_streak_days", 1).commit()
        appPrefs.edit().putInt("streak_count", 1).putInt("best_streak", 1).commit()
        val afterStreakDrop = ArenaProgressStore.loadSnapshot(context)
        assertTrue(afterStreakDrop.isGame6Unlocked)

        // 2. Infinite Procedural Generator covers all 4 Modes with 4 distinct options & valid correctIndex
        val rng = kotlin.random.Random(2026)
        val generatedQuestions = (1..48).map { num ->
            WealthMathDuelEngine.generateQuestion(
                questionNumber = num,
                difficultyTier = ((num - 1) / 10 + 1).coerceAtMost(5),
                random = rng
            )
        }
        val observedModes = generatedQuestions.map { it.mode }.toSet()
        assertEquals(MathDuelMode.entries.toSet(), observedModes)
        generatedQuestions.forEach { q ->
            assertEquals("Question ${q.id} must have 4 options", 4, q.options.size)
            assertEquals("Question ${q.id} options must be distinct: ${q.options}", 4, q.options.toSet().size)
            assertTrue("Question ${q.id} correctIndex out of bounds", q.correctIndex in 0..3)
            assertTrue("Question ${q.id} prompt blank", q.prompt.isNotBlank())
        }

        // 3. Streak Multiplier Ladder (2x at 4, 3x at 6, 5x at 9+), Gold Fever & Reset on Wrong Answer
        var state: MathDuelState = WealthMathDuelEngine.newGame(random = rng)
        assertFalse(state.isGoldFeverActive)
        assertEquals(1, state.multiplier)

        repeat(4) {
            val res = WealthMathDuelEngine.submitAnswer(state, state.currentQuestion.correctIndex, random = rng)
            assertTrue(res.wasCorrect)
            assertTrue(res.pointsDelta > 0L)
            state = res.nextState
        }
        assertEquals(4, state.currentStreak)
        assertTrue(state.isGoldFeverActive)
        assertEquals(2, state.multiplier)

        repeat(2) {
            state = WealthMathDuelEngine.submitAnswer(state, state.currentQuestion.correctIndex, random = rng).nextState
        }
        assertEquals(6, state.currentStreak)
        assertEquals(3, state.multiplier)

        repeat(3) {
            state = WealthMathDuelEngine.submitAnswer(state, state.currentQuestion.correctIndex, random = rng).nextState
        }
        assertEquals(9, state.currentStreak)
        assertEquals(5, state.multiplier)
        assertEquals(9, state.correctCount)

        // Wrong answer resets streak & multiplier cleanly without decreasing score
        val scoreBeforeWrong = state.score
        val wrongIndex = (state.currentQuestion.correctIndex + 1) % 4
        val wrongRes = WealthMathDuelEngine.submitAnswer(state, wrongIndex, random = rng)
        assertFalse(wrongRes.wasCorrect)
        assertEquals(0L, wrongRes.pointsDelta)
        state = wrongRes.nextState
        assertEquals(0, state.currentStreak)
        assertFalse(state.isGoldFeverActive)
        assertEquals(1, state.multiplier)
        assertEquals(1, state.wrongCount)
        assertEquals(scoreBeforeWrong, state.score)
        assertEquals(90, state.accuracyPercent)

        // 4. 50:50 Quantum Eliminate Power-Up hides 2 wrong answers & never eliminates correctIndex
        val beforeElimCorrect = state.currentQuestion.correctIndex
        val eliminatedState = WealthMathDuelEngine.applyFiftyFiftyEliminate(state, random = rng)
        assertEquals(2, eliminatedState.eliminatedIndices.size)
        assertFalse(beforeElimCorrect in eliminatedState.eliminatedIndices)
        assertEquals(0, eliminatedState.fiftyFiftyRemaining)

        // 5. Time Freeze Shield (5s) pauses countdown then resumes cleanly to Game Over at 0ms
        val timeBeforeFreeze = eliminatedState.timeRemainingMs
        val frozenState = WealthMathDuelEngine.activateTimeFreeze(eliminatedState)
        assertTrue(frozenState.isFreezeActive)
        assertEquals(WealthMathDuelEngine.TIME_FREEZE_DURATION_MS, frozenState.freezeRemainingMs)
        assertEquals(0, frozenState.freezeShieldRemaining)

        val midFreeze = WealthMathDuelEngine.stepTimer(frozenState, 3_000L)
        assertTrue(midFreeze.isFreezeActive)
        assertEquals(2_000L, midFreeze.freezeRemainingMs)
        assertEquals(timeBeforeFreeze, midFreeze.timeRemainingMs) // Main timer stayed paused!

        val afterFreeze = WealthMathDuelEngine.stepTimer(midFreeze, 3_000L) // 2s left in freeze, 1s spills to timer
        assertFalse(afterFreeze.isFreezeActive)
        assertEquals(timeBeforeFreeze - 1_000L, afterFreeze.timeRemainingMs)

        val finished = WealthMathDuelEngine.stepTimer(afterFreeze, WealthMathDuelEngine.ROUND_DURATION_MS)
        assertTrue(finished.isGameOver)
        assertEquals(0L, finished.timeRemainingMs)

        // 6. Best Duel Score persistence keeps strictly highest score
        val best1 = ArenaProgressStore.updateBestMathDuelScoreIfHigher(context, 4_250L)
        assertEquals(4_250L, best1.bestMathDuelScore)
        val ignoredLowerBest = ArenaProgressStore.updateBestMathDuelScoreIfHigher(context, 1_900L)
        assertEquals(4_250L, ignoredLowerBest.bestMathDuelScore)
    }

    @Test
    fun `kushwanta mind arena game 7 neon brick breaker handles 4 brick types, plasma shockwaves, capsules, laser cannon, revive, waves, and 3 no-spend days permanent unlock`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val arenaPrefs = context.getSharedPreferences("kushwanta_mind_arena_prefs", Context.MODE_PRIVATE)
        val appPrefs = context.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)
        arenaPrefs.edit().clear().commit()
        appPrefs.edit().clear().commit()

        // 1. Smart 3 No-Spend Days Lock & Permanent Unlock persistence
        assertEquals(0, ArenaProgressStore.countRecordedNoSpendDays(emptyList(), emptySet()))

        val dayMs = 24L * 3_600_000L
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 12)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val now = cal.timeInMillis

        // Only 2 completed no-spend days -> still locked (2/3 Days)
        val twoDaysTxs = listOf(
            TransactionEntity(
                bookId = FinancialBook.PERSONAL.id,
                accountId = 1L,
                categoryId = 1L,
                amount = 45.0,
                type = TransactionType.EXPENSE,
                timestamp = now - 3L * dayMs
            )
        )
        val count2 = ArenaProgressStore.countRecordedNoSpendDays(twoDaysTxs, emptySet(), nowMillis = now)
        assertEquals(2, count2)
        val lockedSnap = ArenaProgressStore.syncNoSpendDays(context, count2)
        assertFalse(lockedSnap.isGame7Unlocked)
        assertEquals(2, lockedSnap.recordedNoSpendDays)

        // 3 completed no-spend days (-3, -2, -1) -> permanently unlocked!
        val threeDaysTxs = listOf(
            TransactionEntity(
                bookId = FinancialBook.PERSONAL.id,
                accountId = 1L,
                categoryId = 1L,
                amount = 45.0,
                type = TransactionType.EXPENSE,
                timestamp = now - 4L * dayMs
            ),
            TransactionEntity(
                bookId = FinancialBook.PERSONAL.id,
                accountId = 2L,
                categoryId = 5L,
                amount = 300.0,
                type = TransactionType.INCOME,
                timestamp = now - 2L * dayMs
            )
        )
        val count3 = ArenaProgressStore.countRecordedNoSpendDays(threeDaysTxs, emptySet(), nowMillis = now)
        assertEquals(3, count3)
        val unlockedSnap = ArenaProgressStore.syncNoSpendDays(context, count3)
        assertTrue(unlockedSnap.isGame7Unlocked)
        assertTrue(unlockedSnap.shouldShowGame7UnlockFlash)
        assertTrue(arenaPrefs.getBoolean("game7_unlocked", false))

        val afterFlashSnap = ArenaProgressStore.consumeGame7UnlockFlash(context)
        assertFalse(afterFlashSnap.shouldShowGame7UnlockFlash)
        assertTrue(afterFlashSnap.isGame7Unlocked)

        // Even if tracked No-Spend Days later drops back to 0, Game 7 stays permanently unlocked
        val afterReset = ArenaProgressStore.syncNoSpendDays(context, 0)
        assertTrue(afterReset.isGame7Unlocked)

        // 2. Procedural Wave Formations include all 4 Crystal Brick Types & wave speed scaling
        val rng = kotlin.random.Random(77)
        for (w in 1..6) {
            val (waveBricks, _) = NeonBreakerEngine.generateWave(wave = w, random = rng)
            assertTrue("Wave $w should spawn bricks", waveBricks.isNotEmpty())
            assertTrue("Wave $w must include Gold Bonus", waveBricks.any { it.type == BreakerBrickType.GOLD_BONUS })
            assertTrue("Wave $w must include Plasma Core", waveBricks.any { it.type == BreakerBrickType.PLASMA_CORE })
            assertTrue("Wave $w must include Titanium Shield", waveBricks.any { it.type == BreakerBrickType.TITANIUM_SHIELD })
        }
        assertTrue(NeonBreakerEngine.waveSpeedFactor(5) > NeonBreakerEngine.waveSpeedFactor(1))

        // 3. Titanium 2-Hit Cracking + Plasma Core 8-Neighbor Chain Explosion + Gold Capsule Drop
        val core = BreakerBrick(1L, row = 1, col = 1, type = BreakerBrickType.PLASMA_CORE, hp = 1, maxHp = 1, left = 0.25f, top = 0.25f, right = 0.35f, bottom = 0.30f)
        val neighborGlass = BreakerBrick(2L, row = 0, col = 1, type = BreakerBrickType.CYAN_GLASS, hp = 1, maxHp = 1, left = 0.25f, top = 0.19f, right = 0.35f, bottom = 0.24f)
        val neighborGold = BreakerBrick(3L, row = 1, col = 2, type = BreakerBrickType.GOLD_BONUS, hp = 1, maxHp = 1, left = 0.36f, top = 0.25f, right = 0.46f, bottom = 0.30f)
        val distantTitanium = BreakerBrick(4L, row = 4, col = 6, type = BreakerBrickType.TITANIUM_SHIELD, hp = 2, maxHp = 2, left = 0.75f, top = 0.55f, right = 0.85f, bottom = 0.60f)

        val chainState = BreakerGameState(
            bricks = listOf(core, neighborGlass, neighborGold, distantTitanium),
            balls = listOf(BreakerBall(id = 10L, x = 0.30f, y = 0.32f, vx = 0f, vy = -0.9f)),
            nextEntityId = 200L
        )
        val afterCoreHit = NeonBreakerEngine.step(chainState, dtSec = 0.016f, random = rng)
        assertTrue(afterCoreHit.explosions.isNotEmpty())
        // Core + neighborGlass + neighborGold all destroyed by chain reaction, leaving only distantTitanium!
        assertEquals(listOf(4L), afterCoreHit.nextState.bricks.map { it.id })
        assertTrue("Gold Brick destroyed by shockwave must drop capsule", afterCoreHit.nextState.capsules.isNotEmpty())
        assertTrue(afterCoreHit.pointsGained >= 135L)

        // Titanium Shield cracks on Hit 1 without shattering
        val tiState = BreakerGameState(
            bricks = listOf(distantTitanium),
            balls = listOf(BreakerBall(id = 11L, x = 0.80f, y = 0.625f, vx = 0f, vy = -0.9f))
        )
        val afterFirstTiHit = NeonBreakerEngine.step(tiState, dtSec = 0.016f, random = rng)
        val crackedTi = afterFirstTiHit.nextState.bricks.first()
        assertEquals(1, crackedTi.hp)
        assertTrue(crackedTi.isCracked)

        // 4. Falling Capsules: 3x Multi-Ball Split, Wide Paddle (10s), Hyper Fireball (6s piercing without bouncing!)
        val multiState = NeonBreakerEngine.applyCapsuleEffect(tiState, BreakerCapsuleType.MULTI_BALL)
        assertEquals(3, multiState.balls.size)

        val wideState = NeonBreakerEngine.applyCapsuleEffect(multiState, BreakerCapsuleType.WIDE_PADDLE)
        assertTrue(wideState.isWidePaddleActive)
        assertEquals(NeonBreakerEngine.WIDE_PADDLE_W, wideState.paddleWidth, 0.001f)

        // Fireball pierces brick straight through (vy remains negative/upward without bouncing down!)
        val fireGlass = BreakerBrick(99L, row = 2, col = 3, type = BreakerBrickType.CYAN_GLASS, hp = 1, maxHp = 1, left = 0.45f, top = 0.40f, right = 0.55f, bottom = 0.45f)
        val keeperBrick = BreakerBrick(100L, row = 0, col = 0, type = BreakerBrickType.CYAN_GLASS, hp = 1, maxHp = 1, left = 0.05f, top = 0.10f, right = 0.15f, bottom = 0.15f)
        val fireState = NeonBreakerEngine.applyCapsuleEffect(
            BreakerGameState(
                bricks = listOf(fireGlass, keeperBrick),
                balls = listOf(BreakerBall(id = 12L, x = 0.50f, y = 0.47f, vx = 0f, vy = -1.0f))
            ),
            BreakerCapsuleType.HYPER_FIREBALL
        )
        val afterFirePierce = NeonBreakerEngine.step(fireState, dtSec = 0.02f, random = rng)
        assertFalse(afterFirePierce.nextState.bricks.any { it.id == 99L })
        assertTrue("Hyper Fireball must pierce without downward bounce", afterFirePierce.nextState.balls.first().vy < 0f)

        // 5. Bottom HUD Power-Ups: Laser Cannon Burst (5s) + Second Chance Extra Ball (3s Revive Window)
        val laserActive = NeonBreakerEngine.activateLaserCannon(BreakerGameState(bricks = listOf(keeperBrick), balls = listOf(BreakerBall(13L, 0.5f, 0.5f, 0f, -0.5f))))
        assertTrue(laserActive.isLaserCannonActive)
        val afterLaserStep = NeonBreakerEngine.step(laserActive, dtSec = 0.016f, random = rng)
        assertTrue(afterLaserStep.firedLasers)
        assertEquals(2, afterLaserStep.nextState.laserShots.size)

        // Last life lost opens 3s Revive Window instead of immediate Game Over
        val dyingState = BreakerGameState(
            lives = 1,
            secondChanceRemaining = 1,
            bricks = listOf(keeperBrick),
            balls = listOf(BreakerBall(14L, 0.5f, NeonBreakerEngine.BOARD_H - 0.005f, 0f, 1.2f))
        )
        val revivePromptState = NeonBreakerEngine.step(dyingState, dtSec = 0.03f, random = rng).nextState
        assertTrue(revivePromptState.isRevivePromptActive)
        assertFalse(revivePromptState.isGameOver)

        val revivedState = NeonBreakerEngine.activateSecondChanceRevive(revivePromptState, random = rng)
        assertFalse(revivedState.isRevivePromptActive)
        assertEquals(1, revivedState.lives)
        assertEquals(1, revivedState.balls.size)
        assertEquals(0, revivedState.secondChanceRemaining)

        // 6. Highest Score & Highest Wave persistence keeps strictly highest
        val best1 = ArenaProgressStore.updateBestBrickBreakerStatsIfHigher(context, score = 8_600L, wave = 6)
        assertEquals(8_600L, best1.bestBrickBreakerScore)
        assertEquals(6, best1.bestBrickBreakerWave)
        val best2 = ArenaProgressStore.updateBestBrickBreakerStatsIfHigher(context, score = 1_200L, wave = 2)
        assertEquals(8_600L, best2.bestBrickBreakerScore)
        assertEquals(6, best2.bestBrickBreakerWave)
    }

    @Test
    fun `kushwanta mind arena game 8 chrono vault matrix handles goal and budget permanent unlock, 3x3 to 6x6 scaling, 3 modes, neural replay, error shield, and best stats`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val arenaPrefs = context.getSharedPreferences("kushwanta_mind_arena_prefs", Context.MODE_PRIVATE)
        val appPrefs = context.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)
        arenaPrefs.edit().clear().commit()
        appPrefs.edit().clear().commit()

        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.DAY_OF_MONTH, 15)
            set(java.util.Calendar.HOUR_OF_DAY, 12)
        }
        val now = cal.timeInMillis
        val curYear = cal.get(java.util.Calendar.YEAR)
        val curMonth = cal.get(java.util.Calendar.MONTH) + 1

        // 1. Smart Goal / Monthly Budget Lock & Permanent Unlock persistence
        assertFalse(ArenaProgressStore.evaluateGoalOrBudgetMilestone(emptyList(), emptyList(), emptyList(), now))
        val lockedSnap = ArenaProgressStore.loadSnapshot(context)
        assertFalse(lockedSnap.isGame8Unlocked)

        // Over-budget active month must NOT unlock
        val overBudget = BudgetEntity(
            bookId = FinancialBook.PERSONAL.id,
            categoryId = 1L,
            monthlyLimit = 200.0,
            periodMonth = curMonth,
            periodYear = curYear
        )
        val overTx = TransactionEntity(
            bookId = FinancialBook.PERSONAL.id,
            accountId = 1L,
            categoryId = 1L,
            amount = 260.0,
            type = TransactionType.EXPENSE,
            timestamp = now
        )
        assertFalse(ArenaProgressStore.evaluateGoalOrBudgetMilestone(emptyList(), listOf(overBudget), listOf(overTx), now))

        // Under-budget active month with logged transaction unlocks!
        val underTx = overTx.copy(amount = 140.0)
        assertTrue(ArenaProgressStore.evaluateGoalOrBudgetMilestone(emptyList(), listOf(overBudget), listOf(underTx), now))

        // Completed Savings Goal unlocks!
        val completedGoal = SavingsGoalEntity(
            bookId = FinancialBook.PERSONAL.id,
            name = "Emergency Reserve",
            targetAmount = 1_000.0,
            currentAmount = 1_000.0,
            targetDate = now + 86_400_000L
        )
        assertTrue(ArenaProgressStore.evaluateGoalOrBudgetMilestone(listOf(completedGoal), emptyList(), emptyList(), now))

        val unlockedSnap = ArenaProgressStore.syncGoalOrBudgetAchieved(context, true)
        assertTrue(unlockedSnap.isGame8Unlocked)
        assertTrue(unlockedSnap.shouldShowGame8UnlockFlash)
        assertTrue(arenaPrefs.getBoolean("game8_unlocked", false))

        val afterFlash = ArenaProgressStore.consumeGame8UnlockFlash(context)
        assertFalse(afterFlash.shouldShowGame8UnlockFlash)
        assertTrue(afterFlash.isGame8Unlocked)

        // Remains permanently unlocked even if milestone cache toggles false later
        val stillUnlocked = ArenaProgressStore.syncGoalOrBudgetAchieved(context, false)
        assertTrue(stillUnlocked.isGame8Unlocked)

        // 2. Dynamic Grid Progression 3x3 -> 4x4 -> 5x5 -> 6x6
        assertEquals(3, ChronoVaultEngine.gridSizeForRound(1))
        assertEquals(3, ChronoVaultEngine.gridSizeForRound(2))
        assertEquals(4, ChronoVaultEngine.gridSizeForRound(3))
        assertEquals(4, ChronoVaultEngine.gridSizeForRound(5))
        assertEquals(5, ChronoVaultEngine.gridSizeForRound(6))
        assertEquals(5, ChronoVaultEngine.gridSizeForRound(9))
        assertEquals(6, ChronoVaultEngine.gridSizeForRound(10))

        // 3. 2-Second Hologram Flash (MEMORIZING -> RECALL) & Classic Pattern clear
        val rng = kotlin.random.Random(88)
        val r1Memorizing = ChronoVaultEngine.newGame(random = rng)
        assertEquals(ChronoVaultMode.CLASSIC_PATTERN, r1Memorizing.mode)
        assertEquals(ChronoPhase.MEMORIZING, r1Memorizing.phase)
        assertEquals(3, r1Memorizing.gridSize)

        val r1Recall = ChronoVaultEngine.stepTime(r1Memorizing, ChronoVaultEngine.FLASH_DURATION_MS)
        assertEquals(ChronoPhase.RECALL, r1Recall.phase)
        assertFalse(r1Recall.isFlashingPattern)

        var playing = r1Recall
        r1Recall.targets.forEach { target ->
            val out = ChronoVaultEngine.tapCell(playing, target.row, target.col)
            assertTrue(out.isCorrect)
            playing = out.nextState
        }
        assertEquals(ChronoPhase.ROUND_CLEAR, playing.phase)
        assertTrue(playing.score > 0L)

        // 4. Mode B (Chrono-Code Sequence 1 -> 2 -> 3 -> 4...) enforces chronological order
        val r2Mem = ChronoVaultEngine.nextRound(playing, forcedMode = ChronoVaultMode.CHRONO_SEQUENCE, random = rng)
        assertEquals(ChronoVaultMode.CHRONO_SEQUENCE, r2Mem.mode)
        assertNotNull(r2Mem.activeSequentialStepOrder())
        val r2Recall = ChronoVaultEngine.stepTime(r2Mem, ChronoVaultEngine.FLASH_DURATION_MS)

        // Tapping step #2 before step #1 must fail order validation, unless Error Shield absorbs it!
        val shieldedR2 = ChronoVaultEngine.activateErrorShield(r2Recall)
        assertTrue(shieldedR2.isErrorShieldArmed)
        assertEquals(0, shieldedR2.errorShieldRemaining)

        val secondTarget = shieldedR2.targets[1]
        val wrongOrderOutcome = ChronoVaultEngine.tapCell(shieldedR2, secondTarget.row, secondTarget.col)
        assertFalse(wrongOrderOutcome.isCorrect)
        assertTrue(wrongOrderOutcome.absorbedByShield)
        assertEquals(3, wrongOrderOutcome.nextState.shields) // Error Shield forgave wrong tap without losing shield life!
        assertFalse(wrongOrderOutcome.nextState.isErrorShieldArmed)

        // Neural Replay (1.5s Re-Flash) re-illuminates matrix cleanly
        val replayed = ChronoVaultEngine.activateNeuralReplay(wrongOrderOutcome.nextState)
        assertEquals(ChronoVaultEngine.REPLAY_FLASH_DURATION_MS, replayed.replayFlashRemainingMs)
        assertTrue(replayed.isFlashingPattern)
        assertEquals(0, replayed.neuralReplayRemaining)
        val afterReplay = ChronoVaultEngine.stepTime(replayed, ChronoVaultEngine.REPLAY_FLASH_DURATION_MS)
        assertFalse(afterReplay.isFlashingPattern)

        // Tap steps 1..N in exact order -> clears sequence round!
        var seqState = afterReplay
        seqState.targets.sortedBy { it.orderIndex }.forEach { target ->
            val res = ChronoVaultEngine.tapCell(seqState, target.row, target.col)
            assertTrue("Tapping step #${target.orderIndex} in exact sequence must succeed", res.isCorrect)
            seqState = res.nextState
        }
        assertEquals(ChronoPhase.ROUND_CLEAR, seqState.phase)

        // 5. Mode C (24K Golden Bonus Node) awards 2x score & bonus XP
        val r3Mem = ChronoVaultEngine.nextRound(seqState, forcedMode = ChronoVaultMode.GOLDEN_BONUS_MIX, random = rng)
        assertEquals(ChronoVaultMode.GOLDEN_BONUS_MIX, r3Mem.mode)
        assertEquals(4, r3Mem.gridSize)
        assertTrue("Golden Bonus mode must contain at least 1 24K Gold node", r3Mem.targets.any { it.isGoldBonus })
        val r3Recall = ChronoVaultEngine.stepTime(r3Mem, ChronoVaultEngine.FLASH_DURATION_MS)
        val goldNode = r3Recall.targets.first { it.isGoldBonus }
        val cyanNode = r3Recall.targets.first { !it.isGoldBonus }

        val cyanOut = ChronoVaultEngine.tapCell(r3Recall, cyanNode.row, cyanNode.col)
        val goldOut = ChronoVaultEngine.tapCell(r3Recall, goldNode.row, goldNode.col)
        assertTrue(goldOut.tappedGoldBonus)
        assertEquals(cyanOut.pointsGained * 2L, goldOut.pointsGained)
        assertTrue(goldOut.xpGained > cyanOut.xpGained)

        // 6. Wrong taps deplete 3 Shields to Game Over & Highest Round/Score persistence works
        var missState = r3Recall
        val targetCoords = missState.targets.map { it.row to it.col }.toSet()
        val nonTargets = buildList {
            for (r in 0 until missState.gridSize) {
                for (c in 0 until missState.gridSize) {
                    if ((r to c) !in targetCoords) add(r to c)
                }
            }
        }
        for (i in 0 until 3) {
            val (wr, wc) = nonTargets[i]
            missState = ChronoVaultEngine.tapCell(missState, wr, wc).nextState
        }
        assertEquals(0, missState.shields)
        assertTrue(missState.isGameOver)
        assertEquals(ChronoPhase.GAME_OVER, missState.phase)

        val best1 = ArenaProgressStore.updateBestChronoStatsIfHigher(context, score = 6_450L, round = 11)
        assertEquals(6_450L, best1.bestChronoScore)
        assertEquals(11, best1.bestChronoRound)
        val best2 = ArenaProgressStore.updateBestChronoStatsIfHigher(context, score = 950L, round = 2)
        assertEquals(6_450L, best2.bestChronoScore)
        assertEquals(11, best2.bestChronoRound)
    }

    @Test
    fun `kushwanta mind arena game 9 gravity orbit handles 14-day streak permanent unlock, 360 shield deflection, yield orbs multiplier, laser dart telegraph, gold overdrive, nova 360, emp blast, waves, and best stats`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val arenaPrefs = context.getSharedPreferences("kushwanta_mind_arena_prefs", Context.MODE_PRIVATE)
        val appPrefs = context.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)
        arenaPrefs.edit().clear().commit()
        appPrefs.edit().clear().commit()

        // 1. Smart 14-Day Streak Lock & Permanent Unlock persistence
        appPrefs.edit().putInt("streak_count", 13).commit()
        val lockedSnap = ArenaProgressStore.loadSnapshot(context)
        assertFalse(lockedSnap.isGame9Unlocked)
        assertEquals(13, lockedSnap.effectiveStreakDays)

        appPrefs.edit().putInt("streak_count", 14).commit()
        val unlockedSnap = ArenaProgressStore.loadSnapshot(context)
        assertTrue(unlockedSnap.isGame9Unlocked)
        assertTrue(unlockedSnap.shouldShowGame9UnlockFlash)
        assertTrue(arenaPrefs.getBoolean("game9_unlocked", false))

        val afterFlash = ArenaProgressStore.consumeGame9UnlockFlash(context)
        assertFalse(afterFlash.shouldShowGame9UnlockFlash)
        assertTrue(afterFlash.isGame9Unlocked)

        // Remains permanently unlocked forever even if streak resets to 0 later
        appPrefs.edit().putInt("streak_count", 0).putInt("best_streak", 0).commit()
        val afterStreakReset = ArenaProgressStore.loadSnapshot(context)
        assertTrue(afterStreakReset.isGame9Unlocked)

        // 2. 360° Shield Rotation & Debt Asteroid Deflection
        val rng = kotlin.random.Random(99)
        val shieldNorth = GravityOrbitState(
            shieldAngleRad = 0f,
            entities = listOf(
                OrbitEntity(
                    id = 1L,
                    kind = OrbitEntityKind.DEBT_ASTEROID,
                    angleRad = 0.1f,
                    distance = GravityOrbitEngine.ORBIT_RADIUS + 0.02f,
                    speed = 0.5f
                ),
                OrbitEntity(
                    id = 2L,
                    kind = OrbitEntityKind.DEBT_ASTEROID,
                    angleRad = kotlin.math.PI.toFloat(),
                    distance = GravityOrbitEngine.CORE_RADIUS + 0.02f,
                    speed = 0.6f
                )
            )
        )
        val step1 = GravityOrbitEngine.step(shieldNorth, dtSec = 0.08f, random = rng)
        assertEquals(1, step1.deflectedHazards) // Entity 1 shattered by shield at 0 rad!
        assertEquals(1, step1.coreDamageHits) // Entity 2 opposite shield slipped into core (-1 life)!
        assertEquals(2, step1.nextState.lives)
        assertTrue(step1.pointsGained > 0L)

        // 3. Yield Orbs climb Multiplier (1x -> 2x -> 3x -> 5x) when rotated out of shield path
        var orbState = GravityOrbitState(shieldAngleRad = kotlin.math.PI.toFloat())
        for (i in 1..9) {
            orbState = orbState.copy(
                entities = listOf(
                    OrbitEntity(
                        id = 100L + i,
                        kind = OrbitEntityKind.YIELD_ORB,
                        angleRad = 0f,
                        distance = GravityOrbitEngine.CORE_RADIUS + 0.01f,
                        speed = 0.5f
                    )
                )
            )
            val res = GravityOrbitEngine.step(orbState, dtSec = 0.06f, random = rng)
            assertEquals(1, res.absorbedYieldOrbs)
            orbState = res.nextState
        }
        assertEquals(9, orbState.orbStreak)
        assertEquals(5, orbState.multiplier)

        // 4. Fee Laser Dart Telegraph + Gold Overdrive Star (5s 2x multiplier & golden aura)
        val dartAndStar = orbState.copy(
            shieldAngleRad = 0f,
            entities = listOf(
                OrbitEntity(
                    id = 500L,
                    kind = OrbitEntityKind.FEE_LASER_DART,
                    angleRad = 0f,
                    distance = 0.85f,
                    speed = 1.0f,
                    telegraphRemainingMs = 300L
                ),
                OrbitEntity(
                    id = 501L,
                    kind = OrbitEntityKind.GOLD_OVERDRIVE_STAR,
                    angleRad = kotlin.math.PI.toFloat(),
                    distance = GravityOrbitEngine.CORE_RADIUS + 0.01f,
                    speed = 0.5f
                )
            )
        )
        val afterOverdrive = GravityOrbitEngine.step(dartAndStar, dtSec = 0.1f, random = rng)
        assertTrue(afterOverdrive.triggeredOverdrive)
        assertTrue(afterOverdrive.nextState.isOverdriveActive)
        assertEquals(GravityOrbitEngine.OVERDRIVE_DURATION_MS, afterOverdrive.nextState.overdriveRemainingMs)
        assertEquals(10, afterOverdrive.nextState.effectiveScoreMultiplier) // 5x orb multiplier * 2x Gold Overdrive!
        // Dart stayed at spawn distance during telegraph countdown (0.85f)
        val teleDart = afterOverdrive.nextState.entities.first { it.id == 500L }
        assertEquals(0.85f, teleDart.distance, 0.001f)
        assertEquals(200L, teleDart.telegraphRemainingMs)

        // 5. Tactical Power-Ups: 360° Nova Shield (blocks any angle hazard, lets Yield Orbs pass!) & EMP Shockwave Blast
        val novaActive = GravityOrbitEngine.activateNovaShield(
            GravityOrbitState(
                shieldAngleRad = 0f,
                entities = listOf(
                    OrbitEntity(10L, OrbitEntityKind.DEBT_ASTEROID, angleRad = 2.8f, distance = GravityOrbitEngine.ORBIT_RADIUS + 0.02f, speed = 0.6f),
                    OrbitEntity(11L, OrbitEntityKind.YIELD_ORB, angleRad = 0.1f, distance = GravityOrbitEngine.ORBIT_RADIUS + 0.02f, speed = 0.6f)
                )
            )
        )
        assertTrue(novaActive.isNovaShieldActive)
        assertEquals(0, novaActive.novaPowerUpRemaining)
        val novaStep = GravityOrbitEngine.step(novaActive, dtSec = 0.08f, random = rng)
        assertEquals(1, novaStep.deflectedHazards) // Behind-shield asteroid at 2.8 rad deflected by 360° Nova Dome!
        assertTrue("Yield Orb must pass cleanly inside 360° Nova Dome toward Core", novaStep.nextState.entities.any { it.id == 11L })

        val empPre = GravityOrbitState(
            entities = listOf(
                OrbitEntity(21L, OrbitEntityKind.DEBT_ASTEROID, 0.5f, 0.7f, 0.3f),
                OrbitEntity(22L, OrbitEntityKind.FEE_LASER_DART, -1.1f, 0.6f, 0.7f),
                OrbitEntity(23L, OrbitEntityKind.YIELD_ORB, 2.0f, 0.6f, 0.3f)
            )
        )
        val (afterEmp, empSparks, empXp) = GravityOrbitEngine.triggerEmpBlast(empPre)
        assertEquals(0, afterEmp.empBlastPowerUpRemaining)
        assertEquals(2, empSparks.size) // 2 hazards vaporized
        assertEquals(listOf(23L), afterEmp.entities.map { it.id }) // Beneficial Yield Orb preserved!
        assertTrue(afterEmp.score > 0L && empXp > 0L)

        // 6. Infinite 20s Wave Escalation & Highest Wave / Best Score Persistence
        var waveState = GravityOrbitState(novaShieldRemainingMs = 25_000L)
        repeat(40) {
            waveState = GravityOrbitEngine.step(waveState, dtSec = 0.5f, random = rng).nextState
        }
        assertEquals(2, waveState.wave)
        assertTrue(GravityOrbitEngine.waveSpeedScale(3) > GravityOrbitEngine.waveSpeedScale(1))

        val best1 = ArenaProgressStore.updateBestOrbitStatsIfHigher(context, score = 12_850L, wave = 7)
        assertEquals(12_850L, best1.bestOrbitScore)
        assertEquals(7, best1.bestOrbitWave)
        val best2 = ArenaProgressStore.updateBestOrbitStatsIfHigher(context, score = 2_100L, wave = 3)
        assertEquals(12_850L, best2.bestOrbitScore)
        assertEquals(7, best2.bestOrbitWave)
    }

    @Test
    fun `kushwanta mind arena game 10 titanium citadel handles dual VIP unlock, 3 chambers, chrono slow, aegis revive, tier escalation, and grandmaster architect rank`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val arenaPrefs = context.getSharedPreferences("kushwanta_mind_arena_prefs", Context.MODE_PRIVATE)
        val appPrefs = context.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)
        arenaPrefs.edit().clear().commit()
        appPrefs.edit().clear().commit()

        // 1. VIP Dual-Path Unlock Logic: locked when streak < 21 AND arena_xp < 2,500
        appPrefs.edit().putInt("streak_count", 20).commit()
        arenaPrefs.edit().putLong("arena_total_xp", 2_499L).commit()
        val lockedSnap = ArenaProgressStore.loadSnapshot(context)
        assertFalse(lockedSnap.isGame10Unlocked)

        // Path A: 2,500+ Arena XP unlocks permanently even if streak < 21
        val xpUnlocked = ArenaProgressStore.awardXpAndTouchStreak(context, xpDelta = 1L)
        assertTrue(xpUnlocked.totalXp >= 2_500L)
        assertTrue(xpUnlocked.isGame10Unlocked)
        assertTrue(xpUnlocked.shouldShowGame10UnlockFlash)
        assertTrue(arenaPrefs.getBoolean("game10_unlocked", false))

        val afterFlash = ArenaProgressStore.consumeGame10UnlockFlash(context)
        assertFalse(afterFlash.shouldShowGame10UnlockFlash)
        assertTrue(afterFlash.isGame10Unlocked)

        // Path B: 21-Day Streak unlocks permanently even if XP == 0
        arenaPrefs.edit().clear().commit()
        appPrefs.edit().clear().putInt("streak_count", 21).commit()
        val streakUnlocked = ArenaProgressStore.loadSnapshot(context)
        assertTrue(streakUnlocked.isGame10Unlocked)
        assertTrue(arenaPrefs.getBoolean("game10_unlocked", false))

        // Remains permanently unlocked forever even if streak resets to 0 and XP is reset to 0
        appPrefs.edit().putInt("streak_count", 0).putInt("best_streak", 0).commit()
        arenaPrefs.edit().putLong("arena_total_xp", 0L).commit()
        val permanentSnap = ArenaProgressStore.loadSnapshot(context)
        assertTrue(permanentSnap.isGame10Unlocked)

        // 2. Chamber 1 (Laser Vault Lock-Pick): Gold Bullseye shatter + 3 locks clear transitions immediately to Chamber 2
        val rng = kotlin.random.Random(2026)
        var c1State = TitaniumCitadelState(
            chamber = CitadelChamber.CHAMBER_1_LOCK_PICK,
            tier = 1,
            needleAngleDeg = 100f,
            targetArcCenterDeg = 102f, // Within goldBullseyeHalfSpanDeg = 10f
            locksRemainingInStage = 2
        )
        val lockHit1 = TitaniumCitadelEngine.tapLockPick(c1State, random = rng)
        assertTrue(lockHit1.hit)
        assertTrue(lockHit1.isGoldBullseye)
        assertFalse(lockHit1.stageCleared)
        assertEquals(1, lockHit1.nextState.locksRemainingInStage)
        assertTrue(lockHit1.pointsGained >= 150L)

        // Align needle to new target center and shatter final lock -> transitions to Chamber 2!
        c1State = lockHit1.nextState.copy(needleAngleDeg = lockHit1.nextState.targetArcCenterDeg)
        val lockHit2 = TitaniumCitadelEngine.tapLockPick(c1State, random = rng)
        assertTrue(lockHit2.hit)
        assertTrue(lockHit2.stageCleared)
        assertEquals(CitadelChamber.CHAMBER_2_FIREWALL_DODGE, lockHit2.nextState.chamber)

        // 3. Chamber 2 (Cyber Firewall Dodge): Safe gap weave + 24K Gold Diamond catch & Chrono-Slow 50% speed
        val firewallState = TitaniumCitadelEngine.slideCoreTo(
            TitaniumCitadelEngine.activateChronoSlow(
                TitaniumCitadelState(
                    chamber = CitadelChamber.CHAMBER_2_FIREWALL_DODGE,
                    playerX = 0.5f,
                    firewallEntities = listOf(
                        CitadelFirewallEntity(
                            id = 1L,
                            kind = CitadelFirewallKind.LASER_WALL,
                            x = 0.5f,
                            y = TitaniumCitadelEngine.CORE_Y - 0.05f,
                            gapHalfWidth = 0.19f,
                            speed = 0.8f
                        ),
                        CitadelFirewallEntity(
                            id = 2L,
                            kind = CitadelFirewallKind.GOLD_DIAMOND,
                            x = 0.5f,
                            y = TitaniumCitadelEngine.CORE_Y - 0.05f,
                            speed = 0.8f
                        )
                    )
                )
            ),
            normX = 0.5f
        )
        assertTrue(firewallState.isChronoSlowActive)
        // Because Chrono-Slow halves speed (0.8f * 0.5f = 0.4f), dt = 0.125s advances Y by 0.05f cleanly across CORE_Y
        val c2Step = TitaniumCitadelEngine.step(firewallState, dtSec = 0.125f, random = rng)
        assertTrue(c2Step.diamondCollected)
        assertFalse(c2Step.damageTaken)
        assertEquals(1, c2Step.nextState.goldDiamondsCollected)
        assertEquals(2, c2Step.nextState.combo)
        assertEquals(TitaniumCitadelEngine.CHRONO_SLOW_DURATION_MS - 125L, c2Step.nextState.chronoSlowRemainingMs)

        // 4. Chamber 3 (Quantum Code Override): Flashing sequence -> INPUT -> Complete sequence -> Tier 2 Escalation!
        var c3Flash = TitaniumCitadelState(
            chamber = CitadelChamber.CHAMBER_3_QUANTUM_OVERRIDE,
            tier = 1,
            overrideSequence = listOf(0, 3, 1),
            overrideSubPhase = OverrideSubPhase.FLASHING
        )
        repeat(18) {
            c3Flash = TitaniumCitadelEngine.step(c3Flash, dtSec = 0.1f, random = rng).nextState
        }
        assertEquals(OverrideSubPhase.INPUT, c3Flash.overrideSubPhase)

        var c3Input = c3Flash
        c3Input = TitaniumCitadelEngine.tapConduit(c3Input, 0, rng).nextState
        c3Input = TitaniumCitadelEngine.tapConduit(c3Input, 3, rng).nextState
        val tierClearRes = TitaniumCitadelEngine.tapConduit(c3Input, 1, rng)
        assertTrue(tierClearRes.tierCompleted)
        assertEquals(2, tierClearRes.nextState.tier)
        assertEquals(CitadelChamber.CHAMBER_1_LOCK_PICK, tierClearRes.nextState.chamber)

        // 5. Power-Up 2: Aegis Citadel Revive automatically saves on lethal hit with 3s Golden Shield!
        val nearDeath = TitaniumCitadelState(
            chamber = CitadelChamber.CHAMBER_1_LOCK_PICK,
            tier = 2,
            score = 880L,
            lives = 1,
            aegisRevivePowerUpRemaining = 1,
            needleAngleDeg = 0f,
            targetArcCenterDeg = 180f // guaranteed miss
        )
        val lethalMiss = TitaniumCitadelEngine.tapLockPick(nearDeath, random = rng)
        assertTrue(lethalMiss.aegisReviveTriggered)
        assertFalse(lethalMiss.nextState.isGameOver)
        assertEquals(1, lethalMiss.nextState.lives)
        assertEquals(2, lethalMiss.nextState.tier)
        assertEquals(880L, lethalMiss.nextState.score)
        assertTrue(lethalMiss.nextState.isAegisShieldActive)
        assertEquals(0, lethalMiss.nextState.aegisRevivePowerUpRemaining)
        assertEquals(TitaniumCitadelEngine.AEGIS_SHIELD_DURATION_MS, lethalMiss.nextState.aegisShieldRemainingMs)

        // 6. Dynamic 15-second chamber rotation + Exclusive "GRANDMASTER ARCHITECT" Rank Badge on Tier 3 or 1,000+ Score
        var timed = TitaniumCitadelEngine.newGame(random = rng)
        repeat(30) {
            timed = TitaniumCitadelEngine.step(timed, dtSec = 0.5f, random = rng).nextState
        }
        assertEquals(CitadelChamber.CHAMBER_2_FIREWALL_DODGE, timed.chamber)

        val gmSnap = ArenaProgressStore.updateBestCitadelStatsIfHigher(context, score = 1_250L, tier = 3)
        assertTrue(gmSnap.isGrandmasterArchitectUnlocked)
        assertEquals(1_250L, gmSnap.bestCitadelScore)
        assertEquals(3, gmSnap.bestCitadelTier)
        assertTrue(arenaPrefs.getBoolean("grandmaster_architect_unlocked", false))
        assertTrue(appPrefs.getBoolean("grandmaster_architect_unlocked", false))
        assertEquals("GRANDMASTER ARCHITECT", appPrefs.getString("arena_profile_rank", null))

        val lowerIgnore = ArenaProgressStore.updateBestCitadelStatsIfHigher(context, score = 400L, tier = 1)
        assertEquals(1_250L, lowerIgnore.bestCitadelScore)
        assertEquals(3, lowerIgnore.bestCitadelTier)
        assertTrue(lowerIgnore.isGrandmasterArchitectUnlocked)
    }

    @Test
    fun `one-time demo data wipe permanently disables reload demo flag across restarts`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs1 = AppPreferences(context)
        assertFalse(prefs1.isDemoDataPermanentlyDisabled())

        prefs1.permanentlyDisableDemoData()
        assertTrue(prefs1.isDemoDataPermanentlyDisabled())
        assertTrue(prefs1.isDemoDataPermanentlyDisabled.value)
        assertTrue(prefs1.isDemoWiped())

        // Simulate app restart with fresh AppPreferences instance
        val prefsAfterRestart = AppPreferences(context)
        assertTrue(prefsAfterRestart.isDemoDataPermanentlyDisabled())
        assertTrue(prefsAfterRestart.isDemoWiped())
    }

    @Test
    fun `profile hub persistence and firebase sync helper operate safely without google-services`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)

        // Crash-proof sync when CredentialManager / FirebaseAuth is unavailable in headless test
        assertFalse(ProfileSyncManager.syncCurrentFirebaseUser(context, prefs))
        val signInOutcome = ProfileSyncManager.signInWithGoogle(context, prefs)
        assertTrue(signInOutcome is GoogleSignInOutcome.Error)

        // Local editable profile persistence across restart
        prefs.updateEditableProfile(
            displayName = "Aarav Kushwanta",
            bio = "Target 30% annual wealth growth",
            profession = "Quant Architect"
        )
        val reloaded = AppPreferences(context)
        assertEquals("Aarav Kushwanta", reloaded.profileDisplayName.value)
        assertEquals("Target 30% annual wealth growth", reloaded.profileBio.value)
        assertEquals("Quant Architect", reloaded.profileProfession.value)
        assertFalse(reloaded.profileGoogleConnected.value)

        // Simulate Gmail sync cache & clean sign-out
        reloaded.saveGoogleSyncedProfile(
            displayName = "Aarav K.",
            email = "aarav@gmail.com",
            photoUrl = "https://example.com/avatar.png"
        )
        assertTrue(reloaded.profileGoogleConnected.value)
        assertEquals("aarav@gmail.com", reloaded.profileEmail.value)
        assertEquals("https://example.com/avatar.png", reloaded.profilePhotoUri.value)

        ProfileSyncManager.signOutGoogle(context, reloaded)
        assertFalse(reloaded.profileGoogleConnected.value)
        assertEquals("", reloaded.profileEmail.value)
    }

    @Test
    fun `dual theme light and dark palette tokens satisfy contrast specification`() {
        assertEquals(0xFFF4F7FA.toInt(), LightCanvasBg.toArgb())
        assertEquals(0xFF00796B.toInt(), LightEmeraldTeal.toArgb())
        assertEquals(0xFFB45309.toInt(), LightAmberBronze.toArgb())
        assertEquals(0xFFFFFFFF.toInt(), LightSurfaceWhite.toArgb())
        assertEquals(0xFFE2E8F0.toInt(), LightCardBorder.toArgb())
        assertEquals(0xFF0F172A.toInt(), LightTextPrimary.toArgb())
        assertEquals(0xFF0B0C10.toInt(), DarkCanvasBg.toArgb())
        assertEquals(0xFF00FFCC.toInt(), NeonMint.toArgb())
        assertEquals(0xFFFFD700.toInt(), RoyalGold.toArgb())
    }
}






