package com.utp.macribank.domain.model

import java.util.Date

data class Transaction(
    val id: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val description: String = "",
    val date: Date = Date(),
    val category: String = "General"
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}
