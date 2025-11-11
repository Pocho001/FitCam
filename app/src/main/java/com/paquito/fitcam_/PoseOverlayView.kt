package com.paquito.fitcam_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PoseOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val pointPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 15f
    }

    private val linePaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val textPaint = Paint().apply {
        color = Color.YELLOW
        textSize = 40f
        style = Paint.Style.FILL
    }

    private var keypoints: FloatArray? = null
    private var detectedCount: Int = 0

    // Conexiones del esqueleto (índices de los keypoints a conectar)
    private val connections = listOf(
        // Cara
        Pair(0, 1), Pair(0, 2), Pair(1, 3), Pair(2, 4),
        // Torso
        Pair(5, 6), Pair(5, 11), Pair(6, 12), Pair(11, 12),
        // Brazo izquierdo
        Pair(5, 7), Pair(7, 9),
        // Brazo derecho
        Pair(6, 8), Pair(8, 10),
        // Pierna izquierda
        Pair(11, 13), Pair(13, 15),
        // Pierna derecha
        Pair(12, 14), Pair(14, 16)
    )

    private var extraText: String = ""

    fun updateKeypoints(keypoints: FloatArray, detectedCount: Int) {
        this.keypoints = keypoints
        this.detectedCount = detectedCount
        invalidate() // Redibuja la vista
    }

    fun clear() {
        keypoints = null
        detectedCount = 0
        invalidate()
    }

    fun setExtraText(text: String){
        extraText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val kp = keypoints ?: return

        val scaleX = width.toFloat()
        val scaleY = height.toFloat()

        // Dibujar líneas (conexiones del esqueleto)
        for ((start, end) in connections) {
            val startIdx = start * 3
            val endIdx = end * 3

            val startY = kp[startIdx]
            val startX = kp[startIdx + 1]
            val startConf = kp[startIdx + 2]

            val endY = kp[endIdx]
            val endX = kp[endIdx + 1]
            val endConf = kp[endIdx + 2]

            if (startConf > 0.3f && endConf > 0.3f) {
                canvas.drawLine(
                    startX * scaleX,
                    startY * scaleY,
                    endX * scaleX,
                    endY * scaleY,
                    linePaint
                )
            }
        }

        // Dibujar puntos (keypoints)
        for (i in 0 until 17) {
            val idx = i * 3
            val y = kp[idx]
            val x = kp[idx + 1]
            val confidence = kp[idx + 2]

            if (confidence > 0.3f) {
                canvas.drawCircle(
                    x * scaleX,
                    y * scaleY,
                    12f,
                    pointPaint
                )
            }
        }

        // Mostrar contador
        canvas.drawText(
            "Puntos: $detectedCount/17",
            20f,
            60f,
            textPaint
        )

        if (extraText.isNotEmpty()) {
            val textPaint = Paint().apply {
                color = Color.YELLOW
                textSize = 36f
                style = Paint.Style.FILL
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }
            canvas.drawText(extraText, 40f, 80f, textPaint)
        }
    }
}