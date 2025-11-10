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

/**
 * Tests de instrumentación (UI) para el flujo de login
 * ---------------------------------------------------
 * Tipo de test: Instrumented tests usando Espresso.
 * Objetivo: Verificar que la `MainActivity` y su UI interactúan correctamente
 * con los elementos (EditText, Button, TextView) para los siguientes escenarios:
 *  - Inicio de sesión exitoso (mensaje "Inicio de sesión exitoso").
 *  - Credenciales inválidas (mensaje que contiene "Credenciales").
 *  - Error de red (mensaje que contiene "Error de red").
 *
 * Ubicación: `app/src/androidTest/java/.../LoginActivityTest.kt`
 */
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    // Escenario: Login exitoso (UI)
    // Interacción: rellenar username/password y pulsar botón
    // Verificación: TextView `message` muestra "Inicio de sesión exitoso"
    fun successfulLoginShowsSuccessMessage() {
        // user/pass are accepted by the demo AuthService in MainActivity
        onView(withId(R.id.username)).perform(replaceText("user"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("pass"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.message)).check(matches(withText("Inicio de sesión exitoso")))
    }

    @Test
    // Escenario: Credenciales inválidas (UI)
    // Verificación: el TextView `message` contiene la palabra "Credenciales"
    fun invalidCredentialsShowError() {
        onView(withId(R.id.username)).perform(replaceText("user"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("wrong"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        // el mensaje viene desde AuthRepository: "Credenciales inválidas"
        onView(withId(R.id.message)).check(matches(withText(containsString("Credenciales"))))
    }

    @Test
    // Escenario: Error de red simulado (UI)
    // Verificación: el TextView `message` contiene "Error de red"
    fun networkErrorShowsNetworkMessage() {
        onView(withId(R.id.username)).perform(replaceText("network"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("x"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.message)).check(matches(withText(containsString("Error de red"))))
    }
}
