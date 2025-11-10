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
    // Verifica que AuthManager llama al servicio (mockeado) y retorna Success
    fun `signIn delegates to repository and returns Success`() {
        whenever(service.authenticate("user", "pass")).thenReturn(true)
        val result = manager.signIn("user", "pass")
        assertTrue(result is AuthResult.Success)
    }

    @Test
    // Caso: el servicio lanza excepción -> se espera NetworkError
    // Simula un fallo de red en la dependencia externa.
    fun `signIn handles network error`() {
        whenever(service.authenticate("network", "x")).then { throw Exception("net") }
        val result = manager.signIn("network", "x")
        assertTrue(result is AuthResult.NetworkError)
    }
}
