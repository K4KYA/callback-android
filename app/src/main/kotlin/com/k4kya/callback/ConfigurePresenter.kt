package com.k4kya.callback

class ConfigurePresenter(override var callbackService: CallbackService, override var view: ConfigureMvp.View) : ConfigureMvp.Presenter {

    val TRIGGER_MIN_LENGTH = 4

    override fun bindView(view: ConfigureMvp.View) {
        this.view = view
    }

    override fun bindCallbackService(service: CallbackService) {
        callbackService = service
    }

    override fun setCallbackEnabled(enabled: Boolean) {
        if (!enabled) {
            callbackService.setServiceEnabled(!enabled)
        }
        val trigger = view.getLatestTriggerPhrase()
        if (trigger == null || !validateTriggerPhrase(trigger)) {
            callbackService.setServiceEnabled(false)
            return
        }
        setTriggerPhrase(trigger)
        callbackService.setServiceEnabled(true)
    }

    override fun setTriggerPhrase(triggerPhrase: String) {
        callbackService.setTriggerPhrase(triggerPhrase)
    }

    override fun setStartOnSpeaker(speaker: Boolean) {
        callbackService.setSpeakerphoneEnabled(speaker)
    }

    override fun toggleCallbackEnabled() {
        setCallbackEnabled(!callbackService.getServiceStatus())
    }

    fun validateTriggerPhrase(phrase: String) = phrase.length > TRIGGER_MIN_LENGTH
}