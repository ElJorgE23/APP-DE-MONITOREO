package com.example.apptest

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.apptest.data.AppDatabase
import com.example.apptest.data.MonitoreoEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class pantalla_implementoplagas : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private var latitud: Double? = null
    private var longitud: Double? = null
    private var ind: Int = 1

    private lateinit var FyH: String
    private lateinit var Fecha: String
    private lateinit var Hora: String

    private lateinit var tvUbicacion: TextView
    private lateinit var msjPlagas: TextView
    private lateinit var botonOtrasPlagas: Button
    private lateinit var cajaOtrasPlagas: LinearLayout
    private lateinit var cajaPlagasPrincipales: LinearLayout
    private lateinit var ContentButton: LinearLayout
    private lateinit var ContentReciver: LinearLayout
    private lateinit var bNoPlaga: ImageButton

    private lateinit var cDD: LinearLayout; private lateinit var cFM: LinearLayout; private lateinit var cGS: LinearLayout
    private lateinit var cPV: LinearLayout; private lateinit var cPG: LinearLayout; private lateinit var cCL: LinearLayout
    private lateinit var cGP: LinearLayout; private lateinit var cC: LinearLayout; private lateinit var cD: LinearLayout
    private lateinit var cChi: LinearLayout; private lateinit var cGDC: LinearLayout; private lateinit var cGDF: LinearLayout

    private lateinit var botonDD: ImageButton; private lateinit var botonFM: ImageButton; private lateinit var botonPV: ImageButton
    private lateinit var botonPG: ImageButton; private lateinit var botonGS: ImageButton; private lateinit var botonCL: ImageButton
    private lateinit var botonGP: ImageButton; private lateinit var botonC: ImageButton; private lateinit var botonD: ImageButton
    private lateinit var botonChi: ImageButton; private lateinit var botonGDC: ImageButton; private lateinit var botonGDF: ImageButton

    private lateinit var registroDDh: LinearLayout; private lateinit var registroDDc: LinearLayout; private lateinit var registroDDm: LinearLayout; private lateinit var registroDDg: LinearLayout; private lateinit var registroDDp: LinearLayout
    private lateinit var registroFMh: LinearLayout; private lateinit var registroFMc: LinearLayout; private lateinit var registroFMm: LinearLayout; private lateinit var registroFMg: LinearLayout; private lateinit var registroFMp: LinearLayout
    private lateinit var registroGSh: LinearLayout; private lateinit var registroGSc: LinearLayout; private lateinit var registroGSm: LinearLayout; private lateinit var registroGSg: LinearLayout; private lateinit var registroGSp: LinearLayout
    private lateinit var registroGPh: LinearLayout; private lateinit var registroGPc: LinearLayout; private lateinit var registroGPm: LinearLayout; private lateinit var registroGPg: LinearLayout; private lateinit var registroGPp: LinearLayout
    private lateinit var registroGDCh: LinearLayout; private lateinit var registroGDCc: LinearLayout; private lateinit var registroGDCm: LinearLayout; private lateinit var registroGDCg: LinearLayout; private lateinit var registroGDCp: LinearLayout
    private lateinit var registroGDFh: LinearLayout; private lateinit var registroGDFc: LinearLayout; private lateinit var registroGDFm: LinearLayout; private lateinit var registroGDFg: LinearLayout; private lateinit var registroGDFp: LinearLayout
    private lateinit var registroDh: LinearLayout; private lateinit var registroDc: LinearLayout; private lateinit var registroDm: LinearLayout; private lateinit var registroDg: LinearLayout; private lateinit var registroDp: LinearLayout
    private lateinit var registroCLc: LinearLayout; private lateinit var registroCLm: LinearLayout; private lateinit var registroCLg: LinearLayout
    private lateinit var registroCc: LinearLayout; private lateinit var registroCm: LinearLayout; private lateinit var registroCg: LinearLayout
    private lateinit var registroCHIh: LinearLayout; private lateinit var registroCHIn: LinearLayout; private lateinit var registroCHIa: LinearLayout
    private lateinit var registroPVsa: LinearLayout; private lateinit var registroPVca: LinearLayout
    private lateinit var registroPG: LinearLayout

    private lateinit var tvDDh: TextView; private lateinit var tvDDc: TextView; private lateinit var tvDDm: TextView; private lateinit var tvDDg: TextView; private lateinit var tvDDp: TextView
    private lateinit var tvFMh: TextView; private lateinit var tvFMc: TextView; private lateinit var tvFMm: TextView; private lateinit var tvFMg: TextView; private lateinit var tvFMp: TextView
    private lateinit var tvGSh: TextView; private lateinit var tvGSc: TextView; private lateinit var tvGSm: TextView; private lateinit var tvGSg: TextView; private lateinit var tvGSp: TextView
    private lateinit var tvGPh: TextView; private lateinit var tvGPc: TextView; private lateinit var tvGPm: TextView; private lateinit var tvGPg: TextView; private lateinit var tvGPp: TextView
    private lateinit var tvGDCh: TextView; private lateinit var tvGDCc: TextView; private lateinit var tvGDCm: TextView; private lateinit var tvGDCg: TextView; private lateinit var tvGDCp: TextView
    private lateinit var tvGDFh: TextView; private lateinit var tvGDFc: TextView; private lateinit var tvGDFm: TextView; private lateinit var tvGDFg: TextView; private lateinit var tvGDFp: TextView
    private lateinit var tvDh: TextView; private lateinit var tvDc: TextView; private lateinit var tvDm: TextView; private lateinit var tvDg: TextView; private lateinit var tvDp: TextView
    private lateinit var tvCLc: TextView; private lateinit var tvCLm: TextView; private lateinit var tvCLg: TextView
    private lateinit var tvCc: TextView; private lateinit var tvCm: TextView; private lateinit var tvCg: TextView
    private lateinit var tvCHIh: TextView; private lateinit var tvCHIn: TextView; private lateinit var tvCHIa: TextView
    private lateinit var tvPVsa: TextView; private lateinit var tvPVca: TextView
    private lateinit var tvPG: TextView

    private lateinit var tvhdd: TextView; private lateinit var tvcdd: TextView; private lateinit var tvmdd: TextView; private lateinit var tvgdd: TextView; private lateinit var tvpdd: TextView
    private lateinit var tvhfm: TextView; private lateinit var tvcfm: TextView; private lateinit var tvmfm: TextView; private lateinit var tvgfm: TextView; private lateinit var tvpfm: TextView
    private lateinit var tvhgs: TextView; private lateinit var tvcgs: TextView; private lateinit var tvmgs: TextView; private lateinit var tvggs: TextView; private lateinit var tvpgs: TextView
    private lateinit var tvhgdc: TextView; private lateinit var tvcgdc: TextView; private lateinit var tvmgdc: TextView; private lateinit var tvggdc: TextView; private lateinit var tvpgdc: TextView
    private lateinit var tvhgdf: TextView; private lateinit var tvcgdf: TextView; private lateinit var tvmgdf: TextView; private lateinit var tvggdf: TextView; private lateinit var tvpgdf: TextView
    private lateinit var tvhgp: TextView; private lateinit var tvcgp: TextView; private lateinit var tvmgp: TextView; private lateinit var tvggp: TextView; private lateinit var tvpgp: TextView
    private lateinit var tvhd: TextView; private lateinit var tvcd: TextView; private lateinit var tvmd: TextView; private lateinit var tvgd: TextView; private lateinit var tvpd: TextView
    private lateinit var tvccl: TextView; private lateinit var tvmcl: TextView; private lateinit var tvgcl: TextView
    private lateinit var tvcc: TextView; private lateinit var tvmc: TextView; private lateinit var tvgc: TextView
    private lateinit var tvhchi: TextView; private lateinit var tvnchi: TextView; private lateinit var tvachi: TextView
    private lateinit var tvsapv: TextView; private lateinit var tvcapv: TextView
    private lateinit var tvpg: TextView

    private lateinit var ddhplus: ImageButton; private lateinit var ddcplus: ImageButton; private lateinit var ddmplus: ImageButton; private lateinit var ddgplus: ImageButton; private lateinit var ddpplus: ImageButton
    private lateinit var ddhminus: ImageButton; private lateinit var ddcminus: ImageButton; private lateinit var ddmminus: ImageButton; private lateinit var ddgminus: ImageButton; private lateinit var ddpminus: ImageButton

    private lateinit var fmhplus: ImageButton; private lateinit var fmcplus: ImageButton; private lateinit var fmmplus: ImageButton; private lateinit var fmgplus: ImageButton; private lateinit var fmpplus: ImageButton
    private lateinit var fmhminus: ImageButton; private lateinit var fmcminus: ImageButton; private lateinit var fmmminus: ImageButton; private lateinit var fmgminus: ImageButton; private lateinit var fmpminus: ImageButton

    private lateinit var gshplus: ImageButton; private lateinit var gscplus: ImageButton; private lateinit var gsmplus: ImageButton; private lateinit var gsgplus: ImageButton; private lateinit var gspplus: ImageButton
    private lateinit var gshminus: ImageButton; private lateinit var gscminus: ImageButton; private lateinit var gsmminus: ImageButton; private lateinit var gsgminus: ImageButton; private lateinit var gspminus: ImageButton

    private lateinit var gdchplus: ImageButton; private lateinit var gdccplus: ImageButton; private lateinit var gdcmplus: ImageButton; private lateinit var gdcgplus: ImageButton; private lateinit var gdcpplus: ImageButton
    private lateinit var gdchminus: ImageButton; private lateinit var gdccminus: ImageButton; private lateinit var gdcmminus: ImageButton; private lateinit var gdcgminus: ImageButton; private lateinit var gdcpminus: ImageButton

    private lateinit var gdfhplus: ImageButton; private lateinit var gdfcplus: ImageButton; private lateinit var gdfmplus: ImageButton; private lateinit var gdfgplus: ImageButton; private lateinit var gdfpplus: ImageButton
    private lateinit var gdfhminus: ImageButton; private lateinit var gdfcminus: ImageButton; private lateinit var gdfmminus: ImageButton; private lateinit var gdfgminus: ImageButton; private lateinit var gdfpminus: ImageButton

    private lateinit var gphplus: ImageButton; private lateinit var gpcplus: ImageButton; private lateinit var gpmplus: ImageButton; private lateinit var gpgplus: ImageButton; private lateinit var gppplus: ImageButton
    private lateinit var gphminus: ImageButton; private lateinit var gpcminus: ImageButton; private lateinit var gpmminus: ImageButton; private lateinit var gpgminus: ImageButton; private lateinit var gppminus: ImageButton

    private lateinit var dhplus: ImageButton; private lateinit var dcplus: ImageButton; private lateinit var dmplus: ImageButton; private lateinit var dgplus: ImageButton; private lateinit var dpplus: ImageButton
    private lateinit var dhminus: ImageButton; private lateinit var dcminus: ImageButton; private lateinit var dmminus: ImageButton; private lateinit var dgminus: ImageButton; private lateinit var dpminus: ImageButton

    private lateinit var clcplus: ImageButton; private lateinit var clmplus: ImageButton; private lateinit var clgplus: ImageButton
    private lateinit var clcminus: ImageButton; private lateinit var clmminus: ImageButton; private lateinit var clgminus: ImageButton

    private lateinit var ccplus: ImageButton; private lateinit var cmplus: ImageButton; private lateinit var cgplus: ImageButton
    private lateinit var ccminus: ImageButton; private lateinit var cmminus: ImageButton; private lateinit var cgminus: ImageButton

    private lateinit var chihplus: ImageButton; private lateinit var chinplus: ImageButton; private lateinit var chiaplus: ImageButton
    private lateinit var chihminus: ImageButton; private lateinit var chinminus: ImageButton; private lateinit var chiaminus: ImageButton

    private lateinit var pvsaplus: ImageButton; private lateinit var pvcaplus: ImageButton
    private lateinit var pvsaminus: ImageButton; private lateinit var pvcaminus: ImageButton

    private lateinit var pgplus: ImageButton; private lateinit var pgminus: ImageButton

    private var countDDH = 0; private var countDDC = 0; private var countDDM = 0; private var countDDG = 0; private var countDDP = 0
    private var countFMH = 0; private var countFMC = 0; private var countFMM = 0; private var countFMG = 0; private var countFMP = 0
    private var countGSH = 0; private var countGSC = 0; private var countGSM = 0; private var countGSG = 0; private var countGSP = 0
    private var countGDCH = 0; private var countGDCC = 0; private var countGDCM = 0; private var countGDCG = 0; private var countGDCP = 0
    private var countGDFH = 0; private var countGDFC = 0; private var countGDFM = 0; private var countGDFG = 0; private var countGDFP = 0
    private var countGPH = 0; private var countGPC = 0; private var countGPM = 0; private var countGPG = 0; private var countGPP = 0
    private var countDH = 0; private var countDC = 0; private var countDM = 0; private var countDG = 0; private var countDP = 0
    private var countCLC = 0; private var countCLM = 0; private var countCLG = 0
    private var countCC = 0; private var countCM = 0; private var countCG = 0
    private var countCHIH = 0; private var countCHIN = 0; private var countCHIA = 0
    private var countPVSA = 0; private var countPVCA = 0
    private var countPG = 0

    private lateinit var stringplagadd: String; private lateinit var stringplagafm: String; private lateinit var stringplagags: String
    private lateinit var stringplagapv: String; private lateinit var stringplagapg: String; private lateinit var stringplagacl: String
    private lateinit var stringplagagp: String; private lateinit var stringplagac: String; private lateinit var stringplagad: String
    private lateinit var stringplagachi: String; private lateinit var stringplagagdc: String; private lateinit var stringplagagdf: String

    private lateinit var stringfaseh: String; private lateinit var stringfasec: String; private lateinit var stringfasem: String
    private lateinit var stringfaseg: String; private lateinit var stringfasep: String; private lateinit var stringfasen: String
    private lateinit var stringfasea: String; private lateinit var stringfaseca: String; private lateinit var stringfasesa: String
    private lateinit var stringfasena: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_implementoplagas)

        db = AppDatabase.getDatabase(applicationContext)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        ind = intent.getIntExtra("ind_value", 1)

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
                    tvUbicacion.text = "Lat: ${latitud ?: ""}, Lon: ${longitud ?: ""}"
                }
            }
        }

        inicializarStrings()
        inicializarVistas()
        inicializarBotonesYTextos()
        configurarSelectorPlagas()
        configurarBotonesConteo()

        FyH = obtenerFechaYHora()
        Fecha = getFecha()
        Hora = getHora()

        val idPlague: TextView = findViewById(R.id.tv_idPlagas)
        idPlague.text = ind.toString()

        getLastLocation()
        startLocationUpdates()

        findViewById<Button>(R.id.b_EnviarPlagas).setOnClickListener {
            val registros = construirRegistrosSeleccionados()

            if (registros.isEmpty()) {
                showMessage("Favor de ingresar un registro")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    db.monitoreoDao().insertarMonitoreos(registros)
                    abrirMapaDespuesDeGuardar()
                } catch (e: Exception) {
                    showMessage("Error al guardar en Room: ${e.message}")
                }
            }
        }

        bNoPlaga.setOnClickListener {
            showadvertence()
        }
    }

    private fun inicializarStrings() {
        stringplagadd = getString(R.string.plaga_dorsodiamante)
        stringplagafm = getString(R.string.plaga_falsomedidor)
        stringplagags = getString(R.string.plaga_gusanosoldado)
        stringplagapv = getString(R.string.plaga_pulgonverde)
        stringplagapg = getString(R.string.plaga_pulgongris)
        stringplagacl = getString(R.string.plaga_chinchelygus)
        stringplagagp = getString(R.string.plaga_pieris)
        stringplagac = getString(R.string.plaga_copitarsia)
        stringplagad = getString(R.string.plaga_diabrotica)
        stringplagachi = getString(R.string.plaga_chicharrita)
        stringplagagdc = getString(R.string.plaga_gusanocol)
        stringplagagdf = getString(R.string.plaga_gusanofruta)

        stringfaseh = getString(R.string.fase_huevecillo)
        stringfasec = getString(R.string.fase_chico)
        stringfasem = getString(R.string.fase_mediano)
        stringfaseg = getString(R.string.fase_grande)
        stringfasep = getString(R.string.fase_pupa)
        stringfasen = getString(R.string.fase_ninfa)
        stringfasea = getString(R.string.fase_adulto)
        stringfaseca = getString(R.string.fase_conalas)
        stringfasesa = getString(R.string.fase_sinalas)
        stringfasena = getString(R.string.msj_NA)
    }

    private fun inicializarVistas() {
        registroDDh = findViewById(R.id.ddregistroh); registroDDc = findViewById(R.id.ddregistroc); registroDDm = findViewById(R.id.ddregistrom); registroDDg = findViewById(R.id.ddregistrog); registroDDp = findViewById(R.id.ddregistrop)
        registroFMh = findViewById(R.id.fmregistroh); registroFMc = findViewById(R.id.fmregistroc); registroFMm = findViewById(R.id.fmregistrom); registroFMg = findViewById(R.id.fmregistrog); registroFMp = findViewById(R.id.fmregistrop)
        registroGSh = findViewById(R.id.gsregistroh); registroGSc = findViewById(R.id.gsregistroc); registroGSm = findViewById(R.id.gsregistrom); registroGSg = findViewById(R.id.gsregistrog); registroGSp = findViewById(R.id.gsregistrop)
        registroGPh = findViewById(R.id.gpregistroh); registroGPc = findViewById(R.id.gpregistroc); registroGPm = findViewById(R.id.gpregistrom); registroGPg = findViewById(R.id.gpregistrog); registroGPp = findViewById(R.id.gpregistrop)
        registroDh = findViewById(R.id.dregistroh); registroDc = findViewById(R.id.dregistroc); registroDm = findViewById(R.id.dregistrom); registroDg = findViewById(R.id.dregistrog); registroDp = findViewById(R.id.dregistrop)
        registroGDCh = findViewById(R.id.gdcregistroh); registroGDCc = findViewById(R.id.gdcregistroc); registroGDCm = findViewById(R.id.gdcregistrom); registroGDCg = findViewById(R.id.gdcregistrog); registroGDCp = findViewById(R.id.gdcregistrop)
        registroGDFh = findViewById(R.id.gdfregistroh); registroGDFc = findViewById(R.id.gdfregistroc); registroGDFm = findViewById(R.id.gdfregistrom); registroGDFg = findViewById(R.id.gdfregistrog); registroGDFp = findViewById(R.id.gdfregistrop)
        registroCLc = findViewById(R.id.clregistroc); registroCLm = findViewById(R.id.clregistrom); registroCLg = findViewById(R.id.clregistrog)
        registroCc = findViewById(R.id.cregistroc); registroCm = findViewById(R.id.cregistrom); registroCg = findViewById(R.id.cregistrog)
        registroCHIh = findViewById(R.id.chiregistroh); registroCHIn = findViewById(R.id.chiregistron); registroCHIa = findViewById(R.id.chiregistroa)
        registroPVca = findViewById(R.id.pvregistroca); registroPVsa = findViewById(R.id.pvregistrosa)
        registroPG = findViewById(R.id.pgregistro)

        cDD = findViewById(R.id.CajaDD); cFM = findViewById(R.id.CajaFM); cGS = findViewById(R.id.CajaGS); cPV = findViewById(R.id.CajaPV)
        cPG = findViewById(R.id.CajaPG); cCL = findViewById(R.id.CajaCL); cGP = findViewById(R.id.CajaGP); cC = findViewById(R.id.CajaC)
        cD = findViewById(R.id.CajaD); cChi = findViewById(R.id.CajaCHI); cGDC = findViewById(R.id.CajaGDC); cGDF = findViewById(R.id.CajaGDF)

        tvDDh = findViewById(R.id.tv_ddh); tvDDc = findViewById(R.id.tv_ddc); tvDDm = findViewById(R.id.tv_ddm); tvDDg = findViewById(R.id.tv_ddg); tvDDp = findViewById(R.id.tv_ddp)
        tvFMh = findViewById(R.id.tv_fmh); tvFMc = findViewById(R.id.tv_fmc); tvFMm = findViewById(R.id.tv_fmm); tvFMg = findViewById(R.id.tv_fmg); tvFMp = findViewById(R.id.tv_fmp)
        tvGSh = findViewById(R.id.tv_gsh); tvGSc = findViewById(R.id.tv_gsc); tvGSm = findViewById(R.id.tv_gsm); tvGSg = findViewById(R.id.tv_gsg); tvGSp = findViewById(R.id.tv_gsp)
        tvGPh = findViewById(R.id.tv_gph); tvGPc = findViewById(R.id.tv_gpc); tvGPm = findViewById(R.id.tv_gpm); tvGPg = findViewById(R.id.tv_gpg); tvGPp = findViewById(R.id.tv_gpp)
        tvDh = findViewById(R.id.tv_dh); tvDc = findViewById(R.id.tv_dc); tvDm = findViewById(R.id.tv_dm); tvDg = findViewById(R.id.tv_dg); tvDp = findViewById(R.id.tv_dp)
        tvGDCh = findViewById(R.id.tv_gdch); tvGDCc = findViewById(R.id.tv_gdcc); tvGDCm = findViewById(R.id.tv_gdcm); tvGDCg = findViewById(R.id.tv_gdcg); tvGDCp = findViewById(R.id.tv_gdcp)
        tvGDFh = findViewById(R.id.tv_gdfh); tvGDFc = findViewById(R.id.tv_gdfc); tvGDFm = findViewById(R.id.tv_gdfm); tvGDFg = findViewById(R.id.tv_gdfg); tvGDFp = findViewById(R.id.tv_gdfp)
        tvCLc = findViewById(R.id.tv_clc); tvCLm = findViewById(R.id.tv_clm); tvCLg = findViewById(R.id.tv_clg)
        tvCc = findViewById(R.id.tv_cc); tvCm = findViewById(R.id.tv_cm); tvCg = findViewById(R.id.tv_cg)
        tvCHIh = findViewById(R.id.tv_chih); tvCHIn = findViewById(R.id.tv_chin); tvCHIa = findViewById(R.id.tv_chia)
        tvPVsa = findViewById(R.id.tv_pvsa); tvPVca = findViewById(R.id.tv_pvca)
        tvPG = findViewById(R.id.tv_pg)

        tvhdd = findViewById(R.id.cantidad_hDD); tvcdd = findViewById(R.id.cantidad_cDD); tvmdd = findViewById(R.id.cantidad_mDD); tvgdd = findViewById(R.id.cantidad_gDD); tvpdd = findViewById(R.id.cantidad_pDD)
        tvhfm = findViewById(R.id.cantidad_hFM); tvcfm = findViewById(R.id.cantidad_cFM); tvmfm = findViewById(R.id.cantidad_mFM); tvgfm = findViewById(R.id.cantidad_gFM); tvpfm = findViewById(R.id.cantidad_pFM)
        tvhgs = findViewById(R.id.cantidad_hGS); tvcgs = findViewById(R.id.cantidad_cGS); tvmgs = findViewById(R.id.cantidad_mGS); tvggs = findViewById(R.id.cantidad_gGS); tvpgs = findViewById(R.id.cantidad_pGS)
        tvhgdc = findViewById(R.id.cantidad_hGDC); tvcgdc = findViewById(R.id.cantidad_cGDC); tvmgdc = findViewById(R.id.cantidad_mGDC); tvggdc = findViewById(R.id.cantidad_gGDC); tvpgdc = findViewById(R.id.cantidad_pGDC)
        tvhgdf = findViewById(R.id.cantidad_hGDF); tvcgdf = findViewById(R.id.cantidad_cGDF); tvmgdf = findViewById(R.id.cantidad_mGDF); tvggdf = findViewById(R.id.cantidad_gGDF); tvpgdf = findViewById(R.id.cantidad_pGDF)
        tvhgp = findViewById(R.id.cantidad_hGP); tvcgp = findViewById(R.id.cantidad_chGP); tvmgp = findViewById(R.id.cantidad_mGP); tvggp = findViewById(R.id.cantidad_gGP); tvpgp = findViewById(R.id.cantidad_pGP)
        tvhd = findViewById(R.id.cantidad_hD); tvcd = findViewById(R.id.cantidad_chD); tvmd = findViewById(R.id.cantidad_mD); tvgd = findViewById(R.id.cantidad_gD); tvpd = findViewById(R.id.cantidad_pD)
        tvccl = findViewById(R.id.cantidad_cCL); tvmcl = findViewById(R.id.cantidad_mCL); tvgcl = findViewById(R.id.cantidad_gCL)
        tvcc = findViewById(R.id.cantidad_cC); tvmc = findViewById(R.id.cantidad_mC); tvgc = findViewById(R.id.cantidad_gC)
        tvhchi = findViewById(R.id.cantidad_hCHI); tvnchi = findViewById(R.id.cantidad_nCHI); tvachi = findViewById(R.id.cantidad_aCHI)
        tvcapv = findViewById(R.id.cantidad_caPV); tvsapv = findViewById(R.id.cantidad_saPV)
        tvpg = findViewById(R.id.cantidad_PG)

        tvUbicacion = findViewById(R.id.tv_Ubicacion)
        msjPlagas = findViewById(R.id.tvPlagas)
        botonOtrasPlagas = findViewById(R.id.boton_otrasplagas)
        cajaOtrasPlagas = findViewById(R.id.Caja_otrasplagas)
        cajaPlagasPrincipales = findViewById(R.id.Caja_plagasprincipales)
        ContentButton = findViewById(R.id.ContenedorBoton)
        ContentReciver = findViewById(R.id.ContenedorReciver)
        bNoPlaga = findViewById(R.id.boton_noplaga)
    }

    private fun inicializarBotonesYTextos() {
        botonDD = findViewById(R.id.boton_dorsodiamante); botonFM = findViewById(R.id.boton_falsomedidor); botonPV = findViewById(R.id.boton_pulgonverde)
        botonPG = findViewById(R.id.boton_pulgongris); botonGS = findViewById(R.id.boton_gusanosoldado); botonCL = findViewById(R.id.boton_chinchelygus)
        botonGP = findViewById(R.id.boton_gusanopieris); botonC = findViewById(R.id.boton_copitarsia); botonD = findViewById(R.id.boton_diabrotica)
        botonChi = findViewById(R.id.boton_chicharrita); botonGDC = findViewById(R.id.boton_gusanocol); botonGDF = findViewById(R.id.boton_gusanofruta)

        ddhplus = findViewById(R.id.ib_huevecillomasDD); ddcplus = findViewById(R.id.ib_chicomasDD); ddmplus = findViewById(R.id.ib_medianomasDD); ddgplus = findViewById(R.id.ib_grandemasDD); ddpplus = findViewById(R.id.ib_pupamasDD)
        ddhminus = findViewById(R.id.ib_huevecillomenosDD); ddcminus = findViewById(R.id.ib_chicomenosDD); ddmminus = findViewById(R.id.ib_medianomenosDD); ddgminus = findViewById(R.id.ib_grandemenosDD); ddpminus = findViewById(R.id.ib_pupamenosDD)

        fmhplus = findViewById(R.id.ib_huevecillomasFM); fmcplus = findViewById(R.id.ib_chicomasFM); fmmplus = findViewById(R.id.ib_medianomasFM); fmgplus = findViewById(R.id.ib_grandemasFM); fmpplus = findViewById(R.id.ib_pupamasFM)
        fmhminus = findViewById(R.id.ib_huevecillomenosFM); fmcminus = findViewById(R.id.ib_chicomenosFM); fmmminus = findViewById(R.id.ib_medianomenosFM); fmgminus = findViewById(R.id.ib_grandemenosFM); fmpminus = findViewById(R.id.ib_pupamenosFM)

        gshplus = findViewById(R.id.ib_huevecillomasGS); gscplus = findViewById(R.id.ib_chicomasGS); gsmplus = findViewById(R.id.ib_medianomasGS); gsgplus = findViewById(R.id.ib_grandemasGS); gspplus = findViewById(R.id.ib_pupamasGS)
        gshminus = findViewById(R.id.ib_huevecillomenosGS); gscminus = findViewById(R.id.ib_chicomenosGS); gsmminus = findViewById(R.id.ib_medianomenosGS); gsgminus = findViewById(R.id.ib_grandemenosGS); gspminus = findViewById(R.id.ib_pupamenosGS)

        gdchplus = findViewById(R.id.ib_huevecillomasGDC); gdccplus = findViewById(R.id.ib_chicomasGDC); gdcmplus = findViewById(R.id.ib_medianomasGDC); gdcgplus = findViewById(R.id.ib_grandemasGDC); gdcpplus = findViewById(R.id.ib_pupamasGDC)
        gdchminus = findViewById(R.id.ib_huevecillomenosGDC); gdccminus = findViewById(R.id.ib_chicomenosGDC); gdcmminus = findViewById(R.id.ib_medianomenosGDC); gdcgminus = findViewById(R.id.ib_grandemenosGDC); gdcpminus = findViewById(R.id.ib_pupamenosGDC)

        gdfhplus = findViewById(R.id.ib_huevecillomasGDF); gdfcplus = findViewById(R.id.ib_chicomasGDF); gdfmplus = findViewById(R.id.ib_medianomasGDF); gdfgplus = findViewById(R.id.ib_grandemasGDF); gdfpplus = findViewById(R.id.ib_pupamasGDF)
        gdfhminus = findViewById(R.id.ib_huevecillomenosGDF); gdfcminus = findViewById(R.id.ib_chicomenosGDF); gdfmminus = findViewById(R.id.ib_medianomenosGDF); gdfgminus = findViewById(R.id.ib_grandemenosGDF); gdfpminus = findViewById(R.id.ib_pupamenosGDF)

        gphplus = findViewById(R.id.ib_huevecillomasGP); gpcplus = findViewById(R.id.ib_chicomasGP); gpmplus = findViewById(R.id.ib_medianomasGP); gpgplus = findViewById(R.id.ib_grandemasGP); gppplus = findViewById(R.id.ib_pupamasGP)
        gphminus = findViewById(R.id.ib_huevecillomenosGP); gpcminus = findViewById(R.id.ib_chicomenosGP); gpmminus = findViewById(R.id.ib_medianomenosGP); gpgminus = findViewById(R.id.ib_grandemenosGP); gppminus = findViewById(R.id.ib_pupamenosGP)

        dhplus = findViewById(R.id.ib_huevecillomasD); dcplus = findViewById(R.id.ib_chicomasD); dmplus = findViewById(R.id.ib_medianomasD); dgplus = findViewById(R.id.ib_grandemasD); dpplus = findViewById(R.id.ib_pupamasD)
        dhminus = findViewById(R.id.ib_huevecillomenosD); dcminus = findViewById(R.id.ib_chicomenosD); dmminus = findViewById(R.id.ib_medianomenosD); dgminus = findViewById(R.id.ib_grandemenosD); dpminus = findViewById(R.id.ib_pupamenosD)

        clcplus = findViewById(R.id.ib_chicomasCL); clmplus = findViewById(R.id.ib_medianomasCL); clgplus = findViewById(R.id.ib_grandemasCL)
        clcminus = findViewById(R.id.ib_chicomenosCL); clmminus = findViewById(R.id.ib_medianomenosCL); clgminus = findViewById(R.id.ib_grandemenosCL)

        ccplus = findViewById(R.id.ib_chicomasC); cmplus = findViewById(R.id.ib_medianomasC); cgplus = findViewById(R.id.ib_grandemasC)
        ccminus = findViewById(R.id.ib_chicomenosC); cmminus = findViewById(R.id.ib_medianomenosC); cgminus = findViewById(R.id.ib_grandemenosC)

        chihplus = findViewById(R.id.ib_huevecillomasCHI); chinplus = findViewById(R.id.ib_ninfamasCHI); chiaplus = findViewById(R.id.ib_adultomasCHI)
        chihminus = findViewById(R.id.ib_huevecillomenosCHI); chinminus = findViewById(R.id.ib_ninfamenosCHI); chiaminus = findViewById(R.id.ib_adultomenosCHI)

        pvsaplus = findViewById(R.id.ib_sinalasmasPV); pvcaplus = findViewById(R.id.ib_conalasmasPV)
        pvsaminus = findViewById(R.id.ib_sinalasmenosPV); pvcaminus = findViewById(R.id.ib_conalasmenosPV)

        pgplus = findViewById(R.id.ib_masPG); pgminus = findViewById(R.id.ib_menosPG)
    }

    private fun configurarSelectorPlagas() {
        val buttonsPlague = listOf(botonDD, botonFM, botonGS, botonPV, botonPG, botonCL, botonGP, botonC, botonD, botonChi, botonGDC, botonGDF)
        val registersPlague = listOf(cDD, cFM, cGS, cPV, cPG, cCL, cGP, cC, cD, cChi, cGDC, cGDF)

        buttonsPlague.forEachIndexed { index, button ->
            button.setOnClickListener {
                registersPlague.forEachIndexed { layoutIndex, layout ->
                    layout.visibility = if (index == layoutIndex) View.VISIBLE else View.GONE
                }
            }
        }

        var currentIcon = 0
        val icAdd = ContextCompat.getDrawable(this, R.drawable.ic_add)
        val icHide = ContextCompat.getDrawable(this, R.drawable.ic_hide)

        botonOtrasPlagas.setOnClickListener {
            if (currentIcon == 0) {
                botonOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(icHide, null, null, null)
                currentIcon = 1
                cajaOtrasPlagas.visibility = View.VISIBLE
                msjPlagas.text = "Ver Plagas Principales"
                cajaPlagasPrincipales.visibility = View.GONE
                (ContentButton.parent as? LinearLayout)?.removeView(ContentButton)
                ContentReciver.addView(ContentButton)
            } else {
                botonOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(icAdd, null, null, null)
                currentIcon = 0
                cajaOtrasPlagas.visibility = View.GONE
                msjPlagas.text = "Ver Otras Plagas"
                cajaPlagasPrincipales.visibility = View.VISIBLE
                ContentReciver.removeView(ContentButton)
                cajaPlagasPrincipales.addView(ContentButton, 0)
            }
        }
    }

    private fun configurarBotonesConteo() {
        ddhplus.setOnClickListener { countDDH = increase(countDDH, tvhdd, tvDDh, registroDDh) }
        ddcplus.setOnClickListener { countDDC = increase(countDDC, tvcdd, tvDDc, registroDDc) }
        ddmplus.setOnClickListener { countDDM = increase(countDDM, tvmdd, tvDDm, registroDDm) }
        ddgplus.setOnClickListener { countDDG = increase(countDDG, tvgdd, tvDDg, registroDDg) }
        ddpplus.setOnClickListener { countDDP = increase(countDDP, tvpdd, tvDDp, registroDDp) }
        ddhminus.setOnClickListener { countDDH = decrease(countDDH, tvhdd, tvDDh, registroDDh) }
        ddcminus.setOnClickListener { countDDC = decrease(countDDC, tvcdd, tvDDc, registroDDc) }
        ddmminus.setOnClickListener { countDDM = decrease(countDDM, tvmdd, tvDDm, registroDDm) }
        ddgminus.setOnClickListener { countDDG = decrease(countDDG, tvgdd, tvDDg, registroDDg) }
        ddpminus.setOnClickListener { countDDP = decrease(countDDP, tvpdd, tvDDp, registroDDp) }

        fmhplus.setOnClickListener { countFMH = increase(countFMH, tvhfm, tvFMh, registroFMh) }
        fmcplus.setOnClickListener { countFMC = increase(countFMC, tvcfm, tvFMc, registroFMc) }
        fmmplus.setOnClickListener { countFMM = increase(countFMM, tvmfm, tvFMm, registroFMm) }
        fmgplus.setOnClickListener { countFMG = increase(countFMG, tvgfm, tvFMg, registroFMg) }
        fmpplus.setOnClickListener { countFMP = increase(countFMP, tvpfm, tvFMp, registroFMp) }
        fmhminus.setOnClickListener { countFMH = decrease(countFMH, tvhfm, tvFMh, registroFMh) }
        fmcminus.setOnClickListener { countFMC = decrease(countFMC, tvcfm, tvFMc, registroFMc) }
        fmmminus.setOnClickListener { countFMM = decrease(countFMM, tvmfm, tvFMm, registroFMm) }
        fmgminus.setOnClickListener { countFMG = decrease(countFMG, tvgfm, tvFMg, registroFMg) }
        fmpminus.setOnClickListener { countFMP = decrease(countFMP, tvpfm, tvFMp, registroFMp) }

        gshplus.setOnClickListener { countGSH = increase(countGSH, tvhgs, tvGSh, registroGSh) }
        gscplus.setOnClickListener { countGSC = increase(countGSC, tvcgs, tvGSc, registroGSc) }
        gsmplus.setOnClickListener { countGSM = increase(countGSM, tvmgs, tvGSm, registroGSm) }
        gsgplus.setOnClickListener { countGSG = increase(countGSG, tvggs, tvGSg, registroGSg) }
        gspplus.setOnClickListener { countGSP = increase(countGSP, tvpgs, tvGSp, registroGSp) }
        gshminus.setOnClickListener { countGSH = decrease(countGSH, tvhgs, tvGSh, registroGSh) }
        gscminus.setOnClickListener { countGSC = decrease(countGSC, tvcgs, tvGSc, registroGSc) }
        gsmminus.setOnClickListener { countGSM = decrease(countGSM, tvmgs, tvGSm, registroGSm) }
        gsgminus.setOnClickListener { countGSG = decrease(countGSG, tvggs, tvGSg, registroGSg) }
        gspminus.setOnClickListener { countGSP = decrease(countGSP, tvpgs, tvGSp, registroGSp) }

        gdchplus.setOnClickListener { countGDCH = increase(countGDCH, tvhgdc, tvGDCh, registroGDCh) }
        gdccplus.setOnClickListener { countGDCC = increase(countGDCC, tvcgdc, tvGDCc, registroGDCc) }
        gdcmplus.setOnClickListener { countGDCM = increase(countGDCM, tvmgdc, tvGDCm, registroGDCm) }
        gdcgplus.setOnClickListener { countGDCG = increase(countGDCG, tvggdc, tvGDCg, registroGDCg) }
        gdcpplus.setOnClickListener { countGDCP = increase(countGDCP, tvpgdc, tvGDCp, registroGDCp) }
        gdchminus.setOnClickListener { countGDCH = decrease(countGDCH, tvhgdc, tvGDCh, registroGDCh) }
        gdccminus.setOnClickListener { countGDCC = decrease(countGDCC, tvcgdc, tvGDCc, registroGDCc) }
        gdcmminus.setOnClickListener { countGDCM = decrease(countGDCM, tvmgdc, tvGDCm, registroGDCm) }
        gdcgminus.setOnClickListener { countGDCG = decrease(countGDCG, tvggdc, tvGDCg, registroGDCg) }
        gdcpminus.setOnClickListener { countGDCP = decrease(countGDCP, tvpgdc, tvGDCp, registroGDCp) }

        gdfhplus.setOnClickListener { countGDFH = increase(countGDFH, tvhgdf, tvGDFh, registroGDFh) }
        gdfcplus.setOnClickListener { countGDFC = increase(countGDFC, tvcgdf, tvGDFc, registroGDFc) }
        gdfmplus.setOnClickListener { countGDFM = increase(countGDFM, tvmgdf, tvGDFm, registroGDFm) }
        gdfgplus.setOnClickListener { countGDFG = increase(countGDFG, tvggdf, tvGDFg, registroGDFg) }
        gdfpplus.setOnClickListener { countGDFP = increase(countGDFP, tvpgdf, tvGDFp, registroGDFp) }
        gdfhminus.setOnClickListener { countGDFH = decrease(countGDFH, tvhgdf, tvGDFh, registroGDFh) }
        gdfcminus.setOnClickListener { countGDFC = decrease(countGDFC, tvcgdf, tvGDFc, registroGDFc) }
        gdfmminus.setOnClickListener { countGDFM = decrease(countGDFM, tvmgdf, tvGDFm, registroGDFm) }
        gdfgminus.setOnClickListener { countGDFG = decrease(countGDFG, tvggdf, tvGDFg, registroGDFg) }
        gdfpminus.setOnClickListener { countGDFP = decrease(countGDFP, tvpgdf, tvGDFp, registroGDFp) }

        gphplus.setOnClickListener { countGPH = increase(countGPH, tvhgp, tvGPh, registroGPh) }
        gpcplus.setOnClickListener { countGPC = increase(countGPC, tvcgp, tvGPc, registroGPc) }
        gpmplus.setOnClickListener { countGPM = increase(countGPM, tvmgp, tvGPm, registroGPm) }
        gpgplus.setOnClickListener { countGPG = increase(countGPG, tvggp, tvGPg, registroGPg) }
        gppplus.setOnClickListener { countGPP = increase(countGPP, tvpgp, tvGPp, registroGPp) }
        gphminus.setOnClickListener { countGPH = decrease(countGPH, tvhgp, tvGPh, registroGPh) }
        gpcminus.setOnClickListener { countGPC = decrease(countGPC, tvcgp, tvGPc, registroGPc) }
        gpmminus.setOnClickListener { countGPM = decrease(countGPM, tvmgp, tvGPm, registroGPm) }
        gpgminus.setOnClickListener { countGPG = decrease(countGPG, tvggp, tvGPg, registroGPg) }
        gppminus.setOnClickListener { countGPP = decrease(countGPP, tvpgp, tvGPp, registroGPp) }

        dhplus.setOnClickListener { countDH = increase(countDH, tvhd, tvDh, registroDh) }
        dcplus.setOnClickListener { countDC = increase(countDC, tvcd, tvDc, registroDc) }
        dmplus.setOnClickListener { countDM = increase(countDM, tvmd, tvDm, registroDm) }
        dgplus.setOnClickListener { countDG = increase(countDG, tvgd, tvDg, registroDg) }
        dpplus.setOnClickListener { countDP = increase(countDP, tvpd, tvDp, registroDp) }
        dhminus.setOnClickListener { countDH = decrease(countDH, tvhd, tvDh, registroDh) }
        dcminus.setOnClickListener { countDC = decrease(countDC, tvcd, tvDc, registroDc) }
        dmminus.setOnClickListener { countDM = decrease(countDM, tvmd, tvDm, registroDm) }
        dgminus.setOnClickListener { countDG = decrease(countDG, tvgd, tvDg, registroDg) }
        dpminus.setOnClickListener { countDP = decrease(countDP, tvpd, tvDp, registroDp) }

        clcplus.setOnClickListener { countCLC = increase(countCLC, tvccl, tvCLc, registroCLc) }
        clmplus.setOnClickListener { countCLM = increase(countCLM, tvmcl, tvCLm, registroCLm) }
        clgplus.setOnClickListener { countCLG = increase(countCLG, tvgcl, tvCLg, registroCLg) }
        clcminus.setOnClickListener { countCLC = decrease(countCLC, tvccl, tvCLc, registroCLc) }
        clmminus.setOnClickListener { countCLM = decrease(countCLM, tvmcl, tvCLm, registroCLm) }
        clgminus.setOnClickListener { countCLG = decrease(countCLG, tvgcl, tvCLg, registroCLg) }

        ccplus.setOnClickListener { countCC = increase(countCC, tvcc, tvCc, registroCc) }
        cmplus.setOnClickListener { countCM = increase(countCM, tvmc, tvCm, registroCm) }
        cgplus.setOnClickListener { countCG = increase(countCG, tvgc, tvCg, registroCg) }
        ccminus.setOnClickListener { countCC = decrease(countCC, tvcc, tvCc, registroCc) }
        cmminus.setOnClickListener { countCM = decrease(countCM, tvmc, tvCm, registroCm) }
        cgminus.setOnClickListener { countCG = decrease(countCG, tvgc, tvCg, registroCg) }

        chihplus.setOnClickListener { countCHIH = increase(countCHIH, tvhchi, tvCHIh, registroCHIh) }
        chinplus.setOnClickListener { countCHIN = increase(countCHIN, tvnchi, tvCHIn, registroCHIn) }
        chiaplus.setOnClickListener { countCHIA = increase(countCHIA, tvachi, tvCHIa, registroCHIa) }
        chihminus.setOnClickListener { countCHIH = decrease(countCHIH, tvhchi, tvCHIh, registroCHIh) }
        chinminus.setOnClickListener { countCHIN = decrease(countCHIN, tvnchi, tvCHIn, registroCHIn) }
        chiaminus.setOnClickListener { countCHIA = decrease(countCHIA, tvachi, tvCHIa, registroCHIa) }

        pvsaplus.setOnClickListener { countPVSA = increase(countPVSA, tvsapv, tvPVsa, registroPVsa) }
        pvcaplus.setOnClickListener { countPVCA = increase(countPVCA, tvcapv, tvPVca, registroPVca) }
        pvsaminus.setOnClickListener { countPVSA = decrease(countPVSA, tvsapv, tvPVsa, registroPVsa) }
        pvcaminus.setOnClickListener { countPVCA = decrease(countPVCA, tvcapv, tvPVca, registroPVca) }

        pgplus.setOnClickListener { countPG = increase(countPG, tvpg, tvPG, registroPG) }
        pgminus.setOnClickListener { countPG = decrease(countPG, tvpg, tvPG, registroPG) }
    }

    private fun increase(actual: Int, tvCantidad: TextView, tvRegistro: TextView, registro: LinearLayout): Int {
        val nuevo = actual + 1
        tvCantidad.text = nuevo.toString()
        tvRegistro.text = nuevo.toString()
        registro.visibility = if (nuevo > 0) View.VISIBLE else View.GONE
        return nuevo
    }

    private fun decrease(actual: Int, tvCantidad: TextView, tvRegistro: TextView, registro: LinearLayout): Int {
        val nuevo = if (actual > 0) actual - 1 else 0
        tvCantidad.text = nuevo.toString()
        tvRegistro.text = nuevo.toString()
        registro.visibility = if (nuevo > 0) View.VISIBLE else View.GONE
        return nuevo
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

    private fun crearRegistro(tipo: String, nombre: String, fase: String, cantidad: Int): MonitoreoEntity {
        return MonitoreoEntity(
            sesionId = obtenerSesionActual(),
            punto = ind.toString(),
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

    private fun construirRegistrosSeleccionados(): MutableList<MonitoreoEntity> {
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

        agregarRegistroSiVisible(registros, registroGSh, "Plaga", stringplagags, stringfaseh, countGSH)
        agregarRegistroSiVisible(registros, registroGSc, "Plaga", stringplagags, stringfasec, countGSC)
        agregarRegistroSiVisible(registros, registroGSm, "Plaga", stringplagags, stringfasem, countGSM)
        agregarRegistroSiVisible(registros, registroGSg, "Plaga", stringplagags, stringfaseg, countGSG)
        agregarRegistroSiVisible(registros, registroGSp, "Plaga", stringplagags, stringfasep, countGSP)

        agregarRegistroSiVisible(registros, registroGPh, "Plaga", stringplagagp, stringfaseh, countGPH)
        agregarRegistroSiVisible(registros, registroGPc, "Plaga", stringplagagp, stringfasec, countGPC)
        agregarRegistroSiVisible(registros, registroGPm, "Plaga", stringplagagp, stringfasem, countGPM)
        agregarRegistroSiVisible(registros, registroGPg, "Plaga", stringplagagp, stringfaseg, countGPG)
        agregarRegistroSiVisible(registros, registroGPp, "Plaga", stringplagagp, stringfasep, countGPP)

        agregarRegistroSiVisible(registros, registroDh, "Plaga", stringplagad, stringfaseh, countDH)
        agregarRegistroSiVisible(registros, registroDc, "Plaga", stringplagad, stringfasec, countDC)
        agregarRegistroSiVisible(registros, registroDm, "Plaga", stringplagad, stringfasem, countDM)
        agregarRegistroSiVisible(registros, registroDg, "Plaga", stringplagad, stringfaseg, countDG)
        agregarRegistroSiVisible(registros, registroDp, "Plaga", stringplagad, stringfasep, countDP)

        agregarRegistroSiVisible(registros, registroGDCh, "Plaga", stringplagagdc, stringfaseh, countGDCH)
        agregarRegistroSiVisible(registros, registroGDCc, "Plaga", stringplagagdc, stringfasec, countGDCC)
        agregarRegistroSiVisible(registros, registroGDCm, "Plaga", stringplagagdc, stringfasem, countGDCM)
        agregarRegistroSiVisible(registros, registroGDCg, "Plaga", stringplagagdc, stringfaseg, countGDCG)
        agregarRegistroSiVisible(registros, registroGDCp, "Plaga", stringplagagdc, stringfasep, countGDCP)

        agregarRegistroSiVisible(registros, registroGDFh, "Plaga", stringplagagdf, stringfaseh, countGDFH)
        agregarRegistroSiVisible(registros, registroGDFc, "Plaga", stringplagagdf, stringfasec, countGDFC)
        agregarRegistroSiVisible(registros, registroGDFm, "Plaga", stringplagagdf, stringfasem, countGDFM)
        agregarRegistroSiVisible(registros, registroGDFg, "Plaga", stringplagagdf, stringfaseg, countGDFG)
        agregarRegistroSiVisible(registros, registroGDFp, "Plaga", stringplagagdf, stringfasep, countGDFP)

        agregarRegistroSiVisible(registros, registroCLc, "Plaga", stringplagacl, stringfasec, countCLC)
        agregarRegistroSiVisible(registros, registroCLm, "Plaga", stringplagacl, stringfasem, countCLM)
        agregarRegistroSiVisible(registros, registroCLg, "Plaga", stringplagacl, stringfaseg, countCLG)

        agregarRegistroSiVisible(registros, registroCc, "Plaga", stringplagac, stringfasec, countCC)
        agregarRegistroSiVisible(registros, registroCm, "Plaga", stringplagac, stringfasem, countCM)
        agregarRegistroSiVisible(registros, registroCg, "Plaga", stringplagac, stringfaseg, countCG)

        agregarRegistroSiVisible(registros, registroCHIh, "Plaga", stringplagachi, stringfaseh, countCHIH)
        agregarRegistroSiVisible(registros, registroCHIn, "Plaga", stringplagachi, stringfasen, countCHIN)
        agregarRegistroSiVisible(registros, registroCHIa, "Plaga", stringplagachi, stringfasea, countCHIA)

        agregarRegistroSiVisible(registros, registroPVsa, "Plaga", stringplagapv, stringfasesa, countPVSA)
        agregarRegistroSiVisible(registros, registroPVca, "Plaga", stringplagapv, stringfaseca, countPVCA)

        agregarRegistroSiVisible(registros, registroPG, "Plaga", stringplagapg, stringfasena, countPG)

        return registros
    }

    private fun abrirMapaDespuesDeGuardar() {
        stopLocationUpdates()
        val intent = Intent(this, Pantalla_registromapa::class.java)
        intent.putExtra("ind_value", ind)
        intent.putExtra("cultivo", Cultivo)
        startActivity(intent)
        finish()
    }

    private fun showadvertence() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Advertencia")
        builder.setMessage("Esta opción indica que no haz encontrado ninguna plaga\n¿Quieres continuar?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            val registro = MonitoreoEntity(
                sesionId = obtenerSesionActual(),
                punto = ind.toString(),
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

    private fun getLastLocation(callback: ((Double, Double) -> Unit)? = null) {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
                tvUbicacion.text = "Lat: ${latitud ?: ""}, Lon: ${longitud ?: ""}"
                callback?.invoke(latitud!!, longitud!!)
            }
        }
    }

    private fun startLocationUpdates() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerFechaYHora(): String {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatoFecha.format(calendario.time)
    }

    private fun getFecha(): String {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatoFecha.format(calendario.time)
    }

    private fun getHora(): String {
        val calendario = Calendar.getInstance()
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return formatoHora.format(calendario.time)
    }

    private fun showMessage(mostrarMsj: String) {
        Toast.makeText(applicationContext, mostrarMsj, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "No se puede volver en esta Pantalla.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
    }
}