package com.example.apptest

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.data.AppDatabase
import com.example.apptest.data.MonitoreoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class pantalla_monitoreos : AppCompatActivity() {

    private lateinit var backMonitoreo: ImageButton
    private lateinit var recyclerMonitoreos: RecyclerView
    private lateinit var tvSinDatos: TextView
    private lateinit var monitoreoAdapter: MonitoreoAdapter// Adaptador que conecta los datos con el RecyclerView
    private lateinit var db: AppDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtiene la instancia de la base de datos usando el contexto de la app
        db = AppDatabase.getDatabase(applicationContext)

        sessionManager = SessionManager(this)

        // Layout raíz de toda la pantalla
        // Aquí estás creando la interfaz por código, no con XML
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Encabezado superior de la pantalla
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(ContextCompat.getColor(this@pantalla_monitoreos, R.color.greenti))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Botón de regreso
        backMonitoreo = ImageButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
            setImageResource(R.drawable.ic_back)
            setBackgroundColor(Color.TRANSPARENT)

            // Aplica el color del tema al fondo del boton
            backgroundTintList =
                ContextCompat.getColorStateList(this@pantalla_monitoreos, R.color.greenti)
        }

        // Título que aparece en el header
        val titulo = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            text = "Monitoreos"
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        // Texto que se muestra si el usuario no tiene monitoreos guardados
        tvSinDatos = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(24)
            }
            text = "No tienes monitoreos guardados"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#666666"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

            // Inicialmente está oculto; solo se mostrará si no hay datos
            visibility = View.GONE
        }

        // RecyclerView que mostrará la lista de monitoreos
        recyclerMonitoreos = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            layoutManager = LinearLayoutManager(this@pantalla_monitoreos)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        // Se crea el adaptador y se define qué hacer cuando se toca un monitoreo
        monitoreoAdapter = MonitoreoAdapter { sesion ->
            abrirDetalleMonitoreo(sesion)
        }

        // Se conecta el adaptador al RecyclerView
        recyclerMonitoreos.adapter = monitoreoAdapter

        header.addView(backMonitoreo)
        header.addView(titulo)

        root.addView(header)
        root.addView(tvSinDatos)
        root.addView(recyclerMonitoreos)

        setContentView(root)


        backMonitoreo.setOnClickListener {
            openActivity(MainActivity::class.java)
        }
        cargarMonitoreosDelUsuario()
    }

    private fun cargarMonitoreosDelUsuario() {
        lifecycleScope.launch {
            // withContext se usa para ejecutar operaciones pesadas de base de datos
            val lista = withContext(Dispatchers.IO) {
                val userId = sessionManager.obtenerUserId()

                // Si no hay usuario logueado, regresa lista vacía
                if (userId <= 0) return@withContext emptyList<MonitoreoEntity>()
                val usuario = db.userDao().obtenerUsuarioPorId(userId)
                    ?: return@withContext emptyList<MonitoreoEntity>()
                val nombreCompleto = "${usuario.nombre} ${usuario.apellido}".trim()
                db.monitoreoDao().obtenerPorUsuario(nombreCompleto)
            }

            // Aquí agrupas y preparas los datos para mostrarlos en la UI
            val listaAgrupada = withContext(Dispatchers.Default) {
                agruparMonitoreos(lista)
            }

            // Si no hay datos, muestra el mensaje
            if (listaAgrupada.isEmpty()) {
                tvSinDatos.visibility = View.VISIBLE
                recyclerMonitoreos.visibility = View.GONE
            } else {
                tvSinDatos.visibility = View.GONE
                recyclerMonitoreos.visibility = View.VISIBLE

                // Envía la lista al adaptador para que la pinte en pantalla
                monitoreoAdapter.submitList(listaAgrupada)
            }
        }
    }

    // Esta función toma todos los registros de monitoreo y los agrupa por sesionId
    // para que cada sesión aparezca como un solo "Monitoreo 1", "Monitoreo 2", etc.
    private fun agruparMonitoreos(lista: List<MonitoreoEntity>): List<MonitoreoSesionUI> {
        val sesionesOrdenadas = lista
            .groupBy { it.sesionId }
            .map { (sesionId, registros) ->
                SesionTemp(
                    sesionId = sesionId,

                    // Se usa el ID menor para definir el orden en que apareció esa sesión
                    orden = registros.minOfOrNull { it.id } ?: 0,
                    registros = registros
                )
            }
            .sortedBy { it.orden }
        return sesionesOrdenadas.mapIndexed { index, sesion ->

            // Dentro de cada sesión ordena los registros por número de punto
            // y si empatan, por ID
            val registrosOrdenados = sesion.registros.sortedWith(
                compareBy<MonitoreoEntity> { extraerNumeroPunto(it.punto) }
                    .thenBy { it.id }
            )

            val primero = registrosOrdenados.first()

            MonitoreoSesionUI(
                sesionId = sesion.sesionId,
                numeroMonitoreo = index + 1,
                agricultor = primero.agricola.ifBlank { "-" },
                granja = primero.granja.ifBlank { "-" },
                lote = primero.lote.ifBlank { "-" },
                fecha = registrosOrdenados.firstOrNull { it.fecha.isNotBlank() }?.fecha ?: "-",
                cultivo = primero.cultivo.ifBlank { "-" },
                registros = registrosOrdenados.map { reg ->
                    RegistroDetalleUI(
                        punto = reg.punto.ifBlank { "-" },
                        latitud = reg.latitud.ifBlank { "-" },
                        longitud = reg.longitud.ifBlank { "-" },
                        afectacion = reg.nombre.ifBlank { reg.tipo.ifBlank { "-" } },

                        fase = reg.fase.ifBlank { "-" },
                        cantidad = reg.cantidad.ifBlank { "-" },
                        fecha = reg.fecha.ifBlank { "-" },
                        hora = reg.hora.ifBlank { "-" }
                    )
                }
            )
        }
    }

    // Esta función abre la pantalla de detalle/mapa del monitoreo seleccionado
    private fun abrirDetalleMonitoreo(item: MonitoreoSesionUI) {
        val intent = Intent(this, PantallaMapaMonitoreo::class.java)

        // Manda datos generales del monitoreo por extras
        intent.putExtra("titulo", "Monitoreo ${item.numeroMonitoreo}")
        intent.putExtra("agricultor", item.agricultor)
        intent.putExtra("granja", item.granja)
        intent.putExtra("lote", item.lote)
        intent.putExtra("fecha", item.fecha)
        intent.putExtra("cultivo", item.cultivo)

        // Aquí conviertes cada registro a un String con separador
        val registros = ArrayList<String>()
        item.registros.forEach { reg ->
            registros.add(
                "${reg.punto}|${reg.latitud}|${reg.longitud}|${reg.afectacion}|${reg.fase}|${reg.cantidad}|${reg.fecha}|${reg.hora}"
            )
        }
        intent.putStringArrayListExtra("registros_detalle", registros)
        startActivity(intent)
    }

    // Esta función extrae el número de un texto como "Punto 1", "Punto 2", etc.
    private fun extraerNumeroPunto(punto: String): Int {
        return Regex("\\d+").find(punto)?.value?.toIntOrNull() ?: Int.MAX_VALUE
    }

    private fun openActivity(sto: Class<*>) {
        val intent = Intent(this@pantalla_monitoreos, sto)
        startActivity(intent)
        finish()
    }

    // Esto ayuda a que la interfaz se vea proporcional en distintos dispositivos
    private fun dp(valor: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            valor.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    // Clase temporal para agrupar registros por sesión
    data class SesionTemp(
        val sesionId: String,
        val orden: Int,
        val registros: List<MonitoreoEntity>
    )

    // Modelo que ya está preparado para mostrarse en la interfaz
    data class MonitoreoSesionUI(
        val sesionId: String,
        val numeroMonitoreo: Int,
        val agricultor: String,
        val granja: String,
        val lote: String,
        val fecha: String,
        val cultivo: String,
        val registros: List<RegistroDetalleUI>
    )

    // Modelo para el detalle de cada registro dentro de una sesión
    data class RegistroDetalleUI(
        val punto: String,
        val latitud: String,
        val longitud: String,
        val afectacion: String,
        val fase: String,
        val cantidad: String,
        val fecha: String,
        val hora: String
    )

    // Adaptador del RecyclerView
    class MonitoreoAdapter(
        private val onItemClick: (MonitoreoSesionUI) -> Unit
    ) : ListAdapter<MonitoreoSesionUI, MonitoreoAdapter.MonitoreoViewHolder>(DiffCallback) {

        // DiffUtil sirve para comparar la lista vieja con la nueva
        // y actualizar solo los elementos necesarios del RecyclerView
        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<MonitoreoSesionUI>() {

                // Aquí comparas si dos elementos representan el mismo monitoreo
                override fun areItemsTheSame(
                    oldItem: MonitoreoSesionUI,
                    newItem: MonitoreoSesionUI
                ): Boolean {
                    return oldItem.sesionId == newItem.sesionId
                }

                // Aquí comparas si el contenido completo cambió
                override fun areContentsTheSame(
                    oldItem: MonitoreoSesionUI,
                    newItem: MonitoreoSesionUI
                ): Boolean {
                    return oldItem == newItem
                }
            }
        }

        // ViewHolder representa cada tarjeta/fila individual del RecyclerView
        inner class MonitoreoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val tvTituloSesion: TextView = itemView.findViewById(R.id.tvTituloSesion)

            // Esta función llena la vista con los datos del item actual
            fun bind(item: MonitoreoSesionUI) {
                tvTituloSesion.text = "Monitoreo ${item.numeroMonitoreo}"

                itemView.setOnClickListener {
                    onItemClick(item)
                }
            }
        }

        // Aquí se infla el XML de cada item del RecyclerView
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitoreoViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_monitoreo_sesion, parent, false)
            return MonitoreoViewHolder(view)
        }

        // Aquí se enlazan los datos con el ViewHolder según la posición
        override fun onBindViewHolder(holder: MonitoreoViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }
}