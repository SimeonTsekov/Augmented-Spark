package com.example.miditeslacoilapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.miditeslacoilapp.BluetoothApplication
import com.example.miditeslacoilapp.Extensions.isConnected
import com.jakewharton.rx.ReplayingShare
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*

private const val CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb"

class BluetoothViewModel (
        private val macAddress: String
) : ViewModel() {

    private lateinit var bleDevice: RxBleDevice

    private val disconnectTriggerSubject = PublishSubject.create<Unit>()

    private lateinit var connectionObservable: Observable<RxBleConnection>

    private val connectionDisposable = CompositeDisposable()

    private lateinit var characteristicUuid: UUID

    fun connect() {
        characteristicUuid = UUID.fromString(CHARACTERISTIC_UUID)
        bleDevice = BluetoothApplication.rxBleClient.getBleDevice(macAddress)
        connectionObservable = prepareConnectionObservable()
        setupNotifications()
        discoverCharacteristic()
    }

    private fun prepareConnectionObservable(): Observable<RxBleConnection> =
            bleDevice
                    .establishConnection(false)
                    .takeUntil(disconnectTriggerSubject)
                    .compose(ReplayingShare.instance())

    private fun discoverCharacteristic() {
        if (bleDevice.isConnected) {
            disconnect()
        } else {
            connectionObservable
                    .flatMapSingle { it.discoverServices() }
                    .flatMapSingle { it.getCharacteristic(characteristicUuid) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ print("Epic") }, { print("Not epic") })
                    .let { connectionDisposable.add(it) }
        }
    }

    private fun setupNotifications() {
        if (bleDevice.isConnected) {
            connectionObservable
                    .flatMap { it.setupNotification(characteristicUuid) }
                    .flatMap { it }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ print("Epic") }, { print("Not epic") })
                    .let { connectionDisposable.add(it) }
        }
    }

    fun disconnect() {
        connectionDisposable.clear()
        disconnectTriggerSubject.onNext(Unit)
    }

    fun writeData(data: String) {
        if (bleDevice.isConnected) {
            connectionObservable
                    .firstOrError()
                    .flatMap {
                        it.writeCharacteristic(UUID.fromString(CHARACTERISTIC_UUID), data.toByteArray())
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ print("Epic") }, { print("Not epic") })
                    .let { connectionDisposable.add(it) }
        }
    }

    fun onPause() {
        if (bleDevice.isConnected) {
            writeData("0")
        }
    }

}