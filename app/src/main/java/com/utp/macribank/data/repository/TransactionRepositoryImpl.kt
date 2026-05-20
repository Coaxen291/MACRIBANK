package com.utp.macribank.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.utp.macribank.domain.model.Transaction
import com.utp.macribank.domain.model.TransactionType
import com.utp.macribank.domain.repository.TransactionRepository
import com.utp.macribank.domain.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : TransactionRepository {

    override fun sendMoney(toAccountNumber: String, amount: Double, description: String): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading())
        val fromId = auth.currentUser?.uid ?: ""

        db.collection("users")
            .whereEqualTo("accountNumber", toAccountNumber)
            .get()
            .addOnSuccessListener { query ->
                if (query.isEmpty) {
                    trySend(Resource.Error("La cuenta destino no existe"))
                    close()
                    return@addOnSuccessListener
                }

                val recipientDoc = query.documents[0]
                val toId = recipientDoc.id
                val toName = recipientDoc.getString("name") ?: "Usuario"

                db.runTransaction { transaction ->
                    val fromDoc = transaction.get(db.collection("users").document(fromId))
                    val fromName = fromDoc.getString("name") ?: "Usuario"
                    val fromAccount = fromDoc.getString("accountNumber") ?: "S.N"
                    val currentBalance = fromDoc.getDouble("balance") ?: 0.0

                    if (currentBalance < amount) {
                        throw Exception("Saldo insuficiente")
                    }

                    // Actualizar balances
                    transaction.update(db.collection("users").document(fromId), "balance", currentBalance - amount)
                    
                    val toBalance = recipientDoc.getDouble("balance") ?: 0.0
                    transaction.update(db.collection("users").document(toId), "balance", toBalance + amount)

                    val txId = UUID.randomUUID().toString()
                    
                    // Comprobante para el que envía
                    val outTx = Transaction(
                        id = txId, 
                        amount = amount, 
                        type = TransactionType.EXPENSE, 
                        description = "Enviado a: $toName ($toAccountNumber)", 
                        date = Date(), 
                        category = "Transferencia"
                    )
                    
                    // Comprobante para el que recibe (AQUÍ ESTABA EL ERROR)
                    val inTx = Transaction(
                        id = txId, 
                        amount = amount, 
                        type = TransactionType.INCOME, 
                        description = "De: $fromName (Cuenta: $fromAccount)", 
                        date = Date(), 
                        category = "Transferencia"
                    )

                    transaction.set(db.collection("users").document(fromId).collection("transactions").document(), outTx)
                    transaction.set(db.collection("users").document(toId).collection("transactions").document(), inTx)
                }.addOnSuccessListener {
                    trySend(Resource.Success(Unit))
                    close()
                }.addOnFailureListener {
                    trySend(Resource.Error(it.message ?: "Error en la transferencia"))
                    close()
                }
            }
            .addOnFailureListener {
                trySend(Resource.Error(it.message ?: "Error al buscar cuenta"))
                close()
            }
        awaitClose()
    }

    override fun depositMoney(amount: Double): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading())
        val userId = auth.currentUser?.uid ?: ""
        
        db.runTransaction { transaction ->
            val userRef = db.collection("users").document(userId)
            val userDoc = transaction.get(userRef)
            val currentBalance = userDoc.getDouble("balance") ?: 0.0
            
            transaction.update(userRef, "balance", currentBalance + amount)
            
            val txId = UUID.randomUUID().toString()
            val depositTx = Transaction(
                id = txId,
                amount = amount,
                type = TransactionType.INCOME,
                description = "Consignación en efectivo",
                date = Date(),
                category = "Depósito"
            )
            transaction.set(userRef.collection("transactions").document(), depositTx)
        }.addOnSuccessListener {
            trySend(Resource.Success(Unit))
            close()
        }.addOnFailureListener {
            trySend(Resource.Error(it.message ?: "Error al consignar"))
            close()
        }
        awaitClose()
    }

    override fun getTransactions(userId: String): Flow<Resource<List<Transaction>>> = callbackFlow {
        val listener = db.collection("users").document(userId).collection("transactions")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error al cargar movimientos"))
                    return@addSnapshotListener
                }
                val txs = snapshot?.toObjects(Transaction::class.java) ?: emptyList()
                trySend(Resource.Success(txs))
            }
        awaitClose { listener.remove() }
    }
}
