package com.example.apptest

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.apptest.data.AppDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import java.util.UUID
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class Pantalla_registromapa : AppCompatActivity() {


    private var csvCargado = false

    private val abrirCsvLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }

            cargarPoligonosDesdeCsv(uri)
        } else {
            showMessage("No se seleccionó ningún archivo.")
        }
    }

    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var webView: WebView
    private lateinit var db: AppDatabase

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var currentLocation: LatLng? = null
    private var cultivoActual: String = ""
    private var mapaCargado = false

    private val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 3000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
                actualizarUbicacionEnPantalla()
                actualizarMapa()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_registromapa)

        db = AppDatabase.getDatabase(applicationContext)

        tvLatitude = findViewById(R.id.tv_latitud)
        tvLongitude = findViewById(R.id.tv_longitud)
        webView = findViewById(R.id.webViewMapa)
        val bCargarCsv: Button = findViewById(R.id.bCargarCsv)

        bCargarCsv.setOnClickListener {
            abrirCsvLauncher.launch(arrayOf("text/*", "text/csv", "application/csv"))
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        cultivoActual = intent.getStringExtra("cultivo") ?: ""

        configurarWebView()
        cargarMapaHtml()


        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            obtenerUbicacionActual()
            startLocationUpdates()
        }

        val idActual = intent.getIntExtra("ind_value", 1)

        val actividades = mapOf(
            "Brocoli" to pantalla_implementoplagas::class.java,
            "Lechuga" to pantalla_plagaslechuga::class.java,
            "Berries" to pantalla_plagasberries::class.java,
            "Maiz" to pantalla_plagasmaiz::class.java,
            "Agave" to pantalla_plagasagave::class.java,
            "Trigo" to pantalla_plagastrigo::class.java,
            "Sorgo" to pantalla_plagassorgo::class.java
        )

        val bOtroregistro: Button = findViewById(R.id.bOtroRegistro)
        bOtroregistro.setOnClickListener {
            val actividad = actividades[cultivoActual]

            if (actividad == null) {
                showMessage("No se encontró la pantalla del cultivo: $cultivoActual")
                return@setOnClickListener
            }

            val updatedID = idActual + 1
            val intent = Intent(this, actividad)
            intent.putExtra("ind_value", updatedID)
            intent.putExtra("cultivo", cultivoActual)

            IDcount = updatedID
            restartcounts()
            guardarPosicionActual()
            startActivity(intent)
            finish()
        }

        val bTerminarregistro: Button = findViewById(R.id.bTerminarRegistro)
        bTerminarregistro.setOnClickListener {
            lifecycleScope.launch {
                try {
                    cargarDataSaveDesdeRoom()

                    if (dataSave.isEmpty() || todosSonSinPlaga()) {
                        terminarSinPlagas()
                    } else {
                        showumbral()
                    }
                } catch (e: Exception) {
                    showMessage("Error al leer registros locales: ${e.message}")
                }
            }
        }
    }
    private fun cargarMapaHtml() {
        val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                html, body, #map {
                    height: 100%;
                    margin: 0;
                    padding: 0;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>

            <script>
                const map = L.map('map').setView([20.0, -101.0], 16);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                let marker = null;

                function actualizarPunto(lat, lon) {
                    const nuevaPosicion = [lat, lon];

                    if (marker === null) {
                        marker = L.marker(nuevaPosicion).addTo(map);
                    } else {
                        marker.setLatLng(nuevaPosicion);
                    }

                    marker.bindPopup('<b>Mi ubicación</b><br>' + lat + ', ' + lon);
                    map.setView(nuevaPosicion, 18);
                }

                function limpiarMapa() {
                    if (marker !== null) {
                        map.removeLayer(marker);
                        marker = null;
                    }
                }
            </script>
        </body>
        </html>
    """.trimIndent()

        webView.loadDataWithBaseURL(
            "https://localhost/",
            html,
            "text/html",
            "UTF-8",
            null
        )
    }
    private fun construirHtmlMapaConPoligonos(vertices: List<VerticeLote>): String {
        val poligonosJson = JSONArray()

        val agrupados = vertices
            .groupBy { it.poligono }
            .mapValues { (_, lista) -> lista.sortedBy { it.idVertice } }

        agrupados.forEach { (nombrePoligono, listaVertices) ->
            val obj = JSONObject()
            obj.put("nombre", nombrePoligono)

            val coords = JSONArray()
            listaVertices.forEach { v ->
                val par = JSONArray()
                par.put(v.lat)
                par.put(v.lon)
                coords.put(par)
            }

            obj.put("coords", coords)
            poligonosJson.put(obj)
        }

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                html, body, #map {
                    height: 100%;
                    margin: 0;
                    padding: 0;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>

            <script>
                const poligonos = $poligonosJson;

                const map = L.map('map');
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                const bounds = [];
                let marker = null;

                poligonos.forEach(p => {
                    const polygon = L.polygon(p.coords, {
                        color: '#2E7D32',
                        weight: 3,
                        fillColor: '#66BB6A',
                        fillOpacity: 0.35
                    }).addTo(map);

                    polygon.bindPopup('<b>Lote:</b> ' + p.nombre);

                    p.coords.forEach(c => bounds.push(c));
                });

                if (bounds.length > 0) {
                    map.fitBounds(bounds, { padding: [20, 20] });
                } else {
                    map.setView([20.0, -101.0], 16);
                }

                function actualizarPunto(lat, lon) {
                    const nuevaPosicion = [lat, lon];

                    if (marker === null) {
                        marker = L.marker(nuevaPosicion).addTo(map);
                        marker.bindPopup('<b>Mi ubicación</b><br>' + lat + ', ' + lon);
                    } else {
                        marker.setLatLng(nuevaPosicion);
                        marker.setPopupContent('<b>Mi ubicación</b><br>' + lat + ', ' + lon);
                    }
                }

                function limpiarMapa() {
                    if (marker !== null) {
                        map.removeLayer(marker);
                        marker = null;
                    }
                }
            </script>
        </body>
        </html>
    """.trimIndent()
    }
    private fun cargarPoligonosDesdeCsv(uri: Uri) {
        try {
            val csvTexto = contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            }

            if (csvTexto.isNullOrBlank()) {
                showMessage("El archivo CSV está vacío.")
                return
            }

            val vertices = leerVerticesDesdeCsv(csvTexto)

            if (vertices.isEmpty()) {
                showMessage("No se encontraron coordenadas válidas en el CSV.")
                return
            }

            csvCargado = true

            webView.loadDataWithBaseURL(
                "https://localhost/",
                construirHtmlMapaConPoligonos(vertices),
                "text/html",
                "UTF-8",
                null
            )

            showMessage("CSV cargado correctamente.")
        } catch (e: Exception) {
            showMessage("Error al cargar CSV: ${e.message}")
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun configurarWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mapaCargado = true
                actualizarMapa()
            }
        }
    }
    data class VerticeLote(
        val archivo: String,
        val poligono: String,
        val idVertice: Int,
        val lat: Double,
        val lon: Double
    )
    private fun leerVerticesDesdeCsv(csv: String): List<VerticeLote> {
        return csv
            .replace("\uFEFF", "")
            .lineSequence()
            .drop(1)
            .filter { it.isNotBlank() }
            .mapNotNull { linea ->
                val partes = linea.split(",").map { it.trim().removeSurrounding("\"") }

                if (partes.size >= 5) {
                    val id = partes[2].toIntOrNull()
                    val lat = partes[3].toDoubleOrNull()
                    val lon = partes[4].toDoubleOrNull()

                    if (id != null && lat != null && lon != null) {
                        VerticeLote(
                            archivo = partes[0],
                            poligono = partes[1],
                            idVertice = id,
                            lat = lat,
                            lon = lon
                        )
                    } else null
                } else null
            }
            .toList()
    }
    private fun agruparPoligonos(vertices: List<VerticeLote>): Map<String, List<VerticeLote>> {
        return vertices
            .groupBy { it.poligono }
            .mapValues { (_, lista) -> lista.sortedBy { it.idVertice } }
    }


    private fun todosSonSinPlaga(): Boolean {
        if (dataSave.isEmpty()) return true

        return dataSave.all { fila ->
            fila.size > 8 && fila[8].trim().equals("Sin Plaga", ignoreCase = true)
        }
    }

    private fun terminarSinPlagas() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Monitoreo terminado")
        builder.setMessage("No se registraron plagas en este monitoreo. Se regresará al inicio.")

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            try {
                stopLocationUpdates()
                cerrarSesionActual()
                lastreset()
                restartcounts()
                IDcount = 0

                val intent = Intent(this@Pantalla_registromapa, MainActivity::class.java)
                startActivity(intent)
                finish()

                dialog.dismiss()
            } catch (e: Exception) {
                showMessage("Ocurrió un error al cerrar el monitoreo.")
            }
        }

        builder.setCancelable(false)
        builder.show()
    }

    private fun obtenerSesionActual(): String {
        val prefs = getSharedPreferences("registro_local", Context.MODE_PRIVATE)
        return prefs.getString("sesion_id_actual", "") ?: ""
    }

    private fun cerrarSesionActual() {
        val prefs = getSharedPreferences("registro_local", Context.MODE_PRIVATE)
        prefs.edit().remove("sesion_id_actual").apply()
    }

    private suspend fun cargarDataSaveDesdeRoom() {
        val sesionId = obtenerSesionActual()

        if (sesionId.isBlank()) {
            dataSave.clear()
            return
        }

        val registros = db.monitoreoDao().obtenerPorSesion(sesionId)

        dataSave.clear()

        registros.forEach { r ->
            dataSave.add(
                mutableListOf(
                    r.punto,
                    r.usuario,
                    r.agricola,
                    r.granja,
                    r.lote,
                    r.cultivo,
                    r.latitud,
                    r.longitud,
                    r.tipo,
                    r.nombre,
                    r.fase,
                    r.cantidad,
                    r.fecha,
                    r.hora
                )
            )
        }

        if (cultivoActual.isBlank() && registros.isNotEmpty()) {
            cultivoActual = registros.first().cultivo
        }
    }

    private fun obtenerUbicacionActual() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
                actualizarUbicacionEnPantalla()
                actualizarMapa()
            } else {
                obtenerUltimaUbicacion()
            }
        }.addOnFailureListener {
            obtenerUltimaUbicacion()
        }
    }

    private fun obtenerUltimaUbicacion() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
                actualizarUbicacionEnPantalla()
                actualizarMapa()
            } else {
                showMessage("No se pudo obtener la ubicación.")
            }
        }
    }

    private fun actualizarUbicacionEnPantalla() {
        currentLocation?.let { ubicacion ->
            tvLatitude.text = ubicacion.latitude.toString()
            tvLongitude.text = ubicacion.longitude.toString()
        }
    }

    private fun actualizarMapa() {
        val ubicacion = currentLocation ?: return
        if (!mapaCargado) return

        val script = """
        actualizarPunto(${ubicacion.latitude}, ${ubicacion.longitude});
    """.trimIndent()

        webView.evaluateJavascript(script, null)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                    obtenerUbicacionActual()
                    startLocationUpdates()
                } else {
                    showMessage("Permiso de ubicación denegado.")
                }
            }
        }
    }

    fun showMessage(mostrarMsj: String) {
        Toast.makeText(applicationContext, mostrarMsj, Toast.LENGTH_SHORT).show()
    }

    private fun startLocationUpdates() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun guardarPosicionActual() {
        currentLocation?.let { location ->
            val sharedPrefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
            with(sharedPrefs.edit()) {
                putString("latitud", location.latitude.toString())
                putString("longitud", location.longitude.toString())
                apply()
            }
        } ?: run {
            Toast.makeText(this, "No hay ubicación para guardar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun borrarUbicaciones() {
        if (mapaCargado) {
            webView.evaluateJavascript("limpiarMapa();", null)
        }
    }

    fun showumbral() {
        if (dataSave.isEmpty()) {
            showMessage("No hay registros guardados en Room para esta sesión.")
            return
        }

        val primerRegistro = dataSave.first()
        val agricultorReporte = primerRegistro.getOrNull(2) ?: ""
        val granjaReporte = primerRegistro.getOrNull(3) ?: ""
        val loteReporte = primerRegistro.getOrNull(4) ?: ""
        val cultivoReporte = primerRegistro.getOrNull(5) ?: cultivoActual

        val message = mutableListOf<String>()
        val umbralStrings = mutableListOf<String>()

        IDstrings.clear()

        for (line in dataSave) {
            val punto = line.getOrNull(0)
            if (!punto.isNullOrBlank() && !punto.equals("Punto", ignoreCase = true)) {
                IDstrings.add(punto)
            }
        }

        IDints = IDstrings.mapNotNull { it.toIntOrNull() }.toMutableList()

        lecturefivefases("Dorso de Diamante", DDUmbral, DDTotal, DDH, DDC, DDM, DDG, DDP)
        DDumbralValue = calculoumbral(DDUmbral)

        lecturefivefases("Falso Medidor", FMUmbral, FMTotal, FMH, FMC, FMM, FMG, FMP)
        FMumbralValue = calculoumbral(FMUmbral)

        lecturefivefases("Gusano Soldado", GSUmbral, GSTotal, GSH, GSC, GSM, GSG, GSP)
        GSumbralValue = calculoumbral(GSUmbral)

        lecturefivefases("Gusano Pieris", GPUmbral, GPTotal, GPH, GPC, GPM, GPG, GPP)
        GPumbralValue = calculoumbral(GPUmbral)

        lecturefivefases("Diabrotica", DUmbral, DTotal, DH, DC, DM, DG, DP)
        DumbralValue = calculoumbral(DUmbral)

        lecturefivefases("Gusano de la Fruta", GDFUmbral, GDFTotal, GDFH, GDFC, GDFM, GDFG, GDFP)
        GDFumbralValue = calculoumbral(GDFUmbral)

        lecturefivefases("Gusano de la Col", GDCUmbral, GDCTotal, GDCH, GDCC, GDCM, GDCG, GDCP)
        GDCumbralValue = calculoumbral(GDCUmbral)

        lecturefivefases("Helicoverpa Zea", HZUmbral, HZTotal, HZH, HZC, HZM, HZG, HZP)
        HZumbralValue = calculoumbral(HZUmbral)

        lecturethreefases("Chinche Lygus", CLUmbral, CLC, CLM, CLG)
        CLumbralValue = calculoumbral(CLUmbral)

        lecturethreefases("Copitarsia", CUmbral, CC, CM, CG)
        CumbralValue = calculoumbral(CUmbral)

        lecturethreefases("Chinche Nysius", CNUmbral, CNC, CNM, CNG)
        CNumbralValue = calculoumbral(CNUmbral)

        lecturethreefasesninfa("Chicharrita", CHIUmbral, CHIH, CHIN, CHIA)
        CHIumbralValue = calculoumbral(CHIUmbral)

        lecturethreefasesninfa("Trips", TUmbral, TH, TN, TA)
        TumbralValue = calculoumbral(TUmbral)

        lecturethreefasesninfa("Mosquita Blanca", MBUmbral, MBH, MBN, MBA)
        MBumbralValue = calculoumbral(MBUmbral)

        lecturethreefaseslarva("Coccinallidae (Mariquita)", COCUmbral, COCH, COCL, COCA)
        COCumbralValue = calculoumbral(COCUmbral)

        lecturetwofases("Pulgon Verde", PVUmbral, PVSA, PVCA)
        PVumbralValue = calculoumbral(PVUmbral)

        lectureonefase("Pulgon Gris", PGUmbral)
        PGumbralValue = calculoumbral(PGUmbral)

        lecturethreefasesninfa("Araña Roja", ARUmbral, ARH, ARN, ARA)
        ARumbralValue = calculoumbral(ARUmbral)

        lecturethreefasesninfa("Mosca de Vinagre", MVUmbral, MVH, MVN, MVA)
        MVumbralValue = calculoumbral(MVUmbral)

        lecturethreefasesninfa("Gallina Ciega", GCUmbral, GCH, GCN, GCA)
        GCumbralValue = calculoumbral(GCUmbral)

        lecturethreefasesninfa("Gusano Alambre", GAUmbral, GAH, GAN, GAA)
        GAUmbralValue = calculoumbral(GAUmbral)

        lecturethreefasesninfa("Gusano Cogollero", GCOGUmbral, GCOGH, GCOGN, GCOGA)
        GCOGUmbralValue = calculoumbral(GCOGUmbral)

        lecturethreefasesninfa("Picudo del Agave", PAUmbral, PAH, PAN, PAA)
        PAUmbralValue = calculoumbral(PAUmbral)

        lecturethreefasesninfa("Picudo del Maiz", PMUmbral, PMH, PMN, PMA)
        PMUmbralValue = calculoumbral(PMUmbral)

        lecturethreefaseslarva("Mosquita Panoja", MPUmbral, MPH, MPN, MPA)
        lectureonefase("Pulgon Amarillo", PULAUmbral)

        message.add("\t \t Reporte de Umbrales")
        message.add("\t \t Agricultor: $agricultorReporte")
        message.add("\t \t Granja: $granjaReporte")
        message.add("\t \t Lote: $loteReporte")
        message.add("\t \t Cultivo: $cultivoReporte")

        val currentDate: String = getDateTime()
        message.add("\t \t Fecha: $currentDate \n")

        val umbralVariables = mapOf(
            "\t \t \t Umbral Dorso de Diamante" to DDumbralValue,
            "\t \t \t Umbral Falso Medidor" to FMumbralValue,
            "\t \t \t Umbral Gusano Soldado" to GSumbralValue,
            "\t \t \t Umbral Gusano Pieris" to GPumbralValue,
            "\t \t \t Umbral Diabrotica" to DumbralValue,
            "\t \t \t Umbral Gusano de la Col" to GDCumbralValue,
            "\t \t \t Umbral Gusano de la Fruta" to GDFumbralValue,
            "\t \t \t Umbral Helicoverpa Zea" to HZumbralValue,
            "\t \t \t Umbral Chinche Lygus" to CLumbralValue,
            "\t \t \t Umbral Copitarsia" to CumbralValue,
            "\t \t \t Umbral Chinche Nysius" to CNumbralValue,
            "\t \t \t Umbral Chicharrita" to CHIumbralValue,
            "\t \t \t Umbral Trips" to TumbralValue,
            "\t \t \t Umbral Mosquita Blanca" to MBumbralValue,
            "\t \t \t Umbral Coccinallidade (Mariquita)" to COCumbralValue,
            "\t \t \t Umbral Pulgon Verde" to PVumbralValue,
            "\t \t \t Umbral Pulgon Gris" to PGumbralValue,
            "\t \t \t Umbral Arana Roja" to ARumbralValue,
            "\t \t \t Umbral Mosca de Vinagre" to MVumbralValue,
            "\t \t \t Umbral Gallina Ciega" to GCumbralValue,
            "\t \t \t Umbral Gusano Alambre" to GAUmbralValue,
            "\t \t \t Umbral Gusano Cogollero" to GCOGUmbralValue,
            "\t \t \t Umbral Picudo del Agave" to PAUmbralValue,
            "\t \t \t Umbral Picudo del Maiz" to PMUmbralValue,
            "\t \t \t Umbral Mosquita Panoja" to MPUmbralValue,
            "\t \t \t Umbral Pulgon Amarillo" to PULAumbralValue
        )

        umbralStrings.add(
            umbralVariables.filterValues { it != 0f }
                .map { (umbral, value) -> "\t $umbral: $value \n" }
                .joinToString("\n")
        )

        umbralStrings.forEach { sublist ->
            message.add(sublist)
        }

        val baseName = "Monitoreo_${System.currentTimeMillis()}"
        val uniqueArchiveName = "${UUID.randomUUID()}_$baseName"
        val uniqueArchiveUmbral = "${UUID.randomUUID()}Umbral$baseName"

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Termino de Monitoreo \n  Umbrales Obtenidos en el registro:")

        val mensaje = message.joinToString("\n")
        builder.setMessage(mensaje)
        builder.setCancelable(false)

        builder.setPositiveButton("Continuar") { dialog, _ ->
            try {
                val intent = Intent(this, MainActivity::class.java)

                IDcount = 1
                restartcounts()

                dataSave.forEach { sublist ->
                    val line = sublist.joinToString(",")
                    message.add(line)
                }

                saveToDownloads(this, dataSave, uniqueArchiveName)
                saveToDownloadsUmbralAsPdfWithImage(this, message, uniqueArchiveUmbral)

                stopLocationUpdates()
                lastreset()
                cerrarSesionActual()

                dialog.dismiss()
                showMessage("Los archivos se han guardado con éxito.")
                borrarUbicaciones()
                finish()
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                showMessage("Ocurrió una excepción al guardar.")
            }
        }

        builder.show()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onStop() {
        stopLocationUpdates()
        super.onStop()
    }

    override fun onDestroy() {
        stopLocationUpdates()
        webView.destroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "No se puede volver en esta Pantalla.", Toast.LENGTH_SHORT).show()
    }
}