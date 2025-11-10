package com.aplicaciones_android.ae3_abpro1

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun successfulLoginShowsSuccessMessage() {
        // user/pass are accepted by the demo AuthService in MainActivity
        onView(withId(R.id.username)).perform(replaceText("user"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("pass"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.message)).check(matches(withText("Inicio de sesión exitoso")))
    }

    @Test
    fun invalidCredentialsShowError() {
        onView(withId(R.id.username)).perform(replaceText("user"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("wrong"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        // el mensaje viene desde AuthRepository: "Credenciales inválidas"
        onView(withId(R.id.message)).check(matches(withText(containsString("Credenciales"))))
    }

    @Test
    fun networkErrorShowsNetworkMessage() {
        onView(withId(R.id.username)).perform(replaceText("network"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("x"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.message)).check(matches(withText(containsString("Error de red"))))
    }
}
