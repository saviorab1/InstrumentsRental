package com.example.instrumentsrental

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppLaunchTest {

    @Test
    fun testAppLaunches() {
        // Launch the MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        
        // Verify the activity launched successfully
        scenario.onActivity { activity ->
            // Simply assert that the activity is not null
            assertTrue("Activity should not be null", activity != null)
        }
        
        // Close the scenario
        scenario.close()
    }
} 