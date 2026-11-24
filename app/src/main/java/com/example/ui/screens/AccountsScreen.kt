package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppStrings
import com.example.ui.components.AddCustomMetalCardDialog
import com.example.ui.components.CountryCurrencyPickerDialog
import com.example.ui.components.CustomCategoryDialog
import com.example.ui.components.EditManageMetalCardDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.MetalCardFinish
import com.example.ui.components.MetalCardVaultCalculator
import com.example.ui.components.MetalCardVaultSection
import com.example.ui.components.MetalCardVaultStore
import com.example.ui.components.TransactionDetailEditDialog
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.StateGreen
import com.example.ui.theme.StateRed
import com.example.ui.viewmodel.KoshvantaViewModel
import kotlinx.coroutines.delay

@Composable
fun AccountsScreen(
    viewModel: KoshvantaViewModel,
    accounts: List<AccountEntity>,
    transactions: List<TransactionWithDetails> = emptyList(),
    onAddAccountClick: () -> Unit,
    onTransferClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = LocalAccentColor.current
    val haptic = LocalHapticFeedback.current
    val lang by viewModel.appLanguage.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val currentCurrency by viewModel.currentCurrency.collectAsState()
    val customCurrencySymbol by viewModel.customCurrencySymbol.collectAsState()
    val activeProfile by viewModel.activeProfile.collectAsState()
    val familyProfiles by viewModel.familyProfiles.collectAsState()
    val targetVaultAccountId by viewModel.targetVaultAccountId.collectAsState()

    var showCashCounter by remember { mutableStateOf(false) }
    var showCustomCategoryDialog by remember { mutableStateOf(false) }
    var showAddProfileDialog by remember { mutableStateOf(false) }
    var showCountryPickerDialog by remember { mutableStateOf(false) }
    var showAddCustomCardDialog by remember { mutableStateOf(false) }
    var managingAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var selectedTransactionForDetail by remember { mutableStateOf<TransactionWithDetails?>(null) }

    var pendingScrollAccountId by remember { mutableStateOf<Long?>(null) }
    var addedBannerMessage by remember { mutableStateOf<String?>(null) }

    // Page count includes +1 slot at end of horizontal scroll for "+ Add Custom Card"
    val pagerState = rememberPagerState(pageCount = { accounts.size + 1 })
    val accountFinishes = remember { mutableStateMapOf<Long, MetalCardFinish>() }
    val accountCurrencies = remember { mutableStateMapOf<Long, CurrencyCode?>() }

    LaunchedEffect(accounts, targetVaultAccountId) {
        accounts.forEachIndexed { idx, acc ->
            accountFinishes[acc.id] = MetalCardVaultStore.getFinish(context, acc, idx)
            accountCurrencies[acc.id] = MetalCardVaultStore.getCardCurrencyOverride(context, acc.id)
        }
        val targetId = pendingScrollAccountId ?: targetVaultAccountId
        if (targetId != null) {
            val targetIndex = accounts.indexOfFirst { it.id == targetId }
            if (targetIndex >= 0) {
                pagerState.animateScrollToPage(targetIndex)
                if (pendingScrollAccountId == targetId) pendingScrollAccountId = null
                if (targetVaultAccountId == targetId) viewModel.clearTargetVaultAccountId()
            } else if (accounts.isNotEmpty() && targetVaultAccountId == targetId) {
                viewModel.clearTargetVaultAccountId()
            }
        } else if (pagerState.currentPage > accounts.size) {
            pagerState.scrollToPage(accounts.size)
        }
    }

    LaunchedEffect(addedBannerMessage) {
        if (!addedBannerMessage.isNullOrBlank()) {
            delay(2800L)
            addedBannerMessage = null
        }
    }

    val selectedAccount = accounts.getOrNull(pagerState.currentPage)

    val denominationCounts = remember {
        mutableStateMapOf(500 to 0, 200 to 0, 100 to 0, 50 to 0, 20 to 0, 10 to 0)
    }
    val totalCashCounted = denominationCounts.entries.sumOf { (denom, count) -> denom * count }

    if (showCustomCategoryDialog) {
        CustomCategoryDialog(viewModel = viewModel, onDismiss = { showCustomCategoryDialog = false })
    }

    if (showCountryPickerDialog) {
        CountryCurrencyPickerDialog(
            currentCurrency = currentCurrency,
            onSelect = { chosen -> viewModel.switchCurrency(chosen) },
            onDismiss = { showCountryPickerDialog = false }
        )
    }

    if (showAddCustomCardDialog) {
        AddCustomMetalCardDialog(
            defaultCurrency = currentCurrency,
            onCreateCard = { name, type, currency, balance, limit, last4, finish ->
                viewModel.addCustomMetalCard(
                    name = name,
                    type = type,
                    currency = currency,
                    balanceInCurrency = balance,
                    monthlyLimitInCurrency = limit,
                    last4 = last4,
                    finish = finish
                ) { newId ->
                    accountFinishes[newId] = finish
                    accountCurrencies[newId] = if (currency != currentCurrency) currency else null
                    pendingScrollAccountId = newId
                    addedBannerMessage = "✨ $name Added & Saved"
                }
            },
            onDismiss = { showAddCustomCardDialog = false }
        )
    }

    managingAccount?.let { targetAcc ->
        val idx = accounts.indexOfFirst { it.id == targetAcc.id }.coerceAtLeast(0)
        val currFinish = accountFinishes[targetAcc.id] ?: MetalCardFinish.defaultForAccount(targetAcc, idx)
        val currCurrency = accountCurrencies[targetAcc.id] ?: currentCurrency
        EditManageMetalCardDialog(
            account = targetAcc,
            currentFinish = currFinish,
            currentCardCurrency = currCurrency,
            onSave = { acc, newName, newType, newCurrency, newBalance, newLimit, newLast4, newFinish, setPrimary ->
                viewModel.updateMetalCardDetails(
                    account = acc,
                    newName = newName,
                    newType = newType,
                    newCurrency = newCurrency,
                    newBalanceInCurrency = newBalance,
                    newMonthlyLimitInCurrency = newLimit,
                    newLast4 = newLast4,
                    newFinish = newFinish,
                    setPrimary = setPrimary
                ) {
                    accountFinishes[acc.id] = newFinish
                    accountCurrencies[acc.id] = if (newCurrency != currentCurrency) newCurrency else null
                }
            },
            onDelete = { acc -> viewModel.deleteAccount(acc.id) },
            onDismiss = { managingAccount = null }
        )
    }

    selectedTransactionForDetail?.let { txItem ->
        TransactionDetailEditDialog(
            viewModel = viewModel,
            item = txItem,
            categories = uiState.categories,
            onDismiss = { selectedTransactionForDetail = null }
        )
    }

    if (showAddProfileDialog) {
        var newName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddProfileDialog = false },
            title = { Text("Add Family / Team Profile", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Member Name (e.g. Spouse, Kids, Partner)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.addFamilyProfile(newName)
                            showAddProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) { Text("Add Profile", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showAddProfileDialog = false }) { Text("Cancel") } }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.walletsAndBanks(lang),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${currentCurrency.flag} ${currentCurrency.countryName} 3D Card Vault • swipe cards to filter",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showAddCustomCardDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("accounts_add_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Custom Card", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Card", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // --- 1. Country-Aware 3D Neon Metal Card Vault, Quick-Add Pills & End Slot ---
        item {
            MetalCardVaultSection(
                accounts = accounts,
                pagerState = pagerState,
                currentCurrency = currentCurrency,
                customCurrencySymbol = customCurrencySymbol,
                allTransactions = transactions,
                accountFinishes = accountFinishes,
                accountCurrencies = accountCurrencies,
                addedBannerMessage = addedBannerMessage,
                onSelectFinish = { accountId, finish ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    accountFinishes[accountId] = finish
                    MetalCardVaultStore.saveFinish(context, accountId, finish)
                    viewModel.notifyVaultVisualChanged()
                },
                onQuickAddPreset = { preset ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.quickAddCountryCard(preset) { newId ->
                        accountFinishes[newId] = preset.finish
                        pendingScrollAccountId = newId
                        addedBannerMessage = "✨ ${preset.cardTitle} Added & Saved"
                    }
                },
                onOpenCountryPicker = { showCountryPickerDialog = true },
                onOpenCustomCardDialog = { showAddCustomCardDialog = true },
                onOpenManageCardDialog = { acc -> managingAccount = acc },
                onDeleteAccountClick = { acc -> viewModel.deleteAccount(acc.id) },
                onAutoPreloadCountryCards = { viewModel.preloadCountryStarterCards() }
            )
        }

        // --- 2. Quick Actions: Transfer, Cash Denomination Counter & Custom Categories ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTransferClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Transfer", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                }

                Button(
                    onClick = { showCashCounter = !showCashCounter },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showCashCounter) accentColor.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = null,
                        tint = if (showCashCounter) accentColor else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showCashCounter) "Hide Cash" else "Cash Counter",
                        color = if (showCashCounter) accentColor else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = { showCustomCategoryDialog = true },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Category, contentDescription = "Custom Categories", modifier = Modifier.size(18.dp))
                }
            }
        }

        // --- 3. Physical Cash Denomination Counter (Collapsible) ---
        if (showCashCounter) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Physical Cash Denomination Counter", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "Total: ${viewModel.activeSymbol()}$totalCashCounted",
                                fontWeight = FontWeight.ExtraBold,
                                color = StateGreen,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        listOf(500, 200, 100, 50, 20, 10).chunked(3).forEach { rowDenoms ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowDenoms.forEach { denom ->
                                    val count = denominationCounts[denom] ?: 0
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                viewModel.playKeypadTick()
                                                denominationCounts[denom] = count + 1
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("${viewModel.activeSymbol()}$denom", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Count: $count", fontSize = 11.sp, color = accentColor)
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                text = "Reset Counter",
                                color = StateRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { denominationCounts.keys.forEach { denominationCounts[it] = 0 } }
                            )
                        }
                    }
                }
            }
        }

        // --- 4. Active Spender Profile Switcher ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active Spender Profile", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "+ Add Profile",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showAddProfileDialog = true }
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    familyProfiles.forEach { profile ->
                        val sel = profile == activeProfile
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (sel) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.setActiveProfile(profile) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (sel) Color.White else accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    profile,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 5. Account-Filtered Activity List with Smooth Slide-Up Animation ---
        item {
            AnimatedContent(
                targetState = selectedAccount,
                transitionSpec = {
                    (slideInVertically(animationSpec = tween(280)) { height -> height / 5 } + fadeIn(tween(240)))
                        .togetherWith(fadeOut(tween(150)))
                },
                label = "account_filtered_transactions"
            ) { currentAccount ->
                if (currentAccount == null) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = 0.dp
                    ) {
                        Text(
                            text = "Tap any Quick-Add card pill above or '+ Add Custom Card' to start tracking card activity.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    val accountTxs = remember(currentAccount.id, transactions) {
                        MetalCardVaultCalculator.filterTransactionsForAccount(currentAccount.id, transactions)
                    }
                    val cardCurrency = accountCurrencies[currentAccount.id] ?: currentCurrency
                    val cardSym = if (cardCurrency == currentCurrency) customCurrencySymbol else ""
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("filtered_account_activity_${currentAccount.id}"),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${currentAccount.name} Activity",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = accentColor.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "${accountTxs.size} transactions",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (accountTxs.isEmpty()) {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = 0.dp
                            ) {
                                Text(
                                    text = "No transactions recorded on ${currentAccount.name} yet. Tap + or the Mic to log one.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            accountTxs.forEach { txItem ->
                                TransactionRowItem(
                                    item = txItem,
                                    formattedAmount = MetalCardVaultCalculator.formatUsdInCurrency(
                                        usdAmount = txItem.transaction.amount,
                                        currency = cardCurrency,
                                        customSymbol = cardSym
                                    ),
                                    onClick = { selectedTransactionForDetail = txItem },
                                    onDelete = { viewModel.deleteTransaction(txItem.transaction) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}
