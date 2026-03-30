package com.example.apptest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var cbAceptTyC: CheckBox
    private lateinit var cbAceptPP: CheckBox

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        cbAceptTyC = findViewById(R.id.acept_tyc)
        cbAceptPP = findViewById(R.id.acept_pp)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val acceptedTerms = sharedPreferences.getBoolean("AcceptedTerms", false)

        if (acceptedTerms) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val acceptButton: Button = findViewById(R.id.b_accept)
        acceptButton.setOnClickListener {
            if (cbAceptPP.isChecked && cbAceptTyC.isChecked) {
                with(sharedPreferences.edit()) {
                    putBoolean("AcceptedTerms", true)
                    apply()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else if (cbAceptPP.isChecked && !cbAceptTyC.isChecked) {
                showMessage("Debes aceptar los Términos y Condiciones para comenzar.")
            } else if (!cbAceptPP.isChecked && cbAceptTyC.isChecked) {
                showMessage("Debes aceptar las Políticas de Privacidad para comenzar.")
            } else {
                showMessage("Debes aceptar los Términos y Condiciones y Políticas de Privacidad para comenzar.")
            }
        }
    }

    private fun showMessage(mostrarMsj: String) {
        Toast.makeText(applicationContext, mostrarMsj, Toast.LENGTH_SHORT).show()
    }
}