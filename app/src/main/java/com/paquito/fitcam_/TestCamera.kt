package com.paquito.fitcam_

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TestCamera : ComponentActivity(){

    private lateinit var viewFinder: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_camara)

        viewFinder = findViewById(R.id.viewFinder)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
        }

    }

    private fun iniciarCamara(){
        val cameraProviderFuture= ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider=cameraProviderFuture.get()

            val preview=androidx.camera.core.Preview.Builder().build()
            preview.setSurfaceProvider(viewFinder.surfaceProvider)

            val cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch(exc: Exception){
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }
}
