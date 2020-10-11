package com.example.miditeslacoilapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.akexorcist.bluetotohspp.library.BluetoothSPP

class BluetoothViewModel : ViewModel() {
    private var _bluetoothSPP = MutableLiveData<BluetoothSPP>()
    val bluetoothSPP: LiveData<BluetoothSPP>
        get() = _bluetoothSPP

    fun setBluetooth(bluetoothSPP: BluetoothSPP) = _bluetoothSPP.postValue(bluetoothSPP)

}