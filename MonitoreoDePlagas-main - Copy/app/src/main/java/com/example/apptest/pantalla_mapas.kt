package com.example.apptest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class pantalla_mapas : AppCompatActivity() {

    private lateinit var backVermapas: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_mapas)

        backVermapas = findViewById(R.id.ib_backvermapas)
        backVermapas.setOnClickListener {
            openActivity(MainActivity::class.java)
        }
    }

    private fun openActivity(sto: Class<*>) {
        val intent = Intent(this, sto)
        startActivity(intent)
        finish()
    }
}