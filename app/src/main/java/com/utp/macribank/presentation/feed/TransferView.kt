package com.utp.macribank.presentation.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.utp.macribank.domain.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferView(
    navController: NavController,
    viewModel: TransferViewModel = hiltViewModel()
) {
    // Obtenemos los valores desde el ViewModel
    val account by viewModel.account
    val amount by viewModel.amount
    val description by viewModel.description
    
    val transferState by viewModel.transferState
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Escuchar el resultado de la verificación de la cámara
    val verificationResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("verified")
        ?.observeAsState()

    LaunchedEffect(verificationResult?.value) {
        if (verificationResult?.value == true) {
            viewModel.sendMoney() // El ViewModel ya tiene los datos guardados
            // Limpiar el estado para que no se repita
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("verified")
        }
    }

    LaunchedEffect(transferState) {
        if (transferState is Resource.Success) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = { 
                    showSuccessDialog = false
                    navController.popBackStack() 
                }) {
                    Text("Aceptar")
                }
            },
            title = { Text("¡Transferencia Exitosa!") },
            text = { Text("La plata ha sido enviada correctamente.") },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32)) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transferir Plata") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "¿A quién le vas a enviar?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = account,
                onValueChange = { viewModel.account.value = it },
                label = { Text("Número de cuenta") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { 
                    if (it.all { char -> char.isDigit() || char == '.' || char == ',' }) {
                        viewModel.amount.value = it 
                    }
                },
                label = { Text("¿Cuánto vas a enviar?") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("$ ") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.description.value = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (transferState is Resource.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { 
                        if (account.isNotEmpty() && amount.isNotEmpty()) {
                            navController.navigate("camera_verify")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = account.isNotEmpty() && amount.isNotEmpty()
                ) {
                    Text("Continuar con Verificación")
                }
            }

            if (transferState is Resource.Error) {
                Text(
                    text = transferState?.message ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
