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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    
    private var userJob: Job? = null
    private var transJob: Job? = null

    init {
        refreshData()
    }

    fun refreshData() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            _userState.value = User()
            _transactions.value = emptyList()
            return
        }

        // Cancelar suscripciones anteriores si existen
        userJob?.cancel()
        transJob?.cancel()

        // Escuchar datos del usuario
        userJob = authRepository.getUserData(currentUser.id).onEach { result ->
            if (result is Resource.Success) {
                result.data?.let { _userState.value = it }
            }
        }.launchIn(viewModelScope)

        // Escuchar transacciones
        transJob = transactionRepository.getTransactions(currentUser.id).onEach { result ->
            if (result is Resource.Success) {
                _transactions.value = result.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }

    fun logout() {
        authRepository.logout()
        // Limpiar estado inmediatamente
        _userState.value = User(name = "Cerrando sesión...")
        _transactions.value = emptyList()
        userJob?.cancel()
        transJob?.cancel()
    }
}
