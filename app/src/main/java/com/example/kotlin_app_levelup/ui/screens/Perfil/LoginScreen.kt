package com.example.kotlin_app_levelup.ui.screens.Perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar sesión 🎮", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF39FF14))

        Spacer(modifier = Modifier.height(40.dp))

        TextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico", color = Color.LightGray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña", color = Color.LightGray) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // BOTÓN LOGIN
        Button(
            onClick = {
                scope.launch {
                    val user = userDao.getUserByEmail(correo)
                    if (user != null && user.contrasena == contrasena) {
                        userPrefs.setLoggedIn(true)
                        userPrefs.saveUser(user.nombre, user.correo, user.edad)
                        mensaje = "Bienvenido ${user.nombre}"
                        navController.navigate("perfil") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        mensaje = "Credenciales inválidas ❌"
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BOTÓN REGISTRO
        TextButton(onClick = { navController.navigate("registro") }) {
            Text("¿No tienes cuenta? Regístrate aquí", color = Color(0xFF39FF14))
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(mensaje, color = Color.White)
        }
    }
}
