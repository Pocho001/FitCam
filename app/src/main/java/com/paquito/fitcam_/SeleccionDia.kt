package com.paquito.fitcam_

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class SeleccionDia : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccion_dia)

        val textDetalle = findViewById<TextView>(R.id.textDetalle)
        val fecha = intent.getStringExtra("fecha")

        if (fecha == null) {
            textDetalle.text = "❌ Fecha no valida"
            return
        }

        val prefs = getSharedPreferences("ProgresoEjercicios", MODE_PRIVATE)
        val ejercicios = prefs.all.filterKeys { it.startsWith(fecha) }

        if (ejercicios == null) {
            textDetalle.text = "\uD83D\uDCC5 $fecha: No se registraron ejercicios"
        } else {
            val builder = StringBuilder("📅 $fecha:\n\n")
            ejercicios.forEach { (clave, valor) ->
                val partes = clave.split("_")
                if (partes.size == 2) {
                    val ejercicio = partes[1]
                    builder.append("\uD83D\uDCAA $ejercicio: $valor repeticiones\\n")
                }
            }
            textDetalle.text = builder.toString()
        }
    }
}