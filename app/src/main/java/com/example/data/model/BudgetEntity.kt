package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String = FinancialBook.PERSONAL.id,
    val categoryId: Long,
    val monthlyLimit: Double,
    val periodMonth: Int, // 1 to 12
    val periodYear: Int
)
