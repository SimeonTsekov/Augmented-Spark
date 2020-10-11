package com.example.miditeslacoilapp.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.miditeslacoilapp.BluetoothApplication
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.Utils.isLocationPermissionGranted
import com.example.miditeslacoilapp.Utils.requestLocationPermission
import com.example.miditeslacoilapp.ui.adapters.ScanResultAdapter
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class DeviceListActivity : AppCompatActivity() {
    private val rxBleClient: RxBleClient = BluetoothApplication.rxBleClient

    private var scanDisposable: Disposable? = null

    private val resultsAdapter =
            ScanResultAdapter { startActivity(this.let { it1 -> MainActivity.newInstance(it1, it.bleDevice.macAddress) }) } // make  newInsance of main activity.

    private var hasClickedScan = false

    private val isScanning: Boolean
        get() = scanDisposable != null

    private lateinit var scanButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        scanButton = findViewById(R.id.scan_toggle_btn)
        configureResultList(findViewById(R.id.scan_results))
        scanButton.setOnClickListener { onScanToggleClick() }
    }

    private fun configureResultList(view: RecyclerView) = with(view) {
        setHasFixedSize(true)
        itemAnimator = null
        adapter = resultsAdapter
    }

    private fun onScanToggleClick() {
        if (isScanning) {
            scanDisposable?.dispose()
        } else {
            if (rxBleClient.isScanRuntimePermissionGranted) {
                scanBleDevices()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally { dispose() }
                        .subscribe { resultsAdapter.addScanResult(it) }
                        .let { scanDisposable = it }
            } else {
                hasClickedScan = true
                requestLocationPermission(rxBleClient)
            }
        }
        updateButtonUIState()
    }

    private fun scanBleDevices(): Observable<ScanResult> {
        val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build()

        val scanFilter = ScanFilter.Builder()
                .build()

        return rxBleClient.scanBleDevices(scanSettings, scanFilter)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isLocationPermissionGranted(requestCode, grantResults) && hasClickedScan) {
            hasClickedScan = false
            scanBleDevices()
        }
    }

    private fun dispose() {
        scanDisposable = null
        resultsAdapter.clearScanResults()
        updateButtonUIState()
    }

    private fun updateButtonUIState() =
            scanButton.setText(if (isScanning) R.string.button_stop_scan else R.string.button_start_scan)

    override fun onPause() {
        super.onPause()
        if (isScanning) scanDisposable?.dispose()
    }
}