package com.example.futbolito

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun GameScreen(sensorX: Float, sensorY: Float) {

    var ballX by remember { mutableStateOf(500f) }
    var ballY by remember { mutableStateOf(900f) }

    var velX by remember { mutableStateOf(0f) }
    var velY by remember { mutableStateOf(0f) }

    var scoreA by remember { mutableStateOf(0) }
    var scoreB by remember { mutableStateOf(0) }

    var goalText by remember { mutableStateOf("") }
    var ballVisible by remember { mutableStateOf(true) }

    val radius = 55f

    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    Box(Modifier.fillMaxSize()) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val width = size.width
            val height = size.height

            canvasWidth = width
            canvasHeight = height

            val goalWidth = width * 0.35f
            val goalDepth = 80f
            val goalDepth3D = 60f

            val goalLeft = width / 2 - goalWidth / 2
            val goalRight = width / 2 + goalWidth / 2

            val postRadius = 20f
            
            // MOVIMIENTO DEL BALÓN
            if (ballVisible) {

                val acceleration = 0.25f
                velX += -sensorX * acceleration
                velY += sensorY * acceleration

                val maxSpeed = 60f
                velX = velX.coerceIn(-maxSpeed, maxSpeed)
                velY = velY.coerceIn(-maxSpeed, maxSpeed)

                velX *= 0.96f
                velY *= 0.96f

                ballX += velX
                ballY += velY
            }

            // REBOTES
            if (ballX < radius) { ballX = radius; velX *= -0.85f }
            if (ballX > width - radius) { ballX = width - radius; velX *= -0.85f }
            if (ballY < radius) { ballY = radius; velY *= -0.85f }
            if (ballY > height - radius) { ballY = height - radius; velY *= -0.85f }


            // REBOTE EN POSTES
            fun checkPost(px: Float, py: Float) {
                val dx = ballX - px
                val dy = ballY - py
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < radius + postRadius) {
                    val nx = dx / dist
                    val ny = dy / dist
                    velX = nx * abs(velX)
                    velY = ny * abs(velY)
                }
            }

            checkPost(goalLeft, goalDepth)
            checkPost(goalRight, goalDepth)
            checkPost(goalLeft, height - goalDepth)
            checkPost(goalRight, height - goalDepth)


            // DETECCIÓN DE GOL
            if (ballVisible && ballY < goalDepth && ballX > goalLeft && ballX < goalRight) {
                scoreB++
                goalText = "GOOOL"
                ballVisible = false
            }

            if (ballVisible && ballY > height - goalDepth && ballX > goalLeft && ballX < goalRight) {
                scoreA++
                goalText = "GOOOL"
                ballVisible = false
            }


            // CÉSPED
            for (i in 0..8) {
                drawRect(
                    color = if (i % 2 == 0) Color(0xFF2E7D32) else Color(0xFF388E3C),
                    topLeft = Offset(0f, i * (height / 9)),
                    size = Size(width, height / 9)
                )
            }


            // LÍNEAS DE CANCHA
            drawLine(Color.White, Offset(0f, height / 2), Offset(width, height / 2), 6f)

            drawCircle(
                Color.White,
                120f,
                Offset(width / 2, height / 2),
                style = Stroke(6f)
            )

            drawRect(
                Color.White,
                topLeft = Offset(width / 2 - 260f, 0f),
                size = Size(520f, 260f),
                style = Stroke(6f)
            )

            drawRect(
                Color.White,
                topLeft = Offset(width / 2 - 260f, height - 260f),
                size = Size(520f, 260f),
                style = Stroke(6f)
            )


            // PORTERÍA ARRIBA
            drawRect(Color.White, Offset(goalLeft - 10f, 0f), Size(20f, goalDepth))
            drawRect(Color.White, Offset(goalRight - 10f, 0f), Size(20f, goalDepth))
            drawRect(Color.White, Offset(goalLeft - 10f, 0f), Size(goalWidth + 20f, 20f))

            drawRect(Color.LightGray, Offset(goalLeft, goalDepth), Size(goalWidth, goalDepth3D))

            for (i in 0..12) {
                val x = goalLeft + i * (goalWidth / 12)
                drawLine(
                    Color.White.copy(alpha = 0.6f),
                    Offset(x, 0f),
                    Offset(x, goalDepth + goalDepth3D),
                    2f
                )
            }

            // líneas horizontales de la red
            for (i in 0..8) {
                val y = i * ((goalDepth + goalDepth3D) / 8)
                drawLine(
                    Color.White.copy(alpha = 0.6f),
                    Offset(goalLeft, y),
                    Offset(goalRight, y),
                    2f
                )
            }

            // PORTERÍA ABAJO
            drawRect(Color.White, Offset(goalLeft - 10f, height - goalDepth), Size(20f, goalDepth))
            drawRect(Color.White, Offset(goalRight - 10f, height - goalDepth), Size(20f, goalDepth))
            drawRect(Color.White, Offset(goalLeft - 10f, height - goalDepth), Size(goalWidth + 20f, 20f))

            drawRect(
                Color.LightGray,
                Offset(goalLeft, height - goalDepth - goalDepth3D),
                Size(goalWidth, goalDepth3D)
            )

            // líneas verticales de la red
            for (i in 0..12) {
                val x = goalLeft + i * (goalWidth / 12)
                drawLine(
                    Color.White.copy(alpha = 0.6f),
                    Offset(x, height - goalDepth - goalDepth3D),
                    Offset(x, height),
                    2f
                )
            }

            // líneas horizontales de la red
            for (i in 0..8) {
                val y = height - goalDepth - goalDepth3D + i * ((goalDepth + goalDepth3D) / 8)
                drawLine(
                    Color.White.copy(alpha = 0.6f),
                    Offset(goalLeft, y),
                    Offset(goalRight, y),
                    2f
                )
            }

            // MARCADOR
            val scoreText = "$scoreA  -  $scoreB"

            drawIntoCanvas { canvas ->

                val scorePaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 110f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }

                canvas.nativeCanvas.drawText(
                    scoreText,
                    width / 2f,
                    height / 2f + 36f,
                    scorePaint
                )
            }

            // TEXTO GOOOL
            if (goalText.isNotEmpty()) {

                drawIntoCanvas { canvas ->

                    val goalPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 200f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        isAntiAlias = true
                    }

                    canvas.nativeCanvas.drawText(
                        "GOOOL!",
                        width / 2f,
                        height / 2f - 80f,
                        goalPaint
                    )
                }
            }


            // BALÓN
            if (ballVisible) {

                drawCircle(
                    Color.Black.copy(alpha = 0.25f),
                    radius + 10,
                    Offset(ballX + 12, ballY + 12)
                )

                drawCircle(Color.White, radius, Offset(ballX, ballY))
                drawCircle(Color.Black, radius / 3, Offset(ballX, ballY))

                val panel = radius * 0.6f
                drawCircle(Color.Black, radius / 5, Offset(ballX + panel, ballY))
                drawCircle(Color.Black, radius / 5, Offset(ballX - panel, ballY))
                drawCircle(Color.Black, radius / 5, Offset(ballX, ballY + panel))
                drawCircle(Color.Black, radius / 5, Offset(ballX, ballY - panel))
            }
        }


        // DESAPARECER TEXTO DE GOL
        if (goalText.isNotEmpty()) {
            LaunchedEffect(goalText) {
                delay(2000)
                goalText = ""
            }
        }


        // REAPARECER BALÓN
        LaunchedEffect(ballVisible) {

            if (!ballVisible) {

                delay(2000)

                ballX = canvasWidth / 2
                ballY = canvasHeight / 2

                velX = 0f
                velY = 0f

                ballVisible = true
            }
        }
    }


    // LOOP DEL JUEGO
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
        }
    }
}