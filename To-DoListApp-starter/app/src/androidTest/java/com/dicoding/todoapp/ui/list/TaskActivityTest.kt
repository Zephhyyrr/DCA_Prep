package com.dicoding.todoapp.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.action.ViewActions.click
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.add.AddTaskActivity
import com.dicoding.todoapp.ui.list.TaskActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskActivityTest {

    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch(TaskActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun whenTapAddTaskButton_shouldDisplayAddTaskActivity() {
        onView(withId(R.id.fab)).perform(click())

        Intents.intended(hasComponent(AddTaskActivity::class.java.name))

        onView(withId(R.id.add_layout)).check(matches(isDisplayed()))
    }
}
