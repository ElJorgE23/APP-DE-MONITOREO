package com.example.apptest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
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


class pantalla_plagasagave : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var db: AppDatabase
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

    /* -------------------------------------------
    |   Aquí definimos las variables necesarias como privadas para poder usarlas en el contexto de las funciones globales
     -----------------------------------------------*/
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
    
    //Para las plagas
    private lateinit var botonGC: ImageButton; private lateinit var botonPA: ImageButton
    //...
    private lateinit var cGC: LinearLayout; private lateinit var cPA: LinearLayout
    //...
    private lateinit var registroGCh: LinearLayout ; private lateinit var registroGCn: LinearLayout ; private lateinit var registroGCa: LinearLayout
    private lateinit var registroPAh: LinearLayout ; private lateinit var registroPAn: LinearLayout ; private lateinit var registroPAa: LinearLayout
    //...
    private lateinit var gchplus: ImageButton; private lateinit var gcnplus: ImageButton; private lateinit var gcaplus: ImageButton; private lateinit var gchminus: ImageButton; private lateinit var gcnminus: ImageButton; private lateinit var gcaminus: ImageButton
    private lateinit var pahplus: ImageButton; private lateinit var panplus: ImageButton; private lateinit var paaplus: ImageButton; private lateinit var pahminus: ImageButton; private lateinit var panminus: ImageButton; private lateinit var paaminus: ImageButton
    //...
    private lateinit var tvhgc: TextView ; private lateinit var tvngc: TextView ; private lateinit var tvagc: TextView
    private lateinit var tvhpa: TextView ; private lateinit var tvnpa: TextView ; private lateinit var tvapa: TextView
    //...
    private lateinit var tvGCh: TextView ; private lateinit var tvGCn: TextView ; private lateinit var tvGCa: TextView
    private lateinit var tvPAh: TextView ; private lateinit var tvPAn: TextView ; private lateinit var tvPAa: TextView
    
    //Para las enfermedades
    private lateinit var botonPDC: ImageButton
    private lateinit var botonTP: ImageButton
    private lateinit var botonFUS: ImageButton
    //...
    private lateinit var cPDC: LinearLayout
    private lateinit var cTP: LinearLayout
    private lateinit var cFUS: LinearLayout
    //...
    private lateinit var registroPDC: LinearLayout
    private lateinit var registroTP: LinearLayout
    private lateinit var registroFUS: LinearLayout
    //...
    private lateinit var cbPudricioncogollo: CheckBox
    private lateinit var cbThielaviopsis: CheckBox
    private lateinit var cbFusarium: CheckBox
    //...
    private lateinit var tvPudricioncogollo: TextView
    private lateinit var tvThielaviopsis: TextView
    private lateinit var tvFusarium: TextView
    



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_plagasagave)
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
        /*------------------------------------------------------------
        |   Coincidimos las variables a usar con su respectivo id
         ------------------------------------------------------------*/
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
        
        //Para las Plagas
        botonGC = findViewById(R.id.boton_gallinaciega); botonPA = findViewById(R.id.boton_picudoagave)
        //...
        cGC = findViewById(R.id.CajaGC); cPA = findViewById(R.id.CajaPA)
        //...
        registroGCh = findViewById(R.id.gcregistroh); registroGCn = findViewById(R.id.gcregistrol); registroGCa = findViewById(R.id.gcregistroa)
        registroPAh = findViewById(R.id.paregistroh); registroPAn = findViewById(R.id.paregistrol); registroPAa = findViewById(R.id.paregistroa)
        //...
        pahplus = findViewById(R.id.ib_huevecillomasPA); panplus = findViewById(R.id.ib_ninfamasPA); paaplus = findViewById(R.id.ib_adultomasPA); pahminus = findViewById(R.id.ib_huevecillomenosPA); panminus = findViewById(R.id.ib_ninfamenosPA); paaminus = findViewById(R.id.ib_adultomenosPA)
        gchplus = findViewById(R.id.ib_huevecillomasGC); gcnplus = findViewById(R.id.ib_ninfamasGC); gcaplus = findViewById(R.id.ib_adultomasGC); gchminus = findViewById(R.id.ib_huevecillomenosGC); gcnminus = findViewById(R.id.ib_ninfamenosGC); gcaminus = findViewById(R.id.ib_adultomenosGC)
        //...
        tvhgc = findViewById(R.id.cantidad_hGC); tvngc = findViewById(R.id.cantidad_nGC); tvagc = findViewById(R.id.cantidad_aGC)
        tvhpa = findViewById(R.id.cantidad_hPA); tvnpa = findViewById(R.id.cantidad_nPA); tvapa = findViewById(R.id.cantidad_aPA)
        //...
        tvPAh = findViewById(R.id.tv_pah); tvPAn = findViewById(R.id.tv_pal); tvPAa = findViewById(R.id.tv_paa)
        tvGCh = findViewById(R.id.tv_gch); tvGCn = findViewById(R.id.tv_gcl); tvGCa = findViewById(R.id.tv_gca)
        
        //Para las enfermedades
        botonPDC = findViewById(R.id.boton_pudricioncogollo)
        botonTP= findViewById(R.id.boton_thielaviopsis)
        botonFUS = findViewById(R.id.boton_fusarium)
        //...
        cPDC = findViewById(R.id.CajaPDC)
        cTP = findViewById(R.id.CajaTP)
        cFUS = findViewById(R.id.CajaFUS)
        //...
        registroPDC = findViewById(R.id.pdcregistro)
        registroTP = findViewById(R.id.tpregistro)
        registroFUS = findViewById(R.id.fusregistro)
        //...
        cbPudricioncogollo = findViewById(R.id.cb_pudricioncogollo)
        cbThielaviopsis = findViewById(R.id.cb_thielaviopsis)
        cbFusarium = findViewById(R.id.cb_fusarium)
        //...
        tvPudricioncogollo = findViewById(R.id.tv_pdc)
        tvThielaviopsis = findViewById(R.id.tv_tp)
        tvFusarium = findViewById(R.id.tv_fus)
        
        //Agregamos las strings necesarias para registrar los nombres de las plagas
        val strplaGC = getString(R.string.plaga_gallinaciega)
        val strplaPA = getString(R.string.plaga_picudoagave)
        //Agregamos las strings que contienen las diferentes fases de las plagas
        val strfaseh = getString(R.string.fase_huevecillo)
        val strfasel = getString(R.string.fase_larva)
        val strfasea = getString(R.string.fase_adulto)
        val strfasena = getString(R.string.msj_NA)
        //Agregamos las strings para el nombre de las enfermedades
        val strenfPDC = getString(R.string.enfermedad_pudricioncogollo)
        val strenfTP = getString(R.string.enfermedad_thielaviopsis)
        val strenfFUS = getString(R.string.enfermedad_fusarium)

        //Definimos una lista que contenga a los botones que usaremos 
        val buttonsPlague: List<ImageButton> = listOf(botonGC, botonPA, botonPDC, botonTP, botonFUS)
        //Agregamos una lista con los LinearLayouts de las Plagas
        val registerPlague: List<LinearLayout> = listOf(cGC, cPA, cPDC, cTP, cFUS)
        


        /*-----------------------------------------------------------
        |   Implementamos los métodos y los eventos
         -----------------------------------------------------------*/
        //Actualizamos el TextView para que nos muestre el número de punto que estamos registrando
        idPlague.text = IDcount.toString()

        //Implementación de botón para cuando no se detecta ninguna plaga
        bNoPlagas.setOnClickListener {
            showAdvertence()
        }
        
        //Agregamos los eventos que activa el botón al ser pulsado
        bOtrasPlagas.setOnClickListener {
            if (current_icon == 0){
                bOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_hide, null, null, null)
                current_icon = 1
                llOtrasPlagas.visibility = View.VISIBLE
                tvPlagas.text = "Otras Plagas"
                llPlagasPrincipales.visibility = View.GONE
                (ContentButton.parent as? LinearLayout)?.removeView(ContentButton)
                ContentReciver.addView(ContentButton)
            }else{
                bOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_add, null, null, null)
                current_icon = 0
                llOtrasPlagas.visibility = View.GONE
                tvPlagas.text = "Plagas Principales"
                llPlagasPrincipales.visibility = View.VISIBLE
                ContentReciver.removeView(ContentButton)
                llPlagasPrincipales.addView(ContentButton, 0)
            }
        }
        //Definimos la funcionalidad para que al presionar el boton de una plaga se aparezca su registro
        buttonsPlague.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener { 
                registerPlague.forEachIndexed { indexLayout, linearLayout ->  
                    if(index == indexLayout){
                        linearLayout.visibility = View.VISIBLE
                    }else{
                        linearLayout.visibility = View.GONE   
                    }
                }
            }
        }
        
        //Funcionalidad de eventos para Plagas
        gchplus.setOnClickListener { countGCH = increase(countGCH, tvhgc, tvGCh, registroGCh) };  gcnplus.setOnClickListener { countGCN = increase(countGCN, tvngc, tvGCn, registroGCn) };  gcaplus.setOnClickListener { countGCA = increase(countGCA, tvagc, tvGCa, registroGCa) };  gchminus.setOnClickListener { countGCH = decrease(countGCH, tvhgc, tvGCh, registroGCh) };  gcnminus.setOnClickListener { countGCN = decrease(countGCN, tvngc, tvGCn, registroGCn) };  gcaminus.setOnClickListener { countGCA = decrease(countGCA, tvagc, tvGCa, registroGCa) }
        pahplus.setOnClickListener { countPAH = increase(countPAH, tvhpa, tvPAh, registroPAh) };  panplus.setOnClickListener { countPAN = increase(countPAN, tvnpa, tvPAn, registroPAn) };  paaplus.setOnClickListener { countPAA = increase(countPAA, tvapa, tvPAa, registroPAa) };  pahminus.setOnClickListener { countPAH = decrease(countPAH, tvhpa, tvPAh, registroPAh) };  panminus.setOnClickListener { countPAN = decrease(countPAN, tvnpa, tvPAn, registroPAn) };  paaminus.setOnClickListener { countPAA = decrease(countPAA, tvapa, tvPAa, registroPAa) }

        
        //Funcionalidad de eventos para Enfermedades
        cbPudricioncogollo.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                registroPDC.visibility = View.VISIBLE
                tvPudricioncogollo.text = "Sí"
            }else{
                registroPDC.visibility = View.GONE
                tvPudricioncogollo.text = ""
            }
        }
        cbThielaviopsis.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                registroTP.visibility = View.VISIBLE
                tvThielaviopsis.text = "Sí"
            }else{
                registroTP.visibility = View.GONE
                tvThielaviopsis.text = ""
            }
        }
        cbFusarium.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                registroFUS.visibility = View.VISIBLE
                tvFusarium.text = "Sí"
            }else{
                registroFUS.visibility = View.GONE
                tvFusarium.text = ""
            }
        }


        //Definimos que al pulsar el botón se abra la actividad de Mapa
        bEnviarRegistro.setOnClickListener {
            val registros = mutableListOf<MonitoreoEntity>()

            agregarRegistroSiVisible(registros, registroGCh, "Plaga", strplaGC, strfaseh, countGCH)
            agregarRegistroSiVisible(registros, registroGCn, "Plaga", strplaGC, strfasel, countGCN)
            agregarRegistroSiVisible(registros, registroGCa, "Plaga", strplaGC, strfasea, countGCA)

            agregarRegistroSiVisible(registros, registroPAh, "Plaga", strplaPA, strfaseh, countPAH)
            agregarRegistroSiVisible(registros, registroPAn, "Plaga", strplaPA, strfasel, countPAN)
            agregarRegistroSiVisible(registros, registroPAa, "Plaga", strplaPA, strfasea, countPAA)

            agregarRegistroSiVisible(registros, registroPDC, "Enfermedad", strenfPDC, strfasena, 1)
            agregarRegistroSiVisible(registros, registroTP, "Enfermedad", strenfTP, strfasena, 1)
            agregarRegistroSiVisible(registros, registroFUS, "Enfermedad", strenfFUS, strfasena, 1)

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
    /* -------------------------------------------------------
    |   Implementamos las funciones necesarias
     --------------------------------------------------------*/
    //Esta función nos permite direccionar al usuario a otra dirección
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

        val intent = Intent(this@pantalla_plagasagave, Pantalla_registromapa::class.java)
        intent.putExtra("ind_value", IDcount)
        intent.putExtra("cultivo", Cultivo)
        startActivity(intent)
        finish()
    }
    //Esta función nos mostrará una pantalla emergente en la que el usuario debe confirmar si verdaderamente no se registró plaga
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
    //Esta función nos permite mandar un mensaje al usuario
    fun showMessage (mostrarMsj:String){
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, mostrarMsj, duracion)
        toast.show()
    }
    //Esta Función nos ayuda a mandar una alerta al usuario cuando no se ha registrado nada
    fun verifyRegister(registro: LinearLayout,tipo: String, nombre: String, fase: String, cantidad: Int){
        if(registro.visibility == View.VISIBLE){
            dataSave.add(mutableListOf(IDcount.toString(), loggedUser, Agricultor, Granja, Lote, Cultivo, latitud.toString(), longitud.toString(),tipo , nombre, fase, cantidad.toString(), Fecha, Hora))
        }
    }
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
    override fun onBackPressed() {
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, "No se puede volver en esta Pantalla.", duracion)
        toast.show()
    }
}