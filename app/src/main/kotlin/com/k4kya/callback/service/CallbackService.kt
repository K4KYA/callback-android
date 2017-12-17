package com.k4kya.callback.service

interface CallbackService {
    fun setServiceEnabled(enabled: Boolean)
    fun setTriggerPhrase(trigger: String)
    fun setSpeakerphoneEnabled(enabled: Boolean)
    fun getTriggerPhrase(): String?
    fun getServiceStatus(): Boolean
    fun getStartOnSpeakerStatus(): Boolean
}