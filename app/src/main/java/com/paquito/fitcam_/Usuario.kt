package com.paquito.fitcam_

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ImageButton

class Usuario : ComponentActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos_usuario)

        val txtPesoActual = findViewById<TextView>(R.id.textPesoActual)
        val txtDatosDeUsuario = findViewById<TextView>(id = R.id.textDatosDeUsuario)
        val txtConfiguraTusMedidas = findViewById<TextView>(id = R.id.textConfiguraTusMedidas)
        val txtConsejoUsuario = findViewById<TextView>( id = R.id.textConsejoUsuario)
        val txtAlturaActual = findViewById<TextView>(R.id.textAlturaActual)
        val editPeso = findViewById<EditText>(R.id.editPeso)
        val editAltura = findViewById<EditText>(R.id.editAltura)
        val btnAtras = findViewById<ImageButton>( id = R.id.btnAtras)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val btnCasa = findViewById<ImageButton>( id = R.id.btnCasa)

        val prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE)
        val pesoGuardado = prefs.getFloat("peso", 0f)
        val alturaGuardada = prefs.getFloat("altura", 0f)

        txtPesoActual.text = "Peso actual: ${if (pesoGuardado != 0f) pesoGuardado else "No registrado"} kg"
        txtAlturaActual.text = "Altura actual: ${if (alturaGuardada != 0f) alturaGuardada else "No registrada"} m"

        btnActualizar.setOnClickListener {
            val nuevoPeso = editPeso.text.toString().toFloatOrNull()
            val nuevaAltura = editAltura.text.toString().toFloatOrNull()

            if(nuevoPeso != null && nuevaAltura != null){
                val editor = prefs.edit()
                editor.putFloat("peso", nuevoPeso)
                editor.putFloat("altura", nuevaAltura)
                editor.apply()

                txtPesoActual.text = "Peso Actual $nuevoPeso kg"
                txtAlturaActual.text = "Altura Actual $nuevaAltura m"
                editPeso.text.clear()
                editAltura.text.clear()
            }
        }
    }
}