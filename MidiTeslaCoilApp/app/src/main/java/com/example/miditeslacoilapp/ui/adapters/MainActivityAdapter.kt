package com.example.miditeslacoilapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.miditeslacoilapp.ui.InterruptorFragment
import com.example.miditeslacoilapp.ui.MidiFragment

class MainActivityAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InterruptorFragment()
            1 -> MidiFragment()
            else -> Fragment() // can't get to here
        }
    }
}