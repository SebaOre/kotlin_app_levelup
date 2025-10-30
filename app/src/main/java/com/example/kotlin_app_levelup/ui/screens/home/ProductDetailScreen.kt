package com.example.kotlin_app_levelup.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.viewmodel.CartViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    product: ProductEntity,
    cartViewModel: CartViewModel
) {
    var addedToCart by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle 🎮",
                        color = Color(0xFF39FF14),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1E90FF)
                        )
                    }
                },
                actions = {
                    // 🛒 Carrito arriba a la derecha
                    IconButton(onClick = { navController.navigate("carrito") }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito",
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
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🖼️ Imagen
            Image(
                painter = rememberAsyncImagePainter(model = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = product.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$${product.price}",
                color = Color(0xFF39FF14),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Código: ${product.code}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Descripción: ${product.description}",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // ➕ Agregar al carrito
            Button(
                onClick = {
                    cartViewModel.addToCart(product)
                    addedToCart = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (addedToCart) "✅ Añadido al carrito" else "🛒 Añadir al carrito",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ⬅️ Volver
            OutlinedButton(
                onClick = { navController.popBackStack() },
                border = BorderStroke(1.dp, Color.DarkGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("← Volver", color = Color.Gray)
            }
        }
    }
}
