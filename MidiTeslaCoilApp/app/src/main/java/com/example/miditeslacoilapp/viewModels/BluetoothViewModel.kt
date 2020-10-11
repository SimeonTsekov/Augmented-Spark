package com.example.miditeslacoilapp.ViewModel

import android.net.MacAddress
import androidx.lifecycle.ViewModel
import com.example.miditeslacoilapp.BluetoothApplication
import com.example.miditeslacoilapp.activities.CHARACTERISTIC_UUID
import com.example.miditeslacoilapp.activities.EXTRA_MAC_ADDRESS
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*

private const val EXTRA_MAC_ADDRESS = "extra_mac_address"

private const val CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb"

class BluetoothViewModel (
        private val macAddress: String
) : ViewModel() {

    private lateinit var bleDevice: RxBleDevice

    private val disconnectTriggerSubject = PublishSubject.create<Unit>()

    private lateinit var connectionObservable: Observable<RxBleConnection>

    private val connectionDisposable = CompositeDisposable()

    private lateinit var characteristicUuid: UUID

    private fun connect() {
        characteristicUuid = UUID.fromString(CHARACTERISTIC_UUID)
        bleDevice = BluetoothApplication.rxBleClient.getBleDevice(macAddress)
        connectionObservable = prepareConnectionObservable()
    }



    fun writeData(data: String) {
        if (bleDevice.isConnected) {
            connectionObservable
                    .firstOrError()
                    .flatMap {
                        it.writeCharacteristic(UUID.fromString(CHARACTERISTIC_UUID), data.toByteArray())
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Toast.makeText(this, "WROTE TEXT", Toast.LENGTH_SHORT).show()
                    }, { print("WE DID NOT WRITE SOME EPIC SHIT BOY") })
                    .let { connectionDisposable.add(it) }
        }
    }

    fun onPause() = writeData("0")



}