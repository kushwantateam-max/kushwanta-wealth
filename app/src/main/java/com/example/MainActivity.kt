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
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.screens.ProfileAvatarImage
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WelcomeAuthScreen
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.KoshvantaTheme
import com.example.ui.theme.LocalAccentColor
import com.example.ui.viewmodel.KoshvantaViewModel

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
                    KoshvantaApp(viewModel = viewModel)
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
fun KoshvantaApp(viewModel: KoshvantaViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedScreen by remember { mutableIntStateOf(0) }
    var isSettingsSubPageOpen by remember { mutableStateOf(false) }
    var isFullScreenArenaGame by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
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

    LaunchedEffect(hasCompletedOnboardingAuth) {
        if (!hasCompletedOnboardingAuth) {
            selectedScreen = 0
            isSettingsSubPageOpen = false
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

    BackHandler(enabled = selectedScreen == 4 && isSettingsSubPageOpen && !isAppLocked) {
        isSettingsSubPageOpen = false
    }

    BackHandler(enabled = selectedScreen != 0 && !(selectedScreen == 4 && isSettingsSubPageOpen) && !isAppLocked) {
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
                if (!isFullScreenArenaGame) {
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
                if (!isFullScreenArenaGame) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("bottom_navigation_bar")
                    ) {
                    NavigationBarItem(
                        selected = selectedScreen == 0,
                        onClick = { selectedScreen = 0 },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text(AppStrings.home(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )

                    NavigationBarItem(
                        selected = selectedScreen == 1,
                        onClick = { selectedScreen = 1 },
                        icon = { Icon(Icons.Default.PieChart, contentDescription = "Budgets") },
                        label = { Text(AppStrings.budgets(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_budgets")
                    )

                    NavigationBarItem(
                        selected = selectedScreen == 2,
                        onClick = { selectedScreen = 2 },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Accounts") },
                        label = { Text(AppStrings.accounts(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_accounts")
                    )

                    NavigationBarItem(
                        selected = selectedScreen == 3,
                        onClick = { selectedScreen = 3 },
                        icon = { Icon(Icons.Default.Calculate, contentDescription = "Tools") },
                        label = { Text(AppStrings.tools(lang), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f)),
                        modifier = Modifier.testTag("nav_item_calculators")
                    )

                    NavigationBarItem(
                        selected = selectedScreen == 4,
                        onClick = {
                            if (selectedScreen == 4 && isSettingsSubPageOpen) {
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
                                        width = if (selectedScreen == 4) 1.8.dp else 1.dp,
                                        color = if (selectedScreen == 4) accentColor else MaterialTheme.colorScheme.outline,
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
                modifier = if (isFullScreenArenaGame) {
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
                    modifier = if (isFullScreenArenaGame) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .widthIn(max = 840.dp)
                    }
                ) {
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
                            onNavigateToBudgets = { selectedScreen = 1 }
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
                            onFullScreenGameActiveChanged = { isFullScreenArenaGame = it }
                        )
                        4 -> if (isSettingsSubPageOpen) {
                            SettingsScreen(
                                viewModel = viewModel,
                                accounts = accounts,
                                transactions = transactions,
                                currentBook = currentBook,
                                currentCurrency = currentCurrency,
                                onBackClick = { isSettingsSubPageOpen = false }
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
                                onOpenSettings = { isSettingsSubPageOpen = true }
                            )
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

        // Security Shield Gate & Lock Overlay (stable Compose mount; zero auto-biometric trigger)
        if (!isSecurityReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkCanvasBg)
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

