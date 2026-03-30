package com.example.apptest

import android.Manifest
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


class pantalla_plagassorgo : AppCompatActivity() {

    /* ---------------------------------------------------------------------------------------------
    |       Procedimiento para agregar una nueva plaga
    |   0. Agrega el nombre de la plaga en tu archivo de strings.xml
    |   1. Vete al Documento de Plague.kt, allí podras observar la iniciación de variables para cada una de las plagas que ya
    |       existen, agrega la plaga que vas a meter como nueva, recuerda agregarle una abreviación y tener en consideración sus
    |       fases, a menos que sea una enfermedad
    |   2. Crea una layout correspondiente a esa plaga, esto te permitirá reutilizarla y ahorrarte trabajo
    |   3. Cuando uses la plaga en una activity, (agregarla a un cultivo) recuerda exportar todas las variables necesarias como
    |       el botón de la plaga, los layouts que contienen los registros, los botones para aumentar y disminuir la cantidad etc.
    |   4. Para el caso de las enfermedades deberas agregar los checkbox
    |   5. Teniendo estas consideraciones no será un problema agregar una nuva plaga, en esta activity encontrarás como se trabajan las plagas
    |       usala como guía.
    |   6. Finalmente deberás ir a la activity de Pantalla_registromaoa.kt, inspecciona las líneas de código que hace mención a las plagas y enfermedades
    |       y no olvides incluir aquí las variables de la nueva plaga o enfermedad que agregaste.
    |
     ------------------------------------------------------------------------------------------------*/

    /* ---------------------------------------------------------------------------------------------
    |       Procedimiento para agregar un nuevo cultivo
    |   1. Agrega la string con el nombre de tu cultivo en strings.xml
    |   2. Ve al layout de activity_pantalla_registroeventos.xml y agrega el RadioButton en el RadioGroup
    |   3. Ve a la activity de Pantalla_registroeventos y añade tu nuevo cultivo a registrar, puedes ver
    |       donde se utilizan los demás cultivos y agregarlo simplemente
    |   4. Crea una activity nueva con formato Empty Views Activity, llamada pantalla_palgas[cultivo], no
    |       olvides crearle una layout para darle vista
    |   5. En la layout de la activity puedes guiarte de las demás pantallas ya existentes y copiar y pegar,
    |        solo configura las plagas de ese cultivo.
    |   6. Realiza toda la lógica de ese nuevo cultivo, en la activity pantalla_palgas[cultivo], puedes guiarte
    |       de esta activity para implementar el cultivo nuevo.
    |   7. Vete a Pantalla_registromapa y agrega el cultivo correspondiente, de manera que al realizar otro registro
    |       te mande a la activity de tu cultivo.
    |   8. Realiza pruebas para ver que tu activity funciona bien.
     -----------------------------------------------------------------------------------------------*/

    /* ---------------------------------------------------------------------------------------------
    |       PROCEDIMIENTO PARA AGREGAR UNA API DE GOOGLE MAPS AL PROYECTO Y VER MAPAS
    |   1. Accede a https://accounts.google.com/v3/signin/identifier?continue=https%3A%2F%2Fconsole.cloud.google.com%2F%3Fhl%3Des-419%26login%3Dtrue%26ref%3Dhttps%3A%2F%2Fwww.google.com%2F&followup=https%3A%2F%2Fconsole.cloud.google.com%2F%3Fhl%3Des-419%26login%3Dtrue%26ref%3Dhttps%3A%2F%2Fwww.google.com%2F&hl=es-419&ifkv=ARZ0qKK-SJ3ageRbhvjKGxYdSq4napMjXnspdiz_1xeg2tum1Q0hzrllYQAwVQqprtkbQzmlgPJbyw&osid=1&passive=1209600&service=cloudconsole&flowName=GlifWebSignIn&flowEntry=ServiceLogin&dsh=S-66144255%3A1712937285347665&theme=mn&ddm=0
    |       con una cuenta, es posible que te pida facturación, por lo que pedirá ingresar una tarjeta bancaria aunque no se hagan montos.
    |   2. Crea un proyecto y selecciona conseguir API para android, te va a generar una API que es un código muy largo
    |   3. Crea una string en strings.xml con el código de tu API.
    |   4. Añade esta API como metadato en tu archivo AndroidManifest.xml
    |   5. Cuando tengas un widget llamado MapView asignale la api que recibiste en el atributo api-key
    |   6. En la activity de Pantalla_registromapas encontrarás algunas secciones donde están comentadas unas líneas, quitales
    |       el comentario cuando hayas agregado tu API para que puedas ver el funcionamiento del mapa
     ---------------------------------------------------------------------------------------------------*/

