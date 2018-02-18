package com.k4kya.callback.configure

import com.k4kya.callback.interactor.ConfigureInteractor

class ConfigurePresenterImpl(private val configureInteractor: ConfigureInteractor, private val view: ConfigureView) : ConfigurePresenter {
    
    private var state = ConfigureViewModel(configureInteractor.getServiceStatus(), configureInteractor.getStartOnSpeakerStatus(), configureInteractor.getTriggerPhrase())
        set(value) {
            field = value
            view.update(value)
        }
    
    init {
        view.update(state)
    }
    
    override fun setCallbackEnabled(enabled: Boolean, triggerPhrase: String) {
        state = if (!validateTriggerPhrase(triggerPhrase)) {
            configureInteractor.setServiceEnabled(false)
            view.showMessage("Trigger phrase must be at least 4 chars")
            state.copy(toggleEnabled = false, triggerPhrase = triggerPhrase)
        } else {
            configureInteractor.setServiceEnabled(enabled)
            configureInteractor.setTriggerPhrase(triggerPhrase)
            state.copy(toggleEnabled = enabled, triggerPhrase = triggerPhrase)
        }
    }
    
    override fun setStartOnSpeaker(speaker: Boolean) {
        configureInteractor.setSpeakerphoneEnabled(speaker)
        state = state.copy(speakerEnabled = speaker)
    }
    
    internal fun validateTriggerPhrase(phrase: String) = phrase.length > TRIGGER_MIN_LENGTH
    
    companion object {
        const val TRIGGER_MIN_LENGTH = 4
    }
}