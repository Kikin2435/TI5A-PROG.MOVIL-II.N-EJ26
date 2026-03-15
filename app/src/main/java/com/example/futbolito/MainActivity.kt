package com.example.futbolito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var sensorX by remember { mutableStateOf(0f) }
            var sensorY by remember { mutableStateOf(0f) }

            val sensorController = remember {
                SensorController(this) { x, y ->
                    sensorX = x
                    sensorY = y
                }
            }

            DisposableEffect(Unit) {

                sensorController.start()

                onDispose {
                    sensorController.stop()
                }
            }

            GameScreen(sensorX, sensorY)
        }
    }
}