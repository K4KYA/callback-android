package com.k4kya.callback.service

import android.content.Context
import androidx.content.edit

class PhoneCallbackService(private val context: Context) : CallbackService {

    override fun setServiceEnabled(enabled: Boolean) {
        getSharedPrefs().edit{
            putBoolean(SmsListener.CALLBACK_SERVICE_ENABLED_FLAG, enabled)
        }
    }

    override fun setTriggerPhrase(trigger: String) {
        getSharedPrefs().edit{
            putString(SmsListener.CALLBACK_SERVICE_TRIGGER_PHRASE, trigger)
        }
    }

    override fun setSpeakerphoneEnabled(enabled: Boolean) {
        getSharedPrefs().edit {
            putBoolean(SmsListener.CALLBACK_USE_SPEAKERPHONE, enabled)
        }
    }

    override fun getTriggerPhrase() = getSharedPrefs().getString(SmsListener.CALLBACK_SERVICE_TRIGGER_PHRASE, "")!!

    override fun getServiceStatus() = getSharedPrefs().getBoolean(SmsListener.CALLBACK_SERVICE_ENABLED_FLAG, false)

    override fun getStartOnSpeakerStatus() = getSharedPrefs().getBoolean(SmsListener.CALLBACK_USE_SPEAKERPHONE, false)

    private fun getSharedPrefs() = context.getSharedPreferences(SmsListener.CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
}