package com.example.miditeslacoilapp.Utils

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.polidea.rxandroidble2.RxBleClient

private const val REQUEST_PERMISSION_BLE_SCAN = 101

// Epic extension functions here
internal fun Fragment.requestLocationPermission(client: RxBleClient) =
        ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(client.recommendedScanRuntimePermissions[0]),
                REQUEST_PERMISSION_BLE_SCAN
        )

internal fun isLocationPermissionGranted(requestCode: Int, grantResults: IntArray) =
        requestCode == REQUEST_PERMISSION_BLE_SCAN && grantResults[0] == PackageManager.PERMISSION_GRANTED