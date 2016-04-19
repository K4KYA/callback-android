package com.k4kya.callback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log

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
                    val sendFeedbackIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:amal.kakaiya@gmail.com"))
                    sendFeedbackIntent.extras?.putString(Intent.EXTRA_SUBJECT, "Callback v" + getString(R.string.version))
                    startActivity(Intent.createChooser(sendFeedbackIntent, "Send email..."))
                    Log.d("SettingsFragment", "send Feedback clicked")
                    true
                })
        showIntro.setOnPreferenceClickListener { preference -> startActivity(Intent(activity, OnboardingActivity::class.java)); true }
    }
}