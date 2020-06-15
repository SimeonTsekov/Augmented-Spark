package com.example.miditeslacoilapp.ViewModel;

import androidx.lifecycle.ViewModel;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class BluetoothViewModel extends ViewModel {
    private BluetoothSPP bluetoothSPP;

    public void setBluetoothSPP(BluetoothSPP bluetoothSPP) {
        this.bluetoothSPP = bluetoothSPP;
    }

    public BluetoothSPP getBluetoothSPP() {
        return bluetoothSPP;
    }
}
