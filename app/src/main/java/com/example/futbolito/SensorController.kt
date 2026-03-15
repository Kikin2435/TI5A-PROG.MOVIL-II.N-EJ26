package com.example.futbolito

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorController(
    context: Context,
    val onMove: (Float, Float) -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun start() {

        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    fun stop() {

        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val x = event?.values?.get(0) ?: 0f
        val y = event?.values?.get(1) ?: 0f

        onMove(x, y)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

