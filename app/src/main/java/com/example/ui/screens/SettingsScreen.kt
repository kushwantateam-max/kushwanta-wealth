package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccentPalette
import com.example.data.model.AccountEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.FinancialBook
import com.example.data.model.ThemeMode
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppPreferences
import com.example.data.prefs.AppStrings
import com.example.domain.calculator.WorkHourCostCalculator
import com.example.domain.util.NotificationHelper
import com.example.ui.components.GlassCard
import com.example.ui.components.GlobalCurrencyPickerDialog
import com.example.ui.components.SetMasterPinDialog
import com.example.ui.components.VerifyPinToDisableDialog
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.StateRed
import com.example.ui.viewmodel.KoshvantaViewModel
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    transactions: List<TransactionWithDetails>,
    currentBook: FinancialBook,
    currentCurrency: CurrencyCode,
    onBackClick: (() -> Unit)? = null,
    onOpenEliteHub: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = LocalAccentColor.current
    val lang by viewModel.appLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val accentPalette by viewModel.accentPalette.collectAsState()
    val customSymbol by viewModel.customCurrencySymbol.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val alertThreshold by viewModel.alertThresholdPercent.collectAsState()
    val dailyReminder by viewModel.dailyReminderEnabled.collectAsState()
    val soundEffectsEnabled by viewModel.soundEffectsEnabled.collectAsState()
    val largeText by viewModel.largeTextAccessibility.collectAsState()
    val enabledWidgets by viewModel.enabledWidgets.collectAsState()
    val customWorkIncomeUsd by viewModel.workMonthlyIncomeUsd.collectAsState()
    val effectiveWorkIncomeUsd by viewModel.effectiveMonthlyIncomeUsd.collectAsState()
    val dailyWorkHours by viewModel.dailyWorkHours.collectAsState()
    val isDemoDataPermanentlyDisabled by viewModel.isDemoDataPermanentlyDisabled.collectAsState()

    var showWipeConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyScreen by remember { mutableStateOf(false) }
    var showCurrencyPicker by remember { mutableStateOf(false) }
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showDisablePinDialog by remember { mutableStateOf(false) }
    var showWorkRateDialog by remember { mutableStateOf(false) }

    if (showPrivacyPolicyScreen) {
        PrivacyPolicyScreen(
            isDarkTheme = com.example.ui.theme.LocalIsDarkTheme.current,
            onBackClick = { showPrivacyPolicyScreen = false },
            modifier = modifier
        )
        return
    }

    // OpenDocument launcher for zero-permission JSON Backup Restore
    val restoreJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.restoreJsonBackup(context, uri)
        }
    }

    // POST_NOTIFICATIONS runtime permission launcher for Android 13+
    val notificationPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.setDailyReminderEnabled(true)
            viewModel.sendTestNotification(context)
        }
    }

    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            title = { Text("Wipe All Data?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all accounts, transactions, budgets, presets, and savings goals from your device.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.wipeAllData()
                        showWipeConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StateRed)
                ) { Text("Wipe Everything", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showWipeConfirmDialog = false }) { Text("Cancel") } }
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
                    modifier = Modifier.testTag("settings_confirm_delete_account_button")
                ) {
                    Text("Delete Forever", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showCurrencyPicker) {
        GlobalCurrencyPickerDialog(
            currentCurrency = currentCurrency,
            customSymbol = customSymbol,
            onSelectCurrency = { viewModel.switchCurrency(it) },
            onSaveCustomSymbol = { viewModel.setCustomCurrencySymbol(it) },
            onDismiss = { showCurrencyPicker = false }
        )
    }

    if (showSetPinDialog) {
        SetMasterPinDialog(
            onSavePin = { pin ->
                viewModel.enableMasterPin(pin)
                showSetPinDialog = false
            },
            onDismiss = { showSetPinDialog = false }
        )
    }

    if (showDisablePinDialog) {
        VerifyPinToDisableDialog(
            onVerify = { viewModel.verifyPin(it) },
            onVerifiedDisable = {
                viewModel.disableMasterPin()
                showDisablePinDialog = false
            },
            onDismiss = { showDisablePinDialog = false }
        )
    }

    if (showWorkRateDialog) {
        val currentDisplayIncome = viewModel.fromUsd(customWorkIncomeUsd)
        var incomeInput by remember(showWorkRateDialog) {
            mutableStateOf(
                if (currentDisplayIncome > 0.0) String.format(Locale.US, "%.0f", currentDisplayIncome) else ""
            )
        }
        var hoursInput by remember(showWorkRateDialog) {
            mutableStateOf(
                if (dailyWorkHours % 1.0 == 0.0) dailyWorkHours.toInt().toString() else String.format(Locale.US, "%.1f", dailyWorkHours)
            )
        }
        AlertDialog(
            onDismissRequest = { showWorkRateDialog = false },
            title = { Text("Monthly Income & Work Hours", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Used locally for Quick Expense Life-Energy Cost alerts on large purchases (hidden for small daily spends).",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = incomeInput,
                        onValueChange = { incomeInput = it },
                        label = { Text("Monthly Take-Home (${viewModel.activeSymbol()})") },
                        placeholder = { Text("Optional • Auto-reads logged income if blank") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("work_rate_income_input")
                    )
                    Text("Daily Work Hours (default 8 hrs/day)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(6.0, 8.0, 9.0, 10.0, 12.0).forEach { hr ->
                            val selected = (hoursInput.toDoubleOrNull() ?: 8.0) == hr
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { hoursInput = hr.toInt().toString() }
                            ) {
                                Text(
                                    text = "${hr.toInt()}h",
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = hoursInput,
                        onValueChange = { hoursInput = it },
                        label = { Text("Hours Worked Per Day (1–16)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("work_rate_hours_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedIncome = incomeInput.trim().toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                        val parsedHours = (hoursInput.trim().toDoubleOrNull() ?: WorkHourCostCalculator.DEFAULT_DAILY_WORK_HOURS)
                            .coerceIn(1.0, 16.0)
                        viewModel.updateWorkRateSettings(parsedIncome, parsedHours)
                        showWorkRateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier.testTag("work_rate_save_button")
                ) {
                    Text("Save Rate", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    if (customWorkIncomeUsd > 0.0) {
                        TextButton(onClick = {
                            val parsedHours = (hoursInput.trim().toDoubleOrNull() ?: WorkHourCostCalculator.DEFAULT_DAILY_WORK_HOURS)
                                .coerceIn(1.0, 16.0)
                            viewModel.updateWorkRateSettings(0.0, parsedHours)
                            showWorkRateDialog = false
                        }) {
                            Text("Use Auto")
                        }
                    }
                    TextButton(onClick = { showWorkRateDialog = false }) { Text("Cancel") }
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Profile Hub",
                            tint = accentColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(AppStrings.preferencesAndSystem(lang), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = "Themes, global currencies, PIN security, alerts & local PDF/JSON backups",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        item {
            SettingsSectionLabel("APPEARANCE & LEDGER", accentColor)
        }

        // 1. Theme Mode & Custom Accent Palette
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Visual Theme & Custom Accent", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            val sel = mode == themeMode
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (sel) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setThemeMode(mode) }
                                    .testTag("theme_mode_${mode.id}")
                            ) {
                                Text(
                                    text = mode.label,
                                    color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 9.dp)
                                )
                            }
                        }
                    }
                    Text("Accent Color:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AccentPalette.entries.forEach { palette ->
                            val sel = palette == accentPalette
                            Box(
                                modifier = Modifier
                                    .size(if (sel) 34.dp else 28.dp)
                                    .clip(CircleShape)
                                    .background(Color(palette.primaryHex))
                                    .border(if (sel) 2.dp else 0.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    .clickable { viewModel.setAccentPalette(palette) }
                            )
                        }
                    }
                }
            }
        }

        // 2. Active Financial Book
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Book, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Active Financial Book", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    FinancialBook.entries.forEach { book ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.switchBook(book) }.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentBook == book,
                                onClick = { viewModel.switchBook(book) },
                                colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${book.title} Ledger", fontWeight = if (currentBook == book) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }

        item {
            SettingsSectionLabel("CURRENCY & INCOME ENGINE", accentColor)
        }

        // 3. Worldwide Currency Picker (36+ Countries + Custom Symbol)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Worldwide Currency (${CurrencyCode.entries.size} Countries)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCurrencyPicker = true }
                            .testTag("settings_open_currency_picker")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentCurrency.flag, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("${currentCurrency.countryName} • ${currentCurrency.code}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Active Symbol: ${viewModel.activeSymbol()}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("Change", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 3b. Optional Monthly Income & Daily Work Hours (Work-Hour Cost Engine)
        item {
            val activeIncomeDisplay = viewModel.fromUsd(effectiveWorkIncomeUsd)
            val rateSummary = remember(activeIncomeDisplay, dailyWorkHours) {
                WorkHourCostCalculator.computeRates(
                    monthlyIncome = activeIncomeDisplay,
                    dailyWorkHours = dailyWorkHours
                )
            }
            val hoursLabel = if (dailyWorkHours % 1.0 == 0.0) "${dailyWorkHours.toInt()} hrs/day" else "${String.format(Locale.US, "%.1f", dailyWorkHours)} hrs/day"
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_work_rate_card"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Schedule, contentDescription = "Work-Hour Rate", tint = accentColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Monthly Income & Work Hours", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = "Powers Life-Energy alerts on large expenses ($hoursLabel)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        TextButton(
                            onClick = { showWorkRateDialog = true },
                            modifier = Modifier.testTag("settings_edit_work_rate_button")
                        ) {
                            Text(
                                text = if (customWorkIncomeUsd > 0.0) "Edit" else "Set",
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showWorkRateDialog = true }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (customWorkIncomeUsd > 0.0) "Custom Monthly Income" else "Monthly Income (Auto)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (rateSummary.isConfigured) viewModel.formatAmount(effectiveWorkIncomeUsd) else "Not configured",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (rateSummary.isConfigured) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Hourly: ${viewModel.formatAmount(viewModel.toUsd(rateSummary.hourlyRate))}/hr",
                                        fontSize = 11.sp,
                                        color = accentColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "1-Day: ${viewModel.formatAmount(viewModel.toUsd(rateSummary.dailyRate))}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "Smart Threshold: Hidden under ${viewModel.formatAmount(viewModel.toUsd(rateSummary.halfDayThresholdAmount))} (50% of 1-day)",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Tap Set to enter monthly income (or log Income) • Defaults to 8 hrs/day",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SettingsSectionLabel("SECURITY & PRIVACY", accentColor)
        }

        // 4. Functional Security: 4-Digit Master PIN + Biometric Shield
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = accentColor)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("4-Digit Master PIN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = if (isPinEnabled) "PIN active • Protects app launch" else "Tap to create 4-digit security PIN",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isPinEnabled,
                            onCheckedChange = { checked ->
                                if (checked) showSetPinDialog = true else showDisablePinDialog = true
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor),
                            modifier = Modifier.testTag("settings_pin_switch")
                        )
                    }

                    if (isPinEnabled) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showSetPinDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Change PIN", fontSize = 11.sp) }
                            Button(
                                onClick = { viewModel.lockAppNow() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Lock App Now", fontSize = 11.sp, color = Color.White) }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = accentColor)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Biometric App Shield", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Fingerprint / Face ID unlock", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled && !isPinEnabled) {
                                    showSetPinDialog = true
                                }
                                viewModel.setBiometricEnabled(enabled)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor),
                            modifier = Modifier.testTag("settings_biometric_switch")
                        )
                    }
                }
            }
        }

        item {
            SettingsSectionLabel("ALERTS, AUDIO & ACCESSIBILITY", accentColor)
        }

        // 5. Smart Alerts & Notification Scheduler
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Budget Alerts & Daily Reminders", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Text("Trigger threshold alert at:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(50, 75, 80, 90, 100).forEach { pct ->
                            val sel = pct == alertThreshold
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (sel) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f).clickable { viewModel.setAlertThreshold(pct) }
                            ) {
                                Text(
                                    "$pct%",
                                    color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily Expense Reminder", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Local status bar notification alerts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = dailyReminder,
                            onCheckedChange = { enabled ->
                                if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    !NotificationHelper.hasNotificationPermission(context)
                                ) {
                                    notificationPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.setDailyReminderEnabled(enabled)
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = accentColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Sound Effects & Alert Audio", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Chimes for alerts, transactions, PIN & keypad", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = soundEffectsEnabled,
                            onCheckedChange = { viewModel.setSoundEffectsEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor),
                            modifier = Modifier.testTag("settings_sound_switch")
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.sendTestNotification(context) },
                            modifier = Modifier.weight(1f).testTag("settings_test_alert_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Test Alert Notification", fontSize = 11.sp) }

                        OutlinedButton(
                            onClick = { viewModel.previewAlertSound() },
                            modifier = Modifier.weight(1f).testTag("settings_preview_sound_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Preview Alert Sound", fontSize = 11.sp) }
                    }
                }
            }
        }

        // 6. Language & Accessibility + Dashboard Widgets
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Language (भाषा) & Accessibility", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (lang == "EN") accentColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f).clickable { if (lang != "EN") viewModel.toggleLanguage() }
                        ) {
                            Text("English (Global)", color = if (lang == "EN") Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(vertical = 10.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (lang == "HI") accentColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f).clickable { if (lang != "HI") viewModel.toggleLanguage() }
                        ) {
                            Text("हिन्दी (Hindi)", color = if (lang == "HI") Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(vertical = 10.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessibilityNew, contentDescription = null, tint = accentColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Large Text Accessibility Mode", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Switch(
                            checked = largeText,
                            onCheckedChange = { viewModel.setLargeTextAccessibility(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = accentColor)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DashboardCustomize, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Customize Home Widgets", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            AppPreferences.WIDGET_STREAK to "Streak Banner",
                            AppPreferences.WIDGET_PRESETS to "Quick Presets",
                            AppPreferences.WIDGET_ACCOUNTS to "Accounts",
                            AppPreferences.WIDGET_DONUT to "Donut Chart",
                            AppPreferences.WIDGET_TRENDS to "Trends",
                            AppPreferences.WIDGET_BILLS to "Bills"
                        ).forEach { (id, label) ->
                            val active = enabledWidgets.contains(id)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (active) accentColor.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { viewModel.toggleDashboardWidget(id) }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                                    Checkbox(checked = active, onCheckedChange = { viewModel.toggleDashboardWidget(id) })
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            SettingsSectionLabel("REPORTS & LOCAL BACKUPS", accentColor)
        }

        // 7. Real PDF/CSV Export & JSON Backup / Restore
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Backup, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Reports & Local JSON Backups", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.exportPdfReport(context, accounts, transactions) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.exportCsvReport(context, transactions) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("Export CSV", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.exportJsonBackup(context) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) { Text("Export .JSON", fontSize = 11.sp, color = Color.White) }
                        OutlinedButton(
                            onClick = { restoreJsonLauncher.launch(arrayOf("application/json", "*/*")) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore .JSON", fontSize = 11.sp)
                        }
                    }
                    OutlinedButton(
                        onClick = { viewModel.runIntegrityCheck() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Validate Local SQLite Integrity (SHA-256)", fontSize = 11.sp) }
                }
            }
        }

        item {
            SettingsSectionLabel("COMMUNITY, LEGAL & SUPPORT", accentColor)
        }

        // 7b. Community, Legal & Support — Elite Hub, Exact-Match Privacy Policy & Support Email
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenEliteHub() }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .testTag("settings_elite_hub_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = AppStrings.eliteHubTitle(lang),
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.eliteHubTitle(lang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = AppStrings.eliteHubEntrySubtitle(lang),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = AppStrings.eliteHubTitle(lang),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showPrivacyPolicyScreen = true }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .testTag("settings_privacy_policy_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Policy,
                                contentDescription = "Privacy Policy",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Privacy Policy",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Data safety, local storage & disclaimer",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Privacy Policy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                runCatching {
                                    val intent = Intent(
                                        Intent.ACTION_SENDTO,
                                        Uri.parse("mailto:kushwanta.team@gmail.com")
                                    )
                                    context.startActivity(intent)
                                }
                            }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .testTag("settings_contact_support_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Contact & Privacy Support",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Contact & Privacy Support",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "kushwanta.team@gmail.com",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = accentColor
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Email Kushwanta Support",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                runCatching {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://kushwantateam-max.github.io/kushwanta-privacy/")
                                    )
                                    context.startActivity(intent)
                                }
                            }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .testTag("settings_online_privacy_policy_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "View Online Privacy Policy",
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "View Online Privacy Policy ↗",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "kushwantateam-max.github.io/kushwanta-privacy",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Online Privacy Policy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        item {
            SettingsSectionLabel("DATA & RESET CONTROLS", accentColor)
        }

        // 8. Seamless Data & Reset Controls (Single Clean Delete Account & All Data Row)
        item {
            val dangerCrimson = Color(0xFFFF3366)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isDemoDataPermanentlyDisabled) {
                    TextButton(
                        onClick = { viewModel.reloadDemoData() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_reload_demo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reload Sample Portfolio",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showDeleteAccountDialog = true }
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                        .testTag("settings_delete_account_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Delete Account and All Data",
                            tint = dangerCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Delete Account & All Data",
                                color = dangerCrimson,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Permanently remove linked account and erase all local data",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
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
private fun SettingsSectionLabel(title: String, accentColor: Color) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        color = accentColor,
        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
    )
}
