package com.k4kya.callback.configure

import com.k4kya.callback.service.CallbackService

interface ConfigureMvp {

    interface View {
        fun showMessage(resId: Int)
        fun showMessage(message: String)
        fun setCallbackStatusText(status: String)
        fun setTriggerPhrase(trigger: CharSequence?)
        fun setSpeakerEnabled(enabled: Boolean)
        fun getLatestTriggerPhrase(): String?
        fun updateStatusText(enabled: Boolean)
        fun setServiceToggleButtonEnabled(enabled: Boolean)
    }

    interface Presenter {
        var view: View
        var callbackService: CallbackService
        fun setCallbackEnabled(enabled: Boolean)
        fun setTriggerPhrase(triggerPhrase: String)
        fun setStartOnSpeaker(speaker: Boolean)
        fun toggleCallbackEnabled()
    }

}