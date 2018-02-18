package com.k4kya.callback.configure

interface ConfigureView {
    fun showMessage(resId: Int)
    fun showMessage(message: String)
    fun update(viewModel: ConfigureViewModel)
}

interface ConfigurePresenter {
    fun setCallbackEnabled(enabled: Boolean, triggerPhrase: String)
    fun setStartOnSpeaker(speaker: Boolean)
}

data class ConfigureViewModel(
        val toggleEnabled: Boolean,
        val speakerEnabled: Boolean,
        val triggerPhrase: String
)
