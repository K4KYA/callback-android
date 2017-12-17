package com.k4kya.callback.onboarding

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class OnboardingPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        return OnboardingFragment.createInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Section 1"
            1 -> return "Section 2"
            2 -> return "Section 3"
        }
        return null
    }
}