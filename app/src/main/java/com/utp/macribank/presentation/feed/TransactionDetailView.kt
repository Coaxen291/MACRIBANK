package com.utp.macribank.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.utp.macribank.domain.model.Transaction
import com.utp.macribank.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailView(
    navController: NavController,
    transaction: Transaction
) {
    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comprobante", textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Lógica para compartir comprobante */ }) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "Compartir")
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
            // Icono de estado
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.type == TransactionType.INCOME) Color(0xFFE8F5E9) else Color(0xFFFBE9E7)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type == TransactionType.INCOME) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = if (transaction.type == TransactionType.INCOME) Color(0xFF2E7D32) else Color(0xFFD84315)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (transaction.type == TransactionType.INCOME) "Dinero Recibido" else "Dinero Enviado",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            Text(
                text = (if (transaction.type == TransactionType.INCOME) "+" else "-") + formatCurrency(transaction.amount),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.INCOME) Color(0xFF2E7D32) else Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailItem("Descripción", transaction.description)
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    DetailItem("Fecha", dateFormat.format(transaction.date))
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    DetailItem("Hora", timeFormat.format(transaction.date))
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    DetailItem("Categoría", transaction.category)
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    DetailItem("ID de Transacción", transaction.id.take(12).uppercase())
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Este es un comprobante oficial de MACRI BANK",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
