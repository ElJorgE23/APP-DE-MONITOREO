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

class pantalla_plagasmaiz : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var latitud: Double? = null
    private var longitud: Double? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
/*    private var locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 3000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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
    private lateinit var FyH: String
    private lateinit var Fecha: String
    private lateinit var Hora: String
    private lateinit var db: AppDatabase
    private var saveFileLauncher: ActivityResultLauncher<Intent>? = null
    init {
        setupSaveFileLauncher()
    }
    //Iniciamos los botones de cada una de las plagas 
    private lateinit var idPlague: TextView
    private lateinit var botonGC: ImageButton; private lateinit var botonGA: ImageButton; private lateinit var botonD: ImageButton; private lateinit var botonGCOG: ImageButton; private lateinit var botonGS: ImageButton; private lateinit var botonAR: ImageButton; private lateinit var botonChi: ImageButton; private lateinit var botonT: ImageButton; private lateinit var botonPM: ImageButton
    private lateinit var cGC: LinearLayout; private lateinit var cGA: LinearLayout; private lateinit var cD: LinearLayout; private lateinit var cGCOG: LinearLayout; private lateinit var cGS: LinearLayout; private lateinit var cAR: LinearLayout; private lateinit var cChi: LinearLayout; private lateinit var cT: LinearLayout; private lateinit var cPM: LinearLayout
    //Variables para visualizar los registros
    private lateinit var registroGCh: LinearLayout ; private lateinit var registroGCn: LinearLayout ; private lateinit var registroGCa: LinearLayout
    private lateinit var registroGAh: LinearLayout ; private lateinit var registroGAn: LinearLayout ; private lateinit var registroGAa: LinearLayout
    private lateinit var registroDh: LinearLayout; private lateinit var registroDc: LinearLayout; private lateinit var registroDm: LinearLayout; private lateinit var registroDg: LinearLayout; private lateinit var registroDp: LinearLayout
    private lateinit var registroGCOGh: LinearLayout; private lateinit var registroGCOGn: LinearLayout; private lateinit var registroGCOGa: LinearLayout
    private lateinit var registroGSh: LinearLayout; private lateinit var registroGSc: LinearLayout; private lateinit var registroGSm: LinearLayout; private lateinit var registroGSg: LinearLayout; private lateinit var registroGSp: LinearLayout
    private lateinit var registroARh: LinearLayout ; private lateinit var registroARn: LinearLayout ; private lateinit var registroARa: LinearLayout
    private lateinit var registroCHIh: LinearLayout; private lateinit var registroCHIn: LinearLayout; private lateinit var registroCHIa: LinearLayout //chicharrita
    private lateinit var registroTh: LinearLayout; private lateinit var registroTn: LinearLayout; private lateinit var registroTa: LinearLayout     //Trips
    private lateinit var registroPMh: LinearLayout ; private lateinit var registroPMn: LinearLayout ; private lateinit var registroPMa: LinearLayout    //Picudo Maiz
    //Variables para visualizar los botones para agregar o quitar
    private lateinit var gchplus: ImageButton; private lateinit var gcnplus: ImageButton; private lateinit var gcaplus: ImageButton; private lateinit var gchminus: ImageButton; private lateinit var gcnminus: ImageButton; private lateinit var gcaminus: ImageButton
    private lateinit var gshplus: ImageButton; private lateinit var gscplus: ImageButton; private lateinit var gsmplus: ImageButton; private lateinit var gsgplus: ImageButton; private lateinit var gspplus: ImageButton; private lateinit var gshminus: ImageButton; private lateinit var gscminus: ImageButton; private lateinit var gsmminus: ImageButton; private lateinit var gsgminus: ImageButton; private lateinit var gspminus: ImageButton
    private lateinit var dhplus: ImageButton; private lateinit var dcplus: ImageButton; private lateinit var dmplus: ImageButton; private lateinit var dgplus: ImageButton; private lateinit var dpplus: ImageButton; private lateinit var dhminus: ImageButton; private lateinit var dcminus: ImageButton; private lateinit var dmminus: ImageButton; private lateinit var dgminus: ImageButton; private lateinit var dpminus: ImageButton
    private lateinit var gahplus: ImageButton; private lateinit var ganplus: ImageButton; private lateinit var gaaplus: ImageButton; private lateinit var gahminus: ImageButton; private lateinit var ganminus: ImageButton; private lateinit var gaaminus: ImageButton
    private lateinit var gcoghplus: ImageButton; private lateinit var gcognplus: ImageButton; private lateinit var gcogaplus: ImageButton; private lateinit var gcoghminus: ImageButton; private lateinit var gcognminus: ImageButton; private lateinit var gcogaminus: ImageButton
    private lateinit var arhplus: ImageButton; private lateinit var arnplus: ImageButton; private lateinit var araplus: ImageButton; private lateinit var arhminus: ImageButton; private lateinit var arnminus: ImageButton; private lateinit var araminus: ImageButton
    private lateinit var chihplus: ImageButton; private lateinit var chinplus: ImageButton; private lateinit var chiaplus: ImageButton      //chicharrita
    private lateinit var chihminus: ImageButton; private lateinit var chinminus: ImageButton; private lateinit var chiaminus: ImageButton   //chicharrita
    private lateinit var thplus: ImageButton ; private lateinit var tnplus: ImageButton ; private lateinit var taplus: ImageButton          //Trips
    private lateinit var thminus: ImageButton ; private lateinit var tnminus: ImageButton ; private lateinit var taminus: ImageButton       //Trips
    private lateinit var pmhplus: ImageButton; private lateinit var pmnplus: ImageButton; private lateinit var pmaplus: ImageButton         //Picudo Maiz
    private lateinit var pmhminus: ImageButton; private lateinit var pmnminus: ImageButton; private lateinit var pmaminus: ImageButton      //Picudo Maiz
    //Variables para visuializar la cantidad
    private lateinit var tvhar: TextView ; private lateinit var tvnar: TextView ; private lateinit var tvaar: TextView
    private lateinit var tvhgc: TextView ; private lateinit var tvngc: TextView ; private lateinit var tvagc: TextView
    private lateinit var tvhd: TextView; private lateinit var tvcd: TextView; private lateinit var tvmd: TextView; private lateinit var tvgd: TextView; private lateinit var tvpd: TextView
    private lateinit var tvhgs: TextView; private lateinit var tvcgs: TextView; private lateinit var tvmgs: TextView; private lateinit var tvggs: TextView; private lateinit var tvpgs: TextView
    private lateinit var tvhga: TextView ; private lateinit var tvnga: TextView ; private lateinit var tvaga: TextView
    private lateinit var tvhgcog: TextView ; private lateinit var tvngcog: TextView ; private lateinit var tvagcog: TextView
    private lateinit var tvhchi: TextView; private lateinit var tvnchi: TextView; private lateinit var tvachi: TextView         //chicharrita
    private lateinit var tvht: TextView ; private lateinit var tvnt: TextView ; private lateinit var tvat: TextView             //Trips
    private lateinit var tvhpm: TextView ; private lateinit var tvnpm: TextView ; private lateinit var tvapm: TextView          //Picudo Maiz
    //Variables para cantidad en el registro
    private lateinit var tvARh: TextView ; private lateinit var tvARn: TextView ; private lateinit var tvARa: TextView
    private lateinit var tvGCh: TextView ; private lateinit var tvGCn: TextView ; private lateinit var tvGCa: TextView
    private lateinit var tvDh: TextView; private lateinit var tvDc: TextView; private lateinit var tvDm: TextView; private lateinit var tvDg: TextView; private lateinit var tvDp: TextView
    private lateinit var tvGSh: TextView; private lateinit var tvGSc: TextView; private lateinit var tvGSm: TextView; private lateinit var tvGSg: TextView; private lateinit var tvGSp: TextView
    private lateinit var tvGAh: TextView ; private lateinit var tvGAn: TextView ; private lateinit var tvGAa: TextView
    private lateinit var tvGCOGh: TextView ; private lateinit var tvGCOGn: TextView ; private lateinit var tvGCOGa: TextView
    private lateinit var tvCHIh: TextView; private lateinit var tvCHIn: TextView; private lateinit var tvCHIa: TextView         //chicharrita
    private lateinit var tvTh: TextView ; private lateinit var tvTn: TextView ; private lateinit var tvTa: TextView             //Trips
    private lateinit var tvPMh: TextView ; private lateinit var tvPMn: TextView ; private lateinit var tvPMa: TextView          //Picudo Maiz

    //------------------------------------------
    private lateinit var bEnviarRegistro : Button
    private lateinit var llPlagasPrincipales: LinearLayout
    private lateinit var llOtrasPlagas: LinearLayout
    //Iniciamos las variables para el botón que contiene a otras plagas
    private lateinit var ContainerButton: LinearLayout
    private lateinit var bOtrasPlagas: Button
    private lateinit var tvPlagas: TextView
    //Variables para registrar puntos libres de plagas 
    private lateinit var bNoPlaga: ImageButton
    //Variable para el conteneder que recibe a un botón
    private lateinit var ContentReciver: LinearLayout
    private lateinit var ContentButton: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_plagasmaiz)
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


        //Establecemos la variable que guardará la fecha y la hora
        FyH = obtenerFechaYHora()
        Fecha = getFecha()
        Hora = getHora()
        //Establecemos el TextView para mostrar el id del punto en el que se está registrando
        idPlague = findViewById(R.id.tv_idPlagas); idPlague.text = IDcount.toString()
        //Establecemos los Image Buttons de las plagas
        botonAR = findViewById(R.id.boton_aranaroja); botonGC = findViewById(R.id.boton_gallinaciega); botonGA = findViewById(R.id.boton_gusanoalambre); botonD = findViewById(R.id.boton_diabrotica); botonGCOG = findViewById(R.id.boton_gusanocogollero); botonGS = findViewById(R.id.boton_gusanosoldado); botonChi = findViewById(R.id.boton_chicharrita); botonT = findViewById(R.id.boton_trips); botonPM = findViewById(R.id.boton_picudomaiz)
        //Establecemos las cajas contenedoras de cada plaga
        cGC = findViewById(R.id.CajaGC); cGA = findViewById(R.id.CajaGA); cD = findViewById(R.id.CajaD); cGCOG = findViewById(R.id.CajaGCOG); cGS = findViewById(R.id.CajaGS); cAR = findViewById(R.id.CajaAR); cChi = findViewById(R.id.CajaCHI); cT = findViewById(R.id.CajaT); cPM = findViewById(R.id.CajaPM)
        //Establecemos las cajas de los  registros para las plagas
        registroGCh = findViewById(R.id.gcregistroh); registroGCn = findViewById(R.id.gcregistrol); registroGCa = findViewById(R.id.gcregistroa)
        registroGAh = findViewById(R.id.garegistroh); registroGAn = findViewById(R.id.garegistrol); registroGAa = findViewById(R.id.garegistroa)
        registroGCOGh = findViewById(R.id.gcogregistroh); registroGCOGn = findViewById(R.id.gcogregistrol); registroGCOGa = findViewById(R.id.gcogregistroa)
        registroARh = findViewById(R.id.arregistroh); registroARn = findViewById(R.id.arregistrol); registroARa = findViewById(R.id.arregistroa)
        registroGSh = findViewById(R.id.gsregistroh); registroGSc = findViewById(R.id.gsregistroc); registroGSm = findViewById(R.id.gsregistrom); registroGSg = findViewById(R.id.gsregistrog); registroGSp = findViewById(R.id.gsregistrop)
        registroDh = findViewById(R.id.dregistroh); registroDc = findViewById(R.id.dregistroc); registroDm = findViewById(R.id.dregistrom); registroDg = findViewById(R.id.dregistrog); registroDp = findViewById(R.id.dregistrop)
        registroCHIh = findViewById(R.id.chiregistroh); registroCHIn = findViewById(R.id.chiregistron); registroCHIa = findViewById(R.id.chiregistroa)
        registroTh = findViewById(R.id.tregistroh); registroTn = findViewById(R.id.tregistron); registroTa = findViewById(R.id.tregistroa)
        registroPMh = findViewById(R.id.pmregistroh); registroPMn = findViewById(R.id.pmregistrol); registroPMa = findViewById(R.id.pmregistroa)
        
        //Boton para enviar nuestro registro
        bEnviarRegistro = findViewById(R.id.b_EnviarPlagas)
        //Definimos nuestras LinearLayouts que contienen a nuestras plagas
        llPlagasPrincipales = findViewById(R.id.Caja_plagasprincipales)
        llOtrasPlagas = findViewById(R.id.Caja_otrasplagas)
        //Definimos las variables para el boton que nos permite intercambiar de plagas
        ContainerButton = findViewById(R.id.ContenedorBoton)
        bOtrasPlagas = findViewById(R.id.boton_otrasplagas)
        tvPlagas = findViewById(R.id.tvPlagas)
        //Definimos los botones para agregar o quitar plaga
        arhplus = findViewById(R.id.ib_huevecillomasAR); arnplus = findViewById(R.id.ib_ninfamasAR); araplus = findViewById(R.id.ib_adultomasAR); arhminus = findViewById(R.id.ib_huevecillomenosAR); arnminus = findViewById(R.id.ib_ninfamenosAR); araminus = findViewById(R.id.ib_adultomenosAR)
        gchplus = findViewById(R.id.ib_huevecillomasGC); gcnplus = findViewById(R.id.ib_ninfamasGC); gcaplus = findViewById(R.id.ib_adultomasGC); gchminus = findViewById(R.id.ib_huevecillomenosGC); gcnminus = findViewById(R.id.ib_ninfamenosGC); gcaminus = findViewById(R.id.ib_adultomenosGC)
        gshplus = findViewById(R.id.ib_huevecillomasGS); gscplus = findViewById(R.id.ib_chicomasGS); gsmplus = findViewById(R.id.ib_medianomasGS); gsgplus = findViewById(R.id.ib_grandemasGS); gspplus = findViewById(R.id.ib_pupamasGS); gshminus = findViewById(R.id.ib_huevecillomenosGS); gscminus = findViewById(R.id.ib_chicomenosGS); gsmminus = findViewById(R.id.ib_medianomenosGS); gsgminus = findViewById(R.id.ib_grandemenosGS); gspminus = findViewById(R.id.ib_pupamenosGS)
        dhplus = findViewById(R.id.ib_huevecillomasD); dcplus = findViewById(R.id.ib_chicomasD); dmplus = findViewById(R.id.ib_medianomasD); dgplus = findViewById(R.id.ib_grandemasD); dpplus = findViewById(R.id.ib_pupamasD); dhminus = findViewById(R.id.ib_huevecillomenosD); dcminus = findViewById(R.id.ib_chicomenosD); dmminus = findViewById(R.id.ib_medianomenosD); dgminus = findViewById(R.id.ib_grandemenosD); dpminus = findViewById(R.id.ib_pupamenosD)
        gahplus = findViewById(R.id.ib_huevecillomasGA); ganplus = findViewById(R.id.ib_ninfamasGA); gaaplus = findViewById(R.id.ib_adultomasGA); gahminus = findViewById(R.id.ib_huevecillomenosGA); ganminus = findViewById(R.id.ib_ninfamenosGA); gaaminus = findViewById(R.id.ib_adultomenosGA)
        gcoghplus = findViewById(R.id.ib_huevecillomasGCOG); gcognplus = findViewById(R.id.ib_ninfamasGCOG); gcogaplus = findViewById(R.id.ib_adultomasGCOG); gcoghminus = findViewById(R.id.ib_huevecillomenosGCOG); gcognminus = findViewById(R.id.ib_ninfamenosGCOG); gcogaminus = findViewById(R.id.ib_adultomenosGCOG)
        chihplus = findViewById(R.id.ib_huevecillomasCHI); chinplus = findViewById(R.id.ib_ninfamasCHI); chiaplus = findViewById(R.id.ib_adultomasCHI); chihminus = findViewById(R.id.ib_huevecillomenosCHI); chinminus = findViewById(R.id.ib_ninfamenosCHI); chiaminus = findViewById(R.id.ib_adultomenosCHI)
        thplus = findViewById(R.id.ib_huevecillomasT); tnplus = findViewById(R.id.ib_ninfamasT); taplus = findViewById(R.id.ib_adultomasT); thminus = findViewById(R.id.ib_huevecillomenosT); tnminus = findViewById(R.id.ib_ninfamenosT); taminus = findViewById(R.id.ib_adultomenosT)
        pmhplus = findViewById(R.id.ib_huevecillomasPM); pmnplus = findViewById(R.id.ib_pupamasPM); pmaplus = findViewById(R.id.ib_adultomasPM); pmhminus = findViewById(R.id.ib_huevecillomenosPM); pmnminus = findViewById(R.id.ib_pupamenosPM); pmaminus = findViewById(R.id.ib_adultomenosPM) //////CORREGIR ESTO
        //Definimos los TextViews para cantidad con botones
        tvhar = findViewById(R.id.cantidad_hAR); tvnar = findViewById(R.id.cantidad_nAR); tvaar = findViewById(R.id.cantidad_aAR)
        tvhgc = findViewById(R.id.cantidad_hGC); tvngc = findViewById(R.id.cantidad_nGC); tvagc = findViewById(R.id.cantidad_aGC)
        tvhgs = findViewById(R.id.cantidad_hGS); tvcgs = findViewById(R.id.cantidad_cGS); tvmgs = findViewById(R.id.cantidad_mGS); tvggs = findViewById(R.id.cantidad_gGS); tvpgs = findViewById(R.id.cantidad_pGS)
        tvhd = findViewById(R.id.cantidad_hD); tvcd = findViewById(R.id.cantidad_chD); tvmd = findViewById(R.id.cantidad_mD); tvgd = findViewById(R.id.cantidad_gD); tvpd = findViewById(R.id.cantidad_pD)
        tvhga = findViewById(R.id.cantidad_hGA); tvnga = findViewById(R.id.cantidad_nGA); tvaga = findViewById(R.id.cantidad_aGA)
        tvhgcog = findViewById(R.id.cantidad_hGCOG); tvngcog = findViewById(R.id.cantidad_nGCOG); tvagcog = findViewById(R.id.cantidad_aGCOG)
        tvhchi = findViewById(R.id.cantidad_hCHI); tvnchi = findViewById(R.id.cantidad_nCHI); tvachi = findViewById(R.id.cantidad_aCHI)
        tvht = findViewById(R.id.cantidad_hT); tvnt = findViewById(R.id.cantidad_nT); tvat = findViewById(R.id.cantidad_aT)
        tvhpm = findViewById(R.id.cantidad_hPM); tvnpm = findViewById(R.id.cantidad_nPM); tvapm = findViewById(R.id.cantidad_aPM)
        //Definimos los TV para cantidad en el registro
        tvARh = findViewById(R.id.tv_arh); tvARn = findViewById(R.id.tv_arl); tvARa = findViewById(R.id.tv_ara)
        tvGCh = findViewById(R.id.tv_gch); tvGCn = findViewById(R.id.tv_gcl); tvGCa = findViewById(R.id.tv_gca)
        tvGSh = findViewById(R.id.tv_gsh); tvGSc = findViewById(R.id.tv_gsc); tvGSm = findViewById(R.id.tv_gsm); tvGSg = findViewById(R.id.tv_gsg); tvGSp = findViewById(R.id.tv_gsp)
        tvDh = findViewById(R.id.tv_dh); tvDc = findViewById(R.id.tv_dc); tvDm = findViewById(R.id.tv_dm); tvDg = findViewById(R.id.tv_dg); tvDp = findViewById(R.id.tv_dp)
        tvGAh = findViewById(R.id.tv_gah); tvGAn = findViewById(R.id.tv_gal); tvGAa = findViewById(R.id.tv_gaa)
        tvGCOGh = findViewById(R.id.tv_gcogh); tvGCOGn = findViewById(R.id.tv_gcogl); tvGCOGa = findViewById(R.id.tv_gcoga)
        tvCHIh = findViewById(R.id.tv_chih); tvCHIn = findViewById(R.id.tv_chin); tvCHIa = findViewById(R.id.tv_chia)
        tvTh = findViewById(R.id.tv_th); tvTn = findViewById(R.id.tv_tn); tvTa = findViewById(R.id.tv_ta)
        tvPMh = findViewById(R.id.tv_pmh); tvPMn = findViewById(R.id.tv_pml); tvPMa = findViewById(R.id.tv_pma)

        bNoPlaga = findViewById(R.id.boton_noplaga)
        ContentReciver = findViewById(R.id.ContenedorReciver)
        ContentButton = findViewById(R.id.ContenedorBoton)
        //Agregamos las strings que contiene el nombre de las plagas en el Maiz
        val strplaAR = getString(R.string.plaga_aranaroja)
        val strplaGC = getString(R.string.plaga_gallinaciega)
        val strplaGA = getString(R.string.plaga_gusanoalambre)
        val strplaD = getString(R.string.plaga_diabrotica)
        val strplaGCOG = getString(R.string.plaga_gusanocogollero)
        val strplaGS = getString(R.string.plaga_gusanosoldado)
        val strplaCHI = getString(R.string.plaga_chicharrita)
        val strplat = getString(R.string.plaga_trips)
        val strplaPM = getString(R.string.plaga_picudomaiz)
        //Agregamos las strings que contienen las diferentes fases de las plagas 
        val strfaseh = getString(R.string.fase_huevecillo)
        val strfasel = getString(R.string.fase_larva)
        val strfasea = getString(R.string.fase_adulto)
        val strfasec = getString(R.string.fase_chico)
        val strfasem = getString(R.string.fase_mediano)
        val strfaseg = getString(R.string.fase_grande)
        val strfasep = getString(R.string.fase_pupa)
        val strfasen = getString(R.string.fase_ninfa)
        
        //Definimos una lista con los botones de las plagas en Maiz
        val buttonsPlague: List<ImageButton> = listOf(botonGC, botonGA, botonD, botonGCOG, botonGS, botonAR, botonChi, botonT, botonPM)
        //Agrergamos las cajas que corresponden a estos botones, en el mismo orden a como agregamos los botones
        val registerPlague: List<LinearLayout> = listOf(cGC, cGA, cD, cGCOG, cGS, cAR, cChi, cT, cPM)
        //Definimos que la funcionalidad que al presionar un boton de una plaga aparezca el Layout que contiene los botones para agregar plagas
        buttonsPlague.forEachIndexed { index, button -> 
            button.setOnClickListener {
                registerPlague.forEachIndexed { layoutIndex, layout -> 
                    if (index == layoutIndex){
                        layout.visibility = View.VISIBLE
                    } else{
                        layout.visibility = View.GONE
                    }
                }
            }
        }
        //Definimos el botón que nos ayuda a visualizar entre un grupo de plagas u otras 
        var current_icon: Int = 0
        val ic_add = resources.getDrawable(R.drawable.ic_add); val ic_hide = resources.getDrawable(R.drawable.ic_hide)
        //Agregamos los eventos que activa el botón al ser pulsado
        bOtrasPlagas.setOnClickListener { 
            if (current_icon == 0){
                bOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_hide, null, null, null)
                current_icon = 1
                llOtrasPlagas.visibility = View.VISIBLE
                tvPlagas.text = "Plagas que atacan Follaje"
                llPlagasPrincipales.visibility = View.GONE
                (ContentButton.parent as? LinearLayout)?.removeView(ContentButton)
                ContentReciver.addView(ContentButton)
            }else{
                bOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_add, null, null, null)
                current_icon = 0
                llOtrasPlagas.visibility = View.GONE
                tvPlagas.text = "Plagas que atacan Raíz"
                llPlagasPrincipales.visibility = View.VISIBLE
                ContentReciver.removeView(ContentButton)
                llPlagasPrincipales.addView(ContentButton, 0)
            }
        }
        //Agregamos la funcionalidad de los botones que agregan o quitan cantidad de plagas cuando se hace el registro
        arhplus.setOnClickListener { countARH = increase(countARH, tvhar, tvARh, registroARh) };  arnplus.setOnClickListener { countARN = increase(countARN, tvnar, tvARn, registroARn) };  araplus.setOnClickListener { countARA = increase(countARA, tvaar, tvARa, registroARa) };  arhminus.setOnClickListener { countARH = decrease(countARH, tvhar, tvARh, registroARh) };  arnminus.setOnClickListener { countARN = decrease(countARN, tvnar, tvARn, registroARn) };  araminus.setOnClickListener { countARA = decrease(countARA, tvaar, tvARa, registroARa) }
        gchplus.setOnClickListener { countGCH = increase(countGCH, tvhgc, tvGCh, registroGCh) };  gcnplus.setOnClickListener { countGCN = increase(countGCN, tvngc, tvGCn, registroGCn) };  gcaplus.setOnClickListener { countGCA = increase(countGCA, tvagc, tvGCa, registroGCa) };  gchminus.setOnClickListener { countGCH = decrease(countGCH, tvhgc, tvGCh, registroGCh) };  gcnminus.setOnClickListener { countGCN = decrease(countGCN, tvngc, tvGCn, registroGCn) };  gcaminus.setOnClickListener { countGCA = decrease(countGCA, tvagc, tvGCa, registroGCa) }
        gshplus.setOnClickListener { countGSH = increase(countGSH, tvhgs, tvGSh, registroGSh) }; gscplus.setOnClickListener { countGSC = increase(countGSC, tvcgs, tvGSc, registroGSc) }; gsmplus.setOnClickListener { countGSM = increase(countGSM, tvmgs, tvGSm, registroGSm) }; gsgplus.setOnClickListener { countGSG = increase(countGSG, tvggs, tvGSg, registroGSg) }; gspplus.setOnClickListener { countGSP = increase(countGSP, tvpgs, tvGSp, registroGSp) }; gshminus.setOnClickListener { countGSH = decrease(countGSH, tvhgs, tvGSh, registroGSh) }; gscminus.setOnClickListener { countGSC = decrease(countGSC, tvcgs, tvGSc, registroGSc) }; gsmminus.setOnClickListener { countGSM = decrease(countGSM, tvmgs, tvGSm, registroGSm) }; gsgminus.setOnClickListener { countGSG = decrease(countGSG, tvggs, tvGSg, registroGSg) }; gspminus.setOnClickListener { countGSP = decrease(countGSP, tvpgs, tvGSp, registroGSp) }
        dhplus.setOnClickListener { countDH = increase(countDH, tvhd, tvDh, registroDh) }; dcplus.setOnClickListener { countDC = increase(countDC, tvcd, tvDc, registroDc) }; dmplus.setOnClickListener { countDM = increase(countDM, tvmd, tvDm, registroDm) }; dgplus.setOnClickListener { countDG = increase(countDG, tvgd, tvDg, registroDg) }; dpplus.setOnClickListener { countDP = increase(countDP, tvpd, tvDp, registroDp) }; dhminus.setOnClickListener { countDH = decrease(countDH, tvhd, tvDh, registroDh) }; dcminus.setOnClickListener { countDC = decrease(countDC, tvcd, tvDc, registroDc) }; dmminus.setOnClickListener { countDM = decrease(countDM, tvmd, tvDm, registroDm) }; dgminus.setOnClickListener { countDG = decrease(countDG, tvgd, tvDg, registroDg) }; dpminus.setOnClickListener { countDP = decrease(countDP, tvpd, tvDp, registroDp) }
        gahplus.setOnClickListener { countGAH = increase(countGAH, tvhga, tvGAh, registroGAh) };  ganplus.setOnClickListener { countGAN = increase(countGAN, tvnga, tvGAn, registroGAn) };  gaaplus.setOnClickListener { countGAA = increase(countGAA, tvaga, tvGAa, registroGAa) };  gahminus.setOnClickListener { countGAH = decrease(countGAH, tvhga, tvGAh, registroGAh) };  ganminus.setOnClickListener { countGAN = decrease(countGAN, tvnga, tvGAn, registroGAn) };  gaaminus.setOnClickListener { countGAA = decrease(countGAA, tvaga, tvGAa, registroGAa) }
        gcoghplus.setOnClickListener { countGCOGH = increase(countGCOGH, tvhgcog, tvGCOGh, registroGCOGh) };  gcognplus.setOnClickListener { countGCOGN = increase(countGCOGN, tvngcog, tvGCOGn, registroGCOGn) };  gcogaplus.setOnClickListener { countGCOGA = increase(countGCOGA, tvagcog, tvGCOGa, registroGCOGa) };  gcoghminus.setOnClickListener { countGCOGH = decrease(countGCOGH, tvhgcog, tvGCOGh, registroGCOGh) };  gcognminus.setOnClickListener { countGCOGN = decrease(countGCOGN, tvngcog, tvGCOGn, registroGCOGn) };  gcogaminus.setOnClickListener { countGCOGA = decrease(countGCOGA, tvagcog, tvGCOGa, registroGCOGa) }
        chihplus.setOnClickListener { countCHIH = increase(countCHIH, tvhchi, tvCHIh, registroCHIh) }; chinplus.setOnClickListener { countCHIN = increase(countCHIN, tvnchi, tvCHIn, registroCHIn) }; chiaplus.setOnClickListener { countCHIA = increase(countCHIA, tvachi, tvCHIa, registroCHIa) }; chihminus.setOnClickListener { countCHIH = decrease(countCHIH, tvhchi, tvCHIh, registroCHIh) }; chinminus.setOnClickListener { countCHIN = decrease(countCHIN, tvnchi, tvCHIn, registroCHIn) }; chiaminus.setOnClickListener { countCHIA = decrease(countCHIA, tvachi, tvCHIa, registroCHIa) }
        thplus.setOnClickListener { countTH = increase(countTH, tvht, tvTh, registroTh) };tnplus.setOnClickListener { countTN = increase(countTN, tvnt, tvTn, registroTn) };taplus.setOnClickListener { countTA = increase(countTA, tvat, tvTa, registroTa) };thminus.setOnClickListener { countTH = decrease(countTH, tvht, tvTh, registroTh) };tnminus.setOnClickListener { countTN = decrease(countTN, tvnt, tvTn, registroTn) };taminus.setOnClickListener { countTA = decrease(countTA, tvat, tvTa, registroTa) }
        pmhplus.setOnClickListener { countPMH = increase(countPMH, tvhpm, tvPMh, registroPMh) };  pmnplus.setOnClickListener { countPMN = increase(countPMN, tvnpm, tvPMn, registroPMn) };  pmaplus.setOnClickListener { countPMA = increase(countPMA, tvapm, tvPMa, registroPMa) };  pmhminus.setOnClickListener { countPMH = decrease(countPMH, tvhpm, tvPMh, registroPMh) };  pmnminus.setOnClickListener { countPMN = decrease(countPMN, tvnpm, tvPMn, registroPMn) };  pmaminus.setOnClickListener { countPMA = decrease(countPMA, tvapm, tvPMa, registroPMa) }

        //Agregamos la funcionalidad para el boton de registrar planta libre de plagas
        bNoPlaga.setOnClickListener{
            showadvertence()
        }
        bEnviarRegistro.setOnClickListener {
            val registros = mutableListOf<MonitoreoEntity>()

            agregarRegistroSiVisible(registros, registroGCh, "Plaga", strplaGC, strfaseh, countGCH)
            agregarRegistroSiVisible(registros, registroGCn, "Plaga", strplaGC, strfasel, countGCN)
            agregarRegistroSiVisible(registros, registroGCa, "Plaga", strplaGC, strfasea, countGCA)

            agregarRegistroSiVisible(registros, registroGAh, "Plaga", strplaGA, strfaseh, countGAH)
            agregarRegistroSiVisible(registros, registroGAn, "Plaga", strplaGA, strfasel, countGAN)
            agregarRegistroSiVisible(registros, registroGAa, "Plaga", strplaGA, strfasea, countGAA)

            agregarRegistroSiVisible(registros, registroGCOGh, "Plaga", strplaGCOG, strfaseh, countGCOGH)
            agregarRegistroSiVisible(registros, registroGCOGn, "Plaga", strplaGCOG, strfasel, countGCOGN)
            agregarRegistroSiVisible(registros, registroGCOGa, "Plaga", strplaGCOG, strfasea, countGCOGA)

            agregarRegistroSiVisible(registros, registroARh, "Plaga", strplaAR, strfaseh, countARH)
            agregarRegistroSiVisible(registros, registroARn, "Plaga", strplaAR, strfasel, countARN)
            agregarRegistroSiVisible(registros, registroARa, "Plaga", strplaAR, strfasea, countARA)

            agregarRegistroSiVisible(registros, registroGSh, "Plaga", strplaGS, strfaseh, countGSH)
            agregarRegistroSiVisible(registros, registroGSc, "Plaga", strplaGS, strfasec, countGSC)
            agregarRegistroSiVisible(registros, registroGSm, "Plaga", strplaGS, strfasem, countGSM)
            agregarRegistroSiVisible(registros, registroGSg, "Plaga", strplaGS, strfaseg, countGSG)
            agregarRegistroSiVisible(registros, registroGSp, "Plaga", strplaGS, strfasep, countGSP)

            agregarRegistroSiVisible(registros, registroDh, "Plaga", strplaD, strfaseh, countDH)
            agregarRegistroSiVisible(registros, registroDc, "Plaga", strplaD, strfasec, countDC)
            agregarRegistroSiVisible(registros, registroDm, "Plaga", strplaD, strfasem, countDM)
            agregarRegistroSiVisible(registros, registroDg, "Plaga", strplaD, strfaseg, countDG)
            agregarRegistroSiVisible(registros, registroDp, "Plaga", strplaD, strfasep, countDP)

            agregarRegistroSiVisible(registros, registroCHIh, "Plaga", strplaCHI, strfaseh, countCHIH)
            agregarRegistroSiVisible(registros, registroCHIn, "Plaga", strplaCHI, strfasen, countCHIN)
            agregarRegistroSiVisible(registros, registroCHIa, "Plaga", strplaCHI, strfasea, countCHIA)

            agregarRegistroSiVisible(registros, registroTh, "Plaga", strplat, strfaseh, countTH)
            agregarRegistroSiVisible(registros, registroTn, "Plaga", strplat, strfasen, countTN)
            agregarRegistroSiVisible(registros, registroTa, "Plaga", strplat, strfasea, countTA)

            agregarRegistroSiVisible(registros, registroPMh, "Plaga", strplaPM, strfaseh, countPMH)
            agregarRegistroSiVisible(registros, registroPMn, "Plaga", strplaPM, strfasep, countPMN)
            agregarRegistroSiVisible(registros, registroPMa, "Plaga", strplaPM, strfasea, countPMA)

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
        val intent = Intent(this@pantalla_plagasmaiz, Pantalla_registromapa::class.java)
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
}