    /* ---------------------------------------------------------------------------------------------
    |       PROCEDIMIENTO DE BASE DE DATOS
    |   1. Ve a Firebase console, crea una cuenta (Probablemente te pida ingresar tarjeta bancaria)
    |   2. Crea un proyecto y conectalo con tu App
    |   3. Te va a generar un archivo llamado google-service.json, este deberás agregarlo a tu app a nivel de la carpeta app.
    |   4. Las librerias para implementar la comunicación con la Firebase ya están configuradas en la app. pero te marcará error
    |       puesto que esta versión no tiene el archivo google-service.json. Comenta las líneas donde la app se conecte con la base de datos
    |       para arreglar este inconveniente
    |   5. Necesario aclarar que se usa tanto la storage de firebase como la realtime database, tendrás que configurar la realtime database con un
    |       archivo json para darle estructura a la base de datos
     ------------------------------------------------------------------------------------------------*/
    /* ----------------------------------------------------------
    |   Aquí definimos las variables que usaremos
     ------------------------------------------------------------*/
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
    //Definir variable para fecha y para hora
    private lateinit var Fecha: String
    private lateinit var Hora: String
    private lateinit var db: AppDatabase

    private lateinit var idPlague: TextView
    private lateinit var ContentButton: LinearLayout
    private lateinit var ContentReciver: LinearLayout
    private lateinit var bOtrasPlagas: Button
    private lateinit var tvPlagas: TextView
    private lateinit var llPlagasPrincipales: LinearLayout
    private lateinit var llOtrasPlagas: LinearLayout

