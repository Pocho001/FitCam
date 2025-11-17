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
import android.util.Log
import androidx.core.content.edit
import android.widget.Toast


class Usuario : ComponentActivity() {

    // Se crea una variable constante y estática TAG que tenga la clase Usuario que es la actual
    // Que se ocupa en el Logcat
    companion object {
        private const val TAG = "Usuario"
    }

    // Ignora la advertencia de Android studio para concatenar textos con variables
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos_usuario)

        // Textos
        val txtPesoActual = findViewById<TextView>(R.id.textPesoActual)
        val txtDatosDeUsuario = findViewById<TextView>(R.id.textDatosDeUsuario)
        val txtConfiguraTusMedidas = findViewById<TextView>(R.id.textConfiguraTusMedidas)
        val txtConsejoUsuario = findViewById<TextView>(R.id.textConsejoUsuario)
        val txtAlturaActual = findViewById<TextView>(R.id.textAlturaActual)

        // Campos que se pueden editar
        val editPeso = findViewById<EditText>(R.id.editPeso)
        val editAltura = findViewById<EditText>(R.id.editAltura)

        // Botones
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val btnAtras = findViewById<ImageButton>(R.id.btnAtras)
        val btnCasa = findViewById<ImageButton>(R.id.btnCasa)
        val btnFavoritos = findViewById<ImageButton>(R.id.btnFavoritos)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

        // Se toma del archivo .xml que se guarda en el dispositivo del usuario sus "DatosUsuario"
        // que son su peso y su altura
        val prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE)
        // Se guardan los String de los datos con sus respectivos "keys" (peso y altura)
        val pesoGuardado = prefs.getFloat("peso", 0f)
        val alturaGuardada = prefs.getFloat("altura", 0f)

        // Se ponen los Strings en los TextView de los que van a tener
        txtDatosDeUsuario.text = "Datos de usuario"
        txtConfiguraTusMedidas.text = "Configura tus medidas"
        txtConsejoUsuario.text = "Esto servirá para adoptar los ejercicios a ti"

        // Si el String que se recuperó del getSharePreference del peso y altura no son números flotantes
        // En la pantalla del usuario sale en el apartado del peso y altura como "No registrado"
        // Ejemplo de concatenar texto y variable
        txtPesoActual.text = "Peso actual: ${if (pesoGuardado != 0f) pesoGuardado else "No registrado"} kg"
        txtAlturaActual.text = "Altura actual: ${if (alturaGuardada != 0f) alturaGuardada else "No registrada"} m"

        // Momento al pulsar el boton para actualizar los datos
        btnActualizar.setOnClickListener {
            // Se pasan los valores agregados por el usuario de los EditText a Strings
            val pesoTexto = editPeso.text.toString()
            val alturaTexto = editAltura.text.toString()

            // Si alguno de los 2 campos lo dejó vacío manda un pequeño mensaje de error
            if (pesoTexto.isBlank() || alturaTexto.isBlank()) {
                Toast.makeText(this, "Por favor, ingresa todos los datos requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Se convierten los datos a Float y se pasan a nuevas variables
            // Pasando a null si no son Float
            val nuevoPeso = pesoTexto.toFloatOrNull()
            val nuevaAltura = alturaTexto.toFloatOrNull()

            // Si alguna de las 2 variables son null (no fuéron números válidos) manda el mensaje de error
            if (nuevoPeso == null || nuevaAltura == null) {
                Toast.makeText(this, "Los datos ingresados no son válidos.\nVerifica e inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si alguna de las variables no entra dentro de los "estandares" se manda el mensaje de error
            // 40kg <= Peso <= 100kg y 1m <= Altura <= 2.5m
            if (nuevoPeso !in 40.0..100.0 || nuevaAltura !in 1.0..2.5){
                Toast.makeText(this, "Los datos ingresados no son válidos.\nVerifica e inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guarda los nuevos datos de peso y altura
            // Si da algun tipo de error al intentar guardar lo atrapa y manda el mensaje de error
            try {
                // Se editan del archivo .xmllas variables con "key" peso y altura con los nuevos valores
                prefs.edit {
                    putFloat("peso", nuevoPeso)
                    putFloat("altura", nuevaAltura)
                }

                // Se mandan a escribir en pantalla
                txtPesoActual.text = "Peso Actual $nuevoPeso kg"
                txtAlturaActual.text = "Altura Actual $nuevaAltura m"
                // Se limpian los valores del EditText
                editPeso.text.clear()
                editAltura.text.clear()
                // Manda un pequeño mensaje de que los datos se guardaron correctamente
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()

            } catch (e: Exception){
                // Se manda el mensaje al LogCat para saber el tipo de error
                // (Sólo lo ve el desarrollador no el usuario)
                Log.d(TAG, "Error: $e")

                // Mensaje de error que vé el usuario
                Toast.makeText(
                    this,
                    "Ocurrió un error al guardar la información.\nIntenta nuevamente.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        // Boton de atras
        btnAtras.setOnClickListener {
            // Se finaliza la "pantalla" de "Usuario" (en donde está actualmente)
            finish()
        }

        // Boton "Home"
        btnCasa.setOnClickListener {
            // Se empieza la "actividad Main" (menú principal)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Boton Perfil de Usuario
        btnPerfil.setOnClickListener {
            // Se manda un mensaje explicando que ya se encuentra ahí
            Toast.makeText(this, "Ya se encuentra en los datos del usuario", Toast.LENGTH_SHORT).show()
        }

        // Boton Favoritos
        btnFavoritos.setOnClickListener {
            // Aún no se termina este apartado (mensaje de error)
            Toast.makeText(this, "Aún en construcción :c", Toast.LENGTH_SHORT).show()
            /*val intent = Intent(this, Favoritos::class.java)
            startActivity(intent)*/
        }
    }
}