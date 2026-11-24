package com.example.domain.calculator

import kotlin.math.pow
import kotlin.math.roundToInt

data class AmortizationMonth(
    val monthNumber: Int,
    val emiAmount: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)

data class EmiResult(
    val monthlyEmi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val schedule: List<AmortizationMonth>,
    val flatMonthlyEmi: Double = 0.0,
    val flatTotalInterest: Double = 0.0
)

data class HealthScoreResult(
    val score: Int,
    val rating: String,
    val tip: String
)

data class InterestResult(
    val simpleInterest: Double,
    val compoundInterest: Double,
    val maturitySimple: Double,
    val maturityCompound: Double
)

data class BillSplitResult(
    val perPersonShare: Double,
    val tipAmount: Double,
    val totalWithTip: Double
)

data class InflationResult(
    val futureValueRequired: Double,
    val purchasingPowerRemaining: Double,
    val lossOfPurchasingPower: Double
)

data class AchievementBadge(
    val id: String,
    val rankLabel: String,
    val currentTier: Int,
    val tiersCleared: Int,
    val title: String,
    val description: String,
    val progressDetail: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val progressPercent: Int
)

data class UnlockedTrophyRecord(
    val id: String,
    val label: String,
    val trackRank: String
)

data class AchievementsEvaluation(
    val totalTrophiesEarned: Int,
    val unlockedTrophies: List<UnlockedTrophyRecord>,
    val activeBadges: List<AchievementBadge>
)

data class StreakChallengeSummary(
    val totalStreakDays: Int,
    val level: Int,
    val rankTitle: String,
    val completedDays: Int,
    val targetDays: Int,
    val cycleStartDay: Int,
    val cycleEndDay: Int,
    val completedCycles: Int,
    val progressRatio: Float,
    val progressPercent: Int,
    val isCompleted: Boolean
)

object FinanceCalculators {

    fun calculateEmi(principal: Double, annualRatePercent: Double, tenureYears: Int): EmiResult {
        if (principal <= 0 || tenureYears <= 0) {
            return EmiResult(0.0, 0.0, 0.0, emptyList())
        }
        val months = tenureYears * 12
        val monthlyRate = (annualRatePercent / 12.0) / 100.0

        val emi = if (monthlyRate > 0) {
            val factor = (1.0 + monthlyRate).pow(months.toDouble())
            (principal * monthlyRate * factor) / (factor - 1.0)
        } else {
            principal / months
        }

        var balance = principal
        val schedule = mutableListOf<AmortizationMonth>()

        for (m in 1..months.coerceAtMost(60)) {
            val interestForMonth = balance * monthlyRate
            val principalForMonth = (emi - interestForMonth).coerceAtMost(balance)
            balance = (balance - principalForMonth).coerceAtLeast(0.0)

            schedule.add(
                AmortizationMonth(
                    monthNumber = m,
                    emiAmount = emi,
                    principalPaid = principalForMonth,
                    interestPaid = interestForMonth,
                    remainingBalance = balance
                )
            )
        }

        val totalPayment = emi * months
        val totalInterest = totalPayment - principal

        // Flat Rate comparison (Feature 56)
        val flatTotalInterest = principal * (annualRatePercent / 100.0) * tenureYears
        val flatMonthlyEmi = (principal + flatTotalInterest) / months

        return EmiResult(
            monthlyEmi = (emi * 100.0).roundToInt() / 100.0,
            totalInterest = (totalInterest * 100.0).roundToInt() / 100.0,
            totalPayment = (totalPayment * 100.0).roundToInt() / 100.0,
            schedule = schedule,
            flatMonthlyEmi = (flatMonthlyEmi * 100.0).roundToInt() / 100.0,
            flatTotalInterest = (flatTotalInterest * 100.0).roundToInt() / 100.0
        )
    }

    fun calculateInterest(principal: Double, annualRatePercent: Double, years: Double, compoundTimesPerYear: Int = 12): InterestResult {
        if (principal <= 0 || years <= 0) {
            return InterestResult(0.0, 0.0, 0.0, 0.0)
        }
        val r = annualRatePercent / 100.0
        val si = principal * r * years
        val maturitySimple = principal + si

        val n = compoundTimesPerYear.coerceAtLeast(1).toDouble()
        val maturityCompound = principal * (1.0 + (r / n)).pow(n * years)
        val ci = maturityCompound - principal

        return InterestResult(
            simpleInterest = (si * 100.0).roundToInt() / 100.0,
            compoundInterest = (ci * 100.0).roundToInt() / 100.0,
            maturitySimple = (maturitySimple * 100.0).roundToInt() / 100.0,
            maturityCompound = (maturityCompound * 100.0).roundToInt() / 100.0
        )
    }

