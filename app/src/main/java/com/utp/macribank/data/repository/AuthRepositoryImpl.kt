package com.utp.macribank.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.utp.macribank.domain.model.User
import com.utp.macribank.domain.repository.AuthRepository
import com.utp.macribank.domain.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: ""
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            trySend(Resource.Success(user))
                        } else {
                            trySend(Resource.Error("Usuario no encontrado"))
                        }
                    }
                    .addOnFailureListener {
                        trySend(Resource.Error(it.message ?: "Error al obtener datos"))
                    }
            }
            .addOnFailureListener {
                trySend(Resource.Error(it.message ?: "Error de autenticación"))
            }
        awaitClose()
    }

    override fun register(name: String, email: String, password: String): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: ""
                val randomAccount = (1000000000..9999999999).random().toString()
                val newUser = User(
                    id = userId,
                    name = name,
                    email = email,
                    balance = 0.0,
                    accountNumber = randomAccount
                )
                db.collection("users").document(userId).set(newUser)
                    .addOnSuccessListener {
                        trySend(Resource.Success(newUser))
                    }
                    .addOnFailureListener {
                        trySend(Resource.Error(it.message ?: "Error al guardar perfil"))
                    }
            }
            .addOnFailureListener {
                trySend(Resource.Error(it.message ?: "Error al crear cuenta"))
            }
        awaitClose()
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        return auth.currentUser?.let {
            User(id = it.uid, email = it.email ?: "")
        }
    }

    override fun getUserData(userId: String): Flow<Resource<User>> = callbackFlow {
        val listener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error de red"))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        trySend(Resource.Success(user))
                    }
                }
            }
        awaitClose { listener.remove() }
    }
}
