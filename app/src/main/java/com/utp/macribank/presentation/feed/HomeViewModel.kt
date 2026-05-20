package com.utp.macribank.presentation.feed

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utp.macribank.domain.model.Transaction
import com.utp.macribank.domain.model.User
import com.utp.macribank.domain.repository.AuthRepository
import com.utp.macribank.domain.repository.TransactionRepository
import com.utp.macribank.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _userState = mutableStateOf(User(name = "Cargando...", balance = 0.0))
    val userState: State<User> = _userState

    private val _transactions = mutableStateOf<List<Transaction>>(emptyList())
    val transactions: State<List<Transaction>> = _transactions

    var selectedTransaction: Transaction? = null

    init {
        loadUserData()
        loadTransactions()
    }

    private fun loadTransactions() {
        val currentUser = authRepository.getCurrentUser()
        currentUser?.let { userAuth ->
            transactionRepository.getTransactions(userAuth.id).onEach { result ->
                if (result is Resource.Success) {
                    _transactions.value = result.data ?: emptyList()
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadUserData() {
        val currentUser = authRepository.getCurrentUser()
        currentUser?.let { userAuth ->
            authRepository.getUserData(userAuth.id).onEach { result ->
                if (result is Resource.Success) {
                    result.data?.let {
                        _userState.value = it
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
