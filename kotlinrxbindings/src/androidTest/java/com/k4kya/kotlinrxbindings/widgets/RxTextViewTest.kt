package com.k4kya.kotlinrxbindings.widgets

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.UiThreadTest
import android.widget.EditText
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rx.observers.TestSubscriber

@RunWith(AndroidJUnit4::class)
class RxTextViewTest {

    val context = InstrumentationRegistry.getContext()
    val view = EditText(context)
    val testSubscriber = TestSubscriber<TextEvent>()

    @Before
    fun setup() {
        view.setText("")
    }

    @After
    fun teardown() {
        testSubscriber.unsubscribe()
    }

    @Test @UiThreadTest
    fun testTextEventAfter() {
        val newVal = "he"
        val beforeVal = "h"
        view.setText(beforeVal)
        view.textEvents()
                .filter { it is AfterTextChangedEvent }
                .subscribe(testSubscriber)
        view.setText(newVal)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(AfterTextChangedEvent(newVal))
    }

    @Test @UiThreadTest
    fun testTextEventBefore() {
        val newVal = "he"
        val beforeVal = "h"
        view.setText(beforeVal)
        view.textEvents()
                .filter { it is BeforeTextChangedEvent }
                .subscribe(testSubscriber)
        view.setText(newVal)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(BeforeTextChangedEvent(beforeVal, 0, beforeVal.length, newVal.length))
    }

    @Test @UiThreadTest
    fun testTextEventChanged() {
        val newVal = "he"
        val beforeVal = "h"
        view.setText(beforeVal)
        view.textEvents()
                .filter { it is TextChangedEvent }
                .subscribe(testSubscriber)
        view.setText(newVal)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(TextChangedEvent(newVal, 0, beforeVal.length, newVal.length))
    }

    @Test @UiThreadTest
    fun testEventsOrder() {
        val newVal = "he"
        val beforeVal = "h"
        view.setText(beforeVal)
        view.textEvents().subscribe(testSubscriber)
        view.setText(newVal)
        testSubscriber.assertNoErrors()
        val expectedEvents = listOf(
                BeforeTextChangedEvent(beforeVal, 0, beforeVal.length, newVal.length),
                TextChangedEvent(newVal, 0, beforeVal.length, newVal.length),
                AfterTextChangedEvent(newVal)
        )
        testSubscriber.assertReceivedOnNext(expectedEvents)
    }
}