package com.example.apptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.app.AlertDialog

import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
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
import android.view.ViewGroup
import com.google.android.gms.location.Priority

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.example.apptest.data.AppDatabase
import com.example.apptest.data.MonitoreoEntity
import kotlinx.coroutines.launch
import java.util.UUID


class pantalla_plagasberries : AppCompatActivity() {
    /* -----------------------------------------------------
    |
    |   DEFINIMOS LAS VARIABLES DE LA ACTIVITY
    |
     ------------------------------------------------------*/
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
//    private val PERMISSION_REQUEST_CODE = 1001
    private var latitud: Double? = null
    private var longitud: Double? = null
/*    private var locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 3000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Actualiza la ubicación actual
                val currentLocation = LatLng(location.latitude, location.longitude)
                // Realiza acciones con la nueva ubicación
            }
        }
    }*/
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var FyH: String
    private lateinit var Fecha: String
    private lateinit var Hora: String
    private lateinit var db: AppDatabase

    private var saveFileLauncher: ActivityResultLauncher<Intent>? = null
    init {
        setupSaveFileLauncher()
    }
    private lateinit var idPlague: TextView;
    private lateinit var botonAR : ImageButton; private lateinit var botonMV : ImageButton; private lateinit var botonT : ImageButton; private lateinit var botonPV : ImageButton; private lateinit var botonCL : ImageButton; private lateinit var botonGC : ImageButton; private lateinit var botonHZ : ImageButton; private lateinit var botonMB : ImageButton
    private lateinit var cAR: LinearLayout; private lateinit var cMV: LinearLayout; private lateinit var cT: LinearLayout; private lateinit var cMB: LinearLayout; private lateinit var cPV: LinearLayout; private lateinit var cCL: LinearLayout; private lateinit var cGC: LinearLayout; private lateinit var cHZ: LinearLayout

    private lateinit var registroARh: LinearLayout ; private lateinit var registroARn: LinearLayout ; private lateinit var registroARa: LinearLayout
    private lateinit var registroMVh: LinearLayout ; private lateinit var registroMVn: LinearLayout ; private lateinit var registroMVa: LinearLayout
    private lateinit var registroGCh: LinearLayout ; private lateinit var registroGCn: LinearLayout ; private lateinit var registroGCa: LinearLayout
    private lateinit var registroTh: LinearLayout; private lateinit var registroTn: LinearLayout; private lateinit var registroTa: LinearLayout
    private lateinit var registroMBh: LinearLayout; private lateinit var registroMBn: LinearLayout; private lateinit var registroMBa: LinearLayout
    private lateinit var registroPVsa: LinearLayout; private lateinit var registroPVca: LinearLayout
    private lateinit var registroCLc: LinearLayout; private lateinit var registroCLm: LinearLayout; private lateinit var registroCLg: LinearLayout
    private lateinit var registroHZh: LinearLayout; private lateinit var registroHZc: LinearLayout; private lateinit var registroHZm: LinearLayout; private lateinit var registroHZg: LinearLayout; private lateinit var registroHZp: LinearLayout

    private lateinit var botonOtrasPlagas: Button
    private lateinit var cajaPlagasPrincipales: LinearLayout
    private lateinit var cajaOtrasPlagas: LinearLayout
    private lateinit var msjPlagas: TextView

