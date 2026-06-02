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

    // Estado de la transferencia (Cargando, Éxito, Error)
    private val _transferState = mutableStateOf<Resource<Unit>?>(null)
    val transferState: State<Resource<Unit>?> = _transferState

    // Campos de texto guardados en el ViewModel para que no se pierdan
    var account = mutableStateOf("")
    var amount = mutableStateOf("")
    var description = mutableStateOf("")

    fun sendMoney() {
        // Usamos los valores actuales de las variables del ViewModel
        val cleanAmount = amount.value.trim().replace(",", ".")
        val amountDouble = cleanAmount.toDoubleOrNull() ?: -1.0
        
        if (amountDouble <= 0) {
            _transferState.value = Resource.Error("Por favor, ingresa un monto válido (mayor a 0)")
            return
        }
        
        repository.sendMoney(account.value, amountDouble, description.value).onEach { result ->
            _transferState.value = result
        }.launchIn(viewModelScope)
    }

    fun deposit(amountStr: String) {
        val amountDouble = amountStr.toDoubleOrNull() ?: 0.0
        if (amountDouble <= 0) {
            _transferState.value = Resource.Error("Monto inválido")
            return
        }

        repository.depositMoney(amountDouble).onEach { result ->
            _transferState.value = result
        }.launchIn(viewModelScope)
    }
}
