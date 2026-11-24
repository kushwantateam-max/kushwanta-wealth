package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String = FinancialBook.PERSONAL.id,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: Long = System.currentTimeMillis() + (90L * 24 * 3600 * 1000), // default 90 days
    val colorHex: String = "#10B981"
)
