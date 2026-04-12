package com.example.telecomapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.telecomapp.ui.screens.DialerScreen
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
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute?.destination?.route == "home",
                                onClick = { navController.navigate("home") },
                                icon = {
                                    Icon(Icons.Default.Home, contentDescription = "Inicio")
                                },
                                label = { Text("Inicio") }
                            )
                            NavigationBarItem(
                                selected = currentRoute?.destination?.route == "dialer",
                                onClick = { navController.navigate("dialer") },
                                icon = {
                                    Icon(Icons.Default.Call, contentDescription = "Marcador")
                                },
                                label = { Text("Marcador") }
                            )
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("home") { HomeScreen() }
                        composable("dialer") { DialerScreen() }
                    }
                }
            }
        }
    }
}