    private lateinit var arhplus: ImageButton; private lateinit var arnplus: ImageButton; private lateinit var araplus: ImageButton; private lateinit var arhminus: ImageButton; private lateinit var arnminus: ImageButton; private lateinit var araminus: ImageButton
    private lateinit var mvhplus: ImageButton; private lateinit var mvnplus: ImageButton; private lateinit var mvaplus: ImageButton; private lateinit var mvhminus: ImageButton; private lateinit var mvnminus: ImageButton; private lateinit var mvaminus: ImageButton
    private lateinit var gchplus: ImageButton; private lateinit var gcnplus: ImageButton; private lateinit var gcaplus: ImageButton; private lateinit var gchminus: ImageButton; private lateinit var gcnminus: ImageButton; private lateinit var gcaminus: ImageButton
    private lateinit var thminus: ImageButton ; private lateinit var tnminus: ImageButton ; private lateinit var taminus: ImageButton ; private lateinit var thplus: ImageButton ; private lateinit var tnplus: ImageButton ; private lateinit var taplus: ImageButton
    private lateinit var mbhminus: ImageButton ; private lateinit var mbnminus: ImageButton ; private lateinit var mbaminus: ImageButton ;private lateinit var mbhplus: ImageButton ; private lateinit var mbnplus: ImageButton ; private lateinit var mbaplus: ImageButton
    private lateinit var pvsaplus: ImageButton; private lateinit var pvcaplus: ImageButton ; private lateinit var pvsaminus: ImageButton; private lateinit var pvcaminus: ImageButton
    private lateinit var clcplus: ImageButton; private lateinit var clmplus: ImageButton; private lateinit var clgplus: ImageButton ; private lateinit var clcminus: ImageButton; private lateinit var clmminus: ImageButton; private lateinit var clgminus: ImageButton
    private lateinit var hzhminus: ImageButton ; private lateinit var hzcminus: ImageButton ; private lateinit var hzmminus: ImageButton ; private lateinit var hzgminus: ImageButton ; private lateinit var hzpminus: ImageButton ; private lateinit var hzhplus: ImageButton ; private lateinit var hzcplus: ImageButton ; private lateinit var hzmplus: ImageButton ; private lateinit var hzgplus: ImageButton ; private lateinit var hzpplus: ImageButton

    private lateinit var tvhar: TextView ; private lateinit var tvnar: TextView ; private lateinit var tvaar: TextView
    private lateinit var tvhmv: TextView ; private lateinit var tvnmv: TextView ; private lateinit var tvamv: TextView
    private lateinit var tvhgc: TextView ; private lateinit var tvngc: TextView ; private lateinit var tvagc: TextView
    private lateinit var tvht: TextView ; private lateinit var tvnt: TextView ; private lateinit var tvat: TextView
    private lateinit var tvhmb: TextView ; private lateinit var tvnmb: TextView ; private lateinit var tvamb: TextView
    private lateinit var tvsapv: TextView; private lateinit var tvcapv: TextView
    private lateinit var tvccl: TextView; private lateinit var tvmcl: TextView; private lateinit var tvgcl: TextView
    private lateinit var tvhhz: TextView ; private lateinit var tvchz: TextView ; private lateinit var tvmhz: TextView ; private lateinit var tvghz: TextView ; private lateinit var tvphz: TextView

    private lateinit var tvARh: TextView ; private lateinit var tvARn: TextView ; private lateinit var tvARa: TextView
    private lateinit var tvMVh: TextView ; private lateinit var tvMVn: TextView ; private lateinit var tvMVa: TextView
    private lateinit var tvGCh: TextView ; private lateinit var tvGCn: TextView ; private lateinit var tvGCa: TextView
    private lateinit var tvTh: TextView ; private lateinit var tvTn: TextView ; private lateinit var tvTa: TextView
    private lateinit var tvMBh: TextView ; private lateinit var tvMBn: TextView ; private lateinit var tvMBa: TextView
    private lateinit var tvPVca: TextView; private lateinit var tvPVsa: TextView
    private lateinit var tvCLc: TextView; private lateinit var tvCLm: TextView; private lateinit var tvCLg: TextView
    private lateinit var tvHZh: TextView ; private lateinit var tvHZc: TextView ; private lateinit var tvHZm: TextView ; private lateinit var tvHZg: TextView ; private lateinit var tvHZp: TextView

    private lateinit var bNoPlaga: ImageButton
    private lateinit var botonEnvioRegistro: Button

