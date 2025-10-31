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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kotlin_app_levelup.data.local.UserPreferences
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size

sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavItem("home", "Inicio", Icons.Filled.Home)
    object MisCompras : BottomNavItem("miscompras", "Mis Compras", Icons.Filled.Category)
    object Perfil : BottomNavItem("perfil", "Perfil", Icons.Filled.Person)
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val context = LocalContext.current
    val userPrefs = UserPreferences(context)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteRaw = backStackEntry?.destination?.route ?: "home"
    val currentRoute = when (currentRouteRaw) {
        "login", "registro" -> "perfil"
        else -> currentRouteRaw
    }

    val userName = userPrefs.userNameFlow.collectAsState(initial = "").value
    val isLoggedIn = userPrefs.isLoggedInFlow.collectAsState(initial = false).value

    NavigationBar(containerColor = Color.Black) {
        // Si NO está logueado no muestra Mis compras
        val items: List<BottomNavItem?> = if (isLoggedIn) {
            listOf(BottomNavItem.Home, BottomNavItem.MisCompras, BottomNavItem.Perfil)
        } else {
            listOf(BottomNavItem.Home, null, BottomNavItem.Perfil)
        }

        items.forEach { item ->
            if (item == null) {
                // Slot vacío
                NavigationBarItem(
                    selected = false,
                    onClick = { /* no-op */ },
                    enabled = false,
                    icon = { Box(Modifier.size(24.dp).alpha(0f)) },
                    label = { Text("") }
                )
            } else {
                val isSelected = currentRoute == item.route
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
                            "miscompras" -> navController.navigate("miscompras") {
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
                    label = { Text(text = labelText, color = iconColor) }
                )
            }
        }
    }
}
