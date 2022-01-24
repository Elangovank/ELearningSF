package com.gmcoreui.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun isPermissionsGranted(activity: Activity, permissions: Array<String>?): Boolean {
        if (permissions == null || permissions.isEmpty()) {
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        var granted = isPermissionGranted(activity, permissions[0])
        if (permissions.size == 1) {
            return granted
        }
        for (i in 1 until permissions.size) {
            granted = granted && isPermissionGranted(activity, permissions[i])
        }
        return granted
    }

    private fun isPermissionGranted(activity: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isGranted(grantResults: IntArray?): Boolean {
        if (grantResults == null || grantResults.isEmpty()) {
            return false
        }
        var granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (grantResults.size == 1) {
            return granted
        }
        for (i in 1 until grantResults.size) {
            granted = granted && grantResults[i] == PackageManager.PERMISSION_GRANTED
        }
        return granted
    }
}
