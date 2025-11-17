package com.example.kotlin_app_levelup.ui.screens.home

import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app_levelup.R
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.data.local.UserPreferences
import com.example.kotlin_app_levelup.viewmodel.CartViewModel
import com.example.kotlin_app_levelup.viewmodel.ReviewsViewModel
import com.example.kotlin_app_levelup.viewmodel.ReviewsViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    product: ProductEntity,
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedInFlow.collectAsState(initial = false)
    val userName by userPrefs.userNameFlow.collectAsState(initial = "")

    // ViewModel de reseñas (Room)
    val reviewsVM: ReviewsViewModel = viewModel(factory = ReviewsViewModelFactory(context, product.code))
    val reviews by reviewsVM.reviews.collectAsState()
    val avgRating by reviewsVM.avgRating.collectAsState()

    // carrito
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartCount by remember(cartItems) { mutableIntStateOf(cartItems.sumOf { it.quantity }) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Carrusel
    // 🔥 Ahora las imágenes vienen de URLs
    val images: List<String> = remember(product) {
        if (product.imageCarrusel.isNotEmpty()) product.imageCarrusel
        else listOf(product.image) // usa imagen principal si no hay lista
    }

    val pagerState = rememberPagerState(pageCount = { images.size })

    var newRating by remember { mutableStateOf(0) }
    var newText by remember { mutableStateOf("") }

    // ===== SUGERENCIAS =====
    var suggestions by remember(product.code, product.categoria) { mutableStateOf<List<ProductEntity>>(emptyList()) }
    LaunchedEffect(product.code, product.categoria) {
        // trae de la misma categoría, excluyendo el actual
        val all = db.productDao().getSuggestions(product.categoria, product.code)
        suggestions = all.take(8) // muestra hasta 8 miniaturas
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.volver),
                            contentDescription = "Level-Up (Volver)",
                            modifier = Modifier
                                .size(75.dp)
                                .clickable { navController.popBackStack() }
                        )
                        Text(
                            text = "Detalle 🎮",
                            color = Color(0xFF39FF14),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                        IconButton(onClick = { navController.navigate("carrito") }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = Color(0xFF1E90FF)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== Carrusel =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val resId = images[page]
                    Image(
                        painter = rememberAsyncImagePainter(model = resId),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(images.size) { idx ->
                        val selected = pagerState.currentPage == idx
                        Box(
                            modifier = Modifier
                                .size(if (selected) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (selected) Color(0xFF39FF14) else Color.DarkGray)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ⭐ Promedio
            Row(verticalAlignment = Alignment.CenterVertically) {
                val starsFilled = avgRating.toInt()
                repeat(5) { i ->
                    val filled = reviews.isNotEmpty() && i < starsFilled
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = null,
                        tint = if (filled) Color(0xFF39FF14) else Color.Gray
                    )
                }
                if (reviews.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${"%.1f".format(avgRating)} (${reviews.size} reseñas)",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ===== Detalles =====
            Text(
                text = product.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            val formattedPrice = NumberFormat.getNumberInstance(Locale("es", "CL")).format(product.price)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$$formattedPrice",
                color = Color(0xFF39FF14),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("Categoría: ${product.categoria}", color = Color.Gray, fontSize = 12.sp)
            Text("Código: ${product.code}", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Descripción: ${product.description}", color = Color.White, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Añadir al carrito =====
            Button(
                onClick = {
                    if (!isLoggedIn) {
                        navController.navigate("login")
                    } else {
                        cartViewModel.addToCart(product)
                        scope.launch { snackbarHostState.showSnackbar("Producto añadido") }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🛒 Añadir al carrito", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Reseñas =====
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Reseñas", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                if (reviews.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        reviews.forEach { r ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1A1A1A), shape = MaterialTheme.shapes.medium)
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(r.author, color = Color.White, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(8.dp))
                                    Row {
                                        repeat(5) { i ->
                                            val filled = i < r.rating
                                            Icon(
                                                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                                contentDescription = null,
                                                tint = if (filled) Color(0xFF39FF14) else Color.Gray
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(r.text, color = Color.LightGray)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Nueva reseña
                var newRating by remember { mutableStateOf(0) }
                var newText by remember { mutableStateOf("") }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { i ->
                        val idx = i + 1
                        IconButton(onClick = { newRating = idx }) {
                            Icon(
                                imageVector = if (idx <= newRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Calificar $idx",
                                tint = if (idx <= newRating) Color(0xFF39FF14) else Color.Gray
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = newText,
                    onValueChange = { newText = it },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    placeholder = { Text("Escribe tu reseña…", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF39FF14),
                        unfocusedBorderColor = Color.DarkGray,
                        cursorColor = Color(0xFF39FF14),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (newRating in 1..5 && newText.isNotBlank()) {
                            val author = if (isLoggedIn && userName.isNotBlank()) userName else "Anónimo"
                            reviewsVM.addReview(author = author, rating = newRating, text = newText)
                            newRating = 0
                            newText = ""
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Ponle estrellas y escribe algo primero") }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E90FF))
                ) {
                    Text("Enviar reseña", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Sugerencias (miniaturas) =====
            if (suggestions.isNotEmpty()) {
                Text("También te puede interesar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))

                // rejilla simple 2xN en columna (mini cards)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    suggestions.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            row.forEach { p ->
                                SuggestionMiniCard(p) {
                                    navController.navigate("detalle/${p.code}")
                                }
                            }
                            if (row.size == 1) {
                                Spacer(Modifier.weight(1f)) // rellena si impar
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SuggestionMiniCard(p: ProductEntity, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
        modifier = Modifier
            .height(120.dp)
            .clickable { onClick() }
    ) {
        Row(Modifier.fillMaxSize().padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = p.imageCarrusel.firstOrNull() ?: p.image
                ),
                contentDescription = p.name,
                modifier = Modifier
                    .height(110.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                Text(p.name, color = Color.White, maxLines = 2)
                Text("$" + NumberFormat.getNumberInstance(Locale("es", "CL")).format(p.price),
                    color = Color(0xFF39FF14), fontWeight = FontWeight.Bold)
            }
        }
    }
}
