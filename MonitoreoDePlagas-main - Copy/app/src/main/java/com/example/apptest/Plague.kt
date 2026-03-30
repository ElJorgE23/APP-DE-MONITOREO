package com.example.apptest
import android.Manifest
import android.widget.TextView
import android.widget.Toast
import com.example.apptest.pantalla_implementoplagas
import com.example.apptest.Pantalla_registromapa
import com.example.apptest.Pantalla_registroeventos
import java.io.File
import java.util.Calendar
import android.content.Context
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat

import android.view.View
import android.widget.LinearLayout
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
/*
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
*/

//imports firebase
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//imports firebase

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageButton
//import androidx.compose.foundation.pager.PageInfo
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context
import java.lang.Math.ceil
import java.text.SimpleDateFormat
import java.util.Locale
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
import kotlin.math.ceil


/* -----------------------------------------------------------------------
|
|   Esta sección es bastante importante para la aplicación, ya que aquí se declaran los parámetros
|   generales que son compartidos entre los diferentes actividades que componen la aplicación
|
 ----------------------------------------------------------------------- */

private var latitud: Double? = null
private var longitud: Double? = null



data class User(val nombre: String,val apellido: String, val numeroTelefono: String, val correo: String, val rol: String)
var loggedUser: String = ""

/* -------------------------------------------------------------------------------------------------
|
|   Creamos las variables necesarias para anexar a las tablas
|
----------------------------------------------------------------------------------------------------*/

var Agricultor: String = ""
var Granja:String = ""
var Lote: String = ""
var Cultivo: String = ""
var Tipo: String = ""


//Inicializamos el Id para el registro de los datos
var IDcount: Int = 1
//iniciando variables para Dorso de Diamante
var countDDH: Int = 0; var countDDC: Int = 0; var countDDM: Int = 0; var countDDG: Int = 0; var countDDP: Int = 0
//Iniciando variables para Falso Medidor
var countFMH: Int = 0; var countFMC: Int = 0; var countFMM: Int = 0; var countFMG: Int = 0; var countFMP: Int = 0
//Iniciando variables para Gusano Soldado
var countGSH: Int = 0; var countGSC: Int = 0; var countGSM: Int = 0; var countGSG: Int = 0; var countGSP: Int = 0
//Iniciando variables para Gusano de la Fruta
var countGDFH: Int = 0; var countGDFC: Int = 0; var countGDFM: Int = 0; var countGDFG: Int = 0; var countGDFP: Int = 0
//Iniciando variables para Gusano de la Col
var countGDCH: Int = 0; var countGDCC: Int = 0; var countGDCM: Int = 0; var countGDCG: Int = 0; var countGDCP: Int = 0
//Iniciando variables para Gusano Pieris
var countGPH: Int = 0; var countGPC: Int = 0; var countGPM: Int = 0; var countGPG: Int = 0; var countGPP: Int = 0
//Iniciando variables para Diabrótica
var countDH: Int = 0; var countDC: Int = 0; var countDM: Int = 0; var countDG: Int = 0; var countDP: Int = 0
//iniciando variables para Chinche Lygus
var countCLC: Int = 0; var countCLM: Int = 0; var countCLG: Int = 0
//Iniciando variables para Copitarsia
var countCC: Int = 0; var countCM: Int = 0; var countCG: Int = 0
//Iniciando Variables para Chicharrita
var countCHIH: Int = 0; var countCHIN: Int = 0; var countCHIA: Int = 0
//Iniciando Variables para Pulgon Verde
var countPVSA: Int = 0; var countPVCA: Int = 0
//Iniciando variables para Pulgon Gris
var countPG: Int = 0
//Iniciando las variables para Helicoverpa
var countHZH: Int = 0 ; var countHZC: Int = 0 ; var countHZM: Int = 0 ; var countHZG: Int = 0 ; var countHZP: Int = 0
//Iniciando las variables para Chinche Nysus
var countCNC: Int = 0 ; var countCNM: Int = 0 ; var countCNG: Int = 0
//Iniciando las variables para Trips
var countTH: Int = 0; var countTN: Int = 0; var countTA: Int = 0
//Iniciando las variables para Coocinallidae
var countCOCH: Int = 0; var countCOCL: Int = 0; var countCOCA: Int = 0
//Iniciando variables para Mosquita Blanca
var countMBH: Int = 0; var countMBN: Int = 0; var countMBA: Int = 0
//Iniciando las variables para Araña Roja
var countARH: Int = 0 ; var countARN: Int = 0 ; var countARA: Int = 0
//Iniciando las variables para Mosva Vinagre
var countMVH: Int = 0 ; var countMVN: Int = 0 ; var countMVA: Int = 0
//Iniciando variables para Gallina Ciega
var countGCH: Int = 0 ; var countGCN: Int = 0 ; var countGCA: Int = 0
//Iniciando variables para Gusano Alambre
var countGAH: Int = 0; var countGAN: Int = 0; var countGAA: Int = 0
//Iniciando variables para Gusano Cogollero
var countGCOGH: Int = 0; var countGCOGN: Int = 0; var countGCOGA: Int = 0
//Iniciando variables para Picudo del Agave
var countPAH: Int = 0; var countPAN: Int = 0; var countPAA: Int = 0
//Iniciando variables para Picudo del Maiz
var countPMH: Int = 0; var countPMN: Int = 0; var countPMA: Int = 0
//Iniciando variables para Pulgón Amarillo
var countPULA: Int = 0
//Iniciando variables para Mosquita de la Panoja
var countMPH: Int = 0; var countMPN: Int = 0; var countMPA: Int = 0
//Iniciando variable para crear continuidad a partir de la pantalla de registro mapa
var continueActivity: String = ""
//Agregamos las variables para guardar de manera global los registros en los que se haga el monitoreo
var nameAgricultor: String = ""
var nameGranja: String = ""
var nameLote: String = ""