    //Definimos las variables para las plagas
    private lateinit var botonGS: ImageButton; private lateinit var botonMP: ImageButton; private lateinit var botonPULA: ImageButton; private lateinit var botonPV: ImageButton; private lateinit var botonGCOG: ImageButton; private lateinit var botonGA: ImageButton; private lateinit var botonAR: ImageButton
    //...
    private lateinit var cGS: LinearLayout
    private lateinit var cMP: LinearLayout
    private lateinit var cPULA: LinearLayout
    private lateinit var cPV: LinearLayout
    private lateinit var cGCOG: LinearLayout
    private lateinit var cGA: LinearLayout
    private lateinit var cAR: LinearLayout
    //...
    private lateinit var registroGSh: LinearLayout; private lateinit var registroGSc: LinearLayout; private lateinit var registroGSm: LinearLayout; private lateinit var registroGSg: LinearLayout; private lateinit var registroGSp: LinearLayout
    private lateinit var registroMPh: LinearLayout; private lateinit var registroMPl: LinearLayout; private lateinit var registroMPa: LinearLayout
    private lateinit var registroPULA: LinearLayout
    private lateinit var registroPVca: LinearLayout; private lateinit var registroPVsa: LinearLayout
    private lateinit var registroGCOGh: LinearLayout
    private lateinit var registroGCOGl: LinearLayout
    private lateinit var registroGCOGa: LinearLayout
    private lateinit var registroGAh: LinearLayout; private lateinit var registroGAl: LinearLayout; private lateinit var registroGAa: LinearLayout
    private lateinit var registroARh: LinearLayout; private lateinit var registroARl: LinearLayout; private lateinit var registroARa: LinearLayout
    //...
    private lateinit var gshplus: ImageButton; private lateinit var gscplus: ImageButton; private lateinit var gsmplus: ImageButton; private lateinit var gsgplus: ImageButton; private lateinit var gspplus: ImageButton ; private lateinit var gshminus: ImageButton; private lateinit var gscminus: ImageButton; private lateinit var gsmminus: ImageButton; private lateinit var gsgminus: ImageButton; private lateinit var gspminus: ImageButton
    private lateinit var pvcaplus: ImageButton; private lateinit var pvsaplus: ImageButton; private lateinit var pvcaminus: ImageButton; private lateinit var pvsaminus: ImageButton
    private lateinit var pulaplus: ImageButton; private lateinit var pulaminus: ImageButton;
    private lateinit var mphplus: ImageButton; private lateinit var mplplus: ImageButton; private lateinit var mpaplus: ImageButton; private lateinit var mphminus: ImageButton; private lateinit var mplminus: ImageButton; private lateinit var mpaminus: ImageButton
    private lateinit var gcoghplus: ImageButton; private lateinit var gcoglplus: ImageButton; private lateinit var gcogaplus: ImageButton; private lateinit var gcoghminus: ImageButton; private lateinit var gcoglminus: ImageButton; private lateinit var gcogaminus: ImageButton
    private lateinit var gahplus: ImageButton; private lateinit var galplus: ImageButton; private lateinit var gaaplus: ImageButton; private lateinit var gahminus: ImageButton; private lateinit var galminus: ImageButton; private lateinit var gaaminus: ImageButton
    private lateinit var arhplus: ImageButton; private lateinit var arlplus: ImageButton; private lateinit var araplus: ImageButton; private lateinit var arhminus: ImageButton; private lateinit var arlminus: ImageButton; private lateinit var araminus: ImageButton
    //...
    private lateinit var tvhgs: TextView; private lateinit var tvcgs: TextView; private lateinit var tvmgs: TextView; private lateinit var tvggs: TextView; private lateinit var tvpgs: TextView
    private lateinit var tvsapv: TextView; private lateinit var tvcapv: TextView
    private lateinit var tvpula: TextView
    private lateinit var tvhmp: TextView; private lateinit var tvlmp: TextView; private lateinit var tvamp: TextView
    private lateinit var tvhgcog: TextView; private lateinit var tvlgcog: TextView; private lateinit var tvagcog: TextView
    private lateinit var tvhga: TextView; private lateinit var tvlga: TextView; private lateinit var tvaga: TextView
    private lateinit var tvhar: TextView; private lateinit var tvlar: TextView; private lateinit var tvaar: TextView
    //...
    private lateinit var tvGSh: TextView; private lateinit var tvGSc: TextView; private lateinit var tvGSm: TextView; private lateinit var tvGSg: TextView; private lateinit var tvGSp: TextView
    private lateinit var tvPVca: TextView; private lateinit var tvPVsa: TextView
    private lateinit var tvPULA: TextView
    private lateinit var tvMPh: TextView; private lateinit var tvMPl: TextView; private lateinit var tvMPa: TextView
    private lateinit var tvGCOGh: TextView; private lateinit var tvGCOGl: TextView; private lateinit var tvGCOGa: TextView
    private lateinit var tvGAh: TextView; private lateinit var tvGAl: TextView; private lateinit var tvGAa: TextView
    private lateinit var tvARh: TextView; private lateinit var tvARl: TextView; private lateinit var tvARa: TextView

