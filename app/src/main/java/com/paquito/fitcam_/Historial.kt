package com.paquito.fitcam_

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class Historial : ComponentActivity() {

    private lateinit var contenedorMeses: LinearLayout
    private val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val formatoMes = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial)

        contenedorMeses = findViewById(R.id.contenedorMeses)
        generarCalendariosPorMes()
    }

    private fun generarCalendariosPorMes() {
        val prefs = getSharedPreferences("ProgresoEjercicios", MODE_PRIVATE)
        val todasLasFechas = prefs.all.keys.mapNotNull { it.split("_").firstOrNull() }.toSet()

        val hoy = Calendar.getInstance()

        // Generar los ultimos 3 meses (incluyendo el actual)
        for (i in 0..2) {
            val mes = Calendar.getInstance().apply {
                add(Calendar.MONTH, -i)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            agregarMes(mes, hoy, todasLasFechas)
        }
    }

    private fun agregarMes(mes: Calendar, hoy: Calendar, todasLasFechas: Set<String>) {
        // Titulo del mes
        val tituloMes = TextView(this).apply {
            text = formatoMes.format(mes.time).replaceFirstChar { it.uppercase() }
            textSize = 20f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setPadding(0, 24, 0, 16)
        }

        // Crear el grid del mes
        val grid = GridLayout(this).apply {
            columnCount = 7
            alignmentMode = GridLayout.ALIGN_BOUNDS
            useDefaultMargins = true
        }

        // Agregar encabezados L, M, X, J, V, S, D
        val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")
        for (dia in diasSemana) {
            val header = TextView(this).apply {
                text = dia
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textSize = 16f
                setPadding(4, 4, 4, 4)
            }
            grid.addView(header)
        }

        // Posicionar correctamente el primer dia del mes
        val primerDiaSemana = (mes.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Lunes = 0
        repeat(primerDiaSemana) {
            grid.addView(TextView(this))
        }

        // Agregar los dias
        val diasEnMes = mes.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (dia in 1..diasEnMes) {
            mes.set(Calendar.DAY_OF_MONTH, dia)
            if (mes.after(hoy)) break

            val fecha = formato.format(mes.time)
            val botonDia = TextView(this).apply {
                text = dia.toString()
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textSize = 16f
                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.btn_dia_fondo)

                if (todasLasFechas.contains(fecha))
                    background.setTint(ContextCompat.getColor(context, R.color.diaActivo))

                setOnClickListener {
                    val intent = Intent(this@Historial, SeleccionDia::class.java)
                    intent.putExtra("fecha", fecha)
                    startActivity(intent)
                }
            }

            val params = GridLayout.LayoutParams().apply {
                width = 120
                height = 120
                setMargins(6, 6, 6, 6)
            }

            botonDia.layoutParams = params
            grid.addView(botonDia)
        }

        // Agregar todo al contenedor principal
        contenedorMeses.addView(tituloMes)
        contenedorMeses.addView(grid)
    }
}
