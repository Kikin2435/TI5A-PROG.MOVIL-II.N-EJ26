package com.example.telecomapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.telecomapp.viewmodel.TelecomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: TelecomViewModel = viewModel()) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()

    val screeningLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { vm.refreshState(context) }

    val redirectionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { vm.refreshState(context) }

    LaunchedEffect(Unit) {
        vm.refreshState(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TelecomApp", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Estado del sistema Telecom",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            InfoCard(
                title = "Permiso CALL_PHONE",
                value = if (state.hasCallPhonePermission) "Concedido ✓" else "No concedido ✗",
                ok = state.hasCallPhonePermission
            )

            InfoCard(
                title = "Permiso READ_PHONE_STATE",
                value = if (state.hasReadPhoneStatePermission) "Concedido ✓" else "No concedido ✗",
                ok = state.hasReadPhoneStatePermission
            )

            InfoCard(
                title = "App de marcación predeterminada",
                value = if (state.isDefaultDialer) "Esta app ✓" else "Otra app",
                ok = state.isDefaultDialer
            )

            InfoCard(
                title = "Servicio de filtrado activo",
                value = if (state.isScreeningApp) "Activo ✓" else "No activo",
                ok = state.isScreeningApp
            )

            InfoCard(
                title = "Cuentas de teléfono",
                value = state.deviceInfo,
                ok = true
            )

            HorizontalDivider()

            Text(
                "Acciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    val intent = vm.requestScreeningRole(context)
                    if (intent != null) screeningLauncher.launch(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isScreeningApp
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (state.isScreeningApp) "Filtrado activo ✓"
                    else "Activar filtrado de llamadas"
                )
            }

            Button(
                onClick = {
                    val intent = vm.requestRedirectionRole(context)
                    if (intent != null) redirectionLauncher.launch(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Solicitar rol de redirección")
            }

            Button(
                onClick = { vm.refreshState(context) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Actualizar estado")
            }

            HorizontalDivider()

            Text(
                "Funcionalidades implementadas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("• Filtrado de llamadas con STIR/SHAKEN")
                    Text("• Redirección de llamadas salientes")
                    Text("• Detección de permisos del sistema")
                    Text("• Visualización de roles de Telecom")
                    Text("• Detección de cuentas de teléfono")
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, ok: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (ok)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium)
                Text(value, fontWeight = FontWeight.Bold)
            }
            Icon(
                if (ok) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (ok) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}