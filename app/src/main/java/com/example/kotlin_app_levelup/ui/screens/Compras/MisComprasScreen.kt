package com.example.kotlin_app_levelup.ui.screens.miscompras

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app_levelup.R
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.PurchaseEntity
import com.example.kotlin_app_levelup.data.local.PurchaseItemEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisComprasScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    var purchases by remember { mutableStateOf<List<PurchaseEntity>>(emptyList()) }
    var itemsByPurchase by remember { mutableStateOf<Map<Long, List<PurchaseItemEntity>>>(emptyMap()) }

    // 🔁 Cargar Compras + Items desde Room
    LaunchedEffect(Unit) {
        val allPurchases = db.purchaseDao().getAll().sortedByDescending { it.createdAt }
        val mapa = mutableMapOf<Long, List<PurchaseItemEntity>>()

        allPurchases.forEach { p ->
            mapa[p.id] = db.purchaseItemDao().getByPurchase(p.id)
        }

        purchases = allPurchases
        itemsByPurchase = mapa
    }

    val fmt = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(model = R.drawable.volver),
                            contentDescription = "Volver",
                            modifier = Modifier
                                .size(75.dp)
                                .padding(end = 4.dp)
                                .clickable { navController.popBackStack() }
                        )
                        Text(
                            text = "Mis Compras 🧾",
                            color = Color(0xFF39FF14),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { inner ->

        Column(
            modifier = modifier
                .background(Color.Black)
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (purchases.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

                            // 🧾 INFO COMPRA
                            Text("Cliente: ${p.buyerName}", color = Color(0xFF39FF14), fontSize = 15.sp)
                            Text("Fecha: ${fmt.format(Date(p.createdAt))}", color = Color.White, fontSize = 14.sp)
                            Text("Total: $${formatPrice(p.total)}", color = Color(0xFF39FF14), fontSize = 14.sp)
                            Text("Entrega: ${p.deliveryAddress ?: "—"}", color = Color.LightGray, fontSize = 13.sp)

                            Spacer(modifier = Modifier.height(10.dp))

                            // 📦 PRODUCTOS DENTRO DE LA COMPRA
                            itemsByPurchase[p.id]?.forEach { item ->

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {

                                    // 🔥 Imagen desde URL con COIL
                                    Image(
                                        painter = rememberAsyncImagePainter(model = item.imageUrl),
                                        contentDescription = item.productName,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(Modifier.width(10.dp))

                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            item.productName,
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            "x${item.quantity} • $${formatPrice(item.price)} c/u",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Text(
                                        "$${formatPrice(item.price * item.quantity)}",
                                        color = Color(0xFF39FF14),
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

// Formato de moneda simple tipo CLP
private fun formatPrice(n: Int): String = "%,d".format(n).replace(',', '.')
