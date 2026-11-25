package com.example

import com.example.domain.leaderboard.CelebrationKind
import com.example.domain.leaderboard.ChampionsLeaderboardMath
import com.example.domain.leaderboard.LeagueTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChampionsLeaderboardEngineTest {

    @Test
    fun newUser_startsAroundRank1485_with1500TotalUniquePlayers() {
        val state = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.STARTING_USER_SCORE,
            userName = "Aarav Test",
            userPhotoUri = "",
            userStreakDays = 1,
            totalDriftDays = 0
        )

        assertEquals(1500, state.totalParticipants)
        assertEquals(1500, state.entries.size)
        assertEquals(3, state.topThree.size)

        val startRank = state.currentUserEntry.rank
        assertTrue("Expected starting rank between 1450 and 1500, was $startRank", startRank in 1450..1500)
        assertEquals(LeagueTier.BRONZE, state.currentUserEntry.leagueTier)

        // Ensure all 1,499 rivals have unique names and strictly monotonic scores
        val rivalNames = state.entries.filter { !it.isCurrentUser }.map { it.name }
        assertEquals(1499, rivalNames.toSet().size)

        for (i in 0 until state.entries.size - 1) {
            assertEquals(i + 1, state.entries[i].rank)
            assertTrue(
                "Expected monotonic scores at index $i",
                state.entries[i].score >= state.entries[i + 1].score
            )
        }

        // Top 3 podium must all have realistic portraits; ~80% overall portraits, ~20% initial badges
        assertTrue(state.topThree.all { it.portraitIndex != null })
        val rivalPortraitCount = state.entries.count { !it.isCurrentUser && it.portraitIndex != null }
        val rivalInitialBadgeCount = state.entries.count { !it.isCurrentUser && it.portraitIndex == null }
        assertTrue("Expected ~80% portrait avatars (got $rivalPortraitCount)", rivalPortraitCount in 1150..1250)
        assertTrue("Expected ~20% initial badges (got $rivalInitialBadgeCount)", rivalInitialBadgeCount in 249..349)
    }

    @Test
    fun all22FeaturedPlayers_areStrictlyInTop100WithDistinctGenderMatchedAvatars() {
        val maleFeatured = listOf(
            "Aadarsh Sharma", "Sourabh Joshi", "Shubham Yadav", "Aditya Yadav",
            "Prince Yadav", "Satyam Yadav", "Pandit", "Yadav Ji", "Utkarsh Joshi",
            "Naitik Lucky", "Shivam", "Sushil", "Shashikant Dudhiya",
            "Kalu", "Ved Prakash", "Aryan Yadav", "Ayush"
        )
        val femaleFeatured = listOf(
            "Sabira Banu", "Komal", "Sofia", "Arya", "Rasiyan"
        )

        val state = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.STARTING_USER_SCORE,
            userName = "radheshyamyadav275140@gmail.com",
            userPhotoUri = "",
            userStreakDays = 1,
            totalDriftDays = 0,
            elapsedHours = 0L
        )

        // Gmail address must never appear on leaderboard
        assertFalse("@" in state.currentUserEntry.name)
        assertEquals("radheshyamyadav275140", state.currentUserEntry.name)

        val byName = state.entries.associateBy { it.name }
        assertFalse("Combined Sofia Arya must not exist", "Sofia Arya" in byName)
        assertFalse("Combined Pandit Yadav Ji must not exist", "Pandit Yadav Ji" in byName)

        val usedPortraits = mutableSetOf<Int>()
        val allowedMalePortraits = (0..15).toSet() + setOf(26, 27)
        val allowedFemalePortraits = (16..20).toSet() + setOf(28, 29)

        maleFeatured.forEach { name ->
            val entry = byName[name]
            assertNotNull("Missing male featured player $name", entry)
            assertTrue("Expected $name inside Top 100, was #${entry!!.rank}", entry.rank in 1..100)
            assertFalse("Expected $name to be marked male", entry.isFemale)
            val portrait = entry.portraitIndex
            assertNotNull("Expected distinct portrait for $name", portrait)
            assertTrue("Expected male portrait index for $name, got $portrait", portrait!! in allowedMalePortraits)
            assertTrue("Duplicate portrait index $portrait for $name", usedPortraits.add(portrait))
        }

        femaleFeatured.forEach { name ->
            val entry = byName[name]
            assertNotNull("Missing female featured player $name", entry)
            assertTrue("Expected $name inside Top 100, was #${entry!!.rank}", entry.rank in 1..100)
            assertTrue("Expected $name to be marked female", entry.isFemale)
            val portrait = entry.portraitIndex
            assertNotNull("Expected distinct portrait for $name", portrait)
            assertTrue("Expected female portrait index for $name, got $portrait", portrait!! in allowedFemalePortraits)
            assertTrue("Duplicate portrait index $portrait for $name", usedPortraits.add(portrait))
        }

        assertEquals(22, usedPortraits.size)
        assertEquals(26, byName["Pandit"]?.portraitIndex)
        assertEquals(27, byName["Yadav Ji"]?.portraitIndex)
        assertEquals(28, byName["Sofia"]?.portraitIndex)
        assertEquals(29, byName["Arya"]?.portraitIndex)
    }

    @Test
    fun podiumRotation_changesOnlyOneSeatEvery48Hours_andKeepsAllFeaturedInTop100() {
        val allFeaturedNames = setOf(
            "Aadarsh Sharma", "Sourabh Joshi", "Shubham Yadav", "Aditya Yadav",
            "Prince Yadav", "Satyam Yadav", "Pandit", "Yadav Ji", "Utkarsh Joshi",
            "Naitik Lucky", "Shivam", "Sushil", "Shashikant Dudhiya",
            "Kalu", "Ved Prakash", "Aryan Yadav", "Ayush",
            "Sabira Banu", "Komal", "Sofia", "Arya", "Rasiyan"
        )

        val cycle0 = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.STARTING_USER_SCORE,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 1,
            totalDriftDays = 0,
            elapsedHours = 0L
        )
        val cycle1 = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.STARTING_USER_SCORE,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 2,
            totalDriftDays = 2,
            elapsedHours = 48L
        )
        val cycle2 = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.STARTING_USER_SCORE,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 4,
            totalDriftDays = 4,
            elapsedHours = 96L
        )

        val p0 = cycle0.topThree.map { it.name }
        val p1 = cycle1.topThree.map { it.name }
        val p2 = cycle2.topThree.map { it.name }

        val diff01 = (0..2).count { p0[it] != p1[it] }
        val diff12 = (0..2).count { p1[it] != p2[it] }
        assertEquals("Only 1 podium position should change at cycle 1", 1, diff01)
        assertEquals("Only 1 podium position should change at cycle 2", 1, diff12)

        listOf(cycle0, cycle1, cycle2).forEach { st ->
            allFeaturedNames.forEach { featuredName ->
                val rank = st.entries.first { it.name == featuredName }.rank
                assertTrue("Featured player $featuredName dropped out of Top 100 to #$rank", rank in 1..100)
            }
            for (i in 0 until st.entries.size - 1) {
                assertTrue("Monotonic violation at $i", st.entries[i].score >= st.entries[i + 1].score)
            }
        }
    }

    @Test
    fun nonLinearCurve_earlyClimbIsFast_top100WallIsSteep() {
        val day0 = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.STARTING_USER_SCORE,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 1,
            totalDriftDays = 0
        )
        val earlyBoost = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.STARTING_USER_SCORE + 2_500L,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 4,
            totalDriftDays = 0
        )
        val earlyRankGain = day0.currentUserEntry.rank - earlyBoost.currentUserEntry.rank
        assertTrue("Expected fast early boost (>300 ranks), gained $earlyRankGain", earlyRankGain > 300)

        val nearTop100 = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = 36_000L,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 25,
            totalDriftDays = 0
        )
        val top100Plus2500 = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = 38_500L,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 25,
            totalDriftDays = 0
        )
        val endgameRankGain = nearTop100.currentUserEntry.rank - top100Plus2500.currentUserEntry.rank
        assertTrue(
            "Expected endgame wall near top 100 to move slowly (<15 ranks), moved $endgameRankGain",
            endgameRankGain in 1..14
        )
    }

    @Test
    fun rivalDrift_causesNaturalDropWhenInactive_andThroneSlipAlertTriggersFromTop3() {
        val activeDay = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = 2_000L,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 3,
            totalDriftDays = 0
        )
        val after5InactiveDays = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = 2_000L,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 1,
            totalDriftDays = 5
        )
        assertTrue(
            "Expected inactive user rank (${after5InactiveDays.currentUserEntry.rank}) to drop below active rank (${activeDay.currentUserEntry.rank})",
            after5InactiveDays.currentUserEntry.rank > activeDay.currentUserEntry.rank
        )

        val slippedFromThrone = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.rivalScoreForSlot(4, 0, 0) - 10L,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 1,
            totalDriftDays = 0,
            wasEverTopThree = true
        )
        assertTrue(slippedFromThrone.currentUserEntry.rank in 4..10)
        assertEquals(slippedFromThrone.currentUserEntry.rank, slippedFromThrone.throneSlipAlertRank)

        val stillOnPodium = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.rivalScoreForSlot(0, 0, 0) + 100L,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 7,
            totalDriftDays = 0,
            wasEverTopThree = true
        )
        assertEquals(1, stillOnPodium.currentUserEntry.rank)
        assertNull(stillOnPodium.throneSlipAlertRank)
    }

    @Test
    fun celebrationBuildersAndDailyCaps_areCleanAndSanitized() {
        val arenaCapped = ChampionsLeaderboardMath.arenaXpToPrestigeDelta(xpDelta = 10_000L, alreadyAwardedToday = 0L)
        assertTrue(arenaCapped <= 3_500L)
        assertEquals(0L, ChampionsLeaderboardMath.transactionPointsToAdd(alreadyAwardedToday = 360L))
        assertEquals(0L, ChampionsLeaderboardMath.vaultPointsToAdd(alreadyAwardedToday = 270L))
        assertEquals(0L, ChampionsLeaderboardMath.toolPointsToAdd(alreadyAwardedToday = 210L))

        val top100 = ChampionsLeaderboardMath.buildCelebrationForMilestone(100, 98)
        assertEquals(CelebrationKind.MILESTONE_TIER, top100.kind)
        assertTrue(top100.title.contains("Top 100"))

        val podium = ChampionsLeaderboardMath.buildCelebrationForMilestone(3, 3)
        assertEquals(CelebrationKind.ROYAL_PODIUM, podium.kind)

        val grand = ChampionsLeaderboardMath.buildCelebrationForMilestone(1, 1)
        assertEquals(CelebrationKind.GRAND_CHAMPION, grand.kind)
        assertTrue(grand.title.contains("RANK #1"))

        val state = ChampionsLeaderboardMath.buildLeaderboardState(
            userScore = ChampionsLeaderboardMath.STARTING_USER_SCORE,
            userName = "Player",
            userPhotoUri = "",
            userStreakDays = 1,
            totalDriftDays = 0
        )
        val bannedWords = listOf("bot", "dummy", "ai", "simulated")
        state.entries.forEach { entry ->
            assertFalse("@" in entry.name)
            val tokens = entry.name.lowercase().split(" ", "-")
            bannedWords.forEach { banned ->
                assertFalse("Banned token '$banned' found in ${entry.name}", banned in tokens)
            }
        }
        LeagueTier.entries.forEach { tier ->
            val lower = tier.title.lowercase()
            bannedWords.forEach { banned ->
                assertFalse(lower == banned)
            }
        }
    }
}
