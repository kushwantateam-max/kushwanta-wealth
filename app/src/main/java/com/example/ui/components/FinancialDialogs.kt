package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionType
import com.example.domain.calculator.FinanceCalculators
import com.example.domain.calculator.WorkHourCostCalculator
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.viewmodel.KoshvantaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEntryBottomSheet(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>,
    onOpenVoiceEntry: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val expression by viewModel.quickEntryExpression.collectAsState()
    val type by viewModel.quickEntryType.collectAsState()
    val selectedCatId by viewModel.quickEntrySelectedCategoryId.collectAsState()
    val selectedAccId by viewModel.quickEntrySelectedAccountId.collectAsState()
    val note by viewModel.quickEntryNote.collectAsState()
    val isRecurring by viewModel.quickEntryIsRecurring.collectAsState()
    val recurrenceInterval by viewModel.quickEntryRecurrenceInterval.collectAsState()
    val effectiveMonthlyIncomeUsd by viewModel.effectiveMonthlyIncomeUsd.collectAsState()
    val dailyWorkHours by viewModel.dailyWorkHours.collectAsState()
    val currentCurrency by viewModel.currentCurrency.collectAsState()
    val isDark = LocalIsDarkTheme.current
    val accent = LocalAccentColor.current
    val sheetBg = if (isDark) Color(0xFF0B0C10) else MaterialTheme.colorScheme.surface
    val chipUnselectedBg = if (isDark) Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant
    val sheetText = MaterialTheme.colorScheme.onSurface
    val sheetSubtleText = MaterialTheme.colorScheme.onSurfaceVariant
    val sheetAccent = if (isDark) NeonMint else accent

    var showCustomCategoryDialog by remember { mutableStateOf(false) }

    if (showCustomCategoryDialog) {
        CustomCategoryDialog(
            viewModel = viewModel,
            onDismiss = { showCustomCategoryDialog = false }
        )
    }

    val liveDisplayAmount = remember(expression) {
        WorkHourCostCalculator.evaluateQuickExpression(expression)
    }
    val lifeEnergyCost = remember(liveDisplayAmount, type, effectiveMonthlyIncomeUsd, dailyWorkHours, currentCurrency) {
        if (type != TransactionType.EXPENSE) {
            null
        } else {
            WorkHourCostCalculator.evaluatePurchase(
                expenseAmount = liveDisplayAmount,
                monthlyIncome = viewModel.fromUsd(effectiveMonthlyIncomeUsd),
                dailyWorkHours = dailyWorkHours
            )
        }
    }

    val suggestedCategoryName = remember(note, type) {
        if (note.length >= 3) FinanceCalculators.suggestCategoryNameFromNote(note, type == TransactionType.EXPENSE) else null
    }
    val suggestedCategory = remember(suggestedCategoryName, categories) {
        suggestedCategoryName?.let { name -> categories.firstOrNull { it.name.equals(name, ignoreCase = true) } }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBg,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 10.dp)
        ) {
            // Header with Type Toggle & Recurring Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (type == TransactionType.EXPENSE) StateRed else chipUnselectedBg,
                        modifier = Modifier.clickable { viewModel.quickEntryType.value = TransactionType.EXPENSE }
                    ) {
                        Text(
                            text = "Expense",
                            color = if (type == TransactionType.EXPENSE) Color.White else sheetText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (type == TransactionType.INCOME) StateGreen else chipUnselectedBg,
                        modifier = Modifier.clickable { viewModel.quickEntryType.value = TransactionType.INCOME }
                    ) {
                        Text(
                            text = "Income",
                            color = if (type == TransactionType.INCOME) Color.White else sheetText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isRecurring) accent else chipUnselectedBg,
                        modifier = Modifier.clickable {
                            if (!isRecurring) {
                                viewModel.quickEntryIsRecurring.value = true
                            } else if (recurrenceInterval == "MONTHLY") {
                                viewModel.quickEntryRecurrenceInterval.value = "WEEKLY"
                            } else {
                                viewModel.quickEntryIsRecurring.value = false
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Repeat,
                                contentDescription = "Recurring",
                                tint = if (isRecurring) Color.White else sheetText,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRecurring) recurrenceInterval else "Once",
                                color = if (isRecurring) Color.White else sheetText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenVoiceEntry) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Switch to Voice Entry",
                            tint = sheetAccent
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = sheetText)
                    }
                }
            }

            // Expression / Amount Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${viewModel.activeSymbol()} $expression",
                    color = sheetText,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Scrollable middle form section ensures Life-Energy Cost pill + Save Transaction stay pinned
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .testTag("quick_entry_scroll_section")
            ) {
            OutlinedTextField(
                value = note,
                onValueChange = { viewModel.quickEntryNote.value = it },
                placeholder = { Text("Add note (e.g. 'Starbucks coffee', 'Fuel')...", color = sheetSubtleText) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = sheetText,
                    unfocusedTextColor = sheetText,
                    focusedBorderColor = sheetAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = sheetAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp)
            )

            // Auto-Category Suggestion Chip
            if (suggestedCategory != null && suggestedCategory.id != selectedCatId) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = sheetAccent.copy(alpha = 0.14f),
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clickable { viewModel.quickEntrySelectedCategoryId.value = suggestedCategory.id }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = sheetAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Suggested: ${suggestedCategory.name} (Tap to apply)",
                            color = sheetAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Category Chips Carousel + Custom Category Creator
            Text(
                text = "Category",
                color = sheetSubtleText,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 20.dp, top = 6.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filteredCats = categories.filter { it.isExpense == (type == TransactionType.EXPENSE) }
                filteredCats.forEach { cat ->
                    val isSelected = cat.id == selectedCatId
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) accent else chipUnselectedBg,
                        modifier = Modifier.clickable { viewModel.quickEntrySelectedCategoryId.value = cat.id }
                    ) {
                        Text(
                            text = cat.name,
                            color = if (isSelected) Color.White else sheetText,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = sheetAccent.copy(alpha = 0.12f),
                    modifier = Modifier.clickable { showCustomCategoryDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Custom Category", tint = sheetAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Custom", color = sheetAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Account Chips Carousel
            Text(
                text = "Paid via Account",
                color = sheetSubtleText,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { acc ->
                    val isSelected = acc.id == selectedAccId
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) accent else chipUnselectedBg,
                        modifier = Modifier.clickable { viewModel.quickEntrySelectedAccountId.value = acc.id }
                    ) {
                        Text(
                            text = acc.name,
                            color = if (isSelected) Color.White else sheetText,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            } // end scrollable middle form section

            Spacer(modifier = Modifier.height(4.dp))

            // Smart High-Value Work-Hour Cost Pill (Strictly HIDDEN for small expenses < 50% of 1-day earning)
            AnimatedVisibility(
                visible = lifeEnergyCost != null,
                enter = expandVertically(tween(220)) + fadeIn(tween(200)),
                exit = shrinkVertically(tween(180)) + fadeOut(tween(150))
            ) {
                lifeEnergyCost?.let { impact ->
                    val pillAccent = if (impact.isHeavyImpact) Color(0xFFFF3366) else Color(0xFFB45309)
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (isDark) Color(0xFF0B0C10) else pillAccent.copy(alpha = 0.10f),
                        border = BorderStroke(1.2.dp, pillAccent.copy(alpha = 0.78f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .testTag("quick_entry_life_energy_pill")
                    ) {
                        Text(
                            text = impact.message,
                            color = if (isDark) {
                                if (impact.isHeavyImpact) Color(0xFFFF8FA3) else Color(0xFFFDE68A)
                            } else pillAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            QuickNumericKeypad(
                currentExpression = expression,
                onKeyClick = { viewModel.onKeypadPress(it) },
                onBackspace = { viewModel.onKeypadBackspace() },
                onClear = { viewModel.onKeypadClear() },
                onEvaluate = { viewModel.evaluateKeypadExpression() },
                onSaveClick = { viewModel.saveQuickTransaction(accounts, categories) },
                isDarkKeypad = isDark,
                keyHeight = 46.dp,
                onKeyFeedback = { viewModel.playKeypadTick() }
            )
        }
    }
}

@Composable
fun TransferDialog(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit
) {
    var fromAccountId by remember { mutableLongStateOf(accounts.firstOrNull()?.id ?: 1L) }
    var toAccountId by remember { mutableLongStateOf(accounts.getOrNull(1)?.id ?: accounts.firstOrNull()?.id ?: 2L) }
    var amountText by remember { mutableStateOf("150") }
    var noteText by remember { mutableStateOf("Quick Balance Transfer") }
    val accent = LocalAccentColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transfer Money", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "From Account:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts.forEach { acc ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (fromAccountId == acc.id) accent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { fromAccountId = acc.id }
                        ) {
                            Text(
                                text = acc.name,
                                color = if (fromAccountId == acc.id) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Text(text = "To Account:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts.forEach { acc ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (toAccountId == acc.id) accent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { toAccountId = acc.id }
                        ) {
                            Text(
                                text = acc.name,
                                color = if (toAccountId == acc.id) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Transfer Amount (${viewModel.activeSymbol()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Transfer Memo / Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    viewModel.executeTransfer(fromAccountId, toAccountId, amount, noteText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Text("Confirm Transfer", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddBudgetDialog(
    viewModel: KoshvantaViewModel,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit
) {
    val expenseCats = categories.filter { it.isExpense }
    var selectedCatId by remember { mutableLongStateOf(expenseCats.firstOrNull()?.id ?: 1L) }
    var limitText by remember { mutableStateOf("500") }
    val accent = LocalAccentColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Monthly Budget", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Select Category:", fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    expenseCats.forEach { cat ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCatId == cat.id) accent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedCatId = cat.id }
                        ) {
                            Text(
                                text = cat.name,
                                color = if (selectedCatId == cat.id) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("Monthly Limit (${viewModel.activeSymbol()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = limitText.toDoubleOrNull() ?: 0.0
                    viewModel.addBudget(selectedCatId, limit)
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Text("Save Limit", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddGoalDialog(
    viewModel: KoshvantaViewModel,
    onDismiss: () -> Unit
) {
    var goalName by remember { mutableStateOf("") }
    var targetAmountText by remember { mutableStateOf("1000") }
    var initialDepositText by remember { mutableStateOf("100") }
    val accent = LocalAccentColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Savings Vault Goal", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = goalName,
                    onValueChange = { goalName = it },
                    label = { Text("Goal Name (e.g. Vacation, Car)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetAmountText,
                    onValueChange = { targetAmountText = it },
                    label = { Text("Target Amount (${viewModel.activeSymbol()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = initialDepositText,
                    onValueChange = { initialDepositText = it },
                    label = { Text("Initial Deposit (${viewModel.activeSymbol()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetAmountText.toDoubleOrNull() ?: 0.0
                    val initial = initialDepositText.toDoubleOrNull() ?: 0.0
                    viewModel.addSavingsGoal(goalName, target, initial, "#10B981")
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Text("Create Vault", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddAccountDialog(
    viewModel: KoshvantaViewModel,
    onDismiss: () -> Unit
) {
    var accountName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.BANK) }
    var balanceText by remember { mutableStateOf("1000") }
    val accent = LocalAccentColor.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Wallet or Bank", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Account Name (e.g. Chase Bank, Wallet)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccountType.entries.forEach { type ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedType == type) accent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedType = type }
                        ) {
                            Text(
                                text = type.name,
                                color = if (selectedType == type) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Initial Balance (${viewModel.activeSymbol()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val balance = balanceText.toDoubleOrNull() ?: 0.0
                    viewModel.addAccount(accountName, selectedType, balance, "#0284C7")
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Text("Add Account", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
