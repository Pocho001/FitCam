package com.paquito.fitcam_

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val boton = findViewById<Button>(R.id.btnIrDatosUsuario)
        boton.setOnClickListener {
            val intent = Intent(this, Usuario::class.java)
            startActivity(intent)
        }
    }
}
