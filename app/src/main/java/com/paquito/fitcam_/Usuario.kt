package com.paquito.fitcam_

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ImageButton
import android.content.Intent
import androidx.core.content.edit
import android.widget.Toast
class Usuario : ComponentActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos_usuario)

        val txtPesoActual = findViewById<TextView>(R.id.textPesoActual)
        val txtDatosDeUsuario = findViewById<TextView>(R.id.textDatosDeUsuario)
        val txtConfiguraTusMedidas = findViewById<TextView>(R.id.textConfiguraTusMedidas)
        val txtConsejoUsuario = findViewById<TextView>(R.id.textConsejoUsuario)
        val txtAlturaActual = findViewById<TextView>(R.id.textAlturaActual)

        val editPeso = findViewById<EditText>(R.id.editPeso)
        val editAltura = findViewById<EditText>(R.id.editAltura)

        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

        val prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE)
        val pesoGuardado = prefs.getFloat("peso", 0f)
        val alturaGuardada = prefs.getFloat("altura", 0f)

        txtDatosDeUsuario.text = "Datos de usuario"
        txtConfiguraTusMedidas.text = "Configura tus medidas"
        txtConsejoUsuario.text = "Esto servirá para adoptar los ejercicios a ti"

        txtPesoActual.text = "Peso actual: ${if (pesoGuardado != 0f) pesoGuardado else "No registrado"} kg"
        txtAlturaActual.text = "Altura actual: ${if (alturaGuardada != 0f) alturaGuardada else "No registrada"} m"

        btnActualizar.setOnClickListener {
            val pesoTexto = editPeso.text.toString()
            val alturaTexto = editAltura.text.toString()

            if (pesoTexto.isBlank() || alturaTexto.isBlank()) {
                Toast.makeText(this, "Por favor, ingresa todos los datos requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoPeso = pesoTexto.toFloatOrNull()
            val nuevaAltura = alturaTexto.toFloatOrNull()

            if (nuevoPeso == null || nuevaAltura == null) {
                Toast.makeText(this, "Los datos ingresados no son válidos.\nVerifica e inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nuevoPeso !in 40.0..100.0 || nuevaAltura !in 1.0..2.5){
                Toast.makeText(this, "Los datos ingresados no son válidos.\nVerifica e inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit {
                putFloat("peso", nuevoPeso)
                putFloat("altura", nuevaAltura)
            }

            txtPesoActual.text = "Peso Actual $nuevoPeso kg"
            txtAlturaActual.text = "Altura Actual $nuevaAltura m"
            editPeso.text.clear()
            editAltura.text.clear()
            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
        }

        btnAtras.setOnClickListener {
            finish()
        }

        btnCasa.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            Toast.makeText(this, "Ya se encuentra en los datos del usuario", Toast.LENGTH_SHORT).show()
        }

        btnFavoritos.setOnClickListener {
            Toast.makeText(this, "Aún en construcción :c", Toast.LENGTH_SHORT).show()
            /*val intent = Intent(this, Favoritos::class.java)
            startActivity(intent)*/
        }
    }
}