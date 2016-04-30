package com.k4kya.callback

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import org.jetbrains.anko.email
import org.jetbrains.anko.startActivity

class SettingsFragment : PreferenceFragment() {

    val sendFeedback: Preference
        get() = findPreference(getString(R.string.send_feedback))

    val showIntro: Preference
        get() = findPreference(getString(R.string.intro))

    val showOpenSourceLicenses: Preference
        get() = findPreference(getString(R.string.open_source))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        sendFeedback.setOnPreferenceClickListener { preference ->
                    email("amal.kakaiya@gmail.com", "Callback v" + getString(R.string.version))
        }
        showIntro.setOnPreferenceClickListener { preference -> startActivity<OnboardingActivity>(); true }
        showOpenSourceLicenses.setOnPreferenceClickListener { preference -> startActivity<WebViewActivity>("content" to "Open Source Licenses"); true }
    }
}