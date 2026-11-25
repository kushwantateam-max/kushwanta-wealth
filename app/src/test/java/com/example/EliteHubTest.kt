package com.example

import com.example.data.prefs.AppStrings
import com.example.data.prefs.EliteHubConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EliteHubTest {

    @Test
    fun verifiedWhatsAppUrlsMatchExactProductionConstants() {
        assertEquals(
            "https://whatsapp.com/channel/0029Vb9CDf2Lo4hd9hFarY33",
            EliteHubConfig.OFFICIAL_WHATSAPP_CHANNEL_URL
        )
        assertEquals(
            "https://chat.whatsapp.com/C916CTYtlPgDNXPAsO9YIJ",
            EliteHubConfig.KUSHWANTA_VIP_COMMUNITY_URL
        )
        assertNotEquals(
            EliteHubConfig.OFFICIAL_WHATSAPP_CHANNEL_URL,
            EliteHubConfig.KUSHWANTA_VIP_COMMUNITY_URL
        )
        assertTrue(EliteHubConfig.OFFICIAL_WHATSAPP_CHANNEL_URL.startsWith("https://"))
        assertTrue(EliteHubConfig.KUSHWANTA_VIP_COMMUNITY_URL.startsWith("https://"))
    }

    @Test
    fun bilingualEnAndHiStringsAreCompleteAndDistinct() {
        val enPairs = listOf(
            AppStrings.eliteHubTitle("EN") to AppStrings.eliteHubTitle("HI"),
            AppStrings.eliteHubEntryTitle("EN") to AppStrings.eliteHubEntryTitle("HI"),
            AppStrings.eliteHubEntrySubtitle("EN") to AppStrings.eliteHubEntrySubtitle("HI"),
            AppStrings.eliteHubHeroHeadline("EN") to AppStrings.eliteHubHeroHeadline("HI"),
            AppStrings.eliteHubHeroSubtitle("EN") to AppStrings.eliteHubHeroSubtitle("HI"),
            AppStrings.eliteHubPrimaryTitle("EN") to AppStrings.eliteHubPrimaryTitle("HI"),
            AppStrings.eliteHubPrimaryCta("EN") to AppStrings.eliteHubPrimaryCta("HI"),
            AppStrings.eliteHubSecondaryTitle("EN") to AppStrings.eliteHubSecondaryTitle("HI"),
            AppStrings.eliteHubSecondaryCta("EN") to AppStrings.eliteHubSecondaryCta("HI"),
            AppStrings.eliteHubBenefitGameTitle("EN") to AppStrings.eliteHubBenefitGameTitle("HI"),
            AppStrings.eliteHubBenefitWealthTitle("EN") to AppStrings.eliteHubBenefitWealthTitle("HI"),
            AppStrings.eliteHubBenefitUpdatesTitle("EN") to AppStrings.eliteHubBenefitUpdatesTitle("HI"),
            AppStrings.eliteHubPillarWealthTitle("EN") to AppStrings.eliteHubPillarWealthTitle("HI"),
            AppStrings.eliteHubPillarArenaTitle("EN") to AppStrings.eliteHubPillarArenaTitle("HI"),
            AppStrings.eliteHubTrustHeader("EN") to AppStrings.eliteHubTrustHeader("HI"),
            AppStrings.eliteHubUrlErrorFallback("EN") to AppStrings.eliteHubUrlErrorFallback("HI")
        )
        enPairs.forEach { (en, hi) ->
            assertTrue(en.isNotBlank())
            assertTrue(hi.isNotBlank())
            assertNotEquals(en, hi)
        }
    }
}
