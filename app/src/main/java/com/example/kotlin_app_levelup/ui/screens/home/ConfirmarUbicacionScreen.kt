package com.example.kotlin_app_levelup.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.BoundingBox
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import org.json.JSONArray
import androidx.compose.ui.viewinterop.AndroidView

/* ======================= Helpers (mismo archivo) ======================= */

private data class OsmSuggestion(
    val title: String,
    val display: String,
    val lat: Double,
    val lon: Double
)

private suspend fun searchChileOSM(
    context: android.content.Context,
    query: String,
    limit: Int = 8
): List<OsmSuggestion> = withContext(Dispatchers.IO) {
    if (query.length < 3) return@withContext emptyList<OsmSuggestion>()
    val ua = context.packageName.ifBlank { "levelup-app" }

    // Chile: countrycodes=cl + bbox Chile para acotar aún más
    val url = URL(
        "https://nominatim.openstreetmap.org/search" +
                "?format=json&addressdetails=1" +
                "&countrycodes=cl" +
                "&viewbox=-75.0,-17.5,-66.0,-56.0&bounded=1" +
                "&limit=$limit&q=" + URLEncoder.encode(query, "UTF-8")
    )
    val conn = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        setRequestProperty("User-Agent", ua)
        connectTimeout = 8000
        readTimeout = 8000
    }
    conn.inputStream.bufferedReader().use { br ->
        val txt = br.readText()
        val arr = JSONArray(txt)
        (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            val disp = o.optString("display_name")
            val lat = o.optString("lat").toDoubleOrNull() ?: return@mapNotNull null
            val lon = o.optString("lon").toDoubleOrNull() ?: return@mapNotNull null
            val addr = o.optJSONObject("address")
            val title = addr?.let { a ->
                listOf(
                    a.optString("road"),
                    a.optString("house_number"),
                    a.optString("suburb"),
                    a.optString("city"),
                    a.optString("town"),
                    a.optString("village")
                ).filter { it.isNotBlank() }.distinct().joinToString(", ")
            }.takeUnless { it.isNullOrBlank() } ?: disp
            OsmSuggestion(title, disp, lat, lon)
        }
    }
}

private suspend fun reverseGeocode(
    context: android.content.Context,
    lat: Double,
    lng: Double
): String? = withContext(Dispatchers.IO) {
    try {
        val geo = Geocoder(context, Locale.getDefault())
        geo.getFromLocation(lat, lng, 1)?.firstOrNull()?.getAddressLine(0)
    } catch (_: Exception) { null }
}

