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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TuProgreso : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tu_progreso)

        // Botones
        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

        // Textos
        val txtTuProgreso = findViewById<TextView>(R.id.textTuProgreso)
        val txtUltimos30Dias = findViewById<TextView>(R.id.textUltimos30dias)
        val txtTusRepeticiones = findViewById<TextView>(R.id.textTusRepeticiones)

        // Se ponen los Strings en los TextView de los que van a tener
        txtTuProgreso.text = "Tu Progreso"
        txtUltimos30Dias.text = "Últimos 30 días de tu progreso"
        txtTusRepeticiones.text = "Tus repeticiones"

        // Se crea una grafica (chart) de tipo LineChart
        val chart = findViewById<LineChart>(R.id.chartProgreso)
        mostrarGrafica(chart)

        // Boton de atras
        btnAtras.setOnClickListener { finish() }

        // Boton "Home"
        btnCasa.setOnClickListener {
            // Se empieza la "actividad Main" (menú principal)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Boton Perfil de Usuario
        btnFavoritos.setOnClickListener {
            // Aún no se termina este apartado (mensaje de error)
            Toast.makeText(this, "Aún en construcción :c", Toast.LENGTH_SHORT).show()
        }

        btnPerfil.setOnClickListener {
            // Se empieza la "actividad Usuario" (Perfil)
            val intent = Intent(this, Usuario::class.java)
            startActivity(intent)
        }
    }

    // Funcion para mostrar la gráfica
    private fun mostrarGrafica(chart: LineChart) {
        // Se accede al archivo .xml con los datos del progreso de ejercicios
        val prefs = getSharedPreferences("ProgresoEjercicios", MODE_PRIVATE)
        // Se toma el formato "Año-Mes-Día"
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Filtra los ultimos 30 días
        val hoy = Calendar.getInstance()
        val hace30 = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -30)
        }

        val datosPorDia = mutableMapOf<String, Int>()

        for ((key, value) in prefs.all) {
            val partes = key.split("_")
            if (partes.size < 2) continue

            val fecha = partes[0]
            val reps = value.toString().filter { it.isDigit() }.toIntOrNull() ?: 0
            datosPorDia[fecha] = (datosPorDia[fecha] ?: 0) + reps
        }

        // Contar repeticiones de los ultimos 30 dias
        val txtRepeticionesTotales = findViewById<TextView>(R.id.textRepeticionesTotales)
        val total30 = contarUltimos30Dias(datosPorDia)
        txtRepeticionesTotales.text = "$total30"

        val datosUltimos30 = datosPorDia.filter { (fechaStr, _) ->
            val fecha = formato.parse(fechaStr) ?: return@filter false
            fecha.after(hace30.time) || fecha == hace30.time
        }

        // Si el getSharePreference no puede acceder al .xml o no tiene nada para mostrar
        if (datosUltimos30.isEmpty()) {
            Toast.makeText(this, "No hay datos para mostrar aún", Toast.LENGTH_SHORT).show()
            return
        }

        // Toma todas las fehas con datosUltimos30 y las ordena con .sortedBy { formato.parse(it) }
        val fechasOrdenadas = datosUltimos30.keys.sortedBy { formato.parse(it) }
        // Crea una lista vacia que se llenara con coordenadas para la gráfica
        val entradas = mutableListOf<Entry>()

        // Recorre la lista de fechas ordenadas
        fechasOrdenadas.forEachIndexed { index, fecha ->
            val valor = datosUltimos30[fecha]?.toFloat() ?: 0f
            // Se crea un punto en la grafica con el indice y el valor encontrado
            entradas.add(Entry(index.toFloat(), valor))
        }

        // Crea un conjunto de varios datos para la gráfica
        val dataSet = LineDataSet(entradas, "Repeticiones totales por día").apply {
            // Color de la linea
            color = Color.YELLOW
            // Color de los numeros encima de los puntos
            valueTextColor = Color.WHITE
            // Grueso de la linea
            lineWidth = 3f
            // Tamaño de los circulos de cada punto
            circleRadius = 5f
            // Color de los circulos
            setCircleColor(Color.YELLOW)
            // Muestra el valor de cada punto
            setDrawValues(true)
            // Hace que la linea sea curva y no recta
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        // Carga el dataset creado en la gráfica
        chart.data = LineData(dataSet)
        // Color blanco de los números de la izquierda
        chart.axisLeft.textColor = Color.WHITE
        // Se desactiva el eje derecho para q no se vea duplicado con eel de la izquierda
        chart.axisRight.isEnabled = false
        // Color blanco el texto de la leyenda (Repeticiones totales por dia)
        chart.legend.textColor = Color.WHITE
        // Pone el fondo de la gráfica de color gris oscuro
        chart.setBackgroundColor(Color.parseColor("#1E1E1E"))
        // Activa la funcion para "picar" los puntos
        chart.setTouchEnabled(true)
        // Activa la función para poder hacerle zoom a la gráfica con los dedos
        chart.setPinchZoom(true)

        chart.xAxis.apply {
            // Color blanco para el texto del eje x
            textColor = Color.WHITE
            // Se colocan etiquetas abajo de la gráfica
            position = XAxis.XAxisPosition.BOTTOM
            // Se hace que se muestren las etiquetas de una a una (0, 1, 2, ...)
            granularity = 1f
            // Se convierte el numero por fecha real
            valueFormatter = object : ValueFormatter() {
                // Convertir un número Float del eje X del grafico en un String
                override fun getFormattedValue(value: Float): String {
                    // Se convierte el "value" en entero
                    val i = value.toInt()
                    // if (i in fechasOrdenadas.indices)
                    // Se verifica que el indice exista en la lista de la lista ordenada
                    // fechasOrdenadas[i].substring(5)
                    // Se corta la fecha al caracter 5 hacia adelante (2024-) mostrando mes y día
                    return if (i in fechasOrdenadas.indices) fechasOrdenadas[i].substring(5) else ""
                }
            }
        }

        // Quita el mensaje de descripción que pone la libreria por defecto
        val desc = Description().apply { text = "" }
        chart.description = desc

        // Se redibuja la gráfica
        chart.invalidate()
    }

    private fun contarUltimos30Dias(datos: Map<String, Int>): Int {
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val hoy = LocalDate.now()
        val hace30 = hoy.minusDays(30)

        var total = 0

        for ((fechaStr, reps) in datos) {
            val fecha = LocalDate.parse(fechaStr, formato)
            if (fecha.isAfter(hace30) || fecha.isEqual(hace30)) {
                total += reps
            }
        }

        return total
    }
}
