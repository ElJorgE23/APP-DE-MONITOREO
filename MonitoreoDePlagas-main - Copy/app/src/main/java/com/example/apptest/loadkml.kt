package com.example.apptest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class loadkml : AppCompatActivity() {

    private val SELECCIONAR_ARCHIVO_REQUEST_CODE = 1

    private lateinit var bLoadKML: Button
    private lateinit var ibCerrarKML: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loadkml)

        bLoadKML = findViewById(R.id.b_cargaKML)
        ibCerrarKML = findViewById(R.id.close_kml)

        bLoadKML.setOnClickListener {
            showMessage("Elige el archivo KML")
            seleccionarArchivoKML()
        }

        ibCerrarKML.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun seleccionarArchivoKML() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/vnd.google-earth.kml+xml"
        }
        startActivityForResult(intent, SELECCIONAR_ARCHIVO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECCIONAR_ARCHIVO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val archivoUri = data?.data
            archivoUri?.let { procesarArchivoKML(it) }
        }
    }

    private fun procesarArchivoKML(fileUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(fileUri)

            if (inputStream != null) {
                val coordenadas = extraerCoordenadasDesdeKML(inputStream).toMutableList()
                coord_from_kml = coordenadas
                showMessage("KML cargado correctamente. Puntos: ${coordenadas.size}")
            } else {
                showMessage("No se pudo leer el archivo KML")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Error al procesar KML: ${e.message}")
        }
    }

    private fun extraerCoordenadasDesdeKML(inputStream: InputStream): List<LatLng> {
        val coordenadas = mutableListOf<LatLng>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var lat: Double? = null
            var lng: Double? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name.equals("coordinates", ignoreCase = true)) {
                            val coordinates = parser.nextText().split(",")
                            if (coordinates.size >= 2) {
                                lng = coordinates[0].trim().toDoubleOrNull()
                                lat = coordinates[1].trim().toDoubleOrNull()
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name.equals("Placemark", ignoreCase = true) && lat != null && lng != null) {
                            coordenadas.add(LatLng(lat, lng))
                            lat = null
                            lng = null
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return coordenadas
    }
}