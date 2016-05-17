package com.k4kya.callback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Telephony
import android.telecom.TelecomManager
import android.telephony.TelephonyManager

class SmsListener : BroadcastReceiver() {

    companion object {
        const val CALLBACK_SERVICE_ENABLED_FLAG = "callbackServiceEnabled"
        const val CALLBACK_SERVICE_TRIGGER_PHRASE = "callbackTrigger"
        const val CALLBACK_SHARED_PREFS_KEY = BuildConfig.APPLICATION_ID
        const val CALLBACK_USE_SPEAKERPHONE = "useSpeakerPhone"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!isCallbackServiceEnabled(context) || !isTelephonyEnabled(context)) {
            return;
        }
        val triggerPhrase = getTriggerPhrase(context) ?: return
        val messages = extractSmsMessagesFromIntent(intent)
        val callbackNumber = callbackNumberFromMessagesForTrigger(messages, triggerPhrase)
        if (callbackNumber != null) {
            startPhoneCallForNumber(callbackNumber, context)
        }
    }

    private fun isCallbackServiceEnabled(context: Context?): Boolean {
        return getSharedPrefs(context)?.getBoolean(CALLBACK_SERVICE_ENABLED_FLAG, false) ?: false
    }

    private fun isTelephonyEnabled(context: Context?): Boolean {
        val telephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        return telephonyManager == null || (telephonyManager.simState == TelephonyManager.SIM_STATE_READY)
    }

    private fun startPhoneCallForNumber(callbackNumber: String, context: Context?) {
        val dialIntent = Intent(Intent.ACTION_CALL)
        dialIntent.data = Uri.parse("tel:" + callbackNumber)
        dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        dialIntent.putExtra(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, useSpeakerPhone(context))
        context?.startActivity(dialIntent)
    }

    private fun useSpeakerPhone(context: Context?): Boolean {
        return getSharedPrefs(context)?.getBoolean(CALLBACK_USE_SPEAKERPHONE, false) ?: false
    }

    private fun callbackNumberFromMessagesForTrigger(messages: List<Message>, triggerPhrase: String): String? {
        var sender: String? = null
        messages.forEach {
            if (isTriggerPhraseInMessage(it.message, triggerPhrase) ?: false) {
                sender = it.sender
            }
        }
        return sender;
    }

    private fun extractSmsMessagesFromIntent(intent: Intent?): List<Message> {
        var messages = listOf<Message>()
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent?.action)) {
            Telephony.Sms.Intents.getMessagesFromIntent(intent).map {
                val message = Message(it.originatingAddress, it.messageBody)
                messages = messages.plus(message)
            }
        }
        return messages
    }

    fun isTriggerPhraseInMessage(message: String?, triggerPhrase: String) = message?.startsWith(triggerPhrase)

    private fun getTriggerPhrase(context: Context?): String? {
        return getSharedPrefs(context)?.getString(CALLBACK_SERVICE_TRIGGER_PHRASE, null)
    }

    private fun getSharedPrefs(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(CALLBACK_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

    data class Message(val sender: String?, val message: String?)

}