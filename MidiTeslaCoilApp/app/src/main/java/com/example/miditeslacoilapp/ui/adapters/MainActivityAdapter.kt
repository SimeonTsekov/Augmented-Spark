package com.example.miditeslacoilapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.miditeslacoilapp.ui.InterruptorFragment
import com.example.miditeslacoilapp.ui.MidiFilesFragment
import com.example.miditeslacoilapp.ui.MidiPlayerFragment

class MainActivityAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InterruptorFragment()
            1 -> MidiFilesFragment()
            2 -> MidiPlayerFragment()
            else -> Fragment() // can't get to here
        }
    }
}