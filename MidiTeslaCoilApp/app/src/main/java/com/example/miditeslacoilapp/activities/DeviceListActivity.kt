package com.example.miditeslacoilapp.activities

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.miditeslacoilapp.BluetoothApplication
import com.example.miditeslacoilapp.Extensions.isLocationPermissionGranted
import com.example.miditeslacoilapp.Extensions.requestLocationPermission
import com.example.miditeslacoilapp.R
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
            ScanResultAdapter { startActivity(this.let { it1 -> MainActivity.newInstance(it1, it.bleDevice.macAddress) }) }

    private var hasClickedScan = false

    private val isScanning: Boolean
        get() = scanDisposable != null

    companion object{
        private const val REQUEST_ENABLE_BT = 1
    }

    override fun onStart() {
        super.onStart()
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        else configureResultList(findViewById(R.id.scan_results))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                configureResultList(findViewById(R.id.scan_results))
            }
            else{
                Toast.makeText(this, "Bluetooth was not enabled", Toast.LENGTH_SHORT).show()
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_devices_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_search_devices){
            onScanToggleClick()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dispose() {
        scanDisposable = null
        resultsAdapter.clearScanResults()
    }

    override fun onPause() {
        super.onPause()
        if (isScanning) scanDisposable?.dispose()
    }
}