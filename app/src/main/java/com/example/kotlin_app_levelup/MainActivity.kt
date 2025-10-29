package com.example.kotlin_app_levelup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_app_levelup.ui.screens.BottomNavigationBar
import com.example.kotlin_app_levelup.ui.screens.Categorias.CategoriaScreen
import com.example.kotlin_app_levelup.ui.screens.Perfil.LoginScreen
import com.example.kotlin_app_levelup.ui.screens.Perfil.PerfilScreen
import com.example.kotlin_app_levelup.ui.screens.Perfil.RegistroScreen
import com.example.kotlin_app_levelup.ui.screens.home.HomeScreen
import com.example.kotlin_app_levelup.ui.theme.Kotlin_app_levelupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kotlin_app_levelupTheme {

                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("home") { HomeScreen(modifier = Modifier.padding(padding)) }
                        composable("categorias") { CategoriaScreen(modifier = Modifier.padding(padding)) }
                        composable("perfil") { PerfilScreen(modifier = Modifier.padding(padding), navController = navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("registro") { RegistroScreen(navController) }
                    }
                }
            }
        }
    }
}
