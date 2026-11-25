package com.example.domain.leaderboard

import android.content.Context
import android.content.SharedPreferences
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

enum class LeagueTier(
    val title: String,
    val minRank: Int,
    val maxRank: Int
) {
    GRANDMASTER("Grandmaster", 1, 25),
    DIAMOND("Diamond", 26, 120),
    GOLD("Gold", 121, 400),
    SILVER("Silver", 401, 900),
    BRONZE("Bronze", 901, 1500);

    companion object {
        fun fromRank(rank: Int): LeagueTier {
            return when {
                rank <= GRANDMASTER.maxRank -> GRANDMASTER
                rank <= DIAMOND.maxRank -> DIAMOND
                rank <= GOLD.maxRank -> GOLD
                rank <= SILVER.maxRank -> SILVER
                else -> BRONZE
            }
        }
    }
}

enum class CelebrationKind {
    MILESTONE_TIER,
    ROYAL_PODIUM,
    GRAND_CHAMPION
}

data class LeaderboardCelebration(
    val milestoneRankThreshold: Int,
    val currentRank: Int,
    val kind: CelebrationKind,
    val badgeLabel: String,
    val title: String,
    val subtitle: String,
    val leagueName: String
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val initials: String,
    val regionBadge: String,
    val avatarColorIndex: Int,
    val streakDays: Int,
    val score: Long,
    val leagueTier: LeagueTier,
    val isCurrentUser: Boolean = false,
    val photoUri: String = "",
    val portraitIndex: Int? = null,
    val isFemale: Boolean = false
)

data class LeaderboardUiState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val topThree: List<LeaderboardEntry> = emptyList(),
    val currentUserEntry: LeaderboardEntry = LeaderboardEntry(
        rank = 1485,
        name = "Koshvanta Explorer",
        initials = "KE",
        regionBadge = "IN",
        avatarColorIndex = 0,
        streakDays = 1,
        score = ChampionsLeaderboardMath.STARTING_USER_SCORE,
        leagueTier = LeagueTier.BRONZE,
        isCurrentUser = true
    ),
    val totalParticipants: Int = ChampionsLeaderboardMath.TOTAL_PARTICIPANTS,
    val throneSlipAlertRank: Int? = null
)

internal data class FeaturedPlayerSpec(
    val baseSlot: Int,
    val name: String,
    val isFemale: Boolean,
    val portraitIndex: Int,
    val paletteIndex: Int
)

internal data class RivalSeed(
    val baseSlot: Int,
    val name: String,
    val initials: String,
    val regionBadge: String,
    val avatarColorIndex: Int,
    val baseScore: Long,
    val dailyDriftRate: Long,
    val baseStreakDays: Int,
    val portraitIndex: Int?,
    val isFemale: Boolean,
    val isFeatured: Boolean
)

object ChampionsLeaderboardMath {
    const val TOTAL_PARTICIPANTS = 1500
    const val RIVAL_COUNT = TOTAL_PARTICIPANTS - 1
    const val STARTING_USER_SCORE = 1_172L
    const val PODIUM_ROTATION_HOURS = 48L
    const val MICRO_FLUCTUATION_HOURS = 10L

    private const val DAILY_CAP_ARENA_PTS = 3_500L
    private const val DAILY_CAP_TX_PTS = 360L
    private const val DAILY_CAP_VAULT_PTS = 270L
    private const val DAILY_CAP_TOOL_PTS = 210L

    val MILESTONE_THRESHOLDS = listOf(1000, 500, 300, 200, 100, 50, 10, 3, 1)

    // Portrait index map:
    // 0..15  -> 16 distinct youthful Indian male portraits (img_avatar_boy_1..16)
    // 16..20 -> 5 distinct youthful Indian female portraits (img_avatar_girl_1..5)
    // 21, 23, 25 -> male fallback portraits; 22, 24 -> female fallback portraits
    // 26 -> Pandit, 27 -> Yadav Ji, 28 -> Sofia, 29 -> Arya
    internal val featuredSpecs = listOf(
        // Top 3 Podium contenders
        FeaturedPlayerSpec(baseSlot = 0, name = "Aadarsh Sharma", isFemale = false, portraitIndex = 0, paletteIndex = 0),
        FeaturedPlayerSpec(baseSlot = 1, name = "Sabira Banu", isFemale = true, portraitIndex = 16, paletteIndex = 1),
        FeaturedPlayerSpec(baseSlot = 2, name = "Sourabh Joshi", isFemale = false, portraitIndex = 1, paletteIndex = 2),
        // Top 4–25 Grandmaster Elites
        FeaturedPlayerSpec(baseSlot = 4, name = "Shubham Yadav", isFemale = false, portraitIndex = 2, paletteIndex = 3),
        FeaturedPlayerSpec(baseSlot = 6, name = "Komal", isFemale = true, portraitIndex = 17, paletteIndex = 4),
        FeaturedPlayerSpec(baseSlot = 8, name = "Aditya Yadav", isFemale = false, portraitIndex = 3, paletteIndex = 5),
        FeaturedPlayerSpec(baseSlot = 11, name = "Prince Yadav", isFemale = false, portraitIndex = 4, paletteIndex = 6),
        FeaturedPlayerSpec(baseSlot = 14, name = "Sofia", isFemale = true, portraitIndex = 28, paletteIndex = 7),
        FeaturedPlayerSpec(baseSlot = 17, name = "Satyam Yadav", isFemale = false, portraitIndex = 5, paletteIndex = 0),
        FeaturedPlayerSpec(baseSlot = 20, name = "Pandit", isFemale = false, portraitIndex = 26, paletteIndex = 1),
        FeaturedPlayerSpec(baseSlot = 23, name = "Utkarsh Joshi", isFemale = false, portraitIndex = 7, paletteIndex = 2),
        // Ranks #26–#98 Diamond Contenders
        FeaturedPlayerSpec(baseSlot = 26, name = "Yadav Ji", isFemale = false, portraitIndex = 27, paletteIndex = 6),
        FeaturedPlayerSpec(baseSlot = 29, name = "Arya", isFemale = true, portraitIndex = 29, paletteIndex = 3),
        FeaturedPlayerSpec(baseSlot = 35, name = "Naitik Lucky", isFemale = false, portraitIndex = 8, paletteIndex = 4),
        FeaturedPlayerSpec(baseSlot = 42, name = "Shivam", isFemale = false, portraitIndex = 9, paletteIndex = 5),
        FeaturedPlayerSpec(baseSlot = 49, name = "Sushil", isFemale = false, portraitIndex = 10, paletteIndex = 6),
        FeaturedPlayerSpec(baseSlot = 56, name = "Rasiyan", isFemale = true, portraitIndex = 20, paletteIndex = 7),
        FeaturedPlayerSpec(baseSlot = 63, name = "Shashikant Dudhiya", isFemale = false, portraitIndex = 11, paletteIndex = 0),
        FeaturedPlayerSpec(baseSlot = 71, name = "Kalu", isFemale = false, portraitIndex = 12, paletteIndex = 1),
        FeaturedPlayerSpec(baseSlot = 79, name = "Ved Prakash", isFemale = false, portraitIndex = 13, paletteIndex = 2),
        FeaturedPlayerSpec(baseSlot = 87, name = "Aryan Yadav", isFemale = false, portraitIndex = 14, paletteIndex = 3),
        FeaturedPlayerSpec(baseSlot = 95, name = "Ayush", isFemale = false, portraitIndex = 15, paletteIndex = 4)
    )

