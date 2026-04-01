package com.example.apptest

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
//import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.apptest.data.AppDatabase
import com.example.apptest.data.OpcionEntity
import kotlinx.coroutines.launch
import java.util.Calendar
import android.content.Context
import java.util.UUID


class Pantalla_registroeventos : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var sessionManager: SessionManager

    private lateinit var selectAgricultor: String
    private lateinit var selectGranja: String
    private lateinit var selectLote: String
    private var nombreUsuarioLogueado: String = ""

    private lateinit var spAgricultor: Spinner
    private lateinit var spGranja: Spinner
    private lateinit var spLote: Spinner


    private lateinit var etAgricultor: EditText
    private lateinit var etGranja: EditText
    private lateinit var etLote: EditText

    private lateinit var ibAddAgricultor: ImageButton
    private lateinit var ibAddGranja: ImageButton
    private lateinit var ibAddLote: ImageButton

    private lateinit var ibToAgricultor: ImageButton
    private lateinit var ibToGranja: ImageButton
    private lateinit var ibToLote: ImageButton

    private lateinit var llAddAgricultor: LinearLayout
    private lateinit var llAddGranja: LinearLayout
    private lateinit var llAddLote: LinearLayout

    private lateinit var ibDeleteAgricultor: ImageButton
    private lateinit var ibDeleteGranja: ImageButton
    private lateinit var ibDeleteLote: ImageButton

    private val itemsAgricultor = mutableListOf<String>()
    private val itemsGranja = mutableListOf<String>()
    private val itemsLote = mutableListOf<String>()

    private lateinit var adapterAgricultor: ArrayAdapter<String>
    private lateinit var adapterGranja: ArrayAdapter<String>
    private lateinit var adapterLote: ArrayAdapter<String>

    private var selectedItemAgricultor: String = ""
    private var selectedItemGranja: String = ""
    private var selectedItemLote: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_registroeventos)

        db = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        spAgricultor = findViewById(R.id.sp_agricultor)
        spGranja = findViewById(R.id.sp_granja)
        spLote = findViewById(R.id.sp_lote)
        spCultivo = findViewById(R.id.sp_cultivo)

        etAgricultor = findViewById(R.id.et_agricultor)
        etGranja = findViewById(R.id.et_granja)
        etLote = findViewById(R.id.et_lote)

        ibAddAgricultor = findViewById(R.id.ib_addagricultor)
        ibAddGranja = findViewById(R.id.ib_addgranja)
        ibAddLote = findViewById(R.id.ib_addlote)

        ibDeleteAgricultor = findViewById(R.id.ib_deleteagricultor)
        ibDeleteGranja = findViewById(R.id.ib_deletegranja)
        ibDeleteLote = findViewById(R.id.ib_deletelote)

        ibToAgricultor = findViewById(R.id.ib_toagricultor)
        ibToGranja = findViewById(R.id.ib_togranja)
        ibToLote = findViewById(R.id.ib_tolote)

        llAddAgricultor = findViewById(R.id.ll_addagricultor)
        llAddGranja = findViewById(R.id.ll_addgranja)
        llAddLote = findViewById(R.id.ll_addlote)

        adapterCultivo = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            itemsCultivo
        )
        adapterCultivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCultivo.adapter = adapterCultivo
        configurarSpinnerCultivo()

        val inputFilter = InputFilter { source, _, _, _, _, _ ->
            for (i in source.indices) {
                if (!Character.isLetterOrDigit(source[i]) && source[i] != ' ') {
                    return@InputFilter ""
                }
            }
            null
        }

        disableSpecialCharacter(etAgricultor)
        disableSpecialCharacter(etGranja)
        disableSpecialCharacter(etLote)

        etAgricultor.filters = arrayOf(inputFilter)
        etGranja.filters = arrayOf(inputFilter)
        etLote.filters = arrayOf(inputFilter)

        toUpperText(etAgricultor)
        toUpperText(etGranja)
        toUpperText(etLote)

        adapterAgricultor =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, itemsAgricultor)
        adapterAgricultor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapterGranja =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, itemsGranja)
        adapterGranja.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        adapterLote =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, itemsLote)
        adapterLote.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spAgricultor.adapter = adapterAgricultor
        spGranja.adapter = adapterGranja
        spLote.adapter = adapterLote

        configurarBotonesDesplegables()
        configurarSpinners()
        configurarBotonesAgregar()
        configurarBotonesEliminar()

        lifecycleScope.launch {
            cargarUsuarioLogueadoEnAgricultor()
            recargarTodoDesdeRoom()
        }

        val botonRegresarRegistro: Button = findViewById(R.id.bRegresar)
        botonRegresarRegistro.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val botonComenzarRegistro: Button = findViewById(R.id.bComenzarregistro)
        botonComenzarRegistro.setOnClickListener {
            showinfo()
        }
    }
    private lateinit var spCultivo: Spinner
    private lateinit var adapterCultivo: ArrayAdapter<String>

    private val itemsCultivo = mutableListOf(
        "Brocoli",
        "Lechuga",
        "Berries",
        "Maiz",
        "Agave",
        "Trigo",
        "Sorgo"
    )

    private var textRB: String = "Brocoli"

    private fun configurarBotonesDesplegables() {
        val buttons = listOf(ibToAgricultor, ibToGranja, ibToLote)
        val layouts = listOf(llAddAgricultor, llAddGranja, llAddLote)

        buttons.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {
                layouts.forEachIndexed { layoutIndex, linearLayout ->
                    if (layoutIndex == index) {
                        linearLayout.visibility =
                            if (linearLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE

                        val iconResource =
                            if (linearLayout.visibility == View.VISIBLE) {
                                R.drawable.ic_uparrow
                            } else {
                                R.drawable.ic_downarrow
                            }

                        imageButton.setImageResource(iconResource)
                    } else {
                        linearLayout.visibility = View.GONE
                        buttons[layoutIndex].setImageResource(R.drawable.ic_downarrow)
                    }
                }
            }
        }
    }


    private fun configurarSpinnerCultivo() {
        spCultivo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                textRB = itemsCultivo[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                textRB = "Brocoli"
            }
        }
    }

    private fun configurarSpinners() {
        spAgricultor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (itemsAgricultor.isNotEmpty()) {
                    selectedItemAgricultor = itemsAgricultor[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spGranja.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (itemsGranja.isNotEmpty()) {
                    selectedItemGranja = itemsGranja[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spLote.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (itemsLote.isNotEmpty()) {
                    selectedItemLote = itemsLote[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun configurarBotonesAgregar() {
        ibAddAgricultor.setOnClickListener {
            agregarOpcion(
                tipo = "AGRICULTOR",
                editText = etAgricultor,
                lista = itemsAgricultor,
                adapter = adapterAgricultor,
                spinner = spAgricultor,
                mensajeVacio = "No se ha agregado ningún Agricultor.",
                mensajeExito = "El Agricultor ha sido añadido.",
                mensajeExiste = "El Agricultor ya existe en la lista."
            )
        }

        ibAddGranja.setOnClickListener {
            agregarOpcion(
                tipo = "GRANJA",
                editText = etGranja,
                lista = itemsGranja,
                adapter = adapterGranja,
                spinner = spGranja,
                mensajeVacio = "No se ha agregado ninguna Granja.",
                mensajeExito = "La Granja ha sido añadida.",
                mensajeExiste = "La Granja ya existe en la lista."
            )
        }

        ibAddLote.setOnClickListener {
            agregarOpcion(
                tipo = "LOTE",
                editText = etLote,
                lista = itemsLote,
                adapter = adapterLote,
                spinner = spLote,
                mensajeVacio = "No se ha agregado ningún Lote.",
                mensajeExito = "El Lote ha sido añadido.",
                mensajeExiste = "El Lote ya existe en la lista."
            )
        }
    }

    private fun configurarBotonesEliminar() {
        ibDeleteAgricultor.setOnClickListener {
            if (selectedItemAgricultor.isEmpty()) {
                showMessage("No hay ningún Agricultor seleccionado para eliminar.")
                return@setOnClickListener
            }

            if (selectedItemAgricultor == nombreUsuarioLogueado) {
                showMessage("No puedes eliminar el agricultor que inició sesión.")
                return@setOnClickListener
            }

            confirmarEliminacion(
                titulo = "Eliminar Agricultor",
                mensaje = "¿Está seguro que desea eliminar el Agricultor: \"$selectedItemAgricultor\" ?",
                tipo = "AGRICULTOR",
                nombre = selectedItemAgricultor,
                lista = itemsAgricultor,
                adapter = adapterAgricultor,
                mensajeExito = "El Agricultor \"$selectedItemAgricultor\" ha sido eliminado."
            )
        }

        ibDeleteGranja.setOnClickListener {
            if (selectedItemGranja.isEmpty()) {
                showMessage("No hay ninguna Granja seleccionada para eliminar.")
                return@setOnClickListener
            }

            confirmarEliminacion(
                titulo = "Eliminar Granja",
                mensaje = "¿Está seguro que desea eliminar la Granja: \"$selectedItemGranja\" ?",
                tipo = "GRANJA",
                nombre = selectedItemGranja,
                lista = itemsGranja,
                adapter = adapterGranja,
                mensajeExito = "La Granja \"$selectedItemGranja\" ha sido eliminada."
            )
        }

        ibDeleteLote.setOnClickListener {
            if (selectedItemLote.isEmpty()) {
                showMessage("No hay ningún Lote seleccionado para eliminar.")
                return@setOnClickListener
            }

            confirmarEliminacion(
                titulo = "Eliminar Lote",
                mensaje = "¿Está seguro que desea eliminar el Lote: \"$selectedItemLote\" ?",
                tipo = "LOTE",
                nombre = selectedItemLote,
                lista = itemsLote,
                adapter = adapterLote,
                mensajeExito = "El Lote \"$selectedItemLote\" ha sido eliminado."
            )
        }
    }

    private fun agregarOpcion(
        tipo: String,
        editText: EditText,
        lista: MutableList<String>,
        adapter: ArrayAdapter<String>,
        spinner: Spinner,
        mensajeVacio: String,
        mensajeExito: String,
        mensajeExiste: String
    ) {
        val nuevoValor = editText.text.toString().trim()

        if (nuevoValor.isEmpty()) {
            showMessage(mensajeVacio)
            return
        }

        val userId = sessionManager.obtenerUserId()
        if (userId <= 0) {
            showMessage("No se pudo identificar el usuario.")
            return
        }

        lifecycleScope.launch {
            val existe = db.opcionDao().existe(tipo, nuevoValor, userId)

            if (existe) {
                showMessage(mensajeExiste)
            } else {
                db.opcionDao().insertar(
                    OpcionEntity(
                        tipo = tipo,
                        nombre = nuevoValor,
                        userId = userId
                    )
                )

                recargarOpciones(tipo, lista, adapter, spinner, nuevoValor)
                showMessage(mensajeExito)
                editText.text.clear()
            }
        }
    }
    private fun confirmarEliminacion(
        titulo: String,
        mensaje: String,
        tipo: String,
        nombre: String,
        lista: MutableList<String>,
        adapter: ArrayAdapter<String>,
        mensajeExito: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)

        builder.setPositiveButton("Si") { _, _ ->
            val userId = sessionManager.obtenerUserId()
            lifecycleScope.launch {
                db.opcionDao().eliminar(tipo, nombre, userId)
                recargarOpciones(tipo, lista, adapter)
                showMessage(mensajeExito)
            }
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private suspend fun cargarUsuarioLogueadoEnAgricultor() {
        val userId = sessionManager.obtenerUserId()
        if (userId <= 0) return

        val usuario = db.userDao().obtenerUsuarioPorId(userId) ?: return
        nombreUsuarioLogueado = "${usuario.nombre} ${usuario.apellido}".trim()

        if (nombreUsuarioLogueado.isNotEmpty()) {
            val existe = db.opcionDao().existe("AGRICULTOR", nombreUsuarioLogueado, userId)
            if (!existe) {
                db.opcionDao().insertar(
                    OpcionEntity(
                        tipo = "AGRICULTOR",
                        nombre = nombreUsuarioLogueado,
                        userId = userId
                    )
                )
            }
        }
    }

    private suspend fun recargarTodoDesdeRoom() {
        recargarOpciones("AGRICULTOR", itemsAgricultor, adapterAgricultor, spAgricultor, nombreUsuarioLogueado)
        recargarOpciones("GRANJA", itemsGranja, adapterGranja, spGranja)
        recargarOpciones("LOTE", itemsLote, adapterLote, spLote)
    }

    private suspend fun recargarOpciones(
        tipo: String,
        lista: MutableList<String>,
        adapter: ArrayAdapter<String>,
        spinner: Spinner? = null,
        seleccionarNombre: String? = null
    ) {
        val userId = sessionManager.obtenerUserId()
        if (userId <= 0) return

        val opciones = db.opcionDao().obtenerPorTipo(tipo, userId).map { it.nombre }

        lista.clear()
        lista.addAll(opciones)
        adapter.notifyDataSetChanged()

        if (spinner != null && lista.isNotEmpty()) {
            val posicion = if (!seleccionarNombre.isNullOrEmpty()) {
                lista.indexOf(seleccionarNombre).takeIf { it >= 0 } ?: 0
            } else {
                spinner.selectedItemPosition.takeIf { it in lista.indices } ?: 0
            }
            spinner.setSelection(posicion)
        }
    }

    private fun showinfo() {
        selectAgricultor = spAgricultor.selectedItem?.toString() ?: ""
        selectGranja = spGranja.selectedItem?.toString() ?: ""
        selectLote = spLote.selectedItem?.toString() ?: ""

        if (selectAgricultor.isEmpty() || selectGranja.isEmpty() || selectLote.isEmpty()) {
            showMessage("Completa Agricultor, Granja y Lote.")
            return
        }

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Datos de Registro")
        builder.setMessage(
            "Estas a punto de registrar en: \n Agricultor: $selectAgricultor \n Granja: $selectGranja \n Lote: $selectLote \n Cultivo: $textRB \n\t ¿Deseas continuar?"
        )

        val actividades = mapOf(
            "Brocoli" to pantalla_implementoplagas::class.java,
            "Lechuga" to pantalla_plagaslechuga::class.java,
            "Berries" to pantalla_plagasberries::class.java,
            "Maiz" to pantalla_plagasmaiz::class.java,
            "Agave" to pantalla_plagasagave::class.java,
            "Trigo" to pantalla_plagastrigo::class.java,
            "Sorgo" to pantalla_plagassorgo::class.java
        )

        builder.setPositiveButton("Sí") { dialog, _ ->
            actividades[textRB]?.let { actividad ->
                val intent = Intent(this, actividad)

                val nuevaSesion = UUID.randomUUID().toString()
                getSharedPreferences("registro_local", Context.MODE_PRIVATE)
                    .edit()
                    .putString("sesion_id_actual", nuevaSesion)
                    .apply()

                dataSave.add(
                    mutableListOf(
                        "Punto",
                        "Monitoreador",
                        "Agricola",
                        "Granja",
                        "Lote",
                        "Cultivo",
                        "Latitud",
                        "Longitud",
                        "Tipo",
                        "Nombre",
                        "Fase",
                        "Cantidad",
                        "Fecha",
                        "Hora"
                    )
                )

                val fecha = obtenerFechaActual()

                archiveName = archiveName + "_" +
                        selectAgricultor.lowercase().replace("\\s".toRegex(), "") + "_" +
                        selectGranja.lowercase().replace("\\s".toRegex(), "") + "_" +
                        selectLote.lowercase().replace("\\s".toRegex(), "") + "_" +
                        textRB + "_" + fecha + ".csv"

                archiveUmbral = archiveUmbral + "_" +
                        textRB + "_" +
                        selectAgricultor.lowercase().replace("\\s".toRegex(), "") + "_" +
                        selectGranja.lowercase().replace("\\s".toRegex(), "") + "_" +
                        selectLote.lowercase().replace("\\s".toRegex(), "") + "_" +
                        fecha + ".pdf"

                continueActivity = textRB
                nameAgricultor = selectAgricultor
                nameGranja = selectGranja
                nameLote = selectLote

                Agricultor = selectAgricultor
                Granja = selectGranja
                Lote = selectLote
                Cultivo = textRB

                startActivity(intent)
                finish()
                dialog.dismiss()
            }
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showMessage(mostrarMsj: String) {
        Toast.makeText(applicationContext, mostrarMsj, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {}

    private fun disableSpecialCharacter(editText: EditText) {
        val filter = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                if (source != null && source.contains("ñ")) {
                    return ""
                }
                return null
            }
        }

        val filters = editText.filters.toMutableList()
        filters.add(filter)
        editText.filters = filters.toTypedArray()
    }

    private fun toUpperText(etToEdit: EditText) {
        etToEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val text = it.toString().uppercase()
                    if (text != it.toString()) {
                        it.replace(0, it.length, text)
                    }
                }
            }
        })
    }

    private fun obtenerFechaActual(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-$month-$day"
    }
}