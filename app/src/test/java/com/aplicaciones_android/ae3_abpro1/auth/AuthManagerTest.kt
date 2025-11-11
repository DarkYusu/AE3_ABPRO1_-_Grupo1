package com.aplicaciones_android.ae3_abpro1.auth

import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Tests unitarios para AuthManager
 * --------------------------------
 * Tipo de test: Unit tests (JUnit) que usan Mockito-Kotlin para simular el servicio
 * a través de `AuthRepository`.
 * Objetivo: Comprobar la lógica de negocio de `AuthManager.signIn(...)`, incluyendo:
 *  - Validación de inputs vacíos.
 *  - Delegación al repositorio y manejo de resultados (Success, NetworkError).
 *
 * Requisitos cubiertos:
 *  - Probar la lógica de la clase de autenticación.
 */
class AuthManagerTest {
    private val service: AuthService = mock()
    private val repository = AuthRepository(service)
    private val manager = AuthManager(repository)

    @Test
    // Caso: inputs vacíos -> se espera AuthResult.InvalidCredentials
    // Tipo de test: unitario
    fun `signIn returns InvalidCredentials when inputs empty`() {
        val result = manager.signIn("", "")
        assertTrue(result is AuthResult.InvalidCredentials)
    }

    @Test
    // Caso: delegación al repositorio con éxito
    // Verifica que AuthManager llama al servicio (mockeado) y retorna Success con token
    fun `signIn delegates to repository and returns Success`() {
        whenever(service.fetchToken("user", "pass")).thenReturn("ey.token.mock")
        val result = manager.signIn("user", "pass")
        assertTrue("Se esperaba AuthResult.Success pero se obtuvo: $result", result is AuthResult.Success)
        val token = (result as AuthResult.Success).token
        assertEquals("ey.token.mock", token)
    }

    @Test
    // Caso: el servicio lanza excepción -> se espera NetworkError
    // Simula un fallo de red en la dependencia externa.
    fun `signIn handles network error`() {
        whenever(service.fetchToken("network", "x")).then { throw Exception("net") }
        val result = manager.signIn("network", "x")
        assertTrue(result is AuthResult.NetworkError)
    }

    // Nota: Falta test de instrumentación (Espresso) para la UI de LoginActivity.
    // Debe cubrir: introducir credenciales en los EditText, pulsar botón y
    // comprobar navegación/UX y que se almacene el token.
}
