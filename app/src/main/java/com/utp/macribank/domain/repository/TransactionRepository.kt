package com.utp.macribank.domain.repository

import com.utp.macribank.domain.model.Transaction
import com.utp.macribank.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun sendMoney(toAccountNumber: String, amount: Double, description: String): Flow<Resource<Unit>>
    fun depositMoney(amount: Double): Flow<Resource<Unit>>
    fun getTransactions(userId: String): Flow<Resource<List<Transaction>>>
}
