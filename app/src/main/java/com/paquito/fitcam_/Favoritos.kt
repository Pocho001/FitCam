package com.paquito.fitcam_

import androidx.activity.ComponentActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class Favoritos : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var layoutListaFavoritos: LinearLayout
    private lateinit var txtNoFavoritos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favoritos)

        sharedPreferences = getSharedPreferences("favoritos_app", MODE_PRIVATE)

        layoutListaFavoritos = findViewById(R.id.layoutListaFavoritos)
        txtNoFavoritos = findViewById(R.id.txtNoFavoritos)

        // Botones de navegación (MISMOS IDs que el MainActivity)
        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

        btnCasa.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnFavoritos.setOnClickListener {
            // Ya estamos en favoritos, no hacer nada
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, DatosDeUsuario::class.java)
            startActivity(intent)
            finish()
        }

        cargarFavoritos()
    }

    override fun onResume() {
        super.onResume()
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        val favoritos = sharedPreferences.getStringSet("favoritos", mutableSetOf()) ?: mutableSetOf()

        layoutListaFavoritos.removeAllViews()

        if (favoritos.isEmpty()) {
            txtNoFavoritos.visibility = View.VISIBLE
            layoutListaFavoritos.visibility = View.GONE
        } else {
            txtNoFavoritos.visibility = View.GONE
            layoutListaFavoritos.visibility = View.VISIBLE

            favoritos.forEach { ejercicio ->
                val button = crearBotonEjercicio(ejercicio)
                layoutListaFavoritos.addView(button)
            }
        }
    }

    private fun crearBotonEjercicio(ejercicio: String): Button {
        val button = Button(this)
        button.text = ejercicio
        button.textSize = 16f

        button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 16)
        button.layoutParams = layoutParams

        button.setOnClickListener {
            abrirCamara(ejercicio)
        }

        return button
    }

    private fun abrirCamara(tipo: String) {
        val intent = Intent(this, Camara::class.java)
        intent.putExtra("ejercicio", tipo)
        startActivity(intent)
    }
}