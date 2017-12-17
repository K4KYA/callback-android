package com.k4kya.callback.service

import android.content.Context

class PhoneCallbackService(val context: Context?) : CallbackService {

    override fun setServiceEnabled(enabled: Boolean) {
        getSharedPrefs()?.edit()?.putBoolean(SmsListener.CALLBACK_SERVICE_ENABLED_FLAG, enabled)?.apply()
    }

    override fun setTriggerPhrase(trigger: String) {
        getSharedPrefs()?.edit()?.putString(SmsListener.CALLBACK_SERVICE_TRIGGER_PHRASE, trigger)?.apply()
    }

    override fun setSpeakerphoneEnabled(enabled: Boolean) {
        getSharedPrefs()?.edit()?.putBoolean(SmsListener.CALLBACK_SERVICE_ENABLED_FLAG, enabled)?.apply()
    }

    override fun getTriggerPhrase() = getSharedPrefs()?.getString(SmsListener.CALLBACK_SERVICE_TRIGGER_PHRASE, "")

    override fun getServiceStatus() = getSharedPrefs()?.getBoolean(SmsListener.CALLBACK_SERVICE_ENABLED_FLAG, false) ?: false

    override fun getStartOnSpeakerStatus() = getSharedPrefs()?.getBoolean(SmsListener.CALLBACK_USE_SPEAKERPHONE, false) ?: false

    private fun getSharedPrefs() = context?.getSharedPreferences(SmsListener.CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
}