package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String, // "CREATE", "EDIT", "DELETE", "TRANSFER", "REFUND", "RESTORE", "WIPE"
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)
