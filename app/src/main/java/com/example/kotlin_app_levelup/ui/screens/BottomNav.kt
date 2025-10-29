package com.example.kotlin_app_levelup.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlin_app_levelup.data.local.UserPreferences

sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavItem("home", "Inicio", Icons.Filled.Home)
    object Categorias : BottomNavItem("categorias", "Categorías", Icons.Filled.Category)
    object Perfil : BottomNavItem("perfil", "Perfil", Icons.Filled.Person)
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val context = LocalContext.current
    val userPrefs = UserPreferences(context)

    // Lee la ruta actual del NavController (fuente de verdad)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteRaw = backStackEntry?.destination?.route ?: "home"
    // Mapea login/registro como 'perfil' para marcar el tab correcto
    val currentRoute = when (currentRouteRaw) {
        "login", "registro" -> "perfil"
        else -> currentRouteRaw
    }

    // Nombre dinámico del usuario (opcional para label del Perfil)
    val userName = userPrefs.userNameFlow.collectAsState(initial = "").value
    val isLoggedIn = userPrefs.isLoggedInFlow.collectAsState(initial = false).value

    NavigationBar(containerColor = Color.Black) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Categorias,
            BottomNavItem.Perfil
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            // Animaciones gamer
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.15f else 1f, label = "")
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF39FF14) else Color(0xFFD3D3D3),
                label = ""
            )

            val labelText = when (item) {
                BottomNavItem.Perfil ->
                    if (!isLoggedIn) "Mi cuenta" else if (userName.isNotBlank()) userName else "Perfil"
                else -> item.label
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    when (item.route) {
                        "home" -> navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                        "categorias" -> navController.navigate("categorias") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                        "perfil" -> {
                            if (!isLoggedIn) {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = false }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate("perfil") {
                                    popUpTo("home") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = labelText,
                        modifier = Modifier.scale(scale),
                        tint = iconColor
                    )
                },
                label = {
                    Text(text = labelText, color = iconColor)
                }
            )
        }
    }
}
