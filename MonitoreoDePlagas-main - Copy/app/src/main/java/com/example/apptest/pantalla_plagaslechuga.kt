package com.example.apptest

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import android.os.ParcelFileDescriptor
import com.google.android.gms.location.Priority

import androidx.lifecycle.lifecycleScope
import com.example.apptest.data.AppDatabase
import com.example.apptest.data.MonitoreoEntity
import kotlinx.coroutines.launch
import java.util.UUID


class pantalla_plagaslechuga : AppCompatActivity() {
    /*----------------------------------------------------------------------------------------------------------------------
    |
    |       EN ESTA PARTE DE AQUÍ INICIAMOS TODAS LAS VARIABLES DE ESTA ACTIVITY QUE SERÁN USADAS MEDIANTE EL CÓDIGO
    |
     -----------------------------------------------------------------------------------------------------------------------*/
    /* ----------------------------------------------------------------------------------------------
    |   DEFINIMOS LAS VARIABLES QUE SERÁN NECESARIAS PARA PODER OBTENER LA UBICACION DEL DISPOSITIVO
     ------------------------------------------------------------------------------------------------*/
    //Creamos variable para que pueda ser leída la ubicación del dispositivo
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    //Declaramos las variables para pedir la latitud y la longitud del dispositivo
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    //Declaramos variables para obtener ubicación del dispositivo en cierto intervalo de tiempo
/*    private val locationRequest = LocationRequest.create().apply {
        interval = 5000 // Intervalo de actualización en milisegundos (por ejemplo, cada 5 segundos)
        fastestInterval = 3000 // Intervalo de actualización más rápido en milisegundos (por ejemplo, cada 1 segundo)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Prioridad de la solicitud de ubicación
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Actualiza la ubicación actual
                val currentLocation = LatLng(location.latitude, location.longitude)
                // Realiza acciones con la nueva ubicación
            }
        }
    }*/
    /* ----------------------------------------------------------------------------------------------
    |   DEFINIMOS LAS VARIABLES QUE SERÁ NECESARIA PAA OBTENER LA FECHA Y HORA DEL DISPOSITIVO
     ------------------------------------------------------------------------------------------------*/
    //Declaramos variable para tener la fecha y hora del dispositivo
    private lateinit var FyH: String
    private lateinit var Fecha: String
    private lateinit var Hora: String
    private lateinit var db: AppDatabase
    /*-----------------------------------------------------------------------------
    |   DEFINIMOS VARIABLE PARA GUARDAR ARCHIVO
     -----------------------------------------------------------------------------*/
    private var saveFileLauncher: ActivityResultLauncher<Intent>? = null

    init {
        setupSaveFileLauncher()
    }

