package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bill_reminders")
data class BillReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String = FinancialBook.PERSONAL.id,
    val title: String,
    val amount: Double,
    val dueDate: Long,
    val categoryName: String = "Subscriptions",
    val isPaid: Boolean = false,
    val providerIcon: String = "credit_card"
)
