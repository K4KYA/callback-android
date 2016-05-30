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
        view.setText("h")
        view.textEvents()
                .filter { it is AfterTextChangedEvent }
                .subscribe(testSubscriber)
        view.setText(newVal)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(AfterTextChangedEvent("he"))
    }

    @Test @UiThreadTest
    fun testTextEventBefore() {
        val newVal = "he"
        view.setText("h")
        view.textEvents()
                .filter { it is BeforeTextChangedEvent }
                .subscribe(testSubscriber)
        view.setText(newVal)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(BeforeTextChangedEvent("h", 0, 1, 2))
    }

    @Test @UiThreadTest
    fun testTextEventChanged() {
        val newVal = "he"
        view.setText("h")
        view.textEvents()
                .filter { it is TextChangedEvent }
                .subscribe(testSubscriber)
        view.setText(newVal)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(TextChangedEvent("he", 0, 1, 2))
    }
}