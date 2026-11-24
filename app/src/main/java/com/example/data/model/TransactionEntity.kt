package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String = FinancialBook.PERSONAL.id,
    val accountId: Long,
    val toAccountId: Long? = null, // for transfers
    val categoryId: Long,
    val amount: Double,
    val type: TransactionType,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val recurrenceInterval: String = "", // "", "WEEKLY", "MONTHLY"
    val receiptUri: String? = null,
    val isRefund: Boolean = false,
    val profileName: String = "Primary",
    val splitDetails: String = ""
)
