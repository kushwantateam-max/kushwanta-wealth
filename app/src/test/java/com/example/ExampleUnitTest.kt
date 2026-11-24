package com.example

import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.CategoryEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.EmojiMatcher
import com.example.data.model.TransactionType
import com.example.domain.calculator.FinanceCalculators
import com.example.domain.calculator.OfflineTipsData
import com.example.domain.util.SoundEffectManager
import com.example.domain.util.ToneSpec
import com.example.domain.voice.VoiceTransactionParser
import com.example.ui.components.SafeVoiceTextParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testEmiCalculationAndFlatComparison() {
        val result = FinanceCalculators.calculateEmi(
            principal = 10000.0,
            annualRatePercent = 12.0,
            tenureYears = 1
        )
        assertTrue(result.monthlyEmi in 880.0..900.0)
        assertTrue(result.totalInterest > 0.0)
        assertEquals(12, result.schedule.size)
        assertTrue(result.flatTotalInterest > result.totalInterest)
    }

    @Test
    fun testHealthScoreCalculation() {
        val healthResult = FinanceCalculators.calculateHealthScore(
            totalIncome = 5000.0,
            totalExpense = 1500.0,
            totalBudget = 2500.0
        )
        assertTrue(healthResult.score >= 80)
        assertEquals("Excellent", healthResult.rating)
    }

    @Test
    fun testInterestCalculation() {
        val res = FinanceCalculators.calculateInterest(
            principal = 10000.0,
            annualRatePercent = 10.0,
            years = 2.0
        )
        assertEquals(2000.0, res.simpleInterest, 0.01)
        assertTrue(res.compoundInterest > res.simpleInterest)
    }

    @Test
    fun testBillSplit() {
        val split = FinanceCalculators.calculateBillSplit(
            totalAmount = 100.0,
            numberOfPeople = 4,
            tipPercent = 10.0
        )
        assertEquals(110.0, split.totalWithTip, 0.01)
        assertEquals(27.5, split.perPersonShare, 0.01)
    }

    @Test
    fun testInflationCalculation() {
        val inf = FinanceCalculators.calculateInflationImpact(
            presentValue = 10000.0,
            annualInflationPercent = 5.0,
            years = 10
        )
        assertTrue(inf.futureValueRequired > 10000.0)
        assertTrue(inf.purchasingPowerRemaining < 10000.0)
    }

    @Test
    fun testAiAssistantQueryAndLiveBalance() {
        val response = FinanceCalculators.answerFinancialQuery("What is 50 30 20 rule?")
        assertTrue(response.contains("50/30/20"))
        val liveResp = FinanceCalculators.answerFinancialQuery(
            query = "What is my net worth balance?",
            formattedNetWorth = "$12,000.00",
            formattedIncome = "$5,000.00",
            formattedExpense = "$1,200.00",
            savingsRatePercent = 76
        )
        assertTrue(liveResp.contains("$12,000.00"))
    }

    @Test
    fun testEmojiMatcherAndCategorySuggestion() {
        assertEquals("☕", EmojiMatcher.suggestEmoji("Morning Coffee"))
        assertEquals("⛽", EmojiMatcher.suggestEmoji("Petrol Fuel"))
        assertEquals("🛒", EmojiMatcher.suggestEmoji("Weekly Groceries"))
        assertEquals("Food & Drinks", FinanceCalculators.suggestCategoryNameFromNote("Starbucks coffee", true))
        assertEquals("Transport & Fuel", FinanceCalculators.suggestCategoryNameFromNote("Uber cab ride", true))
    }

    @Test
    fun testWorldwideCurrenciesAndTipsCount() {
        assertTrue(CurrencyCode.entries.size >= 36)
        assertTrue(OfflineTipsData.ALL_TIPS.size >= 100)
        assertEquals(10.0, FinanceCalculators.calculateDailySavingsRequired(1000.0, 700.0, 30), 0.01)
    }

    @Test
    fun testSoundEffectPcmSynthesisAndSafeHeadlessCalls() {
        val pcm = SoundEffectManager.generateToneSequencePcm(
            listOf(
                ToneSpec(frequencyHz = 698.46, durationMs = 80, amplitude = 0.35, startDelayMs = 0),
                ToneSpec(frequencyHz = 1046.50, durationMs = 100, amplitude = 0.40, startDelayMs = 60)
            )
        )
        assertTrue(pcm.isNotEmpty())
        assertTrue(pcm.any { it != 0.toShort() })
        // Safe invocation on headless JVM test runner
        SoundEffectManager.playAlertChime()
        SoundEffectManager.playSuccessChime()
        SoundEffectManager.playErrorTone()
    }

    @Test
    fun testUnlimitedLevelCyclesAndEscalatingAchievements() {
        // Level 1 (Days 1..14)
        val day1 = FinanceCalculators.evaluateStreakChallenge(streakDays = 1, targetDays = 14)
        assertEquals(1, day1.level)
        assertEquals("Starter Spark", day1.rankTitle)
        assertEquals(1, day1.completedDays)
        assertEquals(7, day1.progressPercent)
        assertTrue(!day1.isCompleted)

        val day7 = FinanceCalculators.evaluateStreakChallenge(streakDays = 7, targetDays = 14)
        assertEquals(1, day7.level)
        assertEquals(50, day7.progressPercent)
        assertTrue(!day7.isCompleted)

        val day14 = FinanceCalculators.evaluateStreakChallenge(streakDays = 14, targetDays = 14)
        assertEquals(1, day14.level)
        assertEquals(14, day14.completedDays)
        assertEquals(100, day14.progressPercent)
        assertEquals(1, day14.completedCycles)
        assertTrue(day14.isCompleted)

        // Level 2 starts seamlessly on Day 15 (Days 15..28) -> never stops after Day 14!
        val day15 = FinanceCalculators.evaluateStreakChallenge(streakDays = 15, targetDays = 14)
        assertEquals(2, day15.level)
        assertEquals("Silver Saver", day15.rankTitle)
        assertEquals(1, day15.completedDays)
        assertEquals(15, day15.cycleStartDay)
        assertEquals(28, day15.cycleEndDay)
        assertEquals(1, day15.completedCycles)
        assertEquals(7, day15.progressPercent)
        assertTrue(!day15.isCompleted)

        val day28 = FinanceCalculators.evaluateStreakChallenge(streakDays = 28, targetDays = 14)
        assertEquals(2, day28.level)
        assertEquals(14, day28.completedDays)
        assertEquals(100, day28.progressPercent)
        assertEquals(2, day28.completedCycles)
        assertTrue(day28.isCompleted)

        // Level 3 & Level 6+ Grandmaster
        val day29 = FinanceCalculators.evaluateStreakChallenge(streakDays = 29, targetDays = 14)
        assertEquals(3, day29.level)
        assertEquals("Gold Guardian", day29.rankTitle)
        assertEquals(29, day29.cycleStartDay)
        assertEquals(42, day29.cycleEndDay)

        val day71 = FinanceCalculators.evaluateStreakChallenge(streakDays = 71, targetDays = 14)
        assertEquals(6, day71.level)
        assertEquals("Grandmaster Lv.6", day71.rankTitle)
        assertEquals(1, day71.completedDays)

        // Day 1 realistic demo state: 0 trophies earned, all 6 tracks at Tier 1
        val day1Eval = FinanceCalculators.evaluateAchievementsFull(
            streakDays = 1,
            txCount = 7,
            budgetCount = 4,
            exceededBudgetCount = 0,
            bestGoalRatio = 0.74,
            completedGoalsCount = 0,
            totalGoalsCount = 3,
            settledDebtsCount = 0,
            totalDebtsCount = 2
        )
        assertEquals(0, day1Eval.totalTrophiesEarned)
        assertEquals(6, day1Eval.activeBadges.size)
        assertEquals("1st", day1Eval.activeBadges[0].rankLabel)
        assertTrue(day1Eval.activeBadges.none { it.isUnlocked })
        assertEquals(7, day1Eval.activeBadges[0].progressPercent)
        assertEquals(23, day1Eval.activeBadges[1].progressPercent)
        assertEquals(74, day1Eval.activeBadges[3].progressPercent)
        assertEquals(0, day1Eval.activeBadges[4].progressPercent)

        // Clearing Tier 1 unlocks permanent trophies AND automatically advances tracks to Tier 2+
        val day15Eval = FinanceCalculators.evaluateAchievementsFull(
            streakDays = 15,
            txCount = 35,
            budgetCount = 4,
            exceededBudgetCount = 0,
            bestGoalRatio = 1.0,
            completedGoalsCount = 1,
            totalGoalsCount = 3,
            settledDebtsCount = 2,
            totalDebtsCount = 2,
            bestIncompleteGoalRatio = 0.45
        )
        assertTrue(day15Eval.totalTrophiesEarned >= 6) // 14d Streak, 30 Tx, Budget 7d & 14d, Vault #1, Zero-Debt (1+)
        assertEquals(2, day15Eval.activeBadges[0].currentTier) // Next locked streak tier: 28d (Lv.2)
        assertEquals(7, day15Eval.activeBadges[0].progressPercent) // 1/14 in Lv.2
        assertEquals(2, day15Eval.activeBadges[1].currentTier) // Next locked tx tier: 60 Tx
        assertEquals(2, day15Eval.activeBadges[3].currentTier) // Next locked vault: #2
        assertEquals(45, day15Eval.activeBadges[3].progressPercent) // Incomplete goal #2 at 45%
        assertEquals(2, day15Eval.activeBadges[4].currentTier) // Next locked debt tier: 3+ settled
        assertEquals(1, day15Eval.activeBadges[5].currentTier) // 30-day legend at 15/30 = 50%
        assertEquals(50, day15Eval.activeBadges[5].progressPercent)
    }

    @Test
    fun testVoiceTransactionParserMultilingualAndAutoSection() {
        // 1. English UPI + Custom Gym section
        val gymParsed = VoiceTransactionParser.parse("Spent 250 on Gym via GPay UPI")
        assertEquals(250.0, gymParsed.amountDisplay, 0.01)
        assertEquals(TransactionType.EXPENSE, gymParsed.transactionType)
        assertEquals("GPay UPI", gymParsed.paymentModeLabel)
        assertTrue(gymParsed.hasExplicitWalletMode)
        assertEquals("fitness_center", gymParsed.suggestedIconName)

        // 2. Hindi Devanagari numerals + UPI
        val hindiParsed = VoiceTransactionParser.parse("५०० रुपये चाय यूपीआई से")
        assertEquals(500.0, hindiParsed.amountDisplay, 0.01)
        assertEquals(TransactionType.EXPENSE, hindiParsed.transactionType)
        assertEquals("UPI Wallet", hindiParsed.paymentModeLabel)
        assertEquals("Food & Drinks", hindiParsed.categoryHint)

        // 3. Hinglish PhonePe + Medical auto-section
        val medParsed = VoiceTransactionParser.parse("300 PhonePe se medical")
        assertEquals(300.0, medParsed.amountDisplay, 0.01)
        assertEquals("PhonePe UPI", medParsed.paymentModeLabel)
        assertEquals("medical_services", medParsed.suggestedIconName)

        // 4. Income into existing Bank
        val salaryParsed = VoiceTransactionParser.parse("Received 15000 salary in Axis bank")
        assertEquals(15000.0, salaryParsed.amountDisplay, 0.01)
        assertEquals(TransactionType.INCOME, salaryParsed.transactionType)
        assertEquals("Axis Bank", salaryParsed.paymentModeLabel)
        assertEquals("Salary", salaryParsed.categoryHint)

        // 5. Room matching vs missing (auto-create detection)
        val existingAccounts = listOf(
            AccountEntity(id = 1L, name = "HDFC Bank", type = AccountType.BANK, balance = 1000.0, isDefault = true),
            AccountEntity(id = 2L, name = "Axis Bank Platinum", type = AccountType.BANK, balance = 500.0),
            AccountEntity(id = 3L, name = "Cash & Physical Wallet", type = AccountType.CASH, balance = 200.0)
        )
        val existingCategories = listOf(
            CategoryEntity(id = 1L, name = "Food & Drinks", iconName = "restaurant", colorHex = "#06B6D4", isExpense = true),
            CategoryEntity(id = 5L, name = "Salary", iconName = "payments", colorHex = "#10B981", isExpense = false)
        )

        // GPay UPI is missing -> should return null so ViewModel auto-creates it!
        assertNull(VoiceTransactionParser.resolveMatchingAccount(gymParsed, existingAccounts))
        // Gym is missing -> should return null so ViewModel auto-creates Neon section!
        assertNull(VoiceTransactionParser.resolveMatchingCategory(gymParsed, existingCategories))

        // Hindi tea matches existing "Food & Drinks"
        assertNotNull(VoiceTransactionParser.resolveMatchingCategory(hindiParsed, existingCategories))
        // Salary matches "Axis Bank Platinum" & "Salary"
        assertEquals(2L, VoiceTransactionParser.resolveMatchingAccount(salaryParsed, existingAccounts)?.id)
        assertEquals(5L, VoiceTransactionParser.resolveMatchingCategory(salaryParsed, existingCategories)?.id)
    }

    @Test
    fun testSafeVoiceModuleParserAndChips() {
        val petrol = SafeVoiceTextParser.parse("500 UPI Petrol")
        assertEquals(500.0, petrol.amountDisplay, 0.01)
        assertEquals("UPI Wallet", petrol.paymentModeLabel)
        assertEquals("Transport & Fuel", petrol.categoryHint)

        val food = SafeVoiceTextParser.parse("1200 Food")
        assertEquals(1200.0, food.amountDisplay, 0.01)
        assertEquals("Food & Drinks", food.categoryHint)

        val gym = SafeVoiceTextParser.parse("3000 Gym")
        assertEquals(3000.0, gym.amountDisplay, 0.01)
        assertEquals("fitness_center", gym.suggestedIconName)
        assertEquals("#00FFCC", gym.suggestedColorHex)
    }
}

