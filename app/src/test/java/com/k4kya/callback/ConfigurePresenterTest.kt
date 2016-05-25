package com.k4kya.callback

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConfigurePresenterTest {

    val mockService = object : CallbackService {

        var enabled = false
        var trigger: String? = null
        var useSpeaker = false


        override fun setServiceEnabled(enabled: Boolean) {
            this.enabled = enabled
        }

        override fun setTriggerPhrase(trigger: String) {
            this.trigger = trigger
        }

        override fun setSpeakerphoneEnabled(enabled: Boolean) {
            this.useSpeaker = enabled
        }

        override fun getTriggerPhrase() = trigger

        override fun getServiceStatus() = enabled

        override fun getStartOnSpeakerStatus() = useSpeaker

    }

    val mockView = object : ConfigureMvp.View {

        override fun setServiceToggleButtonEnabled(enabled: Boolean) {

        }

        var trigger: String? = null

        override fun showMessage(resId: Int) {

        }

        override fun showMessage(message: String) {

        }

        override fun setCallbackStatusText(status: String) {

        }

        override fun setTriggerPhrase(trigger: CharSequence) {
            this.trigger = trigger.toString()
        }

        override fun setSpeakerEnabled(enabled: Boolean) {

        }

        override fun getLatestTriggerPhrase(): String? {
            return trigger
        }

        override fun updateStatusText(enabled: Boolean) {

        }

    }

    @Before
    fun setup() {
        mockService.setServiceEnabled(false)
        mockService.setTriggerPhrase("valid")
        mockView.setTriggerPhrase("valid")
    }

    @Test
    fun testValidateTriggerPhrase() {
        val subject = ConfigurePresenter(mockService, mockView)
        assertTrue(subject.validateTriggerPhrase("hello"))
        assertFalse(subject.validateTriggerPhrase(""))
        assertFalse(subject.validateTriggerPhrase("four"))
    }

    @Test
    fun testToggleService() {
        mockService.setServiceEnabled(false)
        mockService.setTriggerPhrase("validPhrase")
        val subject = ConfigurePresenter(mockService, mockView)
        subject.toggleCallbackEnabled()
        assertTrue(mockService.getServiceStatus())
        subject.toggleCallbackEnabled()
        assertFalse(mockService.getServiceStatus())
    }

    @Test
    fun testSetEnabledWithBadTrigger() {
        mockService.setServiceEnabled(false)
        mockView.setTriggerPhrase("bad")
        val subject = ConfigurePresenter(mockService, mockView)
        subject.setCallbackEnabled(true)
        assertFalse(mockService.getServiceStatus())
    }

    @Test
    fun testSetEnabledWithGoodTrigger() {
        mockService.setServiceEnabled(false)
        mockView.setTriggerPhrase("valid")
        val subject = ConfigurePresenter(mockService, mockView)
        subject.setCallbackEnabled(true)
        assertTrue(mockService.getServiceStatus())
    }

    @Test
    fun testStartOnSpeaker() {
        mockService.setSpeakerphoneEnabled(false)
        val subject = ConfigurePresenter(mockService, mockView)
        subject.setStartOnSpeaker(true)
        assertTrue(mockService.getStartOnSpeakerStatus())
    }

}