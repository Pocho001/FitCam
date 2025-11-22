package com.paquito.fitcam_

import androidx.activity.ComponentActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout

class SeleccionEjercicioDorso : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var layoutBotonesAccion: LinearLayout
    private var ejercicioSeleccionado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccion_ejercicio_dorso)

        sharedPreferences = getSharedPreferences("favoritos_app", MODE_PRIVATE)
        layoutBotonesAccion = findViewById(R.id.layoutBotonesAccion)

        // Botones de ejercicios
        val btnLagartija = findViewById<Button>(R.id.btnLagartija)
        val btnAbdominal = findViewById<Button>(R.id.btnAbdominal)

        // Botones de acción (genéricos)
        val btnFavorito = findViewById<ImageButton>(R.id.btnFavorito)
        val btnComenzar = findViewById<Button>(R.id.btnComenzar)

        // LISTENER PARA BOTÓN "Lagartija"
        btnLagartija.setOnClickListener {
            ejercicioSeleccionado = "Lagartija"
            mostrarBotonesAccion()
            actualizarBotonFavorito()
        }
        // LISTENER PARA BOTÓN "Abdominales"
        btnAbdominal.setOnClickListener {
            ejercicioSeleccionado = "Abdominal"
            mostrarBotonesAccion()
            actualizarBotonFavorito()
        }

        // LISTENER PARA BOTÓN "FAVORITO" (genérico)
        btnFavorito.setOnClickListener {
            if (ejercicioSeleccionado.isNotEmpty()) {
                toggleFavorito(ejercicioSeleccionado)
                actualizarBotonFavorito()
            }
        }

        // LISTENER PARA BOTÓN "COMENZAR" (genérico)
        btnComenzar.setOnClickListener {
            if (ejercicioSeleccionado.isNotEmpty()) {
                abrirCamara(ejercicioSeleccionado)
            }
        }
    }

    private fun mostrarBotonesAccion() {
        layoutBotonesAccion.visibility = View.VISIBLE
    }

    private fun toggleFavorito(ejercicio: String) {
        val favoritos = sharedPreferences.getStringSet("favoritos", mutableSetOf()) ?: mutableSetOf()
        val nuevosFavoritos = favoritos.toMutableSet()

        if (nuevosFavoritos.contains(ejercicio)) {
            nuevosFavoritos.remove(ejercicio)
        } else {
            nuevosFavoritos.add(ejercicio)
        }

        sharedPreferences.edit().putStringSet("favoritos", nuevosFavoritos).apply()
    }

    private fun actualizarBotonFavorito() {
        val favoritos = sharedPreferences.getStringSet("favoritos", mutableSetOf()) ?: mutableSetOf()
        val btnFavorito = findViewById<ImageButton>(R.id.btnFavorito)

        if (ejercicioSeleccionado.isNotEmpty()) {
            btnFavorito.setImageResource(
                if (favoritos.contains(ejercicioSeleccionado)) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
        }
    }

    private fun abrirCamara(tipo: String) {
        val intent = Intent(this, Camara::class.java)
        intent.putExtra("ejercicio", tipo)
        startActivity(intent)
    }
}