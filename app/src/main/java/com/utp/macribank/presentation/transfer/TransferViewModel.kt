package com.utp.macribank.presentation.transfer

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

    var accountNumber = mutableStateOf("")
    var amount = mutableStateOf("")
    var description = mutableStateOf("")

    fun sendMoney() {
        val amountDouble = amount.value.toDoubleOrNull() ?: return
        
        repository.sendMoney(
            toAccountNumber = accountNumber.value,
            amount = amountDouble,
            description = description.value
        ).onEach { result ->
            _transferState.value = result
        }.launchIn(viewModelScope)
    }
    
    fun resetState() {
        _transferState.value = null
    }
}