    fun calculateBillSplit(totalAmount: Double, numberOfPeople: Int, tipPercent: Double = 0.0): BillSplitResult {
        val people = numberOfPeople.coerceAtLeast(1)
        val tip = totalAmount * (tipPercent / 100.0)
        val grandTotal = totalAmount + tip
        val perPerson = grandTotal / people
        return BillSplitResult(
            perPersonShare = (perPerson * 100.0).roundToInt() / 100.0,
            tipAmount = (tip * 100.0).roundToInt() / 100.0,
            totalWithTip = (grandTotal * 100.0).roundToInt() / 100.0
        )
    }

    fun calculateInflationImpact(presentValue: Double, annualInflationPercent: Double, years: Int): InflationResult {
        if (presentValue <= 0 || years <= 0) {
            return InflationResult(0.0, 0.0, 0.0)
        }
        val i = annualInflationPercent / 100.0
        val factor = (1.0 + i).pow(years.toDouble())
        val futureNeeded = presentValue * factor
        val purchasingPowerRemaining = presentValue / factor
        val loss = presentValue - purchasingPowerRemaining

        return InflationResult(
            futureValueRequired = (futureNeeded * 100.0).roundToInt() / 100.0,
            purchasingPowerRemaining = (purchasingPowerRemaining * 100.0).roundToInt() / 100.0,
            lossOfPurchasingPower = (loss * 100.0).roundToInt() / 100.0
        )
    }

    fun calculateDailySavingsRequired(targetAmount: Double, currentAmount: Double, daysRemaining: Int): Double {
        val remaining = (targetAmount - currentAmount).coerceAtLeast(0.0)
        val days = daysRemaining.coerceAtLeast(1)
        return ((remaining / days) * 100.0).roundToInt() / 100.0
    }

    fun calculateHealthScore(totalIncome: Double, totalExpense: Double, totalBudget: Double): HealthScoreResult {
        if (totalIncome <= 0 && totalExpense <= 0) {
            return HealthScoreResult(75, "Good", "Add your income and expense logs to unlock personalized financial health telemetry.")
        }
        val savingsRate = if (totalIncome > 0) ((totalIncome - totalExpense) / totalIncome) * 100.0 else 0.0
        val budgetUtilization = if (totalBudget > 0) (totalExpense / totalBudget) * 100.0 else 60.0

        var score = 50
        score += when {
            savingsRate >= 30.0 -> 30
            savingsRate >= 20.0 -> 20
            savingsRate >= 10.0 -> 10
            savingsRate >= 0.0 -> 5
            else -> -20
        }

        score += when {
            budgetUtilization < 70.0 -> 20
            budgetUtilization <= 90.0 -> 10
            budgetUtilization <= 100.0 -> 0
            else -> -20
        }

        score = score.coerceIn(15, 98)

        val (rating, tip) = when {
            score >= 80 -> Pair("Excellent", "Exceptional surplus! You're saving over 25% of your income while staying comfortably under budget limits.")
            score >= 60 -> Pair("Good", "Healthy financial balance. Consider investing recurring monthly surplus into an emergency fund.")
            else -> Pair("Needs Attention", "Spending is consuming the majority of your cash flow. Review your non-essential categories.")
        }

        return HealthScoreResult(score, rating, tip)
    }

    fun suggestCategoryNameFromNote(note: String, isExpense: Boolean): String {
        val n = note.lowercase()
        if (!isExpense) {
            return when {
                n.contains("freelance") || n.contains("client") || n.contains("project") -> "Freelancing Work"
                else -> "Salary"
            }
        }
        return when {
            n.contains("coffee") || n.contains("tea") || n.contains("chai") || n.contains("starbucks") || n.contains("lunch") || n.contains("dinner") || n.contains("restaurant") || n.contains("pizza") -> "Food & Drinks"
            n.contains("uber") || n.contains("taxi") || n.contains("metro") || n.contains("fuel") || n.contains("petrol") || n.contains("diesel") -> "Transport & Fuel"
            n.contains("mart") || n.contains("grocery") || n.contains("milk") || n.contains("veg") -> "Groceries"
            n.contains("rent") || n.contains("electric") || n.contains("wifi") || n.contains("bill") || n.contains("water") -> "Rent & Bills"
            n.contains("movie") || n.contains("netflix") || n.contains("youtube") || n.contains("spotify") -> "Entertainment"
            n.contains("sip") || n.contains("mutual") || n.contains("stock") || n.contains("invest") -> "Mutual Funds & Stocks"
            n.contains("amazon") || n.contains("flipkart") || n.contains("shop") || n.contains("clothes") -> "Shopping"
            else -> "Food & Drinks"
        }
    }

