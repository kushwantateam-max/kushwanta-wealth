package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.BudgetEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.DebtEntity
import com.example.data.model.FinancialBook
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppPreferences
import com.example.domain.calculator.FinanceCalculators
import com.example.ui.components.AchievementsModalSheet
import com.example.ui.components.ArenaProgressStore
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalGoldAccentColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StateGreen
import com.example.ui.viewmodel.KoshvantaViewModel

@Composable
fun ProfileScreen(
    viewModel: KoshvantaViewModel,
    budgets: List<BudgetEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    debts: List<DebtEntity>,
    transactions: List<TransactionWithDetails>,
    currentBook: FinancialBook,
    currentCurrency: CurrencyCode,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val accentColor = LocalAccentColor.current
    val goldAccent = LocalGoldAccentColor.current

    val lang by viewModel.appLanguage.collectAsState()
    val displayName by viewModel.profileDisplayName.collectAsState()
    val profileEmail by viewModel.profileEmail.collectAsState()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsState()
    val profileBio by viewModel.profileBio.collectAsState()
    val profileProfession by viewModel.profileProfession.collectAsState()
    val isGoogleConnected by viewModel.profileGoogleConnected.collectAsState()
    val isSignedIn by viewModel.isSignedIn.collectAsState()
    val isMemberSignedIn = isSignedIn || isGoogleConnected

    val streakCount by viewModel.streakCount.collectAsState()
    val bestStreak by viewModel.personalBestStreak.collectAsState()
    val activeLoginDates by viewModel.activeLoginDates.collectAsState()
    val lastLoginDate by viewModel.lastLoginDate.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showAchievementsSheet by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var pendingPickedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val arenaXp = remember(transactions.size, streakCount) {
        runCatching { ArenaProgressStore.loadSnapshot(context).totalXp }.getOrDefault(0L)
    }

    val challenge = remember(streakCount) {
        FinanceCalculators.evaluateStreakChallenge(streakCount, AppPreferences.TARGET_STREAK_CHALLENGE_DAYS)
    }

    val achievementsEval = remember(streakCount, transactions, budgets, savingsGoals, debts) {
        val catSpentMap = transactions
            .filter { it.transaction.type == TransactionType.EXPENSE }
            .groupBy { it.transaction.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.transaction.amount } }
        val exceededCount = budgets.count { b ->
            b.monthlyLimit > 0 && (catSpentMap[b.categoryId] ?: 0.0) >= b.monthlyLimit
        }
        val bestGoalRatio = savingsGoals.maxOfOrNull { g ->
            if (g.targetAmount > 0) (g.currentAmount / g.targetAmount).coerceIn(0.0, 1.0) else 0.0
        } ?: 0.0
        val bestIncompleteGoalRatio = savingsGoals
            .filter { it.targetAmount > 0 && it.currentAmount < it.targetAmount }
            .maxOfOrNull { g -> (g.currentAmount / g.targetAmount).coerceIn(0.0, 0.99) } ?: 0.0
        val completedGoals = savingsGoals.count { it.targetAmount > 0 && it.currentAmount >= it.targetAmount }
        val settledDebts = debts.count { it.isSettled }
        FinanceCalculators.evaluateAchievementsFull(
            streakDays = streakCount,
            txCount = transactions.size,
            budgetCount = budgets.size,
            exceededBudgetCount = exceededCount,
            bestGoalRatio = bestGoalRatio,
            completedGoalsCount = completedGoals,
            totalGoalsCount = savingsGoals.size,
            settledDebtsCount = settledDebts,
            totalDebtsCount = debts.size,
            bestIncompleteGoalRatio = bestIncompleteGoalRatio
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "profile_ring")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pendingPickedPhotoUri = uri
            if (!showEditDialog) {
                viewModel.updateEditableProfile(
                    displayName = displayName,
                    bio = profileBio,
                    profession = profileProfession,
                    pickedPhotoUri = uri
                )
            }
        }
    }

    if (showEditDialog) {
        var nameInput by remember(showEditDialog) { mutableStateOf(displayName) }
        var professionInput by remember(showEditDialog) { mutableStateOf(profileProfession) }
        var bioInput by remember(showEditDialog) { mutableStateOf(profileBio) }

        AlertDialog(
            onDismissRequest = {
                pendingPickedPhotoUri = null
                showEditDialog = false
            },
            title = { Text("Edit Wealth Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileAvatarImage(
                            photoUri = pendingPickedPhotoUri?.toString() ?: profilePhotoUri,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("profile_pick_photo_button")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Choose Photo", fontSize = 12.sp)
                        }
                    }

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_edit_name_input")
                    )
                    OutlinedTextField(
                        value = professionInput,
                        onValueChange = { professionInput = it },
                        label = { Text("Role / Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bioInput,
                        onValueChange = { bioInput = it },
                        label = { Text("Financial Goal / Bio") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateEditableProfile(
                            displayName = nameInput,
                            bio = bioInput,
                            profession = professionInput,
                            pickedPhotoUri = pendingPickedPhotoUri
                        )
                        pendingPickedPhotoUri = null
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier.testTag("profile_edit_save_button")
                ) {
                    Text("Save Profile", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingPickedPhotoUri = null
                        showEditDialog = false
                    }
                ) { Text("Cancel") }
            }
        )
    }

    if (showAchievementsSheet) {
        AchievementsModalSheet(
            streakDays = streakCount,
            bestStreakDays = bestStreak,
            activeLoginDates = activeLoginDates,
            lastLoginDate = lastLoginDate,
            budgets = budgets,
            savingsGoals = savingsGoals,
            debts = debts,
            transactions = transactions,
            onDismiss = { showAchievementsSheet = false }
        )
    }

    if (showDeleteAccountDialog) {
        val dangerCrimson = Color(0xFFFF3366)
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    text = "Delete Account & All Data?",
                    fontWeight = FontWeight.ExtraBold,
                    color = dangerCrimson
                )
            },
            text = {
                Text(
                    text = "Permanently delete your account (Google/Email link) and wipe all local financial & game data? This action cannot be undone.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.deleteAccountAndAllData(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = dangerCrimson),
                    modifier = Modifier.testTag("profile_confirm_delete_account_button")
                ) {
                    Text("Delete Forever", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("profile_hub_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header with Settings Gear Icon
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lang == "HI") "प्रोफ़ाइल हब" else "Profile Hub",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "${currentBook.title} Ledger • ${currentCurrency.flag} ${currentCurrency.code}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.size(48.dp)
                ) {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("profile_settings_gear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Open Settings",
                            tint = accentColor
                        )
                    }
                }
            }
        }

        // 2. Clean, Minimal Profile Card (Apple / Google / Wise understated style)
        item {
            val cardBg = if (isDark) Color(0xFF131720) else Color(0xFFFFFFFF)
            val cardBorder = if (isDark) Color.White.copy(alpha = 0.09f) else Color(0xFFCBD5E1)

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(1.dp, cardBorder),
                shadowElevation = if (isDark) 0.dp else 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) DarkCanvasBg else Color(0xFFF1F5F9))
                                    .clickable { showEditDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                ProfileAvatarImage(
                                    photoUri = profilePhotoUri,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = accentColor,
                                border = BorderStroke(2.dp, cardBg),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(26.dp)
                                    .clickable { showEditDialog = true }
                                    .testTag("profile_edit_avatar_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = displayName,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isMemberSignedIn && profileEmail.isNotBlank()) profileEmail else "Guest User",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (profileProfession.isNotBlank()) {
                                Text(
                                    text = profileProfession,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = accentColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Above-the-fold Ergonomic Action Row: Edit Profile + Sign in / Sign Out
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showEditDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("profile_edit_identity_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        if (isMemberSignedIn) {
                            OutlinedButton(
                                onClick = { viewModel.signOutToWelcome(context) },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, cardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("profile_sign_out_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Sign Out",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sign Out",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.signOutToWelcome(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("profile_sign_in_cta_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Sign in",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sign in",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Live Telemetry Stat Cards (Daily Streak, Total Logs, Wealth Rank)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    emoji = "🔥",
                    value = "${streakCount}d",
                    label = "Daily Streak",
                    sublabel = "Best ${bestStreak}d",
                    accent = goldAccent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_stat_streak")
                )
                ProfileStatCard(
                    emoji = "📊",
                    value = "${transactions.size}",
                    label = "Total Logs",
                    sublabel = "${budgets.size} Budgets",
                    accent = accentColor,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_stat_logs")
                )
                ProfileStatCard(
                    emoji = "🏆",
                    value = "Lv.${challenge.level}",
                    label = challenge.rankTitle,
                    sublabel = if (arenaXp > 0) "$arenaXp XP" else "${achievementsEval.totalTrophiesEarned} Trophies",
                    accent = StateGreen,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_stat_rank")
                )
            }
        }

        // 4. Achievement Badges Section
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = goldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Achievement Badges", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = "${achievementsEval.totalTrophiesEarned} Trophies • ${challenge.completedDays}/14 cycle",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        TextButton(
                            onClick = { showAchievementsSheet = true },
                            modifier = Modifier.testTag("profile_view_achievements_button")
                        ) {
                            Text("View Locker", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Streak Level Progress
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = goldAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Level ${challenge.level} • ${challenge.rankTitle}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text("${challenge.progressPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = goldAccent)
                        }
                        LinearProgressIndicator(
                            progress = { challenge.progressRatio },
                            color = goldAccent,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(999.dp))
                        )
                    }

                    achievementsEval.activeBadges.take(4).forEach { badge ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAchievementsSheet = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    val badgeEmoji = when (badge.iconName) {
                                        "streak" -> "🔥"
                                        "tx" -> "⚡"
                                        "budget" -> "🛡️"
                                        "goal" -> "🎯"
                                        "debt" -> "⚖️"
                                        else -> "🏆"
                                    }
                                    Text(badgeEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(badge.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            text = badge.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (badge.tiersCleared > 0) goldAccent.copy(alpha = 0.16f) else accentColor.copy(alpha = 0.14f)
                                ) {
                                    Text(
                                        text = if (badge.tiersCleared > 0) "★ ${badge.tiersCleared}" else "Active",
                                        color = if (badge.tiersCleared > 0) goldAccent else accentColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}

@Composable
private fun ProfileStatCard(
    emoji: String,
    value: String,
    label: String,
    sublabel: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(emoji, fontSize = 18.sp)
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = sublabel,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ProfileAvatarImage(
    photoUri: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (photoUri.isNotBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(photoUri)
                .crossfade(true)
                .error(R.drawable.koshvanta_official_logo)
                .fallback(R.drawable.koshvanta_official_logo)
                .build(),
            contentDescription = "User Profile Avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.koshvanta_official_logo),
            contentDescription = "Koshvanta Brand Avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}
