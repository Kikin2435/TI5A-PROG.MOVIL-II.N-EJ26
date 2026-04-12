package com.example.telecomapp.services

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection
import android.util.Log

class CallScreeningServiceImpl : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        Log.d("CallScreening", "Llamada recibida: ${callDetails.handle}")

        val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        val responseBuilder = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSilenceCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(false)

        if (isIncoming) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                when (callDetails.callerNumberVerificationStatus) {
                    Connection.VERIFICATION_STATUS_FAILED -> {
                        Log.d("CallScreening", "SPAM detectado - verificación falló")
                        responseBuilder
                            .setRejectCall(true)
                            .setDisallowCall(true)
                    }
                    Connection.VERIFICATION_STATUS_PASSED -> {
                        Log.d("CallScreening", "Llamada verificada - válida")
                    }
                    else -> {
                        Log.d("CallScreening", "No se pudo verificar el número")
                    }
                }
            } else {
                Log.d("CallScreening", "STIR/SHAKEN no disponible en esta versión de Android")
            }
        }

        respondToCall(callDetails, responseBuilder.build())
    }
}