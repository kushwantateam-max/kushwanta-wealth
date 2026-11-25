package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.R
import com.example.data.prefs.AppStrings
import com.example.data.prefs.EliteHubConfig
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMint
import com.example.ui.theme.RoyalGold

private val WhatsAppSubtleBrand = Color(0xFF16A34A)
private val WhatsAppSubtleBrandDark = Color(0xFF25D366)

@Composable
fun EliteHubScreen(
    lang: String,
    onBackClick: () -> Unit,
    onNavigateToWealthOs: () -> Unit,
    onNavigateToMindArena: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val accentColor = LocalAccentColor.current

    val cardBg = if (isDark) Color(0xFF131720) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.09f) else Color(0xFFCBD5E1)
    val subtleSurface = if (isDark) Color(0xFF1B202C) else Color(0xFFF1F5F9)
    val primaryButtonBg = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val primaryButtonFg = if (isDark) Color(0xFF0B0C10) else Color(0xFFFFFFFF)
    val whatsappTint = if (isDark) WhatsAppSubtleBrandDark else WhatsAppSubtleBrand

    val openUrl: (String) -> Unit = { url ->
        EliteHubConfig.launchExternalCommunityUrl(
            context = context,
            rawUrl = url,
            onError = {
                onShowMessage(AppStrings.eliteHubUrlErrorFallback(lang))
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("elite_hub_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Clean Top Navigation Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = cardBg,
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier.size(48.dp)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("elite_hub_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppStrings.eliteHubBackContentDesc(lang),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.eliteHubTitle(lang),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "KUSHWANTA ECOSYSTEM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = subtleSurface,
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = AppStrings.eliteHubOfficialBadge(lang),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 2. Hero Identity Card
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(1.dp, cardBorder),
                shadowElevation = if (isDark) 0.dp else 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("elite_hub_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(if (isDark) DarkCanvasBg else Color(0xFFF1F5F9))
                                .border(1.5.dp, cardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.koshvanta_official_logo),
                                contentDescription = AppStrings.eliteHubTitle(lang),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "KUSHWANTA • ELITE HUB",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp,
                                color = accentColor
                            )
                            Text(
                                text = AppStrings.eliteHubHeroHeadline(lang),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = AppStrings.eliteHubHeroSubtitle(lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = AppStrings.eliteHubHeroDescription(lang),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 3. Primary CTA Card — Official WhatsApp Channel
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(1.2.dp, if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFF94A3B8)),
                shadowElevation = if (isDark) 0.dp else 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("elite_hub_official_channel_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = whatsappTint.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, whatsappTint.copy(alpha = 0.28f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = whatsappTint,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = AppStrings.eliteHubPrimaryBadge(lang),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
                                    color = whatsappTint
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = AppStrings.eliteHubPrimaryTitle(lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = AppStrings.eliteHubPrimaryDesc(lang),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { openUrl(EliteHubConfig.OFFICIAL_WHATSAPP_CHANNEL_URL) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryButtonBg,
                            contentColor = primaryButtonFg
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("elite_hub_official_channel_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = whatsappTint,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.eliteHubPrimaryCta(lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // 4. Secondary CTA Card — Kushwanta VIP Community
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(1.dp, cardBorder),
                shadowElevation = if (isDark) 0.dp else 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("elite_hub_vip_community_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = subtleSurface,
                            border = BorderStroke(1.dp, cardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = whatsappTint,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = AppStrings.eliteHubSecondaryBadge(lang),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    EliteVipHeroShowcase(
                        isDark = isDark,
                        cardBorder = cardBorder,
                        whatsappTint = whatsappTint
                    )

                    Text(
                        text = AppStrings.eliteHubSecondaryTitle(lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = AppStrings.eliteHubSecondaryDesc(lang),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { openUrl(EliteHubConfig.KUSHWANTA_VIP_COMMUNITY_URL) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, cardBorder),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("elite_hub_vip_community_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = if (isDark) RoyalGold else Color(0xFFB45309),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.eliteHubSecondaryCta(lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // 5. Elite Benefits Section (Strictly Informational)
        item {
            EliteSectionHeader(
                title = AppStrings.eliteHubBenefitsHeader(lang),
                accentColor = accentColor
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                EliteBenefitCard(
                    icon = Icons.Default.SportsEsports,
                    title = AppStrings.eliteHubBenefitGameTitle(lang),
                    description = AppStrings.eliteHubBenefitGameDesc(lang),
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    subtleSurface = subtleSurface,
                    accentColor = accentColor,
                    modifier = Modifier.testTag("elite_hub_benefit_game")
                )
                EliteBenefitCard(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    title = AppStrings.eliteHubBenefitWealthTitle(lang),
                    description = AppStrings.eliteHubBenefitWealthDesc(lang),
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    subtleSurface = subtleSurface,
                    accentColor = accentColor,
                    modifier = Modifier.testTag("elite_hub_benefit_wealth")
                )
                EliteBenefitCard(
                    icon = Icons.Default.Notifications,
                    title = AppStrings.eliteHubBenefitUpdatesTitle(lang),
                    description = AppStrings.eliteHubBenefitUpdatesDesc(lang),
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    subtleSurface = subtleSurface,
                    accentColor = accentColor,
                    modifier = Modifier.testTag("elite_hub_benefit_updates")
                )
            }
        }

        // 6. Ecosystem Pillars Section (Smart Wealth OS + Mind Arena)
        item {
            EliteSectionHeader(
                title = AppStrings.eliteHubPillarsHeader(lang),
                accentColor = accentColor
            )
        }

        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val useSideBySide = maxWidth >= 440.dp
                if (useSideBySide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ElitePillarCard(
                            icon = Icons.Default.Calculate,
                            title = AppStrings.eliteHubPillarWealthTitle(lang),
                            description = AppStrings.eliteHubPillarWealthDesc(lang),
                            actionLabel = AppStrings.eliteHubPillarWealthAction(lang),
                            onActionClick = onNavigateToWealthOs,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            subtleSurface = subtleSurface,
                            accentColor = accentColor,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("elite_hub_pillar_wealth_button")
                        )
                        ElitePillarCard(
                            icon = Icons.Default.SportsEsports,
                            title = AppStrings.eliteHubPillarArenaTitle(lang),
                            description = AppStrings.eliteHubPillarArenaDesc(lang),
                            actionLabel = AppStrings.eliteHubPillarArenaAction(lang),
                            onActionClick = onNavigateToMindArena,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            subtleSurface = subtleSurface,
                            accentColor = accentColor,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("elite_hub_pillar_arena_button")
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ElitePillarCard(
                            icon = Icons.Default.Calculate,
                            title = AppStrings.eliteHubPillarWealthTitle(lang),
                            description = AppStrings.eliteHubPillarWealthDesc(lang),
                            actionLabel = AppStrings.eliteHubPillarWealthAction(lang),
                            onActionClick = onNavigateToWealthOs,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            subtleSurface = subtleSurface,
                            accentColor = accentColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("elite_hub_pillar_wealth_button")
                        )
                        ElitePillarCard(
                            icon = Icons.Default.SportsEsports,
                            title = AppStrings.eliteHubPillarArenaTitle(lang),
                            description = AppStrings.eliteHubPillarArenaDesc(lang),
                            actionLabel = AppStrings.eliteHubPillarArenaAction(lang),
                            onActionClick = onNavigateToMindArena,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            subtleSurface = subtleSurface,
                            accentColor = accentColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("elite_hub_pillar_arena_button")
                        )
                    }
                }
            }
        }

        // 7. Community Trust & Transparency Section
        item {
            EliteSectionHeader(
                title = AppStrings.eliteHubTrustHeader(lang),
                accentColor = accentColor
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("elite_hub_trust_section")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val trustPoints = listOf(
                        AppStrings.eliteHubTrustOfficial(lang),
                        AppStrings.eliteHubTrustRespect(lang),
                        AppStrings.eliteHubTrustPrivacy(lang),
                        AppStrings.eliteHubTrustEducation(lang),
                        AppStrings.eliteHubTrustGuidelines(lang)
                    )
                    trustPoints.forEachIndexed { index, point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = point,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (index < trustPoints.lastIndex) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = cardBorder.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}

@Composable
fun EliteHubEntryCard(
    lang: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val accentColor = LocalAccentColor.current
    val cardBg = if (isDark) Color(0xFF131720) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.09f) else Color(0xFFCBD5E1)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        shadowElevation = if (isDark) 0.dp else 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EliteEntryAvatarCluster(isDark = isDark)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = AppStrings.eliteHubEntryTitle(lang),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = AppStrings.eliteHubOfficialBadge(lang),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                }
                Text(
                    text = AppStrings.eliteHubEntrySubtitle(lang),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = AppStrings.eliteHubTitle(lang),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EliteSectionHeader(
    title: String,
    accentColor: Color
) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.1.sp,
        color = accentColor,
        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
    )
}

@Composable
private fun EliteBenefitCard(
    icon: ImageVector,
    title: String,
    description: String,
    cardBg: Color,
    cardBorder: Color,
    subtleSurface: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(subtleSurface)
                    .border(1.dp, cardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ElitePillarCard(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    cardBg: Color,
    cardBorder: Color,
    subtleSurface: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onActionClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(subtleSurface)
                        .border(1.dp, cardBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = actionLabel,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun EliteEntryAvatarCluster(
    isDark: Boolean
) {
    val cyanRing = NeonMint
    val goldRing = RoyalGold
    val platinumRing = if (isDark) Color(0xFFE2E8F0) else Color(0xFF94A3B8)
    val badgeBg = if (isDark) Color(0xFF1A2232) else Color(0xFFFFFBEB)
    val badgeBorder = if (isDark) RoyalGold.copy(alpha = 0.5f) else Color(0xFFD97706).copy(alpha = 0.55f)
    val badgeTextColor = if (isDark) RoyalGold else Color(0xFF92400E)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.testTag("elite_hub_entry_avatar_cluster")
    ) {
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(30.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            EliteCircularAvatar(
                drawableRes = R.drawable.img_avatar_portrait_1,
                borderColor = cyanRing,
                borderWidth = 1.5.dp,
                size = 26.dp,
                glowColor = cyanRing,
                isDark = isDark,
                modifier = Modifier
                    .offset(x = 0.dp)
                    .zIndex(1f)
            )
            EliteCircularAvatar(
                drawableRes = R.drawable.img_avatar_portrait_2,
                borderColor = goldRing,
                borderWidth = 1.5.dp,
                size = 26.dp,
                glowColor = goldRing,
                isDark = isDark,
                modifier = Modifier
                    .offset(x = 16.dp)
                    .zIndex(2f)
            )
            EliteCircularAvatar(
                drawableRes = R.drawable.img_avatar_portrait_3,
                borderColor = platinumRing,
                borderWidth = 1.5.dp,
                size = 26.dp,
                glowColor = platinumRing,
                isDark = isDark,
                modifier = Modifier
                    .offset(x = 32.dp)
                    .zIndex(3f)
            )
        }

        Surface(
            shape = RoundedCornerShape(999.dp),
            color = badgeBg,
            border = BorderStroke(1.dp, badgeBorder)
        ) {
            Text(
                text = "+2K",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = badgeTextColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EliteVipHeroShowcase(
    isDark: Boolean,
    cardBorder: Color,
    whatsappTint: Color
) {
    val showcaseBg = if (isDark) Color(0xFF0E131D) else Color(0xFFF8FAFC)
    val showcaseBorder = if (isDark) RoyalGold.copy(alpha = 0.28f) else cardBorder
    val goldAccent = if (isDark) RoyalGold else Color(0xFFD97706)
    val cyanSideBorder = if (isDark) NeonMint else Color(0xFF0891B2)
    val platinumSideBorder = if (isDark) Color(0xFFE2E8F0) else Color(0xFF64748B)
    val verifiedBg = if (isDark) Color(0xFF12231B) else Color(0xFFECFDF5)
    val verifiedTextColor = if (isDark) Color(0xFFECFDF5) else Color(0xFF0F172A)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = showcaseBg,
        border = BorderStroke(1.dp, showcaseBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("elite_hub_vip_hero_showcase")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(68.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                EliteCircularAvatar(
                    drawableRes = R.drawable.img_avatar_portrait_4,
                    borderColor = cyanSideBorder,
                    borderWidth = 1.6.dp,
                    size = 44.dp,
                    glowColor = cyanSideBorder,
                    isDark = isDark,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 6.dp, y = (-2).dp)
                        .zIndex(1f)
                )

                EliteCircularAvatar(
                    drawableRes = R.drawable.img_avatar_portrait_5,
                    borderColor = platinumSideBorder,
                    borderWidth = 1.6.dp,
                    size = 44.dp,
                    glowColor = platinumSideBorder,
                    isDark = isDark,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-6).dp, y = (-2).dp)
                        .zIndex(1f)
                )

                EliteCircularAvatar(
                    drawableRes = R.drawable.img_avatar_portrait_1,
                    borderColor = RoyalGold,
                    borderWidth = 2.2.dp,
                    size = 56.dp,
                    glowColor = RoyalGold,
                    isDark = isDark,
                    glowElevation = if (isDark) 10.dp else 4.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(3f)
                )

                Surface(
                    shape = CircleShape,
                    color = if (isDark) Color(0xFF131720) else Color(0xFFFFFFFF),
                    border = BorderStroke(1.2.dp, goldAccent),
                    shadowElevation = if (isDark) 4.dp else 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 1.dp)
                        .size(20.dp)
                        .zIndex(4f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = goldAccent,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = verifiedBg,
                border = BorderStroke(1.dp, whatsappTint.copy(alpha = if (isDark) 0.38f else 0.42f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(whatsappTint)
                    )
                    Text(
                        text = "Verified Global Community",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = verifiedTextColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun EliteCircularAvatar(
    drawableRes: Int,
    borderColor: Color,
    borderWidth: Dp,
    size: Dp,
    glowColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    glowElevation: Dp = if (isDark) 5.dp else 2.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = glowElevation,
                shape = CircleShape,
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .clip(CircleShape)
            .background(if (isDark) Color(0xFF131720) else Color(0xFFFFFFFF))
            .border(borderWidth, borderColor, CircleShape)
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}
