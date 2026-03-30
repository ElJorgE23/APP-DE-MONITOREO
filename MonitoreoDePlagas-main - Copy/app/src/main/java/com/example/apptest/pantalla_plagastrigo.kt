package com.example.apptest

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.io.FileWriter
import java.io.IOException


import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.example.apptest.data.AppDatabase
import com.example.apptest.data.MonitoreoEntity
import kotlinx.coroutines.launch
import java.util.UUID

class pantalla_plagastrigo : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
/*    private val locationRequest = LocationRequest.create().apply {
        interval = 5000 // Intervalo de actualización en milisegundos (por ejemplo, cada 5 segundos)
        fastestInterval = 3000 // Intervalo de actualización más rápido en milisegundos (por ejemplo, cada 1 segundo)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Prioridad de la solicitud de ubicación
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation.let { location ->
                // Actualiza la ubicación actual
                val currentLocation = location?.let { LatLng(it.latitude, location.longitude) }
                // Realiza acciones con la nueva ubicación
            }
        }
    }*/

    private var saveFileLauncher: ActivityResultLauncher<Intent>? = null
    init {
        setupSaveFileLauncher()
    }
    private lateinit var bEnviarRegistro: Button
    private lateinit var bNoPlagas: ImageButton
    private lateinit var FyH: String
    private lateinit var Fecha: String
    private lateinit var Hora: String
    private lateinit var idPlague: TextView
    private lateinit var ContentButton: LinearLayout
    private lateinit var ContentReciver: LinearLayout
    private lateinit var bOtrasPlagas: Button
    private lateinit var tvPlagas: TextView
    private lateinit var llPlagasPrincipales: LinearLayout
    private lateinit var llOtrasPlagas: LinearLayout
    private lateinit var db: AppDatabase

    //Definimos las variables para las plagas
    private lateinit var botonGS: ImageButton; private lateinit var botonPV: ImageButton
    //...
    private lateinit var cGS: LinearLayout; private lateinit var cPV: LinearLayout
    //...
    private lateinit var registroGSh: LinearLayout; private lateinit var registroGSc: LinearLayout; private lateinit var registroGSm: LinearLayout; private lateinit var registroGSg: LinearLayout; private lateinit var registroGSp: LinearLayout
    private lateinit var registroPVca: LinearLayout; private lateinit var registroPVsa: LinearLayout
    //...
    private lateinit var gshplus: ImageButton; private lateinit var gscplus: ImageButton; private lateinit var gsmplus: ImageButton; private lateinit var gsgplus: ImageButton; private lateinit var gspplus: ImageButton ; private lateinit var gshminus: ImageButton; private lateinit var gscminus: ImageButton; private lateinit var gsmminus: ImageButton; private lateinit var gsgminus: ImageButton; private lateinit var gspminus: ImageButton
    private lateinit var pvcaplus: ImageButton; private lateinit var pvsaplus: ImageButton; private lateinit var pvcaminus: ImageButton; private lateinit var pvsaminus: ImageButton
    //...
    private lateinit var tvhgs: TextView; private lateinit var tvcgs: TextView; private lateinit var tvmgs: TextView; private lateinit var tvggs: TextView; private lateinit var tvpgs: TextView
    private lateinit var tvsapv: TextView; private lateinit var tvcapv: TextView
    //...
    private lateinit var tvGSh: TextView; private lateinit var tvGSc: TextView; private lateinit var tvGSm: TextView; private lateinit var tvGSg: TextView; private lateinit var tvGSp: TextView
    private lateinit var tvPVca: TextView; private lateinit var tvPVsa: TextView
    
    //Para las enfermedades
    private lateinit var botonRDH: ImageButton
    private lateinit var botonRAH: ImageButton
    private lateinit var botonPDR: ImageButton
    //...
    private lateinit var cRDH: LinearLayout
    private lateinit var cRAH: LinearLayout
    private lateinit var cPDR: LinearLayout
    //...
    private lateinit var registroRDH: LinearLayout
    private lateinit var registroRAH: LinearLayout
    private lateinit var registroPDR: LinearLayout
    //...
    private lateinit var cbRodahoja: CheckBox
    private lateinit var cbRodaamarilla: CheckBox
    private lateinit var cbPodredumbreraices: CheckBox
    //...
    private lateinit var tvRodahoja: TextView
    private lateinit var tvRodaamarilla: TextView
    private lateinit var tvPodredumbreraices: TextView






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_plagastrigo)
        db = AppDatabase.getDatabase(applicationContext)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(20000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    latitud = location.latitude
                    longitud = location.longitude
                    // Maneja la ubicación aquí
                }
            }
        }

        getLastLocation { lat, lon ->
            // Maneja la ubicación aquí
        }
        /*-------------------------------------------------
        |   Coincidimos las variables con su respectivo ID
         -------------------------------------------------*/
        FyH = obtenerFechaYHora()
        Fecha = getFecha()
        Hora = getHora()
        idPlague = findViewById(R.id.tv_idPlagas)
        bEnviarRegistro = findViewById(R.id.b_EnviarPlagas)
        bNoPlagas = findViewById(R.id.boton_noplaga)
        ContentButton = findViewById(R.id.ContenedorBoton)
        ContentReciver = findViewById(R.id.ContenedorReciver)
        bOtrasPlagas = findViewById(R.id.boton_otrasplagas)
        tvPlagas = findViewById(R.id.tvPlagas)
        llPlagasPrincipales = findViewById(R.id.Caja_plagasprincipales)
        llOtrasPlagas = findViewById(R.id.Caja_otrasplagas)

        //Definimos el botón que nos ayuda a visualizar entre un grupo de plagas u otras
        var current_icon: Int = 0
        val ic_add = resources.getDrawable(R.drawable.ic_add); val ic_hide = resources.getDrawable(R.drawable.ic_hide)

        //Para las plagas 
        botonGS = findViewById(R.id.boton_gusanosoldado); botonPV = findViewById(R.id.boton_pulgonverde)
        //...
        cGS = findViewById(R.id.CajaGS); cPV = findViewById(R.id.CajaPV)
        //...
        registroGSh = findViewById(R.id.gsregistroh); registroGSc = findViewById(R.id.gsregistroc); registroGSm = findViewById(R.id.gsregistrom); registroGSg = findViewById(R.id.gsregistrog); registroGSp = findViewById(R.id.gsregistrop)
        registroPVca = findViewById(R.id.pvregistroca); registroPVsa = findViewById(R.id.pvregistrosa)
        //...
        gshplus = findViewById(R.id.ib_huevecillomasGS); gscplus = findViewById(R.id.ib_chicomasGS); gsmplus = findViewById(R.id.ib_medianomasGS); gsgplus = findViewById(R.id.ib_grandemasGS); gspplus = findViewById(R.id.ib_pupamasGS); gshminus = findViewById(R.id.ib_huevecillomenosGS); gscminus = findViewById(R.id.ib_chicomenosGS); gsmminus = findViewById(R.id.ib_medianomenosGS); gsgminus = findViewById(R.id.ib_grandemenosGS); gspminus = findViewById(R.id.ib_pupamenosGS)
        pvcaplus = findViewById(R.id.ib_conalasmasPV); pvsaplus = findViewById(R.id.ib_sinalasmasPV); pvcaminus = findViewById(R.id.ib_conalasmenosPV); pvsaminus = findViewById(R.id.ib_sinalasmenosPV)
        //...
        tvhgs = findViewById(R.id.cantidad_hGS); tvcgs = findViewById(R.id.cantidad_cGS); tvmgs = findViewById(R.id.cantidad_mGS); tvggs = findViewById(R.id.cantidad_gGS); tvpgs = findViewById(R.id.cantidad_pGS)
        tvcapv = findViewById(R.id.cantidad_caPV); tvsapv = findViewById(R.id.cantidad_saPV)
        //...
        tvGSh = findViewById(R.id.tv_gsh); tvGSc = findViewById(R.id.tv_gsc); tvGSm = findViewById(R.id.tv_gsm); tvGSg = findViewById(R.id.tv_gsg); tvGSp = findViewById(R.id.tv_gsp)
        tvPVca = findViewById(R.id.tv_pvca); tvPVsa = findViewById(R.id.tv_pvsa)

        //Para las enfermedades
        botonRDH = findViewById(R.id.boton_royahoja)
        botonRAH = findViewById(R.id.boton_royaamarillaroja)
        botonPDR = findViewById(R.id.boton_podredumbreraices)
        //...
        cRDH = findViewById(R.id.CajaRDH)
        cRAH = findViewById(R.id.CajaRAH)
        cPDR = findViewById(R.id.CajaPDR)
        //...
        registroRDH = findViewById(R.id.rdhregistro)
        registroRAH = findViewById(R.id.rahregistro)
        registroPDR = findViewById(R.id.pdrregistro)
        //...
        cbRodahoja = findViewById(R.id.cb_royahoja)
        cbRodaamarilla = findViewById(R.id.cb_royaamarillahoja)
        cbPodredumbreraices = findViewById(R.id.cb_podredumbreraices)
        //...
        tvRodahoja = findViewById(R.id.tv_rdh)
        tvRodaamarilla = findViewById(R.id.tv_rah)
        tvPodredumbreraices = findViewById(R.id.tv_pdr)

        //Agregamos las strings para los nombres de las plagas
        val strGS = getString(R.string.plaga_gusanosoldado)
        val strPV = getString(R.string.plaga_pulgonverde)
        //Agrergamos las strings con las diferentes fases de las plagas
        val strfaseh = getString(R.string.fase_huevecillo)
        val strfasec = getString(R.string.fase_chico)
        val strfasem = getString(R.string.fase_mediano)
        val strfaseg = getString(R.string.fase_grande)
        val strfasep = getString(R.string.fase_pupa)
        val strfaseca = getString(R.string.fase_conalas)
        val strfasesa = getString(R.string.fase_sinalas)
        val strfasena = getString(R.string.fase_sinfase)
        //Agregamos las strings para el nombre de las enfermedades
        val strenfRDH = getString(R.string.enfermedad_royahoja)
        val strenfRAH = getString(R.string.enfermedad_royaamarillahoja)
        val strenfPDR = getString(R.string.enfermedad_podredumbreraices)

        //Definimos una lista que contiene todos los botones de las palgas y enfermedades
        val buttonsPlague: List<ImageButton> = listOf(botonGS, botonPV, botonRDH, botonRAH, botonPDR)
        //Agregamos una lista con los layouts de las plagas
        val registerPlague: List<LinearLayout> = listOf(cGS, cPV, cRDH, cRAH, cPDR)

        /* ------------------------------------------------------
        |   Implementamos los métodos y los eventos
         --------------------------------------------------------*/
        //Actualizamos el TexView para que muestre el punto que estamos registrando
        idPlague.text = IDcount.toString()

        bNoPlagas.setOnClickListener { showAdvertence() }

        //Agregamos los eventos al intercambiar entre plagas y enfermedades
        bOtrasPlagas.setOnClickListener {
            if (current_icon == 0){
                bOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_hide, null, null, null)
                current_icon = 1
                llOtrasPlagas.visibility = View.VISIBLE
                tvPlagas.text = "Enfermedades"
                llPlagasPrincipales.visibility = View.GONE
                (ContentButton.parent as? LinearLayout)?.removeView(ContentButton)
                ContentReciver.addView(ContentButton)
            }else{
                bOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_add, null, null, null)
                current_icon = 0
                llOtrasPlagas.visibility = View.GONE
                tvPlagas.text = "Plagas"
                llPlagasPrincipales.visibility = View.VISIBLE
                ContentReciver.removeView(ContentButton)
                llPlagasPrincipales.addView(ContentButton, 0)
            }
        }

        //Definimos el funcionamiento para los botones que muestran el registro de cada plaga
        buttonsPlague.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {
                registerPlague.forEachIndexed { layoutindex, linearLayout ->
                    if (layoutindex == index){
                        linearLayout.visibility = View.VISIBLE
                    }else{
                        linearLayout.visibility = View.GONE
                    }
                }
            }
        }

        //Agregamos la funcionalidad de los botones para quitar o agregar cantidad
        gshplus.setOnClickListener { countGSH = increase(countGSH, tvhgs, tvGSh, registroGSh) }; gscplus.setOnClickListener { countGSC = increase(countGSC, tvcgs, tvGSc, registroGSc) }; gsmplus.setOnClickListener { countGSM = increase(countGSM, tvmgs, tvGSm, registroGSm) }; gsgplus.setOnClickListener { countGSG = increase(countGSG, tvggs, tvGSg, registroGSg) }; gspplus.setOnClickListener { countGSP = increase(countGSP, tvpgs, tvGSp, registroGSp) }; gshminus.setOnClickListener { countGSH = decrease(countGSH, tvhgs, tvGSh, registroGSh) }; gscminus.setOnClickListener { countGSC = decrease(countGSC, tvcgs, tvGSc, registroGSc) }; gsmminus.setOnClickListener { countGSM = decrease(countGSM, tvmgs, tvGSm, registroGSm) }; gsgminus.setOnClickListener { countGSG = decrease(countGSG, tvggs, tvGSg, registroGSg) }; gspminus.setOnClickListener { countGSP = decrease(countGSP, tvpgs, tvGSp, registroGSp) }
        pvcaplus.setOnClickListener { countPVCA = increase(countPVCA, tvcapv, tvPVca, registroPVca) }; pvsaplus.setOnClickListener { countPVSA = increase(countPVSA, tvsapv, tvPVsa, registroPVsa) }; pvcaminus.setOnClickListener { countPVCA = decrease(countPVCA, tvcapv, tvPVca, registroPVca) }; pvsaminus.setOnClickListener { countPVSA = decrease(countPVSA, tvsapv, tvPVsa, registroPVsa) }
        //Funcionalidades para registrar las enfermedades
        cbRodahoja.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                registroRDH.visibility = View.VISIBLE
                tvRodahoja.text = "Sí"
            }else{
                registroRDH.visibility = View.GONE
                tvRodahoja.text = ""
            }
        }
        cbRodaamarilla.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                registroRAH.visibility = View.VISIBLE
                tvRodaamarilla.text = "Sí"
            }else{
                registroRAH.visibility = View.GONE
                tvRodaamarilla.text = ""
            }
        }
        cbPodredumbreraices.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                registroPDR.visibility = View.VISIBLE
                tvPodredumbreraices.text = "Sí"
            }else{
                registroPDR.visibility = View.GONE
                tvPodredumbreraices.text = ""
            }
        }

        //Definimos que al enviar el registro se guarden las plagas y nos diriga a la actividad de Mapa
        bEnviarRegistro.setOnClickListener {
            val registros = mutableListOf<MonitoreoEntity>()

            agregarRegistroSiVisible(registros, registroGSh, "Plaga", strGS, strfaseh, countGSH)
            agregarRegistroSiVisible(registros, registroGSc, "Plaga", strGS, strfasec, countGSC)
            agregarRegistroSiVisible(registros, registroGSm, "Plaga", strGS, strfasem, countGSM)
            agregarRegistroSiVisible(registros, registroGSg, "Plaga", strGS, strfaseg, countGSG)
            agregarRegistroSiVisible(registros, registroGSp, "Plaga", strGS, strfasep, countGSP)

            agregarRegistroSiVisible(registros, registroPVca, "Plaga", strPV, strfaseca, countPVCA)
            agregarRegistroSiVisible(registros, registroPVsa, "Plaga", strPV, strfasesa, countPVSA)

            agregarRegistroSiVisible(registros, registroRDH, "Enfermedad", strenfRDH, strfasena, 1)
            agregarRegistroSiVisible(registros, registroRAH, "Enfermedad", strenfRAH, strfasena, 1)
            agregarRegistroSiVisible(registros, registroPDR, "Enfermedad", strenfPDR, strfasena, 1)

            if (registros.isEmpty()) {
                showMessage("Favor de Ingresar un registro.")
            } else {
                lifecycleScope.launch {
                    try {
                        db.monitoreoDao().insertarMonitoreos(registros)
                        abrirMapaDespuesDeGuardar()
                    } catch (e: Exception) {
                        showMessage("Error al guardar en Room: ${e.message}")
                    }
                }
            }
        }

    }

    /* -----------------------------------------------------
    |   Implementamos las funciones necesarias para esta actividad
     ---------------------------------------------------*/
    private fun  openActivity(sto: Class<*>) {
        intent = Intent(this, sto)
        startActivity(intent)
        finish()
    }
    private fun obtenerSesionActual(): String {
        val prefs = getSharedPreferences("registro_local", Context.MODE_PRIVATE)
        var sesionId = prefs.getString("sesion_id_actual", null)

        if (sesionId.isNullOrBlank()) {
            sesionId = UUID.randomUUID().toString()
            prefs.edit().putString("sesion_id_actual", sesionId).apply()
        }

        return sesionId
    }

    private fun crearRegistro(
        tipo: String,
        nombre: String,
        fase: String,
        cantidad: Int
    ): MonitoreoEntity {
        return MonitoreoEntity(
            sesionId = obtenerSesionActual(),
            punto = IDcount.toString(),
            usuario = loggedUser,
            agricola = Agricultor,
            granja = Granja,
            lote = Lote,
            cultivo = Cultivo,
            latitud = latitud?.toString() ?: "",
            longitud = longitud?.toString() ?: "",
            tipo = tipo,
            nombre = nombre,
            fase = fase,
            cantidad = cantidad.toString(),
            fecha = Fecha,
            hora = Hora
        )
    }

    private fun agregarRegistroSiVisible(
        lista: MutableList<MonitoreoEntity>,
        registro: LinearLayout,
        tipo: String,
        nombre: String,
        fase: String,
        cantidad: Int
    ) {
        if (registro.visibility == View.VISIBLE && cantidad > 0) {
            lista.add(crearRegistro(tipo, nombre, fase, cantidad))
        }
    }

    private fun abrirMapaDespuesDeGuardar() {
        stopLocationUpdates()
        val intent = Intent(this@pantalla_plagastrigo, Pantalla_registromapa::class.java)
        intent.putExtra("ind_value", IDcount)
        intent.putExtra("cultivo", Cultivo)
        startActivity(intent)
        finish()
    }
    private fun showAdvertence() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Advertencia")
        builder.setMessage("Esta opción indica que no haz encontrado ninguna plaga \n ¿Quieres continuar?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            val registro = MonitoreoEntity(
                sesionId = obtenerSesionActual(),
                punto = IDcount.toString(),
                usuario = loggedUser,
                agricola = Agricultor,
                granja = Granja,
                lote = Lote,
                cultivo = Cultivo,
                latitud = latitud?.toString() ?: "",
                longitud = longitud?.toString() ?: "",
                tipo = "Sin Plaga",
                nombre = "N/A",
                fase = "N/A",
                cantidad = "0",
                fecha = Fecha,
                hora = Hora
            )

            lifecycleScope.launch {
                try {
                    db.monitoreoDao().insertarMonitoreo(registro)
                    abrirMapaDespuesDeGuardar()
                } catch (e: Exception) {
                    showMessage("Error al guardar: ${e.message}")
                }
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
    private fun setupSaveFileLauncher() {
        saveFileLauncher =
            this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        saveToFile(uri)
                    }
                }
            }
    }
    private fun saveToFile(uri: Uri) {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? = this.contentResolver.openFileDescriptor(uri, "w")
            parcelFileDescriptor?.let { pfd ->
                val fileWriter = FileWriter(pfd.fileDescriptor)
                // Resto del código para escribir en el archivo usando el FileWriter
                // ...
                fileWriter.close()
            }
            parcelFileDescriptor?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun getLastLocation(callback: ((Double, Double) -> Unit)? = null) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitud = location.latitude
                longitud = location.longitude
                callback?.invoke(latitud!!, longitud!!)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permisos concedidos, intenta obtener la ubicación de nuevo
                getLastLocation { lat, lon ->
                    // Maneja la ubicación aquí
                }
            } else {
                // Permisos denegados, maneja el caso aquí
                Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun showMessage (mostrarMsj:String){
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, mostrarMsj, duracion)
        toast.show()
    }
    fun verifyRegister(registro: LinearLayout,tipo: String, nombre: String, fase: String, cantidad: Int){
        if(registro.visibility == View.VISIBLE){
            dataSave.add(mutableListOf(IDcount.toString(), loggedUser, Agricultor, Granja, Lote, Cultivo, latitud.toString(), longitud.toString(),tipo , nombre, fase, cantidad.toString(), Fecha, Hora))
        }
    }
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    override fun onBackPressed() {
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, "No se puede volver en esta Pantalla.", duracion)
        toast.show()
    }

}