//Declaramos variable para poder guardar los registros
var dataSave = mutableListOf<MutableList<String>>()
//Declaramos variable para poder guardar el nombre del archivo
var archiveName: String = "archive"
var archiveUmbral: String = "umbral"

//Creamos las variables necesarias para poder calcualar el valor de los umbrales
//Deben ser declaradas para todas las plagas que hayamos definido en el archivo
/* ------------------------------------------------------------------------
|   Definimos las variables que nos van a ayudar a calcular todos los umbrales
 ---------------------------------------------------------------------------*/
/*------------------------------------------------------------
|   Creamos una variable para obtener los ID´s totales
---------------------------------------------------------------*/
//Una para los ID´s de forma String
var IDstrings = mutableListOf<String>()
//Una para los ID´s de forma Int
var IDints = mutableListOf<Int>()
/* -------------------------------------------------------------------
   |   Para Dorso de Diamante
     ---------------------------------------------------------------------- */
var DDUmbral = mutableListOf<Int>()
var DDTotal = mutableListOf<Int>()
var DDH = mutableListOf<Int>()
var DDC = mutableListOf<Int>()
var DDM = mutableListOf<Int>()
var DDG = mutableListOf<Int>()
var DDP = mutableListOf<Int>()
var DDumbralValue: Float = 0f
/* ------------------------------------------------------------------
|   Para Falso Medidor
-------------------------------------------------------------------- */
var FMUmbral = mutableListOf<Int>()
var FMTotal = mutableListOf<Int>()
var FMH = mutableListOf<Int>()
var FMC = mutableListOf<Int>()
var FMM = mutableListOf<Int>()
var FMG = mutableListOf<Int>()
var FMP = mutableListOf<Int>()
var FMumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Gusano Soldado
-------------------------------------------------------------------*/
var GSUmbral = mutableListOf<Int>()
var GSTotal = mutableListOf<Int>()
var GSH = mutableListOf<Int>()
var GSC = mutableListOf<Int>()
var GSM = mutableListOf<Int>()
var GSG = mutableListOf<Int>()
var GSP = mutableListOf<Int>()
var GSumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Gusano Pieris
-------------------------------------------------------------------*/
var GPUmbral = mutableListOf<Int>()
var GPTotal = mutableListOf<Int>()
var GPH = mutableListOf<Int>()
var GPC = mutableListOf<Int>()
var GPM = mutableListOf<Int>()
var GPG = mutableListOf<Int>()
var GPP = mutableListOf<Int>()
var GPumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Diabrotica
-------------------------------------------------------------------*/
var DUmbral = mutableListOf<Int>()
var DTotal = mutableListOf<Int>()
var DH = mutableListOf<Int>()
var DC = mutableListOf<Int>()
var DM = mutableListOf<Int>()
var DG = mutableListOf<Int>()
var DP = mutableListOf<Int>()
var DumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Gusano de la Fruta
-------------------------------------------------------------------*/
var GDFUmbral = mutableListOf<Int>()
var GDFTotal = mutableListOf<Int>()
var GDFH = mutableListOf<Int>()
var GDFC = mutableListOf<Int>()
var GDFM = mutableListOf<Int>()
var GDFG = mutableListOf<Int>()
var GDFP = mutableListOf<Int>()
var GDFumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Gusano de la Col
-------------------------------------------------------------------*/
var GDCUmbral = mutableListOf<Int>()
var GDCTotal = mutableListOf<Int>()
var GDCH = mutableListOf<Int>()
var GDCC = mutableListOf<Int>()
var GDCM = mutableListOf<Int>()
var GDCG = mutableListOf<Int>()
var GDCP = mutableListOf<Int>()
var GDCumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Helicoverpa Zea
-------------------------------------------------------------------*/
var HZUmbral = mutableListOf<Int>()
var HZTotal = mutableListOf<Int>()
var HZH = mutableListOf<Int>()
var HZC = mutableListOf<Int>()
var HZM = mutableListOf<Int>()
var HZG = mutableListOf<Int>()
var HZP = mutableListOf<Int>()
var HZumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Chinche Lygus
-------------------------------------------------------------------*/
var CLUmbral = mutableListOf<Int>()
var CLC = mutableListOf<Int>()
var CLM = mutableListOf<Int>()
var CLG = mutableListOf<Int>()
var CLumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Copitarsia
-------------------------------------------------------------------*/
var CUmbral = mutableListOf<Int>()
var CC = mutableListOf<Int>()
var CM = mutableListOf<Int>()
var CG = mutableListOf<Int>()
var CumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Chinche Nysius
-------------------------------------------------------------------*/
var CNUmbral = mutableListOf<Int>()
var CNC = mutableListOf<Int>()
var CNM = mutableListOf<Int>()
var CNG = mutableListOf<Int>()
var CNumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Chicharrita
-------------------------------------------------------------------*/
var CHIUmbral = mutableListOf<Int>()
var CHIH = mutableListOf<Int>()
var CHIN = mutableListOf<Int>()
var CHIA = mutableListOf<Int>()
var CHIumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Trips
-------------------------------------------------------------------*/
var TUmbral = mutableListOf<Int>()
var TH = mutableListOf<Int>()
var TN = mutableListOf<Int>()
var TA = mutableListOf<Int>()
var TumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Mosquita Blanca
-------------------------------------------------------------------*/
var MBUmbral = mutableListOf<Int>()
var MBH = mutableListOf<Int>()
var MBN = mutableListOf<Int>()
var MBA = mutableListOf<Int>()
var MBumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Coocinallidae
-------------------------------------------------------------------*/
var COCUmbral = mutableListOf<Int>()
var COCH = mutableListOf<Int>()
var COCL = mutableListOf<Int>()
var COCA = mutableListOf<Int>()
var COCumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Pulgon Verde
-------------------------------------------------------------------*/
var PVUmbral = mutableListOf<Int>()
var PVTotal = mutableListOf<Int>()
var PVCA = mutableListOf<Int>()
var PVSA = mutableListOf<Int>()
var PVumbralValue: Float = 0f
/*-------------------------------------------------------------------
|   Para Pulgon Gris
-------------------------------------------------------------------*/
var PGUmbral = mutableListOf<Int>()
var PGumbralValue: Float = 0f
/*---------------------------------------------------------------
|   Para Araña Roja
 ---------------------------------------------------------------*/