    private val featuredBySlot: Map<Int, FeaturedPlayerSpec> = featuredSpecs.associateBy { it.baseSlot }
    private val podiumContenderPool = listOf(0, 1, 2, 4, 6, 8, 11, 14)

    // Strictly curated male & female first name pools for remaining ranks (never overlaps featured exact names)
    private val maleFirstNames = listOf(
        "Aarav", "Vivaan", "Vihaan", "Arjun", "Sai", "Reyansh", "Ayaan", "Krishna", "Ishaan", "Shaurya",
        "Atharv", "Pranav", "Kabir", "Rohan", "Devansh", "Karan", "Siddharth", "Nikhil", "Varun", "Harsh",
        "Ritik", "Yash", "Manish", "Gaurav", "Liam", "Noah", "Oliver", "Ethan", "Lucas", "Mason",
        "Logan", "James", "Benjamin", "Daniel", "Mateo", "Marcus"
    )

    private val femaleFirstNames = listOf(
        "Ananya", "Diya", "Aadhya", "Pari", "Saanvi", "Myra", "Meera", "Priya", "Kavya", "Riya",
        "Neha", "Shreya", "Tanvi", "Pooja", "Nandini", "Nisha", "Sneha", "Simran", "Khushi", "Muskan",
        "Emma", "Olivia", "Ava", "Mia", "Amelia", "Chloe", "Zara", "Hana", "Elena", "Clara"
    )

    private val lastNames = listOf(
        "Sharma", "Verma", "Mehta", "Nair", "Patel", "Kulkarni", "Kapoor", "Malhotra", "Rao", "Iyer",
        "Reddy", "Joshi", "Desai", "Chopra", "Bansal", "Agarwal", "Sinha", "Mishra", "Tiwari", "Saxena",
        "Gupta", "Singhania", "Chauhan", "Bhat", "Menon", "Subramanian", "Krishnan", "Sengupta", "Banerjee", "Mukherjee",
        "Carter", "Chen", "Vance", "Rostova", "Takahashi", "Moreau", "Bennett", "Lin", "Morales", "Kim",
        "Walker", "Wright", "Mitchell", "Hayes", "Brooks", "Foster", "Al-Mansoor", "Schmidt", "Weber", "Lindqvist",
        "Rossi", "Silva", "Park", "Yadav", "Pandey"
    )

    private val regions = listOf("IN", "US", "GB", "SG", "AE", "DE", "JP", "CA", "AU", "FR", "KR", "CH")

