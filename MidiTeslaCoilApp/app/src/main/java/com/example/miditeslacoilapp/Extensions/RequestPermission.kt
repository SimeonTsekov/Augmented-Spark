package com.example.miditeslacoilapp.Utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.polidea.rxandroidble2.RxBleClient

private const val REQUEST_PERMISSION_BLE_SCAN = 101

internal fun Activity.requestLocationPermission(client: RxBleClient) =
        ActivityCompat.requestPermissions(
                this,
                arrayOf(client.recommendedScanRuntimePermissions[0]),
                REQUEST_PERMISSION_BLE_SCAN
        )

internal fun isLocationPermissionGranted(requestCode: Int, grantResults: IntArray) =
        requestCode == REQUEST_PERMISSION_BLE_SCAN && grantResults[0] == PackageManager.PERMISSION_GRANTED