package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String = FinancialBook.PERSONAL.id,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val accountNumberLast4: String = "",
    val cardColorHex: String = "#1D4ED8",
    val isDefault: Boolean = false,
    val creditLimit: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
)
