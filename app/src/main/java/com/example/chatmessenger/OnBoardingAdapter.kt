package com.example.chatmessenger

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingAdapter
    (activity: FragmentActivity, private val paperList: ArrayList<Page>) :
    FragmentStateAdapter(activity){
    override fun getItemCount(): Int {
        return paperList.size
    }

    override fun createFragment(position: Int): Fragment {
        return OnBoardingFragment(paperList[position])
    }

}