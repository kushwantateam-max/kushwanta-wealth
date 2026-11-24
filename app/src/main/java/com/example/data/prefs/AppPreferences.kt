package com.example.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.data.model.AccentPalette
import com.example.data.model.CurrencyCode
import com.example.data.model.ThemeMode
import com.example.domain.util.SoundEffectManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SecurityLockState(
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val hasValidPinHash: Boolean = false
)

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("koshvanta_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        ThemeMode.entries.firstOrNull { it.id == prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.id) }
            ?: ThemeMode.SYSTEM
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _accentPalette = MutableStateFlow(
        AccentPalette.entries.firstOrNull { it.id == prefs.getString(KEY_ACCENT_PALETTE, AccentPalette.COBALT.id) }
            ?: AccentPalette.COBALT
    )
    val accentPalette: StateFlow<AccentPalette> = _accentPalette.asStateFlow()

    private val _currency = MutableStateFlow(
        CurrencyCode.entries.firstOrNull { it.code == prefs.getString(KEY_CURRENCY, CurrencyCode.USD.code) }
            ?: CurrencyCode.USD
    )
    val currency: StateFlow<CurrencyCode> = _currency.asStateFlow()

    private val _customCurrencySymbol = MutableStateFlow(prefs.getString(KEY_CUSTOM_SYMBOL, "") ?: "")
    val customCurrencySymbol: StateFlow<String> = _customCurrencySymbol.asStateFlow()

    private val _language = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "EN") ?: "EN")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _isPinEnabled = MutableStateFlow(false)
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _alertThresholdPercent = MutableStateFlow(prefs.getInt(KEY_ALERT_THRESHOLD, 80))
    val alertThresholdPercent: StateFlow<Int> = _alertThresholdPercent.asStateFlow()

    private val _dailyReminderEnabled = MutableStateFlow(prefs.getBoolean(KEY_DAILY_REMINDER, true))
    val dailyReminderEnabled: StateFlow<Boolean> = _dailyReminderEnabled.asStateFlow()

    private val _soundEffectsEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, true))
    val soundEffectsEnabled: StateFlow<Boolean> = _soundEffectsEnabled.asStateFlow()

    private val _budgetRolloverEnabled = MutableStateFlow(prefs.getBoolean(KEY_BUDGET_ROLLOVER, false))
    val budgetRolloverEnabled: StateFlow<Boolean> = _budgetRolloverEnabled.asStateFlow()

    private val _dailySpendingLimitUsd = MutableStateFlow(prefs.getFloat(KEY_DAILY_LIMIT, 100f).toDouble())
    val dailySpendingLimitUsd: StateFlow<Double> = _dailySpendingLimitUsd.asStateFlow()

    private val _weeklyBudgetLimitUsd = MutableStateFlow(prefs.getFloat(KEY_WEEKLY_LIMIT, 600f).toDouble())
    val weeklyBudgetLimitUsd: StateFlow<Double> = _weeklyBudgetLimitUsd.asStateFlow()

    private val _largeTextAccessibility = MutableStateFlow(prefs.getBoolean(KEY_LARGE_TEXT, false))
    val largeTextAccessibility: StateFlow<Boolean> = _largeTextAccessibility.asStateFlow()

    private val _activeProfile = MutableStateFlow(prefs.getString(KEY_ACTIVE_PROFILE, "Primary") ?: "Primary")
    val activeProfile: StateFlow<String> = _activeProfile.asStateFlow()

    private val _familyProfiles = MutableStateFlow(
        (prefs.getString(KEY_FAMILY_PROFILES, "Primary,Family Member,Business Partner") ?: "Primary")
            .split(",")
            .filter { it.isNotBlank() }
    )
    val familyProfiles: StateFlow<List<String>> = _familyProfiles.asStateFlow()

    private val _enabledWidgets = MutableStateFlow(
        (prefs.getString(KEY_ENABLED_WIDGETS, DEFAULT_WIDGETS) ?: DEFAULT_WIDGETS)
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()
    )
    val enabledWidgets: StateFlow<Set<String>> = _enabledWidgets.asStateFlow()

    private val _streakCount = MutableStateFlow(loadInitialStreakCount())
    val streakCount: StateFlow<Int> = _streakCount.asStateFlow()

    private val _personalBestStreak = MutableStateFlow(loadInitialBestStreak())
    val personalBestStreak: StateFlow<Int> = _personalBestStreak.asStateFlow()

    private val _activeLoginDates = MutableStateFlow(loadActiveLoginDates())
    val activeLoginDates: StateFlow<Set<String>> = _activeLoginDates.asStateFlow()

    private val _lastLoginDate = MutableStateFlow(loadLastLoginDate())
    val lastLoginDate: StateFlow<String> = _lastLoginDate.asStateFlow()

    private val _isDemoWiped = MutableStateFlow(
        runCatching { prefs.getBoolean(KEY_DEMO_WIPED, false) }.getOrDefault(false)
    )
    val isDemoWiped: StateFlow<Boolean> = _isDemoWiped.asStateFlow()

    private val _isDemoDataPermanentlyDisabled = MutableStateFlow(
        runCatching { prefs.getBoolean(KEY_DEMO_DATA_PERMANENTLY_DISABLED, false) }.getOrDefault(false)
    )
    val isDemoDataPermanentlyDisabled: StateFlow<Boolean> = _isDemoDataPermanentlyDisabled.asStateFlow()

    private val _profileDisplayName = MutableStateFlow(
        prefs.getString(KEY_PROFILE_DISPLAY_NAME, "Koshvanta Explorer") ?: "Koshvanta Explorer"
    )
    val profileDisplayName: StateFlow<String> = _profileDisplayName.asStateFlow()

    private val _profileEmail = MutableStateFlow(
        prefs.getString(KEY_PROFILE_EMAIL, "") ?: ""
    )
    val profileEmail: StateFlow<String> = _profileEmail.asStateFlow()

    private val _profilePhotoUri = MutableStateFlow(
        prefs.getString(KEY_PROFILE_PHOTO_URI, "") ?: ""
    )
    val profilePhotoUri: StateFlow<String> = _profilePhotoUri.asStateFlow()

    private val _profileBio = MutableStateFlow(
        prefs.getString(KEY_PROFILE_BIO, "Building disciplined wealth & smart cashflow")
            ?: "Building disciplined wealth & smart cashflow"
    )
    val profileBio: StateFlow<String> = _profileBio.asStateFlow()

    private val _profileProfession = MutableStateFlow(
        prefs.getString(KEY_PROFILE_PROFESSION, "Financial Strategist") ?: "Financial Strategist"
    )
    val profileProfession: StateFlow<String> = _profileProfession.asStateFlow()

    private val _profileGoogleConnected = MutableStateFlow(
        runCatching { prefs.getBoolean(KEY_PROFILE_GOOGLE_CONNECTED, false) }.getOrDefault(false)
    )
    val profileGoogleConnected: StateFlow<Boolean> = _profileGoogleConnected.asStateFlow()

    private val _hasCompletedOnboardingAuth = MutableStateFlow(
        runCatching { prefs.getBoolean(KEY_HAS_COMPLETED_ONBOARDING_AUTH, false) }.getOrDefault(false)
    )
    val hasCompletedOnboardingAuth: StateFlow<Boolean> = _hasCompletedOnboardingAuth.asStateFlow()

    private val _isSignedIn = MutableStateFlow(
        runCatching { prefs.getBoolean(KEY_IS_SIGNED_IN, false) }.getOrDefault(false)
    )
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private val _authProvider = MutableStateFlow(
        prefs.getString(KEY_AUTH_PROVIDER, "") ?: ""
    )
    val authProvider: StateFlow<String> = _authProvider.asStateFlow()

    private val _workMonthlyIncomeUsd = MutableStateFlow(
        runCatching { prefs.getString(KEY_WORK_MONTHLY_INCOME_USD, null)?.toDoubleOrNull() }.getOrNull() ?: 0.0
    )
    val workMonthlyIncomeUsd: StateFlow<Double> = _workMonthlyIncomeUsd.asStateFlow()

    private val _dailyWorkHours = MutableStateFlow(
        (runCatching { prefs.getString(KEY_DAILY_WORK_HOURS, null)?.toDoubleOrNull() }.getOrNull() ?: 8.0)
            .coerceIn(1.0, 16.0)
    )
    val dailyWorkHours: StateFlow<Double> = _dailyWorkHours.asStateFlow()

    private fun loadInitialStreakCount(): Int {
        if (!prefs.getBoolean(KEY_STREAK_V2_MIGRATED, false)) {
            return 0
        }
        return prefs.getInt(KEY_STREAK_COUNT, 0)
    }

    private fun loadInitialBestStreak(): Int {
        if (!prefs.getBoolean(KEY_STREAK_V2_MIGRATED, false)) {
            return 0
        }
        return prefs.getInt(KEY_BEST_STREAK, 0)
    }

    private fun loadActiveLoginDates(): Set<String> {
        if (!prefs.getBoolean(KEY_STREAK_V2_MIGRATED, false)) {
            return emptySet()
        }
        return (prefs.getString(KEY_LOGIN_DATES, "") ?: "")
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()
    }

    private fun loadLastLoginDate(): String {
        if (!prefs.getBoolean(KEY_STREAK_V2_MIGRATED, false)) {
            return ""
        }
        return prefs.getString(KEY_LAST_LOG_DATE, "") ?: ""
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.id).apply()
        _themeMode.value = mode
    }

    fun setAccentPalette(palette: AccentPalette) {
        prefs.edit().putString(KEY_ACCENT_PALETTE, palette.id).apply()
        _accentPalette.value = palette
    }

    fun setCurrency(currency: CurrencyCode) {
        prefs.edit().putString(KEY_CURRENCY, currency.code).apply()
        _currency.value = currency
    }

    fun setCustomCurrencySymbol(symbol: String) {
        prefs.edit().putString(KEY_CUSTOM_SYMBOL, symbol.trim()).apply()
        _customCurrencySymbol.value = symbol.trim()
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _language.value = lang
    }

    suspend fun loadSecurityStateAsync(): SecurityLockState = withContext(Dispatchers.IO) {
        runCatching {
            val pinEnabled = prefs.getBoolean(KEY_PIN_ENABLED, false)
            val bioEnabled = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
            val savedHash = prefs.getString(KEY_PIN_HASH, null).orEmpty()
            val hasValidHash = savedHash.isNotBlank()

            if (pinEnabled && !hasValidHash) {
                // Safely auto-recover from corrupted or null PIN hash without crashing or locking user out
                prefs.edit()
                    .putBoolean(KEY_PIN_ENABLED, false)
                    .putBoolean(KEY_BIOMETRIC_ENABLED, false)
                    .commit()
                _isPinEnabled.value = false
                _isBiometricEnabled.value = false
                SecurityLockState(
                    isPinEnabled = false,
                    isBiometricEnabled = false,
                    hasValidPinHash = false
                )
            } else {
                _isPinEnabled.value = pinEnabled
                _isBiometricEnabled.value = bioEnabled
                SecurityLockState(
                    isPinEnabled = pinEnabled,
                    isBiometricEnabled = bioEnabled,
                    hasValidPinHash = hasValidHash
                )
            }
        }.getOrElse {
            _isPinEnabled.value = false
            _isBiometricEnabled.value = false
            SecurityLockState()
        }
    }

    suspend fun saveAndEnablePinAsync(pin: String?): Boolean = withContext(Dispatchers.IO) {
        val clean = pin.orEmpty()
        if (clean.length != 4 || !clean.all { it.isDigit() }) return@withContext false
        runCatching {
            val hash = sha256(clean)
            if (hash.isBlank()) return@runCatching false
            prefs.edit()
                .putString(KEY_PIN_HASH, hash)
                .putBoolean(KEY_PIN_ENABLED, true)
                .commit()
            _isPinEnabled.value = true
            true
        }.getOrDefault(false)
    }

    suspend fun disablePinAsync() = withContext(Dispatchers.IO) {
        runCatching {
            prefs.edit()
                .putBoolean(KEY_PIN_ENABLED, false)
                .putBoolean(KEY_BIOMETRIC_ENABLED, false)
                .commit()
        }
        _isPinEnabled.value = false
        _isBiometricEnabled.value = false
    }

    suspend fun verifyPinAsync(pin: String?): Boolean = withContext(Dispatchers.IO) {
        val clean = pin.orEmpty()
        if (clean.length != 4 || !clean.all { it.isDigit() }) return@withContext false
        runCatching {
            val savedHash = prefs.getString(KEY_PIN_HASH, null).orEmpty()
            savedHash.isNotBlank() && savedHash == sha256(clean)
        }.getOrDefault(false)
    }

    suspend fun setBiometricEnabledAsync(enabled: Boolean) = withContext(Dispatchers.IO) {
        runCatching {
            prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).commit()
        }
        _isBiometricEnabled.value = enabled
    }

    fun isDemoWiped(): Boolean = runCatching {
        prefs.getBoolean(KEY_DEMO_WIPED, false)
    }.getOrDefault(false)

    fun setDemoWiped(wiped: Boolean) {
        runCatching {
            prefs.edit().putBoolean(KEY_DEMO_WIPED, wiped).commit()
        }
        _isDemoWiped.value = wiped
    }

    suspend fun setDemoWipedAsync(wiped: Boolean) = withContext(Dispatchers.IO) {
        setDemoWiped(wiped)
    }

    fun isDemoDataPermanentlyDisabled(): Boolean = runCatching {
        prefs.getBoolean(KEY_DEMO_DATA_PERMANENTLY_DISABLED, false) ||
            prefs.getBoolean("demo_data_permanently_cleared", false)
    }.getOrDefault(false)

    fun permanentlyDisableDemoData(firebaseUid: String? = null, email: String? = null) {
        val resolvedEmail = email?.takeIf { it.isNotBlank() } ?: _profileEmail.value
        runCatching {
            prefs.edit()
                .putBoolean(KEY_DEMO_WIPED, true)
                .putBoolean(KEY_DEMO_DATA_PERMANENTLY_DISABLED, true)
                .putBoolean("demo_data_permanently_cleared", true)
                .apply {
                    if (!firebaseUid.isNullOrBlank()) putString("demo_cleared_firebase_uid", firebaseUid)
                    if (resolvedEmail.isNotBlank()) putString("demo_cleared_email", resolvedEmail)
                }
                .commit()
        }
        _isDemoWiped.value = true
        _isDemoDataPermanentlyDisabled.value = true
    }

    fun updateEditableProfile(
        displayName: String,
        bio: String,
        profession: String,
        photoUri: String? = null
    ) {
        val cleanName = displayName.trim().ifBlank { "Koshvanta Explorer" }
        val cleanBio = bio.trim().ifBlank { "Building disciplined wealth & smart cashflow" }
        val cleanProfession = profession.trim().ifBlank { "Financial Strategist" }
        prefs.edit().apply {
            putString(KEY_PROFILE_DISPLAY_NAME, cleanName)
            putString(KEY_PROFILE_BIO, cleanBio)
            putString(KEY_PROFILE_PROFESSION, cleanProfession)
            if (photoUri != null) {
                putString(KEY_PROFILE_PHOTO_URI, photoUri)
            }
        }.apply()
        _profileDisplayName.value = cleanName
        _profileBio.value = cleanBio
        _profileProfession.value = cleanProfession
        if (photoUri != null) {
            _profilePhotoUri.value = photoUri
        }
    }

    fun saveGoogleSyncedProfile(
        displayName: String?,
        email: String?,
        photoUrl: String?
    ) {
        val cleanEmail = email.orEmpty().trim()
        val resolvedName = displayName?.trim()?.takeIf { it.isNotBlank() }
            ?: _profileDisplayName.value.takeIf { it.isNotBlank() }
            ?: "Koshvanta Member"
        val resolvedPhoto = photoUrl?.trim()?.takeIf { it.isNotBlank() } ?: _profilePhotoUri.value
        prefs.edit()
            .putString(KEY_PROFILE_DISPLAY_NAME, resolvedName)
            .putString(KEY_PROFILE_EMAIL, cleanEmail)
            .putString(KEY_PROFILE_PHOTO_URI, resolvedPhoto)
            .putBoolean(KEY_PROFILE_GOOGLE_CONNECTED, true)
            .apply()
        _profileDisplayName.value = resolvedName
        _profileEmail.value = cleanEmail
        _profilePhotoUri.value = resolvedPhoto
        _profileGoogleConnected.value = true
    }

    fun clearGoogleSyncState() {
        val currentPhoto = _profilePhotoUri.value
        val keepLocalPhoto = currentPhoto.startsWith("file:") || currentPhoto.startsWith("content:")
        val nextPhoto = if (keepLocalPhoto) currentPhoto else ""
        prefs.edit()
            .putString(KEY_PROFILE_EMAIL, "")
            .putString(KEY_PROFILE_PHOTO_URI, nextPhoto)
            .putBoolean(KEY_PROFILE_GOOGLE_CONNECTED, false)
            .apply()
        _profileEmail.value = ""
        _profilePhotoUri.value = nextPhoto
        _profileGoogleConnected.value = false
    }

    fun completeGuestOnboarding() {
        prefs.edit()
            .putBoolean(KEY_HAS_COMPLETED_ONBOARDING_AUTH, true)
            .putBoolean(KEY_IS_SIGNED_IN, false)
            .putString(KEY_AUTH_PROVIDER, "GUEST")
            .apply()
        _hasCompletedOnboardingAuth.value = true
        _isSignedIn.value = false
        _authProvider.value = "GUEST"
    }

    fun markGoogleAuthCompleted() {
        prefs.edit()
            .putBoolean(KEY_HAS_COMPLETED_ONBOARDING_AUTH, true)
            .putBoolean(KEY_IS_SIGNED_IN, true)
            .putString(KEY_AUTH_PROVIDER, "GOOGLE")
            .apply()
        _hasCompletedOnboardingAuth.value = true
        _isSignedIn.value = true
        _authProvider.value = "GOOGLE"
    }

    fun saveEmailSignedInProfile(displayName: String?, email: String) {
        val cleanEmail = email.trim()
        val cleanName = displayName?.trim()?.takeIf { it.isNotBlank() }
            ?: cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() }.ifBlank { "Koshvanta Member" }
        prefs.edit()
            .putString(KEY_PROFILE_DISPLAY_NAME, cleanName)
            .putString(KEY_PROFILE_EMAIL, cleanEmail)
            .putBoolean(KEY_HAS_COMPLETED_ONBOARDING_AUTH, true)
            .putBoolean(KEY_IS_SIGNED_IN, true)
            .putString(KEY_AUTH_PROVIDER, "EMAIL")
            .apply()
        _profileDisplayName.value = cleanName
        _profileEmail.value = cleanEmail
        _hasCompletedOnboardingAuth.value = true
        _isSignedIn.value = true
        _authProvider.value = "EMAIL"
    }

    /**
     * Soft sign-out: clears active authentication session and returns user to Welcome/Auth screen
     * while preserving 100% of local transactions, budgets, cards, and game scores.
     */
    fun softSignOutSession() {
        prefs.edit()
            .putBoolean(KEY_IS_SIGNED_IN, false)
            .putBoolean(KEY_HAS_COMPLETED_ONBOARDING_AUTH, false)
            .putBoolean(KEY_PROFILE_GOOGLE_CONNECTED, false)
            .putString(KEY_AUTH_PROVIDER, "")
            .apply()
        _isSignedIn.value = false
        _hasCompletedOnboardingAuth.value = false
        _profileGoogleConnected.value = false
        _authProvider.value = ""
    }

    /**
     * Hard account & data wipe for Play Store compliance: clears all SharedPreferences across
     * Finance, Mind Arena, Metal Card Vault, Warranty Vault, and Impulse Cooldown, deletes
     * cached local avatar, and resets auth/onboarding to Welcome screen.
     */
    fun wipeAllPreferencesForAccountDeletion(context: Context) {
        val appCtx = context.applicationContext
        listOf(
            "koshvanta_prefs",
            "kushwanta_mind_arena_prefs",
            "metal_card_vault_prefs",
            "warranty_vault_prefs",
            "impulse_cooldown_prefs"
        ).forEach { prefName ->
            runCatching {
                appCtx.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().commit()
            }
        }
        runCatching {
            File(appCtx.filesDir, "koshvanta_profile_avatar.jpg").delete()
        }
        _profileDisplayName.value = "Koshvanta Explorer"
        _profileEmail.value = ""
        _profilePhotoUri.value = ""
        _profileBio.value = "Building disciplined wealth & smart cashflow"
        _profileProfession.value = "Financial Strategist"
        _profileGoogleConnected.value = false
        _hasCompletedOnboardingAuth.value = false
        _isSignedIn.value = false
        _authProvider.value = ""
        _isPinEnabled.value = false
        _isBiometricEnabled.value = false
        _streakCount.value = 0
        _personalBestStreak.value = 0
        _activeLoginDates.value = emptySet()
        _lastLoginDate.value = ""
        _isDemoWiped.value = true
        _isDemoDataPermanentlyDisabled.value = true
        prefs.edit()
            .putBoolean(KEY_DEMO_WIPED, true)
            .putBoolean(KEY_DEMO_DATA_PERMANENTLY_DISABLED, true)
            .commit()
    }

    fun persistPickedAvatarFromUri(context: Context, pickedUri: Uri): String? = runCatching {
        val targetFile = File(context.filesDir, "koshvanta_profile_avatar.jpg")
        context.contentResolver.openInputStream(pickedUri)?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return@runCatching null
        val fileUri = Uri.fromFile(targetFile).toString() + "?t=${System.currentTimeMillis()}"
        prefs.edit().putString(KEY_PROFILE_PHOTO_URI, fileUri).apply()
        _profilePhotoUri.value = fileUri
        fileUri
    }.getOrNull()

    fun getLinkedBillTransactionId(billId: Long): Long? {
        val txId = runCatching { prefs.getLong("$KEY_BILL_TX_PREFIX$billId", -1L) }.getOrDefault(-1L)
        return txId.takeIf { it > 0L }
    }

    fun setLinkedBillTransactionId(billId: Long, transactionId: Long?) {
        runCatching {
            prefs.edit().apply {
                if (transactionId != null && transactionId > 0L) {
                    putLong("$KEY_BILL_TX_PREFIX$billId", transactionId)
                } else {
                    remove("$KEY_BILL_TX_PREFIX$billId")
                }
            }.commit()
        }
    }

    fun clearAllBillTransactionLinks() {
        runCatching {
            val keys = prefs.all.keys.filter { it.startsWith(KEY_BILL_TX_PREFIX) }
            if (keys.isNotEmpty()) {
                val editor = prefs.edit()
                keys.forEach { editor.remove(it) }
                editor.commit()
            }
        }
    }

    fun isCustomCardsLocked(): Boolean = runCatching {
        prefs.getBoolean(KEY_CUSTOM_CARDS_LOCKED, false)
    }.getOrDefault(false)

    fun setCustomCardsLocked(locked: Boolean) {
        runCatching {
            prefs.edit().putBoolean(KEY_CUSTOM_CARDS_LOCKED, locked).commit()
        }
    }

    suspend fun setCustomCardsLockedAsync(locked: Boolean) = withContext(Dispatchers.IO) {
        setCustomCardsLocked(locked)
    }

    fun getStarterCardIds(): List<Long> = runCatching {
        (prefs.getString(KEY_STARTER_CARD_IDS, "") ?: "")
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }
    }.getOrDefault(emptyList())

    fun setStarterCardIds(ids: List<Long>) {
        runCatching {
            prefs.edit().putString(KEY_STARTER_CARD_IDS, ids.joinToString(",")).commit()
        }
    }

    fun setWorkMonthlyIncomeUsd(incomeUsd: Double) {
        val safe = incomeUsd.coerceAtLeast(0.0)
        runCatching {
            prefs.edit().putString(KEY_WORK_MONTHLY_INCOME_USD, safe.toString()).apply()
        }
        _workMonthlyIncomeUsd.value = safe
    }

    fun setDailyWorkHours(hours: Double) {
        val safe = hours.coerceIn(1.0, 16.0)
        runCatching {
            prefs.edit().putString(KEY_DAILY_WORK_HOURS, safe.toString()).apply()
        }
        _dailyWorkHours.value = safe
    }

    fun saveAndEnablePin(pin: String) {
        val clean = pin
        if (clean.length != 4 || !clean.all { it.isDigit() }) return
        runCatching {
            val hash = sha256(clean)
            prefs.edit()
                .putString(KEY_PIN_HASH, hash)
                .putBoolean(KEY_PIN_ENABLED, true)
                .apply()
            _isPinEnabled.value = true
        }
    }

    fun disablePin() {
        runCatching {
            prefs.edit()
                .putBoolean(KEY_PIN_ENABLED, false)
                .putBoolean(KEY_BIOMETRIC_ENABLED, false)
                .apply()
        }
        _isPinEnabled.value = false
        _isBiometricEnabled.value = false
    }

    fun verifyPin(pin: String): Boolean {
        if (pin.length != 4 || !pin.all { it.isDigit() }) return false
        return runCatching {
            val savedHash = prefs.getString(KEY_PIN_HASH, null).orEmpty()
            savedHash.isNotBlank() && savedHash == sha256(pin)
        }.getOrDefault(false)
    }

    fun hasPinConfigured(): Boolean = runCatching {
        !prefs.getString(KEY_PIN_HASH, null).isNullOrBlank()
    }.getOrDefault(false)

    fun setBiometricEnabled(enabled: Boolean) {
        runCatching {
            prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
        }
        _isBiometricEnabled.value = enabled
    }

    fun setAlertThresholdPercent(percent: Int) {
        prefs.edit().putInt(KEY_ALERT_THRESHOLD, percent).apply()
        _alertThresholdPercent.value = percent
    }

    fun setDailyReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DAILY_REMINDER, enabled).apply()
        _dailyReminderEnabled.value = enabled
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
        _soundEffectsEnabled.value = enabled
        SoundEffectManager.isEnabled = enabled
    }

    fun setBudgetRolloverEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BUDGET_ROLLOVER, enabled).apply()
        _budgetRolloverEnabled.value = enabled
    }

    fun setDailySpendingLimitUsd(limitUsd: Double) {
        prefs.edit().putFloat(KEY_DAILY_LIMIT, limitUsd.toFloat()).apply()
        _dailySpendingLimitUsd.value = limitUsd
    }

    fun setWeeklyBudgetLimitUsd(limitUsd: Double) {
        prefs.edit().putFloat(KEY_WEEKLY_LIMIT, limitUsd.toFloat()).apply()
        _weeklyBudgetLimitUsd.value = limitUsd
    }

    fun setLargeTextAccessibility(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LARGE_TEXT, enabled).apply()
        _largeTextAccessibility.value = enabled
    }

    fun setActiveProfile(profile: String) {
        prefs.edit().putString(KEY_ACTIVE_PROFILE, profile).apply()
        _activeProfile.value = profile
    }

    fun addFamilyProfile(profile: String) {
        val clean = profile.trim()
        if (clean.isEmpty()) return
        val updated = (_familyProfiles.value + clean).distinct()
        prefs.edit().putString(KEY_FAMILY_PROFILES, updated.joinToString(",")).apply()
        _familyProfiles.value = updated
        setActiveProfile(clean)
    }

    fun toggleWidget(widgetId: String) {
        val updated = _enabledWidgets.value.toMutableSet()
        if (updated.contains(widgetId)) {
            if (updated.size > 1) updated.remove(widgetId)
        } else {
            updated.add(widgetId)
        }
        prefs.edit().putString(KEY_ENABLED_WIDGETS, updated.joinToString(",")).apply()
        _enabledWidgets.value = updated
    }

    fun recordDailyLogin(referenceTimeMillis: Long = System.currentTimeMillis()): Boolean {
        return recordDailyActivityForTime(referenceTimeMillis)
    }

    fun recordDailyLogStreak(referenceTimeMillis: Long = System.currentTimeMillis()): Boolean {
        return recordDailyActivityForTime(referenceTimeMillis)
    }

    /**
     * Tracks consecutive daily activity.
     * - Same day as last login: keeps streak unchanged (idempotent).
     * - Consecutive next day: increments streak by +1.
     * - Gap of >= 1 missed day (or first launch): resets active streak to 1 while preserving personal best.
     * Returns true if streak increased (or started fresh on a new day).
     */
    internal fun recordDailyActivityForTime(timeMillis: Long): Boolean {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayCal = Calendar.getInstance().apply { timeInMillis = timeMillis }
        val todayStr = df.format(todayCal.time)

        val yesterdayCal = (todayCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterdayStr = df.format(yesterdayCal.time)

        val migrated = prefs.getBoolean(KEY_STREAK_V2_MIGRATED, false)
        val lastDate = if (migrated) (prefs.getString(KEY_LAST_LOG_DATE, "") ?: "") else ""
        val currentStreak = if (migrated) _streakCount.value else 0
        val currentBest = if (migrated) _personalBestStreak.value else 0

        if (migrated && lastDate == todayStr && currentStreak >= 1) {
            // Ensure today's date is present in login history
            if (!_activeLoginDates.value.contains(todayStr)) {
                val updatedDates = (_activeLoginDates.value + todayStr).sorted().takeLast(365).toSet()
                prefs.edit().putString(KEY_LOGIN_DATES, updatedDates.joinToString(",")).apply()
                _activeLoginDates.value = updatedDates
            }
            if (_lastLoginDate.value != todayStr) {
                _lastLoginDate.value = todayStr
            }
            return false
        }

        val newStreak = when {
            !migrated || lastDate.isEmpty() -> 1
            lastDate == yesterdayStr -> currentStreak + 1
            else -> 1 // Missed one or more days -> reset consecutive streak cleanly to Day 1
        }
        val newBest = maxOf(currentBest, newStreak)
        val updatedDates = ((if (migrated) _activeLoginDates.value else emptySet()) + todayStr)
            .sorted()
            .takeLast(365)
            .toSet()

        prefs.edit()
            .putBoolean(KEY_STREAK_V2_MIGRATED, true)
            .putString(KEY_LAST_LOG_DATE, todayStr)
            .putInt(KEY_STREAK_COUNT, newStreak)
            .putInt(KEY_BEST_STREAK, newBest)
            .putString(KEY_LOGIN_DATES, updatedDates.joinToString(","))
            .apply()

        _streakCount.value = newStreak
        _personalBestStreak.value = newBest
        _activeLoginDates.value = updatedDates
        _lastLoginDate.value = todayStr
        return true
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ACCENT_PALETTE = "accent_palette"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_CUSTOM_SYMBOL = "custom_symbol"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_PIN_ENABLED = "pin_enabled"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_ALERT_THRESHOLD = "alert_threshold"
        private const val KEY_DAILY_REMINDER = "daily_reminder"
        private const val KEY_SOUND_ENABLED = "sound_effects_enabled"
        private const val KEY_BUDGET_ROLLOVER = "budget_rollover"
        private const val KEY_DAILY_LIMIT = "daily_limit_usd"
        private const val KEY_WEEKLY_LIMIT = "weekly_limit_usd"
        private const val KEY_LARGE_TEXT = "large_text"
        private const val KEY_ACTIVE_PROFILE = "active_profile"
        private const val KEY_FAMILY_PROFILES = "family_profiles"
        private const val KEY_ENABLED_WIDGETS = "enabled_widgets"
        private const val KEY_STREAK_COUNT = "streak_count"
        private const val KEY_BEST_STREAK = "best_streak"
        private const val KEY_LAST_LOG_DATE = "last_log_date"
        private const val KEY_LOGIN_DATES = "active_login_dates"
        private const val KEY_STREAK_V2_MIGRATED = "streak_real_v2_migrated"
        private const val KEY_DEMO_WIPED = "demo_wiped"
        private const val KEY_DEMO_DATA_PERMANENTLY_DISABLED = "demo_data_permanently_disabled"
        private const val KEY_PROFILE_DISPLAY_NAME = "profile_display_name"
        private const val KEY_PROFILE_EMAIL = "profile_email"
        private const val KEY_PROFILE_PHOTO_URI = "profile_photo_uri"
        private const val KEY_PROFILE_BIO = "profile_bio"
        private const val KEY_PROFILE_PROFESSION = "profile_profession"
        private const val KEY_PROFILE_GOOGLE_CONNECTED = "profile_google_connected"
        private const val KEY_HAS_COMPLETED_ONBOARDING_AUTH = "has_completed_onboarding_auth"
        private const val KEY_IS_SIGNED_IN = "is_signed_in"
        private const val KEY_AUTH_PROVIDER = "auth_provider"
        private const val KEY_BILL_TX_PREFIX = "bill_tx_link_"
        private const val KEY_CUSTOM_CARDS_LOCKED = "vault_custom_cards_locked"
        private const val KEY_STARTER_CARD_IDS = "vault_starter_card_ids"
        private const val KEY_WORK_MONTHLY_INCOME_USD = "work_monthly_income_usd"
        private const val KEY_DAILY_WORK_HOURS = "daily_work_hours"

        const val TARGET_STREAK_CHALLENGE_DAYS = 14
        const val WIDGET_STREAK = "STREAK"
        const val WIDGET_PRESETS = "PRESETS"
        const val WIDGET_ACCOUNTS = "ACCOUNTS"
        const val WIDGET_DONUT = "DONUT"
        const val WIDGET_TRENDS = "TRENDS"
        const val WIDGET_BILLS = "BILLS"
        private const val DEFAULT_WIDGETS = "STREAK,PRESETS,ACCOUNTS,DONUT,TRENDS,BILLS"
    }
}
