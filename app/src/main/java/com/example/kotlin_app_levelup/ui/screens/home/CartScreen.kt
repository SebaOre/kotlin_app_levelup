package com.example.kotlin_app_levelup.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.withTransaction
import com.example.kotlin_app_levelup.R
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.PurchaseEntity
import com.example.kotlin_app_levelup.data.local.PurchaseItemEntity
import com.example.kotlin_app_levelup.viewmodel.CartItem
import com.example.kotlin_app_levelup.viewmodel.CartViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel
) {
    val cartItems: List<CartItem> by viewModel.cartItems.collectAsState()
    val total: Int = cartItems.sumOf { it.product.price * it.quantity }

    // Dirección desde ConfirmarUbicacionScreen (nullable)
    val deliveryAddress by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("delivery_address", null)
        ?.collectAsState(initial = null) ?: remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()
    val snack = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_volver),
                            contentDescription = "Level-Up (Volver)",
                            modifier = Modifier
                                .size(75.dp)
                                .clickable { navController.popBackStack() }
                        )
                        Text(
                            text = "Mi Carrito 🛒",
                            color = Color(0xFF39FF14),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        snackbarHost = { SnackbarHost(snack) },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (cartItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío", color = Color.Gray)
                }
                return@Column
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        cartItem = item,
                        onRemove = { viewModel.removeItem(item.product.code) },
                        onQtyChange = { newQty ->
                            if (newQty <= 0) viewModel.removeItem(item.product.code)
                            else viewModel.updateQuantity(item.product.code, newQty)
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF101010), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text("Dirección de entrega", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = deliveryAddress ?: "Sin seleccionar",
                    color = if (deliveryAddress == null) Color.Gray else Color(0xFF39FF14)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { navController.navigate("confirmar_ubicacion") },
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                ) {
                    Text("Elegir / Cambiar ubicación")
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Total: $${total}",
                color = Color(0xFF39FF14),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        // capturar el delegado en variable normal pa’ evitar smart cast
                        val addr: String? = deliveryAddress
                        if (addr.isNullOrBlank()) {
                            snack.showSnackbar("Selecciona una ubicación de entrega primero")
                            return@launch
                        }

                        db.withTransaction {
                            val purchaseId = db.purchaseDao().insert(
                                PurchaseEntity(
                                    buyerName = "Usuario",
                                    total = total,
                                    deliveryAddress = addr
                                )
                            )
                            val items = cartItems.map { ci ->
                                PurchaseItemEntity(
                                    purchaseId = purchaseId,
                                    productCode = ci.product.code,
                                    productName = ci.product.name,
                                    price = ci.product.price,
                                    quantity = ci.quantity,
                                    imageRes = ci.product.imageRes
                                )
                            }
                            db.purchaseItemDao().insertAll(items)
                        }

                        // vaciar carrito con APIs existentes
                        cartItems.map { it.product.code }.distinct().forEach { code ->
                            viewModel.removeItem(code)
                        }

                        // limpiar estado de la dirección guardada
                        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("delivery_address")

                        // navegar a MisCompras y sacar el carrito del backstack
                        navController.navigate("miscompras") {
                            // si tienes una ruta "carrito", bórrala del stack:
                            popUpTo("carrito") { inclusive = true }

                            // si no usas nombre de ruta y quieres volver al inicio del grafo:
                            // popUpTo(navController.graph.startDestinationId) { saveState = true }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar pedido", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onRemove: () -> Unit,
    onQtyChange: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = cartItem.product.imageRes),
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(cartItem.product.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Precio: $${cartItem.product.price}", color = Color(0xFF39FF14))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { onQtyChange((cartItem.quantity - 1).coerceAtLeast(0)) }) {
                        Text("-", color = Color.White)
                    }
                    Text("${cartItem.quantity}", color = Color.White)
                    TextButton(onClick = { onQtyChange(cartItem.quantity + 1) }) {
                        Text("+", color = Color.White)
                    }
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}
