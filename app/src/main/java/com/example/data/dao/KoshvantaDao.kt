package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AccountEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.BillReminderEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DebtEntity
import com.example.data.model.NotificationHistoryEntity
import com.example.data.model.QuickPresetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface KoshvantaDao {

    // --- Accounts ---
    @Query("SELECT * FROM accounts WHERE bookId = :bookId ORDER BY isDefault DESC, id ASC")
    fun getAccountsByBook(bookId: String): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts")
    suspend fun getAllAccountsSync(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE id = :accountId LIMIT 1")
    suspend fun getAccountById(accountId: Long): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Query("UPDATE accounts SET balance = balance + :delta, updatedAt = :updatedAt WHERE id = :accountId")
    suspend fun updateAccountBalance(accountId: Long, delta: Double, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM accounts WHERE id = :accountId")
    suspend fun deleteAccountById(accountId: Long)

    // --- Categories ---
    @Query("SELECT * FROM categories ORDER BY isExpense DESC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesSync(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: Long)

    // --- Transactions ---
    @Query("SELECT * FROM transactions WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getTransactionsByBook(bookId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactionsSync(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE bookId = :bookId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(bookId: String, limit: Int): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE bookId = :bookId AND type = :type AND note = :note ORDER BY timestamp DESC")
    suspend fun findTransactionsByBookTypeAndNote(bookId: String, type: TransactionType, note: String): List<TransactionEntity>

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)

    // --- Budgets ---
    @Query("SELECT * FROM budgets WHERE bookId = :bookId AND periodMonth = :month AND periodYear = :year")
    fun getBudgets(bookId: String, month: Int, year: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets")
    suspend fun getAllBudgetsSync(): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteBudgetById(budgetId: Long)

    // --- Savings Goals ---
    @Query("SELECT * FROM savings_goals WHERE bookId = :bookId ORDER BY id ASC")
    fun getSavingsGoals(bookId: String): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals")
    suspend fun getAllGoalsSync(): List<SavingsGoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<SavingsGoalEntity>)

    @Update
    suspend fun updateGoal(goal: SavingsGoalEntity)

    @Query("DELETE FROM savings_goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    // --- Bill Reminders ---
    @Query("SELECT * FROM bill_reminders WHERE bookId = :bookId ORDER BY dueDate ASC")
    fun getBillReminders(bookId: String): Flow<List<BillReminderEntity>>

    @Query("SELECT * FROM bill_reminders")
    suspend fun getAllBillsSync(): List<BillReminderEntity>

    @Query("SELECT * FROM bill_reminders WHERE id = :billId LIMIT 1")
    suspend fun getBillById(billId: Long): BillReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<BillReminderEntity>)

    @Query("UPDATE bill_reminders SET isPaid = :isPaid WHERE id = :billId")
    suspend fun updateBillPaidStatus(billId: Long, isPaid: Boolean)

    @Query("DELETE FROM bill_reminders WHERE id = :billId")
    suspend fun deleteBillById(billId: Long)

    // --- Debts (Borrowed & Lent Money) ---
    @Query("SELECT * FROM debts WHERE bookId = :bookId ORDER BY isSettled ASC, dueDate ASC")
    fun getDebtsByBook(bookId: String): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts")
    suspend fun getAllDebtsSync(): List<DebtEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(debts: List<DebtEntity>)

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Query("UPDATE debts SET isSettled = :isSettled WHERE id = :debtId")
    suspend fun updateDebtSettledStatus(debtId: Long, isSettled: Boolean)

    @Query("DELETE FROM debts WHERE id = :debtId")
    suspend fun deleteDebtById(debtId: Long)

    // --- Quick Presets ---
    @Query("SELECT * FROM quick_presets WHERE bookId = :bookId ORDER BY id ASC")
    fun getQuickPresetsByBook(bookId: String): Flow<List<QuickPresetEntity>>

    @Query("SELECT * FROM quick_presets")
    suspend fun getAllPresetsSync(): List<QuickPresetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: QuickPresetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<QuickPresetEntity>)

    @Update
    suspend fun updatePreset(preset: QuickPresetEntity)

    @Query("DELETE FROM quick_presets WHERE id = :presetId")
    suspend fun deletePresetById(presetId: Long)

    // --- Notification History ---
    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC")
    fun getNotificationHistory(): Flow<List<NotificationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationHistoryEntity): Long

    @Query("UPDATE notification_history SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM notification_history")
    suspend fun clearNotificationHistory()

    // --- Audit History ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity): Long

    @Query("DELETE FROM audit_logs")
    suspend fun clearAuditLogs()

    // --- Wipe / Demo Management ---
    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    @Query("DELETE FROM accounts")
    suspend fun clearAllAccounts()

    @Query("DELETE FROM budgets")
    suspend fun clearAllBudgets()

    @Query("DELETE FROM savings_goals")
    suspend fun clearAllGoals()

    @Query("DELETE FROM bill_reminders")
    suspend fun clearAllBills()

    @Query("DELETE FROM debts")
    suspend fun clearAllDebts()

    @Query("DELETE FROM quick_presets")
    suspend fun clearAllPresets()
}
