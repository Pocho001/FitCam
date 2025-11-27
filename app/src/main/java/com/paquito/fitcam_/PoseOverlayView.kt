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
    // Paint para los puntos
    private val pointPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 15f
    }

    // Paint para las lineas
    private val linePaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    // Paint para el texto
    private val textPaint = Paint().apply {
        color = Color.YELLOW
        textSize = 40f
        style = Paint.Style.FILL
    }

    // Arreglo de 17 puntos del cuerpo
    private var keypoints: FloatArray? = null
    // Cantidad de articulaciónes detectadas
    private var detectedCount: Int = 0

    // Conexiones del esqueleto (índices de los keypoints a conectar con lineas)
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

    // Actualiza los puntos detectados y redibuja la vista
    fun updateKeypoints(keypoints: FloatArray, detectedCount: Int) {
        this.keypoints = keypoints
        this.detectedCount = detectedCount
        invalidate() // Redibuja la vista
    }

    // Borra el overlay
    fun clear() {
        keypoints = null
        detectedCount = 0
        invalidate()
    }

    // Mostrar mensajes extra
    fun setExtraText(text: String){
        extraText = text
        invalidate()
    }

    // Se ejecuta automaticamente cuando Android necesita mostrar la vista
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Si no hay keypoints no dibuja nada
        val kp = keypoints ?: return

        // Se escalan al tamaño real de la pantalla
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

            // Si la "confianza" de los 2 puntos es alta se dibuja la linea
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

        // Dibujar el contador
        val margin = 20f
        textPaint.textAlign = Paint.Align.RIGHT

        val fm = textPaint.fontMetrics
        val yPos = margin - fm.top
        // Mostrar contador
        canvas.drawText(
            "Puntos: $detectedCount/17",
            width - margin,
            yPos,
            textPaint
        )

        // Dibujar texto extra
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