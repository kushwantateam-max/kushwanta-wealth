package com.example.ui.components

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.DebtEntity
import com.example.data.model.EmojiMatcher
import com.example.data.model.NotificationHistoryEntity
import com.example.data.model.QuickPresetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.domain.calculator.FinanceCalculators
import com.example.domain.util.BiometricHelper
import com.example.ui.theme.DarkCanvasBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.theme.StateYellow
import com.example.ui.viewmodel.KoshvantaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// -------------------------------------------------------------
// 1. Lock Screen Overlay (4-Digit Master PIN + Crash-Proof Biometric)
// -------------------------------------------------------------
@Composable
fun LockScreenOverlay(
    isBiometricEnabled: Boolean,
    onVerifyPin: (String, (Boolean) -> Unit) -> Unit,
    onBiometricSuccess: () -> Unit,
    onKeyTick: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var enteredPin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    // Keep back presses safely contained on lock screen
    BackHandler(enabled = true) { }

    val triggerUserBiometric: () -> Unit = {
        try {
            if (!isBiometricEnabled) {
                errorMsg = "Biometric Shield is off — enter your 4-Digit Master PIN."
            } else if (!BiometricHelper.isBiometricAvailable(context)) {
                errorMsg = "Biometrics unavailable — enter your 4-Digit Master PIN."
            } else {
                BiometricHelper.authenticate(
                    context = context,
                    onSuccess = {
                        try {
                            onBiometricSuccess()
                        } catch (_: Throwable) {
                            // Keep lock screen responsive on unexpected callback errors
                        }
                    },
                    onError = { msg -> errorMsg = msg }
                )
            }
        } catch (_: Throwable) {
            // Silently ignore biometric sensor / API exceptions so the 4-digit PIN pad stays open
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkCanvasBg, Color(0xFF0E131D), DarkCanvasBg)
                )
            )
            .systemBarsPadding()
            .padding(24.dp)
            .testTag("lock_screen_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(NeonMint.copy(alpha = 0.14f))
                    .border(1.5.dp, NeonMint.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = NeonMint,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "KOSHVANTA SHIELD",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Enter your 4-Digit Master PIN to unlock ledger",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            // 4-dot PIN indicator
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(4) { idx ->
                    val filled = idx < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (filled) NeonMint else Color.White.copy(alpha = 0.16f))
                            .border(
                                1.dp,
                                if (filled) NeonMint else NeonMint.copy(alpha = 0.35f),
                                CircleShape
                            )
                            .testTag("pin_dot_$idx")
                    )
                }
            }

            Box(
                modifier = Modifier.heightIn(min = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (errorMsg != null) {
                    Text(
                        text = errorMsg.orEmpty(),
                        color = StateRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("pin_error_text")
                    )
                }
            }

            // Keypad grid
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIO", "0", "DEL")
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        row.forEach { key ->
                            val isBioKey = key == "BIO"
                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    when (key) {
                                        "DEL" -> {
                                            if (!isVerifying) {
                                                onKeyTick()
                                                if (enteredPin.isNotEmpty()) {
                                                    enteredPin = enteredPin.dropLast(1)
                                                    errorMsg = null
                                                }
                                            }
                                        }
                                        "BIO" -> triggerUserBiometric()
                                        else -> {
                                            if (!isVerifying && enteredPin.length < 4) {
                                                val nextPin = enteredPin + key
                                                enteredPin = nextPin
                                                errorMsg = null
                                                if (nextPin.length < 4) {
                                                    onKeyTick()
                                                } else {
                                                    isVerifying = true
                                                    onVerifyPin(nextPin) { ok ->
                                                        isVerifying = false
                                                        if (!ok) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            errorMsg = "Incorrect 4-digit PIN. Try again."
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = CircleShape,
                                color = DarkSurfaceElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (isBioKey && isBiometricEnabled) NeonMint.copy(alpha = 0.45f)
                                    else NeonMint.copy(alpha = 0.22f)
                                ),
                                modifier = Modifier
                                    .size(72.dp)
                                    .testTag(
                                        when (key) {
                                            "BIO" -> "biometric_unlock_button"
                                            "DEL" -> "pin_key_del"
                                            else -> "pin_key_$key"
                                        }
                                    )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    when (key) {
                                        "DEL" -> Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = "Backspace",
                                            tint = Color.White
                                        )
                                        "BIO" -> Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = "Biometric Unlock",
                                            tint = if (isBiometricEnabled) NeonMint else Color.White.copy(alpha = 0.4f),
                                            modifier = Modifier.size(30.dp)
                                        )
                                        else -> Text(
                                            text = key,
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Set / Confirm 4-Digit Master PIN Dialog
// -------------------------------------------------------------
@Composable
fun SetMasterPinDialog(
    onSavePin: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var firstPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    val accentColor = LocalAccentColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set 4-Digit Master PIN", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Create a 4-digit security PIN to lock Koshvanta on app launch.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = firstPin,
                    onValueChange = {
                        if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                            firstPin = it
                            errorText = null
                        }
                    },
                    label = { Text("Enter 4-Digit PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("pin_first_input")
                )
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = {
                        if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                            confirmPin = it
                            errorText = null
                        }
                    },
                    label = { Text("Confirm 4-Digit PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("pin_confirm_input")
                )
                if (errorText != null) {
                    Text(text = errorText!!, color = StateRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        firstPin.length != 4 -> errorText = "PIN must be exactly 4 digits."
                        firstPin != confirmPin -> errorText = "PINs do not match."
                        else -> onSavePin(firstPin)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier.testTag("pin_save_button")
            ) {
                Text("Enable Lock", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun VerifyPinToDisableDialog(
    onVerify: (String) -> Boolean,
    onVerifiedDisable: () -> Unit,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Verify Current PIN", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter your current 4-digit PIN to disable Master PIN lock.", fontSize = 12.sp)
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) { pin = it; error = null } },
                    label = { Text("4-Digit PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) Text(error!!, color = StateRed, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (onVerify(pin)) onVerifiedDisable() else error = "Incorrect PIN."
                },
                colors = ButtonDefaults.buttonColors(containerColor = StateRed)
            ) {
                Text("Disable PIN", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// -------------------------------------------------------------
// 3. Global Searchable Currency Picker (36+ Countries + Custom Symbol)
// -------------------------------------------------------------
@Composable
fun GlobalCurrencyPickerDialog(
    currentCurrency: CurrencyCode,
    customSymbol: String,
    onSelectCurrency: (CurrencyCode) -> Unit,
    onSaveCustomSymbol: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var symbolOverride by remember { mutableStateOf(customSymbol) }
    val accentColor = LocalAccentColor.current

    val filtered = remember(query) {
        if (query.isBlank()) CurrencyCode.entries
        else CurrencyCode.entries.filter {
            it.countryName.contains(query, ignoreCase = true) ||
                it.code.contains(query, ignoreCase = true) ||
                it.displayName.contains(query, ignoreCase = true) ||
                it.symbol.contains(query, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Global Currencies (${CurrencyCode.entries.size} Countries)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search country, INR, USD, AED, Euro...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("currency_search_input")
                )

                // Custom Symbol Override
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = symbolOverride,
                        onValueChange = { if (it.length <= 4) symbolOverride = it },
                        label = { Text("Custom Symbol (opt)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { onSaveCustomSymbol(symbolOverride) },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Apply", fontSize = 12.sp)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 310.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered, key = { it.code }) { c ->
                        val selected = c == currentCurrency
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) accentColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectCurrency(c)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(text = c.flag, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${c.countryName} (${c.code})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = c.displayName,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = c.symbol,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = if (selected) accentColor else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

// -------------------------------------------------------------
// 4. Add / Edit Quick 1-Tap Preset Dialog (Smart Auto-Emoji + Local Price)
// -------------------------------------------------------------
@Composable
fun PresetEditDialog(
    viewModel: KoshvantaViewModel,
    existingPreset: QuickPresetEntity?,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(existingPreset?.title ?: "") }
    var manualEmoji by remember { mutableStateOf<String?>(existingPreset?.emoji) }
    val autoEmoji = remember(title, manualEmoji) {
        manualEmoji?.takeIf { it.isNotBlank() } ?: EmojiMatcher.suggestEmoji(title)
    }
    var amountText by remember {
        mutableStateOf(
            if (existingPreset != null) {
                String.format(Locale.US, "%.2f", viewModel.fromUsd(existingPreset.amountUsd))
            } else ""
        )
    }
    val expenseCats = remember(categories) { categories.filter { it.isExpense } }
    var selectedCatName by remember {
        mutableStateOf(existingPreset?.categoryName ?: expenseCats.firstOrNull()?.name ?: "Food & Drinks")
    }
    val accentColor = LocalAccentColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = autoEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (existingPreset == null) "New Quick Preset" else "Edit Preset",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        // Live auto-suggest emoji as user types!
                        manualEmoji = EmojiMatcher.suggestEmoji(it)
                    },
                    label = { Text("Item Name (e.g. Coffee, Petrol, Chai)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("preset_title_input")
                )

                Text(text = "Auto-matched Symbol (tap to override):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EmojiMatcher.PRESET_EMOJIS.forEach { e ->
                        val isSel = e == autoEmoji
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { manualEmoji = e }
                        ) {
                            Text(text = e, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Local Price (${viewModel.activeSymbol()} ${viewModel.currentCurrency.value.code})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("preset_amount_input")
                )

                Text(text = "Category:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    expenseCats.forEach { cat ->
                        val isSel = cat.name == selectedCatName
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedCatName = cat.name }
                        ) {
                            Text(
                                text = cat.name,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                if (existingPreset != null) {
                    OutlinedButton(
                        onClick = {
                            viewModel.deletePreset(existingPreset.id)
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StateRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Remove Preset", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0.0) {
                        viewModel.saveOrUpdatePreset(
                            existingId = existingPreset?.id ?: 0L,
                            title = title,
                            emoji = autoEmoji,
                            displayAmount = amt,
                            categoryName = selectedCatName
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier.testTag("preset_save_button")
            ) {
                Text("Save Preset", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// -------------------------------------------------------------
// 5. Achievements Locker Bottom Sheet (Opened from Home Top Banner)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsModalSheet(
    streakDays: Int,
    bestStreakDays: Int,
    activeLoginDates: Set<String>,
    lastLoginDate: String,
    budgets: List<BudgetEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    debts: List<DebtEntity>,
    transactions: List<TransactionWithDetails>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accentColor = LocalAccentColor.current
    val challenge = remember(streakDays) {
        FinanceCalculators.evaluateStreakChallenge(streakDays, 14)
    }
    val evaluation = remember(streakDays, transactions, budgets, savingsGoals, debts) {
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
            streakDays = streakDays,
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
    val badges = evaluation.activeBadges
    val parser = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val fullDateFmt = remember { SimpleDateFormat("dd MMM yyyy", Locale.US) }
    val shortDateFmt = remember { SimpleDateFormat("dd MMM", Locale.US) }
    val formattedLastLogin = remember(lastLoginDate) {
        if (lastLoginDate.isBlank()) "Today"
        else try {
            parser.parse(lastLoginDate)?.let { fullDateFmt.format(it) } ?: lastLoginDate
        } catch (_: Exception) {
            lastLoginDate
        }
    }
    val recentDateLabels = remember(activeLoginDates) {
        activeLoginDates.sorted().takeLast(14).reversed().map { raw ->
            try {
                parser.parse(raw)?.let { shortDateFmt.format(it) } ?: raw
            } catch (_: Exception) {
                raw
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Unlimited Streak & Level Locker", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text(
                                text = "Lv.${challenge.level} ${challenge.rankTitle} • Best: $bestStreakDays Days • ${evaluation.totalTrophiesEarned} Trophies",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            // Unlimited Rolling 14-Day Level Cycle Card + Live Dates
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (challenge.isCompleted) StateGreen.copy(alpha = 0.12f) else Color(0xFFF59E0B).copy(alpha = 0.12f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = accentColor
                                ) {
                                    Text(
                                        text = "LEVEL ${challenge.level} • ${challenge.rankTitle.uppercase()}",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StateGreen.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = "${challenge.completedCycles} Cycle(s) Cleared",
                                        color = StateGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${challenge.totalStreakDays}d 🔥",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = Color(0xFFD97706)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Cycle Range: Days ${challenge.cycleStartDay}–${challenge.cycleEndDay}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(
                                    text = if (challenge.isCompleted) {
                                        "Level ${challenge.level} Complete! Tomorrow advances to Level ${challenge.level + 1} (Day ${challenge.totalStreakDays + 1})"
                                    } else {
                                        "Last Check-in: $formattedLastLogin • ${14 - challenge.completedDays} day(s) to Lv.${challenge.level + 1}"
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (challenge.isCompleted) StateGreen.copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.22f)
                            ) {
                                Text(
                                    text = "${challenge.completedDays} / 14 (${challenge.progressPercent}%)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = if (challenge.isCompleted) StateGreen else Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { challenge.progressRatio },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = if (challenge.isCompleted) StateGreen else Color(0xFFF59E0B),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Visual 14-Day Daily Check-In Grid (Dynamic Day Numbers for Current Level)
                        listOf(0..6, 7..13).forEach { range ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                range.forEach { offset ->
                                    val slotInCycle = offset + 1
                                    val absoluteDay = challenge.cycleStartDay + offset
                                    val done = slotInCycle <= challenge.completedDays
                                    val isCurrent = slotInCycle == challenge.completedDays
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = when {
                                            done && challenge.isCompleted -> StateGreen
                                            done -> Color(0xFFF59E0B)
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(30.dp)
                                            .then(
                                                if (isCurrent) Modifier.border(1.5.dp, accentColor, RoundedCornerShape(8.dp))
                                                else Modifier
                                            )
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (done) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                                    Icon(Icons.Default.Check, contentDescription = "Day $absoluteDay completed", tint = Color.White, modifier = Modifier.size(10.dp))
                                                    Text("$absoluteDay", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Text("D$absoluteDay", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (recentDateLabels.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Live Check-in Dates (${activeLoginDates.size} Active Days):",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                recentDateLabels.forEachIndexed { idx, label ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (idx == 0) accentColor.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                                    ) {
                                        Text(
                                            text = if (idx == 0) "Today • $label" else label,
                                            fontSize = 10.sp,
                                            fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.Medium,
                                            color = if (idx == 0) accentColor else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Unlocked Hall of Fame (Persistent Trophies as Milestones Unlock)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Unlocked Records (${evaluation.totalTrophiesEarned} Earned)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (evaluation.unlockedTrophies.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Unlock your first milestone below to start collecting permanent Level & Record trophies!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            evaluation.unlockedTrophies.forEach { trophy ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = StateGreen.copy(alpha = 0.16f),
                                    modifier = Modifier.border(1.dp, StateGreen.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = trophy.label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StateGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Escalating Milestones (1st → 6th • Auto-Locks Next Tier)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            items(badges, key = { it.id }) { badge ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (badge.tiersCleared > 0) Color(0xFFF59E0B).copy(alpha = 0.2f) else accentColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = badge.rankLabel,
                                    color = if (badge.tiersCleared > 0) Color(0xFFD97706) else accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (badge.tiersCleared > 0) Color(0xFFF59E0B).copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Tier ${badge.currentTier} Locked",
                                    tint = if (badge.tiersCleared > 0) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Text(
                                        text = badge.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f, fill = false),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = accentColor.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "Tier ${badge.currentTier} Locked",
                                            color = accentColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    if (badge.tiersCleared > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = StateGreen.copy(alpha = 0.16f)
                                        ) {
                                            Text(
                                                text = "Cleared ${badge.tiersCleared}×",
                                                color = StateGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(badge.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = accentColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${badge.progressPercent}%",
                                    color = accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = badge.progressDetail,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${badge.progressPercent}% / 100%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (badge.progressPercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                            color = accentColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// -------------------------------------------------------------
// 6. Alert Center & Notification History Sheet
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsHistorySheet(
    notifications: List<NotificationHistoryEntity>,
    thresholdPercent: Int,
    onSetThreshold: (Int) -> Unit,
    onSendTestNotification: () -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val df = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.US) }
    val accentColor = LocalAccentColor.current

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = accentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Smart Alert Center", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                }
                Row {
                    TextButton(onClick = onSendTestNotification) { Text("Test Alert") }
                    TextButton(onClick = onClearAll) { Text("Clear", color = StateRed) }
                }
            }

            // Budget Threshold Selector
            Text("Budget Warning Threshold:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(50, 75, 80, 90, 100).forEach { pct ->
                    val sel = pct == thresholdPercent
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (sel) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSetThreshold(pct) }
                    ) {
                        Text(
                            text = "$pct%",
                            color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }

            if (notifications.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No alerts yet. Budget warnings & unusual expense alerts appear here automatically.",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { n ->
                        val badgeColor = when (n.alertType) {
                            "BUDGET_ALERT", "UNUSUAL_EXPENSE" -> StateRed
                            "BILL_DUE" -> StateYellow
                            else -> accentColor
                        }
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(n.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = badgeColor)
                                    Text(df.format(Date(n.timestamp)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(n.message, fontSize = 12.sp, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
