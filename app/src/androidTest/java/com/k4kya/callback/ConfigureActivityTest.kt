package com.k4kya.callback

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.k4kya.callback.configure.ConfigureActivity
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ConfigureActivityTest {

    @Rule @JvmField
    val activity = ActivityTestRule<ConfigureActivity>(ConfigureActivity::class.java)

    @Before
    fun setup() {
        onView(withId(R.id.editTriggerPhrase)).perform(clearText())
    }

    @Test
    fun startServiceWithBadPhrase() {
        onView(withId(R.id.editTriggerPhrase)).perform(typeText("a"))
        onView(withId(R.id.btnToggleService)).check(matches(not((isEnabled()))))
    }

    @Test
    fun startServiceWithValidPhrase() {
        onView(withId(R.id.editTriggerPhrase)).perform(typeText("valid"))
        onView(withId(R.id.btnToggleService)).check(matches((isEnabled())))
        onView(withId(R.id.btnToggleService)).perform(click())
        onView(withId(R.id.btnToggleService)).check(matches(withText(R.string.stop_callback_service)))
    }

    @Test
    fun updateServiceWithBadPhrase() {
        onView(withId(R.id.editTriggerPhrase)).perform(typeText("a"))
        onView(withId(R.id.btnToggleService)).check(matches(not((isEnabled()))))
    }

    @Test
    fun updateServiceWithValidPhrase() {
        onView(withId(R.id.editTriggerPhrase)).perform(typeText("valid"))
        onView(withId(R.id.btnSetTriggerPhrase)).perform(click())
        onView(withId(R.id.btnToggleService)).check(matches((isEnabled())))
    }

}