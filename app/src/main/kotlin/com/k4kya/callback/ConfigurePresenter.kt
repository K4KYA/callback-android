package com.k4kya.callback

class ConfigurePresenter(override var callbackService: CallbackService, override var view: ConfigureMvp.View) : ConfigureMvp.Presenter {

    val TRIGGER_MIN_LENGTH = 4

    init {
        setupView()
    }

    private fun setupView() {
        val enabled = callbackService.getServiceStatus()
        view.updateStatusText(enabled)
        view.setSpeakerEnabled(enabled)
        val currentTrigger = callbackService.getTriggerPhrase()
        view.setTriggerPhrase(currentTrigger)
        view.setServiceToggleButtonEnabled(if (validateTriggerPhrase(currentTrigger)) true else false)
    }

    override fun setCallbackEnabled(enabled: Boolean) {
        if (!enabled) {
            callbackService.setServiceEnabled(enabled)
            view.updateStatusText(false)
            return
        }
        val trigger = view.getLatestTriggerPhrase()
        if (trigger == null || !validateTriggerPhrase(trigger)) {
            callbackService.setServiceEnabled(false)
            view.updateStatusText(false)
            return
        }
        setTriggerPhrase(trigger)
        callbackService.setServiceEnabled(true)
        view.updateStatusText(true)
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

    fun validateTriggerPhrase(phrase: String?) = if (phrase == null) false else phrase.length > TRIGGER_MIN_LENGTH
}