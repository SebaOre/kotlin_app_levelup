package com.example.kotlin_app_levelup
import com.example.kotlin_app_levelup.ui.screens.home.ConfirmarUbicacionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.ui.screens.BottomNavigationBar
import com.example.kotlin_app_levelup.ui.screens.Perfil.LoginScreen
import com.example.kotlin_app_levelup.ui.screens.Perfil.PerfilScreen
import com.example.kotlin_app_levelup.ui.screens.Perfil.RegistroScreen
import com.example.kotlin_app_levelup.ui.screens.home.CartScreen
import com.example.kotlin_app_levelup.ui.screens.home.HomeScreen
import com.example.kotlin_app_levelup.ui.screens.home.ProductDetailScreen
import com.example.kotlin_app_levelup.ui.screens.miscompras.MisComprasScreen // 👈 nuevo import
import com.example.kotlin_app_levelup.ui.theme.Kotlin_app_levelupTheme
import com.example.kotlin_app_levelup.viewmodel.CartViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val cartViewModel: CartViewModel by viewModels() // VM global del carrito

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kotlin_app_levelupTheme(darkTheme = true, dynamicColor = false) {
                val navController = rememberNavController()
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val scope = rememberCoroutineScope()

                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                modifier = Modifier.padding(padding),
                                navController = navController,
                                cartViewModel = cartViewModel
                            )
                        }

                        // 🔁 Categorías -> Mis Compras
                        composable("miscompras") {
                            MisComprasScreen(modifier = Modifier.padding(padding))
                        }

                        composable("perfil") {
                            PerfilScreen(
                                modifier = Modifier.padding(padding),
                                navController = navController
                            )
                        }
                        composable("login") { LoginScreen(navController) }
                        composable("registro") { RegistroScreen(navController) }

                        // 🛒 Carrito (usa el mismo ViewModel global)
                        composable("carrito") {
                            CartScreen(
                                modifier = Modifier.padding(padding),
                                navController = navController,
                                viewModel = cartViewModel
                            )
                        }
                        composable("confirmar_ubicacion") {
                            ConfirmarUbicacionScreen(navController = navController)
                        }

                        // 🧩 Detalle de producto
                        composable("detalle/{code}") { backStackEntry ->
                            val code = backStackEntry.arguments?.getString("code") ?: ""
                            var product by remember { mutableStateOf<ProductEntity?>(null) }

                            LaunchedEffect(code) {
                                scope.launch {
                                    product = db.productDao().getByCode(code)
                                }
                            }

                            product?.let {
                                ProductDetailScreen(
                                    navController = navController,
                                    product = it,
                                    cartViewModel = cartViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
