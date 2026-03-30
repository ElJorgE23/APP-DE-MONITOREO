package com.example.apptest

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class PantallaMapaMonitoreo : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var btnBack: ImageButton
    private lateinit var tvTitulo: TextView
    private lateinit var tvAgricultor: TextView
    private lateinit var tvGranja: TextView
    private lateinit var tvLote: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvCultivo: TextView
    private lateinit var recyclerTabla: RecyclerView
    private lateinit var btnDescargarCsv: Button

    data class RegistroDetalle(
        val punto: String,
        val latitudTexto: String,
        val longitudTexto: String,
        val latitud: Double?,
        val longitud: Double?,
        val afectacion: String,
        val fase: String,
        val cantidad: String,
        val fecha: String,
        val hora: String
    )

    data class PuntoMapaAgrupado(
        val punto: String,
        val latitud: Double,
        val longitud: Double,
        val detalles: List<RegistroDetalle>
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_mapa_monitoreo)

        webView = findViewById(R.id.webViewMapa)
        btnBack = findViewById(R.id.btnBackMapa)
        tvTitulo = findViewById(R.id.tvTituloMapa)
        tvAgricultor = findViewById(R.id.tvAgricultorDetalle)
        tvGranja = findViewById(R.id.tvGranjaDetalle)
        tvLote = findViewById(R.id.tvLoteDetalle)
        tvFecha = findViewById(R.id.tvFechaDetalle)
        tvCultivo = findViewById(R.id.tvCultivoDetalle)
        recyclerTabla = findViewById(R.id.recyclerTablaPuntos)
        btnDescargarCsv = findViewById(R.id.btnDescargarCsv)

        tvTitulo.text = intent.getStringExtra("titulo") ?: "Mapa del monitoreo"
        tvAgricultor.text = "Agricultor: ${intent.getStringExtra("agricultor") ?: "-"}"
        tvGranja.text = "Granja: ${intent.getStringExtra("granja") ?: "-"}"
        tvLote.text = "Lote: ${intent.getStringExtra("lote") ?: "-"}"
        tvFecha.text = "Fecha: ${intent.getStringExtra("fecha") ?: "-"}"
        tvCultivo.text = "Cultivo: ${intent.getStringExtra("cultivo") ?: "-"}"

        btnBack.setOnClickListener { finish() }

        val rawRegistros = intent.getStringArrayListExtra("registros_detalle") ?: arrayListOf()

        val registros = rawRegistros.map { raw ->
            val partes = raw.split("|")

            val latTxt = partes.getOrNull(1)?.ifBlank { "-" } ?: "-"
            val lonTxt = partes.getOrNull(2)?.ifBlank { "-" } ?: "-"

            RegistroDetalle(
                punto = partes.getOrNull(0) ?: "Punto",
                latitudTexto = latTxt,
                longitudTexto = lonTxt,
                latitud = partes.getOrNull(1)?.toDoubleOrNull(),
                longitud = partes.getOrNull(2)?.toDoubleOrNull(),
                afectacion = partes.getOrNull(3) ?: "Sin dato",
                fase = partes.getOrNull(4) ?: "Sin dato",
                cantidad = partes.getOrNull(5) ?: "Sin dato",
                fecha = partes.getOrNull(6) ?: "Sin dato",
                hora = partes.getOrNull(7) ?: "Sin dato"
            )
        }.sortedWith(
            compareBy<RegistroDetalle> { extraerNumeroPunto(it.punto) }
                .thenBy { it.fecha }
                .thenBy { it.hora }
        )
        btnDescargarCsv.setOnClickListener {
            descargarCsv(registros)
        }
        recyclerTabla.layoutManager = LinearLayoutManager(this)
        recyclerTabla.adapter = TablaPuntosAdapter(registros)
        recyclerTabla.isNestedScrollingEnabled = false

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()

        val puntosAgrupados = registros
            .filter { it.latitud != null && it.longitud != null }
            .groupBy { "${it.punto}|${it.latitud}|${it.longitud}" }
            .map { (_, lista) ->
                val primero = lista.first()
                PuntoMapaAgrupado(
                    punto = primero.punto,
                    latitud = primero.latitud!!,
                    longitud = primero.longitud!!,
                    detalles = lista
                )
            }
            .sortedBy { extraerNumeroPunto(it.punto) }

        if (puntosAgrupados.isEmpty()) {
            Toast.makeText(
                this,
                "Este monitoreo no tiene coordenadas válidas para mostrar el mapa.",
                Toast.LENGTH_SHORT
            ).show()

            webView.loadDataWithBaseURL(
                "https://localhost/",
                construirHtmlSinMapa(),
                "text/html",
                "UTF-8",
                null
            )
        } else {
            webView.loadDataWithBaseURL(
                "https://localhost/",
                construirHtmlMapa(puntosAgrupados),
                "text/html",
                "UTF-8",
                null
            )
        }
    }

    private fun extraerNumeroPunto(punto: String): Int {
        return Regex("\\d+").find(punto)?.value?.toIntOrNull() ?: Int.MAX_VALUE
    }
    private fun descargarCsv(registros: List<RegistroDetalle>) {
        if (registros.isEmpty()) {
            Toast.makeText(this, "No hay datos para exportar.", Toast.LENGTH_SHORT).show()
            return
        }

        val agricultor = intent.getStringExtra("agricultor") ?: "SinAgricultor"
        val lote = intent.getStringExtra("lote") ?: "SinLote"

        val nombreArchivo = "Monitoreo_${limpiarTextoArchivo(agricultor)}_${limpiarTextoArchivo(lote)}_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        }.csv"

        val contenido = construirContenidoCsv(registros)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Monitoreos")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val resolver = contentResolver
                val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val uri = resolver.insert(collection, values)

                if (uri == null) {
                    Toast.makeText(this, "No se pudo crear el archivo.", Toast.LENGTH_LONG).show()
                    return
                }

                resolver.openOutputStream(uri)?.use { output ->
                    output.write("\uFEFF".toByteArray(Charsets.UTF_8)) // BOM para Excel
                    output.write(contenido.toByteArray(Charsets.UTF_8))
                    output.flush()
                } ?: run {
                    Toast.makeText(this, "No se pudo abrir el archivo.", Toast.LENGTH_LONG).show()
                    return
                }

                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)

                Toast.makeText(
                    this,
                    "CSV guardado en Descargas/Monitoreos: $nombreArchivo",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                val carpeta = java.io.File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "Monitoreos"
                )

                if (!carpeta.exists()) {
                    carpeta.mkdirs()
                }

                val archivo = java.io.File(carpeta, nombreArchivo)
                archivo.outputStream().use { output ->
                    output.write("\uFEFF".toByteArray(Charsets.UTF_8))
                    output.write(contenido.toByteArray(Charsets.UTF_8))
                    output.flush()
                }

                Toast.makeText(
                    this,
                    "CSV guardado en: ${archivo.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun construirContenidoCsv(registros: List<RegistroDetalle>): String {
        val sb = StringBuilder()

        sb.append("ID,Punto,Latitud,Longitud,Plaga_Enfermedad,Fase,Cantidad,Fecha,Hora\n")

        registros.forEachIndexed { index, item ->
            sb.append(index + 1).append(",")
            sb.append(escaparCsv(item.punto)).append(",")
            sb.append(escaparCsv(item.latitudTexto)).append(",")
            sb.append(escaparCsv(item.longitudTexto)).append(",")
            sb.append(escaparCsv(item.afectacion)).append(",")
            sb.append(escaparCsv(item.fase)).append(",")
            sb.append(escaparCsv(item.cantidad)).append(",")
            sb.append(escaparCsv(item.fecha)).append(",")
            sb.append(escaparCsv(item.hora)).append("\n")
        }

        return sb.toString()
    }

    private fun escaparCsv(valor: String): String {
        val limpio = valor.replace("\"", "\"\"")
        return "\"$limpio\""
    }

    private fun limpiarTextoArchivo(texto: String): String {
        return texto
            .replace("[^a-zA-Z0-9-_]".toRegex(), "_")
            .replace("_+".toRegex(), "_")
            .trim('_')
    }

    private fun crearArchivoEnDownloads(nombreArchivo: String): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            )

            uri?.let { contentResolver.openOutputStream(it) }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, nombreArchivo)
            file.outputStream()
        }
    }

    private fun construirHtmlSinMapa(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    html, body {
                        height: 100%;
                        margin: 0;
                        padding: 0;
                        font-family: Arial, sans-serif;
                        background: #F5F5F5;
                    }
                    .contenedor {
                        height: 100%;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        text-align: center;
                        color: #555;
                        padding: 16px;
                    }
                </style>
            </head>
            <body>
                <div class="contenedor">
                    No hay coordenadas válidas para mostrar el mapa de este monitoreo.
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun construirHtmlMapa(puntos: List<PuntoMapaAgrupado>): String {
        val jsonArray = JSONArray()

        puntos.forEach { punto ->
            val obj = JSONObject()
            obj.put("punto", punto.punto)
            obj.put("lat", punto.latitud)
            obj.put("lon", punto.longitud)

            val detallesArray = JSONArray()
            punto.detalles.forEach { detalle ->
                val d = JSONObject()
                d.put("afectacion", detalle.afectacion)
                d.put("fase", detalle.fase)
                d.put("cantidad", detalle.cantidad)
                d.put("fecha", detalle.fecha)
                d.put("hora", detalle.hora)
                detallesArray.put(d)
            }

            obj.put("detalles", detallesArray)
            jsonArray.put(obj)
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
                const points = $jsonArray;

        const map = L.map('map');
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '© OpenStreetMap'
        }).addTo(map);

        function escapeHtml(value) {
            return String(value ?? '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
        }

        const latlngs = [];

        points.forEach(p => {
            const marker = L.marker([p.lat, p.lon]).addTo(map);

            const detallesHtml = (p.detalles || []).map(d => `
            <div style="margin-top:8px;padding-top:8px;border-top:1px solid #E0E0E0;">
            <b>Plaga/Enfermedad:</b> ${'$'}{escapeHtml(d.afectacion)}<br>
            <b>Fase:</b> ${'$'}{escapeHtml(d.fase)}<br>
            <b>Cantidad:</b> ${'$'}{escapeHtml(d.cantidad)}<br>
            <b>Fecha:</b> ${'$'}{escapeHtml(d.fecha)}<br>
            <b>Hora:</b> ${'$'}{escapeHtml(d.hora)}
            </div>
            `).join('');

            marker.bindPopup(`
            <div style="font-size:13px; line-height:1.45; min-width:220px;">
            <b>${'$'}{escapeHtml(p.punto)}</b><br>
            <b>Latitud:</b> ${'$'}{p.lat}<br>
            <b>Longitud:</b> ${'$'}{p.lon}
            ${'$'}{detallesHtml}
            </div>
            `);

            latlngs.push([p.lat, p.lon]);
        });

        if (latlngs.length === 1) {
            map.setView(latlngs[0], 18);
        } else {
            map.fitBounds(latlngs, { padding: [30, 30] });
            L.polyline(latlngs, { color: '#2E7D32', weight: 4 }).addTo(map);
        }
        </script>
        </body>
        </html>
        """.trimIndent()
    }

    class TablaPuntosAdapter(
        private val items: List<RegistroDetalle>
    ) : RecyclerView.Adapter<TablaPuntosAdapter.TablaViewHolder>() {

        inner class TablaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvIdTabla: TextView = itemView.findViewById(R.id.tvIdTabla)
            private val tvPuntoTabla: TextView = itemView.findViewById(R.id.tvPuntoTabla)
            private val tvCoordenadasTabla: TextView = itemView.findViewById(R.id.tvCoordenadasTabla)
            private val tvAfectacionTabla: TextView = itemView.findViewById(R.id.tvAfectacionTabla)
            private val tvFaseTabla: TextView = itemView.findViewById(R.id.tvFaseTabla)
            private val tvCantidadTabla: TextView = itemView.findViewById(R.id.tvCantidadTabla)
            private val tvHoraTabla: TextView = itemView.findViewById(R.id.tvHoraTabla)

            fun bind(item: RegistroDetalle, position: Int) {
                tvIdTabla.text = (position + 1).toString()
                tvPuntoTabla.text = item.punto
                tvCoordenadasTabla.text = "${item.latitudTexto}, ${item.longitudTexto}"
                tvAfectacionTabla.text = item.afectacion
                tvFaseTabla.text = item.fase
                tvCantidadTabla.text = item.cantidad
                tvHoraTabla.text = item.hora

                itemView.setBackgroundColor(
                    if (position % 2 == 0) Color.parseColor("#F9FCF9")
                    else Color.WHITE
                )
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TablaViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fila_monitoreo, parent, false)
            return TablaViewHolder(view)
        }

        override fun onBindViewHolder(holder: TablaViewHolder, position: Int) {
            holder.bind(items[position], position)
        }

        override fun getItemCount(): Int = items.size
    }
}