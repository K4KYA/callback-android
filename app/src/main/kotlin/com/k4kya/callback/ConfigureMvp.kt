package com.k4kya.callback

import android.text.Editable

interface ConfigureMvp {

    interface View {
        fun showMessage(resId: Int)
        fun showMessage(message: String)
        fun setCallbackStatusText(status: String)
        fun setTriggerPhrase(trigger: Editable)
        fun setSpeakerEnabled(enabled: Boolean)
        fun getLatestTriggerPhrase(): String?
    }

    interface Presenter {
        var view: ConfigureMvp.View
        var callbackService: CallbackService
        fun bindView(view: ConfigureMvp.View)
        fun bindCallbackService(service: CallbackService)
        fun setCallbackEnabled(enabled: Boolean)
        fun setTriggerPhrase(triggerPhrase: String)
        fun setStartOnSpeaker(speaker: Boolean)
        fun toggleCallbackEnabled()
    }

}