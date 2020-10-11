package com.example.miditeslacoilapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class FragmentPageAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(fragmentManager!!) {
    private var fragments = mutableListOf<Fragment>()
    private var fragmentTitles = mutableListOf<String>()

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitles.add(title)
    }

    fun getFragments(): List<Fragment> {
        return fragments
    }

    fun setFragments(fragments: MutableList<Fragment>) {
        this.fragments = fragments
    }

    fun getFragmentTitles(): List<String> {
        return fragmentTitles
    }

    fun setFragmentTitles(fragmentTitles: MutableList<String>) {
        this.fragmentTitles = fragmentTitles
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}