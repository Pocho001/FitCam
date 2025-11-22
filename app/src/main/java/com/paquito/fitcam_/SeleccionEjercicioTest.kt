package com.paquito.fitcam_

import androidx.activity.ComponentActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class SeleccionEjercicioTest : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccion_ejercicio)


    }

    private fun abrirCamara(tipo: String){
        val intent = Intent(this, Camara::class.java)
        intent.putExtra("ejercicio", tipo)
        startActivity(intent)
    }
}