package com.paquito.fitcam_

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import android.graphics.Matrix
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.abs
import kotlin.math.atan2
import java.text.SimpleDateFormat
import java.util.*

class Camara : ComponentActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var poseOverlay: PoseOverlayView
    private var interpreter: Interpreter? = null
    private lateinit var imageProcessor: ImageProcessor
    private var tipoEjercicio: String? = null
    private var abdominalEstado = "inicio"
    private var abdominalesCompletas = 0
    private var estadoSentadilla = "arriba"
    private var sentadillasCompletas = 0
    private var estadoLagartija = "arriba"
    private var lagartijasCompletas = 0
    private var estadoDominada = "arriba"
    private var dominadasCompletas = 0
    private var estadoCurl = "arriba"
    private var curlsCompletas = 0




    companion object {
        private const val TAG = "TestCamera"
        private const val MODEL_WIDTH = 192
        private const val MODEL_HEIGHT = 192
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.camara)
        tipoEjercicio = intent.getStringExtra("ejercicio")
        Log.d(TAG, "Ejercicio seleccionado: $tipoEjercicio")

        window.decorView.systemUiVisibility =
            (android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        viewFinder = findViewById(R.id.viewFinder)
        poseOverlay = findViewById(R.id.poseOverlay)

        setupModel()

        // Solicitar permiso de cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
        }
    }

    private fun setupModel() {
        try {
            // Cargar el modelo desde assest
            val model = FileUtil.loadMappedFile(this, "movenet.tflite")
            val options = Interpreter.Options()
            options.setNumThreads(4)
            interpreter = Interpreter(model, options)

            // Simplificar el procesador - solo normalización
            imageProcessor = ImageProcessor.Builder()
                .add(NormalizeOp(0f, 255f))
                .build()

            Log.d(TAG, "✅ Modelo cargado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al cargar modelo: ${e.message}", e)
        }
    }

    private fun iniciarCamara() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Se crea el preview de lo que se va a ver
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(viewFinder.surfaceProvider)

            // El analyzer va analizando cada imagen que la camara captura
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        try {
                            // Se convierte la imagen a Bitmap
                            val bitmap = imageProxy.toBitmap()
                            detectPose(bitmap)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error en análisis: ${e.message}", e)
                        } finally {
                            imageProxy.close()
                        }
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
                Log.d(TAG, "📹 Cámara iniciada correctamente")
            } catch (exc: Exception) {
                Log.e(TAG, "Error al iniciar cámara: ${exc.message}", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun detectPose(bitmap: Bitmap) {
        interpreter?.let { interp ->
            try {

                val matrix = Matrix()
                // Se rota 90°
                matrix.postRotate(-90f)
                // Hacerle efecto "espejo"
                matrix.postScale(-1f, 1f)
                // Redimensionar la imagen a 192x192 para que lo reconozca TensorFlow
                val rotatedBitmap  = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                // Redimensionar el bitmap volteado antes de pasarlo al modelo
                val resizedBitmap   = Bitmap.createScaledBitmap(rotatedBitmap , MODEL_WIDTH, MODEL_HEIGHT, true)

                // Crear TensorImage
                val tensorImage = TensorImage(DataType.UINT8)
                tensorImage.load(resizedBitmap )

                // Preparar salida: MoveNet produce [1, 1, 17, 3]
                val outputShape = intArrayOf(1, 1, 17, 3)
                val outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

                // Ejecutar modelo
                interp.run(tensorImage.buffer, outputBuffer.buffer.rewind())

                // Procesar resultados
                val keypoints = outputBuffer.floatArray
                // Ahora keypoints contiene los 17 puntos detectados
                var detectedPoints = 0

                for (i in 0 until 17) {
                    val idx = i * 3
                    val confidence = keypoints[idx + 2]

                    if (confidence > 0.3f) {
                        detectedPoints++
                    }
                }

                when(tipoEjercicio){
                    "Sentadilla" -> detectarSentadilla(keypoints)
                    "Lagartija" -> detectarLagartija(keypoints)
                    "Abdominal" -> detectarAbdominal(keypoints)
                    "Dominadas" -> detectarDominadas(keypoints)
                    "Curl de Bicep" -> detectarCurlBiceps(keypoints)
                }

                // Actualizar el overlay en el hilo principal
                runOnUiThread {
                    poseOverlay.updateKeypoints(keypoints, detectedPoints)
                }

                if (detectedPoints > 0) {
                    Log.d(TAG, "🎯 Pose detectada: $detectedPoints/17 puntos")
                }

                // Liberar el bitmap redimensionado
                resizedBitmap.recycle()

            } catch (e: Exception) {
                Log.e(TAG, "Error en detección: ${e.message}", e)
                runOnUiThread {
                    poseOverlay.clear()
                }
            }
        }
    }

    //Para los ejercicios obtiene el angulo
    private fun calcularAngulo(a: Pair<Float, Float>, b: Pair<Float, Float>, c: Pair<Float, Float>): Double {
        val angulo = Math.toDegrees(
            (atan2(c.second - b.second, c.first - b.first) -
                    atan2(a.second - b.second, a.first - b.first)).toDouble()
        )

        // Ajustar el angulo para que siempre este entre 0 y 180
        var anguloAbs = abs(angulo)
        if (anguloAbs > 180) {
            anguloAbs = 360 - anguloAbs
        }
        return anguloAbs
    }

    private fun detectarSentadilla(keypoints: FloatArray){
        val cadera = getPoint(keypoints, 12)
        val rodilla = getPoint(keypoints, 14)
        val tobillo = getPoint(keypoints, 16)

        val scoreCadera = keypoints[12 * 3 + 2]
        val scoreRodilla = keypoints[14 * 3 + 2]
        val scoreTobillo = keypoints[16 * 3 + 2]

        // Si falta alguno, salir sin contar
        if (scoreCadera < 0.3f || scoreRodilla < 0.3f || scoreTobillo < 0.3f) {
            Log.d(TAG, "⚠️ No se detectan bien los puntos clave (cadera, rodilla, tobillo)")
            return
        }

        val anguloPierna = calcularAngulo(cadera, rodilla, tobillo)

        when (estadoSentadilla) {
            "arriba" -> {
                if (anguloPierna < 90) {
                    estadoSentadilla = "abajo"
                }
            }
            "abajo" -> {
                if (anguloPierna > 150) {
                    sentadillasCompletas++
                    estadoSentadilla = "arriba"
                    Log.d(TAG, "✅ Sentadilla completada: $sentadillasCompletas")
                    guardarProgreso("Sentadillas", sentadillasCompletas)
                }
            }
        }

        runOnUiThread {
            poseOverlay.setExtraText("Sentadillas: $sentadillasCompletas")
        }
    }

    private fun detectarLagartija(keypoints: FloatArray){
        val hombro = getPoint(keypoints, 6)
        val codo = getPoint(keypoints, 8)
        val muneca = getPoint(keypoints, 10)

        val scoreHombro = keypoints[6 * 3 + 2]
        val scoreCodo = keypoints[8 * 3 + 2]
        val scoremuneca = keypoints[10 * 3 + 2]

        // Si falta alguno, salir sin contar
        if (scoreHombro < 0.3f || scoreCodo < 0.3f || scoremuneca < 0.3f) {
            Log.d(TAG, "⚠️ No se detectan bien los puntos clave (hombro, codo, muñeca)")
            return
        }

        val anguloBrazo = calcularAngulo(hombro, codo, muneca)

        when(estadoLagartija){
            "arriba" ->{
                if (anguloBrazo<90)
                    estadoLagartija = "abajo"
            }
            "abajo" ->{
                if (anguloBrazo>150){
                    lagartijasCompletas++
                    estadoLagartija = "arriba"
                    Log.d(TAG, "✅ Lagartija completada: $lagartijasCompletas")
                    guardarProgreso("Lagartija", lagartijasCompletas)
                }
            }
        }

        runOnUiThread {
            poseOverlay.setExtraText("Lagartijas: $lagartijasCompletas")
        }
    }

    private fun detectarAbdominal(keypoints: FloatArray) {
        val hombro = getPoint(keypoints, 6)
        val cadera = getPoint(keypoints, 12)
        val rodilla = getPoint(keypoints, 14)

        val scoreHombro = keypoints[6 * 3 + 2]
        val scoreCadera = keypoints[12 * 3 + 2]
        val scoreRodilla = keypoints[14 * 3 + 2]

        // Si falta alguno, salir sin contar
        if (scoreHombro < 0.3f || scoreCadera < 0.3f || scoreRodilla < 0.3f) {
            Log.d(TAG, "⚠️ No se detectan bien los puntos clave (hombro, cadera, rodilla)")
            return
        }

        val anguloTronco = calcularAngulo(hombro, cadera, rodilla)
        Log.d(TAG, "\uD83E\uDD38\u200D♂\uFE0F Angulo tronco: $anguloTronco")

        when (abdominalEstado) {
            "inicio" -> {
                if (anguloTronco < 100) {
                    abdominalEstado = "subiendo"
                    Log.d(TAG, "⬆️ Subiendo")
                }
            }
            "subiendo" -> {
                if (anguloTronco > 150) {
                    abdominalesCompletas++
                    abdominalEstado = "inicio"
                    Log.d(TAG, "✅ Abdominal completada: $abdominalesCompletas")
                    guardarProgreso("Abdominal", abdominalesCompletas)
                }
            }
        }

        runOnUiThread {
            poseOverlay.setExtraText("Abdominales: $abdominalesCompletas")
        }
    }

    private fun detectarDominadas(keypoints: FloatArray) {
        // Usamos hombro(6), codo(8), muñeca(10) (lado derecho)
        val hombro = getPoint(keypoints, 6)
        val codo = getPoint(keypoints, 8)
        val muneca = getPoint(keypoints, 10)

        val scoreHombro = keypoints[6 * 3 + 2]
        val scoreCodo = keypoints[8 * 3 + 2]
        val scoreMuneca = keypoints[10 * 3 + 2]

        // Si falta alguno, salir sin contar
        if (scoreHombro < 0.3f || scoreCodo < 0.3f || scoreMuneca < 0.3f) {
            Log.d(TAG, "⚠️ No se detectan bien los puntos clave (hombro, codo, muñeca) para dominadas")
            return
        }

        // Ángulo en el codo (hombro - codo - muñeca)
        val anguloCodo = calcularAngulo(hombro, codo, muneca)
        Log.d(TAG, "🔎 Dominadas - ángulo codo: $anguloCodo")

        when (estadoDominada) {
            // posición inicial considerada 'arriba' (brazos extendidos en reposo)
            "arriba" -> {
                // Si el usuario sube (flexiona el codo) lo marcamos como 'abajo' (parte alta del movimiento)
                if (anguloCodo < 90) {          // umbral para detectar fase superior
                    estadoDominada = "abajo"
                }
            }
            "abajo" -> {
                // Cuando vuelve a la posición extendida (ángulo grande) contamos la repetición
                if (anguloCodo > 150) {
                    dominadasCompletas++
                    estadoDominada = "arriba"
                    Log.d(TAG, "✅ Dominada completada: $dominadasCompletas")
                    guardarProgreso("Dominadas", dominadasCompletas)
                }
            }
        }

        runOnUiThread {
            poseOverlay.setExtraText("Dominadas: $dominadasCompletas")
        }
    }

    private fun detectarCurlBiceps(keypoints: FloatArray) {
        // Usamos hombro(6), codo(8), muñeca(10) (lado derecho) — mide la flexión del codo
        val hombro = getPoint(keypoints, 6)
        val codo = getPoint(keypoints, 8)
        val muneca = getPoint(keypoints, 10)

        val scoreHombro = keypoints[6 * 3 + 2]
        val scoreCodo = keypoints[8 * 3 + 2]
        val scoreMuneca = keypoints[10 * 3 + 2]

        // Si falta alguno, salir sin contar
        if (scoreHombro < 0.3f || scoreCodo < 0.3f || scoreMuneca < 0.3f) {
            Log.d(TAG, "⚠️ No se detectan bien los puntos clave (hombro, codo, muñeca) para curl")
            return
        }

        // Ángulo en el codo (hombro - codo - muñeca)
        val anguloCodo = calcularAngulo(hombro, codo, muneca)
        Log.d(TAG, "🔎 Curl - ángulo codo: $anguloCodo")

        when (estadoCurl) {
            // tratamos 'arriba' como posición extendida inicial
            "arriba" -> {
                // cuando se flexiona lo consideramos 'abajo' (fase contracción)
                if (anguloCodo < 60) {  // umbral más pequeño para curl (contracción)
                    estadoCurl = "abajo"
                }
            }
            "abajo" -> {
                // cuando vuelve a posición extendida contamos la repetición
                if (anguloCodo > 150) {
                    curlsCompletas++
                    estadoCurl = "arriba"
                    Log.d(TAG, "✅ Curl completado: $curlsCompletas")
                    guardarProgreso("Curl Bíceps", curlsCompletas)
                }
            }
        }

        runOnUiThread {
            poseOverlay.setExtraText("Curls: $curlsCompletas")
        }
    }


    private fun getPoint(keypoints: FloatArray, index: Int): Pair<Float, Float>{
        val x = keypoints[index * 3]
        val y = keypoints[index * 3+1]
        return Pair(x, y)
    }

    private fun guardarProgreso(nombreEjercicio: String, repeticiones: Int){
        val prefs = getSharedPreferences("ProgresoEjercicios", MODE_PRIVATE)
        val editor = prefs.edit()

        val fecha = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())

        val clave = "${fecha}_$nombreEjercicio"

        editor.putInt(clave, repeticiones)
        editor.apply()
        Log.d(TAG, "\uD83D\uDCBE Guardado: $nombreEjercicio -> $repeticiones el $fecha")
    }

    /*private fun checarPersona(keypoints: FloatArray) {

    }*/

    override fun onDestroy() {
        super.onDestroy()
        interpreter?.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        }
    }
}