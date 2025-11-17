package com.example.kotlin_app_levelup.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlin_app_levelup.R
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(navController: NavController) {

    // Animación de aparición
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Fade in
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = LinearOutSlowInEasing)
        )

        delay(1500)

        // Fade out
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(800, easing = FastOutLinearInEasing)
        )

        // Navegar al Home
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo animado
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "LevelUp Logo",
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer(alpha = alpha.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Texto animado
            Text(
                text = "Bienvenido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = alpha.value)
            )
        }
    }
}