    fun answerFinancialQuery(
        query: String,
        formattedNetWorth: String = "",
        formattedIncome: String = "",
        formattedExpense: String = "",
        savingsRatePercent: Int = 0
    ): String {
        val q = query.lowercase().trim()
        return when {
            (q.contains("net worth") || q.contains("balance")) && formattedNetWorth.isNotEmpty() ->
                "Your live total Net Worth across accounts in this book is $formattedNetWorth (Income: $formattedIncome | Expense: $formattedExpense)."
            (q.contains("spend") || q.contains("expense")) && formattedExpense.isNotEmpty() ->
                "Your total recorded spending in this ledger is $formattedExpense against $formattedIncome in income (Savings rate: $savingsRatePercent%)."
            q.contains("50") || q.contains("30") || q.contains("rule") ->
                "The 50/30/20 Rule: Allocate 50% of after-tax income to Needs (rent, utilities, groceries), 30% to Wants (dining, hobbies), and 20% to Savings & Debt reduction."
            q.contains("emergency") ->
                "Emergency Fund: Recommended size is 3 to 6 months of mandatory living expenses kept in a high-yield savings account or liquid fund."
            q.contains("save") || q.contains("savings") ->
                "To accelerate savings: 1) Automate transfers on payday. 2) Set monthly category budget alerts at 80%. 3) Cut unused recurring digital subscriptions."
            q.contains("emi") || q.contains("loan") ->
                "Loan EMI Tip: Prepaying even 5% extra on principal each year can reduce your overall interest burden by up to 25% on a 20-year loan. Always compare Reducing vs Flat rates!"
            q.contains("inflation") ->
                "Beat Inflation: Cash loses value over time. At 6% inflation, money loses half its real purchasing power in ~12 years. Invest in equities or inflation-indexed assets."
            q.contains("72") || q.contains("double") ->
                "Rule of 72: Divide 72 by your annual interest rate to find out how many years it will take to double your money. E.g., at 8%, 72 ÷ 8 = 9 years."
            q.contains("credit") || q.contains("card") ->
                "Credit Card Rule: Always pay the Total Amount Due by the due date. Never carry revolving credit card debt as interest rates often exceed 36-42% APR."
            else ->
                "Smart Financial Insight: Your current savings rate is $savingsRatePercent%. Ask me about 'balance', 'expenses', '50/30/20 rule', 'emergency fund', 'rule of 72', or 'inflation'!"
        }
    }

    val OFFLINE_FINANCIAL_TIPS: List<String> get() = OfflineTipsData.ALL_TIPS

    fun getRandomTip(): String = OFFLINE_FINANCIAL_TIPS.random()

    fun rankTitleForLevel(level: Int): String = when (level) {
        1 -> "Starter Spark"
        2 -> "Silver Saver"
        3 -> "Gold Guardian"
        4 -> "Platinum Planner"
        5 -> "Diamond Strategist"
        else -> "Grandmaster Lv.$level"
    }

    fun evaluateStreakChallenge(streakDays: Int, targetDays: Int = 14): StreakChallengeSummary {
        val safeTarget = targetDays.coerceAtLeast(1)
        val safeStreak = streakDays.coerceAtLeast(0)
        val level = if (safeStreak == 0) 1 else ((safeStreak - 1) / safeTarget) + 1
        val cycleDay = if (safeStreak == 0) 0 else ((safeStreak - 1) % safeTarget) + 1
        val completedCycles = safeStreak / safeTarget
        val isCycleCompleted = safeStreak > 0 && safeStreak % safeTarget == 0
        val cycleStartDay = (level - 1) * safeTarget + 1
        val cycleEndDay = level * safeTarget
        val ratio = (cycleDay.toFloat() / safeTarget.toFloat()).coerceIn(0f, 1f)
        val percent = ((cycleDay * 100.0) / safeTarget.toDouble()).roundToInt().coerceIn(0, 100)
        return StreakChallengeSummary(
            totalStreakDays = safeStreak,
            level = level,
            rankTitle = rankTitleForLevel(level),
            completedDays = cycleDay,
            targetDays = safeTarget,
            cycleStartDay = cycleStartDay,
            cycleEndDay = cycleEndDay,
            completedCycles = completedCycles,
            progressRatio = ratio,
            progressPercent = percent,
            isCompleted = isCycleCompleted
        )
    }

