package com.k4kya.callback.onboarding

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class OnboardingPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    
    private val pages = listOf(
            OnboardingFragment.createInstance(0),
            OnboardingFragment.createInstance(1),
            OnboardingFragment.createInstance(2)
    )

    override fun getCount() = pages.size

    override fun getItem(position: Int): Fragment? {
        return pages[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Section 1"
            1 -> "Section 2"
            2 -> "Section 3"
            else -> null
        }
    }
}