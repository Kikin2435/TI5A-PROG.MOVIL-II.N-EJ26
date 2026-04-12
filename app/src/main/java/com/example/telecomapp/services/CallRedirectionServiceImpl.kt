package com.example.telecomapp.services

import android.net.Uri
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import android.util.Log

class CallRedirectionServiceImpl : CallRedirectionService() {

    override fun onPlaceCall(
        handle: Uri,
        initialPhoneAccount: PhoneAccountHandle,
        allowInteractiveResponse: Boolean
    ) {
        Log.d("CallRedirection", "Llamada saliente a: $handle")
        placeCallUnmodified()
    }
}