var ARUmbral = mutableListOf<Int>()
var ARH = mutableListOf<Int>()
var ARN = mutableListOf<Int>()
var ARA = mutableListOf<Int>()
var ARumbralValue: Float = 0f
/*---------------------------------------------------------------
|   Para Mosca de Vinagre
 ---------------------------------------------------------------*/
var MVUmbral = mutableListOf<Int>()
var MVH = mutableListOf<Int>()
var MVN = mutableListOf<Int>()
var MVA = mutableListOf<Int>()
var MVumbralValue: Float = 0f
/*---------------------------------------------------------------
|   Para Gallina Ciega
 ---------------------------------------------------------------*/
var GCUmbral = mutableListOf<Int>()
var GCH = mutableListOf<Int>()
var GCN = mutableListOf<Int>()
var GCA = mutableListOf<Int>()
var GCumbralValue: Float = 0f
/*---------------------------------------------------------------
 |   Para Gusano de Alambre
 ---------------------------------------------------------------*/
var GAUmbral = mutableListOf<Int>()
var GAH = mutableListOf<Int>()
var GAN = mutableListOf<Int>()
var GAA = mutableListOf<Int>()
var GAUmbralValue: Float = 0f
/*---------------------------------------------------------------
 |   Para Gusano Cogollero
 ---------------------------------------------------------------*/