    private fun resolveLadder(value: Int, baseRungs: List<Int>, stepAfter: Int): Pair<List<Int>, Int> {
        val cleared = mutableListOf<Int>()
        var tier = 1
        while (true) {
            val rung = if (tier <= baseRungs.size) {
                baseRungs[tier - 1]
            } else {
                baseRungs.last() + ((tier - baseRungs.size) * stepAfter)
            }
            if (value >= rung) {
                cleared.add(rung)
                tier++
            } else {
                return cleared to rung
            }
        }
    }

    fun evaluateAchievementsFull(
        streakDays: Int,
        txCount: Int,
        budgetCount: Int,
        exceededBudgetCount: Int,
        bestGoalRatio: Double,
        completedGoalsCount: Int,
        totalGoalsCount: Int,
        settledDebtsCount: Int,
        totalDebtsCount: Int,
        bestIncompleteGoalRatio: Double = bestGoalRatio
    ): AchievementsEvaluation {
        val unlockedTrophies = mutableListOf<UnlockedTrophyRecord>()
        val safeStreak = streakDays.coerceAtLeast(0)

        // 1st: Perpetual 14-Day Streak Cycles (14 -> 28 -> 42 -> 56 -> ...)
        val streakClearedCycles = safeStreak / 14
        for (c in 1..streakClearedCycles) {
            unlockedTrophies.add(
                UnlockedTrophyRecord(
                    id = "trophy_streak_$c",
                    label = "Lv.$c Streak (${c * 14}d)",
                    trackRank = "1st"
                )
            )
        }
        val nextStreakTarget = (streakClearedCycles + 1) * 14
        val inCycleDays = safeStreak - (streakClearedCycles * 14)
        val streakNextPercent = ((inCycleDays * 100.0) / 14.0).roundToInt().coerceIn(0, 99)

        // 2nd: Active Ledger Escalation (30 -> 60 -> 100 -> 150 -> 250 -> 500 -> 1000 -> +500...)
        val (txClearedRungs, txNextTarget) = resolveLadder(
            value = txCount.coerceAtLeast(0),
            baseRungs = listOf(30, 60, 100, 150, 250, 500, 1000),
            stepAfter = 500
        )
        txClearedRungs.forEachIndexed { idx, rung ->
            unlockedTrophies.add(
                UnlockedTrophyRecord(
                    id = "trophy_tx_${idx + 1}",
                    label = "Ledger $rung Tx",
                    trackRank = "2nd"
                )
            )
        }
        val txPercent = ((txCount.coerceAtLeast(0) * 100.0) / txNextTarget).roundToInt().coerceIn(0, 99)

        // 3rd: Budget Guardian Escalation (7 -> 14 -> 28 -> 42 -> 56 -> +14... with 3+ budgets & 0 exceeded)
        val isBudgetHealthy = budgetCount >= 3 && exceededBudgetCount == 0
        val (budgetClearedRungs, budgetNextDays) = if (isBudgetHealthy) {
            resolveLadder(safeStreak, listOf(7, 14, 28, 42, 56), stepAfter = 14)
        } else {
            emptyList<Int>() to 7
        }
        budgetClearedRungs.forEachIndexed { idx, rung ->
            unlockedTrophies.add(
                UnlockedTrophyRecord(
                    id = "trophy_budget_${idx + 1}",
                    label = "Budget Guard ${rung}d",
                    trackRank = "3rd"
                )
            )
        }
        val budgetSetupScore = (budgetCount.coerceIn(0, 3) / 3.0) * 40.0
        val budgetHealthScore = if (budgetCount > 0) {
            val withinLimit = (budgetCount - exceededBudgetCount).coerceAtLeast(0)
            (withinLimit.toDouble() / budgetCount.toDouble()) * 30.0
        } else 0.0
        val budgetStreakScore = (safeStreak.coerceIn(0, budgetNextDays).toDouble() / budgetNextDays.toDouble()) * 30.0
        val budgetPercent = (budgetSetupScore + budgetHealthScore + budgetStreakScore).roundToInt().coerceIn(0, 99)

        // 4th: Savings Vault Mastery (Vault #1 -> Vault #2 -> Vault #3 -> ... ∞)
        val safeCompletedGoals = completedGoalsCount.coerceAtLeast(0)
        for (g in 1..safeCompletedGoals) {
            unlockedTrophies.add(
                UnlockedTrophyRecord(
                    id = "trophy_vault_$g",
                    label = "Savings Vault #$g",
                    trackRank = "4th"
                )
            )
        }
        val nextVaultNumber = safeCompletedGoals + 1
        val hasIncompleteGoal = totalGoalsCount > safeCompletedGoals
        val vaultPercent = if (!hasIncompleteGoal) {
            0
        } else {
            (bestIncompleteGoalRatio.coerceIn(0.0, 0.99) * 100.0).roundToInt().coerceIn(0, 99)
        }

        // 5th: Zero-Debt Hero Escalation (1 -> 3 -> 5 -> 10 -> 20 -> 35 -> 50 -> +25... settled & zero pending)
        val allCurrentDebtsSettled = totalDebtsCount > 0 && settledDebtsCount == totalDebtsCount
        val (debtClearedRungs, debtNextTarget) = if (allCurrentDebtsSettled) {
            resolveLadder(settledDebtsCount, listOf(1, 3, 5, 10, 20, 35, 50), stepAfter = 25)
        } else {
            emptyList<Int>() to 1
        }
        debtClearedRungs.forEachIndexed { idx, rung ->
            unlockedTrophies.add(
                UnlockedTrophyRecord(
                    id = "trophy_debt_${idx + 1}",
                    label = "Zero-Debt ($rung+)",
                    trackRank = "5th"
                )
            )
        }
        val debtPercent = when {
            totalDebtsCount <= 0 -> 0
            !allCurrentDebtsSettled -> ((settledDebtsCount * 100.0) / totalDebtsCount.toDouble()).roundToInt().coerceIn(0, 99)
            else -> ((settledDebtsCount * 100.0) / debtNextTarget.toDouble()).roundToInt().coerceIn(0, 99)
        }

        // 6th: Consistency Grandmaster Escalation (30 -> 60 -> 90 -> 180 -> 365 -> 500 -> 730 -> 1000 -> +365...)
        val (legendClearedRungs, legendNextTarget) = resolveLadder(
            value = safeStreak,
            baseRungs = listOf(30, 60, 90, 180, 365, 500, 730, 1000),
            stepAfter = 365
        )
        legendClearedRungs.forEachIndexed { idx, rung ->
            unlockedTrophies.add(
                UnlockedTrophyRecord(
                    id = "trophy_legend_${idx + 1}",
                    label = "${rung}d Legend",
                    trackRank = "6th"
                )
            )
        }
        val legendPercent = ((safeStreak * 100.0) / legendNextTarget.toDouble()).roundToInt().coerceIn(0, 99)

        val activeBadges = listOf(
            AchievementBadge(
                id = "streak_cycle",
                rankLabel = "1st",
                currentTier = streakClearedCycles + 1,
                tiersCleared = streakClearedCycles,
                title = "${nextStreakTarget}-Day Streak Champion (Lv.${streakClearedCycles + 1})",
                description = "Complete 14-day consecutive login cycles to level up endlessly",
                progressDetail = "$safeStreak / $nextStreakTarget total days ($inCycleDays/14 in Lv.${streakClearedCycles + 1})",
                iconName = "military_tech",
                isUnlocked = streakClearedCycles > 0,
                progressPercent = streakNextPercent
            ),
            AchievementBadge(
                id = "ledger_unlimited",
                rankLabel = "2nd",
                currentTier = txClearedRungs.size + 1,
                tiersCleared = txClearedRungs.size,
                title = "Ledger Master ($txNextTarget Tx)",
                description = "Record $txNextTarget real income or expense transactions in your ledger",
                progressDetail = "$txCount / $txNextTarget transactions logged",
                iconName = "bolt",
                isUnlocked = txClearedRungs.isNotEmpty(),
                progressPercent = txPercent
            ),
            AchievementBadge(
                id = "budget_guardian_unlimited",
                rankLabel = "3rd",
                currentTier = budgetClearedRungs.size + 1,
                tiersCleared = budgetClearedRungs.size,
                title = "Budget Guardian (${budgetNextDays}d)",
                description = "Keep 3+ category budgets within limits for $budgetNextDays consecutive days",
                progressDetail = if (budgetCount == 0) "0 / 3 budgets set" else "${(budgetCount - exceededBudgetCount).coerceAtLeast(0)}/$budgetCount on track • ${safeStreak.coerceAtMost(budgetNextDays)}/$budgetNextDays days",
                iconName = "crown",
                isUnlocked = budgetClearedRungs.isNotEmpty(),
                progressPercent = budgetPercent
            ),
            AchievementBadge(
                id = "vault_finisher_unlimited",
                rankLabel = "4th",
                currentTier = nextVaultNumber,
                tiersCleared = safeCompletedGoals,
                title = "Savings Vault #$nextVaultNumber",
                description = "Fund savings goal #$nextVaultNumber to 100% of its target amount",
                progressDetail = when {
                    totalGoalsCount == 0 -> "Create Savings Goal #$nextVaultNumber to start"
                    !hasIncompleteGoal -> "$safeCompletedGoals goal(s) 100% funded • Add Goal #$nextVaultNumber"
                    else -> "Active goal #$nextVaultNumber progress: $vaultPercent% of 100%"
                },
                iconName = "savings",
                isUnlocked = safeCompletedGoals > 0,
                progressPercent = vaultPercent
            ),
            AchievementBadge(
                id = "zero_debt_unlimited",
                rankLabel = "5th",
                currentTier = debtClearedRungs.size + 1,
                tiersCleared = debtClearedRungs.size,
                title = if (allCurrentDebtsSettled) "Zero-Debt Hero ($debtNextTarget+ Settled)" else "Zero-Debt Hero",
                description = if (allCurrentDebtsSettled) "Settle $debtNextTarget+ debt records with zero pending dues" else "Track & mark all borrowed/lent debt entries as settled",
                progressDetail = when {
                    totalDebtsCount == 0 -> "0 / 1 debt records settled"
                    !allCurrentDebtsSettled -> "$settledDebtsCount / $totalDebtsCount active debts settled"
                    else -> "$settledDebtsCount / $debtNextTarget total debts settled"
                },
                iconName = "shield",
                isUnlocked = debtClearedRungs.isNotEmpty(),
                progressPercent = debtPercent
            ),
            AchievementBadge(
                id = "legend_unlimited",
                rankLabel = "6th",
                currentTier = legendClearedRungs.size + 1,
                tiersCleared = legendClearedRungs.size,
                title = "${legendNextTarget}-Day Consistency Legend",
                description = "Build a $legendNextTarget-day unbroken daily financial habit",
                progressDetail = "$safeStreak / $legendNextTarget consecutive days",
                iconName = "military_tech",
                isUnlocked = legendClearedRungs.isNotEmpty(),
                progressPercent = legendPercent
            )
        )

        return AchievementsEvaluation(
            totalTrophiesEarned = unlockedTrophies.size,
            unlockedTrophies = unlockedTrophies,
            activeBadges = activeBadges
        )
    }

