package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.domain.leaderboard.CelebrationKind
import com.example.domain.leaderboard.LeaderboardCelebration
import com.example.domain.leaderboard.LeaderboardEntry
import com.example.domain.leaderboard.LeaderboardUiState
import com.example.domain.leaderboard.LeagueTier
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMint
import com.example.ui.theme.RoyalGold
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val ObsidianDeep = Color(0xFF05070D)
private val ObsidianCard = Color(0xFF0E131D)
private val ObsidianElevated = Color(0xFF141B29)
private val LightSlateBorder = Color(0xFFE2E8F0)
private val LightTitleInk = Color(0xFF0F172A)
private val LightSubtitleInk = Color(0xFF475569)

private val gmailInitialColors = listOf(
    Color(0xFF1A73E8),
    Color(0xFF0F9D58),
    Color(0xFFD93025),
    Color(0xFFE37400),
    Color(0xFF7E57C2),
    Color(0xFF00897B),
    Color(0xFFC2185B),
    Color(0xFF0288D1)
)

private val portraitDrawables = listOf(
    // 0..15 -> 16 distinct youthful Indian male portraits
    R.drawable.img_avatar_boy_1,
    R.drawable.img_avatar_boy_2,
    R.drawable.img_avatar_boy_3,
    R.drawable.img_avatar_boy_4,
    R.drawable.img_avatar_boy_5,
    R.drawable.img_avatar_boy_6,
    R.drawable.img_avatar_boy_7,
    R.drawable.img_avatar_boy_8,
    R.drawable.img_avatar_boy_9,
    R.drawable.img_avatar_boy_10,
    R.drawable.img_avatar_boy_11,
    R.drawable.img_avatar_boy_12,
    R.drawable.img_avatar_boy_13,
    R.drawable.img_avatar_boy_14,
    R.drawable.img_avatar_boy_15,
    R.drawable.img_avatar_boy_16,
    // 16..20 -> 5 distinct youthful Indian female portraits
    R.drawable.img_avatar_girl_1,
    R.drawable.img_avatar_girl_2,
    R.drawable.img_avatar_girl_3,
    R.drawable.img_avatar_girl_4,
    R.drawable.img_avatar_girl_5,
    // 21..25 -> fallback gender-matched portraits for remaining Top-100 slots
    R.drawable.img_avatar_portrait_1,
    R.drawable.img_avatar_portrait_2,
    R.drawable.img_avatar_portrait_3,
    R.drawable.img_avatar_portrait_4,
    R.drawable.img_avatar_portrait_5,
    // 26..29 -> bespoke portraits for Pandit, Yadav Ji, Sofia, Arya
    R.drawable.img_avatar_pandit,
    R.drawable.img_avatar_yadav_ji,
    R.drawable.img_avatar_sofia,
    R.drawable.img_avatar_arya
)

