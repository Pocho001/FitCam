package com.paquito.fitcam_

import androidx.activity.ComponentActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class SeleccionEjercicio : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccion_ejercicio)

        val btnSentadilla = findViewById<Button>(R.id.btnSentadilla)
        val btnLagartija = findViewById<Button>(R.id.btnLagartija)
        val btnAbdominal = findViewById<Button>(R.id.btnAbdominal)

        btnSentadilla.setOnClickListener {
            abrirCamara("Sentadilla")
        }

        btnLagartija.setOnClickListener {
            abrirCamara("Lagartija")
        }

        btnAbdominal.setOnClickListener {
            abrirCamara("Abdominal")
        }

    }

    private fun abrirCamara(tipo: String){
        val intent = Intent(this, TestCamera::class.java)
        intent.putExtra("ejercicio", tipo)
        startActivity(intent)
    }
}