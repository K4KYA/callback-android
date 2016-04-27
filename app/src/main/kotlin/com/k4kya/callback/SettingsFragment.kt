package com.k4kya.callback

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import org.jetbrains.anko.email

class SettingsFragment : PreferenceFragment() {

    val sendFeedback: Preference
        get() {
            return findPreference(getString(R.string.send_feedback))
        }

    val showIntro: Preference
        get() {
            return findPreference(getString(R.string.intro))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        sendFeedback.setOnPreferenceClickListener(
                { preference ->
                    email("amal.kakaiya@gmail.com", "Callback v" + getString(R.string.version))
                })
        showIntro.setOnPreferenceClickListener { preference -> startActivity(Intent(activity, OnboardingActivity::class.java)); true }
    }
}