    private fun resolveNonFeaturedPortraitIndex(slot: Int, isFemale: Boolean, nameLength: Int): Int? {
        // ~80% portraits, ~20% clean Gmail-style initial badges overall
        if ((slot * 7 + 3) % 10 >= 8) return null
        // Inside Top 100, non-featured slots use adult portraits 21..25 (gender-matched) so the 21 featured youthful avatars remain 100% unique in Top 100
        if (slot <= 99) {
            return if (isFemale) {
                if ((slot + nameLength) % 2 == 0) 22 else 24
            } else {
                when ((slot + nameLength) % 3) {
                    0 -> 21
                    1 -> 23
                    else -> 25
                }
            }
        }
        return if (isFemale) {
            val femalePool = intArrayOf(16, 17, 18, 19, 20, 22, 24)
            femalePool[(slot + nameLength) % femalePool.size]
        } else {
            val malePool = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 21, 23, 25)
            malePool[(slot + nameLength) % malePool.size]
        }
    }

    private val cachedSeeds: List<RivalSeed> by lazy {
        val usedNames = HashSet<String>(TOTAL_PARTICIPANTS)
        featuredSpecs.forEach { usedNames.add(it.name) }

        List(RIVAL_COUNT) { slot ->
            val featured = featuredBySlot[slot]
            if (featured != null) {
                RivalSeed(
                    baseSlot = slot,
                    name = featured.name,
                    initials = computeInitials(featured.name),
                    regionBadge = "IN",
                    avatarColorIndex = featured.paletteIndex,
                    baseScore = baseScoreForSlot(slot),
                    dailyDriftRate = dailyDriftRateForSlot(slot),
                    baseStreakDays = baseStreakForSlot(slot),
                    portraitIndex = featured.portraitIndex,
                    isFemale = featured.isFemale,
                    isFeatured = true
                )
            } else {
                val isFemale = (slot * 5 + 3) % 10 < 4
                val fnPool = if (isFemale) femaleFirstNames else maleFirstNames
                var probe = slot
                var candidate = ""
                var attempts = 0
                while (attempts < 300) {
                    val fn = fnPool[((probe * 17) + 11) % fnPool.size]
                    val ln = lastNames[((probe * 23) + 19) % lastNames.size]
                    val full = "$fn $ln"
                    if (full !in usedNames) {
                        candidate = full
                        usedNames.add(full)
                        break
                    }
                    probe += 1
                    attempts += 1
                }
                if (candidate.isBlank()) {
                    candidate = "${fnPool[slot % fnPool.size]} ${lastNames[(slot / fnPool.size) % lastNames.size]} ${slot + 1}"
                }
                val reg = if (slot <= 99 || (slot % 10) < 6) "IN" else regions[(slot * 7 + 3) % regions.size]
                RivalSeed(
                    baseSlot = slot,
                    name = candidate,
                    initials = computeInitials(candidate),
                    regionBadge = reg,
                    avatarColorIndex = (slot * 5 + 2) % 8,
                    baseScore = baseScoreForSlot(slot),
                    dailyDriftRate = dailyDriftRateForSlot(slot),
                    baseStreakDays = baseStreakForSlot(slot),
                    portraitIndex = resolveNonFeaturedPortraitIndex(slot, isFemale, candidate.length),
                    isFemale = isFemale,
                    isFeatured = false
                )
            }
        }
    }

    fun sanitizePlayerDisplayName(rawName: String): String {
        val trimmed = rawName.trim()
        if (trimmed.isBlank()) return "Koshvanta Explorer"
        val withoutEmail = if ("@" in trimmed) {
            trimmed.substringBefore("@")
                .replace('.', ' ')
                .replace('_', ' ')
                .trim()
        } else {
            trimmed
        }
        return withoutEmail.ifBlank { "Koshvanta Explorer" }
    }

    fun computeInitials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "KE"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
        }
    }

    /**
     * Non-linear Prestige curve across 1,499 rival slots:
     * - Slots 400..1498 (Early Boost): gentle spacing (~6-16 pts/rank) so new users at 1,172 (#1,485) climb fast.
     * - Slots 100..399 (Mid-Tier Grind): moderate spacing (~40-150 pts/rank).
     * - Slots 0..99 (Top-100 Elite Wall): clean gap above slot 100 (35,200 vs 33,500) scaling up to ~258,400 at Rank #1.
     */
    fun baseScoreForSlot(slot: Int): Long {
        val raw = when {
            slot <= 9 -> {
                val t = (9 - slot) / 9.0
                142_000L + (116_400.0 * t.pow(1.38)).roundToLong()
            }
            slot <= 99 -> {
                val t = (99 - slot) / 89.0
                35_200L + (101_000.0 * t.pow(1.72)).roundToLong()
            }
            slot <= 399 -> {
                val t = (399 - slot) / 299.0
                10_500L + (23_000.0 * t.pow(1.48)).roundToLong()
            }
            else -> {
                val stepsFromBottom = (RIVAL_COUNT - 1) - slot // 0..1098
                val t = stepsFromBottom / 1098.0
                1_082L + (stepsFromBottom * 6L) + (2_800.0 * t.pow(2.05)).roundToLong()
            }
        }
        val fineJitter = ((slot * 13 + 5) % 3).toLong()
        return (raw - fineJitter).coerceAtLeast(1_050L)
    }

    fun dailyDriftRateForSlot(slot: Int): Long {
        return when {
            slot < 25 -> 240L + ((25 - slot) * 7L)
            slot < 100 -> 140L + ((100 - slot) * 1L)
            slot < 400 -> 55L + ((400 - slot) / 6L)
            else -> 14L + ((RIVAL_COUNT - slot) / 50L)
        }
    }

    fun baseStreakForSlot(slot: Int): Int {
        return when {
            slot < 10 -> 78 - (slot * 2) + ((slot * 3) % 5)
            slot < 25 -> 48 + ((25 - slot) / 2) + (slot % 4)
            slot < 120 -> 21 + ((120 - slot) / 5) + (slot % 3)
            slot < 400 -> 8 + ((400 - slot) / 24)
            slot < 900 -> 3 + ((900 - slot) / 100)
            else -> 1 + ((RIVAL_COUNT - slot) / 300)
        }.coerceAtLeast(1)
    }

    fun arenaXpToPrestigeDelta(xpDelta: Long, alreadyAwardedToday: Long): Long {
        if (xpDelta <= 0L) return 0L
        val remainingCap = (DAILY_CAP_ARENA_PTS - alreadyAwardedToday).coerceAtLeast(0L)
        val raw = (xpDelta * 2.5).roundToLong().coerceAtLeast(1L)
        return raw.coerceAtMost(remainingCap)
    }

    fun transactionPointsToAdd(alreadyAwardedToday: Long): Long {
        val remaining = (DAILY_CAP_TX_PTS - alreadyAwardedToday).coerceAtLeast(0L)
        return 45L.coerceAtMost(remaining)
    }

    fun vaultPointsToAdd(alreadyAwardedToday: Long): Long {
        val remaining = (DAILY_CAP_VAULT_PTS - alreadyAwardedToday).coerceAtLeast(0L)
        return 90L.coerceAtMost(remaining)
    }

    fun toolPointsToAdd(alreadyAwardedToday: Long): Long {
        val remaining = (DAILY_CAP_TOOL_PTS - alreadyAwardedToday).coerceAtLeast(0L)
        return 35L.coerceAtMost(remaining)
    }

    fun streakDayBonus(streakDay: Int): Long {
        if (streakDay <= 1) return 0L
        return 180L + (streakDay.toLong() * 28L).coerceAtMost(900L)
    }

    /**
     * Rotates ONLY ONE podium seat per 48-hour cycle among featured Top-25 contenders,
     * returning a slot->slot permutation map for podiumContenderPool.
     */
    internal fun computePodiumPermutation(podiumCycle: Long): Map<Int, Int> {
        val safeCycle = podiumCycle.coerceAtLeast(0L).coerceAtMost(240L).toInt()
        if (safeCycle == 0) return emptyMap()

        // Track which contender currently occupies each position in podiumContenderPool
        val activePool = podiumContenderPool.toMutableList()
        var poolCursor = 3
        for (step in 1..safeCycle) {
            // Rotate seat #3 first, then #2, then #1 (strictly 1 seat per cycle)
            val podiumSeat = when ((step - 1) % 3) {
                0 -> 2
                1 -> 1
                else -> 0
            }
            val swapIndex = 3 + ((poolCursor - 3) % (activePool.size - 3))
            val temp = activePool[podiumSeat]
            activePool[podiumSeat] = activePool[swapIndex]
            activePool[swapIndex] = temp
            poolCursor += 1
        }
        return podiumContenderPool.indices.associate { idx ->
            podiumContenderPool[idx] to activePool[idx]
        }
    }

    internal fun microFluctuationForSlot(slot: Int, microWindow: Long): Long {
        if (microWindow <= 0L || slot <= 2) return 0L
        val hash = abs((slot * 1103515245L + microWindow * 12345L + 7919L) % 1000L)
        val normalized = hash - 500L // -500..+499
        return when {
            slot <= 9 -> (normalized * 2L).coerceIn(-600L, 600L)
            slot <= 99 -> (normalized / 2L).coerceIn(-220L, 220L)
            slot <= 399 -> (normalized / 7L).coerceIn(-70L, 70L)
            else -> (normalized / 30L).coerceIn(-16L, 16L)
        }
    }

    fun rivalScoreForSlot(slot: Int, totalDriftDays: Int, elapsedHours: Long = 0L): Long {
        val safeSlot = slot.coerceIn(0, RIVAL_COUNT - 1)
        val safeDriftDays = totalDriftDays.coerceAtLeast(0)
        val microWindow = elapsedHours.coerceAtLeast(0L) / MICRO_FLUCTUATION_HOURS
        val base = baseScoreForSlot(safeSlot) + (dailyDriftRateForSlot(safeSlot) * safeDriftDays)
        val jitter = microFluctuationForSlot(safeSlot, microWindow)
        // Enforce strict Top-100 floor (slot 99 never falls below slot 100)
        return if (safeSlot <= 99) {
            val top100Floor = baseScoreForSlot(100) + (dailyDriftRateForSlot(100) * safeDriftDays) + 600L
            (base + jitter).coerceAtLeast(top100Floor)
        } else {
            val top101Ceiling = baseScoreForSlot(100) + (dailyDriftRateForSlot(100) * safeDriftDays) + 250L
            (base + jitter).coerceAtMost(top101Ceiling).coerceAtLeast(1_050L)
        }
    }

    fun buildCelebrationForMilestone(threshold: Int, currentRank: Int): LeaderboardCelebration {
        val league = LeagueTier.fromRank(currentRank)
        return when (threshold) {
            1 -> LeaderboardCelebration(
                milestoneRankThreshold = 1,
                currentRank = currentRank,
                kind = CelebrationKind.GRAND_CHAMPION,
                badgeLabel = "RANK #1",
                title = "👑 GRAND CHAMPION • RANK #1",
                subtitle = "You've claimed the Kushwanta #1 Throne! Keep your daily streak and momentum sharp to defend your crown.",
                leagueName = league.title
            )
            3 -> LeaderboardCelebration(
                milestoneRankThreshold = 3,
                currentRank = currentRank,
                kind = CelebrationKind.ROYAL_PODIUM,
                badgeLabel = "TOP 3 PODIUM",
                title = "🏆 Royal Podium Unlocked!",
                subtitle = "You've broken onto the 3D Royal Podium at Rank #$currentRank in the ${league.title} League.",
                leagueName = league.title
            )
            10 -> LeaderboardCelebration(
                milestoneRankThreshold = 10,
                currentRank = currentRank,
                kind = CelebrationKind.MILESTONE_TIER,
                badgeLabel = "TOP 10",
                title = "👑 Top 10 Global Finalist!",
                subtitle = "Only single-digit contenders separate you from the 3D Royal Podium.",
                leagueName = league.title
            )
            50 -> LeaderboardCelebration(
                milestoneRankThreshold = 50,
                currentRank = currentRank,
                kind = CelebrationKind.MILESTONE_TIER,
                badgeLabel = "TOP 50",
                title = "🔱 Top 50 Global Titan!",
                subtitle = "You've entered the elite Top 50 contenders pushing toward the Grandmaster podium.",
                leagueName = league.title
            )
            100 -> LeaderboardCelebration(
                milestoneRankThreshold = 100,
                currentRank = currentRank,
                kind = CelebrationKind.MILESTONE_TIER,
                badgeLabel = "TOP 100",
                title = "⚡ Top 100 Global Contender!",
                subtitle = "You've entered the ${league.title} Arena League Top 100.",
                leagueName = league.title
            )
            200 -> LeaderboardCelebration(
                milestoneRankThreshold = 200,
                currentRank = currentRank,
                kind = CelebrationKind.MILESTONE_TIER,
                badgeLabel = "TOP 200",
                title = "🌟 Top 200 Global Vanguard!",
                subtitle = "Consistent Wealth OS & Mind Arena discipline placed you inside the Top 200.",
                leagueName = league.title
            )
            300 -> LeaderboardCelebration(
                milestoneRankThreshold = 300,
                currentRank = currentRank,
                kind = CelebrationKind.MILESTONE_TIER,
                badgeLabel = "TOP 300",
                title = "💎 Top 300 Global Challenger!",
                subtitle = "You've surpassed 1,200+ players and surged into the ${league.title} League Top 300.",
                leagueName = league.title
            )
            500 -> LeaderboardCelebration(
                milestoneRankThreshold = 500,
                currentRank = currentRank,
                kind = CelebrationKind.MILESTONE_TIER,
                badgeLabel = "TOP 500",
                title = "🔥 Top 500 Rising Elite!",
                subtitle = "You're now among the Top 500 players in the ${league.title} League.",
                leagueName = league.title
            )
            else -> LeaderboardCelebration(
                milestoneRankThreshold = 1000,
                currentRank = currentRank,
                kind = CelebrationKind.MILESTONE_TIER,
                badgeLabel = "TOP 1,000",
                title = "⚡ Top 1,000 Arena Breakthrough!",
                subtitle = "You've crossed into the Top 1,000 contenders in the ${league.title} League.",
                leagueName = league.title
            )
        }
    }

    fun buildLeaderboardState(
        userScore: Long,
        userName: String,
        userPhotoUri: String,
        userStreakDays: Int,
        totalDriftDays: Int,
        elapsedHours: Long = 0L,
        wasEverTopThree: Boolean = false
    ): LeaderboardUiState {
        val cleanName = sanitizePlayerDisplayName(userName)
        val userInitials = computeInitials(cleanName)
        val safeDriftDays = totalDriftDays.coerceAtLeast(0)
        val safeElapsedHours = elapsedHours.coerceAtLeast(0L)
        val podiumCycle = safeElapsedHours / PODIUM_ROTATION_HOURS
        val podiumPermutation = computePodiumPermutation(podiumCycle)

        data class RankedCandidate(
            val entry: LeaderboardEntry,
            val sortTieBreaker: Int
        )

        val candidates = ArrayList<RankedCandidate>(TOTAL_PARTICIPANTS)
        cachedSeeds.forEach { slotSeed ->
            val identitySlot = podiumPermutation[slotSeed.baseSlot] ?: slotSeed.baseSlot
            val identitySeed = cachedSeeds[identitySlot]
            val driftedScore = rivalScoreForSlot(
                slot = slotSeed.baseSlot,
                totalDriftDays = safeDriftDays,
                elapsedHours = safeElapsedHours
            )
            val driftedStreak = identitySeed.baseStreakDays + (safeDriftDays / 2)
            candidates.add(
                RankedCandidate(
                    entry = LeaderboardEntry(
                        rank = 0,
                        name = identitySeed.name,
                        initials = identitySeed.initials,
                        regionBadge = identitySeed.regionBadge,
                        avatarColorIndex = identitySeed.avatarColorIndex,
                        streakDays = driftedStreak,
                        score = driftedScore,
                        leagueTier = LeagueTier.BRONZE,
                        isCurrentUser = false,
                        portraitIndex = identitySeed.portraitIndex,
                        isFemale = identitySeed.isFemale
                    ),
                    sortTieBreaker = slotSeed.baseSlot + 1
                )
            )
        }

        candidates.add(
            RankedCandidate(
                entry = LeaderboardEntry(
                    rank = 0,
                    name = cleanName,
                    initials = userInitials,
                    regionBadge = "IN",
                    avatarColorIndex = 0,
                    streakDays = userStreakDays.coerceAtLeast(1),
                    score = userScore.coerceAtLeast(STARTING_USER_SCORE),
                    leagueTier = LeagueTier.BRONZE,
                    isCurrentUser = true,
                    photoUri = userPhotoUri
                ),
                sortTieBreaker = 0
            )
        )

        candidates.sortWith(
            compareByDescending<RankedCandidate> { it.entry.score }
                .thenBy { it.sortTieBreaker }
        )

        var currentUserFinal: LeaderboardEntry? = null
        val rankedEntries = candidates.mapIndexed { idx, candidate ->
            val rank = idx + 1
            val finalEntry = candidate.entry.copy(
                rank = rank,
                leagueTier = LeagueTier.fromRank(rank)
            )
            if (finalEntry.isCurrentUser) {
                currentUserFinal = finalEntry
            }
            finalEntry
        }

        val resolvedUser = currentUserFinal ?: rankedEntries.last()
        val slipAlertRank = if (wasEverTopThree && resolvedUser.rank in 4..10) {
            resolvedUser.rank
        } else {
            null
        }
        return LeaderboardUiState(
            entries = rankedEntries,
            topThree = rankedEntries.take(3),
            currentUserEntry = resolvedUser,
            totalParticipants = rankedEntries.size,
            throneSlipAlertRank = slipAlertRank
        )
    }
}

