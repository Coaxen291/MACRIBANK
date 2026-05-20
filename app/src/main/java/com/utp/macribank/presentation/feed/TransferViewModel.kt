package com.utp.macribank.presentation.feed

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utp.macribank.domain.repository.TransactionRepository
import com.utp.macribank.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transferState = mutableStateOf<Resource<Unit>?>(null)
    val transferState: State<Resource<Unit>?> = _transferState

    fun sendMoney(account: String, amount: String, description: String) {
        val amountDouble = amount.toDoubleOrNull() ?: 0.0
        if (amountDouble <= 0) {
            _transferState.value = Resource.Error("Monto inválido")
            return
        }
        
        repository.sendMoney(account, amountDouble, description).onEach { result ->
            _transferState.value = result
        }.launchIn(viewModelScope)
    }

    fun deposit(amount: String) {
        val amountDouble = amount.toDoubleOrNull() ?: 0.0
        if (amountDouble <= 0) {
            _transferState.value = Resource.Error("Monto inválido")
            return
        }

        repository.depositMoney(amountDouble).onEach { result ->
            _transferState.value = result
        }.launchIn(viewModelScope)
    }
}