var GCOGUmbral = mutableListOf<Int>()
var GCOGH = mutableListOf<Int>()
var GCOGN = mutableListOf<Int>()
var GCOGA = mutableListOf<Int>()
var GCOGUmbralValue: Float = 0f
/*---------------------------------------------------------------
 |   Para Picudo del Agave
 ---------------------------------------------------------------*/
var PAUmbral = mutableListOf<Int>()
var PAH = mutableListOf<Int>()
var PAN = mutableListOf<Int>()
var PAA = mutableListOf<Int>()
var PAUmbralValue: Float = 0f
/*---------------------------------------------------------------
 |   Para Picudo del Maiz
 ---------------------------------------------------------------*/
var PMUmbral = mutableListOf<Int>()
var PMH = mutableListOf<Int>()
var PMN = mutableListOf<Int>()
var PMA = mutableListOf<Int>()
var PMUmbralValue: Float = 0f
/*---------------------------------------------------------------
 |   Para Mosquita de Panoja
 ---------------------------------------------------------------*/
var MPUmbral = mutableListOf<Int>()
var MPH = mutableListOf<Int>()
var MPN = mutableListOf<Int>()
var MPA = mutableListOf<Int>()
var MPUmbralValue: Float = 0f
/*---------------------------------------------------------------
 |   Para Pulgón Amarillo
 ---------------------------------------------------------------*/
var PULAUmbral = mutableListOf<Int>()
var PULAumbralValue: Float = 0f

/* ------------------------------------------------------------------
|
|       EN ESTA SECCIÓN DE AQUÍ DECLARAMOS LAS VARIABLES QUE SERÁN USADAS DE MANERA GLOBAL.
|       (solamente aquellas funciones que no contengan parámetros exclusivos de cada clase.
|
 ------------------------------------------------------------------*/
/* ---------------------------------------------------------------------
|
|   En esta parte definimos lo necesario para trabajar con los archivos kml
|
 ------------------------------------------------------------------------*/
var coord_from_kml = mutableListOf<LatLng>()
/* -----------------------------------------------------------
|
|   LAS SIGUIENTES SON FUNCIONES IMPLEMENTADAS EN LA ACTIVIDAD DE registromapa
|
 -----------------------------------------------------------*/
//Definimos una función para reiniciar los contadores
fun restartcounts(){
    //Reiniciamos las variables que cuentan cada fase de cada plaga puesto que se realizará un nuevo registro
    countDDH = 0; countDDC = 0; countDDM = 0; countDDG = 0; countDDP = 0
    countFMH = 0; countFMC = 0; countFMM = 0; countFMG = 0; countFMP = 0
    countGSH = 0; countGSC = 0; countGSM = 0; countGSG = 0; countGSP = 0
    countGPH = 0; countGPC = 0; countGPM = 0; countGPG = 0; countGPP = 0
    countDH = 0; countDC = 0; countDM = 0; countDG = 0; countDP = 0
    countHZH = 0; countHZC = 0; countHZM = 0; countHZG = 0; countHZP = 0
    countCNC = 0; countCNM = 0; countCNG = 0
    countTN = 0; countTH = 0; countTA = 0
    countCOCL = 0; countCOCH = 0; countCOCA = 0
    countMBN = 0; countMBH = 0; countMBA = 0
    countGDCH = 0; countGDCC = 0; countGDCM = 0; countGDCG = 0; countGDCP = 0
    countGDFH = 0; countGDFC = 0; countGDFM = 0; countGDFG = 0; countGDFP = 0
    countCLC = 0; countCLM = 0; countCLG = 0
    countCC = 0; countCM = 0; countCG = 0
    countCHIN = 0; countCHIH = 0; countCHIA = 0
    countPVSA = 0; countPVCA = 0;
    countPG = 0
    countARH = 0; countARN = 0; countARA = 0
    countMVH = 0; countMVN = 0; countMVA = 0
    countGCH = 0; countGCN = 0; countGCA = 0
    countGAH = 0; countGAN = 0; countGAA = 0
    countGCOGH = 0; countGCOGN = 0; countGCOGA = 0
    countPAH = 0; countPAN = 0; countPAA = 0
    countPMH = 0; countPMN = 0; countPMA = 0
    countMPH = 0; countMPN = 0; countMPA = 0
    countPULA = 0;
}

