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
    private lateinit var monitoreoAdapter: MonitoreoAdapter
    private lateinit var db: AppDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(applicationContext)
        sessionManager = SessionManager(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

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

        backMonitoreo = ImageButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
            setImageResource(R.drawable.ic_back)
            setBackgroundColor(Color.TRANSPARENT)
            backgroundTintList =
                ContextCompat.getColorStateList(this@pantalla_monitoreos, R.color.greenti)
        }

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
            visibility = View.GONE
        }

        recyclerMonitoreos = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            layoutManager = LinearLayoutManager(this@pantalla_monitoreos)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        monitoreoAdapter = MonitoreoAdapter { sesion ->
            abrirDetalleMonitoreo(sesion)
        }

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
            val lista = withContext(Dispatchers.IO) {
                val userId = sessionManager.obtenerUserId()
                if (userId <= 0) return@withContext emptyList<MonitoreoEntity>()

                val usuario = db.userDao().obtenerUsuarioPorId(userId)
                    ?: return@withContext emptyList<MonitoreoEntity>()

                val nombreCompleto = "${usuario.nombre} ${usuario.apellido}".trim()
                db.monitoreoDao().obtenerPorUsuario(nombreCompleto)
            }

            val listaAgrupada = withContext(Dispatchers.Default) {
                agruparMonitoreos(lista)
            }

            if (listaAgrupada.isEmpty()) {
                tvSinDatos.visibility = View.VISIBLE
                recyclerMonitoreos.visibility = View.GONE
            } else {
                tvSinDatos.visibility = View.GONE
                recyclerMonitoreos.visibility = View.VISIBLE
                monitoreoAdapter.submitList(listaAgrupada)
            }
        }
    }

    private fun agruparMonitoreos(lista: List<MonitoreoEntity>): List<MonitoreoSesionUI> {
        val sesionesOrdenadas = lista
            .groupBy { it.sesionId }
            .map { (sesionId, registros) ->
                SesionTemp(
                    sesionId = sesionId,
                    orden = registros.minOfOrNull { it.id } ?: 0,
                    registros = registros
                )
            }
            .sortedBy { it.orden }

        return sesionesOrdenadas.mapIndexed { index, sesion ->
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

    private fun abrirDetalleMonitoreo(item: MonitoreoSesionUI) {
        val intent = Intent(this, PantallaMapaMonitoreo::class.java)
        intent.putExtra("titulo", "Monitoreo ${item.numeroMonitoreo}")
        intent.putExtra("agricultor", item.agricultor)
        intent.putExtra("granja", item.granja)
        intent.putExtra("lote", item.lote)
        intent.putExtra("fecha", item.fecha)
        intent.putExtra("cultivo", item.cultivo)

        val registros = ArrayList<String>()
        item.registros.forEach { reg ->
            registros.add(
                "${reg.punto}|${reg.latitud}|${reg.longitud}|${reg.afectacion}|${reg.fase}|${reg.cantidad}|${reg.fecha}|${reg.hora}"
            )
        }

        intent.putStringArrayListExtra("registros_detalle", registros)
        startActivity(intent)
    }

    private fun extraerNumeroPunto(punto: String): Int {
        return Regex("\\d+").find(punto)?.value?.toIntOrNull() ?: Int.MAX_VALUE
    }

    private fun openActivity(sto: Class<*>) {
        val intent = Intent(this@pantalla_monitoreos, sto)
        startActivity(intent)
        finish()
    }

    private fun dp(valor: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            valor.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    data class SesionTemp(
        val sesionId: String,
        val orden: Int,
        val registros: List<MonitoreoEntity>
    )

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

    class MonitoreoAdapter(
        // se hace una vista de los datos
        private val onItemClick: (MonitoreoSesionUI) -> Unit
    ) : ListAdapter<MonitoreoSesionUI, MonitoreoAdapter.MonitoreoViewHolder>(DiffCallback) {
        // compara la lista vieja con la nueva
        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<MonitoreoSesionUI>() {
                override fun areItemsTheSame(
                    oldItem: MonitoreoSesionUI,
                    newItem: MonitoreoSesionUI
                ): Boolean {
                    return oldItem.sesionId == newItem.sesionId
                }

                override fun areContentsTheSame(
                    oldItem: MonitoreoSesionUI,
                    newItem: MonitoreoSesionUI
                ): Boolean {
                    return oldItem == newItem
                }
            }
        }

        inner class MonitoreoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvTituloSesion: TextView = itemView.findViewById(R.id.tvTituloSesion)

            fun bind(item: MonitoreoSesionUI) {
                tvTituloSesion.text = "Monitoreo ${item.numeroMonitoreo}"
                itemView.setOnClickListener {
                    onItemClick(item)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitoreoViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_monitoreo_sesion, parent, false)
            return MonitoreoViewHolder(view)
        }

        override fun onBindViewHolder(holder: MonitoreoViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }
}