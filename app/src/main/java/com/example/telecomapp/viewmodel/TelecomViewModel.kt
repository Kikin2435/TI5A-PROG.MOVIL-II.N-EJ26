package com.example.telecomapp.viewmodel

import android.app.Application
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TelecomState(
    val hasCallPhonePermission: Boolean = false,
    val hasReadPhoneStatePermission: Boolean = false,
    val isDefaultDialer: Boolean = false,
    val isScreeningApp: Boolean = false,
    val deviceInfo: String = ""
)

class TelecomViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(TelecomState())
    val state: StateFlow<TelecomState> = _state

    private val telecomManager =
        application.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    fun refreshState(context: Context) {
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager

        _state.value = TelecomState(
            hasCallPhonePermission = hasPermission(
                context, android.Manifest.permission.CALL_PHONE
            ),
            hasReadPhoneStatePermission = hasPermission(
                context, android.Manifest.permission.READ_PHONE_STATE
            ),
            isDefaultDialer = roleManager.isRoleHeld(RoleManager.ROLE_DIALER),
            isScreeningApp = roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING),
            deviceInfo = getDeviceInfo()
        )
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun getDeviceInfo(): String {
        return try {
            val phoneAccounts = telecomManager.callCapablePhoneAccounts
            "Cuentas de teléfono registradas: ${phoneAccounts.size}"
        } catch (e: SecurityException) {
            "Sin acceso a cuentas de teléfono"
        }
    }

    fun requestScreeningRole(context: Context): Intent? {
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
        return if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        ) {
            roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        } else null
    }

    fun requestRedirectionRole(context: Context): Intent? {
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
        return if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_REDIRECTION) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_CALL_REDIRECTION)
        ) {
            roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_REDIRECTION)
        } else null
    }
}