fun getDateTime():String{
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // Los meses comienzan desde 0
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return "$year-$month-$day"
}
//Definimos función para estar guardando el archivo generado en la carpeta de descargas
fun saveToDownloads(context: Context, data: List<List<String>>, fileName: String) {
    try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        val fileWriter = FileWriter(file)
        for (line in data) {
            fileWriter.append(line.joinToString(","))
            fileWriter.append("\n")
        }
        fileWriter.flush()
        fileWriter.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
//Esta funcion sirve para obtener los valores de las plagas para los umbrales cuando estas constan de 5 fases
fun lecturefivefases(
    plaguename: String,
    cantUmbral: MutableList<Int>,
    cantTotal: MutableList<Int>,
    cantH: MutableList<Int>,
    cantC: MutableList<Int>,
    cantM: MutableList<Int>,
    cantG: MutableList<Int>,
    cantP: MutableList<Int>
) {
    for (line in dataSave) {
        if (line.size > 11 && line[9] == plaguename) {
            if (line[10] == "Huevecillo" || line[10] == "Chico" || line[10] == "Mediano") {
                cantUmbral.add(line[11].toInt())
            }
            if (line[10] == "Huevecillo" || line[10] == "Chico" || line[10] == "Mediano" || line[10] == "Grande" || line[10] == "Pupa") {
                cantTotal.add(line[11].toInt())
            }
            if (line[10] == "Huevecillo") cantH.add(line[11].toInt())
            if (line[10] == "Chico") cantC.add(line[11].toInt())
            if (line[10] == "Mediano") cantM.add(line[11].toInt())
            if (line[10] == "Grande") cantG.add(line[11].toInt())
            if (line[10] == "Pupa") cantP.add(line[11].toInt())
        }
    }
}
fun lecturethreefases(
    plaguename: String,
    cantUmbral: MutableList<Int>,
    cantC: MutableList<Int>,
    cantM: MutableList<Int>,
    cantG: MutableList<Int>
) {
    for (line in dataSave) {
        if (line.size > 11 && line[9] == plaguename) {
            if (line[10] == "Chico" || line[10] == "Mediano" || line[10] == "Grande") cantUmbral.add(line[11].toInt())
            if (line[10] == "Chico") cantC.add(line[11].toInt())
            if (line[10] == "Mediano") cantM.add(line[11].toInt())
            if (line[10] == "Grande") cantG.add(line[11].toInt())
        }
    }
}
fun lecturethreefasesninfa(
    plaguename: String,
    cantUmbral: MutableList<Int>,
    cantH: MutableList<Int>,
    cantN: MutableList<Int>,
    cantA: MutableList<Int>
) {
    for (line in dataSave) {
        if (line.size > 11 && line[9] == plaguename) {
            if (line[10] == "Huevecillo" || line[10] == "Ninfa" || line[10] == "Adulto") cantUmbral.add(line[11].toInt())
            if (line[10] == "Huevecillo") cantH.add(line[11].toInt())
            if (line[10] == "Ninfa") cantN.add(line[11].toInt())
            if (line[10] == "Adulto") cantA.add(line[11].toInt())
        }
    }
}
fun lecturethreefaseslarva(
    plaguename: String,
    cantUmbral: MutableList<Int>,
    cantH: MutableList<Int>,
    cantL: MutableList<Int>,
    cantA: MutableList<Int>
) {
    for (line in dataSave) {
        if (line.size > 11 && line[9] == plaguename) {
            if (line[10] == "Huevecillo" || line[10] == "Larva" || line[10] == "Adulto") cantUmbral.add(line[11].toInt())
            if (line[10] == "Huevecillo") cantH.add(line[11].toInt())
            if (line[10] == "Larva") cantL.add(line[11].toInt())
            if (line[10] == "Adulto") cantA.add(line[11].toInt())
        }
    }
}
fun lecturetwofases(
    plaguename: String,
    cantUmbral: MutableList<Int>,
    cantSA: MutableList<Int>,
    cantCA: MutableList<Int>
) {
    for (line in dataSave) {
        if (line.size > 11 && line[9] == plaguename) {
            if (line[10] == "Sin Alas" || line[10] == "Con Alas") cantUmbral.add(line[11].toInt())
            if (line[10] == "Sin Alas") cantSA.add(line[11].toInt())
            if (line[10] == "Con Alas") cantCA.add(line[11].toInt())
        }
    }
}
fun lectureonefase(plaguename: String, cantUmbral: MutableList<Int>) {
    for (line in dataSave) {
        if (line.size > 11 && line[9] == plaguename) {
            if (line[10] == "N/A") cantUmbral.add(line[11].toInt())
        }
    }
}
fun calculoumbral(umbral: MutableList<Int>): Float{
    return umbral.sum().toFloat()/ IDints.max().toFloat()
}
//Implementamos función para reiniciar las variables cuando ya se ha terminado el registro
fun lastreset(){
    //Reiniciamos variables en esta parte
    dataSave.clear()
    archiveName = "archive"
    archiveUmbral = "umbral"
    nameAgricultor = ""
    nameGranja = ""
    nameLote = ""
    Agricultor = ""
    Granja = ""
    Lote = ""
    Cultivo = ""
    DDUmbral.clear(); DDTotal.clear(); DDH.clear(); DDC.clear(); DDM.clear(); DDG.clear(); DDP.clear()
    FMUmbral.clear(); FMTotal.clear(); FMH.clear(); FMC.clear(); FMM.clear(); FMG.clear(); FMP.clear()
    GSUmbral.clear(); GSTotal.clear(); GSH.clear(); GSC.clear(); GSM.clear(); GSG.clear(); GSP.clear()
    GPUmbral.clear(); GPTotal.clear(); GPH.clear(); GPC.clear(); GPM.clear(); GPG.clear(); GPP.clear()
    DUmbral.clear(); DTotal.clear(); DH.clear(); DC.clear(); DM.clear(); DG.clear(); DP.clear()
    GDFUmbral.clear(); GDFTotal.clear(); GDFH.clear(); GDFC.clear(); GDFM.clear(); GDFG.clear(); GDFP.clear()
    GDCUmbral.clear(); GDCTotal.clear(); GDCH.clear(); GDCC.clear(); GDCM.clear(); GDCG.clear(); GDCP.clear()
    HZUmbral.clear(); HZTotal.clear(); HZH.clear(); HZC.clear(); HZM.clear(); HZG.clear(); HZP.clear()
    CLUmbral.clear(); CLC.clear(); CLM.clear(); CLG.clear()
    CUmbral.clear(); CC.clear(); CM.clear(); CG.clear()
    CNUmbral.clear(); CNC.clear(); CNM.clear(); CNG.clear()
    CLUmbral.clear(); CLC.clear(); CLM.clear(); CLG.clear()
    CHIUmbral.clear(); CHIH.clear(); CHIN.clear(); CHIA.clear()
    TUmbral.clear(); TH.clear(); TN.clear(); TA.clear()
    MBUmbral.clear(); MBH.clear(); MBN.clear(); MBA.clear()
    COCUmbral.clear(); COCH.clear(); COCL.clear(); COCA.clear()
    PVUmbral.clear(); PVCA.clear(); PVSA.clear()
    PGUmbral.clear()
    ARUmbral.clear(); ARH.clear(); ARN.clear(); ARA.clear()
    MVUmbral.clear(); MVH.clear(); MVN.clear(); MVA.clear()
    GCUmbral.clear(); GCH.clear(); GCN.clear(); GCA.clear()
    GAUmbral.clear(); GAH.clear(); GAN.clear(); GAA.clear()
    GCOGUmbral.clear(); GCOGH.clear(); GCOGN.clear(); GCOGA.clear()
    PAUmbral.clear(); PAH.clear(); PAN.clear(); PAA.clear()
    PMUmbral.clear(); PMH.clear(); PMN.clear(); PMA.clear()
    PULAUmbral.clear()
    MPUmbral.clear(); MPH.clear(); MPN.clear(); MPA.clear()

    //Reiniciamos las variables para calcular los umbrales
    DDumbralValue = 0f; FMumbralValue = 0f; GSumbralValue = 0f; GPumbralValue = 0f; DumbralValue = 0f; GDFumbralValue = 0f; GDCumbralValue = 0f; HZumbralValue = 0f; CLumbralValue = 0f; CumbralValue = 0f; CNumbralValue = 0f; CHIumbralValue = 0f; TumbralValue = 0f; MBumbralValue = 0f; COCumbralValue = 0f; PVumbralValue = 0f; PGumbralValue = 0f
    ARumbralValue = 0f; MVumbralValue = 0f; GCumbralValue = 0f; GAUmbralValue = 0f; GCOGUmbralValue = 0f; PAUmbralValue = 0f; PULAumbralValue = 0f; MPUmbralValue = 0f; PMUmbralValue = 0f
}


fun saveToDownloadsUmbralAsPdfWithImage(context: Context, data: MutableList<String>, fileName: String) {
    try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val pdfFile = File(downloadsDir, fileName)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()

        val margin = 36f

        for (pageNum in 0 until calculateTotalPages(data, margin)) {
            val page = pdfDocument.startPage(pageInfo)
            val imageSize = Pair(400f, 150f)

            // Agregar la imagen al principio del documento con un tamaño específico
            val imageBitmap = getBitmapFromMipmap(context, R.mipmap.logoti)
            val scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, imageSize.first.toInt(), imageSize.second.toInt(), false)
            val canvas: Canvas = page.canvas
            canvas.drawBitmap(scaledBitmap, margin, margin, null) // Ajustar posición y tamaño

            // Agregar los párrafos con los valores de data después de la imagen
            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 15f
            val textStartY = margin + imageSize.second + 20f // Ajustar espacio entre imagen y texto

            val startIndex = pageNum * calculateLinesPerPage(margin)
            val endIndex = minOf((pageNum + 1) * calculateLinesPerPage(margin), data.size)

            for ((index, line) in data.subList(startIndex, endIndex).withIndex()) {
                // Ajustar posición para que el texto comience después de la imagen
                canvas.drawText(line, margin, textStartY + (index + 1) * 20f, paint) // Añadir margen superior
            }

            pdfDocument.finishPage(page)
        }

        val fileOutputStream = FileOutputStream(pdfFile)
        pdfDocument.writeTo(fileOutputStream)

        pdfDocument.close()
        fileOutputStream.close()

        // Muestra un mensaje indicando que el PDF se creó con éxito
        Toast.makeText(context, "PDF creado con éxito", Toast.LENGTH_SHORT).show()
    } catch (e: IOException) {
        e.printStackTrace()

        // Muestra un mensaje de error si hay algún problema
        Toast.makeText(context, "Error al crear el PDF", Toast.LENGTH_SHORT).show()
    }
}