    private lateinit var ContentButton: LinearLayout
    private lateinit var ContentReciver: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_plagasberries)
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

        
        FyH = obtenerFechaYHora()
        Fecha = getFecha()
        Hora = getHora()
        idPlague = findViewById(R.id.tv_idPlagas)
        idPlague.text = IDcount.toString()
        
        botonAR = findViewById(R.id.boton_aranaroja); botonMV = findViewById(R.id.boton_moscavinagre); botonGC = findViewById(R.id.boton_gallinaciega); botonT = findViewById(R.id.boton_trips); botonMB = findViewById(R.id.boton_mosquitablanca); botonPV = findViewById(R.id.boton_pulgonverde); botonCL = findViewById(R.id.boton_chinchelygus); botonHZ = findViewById(R.id.boton_helicoverpa)
        
        cAR = findViewById(R.id.CajaAR); cMV = findViewById(R.id.CajaMV); cGC = findViewById(R.id.CajaGC); cT = findViewById(R.id.CajaT); cMB = findViewById(R.id.CajaMB); cPV = findViewById(R.id.CajaPV); cCL = findViewById(R.id.CajaCL); cHZ = findViewById(R.id.CajaHZ)

        registroARh = findViewById(R.id.arregistroh); registroARn = findViewById(R.id.arregistrol); registroARa = findViewById(R.id.arregistroa)
        registroMVh = findViewById(R.id.mvregistroh); registroMVn = findViewById(R.id.mvregistrol); registroMVa = findViewById(R.id.mvregistroa)
        registroGCh = findViewById(R.id.gcregistroh); registroGCn = findViewById(R.id.gcregistrol); registroGCa = findViewById(R.id.gcregistroa)
        registroTh = findViewById(R.id.tregistroh); registroTn = findViewById(R.id.tregistron); registroTa = findViewById(R.id.tregistroa)
        registroMBh = findViewById(R.id.mbregistroh); registroMBn = findViewById(R.id.mbregistron); registroMBa = findViewById(R.id.mbregistroa)
        registroPVca = findViewById(R.id.pvregistroca); registroPVsa = findViewById(R.id.pvregistrosa)
        registroCLc = findViewById(R.id.clregistroc); registroCLm = findViewById(R.id.clregistrom); registroCLg = findViewById(R.id.clregistrog)
        registroHZh = findViewById(R.id.hzregistroh); registroHZc = findViewById(R.id.hzregistroc); registroHZm = findViewById(R.id.hzregistrom); registroHZg = findViewById(R.id.hzregistrog); registroHZp = findViewById(R.id.hzregistrop)

        botonOtrasPlagas = findViewById(R.id.boton_otrasplagas)
        msjPlagas = findViewById(R.id.tvPlagas)
        cajaOtrasPlagas = findViewById(R.id.Caja_otrasplagas)
        cajaPlagasPrincipales = findViewById(R.id.Caja_plagasprincipales)
        
        arhplus = findViewById(R.id.ib_huevecillomasAR); arnplus = findViewById(R.id.ib_ninfamasAR); araplus = findViewById(R.id.ib_adultomasAR); arhminus = findViewById(R.id.ib_huevecillomenosAR); arnminus = findViewById(R.id.ib_ninfamenosAR); araminus = findViewById(R.id.ib_adultomenosAR)
        mvhplus = findViewById(R.id.ib_huevecillomasMV); mvnplus = findViewById(R.id.ib_ninfamasMV); mvaplus = findViewById(R.id.ib_adultomasMV); mvhminus = findViewById(R.id.ib_huevecillomenosMV); mvnminus = findViewById(R.id.ib_ninfamenosMV); mvaminus = findViewById(R.id.ib_adultomenosMV)
        gchplus = findViewById(R.id.ib_huevecillomasGC); gcnplus = findViewById(R.id.ib_ninfamasGC); gcaplus = findViewById(R.id.ib_adultomasGC); gchminus = findViewById(R.id.ib_huevecillomenosGC); gcnminus = findViewById(R.id.ib_ninfamenosGC); gcaminus = findViewById(R.id.ib_adultomenosGC)
        thplus = findViewById(R.id.ib_huevecillomasT); tnplus = findViewById(R.id.ib_ninfamasT); taplus = findViewById(R.id.ib_adultomasT); thminus = findViewById(R.id.ib_huevecillomenosT); tnminus = findViewById(R.id.ib_ninfamenosT); taminus = findViewById(R.id.ib_adultomenosT)
        mbhplus = findViewById(R.id.ib_huevecillomasMB); mbnplus = findViewById(R.id.ib_ninfamasMB); mbaplus = findViewById(R.id.ib_adultomasMB); mbhminus = findViewById(R.id.ib_huevecillomenosMB); mbnminus = findViewById(R.id.ib_ninfamenosMB); mbaminus = findViewById(R.id.ib_adultomenosMB)
        pvsaplus = findViewById(R.id.ib_sinalasmasPV); pvcaplus = findViewById(R.id.ib_conalasmasPV); pvsaminus = findViewById(R.id.ib_sinalasmenosPV); pvcaminus = findViewById(R.id.ib_conalasmenosPV)
        clcplus = findViewById(R.id.ib_chicomasCL); clmplus = findViewById(R.id.ib_medianomasCL); clgplus = findViewById(R.id.ib_grandemasCL); clcminus = findViewById(R.id.ib_chicomenosCL); clmminus = findViewById(R.id.ib_medianomenosCL); clgminus = findViewById(R.id.ib_grandemenosCL)
        hzhplus = findViewById(R.id.ib_huevecillomasHZ); hzcplus = findViewById(R.id.ib_chicomasHZ); hzmplus = findViewById(R.id.ib_medianomasHZ); hzgplus = findViewById(R.id.ib_grandemasHZ); hzpplus = findViewById(R.id.ib_pupamasHZ); hzhminus = findViewById(R.id.ib_huevecillomenosHZ); hzcminus = findViewById(R.id.ib_chicomenosHZ); hzmminus = findViewById(R.id.ib_medianomenosHZ); hzgminus = findViewById(R.id.ib_grandemenosHZ); hzpminus = findViewById(R.id.ib_pupamenosHZ)

        tvhar = findViewById(R.id.cantidad_hAR); tvnar = findViewById(R.id.cantidad_nAR); tvaar = findViewById(R.id.cantidad_aAR)
        tvhmv = findViewById(R.id.cantidad_hMV); tvnmv = findViewById(R.id.cantidad_nMV); tvamv = findViewById(R.id.cantidad_aMV)
        tvhgc = findViewById(R.id.cantidad_hGC); tvngc = findViewById(R.id.cantidad_nGC); tvagc = findViewById(R.id.cantidad_aGC)
        tvht = findViewById(R.id.cantidad_hT); tvnt = findViewById(R.id.cantidad_nT); tvat = findViewById(R.id.cantidad_aT)
        tvhmb = findViewById(R.id.cantidad_hMB); tvnmb = findViewById(R.id.cantidad_nMB); tvamb = findViewById(R.id.cantidad_aMB)
        tvcapv = findViewById(R.id.cantidad_caPV); tvsapv = findViewById(R.id.cantidad_saPV)
        tvccl = findViewById(R.id.cantidad_cCL); tvmcl = findViewById(R.id.cantidad_mCL); tvgcl = findViewById(R.id.cantidad_gCL)
        tvhhz = findViewById(R.id.cantidad_hHZ); tvchz = findViewById(R.id.cantidad_cHZ); tvmhz = findViewById(R.id.cantidad_mHZ); tvghz = findViewById(R.id.cantidad_gHZ); tvphz = findViewById(R.id.cantidad_pHZ)

        tvARh = findViewById(R.id.tv_arh); tvARn = findViewById(R.id.tv_arl); tvARa = findViewById(R.id.tv_ara)
        tvMVh = findViewById(R.id.tv_mvh); tvMVn = findViewById(R.id.tv_mvl); tvMVa = findViewById(R.id.tv_mva)
        tvGCh = findViewById(R.id.tv_gch); tvGCn = findViewById(R.id.tv_gcl); tvGCa = findViewById(R.id.tv_gca)
        tvHZh= findViewById(R.id.tv_hzh) ;  tvHZc= findViewById(R.id.tv_hzc) ;  tvHZm= findViewById(R.id.tv_hzm) ;  tvHZg= findViewById(R.id.tv_hzg) ;  tvHZp= findViewById(R.id.tv_hzp)
        tvTh = findViewById(R.id.tv_th); tvTn = findViewById(R.id.tv_tn); tvTa = findViewById(R.id.tv_ta)
        tvMBh = findViewById(R.id.tv_mbh); tvMBn = findViewById(R.id.tv_mbn); tvMBa = findViewById(R.id.tv_mba)
        tvCLc = findViewById(R.id.tv_clc); tvCLm = findViewById(R.id.tv_clm); tvCLg = findViewById(R.id.tv_clg)
        tvPVsa = findViewById(R.id.tv_pvsa); tvPVca = findViewById(R.id.tv_pvca)
        
        bNoPlaga = findViewById(R.id.boton_noplaga)
        botonEnvioRegistro = findViewById(R.id.b_EnviarPlagas)

        ContentButton = findViewById(R.id.ContenedorBoton)
        ContentReciver = findViewById(R.id.ContenedorReciver)
        
        val stringplagaar = getString(R.string.plaga_aranaroja)
        val stringplagamv = getString(R.string.plaga_moscavinagre)
        val stringplagat = getString(R.string.plaga_trips)
        val stringplagamb = getString(R.string.plaga_mosquitablanca)
        val stringplagapv = getString(R.string.plaga_pulgonverde)
        val stringplagacl = getString(R.string.plaga_chinchelygus)
        val stringplagagc = getString(R.string.plaga_gallinaciega)
        val stringplagahz = getString(R.string.plaga_helicoverpa)

        val stringfaseh = getString(R.string.fase_huevecillo)
        val stringfasec = getString(R.string.fase_chico)
        val stringfasem = getString(R.string.fase_mediano)
        val stringfaseg = getString(R.string.fase_grande)
        val stringfasep = getString(R.string.fase_pupa)
        val stringfasen = getString(R.string.fase_ninfa)
        val stringfasel = getString(R.string.fase_larva)
        val stringfasea = getString(R.string.fase_adulto)
        val stringfasena = getString(R.string.msj_NA)
        val stringfasesa = getString(R.string.fase_sinalas)
        val stringfaseca = getString(R.string.fase_conalas)
        
        val buttonsPlague: List<ImageButton> = listOf(botonAR, botonMV, botonGC, botonT, botonHZ, botonPV, botonCL, botonMB)
        val registersPlague: List<LinearLayout> = listOf(cAR, cMV, cGC, cT, cHZ, cPV, cCL, cMB)

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
        
        var current_icon: Int = 0
        val ic_add =  resources.getDrawable(R.drawable.ic_add); val ic_hide = resources.getDrawable(R.drawable.ic_hide)

        botonOtrasPlagas.setOnClickListener {
            if (current_icon == 0) {
                botonOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_hide, null, null, null)
                current_icon = 0
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
                //¿Cómo podría implementar para que al clickearse de nuevo, en este espacio, ContentButton vuelva a su posición original?
                ContentReciver.removeView(ContentButton)

                // Agrega ContentButton de nuevo en su posición original dentro de cajaPlagasPrincipales
                cajaPlagasPrincipales.addView(ContentButton, 0)

            }
        }
        
        arhplus.setOnClickListener { countARH = increase(countARH, tvhar, tvARh, registroARh) };  arnplus.setOnClickListener { countARN = increase(countARN, tvnar, tvARn, registroARn) };  araplus.setOnClickListener { countARA = increase(countARA, tvaar, tvARa, registroARa) };  arhminus.setOnClickListener { countARH = decrease(countARH, tvhar, tvARh, registroARh) };  arnminus.setOnClickListener { countARN = decrease(countARN, tvnar, tvARn, registroARn) };  araminus.setOnClickListener { countARA = decrease(countARA, tvaar, tvARa, registroARa) }
        mvhplus.setOnClickListener { countMVH = increase(countMVH, tvhmv, tvMVh, registroMVh) };  mvnplus.setOnClickListener { countMVN = increase(countMVN, tvnmv, tvMVn, registroMVn) };  mvaplus.setOnClickListener { countMVA = increase(countMVA, tvamv, tvMVa, registroMVa) };  mvhminus.setOnClickListener { countMVH = decrease(countMVH, tvhmv, tvMVh, registroMVh) };  mvnminus.setOnClickListener { countMVN = decrease(countMVN, tvnmv, tvMVn, registroMVn) };  mvaminus.setOnClickListener { countMVA = decrease(countMVA, tvamv, tvMVa, registroMVa) }
        gchplus.setOnClickListener { countGCH = increase(countGCH, tvhgc, tvGCh, registroGCh) };  gcnplus.setOnClickListener { countGCN = increase(countGCN, tvngc, tvGCn, registroGCn) };  gcaplus.setOnClickListener { countGCA = increase(countGCA, tvagc, tvGCa, registroGCa) };  gchminus.setOnClickListener { countGCH = decrease(countGCH, tvhgc, tvGCh, registroGCh) };  gcnminus.setOnClickListener { countGCN = decrease(countGCN, tvngc, tvGCn, registroGCn) };  gcaminus.setOnClickListener { countGCA = decrease(countGCA, tvagc, tvGCa, registroGCa) }
        hzhplus.setOnClickListener { countHZH = increase(countHZH, tvhhz, tvHZh, registroHZh) }; hzcplus.setOnClickListener { countHZC = increase(countHZC, tvchz, tvHZc, registroHZc) }; hzmplus.setOnClickListener { countHZM = increase(countHZM, tvmhz, tvHZm, registroHZm) }; hzgplus.setOnClickListener { countHZG = increase(countHZG, tvghz, tvHZg, registroHZg) }; hzpplus.setOnClickListener { countHZP = increase(countHZP, tvphz, tvHZp, registroHZp) }; hzhminus.setOnClickListener { countHZH = decrease(countHZH, tvhhz, tvHZh, registroHZh) }; hzcminus.setOnClickListener { countHZC = decrease(countHZC, tvchz, tvHZc, registroHZc) }; hzmminus.setOnClickListener { countHZM = decrease(countHZM, tvmhz, tvHZm, registroHZm) }; hzgminus.setOnClickListener { countHZG = decrease(countHZG, tvghz, tvHZg, registroHZg) }; hzpminus.setOnClickListener { countHZP = decrease(countHZP, tvphz, tvHZp, registroHZp) }
        thplus.setOnClickListener { countTH = increase(countTH, tvht, tvTh, registroTh) };tnplus.setOnClickListener { countTN = increase(countTN, tvnt, tvTn, registroTn) };taplus.setOnClickListener { countTA = increase(countTA, tvat, tvTa, registroTa) };thminus.setOnClickListener { countTH = decrease(countTH, tvht, tvTh, registroTh) };tnminus.setOnClickListener { countTN = decrease(countTN, tvnt, tvTn, registroTn) };taminus.setOnClickListener { countTA = decrease(countTA, tvat, tvTa, registroTa) }
        mbhplus.setOnClickListener { countMBH = increase(countMBH, tvhmb, tvMBh, registroMBh) };mbnplus.setOnClickListener { countMBN = increase(countMBN, tvnmb, tvMBn, registroMBn) };mbaplus.setOnClickListener { countMBA = increase(countMBA, tvamb, tvMBa, registroMBa) };mbhminus.setOnClickListener { countMBH = decrease(countMBH, tvhmb, tvMBh, registroMBh) };mbnminus.setOnClickListener { countMBN = decrease(countMBN, tvnmb, tvMBn, registroMBn) };mbaminus.setOnClickListener { countMBA = decrease(countMBA, tvamb, tvMBa, registroMBa) }
        clcplus.setOnClickListener { countCLC = increase(countCLC, tvccl, tvCLc, registroCLc) }; clmplus.setOnClickListener { countCLM = increase(countCLM, tvmcl, tvCLm, registroCLm) }; clgplus.setOnClickListener { countCLG = increase(countCLG, tvgcl, tvCLg, registroCLg) }; clcminus.setOnClickListener { countCLC = decrease(countCLC, tvccl, tvCLc, registroCLc) }; clmminus.setOnClickListener { countCLM = decrease(countCLM, tvmcl, tvCLm, registroCLm) }; clgminus.setOnClickListener { countCLG = decrease(countCLG, tvgcl, tvCLg, registroCLg) }
        pvsaplus.setOnClickListener { countPVSA = increase(countPVSA, tvsapv, tvPVsa, registroPVsa) }; pvcaplus.setOnClickListener { countPVCA = increase(countPVCA, tvcapv, tvPVca, registroPVca) }; pvsaminus.setOnClickListener { countPVSA = decrease(countPVSA, tvsapv, tvPVsa, registroPVsa) }; pvcaminus.setOnClickListener { countPVCA = decrease(countPVCA, tvcapv, tvPVca, registroPVca) }

        bNoPlaga.setOnClickListener {
            showadvertence()
        }
        botonEnvioRegistro.setOnClickListener {
            val registros = mutableListOf<MonitoreoEntity>()

            agregarRegistroSiVisible(registros, registroHZh, "Plaga", stringplagahz, stringfaseh, countHZH)
            agregarRegistroSiVisible(registros, registroHZc, "Plaga", stringplagahz, stringfasec, countHZC)
            agregarRegistroSiVisible(registros, registroHZm, "Plaga", stringplagahz, stringfasem, countHZM)
            agregarRegistroSiVisible(registros, registroHZg, "Plaga", stringplagahz, stringfaseg, countHZG)
            agregarRegistroSiVisible(registros, registroHZp, "Plaga", stringplagahz, stringfasep, countHZP)

            agregarRegistroSiVisible(registros, registroTh, "Plaga", stringplagat, stringfaseh, countTH)
            agregarRegistroSiVisible(registros, registroTn, "Plaga", stringplagat, stringfasen, countTN)
            agregarRegistroSiVisible(registros, registroTa, "Plaga", stringplagat, stringfasea, countTA)

            agregarRegistroSiVisible(registros, registroMBh, "Plaga", stringplagamb, stringfaseh, countMBH)
            agregarRegistroSiVisible(registros, registroMBn, "Plaga", stringplagamb, stringfasen, countMBN)
            agregarRegistroSiVisible(registros, registroMBa, "Plaga", stringplagamb, stringfasea, countMBA)

            agregarRegistroSiVisible(registros, registroPVsa, "Plaga", stringplagapv, stringfasesa, countPVSA)
            agregarRegistroSiVisible(registros, registroPVca, "Plaga", stringplagapv, stringfaseca, countPVCA)

            agregarRegistroSiVisible(registros, registroCLc, "Plaga", stringplagacl, stringfasec, countCLC)
            agregarRegistroSiVisible(registros, registroCLm, "Plaga", stringplagacl, stringfasem, countCLM)
            agregarRegistroSiVisible(registros, registroCLg, "Plaga", stringplagacl, stringfaseg, countCLG)

            agregarRegistroSiVisible(registros, registroARh, "Plaga", stringplagaar, stringfaseh, countARH)
            agregarRegistroSiVisible(registros, registroARn, "Plaga", stringplagaar, stringfasen, countARN)
            agregarRegistroSiVisible(registros, registroARa, "Plaga", stringplagaar, stringfasea, countARA)

            agregarRegistroSiVisible(registros, registroMVh, "Plaga", stringplagamv, stringfaseh, countMVH)
            agregarRegistroSiVisible(registros, registroMVn, "Plaga", stringplagamv, stringfasen, countMVN)
            agregarRegistroSiVisible(registros, registroMVa, "Plaga", stringplagamv, stringfasea, countMVA)

            agregarRegistroSiVisible(registros, registroGCh, "Plaga", stringplagagc, stringfaseh, countGCH)
            agregarRegistroSiVisible(registros, registroGCn, "Plaga", stringplagagc, stringfasen, countGCN)
            agregarRegistroSiVisible(registros, registroGCa, "Plaga", stringplagagc, stringfasea, countGCA)

            if (registros.isEmpty()) {
                showMessage("Favor de ingresar un registro.")
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
        val intent = Intent(this@pantalla_plagasberries, Pantalla_registromapa::class.java)
        intent.putExtra("ind_value", IDcount)
        intent.putExtra("cultivo", Cultivo)
        startActivity(intent)
        finish()
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
    //Definimos una funcion para obtener la fecha y la hora del dispositivo
    fun obtenerFechaYHora(): String {
        val calendario = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatoFecha.format(calendario.time)
    }
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
    //Definimos funcion poara dejar de actualizar la localización
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    override fun onBackPressed() {
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, "No se puede volver en esta Pantalla.", duracion)
        toast.show()
    }
}