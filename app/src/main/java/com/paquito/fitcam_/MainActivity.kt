package com.paquito.fitcam_

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

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
}
