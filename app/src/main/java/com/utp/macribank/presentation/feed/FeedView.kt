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
import androidx.compose.runtime.*
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

    // Estados para la "Cortina" (Bottom Sheet)
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedTransactionForSheet by remember { mutableStateOf<Transaction?>(null) }

    // Forzar actualización al entrar a la vista
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

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
                    // Al hacer clic, mostramos la "Cortina"
                    selectedTransactionForSheet = transaction
                    showBottomSheet = true
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // LA CORTINA (Modal Bottom Sheet)
    if (showBottomSheet) {
        selectedTransactionForSheet?.let { transaction ->
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                TransactionDetailSheetContent(transaction)
            }
        }
    }
}

@Composable
fun TransactionDetailSheetContent(transaction: Transaction) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Comprobante de Movimiento",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = (if (transaction.type == TransactionType.INCOME) "+" else "-") + formatCurrency(transaction.amount),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == TransactionType.INCOME) Color(0xFF2E7D32) else Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (transaction.type == TransactionType.INCOME) "Dinero recibido" else "Dinero enviado",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Descripción", transaction.description)
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                DetailRow("Fecha", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(transaction.date))
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                DetailRow("Hora", SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(transaction.date))
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                DetailRow("Categoría", transaction.category)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { /* Compartir logic */ },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Compartir Comprobante")
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
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
