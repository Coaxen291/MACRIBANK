package com.utp.macribank.presentation.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class BillService(val name: String, val icon: ImageVector, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsView(navController: NavController) {
    val services = listOf(
        BillService("Energía (EPM)", Icons.Default.Lightbulb, Color(0xFFFBC02D)),
        BillService("Agua y Gas", Icons.Default.WaterDrop, Color(0xFF1976D2)),
        BillService("Internet y TV", Icons.Default.Wifi, Color(0xFF7B1FA2)),
        BillService("Telefonía Móvil", Icons.Default.PhoneAndroid, Color(0xFF388E3C)),
        BillService("Educación", Icons.Default.School, Color(0xFFD32F2F))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago de Facturas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(
                "Servicios inscritos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(services) { service ->
                    ListItem(
                        headlineContent = { Text(service.name, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("Paga tu factura pendiente") },
                        leadingContent = {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = service.color.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    service.icon,
                                    contentDescription = null,
                                    tint = service.color,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        },
                        trailingContent = {
                            Button(onClick = { /* Lógica de pago */ }) {
                                Text("Pagar")
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.White)
                    )
                }
            }
        }
    }
}
