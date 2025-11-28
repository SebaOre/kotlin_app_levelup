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
import com.example.kotlin_app_levelup.data.local.UserEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
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
        Text("Registrar nuevo usuario", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF39FF14))
        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre", color = Color.LightGray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico", color = Color.LightGray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = edad,
            onValueChange = { edad = it.filter { ch -> ch.isDigit() }.take(3) },
            label = { Text("Edad", color = Color.LightGray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña", color = Color.LightGray) },
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

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {

                    // Validación campos obligatorios
                    if (nombre.isBlank() || correo.isBlank() || edad.isBlank() || contrasena.isBlank()) {
                        mensaje = "Completa todos los campos"
                        return@launch
                    }

                    val nombreRegex= Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")
                    if (!nombreRegex.matches(nombre.trim())){
                        mensaje = "Ingrese nombre Valido"
                        return@launch
                    }

                    // Validación correo
                    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                    if (!emailRegex.matches(correo.trim())) {
                        mensaje = "Correo inválido"
                        return@launch
                    }

                    // Validación edad
                    val edadInt = edad.toIntOrNull()
                    if (edadInt == null || edadInt < 13) {
                        mensaje = "Ingresa una edad válida (mínimo 13 años)"
                        return@launch
                    }

                    // Validar contraseña
                    if (contrasena.length < 6) {
                        mensaje = "La contraseña debe tener al menos 6 caracteres"
                        return@launch
                    }

                    // Verificar correo duplicado
                    val existe = userDao.getUserByEmail(correo.trim())
                    if (existe != null) {
                        mensaje = "Este correo ya está registrado"
                        return@launch
                    }

                    // Si todo OK → insertar
                    userDao.insertUser(
                        UserEntity(
                            nombre = nombre.trim(),
                            correo = correo.trim(),
                            edad = edadInt,
                            contrasena = contrasena.trim(),
                            registrado = true
                        )
                    )

                    mensaje = "Usuario registrado correctamente"

                    navController.navigate("login") {
                        popUpTo("registro") { inclusive = true }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = Color.White)
        }
    }
}
