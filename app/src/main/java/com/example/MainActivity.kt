package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ads.AdManager
import com.example.data.prefs.AppStrings
import com.example.ui.components.AddAccountDialog
import com.example.ui.components.AddBudgetDialog
import com.example.ui.components.AddGoalDialog
import com.example.ui.components.GlowingNeonMicButton
import com.example.ui.components.LockScreenOverlay
import com.example.ui.components.QuickEntryBottomSheet
import com.example.ui.components.TransferDialog
import com.example.ui.components.VoiceTransactionBottomSheet
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EliteHubScreen
import com.example.ui.screens.LeaderboardCelebrationDialog
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileAvatarImage
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WelcomeAuthScreen
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.KoshvantaTheme
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMint
import com.example.ui.theme.RoyalGold
import com.example.ui.viewmodel.KoshvantaViewModel
import java.text.NumberFormat
import java.util.Locale

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdManager.init(this)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        setContent {
            val viewModel: KoshvantaViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val accentPalette by viewModel.accentPalette.collectAsStateWithLifecycle()
            val largeTextMode by viewModel.largeTextAccessibility.collectAsStateWithLifecycle()

            KoshvantaTheme(
                themeMode = themeMode,
                accentPalette = accentPalette,
                largeTextMode = largeTextMode
            ) {
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(1100L)
                    showSplash = false
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    KoshvantaApp(viewModel = viewModel, isSplashVisible = showSplash)
                    AnimatedVisibility(
                        visible = showSplash,
                        exit = fadeOut(animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing))
                    ) {
                        KushwantaAnimatedSplashOverlay()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AdManager.setCurrentActivity(this)
    }

    override fun onDestroy() {
        AdManager.setCurrentActivity(null)
        super.onDestroy()
    }
}

@Composable
private fun KushwantaAnimatedSplashOverlay() {
    val logoScale = remember { Animatable(0.76f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvasBg)
            .testTag("kushwanta_animated_splash"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(260.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00FFCC).copy(alpha = 0.22f * contentAlpha.value),
                        Color(0xFFFFD700).copy(alpha = 0.10f * contentAlpha.value),
                        Color.Transparent
                    )
                ),
                radius = size.minDimension * 0.5f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .scale(logoScale.value)
                .alpha(contentAlpha.value)
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(DarkCanvasBg)
                    .border(
                        width = 2.4.dp,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.koshvanta_official_logo),
                    contentDescription = "Kushwanta Brand Emblem",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
            Text(
                text = "KUSHWANTA",
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.2.sp,
                    fontSize = 22.sp
                )
            )
        }
    }
}

