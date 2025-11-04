package com.example.kotlin_app_levelup.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kotlin_app_levelup.R
import com.example.kotlin_app_levelup.ui.components.ProductCard
import com.example.kotlin_app_levelup.viewmodel.CartViewModel
import com.example.kotlin_app_levelup.viewmodel.HomeViewModel
import com.example.kotlin_app_levelup.viewmodel.HomeViewModelFactory
import com.example.kotlin_app_levelup.ui.components.Carousel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier,
    logoSize: Dp = 75.dp
) {
    Box(modifier = modifier.fillMaxSize())

    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))

    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var catMenuExpanded by remember { mutableStateOf(false) }

    // contador carrito
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartCount = remember(cartItems) { cartItems.sumOf { it.quantity } }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Level-Up",
                        modifier = Modifier.size(logoSize)
                    )
                },
                title = {
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            viewModel.searchProducts(it.text)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top =15.dp, end = 15.dp)
                            .clip(RoundedCornerShape(16.dp))
                        ,
                        placeholder = { Text("Buscar producto...", fontSize = 14.sp, color = Color.LightGray) },
                        singleLine = true,

                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.DarkGray,
                            unfocusedContainerColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF1E90FF),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1E90FF))
                        }
                    )
                },
                actions = {
                    //  Botón de categorías con menú
                    Box {
                        IconButton(onClick = { catMenuExpanded = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Categorías", tint = Color(0xFF1E90FF))
                        }
                        DropdownMenu(
                            expanded = catMenuExpanded,
                            onDismissRequest = { catMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todas", color = Color.White) },
                                onClick = {
                                    viewModel.filterByCategory(null)
                                    catMenuExpanded = false
                                }
                            )
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = Color.White) },
                                    onClick = {
                                        viewModel.filterByCategory(cat)
                                        catMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Carrito
                    BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                        IconButton(onClick = { navController.navigate("carrito") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color(0xFF1E90FF))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            item {
                // Carrusel
                Carousel()
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                // Chip de categoría seleccionada
                if (!selectedCategory.isNullOrBlank()) {
                    AssistChip(
                        onClick = { viewModel.filterByCategory(null) },
                        label = { Text("Categoría: ${selectedCategory}", color = Color.Black) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF39FF14)),
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            item {
                if (products.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Sin productos", color = Color.Gray)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .padding(4.dp)
                            .heightIn(min = 0.dp, max = 5000.dp)
                    ) {
                        items(products) { product ->
                            ProductCard(product) {
                                navController.navigate("detalle/${product.code}")
                            }
                        }
                    }
                }
            }
        }
    }
}
