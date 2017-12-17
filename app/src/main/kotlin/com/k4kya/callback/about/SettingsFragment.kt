package com.k4kya.callback.about

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import com.k4kya.callback.onboarding.OnboardingActivity
import com.k4kya.callback.R
import com.k4kya.callback.external.WebViewActivity
import com.k4kya.callback.util.intentFor

class SettingsFragment : PreferenceFragment() {

    private val sendFeedback: Preference
        get() = findPreference(getString(R.string.send_feedback))

    private val showIntro: Preference
        get() = findPreference(getString(R.string.intro))

    private val showOpenSourceLicenses: Preference
        get() = findPreference(getString(R.string.open_source))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        sendFeedback.setOnPreferenceClickListener { _ ->
            startActivity(Intent().apply {
                action = Intent.ACTION_SENDTO
                type = "text/html"
                putExtra(Intent.EXTRA_EMAIL, "amal.kakaiya@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Feedback for Callback v" + getString(R.string.version))
            }); true
        }
        showIntro.setOnPreferenceClickListener { _ -> startActivity(intentFor<OnboardingActivity>()); true }
        showOpenSourceLicenses.setOnPreferenceClickListener { _ ->
            startActivity(intentFor<WebViewActivity>().apply {
                putExtra("content", "Open Source Licenses")
            }); true
        }
    }
}