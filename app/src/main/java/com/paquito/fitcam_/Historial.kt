package com.paquito.fitcam_

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

        contenedorMeses = findViewById(R.id.contenedorMeses)

        //generarDatosEjemplo()

        generarCalendariosPorMes()

        btnAtras.setOnClickListener {
            finish()
        }

        btnCasa.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, Usuario::class.java)
            startActivity(intent)
        }

        btnFavoritos.setOnClickListener {
            Toast.makeText(this, "Aún en construcción :c", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generarDatosEjemplo() {
        // Datos Falsos para probar
        val prefs = getSharedPreferences("ProgresoEjercicios", MODE_PRIVATE)
        val editor = prefs.edit()

        val ejercicios = listOf("Sentadilla", "Flexiones", "Abdominales")
        val random = Random()

        // Generar 15 dias aleatorios con ejercicios y repeticiones
        repeat(15) {
            val diaAleatorio = random.nextInt(28) + 1 // día del mes
            val mesAleatorio = random.nextInt(3) // últimos 3 meses
            val fecha = Calendar.getInstance().apply {
                add(Calendar.MONTH, -mesAleatorio)
                set(Calendar.DAY_OF_MONTH, diaAleatorio)
            }

            if (fecha.after(Calendar.getInstance())) return@repeat // no generar futuro

            val fechaStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fecha.time)
            val ejercicio = ejercicios.random()
            val reps = (10..40).random()

            // Guardar en SharedPreferences (simula progreso real) (archivo xml)
            editor.putString("${fechaStr}_$ejercicio", "$reps repeticiones")
        }

        editor.apply()

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
        val tituloMes = TextView(this).apply {
            text = formatoMes.format(mes.time).replaceFirstChar { it.uppercase() }
            textSize = 20f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setPadding(0, 24, 0, 16)
        }

        val grid = GridLayout(this).apply {
            columnCount = 7
            alignmentMode = GridLayout.ALIGN_BOUNDS
            useDefaultMargins = true
        }

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

        val primerDiaSemana = (mes.get(Calendar.DAY_OF_WEEK) + 5) % 7
        repeat(primerDiaSemana) {
            grid.addView(TextView(this))
        }

        val diasEnMes = mes.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (dia in 1..diasEnMes) {
            val fechaTemp = Calendar.getInstance().apply {
                time = mes.time
                set(Calendar.DAY_OF_MONTH, dia)
            }

            if (fechaTemp.after(hoy)) break

            val fecha = formato.format(fechaTemp.time)

            val botonDia = TextView(this).apply {
                text = dia.toString()
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textSize = 16f
                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.btn_dia_fondo)
                setTextColor(ContextCompat.getColor(context, R.color.white))

                // Siempre vuelve al color normal primero
                background.setTint(ContextCompat.getColor(context, R.color.dia_normal))

                // Solo se pinta si esa fecha tiene progreso
                if (todasLasFechas.contains(fecha)) {
                    background.setTint(ContextCompat.getColor(context, R.color.diaActivo))
                }

                setOnClickListener {
                    val intent = Intent(this@Historial, SeleccionDia::class.java)
                    intent.putExtra("fecha", fecha)
                    startActivity(intent)
                }
            }

            val displayMetrics = resources.displayMetrics
            val anchoPantalla = displayMetrics.widthPixels
            val anchoDia = (anchoPantalla / 10)

            val params = GridLayout.LayoutParams().apply {
                width = anchoDia
                height = anchoDia
                setMargins(6, 6, 6, 6)
            }

            botonDia.layoutParams = params
            grid.addView(botonDia)
        }

        contenedorMeses.addView(tituloMes)
        contenedorMeses.addView(grid)
    }
}
