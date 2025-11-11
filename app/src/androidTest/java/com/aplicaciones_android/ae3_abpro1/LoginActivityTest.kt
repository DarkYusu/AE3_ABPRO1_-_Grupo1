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
 * Tests instrumentados (Espresso) para la pantalla de login.
 *
 * Estos tests comprueban los flujos principales de la UI:
 *  - Inicio de sesión exitoso muestra el mensaje correcto.
 *  - Credenciales inválidas muestran mensaje de error.
 *  - Error de red simulado muestra mensaje de red.
 */
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    // Regla para lanzar la actividad bajo prueba antes de cada test
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    // Test: login exitoso con credenciales válidas.
    // Paso: rellenar username/password y pulsar el botón de login.
    // Verificación: el TextView con id `message` muestra exactamente "Inicio de sesión exitoso".
    fun successfulLoginShowsSuccessMessage() {
        onView(withId(R.id.username)).perform(replaceText("sebastian"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("123456"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.message)).check(matches(withText("Inicio de sesión exitoso")))
    }

    @Test
    // Test: credenciales inválidas.
    // Paso: usar un usuario existente con contraseña incorrecta.
    // Verificación: el TextView `message` contiene la palabra "Credenciales".
    fun invalidCredentialsShowError() {
        onView(withId(R.id.username)).perform(replaceText("sebastian"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("wrong"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.message)).check(matches(withText(containsString("Credenciales"))))
    }

    @Test
    // Test: simulación de error de red.
    // Paso: usar el nombre de usuario reservado "network" para provocar la excepción simulada.
    // Verificación: el TextView `message` contiene "Error de red".
    fun networkErrorShowsNetworkMessage() {
        onView(withId(R.id.username)).perform(replaceText("network"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(replaceText("x"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.message)).check(matches(withText(containsString("Error de red"))))
    }
}
