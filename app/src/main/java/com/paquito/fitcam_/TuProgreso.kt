package com.paquito.fitcam_

import android.content.Intent
import android.graphics.Color
import android.media.Image
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
import java.text.SimpleDateFormat
import java.util.*

class TuProgreso : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tu_progreso)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

        val txtTuProgreso = findViewById<TextView>(R.id.txtTuProgreso)

        txtTuProgreso.text="Tu Progreso"

        val chart = findViewById<LineChart>(R.id.chartProgreso)
        mostrarGrafica(chart)

        btnAtras.setOnClickListener {
            finish()
        }

        btnCasa.setOnClickListener {
            Toast.makeText(this, "Ya se encuentra en el main", Toast.LENGTH_SHORT).show()
        }

        btnFavoritos.setOnClickListener {
            Toast.makeText(this, "Aún en construcción :c", Toast.LENGTH_SHORT).show()
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, Usuario::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarGrafica(chart: LineChart){
        val prefs = getSharedPreferences("ProgresoEjercicios", MODE_PRIVATE)
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val datosPorDia = mutableMapOf<String, Int>()

        for(key in prefs.all.keys){
            val fecha = key.split("_").firstOrNull() ?: continue
            datosPorDia[fecha] = (datosPorDia[fecha] ?: 0) + 1
        }

        val fechasOrdenadas = datosPorDia.keys.sortedBy { formato.parse(it) }

        val entradas = mutableListOf<Entry>()
        fechasOrdenadas.forEachIndexed { index, fecha ->
            entradas.add(Entry(index.toFloat(), datosPorDia[fecha]?.toFloat() ?: 0f))
        }

        val dataSet = LineDataSet(entradas, "Ejercicios completados").apply {
            color = Color.YELLOW
            valueTextColor = Color.WHITE
            lineWidth = 3f
            circleRadius = 5f
            setCircleColor(Color.YELLOW)
            setDrawValues(true)
        }

        chart.data = LineData(dataSet)

        chart.axisLeft.textColor = Color.WHITE
        chart.axisRight.isEnabled = false
        chart.xAxis.apply {
            textColor = Color.WHITE
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val i = value.toInt()
                    return if (i in fechasOrdenadas.indices) fechasOrdenadas[i].substring(5) else ""
                }
            }
        }

        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.setBackgroundColor(Color.parseColor("#1E1E1E"))
        chart.legend.textColor = Color.WHITE

        val desc = Description()
        desc.text = ""
        chart.description = desc

        chart.invalidate()
    }
}