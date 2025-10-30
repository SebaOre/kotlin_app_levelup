package com.example.kotlin_app_levelup.ui.screens.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.viewmodel.CartViewModel

@Composable
fun ProductDetailScreen(
    navController: NavController,
    product: ProductEntity,
    cartViewModel: CartViewModel
) {
    var addedToCart by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen principal (usa Coil para cargar desde recurso o URL)
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
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // === BOTÓN AGREGAR AL CARRITO ===
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

        // === BOTÓN VOLVER ===
        OutlinedButton(
            onClick = { navController.popBackStack() },
            border = BorderStroke(1.dp, Color.DarkGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("← Volver", color = Color.Gray)
        }
    }
}
