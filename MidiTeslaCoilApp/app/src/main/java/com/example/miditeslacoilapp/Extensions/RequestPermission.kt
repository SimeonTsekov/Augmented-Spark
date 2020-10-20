package com.example.miditeslacoilapp.Extensions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.polidea.rxandroidble2.RxBleClient

private const val REQUEST_PERMISSION_BLE_SCAN = 101

private const val READ_STORAGE_RC = 123

internal fun Activity.requestLocationPermission(client: RxBleClient) =
        ActivityCompat.requestPermissions(
                this,
                arrayOf(client.recommendedScanRuntimePermissions[0]),
                REQUEST_PERMISSION_BLE_SCAN
        )

internal fun Fragment.requestStoragePermission() =
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_RC)

internal fun Fragment.isStoragePermissionGranted() =
        ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

internal fun isLocationPermissionGranted(requestCode: Int, grantResults: IntArray) =
        requestCode == REQUEST_PERMISSION_BLE_SCAN && grantResults[0] == PackageManager.PERMISSION_GRANTED