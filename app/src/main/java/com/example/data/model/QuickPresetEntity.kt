package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_presets")
data class QuickPresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String = FinancialBook.PERSONAL.id,
    val title: String,
    val emoji: String,
    val amountUsd: Double,
    val categoryName: String,
    val isExpense: Boolean = true
)
