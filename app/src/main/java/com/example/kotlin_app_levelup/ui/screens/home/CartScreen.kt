package com.example.kotlin_app_levelup.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app_levelup.viewmodel.CartItem
import com.example.kotlin_app_levelup.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val total by remember(cartItems) {
        mutableStateOf(cartItems.sumOf { it.product.price * it.quantity })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Carrito 🛒",
                        color = Color(0xFF39FF14),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1E90FF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            cartItem = item,
                            onRemove = { viewModel.removeItem(item.product.code) },
                            onQtyChange = { newQty ->
                                if (newQty <= 0) {
                                    viewModel.removeItem(item.product.code)
                                } else {
                                    viewModel.updateQuantity(item.product.code, newQty)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Total: $${total}",
                    color = Color(0xFF39FF14),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.clearCart() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar pedido", color = Color.Black, fontWeight = FontWeight.Bold)
                }
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
                painter = rememberAsyncImagePainter(cartItem.product.imageRes),
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
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
