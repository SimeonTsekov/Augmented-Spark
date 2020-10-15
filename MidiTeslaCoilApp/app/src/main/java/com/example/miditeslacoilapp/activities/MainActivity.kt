package com.example.miditeslacoilapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.viewModels.BluetoothViewModel
import com.example.miditeslacoilapp.ui.adapters.MainActivityAdapter
import com.example.miditeslacoilapp.viewModels.BluetoothViewModelFactory
import com.example.miditeslacoilapp.viewModels.MidiFileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val EXTRA_MAC_ADDRESS = "extra_mac_address"

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var bluetoothViewModel: BluetoothViewModel
    private lateinit var midiFileViewModel: MidiFileViewModel

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager2

    companion object {
        @JvmStatic
        fun newInstance(context: Context, macAddress: String): Intent =
                Intent(context, MainActivity::class.java).apply { putExtra(EXTRA_MAC_ADDRESS, macAddress) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothViewModel = ViewModelProvider(this,
                BluetoothViewModelFactory(intent.getStringExtra(EXTRA_MAC_ADDRESS)!!))
                .get(BluetoothViewModel::class.java)

        midiFileViewModel = ViewModelProvider(this).get(MidiFileViewModel::class.java)

        setupActivity()
        bluetoothViewModel.connect()
    }

    private fun setupActivity() {
        val fragmentPagerAdapter = MainActivityAdapter(supportFragmentManager, lifecycle)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = fragmentPagerAdapter
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    fun navigateToPlayer() {
        viewPager.currentItem = 2
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_interruptor -> viewPager.currentItem = 0
            R.id.action_midi -> viewPager.currentItem = 1
            R.id.action_midi_player -> viewPager.currentItem = 2
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        bluetoothViewModel.disconnect()
    }

}