private fun calculateTotalPages(data: List<String>, margin: Float): Int {
    val linesPerPage = calculateLinesPerPage(margin)
    return ceil(data.size.toFloat() / linesPerPage).toInt()
}

private fun calculateLinesPerPage(margin: Float): Int {
    val pageHeight = 800f // Altura de la página
    val lineHeight = 20f // Altura de una línea de texto
    return ((pageHeight - 2 * margin) / lineHeight).toInt()
}

fun getBitmapFromMipmap(context: Context, resourceId: Int): Bitmap {
    val drawable = context.resources.getDrawable(resourceId, null)
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}


fun saveToDownloadsUmbral(context: Context, data: MutableList<String>, fileName: String) {
    try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        val fileWriter = FileWriter(file)
        for (line in data) {
            fileWriter.append(line)
            fileWriter.append("\n")
        }
        fileWriter.flush()
        fileWriter.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
fun saveToDownloadsUmbral(context: Context, data: ByteArray, fileName: String) {
    try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(data)
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
/* ---------------------------------------------------------------------------------------
|
|   Las siguientes funciones son usadas para la actividad  de MainActivity
|
 ---------------------------------------------------------------------------------------  */
private var isRotated = false

fun rotateIcon(rotationDegrees: Float, stayRotated: Boolean,ibOptions: ImageButton) {
    val rotateAnimation = RotateAnimation(
        if (isRotated) 180f else 0f, rotationDegrees,
        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
        RotateAnimation.RELATIVE_TO_SELF, 0.5f
    )
    rotateAnimation.duration = 500 // Duración de la animación en milisegundos
    rotateAnimation.interpolator = AccelerateDecelerateInterpolator()
    rotateAnimation.fillAfter = stayRotated // Mantener la posición final después de la animación

    ibOptions.startAnimation(rotateAnimation)
    isRotated = stayRotated // Actualizar el estado rotado
}

/* -----------------------------------------------------------
|
|   LAS SIGUIENTES SON FUNCIONES IMPLEMENTADAS EN LA ACTIVIDAD DE registroplagas
|
 -----------------------------------------------------------*/
//Declaramos las variables para los botones de implementar plaga o no
// Función para aumentar el contador
fun increase(cont: Int, cant: TextView, cantr: TextView, registro: LinearLayout): Int {
    val nuevoContador = cont + 1 // Aumenta el contador en 1

    // Actualiza los TextViews con el nuevo valor
    cant.text = nuevoContador.toString()
    cantr.text = nuevoContador.toString()

    // Actualiza la visibilidad del registro según el valor del contador
    registro.visibility = if (nuevoContador > 0) View.VISIBLE else View.GONE

    return nuevoContador // Retorna el nuevo valor del contador
}
fun increaseinten(cont: Int, cant: TextView, cantr: TextView, registro: LinearLayout): Int {
    val nuevoContador = cont + 10 // Aumenta el contador en 1

    // Actualiza los TextViews con el nuevo valor
    cant.text = nuevoContador.toString()
    cantr.text = nuevoContador.toString()

    // Actualiza la visibilidad del registro según el valor del contador
    registro.visibility = if (nuevoContador > 0) View.VISIBLE else View.GONE

    return nuevoContador // Retorna el nuevo valor del contador
}
// Función para disminuir el contador
fun decrease(cont: Int, cant: TextView, cantr: TextView, registro: LinearLayout): Int {
    val nuevoContador = if (cont > 0) cont - 1 else 0 // Disminuye el contador en 1 si es mayor que cero

    // Actualiza los TextViews con el nuevo valor
    cant.text = nuevoContador.toString()
    cantr.text = nuevoContador.toString()

    // Actualiza la visibilidad del registro según el valor del contador
    registro.visibility = if (nuevoContador > 0) View.VISIBLE else View.GONE

    return nuevoContador // Retorna el nuevo valor del contador
}
fun decreaseinten(cont: Int, cant: TextView, cantr: TextView, registro: LinearLayout): Int {
    val nuevoContador = if (cont > 0) cont - 10 else 0 // Disminuye el contador en 1 si es mayor que cero

    // Actualiza los TextViews con el nuevo valor
    cant.text = nuevoContador.toString()
    cantr.text = nuevoContador.toString()

    // Actualiza la visibilidad del registro según el valor del contador
    registro.visibility = if (nuevoContador > 0) View.VISIBLE else View.GONE

    return nuevoContador // Retorna el nuevo valor del contador
}
fun obtenerFechaYHora(): String {
    val calendario = Calendar.getInstance()
    val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatoFecha.format(calendario.time)
}

fun getFecha(): String {
    val calendario = Calendar.getInstance()
    val Fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return  Fecha.format(calendario.time)
}

fun getHora(): String {
    val calendario = Calendar.getInstance()
    val Hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return  Hora.format(calendario.time)
}

object AppConstants {
    val EXPIRATION_DATE = System.currentTimeMillis() + (1 * 60 * 1000)
}
open class PlagaIncremento{


}

