package com.example.miditeslacoilapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.miditeslacoilapp.BluetoothApplication
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.Utils.isConnected
import com.example.miditeslacoilapp.ui.adapters.MainActivityAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.rx.ReplayingShare
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*

private const val EXTRA_MAC_ADDRESS = "extra_mac_address"

private const val CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb"

class MainActivity : FragmentActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager2

    private lateinit var macAddress: String

    private lateinit var bleDevice: RxBleDevice

    private val disconnectTriggerSubject = PublishSubject.create<Unit>()

    private lateinit var connectionObservable: Observable<RxBleConnection>

    private val connectionDisposable = CompositeDisposable()

    private lateinit var characteristicUuid: UUID

    companion object {
        fun newInstance(context: Context, macAddress: String): Intent =
                Intent(context, MainActivity::class.java).apply { putExtra(EXTRA_MAC_ADDRESS, macAddress) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActivity()
        connect()
        setupNotifications()
        establishConnection()
    }

    private fun setupActivity() {
        val fragmentPagerAdapter = MainActivityAdapter(this)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = fragmentPagerAdapter
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun connect(){
        macAddress = intent.getStringExtra(EXTRA_MAC_ADDRESS)!!
        characteristicUuid = UUID.fromString(CHARACTERISTIC_UUID)
        bleDevice = BluetoothApplication.rxBleClient.getBleDevice(macAddress)
        connectionObservable = prepareConnectionObservable()
    }

    private fun prepareConnectionObservable(): Observable<RxBleConnection> =
            bleDevice
                    .establishConnection(false)
                    .takeUntil(disconnectTriggerSubject)
                    .compose(ReplayingShare.instance())

    private fun establishConnection() {
        if (bleDevice.isConnected) {
            triggerDisconnect()
        } else {
            connectionObservable
                    .flatMapSingle { it.discoverServices() }
                    .flatMapSingle { it.getCharacteristic(characteristicUuid) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                    .let { connectionDisposable.add(it) }
        }
    }

    private fun triggerDisconnect() = disconnectTriggerSubject.onNext(Unit)

    private fun setupNotifications() {
        if (bleDevice.isConnected) {
            connectionObservable
                    .flatMap { it.setupNotification(characteristicUuid) }
                    .flatMap { it }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                    .let { connectionDisposable.add(it) }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_interruptor -> viewPager.currentItem = 0
            R.id.action_midi -> viewPager.currentItem = 1
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        triggerDisconnect()
        connectionDisposable.clear()
    }

}