    fun evaluateAchievements(
        streakDays: Int,
        txCount: Int,
        budgetCount: Int,
        exceededBudgetCount: Int,
        bestGoalRatio: Double,
        completedGoalsCount: Int,
        totalGoalsCount: Int,
        settledDebtsCount: Int,
        totalDebtsCount: Int,
        bestIncompleteGoalRatio: Double = bestGoalRatio
    ): List<AchievementBadge> {
        return evaluateAchievementsFull(
            streakDays = streakDays,
            txCount = txCount,
            budgetCount = budgetCount,
            exceededBudgetCount = exceededBudgetCount,
            bestGoalRatio = bestGoalRatio,
            completedGoalsCount = completedGoalsCount,
            totalGoalsCount = totalGoalsCount,
            settledDebtsCount = settledDebtsCount,
            totalDebtsCount = totalDebtsCount,
            bestIncompleteGoalRatio = bestIncompleteGoalRatio
        ).activeBadges
    }
}

data class WorkRateSummary(
    val monthlyIncome: Double,
    val dailyWorkHours: Double,
    val workDaysPerMonth: Double,
    val dailyRate: Double,
    val hourlyRate: Double,
    val halfDayThresholdAmount: Double
) {
    val isConfigured: Boolean
        get() = monthlyIncome > 0.0 && hourlyRate > 0.0 && dailyRate > 0.0
}

