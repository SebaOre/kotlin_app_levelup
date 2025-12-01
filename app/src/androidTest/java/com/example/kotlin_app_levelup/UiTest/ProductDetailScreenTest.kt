package com.example.kotlin_app_levelup.UiTest

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_app_levelup.data.local.ProductEntity
import com.example.kotlin_app_levelup.ui.screens.home.ProductDetailScreen
import com.example.kotlin_app_levelup.viewmodel.CartViewModel
import org.junit.Rule
import org.junit.Test

class ProductDetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun productDetailScreen_muestraDatosDelProducto() {

        // Producto falso para pruebas
        val fakeProduct = ProductEntity(
            code = "ABC123",
            categoria = "Consolas",
            name = "Nintendo Switch",
            price = 299990,
            year = 2019,
            image = "",
            imageCarrusel = listOf(""),
            description = "Consola portátil híbrida",
            manufacturer = "Nintendo",
            distributor = "Retail"
        )

        val fakeCartVM = CartViewModel()

        composeRule.setContent {
            ProductDetailScreen(
                navController = rememberNavController(),
                product = fakeProduct,
                cartViewModel = fakeCartVM
            )
        }

        // VERIFICACIONES UI
        composeRule.onNodeWithText("Detalle 🎮").assertIsDisplayed()
        composeRule.onNodeWithText("Nintendo Switch").assertIsDisplayed()
        composeRule.onNodeWithText("$299.990").assertIsDisplayed()
        composeRule.onNodeWithText("🛒 Añadir al carrito").assertIsDisplayed()
    }
}
