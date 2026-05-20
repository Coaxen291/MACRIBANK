package com.utp.macribank.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val balance: Double = 0.0,
    val accountNumber: String = ""
)
