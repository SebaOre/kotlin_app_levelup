package com.example.kotlin_app_levelup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.kotlin_app_levelup.ui.screens.BottomNavigationBar
import com.example.kotlin_app_levelup.ui.screens.Categorias.CategoriaScreen
import com.example.kotlin_app_levelup.ui.screens.Perfil.PerfilScreen
import com.example.kotlin_app_levelup.ui.screens.home.HomeScreen
import com.example.kotlin_app_levelup.ui.theme.Kotlin_app_levelupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kotlin_app_levelupTheme {

                // Estado que controla la pestaña seleccionada
                var selectedRoute by remember { mutableStateOf("home") }

                // Estructura principal con barra inferior
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            selectedRoute = selectedRoute,
                            onItemSelected = { selectedRoute = it }
                        )
                    }
                ) { padding ->

                    // Renderiza la pantalla correspondiente, aplicando el padding del Scaffold
                    when (selectedRoute) {
                        "home" -> HomeScreen(modifier = Modifier.padding(padding))
                        "categorias" -> CategoriaScreen(modifier = Modifier.padding(padding))
                        "perfil" -> PerfilScreen(modifier = Modifier.padding(padding))
                    }
                }
            }
        }
    }
}