/* ======================= Pantalla principal ======================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmarUbicacionScreen(navController: NavController) {
    val context = LocalContext.current

    // --- Permisos ---
    var hasPermission by remember { mutableStateOf(false) }
    val reqPerms = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        hasPermission =
            (res[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (res[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
    }
    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fine || coarse) hasPermission = true
        else reqPerms.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    // --- OSMDroid config mínima ---
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        // setUseDataConnection(true) se setea sobre el MapView (no sobre Configuration)
    }

    // --- Estado principal ---
    val CHILE_BOUNDS = remember {
        // BoundingBox(north, east, south, west)
        BoundingBox(-17.5, -66.0, -56.0, -75.0)
    }
    var center by remember { mutableStateOf(GeoPoint(-33.4489, -70.6693)) } // Santiago fallback
    var address by remember { mutableStateOf<String?>(null) }

    // TextField editable: arranca con la dirección detectada, y el usuario lo puede editar
    var searchText by remember { mutableStateOf("") }
    var userEdited by remember { mutableStateOf(false) } // para no pisar el texto si el user ya tocó
    var suggestions by remember { mutableStateOf<List<OsmSuggestion>>(emptyList()) }
    var showSuggs by remember { mutableStateOf(false) }

    // --- Intentar ubicar al usuario (LocationManager) ---
    LaunchedEffect(hasPermission) {
        if (!hasPermission) return@LaunchedEffect
        val lm = context.getSystemService(LOCATION_SERVICE) as LocationManager

        fun bestOf(a: Location?, b: Location?): Location? {
            if (a == null) return b
            if (b == null) return a
            return if (a.time >= b.time) a else b
        }

        try {
            val lastGps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            bestOf(lastGps, lastNet)?.let { loc ->
                center = GeoPoint(loc.latitude, loc.longitude)
            }
            val listener = object : android.location.LocationListener {
                override fun onLocationChanged(loc: Location) {
                    center = GeoPoint(loc.latitude, loc.longitude)
                    lm.removeUpdates(this)
                }
                @Deprecated("Deprecated in Java") override fun onStatusChanged(p0: String?, p1: Int, p2: android.os.Bundle?) {}
                override fun onProviderEnabled(p0: String) {}
                override fun onProviderDisabled(p0: String) {}
            }
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener)
            else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, listener)
        } catch (_: SecurityException) { }
    }

    // --- Reverse geocoding al mover centro ---
    LaunchedEffect(center) {
        address = reverseGeocode(context, center.latitude, center.longitude)
        if (!userEdited) searchText = address ?: ""
    }

    // --- Debounce de búsqueda (autosuggest Chile) ---
    LaunchedEffect(searchText) {
        if (!userEdited && searchText.isBlank()) { suggestions = emptyList(); showSuggs = false; return@LaunchedEffect }
        if (searchText.length < 3) { suggestions = emptyList(); showSuggs = false; return@LaunchedEffect }
        delay(300)
        val res = searchChileOSM(context, searchText)
        suggestions = res
        showSuggs = res.isNotEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Confirmar ubicación de entrega",
                        color = Color(0xFF39FF14),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            // === Buscador editable (inicial con dirección detectada) ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        userEdited = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar dirección (Chile)…", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF39FF14),
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF39FF14)
                    )
                )

                DropdownMenu(
                    expanded = showSuggs,
                    onDismissRequest = { showSuggs = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1A1A))
                ) {
                    suggestions.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.title, color = Color.White) },
                            onClick = {
                                val p = GeoPoint(s.lat, s.lon)
                                center = p
                                address = s.display
                                searchText = s.display
                                userEdited = false
                                showSuggs = false
                            }
                        )
                    }
                }
            }

            // === Contenedor del mapa (fijo y recortado para que no “se salga” al hacer zoom) ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0F0F))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            // IMPORTANTE: setUseDataConnection va en el MapView
                            setUseDataConnection(true)
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            isTilesScaledToDpi = true

                            // Bounds Chile + zoom limits
                            setScrollableAreaLimitDouble(CHILE_BOUNDS)
                            minZoomLevel = 4.0
                            maxZoomLevel = 19.5

                            // Cámara inicial
                            controller.setZoom(16.0)
                            controller.setCenter(center)

                            // Marcador arrastrable
                            val marker = Marker(this).apply {
                                position = center
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                isDraggable = true
                                setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                                    override fun onMarkerDragStart(m: Marker?) {}
                                    override fun onMarkerDrag(m: Marker?) {}
                                    override fun onMarkerDragEnd(m: Marker?) {
                                        m?.position?.let { p -> center = GeoPoint(p.latitude, p.longitude) }
                                    }
                                })
                            }
                            overlays.add(marker)

                            // Tap para mover pin
                            overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                    p?.let { center = it }
                                    return true
                                }
                                override fun longPressHelper(p: GeoPoint?): Boolean = false
                            }))
                        }
                    },
                    update = { map ->
                        map.controller.setCenter(center)
                        map.overlays.filterIsInstance<Marker>().firstOrNull()?.position = center
                        map.invalidate()
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = address ?: "Buscando dirección…",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(1f)
                ) { Text("Cancelar", color = Color.White) }

                Button(
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("delivery_lat", center.latitude)
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("delivery_lng", center.longitude)
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("delivery_address", address ?: (searchText.ifBlank { "Ubicación sin dirección" }))
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14)),
                    modifier = Modifier.weight(1f)
                ) { Text("Usar esta ubicación", color = Color.Black) }
            }
        }
    }
}
