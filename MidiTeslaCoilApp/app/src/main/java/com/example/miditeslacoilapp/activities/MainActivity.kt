package com.example.miditeslacoilapp.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.miditeslacoilapp.R
import com.example.miditeslacoilapp.ui.ConnectionFragment
import com.example.miditeslacoilapp.ui.InterruptorFragment
import com.example.miditeslacoilapp.ui.MidiFragment
import com.example.miditeslacoilapp.ui.adapters.FragmentPageAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, OnPageChangeListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager
    private var currentMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActivity()
    }

    private fun setupActivity() {
        val fragmentPagerAdapter = FragmentPageAdapter(supportFragmentManager)
        fragmentPagerAdapter.addFragment(ConnectionFragment(), "connectionFragment")
        fragmentPagerAdapter.addFragment(InterruptorFragment(), "interruptorFragment")
        fragmentPagerAdapter.addFragment(MidiFragment(), "midiFragment")
        viewPager = findViewById(R.id.view_pager)
        viewPager.addOnPageChangeListener(this)
        viewPager.adapter = fragmentPagerAdapter
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        if (currentMenuItem != null) {
            currentMenuItem?.isChecked = false
        } else {
            bottomNavigationView.menu.getItem(1).isChecked = false;
        }
        currentMenuItem = bottomNavigationView.menu.getItem(position)
        currentMenuItem?.isChecked = true
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_connect -> viewPager.currentItem = 0
            R.id.action_interruptor -> viewPager.currentItem = 1
            R.id.action_midi -> viewPager.currentItem = 2
        }
        return false
    }

}