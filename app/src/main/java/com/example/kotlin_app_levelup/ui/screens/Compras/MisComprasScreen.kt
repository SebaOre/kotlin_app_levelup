package com.example.kotlin_app_levelup.ui.screens.miscompras

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.PurchaseEntity
import com.example.kotlin_app_levelup.data.local.PurchaseItemEntity
import java.text.SimpleDateFormat
import java.util.*



@Composable
fun MisComprasScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    var purchases by remember { mutableStateOf<List<PurchaseEntity>>(emptyList()) }
    var itemsByPurchase by remember { mutableStateOf<Map<Long, List<PurchaseItemEntity>>>(emptyMap()) }

    // 🔁 Carga inicial (Room)
    LaunchedEffect(Unit) {
        try {
            val allPurchases = db.purchaseDao().getAll().sortedByDescending { it.createdAt }
            val mapa = mutableMapOf<Long, List<PurchaseItemEntity>>()
            for (p in allPurchases) {
                mapa[p.id] = db.purchaseItemDao().getByPurchase(p.id)
            }
            purchases = allPurchases
            itemsByPurchase = mapa
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val fmt = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Mis Compras 🧾",
            color = Color.White,
            fontSize = 22.sp,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        if (purchases.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes compras", color = Color.Gray)
            }
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(purchases) { p ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        // 🧾 Encabezado
                        Text("Cliente: ${p.buyerName}", color = Color(0xFF39FF14), fontWeight = FontWeight.Bold)
                        Text("Fecha: ${fmt.format(Date(p.createdAt))}", color = Color.White, fontSize = 14.sp)
                        Text("Total: $${formatPrice(p.total)}", color = Color(0xFF39FF14), fontSize = 14.sp)
                        Text("Entrega: ${p.deliveryAddress ?: "—"}", color = Color.LightGray, fontSize = 13.sp)

                        Spacer(Modifier.height(10.dp))

                        // 📦 Productos comprados
                        itemsByPurchase[p.id]?.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = item.imageRes),
                                    contentDescription = item.productName,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(Modifier.width(10.dp))

                                Column(Modifier.weight(1f)) {
                                    Text(item.productName, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(
                                        "x${item.quantity} • $${formatPrice(item.price)} c/u",
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }

                                Text(
                                    "$${formatPrice(item.price * item.quantity)}",
                                    color = Color(0xFF39FF14),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

private fun formatPrice(n: Int): String =
    "%,d".format(n).replace(',', '.')