class ChampionsLeaderboardEngine(context: Context) {
    private val appCtx = context.applicationContext
    private val prefs: SharedPreferences =
        appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val arenaPrefs: SharedPreferences =
        appCtx.getSharedPreferences(ARENA_PREFS_NAME, Context.MODE_PRIVATE)

    @Synchronized
    fun ensureInitialized(
        currentEpochDay: Long = epochDayNow(),
        currentStreak: Int = 1,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            val initialArenaXp = runCatching { arenaPrefs.getLong(KEY_ARENA_TOTAL_XP, 0L) }.getOrDefault(0L)
            prefs.edit()
                .putBoolean(KEY_INITIALIZED, true)
                .putLong(KEY_FIRST_INSTALL_MILLIS, nowMillis)
                .putLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)
                .putLong(KEY_LAST_DRIFT_DAY, currentEpochDay)
                .putInt(KEY_TOTAL_DRIFT_DAYS, 0)
                .putLong(KEY_LAST_SYNCED_ARENA_XP, initialArenaXp)
                .putInt(KEY_HIGHEST_STREAK_REWARDED, currentStreak.coerceAtLeast(1))
                .putInt(KEY_PREVIOUS_STREAK, currentStreak.coerceAtLeast(1))
                .putLong(KEY_LAST_ACTIVE_ENGAGEMENT_DAY, currentEpochDay)
                .putInt(KEY_TOP4_CONSECUTIVE_DAYS, 0)
                .putLong(KEY_TOP4_LAST_CREDIT_DAY, -1L)
                .putLong(KEY_LAST_DECAY_DAY, -1L)
                .putBoolean(KEY_WAS_EVER_TOP_THREE, false)
                .putInt(KEY_HIGHEST_RANK_ACHIEVED, ChampionsLeaderboardMath.TOTAL_PARTICIPANTS)
                .putString(KEY_UNLOCKED_MILESTONES, "")
                .putLong(KEY_DAILY_RESET_DAY, currentEpochDay)
                .commit()
        } else if (prefs.getLong(KEY_FIRST_INSTALL_MILLIS, 0L) <= 0L) {
            prefs.edit().putLong(KEY_FIRST_INSTALL_MILLIS, nowMillis).commit()
        }
    }

    @Synchronized
    fun syncAndGetState(
        userName: String,
        userPhotoUri: String,
        userStreakDays: Int,
        nowMillis: Long = System.currentTimeMillis()
    ): LeaderboardUiState {
        val today = epochDayForMillis(nowMillis)
        ensureInitialized(today, userStreakDays, nowMillis)
        advanceRivalDriftIfNeeded(today)
        resetDailyBucketsIfNewDay(today)
        syncStreakBonusInternal(userStreakDays, today)
        syncArenaXpDeltaInternal(today)

        val totalDriftDays = prefs.getInt(KEY_TOTAL_DRIFT_DAYS, 0)
        val firstInstallMillis = prefs.getLong(KEY_FIRST_INSTALL_MILLIS, nowMillis)
        val elapsedHours = ((nowMillis - firstInstallMillis).coerceAtLeast(0L)) / 3_600_000L

        applyThroneDefenseAndPodiumAscension(
            today = today,
            userStreakDays = userStreakDays.coerceAtLeast(1),
            totalDriftDays = totalDriftDays,
            elapsedHours = elapsedHours
        )

        val score = prefs.getLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)
        val wasEverTopThree = prefs.getBoolean(KEY_WAS_EVER_TOP_THREE, false)
        val state = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = score,
            userName = userName,
            userPhotoUri = userPhotoUri,
            userStreakDays = userStreakDays,
            totalDriftDays = totalDriftDays,
            elapsedHours = elapsedHours,
            wasEverTopThree = wasEverTopThree
        )

        val currentRank = state.currentUserEntry.rank
        val prevBestRank = prefs.getInt(KEY_HIGHEST_RANK_ACHIEVED, ChampionsLeaderboardMath.TOTAL_PARTICIPANTS)
        if (currentRank < prevBestRank) {
            prefs.edit().putInt(KEY_HIGHEST_RANK_ACHIEVED, currentRank).apply()
        }
        if (currentRank <= 3 && !wasEverTopThree) {
            prefs.edit().putBoolean(KEY_WAS_EVER_TOP_THREE, true).apply()
        }
        return state
    }

    /**
     * Checks whether the current rank crossed any uncelebrated milestone thresholds.
     * Marks ALL newly crossed thresholds as persisted immediately so they never repeat,
     * and returns the most prestigious newly unlocked celebration (if any).
     */
    @Synchronized
    fun consumePendingCelebrationForRank(currentRank: Int): LeaderboardCelebration? {
        if (currentRank <= 0 || currentRank > 1000) return null
        val unlockedRaw = prefs.getString(KEY_UNLOCKED_MILESTONES, "") ?: ""
        val unlockedSet = unlockedRaw.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toMutableSet()

        val newlyCrossed = ChampionsLeaderboardMath.MILESTONE_THRESHOLDS
            .filter { threshold -> currentRank <= threshold && threshold !in unlockedSet }

        if (newlyCrossed.isEmpty()) return null

        unlockedSet.addAll(newlyCrossed)
        prefs.edit()
            .putString(KEY_UNLOCKED_MILESTONES, unlockedSet.sortedDescending().joinToString(","))
            .apply()

        val highestThreshold = newlyCrossed.minOrNull() ?: return null
        return ChampionsLeaderboardMath.buildCelebrationForMilestone(
            threshold = highestThreshold,
            currentRank = currentRank
        )
    }

    @Synchronized
    fun recordTransactionActivity(nowMillis: Long = System.currentTimeMillis()) {
        val today = epochDayForMillis(nowMillis)
        ensureInitialized(today, nowMillis = nowMillis)
        resetDailyBucketsIfNewDay(today)
        markActiveEngagementDay(today)
        val awardedToday = prefs.getLong(KEY_DAILY_TX_PTS, 0L)
        val delta = ChampionsLeaderboardMath.transactionPointsToAdd(awardedToday)
        if (delta > 0L) {
            val current = prefs.getLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)
            prefs.edit()
                .putLong(KEY_USER_SCORE, current + delta)
                .putLong(KEY_DAILY_TX_PTS, awardedToday + delta)
                .apply()
        }
    }

    @Synchronized
    fun recordBudgetSetupOrAdherence(
        hasActiveBudgets: Boolean,
        withinAllBudgets: Boolean,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        if (!hasActiveBudgets) return
        val today = epochDayForMillis(nowMillis)
        ensureInitialized(today, nowMillis = nowMillis)
        resetDailyBucketsIfNewDay(today)
        if (withinAllBudgets) {
            markActiveEngagementDay(today)
        }
        var added = 0L

        val monthKey = today / 30L
        if (prefs.getLong(KEY_LAST_BUDGET_SETUP_MONTH, -1L) != monthKey) {
            added += 160L
            prefs.edit().putLong(KEY_LAST_BUDGET_SETUP_MONTH, monthKey).apply()
        }

        if (withinAllBudgets && prefs.getLong(KEY_LAST_BUDGET_ADHERENCE_DAY, -1L) != today) {
            added += 220L
            prefs.edit().putLong(KEY_LAST_BUDGET_ADHERENCE_DAY, today).apply()
        }

        if (added > 0L) {
            val current = prefs.getLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)
            prefs.edit().putLong(KEY_USER_SCORE, current + added).apply()
        }
    }

    @Synchronized
    fun recordCardVaultActivity(nowMillis: Long = System.currentTimeMillis()) {
        val today = epochDayForMillis(nowMillis)
        ensureInitialized(today, nowMillis = nowMillis)
        resetDailyBucketsIfNewDay(today)
        markActiveEngagementDay(today)
        val awardedToday = prefs.getLong(KEY_DAILY_VAULT_PTS, 0L)
        val delta = ChampionsLeaderboardMath.vaultPointsToAdd(awardedToday)
        if (delta > 0L) {
            val current = prefs.getLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)
            prefs.edit()
                .putLong(KEY_USER_SCORE, current + delta)
                .putLong(KEY_DAILY_VAULT_PTS, awardedToday + delta)
                .apply()
        }
    }

    @Synchronized
    fun recordFinancialToolExplored(toolId: String, nowMillis: Long = System.currentTimeMillis()) {
        if (toolId.isBlank()) return
        val today = epochDayForMillis(nowMillis)
        ensureInitialized(today, nowMillis = nowMillis)
        resetDailyBucketsIfNewDay(today)
        markActiveEngagementDay(today)

        val exploredToday = (prefs.getString(KEY_DAILY_TOOLS_SET, "") ?: "")
            .split(",")
            .filter { it.isNotBlank() }
            .toMutableSet()
        if (toolId in exploredToday) return

        val awardedToday = prefs.getLong(KEY_DAILY_TOOL_PTS, 0L)
        val delta = ChampionsLeaderboardMath.toolPointsToAdd(awardedToday)
        exploredToday.add(toolId)
        val editor = prefs.edit().putString(KEY_DAILY_TOOLS_SET, exploredToday.joinToString(","))
        if (delta > 0L) {
            val current = prefs.getLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)
            editor
                .putLong(KEY_USER_SCORE, current + delta)
                .putLong(KEY_DAILY_TOOL_PTS, awardedToday + delta)
        }
        editor.apply()
    }

    @Synchronized
    fun syncArenaXpDelta(nowMillis: Long = System.currentTimeMillis()) {
        val today = epochDayForMillis(nowMillis)
        ensureInitialized(today, nowMillis = nowMillis)
        resetDailyBucketsIfNewDay(today)
        syncArenaXpDeltaInternal(today)
    }

    private fun markActiveEngagementDay(today: Long) {
        if (prefs.getLong(KEY_LAST_ACTIVE_ENGAGEMENT_DAY, -1L) != today) {
            prefs.edit().putLong(KEY_LAST_ACTIVE_ENGAGEMENT_DAY, today).apply()
        }
    }

    private fun syncArenaXpDeltaInternal(today: Long) {
        val currentArenaXp = runCatching { arenaPrefs.getLong(KEY_ARENA_TOTAL_XP, 0L) }.getOrDefault(0L)
        val lastSyncedXp = prefs.getLong(KEY_LAST_SYNCED_ARENA_XP, currentArenaXp)
        if (currentArenaXp > lastSyncedXp) {
            markActiveEngagementDay(today)
            val xpDelta = currentArenaXp - lastSyncedXp
            val awardedToday = prefs.getLong(KEY_DAILY_ARENA_PTS, 0L)
            val prestigeDelta = ChampionsLeaderboardMath.arenaXpToPrestigeDelta(xpDelta, awardedToday)
            val current = prefs.getLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)
            prefs.edit()
                .putLong(KEY_LAST_SYNCED_ARENA_XP, currentArenaXp)
                .putLong(KEY_DAILY_ARENA_PTS, awardedToday + prestigeDelta)
                .putLong(KEY_USER_SCORE, current + prestigeDelta)
                .apply()
        } else if (currentArenaXp < lastSyncedXp) {
            prefs.edit().putLong(KEY_LAST_SYNCED_ARENA_XP, currentArenaXp).apply()
        }
    }

    private fun syncStreakBonusInternal(currentStreak: Int, today: Long) {
        val safeStreak = currentStreak.coerceAtLeast(1)
        val rewardedUpTo = prefs.getInt(KEY_HIGHEST_STREAK_REWARDED, 1)
        if (safeStreak > rewardedUpTo) {
            markActiveEngagementDay(today)
            var bonus = 0L
            for (day in (rewardedUpTo + 1)..safeStreak) {
                bonus += ChampionsLeaderboardMath.streakDayBonus(day)
            }
            val current = prefs.getLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)
            prefs.edit()
                .putInt(KEY_HIGHEST_STREAK_REWARDED, safeStreak)
                .putLong(KEY_USER_SCORE, current + bonus)
                .apply()
        } else if (safeStreak < rewardedUpTo && safeStreak == 1) {
            prefs.edit().putInt(KEY_HIGHEST_STREAK_REWARDED, 1).apply()
        }
    }

    private fun applyThroneDefenseAndPodiumAscension(
        today: Long,
        userStreakDays: Int,
        totalDriftDays: Int,
        elapsedHours: Long
    ) {
        val previousStreak = prefs.getInt(KEY_PREVIOUS_STREAK, userStreakDays)
        val streakBroken = previousStreak > 1 && userStreakDays == 1
        if (userStreakDays != previousStreak) {
            prefs.edit().putInt(KEY_PREVIOUS_STREAK, userStreakDays).apply()
        }

        val lastActiveDay = prefs.getLong(KEY_LAST_ACTIVE_ENGAGEMENT_DAY, today)
        val inactiveDays = (today - lastActiveDay).coerceAtLeast(0L)
        val wasEverTopThree = prefs.getBoolean(KEY_WAS_EVER_TOP_THREE, false)
        val highestRank = prefs.getInt(KEY_HIGHEST_RANK_ACHIEVED, ChampionsLeaderboardMath.TOTAL_PARTICIPANTS)
        var userScore = prefs.getLong(KEY_USER_SCORE, ChampionsLeaderboardMath.STARTING_USER_SCORE)

        // 1. Throne Defense & Rank Decay if user reached Top 10 / Podium and then broke streak or went inactive 2+ days
        val lastDecayDay = prefs.getLong(KEY_LAST_DECAY_DAY, -1L)
        if ((wasEverTopThree || highestRank <= 10) && (streakBroken || inactiveDays >= 2L) && lastDecayDay != today) {
            val targetDecaySlot = when {
                inactiveDays >= 4L -> 9 // Rank #10
                inactiveDays == 3L -> 6 // Rank #7
                inactiveDays == 2L -> 4 // Rank #5
                else -> 3               // Rank #4 on fresh streak break
            }
            val thresholdBelowTarget = ChampionsLeaderboardMath.rivalScoreForSlot(
                slot = targetDecaySlot,
                totalDriftDays = totalDriftDays,
                elapsedHours = elapsedHours
            ) - 35L
            val decayedScore = thresholdBelowTarget.coerceAtLeast(ChampionsLeaderboardMath.STARTING_USER_SCORE)
            if (userScore > decayedScore) {
                userScore = decayedScore
                prefs.edit()
                    .putLong(KEY_USER_SCORE, userScore)
                    .putInt(KEY_TOP4_CONSECUTIVE_DAYS, 0)
                    .putLong(KEY_LAST_DECAY_DAY, today)
                    .apply()
            } else {
                prefs.edit()
                    .putInt(KEY_TOP4_CONSECUTIVE_DAYS, 0)
                    .putLong(KEY_LAST_DECAY_DAY, today)
                    .apply()
            }
            return
        }

        // 2. Podium Ascension (#4 -> #3 -> #2 -> #1) after sustained 3-5 day streak + engagement at Top 4/5
        val rank4GateScore = ChampionsLeaderboardMath.rivalScoreForSlot(
            slot = 4,
            totalDriftDays = totalDriftDays,
            elapsedHours = elapsedHours
        )
        val isEngagedToday = lastActiveDay == today
        if (userScore >= rank4GateScore && isEngagedToday && userStreakDays >= 4) {
            val lastCreditDay = prefs.getLong(KEY_TOP4_LAST_CREDIT_DAY, -1L)
            var consecutiveDays = prefs.getInt(KEY_TOP4_CONSECUTIVE_DAYS, 0)
            if (lastCreditDay != today) {
                consecutiveDays = if (lastCreditDay == today - 1L || consecutiveDays == 0) {
                    consecutiveDays + 1
                } else {
                    1
                }
                prefs.edit()
                    .putInt(KEY_TOP4_CONSECUTIVE_DAYS, consecutiveDays)
                    .putLong(KEY_TOP4_LAST_CREDIT_DAY, today)
                    .apply()
            }

            val podiumTargetSlot = when {
                consecutiveDays >= 5 && userStreakDays >= 5 -> 0 // Rank #1 Grand Champion
                consecutiveDays == 4 -> 1                        // Rank #2
                consecutiveDays == 3 -> 2                        // Rank #3
                else -> -1
            }
            if (podiumTargetSlot >= 0) {
                val minPodiumScore = ChampionsLeaderboardMath.rivalScoreForSlot(
                    slot = podiumTargetSlot,
                    totalDriftDays = totalDriftDays,
                    elapsedHours = elapsedHours
                ) + 85L
                if (userScore < minPodiumScore) {
                    prefs.edit().putLong(KEY_USER_SCORE, minPodiumScore).apply()
                }
            }
        }
    }

    private fun advanceRivalDriftIfNeeded(today: Long) {
        val lastDriftDay = prefs.getLong(KEY_LAST_DRIFT_DAY, today)
        if (today > lastDriftDay) {
            val elapsed = (today - lastDriftDay).coerceIn(1L, 30L).toInt()
            val accumulated = prefs.getInt(KEY_TOTAL_DRIFT_DAYS, 0) + elapsed
            prefs.edit()
                .putLong(KEY_LAST_DRIFT_DAY, today)
                .putInt(KEY_TOTAL_DRIFT_DAYS, accumulated)
                .apply()
        }
    }

    private fun resetDailyBucketsIfNewDay(today: Long) {
        val lastDailyDay = prefs.getLong(KEY_DAILY_RESET_DAY, today)
        if (today != lastDailyDay) {
            prefs.edit()
                .putLong(KEY_DAILY_RESET_DAY, today)
                .putLong(KEY_DAILY_ARENA_PTS, 0L)
                .putLong(KEY_DAILY_TX_PTS, 0L)
                .putLong(KEY_DAILY_VAULT_PTS, 0L)
                .putLong(KEY_DAILY_TOOL_PTS, 0L)
                .putString(KEY_DAILY_TOOLS_SET, "")
                .apply()
        }
    }

    private fun epochDayNow(): Long = epochDayForMillis(System.currentTimeMillis())

    private fun epochDayForMillis(millis: Long): Long = millis / 86_400_000L

    companion object {
        const val PREFS_NAME = "koshvanta_champions_prefs"
        private const val ARENA_PREFS_NAME = "kushwanta_mind_arena_prefs"
        private const val KEY_ARENA_TOTAL_XP = "arena_total_xp"

        private const val KEY_INITIALIZED = "champions_initialized"
        private const val KEY_FIRST_INSTALL_MILLIS = "champions_first_install_millis"
        private const val KEY_USER_SCORE = "champions_user_score"
        private const val KEY_LAST_DRIFT_DAY = "champions_last_drift_day"
        private const val KEY_TOTAL_DRIFT_DAYS = "champions_total_drift_days"
        private const val KEY_LAST_SYNCED_ARENA_XP = "champions_last_synced_arena_xp"
        private const val KEY_HIGHEST_STREAK_REWARDED = "champions_highest_streak_rewarded"
        private const val KEY_PREVIOUS_STREAK = "champions_previous_streak"
        private const val KEY_LAST_BUDGET_SETUP_MONTH = "champions_last_budget_setup_month"
        private const val KEY_LAST_BUDGET_ADHERENCE_DAY = "champions_last_budget_adherence_day"
        private const val KEY_LAST_ACTIVE_ENGAGEMENT_DAY = "champions_last_active_engagement_day"
        private const val KEY_TOP4_CONSECUTIVE_DAYS = "champions_top4_consecutive_days"
        private const val KEY_TOP4_LAST_CREDIT_DAY = "champions_top4_last_credit_day"
        private const val KEY_LAST_DECAY_DAY = "champions_last_decay_day"
        private const val KEY_WAS_EVER_TOP_THREE = "champions_was_ever_top_three"
        private const val KEY_HIGHEST_RANK_ACHIEVED = "champions_highest_rank_achieved"
        private const val KEY_UNLOCKED_MILESTONES = "champions_unlocked_milestones"

        private const val KEY_DAILY_RESET_DAY = "champions_daily_reset_day"
        private const val KEY_DAILY_ARENA_PTS = "champions_daily_arena_pts"
        private const val KEY_DAILY_TX_PTS = "champions_daily_tx_pts"
        private const val KEY_DAILY_VAULT_PTS = "champions_daily_vault_pts"
        private const val KEY_DAILY_TOOL_PTS = "champions_daily_tool_pts"
        private const val KEY_DAILY_TOOLS_SET = "champions_daily_tools_set"
    }
}
