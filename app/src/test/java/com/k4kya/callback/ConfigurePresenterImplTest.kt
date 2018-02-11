package com.k4kya.callback

import com.k4kya.callback.configure.ConfigurePresenterImpl
import com.k4kya.callback.configure.ConfigureView
import com.k4kya.callback.service.CallbackService
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConfigurePresenterImplTest {

    @Mock lateinit var mockService : CallbackService
    @Mock lateinit var mockView : ConfigureView

    @Before
    fun setup() {
        whenever(mockService.getServiceStatus()).thenReturn(false)
        whenever(mockService.getTriggerPhrase()).thenReturn("valid")
        whenever(mockView.getLatestTriggerPhrase()).thenReturn("valid")
    }

    @Test
    fun testValidateTriggerPhrase() {
        val subject = ConfigurePresenterImpl(mockService, mockView)
        assertTrue(subject.validateTriggerPhrase("hello"))
        assertFalse(subject.validateTriggerPhrase(""))
        assertFalse(subject.validateTriggerPhrase("four"))
    }

    @Test
    fun testSetEnabledWithBadTrigger() {
        whenever(mockView.getLatestTriggerPhrase()).thenReturn("bad")
        val subject = ConfigurePresenterImpl(mockService, mockView)
        subject.setCallbackEnabled(true)
        assertFalse(mockService.getServiceStatus())
    }

    @Test
    fun testSetEnabledWithGoodTrigger() {
        val subject = ConfigurePresenterImpl(mockService, mockView)
        subject.setCallbackEnabled(true)
        verify(mockService).setServiceEnabled(eq(true))
    }

    @Test
    fun testStartOnSpeaker() {
        val subject = ConfigurePresenterImpl(mockService, mockView)
        subject.setStartOnSpeaker(true)
        verify(mockService).setSpeakerphoneEnabled(eq(true))
    }

    @Test
    fun cantSetInvalidTriggerPhrase() {
        val subject = ConfigurePresenterImpl(mockService, mockView)
        subject.setTriggerPhrase("bad")
        verify(mockService).setServiceEnabled(eq(false))
    }

}