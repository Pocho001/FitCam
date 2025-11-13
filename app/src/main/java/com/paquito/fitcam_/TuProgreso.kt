package com.paquito.fitcam_

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TuProgreso : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tu_progreso)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val txtTuProgreso = findViewById<TextView>(R.id.textTuProgreso)

        txtTuProgreso.text = "Tu Progreso"

        val chart = findViewById<LineChart>(R.id.chartProgreso)
        mostrarGrafica(chart)

        btnAtras.setOnClickListener { finish() }

        btnCasa.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnFavoritos.setOnClickListener {
            Toast.makeText(this, "Aún en construcción :c", Toast.LENGTH_SHORT).show()
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, Usuario::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarGrafica(chart: LineChart) {
        val prefs = getSharedPreferences("ProgresoEjercicios", MODE_PRIVATE)
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 🔹 Agrupar por día, sumando las repeticiones
        val datosPorDia = mutableMapOf<String, Int>()
        for ((key, value) in prefs.all) {
            val partes = key.split("_")
            if (partes.size < 2) continue

            val fecha = partes[0]
            val texto = value.toString()
            val reps = texto.filter { it.isDigit() }.toIntOrNull() ?: 0

            datosPorDia[fecha] = (datosPorDia[fecha] ?: 0) + reps
        }

        if (datosPorDia.isEmpty()) {
            Toast.makeText(this, "No hay datos para mostrar aún", Toast.LENGTH_SHORT).show()
            return
        }

        val fechasOrdenadas = datosPorDia.keys.sortedBy { formato.parse(it) }
        val entradas = mutableListOf<Entry>()

        fechasOrdenadas.forEachIndexed { index, fecha ->
            val valor = datosPorDia[fecha]?.toFloat() ?: 0f
            entradas.add(Entry(index.toFloat(), valor))
        }

        val dataSet = LineDataSet(entradas, "Repeticiones totales por día").apply {
            color = Color.YELLOW
            valueTextColor = Color.WHITE
            lineWidth = 3f
            circleRadius = 5f
            setCircleColor(Color.YELLOW)
            setDrawValues(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER // curva suave
        }

        chart.data = LineData(dataSet)
        chart.axisLeft.textColor = Color.WHITE
        chart.axisRight.isEnabled = false
        chart.legend.textColor = Color.WHITE
        chart.setBackgroundColor(Color.parseColor("#1E1E1E"))
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)

        chart.xAxis.apply {
            textColor = Color.WHITE
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val i = value.toInt()
                    return if (i in fechasOrdenadas.indices) fechasOrdenadas[i].substring(5) else ""
                }
            }
        }

        val desc = Description().apply { text = "" }
        chart.description = desc

        chart.invalidate()
    }
}