    /* ----------------------------------------------------------------------------------------------
    |   DEFINIMOS LAS VARIABLES QUE SERÁN NECESARIAS PARA MODIFICAR LOS EVENTOS DE LA PANTALLA
     ------------------------------------------------------------------------------------------------*/
    //Declaramos el Text View para mostrar el ID de Punto
    private lateinit var idPlague: TextView
    //Definimos los Images Buttons para activar el registro para cada plaga
    private lateinit var botonDD: ImageButton ; private lateinit var botonFM: ImageButton ; private lateinit var botonCHI: ImageButton ; private lateinit var botonC: ImageButton ; private lateinit var botonD: ImageButton ; private lateinit var botonT: ImageButton ; private lateinit var botonCN: ImageButton ; private lateinit var botonCOC: ImageButton ; private lateinit var botonMB: ImageButton ; private lateinit var botonHZ: ImageButton ; private lateinit var botonM: ImageButton ; private lateinit var botonTI: ImageButton
    //Definimos los loayoyuts donde se presentan los botones para agregar ejemplares de cada plaga
    private lateinit var cDD: LinearLayout; private lateinit var cFM: LinearLayout; private lateinit var cCHI: LinearLayout; private lateinit var cC: LinearLayout; private lateinit var cD: LinearLayout; private lateinit var cT: LinearLayout; private lateinit var cCN: LinearLayout; private lateinit var cCOC: LinearLayout; private lateinit var cMB: LinearLayout; private lateinit var cHZ: LinearLayout; private lateinit var cM: LinearLayout; private lateinit var cTI: LinearLayout
    //Definimos los registros para hacerlos visibles cuando las plagas esten siendo registradas
    private lateinit var registroDDh: LinearLayout; private lateinit var registroDDc: LinearLayout; private lateinit var registroDDm: LinearLayout; private lateinit var registroDDg: LinearLayout; private lateinit var registroDDp: LinearLayout
    private lateinit var registroFMh: LinearLayout; private lateinit var registroFMc: LinearLayout; private lateinit var registroFMm: LinearLayout; private lateinit var registroFMg: LinearLayout; private lateinit var registroFMp: LinearLayout
    private lateinit var registroDh: LinearLayout ; private lateinit var registroDc: LinearLayout ; private lateinit var registroDm: LinearLayout ; private lateinit var registroDg: LinearLayout ; private lateinit var registroDp: LinearLayout
    private lateinit var registroHZh: LinearLayout; private lateinit var registroHZc: LinearLayout; private lateinit var registroHZm: LinearLayout; private lateinit var registroHZg: LinearLayout; private lateinit var registroHZp: LinearLayout
    private lateinit var registroCNc: LinearLayout; private lateinit var registroCNm: LinearLayout ; private lateinit var registroCNg: LinearLayout
    private lateinit var registroCc: LinearLayout ; private lateinit var registroCm: LinearLayout ; private lateinit var registroCg: LinearLayout
    private lateinit var registroCHIh: LinearLayout;private lateinit var registroCHIn: LinearLayout;private lateinit var registroCHIa: LinearLayout
    private lateinit var registroTh: LinearLayout; private lateinit var registroTn: LinearLayout; private lateinit var registroTa: LinearLayout
    private lateinit var registroMBh: LinearLayout; private lateinit var registroMBn: LinearLayout; private lateinit var registroMBa: LinearLayout
    private lateinit var registroCOCh: LinearLayout; private lateinit var registroCOCl: LinearLayout; private lateinit var registroCOCa: LinearLayout
    private lateinit var registroTI: LinearLayout
    private lateinit var registroM: LinearLayout
    //Definimos el Boton para cambiar a Otras Plagas
    private lateinit var botonOtrasPlagas: Button
    //Definimos las cajas para Plagas principales
    private lateinit var cajaPlagasPrincipales: LinearLayout
    //Definimos la caja para Otras Plagas
    private lateinit var  cajaOtrasPlagas: LinearLayout
    //Agregamos el TextView para que se cambie el mensaje segun se registren plagas principales u otras plagas
    private lateinit var msjPlagas: TextView
    //Agregamos los botones para aumentar conteo de plagas 
    private lateinit var ddhplus: ImageButton ; private lateinit var ddcplus: ImageButton ; private lateinit var ddmplus: ImageButton ; private lateinit var ddgplus: ImageButton ; private lateinit var ddpplus: ImageButton
    private lateinit var fmhplus: ImageButton ; private lateinit var fmcplus: ImageButton ; private lateinit var fmmplus: ImageButton ; private lateinit var fmgplus: ImageButton ; private lateinit var fmpplus: ImageButton
    private lateinit var hzhplus: ImageButton ; private lateinit var hzcplus: ImageButton ; private lateinit var hzmplus: ImageButton ; private lateinit var hzgplus: ImageButton ; private lateinit var hzpplus: ImageButton
    private lateinit var dhplus: ImageButton ; private lateinit var dcplus: ImageButton ; private lateinit var dmplus: ImageButton ; private lateinit var dgplus: ImageButton ; private lateinit var dpplus: ImageButton
    private lateinit var cncplus: ImageButton ; private lateinit var cnmplus: ImageButton ; private lateinit var cngplus: ImageButton
    private lateinit var ccplus: ImageButton ; private lateinit var cmplus: ImageButton ; private lateinit var cgplus: ImageButton
    private lateinit var chihplus: ImageButton ; private lateinit var chinplus: ImageButton ; private lateinit var chiaplus: ImageButton
    private lateinit var thplus: ImageButton ; private lateinit var tnplus: ImageButton ; private lateinit var taplus: ImageButton
    private lateinit var mbhplus: ImageButton ; private lateinit var mbnplus: ImageButton ; private lateinit var mbaplus: ImageButton
    private lateinit var cochplus: ImageButton ; private lateinit var coclplus: ImageButton ; private lateinit var cocaplus: ImageButton
    //Declaramos los botones para disminuir la cantidad de plaga
    private lateinit var ddhminus: ImageButton ; private lateinit var ddcminus: ImageButton ; private lateinit var ddmminus: ImageButton ; private lateinit var ddgminus: ImageButton ; private lateinit var ddpminus: ImageButton
    private lateinit var fmhminus: ImageButton ; private lateinit var fmcminus: ImageButton ; private lateinit var fmmminus: ImageButton ; private lateinit var fmgminus: ImageButton ; private lateinit var fmpminus: ImageButton
    private lateinit var hzhminus: ImageButton ; private lateinit var hzcminus: ImageButton ; private lateinit var hzmminus: ImageButton ; private lateinit var hzgminus: ImageButton ; private lateinit var hzpminus: ImageButton
    private lateinit var dhminus: ImageButton ; private lateinit var dcminus: ImageButton ; private lateinit var dmminus: ImageButton ; private lateinit var dgminus: ImageButton ; private lateinit var dpminus: ImageButton
    private lateinit var cncminus: ImageButton ; private lateinit var cnmminus: ImageButton ; private lateinit var cngminus: ImageButton
    private lateinit var ccminus: ImageButton; private lateinit var cmminus: ImageButton; private lateinit var cgminus: ImageButton
    private lateinit var chihminus: ImageButton ; private lateinit var chinminus: ImageButton ; private lateinit var chiaminus: ImageButton
    private lateinit var thminus: ImageButton ; private lateinit var tnminus: ImageButton ; private lateinit var taminus: ImageButton
    private lateinit var mbhminus: ImageButton ; private lateinit var mbnminus: ImageButton ; private lateinit var mbaminus: ImageButton
    private lateinit var cochminus: ImageButton ; private lateinit var coclminus: ImageButton ; private lateinit var cocaminus: ImageButton
    //Declaramos los TextViews para ir contando las plagas que se están agregando
    private lateinit var tvhdd: TextView ; private lateinit var tvcdd: TextView ; private lateinit var tvmdd: TextView ; private lateinit var tvgdd: TextView ; private lateinit var tvpdd: TextView
    private lateinit var tvhfm: TextView ; private lateinit var tvcfm: TextView ; private lateinit var tvmfm: TextView ; private lateinit var tvgfm: TextView ; private lateinit var tvpfm: TextView
    private lateinit var tvhhz: TextView ; private lateinit var tvchz: TextView ; private lateinit var tvmhz: TextView ; private lateinit var tvghz: TextView ; private lateinit var tvphz: TextView
    private lateinit var tvhd: TextView ; private lateinit var tvcd: TextView ; private lateinit var tvmd: TextView ; private lateinit var tvgd: TextView ; private lateinit var tvpd: TextView
    private lateinit var tvccn: TextView ; private lateinit var tvmcn: TextView ; private lateinit var tvgcn: TextView
    private lateinit var tvcc: TextView ; private lateinit var tvmc: TextView ; private lateinit var tvgc: TextView
    private lateinit var tvhchi: TextView ; private lateinit var tvnchi: TextView ; private lateinit var tvachi: TextView
    private lateinit var tvht: TextView ; private lateinit var tvnt: TextView ; private lateinit var tvat: TextView
    private lateinit var tvhmb: TextView ; private lateinit var tvnmb: TextView ; private lateinit var tvamb: TextView
    private lateinit var tvhcoc: TextView ; private lateinit var tvlcoc: TextView ; private lateinit var tvacoc: TextView
    //Declaramos los TextViews para el registro
    private lateinit var tvDDh: TextView ; private lateinit var tvDDc: TextView ; private lateinit var tvDDm: TextView ; private lateinit var tvDDg: TextView ; private lateinit var tvDDp: TextView
    private lateinit var tvFMh: TextView ; private lateinit var tvFMc: TextView ; private lateinit var tvFMm: TextView ; private lateinit var tvFMg: TextView ; private lateinit var tvFMp: TextView
    private lateinit var tvHZh: TextView ; private lateinit var tvHZc: TextView ; private lateinit var tvHZm: TextView ; private lateinit var tvHZg: TextView ; private lateinit var tvHZp: TextView
    private lateinit var tvDh: TextView ; private lateinit var tvDc: TextView ; private lateinit var tvDm: TextView ; private lateinit var tvDg: TextView ; private lateinit var tvDp: TextView
    private lateinit var tvCNc: TextView ; private lateinit var tvCNm: TextView ; private lateinit var tvCNg: TextView
    private lateinit var tvCc: TextView ; private lateinit var tvCm: TextView ; private lateinit var tvCg: TextView
    private lateinit var tvCHIh: TextView ; private lateinit var tvCHIn: TextView ; private lateinit var tvCHIa: TextView
    private lateinit var tvTh: TextView ; private lateinit var tvTn: TextView ; private lateinit var tvTa: TextView
    private lateinit var tvMBh: TextView ; private lateinit var tvMBn: TextView ; private lateinit var tvMBa: TextView
    private lateinit var tvCOCh: TextView ; private lateinit var tvCOCl: TextView ; private lateinit var tvCOCa: TextView
    //Iniciamos el boton para registrar cuando no se registre ninguna plaga
    private lateinit var bNoPlaga: ImageButton
    //Añadimos el boton para enviar el registro del monitoreo
    private lateinit var botonEnvioRegistro: Button
    //Declaramos las checkbox que nos ayudarán a registrar si hay o no presencia de plagas en los cultivos
    private lateinit var cbMinador: CheckBox
    private lateinit var cbTizon: CheckBox
    //Declaramos los textViews para mostra que estamos regisrtrando enfermedades en una planta
    private lateinit var tvMinador: TextView
    private lateinit var tvTizon: TextView

