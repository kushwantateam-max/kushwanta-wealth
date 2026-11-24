package com.example.data.model

data class TransactionWithDetails(
    val transaction: TransactionEntity,
    val category: CategoryEntity?,
    val account: AccountEntity?,
    val toAccount: AccountEntity? = null
)
