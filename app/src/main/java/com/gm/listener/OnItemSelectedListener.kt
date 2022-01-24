package com.gm.listener

import android.widget.TextView

interface OnItemSelectedListener {
    fun onItemSelected(item: Any?, selectedIndex: Int,previous:Int,textView: TextView)
}