    private lateinit var ContentButton: LinearLayout
    private lateinit var ContentReciver: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_plagaslechuga)
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
        /* ----------------------------------------------------------------------------------------------------------------------------------------------------
        |
        |       ESTA ES LA PARTE DEL CÓDIGO DONDE INICIALIZAMOS LAS VARIABLE DE ACUERDO A SU ID PREVIAMENTE DEFINIDO EN SU ARCHIVO XML CORRESPONDIENTE
        |
        --------------------------------------------------------------------------------------------------------------------------------------------------------*/
        //Obtenemos la fecha y hora del dispositivo y la guardamos en una variable
        FyH = obtenerFechaYHora()
        Fecha = getFecha()
        Hora = getHora()
        //Colocamos el valor de el ID de punto
        idPlague = findViewById(R.id.tv_idPlagas)
        idPlague.text = IDcount.toString()
        //Cooincidimos el ID de los botones con la variable que les corresponde
        botonDD = findViewById(R.id.boton_dorsodiamante); botonFM = findViewById(R.id.boton_falsomedidor); botonCHI = findViewById(R.id.boton_chicharrita); botonC = findViewById(R.id.boton_copitarsia); botonD = findViewById(R.id.boton_diabrotica); botonT = findViewById(R.id.boton_trips); botonCN = findViewById(R.id.boton_chinchenysus); botonCOC = findViewById(R.id.boton_coccinellidae); botonMB = findViewById(R.id.boton_mosquitablanca); botonHZ = findViewById(R.id.boton_helicoverpa); botonM = findViewById(R.id.boton_minador); botonTI = findViewById(R.id.boton_tizon)
        //Coincidimos los layouts con los id´s que les correspondan
        cDD = findViewById(R.id.CajaDD); cFM = findViewById(R.id.CajaFM); cCHI = findViewById(R.id.CajaCHI); cC = findViewById(R.id.CajaC); cD = findViewById(R.id.CajaD); cT = findViewById(R.id.CajaT); cCN = findViewById(R.id.CajaCN); cCOC = findViewById(R.id.CajaCOC); cMB = findViewById(R.id.CajaMB); cHZ = findViewById(R.id.CajaHZ); cTI = findViewById(R.id.CajaTI); cM = findViewById(R.id.CajaM)
        //Coincidimos los layouts de registro con los id´s que les corresponden
        registroDDh = findViewById(R.id.ddregistroh); registroDDc = findViewById(R.id.ddregistroc); registroDDm = findViewById(R.id.ddregistrom); registroDDg = findViewById(R.id.ddregistrog); registroDDp = findViewById(R.id.ddregistrop)
        registroFMh = findViewById(R.id.fmregistroh); registroFMc = findViewById(R.id.fmregistroc); registroFMm = findViewById(R.id.fmregistrom); registroFMg = findViewById(R.id.fmregistrog); registroFMp = findViewById(R.id.fmregistrop)
        registroDh = findViewById(R.id.dregistroh); registroDc = findViewById(R.id.dregistroc); registroDm = findViewById(R.id.dregistrom); registroDg = findViewById(R.id.dregistrog); registroDp = findViewById(R.id.dregistrop)
        registroHZh = findViewById(R.id.hzregistroh); registroHZc = findViewById(R.id.hzregistroc); registroHZm = findViewById(R.id.hzregistrom); registroHZg = findViewById(R.id.hzregistrog); registroHZp = findViewById(R.id.hzregistrop)
        registroCc = findViewById(R.id.cregistroc); registroCm = findViewById(R.id.cregistrom); registroCg = findViewById(R.id.cregistrog)
        registroCNc = findViewById(R.id.cnregistroc); registroCNm = findViewById(R.id.cnregistrom); registroCNg = findViewById(R.id.cnregistrog)
        registroCHIh = findViewById(R.id.chiregistroh); registroCHIn = findViewById(R.id.chiregistron); registroCHIa = findViewById(R.id.chiregistroa)
        registroTh = findViewById(R.id.tregistroh); registroTn = findViewById(R.id.tregistron); registroTa = findViewById(R.id.tregistroa)
        registroMBh = findViewById(R.id.mbregistroh); registroMBn = findViewById(R.id.mbregistron); registroMBa = findViewById(R.id.mbregistroa)
        registroCOCh = findViewById(R.id.cocregistroh); registroCOCl = findViewById(R.id.cocregistron); registroCOCa = findViewById(R.id.cocregistroa)
        registroM = findViewById(R.id.mregistro)
        registroTI = findViewById(R.id.tiregistro)
        //Coincidimos el ID del button para registrar Otras plagas
        botonOtrasPlagas = findViewById(R.id.boton_otrasplagaslechuga)
        //Coincidimos el id del TextView
        msjPlagas = findViewById(R.id.tvPlagas)
        cajaOtrasPlagas = findViewById(R.id.Caja_otrasplagaslechuga)
        cajaPlagasPrincipales = findViewById(R.id.Caja_plagasprincipaleslechuga)
        //Coincidimos el boton para agregar con su respectivo ID
        ddhplus = findViewById(R.id.ib_huevecillomasDD); ddcplus = findViewById(R.id.ib_chicomasDD); ddmplus = findViewById(R.id.ib_medianomasDD); ddgplus = findViewById(R.id.ib_grandemasDD); ddpplus = findViewById(R.id.ib_pupamasDD)
        fmhplus = findViewById(R.id.ib_huevecillomasFM); fmcplus = findViewById(R.id.ib_chicomasFM); fmmplus = findViewById(R.id.ib_medianomasFM); fmgplus = findViewById(R.id.ib_grandemasFM); fmpplus = findViewById(R.id.ib_pupamasFM)
        dhplus = findViewById(R.id.ib_huevecillomasD); dcplus = findViewById(R.id.ib_chicomasD); dmplus = findViewById(R.id.ib_medianomasD); dgplus = findViewById(R.id.ib_grandemasD); dpplus = findViewById(R.id.ib_pupamasD)
        hzhplus = findViewById(R.id.ib_huevecillomasHZ); hzcplus = findViewById(R.id.ib_chicomasHZ); hzmplus = findViewById(R.id.ib_medianomasHZ); hzgplus = findViewById(R.id.ib_grandemasHZ); hzpplus = findViewById(R.id.ib_pupamasHZ)
        ccplus = findViewById(R.id.ib_chicomasC); cmplus = findViewById(R.id.ib_medianomasC); cgplus = findViewById(R.id.ib_grandemasC)
        cncplus = findViewById(R.id.ib_chicomasCN); cnmplus = findViewById(R.id.ib_medianomasCN); cngplus = findViewById(R.id.ib_grandemasCN)
        chihplus = findViewById(R.id.ib_huevecillomasCHI); chinplus = findViewById(R.id.ib_ninfamasCHI); chiaplus = findViewById(R.id.ib_adultomasCHI)
        thplus = findViewById(R.id.ib_huevecillomasT); tnplus = findViewById(R.id.ib_ninfamasT); taplus = findViewById(R.id.ib_adultomasT)
        mbhplus = findViewById(R.id.ib_huevecillomasMB); mbnplus = findViewById(R.id.ib_ninfamasMB); mbaplus = findViewById(R.id.ib_adultomasMB)
        cochplus = findViewById(R.id.ib_huevecillomasCOC); coclplus = findViewById(R.id.ib_ninfamasCOC); cocaplus = findViewById(R.id.ib_adultomasCOC)
        //Coincidimos el boton para quitar con su respecivo ID
        ddhminus = findViewById(R.id.ib_huevecillomenosDD); ddcminus = findViewById(R.id.ib_chicomenosDD); ddmminus = findViewById(R.id.ib_medianomenosDD); ddgminus = findViewById(R.id.ib_grandemenosDD); ddpminus = findViewById(R.id.ib_pupamenosDD)
        fmhminus = findViewById(R.id.ib_huevecillomenosFM); fmcminus = findViewById(R.id.ib_chicomenosFM); fmmminus = findViewById(R.id.ib_medianomenosFM); fmgminus = findViewById(R.id.ib_grandemenosFM); fmpminus = findViewById(R.id.ib_pupamenosFM)
        dhminus = findViewById(R.id.ib_huevecillomenosD); dcminus = findViewById(R.id.ib_chicomenosD); dmminus = findViewById(R.id.ib_medianomenosD); dgminus = findViewById(R.id.ib_grandemenosD); dpminus = findViewById(R.id.ib_pupamenosD)
        hzhminus = findViewById(R.id.ib_huevecillomenosHZ); hzcminus = findViewById(R.id.ib_chicomenosHZ); hzmminus = findViewById(R.id.ib_medianomenosHZ); hzgminus = findViewById(R.id.ib_grandemenosHZ); hzpminus = findViewById(R.id.ib_pupamenosHZ)
        ccminus = findViewById(R.id.ib_chicomenosC); cmminus = findViewById(R.id.ib_medianomenosC); cgminus = findViewById(R.id.ib_grandemenosC)
        cncminus = findViewById(R.id.ib_chicomenosCN); cnmminus = findViewById(R.id.ib_medianomenosCN); cngminus = findViewById(R.id.ib_grandemenosCN)
        chihminus = findViewById(R.id.ib_huevecillomenosCHI); chinminus = findViewById(R.id.ib_ninfamenosCHI); chiaminus = findViewById(R.id.ib_adultomenosCHI)
        thminus = findViewById(R.id.ib_huevecillomenosT); tnminus = findViewById(R.id.ib_ninfamenosT); taminus = findViewById(R.id.ib_adultomenosT)
        mbhminus = findViewById(R.id.ib_huevecillomenosMB); mbnminus = findViewById(R.id.ib_ninfamenosMB); mbaminus = findViewById(R.id.ib_adultomenosMB)
        cochminus = findViewById(R.id.ib_huevecillomenosCOC); coclminus = findViewById(R.id.ib_ninfamenosCOC); cocaminus = findViewById(R.id.ib_adultomenosCOC)
        //Coincidimos los textViews de sus fases con su respectivo ID
        tvhdd = findViewById(R.id.cantidad_hDD); tvcdd = findViewById(R.id.cantidad_cDD); tvmdd = findViewById(R.id.cantidad_mDD); tvgdd = findViewById(R.id.cantidad_gDD); tvpdd = findViewById(R.id.cantidad_pDD)
        tvhfm = findViewById(R.id.cantidad_hFM); tvcfm = findViewById(R.id.cantidad_cFM); tvmfm = findViewById(R.id.cantidad_mFM); tvgfm = findViewById(R.id.cantidad_gFM); tvpfm = findViewById(R.id.cantidad_pFM)
        tvhd = findViewById(R.id.cantidad_hD); tvcd = findViewById(R.id.cantidad_chD); tvmd = findViewById(R.id.cantidad_mD); tvgd = findViewById(R.id.cantidad_gD); tvpd = findViewById(R.id.cantidad_pD)
        tvhhz = findViewById(R.id.cantidad_hHZ); tvchz = findViewById(R.id.cantidad_cHZ); tvmhz = findViewById(R.id.cantidad_mHZ); tvghz = findViewById(R.id.cantidad_gHZ); tvphz = findViewById(R.id.cantidad_pHZ)
        tvcc = findViewById(R.id.cantidad_cC); tvmc = findViewById(R.id.cantidad_mC); tvgc = findViewById(R.id.cantidad_gC)
        tvccn = findViewById(R.id.cantidad_cCN); tvmcn = findViewById(R.id.cantidad_mCN); tvgcn = findViewById(R.id.cantidad_gCN)
        tvhchi = findViewById(R.id.cantidad_hCHI); tvnchi = findViewById(R.id.cantidad_nCHI); tvachi = findViewById(R.id.cantidad_aCHI)
        tvht = findViewById(R.id.cantidad_hT); tvnt = findViewById(R.id.cantidad_nT); tvat = findViewById(R.id.cantidad_aT)
        tvhmb = findViewById(R.id.cantidad_hMB); tvnmb = findViewById(R.id.cantidad_nMB); tvamb = findViewById(R.id.cantidad_aMB)
        tvhcoc = findViewById(R.id.cantidad_hCOC); tvlcoc = findViewById(R.id.cantidad_nCOC); tvacoc = findViewById(R.id.cantidad_aCOC)
        //Coincidimos los textViews de registro con sus respectivos ID´s
        tvDDh= findViewById(R.id.tv_ddh) ;  tvDDc= findViewById(R.id.tv_ddc) ;  tvDDm= findViewById(R.id.tv_ddm) ;  tvDDg= findViewById(R.id.tv_ddg) ;  tvDDp= findViewById(R.id.tv_ddp)
        tvFMh= findViewById(R.id.tv_fmh) ;  tvFMc= findViewById(R.id.tv_fmc) ;  tvFMm= findViewById(R.id.tv_fmm) ;  tvFMg= findViewById(R.id.tv_fmg) ;  tvFMp= findViewById(R.id.tv_fmp)
        tvDh= findViewById(R.id.tv_dh) ;  tvDc= findViewById(R.id.tv_dc) ;  tvDm= findViewById(R.id.tv_dm) ;  tvDg= findViewById(R.id.tv_dg) ;  tvDp= findViewById(R.id.tv_dp)
        tvHZh= findViewById(R.id.tv_hzh) ;  tvHZc= findViewById(R.id.tv_hzc) ;  tvHZm= findViewById(R.id.tv_hzm) ;  tvHZg= findViewById(R.id.tv_hzg) ;  tvHZp= findViewById(R.id.tv_hzp)
        tvCNc = findViewById(R.id.tv_cnc); tvCNm = findViewById(R.id.tv_cnm); tvCNg = findViewById(R.id.tv_cng)
        tvCc = findViewById(R.id.tv_cc); tvCm = findViewById(R.id.tv_cm); tvCg = findViewById(R.id.tv_cg)
        tvCHIh = findViewById(R.id.tv_chih); tvCHIn = findViewById(R.id.tv_chin); tvCHIa = findViewById(R.id.tv_chia)
        tvTh = findViewById(R.id.tv_th); tvTn = findViewById(R.id.tv_tn); tvTa = findViewById(R.id.tv_ta)
        tvMBh = findViewById(R.id.tv_mbh); tvMBn = findViewById(R.id.tv_mbn); tvMBa = findViewById(R.id.tv_mba)
        tvCOCh = findViewById(R.id.tv_coch); tvCOCl = findViewById(R.id.tv_cocn); tvCOCa = findViewById(R.id.tv_coca)
        //Concidimos el boton para no registrar plaga con su ID
        bNoPlaga = findViewById(R.id.boton_noplaga)
        //Coincidimos el boton para registrar plagas
        botonEnvioRegistro = findViewById(R.id.b_EnviarPlagas)
        //Coincidimos los checkbox con sus ID´s

        /*------------------------------------------------------------
        |
        |   Declaramos algunas StrinHZ que serán de utilidad para mostrar la información en pantalla
        |
         ------------------------------------------------------------*/
        //Definimos las StrinHZ que contiene el nombre de las plagas
        val stringplagadd = getString(R.string.plaga_dorsodiamante)
        val stringplagafm = getString(R.string.plaga_falsomedidor)
        val stringplagacn = getString(R.string.plaga_chinchenysius)
        val stringplagac = getString(R.string.plaga_copitarsia)
        val stringplagad = getString(R.string.plaga_diabrotica)
        val stringplagachi = getString(R.string.plaga_chicharrita)
        val stringplagat = getString(R.string.plaga_trips)
        val stringplagacoc = getString(R.string.plaga_coccinellidae)
        val stringplagamb = getString(R.string.plaga_mosquitablanca)
        val stringplagahz = getString(R.string.plaga_helicoverpa)
        val stringplagam = getString(R.string.enfermedad_minador)
        val stringplagati = getString(R.string.enfermedad_tizon)
        val strinHZinplaga = getString(R.string.msj_noplaga)
        //Definimos las StrinHZ que contienen el nombre de las fases de todas las plagas
        val stringfaseh = getString(R.string.fase_huevecillo)
        val stringfasec = getString(R.string.fase_chico)
        val stringfasem = getString(R.string.fase_mediano)
        val stringfaseg = getString(R.string.fase_grande)
        val stringfasep = getString(R.string.fase_pupa)
        val stringfasen = getString(R.string.fase_ninfa)
        val stringfasel = getString(R.string.fase_larva)
        val stringfasea = getString(R.string.fase_adulto)
        val stringfasena = getString(R.string.msj_NA)
        //Definimos las StrinHZ que cobntienen el nombre de las Fases de las diferentes plagas
        cbMinador = findViewById(R.id.cb_minador)
        cbTizon = findViewById(R.id.cb_tizon)
        //Definimos los textviews por medio  de su id
        tvMinador = findViewById(R.id.tv_m)
        tvTizon = findViewById(R.id.tv_ti)
        /* -------------------------------------------------------------------------------------------------------------------------------------------------------
        |
        |       EN ESTA PARTE DE AQUÍ IMPLEMENTAMOS EL FUNCIONAMIENTO DE LOS BOTONES QUE SE USAN EN LA APLICACIÓN
        |
         ------------------------------------------------------------------------------------------------------------------------------------------------------- */
        //Agregamos un array que contenga los Image Buttons que se utilizan en esta activity
        val buttonsPlague: List<ImageButton> = listOf(botonDD, botonFM, botonD, botonHZ, botonC, botonCN, botonCHI, botonT, botonMB, botonCOC, botonM, botonTI)
        //Agregamos un array que contenga las cajas con los botones donde se registra cada plaga
        val registersPlague: List<LinearLayout> = listOf(cDD, cFM, cD, cHZ, cC, cCN, cCHI, cT, cMB, cCOC, cM, cTI)
        //Agregamos el funcionamiento para cada boton para registrar las plagas
        buttonsPlague.forEachIndexed { index, button ->
            button.setOnClickListener {
                registersPlague.forEachIndexed { layoutIndex, layout ->
                    if (index == layoutIndex) {
                        layout.visibility = View.VISIBLE //En esta parte se habilita la caja correspondiente a cada boton, si el ID del boton corresponde con el de la caja se muestra visible
                    } else {
                        layout.visibility = View.GONE //Cuando el id de la caja no coincide con el del boton no se muestra la caja de registro
                    }
                }
            }
        }
        ContentButton = findViewById(R.id.ContenedorBoton)
        ContentReciver = findViewById(R.id.ContenedorReciver)
        //Definir para poder cambiar el ícono
        var current_icon: Int = 0
        //Definimos los iconos los cuales se van a intercambiar para mostrar alguna u otra pantalla
        val ic_add =  resources.getDrawable(R.drawable.ic_add); val ic_hide = resources.getDrawable(R.drawable.ic_hide)
        //Agregamos el funcionamiento para el boton de otras plagas
        botonOtrasPlagas.setOnClickListener {
            if (current_icon == 0) {
                botonOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_hide, null, null, null)
                current_icon = 1
                cajaOtrasPlagas.visibility = View.VISIBLE
                msjPlagas.text = "Ocultar otras plagas"
                cajaPlagasPrincipales.visibility = View.GONE
                (ContentButton.parent as? LinearLayout)?.removeView(ContentButton)
                ContentReciver.addView(ContentButton)
            } else {
                botonOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_add, null, null, null)
                current_icon = 0
                cajaOtrasPlagas.visibility = View.GONE
                msjPlagas.text = "Ver Otras Plagas"
                cajaPlagasPrincipales.visibility = View.VISIBLE
                ContentReciver.removeView(ContentButton)

                // Agrega ContentButton de nuevo en su posición original dentro de cajaPlagasPrincipales
                cajaPlagasPrincipales.addView(ContentButton, 0)
            }
        }
        /* ------------------------------------------------------------
        |
        |   IMPLEMENTAMOS LA FUNCIÓN DE LOS BOTONES PARA AGREGAR O QUITAR PLAGAS
        |
         ------------------------------------------------------------  */
        //Para Dorso de Diamante
        ddhplus.setOnClickListener { countDDH = increase(countDDH, tvhdd, tvDDh, registroDDh) }; ddcplus.setOnClickListener { countDDC = increase(countDDC, tvcdd, tvDDc, registroDDc) }; ddmplus.setOnClickListener { countDDM = increase(countDDM, tvmdd, tvDDm, registroDDm) }; ddgplus.setOnClickListener { countDDG = increase(countDDG, tvgdd, tvDDg, registroDDg) }; ddpplus.setOnClickListener { countDDP = increase(countDDP, tvpdd, tvDDp, registroDDp) }; ddhminus.setOnClickListener { countDDH = decrease(countDDH, tvhdd, tvDDh, registroDDh) }; ddcminus.setOnClickListener { countDDC = decrease(countDDC, tvcdd, tvDDc, registroDDc) }; ddmminus.setOnClickListener { countDDM = decrease(countDDM, tvmdd, tvDDm, registroDDm) }; ddgminus.setOnClickListener { countDDG = decrease(countDDG, tvgdd, tvDDg, registroDDg) }; ddpminus.setOnClickListener { countDDP = decrease(countDDP, tvpdd, tvDDp, registroDDp) }
        //Para Falso Medidor
        fmhplus.setOnClickListener { countFMH = increase(countFMH, tvhfm, tvFMh, registroFMh) }; fmcplus.setOnClickListener { countFMC = increase(countFMC, tvcfm, tvFMc, registroFMc) }; fmmplus.setOnClickListener { countFMM = increase(countFMM, tvmfm, tvFMm, registroFMm) }; fmgplus.setOnClickListener { countFMG = increase(countFMG, tvgfm, tvFMg, registroFMg) }; fmpplus.setOnClickListener { countFMP = increase(countFMP, tvpfm, tvFMp, registroFMp) }; fmhminus.setOnClickListener { countFMH = decrease(countFMH, tvhfm, tvFMh, registroFMh) }; fmcminus.setOnClickListener { countFMC = decrease(countFMC, tvcfm, tvFMc, registroFMc) }; fmmminus.setOnClickListener { countFMM = decrease(countFMM, tvmfm, tvFMm, registroFMm) }; fmgminus.setOnClickListener { countFMG = decrease(countFMG, tvgfm, tvFMg, registroFMg) }; fmpminus.setOnClickListener { countFMP = decrease(countFMP, tvpfm, tvFMp, registroFMp) }
        //Para Diabrotica
        dhplus.setOnClickListener { countDH = increase(countDH, tvhd, tvDh, registroDh) }; dcplus.setOnClickListener { countDC = increase(countDC, tvcd, tvDc, registroDc) }; dmplus.setOnClickListener { countDM = increase(countDM, tvmd, tvDm, registroDm) }; dgplus.setOnClickListener { countDG = increase(countDG, tvgd, tvDg, registroDg) }; dpplus.setOnClickListener { countDP = increase(countDP, tvpd, tvDp, registroDp) }; dhminus.setOnClickListener { countDH = decrease(countDH, tvhd, tvDh, registroDh) }; dcminus.setOnClickListener { countDC = decrease(countDC, tvcd, tvDc, registroDc) }; dmminus.setOnClickListener { countDM = decrease(countDM, tvmd, tvDm, registroDm) }; dgminus.setOnClickListener { countDG = decrease(countDG, tvgd, tvDg, registroDg) }; dpminus.setOnClickListener { countDP = decrease(countDP, tvpd, tvDp, registroDp) }
        //Para Helicoverpa
        hzhplus.setOnClickListener { countHZH = increase(countHZH, tvhhz, tvHZh, registroHZh) }; hzcplus.setOnClickListener { countHZC = increase(countHZC, tvchz, tvHZc, registroHZc) }; hzmplus.setOnClickListener { countHZM = increase(countHZM, tvmhz, tvHZm, registroHZm) }; hzgplus.setOnClickListener { countHZG = increase(countHZG, tvghz, tvHZg, registroHZg) }; hzpplus.setOnClickListener { countHZP = increase(countHZP, tvphz, tvHZp, registroHZp) }; hzhminus.setOnClickListener { countHZH = decrease(countHZH, tvhhz, tvHZh, registroHZh) }; hzcminus.setOnClickListener { countHZC = decrease(countHZC, tvchz, tvHZc, registroHZc) }; hzmminus.setOnClickListener { countHZM = decrease(countHZM, tvmhz, tvHZm, registroHZm) }; hzgminus.setOnClickListener { countHZG = decrease(countHZG, tvghz, tvHZg, registroHZg) }; hzpminus.setOnClickListener { countHZP = decrease(countHZP, tvphz, tvHZp, registroHZp) }
        //Para Copitarsia
        ccplus.setOnClickListener { countCC = increase(countCC, tvcc, tvCc, registroCc) }; cmplus.setOnClickListener { countCM = increase(countCM, tvmc, tvCm, registroCm) }; cgplus.setOnClickListener { countCG = increase(countCG, tvgc, tvCg, registroCg) }; ccminus.setOnClickListener { countCC = decrease(countCC, tvcc, tvCc, registroCc) }; cmminus.setOnClickListener { countCM = decrease(countCM, tvmc, tvCm, registroCm) }; cgminus.setOnClickListener { countCG = decrease(countCG, tvgc, tvCg, registroCg) }
        //Para Chinche Nysius
        cncplus.setOnClickListener { countCNC = increase(countCNC, tvccn, tvCNc, registroCNc) }; cnmplus.setOnClickListener { countCNM = increase(countCNM, tvmcn, tvCNm, registroCNm) }; cngplus.setOnClickListener { countCNG = increase(countCNG, tvgcn, tvCNg, registroCNg) }; cncminus.setOnClickListener { countCNC = decrease(countCNC, tvccn, tvCNc, registroCNc) }; cnmminus.setOnClickListener { countCNM = decrease(countCNM, tvmcn, tvCNm, registroCNm) }; cngminus.setOnClickListener { countCNG = decrease(countCNG, tvgcn, tvCNg, registroCNg) }
        //Para Chicharrita
        chihplus.setOnClickListener { countCHIH = increase(countCHIH, tvhchi, tvCHIh, registroCHIh) };chinplus.setOnClickListener { countCHIN = increase(countCHIN, tvnchi, tvCHIn, registroCHIn) };chiaplus.setOnClickListener { countCHIA = increase(countCHIA, tvachi, tvCHIa, registroCHIa) };chihminus.setOnClickListener { countCHIH = decrease(countCHIH, tvhchi, tvCHIh, registroCHIh) };chinminus.setOnClickListener { countCHIN = decrease(countCHIN, tvnchi, tvCHIn, registroCHIn) };chiaminus.setOnClickListener { countCHIA = decrease(countCHIA, tvachi, tvCHIa, registroCHIa) }
        //Para Trips
        thplus.setOnClickListener { countTH = increase(countTH, tvht, tvTh, registroTh) };tnplus.setOnClickListener { countTN = increase(countTN, tvnt, tvTn, registroTn) };taplus.setOnClickListener { countTA = increase(countTA, tvat, tvTa, registroTa) };thminus.setOnClickListener { countTH = decrease(countTH, tvht, tvTh, registroTh) };tnminus.setOnClickListener { countTN = decrease(countTN, tvnt, tvTn, registroTn) };taminus.setOnClickListener { countTA = decrease(countTA, tvat, tvTa, registroTa) }
        //Para Mosquita Blanca
        mbhplus.setOnClickListener { countMBH = increase(countMBH, tvhmb, tvMBh, registroMBh) };mbnplus.setOnClickListener { countMBN = increase(countMBN, tvnmb, tvMBn, registroMBn) };mbaplus.setOnClickListener { countMBA = increase(countMBA, tvamb, tvMBa, registroMBa) };mbhminus.setOnClickListener { countMBH = decrease(countMBH, tvhmb, tvMBh, registroMBh) };mbnminus.setOnClickListener { countMBN = decrease(countMBN, tvnmb, tvMBn, registroMBn) };mbaminus.setOnClickListener { countMBA = decrease(countMBA, tvamb, tvMBa, registroMBa) }
        //Para Coccinallidae
        cochplus.setOnClickListener { countCOCH = increase(countCOCH, tvhcoc, tvCOCh, registroCOCh) };coclplus.setOnClickListener { countCOCL = increase(countCOCL, tvlcoc, tvCOCl, registroCOCl) };cocaplus.setOnClickListener { countCOCA = increase(countCOCA, tvacoc, tvCOCa, registroCOCa) };cochminus.setOnClickListener { countCOCH = decrease(countCOCH, tvhcoc, tvCOCh, registroCOCh) };coclminus.setOnClickListener { countCOCL = decrease(countCOCL, tvlcoc, tvCOCl, registroCOCl) };cocaminus.setOnClickListener { countCOCA = decrease(countCOCA, tvacoc, tvCOCa, registroCOCa) }
        /*------------------------------------------------------------------------------------------
        |
        |   Agregamos la funcionalidad de los checkbox para registrar la presencia de enfermedades en las plantas
        |
         -------------------------------------------------------------------------------------------*/
        cbTizon.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //Agregamos las acciones a ejecutar cuando el checkbox sea marcado
                registroTI.visibility = View.VISIBLE
                tvTizon.text = "Sí"
            } else {
                //Agregamos las acciones a ejecutar cuando el checkbox este inhabilitado
                registroTI.visibility = View.GONE
                tvTizon.text = ""
            }
        }
        cbMinador.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //Agregamos las acciones a ejecutar cuando el checkbox sea marcado
                registroM.visibility = View.VISIBLE
                tvMinador.text = "Sí"
            } else {
                //Agregamos las acciones a ejecutar cuando el checkbox este inhabilitado
                registroM.visibility = View.GONE
                tvMinador.text = ""
            }
        }
        /*----------------------------------------------------------------------------------------------------
        |
        |   Implementación del botón cuando no se registran plagas
        |
         ----------------------------------------------------------------------------------------------------*/
        //Boton no plaga
        bNoPlaga.setOnClickListener {
            showadvertence()
        }
        /* ---------------------------------------------------------------------------------
        |
        |   Implementamos el boton para enviar el registro de las plagas que se encontraron
        |
          ---------------------------------------------------------------------------------*/
        botonEnvioRegistro.setOnClickListener {
            val registros = mutableListOf<MonitoreoEntity>()

            agregarRegistroSiVisible(registros, registroDDh, "Plaga", stringplagadd, stringfaseh, countDDH)
            agregarRegistroSiVisible(registros, registroDDc, "Plaga", stringplagadd, stringfasec, countDDC)
            agregarRegistroSiVisible(registros, registroDDm, "Plaga", stringplagadd, stringfasem, countDDM)
            agregarRegistroSiVisible(registros, registroDDg, "Plaga", stringplagadd, stringfaseg, countDDG)
            agregarRegistroSiVisible(registros, registroDDp, "Plaga", stringplagadd, stringfasep, countDDP)

            agregarRegistroSiVisible(registros, registroFMh, "Plaga", stringplagafm, stringfaseh, countFMH)
            agregarRegistroSiVisible(registros, registroFMc, "Plaga", stringplagafm, stringfasec, countFMC)
            agregarRegistroSiVisible(registros, registroFMm, "Plaga", stringplagafm, stringfasem, countFMM)
            agregarRegistroSiVisible(registros, registroFMg, "Plaga", stringplagafm, stringfaseg, countFMG)
            agregarRegistroSiVisible(registros, registroFMp, "Plaga", stringplagafm, stringfasep, countFMP)

            agregarRegistroSiVisible(registros, registroDh, "Plaga", stringplagad, stringfaseh, countDH)
            agregarRegistroSiVisible(registros, registroDc, "Plaga", stringplagad, stringfasec, countDC)
            agregarRegistroSiVisible(registros, registroDm, "Plaga", stringplagad, stringfasem, countDM)
            agregarRegistroSiVisible(registros, registroDg, "Plaga", stringplagad, stringfaseg, countDG)
            agregarRegistroSiVisible(registros, registroDp, "Plaga", stringplagad, stringfasep, countDP)

            agregarRegistroSiVisible(registros, registroCc, "Plaga", stringplagac, stringfasec, countCC)
            agregarRegistroSiVisible(registros, registroCm, "Plaga", stringplagac, stringfasem, countCM)
            agregarRegistroSiVisible(registros, registroCg, "Plaga", stringplagac, stringfaseg, countCG)

            agregarRegistroSiVisible(registros, registroCHIh, "Plaga", stringplagachi, stringfaseh, countCHIH)
            agregarRegistroSiVisible(registros, registroCHIn, "Plaga", stringplagachi, stringfasen, countCHIN)
            agregarRegistroSiVisible(registros, registroCHIa, "Plaga", stringplagachi, stringfasea, countCHIA)

            agregarRegistroSiVisible(registros, registroHZh, "Plaga", stringplagahz, stringfaseh, countHZH)
            agregarRegistroSiVisible(registros, registroHZc, "Plaga", stringplagahz, stringfasec, countHZC)
            agregarRegistroSiVisible(registros, registroHZm, "Plaga", stringplagahz, stringfasem, countHZM)
            agregarRegistroSiVisible(registros, registroHZg, "Plaga", stringplagahz, stringfaseg, countHZG)
            agregarRegistroSiVisible(registros, registroHZp, "Plaga", stringplagahz, stringfasep, countHZP)

            agregarRegistroSiVisible(registros, registroCNc, "Plaga", stringplagacn, stringfasec, countCNC)
            agregarRegistroSiVisible(registros, registroCNm, "Plaga", stringplagacn, stringfasem, countCNM)
            agregarRegistroSiVisible(registros, registroCNg, "Plaga", stringplagacn, stringfaseg, countCNG)

            agregarRegistroSiVisible(registros, registroTh, "Plaga", stringplagat, stringfaseh, countTH)
            agregarRegistroSiVisible(registros, registroTn, "Plaga", stringplagat, stringfasen, countTN)
            agregarRegistroSiVisible(registros, registroTa, "Plaga", stringplagat, stringfasea, countTA)

            agregarRegistroSiVisible(registros, registroCOCh, "Plaga", stringplagacoc, stringfaseh, countCOCH)
            agregarRegistroSiVisible(registros, registroCOCl, "Plaga", stringplagacoc, stringfasel, countCOCL)
            agregarRegistroSiVisible(registros, registroCOCa, "Plaga", stringplagacoc, stringfasea, countCOCA)

            agregarRegistroSiVisible(registros, registroMBh, "Plaga", stringplagamb, stringfaseh, countMBH)
            agregarRegistroSiVisible(registros, registroMBn, "Plaga", stringplagamb, stringfasen, countMBN)
            agregarRegistroSiVisible(registros, registroMBa, "Plaga", stringplagamb, stringfasea, countMBA)

            agregarRegistroSiVisible(registros, registroTI, "Enfermedad", stringplagati, stringfasena, 1)
            agregarRegistroSiVisible(registros, registroM, "Enfermedad", stringplagam, stringfasena, 1)

            if (registros.isEmpty()) {
                showMessage("Favor de Ingresar un registro")
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
    /* ----------------------------------------------------------------------------------
    |
    |       AQUI DECLARAMOS TODAS LAS FUNCIONES QUE SERAN USADAS EN LA ACTIVIDAD
    |
      ------------------------------------------------------------------------------------*/

    //Declaramos funcion para que se muestre un mensaje cuando se elija la opcioón que no se encontraron plagas 
    private fun showadvertence() {
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
        val intent = Intent(this@pantalla_plagaslechuga, Pantalla_registromapa::class.java)
        intent.putExtra("ind_value", IDcount)
        intent.putExtra("cultivo", Cultivo)
        startActivity(intent)
        finish()
    }
    //Declaramos la función para que se mueste un breve mensaje, en mostrarMsj debes colocar la cadena de caracter que quieres que se muestre en pantalla
    fun showMessage (mostrarMsj:String){
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, mostrarMsj, duracion)
        toast.show()
    }
    //Definimos funcion para verificar los registros, en base a esto evaluamos cuales son añadidos en el archivo y cuales no
    fun verifyRegister(registro: LinearLayout,tipo: String, nombre: String, fase: String, cantidad: Int){
        if(registro.visibility == View.VISIBLE){
            dataSave.add(mutableListOf(IDcount.toString(), loggedUser, Agricultor, Granja, Lote, Cultivo, latitud.toString(), longitud.toString(),tipo , nombre, fase, cantidad.toString(), Fecha, Hora))
        }
    }
    //Definimos una funcion para obtener la fecha y la hora del dispositivo
    fun obtenerFechaYHora(): String {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatoFecha.format(calendario.time)
    }
    /*______________________________________________________________________________________________
    |
    |   Declaramos las funciones necesarias para obtener la ubicación del dispositivo
    |
     _______________________________________________________________________________________________*/
    //Definimos función para solicitar actualización de ubicación
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Manejo de permisos
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    //Definimos funcion poara dejar de actualizar la localización
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    //Declaramos función para obtener la ultima ubicación del dispositivo
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
    //Creamos funcion para decidir dondde guardamos el archivo
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
    override fun onBackPressed() {
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, "No se puede volver en esta Pantalla.", duracion)
        toast.show()
    }
}