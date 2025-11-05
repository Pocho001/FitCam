package com.paquito.fitcam_

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class Historial : ComponentActivity() {

    private lateinit var gridCalendario: GridLayout
    private lateinit var textTitulo: TextView
    private val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial)

        val textTitulo = findViewById<TextView>(R.id.textTitulo)
        val gridCalendario = findViewById<GridLayout>(R.id.gridCalendario)

        textTitulo.text = "\uD83D\uDCC6 Progreso (ultimos 3 meses)"
        generarCalendario()
    }

    private fun generarCalendario(){
        val prefs = getSharedPreferences("ProgresoEjercicios", MODE_PRIVATE)
        val todasLasFechas = prefs.all.keys.mapNotNull { it.split("_").firstOrNull() }.toSet()

        val hoy = Calendar.getInstance()
        val tresMesesAtras = Calendar.getInstance().apply{
            add(Calendar.MONTH, -3)
        }

        val calendario = Calendar.getInstance()
        calendario.time = tresMesesAtras.time

        while (calendario.before(hoy) || calendario == hoy){
            val fecha = formato.format(calendario.time)
            val botonDia = TextView(this).apply {
                text = calendario.get(Calendar.DAY_OF_MONTH).toString()
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textSize = 16f
                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.btn_dia_fondo)

                if(todasLasFechas.contains(fecha))
                    background.setTint(ContextCompat.getColor(context, R.color.diaActivo))

                setOnClickListener {
                    val intent = Intent(this@Historial, SeleccionDia::class.java)
                    startActivity(intent)
                }
            }

            gridCalendario.addView(botonDia, params)
            calendario.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}