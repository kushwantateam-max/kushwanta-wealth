package com.example.data.repository

import android.content.Context
import com.example.data.dao.KoshvantaDao
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.AuditLogEntity
import com.example.data.model.BillReminderEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CurrencyCode
import com.example.data.model.DebtEntity
import com.example.data.model.FinancialBook
import com.example.data.model.NotificationHistoryEntity
import com.example.data.model.QuickPresetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.TransactionWithDetails
import com.example.data.prefs.AppPreferences
import com.example.domain.util.BackupPayload
import com.example.ui.components.CountryCardEngine
import com.example.ui.components.MetalCardVaultStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

sealed class BillToggleResult {
    data class Paid(
        val bill: BillReminderEntity,
        val transaction: TransactionEntity,
        val category: CategoryEntity,
        val categoryAutoCreated: Boolean,
        val accountName: String
    ) : BillToggleResult()

    data class Unpaid(
        val bill: BillReminderEntity,
        val reversedTransaction: TransactionEntity?
    ) : BillToggleResult()

    data object NoOpAlreadySynced : BillToggleResult()
    data object NotFound : BillToggleResult()
}

class KoshvantaRepository(
    private val dao: KoshvantaDao,
    private val prefs: AppPreferences? = null
) {

    private val billToggleMutex = Mutex()
    private val billTxLinkCache = ConcurrentHashMap<Long, Long>()

    fun getAccounts(bookId: String): Flow<List<AccountEntity>> = dao.getAccountsByBook(bookId)

    fun getCategories(): Flow<List<CategoryEntity>> = dao.getAllCategories()

    fun getTransactions(bookId: String): Flow<List<TransactionEntity>> = dao.getTransactionsByBook(bookId)

    fun getBudgets(bookId: String, month: Int, year: Int): Flow<List<BudgetEntity>> =
        dao.getBudgets(bookId, month, year)

    fun getSavingsGoals(bookId: String): Flow<List<SavingsGoalEntity>> = dao.getSavingsGoals(bookId)

    fun getBillReminders(bookId: String): Flow<List<BillReminderEntity>> = dao.getBillReminders(bookId)

    fun getQuickPresets(bookId: String): Flow<List<QuickPresetEntity>> = dao.getQuickPresetsByBook(bookId)

    fun getNotificationHistory(): Flow<List<NotificationHistoryEntity>> = dao.getNotificationHistory()

    fun getAuditLogs(): Flow<List<AuditLogEntity>> = dao.getAuditLogs()

    fun getDebts(bookId: String): Flow<List<DebtEntity>> = dao.getDebtsByBook(bookId)

    fun getTransactionsWithDetails(bookId: String): Flow<List<TransactionWithDetails>> {
        return combine(
            dao.getTransactionsByBook(bookId),
            dao.getAllCategories(),
            dao.getAccountsByBook(bookId)
        ) { txList, catList, accList ->
            val catMap = catList.associateBy { it.id }
            val accMap = accList.associateBy { it.id }
            txList.map { tx ->
                TransactionWithDetails(
                    transaction = tx,
                    category = catMap[tx.categoryId],
                    account = accMap[tx.accountId],
                    toAccount = tx.toAccountId?.let { accMap[it] }
                )
            }
        }
    }

    suspend fun ensureBookInitialized(bookId: String, isDemoWiped: Boolean = false) = withContext(Dispatchers.IO) {
        if (isDemoWiped) return@withContext
        val existingAccs = dao.getAccountsByBook(bookId).first()
        if (existingAccs.isEmpty() && bookId != FinancialBook.PERSONAL.id) {
            val label = when (bookId) {
                FinancialBook.FAMILY.id -> "Family Joint Account"
                FinancialBook.BUSINESS.id -> "Business Operating Ledger"
                else -> "Primary Wallet"
            }
            val color = if (bookId == FinancialBook.FAMILY.id) "#059669" else "#7C3AED"
            dao.insertAccount(
                AccountEntity(
                    bookId = bookId,
                    name = label,
                    type = AccountType.BANK,
                    balance = 1500.0,
                    cardColorHex = color,
                    isDefault = true
                )
            )
        }
        val presets = dao.getQuickPresetsByBook(bookId).first()
        if (presets.isEmpty()) {
            dao.insertPresets(defaultPresetsForBook(bookId))
        }
    }

    suspend fun addTransaction(transaction: TransactionEntity): Long = withContext(Dispatchers.IO) {
        val insertedId = dao.insertTransaction(transaction)
        applyBalanceImpact(transaction, multiplier = 1.0)
        logAudit("CREATE", "${transaction.type.name}: ${transaction.note} ($${String.format("%.2f", transaction.amount)})")
        insertedId
    }

    suspend fun updateTransaction(oldTx: TransactionEntity, updatedTx: TransactionEntity) = withContext(Dispatchers.IO) {
        // Reverse old impact, apply new impact
        applyBalanceImpact(oldTx, multiplier = -1.0)
        dao.updateTransaction(updatedTx)
        applyBalanceImpact(updatedTx, multiplier = 1.0)
        logAudit("EDIT", "Updated ${updatedTx.note} to $${String.format("%.2f", updatedTx.amount)}")
    }

    suspend fun deleteTransaction(tx: TransactionEntity) = withContext(Dispatchers.IO) {
        dao.deleteTransactionById(tx.id)
        applyBalanceImpact(tx, multiplier = -1.0)
        logAudit("DELETE", "Removed ${tx.type.name}: ${tx.note} ($${String.format("%.2f", tx.amount)})")
    }

    private suspend fun applyBalanceImpact(tx: TransactionEntity, multiplier: Double) {
        when (tx.type) {
            TransactionType.EXPENSE -> {
                dao.updateAccountBalance(tx.accountId, -tx.amount * multiplier)
            }
            TransactionType.INCOME -> {
                dao.updateAccountBalance(tx.accountId, tx.amount * multiplier)
            }
            TransactionType.TRANSFER -> {
                dao.updateAccountBalance(tx.accountId, -tx.amount * multiplier)
                tx.toAccountId?.let { toAcc ->
                    dao.updateAccountBalance(toAcc, tx.amount * multiplier)
                }
            }
        }
    }

    suspend fun transferFunds(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        note: String,
        bookId: String,
        profileName: String = "Primary"
    ) = withContext(Dispatchers.IO) {
        val categories = dao.getAllCategories().first()
        val transferCategory = categories.firstOrNull { it.name.contains("Transfer", ignoreCase = true) }
        val catId = transferCategory?.id ?: categories.firstOrNull()?.id ?: 1L

        val tx = TransactionEntity(
            bookId = bookId,
            accountId = fromAccountId,
            toAccountId = toAccountId,
            categoryId = catId,
            amount = amount,
            type = TransactionType.TRANSFER,
            note = if (note.isBlank()) "Account Transfer" else note,
            timestamp = System.currentTimeMillis(),
            profileName = profileName
        )
        addTransaction(tx)
    }

    // --- Categories ---
    suspend fun insertCategory(category: CategoryEntity): Long = withContext(Dispatchers.IO) {
        val id = dao.insertCategory(category)
        logAudit("CREATE", "Custom category '${category.name}' created")
        id
    }

    suspend fun deleteCategory(categoryId: Long) = withContext(Dispatchers.IO) {
        dao.deleteCategoryById(categoryId)
    }

    // --- Accounts ---
    suspend fun insertAccount(account: AccountEntity) = withContext(Dispatchers.IO) {
        dao.insertAccount(account)
        logAudit("CREATE", "Account '${account.name}' opened")
    }

    suspend fun insertAccountAndReturnId(account: AccountEntity): Long = withContext(Dispatchers.IO) {
        val id = dao.insertAccount(account)
        logAudit("CREATE", "Account '${account.name}' opened")
        id
    }

    suspend fun updateAccount(account: AccountEntity) = withContext(Dispatchers.IO) {
        dao.updateAccount(account)
        logAudit("EDIT", "Account '${account.name}' updated")
    }

    suspend fun getAccountById(accountId: Long): AccountEntity? = withContext(Dispatchers.IO) {
        dao.getAccountById(accountId)
    }

    suspend fun setPrimaryAccount(bookId: String, primaryAccountId: Long) = withContext(Dispatchers.IO) {
        val accounts = dao.getAccountsByBook(bookId).first()
        accounts.forEach { acc ->
            val shouldBeDefault = acc.id == primaryAccountId
            if (acc.isDefault != shouldBeDefault) {
                dao.updateAccount(acc.copy(isDefault = shouldBeDefault, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    suspend fun deleteAccount(accountId: Long) = withContext(Dispatchers.IO) {
        dao.deleteAccountById(accountId)
        logAudit("DELETE", "Account #$accountId removed")
    }

    suspend fun syncCountryStarterCards(
        bookId: String,
        currency: CurrencyCode,
        context: Context? = null,
        force: Boolean = false
    ) = withContext(Dispatchers.IO) {
        if (!force) {
            if (prefs?.isCustomCardsLocked() == true) return@withContext
            if (prefs?.isDemoWiped() == true) return@withContext
        }

        val specs = CountryCardEngine.getStarterCards(currency)
        val existing = dao.getAccountsByBook(bookId).first()
        val existingTxs = dao.getTransactionsByBook(bookId).first()

        if (existing.isEmpty()) {
            val insertedIds = mutableListOf<Long>()
            specs.forEach { spec ->
                val id = dao.insertAccount(
                    AccountEntity(
                        bookId = bookId,
                        name = spec.title,
                        type = spec.type,
                        balance = spec.balanceUsd,
                        accountNumberLast4 = spec.last4,
                        cardColorHex = spec.cardColorHex,
                        isDefault = spec.isDefault,
                        creditLimit = spec.monthlyLimitUsd
                    )
                )
                insertedIds += id
                if (context != null) {
                    MetalCardVaultStore.saveFinish(context, id, spec.finish)
                }
            }
            prefs?.setStarterCardIds(insertedIds)
            logAudit("SYNC", "Preloaded ${specs.size} ${currency.countryName} (${currency.code}) starter 3D cards")
            return@withContext
        }

        // Update unlocked starter slots in-place so existing demo transactions never lose foreign keys
        val starterIds = mutableListOf<Long>()
        val referencedIds = existingTxs.flatMap { listOfNotNull(it.accountId, it.toAccountId) }.toSet()

        specs.forEachIndexed { idx, spec ->
            if (idx < existing.size) {
                val target = existing[idx]
                val updated = target.copy(
                    name = spec.title,
                    type = spec.type,
                    balance = if (existingTxs.isEmpty()) spec.balanceUsd else target.balance,
                    accountNumberLast4 = spec.last4,
                    cardColorHex = spec.cardColorHex,
                    isDefault = spec.isDefault,
                    creditLimit = spec.monthlyLimitUsd,
                    updatedAt = System.currentTimeMillis()
                )
                dao.updateAccount(updated)
                starterIds += target.id
                if (context != null) {
                    MetalCardVaultStore.saveFinish(context, target.id, spec.finish)
                }
            } else {
                val newId = dao.insertAccount(
                    AccountEntity(
                        bookId = bookId,
                        name = spec.title,
                        type = spec.type,
                        balance = spec.balanceUsd,
                        accountNumberLast4 = spec.last4,
                        cardColorHex = spec.cardColorHex,
                        isDefault = spec.isDefault,
                        creditLimit = spec.monthlyLimitUsd
                    )
                )
                starterIds += newId
                if (context != null) {
                    MetalCardVaultStore.saveFinish(context, newId, spec.finish)
                }
            }
        }

        // Cleanly remove extra 4th unreferenced demo account if converting starter set to 3 regional cards
        if (existing.size > specs.size) {
            existing.drop(specs.size).forEach { extra ->
                if (extra.id !in referencedIds) {
                    dao.deleteAccountById(extra.id)
                }
            }
        }

        prefs?.setStarterCardIds(starterIds)
        logAudit("SYNC", "Updated starter 3D cards for ${currency.countryName} (${currency.code})")
    }

    // --- Budgets ---
    suspend fun insertBudget(budget: BudgetEntity) = withContext(Dispatchers.IO) {
        dao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) = withContext(Dispatchers.IO) {
        dao.updateBudget(budget)
    }

    suspend fun deleteBudget(budgetId: Long) = withContext(Dispatchers.IO) {
        dao.deleteBudgetById(budgetId)
    }

    // --- Savings Goals ---
    suspend fun insertGoal(goal: SavingsGoalEntity) = withContext(Dispatchers.IO) {
        dao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: SavingsGoalEntity) = withContext(Dispatchers.IO) {
        dao.updateGoal(goal)
    }

    suspend fun updateGoalDeposit(goalId: Long, depositAmount: Double, accountId: Long, bookId: String) =
        withContext(Dispatchers.IO) {
            val goals = dao.getSavingsGoals(bookId).first()
            val goal = goals.firstOrNull { it.id == goalId }
            if (goal != null) {
                val updated = goal.copy(currentAmount = (goal.currentAmount + depositAmount).coerceAtMost(goal.targetAmount * 1.5))
                dao.updateGoal(updated)
                dao.updateAccountBalance(accountId, -depositAmount)
                logAudit("CREATE", "Vault deposit $${String.format("%.2f", depositAmount)} -> ${goal.name}")
            }
        }

    suspend fun deleteGoal(goalId: Long) = withContext(Dispatchers.IO) {
        dao.deleteGoalById(goalId)
    }

    // --- Bills ---
    suspend fun toggleBillPaid(billId: Long, isPaid: Boolean) {
        toggleBillPaidWithSync(billId = billId, isPaid = isPaid)
    }

    suspend fun toggleBillPaidWithSync(
        billId: Long,
        isPaid: Boolean,
        profileName: String = "Primary"
    ): BillToggleResult = billToggleMutex.withLock {
        withContext(Dispatchers.IO) {
            val bill = dao.getBillById(billId) ?: return@withContext BillToggleResult.NotFound
            val billNote = billTransactionNote(bill.title)

            if (isPaid) {
                // Duplicate protection: if bill is already marked Paid OR a valid linked tx already exists, do not double-deduct
                val existingLinkedTx = findLinkedTransaction(bill, billNote)
                if (bill.isPaid || existingLinkedTx != null) {
                    if (!bill.isPaid) {
                        dao.updateBillPaidStatus(billId, true)
                    }
                    if (existingLinkedTx != null) {
                        saveBillTxLink(billId, existingLinkedTx.id)
                    }
                    return@withContext BillToggleResult.NoOpAlreadySynced
                }

                // 1. Resolve or auto-create target Account for the bill's book
                val bookAccounts = dao.getAccountsByBook(bill.bookId).first()
                val targetAccount = bookAccounts.firstOrNull { it.isDefault } ?: bookAccounts.firstOrNull()
                val resolvedAccountId = targetAccount?.id ?: insertAccountAndReturnId(
                    AccountEntity(
                        bookId = bill.bookId,
                        name = "Primary Wallet",
                        type = AccountType.CASH,
                        balance = 0.0,
                        cardColorHex = "#00C8A0",
                        isDefault = true
                    )
                )
                val resolvedAccountName = targetAccount?.name ?: "Primary Wallet"

                // 2. Smart Category Resolver (match existing DB category or auto-create custom / "Bills & Other")
                val (resolvedCategory, categoryAutoCreated) = resolveOrCreateCategoryForBill(bill)

                // 3. Record real Expense Transaction & update account balance synchronously
                val newTx = TransactionEntity(
                    bookId = bill.bookId,
                    accountId = resolvedAccountId,
                    categoryId = resolvedCategory.id,
                    amount = bill.amount,
                    type = TransactionType.EXPENSE,
                    note = billNote,
                    timestamp = System.currentTimeMillis(),
                    profileName = profileName
                )
                val insertedTxId = addTransaction(newTx)
                saveBillTxLink(billId, insertedTxId)
                dao.updateBillPaidStatus(billId, true)

                return@withContext BillToggleResult.Paid(
                    bill = bill.copy(isPaid = true),
                    transaction = newTx.copy(id = insertedTxId),
                    category = resolvedCategory,
                    categoryAutoCreated = categoryAutoCreated,
                    accountName = resolvedAccountName
                )
            } else {
                // Undo / Unmark Paid: safely reverse & delete the linked transaction if present
                val existingLinkedTx = findLinkedTransaction(bill, billNote)
                if (!bill.isPaid && existingLinkedTx == null) {
                    saveBillTxLink(billId, null)
                    return@withContext BillToggleResult.NoOpAlreadySynced
                }

                if (existingLinkedTx != null) {
                    deleteTransaction(existingLinkedTx)
                }
                saveBillTxLink(billId, null)
                dao.updateBillPaidStatus(billId, false)

                return@withContext BillToggleResult.Unpaid(
                    bill = bill.copy(isPaid = false),
                    reversedTransaction = existingLinkedTx
                )
            }
        }
    }

    private fun billTransactionNote(billTitle: String): String = "Bill Paid: ${billTitle.trim()}"

    private fun saveBillTxLink(billId: Long, txId: Long?) {
        if (txId != null && txId > 0L) {
            billTxLinkCache[billId] = txId
        } else {
            billTxLinkCache.remove(billId)
        }
        prefs?.setLinkedBillTransactionId(billId, txId)
    }

    private suspend fun findLinkedTransaction(bill: BillReminderEntity, billNote: String): TransactionEntity? {
        val storedTxId = prefs?.getLinkedBillTransactionId(bill.id) ?: billTxLinkCache[bill.id]
        if (storedTxId != null) {
            val byId = dao.getTransactionById(storedTxId)
            if (byId != null) return byId
        }
        val byNote = dao.findTransactionsByBookTypeAndNote(bill.bookId, TransactionType.EXPENSE, billNote)
        return byNote.firstOrNull { abs(it.amount - bill.amount) < 0.01 }
    }

    private suspend fun resolveOrCreateCategoryForBill(bill: BillReminderEntity): Pair<CategoryEntity, Boolean> {
        val allCats = dao.getAllCategories().first()
        val expenseCats = allCats.filter { it.isExpense }.ifEmpty { allCats }

        val rawCat = bill.categoryName.trim()
        val lowerTitle = bill.title.trim().lowercase()

        // Step A: Explicit custom categoryName match (if user/bill specified one beyond default "Subscriptions")
        if (rawCat.isNotEmpty() && !rawCat.equals("Subscriptions", ignoreCase = true)) {
            expenseCats.firstOrNull {
                it.name.equals(rawCat, ignoreCase = true) ||
                    it.name.contains(rawCat, ignoreCase = true) ||
                    rawCat.contains(it.name, ignoreCase = true)
            }?.let { return it to false }
        }

        // Step B: Direct word match between bill title and existing category names
        val titleTokens = lowerTitle.split(Regex("[^a-z0-9]+")).filter { it.length >= 3 }
        expenseCats.firstOrNull { cat ->
            val catLower = cat.name.lowercase()
            titleTokens.any { token -> catLower.contains(token) } || lowerTitle.contains(catLower)
        }?.let { return it to false }

        // Step C: Smart domain keyword match against existing Room categories
        val inferredBucketNames: List<String> = when {
            listOf("netflix", "youtube", "spotify", "prime", "disney", "hotstar", "hulu", "apple tv", "music", "cinema", "gaming", "xbox", "playstation", "stream")
                .any { lowerTitle.contains(it) } -> listOf("Entertainment", "Streaming")
            listOf("electric", "electricity", "power", "water", "gas", "rent", "maintenance", "wifi", "fiber", "internet", "broadband", "mobile", "phone", "recharge", "cloud", "workspace", "aws", "hosting", "domain", "utility", "utilities", "insurance", "emi", "tax")
                .any { lowerTitle.contains(it) } -> listOf("Rent & Bills", "Utilities", "Bills & Other")
            listOf("fuel", "petrol", "diesel", "metro", "transit", "toll", "parking", "uber", "ola")
                .any { lowerTitle.contains(it) } -> listOf("Transport & Fuel")
            listOf("grocery", "groceries", "milk", "blinkit", "zepto", "instamart", "bigbasket")
                .any { lowerTitle.contains(it) } -> listOf("Groceries")
            listOf("food", "coffee", "dining", "restaurant", "swiggy", "zomato")
                .any { lowerTitle.contains(it) } -> listOf("Food & Drinks")
            listOf("shopping", "amazon", "flipkart", "myntra")
                .any { lowerTitle.contains(it) } -> listOf("Shopping")
            listOf("mutual", "sip", "stock", "invest")
                .any { lowerTitle.contains(it) } -> listOf("Mutual Funds & Stocks")
            else -> emptyList()
        }

        for (targetName in inferredBucketNames) {
            expenseCats.firstOrNull { it.name.equals(targetName, ignoreCase = true) }?.let {
                return it to false
            }
        }

        // Step D: If bill.categoryName (e.g., "Subscriptions" or "Bills & Other") already exists in Room DB, link it
        if (rawCat.isNotEmpty()) {
            expenseCats.firstOrNull { it.name.equals(rawCat, ignoreCase = true) }?.let {
                return it to false
            }
        }

        // Fallback check for "Bills & Other" if it already exists
        expenseCats.firstOrNull { it.name.equals("Bills & Other", ignoreCase = true) }?.let {
            if (rawCat.isEmpty()) return it to false
        }

        // Step E: Auto-create custom category cleanly (falling back safely to "Bills & Other")
        val targetCreateName = rawCat.ifBlank { "Bills & Other" }
        val iconName = when {
            targetCreateName.contains("Subscription", ignoreCase = true) -> "subscriptions"
            else -> "receipt_long"
        }
        val colorHex = when {
            targetCreateName.contains("Subscription", ignoreCase = true) -> "#8B5CF6"
            else -> "#6366F1"
        }

        val insertedCat = runCatching {
            val newCat = CategoryEntity(
                name = targetCreateName,
                iconName = iconName,
                colorHex = colorHex,
                isExpense = true
            )
            val newId = insertCategory(newCat)
            newCat.copy(id = newId)
        }.getOrElse {
            val existingFallback = dao.getAllCategories().first()
                .firstOrNull { it.name.equals("Bills & Other", ignoreCase = true) }
            if (existingFallback != null) {
                existingFallback
            } else {
                val safeFallback = CategoryEntity(
                    name = "Bills & Other",
                    iconName = "receipt_long",
                    colorHex = "#6366F1",
                    isExpense = true
                )
                val fallbackId = dao.insertCategory(safeFallback)
                safeFallback.copy(id = fallbackId)
            }
        }
        return insertedCat to true
    }

    suspend fun insertBill(bill: BillReminderEntity) = withContext(Dispatchers.IO) {
        dao.insertBill(bill)
    }

    suspend fun deleteBill(billId: Long) = withContext(Dispatchers.IO) {
        saveBillTxLink(billId, null)
        dao.deleteBillById(billId)
    }

    // --- Debts ---
    suspend fun insertDebt(debt: DebtEntity) = withContext(Dispatchers.IO) {
        dao.insertDebt(debt)
    }

    suspend fun toggleDebtSettled(debtId: Long, isSettled: Boolean) = withContext(Dispatchers.IO) {
        dao.updateDebtSettledStatus(debtId, isSettled)
    }

    suspend fun deleteDebt(debtId: Long) = withContext(Dispatchers.IO) {
        dao.deleteDebtById(debtId)
    }

    // --- Quick Presets ---
    suspend fun upsertPreset(preset: QuickPresetEntity) = withContext(Dispatchers.IO) {
        if (preset.id == 0L) {
            dao.insertPreset(preset)
        } else {
            dao.updatePreset(preset)
        }
    }

    suspend fun deletePreset(presetId: Long) = withContext(Dispatchers.IO) {
        dao.deletePresetById(presetId)
    }

    // --- Notifications & Audit Logs ---
    suspend fun insertNotification(notification: NotificationHistoryEntity) = withContext(Dispatchers.IO) {
        dao.insertNotification(notification)
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsRead()
    }

    suspend fun clearNotifications() = withContext(Dispatchers.IO) {
        dao.clearNotificationHistory()
    }

    suspend fun logAudit(action: String, summary: String) = withContext(Dispatchers.IO) {
        dao.insertAuditLog(AuditLogEntity(action = action, summary = summary))
    }

    // --- Backup & Restore ---
    suspend fun exportBackupPayload(): BackupPayload = withContext(Dispatchers.IO) {
        BackupPayload(
            accounts = dao.getAllAccountsSync(),
            categories = dao.getAllCategoriesSync(),
            transactions = dao.getAllTransactionsSync(),
            budgets = dao.getAllBudgetsSync(),
            goals = dao.getAllGoalsSync(),
            bills = dao.getAllBillsSync(),
            debts = dao.getAllDebtsSync(),
            presets = dao.getAllPresetsSync()
        )
    }

    suspend fun restoreBackupPayload(payload: BackupPayload) = withContext(Dispatchers.IO) {
        dao.clearAllTransactions()
        dao.clearAllBudgets()
        dao.clearAllGoals()
        dao.clearAllBills()
        dao.clearAllDebts()
        dao.clearAllAccounts()
        dao.clearAllPresets()

        if (payload.categories.isNotEmpty()) dao.insertCategories(payload.categories)
        if (payload.accounts.isNotEmpty()) dao.insertAccounts(payload.accounts)
        if (payload.transactions.isNotEmpty()) dao.insertTransactions(payload.transactions)
        if (payload.budgets.isNotEmpty()) dao.insertBudgets(payload.budgets)
        if (payload.goals.isNotEmpty()) dao.insertGoals(payload.goals)
        if (payload.bills.isNotEmpty()) dao.insertBills(payload.bills)
        if (payload.debts.isNotEmpty()) dao.insertDebts(payload.debts)
        if (payload.presets.isNotEmpty()) dao.insertPresets(payload.presets)

        logAudit("RESTORE", "Restored ${payload.transactions.size} transactions & ${payload.accounts.size} accounts from verified JSON")
    }

    // --- Preload Realistic Sample Data & 1-Tap Wipe ---
    suspend fun checkAndPreloadInitialData(isDemoWiped: Boolean = false) = withContext(Dispatchers.IO) {
        val existingCats = dao.getAllCategories().first()
        if (existingCats.isEmpty()) {
            val defaultCategories = listOf(
                CategoryEntity(name = "Food & Drinks", iconName = "restaurant", colorHex = "#06B6D4", isExpense = true),
                CategoryEntity(name = "Freelancing Work", iconName = "laptop", colorHex = "#10B981", isExpense = false),
                CategoryEntity(name = "Shopping", iconName = "shopping_bag", colorHex = "#EC4899", isExpense = true),
                CategoryEntity(name = "Mutual Funds & Stocks", iconName = "trending_up", colorHex = "#3B82F6", isExpense = true),
                CategoryEntity(name = "Salary", iconName = "payments", colorHex = "#10B981", isExpense = false),
                CategoryEntity(name = "Groceries", iconName = "local_grocery_store", colorHex = "#F59E0B", isExpense = true),
                CategoryEntity(name = "Rent & Bills", iconName = "home", colorHex = "#6366F1", isExpense = true),
                CategoryEntity(name = "Transfer", iconName = "swap_horiz", colorHex = "#64748B", isExpense = true),
                CategoryEntity(name = "Entertainment", iconName = "movie", colorHex = "#8B5CF6", isExpense = true),
                CategoryEntity(name = "Transport & Fuel", iconName = "directions_car", colorHex = "#0EA5E9", isExpense = true)
            )
            dao.insertCategories(defaultCategories)
        }

        if (isDemoWiped) {
            return@withContext
        }

        val existingAccounts = dao.getAccountsByBook(FinancialBook.PERSONAL.id).first()
        if (existingAccounts.isEmpty()) {
            loadDemoData()
        } else {
            val presets = dao.getQuickPresetsByBook(FinancialBook.PERSONAL.id).first()
            if (presets.isEmpty()) {
                dao.insertPresets(defaultPresetsForBook(FinancialBook.PERSONAL.id))
            }
        }
    }

    private fun defaultPresetsForBook(bookId: String): List<QuickPresetEntity> = listOf(
        QuickPresetEntity(bookId = bookId, title = "Coffee", emoji = "☕", amountUsd = 4.50, categoryName = "Food & Drinks", isExpense = true),
        QuickPresetEntity(bookId = bookId, title = "Fuel", emoji = "⛽", amountUsd = 35.00, categoryName = "Transport & Fuel", isExpense = true),
        QuickPresetEntity(bookId = bookId, title = "Groceries", emoji = "🛒", amountUsd = 60.00, categoryName = "Groceries", isExpense = true),
        QuickPresetEntity(bookId = bookId, title = "Dining", emoji = "🍔", amountUsd = 22.00, categoryName = "Food & Drinks", isExpense = true),
        QuickPresetEntity(bookId = bookId, title = "Utilities", emoji = "⚡", amountUsd = 45.00, categoryName = "Rent & Bills", isExpense = true)
    )

    suspend fun loadDemoData() = withContext(Dispatchers.IO) {
        val bookId = FinancialBook.PERSONAL.id
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH) + 1
        val currentYear = cal.get(Calendar.YEAR)
        val now = System.currentTimeMillis()

        // 1. Accounts matching reference UI style
        val accounts = listOf(
            AccountEntity(
                bookId = bookId,
                name = "HDFC Bank",
                type = AccountType.BANK,
                balance = 8720.76,
                accountNumberLast4 = "9874",
                cardColorHex = "#1D4ED8",
                isDefault = true,
                creditLimit = 0.0
            ),
            AccountEntity(
                bookId = bookId,
                name = "Axis Bank Platinum",
                type = AccountType.BANK,
                balance = 4280.50,
                accountNumberLast4 = "6231",
                cardColorHex = "#0284C7",
                isDefault = false,
                creditLimit = 0.0
            ),
            AccountEntity(
                bookId = bookId,
                name = "Cash & Physical Wallet",
                type = AccountType.CASH,
                balance = 750.00,
                accountNumberLast4 = "",
                cardColorHex = "#059669",
                isDefault = false,
                creditLimit = 0.0
            ),
            AccountEntity(
                bookId = bookId,
                name = "World Elite Credit Card",
                type = AccountType.CREDIT_CARD,
                balance = 1502.89,
                accountNumberLast4 = "4321",
                cardColorHex = "#7C3AED",
                isDefault = false,
                creditLimit = 10000.00
            )
        )
        dao.insertAccounts(accounts)
        val savedAccounts = dao.getAccountsByBook(bookId).first()
        val hdfcId = savedAccounts.firstOrNull { it.name.contains("HDFC") }?.id ?: 1L
        val axisId = savedAccounts.firstOrNull { it.name.contains("Axis") }?.id ?: 2L
        val cashId = savedAccounts.firstOrNull { it.type == AccountType.CASH }?.id ?: 3L

        val categories = dao.getAllCategories().first()
        val foodCat = categories.firstOrNull { it.name.contains("Food") }?.id ?: 1L
        val freelanceCat = categories.firstOrNull { it.name.contains("Freelance") }?.id ?: 2L
        val shoppingCat = categories.firstOrNull { it.name.contains("Shopping") }?.id ?: 3L
        val investCat = categories.firstOrNull { it.name.contains("Mutual") }?.id ?: 4L
        val salaryCat = categories.firstOrNull { it.name.contains("Salary") }?.id ?: 5L
        val billsCat = categories.firstOrNull { it.name.contains("Rent") }?.id ?: 7L
        val transportCat = categories.firstOrNull { it.name.contains("Transport") }?.id ?: 10L

        // 2. Realistic Transactions across recent days (for Calendar Heatmap & Bar Chart)
        val sampleTransactions = listOf(
            TransactionEntity(
                bookId = bookId,
                accountId = hdfcId,
                categoryId = freelanceCat,
                amount = 1250.00,
                type = TransactionType.INCOME,
                note = "UI Design Sprint milestone payout",
                timestamp = now - (2L * 3600 * 1000)
            ),
            TransactionEntity(
                bookId = bookId,
                accountId = hdfcId,
                categoryId = foodCat,
                amount = 45.50,
                type = TransactionType.EXPENSE,
                note = "Starbucks Artisan Roast & Snacks",
                timestamp = now - (6L * 3600 * 1000)
            ),
            TransactionEntity(
                bookId = bookId,
                accountId = axisId,
                categoryId = shoppingCat,
                amount = 120.00,
                type = TransactionType.EXPENSE,
                note = "Nordstrom weekend shopping",
                timestamp = now - (24L * 3600 * 1000)
            ),
            TransactionEntity(
                bookId = bookId,
                accountId = hdfcId,
                categoryId = investCat,
                amount = 300.00,
                type = TransactionType.EXPENSE,
                note = "Index Fund SIP monthly contribution",
                timestamp = now - (48L * 3600 * 1000),
                isRecurring = true,
                recurrenceInterval = "MONTHLY"
            ),
            TransactionEntity(
                bookId = bookId,
                accountId = cashId,
                categoryId = transportCat,
                amount = 28.00,
                type = TransactionType.EXPENSE,
                note = "City Express Metro & Taxi",
                timestamp = now - (72L * 3600 * 1000)
            ),
            TransactionEntity(
                bookId = bookId,
                accountId = hdfcId,
                categoryId = salaryCat,
                amount = 4800.00,
                type = TransactionType.INCOME,
                note = "Monthly Core Engineering Salary",
                timestamp = now - (7L * 24 * 3600 * 1000),
                isRecurring = true,
                recurrenceInterval = "MONTHLY"
            ),
            TransactionEntity(
                bookId = bookId,
                accountId = axisId,
                categoryId = billsCat,
                amount = 350.00,
                type = TransactionType.EXPENSE,
                note = "High-speed Fiber & Cloud Utilities",
                timestamp = now - (10L * 24 * 3600 * 1000),
                isRecurring = true,
                recurrenceInterval = "MONTHLY"
            )
        )
        dao.insertTransactions(sampleTransactions)

        // 3. Budgets
        val sampleBudgets = listOf(
            BudgetEntity(bookId = bookId, categoryId = foodCat, monthlyLimit = 500.00, periodMonth = currentMonth, periodYear = currentYear),
            BudgetEntity(bookId = bookId, categoryId = shoppingCat, monthlyLimit = 350.00, periodMonth = currentMonth, periodYear = currentYear),
            BudgetEntity(bookId = bookId, categoryId = investCat, monthlyLimit = 600.00, periodMonth = currentMonth, periodYear = currentYear),
            BudgetEntity(bookId = bookId, categoryId = billsCat, monthlyLimit = 800.00, periodMonth = currentMonth, periodYear = currentYear)
        )
        dao.insertBudgets(sampleBudgets)

        // 4. Savings Goals
        val sampleGoals = listOf(
            SavingsGoalEntity(bookId = bookId, name = "MacBook Pro M-Max", targetAmount = 2500.00, currentAmount = 1850.00, colorHex = "#06B6D4"),
            SavingsGoalEntity(bookId = bookId, name = "Emergency Safety Vault", targetAmount = 10000.00, currentAmount = 6400.00, colorHex = "#10B981"),
            SavingsGoalEntity(bookId = bookId, name = "Japan Autumn Expedition", targetAmount = 4000.00, currentAmount = 1200.00, colorHex = "#EC4899")
        )
        dao.insertGoals(sampleGoals)

        // 5. Upcoming Subscriptions / Bills
        val sampleBills = listOf(
            BillReminderEntity(bookId = bookId, title = "Google Cloud & Workspace", amount = 14.99, dueDate = now + (3L * 24 * 3600 * 1000), providerIcon = "cloud"),
            BillReminderEntity(bookId = bookId, title = "YouTube Premium Family", amount = 18.99, dueDate = now + (7L * 24 * 3600 * 1000), providerIcon = "play_circle"),
            BillReminderEntity(bookId = bookId, title = "Apple Developer Program", amount = 99.00, dueDate = now + (14L * 24 * 3600 * 1000), providerIcon = "verified")
        )
        dao.insertBills(sampleBills)

        // 6. Debts
        val sampleDebts = listOf(
            DebtEntity(bookId = bookId, personName = "Rahul Verma", amount = 200.00, isLent = true, dueDate = now + (10L * 24 * 3600 * 1000), note = "Lunch & Cab split"),
            DebtEntity(bookId = bookId, personName = "Aman Sharma", amount = 50.00, isLent = false, dueDate = now + (5L * 24 * 3600 * 1000), note = "Concert Ticket share")
        )
        dao.insertDebts(sampleDebts)

        // 7. Editable Quick 1-Tap Presets
        dao.insertPresets(defaultPresetsForBook(bookId))

        // 8. Initial Alert History & Audit Log
        dao.insertNotification(
            NotificationHistoryEntity(
                title = "Welcome to Koshvanta Smart Ledger",
                message = "Instant Local Vault SQLite storage initialized. Tap the pencil on any quick preset to customize amounts for your country.",
                alertType = "SYSTEM",
                isRead = false
            )
        )
        dao.insertAuditLog(
            AuditLogEntity(
                action = "CREATE",
                summary = "Initialized sample multi-account ledger (Personal Book)"
            )
        )
    }

    suspend fun wipeAllUserData() = withContext(Dispatchers.IO) {
        billTxLinkCache.clear()
        prefs?.clearAllBillTransactionLinks()
        dao.clearAllTransactions()
        dao.clearAllBudgets()
        dao.clearAllGoals()
        dao.clearAllBills()
        dao.clearAllDebts()
        dao.clearAllAccounts()
        dao.clearAllPresets()
        dao.clearNotificationHistory()
        logAudit("WIPE", "User performed single-tap full data wipe")
    }
}
