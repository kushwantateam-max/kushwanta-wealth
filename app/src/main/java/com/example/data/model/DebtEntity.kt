package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String = FinancialBook.PERSONAL.id,
    val personName: String,
    val amount: Double,
    val isLent: Boolean, // true = You lent money (They owe you), false = You borrowed (You owe them)
    val dueDate: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000),
    val isSettled: Boolean = false,
    val note: String = ""
)