data class LifeEnergyCostResult(
    val totalWorkHours: Double,
    val days: Int,
    val hours: Int,
    val halfDayThresholdAmount: Double,
    val isHeavyImpact: Boolean,
    val durationLabel: String,
    val message: String
)

object WorkHourCostCalculator {
    const val DEFAULT_DAILY_WORK_HOURS = 8.0
    const val DEFAULT_WORK_DAYS_PER_MONTH = 22.0

    fun evaluateQuickExpression(expression: String): Double {
        val normalized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .trim()
        if (normalized.isEmpty()) return 0.0
        val tokens = normalized
            .split(Regex("(?<=[-+*/])|(?=[-+*/])"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return 0.0

        var total = tokens[0].toDoubleOrNull() ?: 0.0
        var i = 1
        while (i < tokens.size - 1) {
            val op = tokens[i]
            val nextVal = tokens[i + 1].toDoubleOrNull()
            if (nextVal != null) {
                when (op) {
                    "+" -> total += nextVal
                    "-" -> total -= nextVal
                    "*" -> total *= nextVal
                    "/" -> if (nextVal != 0.0) total /= nextVal
                }
            }
            i += 2
        }
        return total.coerceAtLeast(0.0)
    }

    fun computeRates(
        monthlyIncome: Double,
        dailyWorkHours: Double = DEFAULT_DAILY_WORK_HOURS,
        workDaysPerMonth: Double = DEFAULT_WORK_DAYS_PER_MONTH
    ): WorkRateSummary {
        val safeHours = dailyWorkHours.coerceIn(1.0, 16.0)
        val safeDays = workDaysPerMonth.coerceIn(1.0, 31.0)
        val safeIncome = monthlyIncome.coerceAtLeast(0.0)
        if (safeIncome <= 0.0) {
            return WorkRateSummary(
                monthlyIncome = 0.0,
                dailyWorkHours = safeHours,
                workDaysPerMonth = safeDays,
                dailyRate = 0.0,
                hourlyRate = 0.0,
                halfDayThresholdAmount = 0.0
            )
        }
        val dailyRate = safeIncome / safeDays
        val hourlyRate = dailyRate / safeHours
        val halfDayThreshold = dailyRate * 0.5
        return WorkRateSummary(
            monthlyIncome = safeIncome,
            dailyWorkHours = safeHours,
            workDaysPerMonth = safeDays,
            dailyRate = dailyRate,
            hourlyRate = hourlyRate,
            halfDayThresholdAmount = halfDayThreshold
        )
    }

    fun evaluatePurchase(
        expenseAmount: Double,
        monthlyIncome: Double,
        dailyWorkHours: Double = DEFAULT_DAILY_WORK_HOURS,
        workDaysPerMonth: Double = DEFAULT_WORK_DAYS_PER_MONTH
    ): LifeEnergyCostResult? {
        if (expenseAmount <= 0.0) return null
        val rates = computeRates(monthlyIncome, dailyWorkHours, workDaysPerMonth)
        if (!rates.isConfigured) return null

        // Strictly hide for small daily expenses (< 50% of 1-day earning / < half-day work)
        if (expenseAmount + 1e-6 < rates.halfDayThresholdAmount) {
            return null
        }

        val totalHours = expenseAmount / rates.hourlyRate
        val hoursPerDayInt = rates.dailyWorkHours.roundToInt().coerceAtLeast(1)
        var days = kotlin.math.floor(totalHours / rates.dailyWorkHours).toInt()
        var remHours = (totalHours - (days * rates.dailyWorkHours)).roundToInt()

        if (remHours >= hoursPerDayInt) {
            days += 1
            remHours = 0
        }

        val daysPart = if (days > 0) "$days ${if (days == 1) "Day" else "Days"}" else null
        val hoursPart = if (remHours > 0) "$remHours ${if (remHours == 1) "Hour" else "Hours"}" else null
        val durationLabel = listOfNotNull(daysPart, hoursPart).joinToString(", ")
            .ifEmpty { "1 Hour" }

        return LifeEnergyCostResult(
            totalWorkHours = totalHours,
            days = days,
            hours = remHours,
            halfDayThresholdAmount = rates.halfDayThresholdAmount,
            isHeavyImpact = days >= 2,
            durationLabel = durationLabel,
            message = "⏳ Life-Energy Cost: $durationLabel of your hard work"
        )
    }
}