@Composable
fun LeaderboardScreen(
    state: LeaderboardUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    var selectedTier by remember { mutableStateOf<LeagueTier?>(null) }
    var dismissSlipBanner by remember(state.throneSlipAlertRank) { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val numberFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    val showPodium = selectedTier == null || selectedTier == LeagueTier.GRANDMASTER
    val listEntries = remember(state.entries, selectedTier) {
        when (selectedTier) {
            null -> state.entries.drop(3)
            LeagueTier.GRANDMASTER -> state.entries.filter { it.leagueTier == LeagueTier.GRANDMASTER && it.rank > 3 }
            else -> state.entries.filter { it.leagueTier == selectedTier }
        }
    }

    val scrollToCurrentUser: () -> Unit = {
        coroutineScope.launch {
            val userRank = state.currentUserEntry.rank
            if (selectedTier != null && state.currentUserEntry.leagueTier != selectedTier) {
                selectedTier = null
            }
            val targetIdx = if (selectedTier == null || selectedTier == LeagueTier.GRANDMASTER) {
                if (userRank <= 3) 0 else (userRank - 4 + 1).coerceAtLeast(0)
            } else {
                val idxInTier = state.entries
                    .filter { it.leagueTier == selectedTier }
                    .indexOfFirst { it.isCurrentUser }
                idxInTier.coerceAtLeast(0)
            }
            listState.animateScrollToItem(targetIdx.coerceAtLeast(0))
        }
    }

    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                listOf(ObsidianDeep, Color(0xFF080D18), ObsidianDeep)
            )
        } else {
            Brush.verticalGradient(
                listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9), Color(0xFFF8FAFC))
            )
        }
    }

    val primaryText = if (isDark) Color(0xFFF8FAFC) else LightTitleInk
    val secondaryText = if (isDark) Color(0xFF94A3B8) else LightSubtitleInk
    val goldAccent = if (isDark) RoyalGold else Color(0xFFB45309)
    val cyanAccent = if (isDark) NeonMint else Color(0xFF00695C)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("leaderboard_screen_root")
    ) {
        // Subtle ambient glow backdrop in Dark Mode / crisp light sheen in Light Mode
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (isDark) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(RoyalGold.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.12f),
                        radius = size.width * 0.72f
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.12f),
                    radius = size.width * 0.72f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonMint.copy(alpha = 0.09f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.25f),
                        radius = size.width * 0.55f
                    ),
                    center = Offset(size.width * 0.88f, size.height * 0.25f),
                    radius = size.width * 0.55f
                )
            } else {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFEF3C7).copy(alpha = 0.65f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.10f),
                        radius = size.width * 0.70f
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.10f),
                    radius = size.width * 0.70f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Top Navigation Header ---
            Row(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBackClick,
                    shape = CircleShape,
                    color = if (isDark) ObsidianElevated else Color.White,
                    border = BorderStroke(
                        1.dp,
                        if (isDark) NeonMint.copy(alpha = 0.25f) else LightSlateBorder
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("leaderboard_back_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = primaryText,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kushwanta Champions",
                        color = primaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = goldAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Global Hall • ${numberFormatter.format(state.totalParticipants)} Players",
                            color = secondaryText,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    onClick = scrollToCurrentUser,
                    shape = RoundedCornerShape(18.dp),
                    color = if (isDark) ObsidianElevated else Color.White,
                    border = BorderStroke(
                        1.dp,
                        if (isDark) NeonMint.copy(alpha = 0.45f) else cyanAccent.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("leaderboard_jump_to_me_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Locate My Rank",
                            tint = cyanAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "#${numberFormatter.format(state.currentUserEntry.rank)}",
                            color = cyanAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // --- League Tier Filter Strip ---
            Row(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LeagueTierTabPill(
                    label = "All",
                    subtitle = "1-1,500",
                    tierColor = cyanAccent,
                    selected = selectedTier == null,
                    isDark = isDark,
                    onClick = { selectedTier = null },
                    modifier = Modifier.testTag("leaderboard_tier_all")
                )
                LeagueTier.entries.forEach { tier ->
                    val tierColor = tierAccentColor(tier, isDark)
                    val isSelected = selectedTier == tier
                    LeagueTierTabPill(
                        label = tier.title,
                        subtitle = "${tier.minRank}-${numberFormatter.format(tier.maxRank)}",
                        tierColor = tierColor,
                        selected = isSelected,
                        isDark = isDark,
                        onClick = { selectedTier = tier },
                        modifier = Modifier.testTag("leaderboard_tier_${tier.name.lowercase()}")
                    )
                }
            }

            // --- Throne Slip Alert Banner (Motivating Challenge when slipping from Top 3 to #4–#10) ---
            val slipRank = state.throneSlipAlertRank
            AnimatedVisibility(
                visible = slipRank != null && !dismissSlipBanner,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (slipRank != null) {
                    val slipBorder = if (isDark) {
                        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))
                    } else {
                        Brush.linearGradient(listOf(Color(0xFFD97706), Color(0xFFDC2626)))
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDark) Color(0xFF1A1416) else Color(0xFFFFFBEB),
                        border = BorderStroke(1.2.dp, slipBorder),
                        shadowElevation = if (isDark) 6.dp else 2.dp,
                        modifier = Modifier
                            .widthIn(max = 760.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("leaderboard_throne_slip_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "\u2694\uFE0F Throne Challenged! You're now #${numberFormatter.format(slipRank)} \u2014 Reclaim your Top 3 Crown",
                                color = if (isDark) Color(0xFFFEF3C7) else Color(0xFF0F172A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { dismissSlipBanner = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss Alert",
                                    tint = secondaryText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // --- Main Scrollable Stage (3D Royal Podium + Rankings List) ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 6.dp,
                        bottom = 112.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    if (showPodium && state.topThree.size >= 3) {
                        item(key = "royal_podium_stage") {
                            RoyalPodiumStage(
                                first = state.topThree[0],
                                second = state.topThree[1],
                                third = state.topThree[2],
                                isDark = isDark,
                                numberFormatter = numberFormatter
                            )
                        }
                    }

                    items(
                        items = listEntries,
                        key = { entry ->
                            if (entry.isCurrentUser) "current_user_row" else "rival_${entry.name}"
                        }
                    ) { entry ->
                        LeaderboardRankRow(
                            entry = entry,
                            isDark = isDark,
                            numberFormatter = numberFormatter
                        )
                    }
                }

                // --- Sticky Bottom "My Live Rank" Card ---
                StickyMyRankCard(
                    entry = state.currentUserEntry,
                    isDark = isDark,
                    numberFormatter = numberFormatter,
                    onClick = scrollToCurrentUser,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .widthIn(max = 760.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun LeagueTierTabPill(
    label: String,
    subtitle: String,
    tierColor: Color,
    selected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        selected && isDark -> tierColor.copy(alpha = 0.18f)
        selected && !isDark -> Color.White
        isDark -> ObsidianCard
        else -> Color(0xFFF8FAFC)
    }
    val borderColor = when {
        selected -> tierColor.copy(alpha = if (isDark) 0.85f else 0.75f)
        isDark -> Color.White.copy(alpha = 0.08f)
        else -> LightSlateBorder
    }
    val titleColor = when {
        selected -> if (isDark) Color.White else LightTitleInk
        isDark -> Color(0xFFCBD5E1)
        else -> LightSubtitleInk
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(if (selected) 1.4.dp else 1.dp, borderColor),
        shadowElevation = if (!isDark && selected) 3.dp else 0.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(tierColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    color = titleColor,
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    color = if (selected) tierColor else (if (isDark) Color(0xFF64748B) else Color(0xFF64748B)),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RoyalPodiumStage(
    first: LeaderboardEntry,
    second: LeaderboardEntry,
    third: LeaderboardEntry,
    isDark: Boolean,
    numberFormatter: NumberFormat
) {
    val infiniteTransition = rememberInfiniteTransition(label = "podium_crown_glow")
    val crownPulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crown_pulse"
    )

    val containerBg = if (isDark) ObsidianCard.copy(alpha = 0.92f) else Color.White
    val containerBorder = if (isDark) {
        Brush.linearGradient(
            listOf(RoyalGold.copy(alpha = 0.45f), NeonMint.copy(alpha = 0.35f), RoyalGold.copy(alpha = 0.35f))
        )
    } else {
        Brush.linearGradient(
            listOf(Color(0xFFE2E8F0), Color(0xFFFDE68A), Color(0xFFE2E8F0))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 6.dp)
            .shadow(
                elevation = if (isDark) 12.dp else 6.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = if (isDark) RoyalGold.copy(alpha = 0.25f) else Color(0xFFCBD5E1),
                spotColor = if (isDark) NeonMint.copy(alpha = 0.2f) else Color(0xFFCBD5E1)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(containerBg)
            .border(1.2.dp, containerBorder, RoundedCornerShape(26.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .testTag("leaderboard_royal_podium")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Rank #2 (Left - Platinum Silver)
            PodiumColumn(
                entry = second,
                pedestalHeight = 92.dp,
                avatarSize = 54.dp,
                medalColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF64748B),
                glowAlpha = 0.22f,
                isFirstPlace = false,
                isDark = isDark,
                numberFormatter = numberFormatter,
                modifier = Modifier.weight(1f)
            )

            // Rank #1 (Center - Royal Gold Crown)
            PodiumColumn(
                entry = first,
                pedestalHeight = 122.dp,
                avatarSize = 68.dp,
                medalColor = if (isDark) RoyalGold else Color(0xFFD97706),
                glowAlpha = 0.26f * crownPulse,
                isFirstPlace = true,
                isDark = isDark,
                numberFormatter = numberFormatter,
                modifier = Modifier.weight(1.12f)
            )

            // Rank #3 (Right - Warm Bronze)
            PodiumColumn(
                entry = third,
                pedestalHeight = 76.dp,
                avatarSize = 52.dp,
                medalColor = if (isDark) Color(0xFFE08E45) else Color(0xFFB45309),
                glowAlpha = 0.20f,
                isFirstPlace = false,
                isDark = isDark,
                numberFormatter = numberFormatter,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: LeaderboardEntry,
    pedestalHeight: Dp,
    avatarSize: Dp,
    medalColor: Color,
    glowAlpha: Float,
    isFirstPlace: Boolean,
    isDark: Boolean,
    numberFormatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    val nameColor = if (isDark) Color(0xFFF8FAFC) else LightTitleInk
    val scoreColor = if (isFirstPlace) medalColor else (if (isDark) NeonMint else Color(0xFF00695C))
    val pedestalTop = if (isDark) {
        medalColor.copy(alpha = if (isFirstPlace) 0.26f else 0.16f)
    } else {
        medalColor.copy(alpha = if (isFirstPlace) 0.18f else 0.11f)
    }
    val pedestalBottom = if (isDark) ObsidianElevated else Color(0xFFF8FAFC)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isFirstPlace) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "Rank 1 Crown",
                tint = medalColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        // Circular Avatar with metallic ring + rank badge
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .size(avatarSize + 8.dp)
                    .clip(CircleShape)
                    .background(medalColor.copy(alpha = glowAlpha)),
                contentAlignment = Alignment.Center
            ) {
                PlayerAvatarBadge(
                    entry = entry,
                    size = avatarSize,
                    ringColor = medalColor,
                    ringWidth = if (isFirstPlace) 2.5.dp else 2.dp
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = medalColor,
                shadowElevation = 4.dp,
                modifier = Modifier.offset(y = 6.dp)
            ) {
                Text(
                    text = "#${entry.rank}",
                    color = if (isDark && entry.rank <= 2) Color(0xFF05070D) else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = entry.name,
            color = nameColor,
            fontSize = if (isFirstPlace) 13.sp else 12.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3D Elevated Pedestal Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(Brush.verticalGradient(listOf(pedestalTop, pedestalBottom)))
                .border(
                    width = 1.dp,
                    color = medalColor.copy(alpha = if (isDark) 0.55f else 0.45f),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
                )
                .padding(horizontal = 6.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = numberFormatter.format(entry.score),
                    color = scoreColor,
                    fontSize = if (isFirstPlace) 14.sp else 12.5.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(fontFeatureSettings = "tnum"),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Color.Black.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.85f),
                    border = BorderStroke(0.8.dp, medalColor.copy(alpha = 0.30f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${entry.streakDays}d",
                            color = nameColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRankRow(
    entry: LeaderboardEntry,
    isDark: Boolean,
    numberFormatter: NumberFormat
) {
    val tierColor = tierAccentColor(entry.leagueTier, isDark)
    val cardBg = when {
        entry.isCurrentUser && isDark -> Color(0xFF0D1D24)
        entry.isCurrentUser && !isDark -> Color(0xFFF0FDF4)
        isDark -> ObsidianCard
        else -> Color.White
    }
    val borderBrush = when {
        entry.isCurrentUser && isDark -> Brush.linearGradient(listOf(NeonMint, RoyalGold))
        entry.isCurrentUser && !isDark -> Brush.linearGradient(listOf(Color(0xFF00695C), Color(0xFFD97706)))
        isDark -> Brush.linearGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.04f)))
        else -> Brush.linearGradient(listOf(LightSlateBorder, LightSlateBorder))
    }

    val nameColor = if (isDark) Color(0xFFF8FAFC) else LightTitleInk
    val subColor = if (isDark) Color(0xFF94A3B8) else LightSubtitleInk
    val scoreColor = if (entry.isCurrentUser) {
        if (isDark) NeonMint else Color(0xFF00695C)
    } else {
        if (isDark) Color(0xFFF8FAFC) else LightTitleInk
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = cardBg,
        border = BorderStroke(if (entry.isCurrentUser) 1.5.dp else 1.dp, borderBrush),
        shadowElevation = if (!isDark) 2.dp else 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(if (entry.isCurrentUser) "leaderboard_user_row" else "leaderboard_row_${entry.rank}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Box(
                modifier = Modifier.widthIn(min = 52.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "#${numberFormatter.format(entry.rank)}",
                    color = if (entry.rank <= 25) tierColor else subColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(fontFeatureSettings = "tnum"),
                    maxLines = 1
                )
            }

            PlayerAvatarBadge(
                entry = entry,
                size = 42.dp,
                ringColor = tierColor.copy(alpha = 0.85f),
                ringWidth = 1.5.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.name,
                        color = nameColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (entry.isCurrentUser) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) NeonMint.copy(alpha = 0.18f) else Color(0xFF00695C).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "YOU",
                                color = if (isDark) NeonMint else Color(0xFF00695C),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.leagueTier.title,
                        color = tierColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " • ${entry.regionBadge}",
                        color = subColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${entry.streakDays}d",
                        color = subColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = numberFormatter.format(entry.score),
                    color = scoreColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(fontFeatureSettings = "tnum"),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun StickyMyRankCard(
    entry: LeaderboardEntry,
    isDark: Boolean,
    numberFormatter: NumberFormat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tierColor = tierAccentColor(entry.leagueTier, isDark)
    val cardBg = if (isDark) Color(0xF50B111E) else Color.White
    val borderBrush = if (isDark) {
        Brush.linearGradient(listOf(NeonMint, RoyalGold))
    } else {
        Brush.linearGradient(listOf(Color(0xFF00695C), Color(0xFFD97706)))
    }
    val primaryText = if (isDark) Color(0xFFF8FAFC) else LightTitleInk
    val secondaryText = if (isDark) Color(0xFF94A3B8) else LightSubtitleInk
    val scoreColor = if (isDark) RoyalGold else Color(0xFFB45309)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        border = BorderStroke(1.6.dp, borderBrush),
        shadowElevation = if (isDark) 14.dp else 10.dp,
        modifier = modifier.testTag("leaderboard_my_live_rank_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerAvatarBadge(
                entry = entry,
                size = 46.dp,
                ringColor = if (isDark) NeonMint else Color(0xFF00695C),
                ringWidth = 2.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.name,
                        color = primaryText,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tierColor.copy(alpha = if (isDark) 0.18f else 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = tierColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = entry.leagueTier.title,
                                color = tierColor,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rank #${numberFormatter.format(entry.rank)}",
                        color = if (isDark) NeonMint else Color(0xFF00695C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        style = TextStyle(fontFeatureSettings = "tnum")
                    )
                    Text(
                        text = "  •  ",
                        color = secondaryText,
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "My Streak",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${entry.streakDays}d",
                        color = secondaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = numberFormatter.format(entry.score),
                    color = scoreColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(fontFeatureSettings = "tnum")
                )
            }
        }
    }
}

@Composable
private fun PlayerAvatarBadge(
    entry: LeaderboardEntry,
    size: Dp,
    ringColor: Color,
    ringWidth: Dp
) {
    val isDark = LocalIsDarkTheme.current
    val context = LocalContext.current
    val gmailColor = gmailInitialColors[entry.avatarColorIndex.mod(gmailInitialColors.size)]
    val userGlowBrush = remember(isDark) {
        Brush.linearGradient(
            if (isDark) listOf(RoyalGold, NeonMint)
            else listOf(Color(0xFFB45309), Color(0xFF00695C))
        )
    }
    var photoLoadFailed by remember(entry.photoUri) { mutableStateOf(false) }

    val badgeModifier = if (entry.isCurrentUser) {
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(gmailColor)
            .border(ringWidth + 0.5.dp, userGlowBrush, CircleShape)
            .padding(1.5.dp)
            .clip(CircleShape)
    } else {
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(gmailColor)
            .border(ringWidth, ringColor, CircleShape)
    }

    Box(
        modifier = badgeModifier,
        contentAlignment = Alignment.Center
    ) {
        if (entry.isCurrentUser) {
            Text(
                text = entry.initials,
                color = Color.White,
                fontSize = (size.value * 0.35f).sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            if (entry.photoUri.isNotBlank() && !photoLoadFailed) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(entry.photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${entry.name} Profile Photo",
                    contentScale = ContentScale.Crop,
                    onError = { photoLoadFailed = true },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        } else {
            val portraitIdx = entry.portraitIndex
            if (portraitIdx != null) {
                val resId = portraitDrawables[portraitIdx.mod(portraitDrawables.size)]
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "${entry.name} Portrait",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                val badgeText = if (entry.rank % 2 == 0) {
                    entry.initials.take(1)
                } else {
                    entry.initials
                }
                Text(
                    text = badgeText,
                    color = Color.White,
                    fontSize = (size.value * if (badgeText.length == 1) 0.42f else 0.34f).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

private fun tierAccentColor(tier: LeagueTier, isDark: Boolean): Color {
    return when (tier) {
        LeagueTier.GRANDMASTER -> if (isDark) RoyalGold else Color(0xFFB45309)
        LeagueTier.DIAMOND -> if (isDark) NeonMint else Color(0xFF0284C7)
        LeagueTier.GOLD -> if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)
        LeagueTier.SILVER -> if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
        LeagueTier.BRONZE -> if (isDark) Color(0xFFD97706) else Color(0xFF9A3412)
    }
}

@Composable
fun LeaderboardCelebrationDialog(
    celebration: LeaderboardCelebration,
    onContinue: () -> Unit,
    onViewLeaderboard: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val infiniteTransition = rememberInfiniteTransition(label = "celebration_halo")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebration_pulse"
    )

    val isGrand = celebration.kind == CelebrationKind.GRAND_CHAMPION
    val isPodium = celebration.kind == CelebrationKind.ROYAL_PODIUM
    val primaryAccent = when {
        isGrand || isPodium -> if (isDark) RoyalGold else Color(0xFFD97706)
        else -> if (isDark) NeonMint else Color(0xFF00695C)
    }
    val secondaryAccent = if (isDark) NeonMint else Color(0xFF0284C7)
    val cardBg = if (isDark) Color(0xFF0D121D) else Color(0xFFFFFFFF)
    val borderBrush = Brush.linearGradient(
        if (isGrand || isPodium) {
            listOf(RoyalGold, secondaryAccent, RoyalGold)
        } else {
            listOf(primaryAccent, RoyalGold)
        }
    )
    val titleInk = if (isDark) Color(0xFFF8FAFC) else LightTitleInk
    val subtitleInk = if (isDark) Color(0xFFCBD5E1) else LightSubtitleInk

    Dialog(
        onDismissRequest = onContinue,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = if (isDark) 0.74f else 0.45f))
                .padding(horizontal = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(initialScale = 0.88f) + fadeIn()
            ) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = cardBg,
                    border = BorderStroke(1.6.dp, borderBrush),
                    shadowElevation = if (isDark) 20.dp else 14.dp,
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth()
                        .testTag("leaderboard_celebration_modal")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(86.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            primaryAccent.copy(alpha = 0.34f * pulse),
                                            secondaryAccent.copy(alpha = 0.12f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .border(2.dp, borderBrush, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isGrand || isPodium) {
                                    Icons.Default.WorkspacePremium
                                } else {
                                    Icons.Default.EmojiEvents
                                },
                                contentDescription = celebration.badgeLabel,
                                tint = primaryAccent,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = primaryAccent.copy(alpha = if (isDark) 0.16f else 0.12f),
                            border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.45f))
                        ) {
                            Text(
                                text = "${celebration.badgeLabel}  •  ${celebration.leagueName.uppercase()}",
                                color = primaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = celebration.title,
                            color = titleInk,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 25.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = celebration.subtitle,
                            color = subtitleInk,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onViewLeaderboard,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryAccent,
                                contentColor = if (isDark) Color(0xFF05070D) else Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("celebration_view_leaderboard_button")
                        ) {
                            Text(
                                text = "View Leaderboard",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onContinue,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isDark) Color.White.copy(alpha = 0.18f) else LightSlateBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("celebration_continue_button")
                        ) {
                            Text(
                                text = "Continue",
                                color = titleInk,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
