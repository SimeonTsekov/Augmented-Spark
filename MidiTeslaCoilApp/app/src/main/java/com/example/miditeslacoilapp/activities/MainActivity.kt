package com.example.miditeslacoilapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.viewModels.BluetoothViewModel
import com.example.miditeslacoilapp.ui.adapters.MainActivityAdapter
import com.example.miditeslacoilapp.viewModels.BluetoothViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val EXTRA_MAC_ADDRESS = "extra_mac_address"

class MainActivity : FragmentActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: BluetoothViewModel

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager2

    companion object {
        fun newInstance(context: Context, macAddress: String): Intent =
                Intent(context, MainActivity::class.java).apply { putExtra(EXTRA_MAC_ADDRESS, macAddress) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this,
                BluetoothViewModelFactory(intent.getStringExtra(EXTRA_MAC_ADDRESS)!!))
                .get(BluetoothViewModel::class.java)

        setupActivity()
        viewModel.connect()
    }

    private fun setupActivity() {
        val fragmentPagerAdapter = MainActivityAdapter(this)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = fragmentPagerAdapter
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
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
        viewModel.disconnect()
    }

}