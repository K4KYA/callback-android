package com.k4kya.callback.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Telephony
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import com.k4kya.callback.BuildConfig

class SmsListener : BroadcastReceiver() {

    companion object {
        const val CALLBACK_SERVICE_ENABLED_FLAG = "callbackServiceEnabled"
        const val CALLBACK_SERVICE_TRIGGER_PHRASE = "callbackTrigger"
        const val CALLBACK_SHARED_PREFS_KEY = BuildConfig.APPLICATION_ID
        const val CALLBACK_USE_SPEAKERPHONE = "useSpeakerPhone"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (!isCallbackServiceEnabled(context) || !isTelephonyEnabled(context)) {
            return
        }
        val triggerPhrase = getTriggerPhrase(context) ?: return
        val messages = extractSmsMessagesFromIntent(intent)
        val callbackNumber = callbackNumberFromMessagesForTrigger(messages, triggerPhrase)
        if (callbackNumber != null) {
            startPhoneCallForNumber(callbackNumber, context)
        }
    }

    private fun isCallbackServiceEnabled(context: Context) = getSharedPrefs(context).getBoolean(CALLBACK_SERVICE_ENABLED_FLAG, false)

    private fun isTelephonyEnabled(context: Context): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        return telephonyManager == null || (telephonyManager.simState == TelephonyManager.SIM_STATE_READY)
    }

    private fun startPhoneCallForNumber(callbackNumber: String, context: Context) {
        val dialIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:" + callbackNumber)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, useSpeakerPhone(context))
        }
        context.startActivity(dialIntent)
    }

    private fun useSpeakerPhone(context: Context) = getSharedPrefs(context).getBoolean(CALLBACK_USE_SPEAKERPHONE, false)

    private fun callbackNumberFromMessagesForTrigger(messages: List<Message>, triggerPhrase: String) =
            messages.firstOrNull {
                isTriggerPhraseInMessage(it.message, triggerPhrase)
            }?.sender

    private fun extractSmsMessagesFromIntent(intent: Intent) = intent.takeIf {
        Telephony.Sms.Intents.SMS_RECEIVED_ACTION == it.action
    }.let {
        Telephony.Sms.Intents.getMessagesFromIntent(it).map {
            Message(it.originatingAddress, it.messageBody)
        }
    }

    private fun isTriggerPhraseInMessage(message: String?, triggerPhrase: String) = message?.startsWith(triggerPhrase) ?: false

    private fun getTriggerPhrase(context: Context) = getSharedPrefs(context).getString(CALLBACK_SERVICE_TRIGGER_PHRASE, null)

    private fun getSharedPrefs(context: Context) = context.getSharedPreferences(CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)

    data class Message(val sender: String?, val message: String?)

}