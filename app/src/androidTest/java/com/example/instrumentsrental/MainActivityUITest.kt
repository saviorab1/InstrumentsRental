package com.example.instrumentsrental

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Matchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testButtonsEnabled() {
        // Verify both buttons are enabled
        onView(withId(R.id.borrowButton)).check(matches(isEnabled()))
        onView(withId(R.id.cancelButton)).check(matches(isEnabled()))
    }

    @Test
    fun testBasicRentalFlow() {
        // 1. Select first instrument from spinner
        onView(withId(R.id.instrumentSpinner)).perform(click())
        onData(anything()).atPosition(0).perform(click())

        // 2. Scroll to the monthly option first and then select it
        onView(withId(R.id.monthlyChip)).perform(scrollTo(), click())

        // 3. Input quantity 2
        onView(withId(R.id.quantityEditText))
            .perform(scrollTo(), clearText(), typeText("2"), closeSoftKeyboard())
            
        // Verify "2" is entered
        onView(withId(R.id.quantityEditText)).check(matches(withText("2")))

        // 4. Scroll to and click cancel button
        onView(withId(R.id.cancelButton)).perform(scrollTo(), click())
    }
} 