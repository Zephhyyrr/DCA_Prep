package com.dicoding.courseschedule.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.dicoding.courseschedule.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.courseschedule.ui.add.AddCourseActivity
import com.dicoding.courseschedule.ui.home.HomeActivity
import org.junit.Rule
import android.Manifest

import androidx.test.rule.GrantPermissionRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddCourseTesting {

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

    @get:Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun whenClickAddCourse_thenAddCourseActivityDisplayed() {
        onView(withId(R.id.action_add)).perform(click())

        Intents.intended(hasComponent(AddCourseActivity::class.java.name))
    }
}
