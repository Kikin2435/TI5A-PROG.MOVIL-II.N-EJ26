package com.example.telecomapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.telecomapp.ui.screens.HomeScreen
import com.example.telecomapp.ui.theme.TelecomAppTheme

class MainActivity : ComponentActivity() {

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions.launch(
            arrayOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG
            )
        )

        setContent {
            TelecomAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}