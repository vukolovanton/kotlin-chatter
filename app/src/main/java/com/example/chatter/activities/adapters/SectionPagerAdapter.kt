package com.example.chatter.activities.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.chatter.activities.fragments.ChatsFragment
import com.example.chatter.activities.fragments.UserFragment

class SectionPagerAdapter(val myContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return UserFragment()
            1 -> return ChatsFragment()
        }
        return null!!
    }

    override fun getCount(): Int {
        return 2
    }
}