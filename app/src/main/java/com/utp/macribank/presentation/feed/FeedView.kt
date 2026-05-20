package com.utp.macribank.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.utp.macribank.domain.model.Transaction
import com.utp.macribank.domain.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedView(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.userState
    val transactions by viewModel.transactions

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.clickable { navController.navigate("profile") }) {
                        Text("Hola,", style = MaterialTheme.typography.bodySmall)
                        Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BalanceCard(user.balance, user.accountNumber)
            }

            item {
                ActionButtonsRow(navController)
            }

            item {
                Text(
                    "Movimientos recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No tienes movimientos aún", color = Color.Gray)
                    }
                }
            }

            items(transactions) { transaction ->
                TransactionItem(transaction) {
                    viewModel.selectedTransaction = transaction
                    navController.navigate("transactionDetail")
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BalanceCard(balance: Double, accountNumber: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Saldo disponible", color = Color.White.copy(alpha = 0.8f))
                Text(
                    formatCurrency(balance),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ahorros: $accountNumber", color = Color.White.copy(alpha = 0.8f))
                Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun ActionButtonsRow(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(Icons.Default.Send, "Enviar") {
            navController.navigate("transfer")
        }
        ActionButton(Icons.Default.Add, "Recibir") {
            navController.navigate("receive")
        }
        ActionButton(Icons.Default.Receipt, "Facturas") {
            navController.navigate("bills")
        }
        ActionButton(Icons.Default.MoreHoriz, "Más") {
            navController.navigate("more")
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (transaction.type == TransactionType.INCOME) Color(0xFFE8F5E9) else Color(0xFFFBE9E7)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (transaction.type == TransactionType.INCOME) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = if (transaction.type == TransactionType.INCOME) Color(0xFF2E7D32) else Color(0xFFD84315)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.description, fontWeight = FontWeight.Medium, maxLines = 1)
            Row {
                Text(dateFormat.format(transaction.date), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(" • ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(timeFormat.format(transaction.date), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        Text(
            text = (if (transaction.type == TransactionType.INCOME) "+" else "-") + formatCurrency(transaction.amount),
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == TransactionType.INCOME) Color(0xFF2E7D32) else Color.Black
        )
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    return format.format(amount)
}
