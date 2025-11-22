package com.paquito.fitcam_


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.widget.TextView

class MainActivity : ComponentActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val consejos = listOf(
            "Toma agua antes de cada ejercicio 💧",
            "Haz estiramientos 5 minutos al despertar 🧘‍♂️",
            "No te saltes el desayuno 🍳",
            "Camina al menos 30 minutos al dia 🚶",
            "Duerme bien, tu cuerpo lo necesita 😴",
            "No te compares, avanza a tu ritmo 💪"
        )

        val txtConsejos = findViewById<TextView>(R.id.txtConsejos)
        val txtQueVamosAHacerHoy = findViewById<TextView>(R.id.txtQueVamosAHacerHoy)
        val txtPierna = findViewById<TextView>(R.id.txtPierna)
        val txtBrazo = findViewById<TextView>(R.id.txtBrazo)
        val txtEspalda = findViewById<TextView>(R.id.txtEspalda)
        val txtDorso = findViewById<TextView>(R.id.txtDorso)
        val txtObservaTuProgreso = findViewById<TextView>(R.id.txtObservaTuProgreso)
        val txtHistorial = findViewById<TextView>(R.id.txtHistorial)
        val txtTuProgreso = findViewById<TextView>(R.id.txtTuProgreso)

        val btnEjercicioAleatorio = findViewById<ImageButton>(R.id.btnEjercicioAleatorio)
        val btnPierna = findViewById<ImageButton>(R.id.btnPierna)
        val btnBrazo = findViewById<ImageButton>(R.id.btnBrazo)
        val btnEspalda = findViewById<ImageButton>(R.id.btnEspalda)
        val btnDorso = findViewById<ImageButton>(R.id.btnDorso)
        val btnHistorial = findViewById<ImageButton>(R.id.btnHistorial)
        val btnTuProgreso = findViewById<ImageButton>(R.id.btnTuProgreso)
        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

        txtConsejos.text = consejos.random()
        txtQueVamosAHacerHoy.text = "¿Qué vámos a hacer hoy?"
        txtPierna.text = "Pierna"
        txtBrazo.text = "Brazo"
        txtEspalda.text = "Espalda"
        txtDorso.text = "Dorso"
        txtObservaTuProgreso.text = "Observa tu progreso"
        txtHistorial.text = "Historial"
        txtTuProgreso.text = "Tu progreso"

        btnEjercicioAleatorio.setOnClickListener {
            val intent = Intent(this, Camara::class.java)
            startActivity(intent)
        }
        btnPierna.setOnClickListener {
            val intent = Intent(this, SeleccionEjercicioPierna::class.java)
            startActivity(intent)
        }
        btnBrazo.setOnClickListener {
            Toast.makeText(this, "Aún en construcción :c", Toast.LENGTH_SHORT).show()
        }
        btnEspalda.setOnClickListener {
            Toast.makeText(this, "Aún en construcción :c", Toast.LENGTH_SHORT).show()
        }
        btnDorso.setOnClickListener {
            val intent = Intent(this, SeleccionEjercicioDorso::class.java)
            startActivity(intent)
        }
        btnHistorial.setOnClickListener {
            val intent = Intent(this, Historial::class.java)
            startActivity(intent)
        }
        btnTuProgreso.setOnClickListener {
            val intent = Intent(this, TuProgreso::class.java)
            startActivity(intent)
        }

        btnCasa.setOnClickListener {
            Toast.makeText(this, "Ya se encuentra en el main", Toast.LENGTH_SHORT).show()
        }

        btnFavoritos.setOnClickListener {
            val intent = Intent(this, Favoritos::class.java)
            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, Usuario::class.java)
            startActivity(intent)
        }
    }
}