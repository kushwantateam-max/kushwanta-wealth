package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.KoshvantaDatabase
import com.example.data.model.AccentPalette
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.AuditLogEntity
import com.example.data.model.BillReminderEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.DebtEntity
import com.example.data.model.EmojiMatcher
import com.example.data.model.FinancialBook
import com.example.data.model.NotificationHistoryEntity
import com.example.data.model.QuickPresetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.ThemeMode
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppPreferences
import com.example.data.prefs.SecurityLockState
import com.example.data.repository.BillToggleResult
import com.example.data.repository.KoshvantaRepository
import com.example.domain.calculator.FinanceCalculators
import com.example.domain.calculator.HealthScoreResult
import com.example.domain.util.EmailAuthOutcome
import com.example.domain.util.GoogleSignInOutcome
import com.example.domain.util.NotificationHelper
import com.example.domain.util.PasswordResetOutcome
import com.example.domain.util.ProfileSyncManager
import com.example.domain.util.ReportExporter
import com.example.domain.util.SoundEffectManager
import com.example.domain.voice.VoiceParseResult
import com.example.domain.voice.VoiceTransactionParser
import com.example.ui.components.CountryCardPreset
import com.example.ui.components.MetalCardFinish
import com.example.ui.components.MetalCardVaultStore
import com.example.ui.components.NetWorthCurveCalculator
import com.example.ui.components.NetWorthCurvePoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.SharedPreferences
import com.example.domain.leaderboard.ChampionsLeaderboardEngine
import com.example.domain.leaderboard.ChampionsLeaderboardMath
import com.example.domain.leaderboard.LeaderboardCelebration
import com.example.domain.leaderboard.LeaderboardUiState
import java.text.NumberFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale

data class KoshvantaUiState(
    val currentBook: FinancialBook = FinancialBook.PERSONAL,
    val currentCurrency: CurrencyCode = CurrencyCode.USD,
    val categories: List<CategoryEntity> = emptyList(),
    val healthScore: HealthScoreResult = HealthScoreResult(85, "Excellent", "Surplus balance maintained."),
    val filterTimeframe: String = "Monthly",
    val isQuickEntrySheetOpen: Boolean = false,
    val isTransferDialogOpen: Boolean = false,
    val isAddBudgetDialogOpen: Boolean = false,
    val isAddGoalDialogOpen: Boolean = false,
    val isAddAccountDialogOpen: Boolean = false,
    val toastMessage: String? = null
)

data class VoiceSaveOutcome(
    val amountUsd: Double,
    val categoryName: String,
    val categoryCreated: Boolean,
    val walletName: String,
    val walletCreated: Boolean,
    val transactionType: TransactionType,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalCoroutinesApi::class)
class KoshvantaViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val db = KoshvantaDatabase.getInstance(application)
    val prefs = AppPreferences(application)
    private val repository = KoshvantaRepository(db.dao(), prefs)
    private val championsEngine = ChampionsLeaderboardEngine(application)
    private val arenaPrefs: SharedPreferences =
        appContext.getSharedPreferences("kushwanta_mind_arena_prefs", Context.MODE_PRIVATE)
    private val inFlightBillIds: MutableSet<Long> = Collections.synchronizedSet(mutableSetOf())

    private val _currentBook = MutableStateFlow(FinancialBook.PERSONAL)
    val currentBook: StateFlow<FinancialBook> = _currentBook.asStateFlow()

    val currentCurrency: StateFlow<CurrencyCode> = prefs.currency
    val customCurrencySymbol: StateFlow<String> = prefs.customCurrencySymbol
    val themeMode: StateFlow<ThemeMode> = prefs.themeMode
    val accentPalette: StateFlow<AccentPalette> = prefs.accentPalette
    val appLanguage: StateFlow<String> = prefs.language
    val isPinEnabled: StateFlow<Boolean> = prefs.isPinEnabled
    val isBiometricEnabled: StateFlow<Boolean> = prefs.isBiometricEnabled
    val alertThresholdPercent: StateFlow<Int> = prefs.alertThresholdPercent
    val dailyReminderEnabled: StateFlow<Boolean> = prefs.dailyReminderEnabled
    val soundEffectsEnabled: StateFlow<Boolean> = prefs.soundEffectsEnabled
    val budgetRolloverEnabled: StateFlow<Boolean> = prefs.budgetRolloverEnabled
    val dailySpendingLimitUsd: StateFlow<Double> = prefs.dailySpendingLimitUsd
    val weeklyBudgetLimitUsd: StateFlow<Double> = prefs.weeklyBudgetLimitUsd
    val largeTextAccessibility: StateFlow<Boolean> = prefs.largeTextAccessibility
    val activeProfile: StateFlow<String> = prefs.activeProfile
    val familyProfiles: StateFlow<List<String>> = prefs.familyProfiles
    val enabledWidgets: StateFlow<Set<String>> = prefs.enabledWidgets
    val streakCount: StateFlow<Int> = prefs.streakCount
    val personalBestStreak: StateFlow<Int> = prefs.personalBestStreak
    val activeLoginDates: StateFlow<Set<String>> = prefs.activeLoginDates
    val lastLoginDate: StateFlow<String> = prefs.lastLoginDate
    val workMonthlyIncomeUsd: StateFlow<Double> = prefs.workMonthlyIncomeUsd
    val dailyWorkHours: StateFlow<Double> = prefs.dailyWorkHours
    val isDemoDataPermanentlyDisabled: StateFlow<Boolean> = prefs.isDemoDataPermanentlyDisabled
    val profileDisplayName: StateFlow<String> = prefs.profileDisplayName
    val profileEmail: StateFlow<String> = prefs.profileEmail
    val profilePhotoUri: StateFlow<String> = prefs.profilePhotoUri
    val profileBio: StateFlow<String> = prefs.profileBio
    val profileProfession: StateFlow<String> = prefs.profileProfession
    val profileGoogleConnected: StateFlow<Boolean> = prefs.profileGoogleConnected
    val hasCompletedOnboardingAuth: StateFlow<Boolean> = prefs.hasCompletedOnboardingAuth
    val isSignedIn: StateFlow<Boolean> = prefs.isSignedIn
    val authProvider: StateFlow<String> = prefs.authProvider

    private val _isSecurityReady = MutableStateFlow(false)
    val isSecurityReady: StateFlow<Boolean> = _isSecurityReady.asStateFlow()

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _filterTimeframe = MutableStateFlow("Monthly")
    val filterTimeframe: StateFlow<String> = _filterTimeframe.asStateFlow()

    private val _isQuickEntrySheetOpen = MutableStateFlow(false)
    val isQuickEntrySheetOpen: StateFlow<Boolean> = _isQuickEntrySheetOpen.asStateFlow()

    private val _isTransferDialogOpen = MutableStateFlow(false)
    val isTransferDialogOpen: StateFlow<Boolean> = _isTransferDialogOpen.asStateFlow()

    private val _isAddBudgetDialogOpen = MutableStateFlow(false)
    val isAddBudgetDialogOpen: StateFlow<Boolean> = _isAddBudgetDialogOpen.asStateFlow()

    private val _isAddGoalDialogOpen = MutableStateFlow(false)
    val isAddGoalDialogOpen: StateFlow<Boolean> = _isAddGoalDialogOpen.asStateFlow()

    private val _isAddAccountDialogOpen = MutableStateFlow(false)
    val isAddAccountDialogOpen: StateFlow<Boolean> = _isAddAccountDialogOpen.asStateFlow()

    private val _isVoiceSheetOpen = MutableStateFlow(false)
    val isVoiceSheetOpen: StateFlow<Boolean> = _isVoiceSheetOpen.asStateFlow()

    private val _lastVoiceOutcome = MutableStateFlow<VoiceSaveOutcome?>(null)
    val lastVoiceOutcome: StateFlow<VoiceSaveOutcome?> = _lastVoiceOutcome.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _targetVaultAccountId = MutableStateFlow<Long?>(null)
    val targetVaultAccountId: StateFlow<Long?> = _targetVaultAccountId.asStateFlow()

    private val _vaultVisualVersion = MutableStateFlow(0)
    val vaultVisualVersion: StateFlow<Int> = _vaultVisualVersion.asStateFlow()

    private val _leaderboardState = MutableStateFlow(LeaderboardUiState())
    val leaderboardState: StateFlow<LeaderboardUiState> = _leaderboardState.asStateFlow()

    private val _userLeaderboardRank = MutableStateFlow(1485)
    val userLeaderboardRank: StateFlow<Int> = _userLeaderboardRank.asStateFlow()

    private val _hasShownSessionLeaderboardPill = MutableStateFlow(false)
    val hasShownSessionLeaderboardPill: StateFlow<Boolean> = _hasShownSessionLeaderboardPill.asStateFlow()

    private val _pendingCelebration = MutableStateFlow<LeaderboardCelebration?>(null)
    val pendingCelebration: StateFlow<LeaderboardCelebration?> = _pendingCelebration.asStateFlow()

    private val arenaPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "arena_total_xp" || key == "arena_games_played" || key == "arena_streak_days") {
            refreshLeaderboardState()
        }
    }

    fun requestOpenCardInVault(accountId: Long) {
        _targetVaultAccountId.value = accountId
    }

    fun clearTargetVaultAccountId() {
        _targetVaultAccountId.value = null
    }

    fun notifyVaultVisualChanged() {
        _vaultVisualVersion.value += 1
    }

    // Quick Entry form state
    val quickEntryExpression = MutableStateFlow("0")
    val quickEntryType = MutableStateFlow(TransactionType.EXPENSE)
    val quickEntrySelectedCategoryId = MutableStateFlow(1L)
    val quickEntrySelectedAccountId = MutableStateFlow(1L)
    val quickEntryNote = MutableStateFlow("")
    val quickEntryIsRecurring = MutableStateFlow(false)
    val quickEntryRecurrenceInterval = MutableStateFlow("MONTHLY")

    val uiState: StateFlow<KoshvantaUiState> = combine<Any?, KoshvantaUiState>(
        _currentBook,
        currentCurrency,
        _filterTimeframe,
        _isQuickEntrySheetOpen,
        _isTransferDialogOpen,
        _isAddBudgetDialogOpen,
        _isAddGoalDialogOpen,
        _isAddAccountDialogOpen,
        _toastMessage
    ) { params ->
        KoshvantaUiState(
            currentBook = params[0] as FinancialBook,
            currentCurrency = params[1] as CurrencyCode,
            filterTimeframe = params[2] as String,
            isQuickEntrySheetOpen = params[3] as Boolean,
            isTransferDialogOpen = params[4] as Boolean,
            isAddBudgetDialogOpen = params[5] as Boolean,
            isAddGoalDialogOpen = params[6] as Boolean,
            isAddAccountDialogOpen = params[7] as Boolean,
            toastMessage = params[8] as String?
        )
    }.combine(repository.getCategories()) { state, categories ->
        state.copy(categories = categories)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = KoshvantaUiState()
    )

    // Book-reactive Flows (Switching between Personal / Family / Business updates all screens!)
    val accounts: Flow<List<AccountEntity>> = _currentBook.flatMapLatest { repository.getAccounts(it.id) }
    val transactionsWithDetails: Flow<List<TransactionWithDetails>> = _currentBook.flatMapLatest { repository.getTransactionsWithDetails(it.id) }
    val netWorthCurve: StateFlow<List<NetWorthCurvePoint>> = combine(
        accounts,
        transactionsWithDetails
    ) { accs, txs ->
        NetWorthCurveCalculator.buildCurve(
            currentNetWorthUsd = accs.sumOf { it.balance },
            transactions = txs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val effectiveMonthlyIncomeUsd: StateFlow<Double> = combine(
        workMonthlyIncomeUsd,
        transactionsWithDetails
    ) { configuredUsd, txs ->
        if (configuredUsd > 0.0) {
            configuredUsd
        } else {
            val cal = Calendar.getInstance()
            val curMonth = cal.get(Calendar.MONTH)
            val curYear = cal.get(Calendar.YEAR)
            val thisMonthIncome = txs.sumOf { item ->
                if (item.transaction.type == TransactionType.INCOME) {
                    val txCal = Calendar.getInstance().apply { timeInMillis = item.transaction.timestamp }
                    if (txCal.get(Calendar.MONTH) == curMonth && txCal.get(Calendar.YEAR) == curYear) {
                        item.transaction.amount
                    } else 0.0
                } else 0.0
            }
            if (thisMonthIncome > 0.0) {
                thisMonthIncome
            } else {
                txs.sumOf { if (it.transaction.type == TransactionType.INCOME) it.transaction.amount else 0.0 }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = prefs.workMonthlyIncomeUsd.value
    )
    val savingsGoals: Flow<List<SavingsGoalEntity>> = _currentBook.flatMapLatest { repository.getSavingsGoals(it.id) }
    val billReminders: Flow<List<BillReminderEntity>> = _currentBook.flatMapLatest { repository.getBillReminders(it.id) }
    val debts: Flow<List<DebtEntity>> = _currentBook.flatMapLatest { repository.getDebts(it.id) }
    val quickPresets: Flow<List<QuickPresetEntity>> = _currentBook.flatMapLatest { repository.getQuickPresets(it.id) }
    val currentMonthBudgets: Flow<List<BudgetEntity>> = _currentBook.flatMapLatest { book ->
        val cal = Calendar.getInstance()
        repository.getBudgets(book.id, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
    }

    val categories: Flow<List<CategoryEntity>> = repository.getCategories()
    val notificationHistory: Flow<List<NotificationHistoryEntity>> = repository.getNotificationHistory()
    val auditLogs: Flow<List<AuditLogEntity>> = repository.getAuditLogs()

    // Search & Filter
    val searchQuery = MutableStateFlow("")
    val transactionTypeFilter = MutableStateFlow("ALL")
    var lastDeletedTransaction: TransactionEntity? = null
    var lastDeletedAccount: AccountEntity? = null
    val bookmarkedTips = MutableStateFlow<Set<String>>(emptySet())

    init {
        SoundEffectManager.isEnabled = prefs.soundEffectsEnabled.value
        NotificationHelper.ensureChannels(appContext)
        prefs.recordDailyLogin()
        arenaPrefs.registerOnSharedPreferenceChangeListener(arenaPrefsListener)
        refreshLeaderboardState()
        viewModelScope.launch {
            combine(profileDisplayName, profilePhotoUri, streakCount) { _, _, _ ->
            }.collect {
                refreshLeaderboardState()
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            combine(currentMonthBudgets, transactionsWithDetails) { budgets, txs ->
                if (prefs.isDemoWiped() && budgets.isNotEmpty()) {
                    val cal = Calendar.getInstance()
                    val curMonth = cal.get(Calendar.MONTH)
                    val curYear = cal.get(Calendar.YEAR)
                    val spentByCat = HashMap<Long, Double>()
                    txs.forEach { item ->
                        if (item.transaction.type == TransactionType.EXPENSE) {
                            val txCal = Calendar.getInstance().apply { timeInMillis = item.transaction.timestamp }
                            if (txCal.get(Calendar.MONTH) == curMonth && txCal.get(Calendar.YEAR) == curYear) {
                                spentByCat[item.transaction.categoryId] =
                                    (spentByCat[item.transaction.categoryId] ?: 0.0) + item.transaction.amount
                            }
                        }
                    }
                    val withinAll = budgets.all { b ->
                        val spent = spentByCat[b.categoryId] ?: 0.0
                        b.monthlyLimit > 0.0 && spent <= b.monthlyLimit
                    }
                    championsEngine.recordBudgetSetupOrAdherence(
                        hasActiveBudgets = true,
                        withinAllBudgets = withinAll
                    )
                    refreshLeaderboardState()
                }
            }.collect { }
        }
        viewModelScope.launch {
            val security = runCatching { prefs.loadSecurityStateAsync() }
                .getOrDefault(SecurityLockState())
            _isAppLocked.value = security.isPinEnabled && security.hasValidPinHash
            _isSecurityReady.value = true
        }
        viewModelScope.launch {
            repository.checkAndPreloadInitialData(isDemoWiped = prefs.isDemoWiped())
        }
        viewModelScope.launch(Dispatchers.IO) {
            ProfileSyncManager.syncCurrentFirebaseUser(appContext, prefs)
        }
    }

    override fun onCleared() {
        runCatching { arenaPrefs.unregisterOnSharedPreferenceChangeListener(arenaPrefsListener) }
        super.onCleared()
    }

    fun refreshLeaderboardState() {
        viewModelScope.launch(Dispatchers.Default) {
            val rawName = prefs.profileDisplayName.value.trim()
            val resolvedName = if (rawName.isNotBlank()) {
                ChampionsLeaderboardMath.sanitizePlayerDisplayName(rawName)
            } else {
                ChampionsLeaderboardMath.sanitizePlayerDisplayName(prefs.profileEmail.value)
            }
            val nextState = championsEngine.syncAndGetState(
                userName = resolvedName,
                userPhotoUri = prefs.profilePhotoUri.value,
                userStreakDays = prefs.streakCount.value.coerceAtLeast(1)
            )
            _leaderboardState.value = nextState
            _userLeaderboardRank.value = nextState.currentUserEntry.rank
            val celebration = championsEngine.consumePendingCelebrationForRank(nextState.currentUserEntry.rank)
            if (celebration != null && _pendingCelebration.value == null) {
                _pendingCelebration.value = celebration
            }
        }
    }

    fun dismissPendingCelebration() {
        _pendingCelebration.value = null
    }

    fun recordFinancialToolExplored(toolId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            championsEngine.recordFinancialToolExplored(toolId)
            refreshLeaderboardState()
        }
    }

    fun markSessionLeaderboardPillShown() {
        _hasShownSessionLeaderboardPill.value = true
    }

    fun checkInDailyLogin() {
        val advanced = prefs.recordDailyLogin()
        if (prefs.profilePhotoUri.value.isBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                ProfileSyncManager.syncCurrentFirebaseUser(appContext, prefs)
            }
        }
        refreshLeaderboardState()
        if (advanced) {
            val summary = FinanceCalculators.evaluateStreakChallenge(prefs.streakCount.value)
            when {
                summary.isCompleted -> {
                    SoundEffectManager.playAchievementChime()
                    _toastMessage.value = "Level ${summary.level} Complete! (${summary.totalStreakDays} Days) • ${summary.rankTitle} Trophy Unlocked!"
                }
                summary.totalStreakDays > 1 && summary.completedDays == 1 -> {
                    SoundEffectManager.playAchievementChime()
                    _toastMessage.value = "Welcome to Level ${summary.level}: ${summary.rankTitle} (Day ${summary.totalStreakDays} Streak)!"
                }
                summary.totalStreakDays > 1 -> {
                    SoundEffectManager.playAchievementChime()
                    _toastMessage.value = "Day ${summary.totalStreakDays} Streak • Lv.${summary.level} (${summary.completedDays}/14)"
                }
            }
        }
    }

    // --- Book, Theme, Currency, Language ---
    fun switchBook(book: FinancialBook) {
        _currentBook.value = book
        viewModelScope.launch {
            repository.ensureBookInitialized(book.id, isDemoWiped = prefs.isDemoWiped())
        }
        _toastMessage.value = "Switched to ${book.title} Ledger"
    }

    fun switchCurrency(currency: CurrencyCode) {
        prefs.setCurrency(currency)
        viewModelScope.launch {
            repository.syncCountryStarterCards(
                bookId = _currentBook.value.id,
                currency = currency,
                context = appContext,
                force = false
            )
            notifyVaultVisualChanged()
        }
        _toastMessage.value = "Region Vault: ${currency.flag} ${currency.countryName} (${currency.symbol})"
    }

    fun preloadCountryStarterCards() {
        val currency = currentCurrency.value
        viewModelScope.launch {
            repository.syncCountryStarterCards(
                bookId = _currentBook.value.id,
                currency = currency,
                context = appContext,
                force = true
            )
            prefs.setCustomCardsLockedAsync(false)
            prefs.setDemoWipedAsync(true)
            championsEngine.recordCardVaultActivity()
            refreshLeaderboardState()
            notifyVaultVisualChanged()
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "✨ Loaded 3 ${currency.countryName} 3D starter cards"
        }
    }

    fun setCustomCurrencySymbol(symbol: String) {
        prefs.setCustomCurrencySymbol(symbol)
        _toastMessage.value = if (symbol.isBlank()) "Custom currency symbol cleared" else "Custom symbol '$symbol' applied"
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.setThemeMode(mode)
        _toastMessage.value = "Theme set to ${mode.label}"
    }

    fun setAccentPalette(palette: AccentPalette) {
        prefs.setAccentPalette(palette)
        _toastMessage.value = "Accent updated: ${palette.label}"
    }

    fun toggleLanguage() {
        val next = if (appLanguage.value == "EN") "HI" else "EN"
        prefs.setLanguage(next)
        _toastMessage.value = if (next == "HI") "भाषा बदलकर हिंदी कर दी गई है" else "Language switched to English"
    }

    fun setLargeTextAccessibility(enabled: Boolean) {
        prefs.setLargeTextAccessibility(enabled)
    }

    fun toggleDashboardWidget(widgetId: String) {
        prefs.toggleWidget(widgetId)
    }

    fun setActiveProfile(profile: String) {
        prefs.setActiveProfile(profile)
        _toastMessage.value = "Active spender profile: $profile"
    }

    fun addFamilyProfile(name: String) {
        if (name.isBlank()) return
        prefs.addFamilyProfile(name)
        _toastMessage.value = "Profile '$name' added"
    }

    // --- Profile Hub & Firebase-Ready Gmail Auto-Sync ---
    fun syncFirebaseProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val synced = ProfileSyncManager.syncCurrentFirebaseUser(appContext, prefs)
            if (synced) {
                refreshLeaderboardState()
                _toastMessage.value = "Gmail profile & photo synced!"
            }
        }
    }

    fun updateEditableProfile(
        displayName: String,
        bio: String,
        profession: String,
        pickedPhotoUri: Uri? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val savedPhotoUri = if (pickedPhotoUri != null) {
                prefs.persistPickedAvatarFromUri(appContext, pickedPhotoUri)
            } else {
                null
            }
            prefs.updateEditableProfile(
                displayName = displayName,
                bio = bio,
                profession = profession,
                photoUri = savedPhotoUri
            )
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Profile updated!"
        }
    }

    fun signInWithGoogle(
        activityContext: Context,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            when (val outcome = ProfileSyncManager.signInWithGoogle(activityContext, prefs)) {
                is GoogleSignInOutcome.Success -> {
                    refreshLeaderboardState()
                    SoundEffectManager.playAchievementChime()
                    _toastMessage.value = "Signed in as ${outcome.displayName}"
                    onComplete(true, null)
                }
                GoogleSignInOutcome.Cancelled -> {
                    onComplete(false, null)
                }
                is GoogleSignInOutcome.Error -> {
                    SoundEffectManager.playErrorTone()
                    _toastMessage.value = outcome.message
                    onComplete(false, outcome.message)
                }
            }
        }
    }

    fun signOutGoogleProfile(context: Context) {
        viewModelScope.launch {
            ProfileSyncManager.signOutGoogle(context, prefs)
            _toastMessage.value = "Signed out from Google Sync"
        }
    }

    // --- Security (4-Digit Master PIN & Biometric) ---
    fun enableMasterPin(pin: String) {
        if (pin.length == 4 && pin.all { it.isDigit() }) {
            viewModelScope.launch {
                if (prefs.saveAndEnablePinAsync(pin)) {
                    _toastMessage.value = "4-Digit Master PIN enabled & encrypted"
                }
            }
        }
    }

    fun disableMasterPin() {
        viewModelScope.launch {
            prefs.disablePinAsync()
            _isAppLocked.value = false
            _toastMessage.value = "Master PIN lock disabled"
        }
    }

    fun verifyPin(pin: String): Boolean {
        val ok = runCatching { prefs.verifyPin(pin) }.getOrDefault(false)
        if (ok) {
            _isAppLocked.value = false
            SoundEffectManager.playUnlockChime()
        } else {
            SoundEffectManager.playErrorTone()
        }
        return ok
    }

    fun verifyPinAsync(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = runCatching { prefs.verifyPinAsync(pin) }.getOrDefault(false)
            if (ok) {
                _isAppLocked.value = false
                SoundEffectManager.playUnlockChime()
            } else {
                SoundEffectManager.playErrorTone()
            }
            onResult(ok)
        }
    }

    fun unlockViaBiometric() {
        _isAppLocked.value = false
        SoundEffectManager.playUnlockChime()
        _toastMessage.value = "Unlocked via Biometrics"
    }

    fun lockAppNow() {
        if (prefs.isPinEnabled.value && prefs.hasPinConfigured()) {
            _isAppLocked.value = true
        } else {
            _toastMessage.value = "Enable 4-Digit Master PIN first to lock the app."
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setBiometricEnabledAsync(enabled)
            _toastMessage.value = if (enabled) "Biometric App Shield activated" else "Biometric App Shield disabled"
        }
    }

    // --- Alerts & Thresholds ---
    fun setAlertThreshold(percent: Int) {
        prefs.setAlertThresholdPercent(percent)
        _toastMessage.value = "Budget alert threshold set to $percent%"
    }

    fun setDailyReminderEnabled(enabled: Boolean) {
        prefs.setDailyReminderEnabled(enabled)
        if (enabled) {
            NotificationHelper.showNotification(
                appContext,
                "Daily Logging Reminder Active",
                "Koshvanta will alert you on budget thresholds and bill due dates.",
                isHighPriority = false
            )
        }
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        prefs.setSoundEffectsEnabled(enabled)
        if (enabled) {
            SoundEffectManager.playSuccessChime()
        }
        _toastMessage.value = if (enabled) "Sound Effects & Notification Audio ON" else "Sound Effects muted"
    }

    fun playKeypadTick() {
        SoundEffectManager.playSoftKeyTick()
    }

    fun playAchievementChime() {
        SoundEffectManager.playAchievementChime()
    }

    fun previewAlertSound() {
        SoundEffectManager.playAlertChime()
        _toastMessage.value = "Alert sound chime previewed"
    }

    fun sendTestNotification(context: Context) {
        viewModelScope.launch {
            val title = "Koshvanta Smart Alert"
            val msg = "Alert engine verified! Budget warnings trigger automatically at ${alertThresholdPercent.value}%."
            repository.insertNotification(
                NotificationHistoryEntity(title = title, message = msg, alertType = "REMINDER")
            )
            val sent = NotificationHelper.showNotification(
                context = context,
                title = title,
                message = msg,
                isHighPriority = true,
                playInAppChime = true
            )
            _toastMessage.value = if (sent) "Notification & Alert Sound triggered!" else "Alert chime played & saved to Alert Center"
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch { repository.markAllNotificationsRead() }
    }

    fun clearNotificationHistory() {
        viewModelScope.launch {
            repository.clearNotifications()
            SoundEffectManager.playDeleteSound()
        }
    }

    fun setBudgetRollover(enabled: Boolean) {
        prefs.setBudgetRolloverEnabled(enabled)
    }

    fun setDailyAndWeeklyLimits(dailyDisplay: Double, weeklyDisplay: Double) {
        prefs.setDailySpendingLimitUsd(toUsd(dailyDisplay))
        prefs.setWeeklyBudgetLimitUsd(toUsd(weeklyDisplay))
        _toastMessage.value = "Daily & Weekly spending guardrails updated"
    }

    fun updateWorkRateSettings(monthlyIncomeDisplay: Double, dailyHours: Double) {
        val safeHours = dailyHours.coerceIn(1.0, 16.0)
        val incomeUsd = if (monthlyIncomeDisplay > 0.0) toUsd(monthlyIncomeDisplay) else 0.0
        prefs.setWorkMonthlyIncomeUsd(incomeUsd)
        prefs.setDailyWorkHours(safeHours)
        SoundEffectManager.playSuccessChime()
        _toastMessage.value = if (incomeUsd > 0.0) {
            "Work-hour rate saved (${safeHours.toInt()}h/day)"
        } else {
            "Custom monthly income cleared (${safeHours.toInt()}h/day)"
        }
    }

    // --- Dialog Open/Close ---
    fun setFilterTimeframe(timeframe: String) {
        _filterTimeframe.value = timeframe
    }

    fun openQuickEntry(type: TransactionType = TransactionType.EXPENSE) {
        quickEntryType.value = type
        quickEntryExpression.value = "0"
        quickEntryNote.value = ""
        quickEntryIsRecurring.value = false
        _isQuickEntrySheetOpen.value = true
    }

    fun closeQuickEntry() { _isQuickEntrySheetOpen.value = false }
    fun openVoiceEntry() { _isVoiceSheetOpen.value = true }
    fun closeVoiceEntry() { _isVoiceSheetOpen.value = false }
    fun openTransferDialog() { _isTransferDialogOpen.value = true }
    fun closeTransferDialog() { _isTransferDialogOpen.value = false }
    fun openAddBudgetDialog() { _isAddBudgetDialogOpen.value = true }
    fun closeAddBudgetDialog() { _isAddBudgetDialogOpen.value = false }
    fun openAddGoalDialog() { _isAddGoalDialogOpen.value = true }
    fun closeAddGoalDialog() { _isAddGoalDialogOpen.value = false }
    fun openAddAccountDialog() { _isAddAccountDialogOpen.value = true }
    fun closeAddAccountDialog() { _isAddAccountDialogOpen.value = false }
    fun clearToast() { _toastMessage.value = null }

    // --- Currency Conversion Helpers ---
    fun activeSymbol(): String {
        val custom = customCurrencySymbol.value
        return if (custom.isNotEmpty()) custom else currentCurrency.value.symbol
    }

    fun toUsd(displayAmount: Double): Double {
        val rate = currentCurrency.value.exchangeRateToUsd
        return if (rate > 0) displayAmount / rate else displayAmount
    }

    fun fromUsd(usdAmount: Double): Double = usdAmount * currentCurrency.value.exchangeRateToUsd

    fun formatAmount(amountInUsd: Double): String {
        val converted = fromUsd(amountInUsd)
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "${activeSymbol()}${formatter.format(converted)}"
    }

    // --- Keypad & Quick Entry ---
    fun onKeypadPress(key: String) {
        val current = quickEntryExpression.value
        if (current == "0" && key in listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")) {
            quickEntryExpression.value = key
        } else {
            quickEntryExpression.value = current + key
        }
    }

    fun onKeypadBackspace() {
        val current = quickEntryExpression.value
        quickEntryExpression.value = if (current.length > 1) current.dropLast(1) else "0"
    }

    fun onKeypadClear() { quickEntryExpression.value = "0" }

    fun evaluateKeypadExpression(): Double {
        val expr = quickEntryExpression.value.replace("×", "*").replace("÷", "/")
        return try {
            val result = simpleEvaluate(expr)
            quickEntryExpression.value = String.format(Locale.US, "%.2f", result)
            result
        } catch (_: Exception) {
            0.0
        }
    }

    private fun simpleEvaluate(expression: String): Double {
        val tokens = expression.split(Regex("(?<=[-+*/])|(?=[-+*/])")).map { it.trim() }.filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return 0.0
        var total = tokens[0].toDoubleOrNull() ?: 0.0
        var i = 1
        while (i < tokens.size - 1) {
            val op = tokens[i]
            val nextVal = tokens[i + 1].toDoubleOrNull() ?: 0.0
            when (op) {
                "+" -> total += nextVal
                "-" -> total -= nextVal
                "*" -> total *= nextVal
                "/" -> if (nextVal != 0.0) total /= nextVal
            }
            i += 2
        }
        return total
    }

    fun saveQuickTransaction(availableAccounts: List<AccountEntity>, availableCategories: List<CategoryEntity>) {
        val enteredDisplayAmount = evaluateKeypadExpression()
        if (enteredDisplayAmount <= 0.0) {
            SoundEffectManager.playErrorTone()
            _toastMessage.value = "Please enter a valid amount greater than 0"
            return
        }
        val amountUsd = toUsd(enteredDisplayAmount)

        viewModelScope.launch {
            val existingAccId = quickEntrySelectedAccountId.value.takeIf { id -> availableAccounts.any { it.id == id } }
                ?: availableAccounts.firstOrNull()?.id
            val accId = existingAccId ?: repository.insertAccountAndReturnId(
                AccountEntity(
                    bookId = _currentBook.value.id,
                    name = "Primary Wallet",
                    type = AccountType.CASH,
                    balance = 0.0,
                    cardColorHex = "#00C8A0",
                    isDefault = true
                )
            )
            val catId = quickEntrySelectedCategoryId.value.takeIf { id -> availableCategories.any { it.id == id } }
                ?: availableCategories.firstOrNull { it.isExpense == (quickEntryType.value == TransactionType.EXPENSE) }?.id
                ?: 1L

            val noteStr = quickEntryNote.value.trim().ifEmpty {
                "Quick ${quickEntryType.value.name.lowercase().replaceFirstChar { it.uppercase() }}"
            }

            // Duplicate check within last 3 minutes
            val existingTxs = repository.getTransactions(_currentBook.value.id).first()
            val duplicate = existingTxs.firstOrNull {
                kotlin.math.abs(it.amount - amountUsd) < 0.01 &&
                    it.categoryId == catId &&
                    (System.currentTimeMillis() - it.timestamp) < 180_000L
            }

            val tx = TransactionEntity(
                bookId = _currentBook.value.id,
                accountId = accId,
                categoryId = catId,
                amount = amountUsd,
                type = quickEntryType.value,
                note = noteStr,
                timestamp = System.currentTimeMillis(),
                isRecurring = quickEntryIsRecurring.value,
                recurrenceInterval = if (quickEntryIsRecurring.value) quickEntryRecurrenceInterval.value else "",
                profileName = activeProfile.value
            )

            repository.addTransaction(tx)
            prefs.setDemoWipedAsync(true)
            prefs.recordDailyLogStreak()
            championsEngine.recordTransactionActivity()
            refreshLeaderboardState()
            _isQuickEntrySheetOpen.value = false

            val alertFired = if (tx.type == TransactionType.EXPENSE) {
                evaluateAlertsAfterExpense(tx, availableCategories)
            } else {
                false
            }
            if (!alertFired) {
                SoundEffectManager.playSuccessChime()
            }

            _toastMessage.value = if (duplicate != null) {
                "Saved ${formatAmount(amountUsd)} (Note: similar recent entry detected)"
            } else {
                "Saved ${formatAmount(amountUsd)} successfully!"
            }
        }
    }

    fun processVoiceTransaction(
        rawText: String,
        availableAccounts: List<AccountEntity>,
        availableCategories: List<CategoryEntity>,
        overrideType: TransactionType? = null
    ) {
        val baseParsed: VoiceParseResult = VoiceTransactionParser.parse(rawText)
        val effectiveType = overrideType ?: baseParsed.transactionType
        val parsed = baseParsed.copy(transactionType = effectiveType)

        if (parsed.amountDisplay <= 0.0) {
            SoundEffectManager.playErrorTone()
            _toastMessage.value = "Could not detect an amount. Please speak or type a value."
            return
        }

        val amountUsd = toUsd(parsed.amountDisplay)

        viewModelScope.launch {
            // 1. Resolve or Auto-Create Wallet / Account (UPI, Cash, Bank, Card)
            val existingAcc = VoiceTransactionParser.resolveMatchingAccount(parsed, availableAccounts)
            var walletCreated = false
            val walletId: Long
            val walletName: String

            if (existingAcc != null) {
                walletId = existingAcc.id
                walletName = existingAcc.name
            } else {
                val neonColor = when {
                    parsed.paymentModeLabel.contains("UPI", ignoreCase = true) -> "#00B894"
                    parsed.preferredAccountType == AccountType.CASH -> "#059669"
                    parsed.preferredAccountType == AccountType.CREDIT_CARD -> "#7C3AED"
                    else -> "#0284C7"
                }
                val newAcc = AccountEntity(
                    bookId = _currentBook.value.id,
                    name = parsed.paymentModeLabel,
                    type = parsed.preferredAccountType,
                    balance = 0.0,
                    cardColorHex = neonColor,
                    isDefault = availableAccounts.isEmpty()
                )
                walletId = repository.insertAccountAndReturnId(newAcc)
                walletName = newAcc.name
                walletCreated = true
            }

            // 2. Resolve or Auto-Create Category / Custom Neon Section
            val existingCat = VoiceTransactionParser.resolveMatchingCategory(parsed, availableCategories)
            var categoryCreated = false
            val categoryId: Long
            val categoryName: String

            if (existingCat != null) {
                categoryId = existingCat.id
                categoryName = existingCat.name
            } else {
                val newCat = CategoryEntity(
                    name = parsed.categoryHint,
                    iconName = parsed.suggestedIconName,
                    colorHex = parsed.suggestedColorHex,
                    isExpense = effectiveType == TransactionType.EXPENSE
                )
                categoryId = repository.insertCategory(newCat)
                categoryName = newCat.name
                categoryCreated = true
            }

            // 3. Save Transaction into Room DB
            val tx = TransactionEntity(
                bookId = _currentBook.value.id,
                accountId = walletId,
                categoryId = categoryId,
                amount = amountUsd,
                type = effectiveType,
                note = parsed.note,
                timestamp = System.currentTimeMillis(),
                profileName = activeProfile.value
            )
            repository.addTransaction(tx)
            prefs.setDemoWipedAsync(true)
            prefs.recordDailyLogStreak()
            championsEngine.recordTransactionActivity()
            refreshLeaderboardState()
            _isVoiceSheetOpen.value = false

            val updatedCategories = repository.getCategories().first()
            val alertFired = if (tx.type == TransactionType.EXPENSE) {
                evaluateAlertsAfterExpense(tx, updatedCategories)
            } else {
                false
            }
            if (!alertFired) {
                SoundEffectManager.playSuccessChime()
            }

            _lastVoiceOutcome.value = VoiceSaveOutcome(
                amountUsd = amountUsd,
                categoryName = categoryName,
                categoryCreated = categoryCreated,
                walletName = walletName,
                walletCreated = walletCreated,
                transactionType = effectiveType
            )

            val createdExtras = buildList {
                if (categoryCreated) add("New Section: $categoryName")
                if (walletCreated) add("New Wallet: $walletName")
            }
            _toastMessage.value = if (createdExtras.isEmpty()) {
                "Voice saved ${formatAmount(amountUsd)} in $categoryName ($walletName)"
            } else {
                "Voice saved ${formatAmount(amountUsd)} • Created ${createdExtras.joinToString(" & ")}"
            }
        }
    }

    private suspend fun evaluateAlertsAfterExpense(tx: TransactionEntity, categories: List<CategoryEntity>): Boolean {
        val cal = Calendar.getInstance()
        val budgets = repository.getBudgets(_currentBook.value.id, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)).first()
        val allTxs = repository.getTransactions(_currentBook.value.id).first()
        var triggered = false

        // 1. Category Budget Threshold Alert
        val catBudget = budgets.firstOrNull { it.categoryId == tx.categoryId }
        if (catBudget != null && catBudget.monthlyLimit > 0) {
            val catSpent = allTxs.filter { it.type == TransactionType.EXPENSE && it.categoryId == tx.categoryId }.sumOf { it.amount }
            val percent = ((catSpent / catBudget.monthlyLimit) * 100).toInt()
            val threshold = alertThresholdPercent.value
            if (percent >= threshold) {
                val catName = categories.firstOrNull { it.id == tx.categoryId }?.name ?: "Category"
                val title = if (percent >= 100) "Budget Exceeded: $catName" else "$percent% Budget Reached: $catName"
                val msg = "You've spent ${formatAmount(catSpent)} of your ${formatAmount(catBudget.monthlyLimit)} limit."
                repository.insertNotification(NotificationHistoryEntity(title = title, message = msg, alertType = "BUDGET_ALERT"))
                NotificationHelper.showNotification(appContext, title, msg, isHighPriority = true, playInAppChime = true)
                triggered = true
            }
        }

        // 2. Unusual Expense Detection (> 2.5x average expense)
        val expenses = allTxs.filter { it.type == TransactionType.EXPENSE && it.id != tx.id }
        if (expenses.size >= 3) {
            val avg = expenses.map { it.amount }.average()
            if (tx.amount > avg * 2.5) {
                val title = "Unusual Expense Detected"
                val msg = "${tx.note} (${formatAmount(tx.amount)}) is over 2.5× your average transaction."
                repository.insertNotification(NotificationHistoryEntity(title = title, message = msg, alertType = "UNUSUAL_EXPENSE"))
                NotificationHelper.showNotification(appContext, title, msg, isHighPriority = true, playInAppChime = !triggered)
                triggered = true
            }
        }
        return triggered
    }

    // --- Quick Editable 1-Tap Presets ---
    fun logFromPreset(
        preset: QuickPresetEntity,
        availableAccounts: List<AccountEntity>,
        availableCategories: List<CategoryEntity>
    ) {
        viewModelScope.launch {
            val accId = availableAccounts.firstOrNull()?.id ?: repository.insertAccountAndReturnId(
                AccountEntity(
                    bookId = _currentBook.value.id,
                    name = "Primary Wallet",
                    type = AccountType.CASH,
                    balance = 0.0,
                    cardColorHex = "#00C8A0",
                    isDefault = true
                )
            )
            val catId = availableCategories.firstOrNull { it.name.contains(preset.categoryName, ignoreCase = true) }?.id
                ?: availableCategories.firstOrNull()?.id ?: 1L

            val tx = TransactionEntity(
                bookId = _currentBook.value.id,
                accountId = accId,
                categoryId = catId,
                amount = preset.amountUsd,
                type = if (preset.isExpense) TransactionType.EXPENSE else TransactionType.INCOME,
                note = "${preset.emoji} ${preset.title}",
                timestamp = System.currentTimeMillis(),
                profileName = activeProfile.value
            )
            repository.addTransaction(tx)
            prefs.setDemoWipedAsync(true)
            prefs.recordDailyLogStreak()
            championsEngine.recordTransactionActivity()
            refreshLeaderboardState()
            val alertFired = if (tx.type == TransactionType.EXPENSE) {
                evaluateAlertsAfterExpense(tx, availableCategories)
            } else {
                false
            }
            if (!alertFired) {
                SoundEffectManager.playSuccessChime()
            }
            _toastMessage.value = "Logged ${preset.emoji} ${preset.title} (${formatAmount(preset.amountUsd)})"
        }
    }

    fun saveOrUpdatePreset(
        existingId: Long,
        title: String,
        emoji: String,
        displayAmount: Double,
        categoryName: String,
        isExpense: Boolean = true
    ) {
        if (title.isBlank() || displayAmount <= 0.0) return
        viewModelScope.launch {
            val cleanEmoji = emoji.ifBlank { EmojiMatcher.suggestEmoji(title) }
            val preset = QuickPresetEntity(
                id = existingId,
                bookId = _currentBook.value.id,
                title = title.trim(),
                emoji = cleanEmoji,
                amountUsd = toUsd(displayAmount),
                categoryName = categoryName,
                isExpense = isExpense
            )
            repository.upsertPreset(preset)
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Saved preset: $cleanEmoji ${preset.title} (${formatAmount(preset.amountUsd)})"
        }
    }

    fun deletePreset(presetId: Long) {
        viewModelScope.launch {
            repository.deletePreset(presetId)
            SoundEffectManager.playDeleteSound()
            _toastMessage.value = "Preset removed"
        }
    }

    // --- Transaction Edit / Split / Refund / Receipt / Delete ---
    fun updateTransactionDetails(
        oldTx: TransactionEntity,
        newDisplayAmount: Double,
        newNote: String,
        newCategoryId: Long,
        receiptUri: String? = oldTx.receiptUri,
        splitDetails: String = oldTx.splitDetails
    ) {
        if (newDisplayAmount <= 0.0) return
        viewModelScope.launch {
            val updated = oldTx.copy(
                amount = toUsd(newDisplayAmount),
                note = newNote.trim().ifEmpty { oldTx.note },
                categoryId = newCategoryId,
                receiptUri = receiptUri,
                splitDetails = splitDetails
            )
            repository.updateTransaction(oldTx, updated)
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Transaction updated!"
        }
    }

    fun markTransactionAsRefund(tx: TransactionEntity) {
        viewModelScope.launch {
            val refundTx = TransactionEntity(
                bookId = _currentBook.value.id,
                accountId = tx.accountId,
                categoryId = tx.categoryId,
                amount = tx.amount,
                type = TransactionType.INCOME,
                note = "Refund: ${tx.note}",
                timestamp = System.currentTimeMillis(),
                isRefund = true,
                profileName = activeProfile.value
            )
            repository.addTransaction(refundTx)
            prefs.setDemoWipedAsync(true)
            repository.logAudit("REFUND", "Issued refund reversal for ${tx.note}")
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Refund of ${formatAmount(tx.amount)} credited!"
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        lastDeletedAccount = null
        lastDeletedTransaction = tx
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            SoundEffectManager.playDeleteSound()
            _toastMessage.value = "Transaction removed (Undo available)"
        }
    }

    fun undoLastDelete() {
        val acc = lastDeletedAccount
        if (acc != null) {
            viewModelScope.launch {
                repository.insertAccount(acc)
                lastDeletedAccount = null
                SoundEffectManager.playSuccessChime()
                _toastMessage.value = "Restored card '${acc.name}'!"
            }
            return
        }
        val tx = lastDeletedTransaction ?: return
        viewModelScope.launch {
            repository.addTransaction(tx)
            lastDeletedTransaction = null
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Restored transaction successfully!"
        }
    }

    // --- Transfers, Accounts, Categories, Budgets, Goals, Bills, Debts ---
    fun executeTransfer(fromAccountId: Long, toAccountId: Long, displayAmount: Double, note: String) {
        if (displayAmount <= 0.0) {
            SoundEffectManager.playErrorTone()
            _toastMessage.value = "Please enter a transfer amount greater than 0"
            return
        }
        if (fromAccountId == toAccountId) {
            SoundEffectManager.playErrorTone()
            _toastMessage.value = "Source and destination accounts must be different"
            return
        }
        val amountUsd = toUsd(displayAmount)
        viewModelScope.launch {
            repository.transferFunds(fromAccountId, toAccountId, amountUsd, note, _currentBook.value.id, activeProfile.value)
            prefs.setDemoWipedAsync(true)
            championsEngine.recordTransactionActivity()
            refreshLeaderboardState()
            _isTransferDialogOpen.value = false
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Transferred ${formatAmount(amountUsd)} successfully!"
        }
    }

    fun addCustomCategory(name: String, iconName: String, colorHex: String, isExpense: Boolean) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCategory(
                CategoryEntity(
                    name = name.trim(),
                    iconName = iconName,
                    colorHex = colorHex,
                    isExpense = isExpense
                )
            )
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Category '$name' added!"
        }
    }

    fun addAccount(name: String, type: AccountType, initialDisplayBalance: Double, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val account = AccountEntity(
                bookId = _currentBook.value.id,
                name = name.trim(),
                type = type,
                balance = toUsd(initialDisplayBalance),
                cardColorHex = colorHex
            )
            repository.insertAccount(account)
            prefs.setCustomCardsLockedAsync(true)
            prefs.setDemoWipedAsync(true)
            _isAddAccountDialogOpen.value = false
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Account '$name' created!"
        }
    }

    fun quickAddCountryCard(
        preset: CountryCardPreset,
        onCreated: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val existing = repository.getAccounts(_currentBook.value.id).first()
            val newId = repository.insertAccountAndReturnId(
                AccountEntity(
                    bookId = _currentBook.value.id,
                    name = preset.cardTitle,
                    type = preset.accountType,
                    balance = 0.0,
                    accountNumberLast4 = preset.last4,
                    cardColorHex = preset.cardColorHex,
                    isDefault = existing.isEmpty(),
                    creditLimit = preset.defaultLimitUsd
                )
            )
            MetalCardVaultStore.saveFinish(appContext, newId, preset.finish)
            prefs.setCustomCardsLockedAsync(true)
            prefs.setDemoWipedAsync(true)
            championsEngine.recordCardVaultActivity()
            refreshLeaderboardState()
            notifyVaultVisualChanged()
            SoundEffectManager.playSuccessChime()
            onCreated(newId)
        }
    }

    fun addCustomMetalCard(
        name: String,
        type: AccountType,
        currency: CurrencyCode,
        balanceInCurrency: Double,
        monthlyLimitInCurrency: Double,
        last4: String,
        finish: MetalCardFinish,
        onCreated: (Long) -> Unit = {}
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val existing = repository.getAccounts(_currentBook.value.id).first()
            val rate = currency.exchangeRateToUsd.takeIf { it > 0.0 } ?: 1.0
            val balanceUsd = balanceInCurrency / rate
            val limitUsd = monthlyLimitInCurrency / rate
            val newId = repository.insertAccountAndReturnId(
                AccountEntity(
                    bookId = _currentBook.value.id,
                    name = name.trim(),
                    type = type,
                    balance = balanceUsd,
                    accountNumberLast4 = last4.trim(),
                    cardColorHex = "#%06X".format(0xFFFFFF and finish.primaryHex.toInt()),
                    isDefault = existing.isEmpty(),
                    creditLimit = limitUsd
                )
            )
            MetalCardVaultStore.saveFinish(appContext, newId, finish)
            val override = if (currency != currentCurrency.value) currency else null
            MetalCardVaultStore.saveCardCurrencyOverride(appContext, newId, override)
            prefs.setCustomCardsLockedAsync(true)
            prefs.setDemoWipedAsync(true)
            championsEngine.recordCardVaultActivity()
            refreshLeaderboardState()
            notifyVaultVisualChanged()
            _isAddAccountDialogOpen.value = false
            SoundEffectManager.playSuccessChime()
            onCreated(newId)
        }
    }

    fun updateMetalCardDetails(
        account: AccountEntity,
        newName: String,
        newType: AccountType,
        newCurrency: CurrencyCode,
        newBalanceInCurrency: Double,
        newMonthlyLimitInCurrency: Double,
        newLast4: String,
        newFinish: MetalCardFinish,
        setPrimary: Boolean,
        onUpdated: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val rate = newCurrency.exchangeRateToUsd.takeIf { it > 0.0 } ?: 1.0
            val updated = account.copy(
                name = newName.trim().ifEmpty { account.name },
                type = newType,
                balance = newBalanceInCurrency / rate,
                creditLimit = newMonthlyLimitInCurrency / rate,
                accountNumberLast4 = newLast4.trim(),
                cardColorHex = "#%06X".format(0xFFFFFF and newFinish.primaryHex.toInt()),
                isDefault = setPrimary,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateAccount(updated)
            if (setPrimary) {
                repository.setPrimaryAccount(account.bookId, account.id)
            }
            MetalCardVaultStore.saveFinish(appContext, account.id, newFinish)
            val override = if (newCurrency != currentCurrency.value) newCurrency else null
            MetalCardVaultStore.saveCardCurrencyOverride(appContext, account.id, override)
            prefs.setCustomCardsLockedAsync(true)
            championsEngine.recordCardVaultActivity()
            refreshLeaderboardState()
            notifyVaultVisualChanged()
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Updated '${updated.name}'!"
            onUpdated()
        }
    }

    fun deleteAccount(accountId: Long) {
        viewModelScope.launch {
            val existing = repository.getAccountById(accountId)
            lastDeletedTransaction = null
            lastDeletedAccount = existing
            repository.deleteAccount(accountId)
            prefs.setCustomCardsLockedAsync(true)
            notifyVaultVisualChanged()
            SoundEffectManager.playDeleteSound()
            _toastMessage.value = "Card '${existing?.name ?: "Account"}' removed (Undo available)"
        }
    }

    fun addBudget(categoryId: Long, displayLimit: Double) {
        if (displayLimit <= 0.0) return
        val cal = Calendar.getInstance()
        val limitUsd = toUsd(displayLimit)
        viewModelScope.launch {
            val budget = BudgetEntity(
                bookId = _currentBook.value.id,
                categoryId = categoryId,
                monthlyLimit = limitUsd,
                periodMonth = cal.get(Calendar.MONTH) + 1,
                periodYear = cal.get(Calendar.YEAR)
            )
            repository.insertBudget(budget)
            prefs.setDemoWipedAsync(true)
            championsEngine.recordBudgetSetupOrAdherence(hasActiveBudgets = true, withinAllBudgets = true)
            refreshLeaderboardState()
            _isAddBudgetDialogOpen.value = false
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Monthly budget set to ${formatAmount(limitUsd)}"
        }
    }

    fun deleteBudget(budgetId: Long) {
        viewModelScope.launch {
            repository.deleteBudget(budgetId)
            SoundEffectManager.playDeleteSound()
            _toastMessage.value = "Category budget removed"
        }
    }

    fun addBillReminder(title: String, displayAmount: Double, daysUntilDue: Int) {
        if (title.isBlank() || displayAmount <= 0.0) return
        viewModelScope.launch {
            repository.insertBill(
                BillReminderEntity(
                    bookId = _currentBook.value.id,
                    title = title.trim(),
                    amount = toUsd(displayAmount),
                    dueDate = System.currentTimeMillis() + (daysUntilDue.coerceAtLeast(1) * 24L * 3600 * 1000)
                )
            )
            prefs.setDemoWipedAsync(true)
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Upcoming bill '$title' scheduled!"
        }
    }

    fun toggleBillPaid(billId: Long, currentPaidStatus: Boolean) {
        if (!inFlightBillIds.add(billId)) return
        viewModelScope.launch {
            try {
                when (
                    val result = repository.toggleBillPaidWithSync(
                        billId = billId,
                        isPaid = !currentPaidStatus,
                        profileName = activeProfile.value
                    )
                ) {
                    is BillToggleResult.Paid -> {
                        prefs.setDemoWipedAsync(true)
                        prefs.recordDailyLogStreak()
                        val updatedCategories = repository.getCategories().first()
                        val alertFired = evaluateAlertsAfterExpense(result.transaction, updatedCategories)
                        if (!alertFired) {
                            SoundEffectManager.playSuccessChime()
                        }
                        val createdTag = if (result.categoryAutoCreated) " (Created Category)" else ""
                        _toastMessage.value =
                            "Paid ${result.bill.title}: ${formatAmount(result.transaction.amount)} logged in ${result.category.name}$createdTag"
                    }
                    is BillToggleResult.Unpaid -> {
                        SoundEffectManager.playDeleteSound()
                        _toastMessage.value = if (result.reversedTransaction != null) {
                            "Unmarked ${result.bill.title} & reversed ${formatAmount(result.reversedTransaction.amount)}"
                        } else {
                            "Unmarked ${result.bill.title}"
                        }
                    }
                    is BillToggleResult.NoOpAlreadySynced,
                    is BillToggleResult.NotFound -> Unit
                }
            } finally {
                inFlightBillIds.remove(billId)
            }
        }
    }

    fun addSavingsGoal(name: String, targetDisplay: Double, initialDisplay: Double, colorHex: String) {
        if (name.isBlank() || targetDisplay <= 0.0) return
        viewModelScope.launch {
            val goal = SavingsGoalEntity(
                bookId = _currentBook.value.id,
                name = name.trim(),
                targetAmount = toUsd(targetDisplay),
                currentAmount = toUsd(initialDisplay),
                colorHex = colorHex
            )
            repository.insertGoal(goal)
            _isAddGoalDialogOpen.value = false
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Savings Goal '$name' created!"
        }
    }

    fun depositToGoal(goalId: Long, depositAmountUsd: Double, accountId: Long) {
        if (depositAmountUsd <= 0.0) return
        viewModelScope.launch {
            repository.updateGoalDeposit(goalId, depositAmountUsd, accountId, _currentBook.value.id)
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Deposited ${formatAmount(depositAmountUsd)} into vault!"
        }
    }

    fun addDebt(personName: String, displayAmount: Double, isLent: Boolean, note: String) {
        if (personName.isBlank() || displayAmount <= 0.0) return
        viewModelScope.launch {
            val debt = DebtEntity(
                bookId = _currentBook.value.id,
                personName = personName.trim(),
                amount = toUsd(displayAmount),
                isLent = isLent,
                note = note.trim()
            )
            repository.insertDebt(debt)
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Recorded ${if (isLent) "Lent to" else "Borrowed from"} $personName"
        }
    }

    fun toggleDebtSettled(debtId: Long, currentSettled: Boolean) {
        viewModelScope.launch {
            repository.toggleDebtSettled(debtId, !currentSettled)
            if (!currentSettled) {
                SoundEffectManager.playSuccessChime()
            }
            _toastMessage.value = if (!currentSettled) "Marked as settled!" else "Marked as pending"
        }
    }

    fun deleteDebt(debtId: Long) {
        viewModelScope.launch {
            repository.deleteDebt(debtId)
            SoundEffectManager.playDeleteSound()
            _toastMessage.value = "Debt record deleted."
        }
    }

    fun toggleTipBookmark(tip: String) {
        val current = bookmarkedTips.value.toMutableSet()
        if (current.contains(tip)) {
            current.remove(tip)
            _toastMessage.value = "Tip removed from bookmarks"
        } else {
            current.add(tip)
            _toastMessage.value = "Tip bookmarked!"
        }
        bookmarkedTips.value = current
    }

    // --- Real PDF, CSV, JSON Export / Restore / Integrity ---
    fun exportPdfReport(context: Context, accounts: List<AccountEntity>, transactions: List<TransactionWithDetails>) {
        viewModelScope.launch {
            try {
                ReportExporter.generateAndSharePdfReport(
                    context = context,
                    bookTitle = _currentBook.value.title,
                    currencySymbol = "${currentCurrency.value.code} (${activeSymbol()})",
                    accounts = accounts,
                    transactions = transactions,
                    formatAmount = { formatAmount(it) }
                )
                repository.logAudit("EXPORT", "Generated & shared PDF Financial Statement (${transactions.size} items)")
                _toastMessage.value = "PDF Statement ready to share!"
            } catch (e: Exception) {
                _toastMessage.value = "PDF export failed: ${e.message}"
            }
        }
    }

    fun exportCsvReport(context: Context, transactions: List<TransactionWithDetails>) {
        viewModelScope.launch {
            try {
                ReportExporter.generateAndShareCsvReport(
                    context = context,
                    bookTitle = _currentBook.value.title,
                    currencyCode = currentCurrency.value.code,
                    transactions = transactions,
                    convertToDisplay = { fromUsd(it) }
                )
                repository.logAudit("EXPORT", "Generated & shared CSV Spreadsheet")
                _toastMessage.value = "CSV Ledger ready to share!"
            } catch (e: Exception) {
                _toastMessage.value = "CSV export failed: ${e.message}"
            }
        }
    }

    fun exportJsonBackup(context: Context) {
        viewModelScope.launch {
            try {
                val payload = repository.exportBackupPayload()
                ReportExporter.exportAndShareJsonBackup(context, payload)
                repository.logAudit("EXPORT", "Exported SHA-256 verified .json backup")
                _toastMessage.value = "Encrypted-checksum .JSON backup created!"
            } catch (e: Exception) {
                _toastMessage.value = "Backup export failed: ${e.message}"
            }
        }
    }

    fun restoreJsonBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            val raw = ReportExporter.readTextFromUri(context, uri)
            if (raw == null) {
                SoundEffectManager.playErrorTone()
                _toastMessage.value = "Unable to read selected backup file"
                return@launch
            }
            val parsed = ReportExporter.validateAndParseBackupJson(raw)
            parsed.onSuccess { payload ->
                repository.restoreBackupPayload(payload)
                prefs.setDemoWipedAsync(true)
                SoundEffectManager.playSuccessChime()
                _toastMessage.value = "Restored ${payload.transactions.size} transactions & ${payload.accounts.size} accounts!"
            }.onFailure { err ->
                SoundEffectManager.playErrorTone()
                _toastMessage.value = err.message ?: "Backup validation failed"
            }
        }
    }

    fun runIntegrityCheck() {
        viewModelScope.launch {
            val payload = repository.exportBackupPayload()
            val json = ReportExporter.generateJsonBackupString(payload)
            val result = ReportExporter.validateAndParseBackupJson(json)
            if (result.isSuccess) {
                repository.logAudit("AUDIT", "SQLite Integrity Check PASSED (${payload.transactions.size} tx verified)")
                SoundEffectManager.playSuccessChime()
                _toastMessage.value = "Integrity Verified: ${payload.accounts.size} accounts & ${payload.transactions.size} logs SHA-256 valid!"
            } else {
                SoundEffectManager.playErrorTone()
                _toastMessage.value = "Integrity warning: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun startFreshPersonalVault() {
        viewModelScope.launch {
            val activeUid = runCatching {
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            }.getOrNull()
            prefs.permanentlyDisableDemoData(
                firebaseUid = activeUid,
                email = prefs.profileEmail.value
            )
            prefs.setCustomCardsLockedAsync(false)
            prefs.setStarterCardIds(emptyList())
            repository.wipeAllUserData()
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "✓ Sample data cleared — Your personal vault is ready"
        }
    }

    fun wipeAllData() {
        viewModelScope.launch {
            val activeUid = runCatching {
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            }.getOrNull()
            prefs.permanentlyDisableDemoData(
                firebaseUid = activeUid,
                email = prefs.profileEmail.value
            )
            prefs.setCustomCardsLockedAsync(false)
            prefs.setStarterCardIds(emptyList())
            repository.wipeAllUserData()
            SoundEffectManager.playDeleteSound()
            _toastMessage.value = "All demo & user data wiped cleanly."
        }
    }

    fun reloadDemoData() {
        viewModelScope.launch {
            if (prefs.isDemoDataPermanentlyDisabled()) {
                _toastMessage.value = "Demo data is permanently disabled after initial wipe."
                return@launch
            }
            repository.wipeAllUserData()
            prefs.setCustomCardsLockedAsync(false)
            prefs.setStarterCardIds(emptyList())
            repository.loadDemoData()
            prefs.setDemoWipedAsync(false)
            SoundEffectManager.playSuccessChime()
            _toastMessage.value = "Fresh demo financial data reloaded!"
        }
    }

    // --- Triple-Auth (Google, Email/Password, Guest) & Separate Account Controls ---
    fun continueAsGuest() {
        prefs.completeGuestOnboarding()
        SoundEffectManager.playSuccessChime()
        _toastMessage.value = "Welcome to Kushwanta Vault!"
    }

    fun signInWithEmail(
        context: Context,
        email: String,
        password: String,
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            when (val outcome = ProfileSyncManager.signInWithEmail(context, email, password, prefs)) {
                is EmailAuthOutcome.Success -> {
                    refreshLeaderboardState()
                    SoundEffectManager.playSuccessChime()
                    val msg = "Welcome back, ${outcome.displayName}!"
                    _toastMessage.value = msg
                    onComplete(true, msg)
                }
                is EmailAuthOutcome.Error -> {
                    SoundEffectManager.playErrorTone()
                    onComplete(false, outcome.message)
                }
            }
        }
    }

    fun createAccountWithEmail(
        context: Context,
        fullName: String,
        email: String,
        password: String,
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            when (val outcome = ProfileSyncManager.createAccountWithEmail(context, fullName, email, password, prefs)) {
                is EmailAuthOutcome.Success -> {
                    refreshLeaderboardState()
                    SoundEffectManager.playSuccessChime()
                    val msg = "Account created! Welcome, ${outcome.displayName}"
                    _toastMessage.value = msg
                    onComplete(true, msg)
                }
                is EmailAuthOutcome.Error -> {
                    SoundEffectManager.playErrorTone()
                    onComplete(false, outcome.message)
                }
            }
        }
    }

    fun sendPasswordReset(
        context: Context,
        email: String,
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            when (val outcome = ProfileSyncManager.sendPasswordResetEmail(context, email)) {
                is PasswordResetOutcome.Sent -> {
                    SoundEffectManager.playSuccessChime()
                    onComplete(true, outcome.message)
                }
                is PasswordResetOutcome.Error -> {
                    SoundEffectManager.playErrorTone()
                    onComplete(false, outcome.message)
                }
            }
        }
    }

    /**
     * Soft Sign-Out: signs out FirebaseAuth cleanly and returns to Welcome/Login screen
     * while preserving 100% of local transactions, budgets, cards, and game scores.
     */
    fun signOutToWelcome(context: Context) {
        viewModelScope.launch {
            ProfileSyncManager.signOutGoogle(context, prefs)
            prefs.softSignOutSession()
            _isAppLocked.value = false
            playKeypadTick()
            _toastMessage.value = "Signed out safely. Your local vault data is preserved."
        }
    }

    /**
     * Hard Delete Account & All Data (Play Store Compliance):
     * Deletes active Firebase user (try-catch), wipes all Room DB tables & SharedPreferences
     * (Finance, Profile, Cards, Warranties, Cooldown, all 10 Mind Arena games), and resets to Welcome.
     */
    fun deleteAccountAndAllData(context: Context) {
        viewModelScope.launch {
            ProfileSyncManager.deleteActiveFirebaseUser(context)
            repository.wipeAllUserData()
            prefs.wipeAllPreferencesForAccountDeletion(context)
            _pendingCelebration.value = null
            _hasShownSessionLeaderboardPill.value = false
            refreshLeaderboardState()
            _isAppLocked.value = false
            SoundEffectManager.playDeleteSound()
            _toastMessage.value = "Account & all local data permanently deleted."
        }
    }
}
