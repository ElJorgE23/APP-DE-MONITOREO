package com.example.apptest

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_ROL = "rol"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun guardarSesion(userId: Int, nombre: String, rol: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_NOMBRE, nombre)
            .putString(KEY_ROL, rol)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }

    fun estaLogueado(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun obtenerUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun obtenerNombre(): String {
        return prefs.getString(KEY_NOMBRE, "") ?: ""
    }

    fun obtenerRol(): String {
        return prefs.getString(KEY_ROL, "") ?: ""
    }
}