    //Para las enfermedades
    private lateinit var botonPR: ImageButton
    private lateinit var botonRS: ImageButton
    //...
    private lateinit var cPR: LinearLayout
    private lateinit var cRS: LinearLayout
    //...
    private lateinit var registroPR: LinearLayout
    private lateinit var registroRS: LinearLayout
    //...
    private lateinit var cbPudricionroja: CheckBox
    private lateinit var cbRoyasorgo: CheckBox
    //...
    private lateinit var tvPudricionroja: TextView
    private lateinit var tvRoyasorgo: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_plagassorgo)
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
        botonGS = findViewById(R.id.boton_gusanosoldado); botonPV = findViewById(R.id.boton_pulgonverde); botonPULA = findViewById(R.id.boton_pulgonamarillo); botonMP = findViewById(R.id.boton_mosquitapanoja); botonGCOG = findViewById(R.id.boton_gusanocogollero); botonGA = findViewById(R.id.boton_gusanoalambre); botonAR = findViewById(R.id.boton_aranaroja)
        //...
        cGS = findViewById(R.id.CajaGS); cPV = findViewById(R.id.CajaPV); cPULA = findViewById(R.id.CajaPULA); cMP = findViewById(R.id.CajaMP); cGCOG = findViewById(R.id.CajaGCOG); cGA = findViewById(R.id.CajaGA); cAR = findViewById(R.id.CajaAR)
        //...
        registroGSh = findViewById(R.id.gsregistroh); registroGSc = findViewById(R.id.gsregistroc); registroGSm = findViewById(R.id.gsregistrom); registroGSg = findViewById(R.id.gsregistrog); registroGSp = findViewById(R.id.gsregistrop)
        registroPVca = findViewById(R.id.pvregistroca); registroPVsa = findViewById(R.id.pvregistrosa)
        registroPULA = findViewById(R.id.pularegistro)
        registroMPh = findViewById(R.id.mpregistroh); registroMPl = findViewById(R.id.mpregistrol); registroMPa = findViewById(R.id.mpregistroa)
        registroGCOGh = findViewById(R.id.gcogregistroh); registroGCOGl = findViewById(R.id.gcogregistrol); registroGCOGa = findViewById(R.id.gcogregistroa)
        registroGAh = findViewById(R.id.garegistroh); registroGAl = findViewById(R.id.garegistrol); registroGAa = findViewById(R.id.garegistroa)
        registroARh = findViewById(R.id.arregistroh); registroARl = findViewById(R.id.arregistrol); registroARa = findViewById(R.id.arregistroa)
        //...
        gshplus = findViewById(R.id.ib_huevecillomasGS); gscplus = findViewById(R.id.ib_chicomasGS); gsmplus = findViewById(R.id.ib_medianomasGS); gsgplus = findViewById(R.id.ib_grandemasGS); gspplus = findViewById(R.id.ib_pupamasGS); gshminus = findViewById(R.id.ib_huevecillomenosGS); gscminus = findViewById(R.id.ib_chicomenosGS); gsmminus = findViewById(R.id.ib_medianomenosGS); gsgminus = findViewById(R.id.ib_grandemenosGS); gspminus = findViewById(R.id.ib_pupamenosGS)
        pvcaplus = findViewById(R.id.ib_conalasmasPV); pvsaplus = findViewById(R.id.ib_sinalasmasPV); pvcaminus = findViewById(R.id.ib_conalasmenosPV); pvsaminus = findViewById(R.id.ib_sinalasmenosPV)
        pulaplus = findViewById(R.id.ib_masPULA); pulaminus = findViewById(R.id.ib_menosPULA)
        mphplus = findViewById(R.id.ib_huevecillomasMP); mplplus = findViewById(R.id.ib_ninfamasMP); mpaplus = findViewById(R.id.ib_adultomasMP); mphminus = findViewById(R.id.ib_huevecillomenosMP); mplminus = findViewById(R.id.ib_ninfamenosMP); mpaminus = findViewById(R.id.ib_adultomenosMP)
        gcoghplus = findViewById(R.id.ib_huevecillomasGCOG); gcoglplus = findViewById(R.id.ib_ninfamasGCOG); gcogaplus = findViewById(R.id.ib_adultomasGCOG); gcoghminus = findViewById(R.id.ib_huevecillomenosGCOG); gcoglminus = findViewById(R.id.ib_ninfamenosGCOG); gcogaminus = findViewById(R.id.ib_adultomenosGCOG)
        gahplus = findViewById(R.id.ib_huevecillomasGA); galplus = findViewById(R.id.ib_ninfamasGA); gaaplus = findViewById(R.id.ib_adultomasGA); gahminus = findViewById(R.id.ib_huevecillomenosGA); galminus = findViewById(R.id.ib_ninfamenosGA); gaaminus = findViewById(R.id.ib_adultomenosGA)
        arhplus = findViewById(R.id.ib_huevecillomasAR); arlplus = findViewById(R.id.ib_ninfamasAR); araplus = findViewById(R.id.ib_adultomasAR); arhminus = findViewById(R.id.ib_huevecillomenosAR); arlminus = findViewById(R.id.ib_ninfamenosAR); araminus = findViewById(R.id.ib_adultomenosAR)
        //...
        tvhgs = findViewById(R.id.cantidad_hGS); tvcgs = findViewById(R.id.cantidad_cGS); tvmgs = findViewById(R.id.cantidad_mGS); tvggs = findViewById(R.id.cantidad_gGS); tvpgs = findViewById(R.id.cantidad_pGS)
        tvcapv = findViewById(R.id.cantidad_caPV); tvsapv = findViewById(R.id.cantidad_saPV)
        tvpula = findViewById(R.id.cantidad_PULA)
        tvhmp = findViewById(R.id.cantidad_hMP); tvlmp = findViewById(R.id.cantidad_nMP); tvamp = findViewById(R.id.cantidad_aMP)
        tvhgcog = findViewById(R.id.cantidad_hGCOG); tvlgcog = findViewById(R.id.cantidad_nGCOG); tvagcog = findViewById(R.id.cantidad_aGCOG)
        tvhga = findViewById(R.id.cantidad_hGA); tvlga = findViewById(R.id.cantidad_nGA); tvaga = findViewById(R.id.cantidad_aGA)
        tvhar = findViewById(R.id.cantidad_hAR); tvlar = findViewById(R.id.cantidad_nAR); tvaar = findViewById(R.id.cantidad_aAR)
        //...
        tvGSh = findViewById(R.id.tv_gsh); tvGSc = findViewById(R.id.tv_gsc); tvGSm = findViewById(R.id.tv_gsm); tvGSg = findViewById(R.id.tv_gsg); tvGSp = findViewById(R.id.tv_gsp)
        tvPVca = findViewById(R.id.tv_pvca); tvPVsa = findViewById(R.id.tv_pvsa)
        tvPULA = findViewById(R.id.tv_pula)
        tvMPh = findViewById(R.id.tv_mph); tvMPl = findViewById(R.id.tv_mpl); tvMPa = findViewById(R.id.tv_mpa)
        tvGCOGh = findViewById(R.id.tv_gcogh); tvGCOGl = findViewById(R.id.tv_gcogl); tvGCOGa = findViewById(R.id.tv_gcoga)
        tvGAh = findViewById(R.id.tv_gah); tvGAl = findViewById(R.id.tv_gal); tvGAa = findViewById(R.id.tv_gaa)
        tvARh = findViewById(R.id.tv_arh); tvARl = findViewById(R.id.tv_arl); tvARa = findViewById(R.id.tv_ara)

        //Para las enfermedades
        botonPR = findViewById(R.id.boton_pudricionroja)
        botonRS = findViewById(R.id.boton_royasorgo)
        //...
        cPR = findViewById(R.id.CajaPR)
        cRS = findViewById(R.id.CajaRS)
        //...
        registroPR = findViewById(R.id.prregistro)
        registroRS = findViewById(R.id.rsregistro)
        //...
        cbPudricionroja = findViewById(R.id.cb_pudricionroja)
        cbRoyasorgo = findViewById(R.id.cb_royasorgo)
        //...
        tvPudricionroja = findViewById(R.id.tv_pr)
        tvRoyasorgo = findViewById(R.id.tv_rs)

        //Agregamos las strings con el nombre de las plagas
        val strGS = getString(R.string.plaga_gusanosoldado)
        val strPV = getString(R.string.plaga_pulgonverde)
        val strPULA = getString(R.string.plaga_pulgonamarillo)
        val strMP = getString(R.string.plaga_mosquitapanoja)
        val strGCOG = getString(R.string.plaga_gusanocogollero)
        val strGA = getString(R.string.plaga_gusanoalambre)
        val strAR = getString(R.string.plaga_aranaroja)
        //Agregamos las strings con las fases de las plagas
        val strh = getString(R.string.fase_huevecillo)
        val strc = getString(R.string.fase_chico)
        val strm = getString(R.string.fase_mediano)
        val strg = getString(R.string.fase_grande)
        val strp = getString(R.string.fase_pupa)
        val strl = getString(R.string.fase_larva)
        val stra = getString(R.string.fase_adulto)
        val strca = getString(R.string.fase_conalas)
        val strsa = getString(R.string.fase_sinalas)
        val strna = getString(R.string.fase_sinfase)
        //Agregamos las strings con el nombre de las enfermedades
        val strPR = getString(R.string.enfermedad_podredumbreraices)
        val strRS = getString(R.string.enfermedad_royasorgo)

        //Definimos la lista que contiene el nombre de las plagas
        val buttonsPlague: List<ImageButton> = listOf(botonGS, botonPV, botonPULA, botonMP, botonGCOG, botonGA, botonAR, botonPR, botonRS)
        val registerPlague: List<LinearLayout> = listOf(cGS, cPV, cPULA, cMP, cGCOG, cGA, cAR, cPR, cRS)

        /* --------------------------------------------------------------------
        |   Implementamos los métodos y los eventos
         --------------------------------------------------------------------*/

        //Actualizamos el TexView para que muestre el punto que estamos registrando
        idPlague.text = IDcount.toString()

        bNoPlagas.setOnClickListener { showAdvertence() }

        //Agregamos los eventos al intercambiar entre plagas y enfermedades
        bOtrasPlagas.setOnClickListener {
            if (current_icon == 0){
                bOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_hide, null, null, null)
                current_icon = 1
                llOtrasPlagas.visibility = View.VISIBLE
                tvPlagas.text = "Ver Otras Plagas "
                llPlagasPrincipales.visibility = View.GONE
                (ContentButton.parent as? LinearLayout)?.removeView(ContentButton)
                ContentReciver.addView(ContentButton)
            }else{
                bOtrasPlagas.setCompoundDrawablesWithIntrinsicBounds(ic_add, null, null, null)
                current_icon = 0
                llOtrasPlagas.visibility = View.GONE
                tvPlagas.text = "Ver Plagas Principales"
                llPlagasPrincipales.visibility = View.VISIBLE
                ContentReciver.removeView(ContentButton)
                llPlagasPrincipales.addView(ContentButton, 0)
            }
        }

        //Agregamos el funcionamiento de los botones para elegir plaga o enfermedad
        buttonsPlague.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {
                registerPlague.forEachIndexed { indexlayout, linearLayout ->
                    if (indexlayout == index){
                        linearLayout.visibility = View.VISIBLE
                    }else{
                        linearLayout.visibility = View.GONE
                    }
                }
            }
        }

        //Agregamos el funcionamiento de los botones para agregar o quitar cantidad
        gshplus.setOnClickListener { countGSH = increase(countGSH, tvhgs, tvGSh, registroGSh) }; gscplus.setOnClickListener { countGSC = increase(countGSC, tvcgs, tvGSc, registroGSc) }; gsmplus.setOnClickListener { countGSM = increase(countGSM, tvmgs, tvGSm, registroGSm) }; gsgplus.setOnClickListener { countGSG = increase(countGSG, tvggs, tvGSg, registroGSg) }; gspplus.setOnClickListener { countGSP = increase(countGSP, tvpgs, tvGSp, registroGSp) }; gshminus.setOnClickListener { countGSH = decrease(countGSH, tvhgs, tvGSh, registroGSh) }; gscminus.setOnClickListener { countGSC = decrease(countGSC, tvcgs, tvGSc, registroGSc) }; gsmminus.setOnClickListener { countGSM = decrease(countGSM, tvmgs, tvGSm, registroGSm) }; gsgminus.setOnClickListener { countGSG = decrease(countGSG, tvggs, tvGSg, registroGSg) }; gspminus.setOnClickListener { countGSP = decrease(countGSP, tvpgs, tvGSp, registroGSp) }
        pvcaplus.setOnClickListener { countPVCA = increase(countPVCA, tvcapv, tvPVca, registroPVca) }; pvsaplus.setOnClickListener { countPVSA = increase(countPVSA, tvsapv, tvPVsa, registroPVsa) }; pvcaminus.setOnClickListener { countPVCA = decrease(countPVCA, tvcapv, tvPVca, registroPVca) }; pvsaminus.setOnClickListener { countPVSA = decrease(countPVSA, tvsapv, tvPVsa, registroPVsa) }
        pulaplus.setOnClickListener { countPULA = increase(countPULA, tvpula, tvPULA, registroPULA) }; pulaminus.setOnClickListener { countPULA = decrease(countPULA, tvpula, tvPULA, registroPULA) }
        mphplus.setOnClickListener { countMPH = increase(countMPH, tvhmp, tvMPh, registroMPh) }; mplplus.setOnClickListener { countMPN = increase(countMPN, tvlmp, tvMPl, registroMPl) }; mpaplus.setOnClickListener { countMPA = increase(countMPA, tvamp, tvMPa, registroMPa) }; mphminus.setOnClickListener { countMPH = decrease(countMPH, tvhmp, tvMPh, registroMPh) }; mplminus.setOnClickListener { countMPN = decrease(countMPN, tvlmp, tvMPl, registroMPl) }; mpaminus.setOnClickListener { countMPA = decrease(countMPA, tvamp, tvMPa, registroMPa) }
        gcoghplus.setOnClickListener { countGCOGH = increase(countGCOGH, tvhgcog, tvGCOGh, registroGCOGh) }; gcoglplus.setOnClickListener { countGCOGN = increase(countGCOGN, tvlgcog, tvGCOGl, registroGCOGl) }; gcogaplus.setOnClickListener { countGCOGA = increase(countGCOGA, tvagcog, tvGCOGa, registroGCOGa) }; gcoghminus.setOnClickListener { countGCOGH = decrease(countGCOGH, tvhgcog, tvGCOGh, registroGCOGh) }; gcoglminus.setOnClickListener { countGCOGN = decrease(countGCOGN, tvlgcog, tvGCOGl, registroGCOGl) }; gcogaminus.setOnClickListener { countGCOGA = decrease(countGCOGA, tvagcog, tvGCOGa, registroGCOGa) }
        gahplus.setOnClickListener { countGAH = increase(countGAH, tvhga, tvGAh, registroGAh) }; galplus.setOnClickListener { countGAN = increase(countGAN, tvlga, tvGAl, registroGAl) }; gaaplus.setOnClickListener { countGAA = increase(countGAA, tvaga, tvGAa, registroGAa) }; gahminus.setOnClickListener { countGAH = decrease(countGAH, tvhga, tvGAh, registroGAh) }; galminus.setOnClickListener { countGAN = decrease(countGAN, tvlga, tvGAl, registroGAl) }; gaaminus.setOnClickListener { countGAA = decrease(countGAA, tvaga, tvGAa, registroGAa) }
        arhplus.setOnClickListener { countARH = increase(countARH, tvhar, tvARh, registroARh) }; arlplus.setOnClickListener { countARN = increase(countARN, tvlar, tvARl, registroARl) }; araplus.setOnClickListener { countARA = increase(countARA, tvaar, tvARa, registroARa) }; arhminus.setOnClickListener { countARH = decrease(countARH, tvhar, tvARh, registroARh) }; arlminus.setOnClickListener { countARN = decrease(countARN, tvlar, tvARl, registroARl) }; araminus.setOnClickListener { countARA = decrease(countARA, tvaar, tvARa, registroARa) }

        //Funcionalidades para registrar plagas
        cbPudricionroja.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                registroPR.visibility = View.VISIBLE
                tvPudricionroja.text = "Sí"
            }else{
                registroPR.visibility = View.GONE
                tvPudricionroja.text = ""
            }
        }
        cbRoyasorgo.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                registroRS.visibility = View.VISIBLE
                tvRoyasorgo.text = "Sí"
            }else{
                registroRS.visibility = View.GONE
                tvRoyasorgo.text = ""
            }
        }
        bEnviarRegistro.setOnClickListener {
            val registros = mutableListOf<MonitoreoEntity>()

            agregarRegistroSiVisible(registros, registroGSh, "Plaga", strGS, strh, countGSH)
            agregarRegistroSiVisible(registros, registroGSc, "Plaga", strGS, strc, countGSC)
            agregarRegistroSiVisible(registros, registroGSm, "Plaga", strGS, strm, countGSM)
            agregarRegistroSiVisible(registros, registroGSg, "Plaga", strGS, strg, countGSG)
            agregarRegistroSiVisible(registros, registroGSp, "Plaga", strGS, strp, countGSP)

            agregarRegistroSiVisible(registros, registroPVca, "Plaga", strPV, strca, countPVCA)
            agregarRegistroSiVisible(registros, registroPVsa, "Plaga", strPV, strsa, countPVSA)

            agregarRegistroSiVisible(registros, registroPULA, "Plaga", strPULA, strna, countPULA)

            agregarRegistroSiVisible(registros, registroMPh, "Plaga", strMP, strh, countMPH)
            agregarRegistroSiVisible(registros, registroMPl, "Plaga", strMP, strl, countMPN)
            agregarRegistroSiVisible(registros, registroMPa, "Plaga", strMP, stra, countMPA)

            agregarRegistroSiVisible(registros, registroGCOGh, "Plaga", strGCOG, strh, countGCOGH)
            agregarRegistroSiVisible(registros, registroGCOGl, "Plaga", strGCOG, strl, countGCOGN)
            agregarRegistroSiVisible(registros, registroGCOGa, "Plaga", strGCOG, stra, countGCOGA)

            agregarRegistroSiVisible(registros, registroGAh, "Plaga", strGA, strh, countGAH)
            agregarRegistroSiVisible(registros, registroGAl, "Plaga", strGA, strl, countGAN)
            agregarRegistroSiVisible(registros, registroGAa, "Plaga", strGA, stra, countGAA)

            agregarRegistroSiVisible(registros, registroARh, "Plaga", strAR, strh, countARH)
            agregarRegistroSiVisible(registros, registroARl, "Plaga", strAR, strl, countARN)
            agregarRegistroSiVisible(registros, registroARa, "Plaga", strAR, stra, countARA)

            agregarRegistroSiVisible(registros, registroPR, "Enfermedad", strPR, strna, 1)
            agregarRegistroSiVisible(registros, registroRS, "Enfermedad", strRS, strna, 1)

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

    /* --------------------------------------------------------
    |   Iniciamos las funciones necesarias para esta acvtividad
     ----------------------------------------------------------*/
    //Función para abrir otra activity (Se puede activar al presionar el botón)
    private fun  openActivity(sto: Class<*>) {
        intent = Intent(this, sto)
        startActivity(intent)
        finish()
    }
    //Función para guardar el archivo en caso dde cierre de app
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
        val intent = Intent(this@pantalla_plagassorgo, Pantalla_registromapa::class.java)
        intent.putExtra("ind_value", IDcount)
        intent.putExtra("cultivo", Cultivo)
        startActivity(intent)
        finish()
    }
    //Función para guardar en archivos la información
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
    //Función para solicitar al dispositivo la ultima posición registrada
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
    //Función para crear un cuadro de dialogo para notificarle al usuario que no se encontró ninguna plaga
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
    //Función para mostrar mensajes emergentes
    fun showMessage (mostrarMsj:String){
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, mostrarMsj, duracion)
        toast.show()
    }
    //Función para añadir la información que se va registrando a la variable dataSave para ir guardando esa información y subirla a la base de datos
    fun verifyRegister(registro: LinearLayout,tipo: String, nombre: String, fase: String, cantidad: Int){
        if(registro.visibility == View.VISIBLE){
            dataSave.add(mutableListOf(IDcount.toString(), loggedUser, Agricultor, Granja, Lote, Cultivo, latitud.toString(), longitud.toString(),tipo , nombre, fase, cantidad.toString(), Fecha, Hora))
        }
    }
    //Función para actualizar ubicaciones
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    //Funcion para no permitir al usuario regresar de esta activity
    override fun onBackPressed() {
        val duracion = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, "No se puede volver en esta Pantalla.", duracion)
        toast.show()
    }


}