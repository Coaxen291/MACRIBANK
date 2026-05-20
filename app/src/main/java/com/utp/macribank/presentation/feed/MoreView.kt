package com.utp.macribank.presentation.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreView(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Más Opciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Gestión de cuenta",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            item { MoreItem("Mis Tarjetas", Icons.Default.CreditCard) }
            item { 
                MoreItem("Consignar Plata", Icons.Default.AccountBalance) {
                    navController.navigate("deposit")
                }
            }
            item { MoreItem("Préstamos", Icons.Default.AccountBalance) }
            item { MoreItem("Inversiones", Icons.Default.TrendingUp) }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Seguridad y Ayuda",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            item { MoreItem("Cambiar Clave", Icons.Default.Lock) }
            item { MoreItem("Atención al Cliente", Icons.Default.SupportAgent) }
            item { MoreItem("Puntos de Atención", Icons.Default.LocationOn) }
        }
    }
}

@Composable
fun MoreItem(label: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        ListItem(
            headlineContent = { Text(label) },
            leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
        )
    }
}
