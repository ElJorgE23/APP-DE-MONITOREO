package com.example.apptest

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver // cerrar la app
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.apptest.data.AppDatabase
import com.example.apptest.data.UserEntity
import kotlinx.coroutines.launch
import java.util.Calendar

class CloseAppReceiver : BroadcastReceiver() {// clase para cerrar la app
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "cerrar_app") {
            finishAllActivities(context)
        }
    }

    private fun finishAllActivities(context: Context?) { // metodo para cerrar pantallas
        if (context != null) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 123

    private val expirationDateCalendar = Calendar.getInstance().apply {
        set(2027, Calendar.DECEMBER, 31, 23, 59, 59)
    }

    private lateinit var ibOptions: ImageButton
    private lateinit var layoutOptions: LinearLayout
    private lateinit var layoutFrame: LinearLayout
    private lateinit var tvLogin: TextView
    private lateinit var tvSignin: TextView
    private lateinit var tvMonitoreo: TextView
    private lateinit var tvEstadistica: TextView
    private lateinit var tvLogout: TextView

    private lateinit var llLogin: LinearLayout
    private lateinit var llSignin: LinearLayout
    private lateinit var llMonitoreo: LinearLayout
    private lateinit var llEstadistica: LinearLayout
    private lateinit var llLogout: LinearLayout
    private var isRotated = false

    private lateinit var llBegin: LinearLayout
    private lateinit var llClose: LinearLayout
    private lateinit var containerMain: TableLayout
    private lateinit var cargarKML: LinearLayout

    private lateinit var tvUsername: TextView
    private lateinit var nombre: String
    private lateinit var llAdmin: LinearLayout

    private lateinit var ibMonitoreo: ImageButton
    private lateinit var ibEstadistica: ImageButton
    private lateinit var ibRegistrot: ImageButton
    private lateinit var ibVerMapas: ImageButton

    private lateinit var llTyC: LinearLayout
    private lateinit var llPP: LinearLayout

    private lateinit var db: AppDatabase
    private lateinit var sessionManager: SessionManager
//Se obtiene la fecha del calendario
    private val EXPIRATION_DATE = expirationDateCalendar.timeInMillis

    @SuppressLint("MissingInflatedId", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)
        crearAdminPorDefecto()
        // permisos de vibracion del celular
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.VIBRATE),
                PERMISSION_REQUEST_CODE
            )
        }

        ibOptions = findViewById(R.id.imageButton)
        nombre = ""

        containerMain = findViewById(R.id.mainContainer)

        llTyC = findViewById(R.id.ll_tyc)
        llPP = findViewById(R.id.ll_pp)
        llTyC.setOnClickListener { mostrarTYC() }
        llPP.setOnClickListener { mostrarPP() }

        layoutOptions = findViewById(R.id.layoutContainer)
        layoutFrame = findViewById(R.id.layoutFrame)
        tvLogin = findViewById(R.id.tv_Login)
        tvSignin = findViewById(R.id.tv_Signin)
        tvMonitoreo = findViewById(R.id.tv_Monitoreo)
        tvEstadistica = findViewById(R.id.tv_Estadisticas)
        tvLogout = findViewById(R.id.tv_Logout)

        llLogin = findViewById(R.id.ll_Login)
        llSignin = findViewById(R.id.ll_Signin)
        llMonitoreo = findViewById(R.id.ll_Monitoreos)
        llEstadistica = findViewById(R.id.ll_Estadisticas)
        llLogout = findViewById(R.id.ll_Logout)

        llBegin = findViewById(R.id.ll_Begin)
        llClose = findViewById(R.id.ll_Close)
        cargarKML = findViewById(R.id.ll_Cargarkml)
        tvUsername = findViewById(R.id.username)

        ibMonitoreo = findViewById(R.id.ib_Monitoreo)
        ibEstadistica = findViewById(R.id.ib_Estadistica)
        ibRegistrot = findViewById(R.id.ib_Registrost)
        ibVerMapas = findViewById(R.id.ib_Vermapas)

        llAdmin = findViewById(R.id.ll_administrador)

        verificarEstadoSesion()
        actInfoUser()

        ibOptions.setOnClickListener {
            if (layoutOptions.visibility == View.VISIBLE) {
                layoutOptions.visibility = View.GONE
                containerMain.visibility = View.VISIBLE
            } else {
                layoutOptions.visibility = View.VISIBLE
                containerMain.visibility = View.GONE
            }

            rotateIcon(180f, !isRotated, ibOptions)
            verificarEstadoSesion()
            actInfoUser()
        }

        llClose.setOnClickListener {
            val broadcastIntent = Intent("cerrar_app")
            sendBroadcast(broadcastIntent)
            finishAffinity()
        }

        llTyC.setOnClickListener { mostrarTYC() }
        llPP.setOnClickListener { mostrarPP() }

        llLogin.setOnClickListener {
            mostrarLogin()
        }

        llSignin.setOnClickListener {
            mostrarRegistro()
        }

        llLogout.setOnClickListener {
            logOut()
        }

        llMonitoreo.setOnClickListener {
            if (sessionManager.estaLogueado()) {
                openActivity(pantalla_monitoreos::class.java)
            } else {
                showMessage("Primero inicia sesión.")
            }
        }

        llEstadistica.setOnClickListener {
            if (sessionManager.estaLogueado()) {
                openActivity(pantalla_estadisticas::class.java)
            } else {
                showMessage("Primero inicia sesión.")
            }
        }

        cargarKML.setOnClickListener {
            if (sessionManager.estaLogueado()) {
                openActivity(loadkml::class.java)
            } else {
                showMessage("Primero inicia sesión.")
            }
        }

        ibMonitoreo.setOnClickListener {
            if (sessionManager.estaLogueado()) {
                openActivity(pantalla_monitoreos::class.java)
            } else {
                showMessage("Primero inicia sesión.")
            }
        }

        ibEstadistica.setOnClickListener {
            if (sessionManager.estaLogueado()) {
                openActivity(pantalla_estadisticas::class.java)
            } else {
                showMessage("Primero inicia sesión.")
            }
        }

        ibVerMapas.setOnClickListener {
            if (sessionManager.estaLogueado()) {
                openActivity(pantalla_mapas::class.java)
            } else {
                showMessage("Primero inicia sesión.")
            }
        }

        ibRegistrot.setOnClickListener {
            if (sessionManager.estaLogueado()) {
                openActivity(Pantalla_registroeventos::class.java)
            } else {
                showMessage("Primero inicia sesión.")
            }
        }
    }

    private fun crearAdminPorDefecto() {
        lifecycleScope.launch {
            val adminExistente = db.userDao().obtenerUsuarioPorCorreo("admin@tierra.com")
            if (adminExistente == null) {
                val admin = UserEntity(
                    nombre = "Administrador",
                    apellido = "General",
                    celular = "0000000000",
                    correo = "admin@tierra.com",
                    password = "1234",
                    rol = "Administrador",
                    agricola = "Agricola Default"
                )
                db.userDao().insertarUsuario(admin)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showMessage("Saliendo de la app.")
    }

    fun isDemoExpired(): Boolean {
        return System.currentTimeMillis() > EXPIRATION_DATE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showMessage("Habilita las notificaciones en la App para una mejor experiencia.")
            }
        }
    }

    fun showMessage(mostrarMsj: String) {
        Toast.makeText(applicationContext, mostrarMsj, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingInflatedId")
    private fun mostrarRegistro() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.registro_usuario, null)
        builder.setView(dialogView)

        val ptName = dialogView.findViewById<EditText>(R.id.pt_Name)
        val ptLastName = dialogView.findViewById<EditText>(R.id.pt_LastName)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)
        val etCellPhone = dialogView.findViewById<EditText>(R.id.n_CellPhone)
        val cbPassword = dialogView.findViewById<CheckBox>(R.id.checkBoxPassword)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)

        cbPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etPassword.transformationMethod = null
                etConfirmPassword.transformationMethod = null
            } else {
                etPassword.transformationMethod =
                    android.text.method.PasswordTransformationMethod.getInstance()
                etConfirmPassword.transformationMethod =
                    android.text.method.PasswordTransformationMethod.getInstance()
            }
        }

        builder.setPositiveButton("Registrar", null)
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        val alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = ptName.text.toString().trim()
            val lastName = ptLastName.text.toString().trim()
            val cellPhone = etCellPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (name.isEmpty() || lastName.isEmpty() || cellPhone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showMessage("Completa todos los campos.")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showMessage("Las contraseñas no coinciden.")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuarioExistente = db.userDao().obtenerUsuarioPorCorreo(email)

                if (usuarioExistente != null) {
                    showMessage("Ese correo ya está registrado.")
                } else {
                    val rolUsuario =
                        if (email.equals("admin@tierra.com", ignoreCase = true)) {
                            "Administrador"
                        } else {
                            "Monitoreador"
                        }

                    val nuevoUsuario = UserEntity(
                        nombre = name,
                        apellido = lastName,
                        celular = cellPhone,
                        correo = email,
                        password = password,
                        rol = rolUsuario,
                        agricola = "Agricola Default"
                    )

                    db.userDao().insertarUsuario(nuevoUsuario)
                    showMessage("Usuario registrado correctamente.")
                    alertDialog.dismiss()
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun mostrarLogin() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.login_usuario, null)
        builder.setView(dialogView)

        val etEmail = dialogView.findViewById<EditText>(R.id.et_emailLogin)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPasswordLogin)
        val cbLogin = dialogView.findViewById<CheckBox>(R.id.cb_login)

        cbLogin.setOnCheckedChangeListener { _, isChecked ->
            etPassword.transformationMethod =
                if (isChecked) null
                else android.text.method.PasswordTransformationMethod.getInstance()
        }

        builder.setPositiveButton("Iniciar Sesión", null)
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        val alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showMessage("Ingresa correo y contraseña.")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuario = db.userDao().login(email, password)

                if (usuario != null) {
                    sessionManager.guardarSesion(usuario.id, usuario.nombre, usuario.rol)
                    loggedUser = "${usuario.nombre} ${usuario.apellido}"
                    showMessage("Has iniciado sesión correctamente.")
                    verificarEstadoSesion()
                    actInfoUser()
                    alertDialog.dismiss()
                } else {
                    showMessage("Correo o contraseña incorrectos.")
                }
            }
        }
    }

    private fun mostrarTYC() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.layout_tyc, null)
        builder.setView(dialogView)
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun mostrarPP() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.layout_pp, null)
        builder.setView(dialogView)
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun logOut() {
        sessionManager.cerrarSesion()
        llAdmin.visibility = View.GONE
        tvUsername.text = "No se ha iniciado sesión."
        llBegin.isEnabled = false
        loggedUser = ""
        showMessage("Has cerrado sesión")
        verificarEstadoSesion()
        actInfoUser()
    }

    private fun verificarEstadoSesion() {
        if (sessionManager.estaLogueado()) {
            val userId = sessionManager.obtenerUserId()

            lifecycleScope.launch {
                val usuario = db.userDao().obtenerUsuarioPorId(userId)

                if (usuario != null) {
                    tvUsername.text = "Usuario: ${usuario.nombre} ${usuario.apellido}"
                    loggedUser = "${usuario.nombre} ${usuario.apellido}"

                    llBegin.isEnabled = true
                    llBegin.setOnClickListener {
                        val intent = Intent(this@MainActivity, Pantalla_registroeventos::class.java)
                        startActivity(intent)
                    }
                } else {
                    tvUsername.text = "No se encontró la información del usuario."
                    llBegin.isEnabled = false
                }
            }
        } else {
            tvUsername.text = "No se ha iniciado sesión."
            llBegin.isEnabled = false
            llBegin.setOnClickListener {
                Toast.makeText(
                    this,
                    "Por favor, inicia sesión para continuar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun actInfoUser() {
        if (sessionManager.estaLogueado()) {
            val userId = sessionManager.obtenerUserId()

            lifecycleScope.launch {
                val usuario = db.userDao().obtenerUsuarioPorId(userId)

                if (usuario != null) {
                    nombre = usuario.nombre
                    llAdmin.visibility =
                        if (usuario.rol == "Administrador") View.VISIBLE else View.GONE
                } else {
                    llAdmin.visibility = View.GONE
                }
            }
        } else {
            llAdmin.visibility = View.GONE
        }
    }

    private fun openActivity(sto: Class<*>) {
        val intent = Intent(this, sto)
        startActivity(intent)
    }

    fun notifyUser(context: Context, mensajex: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_01"
            val channelName = "My channel"
            val channelDescription = "My channel description"
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = channelDescription
                }
            )
        }

        val builder = NotificationCompat.Builder(context, "my_channel_01")
            .setSmallIcon(R.mipmap.logoti)
            .setContentTitle("¡Se ha subido un monitoreo!")
            .setContentText(mensajex)
            .setAutoCancel(true)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        builder.setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }

    private fun rotateIcon(degrees: Float, rotate: Boolean, view: View) {
        val toDegree = if (rotate) degrees else 0f

        view.animate()
            .rotation(toDegree)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        isRotated = rotate
    }
}