package com.utp.macribank.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utp.macribank.domain.model.User
import com.utp.macribank.domain.repository.AuthRepository
import com.utp.macribank.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = mutableStateOf<Resource<User>?>(null)
    val loginState: State<Resource<User>?> = _loginState

    fun login(email: String, password: String) {
        repository.login(email, password).onEach { result ->
            _loginState.value = result
        }.launchIn(viewModelScope)
    }

    fun register(name: String, email: String, password: String) {
        repository.register(name, email, password).onEach { result ->
            _loginState.value = result
        }.launchIn(viewModelScope)
    }
}
