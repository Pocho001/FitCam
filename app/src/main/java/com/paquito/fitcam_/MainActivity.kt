package com.paquito.fitcam_


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.widget.TextView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val consejos = listOf(
            "Toma agua antes de cada ejercicio 💧",
            "Haz estiramientos 5 minutos al despertar 🧘‍♂️",
            "No te saltes el desayuno 🍳",
            "Camina al menos 30 minutos al dia 🚶",
            "Duerme bien, tu cuerpo lo necesita 😴",
            "No te compares, avanza a tu ritmo 💪",
            "Pequenos pasos diarios hacen grandes cambios 🌱",
            "Respira profundo cuando te sientas estresado 😮‍💨",
            "Tu cuerpo puede mas de lo que piensas 🔥",
            "Haz un calentamiento ligero antes de entrenar 🏃‍♂️",
            "No entrenes sin comer algo ligero antes 🍌",
            "Un mal dia no arruina tu progreso ✨",
            "Haz actividad fisica que realmente disfrutes 😄",
            "Tu salud vale el esfuerzo 💚",
            "No te rindas, vas mejorando 👊",
            "Un descanso tambien es parte del progreso 🛌",
            "Se constante, no perfecto ✔️",
            "Haz ejercicio escuchando tu musica favorita 🎧",
            "Come mas frutas y verduras cada dia 🍎🥦",
            "Evita el exceso de azucar hoy 🍬❌",
            "Sonrie, es parte de sentirte mejor 😁",
            "Mantente en movimiento, aunque sea poquito 🕺",
            "No olvides estirar despues de entrenar 🤸‍♂️",
            "Hidrata tu piel despues de sudar 🧴",
            "Cuidar tu cuerpo tambien es amor propio ❤️",
            "Evita el celular 30 minutos antes de dormir 📵",
            "Mejora tu postura cuando estes sentado 🪑",
            "Descansa si te duele demasiado, escuchate 🤕",
            "Tu version del futuro te agradecera este esfuerzo 🏆",
            "Hoy puede ser tu mejor entrenamiento ✨",
            "Come mas despacio para mejorar tu digestion 🍽️",
            "Haz pausas activas si pasas mucho tiempo sentado 🔄",
            "Salir a caminar despeja la mente 🌤️",
            "No te castigues, aprende y sigue adelante 🌟"
        )

        val txtConsejos = findViewById<TextView>(R.id.txtConsejos)
        val txtQueVamosAHacerHoy = findViewById<TextView>(R.id.txtQueVamosAHacerHoy)
        val txtPierna = findViewById<TextView>(R.id.txtPierna)
        val txtBrazo = findViewById<TextView>(R.id.txtBrazo)
        val txtEspalda = findViewById<TextView>(R.id.txtEspalda)
        val txtDorso = findViewById<TextView>(R.id.txtDorso)
        val txtObservaTuProgreso = findViewById<TextView>(R.id.txtObservaTuProgreso)
        val txtCalendario = findViewById<TextView>(R.id.txtMostrarCalendario)
        val txtTuProgreso = findViewById<TextView>(R.id.txtTuProgreso)
        val txtPruebaLaCamara = findViewById<TextView>(R.id.textPruebaLaCamara)

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
        txtPruebaLaCamara.text = "Prueba de la cámara"
        txtQueVamosAHacerHoy.text = "¿Qué vámos a hacer hoy?"
        txtPierna.text = "Pierna"
        txtBrazo.text = "Brazo"
        txtEspalda.text = "Espalda"
        txtDorso.text = "Dorso"
        txtObservaTuProgreso.text = "Observa tu progreso"
        txtCalendario.text = "Calendario"
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
            Toast.makeText(this, "Ya se encuentra en el main", Toast.LENGTH_SHORT).show()
        }
        btnEspalda.setOnClickListener {
            Toast.makeText(this, "Ya se encuentra en el main", Toast.LENGTH_SHORT).show()
        }
        btnDorso.setOnClickListener {
            val intent = Intent(this, SeleccionEjercicioDorso::class.java)
            startActivity(intent)
        }
        btnHistorial.setOnClickListener {
            val intent = Intent(this, Calendario::class.java)
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
            val intent = Intent(this, DatosDeUsuario::class.java)
            startActivity(intent)
        }
    }
}