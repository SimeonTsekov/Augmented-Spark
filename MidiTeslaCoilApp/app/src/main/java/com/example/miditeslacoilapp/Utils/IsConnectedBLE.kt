package com.example.miditeslacoilapp.Utils

import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice

internal val RxBleDevice.isConnected: Boolean
    get() = connectionState == RxBleConnection.RxBleConnectionState.CONNECTED