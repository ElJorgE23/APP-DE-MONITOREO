package com.example.apptest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class pantalla_estadisticas : AppCompatActivity() {

    private lateinit var backStats: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_estadisticas)

        backStats = findViewById(R.id.ib_backstats)
        backStats.setOnClickListener {
            openActivity(MainActivity::class.java)
        }
    }

    private fun openActivity(sto: Class<*>) {
        val intent = Intent(this, sto)
        startActivity(intent)
        finish()
    }
}