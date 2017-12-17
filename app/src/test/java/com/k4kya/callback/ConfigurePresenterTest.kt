package com.k4kya.callback

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ConfigurePresenterTest {

    @Mock lateinit var mockService : CallbackService
    @Mock lateinit var mockView : ConfigureMvp.View

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        whenever(mockService.getServiceStatus()).thenReturn(false)
        whenever(mockService.getTriggerPhrase()).thenReturn("valid")
        whenever(mockView.getLatestTriggerPhrase()).thenReturn("valid")
    }

    @Test
    fun testValidateTriggerPhrase() {
        val subject = ConfigurePresenter(mockService, mockView)
        assertTrue(subject.validateTriggerPhrase("hello"))
        assertFalse(subject.validateTriggerPhrase(""))
        assertFalse(subject.validateTriggerPhrase("four"))
    }

    @Test
    fun testSetEnabledWithBadTrigger() {
        whenever(mockView.getLatestTriggerPhrase()).thenReturn("bad")
        val subject = ConfigurePresenter(mockService, mockView)
        subject.setCallbackEnabled(true)
        assertFalse(mockService.getServiceStatus())
    }

    @Test
    fun testSetEnabledWithGoodTrigger() {
        val subject = ConfigurePresenter(mockService, mockView)
        subject.setCallbackEnabled(true)
        verify(mockService).setServiceEnabled(eq(true))
    }

    @Test
    fun testStartOnSpeaker() {
        val subject = ConfigurePresenter(mockService, mockView)
        subject.setStartOnSpeaker(true)
        verify(mockService).setSpeakerphoneEnabled(eq(true))
    }

    @Test
    fun cantSetInvalidTriggerPhrase() {
        whenever(mockService.getStartOnSpeakerStatus()).thenReturn(false)
        val subject = ConfigurePresenter(mockService, mockView)
        subject.setTriggerPhrase("bad")
        verify(mockService).setServiceEnabled(eq(false))
    }

}