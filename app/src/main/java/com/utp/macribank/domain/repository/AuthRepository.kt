package com.utp.macribank.domain.repository

import com.utp.macribank.domain.model.User
import com.utp.macribank.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<User>>
    fun register(name: String, email: String, password: String): Flow<Resource<User>>
    fun logout()
    fun getCurrentUser(): User?
    fun getUserData(userId: String): Flow<Resource<User>>
}
