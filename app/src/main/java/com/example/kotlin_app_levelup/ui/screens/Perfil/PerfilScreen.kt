package com.example.kotlin_app_levelup.ui.screens.Perfil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kotlin_app_levelup.data.local.AppDatabase
import com.example.kotlin_app_levelup.data.local.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }

    // 🔹 Datos del usuario
    val savedName by userPrefs.userNameFlow.collectAsState(initial = "")
    val savedEmail by userPrefs.userEmailFlow.collectAsState(initial = "")
    val savedAge by userPrefs.userAgeFlow.collectAsState(initial = 0)

    // 🔹 Imagen por usuario
    val userImageUri by userPrefs.userImageFlowForCurrentUser.collectAsState(initial = "")
    var imageUri by remember(userImageUri) {
        mutableStateOf(userImageUri.takeIf { it.isNotBlank() }?.let { Uri.parse(it) })
    }

    // === Selector de imagen ===
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null && savedEmail.isNotBlank()) {
                imageUri = uri
                scope.launch {
                    userPrefs.saveUserImageForUser(savedEmail, uri.toString())
                }
            }
        }
    }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)
    }

    // === INTERFAZ ===
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mi Perfil",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF39FF14),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // === FOTO DE PERFIL ===
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(3.dp, Color(0xFF39FF14), CircleShape)
                .background(Color.DarkGray)
                .clickable { selectImage() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "Seleccionar\nFoto",
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        InfoField(label = "Nombre", value = savedName)
        InfoField(label = "Correo", value = savedEmail)
        InfoField(label = "Edad", value = if (savedAge > 0) savedAge.toString() else "—")

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                scope.launch {
                    userPrefs.setLoggedIn(false)
                    // 🔹 No se borra la imagen de otros usuarios
                    navController.navigate("login") {
                        popUpTo("perfil") { inclusive = true }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
