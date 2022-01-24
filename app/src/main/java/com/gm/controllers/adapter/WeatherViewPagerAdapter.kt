package com.gm.controllers.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.gm.controllers.fragments.WeatherFragment
import com.gm.models.Model

class MyAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int, var list: ArrayList<Model.WeatherDetailsList>) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return list.size!!
    }

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        val bundle=Bundle()
        bundle.putSerializable("data",list?.get(position))

        return WeatherFragment.newInstance(bundle,position);
    }


}