package com.k4kya.callback.configure

import com.k4kya.callback.service.CallbackService

class ConfigurePresenterImpl(override var callbackService: CallbackService, override var view: ConfigureView) : ConfigurePresenter {

    init {
        setupView()
    }

    private fun setupView() {
        val enabled = callbackService.getServiceStatus()
        view.updateStatusText(enabled)
        view.setSpeakerEnabled(enabled)
        val currentTrigger = callbackService.getTriggerPhrase()
        view.setTriggerPhrase(currentTrigger)
        view.setServiceToggleButtonEnabled(validateTriggerPhrase(currentTrigger))
    }

    override fun setCallbackEnabled(enabled: Boolean) {
        val trigger = view.getLatestTriggerPhrase()
        if (trigger == null || !validateTriggerPhrase(trigger)) {
            callbackService.setServiceEnabled(false)
            view.updateStatusText(false)
        } else {
            setTriggerPhrase(trigger)
            callbackService.setServiceEnabled(enabled)
            view.updateStatusText(enabled)
        }
    }

    override fun setTriggerPhrase(triggerPhrase: String) {
        if (validateTriggerPhrase(triggerPhrase)) {
            callbackService.setTriggerPhrase(triggerPhrase)
            view.setServiceToggleButtonEnabled(true)
        } else {
            setCallbackEnabled(false)
            view.setServiceToggleButtonEnabled(false)
        }
    }

    override fun setStartOnSpeaker(speaker: Boolean) {
        callbackService.setSpeakerphoneEnabled(speaker)
    }

    override fun toggleCallbackEnabled() {
        setCallbackEnabled(!callbackService.getServiceStatus())
    }

    fun validateTriggerPhrase(phrase: String?) = when (phrase.isNullOrBlank()) {
        true -> false
        false -> phrase!!.length > TRIGGER_MIN_LENGTH
    }

    companion object {
        const val TRIGGER_MIN_LENGTH = 4
    }
}