package com.example.kotlin_app_levelup.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app_levelup.data.local.ProductEntity

@Composable
fun ProductCard(product: ProductEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101010)),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { /* Detalle del producto */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .background(Color.Black)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$${product.price}",
                color = Color(0xFF39FF14),
                fontSize = 14.sp
            )
        }
    }
}