@Composable
fun KoshvantaApp(
    viewModel: KoshvantaViewModel = viewModel(),
    isSplashVisible: Boolean = false
) {
    val context = LocalContext.current
    var selectedScreen by remember { mutableIntStateOf(0) }
    var isSettingsSubPageOpen by remember { mutableStateOf(false) }
    var isEliteHubOpen by remember { mutableStateOf(false) }
    var isLeaderboardOpen by remember { mutableStateOf(false) }
    var showFloatingRankPill by remember { mutableStateOf(false) }
    var requestedCalculatorsToolId by remember { mutableStateOf<String?>(null) }
    var isFullScreenArenaGame by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val accentColor = LocalAccentColor.current

    LaunchedEffect(selectedScreen) {
        if (selectedScreen != 3 && isFullScreenArenaGame) {
            isFullScreenArenaGame = false
        }
        if (selectedScreen != 4 && isSettingsSubPageOpen) {
            isSettingsSubPageOpen = false
        }
    }

    val accounts by viewModel.accounts.collectAsStateWithLifecycle(initialValue = emptyList())
    val transactions by viewModel.transactionsWithDetails.collectAsStateWithLifecycle(initialValue = emptyList())
    val budgets by viewModel.currentMonthBudgets.collectAsStateWithLifecycle(initialValue = emptyList())
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle(initialValue = emptyList())
    val bills by viewModel.billReminders.collectAsStateWithLifecycle(initialValue = emptyList())
    val debts by viewModel.debts.collectAsStateWithLifecycle(initialValue = emptyList())
    val presets by viewModel.quickPresets.collectAsStateWithLifecycle(initialValue = emptyList())
    val notifications by viewModel.notificationHistory.collectAsStateWithLifecycle(initialValue = emptyList())
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle(initialValue = emptyList())

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentBook by viewModel.currentBook.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    val lang by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isSecurityReady by viewModel.isSecurityReady.collectAsStateWithLifecycle()
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsStateWithLifecycle()
    val hasCompletedOnboardingAuth by viewModel.hasCompletedOnboardingAuth.collectAsStateWithLifecycle()
    val leaderboardState by viewModel.leaderboardState.collectAsStateWithLifecycle()
    val hasShownSessionLeaderboardPill by viewModel.hasShownSessionLeaderboardPill.collectAsStateWithLifecycle()
    val pendingCelebration by viewModel.pendingCelebration.collectAsStateWithLifecycle()

    LaunchedEffect(
        isSplashVisible,
        hasCompletedOnboardingAuth,
        isSecurityReady,
        isAppLocked,
        selectedScreen,
        isLeaderboardOpen,
        isEliteHubOpen,
        hasShownSessionLeaderboardPill
    ) {
        if (!isSplashVisible &&
            hasCompletedOnboardingAuth &&
            isSecurityReady &&
            !isAppLocked &&
            selectedScreen == 0 &&
            !isLeaderboardOpen &&
            !isEliteHubOpen &&
            !hasShownSessionLeaderboardPill
        ) {
            viewModel.refreshLeaderboardState()
            viewModel.markSessionLeaderboardPillShown()
            showFloatingRankPill = true
        } else if (isLeaderboardOpen || isEliteHubOpen || selectedScreen != 0 || !hasCompletedOnboardingAuth || isAppLocked) {
            showFloatingRankPill = false
        }
    }

    LaunchedEffect(showFloatingRankPill) {
        if (showFloatingRankPill) {
            delay(5000L)
            showFloatingRankPill = false
        }
    }

    LaunchedEffect(hasCompletedOnboardingAuth) {
        if (!hasCompletedOnboardingAuth) {
            selectedScreen = 0
            isSettingsSubPageOpen = false
            isEliteHubOpen = false
            isLeaderboardOpen = false
            showFloatingRankPill = false
            requestedCalculatorsToolId = null
            isFullScreenArenaGame = false
        }
    }

    val isQuickEntryOpen by viewModel.isQuickEntrySheetOpen.collectAsStateWithLifecycle()
    val isVoiceSheetOpen by viewModel.isVoiceSheetOpen.collectAsStateWithLifecycle()
    val isTransferOpen by viewModel.isTransferDialogOpen.collectAsStateWithLifecycle()
    val isAddBudgetOpen by viewModel.isAddBudgetDialogOpen.collectAsStateWithLifecycle()
    val isAddGoalOpen by viewModel.isAddGoalDialogOpen.collectAsStateWithLifecycle()
    val isAddAccountOpen by viewModel.isAddAccountDialogOpen.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        viewModel.openVoiceEntry()
    }

    val triggerVoiceEntry: () -> Unit = {
        val granted = try {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } catch (_: Throwable) {
            false
        }
        hasAudioPermission = granted
        if (granted) {
            viewModel.openVoiceEntry()
        } else {
            try {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } catch (_: Throwable) {
                viewModel.openVoiceEntry()
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkInDailyLogin()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(enabled = isLeaderboardOpen && !isAppLocked) {
        isLeaderboardOpen = false
    }

    BackHandler(enabled = !isLeaderboardOpen && isEliteHubOpen && !isAppLocked) {
        isEliteHubOpen = false
    }

    BackHandler(enabled = !isLeaderboardOpen && !isEliteHubOpen && selectedScreen == 4 && isSettingsSubPageOpen && !isAppLocked) {
        isSettingsSubPageOpen = false
    }

    BackHandler(enabled = !isLeaderboardOpen && !isEliteHubOpen && selectedScreen != 0 && !(selectedScreen == 4 && isSettingsSubPageOpen) && !isAppLocked) {
        selectedScreen = 0
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            if (msg.contains("Undo available")) {
                val res = snackbarHostState.showSnackbar(
                    message = msg,
                    actionLabel = "UNDO",
                    duration = SnackbarDuration.Short
                )
                if (res == SnackbarResult.ActionPerformed) {
                    viewModel.undoLastDelete()
                }
            } else {
                snackbarHostState.showSnackbar(msg)
            }
            viewModel.clearToast()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!hasCompletedOnboardingAuth) {
            WelcomeAuthScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                if (!isFullScreenArenaGame && !isEliteHubOpen && !isLeaderboardOpen) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlowingNeonMicButton(onClick = triggerVoiceEntry)
                        FloatingActionButton(
                            onClick = { viewModel.openQuickEntry() },
                            containerColor = accentColor,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("floating_quick_add_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Quick Add Transaction",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (!isFullScreenArenaGame && !isLeaderboardOpen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_navigation_bar")
                    ) {
                    NavigationBarItem(
                        selected = !isEliteHubOpen && selectedScreen == 0,
                        onClick = {
                            isEliteHubOpen = false
                            selectedScreen = 0
                        },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text(AppStrings.home(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )

                    NavigationBarItem(
                        selected = !isEliteHubOpen && selectedScreen == 1,
                        onClick = {
                            isEliteHubOpen = false
                            selectedScreen = 1
                        },
                        icon = { Icon(Icons.Default.PieChart, contentDescription = "Budgets") },
                        label = { Text(AppStrings.budgets(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_budgets")
                    )

                    NavigationBarItem(
                        selected = !isEliteHubOpen && selectedScreen == 2,
                        onClick = {
                            isEliteHubOpen = false
                            selectedScreen = 2
                        },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Accounts") },
                        label = { Text(AppStrings.accounts(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_accounts")
                    )

                    NavigationBarItem(
                        selected = !isEliteHubOpen && selectedScreen == 3,
                        onClick = {
                            isEliteHubOpen = false
                            selectedScreen = 3
                        },
                        icon = { Icon(Icons.Default.Calculate, contentDescription = "Tools") },
                        label = { Text(AppStrings.tools(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_calculators")
                    )

                    NavigationBarItem(
                        selected = (isEliteHubOpen || selectedScreen == 4),
                        onClick = {
                            if (isEliteHubOpen) {
                                isEliteHubOpen = false
                                selectedScreen = 4
                            } else if (selectedScreen == 4 && isSettingsSubPageOpen) {
                                isSettingsSubPageOpen = false
                            } else {
                                selectedScreen = 4
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (isEliteHubOpen || selectedScreen == 4) 1.8.dp else 1.dp,
                                        color = if (isEliteHubOpen || selectedScreen == 4) accentColor else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                            ) {
                                ProfileAvatarImage(
                                    photoUri = profilePhotoUri,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                        },
                        label = { Text(AppStrings.profile(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = if (isFullScreenArenaGame || isLeaderboardOpen) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(innerPadding)
                },
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = if (isFullScreenArenaGame || isLeaderboardOpen) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .widthIn(max = 840.dp)
                    }
                ) {
                    if (isLeaderboardOpen) {
                        LeaderboardScreen(
                            state = leaderboardState,
                            onBackClick = { isLeaderboardOpen = false }
                        )
                    } else if (isEliteHubOpen) {
                        EliteHubScreen(
                            lang = lang,
                            onBackClick = { isEliteHubOpen = false },
                            onNavigateToWealthOs = {
                                requestedCalculatorsToolId = null
                                isEliteHubOpen = false
                                selectedScreen = 3
                            },
                            onNavigateToMindArena = {
                                requestedCalculatorsToolId = "mind_arena"
                                isEliteHubOpen = false
                                selectedScreen = 3
                            },
                            onShowMessage = { msg ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    } else {
                        when (selectedScreen) {
                            0 -> DashboardScreen(
                                viewModel = viewModel,
                                accounts = accounts,
                                transactions = transactions,
                                budgets = budgets,
                                bills = bills,
                                presets = presets,
                                savingsGoals = savingsGoals,
                                debts = debts,
                                notifications = notifications,
                                currentBook = currentBook,
                                currentCurrency = currentCurrency,
                                onNavigateToAccounts = { selectedScreen = 2 },
                                onOpenAccountInVault = { accountId ->
                                    viewModel.requestOpenCardInVault(accountId)
                                    selectedScreen = 2
                                },
                                onNavigateToBudgets = { selectedScreen = 1 },
                                onOpenLeaderboard = {
                                    viewModel.refreshLeaderboardState()
                                    isLeaderboardOpen = true
                                }
                            )
                            1 -> BudgetsScreen(
                                viewModel = viewModel,
                                budgets = budgets,
                                categories = uiState.categories,
                                transactions = transactions,
                                onAddBudgetClick = { viewModel.openAddBudgetDialog() }
                            )
                            2 -> AccountsScreen(
                                viewModel = viewModel,
                                accounts = accounts,
                                transactions = transactions,
                                onAddAccountClick = { viewModel.openAddAccountDialog() },
                                onTransferClick = { viewModel.openTransferDialog() }
                            )
                            3 -> CalculatorsScreen(
                                viewModel = viewModel,
                                accounts = accounts,
                                transactions = transactions,
                                budgets = budgets,
                                savingsGoals = savingsGoals,
                                debts = debts,
                                auditLogs = auditLogs,
                                onAddGoalClick = { viewModel.openAddGoalDialog() },
                                onFullScreenGameActiveChanged = { isFullScreenArenaGame = it },
                                initialToolId = requestedCalculatorsToolId,
                                onInitialToolConsumed = { requestedCalculatorsToolId = null }
                            )
                            4 -> if (isSettingsSubPageOpen) {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    accounts = accounts,
                                    transactions = transactions,
                                    currentBook = currentBook,
                                    currentCurrency = currentCurrency,
                                    onBackClick = { isSettingsSubPageOpen = false },
                                    onOpenEliteHub = { isEliteHubOpen = true }
                                )
                            } else {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    budgets = budgets,
                                    savingsGoals = savingsGoals,
                                    debts = debts,
                                    transactions = transactions,
                                    currentBook = currentBook,
                                    currentCurrency = currentCurrency,
                                    onOpenSettings = { isSettingsSubPageOpen = true },
                                    onOpenEliteHub = { isEliteHubOpen = true }
                                )
                            }
                        }
                    }
                }

                if (isQuickEntryOpen) {
                    QuickEntryBottomSheet(
                        viewModel = viewModel,
                        accounts = accounts,
                        categories = uiState.categories,
                        onOpenVoiceEntry = {
                            viewModel.closeQuickEntry()
                            triggerVoiceEntry()
                        },
                        onDismiss = { viewModel.closeQuickEntry() }
                    )
                }

                if (isVoiceSheetOpen) {
                    VoiceTransactionBottomSheet(
                        viewModel = viewModel,
                        accounts = accounts,
                        categories = uiState.categories,
                        hasAudioPermission = hasAudioPermission,
                        onRequestAudioPermission = {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        onDismiss = { viewModel.closeVoiceEntry() }
                    )
                }

                if (isTransferOpen) {
                    TransferDialog(
                        viewModel = viewModel,
                        accounts = accounts,
                        onDismiss = { viewModel.closeTransferDialog() }
                    )
                }

                if (isAddBudgetOpen) {
                    AddBudgetDialog(
                        viewModel = viewModel,
                        categories = uiState.categories,
                        onDismiss = { viewModel.closeAddBudgetDialog() }
                    )
                }

                if (isAddGoalOpen) {
                    AddGoalDialog(
                        viewModel = viewModel,
                        onDismiss = { viewModel.closeAddGoalDialog() }
                    )
                }

                if (isAddAccountOpen) {
                    AddAccountDialog(
                        viewModel = viewModel,
                        onDismiss = { viewModel.closeAddAccountDialog() }
                    )
                }
            }
        }

        // Luxury 5-second Auto-Hiding Floating In-App Rank Pill (below status bar, once per session)
        val isDarkPill = LocalIsDarkTheme.current
        val formattedPillRank = remember(leaderboardState.currentUserEntry.rank) {
            NumberFormat.getNumberInstance(Locale.US).format(leaderboardState.currentUserEntry.rank)
        }
        AnimatedVisibility(
            visible = showFloatingRankPill &&
                hasCompletedOnboardingAuth &&
                isSecurityReady &&
                !isAppLocked &&
                !isLeaderboardOpen &&
                !isEliteHubOpen &&
                selectedScreen == 0,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                initialOffsetY = { -it }
            ) + fadeIn(animationSpec = tween(durationMillis = 320)),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
                targetOffsetY = { -it }
            ) + fadeOut(animationSpec = tween(durationMillis = 280)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(50f)
                .statusBarsPadding()
                .padding(top = 10.dp, start = 16.dp, end = 16.dp)
        ) {
            val pillBorder = remember(isDarkPill) {
                Brush.linearGradient(
                    if (isDarkPill) listOf(RoyalGold, NeonMint)
                    else listOf(Color(0xFFD97706), Color(0xFF00695C))
                )
            }
            Surface(
                onClick = {
                    showFloatingRankPill = false
                    viewModel.refreshLeaderboardState()
                    isLeaderboardOpen = true
                },
                shape = RoundedCornerShape(24.dp),
                color = if (isDarkPill) Color(0xEB0A0F1A) else Color(0xFAFFFFFF),
                border = BorderStroke(1.3.dp, pillBorder),
                shadowElevation = if (isDarkPill) 14.dp else 10.dp,
                modifier = Modifier
                    .defaultMinSize(minHeight = 44.dp)
                    .testTag("home_floating_rank_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "\uD83C\uDFC6 Global Arena Rank: #$formattedPillRank \u2022 Tap to view League",
                        color = if (isDarkPill) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Open Leaderboard",
                        tint = if (isDarkPill) NeonMint else Color(0xFF00695C),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Smart Milestone & Podium Celebration Popup (once per milestone)
        val activeCelebration = pendingCelebration
        if (activeCelebration != null &&
            !isSplashVisible &&
            hasCompletedOnboardingAuth &&
            isSecurityReady &&
            !isAppLocked
        ) {
            LeaderboardCelebrationDialog(
                celebration = activeCelebration,
                onContinue = { viewModel.dismissPendingCelebration() },
                onViewLeaderboard = {
                    viewModel.dismissPendingCelebration()
                    viewModel.refreshLeaderboardState()
                    isLeaderboardOpen = true
                }
            )
        }

        // Security Shield Gate & Lock Overlay (stable Compose mount; zero auto-biometric trigger)
        if (!isSecurityReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("security_loading_shield")
            )
        } else if (isAppLocked) {
            LockScreenOverlay(
                isBiometricEnabled = isBiometricEnabled,
                onVerifyPin = { pin, onResult -> viewModel.verifyPinAsync(pin, onResult) },
                onBiometricSuccess = { viewModel.unlockViaBiometric() },
                onKeyTick = { viewModel.playKeypadTick() }
            )
        }
        }
    }
}

