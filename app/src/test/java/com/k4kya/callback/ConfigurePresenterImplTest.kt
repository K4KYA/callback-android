package com.k4kya.callback

import com.k4kya.callback.configure.ConfigurePresenterImpl
import com.k4kya.callback.configure.ConfigureView
import com.k4kya.callback.configure.ConfigureViewModel
import com.k4kya.callback.interactor.ConfigureInteractor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.reset
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
    
    @Mock lateinit var interactor: ConfigureInteractor
    @Mock lateinit var view: ConfigureView
    private lateinit var presenter: ConfigurePresenterImpl
    
    @Before fun setup() {
        whenever(interactor.getServiceStatus()).thenReturn(false)
        whenever(interactor.getTriggerPhrase()).thenReturn("valid")
        whenever(interactor.getStartOnSpeakerStatus()).thenReturn(false)
        presenter = ConfigurePresenterImpl(interactor, view)
        reset(view)
    }
    
    @Test fun testValidateTriggerPhrase() {
        assertTrue(presenter.validateTriggerPhrase("hello"))
        assertFalse(presenter.validateTriggerPhrase(""))
        assertFalse(presenter.validateTriggerPhrase("four"))
    }
    
    @Test fun testSetEnabledWithGoodTrigger() {
        presenter.setCallbackEnabled(true, "valid")
        verify(interactor).setServiceEnabled(eq(true))
        verify(interactor).setTriggerPhrase("valid")
        verify(view).update(eq(
                ConfigureViewModel(
                        true,
                        false,
                        "valid"
                )
        ))
    }
    
    @Test fun testStartOnSpeaker() {
        presenter.setStartOnSpeaker(true)
        verify(interactor).setSpeakerphoneEnabled(eq(true))
    }
    
    @Test fun cantSetInvalidTriggerPhrase() {
        presenter.setCallbackEnabled(true, "bad")
        verify(interactor).setServiceEnabled(eq(false))
        verify(view).update(eq(
                ConfigureViewModel(
                        false,
                        false,
                        "bad"
                )
        ))
    }
    
}