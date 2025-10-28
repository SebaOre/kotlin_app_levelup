package com.example.kotlin_app_levelup.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavItem("home", "Inicio", Icons.Filled.Home)
    object Categorias : BottomNavItem("categorias", "Categorías", Icons.Filled.Category)
    object Perfil : BottomNavItem("perfil", "Mi Cuenta", Icons.Filled.Person)
}

@Composable
fun BottomNavigationBar(selectedRoute: String, onItemSelected: (String) -> Unit) {
    NavigationBar(containerColor = Color.Black) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Categorias,
            BottomNavItem.Perfil
        )
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onItemSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selectedRoute == item.route) Color(0xFF39FF14) else Color(0xFFD3D3D3)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selectedRoute == item.route) Color(0xFF39FF14) else Color(0xFFD3D3D3)
                    )
                }
            )
        }
    }
}
