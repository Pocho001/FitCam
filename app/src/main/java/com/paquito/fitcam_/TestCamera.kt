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
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import com.paquito.fitcam_.utils.toBitmap

class TestCamera : ComponentActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var poseOverlay: PoseOverlayView
    private var interpreter: Interpreter? = null
    private lateinit var imageProcessor: ImageProcessor

    companion object {
        private const val TAG = "TestCamera"
        private const val MODEL_WIDTH = 192
        private const val MODEL_HEIGHT = 192
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.test_camara)

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
            // Cargar el modelo
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

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(viewFinder.surfaceProvider)

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        try {
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
                matrix.postRotate(-90f)
                matrix.postScale(-1f, 1f)
                val rotatedBitmap  = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                // Redimensionar el bitmap volteado antes de pasarlo al modelo
                val resizedBitmap   = Bitmap.createScaledBitmap(rotatedBitmap , MODEL_WIDTH, MODEL_HEIGHT, true)

                // Crear TensorImage
                val tensorImage = TensorImage(DataType.UINT8)
                tensorImage.load(resizedBitmap )

                // Preparar salida: MoveNet produce [1, 1, 17, 3]
                val outputShape = intArrayOf(1, 1, 17, 3)
                val outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

                // Ejecutar inferencia
                interp.run(tensorImage.buffer, outputBuffer.buffer.rewind())

                // Procesar resultados
                val keypoints = outputBuffer.floatArray
                var detectedPoints = 0

                for (i in 0 until 17) {
                    val idx = i * 3
                    val confidence = keypoints[idx + 2]

                    if (confidence > 0.3f) {
                        detectedPoints++
                    }
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